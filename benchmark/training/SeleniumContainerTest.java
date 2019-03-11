

import SpringBootTest.WebEnvironment;
import com.example.DemoApplication;
import java.io.File;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.BrowserWebDriverContainer;


/**
 * Simple example of plain Selenium usage.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = SeleniumContainerTest.Initializer.class)
public class SeleniumContainerTest {
    @LocalServerPort
    private int port;

    @Rule
    public BrowserWebDriverContainer chrome = new BrowserWebDriverContainer().withCapabilities(new ChromeOptions()).withRecordingMode(RECORD_ALL, new File("build"));

    @Test
    public void simplePlainSeleniumTest() {
        RemoteWebDriver driver = chrome.getWebDriver();
        driver.get((("http://host.testcontainers.internal:" + (port)) + "/foo.html"));
        List<WebElement> hElement = driver.findElementsByTagName("h");
        assertTrue("The h element is found", ((hElement != null) && ((hElement.size()) > 0)));
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            applicationContext.addApplicationListener(((ApplicationListener<WebServerInitializedEvent>) (( event) -> {
                Testcontainers.exposeHostPorts(event.getWebServer().getPort());
            })));
        }
    }
}

