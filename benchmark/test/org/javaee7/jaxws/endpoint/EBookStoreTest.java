package org.javaee7.jaxws.endpoint;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.xml.ws.Service;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


/**
 *
 *
 * @author Fermin Gallego
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EBookStoreTest {
    private static Service eBookStoreService;

    @ArquillianResource
    private URL url;

    @Test
    public void test1WelcomeMessage() throws MalformedURLException {
        EBookStore eBookStore = EBookStoreTest.eBookStoreService.getPort(EBookStore.class);
        String response = eBookStore.welcomeMessage("Johnson");
        Assert.assertEquals("Welcome to EBookStore WebService, Mr/Mrs Johnson", response);
    }

    @Test
    public void test2SaveAndTakeBook() throws MalformedURLException {
        EBookStore eBookStore = EBookStoreTest.eBookStoreService.getPort(EBookStore.class);
        EBook eBook = new EBook();
        eBook.setTitle("The Lord of the Rings");
        eBook.setNumPages(1178);
        eBook.setPrice(21.8);
        eBookStore.saveBook(eBook);
        eBook = new EBook();
        eBook.setTitle("Oliver Twist");
        eBook.setNumPages(268);
        eBook.setPrice(7.45);
        eBookStore.saveBook(eBook);
        EBook response = eBookStore.takeBook("Oliver Twist");
        Assert.assertEquals(eBook.getNumPages(), response.getNumPages());
    }

    @Test
    public void test3FindEbooks() {
        EBookStore eBookStore = EBookStoreTest.eBookStoreService.getPort(EBookStore.class);
        List<String> titleList = eBookStore.findEBooks("Rings");
        Assert.assertNotNull(titleList);
        Assert.assertEquals(1, titleList.size());
        Assert.assertEquals("The Lord of the Rings", titleList.get(0));
    }

    @Test
    public void test4AddAppendix() {
        EBookStore eBookStore = EBookStoreTest.eBookStoreService.getPort(EBookStore.class);
        EBook eBook = eBookStore.takeBook("Oliver Twist");
        Assert.assertEquals(268, eBook.getNumPages());
        EBook eBookResponse = eBookStore.addAppendix(eBook, 5);
        Assert.assertEquals(268, eBook.getNumPages());
        Assert.assertEquals(273, eBookResponse.getNumPages());
    }
}

