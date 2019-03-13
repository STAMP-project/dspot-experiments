/**
 * Copyright 2018 Naver Corp.
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
package com.navercorp.pinpoint.plugin.mongodb;


import WriteConcern.ACKNOWLEDGED;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifierHolder;
import de.flapdoodle.embed.mongo.MongodProcess;
import org.bson.Document;
import org.junit.Test;


/**
 *
 *
 * @author Roy Kim
 */
public abstract class MongoDBITBase {
    protected static final String MONGO = "MONGO";

    protected static final String MONGO_EXECUTE_QUERY = "MONGO_EXECUTE_QUERY";

    public static MongoDatabase database;

    public static String secondCollectionDefaultOption = "ACKNOWLEDGED";

    public static double version = 0;

    public static boolean version_over_3_4_0 = true;

    public static boolean version_over_3_7_0 = true;

    protected static String MONGODB_ADDRESS = "localhost:" + 27018;

    private static String OS = System.getProperty("os.name").toLowerCase();

    MongodProcess mongod;

    @Test
    public void testConnection() throws Exception {
        if (MongoDBITBase.isWindows()) {
            return;
        }
        startDB();
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        MongoCollection<Document> collection = MongoDBITBase.database.getCollection("customers");
        MongoCollection<Document> collection2 = MongoDBITBase.database.getCollection("customers2").withWriteConcern(ACKNOWLEDGED);
        Class<?> mongoDatabaseImpl;
        if ((MongoDBITBase.version) >= 3.7) {
            mongoDatabaseImpl = Class.forName("com.mongodb.client.internal.MongoCollectionImpl");
        } else {
            mongoDatabaseImpl = Class.forName("com.mongodb.MongoCollectionImpl");
        }
        if ((MongoDBITBase.version) >= 3.4) {
            insertComlexBsonValueData34(verifier, collection, mongoDatabaseImpl, "customers", "MAJORITY");
        } else {
            insertComlexBsonValueData30(verifier, collection, mongoDatabaseImpl, "customers", "MAJORITY");
        }
        insertData(verifier, collection, mongoDatabaseImpl, "customers", "MAJORITY");
        insertData(verifier, collection2, mongoDatabaseImpl, "customers2", MongoDBITBase.secondCollectionDefaultOption);
        updateData(verifier, collection, mongoDatabaseImpl);
        readData(verifier, collection, mongoDatabaseImpl);
        filterData(verifier, collection, mongoDatabaseImpl);
        filterData2(verifier, collection, mongoDatabaseImpl);
        deleteData(verifier, collection, mongoDatabaseImpl);
        stopDB();
    }
}

