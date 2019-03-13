package org.keycloak.testsuite.adapter.javascript;


import org.junit.Test;


public class JavascriptAdapterWithNativePromisesTest extends JavascriptAdapterTest {
    @Test
    @Override
    public void reentrancyCallbackTest() {
        testExecutor.logInAndInit(defaultArguments(), AbstractJavascriptTest.testUser, this::assertSuccessfullyLoggedIn).executeAsyncScript(("var callback = arguments[arguments.length - 1];" + ((((((("keycloak.updateToken(60).then(function () {" + "       event(\"First callback\");") + "       keycloak.updateToken(60).then(function () {") + "          event(\"Second callback\");") + "          callback(\"Success\");") + "       });") + "    }") + ");")), ( driver1, output, events) -> {
            waitUntilElement(events).text().contains("First callback");
            waitUntilElement(events).text().contains("Second callback");
            waitUntilElement(events).text().not().contains("Auth Logout");
        });
    }
}

