package fr.epitech.project.manager.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "share")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ownerId", nullable = false)
    private UserEntity ownerId;

    @ManyToOne
    @JoinColumn(name = "friendId", nullable = false)
    private UserEntity friendId;

    @ManyToOne
    @JoinColumn(name = "albumId", nullable = false)
    private AlbumEntity albumId;
}