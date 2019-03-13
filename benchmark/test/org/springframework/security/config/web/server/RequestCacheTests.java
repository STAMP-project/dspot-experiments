/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.security.config.web.server;


import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.security.config.annotation.web.reactive.ServerHttpSecurityConfigurationBuilder;
import org.springframework.security.htmlunit.server.WebTestClientHtmlUnitDriverBuilder;
import org.springframework.security.test.web.reactive.server.WebTestClientBuilder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
public class RequestCacheTests {
    private ServerHttpSecurity http = ServerHttpSecurityConfigurationBuilder.httpWithDefaultAuthentication();

    @Test
    public void defaultFormLoginRequestCache() {
        SecurityWebFilterChain securityWebFilter = this.http.authorizeExchange().anyExchange().authenticated().and().formLogin().and().build();
        WebTestClient webTestClient = WebTestClient.bindToController(new RequestCacheTests.SecuredPageController(), new WebTestClientBuilder.Http200RestController()).webFilter(new org.springframework.security.web.server.WebFilterChainProxy(securityWebFilter)).build();
        WebDriver driver = WebTestClientHtmlUnitDriverBuilder.webTestClientSetup(webTestClient).build();
        FormLoginTests.DefaultLoginPage loginPage = RequestCacheTests.SecuredPage.to(driver, FormLoginTests.DefaultLoginPage.class).assertAt();
        RequestCacheTests.SecuredPage securedPage = loginPage.loginForm().username("user").password("password").submit(RequestCacheTests.SecuredPage.class);
        securedPage.assertAt();
    }

    @Test
    public void requestCacheNoOp() {
        SecurityWebFilterChain securityWebFilter = this.http.authorizeExchange().anyExchange().authenticated().and().formLogin().and().requestCache().requestCache(NoOpServerRequestCache.getInstance()).and().build();
        WebTestClient webTestClient = WebTestClient.bindToController(new RequestCacheTests.SecuredPageController(), new WebTestClientBuilder.Http200RestController()).webFilter(new org.springframework.security.web.server.WebFilterChainProxy(securityWebFilter)).build();
        WebDriver driver = WebTestClientHtmlUnitDriverBuilder.webTestClientSetup(webTestClient).build();
        FormLoginTests.DefaultLoginPage loginPage = RequestCacheTests.SecuredPage.to(driver, FormLoginTests.DefaultLoginPage.class).assertAt();
        FormLoginTests.HomePage securedPage = loginPage.loginForm().username("user").password("password").submit(FormLoginTests.HomePage.class);
        securedPage.assertAt();
    }

    public static class SecuredPage {
        private WebDriver driver;

        public SecuredPage(WebDriver driver) {
            this.driver = driver;
        }

        public void assertAt() {
            assertThat(this.driver.getTitle()).isEqualTo("Secured");
        }

        static <T> T to(WebDriver driver, Class<T> page) {
            driver.get("http://localhost/secured");
            return PageFactory.initElements(driver, page);
        }
    }

    @Controller
    public static class SecuredPageController {
        @ResponseBody
        @GetMapping("/secured")
        public String login(ServerWebExchange exchange) {
            return "<!DOCTYPE html>\n" + ((((((("<html lang=\"en\">\n" + "  <head>\n") + "    <title>Secured</title>\n") + "  </head>\n") + "  <body>\n") + "    <h1>Secured</h1>\n") + "  </body>\n") + "</html>");
        }
    }
}

