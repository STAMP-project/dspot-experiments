package com.vaadin.tests.design;


import com.vaadin.server.ExternalResource;
import com.vaadin.ui.AbstractEmbedded;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Flash;
import com.vaadin.ui.Image;
import com.vaadin.ui.declarative.Design;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests declarative support for implementations of {@link AbstractEmbedded} and
 * {@link Embedded}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class EmbeddedsTest {
    @Test
    public void testAbstractEmbeddedsToFromDesign() throws Exception {
        for (AbstractEmbedded ae : new AbstractEmbedded[]{ new Image(), new Flash(), new BrowserFrame() }) {
            ae.setSource(new ExternalResource("http://www.example.org"));
            ae.setAlternateText("some alternate text");
            ae.setCaption("some <b>caption</b>");
            ae.setCaptionAsHtml(true);
            ae.setDescription("some description");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Design.write(ae, bos);
            AbstractEmbedded result = ((AbstractEmbedded) (Design.read(new ByteArrayInputStream(bos.toByteArray()))));
            Assert.assertTrue(EmbeddedsTest.equals(((ExternalResource) (ae.getSource())), ((ExternalResource) (result.getSource()))));
            Assert.assertEquals(ae.getAlternateText(), result.getAlternateText());
            Assert.assertEquals(ae.getCaption(), result.getCaption());
            Assert.assertEquals(ae.isCaptionAsHtml(), result.isCaptionAsHtml());
            Assert.assertEquals(ae.getDescription(), result.getDescription());
        }
    }

    @Test
    public void testFlashToFromDesign() throws Exception {
        Flash ae = new Flash();
        ae.setSource(new ExternalResource("http://www.example.org"));
        ae.setAlternateText("some alternate text");
        ae.setCaption("some <b>caption</b>");
        ae.setCaptionAsHtml(true);
        ae.setDescription("some description");
        ae.setCodebase("codebase");
        ae.setArchive("archive");
        ae.setCodetype("codetype");
        ae.setParameter("foo", "bar");
        ae.setParameter("something", "else");
        ae.setStandby("foobar");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Design.write(ae, bos);
        Flash result = ((Flash) (Design.read(new ByteArrayInputStream(bos.toByteArray()))));
        Assert.assertTrue(EmbeddedsTest.equals(((ExternalResource) (ae.getSource())), ((ExternalResource) (result.getSource()))));
        Assert.assertEquals(ae.getAlternateText(), result.getAlternateText());
        Assert.assertEquals(ae.getCaption(), result.getCaption());
        Assert.assertEquals(ae.isCaptionAsHtml(), result.isCaptionAsHtml());
        Assert.assertEquals(ae.getDescription(), result.getDescription());
        Assert.assertEquals(ae.getCodebase(), result.getCodebase());
        Assert.assertEquals(ae.getArchive(), result.getArchive());
        Assert.assertEquals(ae.getCodetype(), result.getCodetype());
        Assert.assertEquals(ae.getParameter("foo"), result.getParameter("foo"));
        Assert.assertEquals(ae.getParameter("something"), result.getParameter("something"));
        Assert.assertEquals(ae.getStandby(), result.getStandby());
    }
}

