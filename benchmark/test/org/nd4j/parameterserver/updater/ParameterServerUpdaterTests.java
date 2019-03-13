package org.nd4j.parameterserver.updater;


import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.nd4j.aeron.ipc.NDArrayMessage;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.parameterserver.updater.storage.NoUpdateStorage;


/**
 * Created by agibsonccc on 12/2/16.
 */
public class ParameterServerUpdaterTests {
    @Test
    public void synchronousTest() {
        int cores = Runtime.getRuntime().availableProcessors();
        ParameterServerUpdater updater = new SynchronousParameterUpdater(new NoUpdateStorage(), new org.nd4j.aeron.ndarrayholder.InMemoryNDArrayHolder(Nd4j.zeros(2, 2)), cores);
        for (int i = 0; i < cores; i++) {
            updater.update(NDArrayMessage.wholeArrayUpdate(Nd4j.ones(2, 2)));
        }
        Assert.assertTrue(updater.shouldReplicate());
        updater.reset();
        Assert.assertFalse(updater.shouldReplicate());
        Assume.assumeNotNull(updater.toJson());
    }
}

