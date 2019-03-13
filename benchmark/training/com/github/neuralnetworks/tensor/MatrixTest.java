package com.github.neuralnetworks.tensor;


import com.github.neuralnetworks.test.AbstractTest;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.RuntimeConfiguration;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Created by chass on 18.11.14.
 */
@RunWith(Parameterized.class)
public class MatrixTest extends AbstractTest {
    public MatrixTest(RuntimeConfiguration conf) {
        super();
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    @Test
    public void testMatrix01() {
        int[] globalDimensions = new int[2];
        // 100 rows, 10 columns
        globalDimensions[0] = 100;
        globalDimensions[1] = 10;
        int[][] globalDimensionsLimit = new int[2][2];
        globalDimensionsLimit[0][0] = 0;
        globalDimensionsLimit[1][0] = 9;
        globalDimensionsLimit[1][1] = 9;
        globalDimensionsLimit[0][1] = 0;
        float[] elements = new float[1000];
        elements[0] = 0.9999999F;
        elements[1] = 2.0E-8F;
        for (int i = 1; i < 10; i++) {
            elements[i] = 1.0E-8F;
        }
    }

    @Test
    public void testMatrix() {
        Matrix m = TensorFactory.tensor(5, 6);
        Assert.assertEquals(5, m.getRows(), 0);
        Assert.assertEquals(6, m.getColumns(), 0);
        Assert.assertEquals(6, m.getDimensionElementsDistance(0), 0);
        Assert.assertEquals(1, m.getDimensionElementsDistance(1), 0);
        Assert.assertEquals(5, m.getDimensions()[0], 0);
        Assert.assertEquals(6, m.getDimensions()[1], 0);
        for (int i = 0; i < (m.getElements().length); i++) {
            m.getElements()[i] = i + 1;
        }
        Assert.assertEquals(2, m.get(0, 1), 0);
        Assert.assertEquals(15, m.get(2, 2), 0);
        m = TensorFactory.tensor(1, 6);
        for (int i = 0; i < (m.getElements().length); i++) {
            m.getElements()[i] = i + 1;
        }
        Assert.assertEquals(2, m.get(0, 1), 0);
        Assert.assertEquals(6, m.get(0, 5), 0);
        m = TensorFactory.tensor(6, 1);
        for (int i = 0; i < (m.getElements().length); i++) {
            m.getElements()[i] = i + 1;
        }
        Assert.assertEquals(2, m.get(1, 0), 0);
        Assert.assertEquals(6, m.get(5, 0), 0);
        // submatrix
        Tensor t = TensorFactory.tensor(5, 5, 5);
        float[] elements = t.getElements();
        for (int i = 0; i < (elements.length); i++) {
            elements[i] = i + 1;
        }
        m = TensorFactory.tensor(t, new int[][]{ new int[]{ 1, 0, 0 }, new int[]{ 1, 4, 4 } }, true);
        Assert.assertEquals(26, m.get(0, 0), 0);
        Assert.assertEquals(27, m.get(0, 1), 0);
        Assert.assertEquals(36, m.get(2, 0), 0);
        Assert.assertEquals(38, m.get(2, 2), 0);
        m = TensorFactory.tensor(t, new int[][]{ new int[]{ 1, 0, 0 }, new int[]{ 1, 4, 4 } }, true);
        Assert.assertEquals(26, m.get(0, 0), 0);
        Assert.assertEquals(27, m.get(0, 1), 0);
        Assert.assertEquals(36, m.get(2, 0), 0);
        Assert.assertEquals(38, m.get(2, 2), 0);
        m = TensorFactory.tensor(t, new int[][]{ new int[]{ 0, 0, 1 }, new int[]{ 4, 4, 1 } }, true);
        Assert.assertEquals(2, m.get(0, 0), 0);
        Assert.assertEquals(7, m.get(0, 1), 0);
        Assert.assertEquals(12, m.get(0, 2), 0);
        Assert.assertEquals(27, m.get(1, 0), 0);
        Assert.assertEquals(32, m.get(1, 1), 0);
        Assert.assertEquals(37, m.get(1, 2), 0);
        m = TensorFactory.tensor(t, new int[][]{ new int[]{ 2, 2, 1 }, new int[]{ 3, 3, 1 } }, true);
        Assert.assertEquals(62, m.get(0, 0), 0);
        Assert.assertEquals(67, m.get(0, 1), 0);
        Assert.assertEquals(92, m.get(1, 1), 0);
        Iterator<Integer> it = m.iterator();
        Assert.assertEquals(62, m.getElements()[it.next()], 0);
        Assert.assertEquals(67, m.getElements()[it.next()], 0);
        it.next();
        Assert.assertEquals(92, m.getElements()[it.next()], 0);
        it = m.iterator(new int[][]{ new int[]{ 1, 0 }, new int[]{ 1, 1 } });
        it.next();
        Assert.assertEquals(92, m.getElements()[it.next()], 0);
        m = TensorFactory.tensor(4, 4);
        for (int i = 0; i < (m.getElements().length); i++) {
            m.getElements()[i] = i + 1;
        }
        Matrix m2 = TensorFactory.tensor(m, new int[][]{ new int[]{ 1, 1 }, new int[]{ 2, 2 } }, true);
        Assert.assertEquals(6, m2.get(0, 0), 0);
        Assert.assertEquals(7, m2.get(0, 1), 0);
        Assert.assertEquals(10, m2.get(1, 0), 0);
        Assert.assertEquals(11, m2.get(1, 1), 0);
    }
}

