package org.nd4j.linalg.dataset;


import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 * Created by agibsonccc on 9/10/15.
 */
@RunWith(Parameterized.class)
public class MiniBatchFileDataSetIteratorTest extends BaseNd4jTest {
    public MiniBatchFileDataSetIteratorTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testMiniBatches() throws Exception {
        DataSet load = new IrisDataSetIterator(150, 150).next();
        final MiniBatchFileDataSetIterator iter = new MiniBatchFileDataSetIterator(load, 10, false);
        while (iter.hasNext())
            Assert.assertEquals(10, iter.next().numExamples());

        if ((iter.getRootDir()) == null)
            return;

        DataSetIterator existing = new ExistingMiniBatchDataSetIterator(iter.getRootDir());
        while (iter.hasNext())
            Assert.assertEquals(10, existing.next().numExamples());

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileUtils.deleteDirectory(iter.getRootDir());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
    }
}

