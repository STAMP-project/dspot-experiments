package spark;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class BooksIntegrationTest {
    private static int PORT = 4567;

    private static String AUTHOR = "FOO";

    private static String TITLE = "BAR";

    private static String NEW_TITLE = "SPARK";

    private String bookId;

    @Test
    public void canCreateBook() {
        BooksIntegrationTest.UrlResponse response = createBookViaPOST();
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.body);
        Assert.assertTrue(((Integer.valueOf(response.body)) > 0));
        Assert.assertEquals(201, response.status);
    }

    @Test
    public void canListBooks() {
        bookId = createBookViaPOST().body.trim();
        BooksIntegrationTest.UrlResponse response = BooksIntegrationTest.doMethod("GET", "/books", null);
        Assert.assertNotNull(response);
        String body = response.body.trim();
        Assert.assertNotNull(body);
        Assert.assertTrue(((Integer.valueOf(body)) > 0));
        Assert.assertEquals(200, response.status);
        Assert.assertTrue(response.body.contains(bookId));
    }

    @Test
    public void canGetBook() {
        bookId = createBookViaPOST().body.trim();
        BooksIntegrationTest.UrlResponse response = BooksIntegrationTest.doMethod("GET", ("/books/" + (bookId)), null);
        String result = response.body;
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.body);
        Assert.assertEquals(200, response.status);
        Assert.assertTrue(result.contains(BooksIntegrationTest.AUTHOR));
        Assert.assertTrue(result.contains(BooksIntegrationTest.TITLE));
        Assert.assertTrue(beforeFilterIsSet(response));
        Assert.assertTrue(afterFilterIsSet(response));
    }

    @Test
    public void canUpdateBook() {
        bookId = createBookViaPOST().body.trim();
        BooksIntegrationTest.UrlResponse response = updateBook();
        String result = response.body;
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.body);
        Assert.assertEquals(200, response.status);
        Assert.assertTrue(result.contains(bookId));
        Assert.assertTrue(result.contains("updated"));
    }

    @Test
    public void canGetUpdatedBook() {
        bookId = createBookViaPOST().body.trim();
        updateBook();
        BooksIntegrationTest.UrlResponse response = BooksIntegrationTest.doMethod("GET", ("/books/" + (bookId)), null);
        String result = response.body;
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.body);
        Assert.assertEquals(200, response.status);
        Assert.assertTrue(result.contains(BooksIntegrationTest.AUTHOR));
        Assert.assertTrue(result.contains(BooksIntegrationTest.NEW_TITLE));
    }

    @Test
    public void canDeleteBook() {
        bookId = createBookViaPOST().body.trim();
        BooksIntegrationTest.UrlResponse response = BooksIntegrationTest.doMethod("DELETE", ("/books/" + (bookId)), null);
        String result = response.body;
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.body);
        Assert.assertEquals(200, response.status);
        Assert.assertTrue(result.contains(bookId));
        Assert.assertTrue(result.contains("deleted"));
    }

    @Test(expected = FileNotFoundException.class)
    public void wontFindBook() throws IOException {
        BooksIntegrationTest.getResponse("GET", ("/books/" + (bookId)), null);
    }

    private static class UrlResponse {
        public Map<String, List<String>> headers;

        private String body;

        private int status;
    }
}

