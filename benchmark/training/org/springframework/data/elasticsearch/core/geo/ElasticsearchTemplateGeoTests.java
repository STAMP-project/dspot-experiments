/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.data.elasticsearch.core.geo;


import java.util.List;
import org.elasticsearch.common.geo.GeoHashUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.geo.Point;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Franck Marchand
 * @author Artur Konczak

Basic info:
latitude  - horizontal lines (equator = 0.0, values -90.0 to 90.0)
longitude - vertical lines (Greenwich = 0.0, values -180 to 180)
London [lat,lon] = [51.50985,-0.118082] - geohash = gcpvj3448
Bouding Box for London = (bbox=-0.489,51.28,0.236,51.686)
bbox = left,bottom,right,top
bbox = min Longitude , min Latitude , max Longitude , max Latitude
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:elasticsearch-template-test.xml")
public class ElasticsearchTemplateGeoTests {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void shouldPutMappingForGivenEntityWithGeoLocation() throws Exception {
        // given
        Class entity = AuthorMarkerEntity.class;
        elasticsearchTemplate.createIndex(entity);
        // when
        MatcherAssert.assertThat(elasticsearchTemplate.putMapping(entity), is(true));
    }

    @Test
    public void shouldFindAuthorMarkersInRangeForGivenCriteriaQuery() {
        // given
        loadClassBaseEntities();
        CriteriaQuery geoLocationCriteriaQuery = new CriteriaQuery(new Criteria("location").within(new GeoPoint(45.7806, 3.0875), "20km"));
        // when
        List<AuthorMarkerEntity> geoAuthorsForGeoCriteria = elasticsearchTemplate.queryForList(geoLocationCriteriaQuery, AuthorMarkerEntity.class);
        // then
        MatcherAssert.assertThat(geoAuthorsForGeoCriteria.size(), is(1));
        Assert.assertEquals("Franck Marchand", geoAuthorsForGeoCriteria.get(0).getName());
    }

    @Test
    public void shouldFindSelectedAuthorMarkerInRangeForGivenCriteriaQuery() {
        // given
        loadClassBaseEntities();
        CriteriaQuery geoLocationCriteriaQuery2 = new CriteriaQuery(new Criteria("name").is("Mohsin Husen").and("location").within(new GeoPoint(51.5171, 0.1062), "20km"));
        // when
        List<AuthorMarkerEntity> geoAuthorsForGeoCriteria2 = elasticsearchTemplate.queryForList(geoLocationCriteriaQuery2, AuthorMarkerEntity.class);
        // then
        MatcherAssert.assertThat(geoAuthorsForGeoCriteria2.size(), is(1));
        Assert.assertEquals("Mohsin Husen", geoAuthorsForGeoCriteria2.get(0).getName());
    }

    @Test
    public void shouldFindStringAnnotatedGeoMarkersInRangeForGivenCriteriaQuery() {
        // given
        loadAnnotationBaseEntities();
        CriteriaQuery geoLocationCriteriaQuery = new CriteriaQuery(new Criteria("locationAsString").within(new GeoPoint(51.0, 0.1), "1km"));
        // when
        List<LocationMarkerEntity> geoAuthorsForGeoCriteria = elasticsearchTemplate.queryForList(geoLocationCriteriaQuery, LocationMarkerEntity.class);
        // then
        MatcherAssert.assertThat(geoAuthorsForGeoCriteria.size(), is(1));
    }

    @Test
    public void shouldFindDoubleAnnotatedGeoMarkersInRangeForGivenCriteriaQuery() {
        // given
        loadAnnotationBaseEntities();
        CriteriaQuery geoLocationCriteriaQuery = new CriteriaQuery(new Criteria("locationAsArray").within(new GeoPoint(51.001, 0.101), "1km"));
        // when
        List<LocationMarkerEntity> geoAuthorsForGeoCriteria = elasticsearchTemplate.queryForList(geoLocationCriteriaQuery, LocationMarkerEntity.class);
        // then
        MatcherAssert.assertThat(geoAuthorsForGeoCriteria.size(), is(3));
    }

    @Test
    public void shouldFindAnnotatedGeoMarkersInRangeForGivenCriteriaQuery() {
        // given
        loadAnnotationBaseEntities();
        CriteriaQuery geoLocationCriteriaQuery = new CriteriaQuery(new Criteria("locationAsArray").within("51.001000, 0.10100", "1km"));
        // when
        List<LocationMarkerEntity> geoAuthorsForGeoCriteria = elasticsearchTemplate.queryForList(geoLocationCriteriaQuery, LocationMarkerEntity.class);
        // then
        MatcherAssert.assertThat(geoAuthorsForGeoCriteria.size(), is(3));
    }

    @Test
    public void shouldFindAnnotatedGeoMarkersInRangeForGivenCriteriaQueryUsingGeohashLocation() {
        // given
        loadAnnotationBaseEntities();
        CriteriaQuery geoLocationCriteriaQuery = new CriteriaQuery(new Criteria("locationAsArray").within("u1044", "3km"));
        // when
        List<LocationMarkerEntity> geoAuthorsForGeoCriteria = elasticsearchTemplate.queryForList(geoLocationCriteriaQuery, LocationMarkerEntity.class);
        // then
        MatcherAssert.assertThat(geoAuthorsForGeoCriteria.size(), is(3));
    }

    @Test
    public void shouldFindAllMarkersForNativeSearchQuery() {
        // Given
        loadAnnotationBaseEntities();
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder().withFilter(QueryBuilders.geoBoundingBoxQuery("locationAsArray").setCorners(52, (-1), 50, 1));
        // When
        List<LocationMarkerEntity> geoAuthorsForGeoCriteria = elasticsearchTemplate.queryForList(queryBuilder.build(), LocationMarkerEntity.class);
        // Then
        MatcherAssert.assertThat(geoAuthorsForGeoCriteria.size(), is(3));
    }

    @Test
    public void shouldFindAuthorMarkersInBoxForGivenCriteriaQueryUsingGeoBox() {
        // given
        loadClassBaseEntities();
        CriteriaQuery geoLocationCriteriaQuery3 = new CriteriaQuery(new Criteria("location").boundedBy(new GeoBox(new GeoPoint(53.5171, 0), new GeoPoint(49.5171, 0.2062))));
        // when
        List<AuthorMarkerEntity> geoAuthorsForGeoCriteria3 = elasticsearchTemplate.queryForList(geoLocationCriteriaQuery3, AuthorMarkerEntity.class);
        // then
        MatcherAssert.assertThat(geoAuthorsForGeoCriteria3.size(), is(2));
        MatcherAssert.assertThat(geoAuthorsForGeoCriteria3, containsInAnyOrder(hasProperty("name", equalTo("Mohsin Husen")), hasProperty("name", equalTo("Rizwan Idrees"))));
    }

    @Test
    public void shouldFindAuthorMarkersInBoxForGivenCriteriaQueryUsingGeohash() {
        // given
        loadClassBaseEntities();
        CriteriaQuery geoLocationCriteriaQuery3 = new CriteriaQuery(new Criteria("location").boundedBy(GeoHashUtils.stringEncode(0, 53.5171), GeoHashUtils.stringEncode(0.2062, 49.5171)));
        // when
        List<AuthorMarkerEntity> geoAuthorsForGeoCriteria3 = elasticsearchTemplate.queryForList(geoLocationCriteriaQuery3, AuthorMarkerEntity.class);
        // then
        MatcherAssert.assertThat(geoAuthorsForGeoCriteria3.size(), is(2));
        MatcherAssert.assertThat(geoAuthorsForGeoCriteria3, containsInAnyOrder(hasProperty("name", equalTo("Mohsin Husen")), hasProperty("name", equalTo("Rizwan Idrees"))));
    }

    @Test
    public void shouldFindAuthorMarkersInBoxForGivenCriteriaQueryUsingGeoPoints() {
        // given
        loadClassBaseEntities();
        CriteriaQuery geoLocationCriteriaQuery3 = new CriteriaQuery(new Criteria("location").boundedBy(new GeoPoint(53.5171, 0), new GeoPoint(49.5171, 0.2062)));
        // when
        List<AuthorMarkerEntity> geoAuthorsForGeoCriteria3 = elasticsearchTemplate.queryForList(geoLocationCriteriaQuery3, AuthorMarkerEntity.class);
        // then
        MatcherAssert.assertThat(geoAuthorsForGeoCriteria3.size(), is(2));
        MatcherAssert.assertThat(geoAuthorsForGeoCriteria3, containsInAnyOrder(hasProperty("name", equalTo("Mohsin Husen")), hasProperty("name", equalTo("Rizwan Idrees"))));
    }

    @Test
    public void shouldFindAuthorMarkersInBoxForGivenCriteriaQueryUsingPoints() {
        // given
        loadClassBaseEntities();
        CriteriaQuery geoLocationCriteriaQuery3 = new CriteriaQuery(new Criteria("location").boundedBy(new Point(53.5171, 0), new Point(49.5171, 0.2062)));
        // when
        List<AuthorMarkerEntity> geoAuthorsForGeoCriteria3 = elasticsearchTemplate.queryForList(geoLocationCriteriaQuery3, AuthorMarkerEntity.class);
        // then
        MatcherAssert.assertThat(geoAuthorsForGeoCriteria3.size(), is(2));
        MatcherAssert.assertThat(geoAuthorsForGeoCriteria3, containsInAnyOrder(hasProperty("name", equalTo("Mohsin Husen")), hasProperty("name", equalTo("Rizwan Idrees"))));
    }

    @Test
    public void shouldFindLocationWithGeoHashPrefix() {
        // given
        loadAnnotationBaseEntities();
        NativeSearchQueryBuilder location1 = new NativeSearchQueryBuilder().withFilter(QueryBuilders.geoBoundingBoxQuery("locationAsGeoHash").setCorners("u"));
        NativeSearchQueryBuilder location2 = new NativeSearchQueryBuilder().withFilter(QueryBuilders.geoBoundingBoxQuery("locationAsGeoHash").setCorners("u1"));
        NativeSearchQueryBuilder location3 = new NativeSearchQueryBuilder().withFilter(QueryBuilders.geoBoundingBoxQuery("locationAsGeoHash").setCorners("u10"));
        NativeSearchQueryBuilder location4 = new NativeSearchQueryBuilder().withFilter(QueryBuilders.geoBoundingBoxQuery("locationAsGeoHash").setCorners("u10j"));
        NativeSearchQueryBuilder location5 = new NativeSearchQueryBuilder().withFilter(QueryBuilders.geoBoundingBoxQuery("locationAsGeoHash").setCorners("u10j4"));
        NativeSearchQueryBuilder location11 = new NativeSearchQueryBuilder().withFilter(QueryBuilders.geoBoundingBoxQuery("locationAsGeoHash").setCorners("u10j46mkfek"));
        // when
        List<LocationMarkerEntity> result1 = elasticsearchTemplate.queryForList(location1.build(), LocationMarkerEntity.class);
        List<LocationMarkerEntity> result2 = elasticsearchTemplate.queryForList(location2.build(), LocationMarkerEntity.class);
        List<LocationMarkerEntity> result3 = elasticsearchTemplate.queryForList(location3.build(), LocationMarkerEntity.class);
        List<LocationMarkerEntity> result4 = elasticsearchTemplate.queryForList(location4.build(), LocationMarkerEntity.class);
        List<LocationMarkerEntity> result5 = elasticsearchTemplate.queryForList(location5.build(), LocationMarkerEntity.class);
        List<LocationMarkerEntity> result11 = elasticsearchTemplate.queryForList(location11.build(), LocationMarkerEntity.class);
        // then
        MatcherAssert.assertThat(result1.size(), is(3));
        MatcherAssert.assertThat(result2.size(), is(3));
        MatcherAssert.assertThat(result3.size(), is(3));
        MatcherAssert.assertThat(result4.size(), is(3));
        MatcherAssert.assertThat(result5.size(), is(3));
        MatcherAssert.assertThat(result11.size(), is(2));
    }
}

