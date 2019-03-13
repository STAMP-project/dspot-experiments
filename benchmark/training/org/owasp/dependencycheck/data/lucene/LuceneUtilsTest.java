/**
 * This file is part of dependency-check-core.
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
 *
 * Copyright (c) 2012 Jeremy Long. All Rights Reserved.
 */
package org.owasp.dependencycheck.data.lucene;


import org.junit.Assert;
import org.junit.Test;
import org.owasp.dependencycheck.BaseTest;


/**
 *
 *
 * @author Jeremy Long
 */
public class LuceneUtilsTest extends BaseTest {
    /**
     * Test of appendEscapedLuceneQuery method, of class LuceneUtils.
     */
    @Test
    public void testAppendEscapedLuceneQuery() {
        StringBuilder buf = new StringBuilder();
        CharSequence text = "test encoding + - & | ! ( ) { } [ ] ^ \" ~ * ? : \\";
        String expResult = "test encoding \\+ \\- \\& \\| \\! \\( \\) \\{ \\} \\[ \\] \\^ \\\" \\~ \\* \\? \\: \\\\";
        LuceneUtils.appendEscapedLuceneQuery(buf, text);
        Assert.assertEquals(expResult, buf.toString());
    }

    /**
     * Test of appendEscapedLuceneQuery method, of class LuceneUtils.
     */
    @Test
    public void testAppendEscapedLuceneQuery_null() {
        StringBuilder buf = new StringBuilder();
        CharSequence text = null;
        LuceneUtils.appendEscapedLuceneQuery(buf, text);
        Assert.assertEquals(0, buf.length());
    }

    /**
     * Test of escapeLuceneQuery method, of class LuceneUtils.
     */
    @Test
    public void testEscapeLuceneQuery() {
        CharSequence text = "test encoding + - & | ! ( ) { } [ ] ^ \" ~ * ? : \\";
        String expResult = "test encoding \\+ \\- \\& \\| \\! \\( \\) \\{ \\} \\[ \\] \\^ \\\" \\~ \\* \\? \\: \\\\";
        String result = LuceneUtils.escapeLuceneQuery(text);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of escapeLuceneQuery method, of class LuceneUtils.
     */
    @Test
    public void testEscapeLuceneQuery_null() {
        CharSequence text = null;
        String expResult = null;
        String result = LuceneUtils.escapeLuceneQuery(text);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testIsKeyword() {
        Assert.assertTrue("'AND' is a keyword and should return true", LuceneUtils.isKeyword("And"));
        Assert.assertTrue("'OR' is a keyword and should return true", LuceneUtils.isKeyword("OR"));
        Assert.assertTrue("'NOT' is a keyword and should return true", LuceneUtils.isKeyword("nOT"));
        Assert.assertTrue("'TO' is being considered a keyword and should return true", LuceneUtils.isKeyword("TO"));
        Assert.assertTrue("'+' is being considered a keyword and should return true", LuceneUtils.isKeyword("+"));
        Assert.assertTrue("'-' is being considered a keyword and should return true", LuceneUtils.isKeyword("-"));
        Assert.assertFalse("'the' is not a keyword and should return false", LuceneUtils.isKeyword("test"));
    }
}

