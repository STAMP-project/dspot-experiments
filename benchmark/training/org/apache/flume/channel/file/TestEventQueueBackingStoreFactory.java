/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.channel.file;


import EventQueueBackingStoreFile.CHECKPOINT_INCOMPLETE;
import ProtosFactory.Checkpoint;
import Serialization.VERSION_2;
import Serialization.VERSION_3;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import junit.framework.Assert;
import org.apache.flume.channel.file.instrumentation.FileChannelCounter;
import org.apache.flume.channel.file.proto.ProtosFactory;
import org.junit.Test;

import static EventQueueBackingStoreFile.INDEX_CHECKPOINT_MARKER;
import static EventQueueBackingStoreFile.INDEX_VERSION;
import static EventQueueBackingStoreFile.INDEX_WRITE_ORDER_ID;
import static Serialization.SIZE_OF_LONG;


public class TestEventQueueBackingStoreFactory {
    static final List<Long> pointersInTestCheckpoint = Arrays.asList(new Long[]{ 8589936804L, 4294969563L, 12884904153L, 8589936919L, 4294969678L, 12884904268L, 8589937034L, 4294969793L, 12884904383L });

    File baseDir;

    File checkpoint;

    File inflightTakes;

    File inflightPuts;

    File queueSetDir;

    @Test
    public void testWithNoFlag() throws Exception {
        verify(EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test")), VERSION_3, TestEventQueueBackingStoreFactory.pointersInTestCheckpoint);
    }

    @Test
    public void testWithFlag() throws Exception {
        verify(EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"), true), VERSION_3, TestEventQueueBackingStoreFactory.pointersInTestCheckpoint);
    }

    @Test
    public void testNoUprade() throws Exception {
        verify(EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"), false), VERSION_2, TestEventQueueBackingStoreFactory.pointersInTestCheckpoint);
    }

    @Test(expected = BadCheckpointException.class)
    public void testDecreaseCapacity() throws Exception {
        Assert.assertTrue(checkpoint.delete());
        EventQueueBackingStore backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
        backingStore.close();
        EventQueueBackingStoreFactory.get(checkpoint, 9, "test", new FileChannelCounter("test"));
        Assert.fail();
    }

    @Test(expected = BadCheckpointException.class)
    public void testIncreaseCapacity() throws Exception {
        Assert.assertTrue(checkpoint.delete());
        EventQueueBackingStore backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
        backingStore.close();
        EventQueueBackingStoreFactory.get(checkpoint, 11, "test", new FileChannelCounter("test"));
        Assert.fail();
    }

    @Test
    public void testNewCheckpoint() throws Exception {
        Assert.assertTrue(checkpoint.delete());
        verify(EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"), false), VERSION_3, Collections.<Long>emptyList());
    }

    @Test(expected = BadCheckpointException.class)
    public void testCheckpointBadVersion() throws Exception {
        RandomAccessFile writer = new RandomAccessFile(checkpoint, "rw");
        try {
            EventQueueBackingStore backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
            backingStore.close();
            writer.seek(((INDEX_VERSION) * (SIZE_OF_LONG)));
            writer.writeLong(94L);
            writer.getFD().sync();
            backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
        } finally {
            writer.close();
        }
    }

    @Test(expected = BadCheckpointException.class)
    public void testIncompleteCheckpoint() throws Exception {
        RandomAccessFile writer = new RandomAccessFile(checkpoint, "rw");
        try {
            EventQueueBackingStore backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
            backingStore.close();
            writer.seek(((INDEX_CHECKPOINT_MARKER) * (SIZE_OF_LONG)));
            writer.writeLong(CHECKPOINT_INCOMPLETE);
            writer.getFD().sync();
            backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
        } finally {
            writer.close();
        }
    }

    @Test(expected = BadCheckpointException.class)
    public void testCheckpointVersionNotEqualToMeta() throws Exception {
        RandomAccessFile writer = new RandomAccessFile(checkpoint, "rw");
        try {
            EventQueueBackingStore backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
            backingStore.close();
            writer.seek(((INDEX_VERSION) * (SIZE_OF_LONG)));
            writer.writeLong(2L);
            writer.getFD().sync();
            backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
        } finally {
            writer.close();
        }
    }

    @Test(expected = BadCheckpointException.class)
    public void testCheckpointVersionNotEqualToMeta2() throws Exception {
        FileOutputStream os = null;
        try {
            EventQueueBackingStore backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
            backingStore.close();
            Assert.assertTrue(checkpoint.exists());
            Assert.assertTrue(((Serialization.getMetaDataFile(checkpoint).length()) != 0));
            FileInputStream is = new FileInputStream(Serialization.getMetaDataFile(checkpoint));
            ProtosFactory.Checkpoint meta = Checkpoint.parseDelimitedFrom(is);
            Assert.assertNotNull(meta);
            is.close();
            os = new FileOutputStream(Serialization.getMetaDataFile(checkpoint));
            meta.toBuilder().setVersion(2).build().writeDelimitedTo(os);
            os.flush();
            backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
        } finally {
            os.close();
        }
    }

    @Test(expected = BadCheckpointException.class)
    public void testCheckpointOrderIdNotEqualToMeta() throws Exception {
        RandomAccessFile writer = new RandomAccessFile(checkpoint, "rw");
        try {
            EventQueueBackingStore backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
            backingStore.close();
            writer.seek(((INDEX_WRITE_ORDER_ID) * (SIZE_OF_LONG)));
            writer.writeLong(2L);
            writer.getFD().sync();
            backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
        } finally {
            writer.close();
        }
    }

    @Test(expected = BadCheckpointException.class)
    public void testCheckpointOrderIdNotEqualToMeta2() throws Exception {
        FileOutputStream os = null;
        try {
            EventQueueBackingStore backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
            backingStore.close();
            Assert.assertTrue(checkpoint.exists());
            Assert.assertTrue(((Serialization.getMetaDataFile(checkpoint).length()) != 0));
            FileInputStream is = new FileInputStream(Serialization.getMetaDataFile(checkpoint));
            ProtosFactory.Checkpoint meta = Checkpoint.parseDelimitedFrom(is);
            Assert.assertNotNull(meta);
            is.close();
            os = new FileOutputStream(Serialization.getMetaDataFile(checkpoint));
            meta.toBuilder().setWriteOrderID(1).build().writeDelimitedTo(os);
            os.flush();
            backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
        } finally {
            os.close();
        }
    }

    @Test(expected = BadCheckpointException.class)
    public void testTruncateMeta() throws Exception {
        EventQueueBackingStore backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
        backingStore.close();
        Assert.assertTrue(checkpoint.exists());
        File metaFile = Serialization.getMetaDataFile(checkpoint);
        Assert.assertTrue(((metaFile.length()) != 0));
        RandomAccessFile writer = new RandomAccessFile(metaFile, "rw");
        writer.setLength(0);
        writer.getFD().sync();
        writer.close();
        backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
    }

    @Test(expected = InvalidProtocolBufferException.class)
    public void testCorruptMeta() throws Throwable {
        EventQueueBackingStore backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
        backingStore.close();
        Assert.assertTrue(checkpoint.exists());
        File metaFile = Serialization.getMetaDataFile(checkpoint);
        Assert.assertTrue(((metaFile.length()) != 0));
        RandomAccessFile writer = new RandomAccessFile(metaFile, "rw");
        writer.seek(10);
        writer.writeLong(new Random().nextLong());
        writer.getFD().sync();
        writer.close();
        try {
            backingStore = EventQueueBackingStoreFactory.get(checkpoint, 10, "test", new FileChannelCounter("test"));
        } catch (BadCheckpointException ex) {
            throw ex.getCause();
        }
    }
}

