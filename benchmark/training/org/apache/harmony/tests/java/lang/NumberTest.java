/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.tests.java.lang;


import junit.framework.TestCase;


public class NumberTest extends TestCase {
    /**
     * java.lang.Number#byteValue()
     */
    public void test_byteValue() {
        int number = 1231243;
        TestCase.assertTrue(("Incorrect byte returned for: " + number), (((byte) (new Integer(number).intValue())) == (new Integer(number).byteValue())));
        number = 0;
        TestCase.assertTrue(("Incorrect byte returned for: " + number), (((byte) (new Integer(number).intValue())) == (new Integer(number).byteValue())));
        number = -1;
        TestCase.assertTrue(("Incorrect byte returned for: " + number), (((byte) (new Integer(number).intValue())) == (new Integer(number).byteValue())));
        number = -84109328;
        TestCase.assertTrue(("Incorrect byte returned for: " + number), (((byte) (new Integer(number).intValue())) == (new Integer(number).byteValue())));
    }

    /**
     * java.lang.Number#shortValue()
     */
    public void test_shortValue() {
        int number = 1231243;
        TestCase.assertTrue(("Incorrect byte returned for: " + number), (((short) (new Integer(number).intValue())) == (new Integer(number).shortValue())));
        number = 0;
        TestCase.assertTrue(("Incorrect byte returned for: " + number), (((short) (new Integer(number).intValue())) == (new Integer(number).shortValue())));
        number = -1;
        TestCase.assertTrue(("Incorrect byte returned for: " + number), (((short) (new Integer(number).intValue())) == (new Integer(number).shortValue())));
        number = -84109328;
        TestCase.assertTrue(("Incorrect byte returned for: " + number), (((short) (new Integer(number).intValue())) == (new Integer(number).shortValue())));
    }
}

