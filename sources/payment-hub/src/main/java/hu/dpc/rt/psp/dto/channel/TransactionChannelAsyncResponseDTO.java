/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.dto.channel;

import hu.dpc.rt.psp.type.TransferState;
import hu.dpc.rt.psp.util.ContextUtil;

import java.beans.Transient;
import java.time.LocalDateTime;

/**
 * {
 *   "clientRefId": "{{ch_client1_ref}}",
 *   "transactionId": "{{hub_transaction1_id}}",
 *   "completedTimestamp": "2019-01-22T00:00:00.000-01:00",
 *   "transferState": "COMMITTED"
 * }
 */
public class TransactionChannelAsyncResponseDTO {

    private String clientRefId;
    private String transactionId;
    private String completedTimestamp;
    private TransferState transferState;
    private TransactionChannelRequestDTO originalRequestData;

    TransactionChannelAsyncResponseDTO() {
    }

    public TransactionChannelAsyncResponseDTO(String clientRefId, String transactionId, LocalDateTime completedTimestamp, TransferState transferState,
                                              TransactionChannelRequestDTO originalRequestData) {
        this.clientRefId = clientRefId;
        this.transactionId = transactionId;
        this.completedTimestamp = ContextUtil.formatDate(completedTimestamp);
        this.transferState = transferState;
        this.originalRequestData = originalRequestData;
    }

    public String getClientRefId() {
        return clientRefId;
    }

    public void setClientRefId(String clientRefId) {
        this.clientRefId = clientRefId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCompletedTimestamp() {
        return completedTimestamp;
    }

    public void setCompletedTimestamp(String completedTimestamp) {
        this.completedTimestamp = completedTimestamp;
    }

    @Transient
    public LocalDateTime getCompletedTimestampDate() {
        return ContextUtil.parseDate(completedTimestamp);
    }

    public void setCompletedTimestamp(LocalDateTime completedTimestamp) {
        this.completedTimestamp = ContextUtil.formatDate(completedTimestamp);
    }

    public TransferState getTransferState() {
        return transferState;
    }

    public void setTransferState(TransferState transferState) {
        this.transferState = transferState;
    }

    public TransactionChannelRequestDTO getOriginalRequestData() {
        return originalRequestData;
    }

    public void setOriginalRequestData(TransactionChannelRequestDTO originalRequestData) {
        this.originalRequestData = originalRequestData;
    }
}
