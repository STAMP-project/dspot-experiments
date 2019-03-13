package org.nd4j.parameterserver.updater.storage;


import junit.framework.TestCase;
import org.junit.Test;
import org.nd4j.aeron.ipc.NDArrayMessage;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Created by agibsonccc on 12/2/16.
 */
public class UpdaterStorageTests {
    @Test
    public void testInMemory() {
        UpdateStorage updateStorage = new RocksDbStorage("/tmp/rocksdb");
        NDArrayMessage message = NDArrayMessage.wholeArrayUpdate(Nd4j.scalar(1.0));
        updateStorage.addUpdate(message);
        TestCase.assertEquals(1, updateStorage.numUpdates());
        TestCase.assertEquals(message, updateStorage.getUpdate(0));
        updateStorage.clear();
        TestCase.assertEquals(0, updateStorage.numUpdates());
        updateStorage.close();
    }
}

