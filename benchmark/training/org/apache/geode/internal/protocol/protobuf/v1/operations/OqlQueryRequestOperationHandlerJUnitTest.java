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


import BasicTypes.EncodedValueList;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.geode.cache.query.FunctionDomainException;
import org.apache.geode.cache.query.NameResolutionException;
import org.apache.geode.cache.query.Query;
import org.apache.geode.cache.query.QueryInvocationTargetException;
import org.apache.geode.cache.query.SelectResults;
import org.apache.geode.cache.query.TypeMismatchException;
import org.apache.geode.cache.query.internal.DefaultQuery;
import org.apache.geode.cache.query.internal.InternalQueryService;
import org.apache.geode.cache.query.internal.LinkedStructSet;
import org.apache.geode.cache.query.internal.ResultsBag;
import org.apache.geode.cache.query.internal.types.ObjectTypeImpl;
import org.apache.geode.cache.query.internal.types.StructTypeImpl;
import org.apache.geode.internal.exception.InvalidExecutionContextException;
import org.apache.geode.internal.protocol.TestExecutionContext;
import org.apache.geode.internal.protocol.protobuf.v1.RegionAPI.OQLQueryRequest;
import org.apache.geode.internal.protocol.protobuf.v1.RegionAPI.OQLQueryResponse;
import org.apache.geode.internal.protocol.protobuf.v1.Result;
import org.apache.geode.internal.protocol.protobuf.v1.serialization.exception.DecodingException;
import org.apache.geode.internal.protocol.protobuf.v1.serialization.exception.EncodingException;
import org.apache.geode.internal.protocol.protobuf.v1.state.exception.ConnectionStateException;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ ClientServerTest.class })
public class OqlQueryRequestOperationHandlerJUnitTest extends OperationHandlerJUnitTest {
    public static final String SELECT_STAR_QUERY = "select * from /region";

    public static final String STRING_RESULT_1 = "result1";

    public static final String STRING_RESULT_2 = "result2";

    private InternalQueryService queryService;

    @Test
    public void queryForSingleObject() throws FunctionDomainException, NameResolutionException, QueryInvocationTargetException, TypeMismatchException, InvalidExecutionContextException, DecodingException, EncodingException, ConnectionStateException {
        Query query = Mockito.mock(DefaultQuery.class);
        Mockito.when(queryService.newQuery(ArgumentMatchers.eq(OqlQueryRequestOperationHandlerJUnitTest.SELECT_STAR_QUERY))).thenReturn(query);
        Mockito.when(query.execute(((Object[]) (ArgumentMatchers.any())))).thenReturn(OqlQueryRequestOperationHandlerJUnitTest.STRING_RESULT_1);
        final OQLQueryRequest request = OQLQueryRequest.newBuilder().setQuery(OqlQueryRequestOperationHandlerJUnitTest.SELECT_STAR_QUERY).build();
        final Result<OQLQueryResponse> result = operationHandler.process(serializationService, request, TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
        Assert.assertEquals(serializationService.encode(OqlQueryRequestOperationHandlerJUnitTest.STRING_RESULT_1), result.getMessage().getSingleResult());
    }

    @Test
    public void queryForMultipleObjects() throws FunctionDomainException, NameResolutionException, QueryInvocationTargetException, TypeMismatchException, InvalidExecutionContextException, DecodingException, EncodingException, ConnectionStateException {
        Query query = Mockito.mock(DefaultQuery.class);
        Mockito.when(queryService.newQuery(ArgumentMatchers.eq(OqlQueryRequestOperationHandlerJUnitTest.SELECT_STAR_QUERY))).thenReturn(query);
        SelectResults results = new ResultsBag();
        results.setElementType(new ObjectTypeImpl());
        results.add(OqlQueryRequestOperationHandlerJUnitTest.STRING_RESULT_1);
        results.add(OqlQueryRequestOperationHandlerJUnitTest.STRING_RESULT_2);
        Mockito.when(query.execute(((Object[]) (ArgumentMatchers.any())))).thenReturn(results);
        final OQLQueryRequest request = OQLQueryRequest.newBuilder().setQuery(OqlQueryRequestOperationHandlerJUnitTest.SELECT_STAR_QUERY).build();
        final Result<OQLQueryResponse> result = operationHandler.process(serializationService, request, TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
        Assert.assertEquals(Arrays.asList(OqlQueryRequestOperationHandlerJUnitTest.STRING_RESULT_1, OqlQueryRequestOperationHandlerJUnitTest.STRING_RESULT_2), result.getMessage().getListResult().getElementList().stream().map(serializationService::decode).collect(Collectors.toList()));
    }

    @Test
    public void queryForMultipleStructs() throws FunctionDomainException, NameResolutionException, QueryInvocationTargetException, TypeMismatchException, InvalidExecutionContextException, DecodingException, EncodingException, ConnectionStateException {
        Query query = Mockito.mock(DefaultQuery.class);
        Mockito.when(queryService.newQuery(ArgumentMatchers.eq(OqlQueryRequestOperationHandlerJUnitTest.SELECT_STAR_QUERY))).thenReturn(query);
        SelectResults results = new LinkedStructSet();
        StructTypeImpl elementType = new StructTypeImpl(new String[]{ "field1" });
        results.setElementType(elementType);
        results.add(new org.apache.geode.cache.query.internal.StructImpl(elementType, new Object[]{ OqlQueryRequestOperationHandlerJUnitTest.STRING_RESULT_1 }));
        results.add(new org.apache.geode.cache.query.internal.StructImpl(elementType, new Object[]{ OqlQueryRequestOperationHandlerJUnitTest.STRING_RESULT_2 }));
        Mockito.when(query.execute(((Object[]) (ArgumentMatchers.any())))).thenReturn(results);
        final OQLQueryRequest request = OQLQueryRequest.newBuilder().setQuery(OqlQueryRequestOperationHandlerJUnitTest.SELECT_STAR_QUERY).build();
        final Result<OQLQueryResponse> result = operationHandler.process(serializationService, request, TestExecutionContext.getNoAuthCacheExecutionContext(cacheStub));
        Assert.assertEquals(Arrays.asList(EncodedValueList.newBuilder().addElement(serializationService.encode(OqlQueryRequestOperationHandlerJUnitTest.STRING_RESULT_1)).build(), EncodedValueList.newBuilder().addElement(serializationService.encode(OqlQueryRequestOperationHandlerJUnitTest.STRING_RESULT_2)).build()), result.getMessage().getTableResult().getRowList());
    }
}

