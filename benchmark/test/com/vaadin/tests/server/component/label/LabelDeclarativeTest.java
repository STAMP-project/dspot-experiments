package com.vaadin.tests.server.component.label;


import ContentMode.HTML;
import ContentMode.TEXT;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.DesignContext;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Test;


/**
 * Tests declarative support for implementations of {@link Label}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class LabelDeclarativeTest extends DeclarativeTestBase<Label> {
    @Test
    public void testEmpty() {
        String design = "<vaadin-label />";
        Label l = new Label();
        l.setContentMode(HTML);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testDefault() {
        String design = "<vaadin-label>Hello world!</vaadin-label>";
        Label l = createLabel("Hello world!", null, true);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testRich() {
        String design = "<vaadin-label>This is <b><u>Rich</u></b> content!</vaadin-label>";
        Label l = createLabel("This is \n<b><u>Rich</u></b> content!", null, true);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testPlainText() {
        String design = "<vaadin-label plain-text>This is only &lt;b&gt;text&lt;/b&gt;" + " and will contain visible tags</vaadin-label>";
        Label l = createLabel("This is only <b>text</b> and will contain visible tags", null, false);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testContentAndCaption() {
        String design = "<vaadin-label caption='This is a label'>This is <b><u>Rich</u></b> " + "content!</vaadin-label>";
        Label l = createLabel("This is \n<b><u>Rich</u></b> content!", "This is a label", true);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testCaption() {
        String design = "<vaadin-label caption='This is a label' />";
        Label l = createLabel(null, "This is a label", true);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testHtmlEntities() {
        String design = "<vaadin-label plain-text=\"true\">&gt; Test</vaadin-label>";
        Label read = read(design);
        assertEquals("> Test", read.getValue());
        design = design.replace("plain-text=\"true\"", "");
        read = read(design);
        assertEquals("&gt; Test", read.getValue());
        Label label = new Label("&amp; Test");
        label.setContentMode(TEXT);
        Element root = new Element(Tag.valueOf("vaadin-label"), "");
        label.writeDesign(root, new DesignContext());
        assertEquals("&amp;amp; Test", root.html());
        label.setContentMode(HTML);
        root = new Element(Tag.valueOf("vaadin-label"), "");
        label.writeDesign(root, new DesignContext());
        assertEquals("&amp; Test", root.html());
    }
}

