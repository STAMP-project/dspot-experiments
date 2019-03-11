package org.mockserver.examples.proxy.web.controller;


import MediaType.TEXT_HTML;
import javax.annotation.Resource;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;
import org.mockserver.examples.proxy.servicebackend.BookServer;
import org.mockserver.examples.proxy.web.controller.pageobjects.BookPage;
import org.mockserver.examples.proxy.web.controller.pageobjects.BooksPage;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Parameter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;


/**
 *
 *
 * @author jamesdbloom
 */
public abstract class BooksPageEndToEndIntegrationTest {
    private static ClientAndServer proxy;

    @Resource
    private BookServer bookServer;

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Test
    public void shouldLoadListOfBooks() throws Exception {
        Assume.assumeThat("SOCKS5 is broken in JRE <9", System.getProperty("java.version"), Matchers.not(Matchers.anyOf(Matchers.startsWith("1.7."), Matchers.equalTo("1.7"), Matchers.startsWith("7."), Matchers.equalTo("7"), Matchers.startsWith("1.8."), Matchers.equalTo("1.8"), Matchers.startsWith("8."), Matchers.equalTo("8"))));
        // when
        MvcResult response = mockMvc.perform(get("/books").accept(TEXT_HTML)).andExpect(status().isOk()).andExpect(content().contentType("text/html; charset=utf-8")).andReturn();
        // then
        BooksPage booksPage = new BooksPage(response);
        booksPage.containsListOfBooks(bookServer.getBooksDB().values());
        BooksPageEndToEndIntegrationTest.proxy.verify(request().withPath("/get_books"), exactly(1));
    }

    @Test
    public void shouldLoadSingleBook() throws Exception {
        Assume.assumeThat("SOCKS5 is broken in JRE <9", System.getProperty("java.version"), Matchers.not(Matchers.anyOf(Matchers.startsWith("1.7."), Matchers.equalTo("1.7"), Matchers.startsWith("7."), Matchers.equalTo("7"), Matchers.startsWith("1.8."), Matchers.equalTo("1.8"), Matchers.startsWith("8."), Matchers.equalTo("8"))));
        // when
        MvcResult response = mockMvc.perform(get("/book/1").accept(TEXT_HTML)).andExpect(status().isOk()).andExpect(content().contentType("text/html; charset=utf-8")).andReturn();
        // then
        BookPage bookPage = new BookPage(response);
        bookPage.containsBook(get("1"));
        BooksPageEndToEndIntegrationTest.proxy.verify(request().withPath("/get_book").withQueryStringParameter(new Parameter("id", "1")), exactly(1));
    }
}

