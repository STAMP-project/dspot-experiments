package com.vaadin.tests.components.table;


import Keys.ADD;
import Keys.CONTROL;
import Keys.SUBTRACT;
import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.Arrays;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class UnnecessaryScrollbarWhenZoomingTest extends MultiBrowserTest {
    private UnnecessaryScrollbarWhenZoomingTest.ZoomLevelSetter zoomSetter;

    private int zoomOutIterations = 3;

    private int zoomInIterations = 3;

    @Test
    public void testInitial() {
        testExtraScrollbarsNotShown();
    }

    @Test
    public void testZoomingIn() {
        for (int i = 0; i < (zoomInIterations); i++) {
            zoomSetter.increaseZoom();
            testExtraScrollbarsNotShown();
        }
    }

    @Test
    public void testZoomingOut() throws InterruptedException {
        for (int i = 0; i < (zoomOutIterations); i++) {
            zoomSetter.decreaseZoom();
            testExtraScrollbarsNotShown();
        }
    }

    interface ZoomLevelSetter {
        public void increaseZoom();

        public void decreaseZoom();

        public void resetZoom();
    }

    /* A class for setting the zoom levels by sending keys such as ctrl and +. */
    class NonChromeZoomLevelSetter implements UnnecessaryScrollbarWhenZoomingTest.ZoomLevelSetter {
        private WebDriver driver;

        public NonChromeZoomLevelSetter(WebDriver driver) {
            this.driver = driver;
        }

        @Override
        public void increaseZoom() {
            getElement().sendKeys(Keys.chord(CONTROL, ADD));
        }

        @Override
        public void decreaseZoom() {
            getElement().sendKeys(Keys.chord(CONTROL, SUBTRACT));
        }

        @Override
        public void resetZoom() {
            getElement().sendKeys(Keys.chord(CONTROL, "0"));
        }

        private WebElement getElement() {
            return driver.findElement(By.tagName("html"));
        }
    }

    /* A class for setting the zoom levels using JavaScript. This setter is used
    for browsers for which the method of sending the keys ctrl and + does not
    work.
     */
    class ChromeZoomLevelSetter implements UnnecessaryScrollbarWhenZoomingTest.ZoomLevelSetter {
        private JavascriptExecutor js;

        private int currentZoomIndex = 2;

        private int[] zoomLevels = new int[]{ 70, 80, 90, 100, 110, 120, 130 };

        public ChromeZoomLevelSetter(WebDriver driver) {
            js = ((JavascriptExecutor) (driver));
        }

        @Override
        public void increaseZoom() {
            (currentZoomIndex)++;
            if ((currentZoomIndex) >= (zoomLevels.length)) {
                currentZoomIndex = (zoomLevels.length) - 1;
            }
            js.executeScript((("document.body.style.zoom='" + (zoomLevels[currentZoomIndex])) + "%'"));
        }

        @Override
        public void decreaseZoom() {
            (currentZoomIndex)--;
            if ((currentZoomIndex) < 0) {
                currentZoomIndex = 0;
            }
            js.executeScript((("document.body.style.zoom='" + (zoomLevels[currentZoomIndex])) + "%'"));
        }

        @Override
        public void resetZoom() {
            js.executeScript("document.body.style.zoom='100%'");
            currentZoomIndex = Arrays.binarySearch(zoomLevels, 100);
        }
    }
}

