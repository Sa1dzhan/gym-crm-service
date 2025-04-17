package com.gymcrm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_acc")
@Getter
@Setter
@RequiredArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
        @NamedQuery(
                name = "User.findByUsername",
                query = "SELECT t FROM User t WHERE t.username = :username"
        ),
        @NamedQuery(
                name = "User.findAll",
                query = "SELECT t FROM User t"
        ),
        @NamedQuery(
                name = "User.findAllByUsername",
                query = "SELECT t FROM User t WHERE t.username IN :usernames"
        ),
        @NamedQuery(
                name = "User.existsByUsername",
                query = "SELECT t FROM User t WHERE t.username = :username"
        )
})
public class User {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false, columnDefinition = "boolean DEFAULT true")
    private Boolean isActive = true;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
