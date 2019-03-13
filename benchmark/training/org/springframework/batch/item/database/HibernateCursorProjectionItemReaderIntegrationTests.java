/**
 * Copyright 2008-2012 the original author or authors.
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
package org.springframework.batch.item.database;


import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Tests for {@link HibernateCursorItemReader} using {@link StatelessSession}.
 *
 * @author Robert Kasanicky
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "data-source-context.xml")
public class HibernateCursorProjectionItemReaderIntegrationTests {
    @Autowired
    private DataSource dataSource;

    @Test
    public void testMultipleItemsInProjection() throws Exception {
        HibernateCursorItemReader<Object[]> reader = new HibernateCursorItemReader();
        initializeItemReader(reader, "select f.value, f.name from Foo f");
        Object[] foo1 = reader.read();
        Assert.assertEquals(1, foo1[0]);
    }

    @Test
    public void testSingleItemInProjection() throws Exception {
        HibernateCursorItemReader<Object> reader = new HibernateCursorItemReader();
        initializeItemReader(reader, "select f.value from Foo f");
        Object foo1 = reader.read();
        Assert.assertEquals(1, foo1);
    }

    @Test
    public void testSingleItemInProjectionWithArrayType() throws Exception {
        HibernateCursorItemReader<Object[]> reader = new HibernateCursorItemReader();
        initializeItemReader(reader, "select f.value from Foo f");
        try {
            Object[] foo1 = reader.read();
            Assert.assertNotNull(foo1);
            Assert.fail("Expected ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
    }
}

