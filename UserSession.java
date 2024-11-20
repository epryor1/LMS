
public class UserSession {
    private int userId;
    private String role;
    private String memberId;

    public UserSession(int userId, String role, String memberId) {
        this.userId = userId;
        this.role = role;
        this.memberId = memberId;
    }

    public int getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getMemberId() {
        return memberId;
    }
}

