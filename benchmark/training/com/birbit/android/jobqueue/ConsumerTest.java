package com.birbit.android.jobqueue;


import CommandMessage.POKE;
import CommandMessage.QUIT;
import Consumer.pokeMessagePredicate;
import JobHolder.RUN_RESULT_SUCCESS;
import com.birbit.android.jobqueue.ConsumerManager.Consumer;
import com.birbit.android.jobqueue.messaging.Message;
import com.birbit.android.jobqueue.messaging.MessageFactory;
import com.birbit.android.jobqueue.messaging.MessageQueueConsumer;
import com.birbit.android.jobqueue.messaging.PriorityMessageQueue;
import com.birbit.android.jobqueue.messaging.SafeMessageQueue;
import com.birbit.android.jobqueue.messaging.message.CommandMessage;
import com.birbit.android.jobqueue.messaging.message.JobConsumerIdleMessage;
import com.birbit.android.jobqueue.messaging.message.RunJobMessage;
import com.birbit.android.jobqueue.messaging.message.RunJobResultMessage;
import com.birbit.android.jobqueue.test.timer.MockTimer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class ConsumerTest {
    MessageFactory factory = new MessageFactory();

    MockTimer timer = new MockTimer();

    private MessageQueueConsumer dummyConsumer = new MessageQueueConsumer() {
        @Override
        public void handleMessage(Message message) {
        }

        @Override
        public void onIdle() {
        }
    };

    @Test
    public void init() {
        PriorityMessageQueue pmq = Mockito.mock(PriorityMessageQueue.class);
        SafeMessageQueue mq = Mockito.mock(SafeMessageQueue.class);
        Consumer consumer = new Consumer(pmq, mq, factory, timer);
        consumer.run();
        Mockito.verify(mq).consume(ArgumentMatchers.any(MessageQueueConsumer.class));
    }

    @Test
    public void idleMessage() {
        MessageFactory factory = new MessageFactory();
        PriorityMessageQueue pmq = new PriorityMessageQueue(timer, factory);
        SafeMessageQueue mq = new SafeMessageQueue(timer, factory, "test");
        setRunning(pmq);
        setRunning(mq);
        timer.setNow(2001);
        Consumer consumer = new Consumer(pmq, mq, factory, timer);
        consumer.queueConsumer.onIdle();
        Message message = pmq.next(dummyConsumer);
        MatcherAssert.assertThat(message, CoreMatchers.instanceOf(JobConsumerIdleMessage.class));
        MatcherAssert.assertThat(getLastJobCompleted(), CoreMatchers.is(2001L));
    }

    @Test
    public void runJobMessage() {
        MessageFactory factory = new MessageFactory();
        PriorityMessageQueue pmq = new PriorityMessageQueue(timer, factory);
        setRunning(pmq);
        SafeMessageQueue mq = new SafeMessageQueue(timer, factory, "test");
        setRunning(mq);
        timer.setNow(2001);
        Consumer consumer = new Consumer(pmq, mq, factory, timer);
        RunJobMessage rjm = factory.obtain(RunJobMessage.class);
        JobHolder mockHolder = Mockito.mock(JobHolder.class);
        Mockito.when(mockHolder.safeRun(0, timer)).thenReturn(RUN_RESULT_SUCCESS);
        rjm.setJobHolder(mockHolder);
        timer.setNow(3001);
        consumer.queueConsumer.handleMessage(rjm);
        Message message = pmq.next(dummyConsumer);
        MatcherAssert.assertThat(message, CoreMatchers.instanceOf(RunJobResultMessage.class));
        RunJobResultMessage result = ((RunJobResultMessage) (message));
        MatcherAssert.assertThat(result.getResult(), CoreMatchers.is(RUN_RESULT_SUCCESS));
        MatcherAssert.assertThat(result.getJobHolder(), CoreMatchers.is(mockHolder));
        MatcherAssert.assertThat(consumer.lastJobCompleted, CoreMatchers.is(3001L));
    }

    @Test
    public void removePokesAfterJobTest() {
        MessageFactory factory = new MessageFactory();
        PriorityMessageQueue pmq = new PriorityMessageQueue(timer, factory);
        setRunning(pmq);
        SafeMessageQueue mq = Mockito.spy(new SafeMessageQueue(timer, factory, "test"));
        setRunning(mq);
        timer.setNow(2001);
        Consumer consumer = new Consumer(pmq, mq, factory, timer);
        RunJobMessage rjm = factory.obtain(RunJobMessage.class);
        JobHolder mockHolder = Mockito.mock(JobHolder.class);
        Mockito.when(mockHolder.safeRun(0, new MockTimer())).thenReturn(RUN_RESULT_SUCCESS);
        rjm.setJobHolder(mockHolder);
        timer.setNow(3001);
        Mockito.verify(mq, Mockito.times(0)).cancelMessages(pokeMessagePredicate);
        consumer.queueConsumer.handleMessage(rjm);
        Mockito.verify(mq, Mockito.times(1)).cancelMessages(pokeMessagePredicate);
    }

    @Test
    public void pokePredicateTest() {
        CommandMessage cm = new CommandMessage();
        cm.set(POKE);
        MatcherAssert.assertThat(pokeMessagePredicate.onMessage(cm), CoreMatchers.is(true));
        cm.set(QUIT);
        MatcherAssert.assertThat(pokeMessagePredicate.onMessage(cm), CoreMatchers.is(false));
        MatcherAssert.assertThat(pokeMessagePredicate.onMessage(new RunJobMessage()), CoreMatchers.is(false));
    }
}

