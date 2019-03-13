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


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.MongoOperations;


/**
 *
 *
 * @author Glenn Renfro
 */
public class MongoItemWriterBuilderTests {
    @Mock
    private MongoOperations template;

    private List<String> items;

    @Test
    public void testBasicWrite() throws Exception {
        MongoItemWriter<String> writer = new MongoItemWriterBuilder<String>().template(this.template).build();
        writer.write(this.items);
        Mockito.verify(this.template).save(this.items.get(0));
        Mockito.verify(this.template).save(this.items.get(1));
        Mockito.verify(this.template, Mockito.never()).remove(this.items.get(0));
        Mockito.verify(this.template, Mockito.never()).remove(this.items.get(1));
    }

    @Test
    public void testDelete() throws Exception {
        MongoItemWriter<String> writer = new MongoItemWriterBuilder<String>().template(this.template).delete(true).build();
        writer.write(this.items);
        Mockito.verify(this.template).remove(this.items.get(0));
        Mockito.verify(this.template).remove(this.items.get(1));
        Mockito.verify(this.template, Mockito.never()).save(this.items.get(0));
        Mockito.verify(this.template, Mockito.never()).save(this.items.get(1));
    }

    @Test
    public void testWriteToCollection() throws Exception {
        MongoItemWriter<String> writer = new MongoItemWriterBuilder<String>().collection("collection").template(this.template).build();
        writer.write(this.items);
        Mockito.verify(this.template).save(this.items.get(0), "collection");
        Mockito.verify(this.template).save(this.items.get(1), "collection");
        Mockito.verify(this.template, Mockito.never()).remove(this.items.get(0), "collection");
        Mockito.verify(this.template, Mockito.never()).remove(this.items.get(1), "collection");
    }

    @Test
    public void testNullTemplate() {
        try {
            new MongoItemWriterBuilder().build();
            Assert.fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("IllegalArgumentException message did not match the expected result.", "template is required.", iae.getMessage());
        }
    }
}

