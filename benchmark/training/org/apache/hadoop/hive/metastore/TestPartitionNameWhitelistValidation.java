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
package org.apache.hadoop.hive.metastore;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


// Validate the metastore client call validatePartitionNameCharacters to ensure it throws
// an exception if partition fields contain Unicode characters or commas
@Category(MetastoreUnitTest.class)
public class TestPartitionNameWhitelistValidation {
    private static final String partitionValidationPattern = "[\\x20-\\x7E&&[^,]]*";

    private static Configuration conf;

    private static HiveMetaStoreClient msc;

    @Test
    public void testAddPartitionWithCommas() {
        Assert.assertFalse("Add a partition with commas in name", runValidation(getPartValsWithCommas()));
    }

    @Test
    public void testAddPartitionWithUnicode() {
        Assert.assertFalse("Add a partition with unicode characters in name", runValidation(getPartValsWithUnicode()));
    }

    @Test
    public void testAddPartitionWithValidPartVal() {
        Assert.assertTrue("Add a partition with unicode characters in name", runValidation(getPartValsWithValidCharacters()));
    }

    @Test
    public void testAppendPartitionWithUnicode() {
        Assert.assertFalse("Append a partition with unicode characters in name", runValidation(getPartValsWithUnicode()));
    }

    @Test
    public void testAppendPartitionWithCommas() {
        Assert.assertFalse("Append a partition with unicode characters in name", runValidation(getPartValsWithCommas()));
    }

    @Test
    public void testAppendPartitionWithValidCharacters() {
        Assert.assertTrue("Append a partition with no unicode characters in name", runValidation(getPartValsWithValidCharacters()));
    }
}

