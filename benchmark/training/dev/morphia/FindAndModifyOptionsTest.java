/**
 * Copyright 2016 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.morphia;


import WriteConcern.JOURNALED;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.DBCollectionFindAndModifyOptions;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class FindAndModifyOptionsTest {
    @Test
    public void passThrough() {
        Collation collation = Collation.builder().locale("en").caseLevel(true).build();
        DBCollectionFindAndModifyOptions options = new FindAndModifyOptions().bypassDocumentValidation(true).collation(collation).getOptions().maxTime(15, TimeUnit.MINUTES).projection(new BasicDBObject("field", "value")).remove(true).returnNew(true).sort(new BasicDBObject("field", (-1))).update(new BasicDBObject("$inc", "somefield")).upsert(true).writeConcern(JOURNALED);
        Assert.assertTrue(options.getBypassDocumentValidation());
        Assert.assertEquals(collation, options.getCollation());
        Assert.assertEquals(15, options.getMaxTime(TimeUnit.MINUTES));
        Assert.assertEquals(new BasicDBObject("field", "value"), options.getProjection());
        Assert.assertTrue(options.isRemove());
        Assert.assertTrue(options.returnNew());
        Assert.assertEquals(new BasicDBObject("field", (-1)), options.getSort());
        Assert.assertEquals(new BasicDBObject("$inc", "somefield"), options.getUpdate());
        Assert.assertTrue(options.isUpsert());
        Assert.assertEquals(JOURNALED, options.getWriteConcern());
    }
}

