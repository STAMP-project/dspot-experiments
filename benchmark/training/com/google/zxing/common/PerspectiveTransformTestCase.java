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
package com.google.zxing.common;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sean Owen
 */
public final class PerspectiveTransformTestCase extends Assert {
    private static final float EPSILON = 1.0E-4F;

    @Test
    public void testSquareToQuadrilateral() {
        PerspectiveTransform pt = PerspectiveTransform.squareToQuadrilateral(2.0F, 3.0F, 10.0F, 4.0F, 16.0F, 15.0F, 4.0F, 9.0F);
        PerspectiveTransformTestCase.assertPointEquals(2.0F, 3.0F, 0.0F, 0.0F, pt);
        PerspectiveTransformTestCase.assertPointEquals(10.0F, 4.0F, 1.0F, 0.0F, pt);
        PerspectiveTransformTestCase.assertPointEquals(4.0F, 9.0F, 0.0F, 1.0F, pt);
        PerspectiveTransformTestCase.assertPointEquals(16.0F, 15.0F, 1.0F, 1.0F, pt);
        PerspectiveTransformTestCase.assertPointEquals(6.535211F, 6.8873234F, 0.5F, 0.5F, pt);
        PerspectiveTransformTestCase.assertPointEquals(48.0F, 42.42857F, 1.5F, 1.5F, pt);
    }

    @Test
    public void testQuadrilateralToQuadrilateral() {
        PerspectiveTransform pt = PerspectiveTransform.quadrilateralToQuadrilateral(2.0F, 3.0F, 10.0F, 4.0F, 16.0F, 15.0F, 4.0F, 9.0F, 103.0F, 110.0F, 300.0F, 120.0F, 290.0F, 270.0F, 150.0F, 280.0F);
        PerspectiveTransformTestCase.assertPointEquals(103.0F, 110.0F, 2.0F, 3.0F, pt);
        PerspectiveTransformTestCase.assertPointEquals(300.0F, 120.0F, 10.0F, 4.0F, pt);
        PerspectiveTransformTestCase.assertPointEquals(290.0F, 270.0F, 16.0F, 15.0F, pt);
        PerspectiveTransformTestCase.assertPointEquals(150.0F, 280.0F, 4.0F, 9.0F, pt);
        PerspectiveTransformTestCase.assertPointEquals(7.1516876F, (-64.60185F), 0.5F, 0.5F, pt);
        PerspectiveTransformTestCase.assertPointEquals(328.09116F, 334.16385F, 50.0F, 50.0F, pt);
    }
}

