package org.javaee7.jaxws.client;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import org.javaee7.jaxws.client.gen.EBook;
import org.javaee7.jaxws.client.gen.EBookStore;
import org.javaee7.jaxws.client.gen.EBookStoreImplService;
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
public class EBookStoreClientSampleTest {
    private static EBookStoreImplService eBookStoreService;

    @ArquillianResource
    private URL url;

    @Test
    public void test1WelcomeMessage() throws MalformedURLException {
        EBookStore eBookStore = EBookStoreClientSampleTest.eBookStoreService.getEBookStoreImplPort();
        String response = eBookStore.welcomeMessage("Jackson");
        Assert.assertEquals("Welcome to EBookStore WebService, Mr/Mrs Jackson", response);
    }

    @Test
    public void test2SaveAndTakeBook() throws MalformedURLException {
        EBookStore eBookStore = EBookStoreClientSampleTest.eBookStoreService.getPort(EBookStore.class);
        EBook eBook = new EBook();
        eBook.setTitle("The Jungle Book");
        eBook.setNumPages(225);
        eBook.setPrice(17.9);
        eBookStore.saveBook(eBook);
        eBook = new EBook();
        eBook.setTitle("Animal Farm");
        eBook.setNumPages(113);
        eBook.setPrice(22.5);
        List<String> notes = Arrays.asList(new String[]{ "Great book", "Not too bad" });
        eBook.getNotes().addAll(notes);
        eBookStore.saveBook(eBook);
        EBook response = eBookStore.takeBook("Animal Farm");
        Assert.assertEquals(eBook.getNumPages(), response.getNumPages());
        Assert.assertEquals(eBook.getPrice(), response.getPrice(), 0);
        Assert.assertEquals(eBook.getTitle(), response.getTitle());
        Assert.assertEquals(notes, response.getNotes());
    }
}

