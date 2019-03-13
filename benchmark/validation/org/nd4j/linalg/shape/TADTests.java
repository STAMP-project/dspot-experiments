package org.nd4j.linalg.shape;


import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.primitives.Pair;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
@RunWith(Parameterized.class)
public class TADTests extends BaseNd4jTest {
    public TADTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testStall() {
        // [4, 3, 3, 4, 5, 60, 20, 5, 1, 0, 1, 99], dimensions: [1, 2, 3]
        INDArray arr = Nd4j.create(3, 3, 4, 5);
        arr.tensorAlongDimension(0, 1, 2, 3);
    }

    /**
     * This test checks for TADs equality between Java & native
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEquality1() throws Exception {
        char[] order = new char[]{ 'c', 'f' };
        int[] dim_e = new int[]{ 0, 2 };
        int[] dim_x = new int[]{ 1, 3 };
        List<int[]> dim_3 = Arrays.asList(new int[]{ 0, 2, 3 }, new int[]{ 0, 1, 2 }, new int[]{ 1, 2, 3 }, new int[]{ 0, 1, 3 });
        for (char o : order) {
            INDArray array = Nd4j.create(new int[]{ 3, 5, 7, 9 }, o);
            for (int e : dim_e) {
                for (int x : dim_x) {
                    int[] shape = new int[]{ e, x };
                    Arrays.sort(shape);
                    INDArray assertion = array.javaTensorAlongDimension(0, shape);
                    INDArray test = array.tensorAlongDimension(0, shape);
                    Assert.assertEquals(assertion, test);
                    Assert.assertEquals(assertion.shapeInfoDataBuffer(), test.shapeInfoDataBuffer());
                    /* DataBuffer tadShape_N = Nd4j.getExecutioner().getTADManager().getTADOnlyShapeInfo(array, shape).getFirst();
                    DataBuffer tadShape_J = array.tensorAlongDimension(0, shape).shapeInfoDataBuffer();
                    log.info("Original order: {}; Dimensions: {}; Original shape: {};", o, Arrays.toString(shape), Arrays.toString(array.shapeInfoDataBuffer().asInt()));
                    log.info("Java shape: {}; Native shape: {}", Arrays.toString(tadShape_J.asInt()), Arrays.toString(tadShape_N.asInt()));
                    System.out.println();
                    assertEquals("TAD asertadShape_J,tadShape_N);
                     */
                }
            }
        }
        log.info("3D TADs:");
        for (char o : order) {
            INDArray array = Nd4j.create(new int[]{ 9, 7, 5, 3 }, o);
            for (int[] shape : dim_3) {
                Arrays.sort(shape);
                log.info(((("About to do shape: " + (Arrays.toString(shape))) + " for array of shape ") + (array.shapeInfoToString())));
                INDArray assertion = array.javaTensorAlongDimension(0, shape);
                INDArray test = array.tensorAlongDimension(0, shape);
                Assert.assertEquals(assertion, test);
                Assert.assertEquals(assertion.shapeInfoDataBuffer(), test.shapeInfoDataBuffer());
                /* log.info("Original order: {}; Dimensions: {}; Original shape: {};", o, Arrays.toString(shape), Arrays.toString(array.shapeInfoDataBuffer().asInt()));
                log.info("Java shape: {}; Native shape: {}", Arrays.toString(tadShape_J.asInt()), Arrays.toString(tadShape_N.asInt()));
                System.out.println();
                assertEquals(true, compareShapes(tadShape_N, tadShape_J));
                 */
            }
        }
    }

    @Test
    public void testMysteriousCrash() {
        INDArray arrayF = Nd4j.create(new int[]{ 1, 1, 4, 4 }, 'f');
        INDArray arrayC = Nd4j.create(new int[]{ 1, 1, 4, 4 }, 'c');
        INDArray javaCTad = arrayC.javaTensorAlongDimension(0, 2, 3);
        INDArray javaFTad = arrayF.javaTensorAlongDimension(0, 2, 3);
        Pair<DataBuffer, DataBuffer> tadBuffersF = Nd4j.getExecutioner().getTADManager().getTADOnlyShapeInfo(arrayF, new int[]{ 2, 3 });
        Pair<DataBuffer, DataBuffer> tadBuffersC = Nd4j.getExecutioner().getTADManager().getTADOnlyShapeInfo(arrayC, new int[]{ 2, 3 });
        log.info("Got TADShapeF: {}", (((Arrays.toString(tadBuffersF.getFirst().asInt())) + " with java ") + (javaFTad.shapeInfoDataBuffer())));
        log.info("Got TADShapeC: {}", (((Arrays.toString(tadBuffersC.getFirst().asInt())) + " with java ") + (javaCTad.shapeInfoDataBuffer())));
    }
}

