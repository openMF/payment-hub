/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 *  https://mozilla.org/MPL/2.0/.
 */

/**
@Author Sidhant Gupta
*/
package org.openmf.psp.gsma.dto;

import java.util.Arrays;

public class GSMATransaction {
	 String amount;
	    String currency;
	    String type;
	    String subType;
	    String descriptionText;
	    String date;
	    String requestingOrganisationTransactionReference;
	    String oneTimeCode;
	    String geoCode;

	    Party[] debitParty;
	    Party[] creditParty;

	    Kyc senderKyc;
	    Kyc receiverKyc;

	    String originalTransactionReference;

	    String servicingIdentity;
	    String requestingLei;
	    String recievingLei;

	    Party[] metadata;

	    String transactionStatus;
	    String creationDate;
	    String modificationDate;
	    String transactionReference;
	    String transactionReceipt;

	    InternationalTransferInformation internationalTransferInformation;

	    public String getAmount() {
	        return amount;
	    }

	    public void setAmount(String amount) {
	        this.amount = amount;
	    }

	    public String getCurrency() {
	        return currency;
	    }

	    public void setCurrency(String currency) {
	        this.currency = currency;
	    }

	    public String getType() {
	        return type;
	    }

	    public void setType(String type) {
	        this.type = type;
	    }

	    public String getSubType() {
	        return subType;
	    }

	    public void setSubType(String subType) {
	        this.subType = subType;
	    }

	    public String getDescriptionText() {
	        return descriptionText;
	    }

	    public void setDescriptionText(String descriptionText) {
	        this.descriptionText = descriptionText;
	    }

	    public String getDate() {
	        return date;
	    }

	    public void setDate(String date) {
	        this.date = date;
	    }

	    public String getRequestingOrganisationTransactionReference() {
	        return requestingOrganisationTransactionReference;
	    }

	    public void setRequestingOrganisationTransactionReference(String requestingOrganisationTransactionReference) {
	        this.requestingOrganisationTransactionReference = requestingOrganisationTransactionReference;
	    }

	    public String getOneTimeCode() {
	        return oneTimeCode;
	    }

	    public void setOneTimeCode(String oneTimeCode) {
	        this.oneTimeCode = oneTimeCode;
	    }

	    public String getGeoCode() {
	        return geoCode;
	    }

	    public void setGeoCode(String geoCode) {
	        this.geoCode = geoCode;
	    }

	    public Kyc getSenderKyc() {
	        return senderKyc;
	    }

	    public void setSenderKyc(Kyc senderKyc) {
	        this.senderKyc = senderKyc;
	    }

	    public Kyc getReceiverKyc() {
	        return receiverKyc;
	    }

	    public void setReceiverKyc(Kyc receiverKyc) {
	        this.receiverKyc = receiverKyc;
	    }

	    public String getOriginalTransactionReference() {
	        return originalTransactionReference;
	    }

	    public void setOriginalTransactionReference(String originalTransactionReference) {
	        this.originalTransactionReference = originalTransactionReference;
	    }

	    public String getServicingIdentity() {
	        return servicingIdentity;
	    }

	    public void setServicingIdentity(String servicingIdentity) {
	        this.servicingIdentity = servicingIdentity;
	    }

	    public String getRequestingLei() {
	        return requestingLei;
	    }

	    public void setRequestingLei(String requestingLei) {
	        this.requestingLei = requestingLei;
	    }

	    public String getRecievingLei() {
	        return recievingLei;
	    }

	    public void setRecievingLei(String recievingLei) {
	        this.recievingLei = recievingLei;
	    }

	    public String getTransactionStatus() {
	        return transactionStatus;
	    }

	    public void setTransactionStatus(String transactionStatus) {
	        this.transactionStatus = transactionStatus;
	    }

	    public String getCreationDate() {
	        return creationDate;
	    }

	    public void setCreationDate(String creationDate) {
	        this.creationDate = creationDate;
	    }

	    public String getModificationDate() {
	        return modificationDate;
	    }

	    public void setModificationDate(String modificationDate) {
	        this.modificationDate = modificationDate;
	    }

	    public String getTransactionReference() {
	        return transactionReference;
	    }

	    public void setTransactionReference(String transactionReference) {
	        this.transactionReference = transactionReference;
	    }

	    public String getTransactionReceipt() {
	        return transactionReceipt;
	    }

	    public void setTransactionReceipt(String transactionReceipt) {
	        this.transactionReceipt = transactionReceipt;
	    }

	    public InternationalTransferInformation getInternationalTransferInformation() {
	        return internationalTransferInformation;
	    }

	    public void setInternationalTransferInformation(InternationalTransferInformation internationalTransferInformation) {
	        this.internationalTransferInformation = internationalTransferInformation;
	    }

	    public Party[] getDebitParty() {
	        return debitParty;
	    }

	    public void setDebitParty(Party[] debitParty) {
	        this.debitParty = debitParty;
	    }

	    public Party[] getCreditParty() {
	        return creditParty;
	    }

	    public void setCreditParty(Party[] creditParty) {
	        this.creditParty = creditParty;
	    }

	    public Party[] getMetadata() {
	        return metadata;
	    }

	    public void setMetadata(Party[] metadata) {
	        this.metadata = metadata;
	    }

	    @Override
	    public String toString() {
	        return "{" +
	                "amount='" + amount + '\'' +
	                ", currency='" + currency + '\'' +
	                ", type='" + type + '\'' +
	                ", subType='" + subType + '\'' +
	                ", descriptionText='" + descriptionText + '\'' +
	                ", date='" + date + '\'' +
	                ", requestingOrganisationTransactionReference='" + requestingOrganisationTransactionReference + '\'' +
	                ", oneTimeCode='" + oneTimeCode + '\'' +
	                ", geoCode='" + geoCode + '\'' +
	                ", debitParty=" + Arrays.toString(debitParty) +
	                ", creditParty=" + Arrays.toString(creditParty) +
	                ", senderKyc=" + senderKyc +
	                ", receiverKyc=" + receiverKyc +
	                ", originalTransactionReference='" + originalTransactionReference + '\'' +
	                ", servicingIdentity='" + servicingIdentity + '\'' +
	                ", requestingLei='" + requestingLei + '\'' +
	                ", recievingLei='" + recievingLei + '\'' +
	                ", metadata=" + Arrays.toString(metadata) +
	                ", transactionStatus='" + transactionStatus + '\'' +
	                ", creationDate='" + creationDate + '\'' +
	                ", modificationDate='" + modificationDate + '\'' +
	                ", transactionReference='" + transactionReference + '\'' +
	                ", transactionReceipt='" + transactionReceipt + '\'' +
	                ", internationalTransferInformation=" + internationalTransferInformation +
	                '}';
	    }
}
