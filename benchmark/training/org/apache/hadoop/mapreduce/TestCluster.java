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
package org.apache.hadoop.mapreduce;


import java.util.Iterator;
import java.util.ServiceConfigurationError;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static Cluster.frameworkLoader;


/**
 * Testing the Cluster initialization.
 */
public class TestCluster {
    @Test
    @SuppressWarnings("unchecked")
    public void testProtocolProviderCreation() throws Exception {
        Iterator iterator = Mockito.mock(Iterator.class);
        Mockito.when(iterator.hasNext()).thenReturn(true, true, true, true);
        Mockito.when(iterator.next()).thenReturn(getClientProtocolProvider()).thenThrow(new ServiceConfigurationError("Test error")).thenReturn(getClientProtocolProvider());
        Iterable frameworkLoader = Mockito.mock(Iterable.class);
        Mockito.when(frameworkLoader.iterator()).thenReturn(iterator);
        frameworkLoader = frameworkLoader;
        Cluster testCluster = new Cluster(new Configuration());
        // Check that we get the acceptable client, even after
        // failure in instantiation.
        Assert.assertNotNull("ClientProtocol is expected", testCluster.getClient());
        // Check if we do not try to load the providers after a failure.
        Mockito.verify(iterator, Mockito.times(2)).next();
    }
}

