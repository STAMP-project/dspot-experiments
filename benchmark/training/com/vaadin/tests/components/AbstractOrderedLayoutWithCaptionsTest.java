package com.vaadin.tests.components;


import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Test to see if AbstractOrderedLayout displays captions correctly with
 * expanding ratios.
 *
 * @author Vaadin Ltd
 */
public class AbstractOrderedLayoutWithCaptionsTest extends MultiBrowserTest {
    @Test
    public void CaptionHeightMeasuredCorrectly() {
        openTestURL();
        WebElement div = getDriver().findElement(By.cssSelector(".v-panel-content > div > div"));
        String paddingTop = div.getCssValue("padding-top");
        Integer paddingHeight = Integer.parseInt(paddingTop.substring(0, ((paddingTop.length()) - 2)));
        List<WebElement> children = getDriver().findElements(By.cssSelector(".v-panel-content .v-slot"));
        MatcherAssert.assertThat(children.size(), Is.is(3));
        Integer neededHeight = (children.get(0).getSize().getHeight()) + (children.get(2).getSize().getHeight());
        MatcherAssert.assertThat(neededHeight, Is.is(Matchers.lessThanOrEqualTo(paddingHeight)));
    }
}

