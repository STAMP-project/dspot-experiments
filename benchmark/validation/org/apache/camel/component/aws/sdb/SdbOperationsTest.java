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
package org.apache.camel.component.aws.sdb;


import SdbOperations.BatchDeleteAttributes;
import SdbOperations.BatchPutAttributes;
import SdbOperations.DeleteAttributes;
import SdbOperations.DeleteDomain;
import SdbOperations.DomainMetadata;
import SdbOperations.GetAttributes;
import SdbOperations.ListDomains;
import SdbOperations.PutAttributes;
import SdbOperations.Select;
import org.junit.Assert;
import org.junit.Test;


public class SdbOperationsTest {
    @Test
    public void supportedOperationCount() {
        Assert.assertEquals(9, SdbOperations.values().length);
    }

    @Test
    public void valueOf() {
        Assert.assertEquals(BatchDeleteAttributes, SdbOperations.valueOf("BatchDeleteAttributes"));
        Assert.assertEquals(BatchPutAttributes, SdbOperations.valueOf("BatchPutAttributes"));
        Assert.assertEquals(DeleteAttributes, SdbOperations.valueOf("DeleteAttributes"));
        Assert.assertEquals(DeleteDomain, SdbOperations.valueOf("DeleteDomain"));
        Assert.assertEquals(DomainMetadata, SdbOperations.valueOf("DomainMetadata"));
        Assert.assertEquals(GetAttributes, SdbOperations.valueOf("GetAttributes"));
        Assert.assertEquals(ListDomains, SdbOperations.valueOf("ListDomains"));
        Assert.assertEquals(PutAttributes, SdbOperations.valueOf("PutAttributes"));
        Assert.assertEquals(Select, SdbOperations.valueOf("Select"));
    }

    @Test
    public void testToString() {
        Assert.assertEquals(BatchDeleteAttributes.toString(), "BatchDeleteAttributes");
        Assert.assertEquals(BatchPutAttributes.toString(), "BatchPutAttributes");
        Assert.assertEquals(DeleteAttributes.toString(), "DeleteAttributes");
        Assert.assertEquals(DeleteDomain.toString(), "DeleteDomain");
        Assert.assertEquals(DomainMetadata.toString(), "DomainMetadata");
        Assert.assertEquals(GetAttributes.toString(), "GetAttributes");
        Assert.assertEquals(ListDomains.toString(), "ListDomains");
        Assert.assertEquals(PutAttributes.toString(), "PutAttributes");
        Assert.assertEquals(Select.toString(), "Select");
    }
}

