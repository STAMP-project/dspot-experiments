package com.birbit.android.jobqueue.messaging;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class DelayedMessageBagRemoveTest {
    MessageFactory factory = new MessageFactory();

    DelayedMessageBag bag = new DelayedMessageBag(factory);

    List<Long> items = Arrays.asList(1000L, 2000L, 3000L);

    Map<Long, Message> added = new HashMap<>();

    @Test
    public void testRemoveAll() {
        for (Long readyNs : items) {
            add(readyNs);
        }
        bag.removeMessages(new MessagePredicate() {
            @Override
            public boolean onMessage(Message message) {
                return true;
            }
        });
        MessageQueue mq = Mockito.mock(MessageQueue.class);
        Long t1 = bag.flushReadyMessages(500, mq);
        MatcherAssert.assertThat(t1, CoreMatchers.nullValue());
        bag.flushReadyMessages(4000, mq);
        Mockito.verify(mq, Mockito.times(0)).post(ArgumentMatchers.any(Message.class));
    }

    @Test
    public void testRemoveNothing() {
        for (Long readyNs : items) {
            add(readyNs);
        }
        bag.removeMessages(new MessagePredicate() {
            @Override
            public boolean onMessage(Message message) {
                return false;
            }
        });
        MessageQueue mq = Mockito.mock(MessageQueue.class);
        long t1 = bag.flushReadyMessages(500, mq);
        MatcherAssert.assertThat(t1, CoreMatchers.is(1000L));
        bag.flushReadyMessages(4000, mq);
        Mockito.verify(mq, Mockito.times(3)).post(ArgumentMatchers.any(Message.class));
    }

    @Test
    public void testRemoveUnmatch() {
        testRemove(0, 1000, 2000, 3000);
    }

    @Test
    public void testRemove1000() {
        testRemove(1000, 2000, 3000);
    }

    @Test
    public void testRemove2000() {
        testRemove(2000, 1000, 3000);
    }

    @Test
    public void testRemove3000() {
        testRemove(3000, 1000, 2000);
    }
}

