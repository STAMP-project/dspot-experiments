/**
 * Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.springframework.batch.item.data.builder;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.batch.item.data.Neo4jItemWriter;


/**
 *
 *
 * @author Glenn Renfro
 */
public class Neo4jItemWriterBuilderTests {
    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Test
    public void testBasicWriter() throws Exception {
        Neo4jItemWriter<String> writer = new Neo4jItemWriterBuilder<String>().sessionFactory(this.sessionFactory).build();
        List<String> items = new ArrayList<>();
        items.add("foo");
        items.add("bar");
        Mockito.when(this.sessionFactory.openSession()).thenReturn(this.session);
        writer.write(items);
        Mockito.verify(this.session).save("foo");
        Mockito.verify(this.session).save("bar");
        Mockito.verify(this.session, Mockito.never()).delete("foo");
        Mockito.verify(this.session, Mockito.never()).delete("bar");
    }

    @Test
    public void testBasicDelete() throws Exception {
        Neo4jItemWriter<String> writer = new Neo4jItemWriterBuilder<String>().delete(true).sessionFactory(this.sessionFactory).build();
        List<String> items = new ArrayList<>();
        items.add("foo");
        items.add("bar");
        Mockito.when(this.sessionFactory.openSession()).thenReturn(this.session);
        writer.write(items);
        Mockito.verify(this.session).delete("foo");
        Mockito.verify(this.session).delete("bar");
        Mockito.verify(this.session, Mockito.never()).save("foo");
        Mockito.verify(this.session, Mockito.never()).save("bar");
    }

    @Test
    public void testNoSessionFactory() {
        try {
            new Neo4jItemWriterBuilder<String>().build();
            Assert.fail("SessionFactory was not set but exception was not thrown.");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("sessionFactory is required.", iae.getMessage());
        }
    }
}

