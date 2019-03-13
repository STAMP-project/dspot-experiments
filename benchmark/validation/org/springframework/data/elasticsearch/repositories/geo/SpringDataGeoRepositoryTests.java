/**
 * Copyright 2016-2019 the original author or authors.
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
package org.springframework.data.elasticsearch.repositories.geo;


import java.util.Optional;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.entities.GeoEntity;
import org.springframework.data.geo.Point;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Mark Paluch
 * @author Christoph Strobl
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/repository-spring-data-geo-support.xml")
public class SpringDataGeoRepositoryTests {
    @Autowired
    ElasticsearchTemplate template;

    @Autowired
    SpringDataGeoRepository repository;

    @Test
    public void shouldSaveAndLoadGeoPoints() {
        // given
        final Point point = new Point(15, 25);
        GeoEntity entity = builder().pointA(point).pointB(new org.springframework.data.elasticsearch.core.geo.GeoPoint(point.getX(), point.getY())).pointC(toGeoString(point)).pointD(toGeoArray(point)).build();
        // when
        GeoEntity saved = save(entity);
        Optional<GeoEntity> result = repository.findById(getId());
        // then
        Assert.assertThat(result.isPresent(), is(true));
        result.ifPresent(( geoEntity) -> {
            Assert.assertThat(getPointA().getX(), is(getPointA().getX()));
            Assert.assertThat(getPointA().getY(), is(getPointA().getY()));
            Assert.assertThat(getPointB().getLat(), is(getPointB().getLat()));
            Assert.assertThat(getPointB().getLon(), is(getPointB().getLon()));
            Assert.assertThat(getPointC(), is(getPointC()));
            Assert.assertThat(getPointD(), is(getPointD()));
        });
    }
}

