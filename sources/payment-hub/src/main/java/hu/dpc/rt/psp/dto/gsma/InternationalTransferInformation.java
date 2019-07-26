package hu.dpc.rt.psp.dto.gsma;

/*
  "internationalTransferInformation": {
    "originCountry": "AD",
    "quotationReference": "string",
    "quoteId": "string",
    "receivingCountry": "AD",
    "remittancePurpose": "string",
    "relationshipSender": "string",
    "deliveryMethod": "directtoaccount",
    "senderBlockingReason": "string",
    "recipientBlockingReason": "string"
  }
 */

public class InternationalTransferInformation {

    String originCountry;
    String quotationReference;
    String quoteId;

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public String getQuotationReference() {
        return quotationReference;
    }

    public void setQuotationReference(String quotationReference) {
        this.quotationReference = quotationReference;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getReceivingCountry() {
        return receivingCountry;
    }

    public void setReceivingCountry(String receivingCountry) {
        this.receivingCountry = receivingCountry;
    }

    public String getRemittancePurpose() {
        return remittancePurpose;
    }

    public void setRemittancePurpose(String remittancePurpose) {
        this.remittancePurpose = remittancePurpose;
    }

    public String getRelationshipSender() {
        return relationshipSender;
    }

    public void setRelationshipSender(String relationshipSender) {
        this.relationshipSender = relationshipSender;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getSenderBlockingReason() {
        return senderBlockingReason;
    }

    public void setSenderBlockingReason(String senderBlockingReason) {
        this.senderBlockingReason = senderBlockingReason;
    }

    public String getRecipientBlockingReason() {
        return recipientBlockingReason;
    }

    public void setRecipientBlockingReason(String recipientBlockingReason) {
        this.recipientBlockingReason = recipientBlockingReason;
    }

    String receivingCountry;
    String remittancePurpose;
    String relationshipSender;
    String deliveryMethod;
    String senderBlockingReason;
    String recipientBlockingReason;

}
