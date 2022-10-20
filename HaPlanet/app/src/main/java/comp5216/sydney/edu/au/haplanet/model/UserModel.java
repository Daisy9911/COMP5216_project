package comp5216.sydney.edu.au.haplanet.model;

public class UserModel {

    private String uid;
    private String email;

    private String avatarUrl;
    private String username;
    private String introduction;

    public UserModel() {}

    public UserModel(String uid, String email, String avatarUrl,
                     String username, String introduction) {

        this.uid = uid;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.username = username;
        this.introduction = introduction;

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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}