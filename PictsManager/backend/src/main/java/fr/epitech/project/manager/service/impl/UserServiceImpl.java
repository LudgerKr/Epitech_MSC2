package fr.epitech.project.manager.service.impl;

import fr.epitech.project.manager.dto.UserDto;
import fr.epitech.project.manager.dto.mapper.UserMapper;
import fr.epitech.project.manager.entity.UserEntity;
import fr.epitech.project.manager.repository.UserRepository;
import fr.epitech.project.manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service("userDetailsService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String email)
    {
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity == null)
            throw new UsernameNotFoundException("L'utilisateur n'existe pas" + email);

        return new User(userEntity.getEmail(), userEntity.getPassword(), Collections.emptyList());
    }


    @Override
    @Transactional
    public UserDto createUser(String email, String password)
    {
        PasswordEncoder hashPassword = this.passwordEncoder();
        UserEntity userEntity = UserEntity.builder()
                                        .email(email)
                                        .password(hashPassword.encode(password))
                                        .createdAt(new Timestamp(System.currentTimeMillis()))
                                        .updatedAt(new Timestamp(System.currentTimeMillis()))
                                        .build();

        userEntity = userRepository.save(userEntity);

        return UserDto.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getUsers()
    {
        List<UserEntity> userEntityList = userRepository.findAll();
        return userMapper.convertListUserEntity(userEntityList);
    }

    @Override
    public UserDto getUser(Integer id)
    {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        if(userEntity == null)
            return null;
        return userMapper.convert(userEntity);
    }

    @Override
    @Transactional
    public void updateUser(Integer id, UserDto userDto)
    {
        Optional<UserEntity> userEntityOptional = userRepository.findById(id);
        if (!userEntityOptional.isPresent()) {
            ResponseEntity.notFound().build();
            return;
        }

        UserEntity userEntity = userEntityOptional.get();

        if (userDto.getEmail() != null) {
            userEntity.setEmail(userDto.getEmail());
        }
        if (userDto.getPassword() != null) {
            PasswordEncoder hashPassword = this.passwordEncoder();
            userEntity.setPassword(hashPassword.encode(userDto.getPassword()));
        }

        userDto.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        userRepository.save(userEntity);
    }
}
