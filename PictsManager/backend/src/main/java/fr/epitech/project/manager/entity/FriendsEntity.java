package fr.epitech.project.manager.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "friends")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity userId;

    @ManyToOne
    @JoinColumn(name = "friendId", nullable = false)
    private UserEntity friendId;
}