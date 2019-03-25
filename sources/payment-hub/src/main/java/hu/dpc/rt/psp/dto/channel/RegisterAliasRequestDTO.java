/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.dto.channel;

import hu.dpc.rt.psp.type.IdentifierType;

public class RegisterAliasRequestDTO {

    private String accountId;
    private IdentifierType idType;
    private String idValue;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public IdentifierType getIdType() {
        return idType;
    }

    public void setIdType(IdentifierType idType) {
        this.idType = idType;
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }
}
