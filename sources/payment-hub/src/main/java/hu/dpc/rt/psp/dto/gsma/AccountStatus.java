package hu.dpc.rt.psp.dto.gsma;

/*
{
    "status": "available",
    "subStatus": null,
    "lei": "AAAA0012345678901299"
}
 */

public class AccountStatus {

    String status;
    String subStatus;
    String lei;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(String subStatus) {
        this.subStatus = subStatus;
    }

    public String getLei() {
        return lei;
    }

    public void setLei(String lei) {
        this.lei = lei;
    }
}
