package com.zendesk.maxwell.producer;


import InflightMessageList.InflightMessage;
import com.zendesk.maxwell.MaxwellContext;
import com.zendesk.maxwell.replication.BinlogPosition;
import com.zendesk.maxwell.replication.Position;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Created by ben on 5/25/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class InflightMessageListTest {
    private static int capacity = 3;

    private static Position p1 = new Position(BinlogPosition.at(1, "f"), 0L);

    private static Position p2 = new Position(BinlogPosition.at(2, "f"), 0L);

    private static Position p3 = new Position(BinlogPosition.at(3, "f"), 0L);

    private static Position p4 = new Position(BinlogPosition.at(4, "f"), 0L);

    private InflightMessageList list;

    private MaxwellContext context;

    @Captor
    private ArgumentCaptor<RuntimeException> captor;

    @Test
    public void testInOrderCompletion() throws InterruptedException {
        setupWithInflightRequestTimeout(0);
        Position ret;
        ret = list.completeMessage(InflightMessageListTest.p1).position;
        assert ret.equals(InflightMessageListTest.p1);
        ret = list.completeMessage(InflightMessageListTest.p2).position;
        assert ret.equals(InflightMessageListTest.p2);
        ret = list.completeMessage(InflightMessageListTest.p3).position;
        assert ret.equals(InflightMessageListTest.p3);
        assert (list.size()) == 0;
    }

    @Test
    public void testOutOfOrderComplete() throws InterruptedException {
        setupWithInflightRequestTimeout(0);
        Position ret;
        InflightMessageList.InflightMessage m;
        m = list.completeMessage(InflightMessageListTest.p3);
        assert m == null;
        m = list.completeMessage(InflightMessageListTest.p2);
        assert m == null;
        ret = list.completeMessage(InflightMessageListTest.p1).position;
        Assert.assertEquals(InflightMessageListTest.p3, ret);
    }

    @Test
    public void testMaxwellWillTerminateWhenHeadOfInflightMsgListIsStuckAndCheckTurnedOn() throws InterruptedException {
        // Given
        long inflightRequestTimeout = 100;
        setupWithInflightRequestTimeout(inflightRequestTimeout);
        list.completeMessage(InflightMessageListTest.p2);
        list.freeSlot(2);
        Thread.sleep((inflightRequestTimeout + 5));
        // When
        list.completeMessage(InflightMessageListTest.p3);
        list.freeSlot(3);
        // Then
        Mockito.verify(context).terminate(captor.capture());
        Assert.assertThat(captor.getValue().getMessage(), Matchers.is((("Did not receive acknowledgement for the head of the inflight message list for " + inflightRequestTimeout) + " ms")));
    }

    @Test
    public void testMaxwellWillNotTerminateWhenHeadOfInflightMsgListIsStuckAndCheckTurnedOff() throws InterruptedException {
        // Given
        setupWithInflightRequestTimeout(0);
        list.completeMessage(InflightMessageListTest.p2);
        list.freeSlot(2);
        // When
        list.completeMessage(InflightMessageListTest.p3);
        list.freeSlot(3);
        // Then
        Mockito.verify(context, Mockito.never()).terminate(ArgumentMatchers.any(RuntimeException.class));
    }

    @Test
    public void testWaitForSlotWillWaitWhenCapacityIsFull() throws InterruptedException {
        setupWithInflightRequestTimeout(0);
        InflightMessageListTest.AddMessage addMessage = new InflightMessageListTest.AddMessage();
        Thread add = new Thread(addMessage);
        add.start();
        Assert.assertThat("Should never exceed capacity", list.size(), Matchers.is(InflightMessageListTest.capacity));
        long wait = 500;
        Thread.sleep((wait + 100));
        list.completeMessage(InflightMessageListTest.p1);
        list.freeSlot(1);
        add.join();
        long elapse = (addMessage.end) - (addMessage.start);
        Assert.assertThat("Should have waited message to be completed", elapse, Matchers.greaterThanOrEqualTo(wait));
    }

    class AddMessage implements Runnable {
        long start;

        long end;

        @Override
        public void run() {
            start = System.currentTimeMillis();
            try {
                list.waitForSlot();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            end = System.currentTimeMillis();
        }
    }
}

