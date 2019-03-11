package cc.blynk.utils.structure;


import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 24.05.16.
 */
public class LimitedQueueTest {
    @Test
    public void addLimitTest() {
        BaseLimitedQueue<String> list = new BaseLimitedQueue(2);
        list.add("1");
        list.add("2");
        list.add("3");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("2", list.poll());
        Assert.assertEquals("3", list.poll());
    }
}

