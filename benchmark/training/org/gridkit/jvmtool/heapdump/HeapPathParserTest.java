/**
 * Copyright 2014 Alexey Ragozin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gridkit.jvmtool.heapdump;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HeapPathParserTest {
    String expr;

    boolean strictPath;

    String expected;

    public HeapPathParserTest(String expr, boolean strictPath, String expected) {
        this.expr = expr;
        this.strictPath = strictPath;
        this.expected = expected;
    }

    @Test
    public void testExpr() {
        System.out.println(("EXPR: " + (expr)));
        if ((expected) != null) {
            String text;
            text = Arrays.toString(HeapPath.parsePath(expr, strictPath));
            System.out.println(text);
            assertThat(text).isEqualTo(expected);
        } else {
            try {
                System.out.println(Arrays.toString(HeapPath.parsePath(expr, strictPath)));
                Assert.fail("Exception expected");
            } catch (IllegalArgumentException e) {
                // expected
            }
        }
    }
}

