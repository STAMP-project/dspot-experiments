package org.nd4j.linalg.crash;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
@RunWith(Parameterized.class)
public class SpecialTests extends BaseNd4jTest {
    public SpecialTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testDimensionalThings1() {
        INDArray x = Nd4j.rand(new int[]{ 20, 30, 50 });
        INDArray y = Nd4j.rand(x.shape());
        INDArray result = SpecialTests.transform(x, y);
    }

    @Test
    public void testDimensionalThings2() {
        INDArray x = Nd4j.rand(new int[]{ 20, 30, 50 });
        INDArray y = Nd4j.rand(x.shape());
        for (int i = 0; i < 1; i++) {
            int number = 5;
            int start = RandomUtils.nextInt(0, (((int) (x.shape()[2])) - number));
            SpecialTests.transform(SpecialTests.getView(x, start, 5), SpecialTests.getView(y, start, 5));
        }
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testScalarShuffle1() throws Exception {
        List<DataSet> listData = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            INDArray features = Nd4j.ones(25, 25);
            INDArray label = Nd4j.create(new float[]{ 1 }, new int[]{ 1 });
            DataSet dataset = new DataSet(features, label);
            listData.add(dataset);
        }
        DataSet data = DataSet.merge(listData);
        data.shuffle();
    }

    @Test
    public void testScalarShuffle2() throws Exception {
        List<DataSet> listData = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            INDArray features = Nd4j.ones(14, 25);
            INDArray label = Nd4j.create(14, 50);
            DataSet dataset = new DataSet(features, label);
            listData.add(dataset);
        }
        DataSet data = DataSet.merge(listData);
        data.shuffle();
    }

    @Test
    public void testVstack2() throws Exception {
        INDArray matrix = Nd4j.create(10000, 100);
        List<INDArray> views = new ArrayList<>();
        views.add(matrix.getRow(1));
        views.add(matrix.getRow(4));
        views.add(matrix.getRow(7));
        INDArray result = Nd4j.vstack(views);
    }

    @Test
    public void testVstack1() throws Exception {
        INDArray matrix = Nd4j.create(10000, 100);
        List<INDArray> views = new ArrayList<>();
        for (int i = 0; i < ((matrix.rows()) / 2); i++) {
            views.add(matrix.getRow(RandomUtils.nextInt(0, ((int) (matrix.rows())))));
            // views.add(Nd4j.create(1, 10));
        }
        log.info("Starting...");
        // while (true) {
        for (int i = 0; i < 1; i++) {
            INDArray result = Nd4j.vstack(views);
            System.gc();
        }
    }

    @Test
    public void testConcatMulti() throws Exception {
        val shapeA = new int[]{ 50, 20 };
        val shapeB = new int[]{ 50, 497 };
        // Nd4j.create(1);
        val executor = ((ThreadPoolExecutor) (Executors.newFixedThreadPool(2)));
        for (int e = 0; e < 1; e++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    val arrayA = Nd4j.createUninitialized(shapeA);
                }
            });
        }
        Thread.sleep(1000);
    }

    @Test
    public void testConcatMulti2() throws Exception {
        Nd4j.create(1);
        val executor = ((ThreadPoolExecutor) (Executors.newFixedThreadPool(2)));
        executor.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("A");
            }
        });
    }
}

