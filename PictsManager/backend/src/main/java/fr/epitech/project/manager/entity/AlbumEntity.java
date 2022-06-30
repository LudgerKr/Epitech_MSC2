package fr.epitech.project.manager.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "albums")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ownerId", nullable = false)
    private UserEntity ownerId;

    @ManyToOne
    @JoinColumn(name = "userShareId")
    private UserEntity userShareId;

    @Column(name = "isPublic", nullable = false)
    private boolean isPublic;

    @Column(name = "createdAt")
    private Timestamp createdAt;

    @Column(name = "updatedAt")
    private Timestamp updatedAt;
}