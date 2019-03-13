/**
 * Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * For more information: http://www.orientdb.com
 */
package com.orientechnologies.spatial;


import java.text.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.spatial4j.shape.Shape;


/**
 * Created by Enrico Risa on 14/08/15.
 */
public class LuceneSpatialFunctionAsTextTest extends BaseSpatialLuceneTest {
    @Test
    public void testPoint() {
        queryAndAssertGeom("OPoint", BaseSpatialLuceneTest.POINTWKT);
    }

    @Test
    public void testMultiPoint() {
        queryAndAssertGeom("OMultiPoint", BaseSpatialLuceneTest.MULTIPOINTWKT);
    }

    @Test
    public void testLineString() {
        queryAndAssertGeom("OLineString", BaseSpatialLuceneTest.LINESTRINGWKT);
    }

    @Test
    public void testMultiLineString() {
        queryAndAssertGeom("OMultiLineString", BaseSpatialLuceneTest.MULTILINESTRINGWKT);
    }

    @Test
    public void testBugEnvelope() {
        try {
            Shape shape = context.readShapeFromWkt(BaseSpatialLuceneTest.RECTANGLEWKT);
            Geometry geometryFrom = context.getGeometryFrom(shape);
            Assert.assertEquals(geometryFrom.toText(), BaseSpatialLuceneTest.RECTANGLEWKT);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPolygon() {
        queryAndAssertGeom("OPolygon", BaseSpatialLuceneTest.POLYGONWKT);
    }

    @Test
    public void testGeometryCollection() {
        queryAndAssertGeom("OGeometryCollection", BaseSpatialLuceneTest.GEOMETRYCOLLECTION);
    }

    @Test
    public void testMultiPolygon() {
        queryAndAssertGeom("OMultiPolygon", BaseSpatialLuceneTest.MULTIPOLYGONWKT);
    }
}

