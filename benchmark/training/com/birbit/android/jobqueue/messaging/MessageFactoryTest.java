package com.birbit.android.jobqueue.messaging;


import com.birbit.android.jobqueue.messaging.message.AddJobMessage;
import com.birbit.android.jobqueue.messaging.message.CommandMessage;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class MessageFactoryTest {
    MessageFactory factory = new MessageFactory();

    @Test
    public void test() {
        AddJobMessage aj1 = factory.obtain(AddJobMessage.class);
        MatcherAssert.assertThat(aj1, CoreMatchers.isA(AddJobMessage.class));
        CommandMessage cm1 = factory.obtain(CommandMessage.class);
        MatcherAssert.assertThat(cm1, CoreMatchers.isA(CommandMessage.class));
        MatcherAssert.assertThat(factory.obtain(AddJobMessage.class), CoreMatchers.not(CoreMatchers.sameInstance(aj1)));
        MatcherAssert.assertThat(factory.obtain(CommandMessage.class), CoreMatchers.not(CoreMatchers.sameInstance(cm1)));
        factory.release(aj1);
        factory.release(cm1);
        MatcherAssert.assertThat(factory.obtain(AddJobMessage.class), CoreMatchers.sameInstance(aj1));
        MatcherAssert.assertThat(factory.obtain(CommandMessage.class), CoreMatchers.sameInstance(cm1));
        MatcherAssert.assertThat(factory.obtain(AddJobMessage.class), CoreMatchers.not(CoreMatchers.sameInstance(aj1)));
        MatcherAssert.assertThat(factory.obtain(CommandMessage.class), CoreMatchers.not(CoreMatchers.sameInstance(cm1)));
    }
}

