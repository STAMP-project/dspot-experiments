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
package org.apache.beam.sdk.io.gcp;


import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.gax.longrunning.OperationSnapshot;
import com.google.api.gax.paging.Page;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.retrying.RetrySettings.Builder;
import com.google.api.gax.retrying.RetryingFuture;
import com.google.api.gax.retrying.TimedAttemptSettings;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode;
import com.google.api.gax.rpc.StatusCode.Code;
import com.google.api.resourcenames.ResourceName;
import com.google.cloud.BaseServiceException;
import com.google.cloud.BaseServiceException.Error;
import com.google.cloud.BaseServiceException.ExceptionData;
import com.google.cloud.ByteArray;
import com.google.cloud.Date;
import com.google.cloud.RetryHelper.RetryHelperException;
import com.google.cloud.Timestamp;
import com.google.cloud.bigtable.grpc.BigtableClusterName;
import com.google.cloud.bigtable.grpc.BigtableInstanceName;
import com.google.cloud.bigtable.grpc.BigtableTableName;
import com.google.cloud.grpc.BaseGrpcServiceException;
import java.util.Set;
import org.apache.beam.sdk.io.gcp.testing.BigqueryClient;
import org.apache.beam.sdk.io.gcp.testing.BigqueryMatcher;
import org.apache.beam.sdk.util.ApiSurface;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * API surface verification for {@link org.apache.beam.sdk.io.gcp}.
 */
@RunWith(JUnit4.class)
public class GcpApiSurfaceTest {
    @Test
    public void testGcpApiSurface() throws Exception {
        final Package thisPackage = getClass().getPackage();
        final ClassLoader thisClassLoader = getClass().getClassLoader();
        final ApiSurface apiSurface = ApiSurface.ofPackage(thisPackage, thisClassLoader).pruningPattern(BigqueryMatcher.class.getName()).pruningPattern(BigqueryClient.class.getName()).pruningPattern("org[.]apache[.]beam[.].*Test.*").pruningPattern("org[.]apache[.]beam[.].*IT").pruningPattern("java[.]lang.*").pruningPattern("java[.]util.*");
        @SuppressWarnings("unchecked")
        final Set<Matcher<Class<?>>> allowedClasses = ImmutableSet.of(classesInPackage("com.google.api.core"), classesInPackage("com.google.api.client.googleapis"), classesInPackage("com.google.api.client.http"), classesInPackage("com.google.api.client.json"), classesInPackage("com.google.api.client.util"), classesInPackage("com.google.api.services.bigquery.model"), classesInPackage("com.google.auth"), classesInPackage("com.google.bigtable.admin.v2"), classesInPackage("com.google.bigtable.v2"), classesInPackage("com.google.cloud.bigquery.storage.v1beta1"), classesInPackage("com.google.cloud.bigtable.config"), classesInPackage("com.google.cloud.bigtable.data"), classesInPackage("com.google.spanner.v1"), Matchers.equalTo(ApiException.class), Matchers.<Class<?>>equalTo(OperationFuture.class), Matchers.<Class<?>>equalTo(OperationSnapshot.class), Matchers.<Class<?>>equalTo(Page.class), Matchers.<Class<?>>equalTo(RetryingFuture.class), Matchers.<Class<?>>equalTo(RetrySettings.class), Matchers.<Class<?>>equalTo(Builder.class), Matchers.<Class<?>>equalTo(TimedAttemptSettings.class), Matchers.<Class<?>>equalTo(com.google.api.gax.retrying.TimedAttemptSettings.Builder.class), Matchers.<Class<?>>equalTo(StatusCode.class), Matchers.<Class<?>>equalTo(Code.class), Matchers.<Class<?>>equalTo(ResourceName.class), Matchers.<Class<?>>equalTo(BigtableClusterName.class), Matchers.<Class<?>>equalTo(BigtableInstanceName.class), Matchers.<Class<?>>equalTo(BigtableTableName.class), Matchers.<Class<?>>equalTo(BaseServiceException.class), Matchers.<Class<?>>equalTo(Error.class), Matchers.<Class<?>>equalTo(ExceptionData.class), Matchers.<Class<?>>equalTo(com.google.cloud.BaseServiceException.ExceptionData.Builder.class), Matchers.<Class<?>>equalTo(RetryHelperException.class), Matchers.<Class<?>>equalTo(BaseGrpcServiceException.class), Matchers.<Class<?>>equalTo(ByteArray.class), Matchers.<Class<?>>equalTo(Date.class), Matchers.<Class<?>>equalTo(Timestamp.class), classesInPackage("com.google.cloud.spanner"), classesInPackage("com.google.spanner.admin.database.v1"), classesInPackage("com.google.datastore.v1"), classesInPackage("com.google.protobuf"), classesInPackage("com.google.type"), classesInPackage("com.fasterxml.jackson.annotation"), classesInPackage("com.fasterxml.jackson.core"), classesInPackage("com.fasterxml.jackson.databind"), classesInPackage("io.grpc"), classesInPackage("java"), classesInPackage("javax"), classesInPackage("org.apache.avro"), classesInPackage("org.apache.beam"), classesInPackage("org.apache.commons.logging"), classesInPackage("org.codehaus.jackson"), classesInPackage("org.joda.time"), classesInPackage("org.threeten.bp"));
        MatcherAssert.assertThat(apiSurface, containsOnlyClassesMatching(allowedClasses));
    }
}

