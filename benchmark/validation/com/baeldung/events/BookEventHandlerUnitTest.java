package com.baeldung.events;


import com.baeldung.models.Author;
import com.baeldung.models.Book;
import org.junit.Test;
import org.mockito.Mockito;


public class BookEventHandlerUnitTest {
    @Test
    public void whenCreateBookThenSuccess() {
        Book book = Mockito.mock(Book.class);
        BookEventHandler bookEventHandler = new BookEventHandler();
        bookEventHandler.handleBookBeforeCreate(book);
        Mockito.verify(book, Mockito.times(1)).getAuthors();
    }

    @Test
    public void whenCreateAuthorThenSuccess() {
        Author author = Mockito.mock(Author.class);
        BookEventHandler bookEventHandler = new BookEventHandler();
        bookEventHandler.handleAuthorBeforeCreate(author);
        Mockito.verify(author, Mockito.times(1)).getBooks();
    }
}

