package systembatarka.com.batarka.app.batarkasystem.Data;

/**
 * Created by MorcosS on 7/25/16.
 */
public class m5dumData {
    String m5dumName, m5dumPhoto, m5dumAddress, m5dumFloorNo,m5dumFlatNo,m5dumMobile,m5dumPhone,m5dumFatherMob
            ,m5dumMotherMob,m5dumPoints,dob,m5dumID;

    public String getM5dumID() {
        return m5dumID;
    }

    public m5dumData(String m5dumName, String m5dumPhoto, String m5dumAddress, String m5dumFloorNo, String m5dumFlatNo,
                     String m5dumMobile, String m5dumPhone, String m5dumFatherMob, String m5dumMotherMob, String m5dumPoints, String dob, String m5dumID) {
        this.m5dumName = m5dumName;
        this.m5dumPhoto = m5dumPhoto;
        this.m5dumAddress = m5dumAddress;
        this.m5dumFloorNo = m5dumFloorNo;
        this.m5dumFlatNo = m5dumFlatNo;
        this.m5dumMobile = m5dumMobile;
        this.m5dumPhone = m5dumPhone;
        this.m5dumFatherMob = m5dumFatherMob;
        this.m5dumMotherMob = m5dumMotherMob;
        this.m5dumPoints = m5dumPoints;
        this.dob = dob;
        this.m5dumID = m5dumID;
    }


    public String getDob() {
        return dob;
    }

    public String getM5dumName() {
        return m5dumName;
    }

    public String getM5dumPhoto() {
        return m5dumPhoto;
    }

    public String getM5dumAddress() {
        return m5dumAddress;
    }

    public String getM5dumFloorNo() {
        return m5dumFloorNo;
    }

    public String getM5dumFlatNo() {
        return m5dumFlatNo;
    }

    public String getM5dumMobile() {
        return m5dumMobile;
    }

    public String getM5dumPhone() {
        return m5dumPhone;
    }

    public String getM5dumFatherMob() {
        return m5dumFatherMob;
    }

    public String getM5dumMotherMob() {
        return m5dumMotherMob;
    }

    public String getM5dumPoints() {
        return m5dumPoints;
    }
}

