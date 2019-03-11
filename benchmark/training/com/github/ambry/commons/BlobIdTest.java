/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.commons;


import MockClusterMap.DEFAULT_PARTITION_CLASS;
import TestUtils.RANDOM;
import com.github.ambry.clustermap.ClusterMap;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.clustermap.PartitionId;
import com.github.ambry.utils.Pair;
import com.github.ambry.utils.TestUtils;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static BlobDataType.DATACHUNK;
import static BlobIdType.CRAFTED;
import static BlobIdType.NATIVE;


/**
 * Unit tests for {@link BlobId}.
 */
@RunWith(Parameterized.class)
public class BlobIdTest {
    private static final Random random = new Random();

    private final short version;

    private final BlobIdType referenceType;

    private final byte referenceDatacenterId;

    private final short referenceAccountId;

    private final short referenceContainerId;

    private final ClusterMap referenceClusterMap;

    private final PartitionId referencePartitionId;

    private final boolean referenceIsEncrypted;

    private final BlobDataType referenceDataType;

    /**
     * Constructor with parameter to be set.
     *
     * @param version
     * 		The version for BlobId to test.
     */
    public BlobIdTest(short version) throws Exception {
        this.version = version;
        byte[] bytes = new byte[2];
        referenceClusterMap = new MockClusterMap();
        BlobIdTest.random.nextBytes(bytes);
        referenceType = (BlobIdTest.random.nextBoolean()) ? NATIVE : CRAFTED;
        BlobIdTest.random.nextBytes(bytes);
        referenceDatacenterId = bytes[0];
        referenceAccountId = getRandomShort(BlobIdTest.random);
        referenceContainerId = getRandomShort(BlobIdTest.random);
        referencePartitionId = referenceClusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(0);
        referenceIsEncrypted = BlobIdTest.random.nextBoolean();
        referenceDataType = BlobDataType.values()[BlobIdTest.random.nextInt(BlobDataType.values().length)];
    }

    /**
     * Tests blobId construction and assert that values are as expected.
     */
    @Test
    public void testBuildBlobId() throws Exception {
        BlobId.BlobId blobId = new BlobId.BlobId(version, referenceType, referenceDatacenterId, referenceAccountId, referenceContainerId, referencePartitionId, referenceIsEncrypted, referenceDataType);
        Assert.assertEquals("Wrong blobId version", version, getVersionFromBlobString(blobId.getID()));
        assertBlobIdFieldValues(version, blobId, referenceType, referenceDatacenterId, referenceAccountId, referenceContainerId, referencePartitionId, referenceIsEncrypted, referenceDataType);
    }

    /**
     * Tests first serializing a blobId into string, and then deserializing into a blobId object from the string.
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void testSerDes() throws Exception {
        BlobId.BlobId blobId = new BlobId.BlobId(version, referenceType, referenceDatacenterId, referenceAccountId, referenceContainerId, referencePartitionId, referenceIsEncrypted, referenceDataType);
        deserializeBlobIdAndAssert(version, blobId.getID());
        BlobId.BlobId blobIdSerDed = new BlobId.BlobId(new DataInputStream(new ByteArrayInputStream(blobId.toBytes())), referenceClusterMap);
        // Ensure that deserialized blob is exactly the same as the original in comparisons.
        Assert.assertTrue(blobId.equals(blobIdSerDed));
        Assert.assertTrue(blobIdSerDed.equals(blobId));
        Assert.assertEquals(blobId.hashCode(), blobIdSerDed.hashCode());
        Assert.assertEquals(0, blobId.compareTo(blobIdSerDed));
        Assert.assertEquals(0, blobIdSerDed.compareTo(blobId));
    }

    /**
     * Test that the BlobId flag is honored by V3 and above.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBlobIdFlag() throws Exception {
        boolean[] isEncryptedValues = new boolean[]{ true, false };
        if ((version) >= (BLOB_ID_V3)) {
            for (BlobIdType type : BlobIdType.values()) {
                for (boolean isEncrypted : isEncryptedValues) {
                    BlobId.BlobId blobId = new BlobId.BlobId(version, type, referenceDatacenterId, referenceAccountId, referenceContainerId, referencePartitionId, isEncrypted, referenceDataType);
                    BlobId.BlobId blobIdSerDed = new BlobId.BlobId(new DataInputStream(new ByteArrayInputStream(blobId.toBytes())), referenceClusterMap);
                    Assert.assertEquals("The type should match the original's type", type, blobIdSerDed.getType());
                    Assert.assertEquals("The isEncrypted should match the original", (((version) != (BLOB_ID_V3)) && isEncrypted), BlobId.BlobId.isEncrypted(blobId.getID()));
                }
            }
        }
        if ((version) >= (BLOB_ID_V5)) {
            for (BlobDataType dataType : BlobDataType.values()) {
                BlobId.BlobId blobId = new BlobId.BlobId(version, NATIVE, referenceDatacenterId, referenceAccountId, referenceContainerId, referencePartitionId, false, dataType);
                BlobId.BlobId blobIdSerDed = new BlobId.BlobId(new DataInputStream(new ByteArrayInputStream(blobId.toBytes())), referenceClusterMap);
                Assert.assertEquals("The data type should match the original's", dataType, blobIdSerDed.getBlobDataType());
            }
        }
    }

    /**
     * Test {@link BlobId#isEncrypted(String)}.
     * BLOB_ID_V1 and BLOB_ID_V2 encrypted bit should always be {@code false);
     * BLOB_ID_V3 encrypted bit can be {@code true}} if BlobIdString has encrypted bit;
     * BLOB_ID_V4 encrypted bit is based on {@link BlobId} only.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBlobIdIsEncrypted() throws Exception {
        for (boolean isEncrypted : TestUtils.BOOLEAN_VALUES) {
            BlobId.BlobId blobId = new BlobId.BlobId(version, (BlobIdTest.random.nextBoolean() ? NATIVE : CRAFTED), ((byte) (1)), ((short) (1)), ((short) (1)), referenceClusterMap.getWritablePartitionIds(DEFAULT_PARTITION_CLASS).get(BlobIdTest.random.nextInt(3)), isEncrypted, DATACHUNK);
            if ((version) <= (BLOB_ID_V2)) {
                // V1 and V2 should always return false
                Assert.assertFalse((("V" + (version)) + " encrypted bit should be false"), BlobId.BlobId.isEncrypted(blobId.getID()));
            } else
                if ((version) == (BLOB_ID_V3)) {
                    // V3 return False if encrypted is not set in the string of blobId; new blobIDV3s always set encrypted to false
                    // regardless of the constructor argument.
                    Assert.assertFalse("V3 encrypted bit should be false", BlobId.BlobId.isEncrypted(blobId.getID()));
                    // V3 should return true if blobIdString has encrypted bit
                    Assert.assertTrue("V3 should return true if blobIdString has encrypted bit", BlobId.BlobId.isEncrypted("AAME"));
                    Assert.assertTrue("V3 should return true if blobIdString has encrypted bit", BlobId.BlobId.isEncrypted("AAMF"));
                    // V3 should return false if blobIdString has no encrypted
                    Assert.assertFalse("V3 should return false if blobIdString has no encrypted", BlobId.BlobId.isEncrypted("AAMA"));
                    Assert.assertFalse("V3 should return false if blobIdString has no encrypted", BlobId.BlobId.isEncrypted("AAMB"));
                } else {
                    Assert.assertEquals((("V" + (version)) + " should return true or false based on its encrypted bit"), isEncrypted, BlobId.BlobId.isEncrypted(blobId.getID()));
                }

        }
    }

    /**
     * Test various invalid blobIds
     *
     * @throws Exception
     * 		Any unexpected exception.
     */
    @Test
    public void badIdTest() throws Exception {
        generateAndAssertBadBlobId(version);
    }

    /**
     * Tests blobIds comparisons. Among other things, ensures the following requirements are met:
     * <br>
     * V1s are always less than V2s and V3s.
     * V2s are always less than V3s.
     */
    @Test
    public void testComparisons() {
        // the version check is to do this inter-version test just once (since this is a parametrized test).
        Assume.assumeTrue(((version) == (BLOB_ID_V1)));
        for (int i = 0; i < 100; i++) {
            Map<Short, Pair<BlobId.BlobId, BlobId.BlobId>> blobIds = Arrays.stream(BlobId.BlobId.getAllValidVersions()).collect(Collectors.toMap(Function.identity(), ( v) -> new Pair<>(getRandomBlobId(v), getRandomBlobId(v))));
            for (short version : BlobId.BlobId.getAllValidVersions()) {
                BlobId.BlobId blobId = blobIds.get(version).getFirst();
                BlobId.BlobId altBlobId = blobIds.get(version).getSecond();
                Assert.assertEquals((("blobIdV" + version) + " should be equal to itself"), 0, blobId.compareTo(blobId));
                Assert.assertEquals((("blobIdV" + version) + " should be equal to itself"), blobId, blobId);
                Assert.assertThat((("Two randomly generated blobIdV" + version) + "s should be unequal"), blobId.compareTo(altBlobId), CoreMatchers.not(0));
                Assert.assertThat((("Two randomly generated blobIdV" + version) + "s should be unequal"), blobId, CoreMatchers.not(altBlobId));
                for (short otherVersion = 1; otherVersion < version; otherVersion++) {
                    BlobId.BlobId otherBlobId = blobIds.get(otherVersion).getFirst();
                    Assert.assertThat(((("blobIdV" + otherVersion) + " should not equal blobIdV") + version), otherBlobId, CoreMatchers.not(blobId));
                    Assert.assertThat(((("blobIdV" + version) + " should not equal blobIdV") + otherVersion), blobId, CoreMatchers.not(otherBlobId));
                    boolean differentVersionGroup = (version < (BLOB_ID_V3)) || (version < (BLOB_ID_V6) ? otherVersion < (BLOB_ID_V3) : otherVersion < (BLOB_ID_V6));
                    if (differentVersionGroup) {
                        Assert.assertTrue(((("blobIdV" + otherVersion) + " should be less than blobIdV") + version), ((otherBlobId.compareTo(blobId)) < 0));
                        Assert.assertTrue(((("blobIdV" + version) + " should be greater than blobIdV") + otherVersion), ((blobId.compareTo(otherBlobId)) > 0));
                    } else {
                        Assert.assertEquals((((("Comparison between blobIdV" + version) + " and blobIDV") + otherVersion) + " are based on uuid only"), blobId.getUuid().compareTo(otherBlobId.getUuid()), blobId.compareTo(otherBlobId));
                        Assert.assertEquals((((("Comparison between blobIdV" + otherVersion) + " and blobIDV") + version) + " are based on uuid only"), otherBlobId.getUuid().compareTo(blobId.getUuid()), otherBlobId.compareTo(blobId));
                    }
                }
            }
        }
    }

    /**
     * Test crafting of BlobIds.
     * Ensure that, except for the version, type, account and container, crafted id has the same constituents as the
     * input id.
     * Ensure that crafted id is the same as the input id if the input is a crafted V3 id with the same account and
     * container as the account and container in the call to craft.
     */
    @Test
    public void testCrafting() throws Exception {
        BlobId.BlobId[] inputs;
        if ((version) >= (BLOB_ID_V3)) {
            inputs = new BlobId.BlobId[]{ new BlobId.BlobId(version, NATIVE, referenceDatacenterId, referenceAccountId, referenceContainerId, referencePartitionId, false, referenceDataType), new BlobId.BlobId(version, CRAFTED, referenceDatacenterId, referenceAccountId, referenceContainerId, referencePartitionId, false, referenceDataType) };
            Assert.assertFalse("isCrafted() should be false for native id", BlobId.BlobId.isCrafted(inputs[0].getID()));
            Assert.assertTrue("isCrafted() should be true for crafted id", BlobId.BlobId.isCrafted(inputs[1].getID()));
        } else {
            inputs = new BlobId.BlobId[]{ new BlobId.BlobId(version, referenceType, referenceDatacenterId, referenceAccountId, referenceContainerId, referencePartitionId, false, null) };
            Assert.assertFalse("isCrafted() should be false for ids below BLOB_ID_V3", BlobId.BlobId.isCrafted(inputs[0].getID()));
        }
        short newAccountId = ((short) (((referenceAccountId) + 1) + (RANDOM.nextInt(100))));
        short newContainerId = ((short) (((referenceContainerId) + 1) + (RANDOM.nextInt(100))));
        BlobId.BlobId crafted = null;
        for (BlobId.BlobId id : inputs) {
            try {
                BlobId.BlobId.craft(id, BLOB_ID_V1, newAccountId, newContainerId);
                Assert.fail(("Crafting should fail for target version " + (BLOB_ID_V1)));
            } catch (IllegalArgumentException e) {
            }
            try {
                BlobId.BlobId.craft(id, BLOB_ID_V2, newAccountId, newContainerId);
                Assert.fail(("Crafting should fail for target version " + (BLOB_ID_V2)));
            } catch (IllegalArgumentException e) {
            }
            short idVersion = ((short) (Math.max(id.getVersion(), BLOB_ID_V3)));
            crafted = BlobId.BlobId.craft(id, idVersion, newAccountId, newContainerId);
            verifyCrafting(id, crafted);
        }
        BlobId.BlobId craftedAgain = BlobId.BlobId.craft(crafted, crafted.getVersion(), crafted.getAccountId(), crafted.getContainerId());
        verifyCrafting(crafted, craftedAgain);
        Assert.assertEquals("Accounts should match", crafted.getAccountId(), craftedAgain.getAccountId());
        Assert.assertEquals("Containers should match", crafted.getContainerId(), craftedAgain.getContainerId());
        Assert.assertEquals("The id string should match", crafted.getID(), craftedAgain.getID());
        if ((version) == (BLOB_ID_V3)) {
            // version check to avoid testing this repetitively.
            try {
                BlobId.BlobId.isCrafted("");
                Assert.fail("Empty blob id should not get parsed");
            } catch (IOException e) {
            }
            try {
                BlobId.BlobId.isCrafted("ZZZZZ");
                Assert.fail("Invalid version should get caught");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Test for {@link BlobId#isAccountContainerMatch}.
     * For BLOB_ID_V1, {@link BlobId#isAccountContainerMatch} should always return true.
     * For BLOB_ID_V2 and BLOB_ID_V3, return true only when both account and container match.
     */
    @Test
    public void testIsAccountContainerMatch() {
        BlobId.BlobId blobId = getRandomBlobId(version);
        if ((version) == (BLOB_ID_V1)) {
            Assert.assertTrue("isAccountContainerMatch() should always return true for  V1 blobID.", blobId.isAccountContainerMatch(blobId.getAccountId(), blobId.getContainerId()));
            Assert.assertTrue("isAccountContainerMatch() should always return true for  V1 blobID.", blobId.isAccountContainerMatch(((short) (-1)), ((short) (-1))));
            Assert.assertTrue("isAccountContainerMatch() should always return true for  V1 blobID.", blobId.isAccountContainerMatch(getRandomShort(BlobIdTest.random), getRandomShort(BlobIdTest.random)));
        } else {
            Assert.assertTrue("isAccountContainerMatch() should return true because account and container match.", blobId.isAccountContainerMatch(blobId.getAccountId(), blobId.getContainerId()));
            Assert.assertFalse("isAccountContainerMatch() should return false because account or container mismatch.", blobId.isAccountContainerMatch(blobId.getAccountId(), ((short) (-1))));
            Assert.assertFalse("isAccountContainerMatch() should return false because account or container mismatch.", blobId.isAccountContainerMatch(blobId.getAccountId(), getRandomShort(BlobIdTest.random)));
            Assert.assertFalse("isAccountContainerMatch() should return false because account or container mismatch.", blobId.isAccountContainerMatch(((short) (-1)), blobId.getContainerId()));
            Assert.assertFalse("isAccountContainerMatch() should return false because account or container mismatch.", blobId.isAccountContainerMatch(getRandomShort(BlobIdTest.random), blobId.getContainerId()));
            Assert.assertFalse("isAccountContainerMatch() should return false because account or container mismatch.", blobId.isAccountContainerMatch(((short) (-1)), ((short) (-1))));
            Assert.assertFalse("isAccountContainerMatch() should return false because account or container mismatch.", blobId.isAccountContainerMatch(getRandomShort(BlobIdTest.random), getRandomShort(BlobIdTest.random)));
        }
    }
}

