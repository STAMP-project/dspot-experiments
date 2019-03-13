/**
 * Copyright 2007 ZXing authors
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
package com.google.zxing.qrcode.decoder;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sean Owen
 */
public final class DataMaskTestCase extends Assert {
    @Test
    public void testMask0() {
        DataMaskTestCase.testMaskAcrossDimensions(0, new DataMaskTestCase.MaskCondition() {
            @Override
            public boolean isMasked(int i, int j) {
                return ((i + j) % 2) == 0;
            }
        });
    }

    @Test
    public void testMask1() {
        DataMaskTestCase.testMaskAcrossDimensions(1, new DataMaskTestCase.MaskCondition() {
            @Override
            public boolean isMasked(int i, int j) {
                return (i % 2) == 0;
            }
        });
    }

    @Test
    public void testMask2() {
        DataMaskTestCase.testMaskAcrossDimensions(2, new DataMaskTestCase.MaskCondition() {
            @Override
            public boolean isMasked(int i, int j) {
                return (j % 3) == 0;
            }
        });
    }

    @Test
    public void testMask3() {
        DataMaskTestCase.testMaskAcrossDimensions(3, new DataMaskTestCase.MaskCondition() {
            @Override
            public boolean isMasked(int i, int j) {
                return ((i + j) % 3) == 0;
            }
        });
    }

    @Test
    public void testMask4() {
        DataMaskTestCase.testMaskAcrossDimensions(4, new DataMaskTestCase.MaskCondition() {
            @Override
            public boolean isMasked(int i, int j) {
                return (((i / 2) + (j / 3)) % 2) == 0;
            }
        });
    }

    @Test
    public void testMask5() {
        DataMaskTestCase.testMaskAcrossDimensions(5, new DataMaskTestCase.MaskCondition() {
            @Override
            public boolean isMasked(int i, int j) {
                return (((i * j) % 2) + ((i * j) % 3)) == 0;
            }
        });
    }

    @Test
    public void testMask6() {
        DataMaskTestCase.testMaskAcrossDimensions(6, new DataMaskTestCase.MaskCondition() {
            @Override
            public boolean isMasked(int i, int j) {
                return ((((i * j) % 2) + ((i * j) % 3)) % 2) == 0;
            }
        });
    }

    @Test
    public void testMask7() {
        DataMaskTestCase.testMaskAcrossDimensions(7, new DataMaskTestCase.MaskCondition() {
            @Override
            public boolean isMasked(int i, int j) {
                return ((((i + j) % 2) + ((i * j) % 3)) % 2) == 0;
            }
        });
    }

    private interface MaskCondition {
        boolean isMasked(int i, int j);
    }
}

