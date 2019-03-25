/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.rt.psp.internal;

import hu.dpc.rt.psp.dto.Party;
import hu.dpc.rt.psp.dto.PartyIdInfo;
import hu.dpc.rt.psp.dto.fsp.PartyFspResponseDTO;
import hu.dpc.rt.psp.dto.mojaloop.PartySwitchResponseDTO;
import hu.dpc.rt.psp.util.ContextUtil;

public class PartyContext {

    private Party party; // optional, but this or fspId must be set
    private FspId fspId; // optional, but party or this must be set
    private String accountId; // optional

    PartyContext() {
    }

    public PartyContext(Party party, String accountId) {
        this.party = party;
        this.accountId = accountId;
    }

    public PartyContext(FspId fspId) {
        this.fspId = fspId;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public FspId getFspId() {
        return fspId;
    }

    public void setFspId(FspId fspId) {
        if (fspId == null)
            return;
        if (this.fspId != null && !this.fspId.getId().equals(fspId.getId()))
            throw new RuntimeException("Technical error: try to change fspId from " + this.fspId + " to " + fspId);
        this.fspId = fspId;
        if (party != null) {
            party.getPartyIdInfo().setFspId(fspId.getId());
        }
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void update(PartyContext oContext) {
        if (oContext == null)
            return;

        update(oContext.getParty());

        String oAccount = oContext.accountId;
        if (oAccount != null)
            accountId = oAccount;
    }

    public void update(PartyFspResponseDTO partiesDTO) {
        if (partiesDTO == null)
            return;

        String oAccount = partiesDTO.getAccountId();
        if (oAccount != null)
            accountId = oAccount;
    }

    void update(PartySwitchResponseDTO partiesDTO) {
        if (partiesDTO == null)
            return;

        update(partiesDTO.getParty());
    }

    void update(Party oParty) {
        if (party == null)
            party = oParty;
        else
            party.update(oParty);

        updateFspId(oParty);
    }

    void update(PartyIdInfo oPartyIdInfo) {
        if (party == null)
            party = new Party(oPartyIdInfo);
        else
            party.update(oPartyIdInfo);

        updateFspId(oPartyIdInfo);
    }

    private void updateFspId(Party oParty) {
        if (oParty != null) {
            updateFspId(oParty.getPartyIdInfo());
        }
    }

    private void updateFspId(PartyIdInfo oPartyIdInfo) {
        if (oPartyIdInfo == null)
            return;

        String oFspId = oPartyIdInfo.getFspId();
        if (oFspId != null)
            setFspId(ContextUtil.parseFspId(oFspId));
    }
}
