package com.baeldung.repository;


import com.baeldung.config.MongoConfig;
import com.baeldung.model.User;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * This test requires:
 * * mongodb instance running on the environment
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MongoConfig.class)
public class JSONQueryLiveTest extends BaseQueryLiveTest {
    @Test
    public void givenUsersExist_whenFindingUsersByName_thenUsersAreFound() {
        User user = new User();
        user.setName("Eric");
        user.setAge(45);
        mongoOps.insert(user);
        user = new User();
        user.setName("Antony");
        user.setAge(55);
        mongoOps.insert(user);
        List<User> users = userRepository.findUsersByName("Eric");
        Assert.assertThat(users.size(), CoreMatchers.is(1));
    }

    @Test
    public void givenUsersExist_whenFindingUsersWithAgeCreaterThanAndLessThan_thenUsersAreFound() {
        User user = new User();
        user.setAge(20);
        user.setName("Jon");
        mongoOps.insert(user);
        user = new User();
        user.setAge(50);
        user.setName("Jon");
        mongoOps.insert(user);
        user = new User();
        user.setAge(33);
        user.setName("Jim");
        mongoOps.insert(user);
        List<User> users = userRepository.findUsersByAgeBetween(26, 40);
        Assert.assertThat(users.size(), CoreMatchers.is(1));
    }

    @Test
    public void givenUsersExist_whenFindingUserWithNameStartWithA_thenUsersAreFound() {
        User user = new User();
        user.setName("Eric");
        user.setAge(45);
        mongoOps.insert(user);
        user = new User();
        user.setName("Antony");
        user.setAge(33);
        mongoOps.insert(user);
        user = new User();
        user.setName("Alice");
        user.setAge(35);
        mongoOps.insert(user);
        List<User> users = userRepository.findUsersByRegexpName("^A");
        Assert.assertThat(users.size(), CoreMatchers.is(2));
    }

    @Test
    public void givenUsersExist_whenFindingUserWithNameEndWithC_thenUsersAreFound() {
        User user = new User();
        user.setName("Eric");
        user.setAge(45);
        mongoOps.insert(user);
        user = new User();
        user.setName("Antony");
        user.setAge(33);
        mongoOps.insert(user);
        user = new User();
        user.setName("Alice");
        user.setAge(35);
        mongoOps.insert(user);
        List<User> users = userRepository.findUsersByRegexpName("c$");
        Assert.assertThat(users.size(), CoreMatchers.is(1));
    }
}

