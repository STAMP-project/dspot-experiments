package com.vaadin.tests.components.datefield;


import com.google.gwt.editor.client.Editor.Ignore;
import org.junit.Test;


/**
 * Reuse tests from super DateFieldTestTest class.
 *
 * @author Vaadin Ltd
 */
public class InlineDateFieldTestTest extends DateFieldTestTest {
    @Override
    @Test
    @Ignore
    public void testValueAfterOpeningPopupInRequiredField() throws InterruptedException {
        // no popup for inline date field
    }
}

