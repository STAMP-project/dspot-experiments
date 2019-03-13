package com.alibaba.json.bvt.issue_1300;


import InternalProperties.JSON_FEATURE;
import SerializerFeature.DisableCircularReferenceDetect;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.jaxrs.FastJsonProvider;
import java.util.Date;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.server.JSONP;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


public class Issue1341 extends JerseyTest {
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

    static class FastJsonFeature implements Feature {
        private static final String JSON_FEATURE = Issue1341.FastJsonFeature.class.getSimpleName();

        public boolean configure(final FeatureContext context) {
            final Configuration config = context.getConfiguration();
            final String jsonFeature = CommonProperties.getValue(config.getProperties(), config.getRuntimeType(), InternalProperties.JSON_FEATURE, Issue1341.FastJsonFeature.JSON_FEATURE, String.class);
            // Other JSON providers registered.
            if (!(Issue1341.FastJsonFeature.JSON_FEATURE.equalsIgnoreCase(jsonFeature))) {
                return false;
            }
            // Disable other JSON providers.
            context.property(PropertiesHelper.getPropertyNameForRuntime(InternalProperties.JSON_FEATURE, config.getRuntimeType()), Issue1341.FastJsonFeature.JSON_FEATURE);
            // Register FastJson.
            if (!(config.isRegistered(FastJsonProvider.class))) {
                // DisableCircularReferenceDetect
                FastJsonProvider fastJsonProvider = new FastJsonProvider();
                FastJsonConfig fastJsonConfig = new FastJsonConfig();
                // fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.BrowserSecure);
                fastJsonConfig.setSerializerFeatures(DisableCircularReferenceDetect);
                fastJsonProvider.setFastJsonConfig(fastJsonConfig);
                context.register(fastJsonProvider, MessageBodyReader.class, MessageBodyWriter.class);
            }
            return true;
        }
    }

    @Path("book1341")
    public static class BookRestFul {
        @GET
        @Path("{id}")
        @Produces({ "application/javascript", "application/json" })
        @Consumes({ "application/javascript", "application/json" })
        @JSONP(queryParam = "callback")
        public Issue1341.Book getBookById(@PathParam("id")
        Long id) {
            Issue1341.Book book = new Issue1341.Book();
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
        final String reponse = target("book1341").path("123").request().accept("application/javascript").get(String.class);
        System.out.println(reponse);
        Assert.assertTrue(((reponse.indexOf("Python????")) > 0));
        Assert.assertTrue(((reponse.indexOf("???????")) > 0));
    }
}

