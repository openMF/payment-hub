/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.routebuilder.processor;

import hu.dpc.rt.psp.cache.TransactionContextHolder;
import hu.dpc.rt.psp.component.SwitchRestClient;
import hu.dpc.rt.psp.config.SwitchSettings;
import hu.dpc.rt.psp.constant.ExchangeHeader;
import hu.dpc.rt.psp.dto.MoneyData;
import hu.dpc.rt.psp.dto.mojaloop.QuoteSwitchRequestDTO;
import hu.dpc.rt.psp.internal.FspId;
import hu.dpc.rt.psp.internal.TransactionCacheContext;
import hu.dpc.rt.psp.internal.TransactionRoleContext;
import hu.dpc.rt.psp.type.TransactionRole;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static hu.dpc.rt.psp.util.ContextUtil.EXTENSION_SEPARATOR;

/**
 * Processor to send POST /quotes request to the other side FSP through interoperable switch.
 */
@Component("quotesSwitchProcessor")
public class QuotesSwitchProcessor implements Processor {

    private TransactionContextHolder transactionContextHolder;

    private SwitchSettings switchSettings;
    private SwitchRestClient switchRestClient;

    private QuotesFspProcessor fspProcessor;


    @Autowired
    public QuotesSwitchProcessor(TransactionContextHolder transactionContextHolder, SwitchSettings switchSettings, SwitchRestClient switchRestClient,
                                 QuotesFspProcessor quotesFspProcessor) {
        this.transactionContextHolder = transactionContextHolder;
        this.switchSettings = switchSettings;
        this.switchRestClient = switchRestClient;
        this.fspProcessor = quotesFspProcessor;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        if(switchSettings.isIntegrationEnabled()) {
            callSwitch(exchange);
        } else {
            callFspDirectly(exchange);
        }
    }

    private void callSwitch(Exchange exchange) throws Exception {
        String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
        TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);

        TransactionRole currentRole = exchange.getProperty(ExchangeHeader.CURRENT_ROLE.getKey(), TransactionRole.class);
        TransactionRoleContext sourceContext = transactionContext.getRoleContext(currentRole);

        TransactionRole quotesRole = exchange.getProperty(ExchangeHeader.QUOTES_ROLE.getKey(), TransactionRole.class);
        TransactionRoleContext destContext = transactionContext.getRoleContext(quotesRole);

        String quoteId = transactionContext.getOrCreateQuoteId();

        MoneyData amount = new MoneyData(transactionContext.getTransactionAmount(), transactionContext.getCurrency());

        String channelClientRef = transactionContext.getChannelClientRef();
        String note = transactionContext.getNote();
        if (channelClientRef != null) { // TODO: hack, send in extensionList
            note = (note == null ? "" : note) + EXTENSION_SEPARATOR + channelClientRef;
        }
        QuoteSwitchRequestDTO request = new QuoteSwitchRequestDTO(transactionId, transactionContext.getTransactionRequestId(),
                quoteId, destContext.getPartyContext().getParty(), sourceContext.getPartyContext().getParty(), transactionContext.getAmountType(),
                amount, sourceContext.getFee(), transactionContext.getTransactionType(), transactionContext.getGeoCode(),
                note, transactionContext.getExpiration(), transactionContext.getExtensionList());

        switchRestClient.callPostQuotes(request, sourceContext.getFspId(), destContext.getFspId());
    }

    private void callFspDirectly(Exchange exchange) throws Exception {
        FspId currentFsp = exchange.getProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), FspId.class);
        FspId payeeFsp = exchange.getProperty(ExchangeHeader.PAYEE_FSP_ID.getKey(), FspId.class);

        if (payeeFsp == null || !payeeFsp.getInstance().equals(currentFsp.getInstance()))
            throw new RuntimeException("Payee is on another instance, can not get quotes without switch");

        fspProcessor.process(exchange);
    }
}
