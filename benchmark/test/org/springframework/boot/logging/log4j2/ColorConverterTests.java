/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.logging.log4j2;


import Level.DEBUG;
import Level.ERROR;
import Level.FATAL;
import Level.TRACE;
import Level.WARN;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.AbstractLogEvent;
import org.junit.Test;


/**
 * Tests for {@link ColorConverter}.
 *
 * @author Vladimir Tsanev
 */
public class ColorConverterTests {
    private final String in = "in";

    private ColorConverterTests.TestLogEvent event;

    @Test
    public void faint() {
        StringBuilder output = new StringBuilder();
        newConverter("faint").format(this.event, output);
        assertThat(output.toString()).isEqualTo("\u001b[2min\u001b[0;39m");
    }

    @Test
    public void red() {
        StringBuilder output = new StringBuilder();
        newConverter("red").format(this.event, output);
        assertThat(output.toString()).isEqualTo("\u001b[31min\u001b[0;39m");
    }

    @Test
    public void green() {
        StringBuilder output = new StringBuilder();
        newConverter("green").format(this.event, output);
        assertThat(output.toString()).isEqualTo("\u001b[32min\u001b[0;39m");
    }

    @Test
    public void yellow() {
        StringBuilder output = new StringBuilder();
        newConverter("yellow").format(this.event, output);
        assertThat(output.toString()).isEqualTo("\u001b[33min\u001b[0;39m");
    }

    @Test
    public void blue() {
        StringBuilder output = new StringBuilder();
        newConverter("blue").format(this.event, output);
        assertThat(output.toString()).isEqualTo("\u001b[34min\u001b[0;39m");
    }

    @Test
    public void magenta() {
        StringBuilder output = new StringBuilder();
        newConverter("magenta").format(this.event, output);
        assertThat(output.toString()).isEqualTo("\u001b[35min\u001b[0;39m");
    }

    @Test
    public void cyan() {
        StringBuilder output = new StringBuilder();
        newConverter("cyan").format(this.event, output);
        assertThat(output.toString()).isEqualTo("\u001b[36min\u001b[0;39m");
    }

    @Test
    public void highlightFatal() {
        this.event.setLevel(FATAL);
        StringBuilder output = new StringBuilder();
        newConverter(null).format(this.event, output);
        assertThat(output.toString()).isEqualTo("\u001b[31min\u001b[0;39m");
    }

    @Test
    public void highlightError() {
        this.event.setLevel(ERROR);
        StringBuilder output = new StringBuilder();
        newConverter(null).format(this.event, output);
        assertThat(output.toString()).isEqualTo("\u001b[31min\u001b[0;39m");
    }

    @Test
    public void highlightWarn() {
        this.event.setLevel(WARN);
        StringBuilder output = new StringBuilder();
        newConverter(null).format(this.event, output);
        assertThat(output.toString()).isEqualTo("\u001b[33min\u001b[0;39m");
    }

    @Test
    public void highlightDebug() {
        this.event.setLevel(DEBUG);
        StringBuilder output = new StringBuilder();
        newConverter(null).format(this.event, output);
        assertThat(output.toString()).isEqualTo("\u001b[32min\u001b[0;39m");
    }

    @Test
    public void highlightTrace() {
        this.event.setLevel(TRACE);
        StringBuilder output = new StringBuilder();
        newConverter(null).format(this.event, output);
        assertThat(output.toString()).isEqualTo("\u001b[32min\u001b[0;39m");
    }

    private static class TestLogEvent extends AbstractLogEvent {
        private Level level;

        @Override
        public Level getLevel() {
            return this.level;
        }

        public void setLevel(Level level) {
            this.level = level;
        }
    }
}

