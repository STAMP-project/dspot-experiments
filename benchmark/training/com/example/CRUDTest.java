package com.example;


import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.rapidoid.http.Self;
import org.rapidoid.u.U;


public class CRUDTest extends AbstractIntegrationTest {
    @Test
    public void shouldInsertBooks() {
        Book savedBook = Self.post("/books").data(javaBook()).toBean(Book.class);
        assertBookIs(savedBook, 1, "Java Book", 2016);
    }

    @Test
    public void shouldUpdateBooks() {
        Book savedBook = Self.post("/books").data(javaBook()).toBean(Book.class);
        assertBookIs(savedBook, 1, "Java Book", 2016);
        Book updatedBook = Self.put(("/books/" + (savedBook.id))).data(U.map("year", 2017, "title", "J")).toBean(Book.class);
        assertBookIs(updatedBook, 1, "J", 2017);
        List<Map<String, Object>> books = Self.get("/books").parse();
        eq(1, books.size());
        eq("J", books.get(0).get("title"));
    }

    @Test
    public void shouldReadBooks() {
        List<Map<String, Object>> books = Self.get("/books").parse();
        isTrue(books.isEmpty());
        Self.post("/books").data(javaBook()).execute();
        books = Self.get("/books").parse();
        eq(1, books.size());
    }
}

