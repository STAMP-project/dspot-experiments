/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.text;


import java.io.IOException;
import java.io.InvalidObjectException;
import java.text.AttributedCharacterIterator;
import java.text.DateFormat;
import java.text.NumberFormat;
import junit.framework.TestCase;
import libcore.util.SerializationTester;

import static java.text.AttributedCharacterIterator.Attribute.LANGUAGE;
import static java.text.DateFormat.Field.ERA;
import static java.text.DateFormat.Field.TIME_ZONE;
import static java.text.NumberFormat.Field.INTEGER;


/**
 * AttributedCharacterIterator.Attribute is used like the base enum type and
 * subclassed by unrelated classes.
 */
public final class AttributedCharacterIteratorAttributeTest extends TestCase {
    public void testSerialization() throws IOException, ClassNotFoundException {
        assertSameReserialized(LANGUAGE);
        assertSameReserialized(ERA);
        assertSameReserialized(TIME_ZONE);
        assertSameReserialized(INTEGER);
    }

    public void testSerializingSubclass() throws IOException, ClassNotFoundException {
        AttributedCharacterIterator.Attribute a = new AttributedCharacterIteratorAttributeTest.CustomAttribute();
        try {
            SerializationTester.reserialize(a);
            TestCase.fail();
        } catch (InvalidObjectException expected) {
        }
    }

    private static class CustomAttribute extends AttributedCharacterIterator.Attribute {
        public CustomAttribute() {
            super("a");
        }
    }
}

