/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.breaker;


import DataTypes.BYTE;
import DataTypes.GEO_POINT;
import DataTypes.STRING;
import io.crate.test.integration.CrateUnitTest;
import org.hamcrest.Matchers;
import org.junit.Test;


public class SizeEstimatorTest extends CrateUnitTest {
    @Test
    public void testString() throws Exception {
        SizeEstimator sizeEstimator = SizeEstimatorFactory.create(STRING);
        assertThat(sizeEstimator.estimateSize(null), Matchers.is(8L));
        assertThat(sizeEstimator.estimateSize("hello"), Matchers.is(37L));
        assertThat(sizeEstimator.estimateSizeDelta("hello", "hello world"), Matchers.is(6L));
    }

    @Test
    public void testConstant() throws Exception {
        SizeEstimator sizeEstimator = SizeEstimatorFactory.create(BYTE);
        assertThat(sizeEstimator.estimateSize(null), Matchers.is(8L));
        assertThat(sizeEstimator.estimateSize(Byte.valueOf("100")), Matchers.is(16L));
        assertThat(sizeEstimator.estimateSizeDelta(Byte.valueOf("100"), Byte.valueOf("42")), Matchers.is(0L));
    }

    @Test
    public void testGeoPoint() throws Exception {
        SizeEstimator sizeEstimator = SizeEstimatorFactory.create(GEO_POINT);
        assertThat(sizeEstimator.estimateSize(null), Matchers.is(8L));
        assertThat(sizeEstimator.estimateSize(new Double[]{ 1.0, 2.0 }), Matchers.is(40L));
        assertThat(sizeEstimator.estimateSizeDelta(new Double[]{ 1.0, 2.0 }, null), Matchers.is((-32L)));
    }
}

