package hu.dpc.rt.psp.dto.gsma;

/*
    "idDocument": [
      {
        "idType": "passport",
        "idNumber": "string",
        "issueDate": "string",
        "expiryDate": "string",
        "issuer": "string",
        "issuerPlace": "string",
        "issuerCountry": "AD",
        "otherIdDescription": "string"
      }
    ]

 */

public class IdDocument {

    String idType;
    String idNumber;
    String issueDate;
    String expiryDate;
    String issuer;
    String issuerPlace;
    String issuerCountry;
    String otherIdDescription;

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getIssuerPlace() {
        return issuerPlace;
    }

    public void setIssuerPlace(String issuerPlace) {
        this.issuerPlace = issuerPlace;
    }

    public String getIssuerCountry() {
        return issuerCountry;
    }

    public void setIssuerCountry(String issuerCountry) {
        this.issuerCountry = issuerCountry;
    }

    public String getOtherIdDescription() {
        return otherIdDescription;
    }

    public void setOtherIdDescription(String otherIdDescription) {
        this.otherIdDescription = otherIdDescription;
    }
}
