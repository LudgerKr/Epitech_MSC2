package fr.epitech.project.manager.dto;

import lombok.*;

import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Integer id;
    private String email;
    private String password;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}