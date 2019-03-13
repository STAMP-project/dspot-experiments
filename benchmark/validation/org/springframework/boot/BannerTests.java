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
package org.springframework.boot;


import Mode.OFF;
import java.io.PrintStream;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


/**
 * Tests for {@link Banner} and its usage by {@link SpringApplication}.
 *
 * @author Phillip Webb
 * @author Michael Stummvoll
 * @author Michael Simons
 */
public class BannerTests {
    private ConfigurableApplicationContext context;

    @Rule
    public OutputCapture out = new OutputCapture();

    @Captor
    private ArgumentCaptor<Class<?>> sourceClassCaptor;

    @Test
    public void testDefaultBanner() {
        SpringApplication application = createSpringApplication();
        this.context = application.run();
        assertThat(this.out.toString()).contains(":: Spring Boot ::");
    }

    @Test
    public void testDefaultBannerInLog() {
        SpringApplication application = createSpringApplication();
        this.context = application.run();
        assertThat(this.out.toString()).contains(":: Spring Boot ::");
    }

    @Test
    public void testCustomBanner() {
        SpringApplication application = createSpringApplication();
        application.setBanner(new BannerTests.DummyBanner());
        this.context = application.run();
        assertThat(this.out.toString()).contains("My Banner");
    }

    @Test
    public void testBannerInContext() {
        SpringApplication application = createSpringApplication();
        this.context = application.run();
        assertThat(this.context.containsBean("springBootBanner")).isTrue();
    }

    @Test
    public void testCustomBannerInContext() {
        SpringApplication application = createSpringApplication();
        Banner banner = Mockito.mock(Banner.class);
        application.setBanner(banner);
        this.context = application.run();
        Banner printedBanner = ((Banner) (this.context.getBean("springBootBanner")));
        assertThat(printedBanner).hasFieldOrPropertyWithValue("banner", banner);
        Mockito.verify(banner).printBanner(ArgumentMatchers.any(Environment.class), this.sourceClassCaptor.capture(), ArgumentMatchers.any(PrintStream.class));
        Mockito.reset(banner);
        printedBanner.printBanner(this.context.getEnvironment(), null, System.out);
        Mockito.verify(banner).printBanner(ArgumentMatchers.any(Environment.class), ArgumentMatchers.eq(this.sourceClassCaptor.getValue()), ArgumentMatchers.any(PrintStream.class));
    }

    @Test
    public void testDisableBannerInContext() {
        SpringApplication application = createSpringApplication();
        application.setBannerMode(OFF);
        this.context = application.run();
        assertThat(this.context.containsBean("springBootBanner")).isFalse();
    }

    static class DummyBanner implements Banner {
        @Override
        public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
            out.println("My Banner");
        }
    }

    @Configuration
    public static class Config {}
}

