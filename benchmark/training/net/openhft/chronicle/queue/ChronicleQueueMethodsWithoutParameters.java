package net.openhft.chronicle.queue;


import java.io.File;
import net.openhft.chronicle.bytes.MethodReader;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created on 19.10.2016.
 */
public class ChronicleQueueMethodsWithoutParameters extends ChronicleQueueTestBase {
    protected static final Logger LOG = LoggerFactory.getLogger(ChronicleQueueMethodsWithoutParameters.class);

    @Test
    public void test() {
        File file = getTmpDir();
        try (ChronicleQueue queue = ChronicleQueue.singleBuilder(file).testBlockSize().rollCycle(RollCycles.TEST_DAILY).build()) {
            ChronicleQueueMethodsWithoutParameters.SomeListener someListener = queue.acquireAppender().methodWriter(ChronicleQueueMethodsWithoutParameters.SomeListener.class);
            ChronicleQueueMethodsWithoutParameters.SomeManager someManager = new ChronicleQueueMethodsWithoutParameters.SomeManager();
            MethodReader reader = queue.createTailer().methodReader(someManager);
            ChronicleQueueMethodsWithoutParameters.LOG.debug("Writing to queue");
            someListener.methodWithOneParam(1);
            someListener.methodWithoutParams();
            ChronicleQueueMethodsWithoutParameters.LOG.debug("Reading from queue");
            Assert.assertTrue(reader.readOne());
            Assert.assertTrue(reader.readOne());
            Assert.assertFalse(reader.readOne());
            Assert.assertTrue(someManager.methodWithOneParamInvoked);// one param method was invoked

            Assert.assertTrue(someManager.methodWithoutParamsInvoked);// no params method was NOT invoked

            ChronicleQueueMethodsWithoutParameters.LOG.warn(queue.dump());
        }
    }

    public interface SomeListener {
        void methodWithoutParams();

        void methodWithOneParam(int i);
    }

    public static class SomeManager implements ChronicleQueueMethodsWithoutParameters.SomeListener {
        public boolean methodWithoutParamsInvoked = false;

        public boolean methodWithOneParamInvoked = false;

        @Override
        public void methodWithoutParams() {
            methodWithoutParamsInvoked = true;
        }

        @Override
        public void methodWithOneParam(int i) {
            methodWithOneParamInvoked = true;
        }
    }
}

