/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.api.java.nio.charset;


import java.nio.charset.CodingErrorAction;
import junit.framework.TestCase;


/**
 * Test class java.nio.charset.CodingErrorAction
 */
public class CodingErrorActionTest extends TestCase {
    /* Test the constants. */
    public void testIGNORE() {
        TestCase.assertNotNull(CodingErrorAction.IGNORE);
        TestCase.assertNotNull(CodingErrorAction.REPLACE);
        TestCase.assertNotNull(CodingErrorAction.REPORT);
        TestCase.assertNotSame(CodingErrorAction.IGNORE, CodingErrorAction.REPLACE);
        TestCase.assertNotSame(CodingErrorAction.IGNORE, CodingErrorAction.REPORT);
        TestCase.assertNotSame(CodingErrorAction.REPLACE, CodingErrorAction.REPORT);
    }

    /* Test the method toString(). */
    public void testToString() {
        TestCase.assertTrue(((CodingErrorAction.IGNORE.toString().indexOf("IGNORE")) != (-1)));
        TestCase.assertTrue(((CodingErrorAction.REPLACE.toString().indexOf("REPLACE")) != (-1)));
        TestCase.assertTrue(((CodingErrorAction.REPORT.toString().indexOf("REPORT")) != (-1)));
    }
}

