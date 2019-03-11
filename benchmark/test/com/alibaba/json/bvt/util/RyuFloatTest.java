package com.alibaba.json.bvt.util;


import com.alibaba.fastjson.util.RyuFloat;
import java.util.Random;
import junit.framework.TestCase;


public class RyuFloatTest extends TestCase {
    public void test_for_ryu() throws Exception {
        Random random = new Random();
        for (int i = 0; i < ((1000 * 1000) * 10); ++i) {
            float value = random.nextFloat();
            String str1 = Float.toString(value);
            String str2 = RyuFloat.toString(value);
            if (!(str1.equals(str2))) {
                boolean cmp = (Float.parseFloat(str1)) == (Float.parseFloat(str2));
                System.out.println(((((str1 + " -> ") + str2) + " : ") + cmp));
                TestCase.assertTrue(cmp);
                // assertTrue(Float.parseFloat(str1) == Float.parseFloat(str2));
            }
        }
    }

    public void test_0() throws Exception {
        float[] values = new float[]{ Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.MIN_VALUE, Float.MAX_VALUE, 0, 0.0F, -0.0F, Integer.MAX_VALUE, Integer.MIN_VALUE, Long.MAX_VALUE, Long.MIN_VALUE, Float.intBitsToFloat(-2147483648), 1.0F, -1.0F, Float.intBitsToFloat(8388608), 1.0E7F, 9999999.0F, 0.001F, 9.999999E-4F, Float.intBitsToFloat(2139095039), Float.intBitsToFloat(1), 3.3554448E7F, 8.9999995E9F, 3.4366718E10F, 0.33007812F, Float.intBitsToFloat(1561658105), Float.intBitsToFloat(1570046713), Float.intBitsToFloat(1578435321), 4.7223665E21F, 8388608.0F, 1.6777216E7F, 3.3554436E7F, 6.7131496E7F, 1.9310392E-38F, -2.47E-43F, 1.993244E-38F, 4103.9004F, 5.3399997E9F, 6.0898E-39F, 0.0010310042F, 2.8823261E17F, 7.038531E-26F, 9.2234038E17F, 6.7108872E7F, 9.8E-45F, 2.81602484E14F, 9.223372E18F, 1.5846086E29F, 1.1811161E19F, 5.3687091E18F, 4.6143166E18F, 0.007812537F, 1.4E-45F, 1.18697725E20F, 1.00014165E-36F, 200.0F, 3.3554432E7F, 0.1F, 0.01F, 0.001F, 1.0E-4F, 1.0E-5F, 1.0E-6F, 1.0E-7F, 1.1F, 1.01F, 1.001F, 1.0001F, 1.00001F, 1.000001F, 1.0000001F };
        for (float value : values) {
            String str1 = Float.toString(value);
            String str2 = RyuFloat.toString(value);
            if (!(str1.equals(str2))) {
                boolean cmp = (Float.parseFloat(str1)) == (Float.parseFloat(str2));
                System.out.println(((((str1 + " -> ") + str2) + " : ") + cmp));
                TestCase.assertTrue(cmp);
            }
        }
    }
}

