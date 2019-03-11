/**
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.lang;


import junit.framework.TestCase;


/**
 * Tests for basic functionality of floats and doubles.
 */
public class OldAndroidFloatDoubleTest extends TestCase {
    public void testFloatDouble() throws Exception {
        Double d = Double.valueOf(1.0);
        Float f = Float.valueOf(1.0F);
        Object o = new Object();
        TestCase.assertFalse(f.equals(d));
        TestCase.assertFalse(d.equals(f));
        TestCase.assertFalse(f.equals(o));
        TestCase.assertFalse(d.equals(o));
        TestCase.assertFalse(f.equals(null));
        TestCase.assertFalse(d.equals(null));
    }

    public void testFloat() throws Exception {
        float pz = 0.0F;
        float nz = -0.0F;
        float pzero = 1.0F / (Float.POSITIVE_INFINITY);
        float nzero = 1.0F / (Float.NEGATIVE_INFINITY);
        // Everything compares as '=='
        TestCase.assertTrue((pz == pz));
        TestCase.assertTrue((pz == nz));
        TestCase.assertTrue((pz == pzero));
        TestCase.assertTrue((pz == nzero));
        TestCase.assertTrue((nz == pz));
        TestCase.assertTrue((nz == nz));
        TestCase.assertTrue((nz == pzero));
        TestCase.assertTrue((nz == nzero));
        TestCase.assertTrue((pzero == pz));
        TestCase.assertTrue((pzero == nz));
        TestCase.assertTrue((pzero == pzero));
        TestCase.assertTrue((pzero == nzero));
        TestCase.assertTrue((nzero == pz));
        TestCase.assertTrue((nzero == nz));
        TestCase.assertTrue((nzero == pzero));
        TestCase.assertTrue((nzero == nzero));
        // +-0 are distinct as Floats
        TestCase.assertEquals(Float.valueOf(pz), Float.valueOf(pz));
        TestCase.assertTrue((!(Float.valueOf(pz).equals(Float.valueOf(nz)))));
        TestCase.assertEquals(Float.valueOf(pz), Float.valueOf(pzero));
        TestCase.assertTrue((!(Float.valueOf(pz).equals(Float.valueOf(nzero)))));
        TestCase.assertTrue((!(Float.valueOf(nz).equals(Float.valueOf(pz)))));
        TestCase.assertEquals(Float.valueOf(nz), Float.valueOf(nz));
        TestCase.assertTrue((!(Float.valueOf(nz).equals(Float.valueOf(pzero)))));
        TestCase.assertEquals(Float.valueOf(nz), Float.valueOf(nzero));
        TestCase.assertEquals(Float.valueOf(pzero), Float.valueOf(pz));
        TestCase.assertTrue((!(Float.valueOf(pzero).equals(Float.valueOf(nz)))));
        TestCase.assertEquals(Float.valueOf(pzero), Float.valueOf(pzero));
        TestCase.assertTrue((!(Float.valueOf(pzero).equals(Float.valueOf(nzero)))));
        TestCase.assertTrue((!(Float.valueOf(nzero).equals(Float.valueOf(pz)))));
        TestCase.assertEquals(Float.valueOf(nzero), Float.valueOf(nz));
        TestCase.assertTrue((!(Float.valueOf(nzero).equals(Float.valueOf(pzero)))));
        TestCase.assertEquals(Float.valueOf(nzero), Float.valueOf(nzero));
        // Nan's compare as equal
        Float sqrtm2 = Float.valueOf(((float) (Math.sqrt((-2.0F)))));
        Float sqrtm3 = Float.valueOf(((float) (Math.sqrt((-3.0F)))));
        TestCase.assertEquals(sqrtm2, sqrtm3);
    }

    public void testDouble() throws Exception {
        double pz = 0.0;
        double nz = -0.0;
        double pzero = 1.0 / (Double.POSITIVE_INFINITY);
        double nzero = 1.0 / (Double.NEGATIVE_INFINITY);
        // Everything compares as '=='
        TestCase.assertTrue((pz == pz));
        TestCase.assertTrue((pz == nz));
        TestCase.assertTrue((pz == pzero));
        TestCase.assertTrue((pz == nzero));
        TestCase.assertTrue((nz == pz));
        TestCase.assertTrue((nz == nz));
        TestCase.assertTrue((nz == pzero));
        TestCase.assertTrue((nz == nzero));
        TestCase.assertTrue((pzero == pz));
        TestCase.assertTrue((pzero == nz));
        TestCase.assertTrue((pzero == pzero));
        TestCase.assertTrue((pzero == nzero));
        TestCase.assertTrue((nzero == pz));
        TestCase.assertTrue((nzero == nz));
        TestCase.assertTrue((nzero == pzero));
        TestCase.assertTrue((nzero == nzero));
        // +-0 are distinct as Doubles
        TestCase.assertEquals(Double.valueOf(pz), Double.valueOf(pz));
        TestCase.assertTrue((!(Double.valueOf(pz).equals(Double.valueOf(nz)))));
        TestCase.assertEquals(Double.valueOf(pz), Double.valueOf(pzero));
        TestCase.assertTrue((!(Double.valueOf(pz).equals(Double.valueOf(nzero)))));
        TestCase.assertTrue((!(Double.valueOf(nz).equals(Double.valueOf(pz)))));
        TestCase.assertEquals(Double.valueOf(nz), Double.valueOf(nz));
        TestCase.assertTrue((!(Double.valueOf(nz).equals(Double.valueOf(pzero)))));
        TestCase.assertEquals(Double.valueOf(nz), Double.valueOf(nzero));
        TestCase.assertEquals(Double.valueOf(pzero), Double.valueOf(pz));
        TestCase.assertTrue((!(Double.valueOf(pzero).equals(Double.valueOf(nz)))));
        TestCase.assertEquals(Double.valueOf(pzero), Double.valueOf(pzero));
        TestCase.assertTrue((!(Double.valueOf(pzero).equals(Double.valueOf(nzero)))));
        TestCase.assertTrue((!(Double.valueOf(nzero).equals(Double.valueOf(pz)))));
        TestCase.assertEquals(Double.valueOf(nzero), Double.valueOf(nz));
        TestCase.assertTrue((!(Double.valueOf(nzero).equals(Double.valueOf(pzero)))));
        TestCase.assertEquals(Double.valueOf(nzero), Double.valueOf(nzero));
        // Nan's compare as equal
        Double sqrtm2 = Double.valueOf(Math.sqrt((-2.0)));
        Double sqrtm3 = Double.valueOf(Math.sqrt((-3.0)));
        TestCase.assertEquals(sqrtm2, sqrtm3);
    }
}

