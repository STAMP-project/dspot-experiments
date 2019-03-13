/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.processor;


import java.io.File;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.IdempotentRepository;
import org.junit.Assert;
import org.junit.Test;


public class FileIdempotentConsumerLoadStoreTest extends ContextTestSupport {
    protected Endpoint startEndpoint;

    protected MockEndpoint resultEndpoint;

    private File store = new File("target/data/idempotentfilestore.dat");

    private IdempotentRepository repo;

    @Test
    public void testLoadStore() throws Exception {
        Assert.assertFalse(repo.contains("1"));
        Assert.assertFalse(repo.contains("2"));
        Assert.assertFalse(repo.contains("3"));
        Assert.assertTrue(repo.contains("4"));
        resultEndpoint.expectedBodiesReceived("one", "two", "three");
        sendMessage("1", "one");
        sendMessage("2", "two");
        sendMessage("1", "one");
        sendMessage("2", "two");
        sendMessage("4", "four");
        sendMessage("1", "one");
        sendMessage("3", "three");
        resultEndpoint.assertIsSatisfied();
        Assert.assertTrue(repo.contains("1"));
        Assert.assertTrue(repo.contains("2"));
        Assert.assertTrue(repo.contains("3"));
        Assert.assertTrue(repo.contains("4"));
    }
}

