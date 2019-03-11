package com.github.neuralnetworks.tensor;


import Tensor.TensorIterator;
import com.github.neuralnetworks.test.AbstractTest;
import com.github.neuralnetworks.util.Environment;
import com.github.neuralnetworks.util.RuntimeConfiguration;
import java.util.Iterator;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Created by chass on 18.11.14.
 */
@RunWith(Parameterized.class)
public class TensorTest extends AbstractTest {
    public TensorTest(RuntimeConfiguration conf) {
        super();
        Environment.getInstance().setRuntimeConfiguration(conf);
    }

    @Test
    public void testTensorOffset() {
        // 2x2 unused tensor + a 5x5 parent tensor
        float[] elements = new float[]{ 1.1F, 2.1F, 3.1F, 4.1F, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };
        int offset = 4;
        int[] globalDimensions = new int[]{ 5, 5 };
        // upper left, and lower right index of the global dimention
        // parent 3x3 matrix
        Tensor tensor = new Tensor(offset, elements, globalDimensions, new int[][]{ new int[]{ 0, 0 }, new int[]{ 4, 4 } });
        Assert.assertEquals(2, tensor.getDimensions().length);
        Assert.assertEquals(5, tensor.getDimensions()[0]);
        Assert.assertEquals(5, tensor.getDimensions()[1]);
        Assert.assertEquals(offset, tensor.getStartIndex());
        // check parent values
        int count = 1;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Assert.assertEquals(count, tensor.get(i, j), 0);
                count++;
            }
        }
    }

    @Test
    public void testSubTensor() {
        // 5x5 parent tensor
        float[] elements = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };
        int offset = 0;
        // parent 3x3 matrix
        Tensor parent = new Tensor(offset, elements, new int[]{ 5, 5 }, new int[][]{ new int[]{ 0, 0 }, new int[]{ 4, 4 } });
        Assert.assertEquals(2, parent.getDimensions().length);
        Assert.assertEquals(5, parent.getDimensions()[0]);
        Assert.assertEquals(5, parent.getDimensions()[1]);
        Assert.assertEquals(offset, parent.getStartIndex());
        // check parent values
        int count = 1;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Assert.assertEquals(count, parent.get(i, j), 0);
                count++;
            }
        }
        // create suptensor manually which is 3x3 matix in the middel of the 5x5 ,matrix, i.e. offsets 1,1 to 3,3
        Tensor sub1 = new Tensor(parent, new int[][]{ new int[]{ 1, 1 }, new int[]{ 3, 3 } }, false);
        Assert.assertEquals(2, sub1.getDimensions().length);
        Assert.assertEquals(3, sub1.getDimensions()[0]);
        Assert.assertEquals(3, sub1.getDimensions()[1]);
        Assert.assertEquals(6, sub1.getStartIndex());
        Assert.assertEquals(18, sub1.getEndIndex());
        // check values
        Assert.assertEquals(7, sub1.get(0, 0), 0);
        Assert.assertEquals(8, sub1.get(0, 1), 0);
        Assert.assertEquals(9, sub1.get(0, 2), 0);
        Assert.assertEquals(12, sub1.get(1, 0), 0);
        Assert.assertEquals(13, sub1.get(1, 1), 0);
        Assert.assertEquals(14, sub1.get(1, 2), 0);
        Assert.assertEquals(17, sub1.get(2, 0), 0);
        Assert.assertEquals(18, sub1.get(2, 1), 0);
        Assert.assertEquals(19, sub1.get(2, 2), 0);
        // create suptensor manually which is 3x3 matix in the upper left corner of the 5x5 ,matrix, i.e. offsets 0,0 to 2,2
        Tensor sub2 = new Tensor(parent, new int[][]{ new int[]{ 0, 0 }, new int[]{ 2, 2 } }, false);
        Assert.assertEquals(2, sub2.getDimensions().length);
        Assert.assertEquals(3, sub2.getDimensions()[0]);
        Assert.assertEquals(3, sub2.getDimensions()[1]);
        Assert.assertEquals(0, sub2.getStartIndex());
        Assert.assertEquals(12, sub2.getEndIndex());
        // check values
        Assert.assertEquals(1, sub2.get(0, 0), 0);
        Assert.assertEquals(2, sub2.get(0, 1), 0);
        Assert.assertEquals(3, sub2.get(0, 2), 0);
        Assert.assertEquals(6, sub2.get(1, 0), 0);
        Assert.assertEquals(7, sub2.get(1, 1), 0);
        Assert.assertEquals(8, sub2.get(1, 2), 0);
        Assert.assertEquals(11, sub2.get(2, 0), 0);
        Assert.assertEquals(12, sub2.get(2, 1), 0);
        Assert.assertEquals(13, sub2.get(2, 2), 0);
    }

    @Test
    public void testSubTensorManually01() {
        // 5x5 parent tensor
        float[] elements = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };
        int offset = 0;
        // create suptensor manually which is 3x3 matix in the middel of the 5x5 ,matrix, i.e. offsets 1,1 to 3,3
        Tensor sub2 = new Tensor(offset, elements, new int[]{ 5, 5 }, new int[][]{ new int[]{ 1, 1 }, new int[]{ 3, 3 } });
        Assert.assertEquals(2, sub2.getDimensions().length);
        Assert.assertEquals(3, sub2.getDimensions()[0]);
        Assert.assertEquals(3, sub2.getDimensions()[1]);
        Assert.assertEquals(6, sub2.getStartIndex());
        Assert.assertEquals(18, sub2.getEndIndex());
        // check values
        Assert.assertEquals(7, sub2.get(0, 0), 0);
        Assert.assertEquals(8, sub2.get(0, 1), 0);
        Assert.assertEquals(9, sub2.get(0, 2), 0);
        Assert.assertEquals(12, sub2.get(1, 0), 0);
        Assert.assertEquals(13, sub2.get(1, 1), 0);
        Assert.assertEquals(14, sub2.get(1, 2), 0);
        Assert.assertEquals(17, sub2.get(2, 0), 0);
        Assert.assertEquals(18, sub2.get(2, 1), 0);
        Assert.assertEquals(19, sub2.get(2, 2), 0);
    }

    @Test
    public void testSubTensorManually02() {
        // 5x5 parent tensor
        float[] elements = new float[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };
        int offset = 0;
        // create suptensor manually which is 3x3 matix in the upper left corner of the 5x5 ,matrix, i.e. offsets 0,0 to 2,2
        Tensor sub2 = new Tensor(offset, elements, new int[]{ 5, 5 }, new int[][]{ new int[]{ 0, 0 }, new int[]{ 2, 2 } });
        Assert.assertEquals(2, sub2.getDimensions().length);
        Assert.assertEquals(3, sub2.getDimensions()[0]);
        Assert.assertEquals(3, sub2.getDimensions()[1]);
        Assert.assertEquals(0, sub2.getStartIndex());
        Assert.assertEquals(12, sub2.getEndIndex());
        // check values
        Assert.assertEquals(1, sub2.get(0, 0), 0);
        Assert.assertEquals(2, sub2.get(0, 1), 0);
        Assert.assertEquals(3, sub2.get(0, 2), 0);
        Assert.assertEquals(6, sub2.get(1, 0), 0);
        Assert.assertEquals(7, sub2.get(1, 1), 0);
        Assert.assertEquals(8, sub2.get(1, 2), 0);
        Assert.assertEquals(11, sub2.get(2, 0), 0);
        Assert.assertEquals(12, sub2.get(2, 1), 0);
        Assert.assertEquals(13, sub2.get(2, 2), 0);
    }

    @Test
    public void testTensorGetElements() {
        Tensor tensor = TensorFactory.tensor(3, 3, 3, 3);
        int count = 0;
        for (int a = 0; a < 3; a++) {
            for (int b = 0; b < 3; b++) {
                for (int c = 0; c < 3; c++) {
                    for (int d = 0; d < 3; d++) {
                        tensor.set(count, a, b, c, d);
                        count++;
                    }
                }
            }
        }
        Assert.assertEquals(81, tensor.getSize());
        float[] elements = tensor.getElements();
        Assert.assertEquals(81, elements.length);
        for (int i = 0; i < 81; i++) {
            Assert.assertEquals(i, elements[i], 0);
        }
    }

    @Test
    public void testTensorAccess() {
        Tensor tensor = TensorFactory.tensor(3, 3, 3, 3);
        int count = 0;
        for (int a = 0; a < 3; a++) {
            for (int b = 0; b < 3; b++) {
                for (int c = 0; c < 3; c++) {
                    for (int d = 0; d < 3; d++) {
                        tensor.set(count, a, b, c, d);
                        count++;
                    }
                }
            }
        }
        count = 0;
        for (int a = 0; a < 3; a++) {
            for (int b = 0; b < 3; b++) {
                for (int c = 0; c < 3; c++) {
                    for (int d = 0; d < 3; d++) {
                        Assert.assertEquals(count, tensor.get(a, b, c, d), 0);
                        count++;
                    }
                }
            }
        }
    }

    @Test
    public void testTensorIndex() {
        float[] elements = new float[626];
        Tensor tensor = TensorFactory.tensor(elements, 1, 5, 5, 5, 5);
        Assert.assertEquals(1, tensor.getStartIndex());
        Assert.assertEquals(625, tensor.getEndIndex());
    }

    @Test
    public void testTensorDimentionDistance() {
        // second index would be 0 0 0 1 -> the values of the last dimension are 1 afar
        Tensor tensor = TensorFactory.tensor(5, 5, 5, 5);
        Assert.assertEquals(125, tensor.getDimensionElementsDistance(0));
        Assert.assertEquals(25, tensor.getDimensionElementsDistance(1));
        Assert.assertEquals(5, tensor.getDimensionElementsDistance(2));
        Assert.assertEquals(1, tensor.getDimensionElementsDistance(3));
    }

    @Test
    public void testTensorIterator() {
        Tensor tensor = TensorFactory.tensor(2, 2, 2, 2);
        float[] elements = new float[20];
        for (int i = 0; i < (elements.length); i++)
            elements[i] = i;

        tensor.setElements(elements);
        tensor.setStartOffset(4);
        // iterrates over real indexes, i.e. should start with offset
        Tensor.TensorIterator iterator = tensor.iterator();
        int count = 4;
        while (iterator.hasNext()) {
            Integer index = iterator.next();
            Assert.assertNotNull(index);
            Assert.assertEquals(count, index.intValue());
            count++;
        } 
    }

    @Test
    public void testTensorIterator2() {
        Tensor tensor = TensorFactory.tensor(5, 5);
        float[] elements = new float[25];
        for (int i = 0; i < (elements.length); i++)
            elements[i] = i;

        tensor.setElements(elements);
        // iterrates over real indexes, i.e. should start with offset
        Tensor.TensorIterator iterator = tensor.iterator(new int[][]{ new int[]{ 2, 2 }, new int[]{ 3, 3 } });
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(12, iterator.next().intValue());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(13, iterator.next().intValue());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(17, iterator.next().intValue());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(18, iterator.next().intValue());
        Assert.assertTrue((!(iterator.hasNext())));
    }

    @Test
    public void testGetIndex() {
        // 5x5 parent tensor
        float[] elements = new float[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 };
        int offset = 0;
        // parent 3x3 matrix
        Tensor parent = new Tensor(offset, elements, new int[]{ 5, 5 }, new int[][]{ new int[]{ 0, 0 }, new int[]{ 4, 4 } });
        Assert.assertEquals(2, parent.getDimensions().length);
        Assert.assertEquals(5, parent.getDimensions()[0]);
        Assert.assertEquals(5, parent.getDimensions()[1]);
        Assert.assertEquals(offset, parent.getStartIndex());
        // create suptensor manually which is 3x3 matix in the middel of the 5x5 ,matrix, i.e. offsets 1,1 to 3,3
        Tensor subtensor = new Tensor(parent, new int[][]{ new int[]{ 1, 1 }, new int[]{ 3, 3 } }, false);
        Assert.assertEquals(6, subtensor.getIndex(0, 0));
        Assert.assertEquals(7, subtensor.getIndex(0, 1));
        Assert.assertEquals(8, subtensor.getIndex(0, 2));
        Assert.assertEquals(11, subtensor.getIndex(1, 0));
        Assert.assertEquals(12, subtensor.getIndex(1, 1));
        Assert.assertEquals(13, subtensor.getIndex(1, 2));
        Assert.assertEquals(16, subtensor.getIndex(2, 0));
        Assert.assertEquals(17, subtensor.getIndex(2, 1));
        Assert.assertEquals(18, subtensor.getIndex(2, 2));
    }

    @Test
    public void testTensorComplex() {
        Tensor t = TensorFactory.tensor(2, 2, 2);
        float[] elements = t.getElements();
        Assert.assertEquals(8, elements.length, 0);
        t.set(1, 0, 0, 0);
        t.set(2, 0, 0, 1);
        t.set(3, 0, 1, 0);
        t.set(4, 0, 1, 1);
        t.set(5, 1, 0, 0);
        t.set(6, 1, 0, 1);
        t.set(7, 1, 1, 0);
        t.set(8, 1, 1, 1);
        Iterator<Integer> it = t.iterator();
        for (int i = 0; (i < (elements.length)) && (it.hasNext()); i++) {
            Assert.assertEquals((i + 1), elements[i], 0);
            Assert.assertEquals((i + 1), elements[it.next()], 0);
        }
        t = TensorFactory.tensor(5, 5, 5);
        Assert.assertEquals(25, t.getDimensionElementsDistance(0), 0);
        Assert.assertEquals(5, t.getDimensionElementsDistance(1), 0);
        Assert.assertEquals(1, t.getDimensionElementsDistance(2), 0);
        elements = t.getElements();
        for (int i = 0; i < (elements.length); i++) {
            elements[i] = i + 1;
        }
        Tensor t2 = TensorFactory.tensor(t, new int[][]{ new int[]{ 3, 0, 0 }, new int[]{ 4, 4, 4 } }, true);
        Assert.assertEquals(75, t2.getStartIndex(), 0);
        Assert.assertEquals(124, t2.getEndIndex(), 0);
        Assert.assertEquals(25, t2.getDimensionElementsDistance(0), 0);
        Assert.assertEquals(5, t2.getDimensionElementsDistance(1), 0);
        Assert.assertEquals(1, t2.getDimensionElementsDistance(2), 0);
        Assert.assertEquals(50, t2.getSize(), 0);
        Assert.assertEquals(76, t2.get(0, 0, 0), 0);
        Assert.assertEquals(77, t2.get(0, 0, 1), 0);
        Assert.assertEquals(81, t2.get(0, 1, 0), 0);
        Assert.assertEquals(101, t2.get(1, 0, 0), 0);
        Assert.assertEquals(106, t2.get(1, 1, 0), 0);
        Assert.assertEquals(112, t2.get(1, 2, 1), 0);
        Tensor[] tarr = TensorFactory.tensor(new int[]{ 2, 2, 2 }, new int[]{ 3, 3 });
        Assert.assertEquals(17, tarr[0].getElements().length, 0);
        Assert.assertEquals(0, tarr[0].getStartOffset(), 0);
        Assert.assertEquals(8, tarr[1].getStartOffset(), 0);
        Assert.assertTrue(((tarr[1]) instanceof Matrix));
        IntStream.range(0, tarr[0].getElements().length).forEach(( i) -> tarr[0].getElements()[i] = i + 1);
        Assert.assertEquals(7, tarr[0].get(1, 1, 0), 0);
        Assert.assertEquals(13, tarr[1].get(1, 1), 0);
    }

    // from GeneralTests
    @Test
    public void testTensor() {
        Tensor t = TensorFactory.tensor(2, 2, 2);
        float[] elements = t.getElements();
        Assert.assertEquals(8, elements.length, 0);
        t.set(1, 0, 0, 0);
        t.set(2, 0, 0, 1);
        t.set(3, 0, 1, 0);
        t.set(4, 0, 1, 1);
        t.set(5, 1, 0, 0);
        t.set(6, 1, 0, 1);
        t.set(7, 1, 1, 0);
        t.set(8, 1, 1, 1);
        Iterator<Integer> it = t.iterator();
        for (int i = 0; (i < (elements.length)) && (it.hasNext()); i++) {
            Assert.assertEquals((i + 1), elements[i], 0);
            Assert.assertEquals((i + 1), elements[it.next()], 0);
        }
        t = TensorFactory.tensor(5, 5, 5);
        Assert.assertEquals(25, t.getDimensionElementsDistance(0), 0);
        Assert.assertEquals(5, t.getDimensionElementsDistance(1), 0);
        Assert.assertEquals(1, t.getDimensionElementsDistance(2), 0);
        elements = t.getElements();
        for (int i = 0; i < (elements.length); i++) {
            elements[i] = i + 1;
        }
        Tensor t2 = TensorFactory.tensor(t, new int[][]{ new int[]{ 3, 0, 0 }, new int[]{ 4, 4, 4 } }, true);
        Assert.assertEquals(75, t2.getStartIndex(), 0);
        Assert.assertEquals(124, t2.getEndIndex(), 0);
        Assert.assertEquals(25, t2.getDimensionElementsDistance(0), 0);
        Assert.assertEquals(5, t2.getDimensionElementsDistance(1), 0);
        Assert.assertEquals(1, t2.getDimensionElementsDistance(2), 0);
        Assert.assertEquals(50, t2.getSize(), 0);
        Assert.assertEquals(76, t2.get(0, 0, 0), 0);
        Assert.assertEquals(77, t2.get(0, 0, 1), 0);
        Assert.assertEquals(81, t2.get(0, 1, 0), 0);
        Assert.assertEquals(101, t2.get(1, 0, 0), 0);
        Assert.assertEquals(106, t2.get(1, 1, 0), 0);
        Assert.assertEquals(112, t2.get(1, 2, 1), 0);
        Tensor[] tarr = TensorFactory.tensor(new int[]{ 2, 2, 2 }, new int[]{ 3, 3 });
        Assert.assertEquals(17, tarr[0].getElements().length, 0);
        Assert.assertEquals(0, tarr[0].getStartOffset(), 0);
        Assert.assertEquals(8, tarr[1].getStartOffset(), 0);
        Assert.assertTrue(((tarr[1]) instanceof Matrix));
        IntStream.range(0, tarr[0].getElements().length).forEach(( i) -> tarr[0].getElements()[i] = i + 1);
        Assert.assertEquals(7, tarr[0].get(1, 1, 0), 0);
        Assert.assertEquals(13, tarr[1].get(1, 1), 0);
    }
}

