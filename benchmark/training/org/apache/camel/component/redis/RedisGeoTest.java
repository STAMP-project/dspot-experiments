/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.redis;


import Command.GEOADD;
import Command.GEODIST;
import Command.GEOHASH;
import Command.GEOPOS;
import Command.GEORADIUS;
import Command.GEORADIUSBYMEMBER;
import RedisConstants.COMMAND;
import RedisConstants.COUNT;
import RedisConstants.KEY;
import RedisConstants.LATITUDE;
import RedisConstants.LONGITUDE;
import RedisConstants.RADIUS;
import RedisConstants.VALUE;
import RedisConstants.VALUES;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;


@RunWith(MockitoJUnitRunner.class)
public class RedisGeoTest extends RedisTestSupport {
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private GeoOperations<String, String> geoOperations;

    @Test
    public void shouldExecuteGEOADD() throws Exception {
        sendHeaders(COMMAND, GEOADD, KEY, "Sicily", LONGITUDE, 13.361389, LATITUDE, 38.115556, VALUE, "Palermo");
        Mockito.verify(redisTemplate).opsForGeo();
        Mockito.verify(geoOperations).add("Sicily", new Point(13.361389, 38.115556), "Palermo");
    }

    @Test
    public void shouldExecuteGEODIST() throws Exception {
        Object[] members = new String[]{ "Palermo", "Catania" };
        sendHeaders(COMMAND, GEODIST, KEY, "Sicily", VALUES, members);
        Mockito.verify(redisTemplate).opsForGeo();
        Mockito.verify(geoOperations).distance("Sicily", "Palermo", "Catania");
    }

    @Test
    public void shouldExecuteGEOHASH() throws Exception {
        sendHeaders(COMMAND, GEOHASH, KEY, "Sicily", VALUE, "Palermo");
        Mockito.verify(redisTemplate).opsForGeo();
        Mockito.verify(geoOperations).hash("Sicily", "Palermo");
    }

    @Test
    public void shouldExecuteGEOPOS() throws Exception {
        sendHeaders(COMMAND, GEOPOS, KEY, "Sicily", VALUE, "Palermo");
        Mockito.verify(redisTemplate).opsForGeo();
        Mockito.verify(geoOperations).position("Sicily", "Palermo");
    }

    @Test
    public void shouldExecuteGEORADIUS() throws Exception {
        sendHeaders(COMMAND, GEORADIUS, KEY, "Sicily", LONGITUDE, 13.361389, LATITUDE, 38.115556, RADIUS, 200000, COUNT, 10);
        Mockito.verify(redisTemplate).opsForGeo();
        Mockito.verify(geoOperations).radius(ArgumentMatchers.eq("Sicily"), ArgumentMatchers.eq(new org.springframework.data.geo.Circle(new Point(13.361389, 38.115556), 200000)), ArgumentMatchers.any(GeoRadiusCommandArgs.class));
    }

    @Test
    public void shouldExecuteGEORADIUSBYMEMBER() throws Exception {
        sendHeaders(COMMAND, GEORADIUSBYMEMBER, KEY, "Sicily", VALUE, "Palermo", RADIUS, 200000);
        Mockito.verify(redisTemplate).opsForGeo();
        Mockito.verify(geoOperations).radius(ArgumentMatchers.eq("Sicily"), ArgumentMatchers.eq("Palermo"), ArgumentMatchers.eq(new Distance(200000)), ArgumentMatchers.any(GeoRadiusCommandArgs.class));
    }
}

