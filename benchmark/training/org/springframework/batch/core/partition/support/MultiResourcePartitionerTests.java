/**
 * Copyright 2009-2017 the original author or authors.
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
package org.springframework.batch.core.partition.support;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;


public class MultiResourcePartitionerTests {
    private MultiResourcePartitioner partitioner = new MultiResourcePartitioner();

    @Test(expected = IllegalStateException.class)
    public void testMissingResource() {
        partitioner.setResources(new Resource[]{ new FileSystemResource("does-not-exist") });
        partitioner.partition(0);
    }

    @Test
    public void testPartitionSizeAndKey() {
        Map<String, ExecutionContext> partition = partitioner.partition(0);
        Assert.assertEquals(1, partition.size());
        Assert.assertTrue(partition.containsKey("partition0"));
    }

    @Test
    public void testReadFile() throws Exception {
        Map<String, ExecutionContext> partition = partitioner.partition(0);
        String url = partition.get("partition0").getString("fileName");
        Assert.assertTrue(new UrlResource(url).exists());
    }

    @Test
    public void testSetKeyName() {
        partitioner.setKeyName("foo");
        Map<String, ExecutionContext> partition = partitioner.partition(0);
        Assert.assertTrue(partition.get("partition0").containsKey("foo"));
    }
}

