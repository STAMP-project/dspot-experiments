package com.piggymetrics.auth.repository;


import com.piggymetrics.auth.domain.User;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@DataMongoTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Test
    public void shouldSaveAndFindUserByName() {
        User user = new User();
        user.setUsername("name");
        user.setPassword("password");
        repository.save(user);
        Optional<User> found = repository.findById(user.getUsername());
        Assert.assertTrue(found.isPresent());
        Assert.assertEquals(user.getUsername(), found.get().getUsername());
        Assert.assertEquals(user.getPassword(), found.get().getPassword());
    }
}

