package com.baeldung.xml;


import org.junit.Test;
import org.w3c.dom.Document;


public class XMLDocumentWriterUnitTest {
    @Test
    public void givenXMLDocumentWhenWriteIsCalledThenXMLIsWrittenToFile() throws Exception {
        Document document = createSampleDocument();
        new XMLDocumentWriter().write(document, "company_simple.xml", false, false);
    }

    @Test
    public void givenXMLDocumentWhenWriteIsCalledWithPrettyPrintThenFormattedXMLIsWrittenToFile() throws Exception {
        Document document = createSampleDocument();
        new XMLDocumentWriter().write(document, "company_prettyprinted.xml", false, true);
    }
}

