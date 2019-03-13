/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.mongodb;


import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.querydsl.core.testutil.MongoDB;
import com.querydsl.mongodb.domain.GeoEntity;
import com.querydsl.mongodb.domain.QGeoEntity;
import java.net.UnknownHostException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;


@Category(MongoDB.class)
public class GeoSpatialQueryTest {
    private final String dbname = "geodb";

    private final MongoClient mongo;

    private final Morphia morphia;

    private final Datastore ds;

    private final QGeoEntity geoEntity = new QGeoEntity("geoEntity");

    public GeoSpatialQueryTest() throws MongoException, UnknownHostException {
        mongo = new MongoClient();
        morphia = new Morphia().map(GeoEntity.class);
        ds = morphia.createDatastore(mongo, dbname);
    }

    @Test
    public void near() {
        ds.save(new GeoEntity(10.0, 50.0));
        ds.save(new GeoEntity(20.0, 50.0));
        ds.save(new GeoEntity(30.0, 50.0));
        List<GeoEntity> entities = query().where(geoEntity.location.near(50.0, 50.0)).fetch();
        Assert.assertEquals(30.0, entities.get(0).getLocation()[0], 0.1);
        Assert.assertEquals(20.0, entities.get(1).getLocation()[0], 0.1);
        Assert.assertEquals(10.0, entities.get(2).getLocation()[0], 0.1);
    }

    @Test
    public void near_sphere() {
        ds.save(new GeoEntity(10.0, 50.0));
        ds.save(new GeoEntity(20.0, 50.0));
        ds.save(new GeoEntity(30.0, 50.0));
        List<GeoEntity> entities = query().where(MongodbExpressions.nearSphere(geoEntity.location, 50.0, 50.0)).fetch();
        Assert.assertEquals(30.0, entities.get(0).getLocation()[0], 0.1);
        Assert.assertEquals(20.0, entities.get(1).getLocation()[0], 0.1);
        Assert.assertEquals(10.0, entities.get(2).getLocation()[0], 0.1);
    }
}

