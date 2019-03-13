/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.util;


import java.util.Map;
import junit.framework.TestCase;
import org.junit.Test;


/**
 * Test class for the basic functionality of StringUtil.
 *
 * @author Sven Boden
 */
public class StringUtilTest extends TestCase {
    /**
     * Test initCap for JIRA PDI-619.
     */
    public void testinitCap() {
        TestCase.assertEquals("", StringUtil.initCap(null));
        TestCase.assertEquals("", StringUtil.initCap(""));
        TestCase.assertEquals("", StringUtil.initCap("   "));
        TestCase.assertEquals("A word", StringUtil.initCap("a word"));
        TestCase.assertEquals("A word", StringUtil.initCap("A word"));
        TestCase.assertEquals("Award", StringUtil.initCap("award"));
        TestCase.assertEquals("Award", StringUtil.initCap("Award"));
        TestCase.assertEquals("AWard", StringUtil.initCap("aWard"));
        TestCase.assertEquals("AWard", StringUtil.initCap("AWard"));
    }

    /**
     * Test the basic substitute call.
     */
    public void testSubstituteBasic() {
        Map<String, String> map = createVariables1("${", "}");
        TestCase.assertEquals("|${BAD_KEY}|", StringUtil.substitute("|${BAD_KEY}|", map, "${", "}"));
        TestCase.assertEquals("|${NULL}|", StringUtil.substitute("|${NULL}|", map, "${", "}"));
        TestCase.assertEquals("||", StringUtil.substitute("|${EMPTY}|", map, "${", "}"));
        TestCase.assertEquals("|case1|", StringUtil.substitute("|${checkcase}|", map, "${", "}"));
        TestCase.assertEquals("|case2|", StringUtil.substitute("|${CheckCase}|", map, "${", "}"));
        TestCase.assertEquals("|case3|", StringUtil.substitute("|${CHECKCASE}|", map, "${", "}"));
        TestCase.assertEquals("|Arecurse|", StringUtil.substitute("|${recursive1}|", map, "${", "}"));
        TestCase.assertEquals("|recurseB|", StringUtil.substitute("|${recursive3}|", map, "${", "}"));
        TestCase.assertEquals("|ZfinalB|", StringUtil.substitute("|${recursive5}|", map, "${", "}"));
        try {
            StringUtil.substitute("|${recursive_all}|", map, "${", "}", 0);
            TestCase.fail("recursive check is failing");
        } catch (RuntimeException rex) {
        }
        map = createVariables1("%%", "%%");
        TestCase.assertEquals("||", StringUtil.substitute("|%%EMPTY%%|", map, "%%", "%%"));
        TestCase.assertEquals("|case1|", StringUtil.substitute("|%%checkcase%%|", map, "%%", "%%"));
        TestCase.assertEquals("|case2|", StringUtil.substitute("|%%CheckCase%%|", map, "%%", "%%"));
        TestCase.assertEquals("|case3|", StringUtil.substitute("|%%CHECKCASE%%|", map, "%%", "%%"));
        TestCase.assertEquals("|Arecurse|", StringUtil.substitute("|%%recursive1%%|", map, "%%", "%%"));
        TestCase.assertEquals("|recurseB|", StringUtil.substitute("|%%recursive3%%|", map, "%%", "%%"));
        TestCase.assertEquals("|ZfinalB|", StringUtil.substitute("|%%recursive5%%|", map, "%%", "%%"));
        try {
            StringUtil.substitute("|%%recursive_all%%|", map, "%%", "%%");
            TestCase.fail("recursive check is failing");
        } catch (RuntimeException rex) {
        }
        map = createVariables1("${", "}");
        TestCase.assertEquals("||", StringUtil.environmentSubstitute("|%%EMPTY%%|", map));
        TestCase.assertEquals("|case1|", StringUtil.environmentSubstitute("|%%checkcase%%|", map));
        TestCase.assertEquals("|case2|", StringUtil.environmentSubstitute("|%%CheckCase%%|", map));
        TestCase.assertEquals("|case3|", StringUtil.environmentSubstitute("|%%CHECKCASE%%|", map));
        TestCase.assertEquals("|Arecurse|", StringUtil.environmentSubstitute("|%%recursive1%%|", map));
        TestCase.assertEquals("|recurseB|", StringUtil.environmentSubstitute("|%%recursive3%%|", map));
        TestCase.assertEquals("|ZfinalB|", StringUtil.environmentSubstitute("|%%recursive5%%|", map));
        try {
            StringUtil.environmentSubstitute("|%%recursive_all%%|", map);
            TestCase.fail("recursive check is failing");
        } catch (RuntimeException rex) {
        }
    }

    /**
     * Test isEmpty() call.
     */
    public void testIsEmpty() {
        TestCase.assertTrue(StringUtil.isEmpty(((String) (null))));
        TestCase.assertTrue(StringUtil.isEmpty(""));
        TestCase.assertFalse(StringUtil.isEmpty("A"));
        TestCase.assertFalse(StringUtil.isEmpty(" A "));
    }

    /**
     * Test getIndent() call.
     */
    public void testGetIndent() {
        TestCase.assertEquals("", StringUtil.getIndent(0));
        TestCase.assertEquals(" ", StringUtil.getIndent(1));
        TestCase.assertEquals("  ", StringUtil.getIndent(2));
        TestCase.assertEquals("   ", StringUtil.getIndent(3));
    }

    public void testIsVariable() throws Exception {
        TestCase.assertTrue(StringUtil.isVariable("${somename}"));
        TestCase.assertTrue(StringUtil.isVariable("%%somename%%"));
        TestCase.assertTrue(StringUtil.isVariable("$[somename]"));
        TestCase.assertFalse(StringUtil.isVariable("somename"));
        TestCase.assertFalse(StringUtil.isVariable(null));
    }

    public void testSafeToLowerCase() {
        TestCase.assertNull(StringUtil.safeToLowerCase(null));
        TestCase.assertEquals("", StringUtil.safeToLowerCase(""));
        TestCase.assertEquals(" ", StringUtil.safeToLowerCase(" "));
        TestCase.assertEquals("abc123", StringUtil.safeToLowerCase("abc123"));
        TestCase.assertEquals("abc123", StringUtil.safeToLowerCase("Abc123"));
        TestCase.assertEquals("abc123", StringUtil.safeToLowerCase("ABC123"));
        TestCase.assertNull(StringUtil.safeToLowerCase(new StringUtilTest.ToString()));
        TestCase.assertNull(StringUtil.safeToLowerCase(new StringUtilTest.ToString().toString()));
        TestCase.assertEquals("abc123", StringUtil.safeToLowerCase(new StringUtilTest.ToString("ABC123")));
        TestCase.assertEquals("abc123", StringUtil.safeToLowerCase(new StringUtilTest.ToString("ABC123").toString()));
    }

    class ToString {
        private String string;

        ToString() {
        }

        ToString(final String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    @Test
    public void testTrimStart_Single() {
        TestCase.assertEquals("file/path/", StringUtil.trimStart("/file/path/", '/'));
    }

    @Test
    public void testTrimStart_Many() {
        TestCase.assertEquals("file/path/", StringUtil.trimStart("////file/path/", '/'));
    }

    @Test
    public void testTrimStart_None() {
        TestCase.assertEquals("file/path/", StringUtil.trimStart("file/path/", '/'));
    }

    @Test
    public void testTrimEnd_Single() {
        TestCase.assertEquals("/file/path", StringUtil.trimEnd("/file/path/", '/'));
    }

    @Test
    public void testTrimEnd_Many() {
        TestCase.assertEquals("/file/path", StringUtil.trimEnd("/file/path///", '/'));
    }

    @Test
    public void testTrimEnd_None() {
        TestCase.assertEquals("/file/path", StringUtil.trimEnd("/file/path", '/'));
    }
}

