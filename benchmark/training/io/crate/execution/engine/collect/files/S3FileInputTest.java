/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.execution.engine.collect.files;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import io.crate.external.S3ClientHelper;
import io.crate.test.integration.CrateUnitTest;
import java.net.URI;
import java.util.List;
import java.util.function.Predicate;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;


public class S3FileInputTest extends CrateUnitTest {
    private static S3FileInput s3FileInput;

    private static List<S3ObjectSummary> listObjectSummaries;

    private static ObjectListing objectListing = Mockito.mock(ObjectListing.class);

    private static S3ClientHelper clientBuilder = Mockito.mock(S3ClientHelper.class);

    private static AmazonS3 amazonS3 = Mockito.mock(AmazonS3.class);

    private static Predicate<URI> uriPredicate = Mockito.mock(Predicate.class);

    private static final String BUCKET_NAME = "fakeBucket";

    private static final String PREFIX = "prefix";

    private static URI uri;

    @Test
    public void testListListUrlsWhenEmptyKeysIsListed() throws Exception {
        S3ObjectSummary path = new S3ObjectSummary();
        path.setBucketName(S3FileInputTest.BUCKET_NAME);
        path.setKey("prefix/");
        S3FileInputTest.listObjectSummaries = objectSummaries();
        S3FileInputTest.listObjectSummaries.add(path);
        Mockito.when(S3FileInputTest.objectListing.getObjectSummaries()).thenReturn(S3FileInputTest.listObjectSummaries);
        List<URI> uris = S3FileInputTest.s3FileInput.listUris(S3FileInputTest.uri, S3FileInputTest.uriPredicate);
        assertThat(uris.size(), Matchers.is(2));
        assertThat(uris.get(0).toString(), Matchers.is("s3://fakeBucket/prefix/test1.json.gz"));
        assertThat(uris.get(1).toString(), Matchers.is("s3://fakeBucket/prefix/test2.json.gz"));
    }

    @Test
    public void testListListUrlsWithCorrectKeys() throws Exception {
        Mockito.when(S3FileInputTest.objectListing.getObjectSummaries()).thenReturn(objectSummaries());
        List<URI> uris = S3FileInputTest.s3FileInput.listUris(S3FileInputTest.uri, S3FileInputTest.uriPredicate);
        assertThat(uris.size(), Matchers.is(2));
        assertThat(uris.get(0).toString(), Matchers.is("s3://fakeBucket/prefix/test1.json.gz"));
        assertThat(uris.get(1).toString(), Matchers.is("s3://fakeBucket/prefix/test2.json.gz"));
    }
}

