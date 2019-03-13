package org.jsoup.nodes;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the DocumentType node
 *
 * @author Jonathan Hedley, http://jonathanhedley.com/
 */
public class DocumentTypeTest {
    @Test
    public void constructorValidationOkWithBlankName() {
        DocumentType fail = new DocumentType("", "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorValidationThrowsExceptionOnNulls() {
        DocumentType fail = new DocumentType("html", null, null);
    }

    @Test
    public void constructorValidationOkWithBlankPublicAndSystemIds() {
        DocumentType fail = new DocumentType("html", "", "");
    }

    @Test
    public void outerHtmlGeneration() {
        DocumentType html5 = new DocumentType("html", "", "");
        Assert.assertEquals("<!doctype html>", html5.outerHtml());
        DocumentType publicDocType = new DocumentType("html", "-//IETF//DTD HTML//", "");
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//IETF//DTD HTML//\">", publicDocType.outerHtml());
        DocumentType systemDocType = new DocumentType("html", "", "http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd");
        Assert.assertEquals("<!DOCTYPE html \"http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd\">", systemDocType.outerHtml());
        DocumentType combo = new DocumentType("notHtml", "--public", "--system");
        Assert.assertEquals("<!DOCTYPE notHtml PUBLIC \"--public\" \"--system\">", combo.outerHtml());
    }

    @Test
    public void testRoundTrip() {
        String base = "<!DOCTYPE html>";
        Assert.assertEquals("<!doctype html>", htmlOutput(base));
        Assert.assertEquals(base, xmlOutput(base));
        String publicDoc = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
        Assert.assertEquals(publicDoc, htmlOutput(publicDoc));
        Assert.assertEquals(publicDoc, xmlOutput(publicDoc));
        String systemDoc = "<!DOCTYPE html SYSTEM \"exampledtdfile.dtd\">";
        Assert.assertEquals(systemDoc, htmlOutput(systemDoc));
        Assert.assertEquals(systemDoc, xmlOutput(systemDoc));
        String legacyDoc = "<!DOCTYPE html SYSTEM \"about:legacy-compat\">";
        Assert.assertEquals(legacyDoc, htmlOutput(legacyDoc));
        Assert.assertEquals(legacyDoc, xmlOutput(legacyDoc));
    }
}

