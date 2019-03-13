package org.baeldung.spring.data.cassandra.repository;


import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.baeldung.spring.data.cassandra.config.CassandraConfig;
import org.baeldung.spring.data.cassandra.model.Book;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CassandraConfig.class)
public class CassandraTemplateIntegrationTest {
    private static final Log LOGGER = LogFactory.getLog(CassandraTemplateIntegrationTest.class);

    public static final String KEYSPACE_CREATION_QUERY = "CREATE KEYSPACE IF NOT EXISTS testKeySpace " + "WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '3' };";

    public static final String KEYSPACE_ACTIVATE_QUERY = "USE testKeySpace;";

    public static final String DATA_TABLE_NAME = "book";

    @Autowired
    private CassandraAdminOperations adminTemplate;

    @Autowired
    private CassandraOperations cassandraTemplate;

    @Test
    public void whenSavingBook_thenAvailableOnRetrieval() {
        final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        cassandraTemplate.insert(javaBook);
        final Select select = QueryBuilder.select().from("book").where(QueryBuilder.eq("title", "Head First Java")).and(QueryBuilder.eq("publisher", "O'Reilly Media")).limit(10);
        final Book retrievedBook = cassandraTemplate.selectOne(select, Book.class);
        Assert.assertEquals(javaBook.getId(), retrievedBook.getId());
    }

    @Test
    public void whenSavingBooks_thenAllAvailableOnRetrieval() {
        final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        final Book dPatternBook = new Book(UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        final List<Book> bookList = new ArrayList<>();
        bookList.add(javaBook);
        bookList.add(dPatternBook);
        cassandraTemplate.insert(bookList);
        final Select select = QueryBuilder.select().from("book").limit(10);
        final List<Book> retrievedBooks = cassandraTemplate.select(select, Book.class);
        Assert.assertThat(retrievedBooks.size(), CoreMatchers.is(2));
        Assert.assertEquals(javaBook.getId(), retrievedBooks.get(0).getId());
        Assert.assertEquals(dPatternBook.getId(), retrievedBooks.get(1).getId());
    }

    @Test
    public void whenUpdatingBook_thenShouldUpdatedOnRetrieval() {
        final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        cassandraTemplate.insert(javaBook);
        final Select select = QueryBuilder.select().from("book").limit(10);
        final Book retrievedBook = cassandraTemplate.selectOne(select, Book.class);
        retrievedBook.setTags(ImmutableSet.of("Java", "Programming"));
        cassandraTemplate.update(retrievedBook);
        final Book retrievedUpdatedBook = cassandraTemplate.selectOne(select, Book.class);
        Assert.assertEquals(retrievedBook.getTags(), retrievedUpdatedBook.getTags());
    }

    @Test
    public void whenDeletingASelectedBook_thenNotAvailableOnRetrieval() throws InterruptedException {
        final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "OReilly Media", ImmutableSet.of("Computer", "Software"));
        cassandraTemplate.insert(javaBook);
        cassandraTemplate.delete(javaBook);
        final Select select = QueryBuilder.select().from("book").limit(10);
        final Book retrievedUpdatedBook = cassandraTemplate.selectOne(select, Book.class);
        TestCase.assertNull(retrievedUpdatedBook);
    }

    @Test
    public void whenDeletingAllBooks_thenNotAvailableOnRetrieval() {
        final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        final Book dPatternBook = new Book(UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        cassandraTemplate.insert(javaBook);
        cassandraTemplate.insert(dPatternBook);
        cassandraTemplate.deleteAll(Book.class);
        final Select select = QueryBuilder.select().from("book").limit(10);
        final Book retrievedUpdatedBook = cassandraTemplate.selectOne(select, Book.class);
        TestCase.assertNull(retrievedUpdatedBook);
    }

    @Test
    public void whenAddingBooks_thenCountShouldBeCorrectOnRetrieval() {
        final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        final Book dPatternBook = new Book(UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        cassandraTemplate.insert(javaBook);
        cassandraTemplate.insert(dPatternBook);
        final long bookCount = cassandraTemplate.count(Book.class);
        Assert.assertEquals(2, bookCount);
    }
}

