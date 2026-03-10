package app;

/**
 * Singleton that holds the currently logged-in admin's display name.
 * Set once by LoginController after successful authentication,
 * then read by every page controller to populate the sidebar.
 */
public class UserSession {

    private static UserSession instance;

    private String fullName  = "Admin";
    private String initials  = "AD";
    private String role      = "Administrator";
    private String pendingAction = null;  // e.g. "ADD_EMPLOYEE", "ADD_DEPARTMENT", "ADD_LEAVE"

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUser(String fullName, String role) {
        this.fullName = (fullName != null && !fullName.isBlank()) ? fullName : "Admin";
        this.role     = (role != null && !role.isBlank()) ? role : "Administrator";

        // Build initials from up to the first two words
        String[] parts = this.fullName.trim().split("\\s+");
        if (parts.length >= 2) {
            this.initials = ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        } else if (parts.length == 1) {
            this.initials = parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        } else {
            this.initials = "AD";
        }
    }

    public String getFullName()  { return fullName;  }
    public String getInitials()  { return initials;  }
    public String getRole()      { return role;      }

    /** Set an action to be consumed by the next page controller on initialize. */
    public void setPendingAction(String action) { this.pendingAction = action; }

    /**
     * Reads and clears the pending action (consume-once pattern).
     * Returns null if no action is pending.
     */
    public String consumePendingAction() {
        String a = pendingAction;
        pendingAction = null;
        return a;
    }
}

