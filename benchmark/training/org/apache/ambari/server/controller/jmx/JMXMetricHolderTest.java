/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.controller.jmx;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.matchers.IsCollectionContaining;


public class JMXMetricHolderTest {
    private JMXMetricHolder metrics = new JMXMetricHolder();

    @Test
    public void testFindSingleBeanByName() throws Exception {
        Assert.assertThat(metrics.find("bean1/value"), Is.is(Optional.of("val1")));
        Assert.assertThat(metrics.find("bean2/value"), Is.is(Optional.of("val2")));
        Assert.assertThat(metrics.find("bean3/notfound"), Is.is(Optional.empty()));
    }

    @Test
    public void testFindMultipleBeansByName() throws Exception {
        List<Object> result = metrics.findAll(Arrays.asList("bean1/value", "bean2/value", "bean3/notfound"));
        Assert.assertThat(result, IsCollectionContaining.hasItems("val1", "val2"));
    }

    @Test
    public void testFindNestedBean() throws Exception {
        List<Object> result = metrics.findAll(Arrays.asList("nested/value[key1]", "nested/value[key2]"));
        Assert.assertThat(result, IsCollectionContaining.hasItems("nested-val1", "nested-val2"));
    }
}

