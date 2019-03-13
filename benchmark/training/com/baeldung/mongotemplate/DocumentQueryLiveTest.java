package com.baeldung.mongotemplate;


import Sort.Direction;
import com.baeldung.config.MongoConfig;
import com.baeldung.model.User;
import java.util.Iterator;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
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
public class DocumentQueryLiveTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void givenUsersExist_whenFindingUsersByName_thenUsersAreFound() {
        User user = new User();
        user.setName("Eric");
        user.setAge(45);
        mongoTemplate.insert(user);
        user = new User();
        user.setName("Antony");
        user.setAge(55);
        mongoTemplate.insert(user);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is("Eric"));
        List<User> users = mongoTemplate.find(query, User.class);
        Assert.assertThat(users.size(), CoreMatchers.is(1));
    }

    @Test
    public void givenUsersExist_whenFindingUserWithAgeLessThan50AndGreateThan20_thenUsersAreFound() {
        User user = new User();
        user.setAge(20);
        user.setName("Jon");
        mongoTemplate.insert(user);
        user = new User();
        user.setAge(50);
        user.setName("Jon");
        mongoTemplate.insert(user);
        user = new User();
        user.setAge(33);
        user.setName("Jim");
        mongoTemplate.insert(user);
        Query query = new Query();
        query.addCriteria(Criteria.where("age").lt(40).gt(26));
        List<User> users = mongoTemplate.find(query, User.class);
        Assert.assertThat(users.size(), CoreMatchers.is(1));
    }

    @Test
    public void givenUsersExist_whenFindingUserWithNameStartWithA_thenUsersAreFound() {
        User user = new User();
        user.setName("Eric");
        user.setAge(45);
        mongoTemplate.insert(user);
        user = new User();
        user.setName("Antony");
        user.setAge(33);
        mongoTemplate.insert(user);
        user = new User();
        user.setName("Alice");
        user.setAge(35);
        mongoTemplate.insert(user);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex("^A"));
        List<User> users = mongoTemplate.find(query, User.class);
        Assert.assertThat(users.size(), CoreMatchers.is(2));
    }

    @Test
    public void givenUsersExist_whenFindingUserWithNameEndWithC_thenUsersAreFound() {
        User user = new User();
        user.setName("Eric");
        user.setAge(45);
        mongoTemplate.insert(user);
        user = new User();
        user.setName("Antony");
        user.setAge(33);
        mongoTemplate.insert(user);
        user = new User();
        user.setName("Alice");
        user.setAge(35);
        mongoTemplate.insert(user);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex("c$"));
        List<User> users = mongoTemplate.find(query, User.class);
        Assert.assertThat(users.size(), CoreMatchers.is(1));
    }

    @Test
    public void givenUsersExist_whenFindingByPage_thenUsersAreFoundByPage() {
        User user = new User();
        user.setName("Eric");
        user.setAge(45);
        mongoTemplate.insert(user);
        user = new User();
        user.setName("Antony");
        user.setAge(33);
        mongoTemplate.insert(user);
        user = new User();
        user.setName("Alice");
        user.setAge(35);
        mongoTemplate.insert(user);
        final Pageable pageableRequest = PageRequest.of(0, 2);
        Query query = new Query();
        query.with(pageableRequest);
        List<User> users = mongoTemplate.find(query, User.class);
        Assert.assertThat(users.size(), CoreMatchers.is(2));
    }

    @Test
    public void givenUsersExist_whenFindingUsersAndSortThem_thenUsersAreFoundAndSorted() {
        User user = new User();
        user.setName("Eric");
        user.setAge(45);
        mongoTemplate.insert(user);
        user = new User();
        user.setName("Antony");
        user.setAge(33);
        mongoTemplate.insert(user);
        user = new User();
        user.setName("Alice");
        user.setAge(35);
        mongoTemplate.insert(user);
        Query query = new Query();
        query.with(new org.springframework.data.domain.Sort(Direction.ASC, "age"));
        List<User> users = mongoTemplate.find(query, User.class);
        Iterator<User> iter = users.iterator();
        Assert.assertThat(users.size(), CoreMatchers.is(3));
        Assert.assertThat(iter.next().getName(), CoreMatchers.is("Antony"));
        Assert.assertThat(iter.next().getName(), CoreMatchers.is("Alice"));
        Assert.assertThat(iter.next().getName(), CoreMatchers.is("Eric"));
    }
}

