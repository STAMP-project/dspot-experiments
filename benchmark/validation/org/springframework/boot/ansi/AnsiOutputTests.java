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


import AnsiColor.GREEN;
import AnsiColor.RED;
import AnsiStyle.BOLD;
import AnsiStyle.FAINT;
import AnsiStyle.NORMAL;
import org.junit.Test;


/**
 * Tests for {@link AnsiOutput}.
 *
 * @author Phillip Webb
 */
public class AnsiOutputTests {
    @Test
    public void encoding() {
        String encoded = AnsiOutput.toString("A", RED, BOLD, "B", NORMAL, "D", GREEN, "E", FAINT, "F");
        assertThat(encoded).isEqualTo("A[31;1mB[0mD[32mE[2mF[0;39m");
    }
}

