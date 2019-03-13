package com.vaadin.tests.server.component.link;


import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Link;
import org.junit.Test;


/**
 * Test cases for reading the properties of selection components.
 *
 * @author Vaadin Ltd
 */
public class LinkDeclarativeTest extends DeclarativeTestBase<Link> {
    @Test
    public void readBasic() throws Exception {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void writeBasic() throws Exception {
        testWrite(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testReadEmpty() {
        testRead("<vaadin-link />", new Link());
    }

    @Test
    public void testWriteEmpty() {
        testWrite("<vaadin-link />", new Link());
    }
}

