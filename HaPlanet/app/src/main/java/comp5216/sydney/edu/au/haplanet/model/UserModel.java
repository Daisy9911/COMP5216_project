package comp5216.sydney.edu.au.haplanet.model;

public class UserModel {

    private String uid;
    private String email;

    public UserModel() {}

    public UserModel(String uid, String email) {
        this.uid = uid;
        this.email = email;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
