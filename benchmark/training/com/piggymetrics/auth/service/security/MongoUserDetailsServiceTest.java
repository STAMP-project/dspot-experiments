package com.piggymetrics.auth.service.security;


import com.piggymetrics.auth.domain.User;
import com.piggymetrics.auth.repository.UserRepository;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class MongoUserDetailsServiceTest {
    @InjectMocks
    private MongoUserDetailsService service;

    @Mock
    private UserRepository repository;

    @Test
    public void shouldLoadByUsernameWhenUserExists() {
        final User user = new User();
        Mockito.when(repository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(user));
        UserDetails loaded = service.loadUserByUsername("name");
        Assert.assertEquals(user, loaded);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void shouldFailToLoadByUsernameWhenUserNotExists() {
        service.loadUserByUsername("name");
    }
}

