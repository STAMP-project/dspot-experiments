package com.piggymetrics.auth.service;


import com.piggymetrics.auth.domain.User;
import com.piggymetrics.auth.repository.UserRepository;
import java.util.Optional;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository repository;

    @Test
    public void shouldCreateUser() {
        User user = new User();
        user.setUsername("name");
        user.setPassword("password");
        userService.create(user);
        Mockito.verify(repository, Mockito.times(1)).save(user);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenUserAlreadyExists() {
        User user = new User();
        user.setUsername("name");
        user.setPassword("password");
        Mockito.when(repository.findById(user.getUsername())).thenReturn(Optional.of(new User()));
        userService.create(user);
    }
}

