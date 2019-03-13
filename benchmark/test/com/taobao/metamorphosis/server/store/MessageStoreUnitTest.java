/**
 * (C) 2007-2012 Alibaba Group Holding Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Authors:
 *   wuhua <wq163@163.com> , boyan <killme2008@gmail.com>
 */
package com.taobao.metamorphosis.server.store;


import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.MessageAccessor;
import com.taobao.metamorphosis.network.PutCommand;
import com.taobao.metamorphosis.server.store.MessageStore.Segment;
import com.taobao.metamorphosis.server.store.MessageStore.SegmentList;
import com.taobao.metamorphosis.server.utils.MetaConfig;
import com.taobao.metamorphosis.utils.IdWorker;
import com.taobao.metamorphosis.utils.MessageUtils;
import com.taobao.metamorphosis.utils.MessageUtils.DecodedMessage;
import com.taobao.metamorphosis.utils.test.ConcurrentTestCase;
import com.taobao.metamorphosis.utils.test.ConcurrentTestTask;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;


public class MessageStoreUnitTest {
    private static final int MSG_COUNT = 10;

    private MessageStore messageStore;

    private final String topic = "test";

    private final int partition = 1;

    private MetaConfig metaConfig;

    private DeletePolicy deletePolicy;

    private IdWorker idWorker;

    @Test
    public void testSegmentContants() throws Exception {
        final File file = new File("testSegmentContants.test");
        if (!(file.exists())) {
            file.createNewFile();
        }
        final Segment segment = new Segment(0, file);
        try {
            segment.fileMessageSet.setHighWaterMarker(1024);
            Assert.assertTrue(segment.contains(0));
            Assert.assertFalse(segment.contains(1024));
            Assert.assertFalse(segment.contains(1025));
            Assert.assertFalse(segment.contains(2048));
            Assert.assertTrue(segment.contains(1));
            Assert.assertTrue(segment.contains(100));
            Assert.assertTrue(segment.contains(512));
            Assert.assertTrue(segment.contains(1023));
        } finally {
            if (segment != null) {
                segment.fileMessageSet.close();
            }
            file.delete();
        }
    }

    @Test
    public void testAppendSegmentDeleteSegment() throws Exception {
        final SegmentList segmentList = new SegmentList();
        Assert.assertEquals(0, segmentList.contents.get().length);
        final File file = new File("testAppendSegmentDeleteSegment.test");
        if (!(file.exists())) {
            file.createNewFile();
        }
        final Segment segment1 = new Segment(0, file);
        final Segment segment2 = new Segment(1024, file);
        final Segment segment3 = new Segment(2048, file);
        try {
            segmentList.append(segment1);
            Assert.assertEquals(1, segmentList.contents.get().length);
            Assert.assertSame(segment1, segmentList.first());
            Assert.assertSame(segment1, segmentList.last());
            segmentList.append(segment2);
            Assert.assertEquals(2, segmentList.contents.get().length);
            Assert.assertSame(segment1, segmentList.first());
            Assert.assertSame(segment2, segmentList.last());
            segmentList.append(segment3);
            Assert.assertEquals(3, segmentList.contents.get().length);
            Assert.assertSame(segment1, segmentList.first());
            Assert.assertSame(segment3, segmentList.last());
            segmentList.delete(segment1);
            Assert.assertEquals(2, segmentList.contents.get().length);
            Assert.assertSame(segment2, segmentList.first());
            Assert.assertSame(segment3, segmentList.last());
            segmentList.delete(segment3);
            Assert.assertEquals(1, segmentList.contents.get().length);
            Assert.assertSame(segment2, segmentList.first());
            Assert.assertSame(segment2, segmentList.last());
            // delete not existing
            segmentList.delete(segment3);
            Assert.assertEquals(1, segmentList.contents.get().length);
            Assert.assertSame(segment2, segmentList.first());
            Assert.assertSame(segment2, segmentList.last());
            segmentList.delete(segment2);
            Assert.assertEquals(0, segmentList.contents.get().length);
            Assert.assertNull(segmentList.first());
            Assert.assertNull(segmentList.last());
        } finally {
            if (segment1 != null) {
                segment1.fileMessageSet.close();
            }
            file.delete();
        }
    }

    @Test
    public void testAppendMessages() throws Exception {
        final PutCommand cmd1 = new PutCommand(this.topic, this.partition, "hello".getBytes(), null, 0, 0);
        final PutCommand cmd2 = new PutCommand(this.topic, this.partition, "world".getBytes(), null, 0, 0);
        final long id1 = this.idWorker.nextId();
        final long id2 = this.idWorker.nextId();
        this.messageStore.append(id1, cmd1, new AppendCallback() {
            @Override
            public void appendComplete(final Location location) {
                if (0 != (location.getOffset())) {
                    throw new RuntimeException();
                }
            }
        });
        this.messageStore.flush();
        final long size = this.messageStore.getSegments().last().size();
        this.messageStore.append(id2, cmd2, new AppendCallback() {
            @Override
            public void appendComplete(final Location location) {
                Assert.assertEquals(size, location.getOffset());
                if (size != (location.getOffset())) {
                    throw new RuntimeException();
                }
            }
        });
        this.messageStore.flush();
        this.assertMessages(id1, id2);
    }

    @Test
    public void testConcurrentAppendMessages() throws Exception {
        System.out.println("Begin concurrent test....");
        this.metaConfig.setMaxSegmentSize(((1024 * 1024) * 16));
        ConcurrentTestCase testCase = new ConcurrentTestCase(80, 1000, new ConcurrentTestTask() {
            @Override
            public void run(int index, int times) throws Exception {
                final PutCommand cmd = new PutCommand(MessageStoreUnitTest.this.topic, MessageStoreUnitTest.this.partition, new byte[1024], null, 0, 0);
                final long id = MessageStoreUnitTest.this.idWorker.nextId();
                final CountDownLatch latch = new CountDownLatch(1);
                MessageStoreUnitTest.this.messageStore.append(id, cmd, new AppendCallback() {
                    @Override
                    public void appendComplete(final Location location) {
                        if ((location.getOffset()) < 0) {
                            throw new RuntimeException();
                        } else {
                            latch.countDown();
                        }
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        });
        testCase.start();
        System.out.println((("Appended 80000 messages,cost:" + ((testCase.getDurationInMillis()) / 1000)) + " seconds"));
        Assert.assertEquals(80000, this.messageStore.getMessageCount());
    }

    // ??????????offset??MessageStore????????
    @Test
    public void testAppendMessages_toOffset() throws Exception {
        // ????????
        this.clearTopicPartDir();
        final int offset = 2048;
        this.messageStore = new MessageStore(this.topic, this.partition, this.metaConfig, this.deletePolicy, offset);
        final PutCommand cmd1 = new PutCommand(this.topic, this.partition, "hello".getBytes(), null, 0, 0);
        final PutCommand cmd2 = new PutCommand(this.topic, this.partition, "world".getBytes(), null, 0, 0);
        final long id1 = this.idWorker.nextId();
        final long id2 = this.idWorker.nextId();
        this.messageStore.append(id1, cmd1, new AppendCallback() {
            @Override
            public void appendComplete(final Location location) {
                Assert.assertEquals(2048, location.getOffset());
                if (2048 != (location.getOffset())) {
                    throw new RuntimeException();
                }
            }
        });
        this.messageStore.flush();
        final long size = this.messageStore.getSegments().last().size();
        this.messageStore.append(id2, cmd2, new AppendCallback() {
            @Override
            public void appendComplete(final Location location) {
                Assert.assertEquals((offset + size), location.getOffset());
                if ((offset + size) != (location.getOffset())) {
                    throw new RuntimeException();
                }
            }
        });
        this.messageStore.flush();
        final File partDir = this.messageStore.getPartitionDir();
        final File[] logs = partDir.listFiles();
        Assert.assertEquals(1, logs.length);
        Assert.assertTrue(logs[0].exists());
        Assert.assertEquals(this.messageStore.nameFromOffset(offset), logs[0].getName());
        final FileChannel channel = new RandomAccessFile(logs[0], "rw").getChannel();
        try {
            final ByteBuffer buf = ByteBuffer.allocate(((int) (channel.size())));
            while (buf.hasRemaining()) {
                channel.read(buf);
            } 
            buf.flip();
            final DecodedMessage decodedMessage1 = MessageUtils.decodeMessage(this.topic, buf.array(), 0);
            final DecodedMessage decodedMessage2 = MessageUtils.decodeMessage(this.topic, buf.array(), decodedMessage1.newOffset);
            final Message msg1 = new Message(this.topic, "hello".getBytes());
            MessageAccessor.setId(msg1, id1);
            final Message msg2 = new Message(this.topic, "world".getBytes());
            MessageAccessor.setId(msg2, id2);
            Assert.assertEquals(msg1, decodedMessage1.message);
            Assert.assertEquals(msg2, decodedMessage2.message);
        } finally {
            channel.close();
        }
    }

    @Test
    public void testGetNearestOffset() throws Exception {
        final SegmentList segmentList = new SegmentList();
        Assert.assertEquals(0, segmentList.contents.get().length);
        final File file = new File("testGetNearestOffset.test");
        if (!(file.exists())) {
            file.createNewFile();
        }
        final Segment segment1 = new Segment(0, file);
        segment1.fileMessageSet.setHighWaterMarker(1024);
        final Segment segment2 = new Segment(1024, file);
        segment2.fileMessageSet.setHighWaterMarker(1024);
        final Segment segment3 = new Segment(2048, file);
        segment3.fileMessageSet.setHighWaterMarker(1024);
        final Segment segment4 = new Segment(3072, file);
        segment4.fileMessageSet.setHighWaterMarker(1024);
        segmentList.append(segment1);
        segmentList.append(segment2);
        segmentList.append(segment3);
        segmentList.append(segment4);
        try {
            Assert.assertEquals(0, this.messageStore.getNearestOffset((-100), segmentList));
            Assert.assertEquals(0, this.messageStore.getNearestOffset(0, segmentList));
            Assert.assertEquals(0, this.messageStore.getNearestOffset(100, segmentList));
            Assert.assertEquals(0, this.messageStore.getNearestOffset(1023, segmentList));
            Assert.assertEquals(1024, this.messageStore.getNearestOffset(1024, segmentList));
            Assert.assertEquals(1024, this.messageStore.getNearestOffset(2000, segmentList));
            Assert.assertEquals(2048, this.messageStore.getNearestOffset(2048, segmentList));
            Assert.assertEquals(2048, this.messageStore.getNearestOffset(2049, segmentList));
            Assert.assertEquals(2048, this.messageStore.getNearestOffset(2536, segmentList));
            Assert.assertEquals(3072, this.messageStore.getNearestOffset(3072, segmentList));
            Assert.assertEquals(3072, this.messageStore.getNearestOffset(3073, segmentList));
            Assert.assertEquals((3072 + 1024), this.messageStore.getNearestOffset(4096, segmentList));
            Assert.assertEquals((3072 + 1024), this.messageStore.getNearestOffset((16 * 1024), segmentList));
        } finally {
            file.delete();
            segment1.fileMessageSet.close();
            segment2.fileMessageSet.close();
            segment3.fileMessageSet.close();
            segment4.fileMessageSet.close();
        }
    }

    @Test
    public void testFindSegment() throws Exception {
        final SegmentList segmentList = new SegmentList();
        Assert.assertEquals(0, segmentList.contents.get().length);
        final File file = new File("testFindSegment.test");
        if (!(file.exists())) {
            file.createNewFile();
        }
        final Segment segment1 = new Segment(0, file);
        segment1.fileMessageSet.setHighWaterMarker(1024);
        final Segment segment2 = new Segment(1024, file);
        segment2.fileMessageSet.setHighWaterMarker(1024);
        final Segment segment3 = new Segment(2048, file);
        segment3.fileMessageSet.setHighWaterMarker(1024);
        final Segment segment4 = new Segment(3072, file);
        segment4.fileMessageSet.setHighWaterMarker(1024);
        segmentList.append(segment1);
        segmentList.append(segment2);
        segmentList.append(segment3);
        segmentList.append(segment4);
        try {
            Assert.assertSame(segment1, this.messageStore.findSegment(segmentList.view(), 0));
            Assert.assertSame(segment1, this.messageStore.findSegment(segmentList.view(), 1));
            Assert.assertSame(segment1, this.messageStore.findSegment(segmentList.view(), 1023));
            Assert.assertSame(segment2, this.messageStore.findSegment(segmentList.view(), 1024));
            Assert.assertSame(segment2, this.messageStore.findSegment(segmentList.view(), 1536));
            Assert.assertSame(segment3, this.messageStore.findSegment(segmentList.view(), 2048));
            Assert.assertSame(segment3, this.messageStore.findSegment(segmentList.view(), 2049));
            Assert.assertSame(segment3, this.messageStore.findSegment(segmentList.view(), 3000));
            Assert.assertSame(segment4, this.messageStore.findSegment(segmentList.view(), 3072));
            Assert.assertSame(segment4, this.messageStore.findSegment(segmentList.view(), 3073));
            Assert.assertNull(this.messageStore.findSegment(segmentList.view(), 4097));
            Assert.assertNull(this.messageStore.findSegment(segmentList.view(), 4098));
            Assert.assertNull(this.messageStore.findSegment(segmentList.view(), (16 * 1024)));
            try {
                this.messageStore.findSegment(segmentList.view(), (-1));
                Assert.fail();
            } catch (final ArrayIndexOutOfBoundsException e) {
                Assert.assertTrue(true);
            }
        } finally {
            file.delete();
            segment1.fileMessageSet.close();
            segment2.fileMessageSet.close();
            segment3.fileMessageSet.close();
            segment4.fileMessageSet.close();
        }
    }

    @Test
    public void testAppendMessagesRoll() throws Exception {
        final PutCommand req = new PutCommand(this.topic, this.partition, "hello".getBytes(), null, 0, 0);
        final AtomicLong size = new AtomicLong(0);
        for (int i = 0; i < (MessageStoreUnitTest.MSG_COUNT); i++) {
            if (i == 0) {
                this.messageStore.append(this.idWorker.nextId(), req, new AppendCallback() {
                    @Override
                    public void appendComplete(final Location location) {
                        Assert.assertEquals(0, location.getOffset());
                        if (0 != (location.getOffset())) {
                            throw new RuntimeException();
                        }
                    }
                });
                this.messageStore.flush();
                size.set(this.messageStore.getSegments().last().size());
            } else {
                final int j = i;
                this.messageStore.append(this.idWorker.nextId(), req, new AppendCallback() {
                    @Override
                    public void appendComplete(final Location location) {
                        Assert.assertEquals(((size.get()) * j), location.getOffset());
                        if (((size.get()) * j) != (location.getOffset())) {
                            throw new RuntimeException();
                        }
                    }
                });
                this.messageStore.flush();
                // assertEquals(size * i,
                // this.messageStore.append(this.idWorker.nextId(),
                // req).getOffset());
            }
        }
        final File partDir = this.messageStore.getPartitionDir();
        final File[] logs = partDir.listFiles();
        Assert.assertEquals(2, logs.length);
        Arrays.sort(logs, new Comparator<File>() {
            @Override
            public int compare(final File o1, final File o2) {
                final long l = (Long.parseLong(o1.getName().split("\\.")[0])) - (Long.parseLong(o2.getName().split("\\.")[0]));
                return l == 0 ? 0 : l > 0 ? 1 : -1;
            }
        });
        Assert.assertEquals(this.messageStore.nameFromOffset(0), logs[0].getName());
        Assert.assertEquals(this.messageStore.nameFromOffset(this.metaConfig.getMaxSegmentSize()), logs[1].getName());
        final FileChannel channel1 = new RandomAccessFile(logs[0], "rw").getChannel();
        final FileChannel channel2 = new RandomAccessFile(logs[1], "rw").getChannel();
        try {
            Assert.assertEquals(this.metaConfig.getMaxSegmentSize(), channel1.size());
            Assert.assertEquals(0, channel2.size());
        } finally {
            channel1.close();
            channel2.close();
        }
    }

    @Test
    public void testAppendMessagesCloseRecover() throws Exception {
        final PutCommand req = new PutCommand(this.topic, this.partition, "hello".getBytes(), null, 0, 0);
        final AtomicLong size = new AtomicLong(0);
        for (int i = 0; i < 100; i++) {
            if (i == 0) {
                this.messageStore.append(this.idWorker.nextId(), req, new AppendCallback() {
                    @Override
                    public void appendComplete(final Location location) {
                        Assert.assertEquals(0, location.getOffset());
                        if (0 != (location.getOffset())) {
                            throw new RuntimeException();
                        }
                    }
                });
                this.messageStore.flush();
                size.set(this.messageStore.getSegments().last().size());
            } else {
                final int j = i;
                this.messageStore.append(this.idWorker.nextId(), req, new AppendCallback() {
                    @Override
                    public void appendComplete(final Location location) {
                        Assert.assertEquals(((size.get()) * j), location.getOffset());
                        if (((size.get()) * j) != (location.getOffset())) {
                            throw new RuntimeException();
                        }
                    }
                });
                this.messageStore.flush();
            }
        }
        this.messageStore.flush();
        final Segment[] segments = this.messageStore.getSegments().view();
        final int oldSegmentCount = segments.length;
        final String lastSegmentName = segments[((segments.length) - 1)].file.getName();
        final long lastSegmentSize = segments[((segments.length) - 1)].size();
        this.messageStore.close();
        // recover
        this.messageStore = new MessageStore(this.topic, this.partition, this.metaConfig, this.deletePolicy);
        final Segment[] newSegments = this.messageStore.getSegments().view();
        Assert.assertEquals(oldSegmentCount, newSegments.length);
        Assert.assertEquals(lastSegmentName, newSegments[((newSegments.length) - 1)].file.getName());
        Assert.assertEquals(lastSegmentSize, newSegments[((newSegments.length) - 1)].size());
        Segment prev = null;
        for (final Segment s : newSegments) {
            if (prev != null) {
                Assert.assertEquals(s.start, ((prev.start) + (prev.size())));
            }
            prev = s;
        }
    }

    @Test
    public void testSlice() throws Exception {
        final PutCommand req = new PutCommand(this.topic, this.partition, "hello".getBytes(), null, 0, 0);
        for (int i = 0; i < (MessageStoreUnitTest.MSG_COUNT); i++) {
            this.messageStore.append(this.idWorker.nextId(), req, null);
        }
        FileMessageSet subSet = ((FileMessageSet) (this.messageStore.slice(0, ((this.metaConfig.getMaxSegmentSize()) - 10))));
        Assert.assertEquals(0, subSet.getOffset());
        Assert.assertEquals(((this.metaConfig.getMaxSegmentSize()) - 10), subSet.getSizeInBytes());
        subSet = ((FileMessageSet) (this.messageStore.slice(0, ((this.metaConfig.getMaxSegmentSize()) + 10))));
        Assert.assertEquals(0, subSet.getOffset());
        Assert.assertEquals(this.metaConfig.getMaxSegmentSize(), subSet.getSizeInBytes());
        subSet = ((FileMessageSet) (this.messageStore.slice(((this.metaConfig.getMaxSegmentSize()) - 10), ((this.metaConfig.getMaxSegmentSize()) + 10))));
        Assert.assertEquals(((this.metaConfig.getMaxSegmentSize()) - 10), subSet.getOffset());
        Assert.assertEquals(10, subSet.getSizeInBytes());
        Assert.assertNull(this.messageStore.slice(10000, 1024));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSliceIndexOutOfBounds() throws Exception {
        this.messageStore.slice((-1), 1024);
    }

    @Test
    public void testRunDeletePolicy() throws Exception {
        setMaxReservedTime(1000);
        final PutCommand req = new PutCommand(this.topic, this.partition, "hello".getBytes(), null, 0, 0);
        for (int i = 0; i < (MessageStoreUnitTest.MSG_COUNT); i++) {
            this.messageStore.append(this.idWorker.nextId(), req, null);
        }
        final File partDir = this.messageStore.getPartitionDir();
        Assert.assertEquals(2, partDir.listFiles().length);
        Assert.assertEquals(2, this.messageStore.getSegments().view().length);
        Thread.sleep(1500);
        this.messageStore.runDeletePolicy();
        Assert.assertEquals(1, partDir.listFiles().length);
        Assert.assertEquals(1, this.messageStore.getSegments().view().length);
        Thread.sleep(1500);
        this.messageStore.runDeletePolicy();
        Assert.assertEquals(1, partDir.listFiles().length);
        Assert.assertEquals(1, this.messageStore.getSegments().view().length);
    }

    @Test
    public void testAppendMany() throws Exception {
        final PutCommand cmd1 = new PutCommand(this.topic, this.partition, "hello".getBytes(), null, 0, 0);
        final PutCommand cmd2 = new PutCommand(this.topic, this.partition, "world".getBytes(), null, 0, 0);
        final long id1 = this.idWorker.nextId();
        final long id2 = this.idWorker.nextId();
        final List<Long> ids = new ArrayList<Long>();
        ids.add(id1);
        ids.add(id2);
        final List<PutCommand> cmds = new ArrayList<PutCommand>();
        cmds.add(cmd1);
        cmds.add(cmd2);
        this.messageStore.append(ids, cmds, new AppendCallback() {
            @Override
            public void appendComplete(final Location location) {
                Assert.assertEquals(0, location.getOffset());
                if (0 != (location.getOffset())) {
                    throw new RuntimeException();
                }
            }
        });
        this.messageStore.flush();
        this.assertMessages(id1, id2);
    }
}

