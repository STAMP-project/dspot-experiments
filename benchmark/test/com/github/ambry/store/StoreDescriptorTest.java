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
package com.github.ambry.store;


import StoreDescriptor.VERSION_0;
import com.github.ambry.utils.CrcOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

import static StoreDescriptor.STORE_DESCRIPTOR_FILENAME;


/**
 * Tests {@link StoreDescriptor}
 */
public class StoreDescriptorTest {
    /**
     * Tests {@link StoreDescriptor} for unit tests for instantiation and converting bytes into StoreDescriptor
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testStoreDescriptor() throws IOException {
        File tempDir = StoreTestUtils.createTempDirectory("storeDir");
        File storeDescriptorFile = new File(tempDir.getAbsolutePath(), STORE_DESCRIPTOR_FILENAME);
        StoreDescriptor storeDescriptor = new StoreDescriptor(tempDir.getAbsolutePath());
        // store descriptor file should have been created.
        StoreDescriptor newStoreDescriptor = new StoreDescriptor(tempDir.getAbsolutePath());
        Assert.assertEquals("IncarnationId mismatch ", storeDescriptor.getIncarnationId(), newStoreDescriptor.getIncarnationId());
        Assert.assertTrue("Store descriptor file could not be deleted", storeDescriptorFile.delete());
        // Create StoreDescriptor file with new incarnationId
        UUID incarnationIdUUID = UUID.randomUUID();
        byte[] toBytes = getBytesForStoreDescriptor(VERSION_0, incarnationIdUUID);
        storeDescriptorFile = new File(tempDir.getAbsolutePath(), STORE_DESCRIPTOR_FILENAME);
        Assert.assertTrue("Store descriptor file could not be created", storeDescriptorFile.createNewFile());
        createStoreFile(storeDescriptorFile, toBytes);
        storeDescriptor = new StoreDescriptor(tempDir.getAbsolutePath());
        Assert.assertEquals("IncarnationId mismatch ", incarnationIdUUID, storeDescriptor.getIncarnationId());
        // check for wrong version
        Assert.assertTrue("Store descriptor file could not be deleted", storeDescriptorFile.delete());
        toBytes = getBytesForStoreDescriptor(((short) (1)), incarnationIdUUID);
        Assert.assertTrue("Store descriptor file could not be created", storeDescriptorFile.createNewFile());
        createStoreFile(storeDescriptorFile, toBytes);
        try {
            new StoreDescriptor(tempDir.getAbsolutePath());
            Assert.fail("Wrong version should have thrown IllegalArgumentException ");
        } catch (IllegalArgumentException e) {
        }
        // check for wrong Crc
        Assert.assertTrue("Store descriptor file could not be deleted", storeDescriptorFile.delete());
        Assert.assertTrue("Store descriptor file could not be created", storeDescriptorFile.createNewFile());
        toBytes = getBytesForStoreDescriptor(VERSION_0, incarnationIdUUID);
        CrcOutputStream crcOutputStream = new CrcOutputStream(new FileOutputStream(storeDescriptorFile));
        DataOutputStream dataOutputStream = new DataOutputStream(crcOutputStream);
        dataOutputStream.write(toBytes);
        dataOutputStream.writeLong(((crcOutputStream.getValue()) + 1));
        dataOutputStream.close();
        try {
            new StoreDescriptor(tempDir.getAbsolutePath());
            Assert.fail("Wrong CRC should have thrown IllegalStateException ");
        } catch (IllegalStateException e) {
        }
    }
}

