package com.vaadin.tests.design.nested;


import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;


/**
 * Test case for nested custom layouts. The children of the custom layouts must
 * not be rendered.
 *
 * @author Vaadin Ltd
 */
public class NestedCustomLayoutsTest {
    private static final String PACKAGE_MAPPING = "com_vaadin_tests_design_nested_customlayouts:com.vaadin.tests.design.nested.customlayouts";

    @Test
    public void testNestedLayouts() throws IOException {
        VerticalLayout rootLayout = createRootLayout();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Design.write(rootLayout, out);
        Document doc = Jsoup.parse(out.toString(StandardCharsets.UTF_8.name()));
        MatcherAssert.assertThat(doc.head().child(0).attr("name"), CoreMatchers.is("package-mapping"));
        MatcherAssert.assertThat(doc.head().child(0).attr("content"), CoreMatchers.is(NestedCustomLayoutsTest.PACKAGE_MAPPING));
        assertChildrenCount(doc);
    }
}

