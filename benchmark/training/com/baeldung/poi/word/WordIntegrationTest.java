package com.baeldung.poi.word;


import WordDocument.output;
import WordDocument.paragraph1;
import WordDocument.paragraph2;
import WordDocument.paragraph3;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.Assert;
import org.junit.Test;


public class WordIntegrationTest {
    static WordDocument wordDocument;

    @Test
    public void whenParsingOutputDocument_thenCorrect() throws Exception {
        Path msWordPath = Paths.get(output);
        XWPFDocument document = new XWPFDocument(Files.newInputStream(msWordPath));
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        document.close();
        XWPFParagraph title = paragraphs.get(0);
        XWPFRun titleRun = title.getRuns().get(0);
        Assert.assertEquals("Build Your REST API with Spring", title.getText());
        Assert.assertEquals("009933", titleRun.getColor());
        Assert.assertTrue(titleRun.isBold());
        Assert.assertEquals("Courier", titleRun.getFontFamily());
        Assert.assertEquals(20, titleRun.getFontSize());
        Assert.assertEquals("from HTTP fundamentals to API Mastery", paragraphs.get(1).getText());
        Assert.assertEquals("What makes a good API?", paragraphs.get(3).getText());
        Assert.assertEquals(WordIntegrationTest.wordDocument.convertTextFileToString(paragraph1), paragraphs.get(4).getText());
        Assert.assertEquals(WordIntegrationTest.wordDocument.convertTextFileToString(paragraph2), paragraphs.get(5).getText());
        Assert.assertEquals(WordIntegrationTest.wordDocument.convertTextFileToString(paragraph3), paragraphs.get(6).getText());
    }
}

