package fr.epitech.project.manager.controller;

import fr.epitech.project.manager.dto.UserDto;
import fr.epitech.project.manager.repository.UserRepository;
import fr.epitech.project.manager.service.UserService;
import fr.epitech.project.manager.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// @PreAuthorize("hasAuhority('MON_ROLE_ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping(value = "/api/users/signup")
    public ResponseEntity<UserDto> createUser(@RequestParam String email, @RequestParam String password)
    {
        if (!UserUtil.validate(email))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        UserDto userDto = userService.createUser(email, password);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/api/auth/users/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Integer id) {

        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // @PreAuthorize("hasAuthority('MON_ROLE') and OU or hasAuthority('MON_2ND_ROLE')")
    @GetMapping(value = "/api/auth/users")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> userDtoList = userService.getUsers();

        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }

    @GetMapping(value = "/api/auth/users/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Integer id) {
        UserDto userDto = userService.getUser(id);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PatchMapping("/api/auth/users/{id}")
    public ResponseEntity<HttpStatus> updateUser(@PathVariable("id") Integer id,
                                             @RequestBody UserDto userDto) {
        userService.updateUser(id, userDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}