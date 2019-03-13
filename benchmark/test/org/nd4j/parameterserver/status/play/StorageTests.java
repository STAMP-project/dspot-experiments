package org.nd4j.parameterserver.status.play;


import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.parameterserver.model.SubscriberState;


/**
 * Created by agibsonccc on 12/1/16.
 */
public class StorageTests {
    @Test
    public void testMapStorage() throws Exception {
        StatusStorage mapDb = new MapDbStatusStorage();
        TestCase.assertEquals(SubscriberState.empty(), mapDb.getState((-1)));
        SubscriberState noEmpty = SubscriberState.builder().isMaster(true).serverState("master").streamId(1).build();
        mapDb.updateState(noEmpty);
        TestCase.assertEquals(noEmpty, mapDb.getState(1));
        Thread.sleep(10000);
        Assert.assertTrue(((mapDb.numStates()) == 0));
    }

    @Test
    public void testStorage() throws Exception {
        StatusStorage statusStorage = new InMemoryStatusStorage();
        TestCase.assertEquals(SubscriberState.empty(), statusStorage.getState((-1)));
        SubscriberState noEmpty = SubscriberState.builder().isMaster(true).serverState("master").streamId(1).build();
        statusStorage.updateState(noEmpty);
        TestCase.assertEquals(noEmpty, statusStorage.getState(1));
        Thread.sleep(10000);
        Assert.assertTrue(((statusStorage.numStates()) == 0));
    }
}

