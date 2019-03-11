package com.baeldung.persistence.query;


import com.baeldung.persistence.dao.UserRepository;
import com.baeldung.persistence.model.User;
import com.baeldung.spring.PersistenceConfig;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import java.util.List;
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
public class RsqlIntegrationTest {
    @Autowired
    private UserRepository repository;

    private User userJohn;

    private User userTom;

    @Test
    public void givenFirstAndLastName_whenGettingListOfUsers_thenCorrect() {
        final Node rootNode = new RSQLParser().parse("firstName==john;lastName==doe");
        final Specification<User> spec = rootNode.accept(new com.baeldung.persistence.dao.rsql.CustomRsqlVisitor<User>());
        final List<User> results = repository.findAll(spec);
        MatcherAssert.assertThat(userJohn, isIn(results));
        MatcherAssert.assertThat(userTom, IsNot.not(isIn(results)));
    }

    @Test
    public void givenFirstNameInverse_whenGettingListOfUsers_thenCorrect() {
        final Node rootNode = new RSQLParser().parse("firstName!=john");
        final Specification<User> spec = rootNode.accept(new com.baeldung.persistence.dao.rsql.CustomRsqlVisitor<User>());
        final List<User> results = repository.findAll(spec);
        MatcherAssert.assertThat(userTom, isIn(results));
        MatcherAssert.assertThat(userJohn, IsNot.not(isIn(results)));
    }

    @Test
    public void givenMinAge_whenGettingListOfUsers_thenCorrect() {
        final Node rootNode = new RSQLParser().parse("age>25");
        final Specification<User> spec = rootNode.accept(new com.baeldung.persistence.dao.rsql.CustomRsqlVisitor<User>());
        final List<User> results = repository.findAll(spec);
        MatcherAssert.assertThat(userTom, isIn(results));
        MatcherAssert.assertThat(userJohn, IsNot.not(isIn(results)));
    }

    @Test
    public void givenFirstNamePrefix_whenGettingListOfUsers_thenCorrect() {
        final Node rootNode = new RSQLParser().parse("firstName==jo*");
        final Specification<User> spec = rootNode.accept(new com.baeldung.persistence.dao.rsql.CustomRsqlVisitor<User>());
        final List<User> results = repository.findAll(spec);
        MatcherAssert.assertThat(userJohn, isIn(results));
        MatcherAssert.assertThat(userTom, IsNot.not(isIn(results)));
    }

    @Test
    public void givenListOfFirstName_whenGettingListOfUsers_thenCorrect() {
        final Node rootNode = new RSQLParser().parse("firstName=in=(john,jack)");
        final Specification<User> spec = rootNode.accept(new com.baeldung.persistence.dao.rsql.CustomRsqlVisitor<User>());
        final List<User> results = repository.findAll(spec);
        MatcherAssert.assertThat(userJohn, isIn(results));
        MatcherAssert.assertThat(userTom, IsNot.not(isIn(results)));
    }
}

