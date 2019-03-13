package net.librec.math.algorithm;


import net.librec.BaseTestCase;
import org.junit.Assert;
import org.junit.Test;


public class MathsTestCase extends BaseTestCase {
    /**
     * test softmax function
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSoftmax() throws Exception {
        double[] testArray = new double[]{ 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6 };
        double[] dep_output = Maths.softmax_deprecated(testArray);
        double[] new_output = Maths.softmax(testArray);
        for (int i = 0; i < (testArray.length); i++) {
            Assert.assertTrue(((Math.abs(((dep_output[i]) - (new_output[i])))) < 1.0E-5));
        }
    }
}

