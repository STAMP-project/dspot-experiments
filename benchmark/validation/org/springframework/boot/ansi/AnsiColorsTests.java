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


import AnsiColor.BLACK;
import AnsiColor.BLUE;
import AnsiColor.BRIGHT_BLACK;
import AnsiColor.BRIGHT_BLUE;
import AnsiColor.BRIGHT_CYAN;
import AnsiColor.BRIGHT_GREEN;
import AnsiColor.BRIGHT_MAGENTA;
import AnsiColor.BRIGHT_RED;
import AnsiColor.BRIGHT_WHITE;
import AnsiColor.BRIGHT_YELLOW;
import AnsiColor.CYAN;
import AnsiColor.GREEN;
import AnsiColor.MAGENTA;
import AnsiColor.RED;
import AnsiColor.WHITE;
import AnsiColor.YELLOW;
import org.junit.Test;


/**
 * Tests for {@link AnsiColors}.
 *
 * @author Phillip Webb
 */
public class AnsiColorsTests {
    @Test
    public void getClosestWhenExactMatchShouldReturnAnsiColor() {
        assertThat(getClosest(0)).isEqualTo(BLACK);
        assertThat(getClosest(11141120)).isEqualTo(RED);
        assertThat(getClosest(43520)).isEqualTo(GREEN);
        assertThat(getClosest(11162880)).isEqualTo(YELLOW);
        assertThat(getClosest(170)).isEqualTo(BLUE);
        assertThat(getClosest(11141290)).isEqualTo(MAGENTA);
        assertThat(getClosest(43690)).isEqualTo(CYAN);
        assertThat(getClosest(11184810)).isEqualTo(WHITE);
        assertThat(getClosest(5592405)).isEqualTo(BRIGHT_BLACK);
        assertThat(getClosest(16733525)).isEqualTo(BRIGHT_RED);
        assertThat(getClosest(5635840)).isEqualTo(BRIGHT_GREEN);
        assertThat(getClosest(16777045)).isEqualTo(BRIGHT_YELLOW);
        assertThat(getClosest(5592575)).isEqualTo(BRIGHT_BLUE);
        assertThat(getClosest(16733695)).isEqualTo(BRIGHT_MAGENTA);
        assertThat(getClosest(5636095)).isEqualTo(BRIGHT_CYAN);
        assertThat(getClosest(16777215)).isEqualTo(BRIGHT_WHITE);
    }

    @Test
    public void getClosestWhenCloseShouldReturnAnsiColor() {
        assertThat(getClosest(2696228)).isEqualTo(BLACK);
        assertThat(getClosest(9181465)).isEqualTo(RED);
        assertThat(getClosest(762123)).isEqualTo(GREEN);
        assertThat(getClosest(11886345)).isEqualTo(YELLOW);
        assertThat(getClosest(723873)).isEqualTo(BLUE);
        assertThat(getClosest(10687139)).isEqualTo(MAGENTA);
        assertThat(getClosest(767413)).isEqualTo(CYAN);
        assertThat(getClosest(12236470)).isEqualTo(WHITE);
        assertThat(getClosest(6380122)).isEqualTo(BRIGHT_BLACK);
        assertThat(getClosest(15872819)).isEqualTo(BRIGHT_RED);
        assertThat(getClosest(5629964)).isEqualTo(BRIGHT_GREEN);
        assertThat(getClosest(16119116)).isEqualTo(BRIGHT_YELLOW);
        assertThat(getClosest(5658352)).isEqualTo(BRIGHT_BLUE);
        assertThat(getClosest(16404730)).isEqualTo(BRIGHT_MAGENTA);
        assertThat(getClosest(5699061)).isEqualTo(BRIGHT_CYAN);
        assertThat(getClosest(15594997)).isEqualTo(BRIGHT_WHITE);
    }
}

