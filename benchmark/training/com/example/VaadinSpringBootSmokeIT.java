package com.example;


import com.example.ui.SubPathUI;
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
public class VaadinSpringBootSmokeIT extends TestBenchTestCase {
    @Rule
    public ScreenshotOnFailureRule screenshotRule = new ScreenshotOnFailureRule(this, true);

    @LocalServerPort
    Integer port;

    @Test
    public void testPageLoadsAndButtonWorks() {
        getDriver().navigate().to((("http://localhost:" + (port)) + ""));
        runSmokeTest();
    }

    @Test
    public void testSubPathPageLoadsAndButtonWorks() {
        getDriver().navigate().to(((("http://localhost:" + (port)) + "/") + (SubPathUI.SUBPATH)));
        runSmokeTest();
    }
}

