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
package tests.api.java.nio.charset;


import java.nio.CharBuffer;
import java.nio.charset.Charset;
import junit.framework.TestCase;


/**
 * Super class for concrete charset test suites.
 */
public abstract class AbstractCharsetTestCase extends TestCase {
    // the canonical name of this charset
    protected final String canonicalName;

    // the aliases set
    protected final String[] aliases;

    // canEncode
    protected final boolean canEncode;

    // isRegistered
    protected final boolean isRegistered;

    // charset instance
    protected Charset testingCharset;

    /**
     * Constructor for ConcreteCharsetTest.
     */
    public AbstractCharsetTestCase(String arg0, String canonicalName, String[] aliases, boolean canEncode, boolean isRegistered) {
        super(arg0);
        this.canonicalName = canonicalName;
        this.canEncode = canEncode;
        this.isRegistered = isRegistered;
        this.aliases = aliases;
    }

    /* Test canEncode. */
    public void testCanEncode() {
        TestCase.assertEquals(this.canEncode, this.testingCharset.canEncode());
    }

    /* Test isRegistered. */
    public void testIsRegistered() {
        TestCase.assertEquals(this.isRegistered, this.testingCharset.isRegistered());
    }

    /* Test name. */
    public void testName() {
        TestCase.assertEquals(this.canonicalName, this.testingCharset.name());
        // assertEquals(this.canonicalName, this.testingCharset.displayName());
        // assertEquals(this.canonicalName,
        // this.testingCharset.displayName(null));
    }

    /* Test aliases. */
    public void testAliases() {
        for (int i = 0; i < (this.aliases.length); i++) {
            Charset c = Charset.forName(this.aliases[i]);
            TestCase.assertEquals(this.canonicalName, c.name());
            // TODO
            // assertTrue(this.testingCharset.aliases().contains(this.aliases[i]));
        }
    }

    /* Test the method encode(String) with null. */
    public void testEncode_String_Null() {
        try {
            this.testingCharset.encode(((String) (null)));
            TestCase.fail("Should throw NullPointerException!");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /* Test the method encode(CharBuffer) with null. */
    public void testEncode_CharBuffer_Null() {
        try {
            this.testingCharset.encode(((CharBuffer) (null)));
            TestCase.fail("Should throw NullPointerException!");
        } catch (NullPointerException e) {
            // expected
        }
    }
}

