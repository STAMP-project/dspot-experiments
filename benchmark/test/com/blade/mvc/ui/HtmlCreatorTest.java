package com.blade.mvc.ui;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author biezhi
 * @unknown 2017/9/20
 */
public class HtmlCreatorTest {
    private HtmlCreator htmlCreator;

    @Test
    public void testHtmlCreator() {
        String html = htmlCreator.html();
        Assert.assertEquals(true, html.contains("Blade"));
    }

    @Test
    public void testCenter() {
        htmlCreator.center("Hello");
        Assert.assertEquals(true, htmlCreator.html().contains("<center>Hello</center>"));
    }

    @Test
    public void testTitle() {
        htmlCreator.title("Blade");
        Assert.assertEquals(true, htmlCreator.html().contains("<title>Blade</title>"));
    }

    @Test
    public void testAdd() {
        htmlCreator.add("nice");
        Assert.assertEquals(true, htmlCreator.html().contains("nice"));
    }

    @Test
    public void testBold() {
        htmlCreator.addBold("BANNER_PADDING");
        Assert.assertEquals(true, htmlCreator.html().contains("<b>BANNER_PADDING</b>"));
    }

    @Test
    public void testAddRowToTable() {
        htmlCreator.addRowToTable(Arrays.asList("name", "age"));
        Assert.assertEquals(true, htmlCreator.html().contains("<td>name</td><td>age</td>"));
    }

    @Test
    public void testBr() {
        htmlCreator.br();
        Assert.assertEquals(true, htmlCreator.html().contains("<br/>"));
    }

    @Test
    public void testP() {
        htmlCreator.startP();
        Assert.assertEquals(true, htmlCreator.html().contains("<p>"));
        htmlCreator.add("aaa");
        htmlCreator.endP();
        Assert.assertEquals(true, htmlCreator.html().contains("</p>"));
    }

    @Test
    public void testHeadTitle() {
        htmlCreator.h1("Hello H1");
        Assert.assertEquals(true, htmlCreator.html().contains("<h1>Hello H1</h1>"));
        htmlCreator.h2("Hello H2");
        Assert.assertEquals(true, htmlCreator.html().contains("<h2>Hello H2</h2>"));
    }
}

