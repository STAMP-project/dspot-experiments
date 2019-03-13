package com.vaadin.tests.debug;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


/**
 * Check that analyze layouts does not find problems for a trivial application.
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class HierarchyAfterAnalyzeLayoutsTest extends MultiBrowserTest {
    @Test
    public void checkNoLayoutProblemsFound() throws IOException {
        setDebug(true);
        openTestURL();
        // Make sure debug window is visible
        showDebugWindow();
        // select tab
        pressDebugWindowButton(findByXpath("//button[@title = 'Examine component hierarchy']"));
        // click "analyze layouts"
        pressDebugWindowButton(findByXpath("//button[@title = 'Check layouts for potential problems']"));
        // check that no problems found
        findByXpath("//div[text() = 'Layouts analyzed, no top level problems']");
        // check that original label still there
        findByXpath("//div[text() = 'This is a label']");
    }
}

