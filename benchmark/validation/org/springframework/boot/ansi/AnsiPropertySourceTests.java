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
package org.springframework.boot.ansi;


import AnsiBackground.GREEN;
import AnsiColor.RED;
import AnsiStyle.BOLD;
import Enabled.ALWAYS;
import Enabled.NEVER;
import org.junit.Test;


/**
 * Tests for {@link AnsiPropertySource}.
 *
 * @author Phillip Webb
 */
public class AnsiPropertySourceTests {
    private AnsiPropertySource source = new AnsiPropertySource("ansi", false);

    @Test
    public void getAnsiStyle() {
        assertThat(this.source.getProperty("AnsiStyle.BOLD")).isEqualTo(BOLD);
    }

    @Test
    public void getAnsiColor() {
        assertThat(this.source.getProperty("AnsiColor.RED")).isEqualTo(RED);
    }

    @Test
    public void getAnsiBackground() {
        assertThat(this.source.getProperty("AnsiBackground.GREEN")).isEqualTo(GREEN);
    }

    @Test
    public void getAnsi() {
        assertThat(this.source.getProperty("Ansi.BOLD")).isEqualTo(BOLD);
        assertThat(this.source.getProperty("Ansi.RED")).isEqualTo(RED);
        assertThat(this.source.getProperty("Ansi.BG_RED")).isEqualTo(AnsiBackground.RED);
    }

    @Test
    public void getMissing() {
        assertThat(this.source.getProperty("AnsiStyle.NOPE")).isNull();
    }

    @Test
    public void encodeEnabled() {
        AnsiOutput.setEnabled(ALWAYS);
        AnsiPropertySource source = new AnsiPropertySource("ansi", true);
        assertThat(source.getProperty("Ansi.RED")).isEqualTo("\u001b[31m");
    }

    @Test
    public void encodeDisabled() {
        AnsiOutput.setEnabled(NEVER);
        AnsiPropertySource source = new AnsiPropertySource("ansi", true);
        assertThat(source.getProperty("Ansi.RED")).isEqualTo("");
    }
}

