package org.mockserver.collections;


import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class BoundedConcurrentLinkedQueueTest {
    @Test
    public void shouldNotAllowAddingMoreThenMaximumNumberOfEntriesWhenUsingAdd() {
        // given
        BoundedConcurrentLinkedQueue<String> concurrentLinkedQueue = new BoundedConcurrentLinkedQueue<String>(3);
        // when
        concurrentLinkedQueue.add("1");
        concurrentLinkedQueue.add("2");
        concurrentLinkedQueue.add("3");
        concurrentLinkedQueue.add("4");
        // then
        Assert.assertEquals(3, concurrentLinkedQueue.size());
        Assert.assertThat(concurrentLinkedQueue, CoreMatchers.not(Matchers.contains("1")));
        Assert.assertThat(concurrentLinkedQueue, Matchers.contains("2", "3", "4"));
    }

    @Test
    public void shouldNotAllowAddingMoreThenMaximumNumberOfEntriesWhenUsingAddAll() {
        // given
        BoundedConcurrentLinkedQueue<String> concurrentLinkedQueue = new BoundedConcurrentLinkedQueue<String>(3);
        // when
        concurrentLinkedQueue.addAll(Arrays.asList("1", "2", "3", "4"));
        // then
        Assert.assertEquals(3, concurrentLinkedQueue.size());
        Assert.assertThat(concurrentLinkedQueue, CoreMatchers.not(Matchers.contains("1")));
        Assert.assertThat(concurrentLinkedQueue, Matchers.contains("2", "3", "4"));
    }
}

