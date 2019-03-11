package com.baeldung.persistence.service;


import com.baeldung.persistence.model.Foo;
import com.baeldung.spring.PersistenceConfig;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceConfig.class }, loader = AnnotationConfigContextLoader.class)
public class FooStoredProceduresLiveTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(FooStoredProceduresLiveTest.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private IFooService fooService;

    private Session session;

    @Test
    public final void getAllFoosUsingStoredProcedures() {
        fooService.create(new Foo(RandomStringUtils.randomAlphabetic(6)));
        // Stored procedure getAllFoos using createSQLQuery
        Query sqlQuery = session.createSQLQuery("CALL GetAllFoos()").addEntity(Foo.class);
        @SuppressWarnings("unchecked")
        List<Foo> allFoos = sqlQuery.list();
        for (Foo foo : allFoos) {
            FooStoredProceduresLiveTest.LOGGER.info("getAllFoos() SQL Query result : {}", foo.getName());
        }
        Assert.assertEquals(allFoos.size(), fooService.findAll().size());
        // Stored procedure getAllFoos using a Named Query
        Query namedQuery = session.getNamedQuery("callGetAllFoos");
        @SuppressWarnings("unchecked")
        List<Foo> allFoos2 = namedQuery.list();
        for (Foo foo : allFoos2) {
            FooStoredProceduresLiveTest.LOGGER.info("getAllFoos() NamedQuery result : {}", foo.getName());
        }
        Assert.assertEquals(allFoos2.size(), fooService.findAll().size());
    }

    @Test
    public final void getFoosByNameUsingStoredProcedures() {
        fooService.create(new Foo("NewFooName"));
        // Stored procedure getFoosByName using createSQLQuery()
        Query sqlQuery = session.createSQLQuery("CALL GetFoosByName(:fooName)").addEntity(Foo.class).setParameter("fooName", "NewFooName");
        @SuppressWarnings("unchecked")
        List<Foo> allFoosByName = sqlQuery.list();
        for (Foo foo : allFoosByName) {
            FooStoredProceduresLiveTest.LOGGER.info("getFoosByName() using SQL Query : found => {}", foo.toString());
        }
        // Stored procedure getFoosByName using getNamedQuery()
        Query namedQuery = session.getNamedQuery("callGetFoosByName").setParameter("fooName", "NewFooName");
        @SuppressWarnings("unchecked")
        List<Foo> allFoosByName2 = namedQuery.list();
        for (Foo foo : allFoosByName2) {
            FooStoredProceduresLiveTest.LOGGER.info("getFoosByName() using Native Query : found => {}", foo.toString());
        }
    }
}

