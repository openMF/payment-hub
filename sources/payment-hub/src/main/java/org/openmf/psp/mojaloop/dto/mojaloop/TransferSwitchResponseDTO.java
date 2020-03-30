/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.mojaloop.dto.mojaloop;


import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.List;

import org.openmf.psp.dto.Extension;
import org.openmf.psp.mojaloop.type.TransferState;
import org.openmf.psp.util.ContextUtil;


public class TransferSwitchResponseDTO {

    private String fulfilment;
    private String completedTimestamp;
    private TransferState transferState; // mandatory
    private List<Extension> extensionList;

    TransferSwitchResponseDTO() {
    }

    public TransferSwitchResponseDTO(String fulfilment, LocalDateTime completedTimestamp, TransferState transferState, List<Extension> extensionList) {
        this.fulfilment = fulfilment;
        this.completedTimestamp = ContextUtil.formatDate(completedTimestamp);
        this.transferState = transferState;
        this.extensionList = extensionList;
    }

    public TransferSwitchResponseDTO(TransferState transferState) {
        this(null, null, transferState, null);
    }

    public String getFulfilment() {
        return fulfilment;
    }

    public void setFulfilment(String fulfilment) {
        this.fulfilment = fulfilment;
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

    public List<Extension> getExtensionList() {
        return extensionList;
    }

    public void setExtensionList(List<Extension> extensionList) {
        this.extensionList = extensionList;
    }
}
