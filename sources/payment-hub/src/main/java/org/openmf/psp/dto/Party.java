/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package org.openmf.psp.dto;

import com.ilp.conditions.models.pdp.PartyComplexName;
import com.ilp.conditions.models.pdp.PartyPersonalInfo;

import java.beans.Transient;

/**
 * {
 * 	accountId: ""
 * }
 */
public class Party {

    private PartyIdInfo partyIdInfo; // mandatory
    private String merchantClassificationCode; // optional
    private String name; // optional
    private PersonalInfo personalInfo; // optional

    Party() {
    }

    public Party(PartyIdInfo partyIdInfo, String merchantClassificationCode, String name, PersonalInfo personalInfo) {
        this.partyIdInfo = partyIdInfo;
        this.merchantClassificationCode = merchantClassificationCode;
        this.name = name;
        this.personalInfo = personalInfo;
    }

    public Party(PartyIdInfo partyIdInfo) {
        this(partyIdInfo, null, null, null);
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }

    public void update(Party oParty) {
        if (oParty == null)
            return;

        update(oParty.partyIdInfo);

        String oMerchantCode = oParty.merchantClassificationCode;
        if (oMerchantCode != null) {
            merchantClassificationCode = oMerchantCode;
        }

        String oName = oParty.name;
        if (oName == null) {
            name = oName;
        }

        PersonalInfo oInfo = oParty.personalInfo;
        if (personalInfo == null)
            personalInfo = oInfo;
        else
            personalInfo.update(oInfo);
    }

    public void update(PartyIdInfo oPartyIdInfo) {
        if (oPartyIdInfo == null)
            return;

        partyIdInfo.update(oPartyIdInfo);
    }

    @Transient
    public com.ilp.conditions.models.pdp.Party getIlpParty() {
        com.ilp.conditions.models.pdp.Party ilpParty = new com.ilp.conditions.models.pdp.Party();
        ilpParty.setMerchantClassificationCode(merchantClassificationCode);
        ilpParty.setName(name);

        com.ilp.conditions.models.pdp.PartyIdInfo ilpPartyIdInfo = new com.ilp.conditions.models.pdp.PartyIdInfo();
        ilpPartyIdInfo.setFspId(partyIdInfo.getFspId());
        ilpPartyIdInfo.setPartyIdentifier(partyIdInfo.getPartyIdentifier());
        ilpPartyIdInfo.setPartyIdType(partyIdInfo.getPartyIdType().name());
        ilpPartyIdInfo.setPartySubIdOrType(partyIdInfo.getPartySubIdOrType());
        ilpParty.setPartyIdInfo(ilpPartyIdInfo);

        if (personalInfo != null) {
            PartyPersonalInfo ilpPersonalInfo = new PartyPersonalInfo();
            ilpPersonalInfo.setDateOfBirth(personalInfo.getDateOfBirth());
            PartyComplexName payerComplexName = new PartyComplexName();
            ComplexName complexName = personalInfo.getComplexName();
            payerComplexName.setFirstName(complexName.getFirstName());
            payerComplexName.setLastName(complexName.getLastName());
            payerComplexName.setMiddleName(complexName.getMiddleName());
            ilpPersonalInfo.setComplexName(payerComplexName);
            ilpParty.setPersonalInfo(ilpPersonalInfo);
        }

        return ilpParty;
    }
}
