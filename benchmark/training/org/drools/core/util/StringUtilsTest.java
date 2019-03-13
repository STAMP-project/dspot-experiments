/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.util;


import org.junit.Assert;
import org.junit.Test;


public class StringUtilsTest {
    @Test
    public void testFindEndOfMethodArgsIndex() {
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"myId\")", 12);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"myId\").call()", 12);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('myId')", 12);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('myId').call()", 12);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my\'Id\")", 13);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my\'Id\").call()", 13);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my\'Id\'\")", 14);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my\'Id\'\").call()", 14);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\'my\"Id\"\')", 14);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\'my\"Id\"\').call()", 14);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\'my\"Id\')", 13);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\'my\"Id\').call()", 13);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my\\\"Id\")", 14);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my\\\"Id\").call()", 14);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId('myId', 'something')", 25);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"myId\", \"something\")", 25);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my\'Id\", \"somet\'hing\")", 27);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my\'Id\'\", \"somet\'hing\")", 28);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setId(\"my\'(Id\", \"somet\'(hing\'\")", 30);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setObject(new Object())", 22);
        findEndOfMethodArgsIndexAndAssertItEqualsToExpected("setObject(new Object(\"string param\"))", 36);
    }

    @Test
    public void test_codeAwareEqualsIgnoreSpaces() {
        Assert.assertTrue(StringUtils.codeAwareEqualsIgnoreSpaces(null, null));
        Assert.assertTrue(StringUtils.codeAwareEqualsIgnoreSpaces("", ""));
        Assert.assertFalse(StringUtils.codeAwareEqualsIgnoreSpaces("", null));
        Assert.assertFalse(StringUtils.codeAwareEqualsIgnoreSpaces(null, ""));
        Assert.assertTrue(StringUtils.codeAwareEqualsIgnoreSpaces(" ", ""));
        Assert.assertTrue(StringUtils.codeAwareEqualsIgnoreSpaces("", " "));
        Assert.assertTrue(StringUtils.codeAwareEqualsIgnoreSpaces(" ", "  "));
        Assert.assertTrue(// <<- DIFF 3x
        StringUtils.codeAwareEqualsIgnoreSpaces("rule Rx when then end", " rule Rx  when then end "));
        Assert.assertTrue(// <<- DIFF, both terminate with whitespace but different types
        StringUtils.codeAwareEqualsIgnoreSpaces("rule Rx when then end\n", " rule Rx  when then end\n "));
        Assert.assertFalse(StringUtils.codeAwareEqualsIgnoreSpaces("package org.drools.compiler\n", ("package org.drools.compiler\n " + ((("rule Rx when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n"))));
        Assert.assertTrue(StringUtils.codeAwareEqualsIgnoreSpaces(("package org.drools.compiler\n" + ((("rule Rx when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n")), ("package org.drools.compiler\n "// <<- DIFF
         + ((("rule Rx when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n"))));
        Assert.assertTrue(StringUtils.codeAwareEqualsIgnoreSpaces(("package org.drools.compiler\n" + ((("rule Rx when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n")), (" package org.drools.compiler\n"// <<- DIFF (at beginning of this line)
         + ((("rule Rx when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n"))));
        Assert.assertTrue(// <<- DIFF
        StringUtils.codeAwareEqualsIgnoreSpaces(("package org.drools.compiler\n" + ((("rule Rx when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n")), (" package org.drools.compiler\n "// <<- DIFF 2x
         + ((("rule Rx when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n "))));
        Assert.assertTrue(StringUtils.codeAwareEqualsIgnoreSpaces(("package org.drools.compiler\n" + ((("rule Rx when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n")), ("package org.drools.compiler\n" + ((("rule Rx when\n" + "   $m : Message( message == \"Hello World\"  )\n")// <<- DIFF
         + "then\n") + "end\n"))));
        Assert.assertFalse(StringUtils.codeAwareEqualsIgnoreSpaces(("package org.drools.compiler\n" + ((("rule Rx when\n" + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "end\n")), ("package org.drools.compiler\n" + ((("rule Rx when\n" + "   $m : Message( message == \"Hello    World\" )\n")// <<- DIFF
         + "then\n") + "end\n"))));
        Assert.assertFalse(StringUtils.codeAwareEqualsIgnoreSpaces(("package org.drools.compiler\n" + ((("rule Rx when\n" + "   $m : Message( message == \"Hello\' World\" )\n") + "then\n") + "end\n")), ("package org.drools.compiler\n" + ((("rule Rx when\n" + "   $m : Message( message == \"Hello\'    World\" )\n")// <<- DIFF
         + "then\n") + "end\n"))));
        Assert.assertFalse(StringUtils.codeAwareEqualsIgnoreSpaces(("package org.drools.compiler\n" + ((("rule Rx when\n" + "   $m : Message( message == \'Hello World\' )\n") + "then\n") + "end\n")), ("package org.drools.compiler\n" + ((("rule Rx when\n" + "   $m : Message( message == \'Hello    World\' )\n")// <<- DIFF
         + "then\n") + "end\n"))));
        Assert.assertFalse(StringUtils.codeAwareEqualsIgnoreSpaces(("package org.drools.compiler\n" + ((("rule Rx when\n" + "   $m : Message( message == \'Hello\" World\' )\n") + "then\n") + "end\n")), ("package org.drools.compiler\n" + ((("rule Rx when\n" + "   $m : Message( message == \'Hello\"    World\' )\n")// <<- DIFF
         + "then\n") + "end\n"))));
        Assert.assertFalse(StringUtils.codeAwareEqualsIgnoreSpaces(("package org.drools.compiler\n" + ((("rule Rx when\n" + "   $m : Message( message == \'Hello\\\' World\' )\n") + "then\n") + "end\n")), ("package org.drools.compiler\n" + ((("rule Rx when\n" + "   $m : Message( message == \'Hello\\\'    World\' )\n")// <<- DIFF
         + "then\n") + "end\n"))));
    }

    @Test
    public void test_indexOfOutOfQuotes() {
        Assert.assertEquals(0, StringUtils.indexOfOutOfQuotes("bla\"bla\"bla", "bla"));
        Assert.assertEquals(5, StringUtils.indexOfOutOfQuotes("\"bla\"bla", "bla"));
        Assert.assertEquals((-1), StringUtils.indexOfOutOfQuotes("\"bla\"", "bla"));
        Assert.assertEquals(0, StringUtils.indexOfOutOfQuotes("bla\"bla\"bla", "bla", 0));
        Assert.assertEquals(8, StringUtils.indexOfOutOfQuotes("bla\"bla\"bla", "bla", 1));
        Assert.assertEquals((-1), StringUtils.indexOfOutOfQuotes("bla\"bla\"bla", "bla", 9));
    }
}

