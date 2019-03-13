package com.baeldung.springboothibernate.application.tests;


import com.baeldung.springboothibernate.application.models.Book;
import com.baeldung.springboothibernate.application.services.BookService;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class BookServiceUnitTest {
    @Autowired
    private BookService bookService;

    @Test
    public void test() {
        List<Book> books = bookService.list();
        Assert.assertEquals(books.size(), 3);
    }
}

