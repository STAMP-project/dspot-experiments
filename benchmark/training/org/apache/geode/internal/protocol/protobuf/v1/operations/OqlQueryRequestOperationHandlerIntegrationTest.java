/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.protocol.protobuf.v1.operations;


import org.apache.geode.cache.Cache;
import org.apache.geode.cache.query.FunctionDomainException;
import org.apache.geode.cache.query.NameResolutionException;
import org.apache.geode.cache.query.QueryInvocationTargetException;
import org.apache.geode.cache.query.TypeMismatchException;
import org.apache.geode.internal.exception.InvalidExecutionContextException;
import org.apache.geode.internal.protocol.protobuf.v1.BasicTypes.EncodedValue;
import org.apache.geode.internal.protocol.protobuf.v1.ProtobufSerializationService;
import org.apache.geode.internal.protocol.protobuf.v1.serialization.exception.DecodingException;
import org.apache.geode.internal.protocol.protobuf.v1.serialization.exception.EncodingException;
import org.apache.geode.internal.protocol.protobuf.v1.state.exception.ConnectionStateException;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ClientServerTest.class })
public class OqlQueryRequestOperationHandlerIntegrationTest {
    private Cache cache;

    @Test
    public void queryForSingleObject() throws FunctionDomainException, NameResolutionException, QueryInvocationTargetException, TypeMismatchException, InvalidExecutionContextException, DecodingException, EncodingException, ConnectionStateException {
        checkResults("(select * from /region).size", 2);
    }

    @Test
    public void queryForMultipleWholeObjects() throws FunctionDomainException, NameResolutionException, QueryInvocationTargetException, TypeMismatchException, InvalidExecutionContextException, DecodingException, EncodingException, ConnectionStateException {
        checkResults("select ID from /region order by ID", 0, 1);
    }

    @Test
    public void queryForMultipleProjectionFields() throws FunctionDomainException, NameResolutionException, QueryInvocationTargetException, TypeMismatchException, InvalidExecutionContextException, DecodingException, EncodingException, ConnectionStateException {
        checkResults("select ID,status from /region order by ID", new EncodedValue[]{  }, new String[]{ "ID", "status" }, new Object[]{ 0, "active" }, new Object[]{ 1, "inactive" });
    }

    @Test
    public void queryForSingleStruct() throws FunctionDomainException, NameResolutionException, QueryInvocationTargetException, TypeMismatchException, InvalidExecutionContextException, DecodingException, EncodingException, ConnectionStateException {
        checkResults("select count(*),min(ID) from /region", new EncodedValue[]{  }, new String[]{ "0", "ID" }, new Object[]{ 2, 0 });
    }

    @Test
    public void queryWithBindParameters() throws FunctionDomainException, NameResolutionException, QueryInvocationTargetException, TypeMismatchException, InvalidExecutionContextException, DecodingException, EncodingException, ConnectionStateException {
        checkResults("select ID,status from /region where ID=$1", new EncodedValue[]{ new ProtobufSerializationService().encode(0) }, new String[]{ "ID", "status" }, new Object[]{ 0, "active" });
    }
}

