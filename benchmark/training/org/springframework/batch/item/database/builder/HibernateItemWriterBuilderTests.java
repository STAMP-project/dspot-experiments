/**
 * Copyright 2017 the original author or authors.
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
package org.springframework.batch.item.database.builder;


import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.item.database.HibernateItemWriter;
import org.springframework.batch.item.sample.Foo;


/**
 *
 *
 * @author Michael Minella
 */
public class HibernateItemWriterBuilderTests {
    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Test
    public void testConfiguration() {
        HibernateItemWriter<Foo> itemWriter = new HibernateItemWriterBuilder<Foo>().sessionFactory(this.sessionFactory).build();
        itemWriter.afterPropertiesSet();
        List<Foo> foos = getFoos();
        itemWriter.write(foos);
        Mockito.verify(this.session).saveOrUpdate(foos.get(0));
        Mockito.verify(this.session).saveOrUpdate(foos.get(1));
        Mockito.verify(this.session).saveOrUpdate(foos.get(2));
    }

    @Test
    public void testConfigurationClearSession() {
        HibernateItemWriter<Foo> itemWriter = new HibernateItemWriterBuilder<Foo>().sessionFactory(this.sessionFactory).clearSession(false).build();
        itemWriter.afterPropertiesSet();
        List<Foo> foos = getFoos();
        itemWriter.write(foos);
        Mockito.verify(this.session).saveOrUpdate(foos.get(0));
        Mockito.verify(this.session).saveOrUpdate(foos.get(1));
        Mockito.verify(this.session).saveOrUpdate(foos.get(2));
        Mockito.verify(this.session, Mockito.never()).clear();
    }

    @Test
    public void testValidation() {
        try {
            new HibernateItemWriterBuilder<Foo>().build();
            Assert.fail("sessionFactory is required");
        } catch (IllegalStateException ise) {
            Assert.assertEquals("Incorrect message", "SessionFactory must be provided", ise.getMessage());
        }
    }
}

