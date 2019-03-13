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
package org.springframework.boot.cli.command.run;


import java.util.logging.Level;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * Tests for {@link SpringApplicationRunner}.
 *
 * @author Andy Wilkinson
 */
public class SpringApplicationRunnerTests {
    @Test
    public void exceptionMessageWhenSourcesContainsNoClasses() throws Exception {
        SpringApplicationRunnerConfiguration configuration = Mockito.mock(SpringApplicationRunnerConfiguration.class);
        BDDMockito.given(configuration.getClasspath()).willReturn(new String[]{ "foo", "bar" });
        BDDMockito.given(configuration.getLogLevel()).willReturn(Level.INFO);
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> new SpringApplicationRunner(configuration, new String[]{ "foo", "bar" }).compileAndRun()).withMessage("No classes found in '[foo, bar]'");
    }
}

