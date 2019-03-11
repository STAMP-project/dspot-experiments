package com.baeldung.feign.clients;


import com.baeldung.feign.models.Book;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Consumes https://github.com/Baeldung/spring-hypermedia-api
 */
@Slf4j
@RunWith(JUnit4.class)
public class BookClientLiveTest {
    private BookClient bookClient;

    @Test
    public void givenBookClient_shouldRunSuccessfully() throws Exception {
        List<Book> books = bookClient.findAll().stream().map(BookResource::getBook).collect(Collectors.toList());
        Assert.assertTrue(((books.size()) > 2));
        log.info("{}", books);
    }

    @Test
    public void givenBookClient_shouldFindOneBook() throws Exception {
        Book book = bookClient.findByIsbn("0151072558").getBook();
        Assert.assertThat(book.getAuthor(), CoreMatchers.containsString("Orwell"));
        log.info("{}", book);
    }

    @Test
    public void givenBookClient_shouldPostBook() throws Exception {
        String isbn = UUID.randomUUID().toString();
        Book book = new Book(isbn, "Me", "It's me!", null, null);
        bookClient.create(book);
        book = bookClient.findByIsbn(isbn).getBook();
        Assert.assertThat(book.getAuthor(), CoreMatchers.is("Me"));
        log.info("{}", book);
    }
}

