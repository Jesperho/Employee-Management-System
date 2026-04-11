package app;

import entity.Admin;

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
    private Admin admin;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUser(String fullName, String role, Admin admin) {
        this.fullName = (fullName != null && !fullName.isBlank()) ? fullName : "Admin";
        this.role     = (role != null && !role.isBlank()) ? role : "Administrator";
        this.admin = admin;

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

    public Admin getAdmin(){
        return admin;
    }
    public String getFullName()  { return fullName;  }
    public String getInitials()  { return initials;  }
    public String getRole()      { return role;      }
}

