package com.baeldung.persistence.query;


import com.baeldung.persistence.dao.GenericSpecificationsBuilder;
import com.baeldung.persistence.dao.UserRepository;
import com.baeldung.persistence.dao.UserSpecification;
import com.baeldung.persistence.dao.UserSpecificationsBuilder;
import com.baeldung.persistence.model.User;
import com.baeldung.spring.PersistenceConfig;
import com.baeldung.web.util.CriteriaParser;
import com.baeldung.web.util.SearchOperation;
import com.baeldung.web.util.SpecSearchCriteria;
import java.util.List;
import java.util.function.Function;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceConfig.class })
@Transactional
@Rollback
public class JPASpecificationIntegrationTest {
    @Autowired
    private UserRepository repository;

    private User userJohn;

    private User userTom;

    private User userPercy;

    @Test
    public void givenFirstAndLastName_whenGettingListOfUsers_thenCorrect() {
        final UserSpecification spec = new UserSpecification(new SpecSearchCriteria("firstName", SearchOperation.EQUALITY, "john"));
        final UserSpecification spec1 = new UserSpecification(new SpecSearchCriteria("lastName", SearchOperation.EQUALITY, "doe"));
        final List<User> results = repository.findAll(Specification.where(spec).and(spec1));
        MatcherAssert.assertThat(userJohn, isIn(results));
        MatcherAssert.assertThat(userTom, IsNot.not(isIn(results)));
    }

    @Test
    public void givenFirstOrLastName_whenGettingListOfUsers_thenCorrect() {
        UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
        SpecSearchCriteria spec = new SpecSearchCriteria("firstName", SearchOperation.EQUALITY, "john");
        SpecSearchCriteria spec1 = new SpecSearchCriteria("'", "lastName", SearchOperation.EQUALITY, "doe");
        List<User> results = repository.findAll(builder.with(spec).with(spec1).build());
        MatcherAssert.assertThat(results, hasSize(2));
        MatcherAssert.assertThat(userJohn, isIn(results));
        MatcherAssert.assertThat(userTom, isIn(results));
    }

    @Test
    public void givenFirstOrLastNameAndAgeGenericBuilder_whenGettingListOfUsers_thenCorrect() {
        GenericSpecificationsBuilder<User> builder = new GenericSpecificationsBuilder();
        Function<SpecSearchCriteria, Specification<User>> converter = UserSpecification::new;
        CriteriaParser parser = new CriteriaParser();
        List<User> results = repository.findAll(builder.build(parser.parse("( lastName:doe OR firstName:john ) AND age:22"), converter));
        MatcherAssert.assertThat(results, hasSize(1));
        MatcherAssert.assertThat(userJohn, isIn(results));
        MatcherAssert.assertThat(userTom, IsNot.not(isIn(results)));
    }

    @Test
    public void givenFirstOrLastNameGenericBuilder_whenGettingListOfUsers_thenCorrect() {
        GenericSpecificationsBuilder<User> builder = new GenericSpecificationsBuilder();
        Function<SpecSearchCriteria, Specification<User>> converter = UserSpecification::new;
        builder.with("firstName", ":", "john", null, null);
        builder.with("'", "lastName", ":", "doe", null, null);
        List<User> results = repository.findAll(builder.build(converter));
        MatcherAssert.assertThat(results, hasSize(2));
        MatcherAssert.assertThat(userJohn, isIn(results));
        MatcherAssert.assertThat(userTom, isIn(results));
    }

    @Test
    public void givenFirstNameInverse_whenGettingListOfUsers_thenCorrect() {
        final UserSpecification spec = new UserSpecification(new SpecSearchCriteria("firstName", SearchOperation.NEGATION, "john"));
        final List<User> results = repository.findAll(Specification.where(spec));
        MatcherAssert.assertThat(userTom, isIn(results));
        MatcherAssert.assertThat(userJohn, IsNot.not(isIn(results)));
    }

    @Test
    public void givenMinAge_whenGettingListOfUsers_thenCorrect() {
        final UserSpecification spec = new UserSpecification(new SpecSearchCriteria("age", SearchOperation.GREATER_THAN, "25"));
        final List<User> results = repository.findAll(Specification.where(spec));
        MatcherAssert.assertThat(userTom, isIn(results));
        MatcherAssert.assertThat(userJohn, IsNot.not(isIn(results)));
    }

    @Test
    public void givenFirstNamePrefix_whenGettingListOfUsers_thenCorrect() {
        final UserSpecification spec = new UserSpecification(new SpecSearchCriteria("firstName", SearchOperation.STARTS_WITH, "jo"));
        final List<User> results = repository.findAll(spec);
        MatcherAssert.assertThat(userJohn, isIn(results));
        MatcherAssert.assertThat(userTom, IsNot.not(isIn(results)));
    }

    @Test
    public void givenFirstNameSuffix_whenGettingListOfUsers_thenCorrect() {
        final UserSpecification spec = new UserSpecification(new SpecSearchCriteria("firstName", SearchOperation.ENDS_WITH, "n"));
        final List<User> results = repository.findAll(spec);
        MatcherAssert.assertThat(userJohn, isIn(results));
        MatcherAssert.assertThat(userTom, IsNot.not(isIn(results)));
    }

    @Test
    public void givenFirstNameSubstring_whenGettingListOfUsers_thenCorrect() {
        final UserSpecification spec = new UserSpecification(new SpecSearchCriteria("firstName", SearchOperation.CONTAINS, "oh"));
        final List<User> results = repository.findAll(spec);
        MatcherAssert.assertThat(userJohn, isIn(results));
        MatcherAssert.assertThat(userTom, IsNot.not(isIn(results)));
    }

    @Test
    public void givenAgeRange_whenGettingListOfUsers_thenCorrect() {
        final UserSpecification spec = new UserSpecification(new SpecSearchCriteria("age", SearchOperation.GREATER_THAN, "20"));
        final UserSpecification spec1 = new UserSpecification(new SpecSearchCriteria("age", SearchOperation.LESS_THAN, "25"));
        final List<User> results = repository.findAll(Specification.where(spec).and(spec1));
        MatcherAssert.assertThat(userJohn, isIn(results));
        MatcherAssert.assertThat(userTom, IsNot.not(isIn(results)));
    }
}

