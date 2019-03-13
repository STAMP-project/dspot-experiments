package com.baeldung.jooq.springboot;


import AUTHOR.FIRST_NAME;
import AUTHOR.ID;
import AUTHOR.LAST_NAME;
import AUTHOR_BOOK.AUTHOR_ID;
import AUTHOR_BOOK.BOOK_ID;
import BOOK.TITLE;
import com.baeldung.jooq.introduction.PersistenceContextIntegrationTest;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@ContextConfiguration(classes = PersistenceContextIntegrationTest.class)
@Transactional(transactionManager = "transactionManager")
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringBootIntegrationTest {
    @Autowired
    private DSLContext dsl;

    @Test
    public void givenValidData_whenInserting_thenSucceed() {
        dsl.insertInto(AUTHOR).set(ID, 4).set(FIRST_NAME, "Herbert").set(LAST_NAME, "Schildt").execute();
        dsl.insertInto(BOOK).set(BOOK.ID, 4).set(TITLE, "A Beginner's Guide").execute();
        dsl.insertInto(AUTHOR_BOOK).set(AUTHOR_ID, 4).set(BOOK_ID, 4).execute();
        final Result<Record3<Integer, String, Integer>> result = dsl.select(ID, LAST_NAME, DSL.count()).from(AUTHOR).join(AUTHOR_BOOK).on(ID.equal(AUTHOR_ID)).join(BOOK).on(BOOK_ID.equal(BOOK.ID)).groupBy(LAST_NAME).fetch();
        Assert.assertEquals(3, result.size());
        Assert.assertEquals("Sierra", result.getValue(0, LAST_NAME));
        Assert.assertEquals(Integer.valueOf(2), result.getValue(0, DSL.count()));
        Assert.assertEquals("Schildt", result.getValue(2, LAST_NAME));
        Assert.assertEquals(Integer.valueOf(1), result.getValue(2, DSL.count()));
    }

    @Test(expected = DataAccessException.class)
    public void givenInvalidData_whenInserting_thenFail() {
        dsl.insertInto(AUTHOR_BOOK).set(AUTHOR_ID, 4).set(BOOK_ID, 5).execute();
    }

    @Test
    public void givenValidData_whenUpdating_thenSucceed() {
        dsl.update(AUTHOR).set(LAST_NAME, "Baeldung").where(ID.equal(3)).execute();
        dsl.update(BOOK).set(TITLE, "Building your REST API with Spring").where(BOOK.ID.equal(3)).execute();
        dsl.insertInto(AUTHOR_BOOK).set(AUTHOR_ID, 3).set(BOOK_ID, 3).execute();
        final Result<Record3<Integer, String, String>> result = dsl.select(ID, LAST_NAME, TITLE).from(AUTHOR).join(AUTHOR_BOOK).on(ID.equal(AUTHOR_ID)).join(BOOK).on(BOOK_ID.equal(BOOK.ID)).where(ID.equal(3)).fetch();
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(Integer.valueOf(3), result.getValue(0, ID));
        Assert.assertEquals("Baeldung", result.getValue(0, LAST_NAME));
        Assert.assertEquals("Building your REST API with Spring", result.getValue(0, TITLE));
    }

    @Test(expected = DataAccessException.class)
    public void givenInvalidData_whenUpdating_thenFail() {
        dsl.update(AUTHOR_BOOK).set(AUTHOR_ID, 4).set(BOOK_ID, 5).execute();
    }

    @Test
    public void givenValidData_whenDeleting_thenSucceed() {
        dsl.delete(AUTHOR).where(ID.lt(3)).execute();
        final Result<Record3<Integer, String, String>> result = dsl.select(ID, FIRST_NAME, LAST_NAME).from(AUTHOR).fetch();
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("Bryan", result.getValue(0, FIRST_NAME));
        Assert.assertEquals("Basham", result.getValue(0, LAST_NAME));
    }

    @Test(expected = DataAccessException.class)
    public void givenInvalidData_whenDeleting_thenFail() {
        dsl.delete(BOOK).where(BOOK.ID.equal(1)).execute();
    }
}

