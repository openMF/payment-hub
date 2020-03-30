/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.dto;

import org.openmf.psp.type.IdentifierType;

public class PartyIdInfo {

    private IdentifierType partyIdType; // mandatory, immutable
    private String partyIdentifier; // mandatory, immutable
    private String partySubIdOrType; // optional, immutable
    private String fspId; // optional

    PartyIdInfo() {
    }

    public PartyIdInfo(IdentifierType partyIdType, String partyIdentifier, String partySubIdOrType, String fspId) {
        this.partyIdType = partyIdType;
        this.partyIdentifier = partyIdentifier;
        this.partySubIdOrType = partySubIdOrType;
        this.fspId = fspId;
    }

    public PartyIdInfo(IdentifierType partyIdType, String partyIdentifier, String partySubIdOrType) {
        this(partyIdType, partyIdentifier, partySubIdOrType, null);
    }

    public IdentifierType getPartyIdType() {
        return partyIdType;
    }

    void setPartyIdType(IdentifierType partyIdType) {
        this.partyIdType = partyIdType;
    }

    public String getPartyIdentifier() {
        return partyIdentifier;
    }

    void setPartyIdentifier(String partyIdentifier) {
        this.partyIdentifier = partyIdentifier;
    }

    public String getPartySubIdOrType() {
        return partySubIdOrType;
    }

    void setPartySubIdOrType(String partySubIdOrType) {
        this.partySubIdOrType = partySubIdOrType;
    }

    public String getFspId() {
        return fspId;
    }

    public void setFspId(String fspId) {
        if (fspId == null)
            return;
        if (this.fspId != null && !this.fspId.equals(fspId))
            throw new RuntimeException("Technical error: try to change fspId from " + this.fspId + " to " + fspId);
        this.fspId = fspId;
    }

    public void update(PartyIdInfo oInfo) {
        if (oInfo == null)
            return;
        if (!equals(oInfo))
            throw new RuntimeException("Incompatible party info " + this + '/' + oInfo);

        String oFspId = oInfo.fspId;
        if (oFspId != null)
            fspId = oFspId;
    }

    @Override
    public String toString() {
        return "PartyIdInfo{" +
                "idType:" + partyIdType +
                ", id:'" + partyIdentifier + '\'' +
                ", subIdOrType:'" + partySubIdOrType + '\'' +
                ", fspId:'" + fspId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartyIdInfo that = (PartyIdInfo) o;

        if (partyIdType != that.partyIdType) return false;
        if (!partyIdentifier.equals(that.partyIdentifier)) return false;
        return partySubIdOrType != null ? partySubIdOrType.equals(that.partySubIdOrType) : that.partySubIdOrType == null;
    }

    @Override
    public int hashCode() {
        int result = partyIdType.hashCode();
        result = 31 * result + partyIdentifier.hashCode();
        result = 31 * result + (partySubIdOrType != null ? partySubIdOrType.hashCode() : 0);
        return result;
    }
}
