/**
 * Copyright 2005 JBoss Inc
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
package org.kie.dmn.feel.util;


import org.junit.Assert;
import org.junit.Test;


public class EvalHelperTest {
    @Test
    public void testNormalizeSpace() {
        Assert.assertNull(EvalHelper.normalizeVariableName(null));
        Assert.assertEquals("", EvalHelper.normalizeVariableName(""));
        Assert.assertEquals("", EvalHelper.normalizeVariableName(" "));
        Assert.assertEquals("", EvalHelper.normalizeVariableName("\t"));
        Assert.assertEquals("", EvalHelper.normalizeVariableName("\n"));
        Assert.assertEquals("", EvalHelper.normalizeVariableName("\t"));
        Assert.assertEquals("", EvalHelper.normalizeVariableName("\u000b"));
        Assert.assertEquals("", EvalHelper.normalizeVariableName("\f"));
        Assert.assertEquals("", EvalHelper.normalizeVariableName("\u001c"));
        Assert.assertEquals("", EvalHelper.normalizeVariableName("\u001d"));
        Assert.assertEquals("", EvalHelper.normalizeVariableName("\u001e"));
        Assert.assertEquals("", EvalHelper.normalizeVariableName("\u001f"));
        Assert.assertEquals("", EvalHelper.normalizeVariableName("\f"));
        Assert.assertEquals("", EvalHelper.normalizeVariableName("\r"));
        Assert.assertEquals("a", EvalHelper.normalizeVariableName("  a  "));
        Assert.assertEquals("a b c", EvalHelper.normalizeVariableName("  a  b   c  "));
        Assert.assertEquals("a b c", EvalHelper.normalizeVariableName("a\t\f\r  b\u000b   c\n"));
        Assert.assertEquals("a b c", EvalHelper.normalizeVariableName("a\t\f\r  \u00a0\u00a0b\u000b   c\n"));
        Assert.assertEquals("b", EvalHelper.normalizeVariableName(" b"));
        Assert.assertEquals("b", EvalHelper.normalizeVariableName("b "));
        Assert.assertEquals("ab c", EvalHelper.normalizeVariableName("ab c  "));
        Assert.assertEquals("a b", EvalHelper.normalizeVariableName("a\u00a0b"));
    }
}

