package fr.epitech.project.manager.service;

import fr.epitech.project.manager.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    /**
     * Create a new user
     * @param email : email
     * @param password : password
     * @return : UserDto
     */
    UserDto createUser(String email, String password);

    /**
     * Delete an user by its id
     * @param id : id
     */
    void deleteUser(Integer id);

    /**
     * Return a list of all users
     * @return : List<UserDto>
     */
    List<UserDto> getUsers();

    /**
     * Return on user by id
     * @return UserDto
     * @param id : id
     */
    UserDto getUser(Integer id);

    /**
     * Update an user
     * @param id : id
     * @param userDto : userDto
     */
    void updateUser(Integer id, UserDto userDto);
}
