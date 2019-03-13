package com.vaadin.server;


import java.util.Properties;
import org.junit.Test;


public class BootstrapHandlerTest {
    private static final String VAADIN_URL = "http://host/VAADIN/";

    public static class ES5Browser extends WebBrowser {
        @Override
        public boolean isEs6Supported() {
            return false;
        }
    }

    public static class ES6Browser extends WebBrowser {
        @Override
        public boolean isEs6Supported() {
            return true;
        }
    }

    @Test
    public void resolveFrontendES5() {
        BootstrapHandlerTest.testResolveFrontEnd("frontend://foobar.html", "http://host/VAADIN/frontend/es5/foobar.html", new BootstrapHandlerTest.ES5Browser());
    }

    @Test
    public void resolveFrontendES6() {
        BootstrapHandlerTest.testResolveFrontEnd("frontend://foobar.html", "http://host/VAADIN/frontend/es6/foobar.html", new BootstrapHandlerTest.ES6Browser());
    }

    @Test
    public void resolveFrontendES5CustomUrl() {
        Properties properties = new Properties();
        properties.setProperty("frontend.url.es5", "https://cdn.somewhere.com/5");
        BootstrapHandlerTest.testResolveFrontEnd("frontend://foobar.html", "https://cdn.somewhere.com/5/foobar.html", new BootstrapHandlerTest.ES5Browser(), properties);
    }

    @Test
    public void resolveFrontendES6CustomUrl() {
        Properties properties = new Properties();
        properties.setProperty("frontend.url.es6", "https://cdn.somewhere.com/6");
        BootstrapHandlerTest.testResolveFrontEnd("frontend://foobar.html", "https://cdn.somewhere.com/6/foobar.html", new BootstrapHandlerTest.ES6Browser(), properties);
    }
}

