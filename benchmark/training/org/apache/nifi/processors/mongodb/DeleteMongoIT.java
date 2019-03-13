/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.nifi.processors.mongodb;


import DeleteMongo.CLIENT_SERVICE;
import DeleteMongo.DELETE_ATTR;
import DeleteMongo.DELETE_MANY;
import DeleteMongo.DELETE_MODE;
import DeleteMongo.DELETE_ONE;
import DeleteMongo.FAIL_ON_NO_DELETE;
import DeleteMongo.NO_FAIL;
import DeleteMongo.REL_FAILURE;
import DeleteMongo.REL_SUCCESS;
import DeleteMongo.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.mongodb.MongoDBClientService;
import org.apache.nifi.mongodb.MongoDBControllerService;
import org.apache.nifi.util.TestRunner;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;


public class DeleteMongoIT extends MongoWriteTestBase {
    @Test
    public void testDeleteOne() {
        TestRunner runner = init(DeleteMongo.class);
        String query = "{ \"_id\": \"doc_1\" }";
        runner.setProperty(DELETE_MODE, DELETE_ONE);
        testOne(runner, query, new HashMap());
        Map<String, String> attrs = new HashMap<>();
        attrs.put("mongodb.delete.mode", "one");
        runner.setProperty(DELETE_MODE, DELETE_ATTR);
        query = "{ \"_id\": \"doc_2\" }";
        runner.clearTransferState();
        testOne(runner, query, attrs);
    }

    @Test
    public void testDeleteMany() {
        TestRunner runner = init(DeleteMongo.class);
        String query = "{\n" + ((("\t\"_id\": {\n" + "\t\t\"$in\": [\"doc_1\", \"doc_2\"]\n") + "\t}\n") + "}");
        runner.setProperty(DELETE_MODE, DELETE_MANY);
        manyTest(runner, query, new HashMap());
        runner.setProperty(DELETE_MODE, DELETE_ATTR);
        Map<String, String> attrs = new HashMap<>();
        attrs.put("mongodb.delete.mode", "many");
        collection.drop();
        collection.insertMany(MongoWriteTestBase.DOCUMENTS);
        runner.clearTransferState();
        manyTest(runner, query, attrs);
    }

    @Test
    public void testFailOnNoDeleteOptions() {
        TestRunner runner = init(DeleteMongo.class);
        String query = "{ \"_id\": \"doc_4\"} ";
        runner.enqueue(query);
        runner.run(1, true);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_SUCCESS, 0);
        Assert.assertEquals("A document was deleted", 3, collection.count(Document.parse("{}")));
        runner.setProperty(FAIL_ON_NO_DELETE, NO_FAIL);
        runner.clearTransferState();
        runner.enqueue(query);
        runner.run(1, true, true);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        Assert.assertEquals("A document was deleted", 3, collection.count(Document.parse("{}")));
    }

    @Test
    public void testClientService() throws Exception {
        MongoDBClientService clientService = new MongoDBControllerService();
        TestRunner runner = init(DeleteMongo.class);
        runner.addControllerService("clientService", clientService);
        runner.removeProperty(URI);
        runner.setProperty(DELETE_MODE, DELETE_MANY);
        runner.setProperty(clientService, MongoDBControllerService.URI, MongoWriteTestBase.MONGO_URI);
        runner.setProperty(CLIENT_SERVICE, "clientService");
        runner.enableControllerService(clientService);
        runner.assertValid();
        runner.enqueue("{}");
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        Assert.assertEquals(0, collection.count());
    }
}

