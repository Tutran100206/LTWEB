package vn.edu.de06.model;

public class User_24162141 implements java.io.Serializable {
    private int userId;
    private String username;
    private String email;
    private String fullname;
    private String password;
    private String images;
    private String phone;
    private int status;
    private String code;
    private int roleId;
    private Integer sellerId;
    private String roleName;
    private String sellername;
    public int getUserId() { return userId; }
    public void setUserId(int value) { this.userId = value; }
    public String getUsername() { return username; }
    public void setUsername(String value) { this.username = value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { this.email = value; }
    public String getFullname() { return fullname; }
    public void setFullname(String value) { this.fullname = value; }
    public String getPassword() { return password; }
    public void setPassword(String value) { this.password = value; }
    public String getImages() { return images; }
    public void setImages(String value) { this.images = value; }
    public String getPhone() { return phone; }
    public void setPhone(String value) { this.phone = value; }
    public int getStatus() { return status; }
    public void setStatus(int value) { this.status = value; }
    public String getCode() { return code; }
    public void setCode(String value) { this.code = value; }
    public int getRoleId() { return roleId; }
    public void setRoleId(int value) { this.roleId = value; }
    public Integer getSellerId() { return sellerId; }
    public void setSellerId(Integer value) { this.sellerId = value; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String value) { this.roleName = value; }
    public String getSellername() { return sellername; }
    public void setSellername(String value) { this.sellername = value; }
    public boolean isAdmin() { return "ROLE_ADMIN".equals(roleName); }
}
