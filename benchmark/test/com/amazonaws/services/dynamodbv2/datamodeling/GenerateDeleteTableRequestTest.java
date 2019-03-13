/**
 * Copyright (c) 2016. Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.services.dynamodbv2.datamodeling;


import DynamoDBMapperConfig.TableNameOverride;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link DynamoDBMapper#generateDeleteTableRequest(Class)}.
 */
public class GenerateDeleteTableRequestTest {
    private static final String TABLE_PREFIX = "DEV-";

    private static final String TABLE_NAME = "OBJECTORMEXAMPLE";

    @Test
    public void tableNameNotOverriden_UsesTableNameAttributeInAnnotation() {
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(null);
        DeleteTableRequest deleteTableRequest = dynamoDBMapper.generateDeleteTableRequest(GenerateDeleteTableRequestTest.ObjectORMExample.class);
        Assert.assertEquals(deleteTableRequest.getTableName(), GenerateDeleteTableRequestTest.TABLE_NAME);
    }

    @Test
    public void tableNameOverriddenInConfig_UsesPrefixedOverrideTableName() {
        DynamoDBMapperConfig.TableNameOverride tableNameOverride = TableNameOverride.withTableNamePrefix(GenerateDeleteTableRequestTest.TABLE_PREFIX);
        DynamoDBMapperConfig config = new DynamoDBMapperConfig(tableNameOverride);
        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(null, config);
        DeleteTableRequest deleteTableRequest = dynamoDBMapper.generateDeleteTableRequest(GenerateDeleteTableRequestTest.ObjectORMExample.class);
        Assert.assertEquals(deleteTableRequest.getTableName(), GenerateDeleteTableRequestTest.TABLE_PREFIX.concat(GenerateDeleteTableRequestTest.TABLE_NAME));
    }

    @DynamoDBTable(tableName = GenerateDeleteTableRequestTest.TABLE_NAME)
    private static class ObjectORMExample {
        private String id;

        @DynamoDBHashKey
        public final String getId() {
            return this.id;
        }

        public final void setId(String id) {
            this.id = id;
        }
    }
}

