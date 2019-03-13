package com.vaadin.test.dependencyrewrite;


import com.vaadin.testbench.TestBenchTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.phantomjs.PhantomJSDriver;


public class DependencyFilterIT extends TestBenchTestCase {
    @Test
    public void dynamicallyAddedResources() {
        setDriver(new PhantomJSDriver());
        getDriver().get("http://localhost:8080/dynamic/");
        Assert.assertEquals(1L, executeScript("return window.jqueryLoaded"));
    }

    @Test
    public void initiallyLoadedResources() {
        setDriver(new PhantomJSDriver());
        getDriver().get("http://localhost:8080/initial/");
        // 2 because of https://github.com/vaadin/framework/issues/9181
        Assert.assertEquals(2L, executeScript("return window.jqueryLoaded"));
    }
}

