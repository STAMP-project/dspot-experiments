package com.baeldung.jaxb.test;


import com.baeldung.jaxb.Book;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JaxbIntegrationTest {
    private Book book;

    private JAXBContext context;

    @Test
    public void marshal() throws IOException, JAXBException {
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(book, new File(((this.getClass().getResource("/").getPath()) + "/book.xml")));
        File sampleBookFile = new File(this.getClass().getResource("/sample_book.xml").getFile());
        File bookFile = new File(this.getClass().getResource("/book.xml").getFile());
        String sampleBookXML = FileUtils.readFileToString(sampleBookFile, "UTF-8");
        String marshallerBookXML = FileUtils.readFileToString(bookFile, "UTF-8");
        Assert.assertEquals(sampleBookXML.replace("\r", "").replace("\n", ""), marshallerBookXML.replace("\r", "").replace("\n", ""));
    }

    @Test
    public void unMashal() throws IOException, JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        String bookFile = this.getClass().getResource("/book.xml").getFile();
        Book unMarshallerbook = ((Book) (unmarshaller.unmarshal(new FileReader(bookFile))));
        Assert.assertEquals(book, unMarshallerbook);
    }
}

