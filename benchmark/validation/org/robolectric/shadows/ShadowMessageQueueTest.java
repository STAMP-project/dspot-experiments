package org.robolectric.shadows;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.SystemClock;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowMessage._Message_;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.Scheduler;


@RunWith(AndroidJUnit4.class)
public class ShadowMessageQueueTest {
    private Looper looper;

    private MessageQueue queue;

    private ShadowMessageQueue shadowQueue;

    private Message testMessage;

    private ShadowMessageQueueTest.TestHandler handler;

    private Scheduler scheduler;

    private String quitField;

    private static class TestHandler extends Handler {
        public List<Message> handled = new ArrayList<>();

        public TestHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            handled.add(msg);
        }
    }

    @Test
    public void test_setGetHead() {
        shadowQueue.setHead(testMessage);
        assertThat(shadowQueue.getHead()).named("getHead()").isSameAs(testMessage);
    }

    @Test
    public void enqueueMessage_setsHead() {
        enqueueMessage(testMessage, 100);
        assertThat(shadowQueue.getHead()).named("head").isSameAs(testMessage);
    }

    @Test
    public void enqueueMessage_returnsTrue() {
        assertThat(enqueueMessage(testMessage, 100)).named("retval").isTrue();
    }

    @Test
    public void enqueueMessage_setsWhen() {
        enqueueMessage(testMessage, 123);
        assertThat(testMessage.getWhen()).named("when").isEqualTo(123);
    }

    @Test
    public void enqueueMessage_returnsFalse_whenQuitting() {
        ReflectionHelpers.setField(queue, quitField, true);
        assertThat(enqueueMessage(testMessage, 1)).named("enqueueMessage()").isFalse();
    }

    @Test
    public void enqueueMessage_doesntSchedule_whenQuitting() {
        ReflectionHelpers.setField(queue, quitField, true);
        enqueueMessage(testMessage, 1);
        assertThat(scheduler.size()).named("scheduler_size").isEqualTo(0);
    }

    @Test
    public void enqueuedMessage_isSentToHandler() {
        enqueueMessage(testMessage, 200);
        scheduler.advanceTo(199);
        assertThat(handler.handled).named("handled:before").isEmpty();
        scheduler.advanceTo(200);
        assertThat(handler.handled).named("handled:after").containsExactly(testMessage);
    }

    @Test
    public void removedMessage_isNotSentToHandler() {
        enqueueMessage(testMessage, 200);
        assertThat(scheduler.size()).named("scheduler size:before").isEqualTo(1);
        removeMessages(handler, testMessage.what, null);
        scheduler.advanceToLastPostedRunnable();
        assertThat(scheduler.size()).named("scheduler size:after").isEqualTo(0);
        assertThat(handler.handled).named("handled").isEmpty();
    }

    @Test
    public void enqueueMessage_withZeroWhen_postsAtFront() {
        enqueueMessage(testMessage, 0);
        Message m2 = obtainMessage(2);
        enqueueMessage(m2, 0);
        scheduler.advanceToLastPostedRunnable();
        assertThat(handler.handled).named("handled").containsExactly(m2, testMessage);
    }

    @Test
    public void dispatchedMessage_isMarkedInUse_andRecycled() {
        Handler handler = new Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                boolean inUse = ReflectionHelpers.callInstanceMethod(msg, "isInUse");
                assertThat(inUse).named(((msg.what) + ":inUse")).isTrue();
                Message next = reflector(_Message_.class, msg).getNext();
                assertThat(next).named(((msg.what) + ":next")).isNull();
            }
        };
        Message msg = handler.obtainMessage(1);
        enqueueMessage(msg, 200);
        Message msg2 = handler.obtainMessage(2);
        enqueueMessage(msg2, 205);
        scheduler.advanceToNextPostedRunnable();
        // Check that it's been properly recycled.
        assertThat(msg.what).named("msg.what").isEqualTo(0);
        scheduler.advanceToNextPostedRunnable();
        assertThat(msg2.what).named("msg2.what").isEqualTo(0);
    }

    @Test
    public void reset_shouldClearMessageQueue() {
        Message msg = obtainMessage(1234);
        Message msg2 = obtainMessage(5678);
        handler.sendMessage(msg);
        handler.sendMessage(msg2);
        assertThat(hasMessages(1234)).named("before-1234").isTrue();
        assertThat(hasMessages(5678)).named("before-5678").isTrue();
        shadowQueue.reset();
        assertThat(hasMessages(1234)).named("after-1234").isFalse();
        assertThat(hasMessages(5678)).named("after-5678").isFalse();
    }

    @Test
    public void postAndRemoveSyncBarrierToken() {
        int token = ShadowMessageQueueTest.postSyncBarrier(queue);
        ShadowMessageQueueTest.removeSyncBarrier(queue, token);
    }

    @Test
    public void postAndRemoveSyncBarrierToken_messageBefore() {
        enqueueMessage(testMessage, SystemClock.uptimeMillis());
        int token = ShadowMessageQueueTest.postSyncBarrier(queue);
        ShadowMessageQueueTest.removeSyncBarrier(queue, token);
        assertThat(shadowQueue.getHead()).isEqualTo(testMessage);
    }

    @Test
    public void postAndRemoveSyncBarrierToken_messageBeforeConsumed() {
        enqueueMessage(testMessage, SystemClock.uptimeMillis());
        int token = ShadowMessageQueueTest.postSyncBarrier(queue);
        scheduler.advanceToLastPostedRunnable();
        ShadowMessageQueueTest.removeSyncBarrier(queue, token);
        assertThat(shadowQueue.getHead()).isNull();
        assertThat(handler.handled).named("handled:after").containsExactly(testMessage);
    }

    @Test
    public void postAndRemoveSyncBarrierToken_messageAfter() {
        enqueueMessage(testMessage, ((SystemClock.uptimeMillis()) + 100));
        int token = ShadowMessageQueueTest.postSyncBarrier(queue);
        ShadowMessageQueueTest.removeSyncBarrier(queue, token);
        assertThat(shadowQueue.getHead()).isEqualTo(testMessage);
        scheduler.advanceToLastPostedRunnable();
        assertThat(shadowQueue.getHead()).isNull();
        assertThat(handler.handled).named("handled:after").containsExactly(testMessage);
    }

    @Test
    public void postAndRemoveSyncBarrierToken_syncBefore() {
        int token = ShadowMessageQueueTest.postSyncBarrier(queue);
        enqueueMessage(testMessage, SystemClock.uptimeMillis());
        scheduler.advanceToLastPostedRunnable();
        ShadowMessageQueueTest.removeSyncBarrier(queue, token);
        assertThat(shadowQueue.getHead()).isNull();
        assertThat(handler.handled).named("handled:after").containsExactly(testMessage);
    }
}

