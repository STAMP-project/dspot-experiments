/**
 * This file is part of the Heritrix web crawler (crawler.archive.org).
 *
 *  Licensed to the Internet Archive (IA) by one or more individual
 *  contributors.
 *
 *  The IA licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.archive.util;


import java.io.IOException;
import java.io.StringReader;
import junit.framework.TestCase;


/**
 *
 *
 * @author gojomo
 */
public class SurtPrefixSetTest extends TestCase {
    private static final String ARCHIVE_ORG_DOMAIN_SURT = "http://(org,archive,";

    private static final String WWW_EXAMPLE_ORG_HOST_SURT = "http://(org,example,www,)";

    private static final String HOME_EXAMPLE_ORG_PATH_SURT = "http://(org,example,home,)/pages/";

    private static final String BOK_IS_REDUNDANT_SURT = "http://(is,bok,";

    private static final String IS_DOMAIN_SURT = "http://(is,";

    private static final String WWW_BOK_IS_REDUNDANT_SURT = "http://(is,bok,www";

    private static final String TEST_SURT_LIST = ((((((((((("# a test set of surt prefixes \n" + (SurtPrefixSetTest.ARCHIVE_ORG_DOMAIN_SURT)) + "\n") + (SurtPrefixSetTest.WWW_EXAMPLE_ORG_HOST_SURT)) + "\n") + (SurtPrefixSetTest.HOME_EXAMPLE_ORG_PATH_SURT)) + "\n") + (SurtPrefixSetTest.BOK_IS_REDUNDANT_SURT)) + " # is redundant\n") + (SurtPrefixSetTest.IS_DOMAIN_SURT)) + "\n") + (SurtPrefixSetTest.WWW_BOK_IS_REDUNDANT_SURT)) + " # is redundant\n";

    /**
     * Create a new SurtPrefixSetTest object
     *
     * @param testName
     * 		the name of the test
     */
    public SurtPrefixSetTest(final String testName) {
        super(testName);
    }

    public void testMisc() throws IOException {
        SurtPrefixSet surts = new SurtPrefixSet();
        StringReader sr = new StringReader(SurtPrefixSetTest.TEST_SURT_LIST);
        surts.importFrom(sr);
        assertContains(surts, SurtPrefixSetTest.ARCHIVE_ORG_DOMAIN_SURT);
        assertContains(surts, SurtPrefixSetTest.WWW_EXAMPLE_ORG_HOST_SURT);
        assertContains(surts, SurtPrefixSetTest.HOME_EXAMPLE_ORG_PATH_SURT);
        assertContains(surts, SurtPrefixSetTest.IS_DOMAIN_SURT);
        assertDoesntContain(surts, SurtPrefixSetTest.BOK_IS_REDUNDANT_SURT);
        assertDoesntContain(surts, SurtPrefixSetTest.WWW_BOK_IS_REDUNDANT_SURT);
        assertContainsPrefix(surts, SURT.fromURI("http://example.is/foo"));
        assertDoesntContainPrefix(surts, SURT.fromURI("http://home.example.org/foo"));
    }

    public void testImportFromUris() throws IOException {
        String seed = "http://www.archive.org/index.html";
        TestCase.assertEquals(("Convert failed " + seed), "http://(org,archive,www,)/", makeSurtPrefix(seed));
        seed = "http://timmknibbs4senate.blogspot.com/";
        TestCase.assertEquals(("Convert failed " + seed), "http://(com,blogspot,timmknibbs4senate,)/", makeSurtPrefix(seed));
        seed = "https://one.two.three";
        TestCase.assertEquals(("Convert failed " + seed), "http://(three,two,one,", makeSurtPrefix(seed));
        seed = "https://xone.two.three/a/b/c/";
        TestCase.assertEquals(("Convert failed " + seed), "http://(three,two,xone,)/a/b/c/", makeSurtPrefix(seed));
        seed = "https://yone.two.three/a/b/c";
        TestCase.assertEquals(("Convert failed " + seed), "http://(three,two,yone,)/a/b/", makeSurtPrefix(seed));
    }
}

