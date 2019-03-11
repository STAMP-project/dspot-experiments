/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.service;


import CassandraSessionProvider.CLIENT_AUTH;
import CassandraSessionProvider.CONSISTENCY_LEVEL;
import CassandraSessionProvider.CONTACT_POINTS;
import CassandraSessionProvider.KEYSPACE;
import CassandraSessionProvider.PASSWORD;
import CassandraSessionProvider.PROP_SSL_CONTEXT_SERVICE;
import CassandraSessionProvider.USERNAME;
import java.util.List;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestCassandraSessionProvider {
    private static TestRunner runner;

    private static CassandraSessionProvider sessionProvider;

    @Test
    public void testGetPropertyDescriptors() {
        List<PropertyDescriptor> properties = TestCassandraSessionProvider.sessionProvider.getPropertyDescriptors();
        Assert.assertEquals(7, properties.size());
        Assert.assertTrue(properties.contains(CLIENT_AUTH));
        Assert.assertTrue(properties.contains(CONSISTENCY_LEVEL));
        Assert.assertTrue(properties.contains(CONTACT_POINTS));
        Assert.assertTrue(properties.contains(KEYSPACE));
        Assert.assertTrue(properties.contains(PASSWORD));
        Assert.assertTrue(properties.contains(PROP_SSL_CONTEXT_SERVICE));
        Assert.assertTrue(properties.contains(USERNAME));
    }
}

