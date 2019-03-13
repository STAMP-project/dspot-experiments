package com.vaadin.tests.components.embedded;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class EmbeddedClickListenerRelativeCoordinatesTest extends MultiBrowserTest {
    @Test
    public void testRelativeClick() {
        clickAt(41, 22);
        checkLocation(41, 22);
        clickAt(1, 1);
        checkLocation(1, 1);
    }
}

