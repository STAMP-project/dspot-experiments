package com.baeldung.docx;


import org.junit.Assert;
import org.junit.Test;


public class Docx4jReadAndWriteIntegrationTest {
    private static final String imagePath = "src/main/resources/image.jpg";

    private static final String outputPath = "helloWorld.docx";

    @Test
    public void givenWordPackage_whenTextExist_thenReturnTrue() throws Exception {
        Docx4jExample docx4j = new Docx4jExample();
        docx4j.createDocumentPackage(Docx4jReadAndWriteIntegrationTest.outputPath, Docx4jReadAndWriteIntegrationTest.imagePath);
        Assert.assertTrue(docx4j.isTextExist("Hello World!"));
        Assert.assertTrue((!(docx4j.isTextExist("InexistantText"))));
    }
}

