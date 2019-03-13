/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.io;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Random;
import org.junit.Test;


/**
 * Unit tests for VersionedWritable.
 */
public class TestVersionedWritable {
    /**
     * Example class used in test cases below.
     */
    public static class SimpleVersionedWritable extends VersionedWritable {
        private static final Random RANDOM = new Random();

        int state = TestVersionedWritable.SimpleVersionedWritable.RANDOM.nextInt();

        private static byte VERSION = 1;

        @Override
        public byte getVersion() {
            return TestVersionedWritable.SimpleVersionedWritable.VERSION;
        }

        @Override
        public void write(DataOutput out) throws IOException {
            super.write(out);// version.

            out.writeInt(state);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            super.readFields(in);// version

            this.state = in.readInt();
        }

        public static TestVersionedWritable.SimpleVersionedWritable read(DataInput in) throws IOException {
            TestVersionedWritable.SimpleVersionedWritable result = new TestVersionedWritable.SimpleVersionedWritable();
            result.readFields(in);
            return result;
        }

        /**
         * Required by test code, below.
         */
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestVersionedWritable.SimpleVersionedWritable))
                return false;

            TestVersionedWritable.SimpleVersionedWritable other = ((TestVersionedWritable.SimpleVersionedWritable) (o));
            return (this.state) == (other.state);
        }
    }

    public static class AdvancedVersionedWritable extends TestVersionedWritable.SimpleVersionedWritable {
        String shortTestString = "Now is the time for all good men to come to the aid of the Party";

        String longTestString = "Four score and twenty years ago. Blah. Blah. Blah. Blah. Blah. Blah. Blah. Blah.";

        String compressableTestString = "Blah. Blah. Blah. Blah. Blah. Blah. Blah. Blah. Blah. Blah. Blah. Blah. " + ("Blah. Blah. Blah. Blah. Blah. Blah. Blah. Blah. Blah. Blah. Blah. Blah. " + "Blah. Blah. Blah. Blah. Blah. Blah. Blah. Blah. Blah. Blah. Blah. Blah. ");

        TestVersionedWritable.SimpleVersionedWritable containedObject = new TestVersionedWritable.SimpleVersionedWritable();

        String[] testStringArray = new String[]{ "The", "Quick", "Brown", "Fox", "Jumped", "Over", "The", "Lazy", "Dog" };

        @Override
        public void write(DataOutput out) throws IOException {
            super.write(out);
            out.writeUTF(shortTestString);
            WritableUtils.writeString(out, longTestString);
            int comp = WritableUtils.writeCompressedString(out, compressableTestString);
            System.out.println((("Compression is " + comp) + "%"));
            containedObject.write(out);// Warning if this is a recursive call, you need a null value.

            WritableUtils.writeStringArray(out, testStringArray);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            super.readFields(in);
            shortTestString = in.readUTF();
            longTestString = WritableUtils.readString(in);
            compressableTestString = WritableUtils.readCompressedString(in);
            containedObject.readFields(in);// Warning if this is a recursive call, you need a null value.

            testStringArray = WritableUtils.readStringArray(in);
        }

        @Override
        public boolean equals(Object o) {
            super.equals(o);
            if (!(shortTestString.equals(((TestVersionedWritable.AdvancedVersionedWritable) (o)).shortTestString))) {
                return false;
            }
            if (!(longTestString.equals(((TestVersionedWritable.AdvancedVersionedWritable) (o)).longTestString))) {
                return false;
            }
            if (!(compressableTestString.equals(((TestVersionedWritable.AdvancedVersionedWritable) (o)).compressableTestString))) {
                return false;
            }
            if ((testStringArray.length) != (((TestVersionedWritable.AdvancedVersionedWritable) (o)).testStringArray.length)) {
                return false;
            }
            for (int i = 0; i < (testStringArray.length); i++) {
                if (!(testStringArray[i].equals(((TestVersionedWritable.AdvancedVersionedWritable) (o)).testStringArray[i]))) {
                    return false;
                }
            }
            if (!(containedObject.equals(((TestVersionedWritable.AdvancedVersionedWritable) (o)).containedObject))) {
                return false;
            }
            return true;
        }
    }

    /* This one checks that version mismatch is thrown... */
    public static class SimpleVersionedWritableV2 extends TestVersionedWritable.SimpleVersionedWritable {
        static byte VERSION = 2;

        @Override
        public byte getVersion() {
            return TestVersionedWritable.SimpleVersionedWritableV2.VERSION;
        }
    }

    /**
     * Test 1: Check that SimpleVersionedWritable.
     */
    @Test
    public void testSimpleVersionedWritable() throws Exception {
        TestWritable.testWritable(new TestVersionedWritable.SimpleVersionedWritable());
    }

    /**
     * Test 2: Check that AdvancedVersionedWritable Works (well, why wouldn't it!).
     */
    @Test
    public void testAdvancedVersionedWritable() throws Exception {
        TestWritable.testWritable(new TestVersionedWritable.AdvancedVersionedWritable());
    }

    /**
     * Test 3: Check that SimpleVersionedWritable throws an Exception.
     */
    @Test
    public void testSimpleVersionedWritableMismatch() throws Exception {
        TestVersionedWritable.testVersionedWritable(new TestVersionedWritable.SimpleVersionedWritable(), new TestVersionedWritable.SimpleVersionedWritableV2());
    }
}

