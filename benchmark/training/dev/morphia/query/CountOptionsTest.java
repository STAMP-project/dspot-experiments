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


import ReadConcern.LOCAL;
import com.mongodb.ReadPreference;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.DBCollectionCountOptions;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class CountOptionsTest {
    @Test
    public void passThrough() {
        Collation collation = Collation.builder().locale("en").caseLevel(true).build();
        DBCollectionCountOptions options = new CountOptions().collation(collation).hint("i'm a hint").limit(18).maxTime(15, TimeUnit.MINUTES).readPreference(ReadPreference.secondaryPreferred()).readConcern(LOCAL).skip(12).getOptions();
        Assert.assertEquals(collation, options.getCollation());
        Assert.assertEquals("i'm a hint", options.getHintString());
        Assert.assertEquals(18, options.getLimit());
        Assert.assertEquals(15, options.getMaxTime(TimeUnit.MINUTES));
        Assert.assertEquals(ReadPreference.secondaryPreferred(), options.getReadPreference());
        Assert.assertEquals(LOCAL, options.getReadConcern());
        Assert.assertEquals(12, options.getSkip());
    }
}

