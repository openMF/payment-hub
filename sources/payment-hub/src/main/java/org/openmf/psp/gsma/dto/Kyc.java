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

/*
"senderKyc": {
    "nationality": "AD",
    "dateOfBirth": "string",
    "occupation": "string",
    "employerName": "string",
    "contactPhone": "string",
    "gender": "m",
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
    ],
    "postalAddress": {
      "addressLine1": "string",
      "addressLine2": "string",
      "addressLine3": "string",
      "city": "string",
      "stateProvince": "string",
      "postalCode": "string",
      "country": "AD"
    },
    "subjectName": {
      "title": "string",
      "firstName": "string",
      "middleName": "string",
      "lastName": "string",
      "fullName": "string",
      "nativeName": "string"
    },
    "emailAddress": "string",
    "birthCountry": "AD"
  }
 */

public class Kyc {

    String nationality;
    String dateOfBirth;
    String occupation;
    String employerName;
    String contactPhone;
    char gender;

    IdDocument[] idDocument;

    PostalAddress postalAddress;

    SubjectName subjectName;

    String emailAddress;
    String birthCountry;

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public IdDocument[] getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(IdDocument[] idDocument) {
        this.idDocument = idDocument;
    }

    public PostalAddress getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(PostalAddress postalAddress) {
        this.postalAddress = postalAddress;
    }

    public SubjectName getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(SubjectName subjectName) {
        this.subjectName = subjectName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
    }



}

