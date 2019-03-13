/**
 * Copyright (c) 2016 YCSB contributors All rights reserved.
 * Copyright 2014 Basho Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package com.yahoo.ycsb.db.riak;


import Status.NOT_FOUND;
import Status.OK;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.StringByteIterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration tests for the Riak KV client.
 */
public class RiakKVClientTest {
    private static RiakKVClient riakClient;

    private static final String bucket = "testBucket";

    private static final String keyPrefix = "testKey";

    private static final int recordsToInsert = 20;

    private static final int recordsToScan = 7;

    private static final String firstField = "Key number";

    private static final String secondField = "Key number doubled";

    private static final String thirdField = "Key number square";

    private static boolean testStarted = false;

    /**
     * Test method for read transaction. It is designed to read two of the three fields stored for each key, to also test
     * if the createResultHashMap() function implemented in RiakKVClient.java works as expected.
     */
    @Test
    public void testRead() {
        // Choose a random key to read, among the available ones.
        int readKeyNumber = new Random().nextInt(RiakKVClientTest.recordsToInsert);
        // Prepare two fields to read.
        Set<String> fields = new HashSet<>();
        fields.add(RiakKVClientTest.firstField);
        fields.add(RiakKVClientTest.thirdField);
        // Prepare an expected result.
        HashMap<String, String> expectedValue = new HashMap<>();
        expectedValue.put(RiakKVClientTest.firstField, Integer.toString(readKeyNumber));
        expectedValue.put(RiakKVClientTest.thirdField, Integer.toString((readKeyNumber * readKeyNumber)));
        // Define a HashMap to store the actual result.
        HashMap<String, ByteIterator> readValue = new HashMap<>();
        // If a read transaction has been properly done, then one has to receive a Status.OK return from the read()
        // function. Moreover, the actual returned result MUST match the expected one.
        Assert.assertEquals("Read transaction FAILED.", OK, RiakKVClientTest.riakClient.read(RiakKVClientTest.bucket, ((RiakKVClientTest.keyPrefix) + (Integer.toString(readKeyNumber))), fields, readValue));
        Assert.assertEquals("Read test FAILED. Actual read transaction value is NOT MATCHING the expected one.", expectedValue.toString(), readValue.toString());
    }

    /**
     * Test method for scan transaction. A scan transaction has to be considered successfully completed only if all the
     * requested values are read (i.e. scan transaction returns with Status.OK). Moreover, one has to check if the
     * obtained results match the expected ones.
     */
    @Test
    public void testScan() {
        // Choose, among the available ones, a random key as starting point for the scan transaction.
        int startScanKeyNumber = new Random().nextInt(((RiakKVClientTest.recordsToInsert) - (RiakKVClientTest.recordsToScan)));
        // Prepare a HashMap vector to store the scan transaction results.
        Vector<HashMap<String, ByteIterator>> scannedValues = new Vector<>();
        // Check whether the scan transaction is correctly performed or not.
        Assert.assertEquals("Scan transaction FAILED.", OK, RiakKVClientTest.riakClient.scan(RiakKVClientTest.bucket, ((RiakKVClientTest.keyPrefix) + (Integer.toString(startScanKeyNumber))), RiakKVClientTest.recordsToScan, null, scannedValues));
        // After the scan transaction completes, compare the obtained results with the expected ones.
        for (int i = 0; i < (RiakKVClientTest.recordsToScan); i++) {
            Assert.assertEquals("Scan test FAILED: the current scanned key is NOT MATCHING the expected one.", RiakKVClientTest.createExpectedHashMap((startScanKeyNumber + i)).toString(), scannedValues.get(i).toString());
        }
    }

    /**
     * Test method for update transaction. The test is designed to restore the previously read key. It is assumed to be
     * correct when, after performing the update transaction, one reads the just provided values.
     */
    @Test
    public void testUpdate() {
        // Choose a random key to read, among the available ones.
        int updateKeyNumber = new Random().nextInt(RiakKVClientTest.recordsToInsert);
        // Define a HashMap to save the previously stored values for eventually restoring them.
        HashMap<String, ByteIterator> readValueBeforeUpdate = new HashMap<>();
        RiakKVClientTest.riakClient.read(RiakKVClientTest.bucket, ((RiakKVClientTest.keyPrefix) + (Integer.toString(updateKeyNumber))), null, readValueBeforeUpdate);
        // Prepare an update HashMap to store.
        HashMap<String, String> updateValue = new HashMap<>();
        updateValue.put(RiakKVClientTest.firstField, "UPDATED");
        updateValue.put(RiakKVClientTest.secondField, "UPDATED");
        updateValue.put(RiakKVClientTest.thirdField, "UPDATED");
        // First of all, perform the update and check whether it's failed or not.
        Assert.assertEquals("Update transaction FAILED.", OK, RiakKVClientTest.riakClient.update(RiakKVClientTest.bucket, ((RiakKVClientTest.keyPrefix) + (Integer.toString(updateKeyNumber))), StringByteIterator.getByteIteratorMap(updateValue)));
        // Then, read the key again and...
        HashMap<String, ByteIterator> readValueAfterUpdate = new HashMap<>();
        Assert.assertEquals("Update test FAILED. Unable to read key value.", OK, RiakKVClientTest.riakClient.read(RiakKVClientTest.bucket, ((RiakKVClientTest.keyPrefix) + (Integer.toString(updateKeyNumber))), null, readValueAfterUpdate));
        // ...compare the result with the new one!
        Assert.assertEquals("Update transaction NOT EXECUTED PROPERLY. Values DID NOT CHANGE.", updateValue.toString(), readValueAfterUpdate.toString());
        // Finally, restore the previously read key.
        Assert.assertEquals("Update test FAILED. Unable to restore previous key value.", OK, RiakKVClientTest.riakClient.update(RiakKVClientTest.bucket, ((RiakKVClientTest.keyPrefix) + (Integer.toString(updateKeyNumber))), readValueBeforeUpdate));
    }

    /**
     * Test method for insert transaction. It is designed to insert a key just after the last key inserted in the setUp()
     * phase.
     */
    @Test
    public void testInsert() {
        // Define a HashMap to insert and another one for the comparison operation.
        HashMap<String, String> insertValue = RiakKVClientTest.createExpectedHashMap(RiakKVClientTest.recordsToInsert);
        HashMap<String, ByteIterator> readValue = new HashMap<>();
        // Check whether the insertion transaction was performed or not.
        Assert.assertEquals("Insert transaction FAILED.", OK, RiakKVClientTest.riakClient.insert(RiakKVClientTest.bucket, ((RiakKVClientTest.keyPrefix) + (Integer.toString(RiakKVClientTest.recordsToInsert))), StringByteIterator.getByteIteratorMap(insertValue)));
        // Finally, compare the insertion performed with the one expected by reading the key.
        Assert.assertEquals("Insert test FAILED. Unable to read inserted value.", OK, RiakKVClientTest.riakClient.read(RiakKVClientTest.bucket, ((RiakKVClientTest.keyPrefix) + (Integer.toString(RiakKVClientTest.recordsToInsert))), null, readValue));
        Assert.assertEquals("Insert test FAILED. Actual read transaction value is NOT MATCHING the inserted one.", insertValue.toString(), readValue.toString());
    }

    /**
     * Test method for delete transaction. The test deletes a key, then performs a read that should give a
     * Status.NOT_FOUND response. Finally, it restores the previously read key.
     */
    @Test
    public void testDelete() {
        // Choose a random key to delete, among the available ones.
        int deleteKeyNumber = new Random().nextInt(RiakKVClientTest.recordsToInsert);
        // Define a HashMap to save the previously stored values for its eventual restore.
        HashMap<String, ByteIterator> readValueBeforeDelete = new HashMap<>();
        RiakKVClientTest.riakClient.read(RiakKVClientTest.bucket, ((RiakKVClientTest.keyPrefix) + (Integer.toString(deleteKeyNumber))), null, readValueBeforeDelete);
        // First of all, delete the key.
        Assert.assertEquals("Delete transaction FAILED.", OK, RiakKVClientTest.delete(((RiakKVClientTest.keyPrefix) + (Integer.toString(deleteKeyNumber)))));
        // Then, check if the deletion was actually achieved.
        Assert.assertEquals("Delete test FAILED. Key NOT deleted.", NOT_FOUND, RiakKVClientTest.riakClient.read(RiakKVClientTest.bucket, ((RiakKVClientTest.keyPrefix) + (Integer.toString(deleteKeyNumber))), null, null));
        // Finally, restore the previously deleted key.
        Assert.assertEquals("Delete test FAILED. Unable to restore previous key value.", OK, RiakKVClientTest.riakClient.insert(RiakKVClientTest.bucket, ((RiakKVClientTest.keyPrefix) + (Integer.toString(deleteKeyNumber))), readValueBeforeDelete));
    }
}

