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
import org.openmf.psp.dto.MoneyData;
import org.openmf.psp.util.ContextUtil;

/**
 * {
 *   "transferId": "{{hub_in01tn01_in01tn02_transfer_id}}",
 *   "payerFsp": "{{fsp_in01tn01_id}}",
 *   "payeeFsp": "{{fsp_in01tn02_id}}",
 *   "amount": {
 *     "amount": "{{in01tn01_in01tn02_amount}}",
 *     "currency": "{{currency}}"
 *   },
 *   "expiration": "2019-12-10T12:07:47.349Z",
 *   "ilpPacket": "eyJxdW90ZUlkIjoiNGY3NDhiMmUtNGIzOC00ZTQ1LTkxM2UtYTBjYTg2YzI3NTYyIiwidHJhbnNhY3Rpb25JZCI6IjRmNzQ4YjJlLTRiMzgtNGU0NS05MTNlLWEwY2E4NmMyNzU2MiJ9",
 *   "condition": "9V_HPdggIMZliTVven2eY2AKTZcgPQv4FfIZvWeXZX4"
 * }
 */
public class TransferSwitchRequestDTO {

    private String transferId;
    private String payerFsp;
    private String payeeFsp;
    private MoneyData amount;
    private String ilpPacket;
    private String condition;
    private String expiration;
    private List<Extension> extensionList;

    TransferSwitchRequestDTO() {
    }

    public TransferSwitchRequestDTO(String transferId, String payerFsp, String payeeFsp, MoneyData amount, String ilpPacket,
                                    String condition, LocalDateTime expiration, List<Extension> extensionList) {
        this.transferId = transferId;
        this.payerFsp = payerFsp;
        this.payeeFsp = payeeFsp;
        this.amount = amount;
        this.ilpPacket = ilpPacket;
        this.condition = condition;
        this.expiration = ContextUtil.formatDate(expiration);
        this.extensionList = extensionList;
    }

    public TransferSwitchRequestDTO(String transferId, String payerFsp, String payeeFsp, MoneyData amount, String ilpPacket,
                                    String condition, LocalDateTime expiration) {
        this(transferId, payerFsp, payeeFsp, amount, ilpPacket, condition, expiration, null);
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getPayerFsp() {
        return payerFsp;
    }

    public void setPayerFsp(String payerFsp) {
        this.payerFsp = payerFsp;
    }

    public String getPayeeFsp() {
        return payeeFsp;
    }

    public void setPayeeFsp(String payeeFsp) {
        this.payeeFsp = payeeFsp;
    }

    public MoneyData getAmount() {
        return amount;
    }

    public void setAmount(MoneyData amount) {
        this.amount = amount;
    }

    public String getIlpPacket() {
        return ilpPacket;
    }

    public void setIlpPacket(String ilpPacket) {
        this.ilpPacket = ilpPacket;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    @Transient
    public LocalDateTime getExpirationDate() {
        return ContextUtil.parseDate(expiration);
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = ContextUtil.formatDate(expiration);
    }

    public List<Extension> getExtensionList() {
        return extensionList;
    }

    public void setExtensionList(List<Extension> extensionList) {
        this.extensionList = extensionList;
    }

    public Extension getExtension(String key) {
        if (extensionList == null)
            return null;
        for (Extension extension : extensionList) {
            if (extension.getKey().equals(key))
                return extension;
        }
        return null;
    }

    public String getExtensionValue(String key) {
        Extension extension = getExtension(key);
        return extension == null ? null : extension.getValue();
    }
}
