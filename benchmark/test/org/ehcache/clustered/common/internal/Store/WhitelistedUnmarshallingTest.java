/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.clustered.common.internal.Store;


import java.io.ObjectStreamClass;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import org.ehcache.clustered.common.internal.store.Util;
import org.ehcache.clustered.common.internal.store.ValueWrapper;
import org.junit.Assert;
import org.junit.Test;


public class WhitelistedUnmarshallingTest {
    @Test
    public void unmarshallingNonWhitelistedClassTest() {
        String className = ObjectStreamClass.lookup(Date.class).getName();
        Date date = new Date();
        byte[] marshalledDate = Util.marshall(date);
        try {
            Object object = Util.unmarshall(ByteBuffer.wrap(marshalledDate), Arrays.asList(Integer.class, Long.class)::contains);
            Assert.fail("Exception was expected to be thrown here");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().equals((("java.io.InvalidClassException: Class deserialization of " + className) + " blocked.")));
        }
    }

    @Test
    public void unmarshallingIntegerTest() throws Exception {
        unmarshallingStateRepoMessagesCheck(new Integer(10));
    }

    @Test
    public void unmarshallingLongTest() throws Exception {
        unmarshallingStateRepoMessagesCheck(new Long(10));
    }

    @Test
    public void unmarshallingFloatTest() throws Exception {
        unmarshallingStateRepoMessagesCheck(new Float(10.0));
    }

    @Test
    public void unmarshallingDoubleTest() throws Exception {
        unmarshallingStateRepoMessagesCheck(new Double(10.0));
    }

    @Test
    public void unmarshallingByteTest() throws Exception {
        byte b = 101;
        unmarshallingStateRepoMessagesCheck(new Byte(b));
    }

    @Test
    public void unmarshallingCharacterTest() throws Exception {
        unmarshallingStateRepoMessagesCheck(new Character('b'));
    }

    @Test
    public void unmarshallingStringTest() throws Exception {
        unmarshallingStateRepoMessagesCheck(new String("John"));
    }

    @Test
    public void unmarshallingBooleanTest() throws Exception {
        unmarshallingStateRepoMessagesCheck(new Boolean(true));
    }

    @Test
    public void unmarshallingShortTest() throws Exception {
        unmarshallingStateRepoMessagesCheck(new Short(((short) (1))));
    }

    @Test
    public void unmarshallingVoidTest() throws Exception {
        Void i = null;
        unmarshallingStateRepoMessagesCheck(i);
    }

    @Test
    public void unmarshallingValueWrapperTest() throws Exception {
        byte[] b = new byte[]{ 101, 100 };
        unmarshallingStateRepoMessagesCheck(new ValueWrapper(b.hashCode(), b));
    }
}

