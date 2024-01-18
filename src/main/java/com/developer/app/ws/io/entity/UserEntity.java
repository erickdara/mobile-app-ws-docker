package com.developer.app.ws.io.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity(name = "users")
@Getter
@Setter
public class UserEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 120, unique = true)
    private String email;

    @Column(nullable = false)
    private String encryptedPassword;
    private String emailVerificationToken;

    @Column(nullable = true, columnDefinition = "boolean default false")
    private Boolean emailVerificationStatus;

    @OneToMany(mappedBy = "userDetails", cascade = CascadeType.ALL)
    private List<AddressEntity> addresses;

}
