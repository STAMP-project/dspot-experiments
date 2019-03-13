package com.vaadin.tests.design;


import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.hamcrest.CoreMatchers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.Assert;
import org.junit.Test;


/**
 * A test for checking that parsing a layout preserves the IDs and the mapping
 * from prefixes to package names (for example
 * <meta name=?package-mapping? content=?my:com.addon.mypackage? />)
 *
 * @author Vaadin Ltd
 */
public class ParseLayoutTest {
    // The context is used for accessing the created component hierarchy.
    private DesignContext ctx;

    @Test
    public void buttonWithIdIsParsed() {
        Component button = ctx.getComponentByLocalId("firstButton");
        Assert.assertThat(ctx.getComponentByCaption("Native click me"), CoreMatchers.is(button));
        Assert.assertThat(button.getCaption(), CoreMatchers.is("Native click me"));
    }

    @Test
    public void buttonWithIdAndLocalIdIsParsed() {
        Component button = ctx.getComponentById("secondButton");
        Assert.assertThat(ctx.getComponentByCaption("Another button"), CoreMatchers.is(button));
        Assert.assertThat(ctx.getComponentByLocalId("localID"), CoreMatchers.is(button));
        Assert.assertThat(button.getCaption(), CoreMatchers.is("Another button"));
    }

    @Test
    public void buttonWithoutIdsIsParsed() {
        Assert.assertThat(ctx.getComponentByCaption("Yet another button"), CoreMatchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
    }

    @Test
    public void serializationPreservesProperties() throws IOException {
        ByteArrayOutputStream out = serializeDesign(ctx);
        ctx = deSerializeDesign(out);
        assertButtonProperties();
    }

    @Test
    public void serializationPreservesHierarchy() throws IOException {
        ByteArrayOutputStream out = serializeDesign(ctx);
        ctx = deSerializeDesign(out);
        assertComponentHierarchy();
    }

    @Test
    public void designIsSerializedWithCorrectPrefixesAndPackageNames() throws IOException {
        ByteArrayOutputStream out = serializeDesign(ctx);
        // Check the mapping from prefixes to package names using the html tree
        String[] expectedPrefixes = new String[]{ "my" };
        String[] expectedPackageNames = new String[]{ "com.addon.mypackage" };
        int index = 0;
        Document doc = Jsoup.parse(out.toString(StandardCharsets.UTF_8.name()));
        Element head = doc.head();
        for (Node child : head.childNodes()) {
            if ("meta".equals(child.nodeName())) {
                String name = child.attributes().get("name");
                if ("package-mapping".equals(name)) {
                    String content = child.attributes().get("content");
                    String[] parts = content.split(":");
                    Assert.assertEquals("Unexpected prefix.", expectedPrefixes[index], parts[0]);
                    Assert.assertEquals("Unexpected package name.", expectedPackageNames[index], parts[1]);
                    index++;
                }
            }
        }
        Assert.assertEquals("Unexpected number of prefix - package name pairs.", 1, index);
    }

    @Test
    public void fieldsAreBoundToATemplate() throws IOException {
        LayoutTemplate template = new LayoutTemplate();
        InputStream htmlFile = getClass().getResourceAsStream("testFile.html");
        Design.read(htmlFile, template);
        Assert.assertNotNull(template.getFirstButton());
        Assert.assertNotNull(template.getSecondButton());
        Assert.assertNotNull(template.getYetanotherbutton());
        Assert.assertNotNull(template.getClickme());
        Assert.assertEquals("Native click me", template.getFirstButton().getCaption());
    }

    @Test(expected = DesignException.class)
    public void fieldsCannotBeBoundToAnInvalidTemplate() throws IOException {
        InvalidLayoutTemplate template = new InvalidLayoutTemplate();
        InputStream htmlFile = getClass().getResourceAsStream("testFile.html");
        Design.read(htmlFile, template);
    }

    @Test
    public void rootHasCorrectComponents() {
        Component root = ctx.getRootComponent();
        VerticalLayout vlayout = ((VerticalLayout) (root));
        Assert.assertThat(vlayout.getComponentCount(), CoreMatchers.is(3));
    }

    @Test
    public void rootChildHasCorrectComponents() {
        Component root = ctx.getRootComponent();
        VerticalLayout vlayout = ((VerticalLayout) (root));
        HorizontalLayout hlayout = ((HorizontalLayout) (vlayout.getComponent(0)));
        Assert.assertThat(hlayout.getComponentCount(), CoreMatchers.is(5));
        Assert.assertThat(hlayout.getComponent(0).getCaption(), CoreMatchers.is("FooBar"));
        Assert.assertThat(hlayout.getComponent(1).getCaption(), CoreMatchers.is("Native click me"));
        Assert.assertThat(hlayout.getComponent(2).getCaption(), CoreMatchers.is("Another button"));
        Assert.assertThat(hlayout.getComponent(3).getCaption(), CoreMatchers.is("Yet another button"));
        Assert.assertThat(hlayout.getComponent(4).getCaption(), CoreMatchers.is("Click me"));
        Assert.assertThat(hlayout.getComponent(4).getWidth(), CoreMatchers.is(150.0F));
        // Check the remaining two components of the vertical layout
        assertTextField(vlayout);
        assertPasswordField(vlayout);
    }
}

