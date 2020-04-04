package org.openmf.psp.mpesa.dto;

import java.util.Arrays;

public class BalanceRequest {

    String CommandID;
    String PartyA;
    String IdentifierType;
    String Remarks;
    String Initiator;
    String SecurityCredential;
    String QueueTimeOutURL;
    String ResultURL;

    public String getCommandID() {
        return CommandID;
    }

    public void setCommandID(String commandID) {
        CommandID = commandID;
    }

    public String getPartyA() {
        return PartyA;
    }

    public void setPartyA(String partyA) {
        PartyA = partyA;
    }

    public String getIdentifierType() {
        return IdentifierType;
    }

    public void setIdentifierType(String identifierType) {
        IdentifierType = identifierType;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getInitiator() {
        return Initiator;
    }

    public void setInitiator(String initiator) {
        Initiator = initiator;
    }

    public String getSecurityCredential() {
        return SecurityCredential;
    }

    public void setSecurityCredentials(String securityCredential) {
        SecurityCredential = securityCredential;
    }

    public String getQueueTimeOutURL() {
        return QueueTimeOutURL;
    }

    public void setQueueTimeOutURL(String queueTimeOutURL) {
        QueueTimeOutURL = queueTimeOutURL;
    }

    public String getResultURL() {
        return ResultURL;
    }

    public void setResultURL(String resultURL) {
        ResultURL = resultURL;
    }

}
