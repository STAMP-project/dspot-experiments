/**
 * Copyright 2014 The gRPC Authors
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
package io.grpc;


import Metadata.ASCII_STRING_MARSHALLER;
import Metadata.Key;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import io.grpc.internal.GrpcUtil;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link Metadata}.
 */
@RunWith(JUnit4.class)
public class MetadataTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private static final Metadata.BinaryMarshaller<MetadataTest.Fish> FISH_MARSHALLER = new Metadata.BinaryMarshaller<MetadataTest.Fish>() {
        @Override
        public byte[] toBytes(io.grpc.Fish fish) {
            return fish.name.getBytes(Charsets.UTF_8);
        }

        @Override
        public io.grpc.Fish parseBytes(byte[] serialized) {
            return new io.grpc.Fish(new String(serialized, Charsets.UTF_8));
        }
    };

    private static final String LANCE = "lance";

    private static final byte[] LANCE_BYTES = MetadataTest.LANCE.getBytes(Charsets.US_ASCII);

    private static final Metadata.Key<MetadataTest.Fish> KEY = Key.of("test-bin", io.grpc.FISH_MARSHALLER);

    @Test
    public void noPseudoHeaders() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid character");
        Key.of(":test-bin", io.grpc.FISH_MARSHALLER);
    }

    @Test
    public void testMutations() {
        MetadataTest.Fish lance = new MetadataTest.Fish(MetadataTest.LANCE);
        MetadataTest.Fish cat = new MetadataTest.Fish("cat");
        Metadata metadata = new Metadata();
        Assert.assertNull(metadata.get(io.grpc.KEY));
        metadata.put(io.grpc.KEY, lance);
        Assert.assertEquals(Arrays.asList(lance), Lists.newArrayList(metadata.getAll(io.grpc.KEY)));
        Assert.assertEquals(lance, metadata.get(io.grpc.KEY));
        metadata.put(io.grpc.KEY, lance);
        Assert.assertEquals(Arrays.asList(lance, lance), Lists.newArrayList(metadata.getAll(io.grpc.KEY)));
        Assert.assertTrue(metadata.remove(io.grpc.KEY, lance));
        Assert.assertEquals(Arrays.asList(lance), Lists.newArrayList(metadata.getAll(io.grpc.KEY)));
        Assert.assertFalse(metadata.remove(io.grpc.KEY, cat));
        metadata.put(io.grpc.KEY, cat);
        Assert.assertEquals(cat, metadata.get(io.grpc.KEY));
        metadata.put(io.grpc.KEY, lance);
        Assert.assertEquals(Arrays.asList(lance, cat, lance), Lists.newArrayList(metadata.getAll(io.grpc.KEY)));
        Assert.assertEquals(lance, metadata.get(io.grpc.KEY));
        Assert.assertTrue(metadata.remove(io.grpc.KEY, lance));
        Assert.assertEquals(Arrays.asList(cat, lance), Lists.newArrayList(metadata.getAll(io.grpc.KEY)));
        metadata.put(io.grpc.KEY, lance);
        Assert.assertTrue(metadata.remove(io.grpc.KEY, cat));
        Assert.assertEquals(Arrays.asList(lance, lance), Lists.newArrayList(metadata.getAll(io.grpc.KEY)));
        Assert.assertEquals(Arrays.asList(lance, lance), Lists.newArrayList(metadata.removeAll(io.grpc.KEY)));
        Assert.assertNull(metadata.getAll(io.grpc.KEY));
        Assert.assertNull(metadata.get(io.grpc.KEY));
    }

    @Test
    public void discardAll() {
        MetadataTest.Fish lance = new MetadataTest.Fish(MetadataTest.LANCE);
        Metadata metadata = new Metadata();
        metadata.put(io.grpc.KEY, lance);
        metadata.discardAll(io.grpc.KEY);
        Assert.assertNull(metadata.getAll(io.grpc.KEY));
        Assert.assertNull(metadata.get(io.grpc.KEY));
    }

    @Test
    public void discardAll_empty() {
        Metadata metadata = new Metadata();
        metadata.discardAll(io.grpc.KEY);
        Assert.assertNull(metadata.getAll(io.grpc.KEY));
        Assert.assertNull(metadata.get(io.grpc.KEY));
    }

    @Test
    public void testGetAllNoRemove() {
        MetadataTest.Fish lance = new MetadataTest.Fish(MetadataTest.LANCE);
        Metadata metadata = new Metadata();
        metadata.put(io.grpc.KEY, lance);
        Iterator<MetadataTest.Fish> i = metadata.getAll(io.grpc.KEY).iterator();
        Assert.assertEquals(lance, i.next());
        thrown.expect(UnsupportedOperationException.class);
        i.remove();
    }

    @Test
    public void testWriteParsed() {
        MetadataTest.Fish lance = new MetadataTest.Fish(MetadataTest.LANCE);
        Metadata metadata = new Metadata();
        metadata.put(io.grpc.KEY, lance);
        Assert.assertEquals(lance, metadata.get(io.grpc.KEY));
        Iterator<MetadataTest.Fish> fishes = metadata.getAll(io.grpc.KEY).iterator();
        Assert.assertTrue(fishes.hasNext());
        Assert.assertEquals(fishes.next(), lance);
        Assert.assertFalse(fishes.hasNext());
        byte[][] serialized = metadata.serialize();
        Assert.assertEquals(2, serialized.length);
        Assert.assertEquals("test-bin", new String(serialized[0], Charsets.US_ASCII));
        Assert.assertArrayEquals(MetadataTest.LANCE_BYTES, serialized[1]);
        Assert.assertEquals(lance, metadata.get(io.grpc.KEY));
        Assert.assertEquals(serialized[0], metadata.serialize()[0]);
        Assert.assertEquals(serialized[1], metadata.serialize()[1]);
    }

    @Test
    public void testWriteRaw() {
        Metadata raw = new Metadata(io.grpc.KEY.asciiName(), MetadataTest.LANCE_BYTES);
        MetadataTest.Fish lance = raw.get(io.grpc.KEY);
        Assert.assertEquals(lance, new MetadataTest.Fish(MetadataTest.LANCE));
        // Reading again should return the same parsed instance
        Assert.assertEquals(lance, raw.get(io.grpc.KEY));
    }

    @Test
    public void testSerializeRaw() {
        Metadata raw = new Metadata(io.grpc.KEY.asciiName(), MetadataTest.LANCE_BYTES);
        byte[][] serialized = raw.serialize();
        Assert.assertArrayEquals(serialized[0], io.grpc.KEY.asciiName());
        Assert.assertArrayEquals(serialized[1], MetadataTest.LANCE_BYTES);
    }

    @Test
    public void testMergeByteConstructed() {
        Metadata raw = new Metadata(io.grpc.KEY.asciiName(), MetadataTest.LANCE_BYTES);
        Metadata serializable = new Metadata();
        serializable.merge(raw);
        byte[][] serialized = serializable.serialize();
        Assert.assertArrayEquals(serialized[0], io.grpc.KEY.asciiName());
        Assert.assertArrayEquals(serialized[1], MetadataTest.LANCE_BYTES);
        Assert.assertEquals(new MetadataTest.Fish(MetadataTest.LANCE), serializable.get(io.grpc.KEY));
    }

    @Test
    public void headerMergeShouldCopyValues() {
        MetadataTest.Fish lance = new MetadataTest.Fish(MetadataTest.LANCE);
        Metadata h1 = new Metadata();
        Metadata h2 = new Metadata();
        h2.put(io.grpc.KEY, lance);
        h1.merge(h2);
        Iterator<MetadataTest.Fish> fishes = h1.getAll(io.grpc.KEY).iterator();
        Assert.assertTrue(fishes.hasNext());
        Assert.assertEquals(fishes.next(), lance);
        Assert.assertFalse(fishes.hasNext());
    }

    @Test
    public void mergeExpands() {
        MetadataTest.Fish lance = new MetadataTest.Fish(MetadataTest.LANCE);
        Metadata h1 = new Metadata();
        h1.put(io.grpc.KEY, lance);
        Metadata h2 = new Metadata();
        h2.put(io.grpc.KEY, lance);
        h2.put(io.grpc.KEY, lance);
        h2.put(io.grpc.KEY, lance);
        h2.put(io.grpc.KEY, lance);
        h1.merge(h2);
    }

    @Test
    public void shortBinaryKeyName() {
        thrown.expect(IllegalArgumentException.class);
        Key.of("-bin", io.grpc.FISH_MARSHALLER);
    }

    @Test
    public void invalidSuffixBinaryKeyName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Binary header is named");
        Key.of("nonbinary", io.grpc.FISH_MARSHALLER);
    }

    @Test
    public void verifyToString() {
        Metadata h = new Metadata();
        h.put(io.grpc.KEY, new MetadataTest.Fish("binary"));
        h.put(Key.of("test", ASCII_STRING_MARSHALLER), "ascii");
        Assert.assertEquals("Metadata(test-bin=YmluYXJ5,test=ascii)", h.toString());
        Metadata t = new Metadata();
        t.put(Key.of("test", ASCII_STRING_MARSHALLER), "ascii");
        Assert.assertEquals("Metadata(test=ascii)", t.toString());
        t = new Metadata("test".getBytes(Charsets.US_ASCII), "ascii".getBytes(Charsets.US_ASCII), "test-bin".getBytes(Charsets.US_ASCII), "binary".getBytes(Charsets.US_ASCII));
        Assert.assertEquals("Metadata(test=ascii,test-bin=YmluYXJ5)", t.toString());
    }

    @Test
    public void verifyToString_usingBinary() {
        Metadata h = new Metadata();
        h.put(io.grpc.KEY, new MetadataTest.Fish("binary"));
        h.put(Key.of("test", ASCII_STRING_MARSHALLER), "ascii");
        Assert.assertEquals("Metadata(test-bin=YmluYXJ5,test=ascii)", h.toString());
        Metadata t = new Metadata();
        t.put(Key.of("test", ASCII_STRING_MARSHALLER), "ascii");
        Assert.assertEquals("Metadata(test=ascii)", t.toString());
    }

    @Test
    public void testKeyCaseHandling() {
        Locale originalLocale = Locale.getDefault();
        Locale.setDefault(new Locale("tr", "TR"));
        try {
            // In Turkish, both I and i (which are in ASCII) change into non-ASCII characters when their
            // case is changed as ? and ?, respectively.
            Assert.assertEquals("?", "i".toUpperCase());
            Assert.assertEquals("?", "I".toLowerCase());
            Metadata.Key<String> keyTitleCase = Key.of("If-Modified-Since", ASCII_STRING_MARSHALLER);
            Metadata.Key<String> keyLowerCase = Key.of("if-modified-since", ASCII_STRING_MARSHALLER);
            Metadata.Key<String> keyUpperCase = Key.of("IF-MODIFIED-SINCE", ASCII_STRING_MARSHALLER);
            Metadata metadata = new Metadata();
            metadata.put(keyTitleCase, "plain string");
            Assert.assertEquals("plain string", metadata.get(keyTitleCase));
            Assert.assertEquals("plain string", metadata.get(keyLowerCase));
            Assert.assertEquals("plain string", metadata.get(keyUpperCase));
            byte[][] bytes = metadata.serialize();
            Assert.assertEquals(2, bytes.length);
            Assert.assertArrayEquals("if-modified-since".getBytes(Charsets.US_ASCII), bytes[0]);
            Assert.assertArrayEquals("plain string".getBytes(Charsets.US_ASCII), bytes[1]);
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void removeIgnoresMissingValue() {
        Metadata m = new Metadata();
        // Any key will work.
        io.grpc.Metadata.Key<String> key = GrpcUtil.USER_AGENT_KEY;
        boolean success = m.remove(key, "agent");
        Assert.assertFalse(success);
    }

    @Test
    public void removeAllIgnoresMissingValue() {
        Metadata m = new Metadata();
        // Any key will work.
        io.grpc.Metadata.Key<String> key = GrpcUtil.USER_AGENT_KEY;
        Iterable<String> removed = m.removeAll(key);
        Assert.assertNull(removed);
    }

    @Test
    public void keyEqualsHashNameWorks() {
        io.grpc.Metadata.Key<?> k1 = io.grpc.Metadata.Key.of("case", ASCII_STRING_MARSHALLER);
        io.grpc.Metadata.Key<?> k2 = io.grpc.Metadata.Key.of("CASE", ASCII_STRING_MARSHALLER);
        Assert.assertEquals(k1, k1);
        Assert.assertNotEquals(k1, null);
        Assert.assertNotEquals(k1, new Object() {});
        Assert.assertEquals(k1, k2);
        Assert.assertEquals(k1.hashCode(), k2.hashCode());
        // Check that the casing is preserved.
        Assert.assertEquals("CASE", k2.originalName());
        Assert.assertEquals("case", k2.name());
    }

    @Test
    public void invalidKeyName() {
        try {
            io.grpc.Metadata.Key.of("io.grpc/key1", ASCII_STRING_MARSHALLER);
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Invalid character '/' in key name 'io.grpc/key1'", e.getMessage());
        }
    }

    private static final class Fish {
        private String name;

        private Fish(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            MetadataTest.Fish fish = ((MetadataTest.Fish) (o));
            if ((name) != null ? !(name.equals(fish.name)) : (fish.name) != null) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return ("Fish(" + (name)) + ")";
        }
    }
}

