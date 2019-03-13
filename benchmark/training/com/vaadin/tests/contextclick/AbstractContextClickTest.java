package com.vaadin.tests.contextclick;


import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.regex.Pattern;
import org.junit.Test;


@TestCategory("contextclick")
public abstract class AbstractContextClickTest extends MultiBrowserTest {
    private Pattern defaultLog = Pattern.compile("[0-9]+. ContextClickEvent: [(]([0-9]+), ([0-9]+)[)]");

    @Test
    public void testDefaultListener() {
        addOrRemoveDefaultListener();
        assertDefaultContextClickListener(1);
    }
}

