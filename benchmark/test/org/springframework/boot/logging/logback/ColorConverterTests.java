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
package org.springframework.boot.logging.logback;


import Level.DEBUG;
import Level.ERROR;
import Level.TRACE;
import Level.WARN;
import ch.qos.logback.classic.spi.LoggingEvent;
import java.util.Collections;
import org.junit.Test;


/**
 * Tests for {@link ColorConverter}.
 *
 * @author Phillip Webb
 */
public class ColorConverterTests {
    private ColorConverter converter;

    private LoggingEvent event;

    private final String in = "in";

    @Test
    public void faint() {
        this.converter.setOptionList(Collections.singletonList("faint"));
        String out = this.converter.transform(this.event, this.in);
        assertThat(out).isEqualTo("\u001b[2min\u001b[0;39m");
    }

    @Test
    public void red() {
        this.converter.setOptionList(Collections.singletonList("red"));
        String out = this.converter.transform(this.event, this.in);
        assertThat(out).isEqualTo("\u001b[31min\u001b[0;39m");
    }

    @Test
    public void green() {
        this.converter.setOptionList(Collections.singletonList("green"));
        String out = this.converter.transform(this.event, this.in);
        assertThat(out).isEqualTo("\u001b[32min\u001b[0;39m");
    }

    @Test
    public void yellow() {
        this.converter.setOptionList(Collections.singletonList("yellow"));
        String out = this.converter.transform(this.event, this.in);
        assertThat(out).isEqualTo("\u001b[33min\u001b[0;39m");
    }

    @Test
    public void blue() {
        this.converter.setOptionList(Collections.singletonList("blue"));
        String out = this.converter.transform(this.event, this.in);
        assertThat(out).isEqualTo("\u001b[34min\u001b[0;39m");
    }

    @Test
    public void magenta() {
        this.converter.setOptionList(Collections.singletonList("magenta"));
        String out = this.converter.transform(this.event, this.in);
        assertThat(out).isEqualTo("\u001b[35min\u001b[0;39m");
    }

    @Test
    public void cyan() {
        this.converter.setOptionList(Collections.singletonList("cyan"));
        String out = this.converter.transform(this.event, this.in);
        assertThat(out).isEqualTo("\u001b[36min\u001b[0;39m");
    }

    @Test
    public void highlightError() {
        this.event.setLevel(ERROR);
        String out = this.converter.transform(this.event, this.in);
        assertThat(out).isEqualTo("\u001b[31min\u001b[0;39m");
    }

    @Test
    public void highlightWarn() {
        this.event.setLevel(WARN);
        String out = this.converter.transform(this.event, this.in);
        assertThat(out).isEqualTo("\u001b[33min\u001b[0;39m");
    }

    @Test
    public void highlightDebug() {
        this.event.setLevel(DEBUG);
        String out = this.converter.transform(this.event, this.in);
        assertThat(out).isEqualTo("\u001b[32min\u001b[0;39m");
    }

    @Test
    public void highlightTrace() {
        this.event.setLevel(TRACE);
        String out = this.converter.transform(this.event, this.in);
        assertThat(out).isEqualTo("\u001b[32min\u001b[0;39m");
    }
}

