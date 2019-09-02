/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.routebuilder.mojaloop.processor;

import hu.dpc.rt.psp.cache.TransactionContextHolder;
import hu.dpc.rt.psp.component.ChannelRestClient;
import hu.dpc.rt.psp.constant.ExchangeHeader;
import hu.dpc.rt.psp.dto.mojaloop.channel.TransactionChannelAsyncResponseDTO;
import hu.dpc.rt.psp.internal.FspId;
import hu.dpc.rt.psp.internal.TransactionCacheContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Processor to send async PUT response to channel, when the payment transaction was successfully finished.
 */
@Component("channelTransactionsResponseProcessor")
public class ChannelTransactionsResponseProcessor implements Processor {

    private ChannelRestClient channelRestClient;

    private TransactionContextHolder transactionContextHolder;

    @Autowired
    public ChannelTransactionsResponseProcessor(ChannelRestClient channelRestClient, TransactionContextHolder transactionContextHolder) {
        this.channelRestClient = channelRestClient;
        this.transactionContextHolder = transactionContextHolder;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String transactionId = exchange.getProperty(ExchangeHeader.TRANSACTION_ID.getKey(), String.class);
        TransactionCacheContext transactionContext = transactionContextHolder.getTransactionContext(transactionId);

        TransactionChannelAsyncResponseDTO paymentAsyncResponseDTO = new TransactionChannelAsyncResponseDTO(transactionContext.getChannelClientRef(),
                transactionId, transactionContext.getCompletedStamp(), transactionContext.getTransferState(), transactionContext.getPaymentRequestDTO());

        channelRestClient.callPaymentAsyncResponse(paymentAsyncResponseDTO, exchange.getProperty(ExchangeHeader.CURRENT_FSP_ID.getKey(), FspId.class));
    }
}
