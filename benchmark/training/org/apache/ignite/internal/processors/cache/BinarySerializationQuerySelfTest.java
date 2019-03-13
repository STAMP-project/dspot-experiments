/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.cache;


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryObjectException;
import org.apache.ignite.binary.BinaryReader;
import org.apache.ignite.binary.BinaryWriter;
import org.apache.ignite.binary.Binarylizable;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Test for query with BinaryMarshaller and different serialization modes.
 */
public class BinarySerializationQuerySelfTest extends GridCommonAbstractTest {
    /**
     * Ignite instance.
     */
    private Ignite ignite;

    /**
     * Cache.
     */
    private IgniteCache<Integer, Object> cache;

    /**
     * Test plain type.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPlain() throws Exception {
        check(BinarySerializationQuerySelfTest.EntityPlain.class);
    }

    /**
     * Test Serializable type.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSerializable() throws Exception {
        check(BinarySerializationQuerySelfTest.EntitySerializable.class);
    }

    /**
     * Test Externalizable type.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testExternalizable() throws Exception {
        check(BinarySerializationQuerySelfTest.EntityExternalizable.class);
    }

    /**
     * Test Binarylizable type.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testBinarylizable() throws Exception {
        check(BinarySerializationQuerySelfTest.EntityBinarylizable.class);
    }

    /**
     * Test type with readObject/writeObject methods.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testWriteReadObject() throws Exception {
        check(BinarySerializationQuerySelfTest.EntityWriteReadObject.class);
    }

    /**
     * Plain entry.
     */
    private static class EntityPlain {
        /**
         * Value.
         */
        public int val;

        /**
         * Default constructor.
         */
        public EntityPlain() {
            // No-op.
        }

        /**
         * Constructor.
         *
         * @param val
         * 		Value.
         */
        public EntityPlain(int val) {
            this.val = val;
        }
    }

    /**
     * Serializable entity.
     */
    private static class EntitySerializable implements Serializable {
        /**
         * Value.
         */
        public int val;

        /**
         * Default constructor.
         */
        public EntitySerializable() {
            // No-op.
        }

        /**
         * Constructor.
         *
         * @param val
         * 		Value.
         */
        public EntitySerializable(int val) {
            this.val = val;
        }
    }

    /**
     * Serializable entity.
     */
    private static class EntityExternalizable implements Externalizable {
        /**
         * Value.
         */
        public int val;

        /**
         * Default constructor.
         */
        public EntityExternalizable() {
            // No-op.
        }

        /**
         * Constructor.
         *
         * @param val
         * 		Value.
         */
        public EntityExternalizable(int val) {
            this.val = val;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(val);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            val = in.readInt();
        }
    }

    /**
     * Serializable entity.
     */
    private static class EntityBinarylizable implements Binarylizable {
        /**
         * Value.
         */
        public int val;

        /**
         * Default constructor.
         */
        public EntityBinarylizable() {
            // No-op.
        }

        /**
         * Constructor.
         *
         * @param val
         * 		Value.
         */
        public EntityBinarylizable(int val) {
            this.val = val;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void writeBinary(BinaryWriter writer) throws BinaryObjectException {
            writer.writeInt("val", val);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void readBinary(BinaryReader reader) throws BinaryObjectException {
            val = reader.readInt("val");
        }
    }

    /**
     * Serializable entity.
     */
    private static class EntityWriteReadObject implements Serializable {
        /**
         * Value.
         */
        public int val;

        /**
         * Default constructor.
         */
        public EntityWriteReadObject() {
            // No-op.
        }

        /**
         * Constructor.
         *
         * @param val
         * 		Value.
         */
        public EntityWriteReadObject(int val) {
            this.val = val;
        }

        /**
         * {@inheritDoc }
         */
        private void writeObject(ObjectOutputStream s) throws IOException {
            s.writeInt(val);
        }

        /**
         * {@inheritDoc }
         */
        private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
            val = s.readInt();
        }
    }
}

