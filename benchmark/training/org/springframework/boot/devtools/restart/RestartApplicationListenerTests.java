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
package org.springframework.boot.devtools.restart;


import Ordered.HIGHEST_PRECEDENCE;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link RestartApplicationListener}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class RestartApplicationListenerTests {
    private static final String ENABLED_PROPERTY = "spring.devtools.restart.enabled";

    private static final String[] ARGS = new String[]{ "a", "b", "c" };

    @Rule
    public final OutputCapture output = new OutputCapture();

    @Test
    public void isHighestPriority() {
        assertThat(new RestartApplicationListener().getOrder()).isEqualTo(HIGHEST_PRECEDENCE);
    }

    @Test
    public void initializeWithReady() {
        testInitialize(false);
        assertThat(Restarter.getInstance()).hasFieldOrPropertyWithValue("args", RestartApplicationListenerTests.ARGS);
        assertThat(Restarter.getInstance().isFinished()).isTrue();
        assertThat(((java.util.List<?>) (ReflectionTestUtils.getField(Restarter.getInstance(), "rootContexts")))).isNotEmpty();
    }

    @Test
    public void initializeWithFail() {
        testInitialize(true);
        assertThat(Restarter.getInstance()).hasFieldOrPropertyWithValue("args", RestartApplicationListenerTests.ARGS);
        assertThat(Restarter.getInstance().isFinished()).isTrue();
        assertThat(((java.util.List<?>) (ReflectionTestUtils.getField(Restarter.getInstance(), "rootContexts")))).isEmpty();
    }

    @Test
    public void disableWithSystemProperty() {
        System.setProperty(RestartApplicationListenerTests.ENABLED_PROPERTY, "false");
        this.output.reset();
        testInitialize(false);
        assertThat(Restarter.getInstance()).hasFieldOrPropertyWithValue("enabled", false);
        assertThat(this.output.toString()).contains("Restart disabled due to System property");
    }
}

