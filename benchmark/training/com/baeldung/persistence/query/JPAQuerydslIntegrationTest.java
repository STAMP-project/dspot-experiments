package com.baeldung.persistence.query;


import com.baeldung.persistence.dao.MyUserPredicatesBuilder;
import com.baeldung.persistence.dao.MyUserRepository;
import com.baeldung.persistence.model.MyUser;
import com.baeldung.spring.PersistenceConfig;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceConfig.class })
@Transactional
@Rollback
public class JPAQuerydslIntegrationTest {
    @Autowired
    private MyUserRepository repo;

    private MyUser userJohn;

    private MyUser userTom;

    @Test
    public void givenLast_whenGettingListOfUsers_thenCorrect() {
        final MyUserPredicatesBuilder builder = new MyUserPredicatesBuilder().with("lastName", ":", "doe");
        final Iterable<MyUser> results = repo.findAll(builder.build());
        MatcherAssert.assertThat(results, containsInAnyOrder(userJohn, userTom));
    }

    @Test
    public void givenFirstAndLastName_whenGettingListOfUsers_thenCorrect() {
        final MyUserPredicatesBuilder builder = new MyUserPredicatesBuilder().with("firstName", ":", "john").with("lastName", ":", "doe");
        final Iterable<MyUser> results = repo.findAll(builder.build());
        MatcherAssert.assertThat(results, contains(userJohn));
        MatcherAssert.assertThat(results, IsNot.not(contains(userTom)));
    }

    @Test
    public void givenLastAndAge_whenGettingListOfUsers_thenCorrect() {
        final MyUserPredicatesBuilder builder = new MyUserPredicatesBuilder().with("lastName", ":", "doe").with("age", ">", "25");
        final Iterable<MyUser> results = repo.findAll(builder.build());
        MatcherAssert.assertThat(results, contains(userTom));
        MatcherAssert.assertThat(results, IsNot.not(contains(userJohn)));
    }

    @Test
    public void givenWrongFirstAndLast_whenGettingListOfUsers_thenCorrect() {
        final MyUserPredicatesBuilder builder = new MyUserPredicatesBuilder().with("firstName", ":", "adam").with("lastName", ":", "fox");
        final Iterable<MyUser> results = repo.findAll(builder.build());
        MatcherAssert.assertThat(results, emptyIterable());
    }

    @Test
    public void givenPartialFirst_whenGettingListOfUsers_thenCorrect() {
        final MyUserPredicatesBuilder builder = new MyUserPredicatesBuilder().with("firstName", ":", "jo");
        final Iterable<MyUser> results = repo.findAll(builder.build());
        MatcherAssert.assertThat(results, contains(userJohn));
        MatcherAssert.assertThat(results, IsNot.not(contains(userTom)));
    }
}

