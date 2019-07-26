package hu.dpc.rt.psp.dto.gsma;

/*
"subjectName": {
      "title": "string",
      "firstName": "string",
      "middleName": "string",
      "lastName": "string",
      "fullName": "string",
      "nativeName": "string"
    }
*/

public class SubjectName {

    String title;
    String firstName;
    String middleName;
    String lastName;
    String nativeName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }
}
