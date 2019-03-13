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


import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;

import static java.text.AttributedCharacterIterator.Attribute.INPUT_METHOD_SEGMENT;
import static java.text.AttributedCharacterIterator.Attribute.LANGUAGE;
import static java.text.AttributedCharacterIterator.Attribute.READING;


public class OldAttributedCharacterIteratorTest extends TestCase {
    AttributedCharacterIterator it;

    String string = "test test";

    public void test_getRunLimitLSet() {
        AttributedString as = new AttributedString("test");
        as.addAttribute(LANGUAGE, "a", 2, 3);
        AttributedCharacterIterator it = as.getIterator();
        HashSet<AttributedCharacterIterator.Attribute> attr = new HashSet<AttributedCharacterIterator.Attribute>();
        attr.add(LANGUAGE);
        TestCase.assertEquals("non-null value limit", 2, it.getRunLimit(attr));
        as = new AttributedString("test");
        as.addAttribute(LANGUAGE, null, 2, 3);
        it = as.getIterator();
        TestCase.assertEquals("null value limit", 4, it.getRunLimit(attr));
        attr.add(READING);
        TestCase.assertEquals("null value limit", 4, it.getRunLimit(attr));
    }

    public void test_getAllAttributeKeys() {
        AttributedString as = new AttributedString("test");
        AttributedCharacterIterator it = as.getIterator();
        Set<AttributedCharacterIterator.Attribute> emptyAttributes = it.getAllAttributeKeys();
        TestCase.assertTrue(emptyAttributes.isEmpty());
        int attrCount = 10;
        for (int i = 0; i < attrCount; i++) {
            as.addAttribute(LANGUAGE, "a");
        }
        it = as.getIterator();
        Set<AttributedCharacterIterator.Attribute> attributes = it.getAllAttributeKeys();
        for (AttributedCharacterIterator.Attribute attr : attributes) {
            TestCase.assertEquals(LANGUAGE, attr);
        }
    }

    public void test_getAttributeLAttributedCharacterIterator_Attribute() {
        Object attribute = it.getAttribute(LANGUAGE);
        TestCase.assertEquals("ENGLISH", attribute);
        attribute = it.getAttribute(READING);
        TestCase.assertEquals("READ", attribute);
        TestCase.assertNull(it.getAttribute(INPUT_METHOD_SEGMENT));
    }

    public void test_getAttributes() {
        Map<AttributedCharacterIterator.Attribute, Object> attributes = it.getAttributes();
        TestCase.assertEquals(2, attributes.size());
        TestCase.assertEquals("ENGLISH", attributes.get(LANGUAGE));
        TestCase.assertEquals("READ", attributes.get(READING));
        AttributedString as = new AttributedString("test");
        TestCase.assertTrue(as.getIterator().getAttributes().isEmpty());
    }

    public void test_getRunLimit() {
        int limit = it.getRunLimit();
        TestCase.assertEquals(string.length(), limit);
        AttributedString as = new AttributedString("");
        TestCase.assertEquals(0, as.getIterator().getRunLimit());
        as = new AttributedString(new AttributedString("test text").getIterator(), 2, 7);
        AttributedCharacterIterator it = as.getIterator();
        TestCase.assertEquals(5, it.getRunLimit());
    }

    public void test_getRunLimitLAttribute() {
        AttributedString as = new AttributedString("");
        TestCase.assertEquals(0, as.getIterator().getRunLimit(LANGUAGE));
        as = new AttributedString("text");
        as.addAttribute(LANGUAGE, "ENGLISH");
        as.addAttribute(READING, "READ", 1, 3);
        TestCase.assertEquals(4, as.getIterator().getRunLimit(LANGUAGE));
        TestCase.assertEquals(1, as.getIterator().getRunLimit(READING));
    }

    public void test_getRunStart() {
        TestCase.assertEquals(0, it.getRunStart());
        AttributedString as = new AttributedString("");
        TestCase.assertEquals(0, as.getIterator().getRunStart());
        as = new AttributedString(new AttributedString("test text").getIterator(), 2, 7);
        AttributedCharacterIterator it = as.getIterator();
        TestCase.assertEquals(0, it.getRunStart());
        as.addAttribute(LANGUAGE, "GERMAN", 1, 2);
        as.addAttribute(READING, "READ", 1, 3);
        TestCase.assertEquals(0, as.getIterator().getRunStart());
    }

    public void test_getRunStartLAttribute() {
        TestCase.assertEquals(0, it.getRunStart(LANGUAGE));
        AttributedString as = new AttributedString("test text");
        as.addAttribute(LANGUAGE, "GERMAN", 2, 5);
        as.addAttribute(READING, "READ", 2, 7);
        TestCase.assertEquals(0, as.getIterator().getRunStart(LANGUAGE));
        TestCase.assertEquals(0, as.getIterator().getRunStart(READING));
    }

    public void test_getRunStartLjava_util_Set() {
        AttributedString as = new AttributedString("test");
        as.addAttribute(LANGUAGE, "a", 2, 3);
        AttributedCharacterIterator it = as.getIterator();
        HashSet<AttributedCharacterIterator.Attribute> attr = new HashSet<AttributedCharacterIterator.Attribute>();
        attr.add(LANGUAGE);
        TestCase.assertEquals(0, it.getRunStart(attr));
        as = new AttributedString("test");
        as.addAttribute(LANGUAGE, "ENGLISH", 1, 3);
        it = as.getIterator();
        TestCase.assertEquals(0, it.getRunStart(attr));
        attr.add(READING);
        TestCase.assertEquals(0, it.getRunStart(attr));
    }
}

