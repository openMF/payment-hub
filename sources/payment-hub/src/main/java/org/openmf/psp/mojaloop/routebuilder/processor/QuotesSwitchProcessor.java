/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.mojaloop.routebuilder.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmf.psp.config.SwitchSettings;
import org.openmf.psp.dto.MoneyData;
import org.openmf.psp.internal.FspId;
import org.openmf.psp.mojaloop.cache.TransactionContextHolder;
import org.openmf.psp.mojaloop.component.SwitchRestClient;
import org.openmf.psp.mojaloop.constant.ExchangeHeader;
import org.openmf.psp.mojaloop.dto.mojaloop.QuoteSwitchRequestDTO;
import org.openmf.psp.mojaloop.internal.TransactionCacheContext;
import org.openmf.psp.mojaloop.internal.TransactionRoleContext;
import org.openmf.psp.type.TransactionRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmf.psp.util.ContextUtil.*;

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
