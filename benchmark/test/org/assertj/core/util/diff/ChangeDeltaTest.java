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
package org.assertj.core.util.diff;


import Delta.TYPE;
import Delta.TYPE.CHANGE;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class ChangeDeltaTest {
    private static List<String> EMPTY_LIST = Collections.emptyList();

    @Test
    public void testGetType() {
        // given
        Chunk<String> chunk = new Chunk(1, ChangeDeltaTest.EMPTY_LIST);
        Delta<String> delta = new ChangeDelta(chunk, chunk);
        // when
        Delta.TYPE type = delta.getType();
        // then
        Assertions.assertThat(type).isEqualTo(CHANGE);
    }

    @Test
    public void testToString() {
        // given
        Chunk<String> chunk1 = new Chunk(0, Arrays.asList("LINE1", "LINE2"));
        Chunk<String> chunk2 = new Chunk(1, Arrays.asList("line1", "line2"));
        Delta<String> delta = new ChangeDelta(chunk1, chunk2);
        // when
        String desc = delta.toString();
        // then
        Assertions.assertThat(desc).isEqualTo(String.format(("Changed content at line 1:%n" + ((((("expecting:%n" + "  [\"LINE1\",%n") + "   \"LINE2\"]%n") + "but was:%n") + "  [\"line1\",%n") + "   \"line2\"]%n"))));
    }
}

