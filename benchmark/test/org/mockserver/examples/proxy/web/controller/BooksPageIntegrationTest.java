package org.mockserver.examples.proxy.web.controller;


import MediaType.TEXT_HTML;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.util.Arrays;
import javax.annotation.Resource;
import org.junit.Test;
import org.mockserver.examples.proxy.model.Book;
import org.mockserver.examples.proxy.web.controller.pageobjects.BookPage;
import org.mockserver.examples.proxy.web.controller.pageobjects.BooksPage;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.Parameter;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;


/**
 *
 *
 * @author jamesdbloom
 */
public abstract class BooksPageIntegrationTest {
    static {
        MockServerLogger.setRootLogLevel("org.springframework");
        MockServerLogger.setRootLogLevel("org.eclipse");
    }

    private static ClientAndServer proxy;

    private ClientAndServer mockServer;

    @Resource
    private Environment environment;

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Test
    public void shouldLoadListOfBooks() throws Exception {
        // given
        mockServer.when(request().withPath("/get_books")).respond(response().withHeaders(new org.mockserver.model.Header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE)).withBody((((((((((((((((((((((((((((((((((((((((((((("" + "[") + (NEW_LINE)) + "    {") + (NEW_LINE)) + "        \"id\": \"1\",") + (NEW_LINE)) + "        \"title\": \"Xenophon\'s imperial fiction : on the education of Cyrus\",") + (NEW_LINE)) + "        \"author\": \"James Tatum\",") + (NEW_LINE)) + "        \"isbn\": \"0691067570\",") + (NEW_LINE)) + "        \"publicationDate\": \"1989\"") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    {") + (NEW_LINE)) + "        \"id\": \"2\",") + (NEW_LINE)) + "        \"title\": \"You are here : personal geographies and other maps of the imagination\",") + (NEW_LINE)) + "        \"author\": \"Katharine A. Harmon\",") + (NEW_LINE)) + "        \"isbn\": \"1568984308\",") + (NEW_LINE)) + "        \"publicationDate\": \"2004\"") + (NEW_LINE)) + "    },") + (NEW_LINE)) + "    {") + (NEW_LINE)) + "        \"id\": \"3\",") + (NEW_LINE)) + "        \"title\": \"You just don\'t understand : women and men in conversation\",") + (NEW_LINE)) + "        \"author\": \"Deborah Tannen\",") + (NEW_LINE)) + "        \"isbn\": \"0345372050\",") + (NEW_LINE)) + "        \"publicationDate\": \"1990\"") + (NEW_LINE)) + "    }") + "]")));
        // when
        MvcResult response = mockMvc.perform(get("/books").accept(TEXT_HTML)).andExpect(status().isOk()).andExpect(content().contentType("text/html; charset=utf-8")).andReturn();
        // then
        new BooksPage(response).containsListOfBooks(Arrays.asList(new Book(1, "Xenophon's imperial fiction : on the education of Cyrus", "James Tatum", "0691067570", "1989"), new Book(2, "You are here : personal geographies and other maps of the imagination", "Katharine A. Harmon", "1568984308", "2004"), new Book(3, "You just don't understand : women and men in conversation", "Deborah Tannen", "0345372050", "1990")));
        BooksPageIntegrationTest.proxy.verify(request().withPath("/get_books"), exactly(1));
    }

    @Test
    public void shouldLoadSingleBook() throws Exception {
        // given
        mockServer.when(request().withPath("/get_book").withQueryStringParameter(new Parameter("id", "1"))).respond(response().withHeaders(new org.mockserver.model.Header(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json")).withBody(((((((((((((("" + "{") + (NEW_LINE)) + "    \"id\": \"1\",") + (NEW_LINE)) + "    \"title\": \"Xenophon\'s imperial fiction : on the education of Cyrus\",") + (NEW_LINE)) + "    \"author\": \"James Tatum\",") + (NEW_LINE)) + "    \"isbn\": \"0691067570\",") + (NEW_LINE)) + "    \"publicationDate\": \"1989\"") + (NEW_LINE)) + "}")));
        // when
        MvcResult response = mockMvc.perform(get("/book/1").accept(TEXT_HTML)).andExpect(status().isOk()).andExpect(content().contentType("text/html; charset=utf-8")).andReturn();
        // then
        new BookPage(response).containsBook(new Book(1, "Xenophon's imperial fiction : on the education of Cyrus", "James Tatum", "0691067570", "1989"));
        BooksPageIntegrationTest.proxy.verify(request().withPath("/get_book").withQueryStringParameter(new Parameter("id", "1")), exactly(1));
    }
}

