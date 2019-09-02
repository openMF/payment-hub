/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.dto;

public class Payee {

    private PartyIdInfo partyIdInfo;
    private String merchantClassificationCode;

    public PartyIdInfo getPartyIdInfo() {
        return partyIdInfo;
    }

    public void setPartyIdInfo(PartyIdInfo partyIdInfo) {
        this.partyIdInfo = partyIdInfo;
    }

    public String getMerchantClassificationCode() {
        return merchantClassificationCode;
    }

    public void setMerchantClassificationCode(String merchantClassificationCode) {
        this.merchantClassificationCode = merchantClassificationCode;
    }
}
