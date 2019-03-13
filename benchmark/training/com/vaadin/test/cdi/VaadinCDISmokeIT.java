package com.vaadin.test.cdi;


import GreetingView.CALL_COUNT_FORMAT;
import ThankYouServiceImpl.THANK_YOU_TEXT;
import com.vaadin.test.cdi.views.GreetingService;
import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.ParallelRunner;
import com.vaadin.testbench.parallel.ParallelTest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ParallelRunner.class)
@RunLocally(Browser.PHANTOMJS)
public class VaadinCDISmokeIT extends ParallelTest {
    private static final String BASE_URL = "http://localhost:8080/";

    @Rule
    public ScreenshotOnFailureRule rule = new ScreenshotOnFailureRule(this, true);

    @Test
    public void testPageLoadsAndCanBeInterractedWith() {
        getDriver().navigate().to(VaadinCDISmokeIT.BASE_URL);
        $(ButtonElement.class).caption("Click Me").first().click();
        Assert.assertTrue($(NotificationElement.class).exists());
        Assert.assertEquals(THANK_YOU_TEXT, $(NotificationElement.class).first().getText());
    }

    @Test
    public void testAlwaysNewViewIsRecrated() {
        getDriver().navigate().to(VaadinCDISmokeIT.BASE_URL);
        navigateAndGetLogContent("new");
        // A new navigation event should happen when navigating to view again
        navigateAndGetLogContent("new");
        Assert.assertEquals("GreetingService should've been only called once.", String.format(CALL_COUNT_FORMAT, 1), $(LabelElement.class).id("callCount").getText());
    }

    @Test
    public void testParameterChangeRecreatedView() {
        GreetingService service = new GreetingService();
        getDriver().navigate().to(VaadinCDISmokeIT.BASE_URL);
        navigateAndGetLogContent("param");
        Assert.assertEquals("Greeting service was not called with empty parameter.", service.getGreeting(""), $(LabelElement.class).first().getText());
        // Navigation event is fired with same view and different parameters
        navigateAndGetLogContent("param", "foo");
        Assert.assertEquals("Greeting service was not called with correct parameters.", service.getGreeting("foo"), $(LabelElement.class).first().getText());
        Assert.assertEquals("GreetingService should've been only called once.", String.format(CALL_COUNT_FORMAT, 1), $(LabelElement.class).id("callCount").getText());
    }

    @Test
    public void testParameterChangeUsesSameView() {
        GreetingService service = new GreetingService();
        getDriver().navigate().to(VaadinCDISmokeIT.BASE_URL);
        navigateAndGetLogContent("name", "foo");
        Assert.assertEquals("Greeting service was not called with 'foo' parameter.", service.getGreeting("foo"), $(LabelElement.class).first().getText());
        // Navigation event fired with same view and different parameters
        navigateAndGetLogContent("name", "bar");
        Assert.assertEquals("GreetingService should've been only called twice.", String.format(CALL_COUNT_FORMAT, 2), $(LabelElement.class).id("callCount").getText());
        Assert.assertEquals("Greeting service was not called with 'bar' parameter.", service.getGreeting("bar"), $(LabelElement.class).get(1).getText());
    }

    @Test
    public void testUIScopedView() {
        GreetingService service = new GreetingService();
        List<String> expectedLogContents = new ArrayList<>();
        getDriver().navigate().to(VaadinCDISmokeIT.BASE_URL);
        expectedLogContents.add(navigateAndGetLogContent("persisting", "foo", service));
        // Navigation event should with same view and different parameters
        expectedLogContents.add(navigateAndGetLogContent("persisting", "", service));
        // Navigating to another view should not lose the UI Scoped view.
        navigateAndGetLogContent("new");
        expectedLogContents.add(navigateAndGetLogContent("persisting", "", service));
        Assert.assertEquals("GreetingService unexpected call count", String.format(CALL_COUNT_FORMAT, service.getCallCount()), $(LabelElement.class).id("callCount").getText());
        Assert.assertEquals("Unexpected contents in the log.", expectedLogContents, $(LabelElement.class).all().stream().map(LabelElement::getText).collect(Collectors.toList()));
    }

    @Test
    public void testPreserveOnRefreshWithViewName() {
        GreetingService service = new GreetingService();
        List<String> expectedLogContents = new ArrayList<>();
        getDriver().navigate().to(VaadinCDISmokeIT.BASE_URL);
        expectedLogContents.add(navigateAndGetLogContent("name", "foo", service));
        // Navigation event should with same view and different parameters
        expectedLogContents.add(navigateAndGetLogContent("name", "bar", service));
        // Reload the page.
        getDriver().navigate().refresh();
        Assert.assertEquals("GreetingService should've been only called twice.", String.format(CALL_COUNT_FORMAT, service.getCallCount()), $(LabelElement.class).id("callCount").getText());
        Assert.assertEquals("Unexpected contents in the log.", expectedLogContents, $(LabelElement.class).all().stream().map(LabelElement::getText).collect(Collectors.toList()));
    }
}

