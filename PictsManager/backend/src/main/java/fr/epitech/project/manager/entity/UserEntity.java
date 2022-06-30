package fr.epitech.project.manager.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "profilePhoto", nullable = false)
    private String profilePhoto;

    @Column(name = "createdAt")
    private Timestamp createdAt;

    @Column(name = "updatedAt")
    private Timestamp updatedAt;

}