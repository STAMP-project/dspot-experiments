package org.docx4j.model.datastorage;


import java.util.Locale;
import junit.framework.Assert;
import org.docx4j.openpackaging.parts.CustomXmlDataStoragePart;
import org.junit.Test;


public class CachedXPathTest {
    public static CustomXmlDataStoragePart xmlPart;

    @Test
    public void simpleNumber() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("count(//Sender)", null);
        System.out.println(result);
        Assert.assertEquals("1", result);
    }

    @Test
    public void simpleString() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("//fileNumber", null);
        System.out.println(result);
        Assert.assertEquals("xxxx", result);
    }

    // https://github.com/plutext/docx4j/issues/234
    @Test
    public void complexString() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("concat('foo', //fileNumber)", null);
        System.out.println(result);
        Assert.assertEquals("fooxxxx", result);
    }

    @Test
    public void complexBoolean() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("count(//Sender)>0 or count(//Intermediary)>0", null);
        System.out.println(result);
        Assert.assertEquals("true", result);
    }

    @Test
    public void complexBoolean2() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("boolean(//Sender) and count(//Receiver)>0", null);
        System.out.println(result);
        Assert.assertEquals("true", result);
    }

    @Test
    public void complexBoolean3() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("not(//MickeyMouse) or count(//Receiver)>0", null);
        System.out.println(result);
        Assert.assertEquals("true", result);
    }

    @Test
    public void complexNumber() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("count(//*[self::Sender or self::Intermediary])", null);
        System.out.println(result);
        Assert.assertEquals("1", result);
    }

    @Test
    public void simpleBoolean() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("not(//foo)", null);
        System.out.println(result);
        Assert.assertEquals("true", result);
    }

    @Test
    public void booleanPlusString() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("boolean(//foo) + string(//fileNumber[1]) ", null);
        System.out.println(result);
        boolean resultOK = (result.equals("false")) || (result.equals("NaN"));
        // depending on docx4j.openpackaging.parts.XmlPart.cachedXPathGetString.heuristic
        // ie whether the heuristic is used.
        Assert.assertEquals(true, resultOK);
    }

    @Test
    public void testConvertNumber() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("count(//Sender[@class='1'])", null);
        System.out.println(result);
        Assert.assertEquals("1", result);
    }

    @Test
    public void testConvertNumber2() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("count(//Sender[@class='1'])+1.1", null);
        System.out.println(result);
        Assert.assertEquals("2.1", result);
    }

    @Test
    public void booleanContains() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("contains(//fileNumber/text(), 'xx')", null);
        System.out.println(result);
        Assert.assertEquals("true", result);
    }

    @Test
    public void booleanStringEquals() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("string(//fileNumber[1])= 'xxxx'", null);
        System.out.println(result);
        Assert.assertEquals("true", result);
    }

    // https://github.com/plutext/docx4j/issues/235
    @Test
    public void booleanEqualsInPredicate() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("//Sender[@class='1']/id", null);
        System.out.println(result);
        Assert.assertEquals("3", result);
    }

    // https://github.com/plutext/docx4j/issues/235
    @Test
    public void booleanEqualsInPredicate2() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("substring('abcdefg', 6 - number(//Sender[@class='1']/id))", null);
        System.out.println(result);
        Assert.assertEquals("cdefg", result);
    }

    @Test
    public void booleanStringNotEquals() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("string(//fileNumber[1])!= 'xxxxNot'", null);
        System.out.println(result);
        Assert.assertEquals("true", result);
    }

    @Test
    public void booleanStringEqualsComplex() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("string(//fileNumber[1])= 'xxxx' and string(//fileNumber[1])= 'xxxx'", null);
        System.out.println(result);
        Assert.assertEquals("true", result);
    }

    @Test
    public void booleanStringLengthGreaterThan() throws Exception {
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("string-length(//fileNumber[1])>0", null);
        System.out.println(result);
        Assert.assertEquals("true", result);
    }

    @Test
    public void localeDeBooleanStringLengthGreaterThan() throws Exception {
        Locale deLocale = new Locale("de", "DE");
        Locale.setDefault(deLocale);
        String result = CachedXPathTest.xmlPart.cachedXPathGetString("string-length(//fileNumber[1])>0", null);
        System.out.println(result);
        Assert.assertEquals("true", result);
    }
}

