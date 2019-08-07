package hu.dpc.rt.psp.dto.gsma;

/*
{
  "locale": "en",
  "dateFormat": "dd MMMM yyyy",
  "transactionDate": "27 May 2013",
  "transactionAmount": "500",
  "paymentTypeId": "14",
  "accountNumber": "acc123",
  "checkNumber": "che123",
  "routingCode": "rou123",
  "receiptNumber": "rec123"
  "bankNumber": "ban123"
}
 */

public class FineractTransactionsObject {

    public FineractTransactionsObject() {
    }

    String locale;
    String dateFormat;
    String transactionDate;
    String transactionAmount;
    String paymentTypeId;
    String accountNumber;
    String checkNumber;
    String routingCode;
    String receiptNumber;
    String bankNumber;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(String paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getRoutingCode() {
        return routingCode;
    }

    public void setRoutingCode(String routingCode) {
        this.routingCode = routingCode;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }
}

