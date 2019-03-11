package org.jctools.queues;


import org.jctools.queues.matchers.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class QueueSanityTestSpscArrayExtended {
    @Test
    public void shouldWorkAfterWrap() {
        // Arrange
        final SpscArrayQueue<Object> q = new SpscArrayQueue<Object>(1024);
        // starting point for empty queue at max long, next offer will wrap the producerIndex
        q.soConsumerIndex(Long.MAX_VALUE);
        q.soProducerIndex(Long.MAX_VALUE);
        q.producerLimit = Long.MAX_VALUE;
        // valid starting point
        Assert.assertThat(q, Matchers.emptyAndZeroSize());
        // Act
        // assert offer is successful
        final Object e = new Object();
        Assert.assertTrue(q.offer(e));
        // size is computed correctly after wrap
        Assert.assertThat(q, not(Matchers.emptyAndZeroSize()));
        Assert.assertThat(q, hasSize(1));
        // now consumer index wraps
        final Object poll = q.poll();
        Assert.assertThat(poll, sameInstance(e));
        Assert.assertThat(q, Matchers.emptyAndZeroSize());
        // let's go again
        Assert.assertTrue(q.offer(e));
        Assert.assertThat(q, not(Matchers.emptyAndZeroSize()));
        final Object poll2 = q.poll();
        Assert.assertThat(poll2, sameInstance(e));
        Assert.assertThat(q, Matchers.emptyAndZeroSize());
    }
}

