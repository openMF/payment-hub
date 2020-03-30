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
import org.openmf.psp.dto.PartyIdInfo;
import org.openmf.psp.internal.FspId;
import org.openmf.psp.mojaloop.cache.TransactionContextHolder;
import org.openmf.psp.mojaloop.component.SwitchRestClient;
import org.openmf.psp.mojaloop.constant.ExchangeHeader;
import org.openmf.psp.mojaloop.internal.PartyContext;
import org.openmf.psp.mojaloop.internal.TransactionCacheContext;
import org.openmf.psp.mojaloop.internal.TransactionRoleContext;
import org.openmf.psp.type.IdentifierType;
import org.openmf.psp.type.TransactionRole;
import org.openmf.psp.util.ContextUtil;
import org.openmf.psp.util.IbanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Processor to send GET and POST /parties request to the other side FSP through interoperable switch.
 */
@Component("partiesSwitchProcessor")
public class PartiesSwitchProcessor implements Processor {

    private TransactionContextHolder transactionContextHolder;

    private IbanUtil ibanUtil;

    private SwitchSettings switchSettings;
    private SwitchRestClient switchRestClient;

    private PartiesFspProcessor fspProcessor;

    @Autowired
    public PartiesSwitchProcessor(TransactionContextHolder transactionContextHolder, IbanUtil ibanUtil, SwitchSettings switchSettings,
                                  SwitchRestClient switchRestClient, PartiesFspProcessor partiesFspProcessor) {
        this.transactionContextHolder = transactionContextHolder;
        this.ibanUtil = ibanUtil;
        this.switchSettings = switchSettings;
        this.switchRestClient = switchRestClient;
        this.fspProcessor = partiesFspProcessor;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        if (extractFromCache(exchange) != null)
            return;

        if(switchSettings.isIntegrationEnabled()) {
            callSwitch(exchange);
        } else {
            extractLocally(exchange);
        }
    }

    private FspId extractFromCache(Exchange exchange) {
        String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
        TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);

        TransactionRole partiesRole = exchange.getProperty(ExchangeHeader.PARTIES_ROLE.getKey(), TransactionRole.class);

        FspId fspId = ContextUtil.getFspId(exchange, partiesRole);
        if (fspId != null)
            return fspId;

        fspId = transactionContext.getRoleContext(partiesRole).getFspId();
        if (fspId != null) {
            ContextUtil.setFspId(exchange, partiesRole, fspId);
            return fspId;
        }
        return null;
    }

    private void callSwitch(Exchange exchange) {
        String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
        TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);

        TransactionRole partiesRole = exchange.getProperty(ExchangeHeader.PARTIES_ROLE.getKey(), TransactionRole.class);
        TransactionRoleContext roleContext = transactionContext.getRoleContext(partiesRole);

        PartyIdInfo partyIdInfo = roleContext.getPartyContext().getParty().getPartyIdInfo();
        IdentifierType idType = partyIdInfo.getPartyIdType();
        String idValue = partyIdInfo.getPartyIdentifier();
        String subIdOrType = partyIdInfo.getPartySubIdOrType();

        FspId sourceFspId = exchange.getProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), FspId.class);

        switchRestClient.callGetParties(idType, idValue, subIdOrType, sourceFspId);
    }

    private void extractLocally(Exchange exchange) {
        String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
        TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);

        TransactionRole partiesRole = exchange.getProperty(ExchangeHeader.PARTIES_ROLE.getKey(), TransactionRole.class);
        TransactionRoleContext partiesContext = transactionContext.getRoleContext(partiesRole);

        PartyContext partyContext = partiesContext.getPartyContext();
        PartyIdInfo partyIdInfo = partyContext.getParty().getPartyIdInfo();
        IdentifierType idType = partyIdInfo.getPartyIdType();
        if (IdentifierType.IBAN != idType)
            throw new RuntimeException(String.format("Parsing party identifier type (%s) is not supported locally!", idType));

        String idValue = partyIdInfo.getPartyIdentifier();

        FspId fspId = ibanUtil.extractFspIdFromIban(idValue);
        partiesContext.setFspId(fspId);

        String accountId = ibanUtil.extractAccountIdFromIban(idValue);
        partyContext.setAccountId(accountId);

        ContextUtil.setFspId(exchange, partiesRole, fspId);

        TransactionRole currentRole = exchange.getProperty(ExchangeHeader.CURRENT_ROLE.getKey(), TransactionRole.class);
        if (currentRole == partiesRole)
            exchange.setProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), fspId.getTenant());
    }
}
