/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.api;


import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class Assertions_linesOf_Test {
    private static final List<String> EXPECTED_CONTENT = Lists.newArrayList("A text file encoded in UTF-8, with diacritics:", "? ?");

    @Test
    public void should_read_lines_of_file_with_UTF8_charset() {
        File file = new File("src/test/resources/utf8.txt");
        Assertions.assertThat(Assertions.linesOf(file, "UTF-8")).isEqualTo(Assertions_linesOf_Test.EXPECTED_CONTENT);
        Assertions.assertThat(Assertions.linesOf(file, StandardCharsets.UTF_8)).isEqualTo(Assertions_linesOf_Test.EXPECTED_CONTENT);
    }
}

