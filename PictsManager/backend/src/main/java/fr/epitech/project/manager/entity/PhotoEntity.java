package fr.epitech.project.manager.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "photos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ownerId", nullable = false)
    private UserEntity ownerId;

    @ManyToOne
    @JoinColumn(name = "albumId", nullable = false)
    private AlbumEntity albumId;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "createdAt")
    private Timestamp createdAt;

    @Column(name = "updatedAt")
    private Timestamp updatedAt;
}