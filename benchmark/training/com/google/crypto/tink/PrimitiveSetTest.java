/**
 * Copyright 2017 Google Inc.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 *
 */
/**
 * //////////////////////////////////////////////////////////////////////////////
 */
package com.google.crypto.tink;


import CryptoFormat.LEGACY_START_BYTE;
import CryptoFormat.TINK_START_BYTE;
import KeyStatusType.DISABLED;
import KeyStatusType.ENABLED;
import OutputPrefixType.LEGACY;
import OutputPrefixType.RAW;
import OutputPrefixType.TINK;
import com.google.crypto.tink.proto.Keyset.Key;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for PrimitiveSet.
 */
@RunWith(JUnit4.class)
public class PrimitiveSetTest {
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static class DummyMac1 implements Mac {
        public DummyMac1() {
        }

        @Override
        public byte[] computeMac(byte[] data) throws GeneralSecurityException {
            return this.getClass().getSimpleName().getBytes(PrimitiveSetTest.UTF_8);
        }

        @Override
        public void verifyMac(byte[] mac, byte[] data) throws GeneralSecurityException {
            return;
        }
    }

    private static class DummyMac2 implements Mac {
        public DummyMac2() {
        }

        @Override
        public byte[] computeMac(byte[] data) throws GeneralSecurityException {
            return this.getClass().getSimpleName().getBytes(PrimitiveSetTest.UTF_8);
        }

        @Override
        public void verifyMac(byte[] mac, byte[] data) throws GeneralSecurityException {
            return;
        }
    }

    @Test
    public void testBasicFunctionality() throws Exception {
        PrimitiveSet<Mac> pset = PrimitiveSet.newPrimitiveSet(Mac.class);
        Key key1 = Key.newBuilder().setKeyId(1).setStatus(ENABLED).setOutputPrefixType(TINK).build();
        pset.addPrimitive(new PrimitiveSetTest.DummyMac1(), key1);
        Key key2 = Key.newBuilder().setKeyId(2).setStatus(ENABLED).setOutputPrefixType(RAW).build();
        pset.setPrimary(pset.addPrimitive(new PrimitiveSetTest.DummyMac2(), key2));
        Key key3 = Key.newBuilder().setKeyId(3).setStatus(DISABLED).setOutputPrefixType(LEGACY).build();
        pset.addPrimitive(new PrimitiveSetTest.DummyMac1(), key3);
        Assert.assertEquals(3, pset.getAll().size());
        List<PrimitiveSet.Entry<Mac>> entries = pset.getPrimitive(key1);
        Assert.assertEquals(1, entries.size());
        PrimitiveSet.Entry<Mac> entry = entries.get(0);
        Assert.assertEquals(PrimitiveSetTest.DummyMac1.class.getSimpleName(), new String(entry.getPrimitive().computeMac(null), "UTF-8"));
        Assert.assertEquals(ENABLED, entry.getStatus());
        Assert.assertEquals(TINK_START_BYTE, entry.getIdentifier()[0]);
        Assert.assertArrayEquals(CryptoFormat.getOutputPrefix(key1), entry.getIdentifier());
        entries = pset.getPrimitive(key2);
        Assert.assertEquals(1, entries.size());
        entry = entries.get(0);
        Assert.assertEquals(PrimitiveSetTest.DummyMac2.class.getSimpleName(), new String(entry.getPrimitive().computeMac(null), "UTF-8"));
        Assert.assertEquals(ENABLED, entry.getStatus());
        Assert.assertEquals(0, entry.getIdentifier().length);
        Assert.assertArrayEquals(CryptoFormat.getOutputPrefix(key2), entry.getIdentifier());
        entries = pset.getPrimitive(key3);
        Assert.assertEquals(1, entries.size());
        entry = entries.get(0);
        Assert.assertEquals(PrimitiveSetTest.DummyMac1.class.getSimpleName(), new String(entry.getPrimitive().computeMac(null), "UTF-8"));
        Assert.assertEquals(DISABLED, entry.getStatus());
        Assert.assertEquals(LEGACY_START_BYTE, entry.getIdentifier()[0]);
        Assert.assertArrayEquals(CryptoFormat.getOutputPrefix(key3), entry.getIdentifier());
        entry = pset.getPrimary();
        Assert.assertEquals(PrimitiveSetTest.DummyMac2.class.getSimpleName(), new String(entry.getPrimitive().computeMac(null), "UTF-8"));
        Assert.assertEquals(ENABLED, entry.getStatus());
        Assert.assertArrayEquals(CryptoFormat.getOutputPrefix(key2), entry.getIdentifier());
    }

    @Test
    public void testDuplicateKeys() throws Exception {
        PrimitiveSet<Mac> pset = PrimitiveSet.newPrimitiveSet(Mac.class);
        Key key1 = Key.newBuilder().setKeyId(1).setStatus(ENABLED).setOutputPrefixType(TINK).build();
        pset.addPrimitive(new PrimitiveSetTest.DummyMac1(), key1);
        Key key2 = Key.newBuilder().setKeyId(1).setStatus(ENABLED).setOutputPrefixType(RAW).build();
        pset.setPrimary(pset.addPrimitive(new PrimitiveSetTest.DummyMac2(), key2));
        Key key3 = Key.newBuilder().setKeyId(2).setStatus(ENABLED).setOutputPrefixType(LEGACY).build();
        pset.addPrimitive(new PrimitiveSetTest.DummyMac1(), key3);
        Key key4 = Key.newBuilder().setKeyId(2).setStatus(ENABLED).setOutputPrefixType(LEGACY).build();
        pset.addPrimitive(new PrimitiveSetTest.DummyMac2(), key4);
        Key key5 = Key.newBuilder().setKeyId(3).setStatus(ENABLED).setOutputPrefixType(RAW).build();
        pset.addPrimitive(new PrimitiveSetTest.DummyMac1(), key5);
        Key key6 = Key.newBuilder().setKeyId(3).setStatus(ENABLED).setOutputPrefixType(RAW).build();
        pset.addPrimitive(new PrimitiveSetTest.DummyMac1(), key6);
        Assert.assertEquals(3, pset.getAll().size());// 3 instead of 6 because of duplicated key ids

        // tink keys
        List<PrimitiveSet.Entry<Mac>> entries = pset.getPrimitive(key1);
        Assert.assertEquals(1, entries.size());
        PrimitiveSet.Entry<Mac> entry = entries.get(0);
        Assert.assertEquals(PrimitiveSetTest.DummyMac1.class.getSimpleName(), new String(entry.getPrimitive().computeMac(null), PrimitiveSetTest.UTF_8));
        Assert.assertEquals(ENABLED, entry.getStatus());
        Assert.assertEquals(TINK_START_BYTE, entry.getIdentifier()[0]);
        Assert.assertArrayEquals(CryptoFormat.getOutputPrefix(key1), entry.getIdentifier());
        // raw keys
        entries = pset.getPrimitive(key2);
        Assert.assertEquals(3, entries.size());
        entry = entries.get(0);
        Assert.assertEquals(PrimitiveSetTest.DummyMac2.class.getSimpleName(), new String(entry.getPrimitive().computeMac(null), PrimitiveSetTest.UTF_8));
        Assert.assertEquals(ENABLED, entry.getStatus());
        Assert.assertEquals(0, entry.getIdentifier().length);
        entry = entries.get(1);
        Assert.assertEquals(PrimitiveSetTest.DummyMac1.class.getSimpleName(), new String(entry.getPrimitive().computeMac(null), PrimitiveSetTest.UTF_8));
        Assert.assertEquals(ENABLED, entry.getStatus());
        Assert.assertEquals(0, entry.getIdentifier().length);
        entry = entries.get(2);
        Assert.assertEquals(PrimitiveSetTest.DummyMac1.class.getSimpleName(), new String(entry.getPrimitive().computeMac(null), PrimitiveSetTest.UTF_8));
        Assert.assertEquals(ENABLED, entry.getStatus());
        Assert.assertEquals(0, entry.getIdentifier().length);
        // legacy keys
        entries = pset.getPrimitive(key3);
        Assert.assertEquals(2, entries.size());
        entry = entries.get(0);
        Assert.assertEquals(PrimitiveSetTest.DummyMac1.class.getSimpleName(), new String(entry.getPrimitive().computeMac(null), PrimitiveSetTest.UTF_8));
        Assert.assertEquals(ENABLED, entry.getStatus());
        Assert.assertArrayEquals(CryptoFormat.getOutputPrefix(key3), entry.getIdentifier());
        entry = entries.get(1);
        Assert.assertEquals(PrimitiveSetTest.DummyMac2.class.getSimpleName(), new String(entry.getPrimitive().computeMac(null), PrimitiveSetTest.UTF_8));
        Assert.assertEquals(ENABLED, entry.getStatus());
        Assert.assertArrayEquals(CryptoFormat.getOutputPrefix(key4), entry.getIdentifier());
        entry = pset.getPrimary();
        Assert.assertEquals(PrimitiveSetTest.DummyMac2.class.getSimpleName(), new String(entry.getPrimitive().computeMac(null), PrimitiveSetTest.UTF_8));
        Assert.assertEquals(ENABLED, entry.getStatus());
        Assert.assertEquals(0, entry.getIdentifier().length);
        Assert.assertArrayEquals(CryptoFormat.getOutputPrefix(key2), entry.getIdentifier());
    }

    @Test
    public void testAddInvalidKey() throws Exception {
        PrimitiveSet<Mac> pset = PrimitiveSet.newPrimitiveSet(Mac.class);
        Key key1 = Key.newBuilder().setKeyId(1).setStatus(ENABLED).build();
        try {
            pset.addPrimitive(new PrimitiveSetTest.DummyMac1(), key1);
            Assert.fail("Expected GeneralSecurityException.");
        } catch (GeneralSecurityException e) {
            TestUtil.assertExceptionContains(e, "unknown output prefix type");
        }
    }
}

