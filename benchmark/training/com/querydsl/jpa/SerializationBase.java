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


import com.querydsl.core.QueryMetadata;
import com.querydsl.core.testutil.Serialization;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.testutil.JPATestRunner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.persistence.EntityManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JPATestRunner.class)
public class SerializationBase implements JPATest {
    private QCat cat = QCat.cat;

    private EntityManager entityManager;

    @Test
    public void test() throws IOException, ClassNotFoundException {
        // create query
        JPAQuery<?> query = query();
        query.from(cat).where(cat.name.eq("Kate")).select(cat).fetch();
        QueryMetadata metadata = query.getMetadata();
        Assert.assertFalse(metadata.getJoins().isEmpty());
        Assert.assertTrue(((metadata.getWhere()) != null));
        Assert.assertTrue(((metadata.getProjection()) != null));
        QueryMetadata metadata2 = Serialization.serialize(metadata);
        // validate it
        Assert.assertEquals(metadata.getJoins(), metadata2.getJoins());
        Assert.assertEquals(metadata.getWhere(), metadata2.getWhere());
        Assert.assertEquals(metadata.getProjection(), metadata2.getProjection());
        // create new query
        JPAQuery<?> query2 = new JPAQuery<Void>(entityManager, metadata2);
        Assert.assertEquals("select cat\nfrom Cat cat\nwhere cat.name = ?1", query2.toString());
        query2.select(cat).fetch();
    }

    @Test
    public void any_serialized() throws Exception {
        Predicate where = cat.kittens.any().name.eq("Ruth234");
        Predicate where2 = Serialization.serialize(where);
        Assert.assertEquals(0, query().from(cat).where(where).fetchCount());
        Assert.assertEquals(0, query().from(cat).where(where2).fetchCount());
    }

    @Test
    public void any_serialized2() throws Exception {
        Predicate where = cat.kittens.any().name.eq("Ruth234");
        File file = new File("target", "predicate.ser");
        if (!(file.exists())) {
            // serialize predicate on first run
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
            out.writeObject(where);
            out.close();
            Assert.assertEquals(0, query().from(cat).where(where).fetchCount());
        } else {
            // deserialize predicate on second run
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileInputStream);
            Predicate where2 = ((Predicate) (in.readObject()));
            in.close();
            Assert.assertEquals(0, query().from(cat).where(where2).fetchCount());
        }
    }
}

