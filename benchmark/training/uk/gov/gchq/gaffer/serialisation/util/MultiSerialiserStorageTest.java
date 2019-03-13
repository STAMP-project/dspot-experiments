/**
 * Copyright 2018-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.serialisation.util;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.serialisation.IntegerSerialiser;
import uk.gov.gchq.gaffer.serialisation.ToBytesSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.MultiSerialiserStorage;
import uk.gov.gchq.gaffer.serialisation.implementation.raw.RawIntegerSerialiser;


public class MultiSerialiserStorageTest {
    public static final byte BYTE = ((byte) (0));

    public static final ToBytesSerialiser SERIALISER_CLASS = new IntegerSerialiser();

    public static final int VALUE = 1;

    public static final ToBytesSerialiser SERIALISER_CLASS2 = new RawIntegerSerialiser();

    public static final Class SUPPORTED_CLASS = Integer.class;

    private MultiSerialiserStorage mss;

    @Test
    public void shouldPutAndGet() throws Exception {
        // when
        mss.put(MultiSerialiserStorageTest.BYTE, MultiSerialiserStorageTest.SERIALISER_CLASS, MultiSerialiserStorageTest.SUPPORTED_CLASS);
        // then
        checkBasicPut();
    }

    @Test
    public void shouldNotRetainOldSerialiserWhenKeyIsOverWritten() throws Exception {
        // when
        mss.put(MultiSerialiserStorageTest.BYTE, MultiSerialiserStorageTest.SERIALISER_CLASS, MultiSerialiserStorageTest.SUPPORTED_CLASS);
        mss.put(MultiSerialiserStorageTest.BYTE, MultiSerialiserStorageTest.SERIALISER_CLASS2, MultiSerialiserStorageTest.SUPPORTED_CLASS);
        // then
        Assert.assertNotNull(mss.getKeyFromValue(MultiSerialiserStorageTest.VALUE));
        Assert.assertEquals("Wrong key for value", ((Object) (MultiSerialiserStorageTest.BYTE)), mss.getKeyFromValue(MultiSerialiserStorageTest.VALUE));
        ToBytesSerialiser actualClassFromByte = mss.getSerialiserFromKey(MultiSerialiserStorageTest.BYTE);
        Assert.assertNotNull("Byte key not found", actualClassFromByte);
        Assert.assertEquals("Wrong new SerialiserClass returned for key", MultiSerialiserStorageTest.SERIALISER_CLASS2, actualClassFromByte);
        ToBytesSerialiser actualClassFromValue = mss.getSerialiserFromValue(Integer.MAX_VALUE);
        Assert.assertNotNull("Value class not found", actualClassFromValue);
        Assert.assertEquals("Wrong new SerialiserClass returned for value class", MultiSerialiserStorageTest.SERIALISER_CLASS2, actualClassFromValue);
    }

    @Test
    public void shouldUpdateToNewerValueToSerialiser() throws Exception {
        // give
        byte serialiserEncoding = (MultiSerialiserStorageTest.BYTE) + 1;
        // when
        mss.put(serialiserEncoding, MultiSerialiserStorageTest.SERIALISER_CLASS2, MultiSerialiserStorageTest.SUPPORTED_CLASS);
        mss.put(MultiSerialiserStorageTest.BYTE, MultiSerialiserStorageTest.SERIALISER_CLASS, MultiSerialiserStorageTest.SUPPORTED_CLASS);
        // then
        checkBasicPut();
        Assert.assertEquals(MultiSerialiserStorageTest.BYTE, ((byte) (mss.getKeyFromValue(MultiSerialiserStorageTest.VALUE))));
        ToBytesSerialiser actualClassFromByte2 = mss.getSerialiserFromKey(serialiserEncoding);
        Assert.assertNotNull("Byte key not found", actualClassFromByte2);
        Assert.assertEquals("Wrong SerialiserClass returned for key", MultiSerialiserStorageTest.SERIALISER_CLASS2, actualClassFromByte2);
        ToBytesSerialiser actualClassFromValue2 = mss.getSerialiserFromValue(Integer.MAX_VALUE);
        Assert.assertNotNull("Value class not found", actualClassFromValue2);
        Assert.assertEquals("Wrong SerialiserClass, should have updated to newer SerialiserClass", MultiSerialiserStorageTest.SERIALISER_CLASS, actualClassFromValue2);
    }
}

