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
import org.springframework.security.config.annotation.web.reactive.ServerHttpSecurityConfigurationBuilder;
import org.springframework.security.htmlunit.server.WebTestClientHtmlUnitDriverBuilder;
import org.springframework.security.test.web.reactive.server.WebTestClientBuilder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.config.web.server.FormLoginTests.DefaultLoginPage.create;
import static org.springframework.security.config.web.server.FormLoginTests.HomePage.to;


/**
 *
 *
 * @author Shazin Sadakath
 * @since 5.0
 */
public class LogoutSpecTests {
    private ServerHttpSecurity http = ServerHttpSecurityConfigurationBuilder.httpWithDefaultAuthentication();

    @Test
    public void defaultLogout() {
        SecurityWebFilterChain securityWebFilter = this.http.authorizeExchange().anyExchange().authenticated().and().formLogin().and().build();
        WebTestClient webTestClient = WebTestClientBuilder.bindToWebFilters(securityWebFilter).build();
        WebDriver driver = WebTestClientHtmlUnitDriverBuilder.webTestClientSetup(webTestClient).build();
        FormLoginTests.DefaultLoginPage loginPage = to(driver, FormLoginTests.DefaultLoginPage.class).assertAt();
        loginPage = loginPage.loginForm().username("user").password("invalid").submit(FormLoginTests.DefaultLoginPage.class).assertError();
        FormLoginTests.HomePage homePage = loginPage.loginForm().username("user").password("password").submit(FormLoginTests.HomePage.class);
        homePage.assertAt();
        loginPage = FormLoginTests.DefaultLogoutPage.to(driver).assertAt().logout();
        loginPage.assertAt().assertLogout();
    }

    @Test
    public void customLogout() {
        SecurityWebFilterChain securityWebFilter = this.http.authorizeExchange().anyExchange().authenticated().and().formLogin().and().logout().requiresLogout(ServerWebExchangeMatchers.pathMatchers("/custom-logout")).and().build();
        WebTestClient webTestClient = WebTestClientBuilder.bindToWebFilters(securityWebFilter).build();
        WebDriver driver = WebTestClientHtmlUnitDriverBuilder.webTestClientSetup(webTestClient).build();
        FormLoginTests.DefaultLoginPage loginPage = to(driver, FormLoginTests.DefaultLoginPage.class).assertAt();
        loginPage = loginPage.loginForm().username("user").password("invalid").submit(FormLoginTests.DefaultLoginPage.class).assertError();
        FormLoginTests.HomePage homePage = loginPage.loginForm().username("user").password("password").submit(FormLoginTests.HomePage.class);
        homePage.assertAt();
        driver.get("http://localhost/custom-logout");
        create(driver).assertAt().assertLogout();
    }
}

