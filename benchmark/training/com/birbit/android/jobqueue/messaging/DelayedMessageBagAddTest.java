package com.birbit.android.jobqueue.messaging;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
public class DelayedMessageBagAddTest {
    MessageFactory factory = new MessageFactory();

    DelayedMessageBag bag = new DelayedMessageBag(factory);

    List<Long> ordered;

    Map<Long, Message> added = new HashMap<>();

    public DelayedMessageBagAddTest(List<Long> ordered) {
        this.ordered = ordered;
    }

    @Test
    public void testAddOrdered() {
        for (Long readyNs : ordered) {
            add(readyNs);
        }
        Message m1 = added.get(1000L);
        Message m2 = added.get(2000L);
        Message m3 = added.get(3000L);
        MatcherAssert.assertThat(m1, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(m2, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(m3, CoreMatchers.notNullValue());
        MessageQueue mq = Mockito.mock(MessageQueue.class);
        long t1 = bag.flushReadyMessages(500, mq);
        MatcherAssert.assertThat(t1, CoreMatchers.is(1000L));
        Mockito.verify(mq, Mockito.times(0)).post(ArgumentMatchers.any(Message.class));
        long t2 = bag.flushReadyMessages(1000, mq);
        MatcherAssert.assertThat(t2, CoreMatchers.is(2000L));
        Mockito.verify(mq).post(m1);
        Long t3 = bag.flushReadyMessages(3001, mq);
        MatcherAssert.assertThat(t3, CoreMatchers.nullValue());
        Mockito.verify(mq).post(m2);
        Mockito.verify(mq).post(m3);
    }
}

