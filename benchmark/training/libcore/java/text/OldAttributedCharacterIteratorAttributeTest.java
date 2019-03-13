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
package libcore.java.text;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.AttributedCharacterIterator;
import junit.framework.TestCase;

import static java.text.AttributedCharacterIterator.Attribute.LANGUAGE;


public class OldAttributedCharacterIteratorAttributeTest extends TestCase {
    private class MockAttributedCharacterIteratorAttribute extends AttributedCharacterIterator.Attribute {
        private static final long serialVersionUID = 1L;

        public MockAttributedCharacterIteratorAttribute(String name) {
            super(name);
        }

        @Override
        public String getName() {
            return super.getName();
        }

        @Override
        public Object readResolve() throws InvalidObjectException {
            return super.readResolve();
        }
    }

    private class TestAttributedCharacterIteratorAttribute extends AttributedCharacterIterator.Attribute {
        private static final long serialVersionUID = -2917613373935785179L;

        public TestAttributedCharacterIteratorAttribute(String name) {
            super(name);
        }
    }

    /**
     * java.text.AttributedCharacterIterator.Attribute#AttributedCharacterIterator.Attribute(java.lang.String)
     *        Test of method
     *        java.text.AttributedCharacterIterator.Attribute#AttributedCharacterIterator.Attribute(java.lang.String).
     */
    public void test_Constructor() {
        try {
            new OldAttributedCharacterIteratorAttributeTest.MockAttributedCharacterIteratorAttribute("test");
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
    }

    /**
     * java.text.AttributedCharacterIterator.Attribute#equals(java.lang.Object)
     *        Test of method
     *        java.text.AttributedCharacterIterator.Attribute#equals(java.lang.Object).
     */
    public void test_equalsLjava_lang_Object() {
        try {
            OldAttributedCharacterIteratorAttributeTest.MockAttributedCharacterIteratorAttribute mac1 = new OldAttributedCharacterIteratorAttributeTest.MockAttributedCharacterIteratorAttribute("test1");
            OldAttributedCharacterIteratorAttributeTest.MockAttributedCharacterIteratorAttribute mac2 = new OldAttributedCharacterIteratorAttributeTest.MockAttributedCharacterIteratorAttribute("test2");
            TestCase.assertFalse("Attributes are equal", mac2.equals(mac1));
            OldAttributedCharacterIteratorAttributeTest.TestAttributedCharacterIteratorAttribute mac3 = new OldAttributedCharacterIteratorAttributeTest.TestAttributedCharacterIteratorAttribute("test1");
            TestCase.assertFalse("Attributes are equal", mac3.equals(mac1));
            AttributedCharacterIterator.Attribute mac4 = mac1;
            TestCase.assertTrue("Attributes are non-equal", mac4.equals(mac1));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
    }

    /**
     * java.text.AttributedCharacterIterator.Attribute#getName() Test of
     *        method java.text.AttributedCharacterIterator.Attribute#getName().
     */
    public void test_getName() {
        try {
            OldAttributedCharacterIteratorAttributeTest.MockAttributedCharacterIteratorAttribute mac1 = new OldAttributedCharacterIteratorAttributeTest.MockAttributedCharacterIteratorAttribute("test1");
            TestCase.assertEquals("Incorrect attribute name", "test1", mac1.getName());
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
    }

    /**
     * java.text.AttributedCharacterIterator.Attribute#hashCode()
     */
    public void test_hashCode() {
        try {
            OldAttributedCharacterIteratorAttributeTest.MockAttributedCharacterIteratorAttribute mac1 = new OldAttributedCharacterIteratorAttributeTest.MockAttributedCharacterIteratorAttribute("test1");
            OldAttributedCharacterIteratorAttributeTest.TestAttributedCharacterIteratorAttribute mac2 = new OldAttributedCharacterIteratorAttributeTest.TestAttributedCharacterIteratorAttribute("test1");
            TestCase.assertTrue("The hash codes of same attributes are not equal", ((mac1.hashCode()) != (mac2.hashCode())));
            OldAttributedCharacterIteratorAttributeTest.MockAttributedCharacterIteratorAttribute mac3 = new OldAttributedCharacterIteratorAttributeTest.MockAttributedCharacterIteratorAttribute("test2");
            TestCase.assertTrue("The hash codes of different attributes are equal", ((mac1.hashCode()) != (mac3.hashCode())));
            AttributedCharacterIterator.Attribute mac4 = mac1;
            TestCase.assertTrue("The hash codes of same attributes but different hierarchy classes are not equal", ((mac1.hashCode()) == (mac4.hashCode())));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
    }

    /**
     * java.text.AttributedCharacterIterator.Attribute#readResolve() Test
     *        of method
     *        java.text.AttributedCharacterIterator.Attribute#readResolve().
     */
    public void test_readResolve() {
        OldAttributedCharacterIteratorAttributeTest.MockAttributedCharacterIteratorAttribute mac1 = new OldAttributedCharacterIteratorAttributeTest.MockAttributedCharacterIteratorAttribute("test");
        try {
            mac1.readResolve();
            TestCase.fail("InvalidObjectException has not been thrown");
        } catch (InvalidObjectException e) {
            // expected
        }
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bytes);
            AttributedCharacterIterator.Attribute attr1;
            AttributedCharacterIterator.Attribute attr2;
            attr1 = LANGUAGE;
            out.writeObject(attr1);
            in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
            try {
                attr2 = ((AttributedCharacterIterator.Attribute) (in.readObject()));
                TestCase.assertSame("resolved incorrectly", attr1, attr2);
            } catch (IllegalArgumentException e) {
                TestCase.fail(("Unexpected IllegalArgumentException: " + e));
            }
        } catch (IOException e) {
            TestCase.fail(("unexpected IOException" + e));
        } catch (ClassNotFoundException e) {
            TestCase.fail(("unexpected ClassNotFoundException" + e));
        } finally {
            try {
                if (out != null)
                    out.close();

                if (in != null)
                    in.close();

            } catch (IOException e) {
            }
        }
    }

    /**
     * java.text.AttributedCharacterIterator.Attribute#toString() Test of
     *        method java.text.AttributedCharacterIterator.Attribute#toString().
     */
    public void test_toString() {
        OldAttributedCharacterIteratorAttributeTest.MockAttributedCharacterIteratorAttribute mac1 = new OldAttributedCharacterIteratorAttributeTest.MockAttributedCharacterIteratorAttribute(null);
        TestCase.assertEquals("Unexpected class representation string", mac1.toString(), ((getClass().getName()) + "$MockAttributedCharacterIteratorAttribute(null)"));
        OldAttributedCharacterIteratorAttributeTest.TestAttributedCharacterIteratorAttribute mac2 = new OldAttributedCharacterIteratorAttributeTest.TestAttributedCharacterIteratorAttribute("test1");
        TestCase.assertEquals("Unexpected class representation string", mac2.toString(), ((getClass().getName()) + "$TestAttributedCharacterIteratorAttribute(test1)"));
    }
}

