/**
 * Copyright 2011-2019 the original author or authors.
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
package io.lettuce.core.commands;


import GeoArgs.Unit.km;
import io.lettuce.core.TestSupport;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.test.LettuceExtension;
import io.lettuce.test.condition.EnabledOnCommand;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;


/**
 *
 *
 * @author Mark Paluch
 */
@ExtendWith(LettuceExtension.class)
@EnabledOnCommand("GEOADD")
@TestInstance(PER_CLASS)
public class GeoCommandIntegrationTests extends TestSupport {
    private final RedisCommands<String, String> redis;

    @Inject
    protected GeoCommandIntegrationTests(RedisCommands<String, String> redis) {
        this.redis = redis;
    }

    @Test
    public void geoaddInTransaction() {
        redis.multi();
        redis.geoadd(TestSupport.key, (-73.9454966), 40.747533, "lic market");
        redis.geoadd(TestSupport.key, (-73.9454966), 40.747533, "lic market");
        assertThat(redis.exec()).containsSequence(1L, 0L);
    }

    @Test
    public void geoaddMultiInTransaction() {
        redis.multi();
        redis.geoadd(TestSupport.key, 8.6638775, 49.5282537, "Weinheim", 8.3796281, 48.9978127, "EFS9", 8.665351, 49.553302, "Bahn");
        assertThat(redis.exec()).contains(3L);
    }

    @Test
    public void georadiusInTransaction() {
        prepareGeo();
        redis.multi();
        redis.georadius(TestSupport.key, 8.6582861, 49.5285695, 1, km);
        redis.georadius(TestSupport.key, 8.6582861, 49.5285695, 5, km);
        TransactionResult exec = redis.exec();
        Set<String> georadius = exec.get(0);
        Set<String> largerGeoradius = exec.get(1);
        assertThat(georadius).hasSize(1).contains("Weinheim");
        assertThat(largerGeoradius).hasSize(2).contains("Weinheim").contains("Bahn");
    }

    @Test
    public void geodistInTransaction() {
        prepareGeo();
        redis.multi();
        redis.geodist(TestSupport.key, "Weinheim", "Bahn", km);
        Double result = ((Double) (redis.exec().get(0)));
        // 10 mins with the bike
        assertThat(result).isGreaterThan(2.5).isLessThan(2.9);
    }

    @Test
    public void geopos() {
        prepareGeo();
        List<GeoCoordinates> geopos = redis.geopos(TestSupport.key, "Weinheim");
        assertThat(geopos).hasSize(1);
        assertThat(geopos.get(0).getX().doubleValue()).isEqualTo(8.6638, offset(0.001));
        geopos = redis.geopos(TestSupport.key, "Weinheim", "foobar", "Bahn");
        assertThat(geopos).hasSize(3);
        assertThat(geopos.get(0).getX().doubleValue()).isEqualTo(8.6638, offset(0.001));
        assertThat(geopos.get(1)).isNull();
        assertThat(geopos.get(2)).isNotNull();
    }

    @Test
    public void geoposInTransaction() {
        prepareGeo();
        redis.multi();
        redis.geopos(TestSupport.key, "Weinheim", "foobar", "Bahn");
        redis.geopos(TestSupport.key, "Weinheim", "foobar", "Bahn");
        List<GeoCoordinates> geopos = redis.exec().get(1);
        assertThat(geopos).hasSize(3);
        assertThat(geopos.get(0).getX().doubleValue()).isEqualTo(8.6638, offset(0.001));
        assertThat(geopos.get(1)).isNull();
        assertThat(geopos.get(2)).isNotNull();
    }

    @Test
    public void georadiusWithArgsAndTransaction() {
        prepareGeo();
        redis.multi();
        GeoArgs geoArgs = new GeoArgs().withHash().withCoordinates().withDistance().withCount(1).desc();
        redis.georadius(TestSupport.key, 8.665351, 49.553302, 5, km, geoArgs);
        redis.georadius(TestSupport.key, 8.665351, 49.553302, 5, km, geoArgs);
        TransactionResult exec = redis.exec();
        assertThat(exec).hasSize(2);
        List<GeoWithin<String>> result = exec.get(1);
        assertThat(result).hasSize(1);
        GeoWithin<String> weinheim = result.get(0);
        assertThat(weinheim.getMember()).isEqualTo("Weinheim");
        assertThat(weinheim.getGeohash()).isEqualTo(3666615932941099L);
        assertThat(weinheim.getDistance()).isEqualTo(2.7882, offset(0.5));
        assertThat(weinheim.getCoordinates().getX().doubleValue()).isEqualTo(8.663875, offset(0.5));
        assertThat(weinheim.getCoordinates().getY().doubleValue()).isEqualTo(49.52825, offset(0.5));
        result = redis.georadius(TestSupport.key, 8.665351, 49.553302, 1, km, new GeoArgs());
        assertThat(result).hasSize(1);
        GeoWithin<String> bahn = result.get(0);
        assertThat(bahn.getMember()).isEqualTo("Bahn");
        assertThat(bahn.getGeohash()).isNull();
        assertThat(bahn.getDistance()).isNull();
        assertThat(bahn.getCoordinates()).isNull();
    }

    @Test
    public void geohashInTransaction() {
        prepareGeo();
        redis.multi();
        redis.geohash(TestSupport.key, "Weinheim", "Bahn", "dunno");
        redis.geohash(TestSupport.key, "Weinheim", "Bahn", "dunno");
        TransactionResult exec = redis.exec();
        List<Value<String>> geohash = exec.get(1);
        assertThat(geohash).containsSequence(Value.just("u0y1v0kffz0"), Value.just("u0y1vhvuvm0"), Value.empty());
    }

    @Test
    public void georadiusbymemberWithArgsInTransaction() {
        prepareGeo();
        redis.multi();
        redis.georadiusbymember(TestSupport.key, "Bahn", 1, km, new GeoArgs().withHash().withCoordinates().withDistance().desc());
        redis.georadiusbymember(TestSupport.key, "Bahn", 5, km, new GeoArgs().withCoordinates().withDistance().desc());
        redis.georadiusbymember(TestSupport.key, "Bahn", 5, km, new GeoArgs().withDistance().withHash().desc());
        redis.georadiusbymember(TestSupport.key, "Bahn", 5, km, new GeoArgs().withCoordinates().desc());
        TransactionResult exec = redis.exec();
        List<GeoWithin<String>> empty = exec.get(0);
        assertThat(empty).isNotEmpty();
        List<GeoWithin<String>> withDistanceAndCoordinates = exec.get(1);
        assertThat(withDistanceAndCoordinates).hasSize(2);
        GeoWithin<String> weinheim = withDistanceAndCoordinates.get(0);
        assertThat(weinheim.getMember()).isEqualTo("Weinheim");
        assertThat(weinheim.getGeohash()).isNull();
        assertThat(weinheim.getDistance()).isNotNull();
        assertThat(weinheim.getCoordinates()).isNotNull();
        List<GeoWithin<String>> withDistanceAndHash = exec.get(2);
        assertThat(withDistanceAndHash).hasSize(2);
        GeoWithin<String> weinheimDistanceHash = withDistanceAndHash.get(0);
        assertThat(weinheimDistanceHash.getMember()).isEqualTo("Weinheim");
        assertThat(weinheimDistanceHash.getGeohash()).isNotNull();
        assertThat(weinheimDistanceHash.getDistance()).isNotNull();
        assertThat(weinheimDistanceHash.getCoordinates()).isNull();
        List<GeoWithin<String>> withCoordinates = exec.get(3);
        assertThat(withCoordinates).hasSize(2);
        GeoWithin<String> weinheimCoordinates = withCoordinates.get(0);
        assertThat(weinheimCoordinates.getMember()).isEqualTo("Weinheim");
        assertThat(weinheimCoordinates.getGeohash()).isNull();
        assertThat(weinheimCoordinates.getDistance()).isNull();
        assertThat(weinheimCoordinates.getCoordinates()).isNotNull();
    }
}

