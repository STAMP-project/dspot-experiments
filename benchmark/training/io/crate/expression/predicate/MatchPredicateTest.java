/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.expression.predicate;


import DataTypes.GEO_SHAPE;
import DataTypes.INTEGER;
import DataTypes.STRING;
import io.crate.test.integration.CrateUnitTest;
import org.hamcrest.core.Is;
import org.junit.Test;


public class MatchPredicateTest extends CrateUnitTest {
    @Test
    public void testGetStringDefaultMatchType() throws Exception {
        assertThat(MatchPredicate.getMatchType(null, STRING), Is.is("best_fields"));
    }

    @Test
    public void testGetValidStringMatchType() throws Exception {
        assertThat(MatchPredicate.getMatchType("most_fields", STRING), Is.is("most_fields"));
    }

    @Test
    public void testGetValidGeoMatchType() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("invalid MATCH type 'contains' for type 'geo_shape'");
        MatchPredicate.getMatchType("contains", GEO_SHAPE);
    }

    @Test
    public void testGetGeoShapeDefaultMatchType() throws Exception {
        assertThat(MatchPredicate.getMatchType(null, GEO_SHAPE), Is.is("intersects"));
    }

    @Test
    public void testGetDefaultMatchTypeForInvalidType() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("No default matchType found for dataType: integer");
        MatchPredicate.getMatchType(null, INTEGER);
    }

    @Test
    public void testGetMatchTypeForInvalidType() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("No match type for dataType: integer");
        MatchPredicate.getMatchType("foo", INTEGER);
    }

    @Test
    public void testInvalidStringMatchType() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("invalid MATCH type 'foo' for type 'string'");
        MatchPredicate.getMatchType("foo", STRING);
    }

    @Test
    public void testInvalidGeoShapeMatchType() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("invalid MATCH type 'foo' for type 'geo_shape'");
        MatchPredicate.getMatchType("foo", GEO_SHAPE);
    }
}

