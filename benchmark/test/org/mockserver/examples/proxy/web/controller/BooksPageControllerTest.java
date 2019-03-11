package org.mockserver.examples.proxy.web.controller;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockserver.examples.proxy.model.Book;
import org.mockserver.examples.proxy.service.BookService;
import org.springframework.ui.Model;


/**
 *
 *
 * @author jamesdbloom
 */
public class BooksPageControllerTest {
    @Mock
    private BookService bookService;

    @InjectMocks
    private BooksPageController booksPageController;

    @Test
    public void shouldLoadListOfBooks() {
        // given
        Model mockModel = Mockito.mock(Model.class);
        Book[] bookList = new Book[]{  };
        Mockito.when(bookService.getAllBooks()).thenReturn(bookList);
        // when
        String viewName = booksPageController.getBookList(mockModel);
        // then
        Assert.assertEquals("books", viewName);
        Mockito.verify(mockModel).addAttribute(ArgumentMatchers.eq("books"), ArgumentMatchers.eq(bookList));
    }

    @Test
    public void shouldLoadSingleBook() {
        // given
        Model mockModel = Mockito.mock(Model.class);
        Book book = new Book(1, "title", "author", "isbn", "publicationDate");
        Mockito.when(bookService.getBook("1")).thenReturn(book);
        // when
        String viewName = booksPageController.getBook("1", mockModel);
        // then
        Assert.assertEquals("book", viewName);
        Mockito.verify(mockModel).addAttribute(ArgumentMatchers.eq("book"), ArgumentMatchers.same(book));
    }
}

