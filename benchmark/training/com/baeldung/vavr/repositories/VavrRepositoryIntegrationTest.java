package com.baeldung.vavr.repositories;


import com.baeldung.Application;
import com.baeldung.repositories.VavrUserRepository;
import com.baeldung.vavr.User;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class VavrRepositoryIntegrationTest {
    @Autowired
    private VavrUserRepository userRepository;

    @Test
    public void whenAddUsers_thenGetUsers() {
        Option<User> user = userRepository.findById(1L);
        Assert.assertFalse(user.isEmpty());
        Assert.assertTrue(user.get().getName().equals("John"));
        Seq<User> users = userRepository.findByName("John");
        Assert.assertEquals(2, users.size());
    }
}

