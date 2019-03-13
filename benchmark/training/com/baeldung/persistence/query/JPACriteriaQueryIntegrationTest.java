package com.baeldung.persistence.query;


import com.baeldung.persistence.dao.IUserDAO;
import com.baeldung.persistence.model.User;
import com.baeldung.spring.PersistenceConfig;
import com.baeldung.web.util.SearchCriteria;
import java.util.ArrayList;
import java.util.List;
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
public class JPACriteriaQueryIntegrationTest {
    @Autowired
    private IUserDAO userApi;

    private User userJohn;

    private User userTom;

    @Test
    public void givenFirstAndLastName_whenGettingListOfUsers_thenCorrect() {
        final List<SearchCriteria> params = new ArrayList<SearchCriteria>();
        params.add(new SearchCriteria("firstName", ":", "john"));
        params.add(new SearchCriteria("lastName", ":", "doe"));
        final List<User> results = userApi.searchUser(params);
        MatcherAssert.assertThat(userJohn, isIn(results));
        MatcherAssert.assertThat(userTom, IsNot.not(isIn(results)));
    }

    @Test
    public void givenLast_whenGettingListOfUsers_thenCorrect() {
        final List<SearchCriteria> params = new ArrayList<SearchCriteria>();
        params.add(new SearchCriteria("lastName", ":", "doe"));
        final List<User> results = userApi.searchUser(params);
        MatcherAssert.assertThat(userJohn, isIn(results));
        MatcherAssert.assertThat(userTom, isIn(results));
    }

    @Test
    public void givenLastAndAge_whenGettingListOfUsers_thenCorrect() {
        final List<SearchCriteria> params = new ArrayList<SearchCriteria>();
        params.add(new SearchCriteria("lastName", ":", "doe"));
        params.add(new SearchCriteria("age", ">", "25"));
        final List<User> results = userApi.searchUser(params);
        MatcherAssert.assertThat(userTom, isIn(results));
        MatcherAssert.assertThat(userJohn, IsNot.not(isIn(results)));
    }

    @Test
    public void givenWrongFirstAndLast_whenGettingListOfUsers_thenCorrect() {
        final List<SearchCriteria> params = new ArrayList<SearchCriteria>();
        params.add(new SearchCriteria("firstName", ":", "adam"));
        params.add(new SearchCriteria("lastName", ":", "fox"));
        final List<User> results = userApi.searchUser(params);
        MatcherAssert.assertThat(userJohn, IsNot.not(isIn(results)));
        MatcherAssert.assertThat(userTom, IsNot.not(isIn(results)));
    }

    @Test
    public void givenPartialFirst_whenGettingListOfUsers_thenCorrect() {
        final List<SearchCriteria> params = new ArrayList<SearchCriteria>();
        params.add(new SearchCriteria("firstName", ":", "jo"));
        final List<User> results = userApi.searchUser(params);
        MatcherAssert.assertThat(userJohn, isIn(results));
        MatcherAssert.assertThat(userTom, IsNot.not(isIn(results)));
    }
}

