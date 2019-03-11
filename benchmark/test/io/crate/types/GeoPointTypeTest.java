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
package io.crate.types;


import DataTypes.GEO_POINT;
import GeoPointType.INSTANCE;
import io.crate.test.integration.CrateUnitTest;
import java.util.Arrays;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.hamcrest.Matchers;
import org.junit.Test;

import static DataTypes.LONG;


public class GeoPointTypeTest extends CrateUnitTest {
    @Test
    public void testStreaming() throws Throwable {
        Double[] p1 = new Double[]{ 41.2, -37.4 };
        BytesStreamOutput out = new BytesStreamOutput();
        GEO_POINT.writeValueTo(out, p1);
        StreamInput in = out.bytes().streamInput();
        Double[] p2 = GEO_POINT.readValueFrom(in);
        assertThat(p1, Matchers.equalTo(p2));
    }

    @Test
    public void testWktToGeoPointValue() throws Exception {
        Double[] value = GEO_POINT.value("POINT(1 2)");
        assertThat(value[0], Matchers.is(1.0));
        assertThat(value[1], Matchers.is(2.0));
    }

    @Test
    public void testInvalidWktToGeoPointValue() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(("Cannot convert \"POINT(54.321 -123.456)\" to geo_point." + " Bad Y value -123.456 is not in boundary Rect(minX=-180.0,maxX=180.0,minY=-90.0,maxY=90.0)"));
        GEO_POINT.value("POINT(54.321 -123.456)");
    }

    @Test
    public void testValueConversionFromList() throws Exception {
        Double[] value = GEO_POINT.value(Arrays.asList(10.0, 20.2));
        assertThat(value[0], Matchers.is(10.0));
        assertThat(value[1], Matchers.is(20.2));
    }

    @Test
    public void testConversionFromObjectArrayOfIntegers() throws Exception {
        Double[] value = GEO_POINT.value(new Object[]{ 1, 2 });
        assertThat(value[0], Matchers.is(1.0));
        assertThat(value[1], Matchers.is(2.0));
    }

    @Test
    public void testConversionFromIntegerArray() throws Exception {
        Double[] value = GEO_POINT.value(new Integer[]{ 1, 2 });
        assertThat(value[0], Matchers.is(1.0));
        assertThat(value[1], Matchers.is(2.0));
    }

    @Test
    public void testConversionFromArrayType() {
        assertThat(new ArrayType(LONG).isConvertableTo(INSTANCE), Matchers.is(true));
    }

    @Test
    public void testInvalidLatitude() throws Exception {
        expectedException.expectMessage("Failed to validate geo point [lon=54.321000, lat=-123.456000], not a valid location.");
        GEO_POINT.value(new Double[]{ 54.321, -123.456 });
    }

    @Test
    public void testInvalidLongitude() throws Exception {
        expectedException.expectMessage("Failed to validate geo point [lon=-187.654000, lat=123.456000], not a valid location.");
        GEO_POINT.value(new Double[]{ -187.654, 123.456 });
    }
}

