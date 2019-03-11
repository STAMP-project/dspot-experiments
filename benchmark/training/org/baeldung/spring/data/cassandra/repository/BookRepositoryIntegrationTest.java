package org.baeldung.spring.data.cassandra.repository;


import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.ImmutableSet;
import java.util.NoSuchElementException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.baeldung.spring.data.cassandra.config.CassandraConfig;
import org.baeldung.spring.data.cassandra.model.Book;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CassandraConfig.class)
public class BookRepositoryIntegrationTest {
    private static final Log LOGGER = LogFactory.getLog(BookRepositoryIntegrationTest.class);

    public static final String KEYSPACE_CREATION_QUERY = "CREATE KEYSPACE IF NOT EXISTS testKeySpace WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': '3' };";

    public static final String KEYSPACE_ACTIVATE_QUERY = "USE testKeySpace;";

    public static final String DATA_TABLE_NAME = "book";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CassandraAdminOperations adminTemplate;

    @Test
    public void whenSavingBook_thenAvailableOnRetrieval() {
        final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        bookRepository.save(ImmutableSet.of(javaBook));
        final Iterable<Book> books = bookRepository.findByTitleAndPublisher("Head First Java", "O'Reilly Media");
        Assert.assertEquals(javaBook.getId(), books.iterator().next().getId());
    }

    @Test
    public void whenUpdatingBooks_thenAvailableOnRetrieval() {
        final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        bookRepository.save(ImmutableSet.of(javaBook));
        final Iterable<Book> books = bookRepository.findByTitleAndPublisher("Head First Java", "O'Reilly Media");
        javaBook.setTitle("Head First Java Second Edition");
        bookRepository.save(ImmutableSet.of(javaBook));
        final Iterable<Book> updateBooks = bookRepository.findByTitleAndPublisher("Head First Java Second Edition", "O'Reilly Media");
        Assert.assertEquals(javaBook.getTitle(), updateBooks.iterator().next().getTitle());
    }

    @Test(expected = NoSuchElementException.class)
    public void whenDeletingExistingBooks_thenNotAvailableOnRetrieval() {
        final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        bookRepository.save(ImmutableSet.of(javaBook));
        bookRepository.delete(javaBook);
        final Iterable<Book> books = bookRepository.findByTitleAndPublisher("Head First Java", "O'Reilly Media");
        Assert.assertNotEquals(javaBook.getId(), books.iterator().next().getId());
    }

    @Test
    public void whenSavingBooks_thenAllShouldAvailableOnRetrieval() {
        final Book javaBook = new Book(UUIDs.timeBased(), "Head First Java", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        final Book dPatternBook = new Book(UUIDs.timeBased(), "Head Design Patterns", "O'Reilly Media", ImmutableSet.of("Computer", "Software"));
        bookRepository.save(ImmutableSet.of(javaBook));
        bookRepository.save(ImmutableSet.of(dPatternBook));
        final Iterable<Book> books = bookRepository.findAll();
        int bookCount = 0;
        for (final Book book : books) {
            bookCount++;
        }
        Assert.assertEquals(bookCount, 2);
    }
}

