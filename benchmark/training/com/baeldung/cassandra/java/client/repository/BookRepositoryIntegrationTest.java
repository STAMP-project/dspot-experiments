package com.baeldung.cassandra.java.client.repository;


import com.baeldung.cassandra.java.client.domain.Book;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.datastax.driver.core.utils.UUIDs;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class BookRepositoryIntegrationTest {
    private KeyspaceRepository schemaRepository;

    private BookRepository bookRepository;

    private Session session;

    final String KEYSPACE_NAME = "testLibrary";

    final String BOOKS = "books";

    final String BOOKS_BY_TITLE = "booksByTitle";

    @Test
    public void whenCreatingATable_thenCreatedCorrectly() {
        bookRepository.deleteTable(BOOKS);
        bookRepository.createTable();
        ResultSet result = session.execute((((("SELECT * FROM " + (KEYSPACE_NAME)) + ".") + (BOOKS)) + ";"));
        // Collect all the column names in one list.
        List<String> columnNames = result.getColumnDefinitions().asList().stream().map(( cl) -> cl.getName()).collect(Collectors.toList());
        Assert.assertEquals(columnNames.size(), 4);
        Assert.assertTrue(columnNames.contains("id"));
        Assert.assertTrue(columnNames.contains("title"));
        Assert.assertTrue(columnNames.contains("author"));
        Assert.assertTrue(columnNames.contains("subject"));
    }

    @Test
    public void whenAlteringTable_thenAddedColumnExists() {
        bookRepository.deleteTable(BOOKS);
        bookRepository.createTable();
        bookRepository.alterTablebooks("publisher", "text");
        ResultSet result = session.execute((((("SELECT * FROM " + (KEYSPACE_NAME)) + ".") + (BOOKS)) + ";"));
        boolean columnExists = result.getColumnDefinitions().asList().stream().anyMatch(( cl) -> cl.getName().equals("publisher"));
        Assert.assertTrue(columnExists);
    }

    @Test
    public void whenAddingANewBook_thenBookExists() {
        bookRepository.deleteTable(BOOKS_BY_TITLE);
        bookRepository.createTableBooksByTitle();
        String title = "Effective Java";
        String author = "Joshua Bloch";
        Book book = new Book(UUIDs.timeBased(), title, author, "Programming");
        bookRepository.insertbookByTitle(book);
        Book savedBook = bookRepository.selectByTitle(title);
        Assert.assertEquals(book.getTitle(), savedBook.getTitle());
    }

    @Test
    public void whenAddingANewBookBatch_ThenBookAddedInAllTables() {
        // Create table books
        bookRepository.deleteTable(BOOKS);
        bookRepository.createTable();
        // Create table booksByTitle
        bookRepository.deleteTable(BOOKS_BY_TITLE);
        bookRepository.createTableBooksByTitle();
        String title = "Effective Java";
        String author = "Joshua Bloch";
        Book book = new Book(UUIDs.timeBased(), title, author, "Programming");
        bookRepository.insertBookBatch(book);
        List<Book> books = bookRepository.selectAll();
        Assert.assertEquals(1, books.size());
        Assert.assertTrue(books.stream().anyMatch(( b) -> b.getTitle().equals("Effective Java")));
        List<Book> booksByTitle = bookRepository.selectAllBookByTitle();
        Assert.assertEquals(1, booksByTitle.size());
        Assert.assertTrue(booksByTitle.stream().anyMatch(( b) -> b.getTitle().equals("Effective Java")));
    }

    @Test
    public void whenSelectingAll_thenReturnAllRecords() {
        bookRepository.deleteTable(BOOKS);
        bookRepository.createTable();
        Book book = new Book(UUIDs.timeBased(), "Effective Java", "Joshua Bloch", "Programming");
        bookRepository.insertbook(book);
        book = new Book(UUIDs.timeBased(), "Clean Code", "Robert C. Martin", "Programming");
        bookRepository.insertbook(book);
        List<Book> books = bookRepository.selectAll();
        Assert.assertEquals(2, books.size());
        Assert.assertTrue(books.stream().anyMatch(( b) -> b.getTitle().equals("Effective Java")));
        Assert.assertTrue(books.stream().anyMatch(( b) -> b.getTitle().equals("Clean Code")));
    }

    @Test
    public void whenDeletingABookByTitle_thenBookIsDeleted() {
        bookRepository.deleteTable(BOOKS_BY_TITLE);
        bookRepository.createTableBooksByTitle();
        Book book = new Book(UUIDs.timeBased(), "Effective Java", "Joshua Bloch", "Programming");
        bookRepository.insertbookByTitle(book);
        book = new Book(UUIDs.timeBased(), "Clean Code", "Robert C. Martin", "Programming");
        bookRepository.insertbookByTitle(book);
        bookRepository.deletebookByTitle("Clean Code");
        List<Book> books = bookRepository.selectAllBookByTitle();
        Assert.assertEquals(1, books.size());
        Assert.assertTrue(books.stream().anyMatch(( b) -> b.getTitle().equals("Effective Java")));
        Assert.assertFalse(books.stream().anyMatch(( b) -> b.getTitle().equals("Clean Code")));
    }

    @Test(expected = InvalidQueryException.class)
    public void whenDeletingATable_thenUnconfiguredTable() {
        bookRepository.createTable();
        bookRepository.deleteTable(BOOKS);
        session.execute((((("SELECT * FROM " + (KEYSPACE_NAME)) + ".") + (BOOKS)) + ";"));
    }
}

