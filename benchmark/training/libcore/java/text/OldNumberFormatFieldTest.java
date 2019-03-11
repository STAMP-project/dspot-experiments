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
import java.text.NumberFormat;
import junit.framework.TestCase;

import static java.text.NumberFormat.Field.CURRENCY;


public class OldNumberFormatFieldTest extends TestCase {
    public void test_ConstructorLjava_lang_String() {
        // protected constructor
        String name = "new number format";
        OldNumberFormatFieldTest.MyNumberFormat field = new OldNumberFormatFieldTest.MyNumberFormat(name);
        TestCase.assertEquals("field has wrong name", name, field.getName());
        field = new OldNumberFormatFieldTest.MyNumberFormat(null);
        TestCase.assertEquals("field has wrong name", null, field.getName());
    }

    public void test_readResolve() throws IOException, ClassNotFoundException {
        // test for method java.lang.Object readResolve()
        // see serialization stress tests:
        // implemented in
        // SerializationStressTest4.test_writeObject_NumberFormat_Field()
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        OldNumberFormatFieldTest.MyNumberFormat field;
        NumberFormat.Field nfield = CURRENCY;
        field = new OldNumberFormatFieldTest.MyNumberFormat(null);
        out.writeObject(nfield);
        out.writeObject(field);
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        NumberFormat.Field nfield2 = ((NumberFormat.Field) (in.readObject()));
        TestCase.assertSame("resolved incorrectly", nfield, nfield2);
        try {
            in.readObject();
            TestCase.fail("Expected InvalidObjectException for subclass instance with null name");
        } catch (InvalidObjectException e) {
        }
        in.close();
        out.close();
    }

    static class MyNumberFormat extends NumberFormat.Field {
        static final long serialVersionUID = 1L;

        static boolean flag = false;

        protected MyNumberFormat(String attr) {
            super(attr);
        }

        protected String getName() {
            return super.getName();
        }
    }
}

