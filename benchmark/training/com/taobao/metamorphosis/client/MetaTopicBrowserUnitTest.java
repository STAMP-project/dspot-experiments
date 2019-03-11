package com.taobao.metamorphosis.client;


import MetaTopicBrowser.Itr;
import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.consumer.MessageConsumer;
import com.taobao.metamorphosis.cluster.Partition;
import com.taobao.metamorphosis.consumer.MessageIterator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;


public class MetaTopicBrowserUnitTest {
    private MetaTopicBrowser browser;

    private List<Partition> partitions;

    private final String topic = "test";

    private final int maxSize = 1024;

    private final long timeoutInMills = 1000L;

    private MessageConsumer consumer;

    private IMocksControl control;

    @Test
    public void testIteratorInSamePartition() throws Exception {
        EasyMock.expect(this.consumer.get(this.topic, new Partition("0-0"), 0L, this.maxSize, this.timeoutInMills, TimeUnit.MILLISECONDS)).andReturn(new MessageIterator(this.topic, this.createMessageBuffer())).once();
        EasyMock.expect(this.consumer.get(this.topic, new Partition("0-0"), 25L, this.maxSize, this.timeoutInMills, TimeUnit.MILLISECONDS)).andReturn(new MessageIterator(this.topic, this.createMessageBuffer())).once();
        this.control.replay();
        Iterator<Message> it = this.browser.iterator();
        if (it.hasNext()) {
            this.assertMsg(it.next());
        }
        if (it.hasNext()) {
            this.assertMsg(it.next());
        }
        this.control.verify();
        MetaTopicBrowser.Itr mit = ((MetaTopicBrowser.Itr) (it));
        Assert.assertEquals(2, mit.partitions.size());
        Assert.assertFalse(mit.partitions.contains(new Partition("0-0")));
    }

    @Test
    public void testIteratorMoveOnPartition() throws Exception {
        EasyMock.expect(this.consumer.get(this.topic, new Partition("0-0"), 0L, this.maxSize, this.timeoutInMills, TimeUnit.MILLISECONDS)).andReturn(new MessageIterator(this.topic, this.createMessageBuffer())).once();
        EasyMock.expect(this.consumer.get(this.topic, new Partition("0-0"), 25L, this.maxSize, this.timeoutInMills, TimeUnit.MILLISECONDS)).andReturn(null);
        EasyMock.expect(this.consumer.get(this.topic, new Partition("0-1"), 0L, this.maxSize, this.timeoutInMills, TimeUnit.MILLISECONDS)).andReturn(new MessageIterator(this.topic, this.createMessageBuffer())).once();
        this.control.replay();
        Iterator<Message> it = this.browser.iterator();
        if (it.hasNext()) {
            this.assertMsg(it.next());
        }
        if (it.hasNext()) {
            this.assertMsg(it.next());
        }
        this.control.verify();
        MetaTopicBrowser.Itr mit = ((MetaTopicBrowser.Itr) (it));
        Assert.assertEquals(1, mit.partitions.size());
        Assert.assertFalse(mit.partitions.contains(new Partition("0-0")));
        Assert.assertFalse(mit.partitions.contains(new Partition("0-1")));
    }

    @Test
    public void testIteratorToEnd() throws Exception {
        EasyMock.expect(this.consumer.get(this.topic, new Partition("0-0"), 0L, this.maxSize, this.timeoutInMills, TimeUnit.MILLISECONDS)).andReturn(new MessageIterator(this.topic, this.createMessageBuffer())).once();
        EasyMock.expect(this.consumer.get(this.topic, new Partition("0-0"), 25L, this.maxSize, this.timeoutInMills, TimeUnit.MILLISECONDS)).andReturn(null);
        EasyMock.expect(this.consumer.get(this.topic, new Partition("0-1"), 0L, this.maxSize, this.timeoutInMills, TimeUnit.MILLISECONDS)).andReturn(new MessageIterator(this.topic, this.createMessageBuffer())).once();
        EasyMock.expect(this.consumer.get(this.topic, new Partition("0-1"), 25L, this.maxSize, this.timeoutInMills, TimeUnit.MILLISECONDS)).andReturn(null).once();
        EasyMock.expect(this.consumer.get(this.topic, new Partition("0-2"), 0L, this.maxSize, this.timeoutInMills, TimeUnit.MILLISECONDS)).andReturn(new MessageIterator(this.topic, this.createMessageBuffer())).once();
        EasyMock.expect(this.consumer.get(this.topic, new Partition("0-2"), 25L, this.maxSize, this.timeoutInMills, TimeUnit.MILLISECONDS)).andReturn(null).once();
        this.control.replay();
        Iterator<Message> it = this.browser.iterator();
        if (it.hasNext()) {
            this.assertMsg(it.next());
        }
        if (it.hasNext()) {
            this.assertMsg(it.next());
        }
        if (it.hasNext()) {
            this.assertMsg(it.next());
        }
        Assert.assertFalse(it.hasNext());
        Assert.assertFalse(it.hasNext());
        Assert.assertFalse(it.hasNext());
        this.control.verify();
        MetaTopicBrowser.Itr mit = ((MetaTopicBrowser.Itr) (it));
        Assert.assertEquals(0, mit.partitions.size());
        try {
            it.next();
            Assert.fail();
        } catch (NoSuchElementException e) {
        }
    }
}

