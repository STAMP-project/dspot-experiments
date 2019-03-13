package com.example;


import com.example.view.ViewScopedView;
import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.testbench.TestBenchTestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class VaadinSpringBootURIFragmentNavigatorIT extends TestBenchTestCase {
    @Rule
    public ScreenshotOnFailureRule screenshotRule = new ScreenshotOnFailureRule(this, true);

    private String currentUIPath;

    @LocalServerPort
    Integer port;

    @Test
    public void testUINavigation() {
        currentUIPath = (("http://localhost:" + (port)) + "/") + (getPath());
        runNavigationTestPattern();
    }

    @Test
    public void testNotDefaultView() {
        currentUIPath = (("http://localhost:" + (port)) + "/") + (getPath());
        getDriver().navigate().to((((currentUIPath) + (getViewSeparator())) + (ViewScopedView.VIEW_NAME)));
        verifyViewScopeViewOpen();
    }
}

