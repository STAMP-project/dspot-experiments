package com.alibaba.json.bvt.issue_1300;


import SerializerFeature.BrowserSecure;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import java.util.Date;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.server.JSONP;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


public class Issue1392 extends JerseyTest {
    static class Book {
        private int bookId;

        private String bookName;

        private String publisher;

        private String isbn;

        private Date publishTime;

        private Object hello;

        public int getBookId() {
            return bookId;
        }

        public void setBookId(int bookId) {
            this.bookId = bookId;
        }

        public String getBookName() {
            return bookName;
        }

        public void setBookName(String bookName) {
            this.bookName = bookName;
        }

        public String getPublisher() {
            return publisher;
        }

        public void setPublisher(String publisher) {
            this.publisher = publisher;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public Date getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(Date publishTime) {
            this.publishTime = publishTime;
        }

        public Object getHello() {
            return hello;
        }

        public void setHello(Object hello) {
            this.hello = hello;
        }
    }

    @Provider
    static class FastJsonResolver implements ContextResolver<FastJsonConfig> {
        public FastJsonConfig getContext(Class<?> type) {
            FastJsonConfig fastJsonConfig = new FastJsonConfig();
            fastJsonConfig.setSerializerFeatures(WriteMapNullValue, BrowserSecure);
            return fastJsonConfig;
        }
    }

    @Path("book1392")
    public static class BookRestFul {
        @GET
        @Path("{id}")
        @Produces({ "application/javascript", "application/json" })
        @Consumes({ "application/javascript", "application/json" })
        @JSONP(queryParam = "callback")
        public Issue1392.Book getBookById(@PathParam("id")
        Long id) {
            Issue1392.Book book = new Issue1392.Book();
            book.setBookId(2);
            book.setBookName("Python????");
            book.setPublisher("???????");
            book.setPublishTime(new Date());
            book.setIsbn("911122");
            return book;
        }
    }

    @Test
    public void test() {
        final String reponse = target("book1392").path("123").request().accept("application/javascript").get(String.class);
        System.out.println(reponse);
        Assert.assertTrue(((reponse.indexOf("Python????")) > 0));
        Assert.assertTrue(((reponse.indexOf("???????")) > 0));
    }
}

