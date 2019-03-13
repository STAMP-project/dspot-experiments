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
package dev.morphia.query;


import CursorType.TailableAwait;
import ReadConcern.LOCAL;
import com.mongodb.BasicDBObject;
import com.mongodb.ReadPreference;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.DBCollectionFindOptions;
import org.junit.Assert;
import org.junit.Test;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;


public class FindOptionsTest {
    @Test
    public void passThrough() {
        Collation collation = Collation.builder().locale("en").caseLevel(true).build();
        DBCollectionFindOptions options = new FindOptions().batchSize(42).limit(18).modifier("i'm a", "modifier").modifier("i am", 2).projection(new BasicDBObject("field", "value")).maxTime(15, MINUTES).maxAwaitTime(45, SECONDS).skip(12).sort(new BasicDBObject("field", (-1))).cursorType(TailableAwait).noCursorTimeout(true).oplogReplay(true).partial(true).readPreference(ReadPreference.secondaryPreferred(2, MINUTES)).readConcern(LOCAL).collation(collation).getOptions();
        Assert.assertEquals(42, options.getBatchSize());
        Assert.assertEquals(18, options.getLimit());
        Assert.assertEquals(new BasicDBObject("i'm a", "modifier").append("i am", 2), options.getModifiers());
        Assert.assertEquals(new BasicDBObject("field", "value"), options.getProjection());
        Assert.assertEquals(15, options.getMaxTime(MINUTES));
        Assert.assertEquals(45, options.getMaxAwaitTime(SECONDS));
        Assert.assertEquals(12, options.getSkip());
        Assert.assertEquals(new BasicDBObject("field", (-1)), options.getSort());
        Assert.assertEquals(TailableAwait, options.getCursorType());
        Assert.assertTrue(options.isNoCursorTimeout());
        Assert.assertTrue(options.isOplogReplay());
        Assert.assertTrue(options.isPartial());
        Assert.assertEquals(ReadPreference.secondaryPreferred(2, MINUTES), options.getReadPreference());
        Assert.assertEquals(LOCAL, options.getReadConcern());
        Assert.assertEquals(collation, options.getCollation());
    }
}

