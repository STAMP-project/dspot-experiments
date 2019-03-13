/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.security.config.annotation.web.configurers;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.security.web.debug.DebugFilter;
import org.springframework.test.web.servlet.MockMvc;


/**
 * Tests to verify {@code EnableWebSecurity(debug)} functionality
 *
 * @author Rob Winch
 * @author Josh Cummings
 */
public class NamespaceDebugTests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    MockMvc mvc;

    @Test
    public void requestWhenDebugSetToTrueThenLogsDebugInformation() throws Exception {
        Appender<ILoggingEvent> appender = mockAppenderFor("Spring Security Debugger");
        this.spring.register(NamespaceDebugTests.DebugWebSecurity.class).autowire();
        this.mvc.perform(get("/"));
        assertThat(filterChainClass()).isEqualTo(DebugFilter.class);
        Mockito.verify(appender, Mockito.atLeastOnce()).doAppend(ArgumentMatchers.any(ILoggingEvent.class));
    }

    @EnableWebSecurity(debug = true)
    static class DebugWebSecurity extends WebSecurityConfigurerAdapter {}

    @Test
    public void requestWhenDebugSetToFalseThenDoesNotLogDebugInformation() throws Exception {
        Appender<ILoggingEvent> appender = mockAppenderFor("Spring Security Debugger");
        this.spring.register(NamespaceDebugTests.NoDebugWebSecurity.class).autowire();
        this.mvc.perform(get("/"));
        assertThat(filterChainClass()).isNotEqualTo(DebugFilter.class);
        Mockito.verify(appender, Mockito.never()).doAppend(ArgumentMatchers.any(ILoggingEvent.class));
    }

    @EnableWebSecurity
    static class NoDebugWebSecurity extends WebSecurityConfigurerAdapter {}
}

