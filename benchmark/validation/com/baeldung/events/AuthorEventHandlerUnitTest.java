package com.baeldung.events;


import com.baeldung.models.Author;
import org.junit.Test;
import org.mockito.Mockito;


public class AuthorEventHandlerUnitTest {
    @Test
    public void whenCreateAuthorThenSuccess() {
        Author author = Mockito.mock(Author.class);
        AuthorEventHandler authorEventHandler = new AuthorEventHandler();
        authorEventHandler.handleAuthorBeforeCreate(author);
        Mockito.verify(author, Mockito.times(1)).getName();
    }

    @Test
    public void whenDeleteAuthorThenSuccess() {
        Author author = Mockito.mock(Author.class);
        AuthorEventHandler authorEventHandler = new AuthorEventHandler();
        authorEventHandler.handleAuthorAfterDelete(author);
        Mockito.verify(author, Mockito.times(1)).getName();
    }
}

