package com.querydsl.example.jpa.repository;


import com.querydsl.example.jpa.model.User;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;


public class UserRepositoryTest extends AbstractPersistenceTest {
    @Inject
    private UserRepository repository;

    @Test
    public void save_and_get_by_id() {
        String username = "jackie";
        User user = new User(username);
        repository.save(user);
        Assert.assertEquals(username, repository.findById(user.getId()).getUsername());
    }

    @Test
    public void get_all() {
        repository.save(new User("jimmy"));
        Assert.assertTrue(((repository.all().size()) == 1));
    }
}

