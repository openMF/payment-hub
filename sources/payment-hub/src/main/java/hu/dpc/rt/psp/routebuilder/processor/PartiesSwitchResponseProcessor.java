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
import hu.dpc.rt.psp.constant.ExchangeHeader;
import hu.dpc.rt.psp.dto.PartyIdInfo;
import hu.dpc.rt.psp.dto.mojaloop.PartySwitchResponseDTO;
import hu.dpc.rt.psp.internal.FspId;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Processor to send PUT /parties response through the switch.
 */
@Component("partiesSwitchResponseProcessor")
public class PartiesSwitchResponseProcessor implements Processor {

    private TransactionContextHolder transactionContextHolder;

    private SwitchRestClient switchRestClient;

    @Autowired
    public PartiesSwitchResponseProcessor(TransactionContextHolder transactionContextHolder, SwitchRestClient switchRestClient) {
        this.transactionContextHolder = transactionContextHolder;
        this.switchRestClient = switchRestClient;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        FspId sourceFspId = exchange.getProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), FspId.class);
        FspId destFspId = exchange.getProperty(ExchangeHeader.CALLER_FSP.getKey(), FspId.class);

        PartyIdInfo partyIdInfo = exchange.getProperty(ExchangeHeader.PARTIES_INFO.getKey(), PartyIdInfo.class);
        PartySwitchResponseDTO response = new PartySwitchResponseDTO(transactionContextHolder.getPartyContext(partyIdInfo).getParty());
        switchRestClient.callPutParties(response, sourceFspId, destFspId);
    }
}
