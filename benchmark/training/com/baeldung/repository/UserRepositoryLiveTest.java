package com.baeldung.repository;


import Sort.Direction;
import com.baeldung.config.MongoConfig;
import com.baeldung.model.User;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * This test requires:
 * * mongodb instance running on the environment
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MongoConfig.class)
public class UserRepositoryLiveTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoOperations mongoOps;

    @Test
    public void whenInsertingUser_thenUserIsInserted() {
        final User user = new User();
        user.setName("Jon");
        userRepository.insert(user);
        Assert.assertThat(mongoOps.findOne(Query.query(Criteria.where("name").is("Jon")), User.class).getName(), CoreMatchers.is("Jon"));
    }

    @Test
    public void whenSavingNewUser_thenUserIsInserted() {
        final User user = new User();
        user.setName("Albert");
        userRepository.save(user);
        Assert.assertThat(mongoOps.findOne(Query.query(Criteria.where("name").is("Albert")), User.class).getName(), CoreMatchers.is("Albert"));
    }

    @Test
    public void givenUserExists_whenSavingExistUser_thenUserIsUpdated() {
        User user = new User();
        user.setName("Jack");
        mongoOps.insert(user);
        user = mongoOps.findOne(Query.query(Criteria.where("name").is("Jack")), User.class);
        user.setName("Jim");
        userRepository.save(user);
        Assert.assertThat(mongoOps.findAll(User.class).size(), CoreMatchers.is(1));
    }

    @Test
    public void givenUserExists_whenDeletingUser_thenUserIsDeleted() {
        final User user = new User();
        user.setName("Benn");
        mongoOps.insert(user);
        userRepository.delete(user);
        Assert.assertThat(mongoOps.find(Query.query(Criteria.where("name").is("Benn")), User.class).size(), CoreMatchers.is(0));
    }

    @Test
    public void givenUserExists_whenFindingUser_thenUserIsFound() {
        User user = new User();
        user.setName("Chris");
        mongoOps.insert(user);
        user = mongoOps.findOne(Query.query(Criteria.where("name").is("Chris")), User.class);
        final User foundUser = userRepository.findById(user.getId()).get();
        Assert.assertThat(user.getName(), CoreMatchers.is(foundUser.getName()));
    }

    @Test
    public void givenUserExists_whenCheckingDoesUserExist_thenUserIsExist() {
        User user = new User();
        user.setName("Harris");
        mongoOps.insert(user);
        user = mongoOps.findOne(Query.query(Criteria.where("name").is("Harris")), User.class);
        final boolean isExists = userRepository.existsById(user.getId());
        Assert.assertThat(isExists, CoreMatchers.is(true));
    }

    @Test
    public void givenUsersExist_whenFindingAllUsersWithSorting_thenUsersAreFoundAndSorted() {
        User user = new User();
        user.setName("Brendan");
        mongoOps.insert(user);
        user = new User();
        user.setName("Adam");
        mongoOps.insert(user);
        final List<User> users = userRepository.findAll(new org.springframework.data.domain.Sort(Direction.ASC, "name"));
        Assert.assertThat(users.size(), CoreMatchers.is(2));
        Assert.assertThat(users.get(0).getName(), CoreMatchers.is("Adam"));
        Assert.assertThat(users.get(1).getName(), CoreMatchers.is("Brendan"));
    }

    @Test
    public void givenUsersExist_whenFindingAllUsersWithPagination_thenUsersAreFoundAndOrderedOnPage() {
        User user = new User();
        user.setName("Brendan");
        mongoOps.insert(user);
        user = new User();
        user.setName("Adam");
        mongoOps.insert(user);
        final Pageable pageableRequest = PageRequest.of(0, 1);
        final Page<User> page = userRepository.findAll(pageableRequest);
        List<User> users = page.getContent();
        Assert.assertThat(users.size(), CoreMatchers.is(1));
        Assert.assertThat(page.getTotalPages(), CoreMatchers.is(2));
    }
}

