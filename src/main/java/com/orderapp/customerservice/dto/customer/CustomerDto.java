package com.orderapp.customerservice.dto.customer;

public class CustomerDto {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String status;
    private Boolean verified;
    private String type;

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
