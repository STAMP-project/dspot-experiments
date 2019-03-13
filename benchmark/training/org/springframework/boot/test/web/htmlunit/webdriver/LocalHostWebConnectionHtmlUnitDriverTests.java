/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.test.web.htmlunit.webdriver;


import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebWindow;
import java.net.URL;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.Capabilities;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link LocalHostWebConnectionHtmlUnitDriver}.
 *
 * @author Phillip Webb
 */
public class LocalHostWebConnectionHtmlUnitDriverTests {
    @Mock
    private WebClient webClient;

    public LocalHostWebConnectionHtmlUnitDriverTests() {
        MockitoAnnotations.initMocks(this);
        BDDMockito.given(this.webClient.getOptions()).willReturn(new WebClientOptions());
    }

    @Test
    public void createWhenEnvironmentIsNullWillThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new LocalHostWebConnectionHtmlUnitDriver(null)).withMessageContaining("Environment must not be null");
    }

    @Test
    public void createWithJavascriptFlagWhenEnvironmentIsNullWillThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new LocalHostWebConnectionHtmlUnitDriver(null, true)).withMessageContaining("Environment must not be null");
    }

    @Test
    public void createWithBrowserVersionWhenEnvironmentIsNullWillThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new LocalHostWebConnectionHtmlUnitDriver(null, BrowserVersion.CHROME)).withMessageContaining("Environment must not be null");
    }

    @Test
    public void createWithCapabilitiesWhenEnvironmentIsNullWillThrowException() {
        Capabilities capabilities = Mockito.mock(Capabilities.class);
        BDDMockito.given(capabilities.getBrowserName()).willReturn("htmlunit");
        BDDMockito.given(capabilities.getVersion()).willReturn("chrome");
        assertThatIllegalArgumentException().isThrownBy(() -> new LocalHostWebConnectionHtmlUnitDriver(null, capabilities)).withMessageContaining("Environment must not be null");
    }

    @Test
    public void getWhenUrlIsRelativeAndNoPortWillUseLocalhost8080() throws Exception {
        MockEnvironment environment = new MockEnvironment();
        LocalHostWebConnectionHtmlUnitDriver driver = new LocalHostWebConnectionHtmlUnitDriverTests.TestLocalHostWebConnectionHtmlUnitDriver(environment);
        driver.get("/test");
        Mockito.verify(this.webClient).getPage(ArgumentMatchers.any(WebWindow.class), requestToUrl(new URL("http://localhost:8080/test")));
    }

    @Test
    public void getWhenUrlIsRelativeAndHasPortWillUseLocalhostPort() throws Exception {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("local.server.port", "8181");
        LocalHostWebConnectionHtmlUnitDriver driver = new LocalHostWebConnectionHtmlUnitDriverTests.TestLocalHostWebConnectionHtmlUnitDriver(environment);
        driver.get("/test");
        Mockito.verify(this.webClient).getPage(ArgumentMatchers.any(WebWindow.class), requestToUrl(new URL("http://localhost:8181/test")));
    }

    public class TestLocalHostWebConnectionHtmlUnitDriver extends LocalHostWebConnectionHtmlUnitDriver {
        public TestLocalHostWebConnectionHtmlUnitDriver(Environment environment) {
            super(environment);
        }

        @Override
        public WebClient getWebClient() {
            return LocalHostWebConnectionHtmlUnitDriverTests.this.webClient;
        }
    }

    private static final class WebRequestUrlArgumentMatcher implements ArgumentMatcher<WebRequest> {
        private final URL expectedUrl;

        private WebRequestUrlArgumentMatcher(URL expectedUrl) {
            this.expectedUrl = expectedUrl;
        }

        @Override
        public boolean matches(WebRequest argument) {
            return argument.getUrl().equals(this.expectedUrl);
        }
    }
}

