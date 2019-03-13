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
package libcore.java.lang;


import java.util.Properties;
import junit.framework.TestCase;


public class OldIntegerTest extends TestCase {
    private Properties orgProps;

    public void test_getIntegerLjava_lang_StringI() {
        // Test for method java.lang.Integer
        // java.lang.Integer.getInteger(java.lang.String, int)
        Properties tProps = new Properties();
        tProps.put("testIncInt", "notInt");
        System.setProperties(tProps);
        TestCase.assertTrue("returned incorrect default Integer", Integer.getInteger("testIncInt", 4).equals(new Integer(4)));
    }

    public void test_getIntegerLjava_lang_StringLjava_lang_Integer() {
        // Test for method java.lang.Integer
        // java.lang.Integer.getInteger(java.lang.String, java.lang.Integer)
        Properties tProps = new Properties();
        tProps.put("testIncInt", "notInt");
        System.setProperties(tProps);
        TestCase.assertTrue("returned incorrect default Integer", Integer.getInteger("testIncInt", new Integer(4)).equals(new Integer(4)));
    }

    public void test_intValue() {
        TestCase.assertEquals(Integer.MAX_VALUE, new Integer(Integer.MAX_VALUE).intValue());
        TestCase.assertEquals(Integer.MIN_VALUE, new Integer(Integer.MIN_VALUE).intValue());
    }

    public void test_longValue() {
        TestCase.assertEquals(Integer.MAX_VALUE, new Integer(Integer.MAX_VALUE).longValue());
        TestCase.assertEquals(Integer.MIN_VALUE, new Integer(Integer.MIN_VALUE).longValue());
    }

    public void test_shortValue() {
        TestCase.assertEquals((-1), new Integer(Integer.MAX_VALUE).shortValue());
        TestCase.assertEquals(0, new Integer(Integer.MIN_VALUE).shortValue());
    }
}

