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
package org.apache.beam.runners.dataflow.worker;


import Context.OUTER;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.beam.runners.dataflow.internal.IsmFormat;
import org.apache.beam.runners.dataflow.internal.IsmFormat.Footer;
import org.apache.beam.runners.dataflow.internal.IsmFormat.FooterCoder;
import org.apache.beam.runners.dataflow.internal.IsmFormat.IsmRecord;
import org.apache.beam.runners.dataflow.internal.IsmFormat.IsmRecordCoder;
import org.apache.beam.runners.dataflow.internal.IsmFormat.IsmShard;
import org.apache.beam.runners.dataflow.internal.IsmFormat.IsmShardCoder;
import org.apache.beam.runners.dataflow.internal.IsmFormat.KeyPrefix;
import org.apache.beam.runners.dataflow.internal.IsmFormat.KeyPrefixCoder;
import org.apache.beam.runners.dataflow.internal.IsmFormat.MetadataKeyCoder;
import org.apache.beam.sdk.coders.ByteArrayCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.sdk.testing.CoderPropertiesTest.NonDeterministicCoder;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link IsmFormat}.
 */
@RunWith(JUnit4.class)
public class IsmFormatTest {
    private static final Coder<String> NON_DETERMINISTIC_CODER = new NonDeterministicCoder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testUsingNonDeterministicShardKeyCoder() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("is expected to be deterministic");
        IsmFormat.validateCoderIsCompatible(// number or shard key coders for value records
        // number of shard key coders for metadata records
        IsmRecordCoder.of(1, 0, ImmutableList.<Coder<?>>of(IsmFormatTest.NON_DETERMINISTIC_CODER, ByteArrayCoder.of()), ByteArrayCoder.of()));
    }

    @Test
    public void testUsingNonDeterministicNonShardKeyCoder() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("is expected to be deterministic");
        IsmFormat.validateCoderIsCompatible(// number or shard key coders for value records
        // number of shard key coders for metadata records
        IsmRecordCoder.of(1, 0, ImmutableList.<Coder<?>>of(ByteArrayCoder.of(), IsmFormatTest.NON_DETERMINISTIC_CODER), ByteArrayCoder.of()));
    }

    @Test
    public void testKeyPrefixCoder() throws Exception {
        KeyPrefix keyPrefixA = KeyPrefix.of(5, 7);
        KeyPrefix keyPrefixB = KeyPrefix.of(5, 7);
        CoderProperties.coderDecodeEncodeEqual(KeyPrefixCoder.of(), keyPrefixA);
        CoderProperties.coderDeterministic(KeyPrefixCoder.of(), keyPrefixA, keyPrefixB);
        CoderProperties.coderConsistentWithEquals(KeyPrefixCoder.of(), keyPrefixA, keyPrefixB);
        CoderProperties.coderSerializable(KeyPrefixCoder.of());
        CoderProperties.structuralValueConsistentWithEquals(KeyPrefixCoder.of(), keyPrefixA, keyPrefixB);
        Assert.assertTrue(KeyPrefixCoder.of().isRegisterByteSizeObserverCheap(keyPrefixA));
        Assert.assertEquals(2, KeyPrefixCoder.of().getEncodedElementByteSize(keyPrefixA));
    }

    @Test
    public void testFooterCoder() throws Exception {
        Footer footerA = Footer.of(1, 2, 3);
        Footer footerB = Footer.of(1, 2, 3);
        CoderProperties.coderDecodeEncodeEqual(FooterCoder.of(), footerA);
        CoderProperties.coderDeterministic(FooterCoder.of(), footerA, footerB);
        CoderProperties.coderConsistentWithEquals(FooterCoder.of(), footerA, footerB);
        CoderProperties.coderSerializable(FooterCoder.of());
        CoderProperties.structuralValueConsistentWithEquals(FooterCoder.of(), footerA, footerB);
        Assert.assertTrue(FooterCoder.of().isRegisterByteSizeObserverCheap(footerA));
        Assert.assertEquals(25, FooterCoder.of().getEncodedElementByteSize(footerA));
    }

    @Test
    public void testNormalIsmRecordWithMetadataKeyIsError() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Expected key components to not contain metadata key");
        IsmRecord.of(ImmutableList.of(IsmFormat.getMetadataKey()), "test");
    }

    @Test
    public void testMetadataIsmRecordWithoutMetadataKeyIsError() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Expected key components to contain metadata key");
        IsmRecord.meta(ImmutableList.of("non-metadata key"), "test".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testIsmRecordCoder() throws Exception {
        IsmRecord<String> ismRecordA = IsmRecord.of(ImmutableList.of("0"), "1");
        IsmRecord<String> ismRecordB = IsmRecord.of(ImmutableList.of("0"), "1");
        IsmRecord<String> ismMetaRecordA = IsmRecord.meta(ImmutableList.of(IsmFormat.getMetadataKey()), "2".getBytes(StandardCharsets.UTF_8));
        IsmRecord<String> ismMetaRecordB = IsmRecord.meta(ImmutableList.of(IsmFormat.getMetadataKey()), "2".getBytes(StandardCharsets.UTF_8));
        IsmRecordCoder<String> coder = IsmRecordCoder.of(1, 0, ImmutableList.<Coder<?>>of(StringUtf8Coder.of()), StringUtf8Coder.of());
        IsmRecordCoder<String> coderWithMetadata = IsmRecordCoder.of(1, 1, ImmutableList.<Coder<?>>of(MetadataKeyCoder.of(StringUtf8Coder.of())), StringUtf8Coder.of());
        // Non-metadata records against coder without metadata key support
        CoderProperties.coderDecodeEncodeEqual(coder, ismRecordA);
        CoderProperties.coderDeterministic(coder, ismRecordA, ismRecordB);
        CoderProperties.coderConsistentWithEquals(coder, ismRecordA, ismRecordB);
        CoderProperties.coderSerializable(coder);
        CoderProperties.structuralValueConsistentWithEquals(coder, ismRecordA, ismRecordB);
        // Non-metadata records against coder with metadata key support
        CoderProperties.coderDecodeEncodeEqual(coderWithMetadata, ismRecordA);
        CoderProperties.coderDeterministic(coderWithMetadata, ismRecordA, ismRecordB);
        CoderProperties.coderConsistentWithEquals(coderWithMetadata, ismRecordA, ismRecordB);
        CoderProperties.coderSerializable(coderWithMetadata);
        CoderProperties.structuralValueConsistentWithEquals(coderWithMetadata, ismRecordA, ismRecordB);
        // Metadata records
        CoderProperties.coderDecodeEncodeEqual(coderWithMetadata, ismMetaRecordA);
        CoderProperties.coderDeterministic(coderWithMetadata, ismMetaRecordA, ismMetaRecordB);
        CoderProperties.coderConsistentWithEquals(coderWithMetadata, ismMetaRecordA, ismMetaRecordB);
        CoderProperties.coderSerializable(coderWithMetadata);
        CoderProperties.structuralValueConsistentWithEquals(coderWithMetadata, ismMetaRecordA, ismMetaRecordB);
    }

    @Test
    public void testIsmRecordCoderHashWithinExpectedRanges() throws Exception {
        IsmRecordCoder<String> coder = IsmRecordCoder.of(2, 0, ImmutableList.<Coder<?>>of(StringUtf8Coder.of(), StringUtf8Coder.of()), StringUtf8Coder.of());
        IsmRecordCoder<String> coderWithMetadata = IsmRecordCoder.of(2, 2, ImmutableList.<Coder<?>>of(MetadataKeyCoder.of(StringUtf8Coder.of()), StringUtf8Coder.of()), StringUtf8Coder.of());
        Assert.assertTrue(((coder.hash(ImmutableList.of("A", "B"))) < ((IsmFormat.SHARD_BITS) + 1)));
        int hash = coderWithMetadata.hash(ImmutableList.of(IsmFormat.getMetadataKey(), "B"));
        Assert.assertTrue(((hash > (IsmFormat.SHARD_BITS)) && (hash < (((IsmFormat.SHARD_BITS) + 1) * 2))));
    }

    @Test
    public void testIsmRecordCoderWithTooManyKeysIsError() throws Exception {
        IsmRecordCoder<String> coder = IsmRecordCoder.of(2, 0, ImmutableList.<Coder<?>>of(StringUtf8Coder.of(), StringUtf8Coder.of()), StringUtf8Coder.of());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Expected at most");
        coder.hash(ImmutableList.of("A", "B", "C"));
    }

    @Test
    public void testIsmRecordCoderHashWithoutEnoughKeysIsError() throws Exception {
        IsmRecordCoder<String> coder = IsmRecordCoder.of(2, 0, ImmutableList.<Coder<?>>of(StringUtf8Coder.of(), StringUtf8Coder.of()), StringUtf8Coder.of());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Expected at least");
        coder.hash(ImmutableList.of("A"));
    }

    @Test
    public void testIsmRecordCoderMetadataHashWithoutEnoughKeysIsError() throws Exception {
        IsmRecordCoder<String> coderWithMetadata = IsmRecordCoder.of(2, 2, ImmutableList.<Coder<?>>of(MetadataKeyCoder.of(StringUtf8Coder.of()), StringUtf8Coder.of()), StringUtf8Coder.of());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Expected at least");
        coderWithMetadata.hash(ImmutableList.of(IsmFormat.getMetadataKey()));
    }

    @Test
    public void testIsmRecordCoderKeyCoderCountMismatch() throws Exception {
        IsmRecord<String> ismRecord = IsmRecord.of(ImmutableList.of("0", "too many"), "1");
        IsmRecordCoder<String> coder = IsmRecordCoder.of(1, 0, ImmutableList.<Coder<?>>of(StringUtf8Coder.of()), StringUtf8Coder.of());
        expectedException.expect(CoderException.class);
        expectedException.expectMessage("Expected 1 key component(s) but received key");
        coder.encode(ismRecord, new ByteArrayOutputStream());
    }

    @Test
    public void testIsmRecordToStringEqualsAndHashCode() {
        IsmRecord<String> ismRecordA = IsmRecord.of(ImmutableList.of("0"), "1");
        IsmRecord<String> ismRecordB = IsmRecord.of(ImmutableList.of("0"), "1");
        IsmRecord<String> ismRecordC = IsmRecord.of(ImmutableList.of("3"), "4");
        IsmRecord<String> ismRecordAWithMeta = IsmRecord.meta(ImmutableList.of(IsmFormat.getMetadataKey(), "0"), "2".getBytes(StandardCharsets.UTF_8));
        IsmRecord<String> ismRecordBWithMeta = IsmRecord.meta(ImmutableList.of(IsmFormat.getMetadataKey(), "0"), "2".getBytes(StandardCharsets.UTF_8));
        IsmRecord<String> ismRecordCWithMeta = IsmRecord.meta(ImmutableList.of(IsmFormat.getMetadataKey(), "0"), "5".getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals(ismRecordA, ismRecordB);
        Assert.assertEquals(ismRecordAWithMeta, ismRecordBWithMeta);
        Assert.assertNotEquals(ismRecordA, ismRecordAWithMeta);
        Assert.assertNotEquals(ismRecordA, ismRecordC);
        Assert.assertNotEquals(ismRecordAWithMeta, ismRecordCWithMeta);
        Assert.assertEquals(ismRecordA.hashCode(), ismRecordB.hashCode());
        Assert.assertEquals(ismRecordAWithMeta.hashCode(), ismRecordBWithMeta.hashCode());
        Assert.assertNotEquals(ismRecordA.hashCode(), ismRecordAWithMeta.hashCode());
        Assert.assertNotEquals(ismRecordA.hashCode(), ismRecordC.hashCode());
        Assert.assertNotEquals(ismRecordAWithMeta.hashCode(), ismRecordCWithMeta.hashCode());
        Assert.assertThat(ismRecordA.toString(), Matchers.allOf(Matchers.containsString("keyComponents=[0]"), Matchers.containsString("value=1")));
        Assert.assertThat(ismRecordAWithMeta.toString(), Matchers.allOf(Matchers.containsString("keyComponents=[META, 0]"), Matchers.containsString("metadata=")));
    }

    @Test
    public void testIsmShardCoder() throws Exception {
        IsmShard shardA = IsmShard.of(1, 2, 3);
        IsmShard shardB = IsmShard.of(1, 2, 3);
        CoderProperties.coderDecodeEncodeEqual(IsmShardCoder.of(), shardA);
        CoderProperties.coderDeterministic(IsmShardCoder.of(), shardA, shardB);
        CoderProperties.coderConsistentWithEquals(IsmShardCoder.of(), shardA, shardB);
        CoderProperties.coderSerializable(IsmShardCoder.of());
        CoderProperties.structuralValueConsistentWithEquals(IsmShardCoder.of(), shardA, shardB);
    }

    @Test
    public void testIsmShardToStringEqualsAndHashCode() {
        IsmShard shardA = IsmShard.of(1, 2, 3);
        IsmShard shardB = IsmShard.of(1, 2, 3);
        IsmShard shardC = IsmShard.of(4, 5, 6);
        Assert.assertEquals(shardA, shardB);
        Assert.assertNotEquals(shardA, shardC);
        Assert.assertEquals(shardA.hashCode(), shardB.hashCode());
        Assert.assertNotEquals(shardA.hashCode(), shardC.hashCode());
        Assert.assertThat(shardA.toString(), Matchers.allOf(Matchers.containsString("id=1"), Matchers.containsString("blockOffset=2"), Matchers.containsString("indexOffset=3")));
    }

    @Test
    public void testUnknownVersion() throws Exception {
        byte[] data = new byte[25];
        data[24] = 5;// unknown version

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        expectedException.expect(IOException.class);
        expectedException.expectMessage("Unknown version 5");
        FooterCoder.of().decode(bais, OUTER);
    }
}

