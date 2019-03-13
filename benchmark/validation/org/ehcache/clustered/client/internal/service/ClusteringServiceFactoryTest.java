/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.clustered.client.internal.service;


import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.ehcache.core.spi.service.ServiceFactory;
import org.ehcache.core.util.ClassLoading;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Clifford W. Johnson
 */
public class ClusteringServiceFactoryTest {
    @Test
    public void testServiceLocator() throws Exception {
        Assert.assertThat(StreamSupport.stream(Spliterators.spliterator(ClassLoading.servicesOfType(ServiceFactory.class).iterator(), Long.MAX_VALUE, 0), false).map(Object::getClass).collect(Collectors.toList()), IsCollectionContaining.hasItem(ClusteringServiceFactory.class));
    }
}

