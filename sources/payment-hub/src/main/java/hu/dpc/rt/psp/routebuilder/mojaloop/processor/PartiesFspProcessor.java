/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.routebuilder.mojaloop.processor;

import hu.dpc.rt.psp.cache.TransactionContextHolder;
import hu.dpc.rt.psp.component.FspRestClient;
import hu.dpc.rt.psp.constant.ExchangeHeader;
import hu.dpc.rt.psp.dto.PartyIdInfo;
import hu.dpc.rt.psp.dto.fsp.PartyFspResponseDTO;
import hu.dpc.rt.psp.internal.FspId;
import hu.dpc.rt.psp.internal.TransactionCacheContext;
import hu.dpc.rt.psp.internal.TransactionRoleContext;
import hu.dpc.rt.psp.type.IdentifierType;
import hu.dpc.rt.psp.type.TransactionRole;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Processor to send GET /parties request to FSP.
 */
@Component("partiesFspProcessor")
public class PartiesFspProcessor implements Processor {

    private TransactionContextHolder transactionContextHolder;

    private FspRestClient fspRestClient;

    @Autowired
    public PartiesFspProcessor(TransactionContextHolder transactionContextHolder, FspRestClient fspRestClient) {
        this.transactionContextHolder = transactionContextHolder;
        this.fspRestClient = fspRestClient;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String transactionId = null;
        TransactionRole partiesRole = null;
        PartyIdInfo partyIdInfo = null;

        if (exchange.getProperty(ExchangeHeader.CALLER_FSP.getKey()) != null) {
            partyIdInfo = exchange.getProperty(ExchangeHeader.PARTIES_INFO.getKey(), PartyIdInfo.class);
        }
        else {
            transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
            TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);

            partiesRole = exchange.getProperty(ExchangeHeader.PARTIES_ROLE.getKey(), TransactionRole.class);
            TransactionRoleContext roleContext = transactionContext.getRoleContext(partiesRole);

            partyIdInfo = roleContext.getPartyContext().getParty().getPartyIdInfo();
        }

        IdentifierType idType = partyIdInfo.getPartyIdType();
        String idValue = partyIdInfo.getPartyIdentifier();
        String subIdOrType = partyIdInfo.getPartySubIdOrType();

        FspId currentFsp = exchange.getProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), FspId.class);

        PartyFspResponseDTO partiesResponseDTO = fspRestClient.callParties(idType, idValue, subIdOrType, currentFsp);

        if (transactionId != null)
            transactionContextHolder.updateFspParty(transactionId, partiesRole, partiesResponseDTO);
        else
            transactionContextHolder.updateFspParty(partyIdInfo, partiesResponseDTO);
    }
}
