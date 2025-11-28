package com.orderapp.customerservice.entity.customerdb;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false, length = 20)
    private String phone;

    @Column(unique = true)
    private String email;

    private String password; // Hashed password

    @Column(columnDefinition = "TEXT")
    private String avatar;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // --- Social Login IDs ---
    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(name = "google_name")
    private String googleName;

    @Column(name = "facebook_id", unique = true)
    private String facebookId;

    @Column(name = "facebook_name")
    private String facebookName;

    @Column(name = "apple_id", unique = true)
    private String appleId;

    @Column(name = "apple_name")
    private String appleName;

    // --- Bank Information ---
    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

    @Column(name = "bank_account_name")
    private String bankAccountName;

    // --- Status & Verification ---
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false) // Chỉ định độ dài và không null
    @ColumnDefault("'PENDING'") // Chỉ định giá trị mặc định cho DB (lưu ý dấu nháy đơn bên trong)
    private CustomerStatus status;

    @Column(length = 50)
    private String step;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "is_login")
    @Builder.Default
    private Boolean isLogin = false;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    // --- Referral & Tokens ---
    @Column(name = "referral_code", unique = true, length = 50)
    private String referralCode;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "fcm_token", columnDefinition = "TEXT")
    private String fcmToken;

    // --- Timestamps ---
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = CustomerStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Enum cho Status
    public enum CustomerStatus {
        PENDING, ACTIVE, INACTIVE
    }


    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public String getName() { return fullName; }
    public void setName(String name) { this.fullName = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }


    public Boolean getVerified() { return isVerified; }
    public void setVerified(Boolean verified) { this.isVerified = verified; }


    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }

    public LocalDateTime getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(LocalDateTime lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    public Boolean getLogin() { return isLogin; }
    public void setLogin(Boolean login) { this.isLogin = login; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }


}