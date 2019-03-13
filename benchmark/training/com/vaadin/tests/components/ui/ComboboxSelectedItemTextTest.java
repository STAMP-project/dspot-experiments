package com.vaadin.tests.components.ui;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


/**
 * Test class for issue #13477, where selecting a combobox item that is too long
 * would render the ending of an item instead of the beginning, which was
 * considered less than informative.
 *
 * @author Vaadin Ltd
 */
public class ComboboxSelectedItemTextTest extends MultiBrowserTest {
    public final String SCREENSHOT_NAME_EDITABLE = "LongComboboxItemSelectedEditable";

    public final String SCREENSHOT_NAME_NON_EDITABLE = "LongComboboxItemSelectedNonEditable";

    public final int INDEX_EDITABLE_COMBOBOX = 1;

    public final int INDEX_NON_EDITABLE_COMBOBOX = 2;

    @Test
    public void testCombobox() throws IOException {
        testCombobox(INDEX_EDITABLE_COMBOBOX, INDEX_NON_EDITABLE_COMBOBOX, SCREENSHOT_NAME_EDITABLE);
    }

    @Test
    public void testComboboxNonEditable() throws IOException {
        testCombobox(INDEX_NON_EDITABLE_COMBOBOX, INDEX_EDITABLE_COMBOBOX, SCREENSHOT_NAME_NON_EDITABLE);
    }
}

