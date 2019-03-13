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
package com.querydsl.jpa;


import JoinType.DEFAULT;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.domain.sql.SAnimal;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.MySQLTemplates;
import javax.persistence.Column;
import org.junit.Assert;
import org.junit.Test;


public class NativeSQLSerializerTest {
    public static class Entity {
        @Column
        private String name;

        @Column(name = "first_name")
        private String firstName;
    }

    @Test
    public void in() {
        Configuration conf = new Configuration(new MySQLTemplates());
        NativeSQLSerializer serializer = new NativeSQLSerializer(conf, true);
        DefaultQueryMetadata md = new DefaultQueryMetadata();
        SAnimal cat = SAnimal.animal_;
        md.addJoin(DEFAULT, cat);
        md.addWhere(cat.name.in("X", "Y"));
        md.setProjection(cat.id);
        serializer.serialize(md, false);
        Assert.assertEquals(("select animal_.id\n" + ("from animal_ animal_\n" + "where animal_.name in (?1, ?2)")), serializer.toString());
    }

    @Test
    public void path_column() {
        PathBuilder<NativeSQLSerializerTest.Entity> entity = new PathBuilder<NativeSQLSerializerTest.Entity>(NativeSQLSerializerTest.Entity.class, "entity");
        Configuration conf = new Configuration(new MySQLTemplates());
        NativeSQLSerializer serializer = new NativeSQLSerializer(conf, true);
        serializer.handle(entity.get("name"));
        Assert.assertEquals("entity.name", serializer.toString());
    }

    @Test
    public void path_column2() {
        PathBuilder<NativeSQLSerializerTest.Entity> entity = new PathBuilder<NativeSQLSerializerTest.Entity>(NativeSQLSerializerTest.Entity.class, "entity");
        Configuration conf = new Configuration(new MySQLTemplates());
        NativeSQLSerializer serializer = new NativeSQLSerializer(conf, true);
        serializer.handle(entity.get("firstName"));
        Assert.assertEquals("entity.first_name", serializer.toString());
    }
}

