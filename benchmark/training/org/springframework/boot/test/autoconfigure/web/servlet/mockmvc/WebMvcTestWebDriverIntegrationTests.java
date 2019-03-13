/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.test.autoconfigure.web.servlet.mockmvc;


import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Tests for {@link WebMvcTest} with {@link WebDriver}.
 *
 * @author Phillip Webb
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@WithMockUser
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WebMvcTestWebDriverIntegrationTests {
    private static WebDriver previousWebDriver;

    @Autowired
    private WebDriver webDriver;

    @Test
    public void shouldAutoConfigureWebClient() {
        this.webDriver.get("/html");
        WebElement element = this.webDriver.findElement(By.tagName("body"));
        assertThat(element.getText()).isEqualTo("Hello");
        WebMvcTestWebDriverIntegrationTests.previousWebDriver = this.webDriver;
    }

    @Test
    public void shouldBeADifferentWebClient() {
        this.webDriver.get("/html");
        WebElement element = this.webDriver.findElement(By.tagName("body"));
        assertThat(element.getText()).isEqualTo("Hello");
        assertThatExceptionOfType(NoSuchWindowException.class).isThrownBy(WebMvcTestWebDriverIntegrationTests.previousWebDriver::getWindowHandle);
        assertThat(WebMvcTestWebDriverIntegrationTests.previousWebDriver).isNotNull().isNotSameAs(this.webDriver);
    }
}

