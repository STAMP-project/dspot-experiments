/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.text;


import java.util.FormattableFlags;
import java.util.Formatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link FormattableUtils}.
 */
@Deprecated
public class FormattableUtilsTest {
    @Test
    public void testDefaultAppend() {
        Assertions.assertEquals("foo", FormattableUtils.append("foo", new Formatter(), 0, (-1), (-1)).toString());
        Assertions.assertEquals("fo", FormattableUtils.append("foo", new Formatter(), 0, (-1), 2).toString());
        Assertions.assertEquals(" foo", FormattableUtils.append("foo", new Formatter(), 0, 4, (-1)).toString());
        Assertions.assertEquals("   foo", FormattableUtils.append("foo", new Formatter(), 0, 6, (-1)).toString());
        Assertions.assertEquals(" fo", FormattableUtils.append("foo", new Formatter(), 0, 3, 2).toString());
        Assertions.assertEquals("   fo", FormattableUtils.append("foo", new Formatter(), 0, 5, 2).toString());
        Assertions.assertEquals("foo ", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 4, (-1)).toString());
        Assertions.assertEquals("foo   ", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 6, (-1)).toString());
        Assertions.assertEquals("fo ", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 3, 2).toString());
        Assertions.assertEquals("fo   ", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 5, 2).toString());
    }

    @Test
    public void testAlternatePadCharacter() {
        final char pad = '_';
        Assertions.assertEquals("foo", FormattableUtils.append("foo", new Formatter(), 0, (-1), (-1), pad).toString());
        Assertions.assertEquals("fo", FormattableUtils.append("foo", new Formatter(), 0, (-1), 2, pad).toString());
        Assertions.assertEquals("_foo", FormattableUtils.append("foo", new Formatter(), 0, 4, (-1), pad).toString());
        Assertions.assertEquals("___foo", FormattableUtils.append("foo", new Formatter(), 0, 6, (-1), pad).toString());
        Assertions.assertEquals("_fo", FormattableUtils.append("foo", new Formatter(), 0, 3, 2, pad).toString());
        Assertions.assertEquals("___fo", FormattableUtils.append("foo", new Formatter(), 0, 5, 2, pad).toString());
        Assertions.assertEquals("foo_", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 4, (-1), pad).toString());
        Assertions.assertEquals("foo___", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 6, (-1), pad).toString());
        Assertions.assertEquals("fo_", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 3, 2, pad).toString());
        Assertions.assertEquals("fo___", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 5, 2, pad).toString());
    }

    @Test
    public void testEllipsis() {
        Assertions.assertEquals("foo", FormattableUtils.append("foo", new Formatter(), 0, (-1), (-1), "*").toString());
        Assertions.assertEquals("f*", FormattableUtils.append("foo", new Formatter(), 0, (-1), 2, "*").toString());
        Assertions.assertEquals(" foo", FormattableUtils.append("foo", new Formatter(), 0, 4, (-1), "*").toString());
        Assertions.assertEquals("   foo", FormattableUtils.append("foo", new Formatter(), 0, 6, (-1), "*").toString());
        Assertions.assertEquals(" f*", FormattableUtils.append("foo", new Formatter(), 0, 3, 2, "*").toString());
        Assertions.assertEquals("   f*", FormattableUtils.append("foo", new Formatter(), 0, 5, 2, "*").toString());
        Assertions.assertEquals("foo ", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 4, (-1), "*").toString());
        Assertions.assertEquals("foo   ", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 6, (-1), "*").toString());
        Assertions.assertEquals("f* ", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 3, 2, "*").toString());
        Assertions.assertEquals("f*   ", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 5, 2, "*").toString());
        Assertions.assertEquals("foo", FormattableUtils.append("foo", new Formatter(), 0, (-1), (-1), "+*").toString());
        Assertions.assertEquals("+*", FormattableUtils.append("foo", new Formatter(), 0, (-1), 2, "+*").toString());
        Assertions.assertEquals(" foo", FormattableUtils.append("foo", new Formatter(), 0, 4, (-1), "+*").toString());
        Assertions.assertEquals("   foo", FormattableUtils.append("foo", new Formatter(), 0, 6, (-1), "+*").toString());
        Assertions.assertEquals(" +*", FormattableUtils.append("foo", new Formatter(), 0, 3, 2, "+*").toString());
        Assertions.assertEquals("   +*", FormattableUtils.append("foo", new Formatter(), 0, 5, 2, "+*").toString());
        Assertions.assertEquals("foo ", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 4, (-1), "+*").toString());
        Assertions.assertEquals("foo   ", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 6, (-1), "+*").toString());
        Assertions.assertEquals("+* ", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 3, 2, "+*").toString());
        Assertions.assertEquals("+*   ", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 5, 2, "+*").toString());
    }

    @Test
    public void testIllegalEllipsis() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FormattableUtils.append("foo", new Formatter(), 0, (-1), 1, "xx"));
    }

    @Test
    public void testAlternatePadCharAndEllipsis() {
        Assertions.assertEquals("foo", FormattableUtils.append("foo", new Formatter(), 0, (-1), (-1), '_', "*").toString());
        Assertions.assertEquals("f*", FormattableUtils.append("foo", new Formatter(), 0, (-1), 2, '_', "*").toString());
        Assertions.assertEquals("_foo", FormattableUtils.append("foo", new Formatter(), 0, 4, (-1), '_', "*").toString());
        Assertions.assertEquals("___foo", FormattableUtils.append("foo", new Formatter(), 0, 6, (-1), '_', "*").toString());
        Assertions.assertEquals("_f*", FormattableUtils.append("foo", new Formatter(), 0, 3, 2, '_', "*").toString());
        Assertions.assertEquals("___f*", FormattableUtils.append("foo", new Formatter(), 0, 5, 2, '_', "*").toString());
        Assertions.assertEquals("foo_", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 4, (-1), '_', "*").toString());
        Assertions.assertEquals("foo___", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 6, (-1), '_', "*").toString());
        Assertions.assertEquals("f*_", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 3, 2, '_', "*").toString());
        Assertions.assertEquals("f*___", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 5, 2, '_', "*").toString());
        Assertions.assertEquals("foo", FormattableUtils.append("foo", new Formatter(), 0, (-1), (-1), '_', "+*").toString());
        Assertions.assertEquals("+*", FormattableUtils.append("foo", new Formatter(), 0, (-1), 2, '_', "+*").toString());
        Assertions.assertEquals("_foo", FormattableUtils.append("foo", new Formatter(), 0, 4, (-1), '_', "+*").toString());
        Assertions.assertEquals("___foo", FormattableUtils.append("foo", new Formatter(), 0, 6, (-1), '_', "+*").toString());
        Assertions.assertEquals("_+*", FormattableUtils.append("foo", new Formatter(), 0, 3, 2, '_', "+*").toString());
        Assertions.assertEquals("___+*", FormattableUtils.append("foo", new Formatter(), 0, 5, 2, '_', "+*").toString());
        Assertions.assertEquals("foo_", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 4, (-1), '_', "+*").toString());
        Assertions.assertEquals("foo___", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 6, (-1), '_', "+*").toString());
        Assertions.assertEquals("+*_", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 3, 2, '_', "+*").toString());
        Assertions.assertEquals("+*___", FormattableUtils.append("foo", new Formatter(), FormattableFlags.LEFT_JUSTIFY, 5, 2, '_', "+*").toString());
    }
}

