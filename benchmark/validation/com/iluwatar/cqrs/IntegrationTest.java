/**
 * The MIT License
 * Copyright (c) 2014 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.cqrs;


import com.iluwatar.cqrs.commandes.ICommandService;
import com.iluwatar.cqrs.dto.Author;
import com.iluwatar.cqrs.dto.Book;
import com.iluwatar.cqrs.queries.IQueryService;
import java.math.BigInteger;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Integration test of IQueryService and ICommandService with h2 data
 */
public class IntegrationTest {
    private static IQueryService queryService;

    private static ICommandService commandService;

    @Test
    public void testGetAuthorByUsername() {
        Author author = IntegrationTest.queryService.getAuthorByUsername("username1");
        Assertions.assertEquals("username1", author.getUsername());
        Assertions.assertEquals("name1", author.getName());
        Assertions.assertEquals("email1", author.getEmail());
    }

    @Test
    public void testGetUpdatedAuthorByUsername() {
        Author author = IntegrationTest.queryService.getAuthorByUsername("new_username2");
        Author expectedAuthor = new Author("new_name2", "new_email2", "new_username2");
        Assertions.assertEquals(expectedAuthor, author);
    }

    @Test
    public void testGetBook() {
        Book book = IntegrationTest.queryService.getBook("title1");
        Assertions.assertEquals("title1", book.getTitle());
        Assertions.assertEquals(10, book.getPrice(), 0.01);
    }

    @Test
    public void testGetAuthorBooks() {
        List<Book> books = IntegrationTest.queryService.getAuthorBooks("username1");
        Assertions.assertEquals(2, books.size());
        Assertions.assertTrue(books.contains(new Book("title1", 10)));
        Assertions.assertTrue(books.contains(new Book("new_title2", 30)));
    }

    @Test
    public void testGetAuthorBooksCount() {
        BigInteger bookCount = IntegrationTest.queryService.getAuthorBooksCount("username1");
        Assertions.assertEquals(new BigInteger("2"), bookCount);
    }

    @Test
    public void testGetAuthorsCount() {
        BigInteger authorCount = IntegrationTest.queryService.getAuthorsCount();
        Assertions.assertEquals(new BigInteger("2"), authorCount);
    }
}

