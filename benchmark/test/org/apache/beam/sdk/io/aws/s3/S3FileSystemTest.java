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
package org.apache.beam.sdk.io.aws.s3;


import MatchResult.Metadata;
import MatchResult.Status.ERROR;
import MatchResult.Status.NOT_FOUND;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import io.findify.s3mock.S3Mock;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.beam.sdk.io.fs.MatchResult;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test case for {@link S3FileSystem}.
 */
@RunWith(JUnit4.class)
public class S3FileSystemTest {
    private static S3Mock api;

    private static AmazonS3 client;

    @Test
    public void testGlobTranslation() {
        Assert.assertEquals("foo", S3FileSystem.wildcardToRegexp("foo"));
        Assert.assertEquals("fo[^/]*o", S3FileSystem.wildcardToRegexp("fo*o"));
        Assert.assertEquals("f[^/]*o\\.[^/]", S3FileSystem.wildcardToRegexp("f*o.?"));
        Assert.assertEquals("foo-[0-9][^/]*", S3FileSystem.wildcardToRegexp("foo-[0-9]*"));
        Assert.assertEquals("foo-[0-9].*", S3FileSystem.wildcardToRegexp("foo-[0-9]**"));
        Assert.assertEquals(".*foo", S3FileSystem.wildcardToRegexp("**/*foo"));
        Assert.assertEquals(".*foo", S3FileSystem.wildcardToRegexp("**foo"));
        Assert.assertEquals("foo/[^/]*", S3FileSystem.wildcardToRegexp("foo/*"));
        Assert.assertEquals("foo[^/]*", S3FileSystem.wildcardToRegexp("foo*"));
        Assert.assertEquals("foo/[^/]*/[^/]*/[^/]*", S3FileSystem.wildcardToRegexp("foo/*/*/*"));
        Assert.assertEquals("foo/[^/]*/.*", S3FileSystem.wildcardToRegexp("foo/*/**"));
        Assert.assertEquals("foo.*baz", S3FileSystem.wildcardToRegexp("foo**baz"));
    }

    @Test
    public void testGetScheme() {
        S3FileSystem s3FileSystem = new S3FileSystem(S3TestUtils.s3Options());
        Assert.assertEquals("s3", s3FileSystem.getScheme());
    }

    @Test
    public void testGetPathStyleAccessEnabled() throws URISyntaxException {
        S3FileSystem s3FileSystem = new S3FileSystem(S3TestUtils.s3OptionsWithCustomEndpointAndPathStyleAccessEnabled());
        URL s3Url = s3FileSystem.getAmazonS3Client().getUrl("bucket", "file");
        Assert.assertEquals("https://s3.custom.dns/bucket/file", s3Url.toURI().toString());
    }

    @Test
    public void testCopy() throws IOException {
        testCopy(S3TestUtils.s3Options());
        testCopy(S3TestUtils.s3OptionsWithSSECustomerKey());
    }

    @Test
    public void testAtomicCopy() {
        testAtomicCopy(S3TestUtils.s3Options());
        testAtomicCopy(S3TestUtils.s3OptionsWithSSECustomerKey());
    }

    @Test
    public void testMultipartCopy() {
        testMultipartCopy(S3TestUtils.s3Options());
        testMultipartCopy(S3TestUtils.s3OptionsWithSSECustomerKey());
    }

    @Test
    public void deleteThousandsOfObjectsInMultipleBuckets() throws IOException {
        S3FileSystem s3FileSystem = S3TestUtils.buildMockedS3FileSystem(S3TestUtils.s3Options());
        List<String> buckets = ImmutableList.of("bucket1", "bucket2");
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < 2500; i++) {
            keys.add(String.format("key-%d", i));
        }
        List<S3ResourceId> paths = new ArrayList<>();
        for (String bucket : buckets) {
            for (String key : keys) {
                paths.add(S3ResourceId.fromComponents(bucket, key));
            }
        }
        s3FileSystem.delete(paths);
        // Should require 6 calls to delete 2500 objects in each of 2 buckets.
        Mockito.verify(s3FileSystem.getAmazonS3Client(), Mockito.times(6)).deleteObjects(ArgumentMatchers.argThat(Matchers.notNullValue(DeleteObjectsRequest.class)));
    }

    @Test
    public void matchNonGlob() {
        S3FileSystem s3FileSystem = S3TestUtils.buildMockedS3FileSystem(S3TestUtils.s3Options());
        S3ResourceId path = S3ResourceId.fromUri("s3://testbucket/testdirectory/filethatexists");
        long lastModifiedMillis = 1540000000000L;
        ObjectMetadata s3ObjectMetadata = new ObjectMetadata();
        s3ObjectMetadata.setContentLength(100);
        s3ObjectMetadata.setContentEncoding("read-seek-efficient");
        s3ObjectMetadata.setLastModified(new Date(lastModifiedMillis));
        Mockito.when(s3FileSystem.getAmazonS3Client().getObjectMetadata(ArgumentMatchers.argThat(new S3FileSystemTest.GetObjectMetadataRequestMatcher(new GetObjectMetadataRequest(path.getBucket(), path.getKey()))))).thenReturn(s3ObjectMetadata);
        MatchResult result = s3FileSystem.matchNonGlobPath(path);
        Assert.assertThat(result, MatchResultMatcher.create(ImmutableList.of(Metadata.builder().setSizeBytes(100).setLastModifiedMillis(lastModifiedMillis).setResourceId(path).setIsReadSeekEfficient(true).build())));
    }

    @Test
    public void matchNonGlobNotReadSeekEfficient() {
        S3FileSystem s3FileSystem = S3TestUtils.buildMockedS3FileSystem(S3TestUtils.s3Options());
        S3ResourceId path = S3ResourceId.fromUri("s3://testbucket/testdirectory/filethatexists");
        long lastModifiedMillis = 1540000000000L;
        ObjectMetadata s3ObjectMetadata = new ObjectMetadata();
        s3ObjectMetadata.setContentLength(100);
        s3ObjectMetadata.setLastModified(new Date(lastModifiedMillis));
        s3ObjectMetadata.setContentEncoding("gzip");
        Mockito.when(s3FileSystem.getAmazonS3Client().getObjectMetadata(ArgumentMatchers.argThat(new S3FileSystemTest.GetObjectMetadataRequestMatcher(new GetObjectMetadataRequest(path.getBucket(), path.getKey()))))).thenReturn(s3ObjectMetadata);
        MatchResult result = s3FileSystem.matchNonGlobPath(path);
        Assert.assertThat(result, MatchResultMatcher.create(ImmutableList.of(Metadata.builder().setSizeBytes(100).setLastModifiedMillis(lastModifiedMillis).setResourceId(path).setIsReadSeekEfficient(false).build())));
    }

    @Test
    public void matchNonGlobNullContentEncoding() {
        S3FileSystem s3FileSystem = S3TestUtils.buildMockedS3FileSystem(S3TestUtils.s3Options());
        S3ResourceId path = S3ResourceId.fromUri("s3://testbucket/testdirectory/filethatexists");
        long lastModifiedMillis = 1540000000000L;
        ObjectMetadata s3ObjectMetadata = new ObjectMetadata();
        s3ObjectMetadata.setContentLength(100);
        s3ObjectMetadata.setLastModified(new Date(lastModifiedMillis));
        s3ObjectMetadata.setContentEncoding(null);
        Mockito.when(s3FileSystem.getAmazonS3Client().getObjectMetadata(ArgumentMatchers.argThat(new S3FileSystemTest.GetObjectMetadataRequestMatcher(new GetObjectMetadataRequest(path.getBucket(), path.getKey()))))).thenReturn(s3ObjectMetadata);
        MatchResult result = s3FileSystem.matchNonGlobPath(path);
        Assert.assertThat(result, MatchResultMatcher.create(ImmutableList.of(Metadata.builder().setSizeBytes(100).setLastModifiedMillis(lastModifiedMillis).setResourceId(path).setIsReadSeekEfficient(true).build())));
    }

    @Test
    public void matchNonGlobNotFound() {
        S3FileSystem s3FileSystem = S3TestUtils.buildMockedS3FileSystem(S3TestUtils.s3Options());
        S3ResourceId path = S3ResourceId.fromUri("s3://testbucket/testdirectory/nonexistentfile");
        AmazonS3Exception exception = new AmazonS3Exception("mock exception");
        exception.setStatusCode(404);
        Mockito.when(s3FileSystem.getAmazonS3Client().getObjectMetadata(ArgumentMatchers.argThat(new S3FileSystemTest.GetObjectMetadataRequestMatcher(new GetObjectMetadataRequest(path.getBucket(), path.getKey()))))).thenThrow(exception);
        MatchResult result = s3FileSystem.matchNonGlobPath(path);
        Assert.assertThat(result, MatchResultMatcher.create(NOT_FOUND, new FileNotFoundException()));
    }

    @Test
    public void matchNonGlobForbidden() {
        S3FileSystem s3FileSystem = S3TestUtils.buildMockedS3FileSystem(S3TestUtils.s3Options());
        AmazonS3Exception exception = new AmazonS3Exception("mock exception");
        exception.setStatusCode(403);
        S3ResourceId path = S3ResourceId.fromUri("s3://testbucket/testdirectory/keyname");
        Mockito.when(s3FileSystem.getAmazonS3Client().getObjectMetadata(ArgumentMatchers.argThat(new S3FileSystemTest.GetObjectMetadataRequestMatcher(new GetObjectMetadataRequest(path.getBucket(), path.getKey()))))).thenThrow(exception);
        Assert.assertThat(s3FileSystem.matchNonGlobPath(path), MatchResultMatcher.create(ERROR, new IOException(exception)));
    }

    static class ListObjectsV2RequestArgumentMatches extends ArgumentMatcher<ListObjectsV2Request> {
        private final ListObjectsV2Request expected;

        ListObjectsV2RequestArgumentMatches(ListObjectsV2Request expected) {
            this.expected = checkNotNull(expected);
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof ListObjectsV2Request) {
                ListObjectsV2Request actual = ((ListObjectsV2Request) (argument));
                return ((expected.getBucketName().equals(actual.getBucketName())) && (expected.getPrefix().equals(actual.getPrefix()))) && ((expected.getContinuationToken()) == null ? (actual.getContinuationToken()) == null : expected.getContinuationToken().equals(actual.getContinuationToken()));
            }
            return false;
        }
    }

    @Test
    public void matchGlob() throws IOException {
        S3FileSystem s3FileSystem = S3TestUtils.buildMockedS3FileSystem(S3TestUtils.s3Options());
        S3ResourceId path = S3ResourceId.fromUri("s3://testbucket/foo/bar*baz");
        ListObjectsV2Request firstRequest = new ListObjectsV2Request().withBucketName(path.getBucket()).withPrefix(path.getKeyNonWildcardPrefix()).withContinuationToken(null);
        // Expected to be returned; prefix and wildcard/regex match
        S3ObjectSummary firstMatch = new S3ObjectSummary();
        firstMatch.setBucketName(path.getBucket());
        firstMatch.setKey("foo/bar0baz");
        firstMatch.setSize(100);
        firstMatch.setLastModified(new Date(1540000000001L));
        // Expected to not be returned; prefix matches, but substring after wildcard does not
        S3ObjectSummary secondMatch = new S3ObjectSummary();
        secondMatch.setBucketName(path.getBucket());
        secondMatch.setKey("foo/bar1qux");
        secondMatch.setSize(200);
        secondMatch.setLastModified(new Date(1540000000002L));
        // Expected first request returns continuation token
        ListObjectsV2Result firstResult = new ListObjectsV2Result();
        firstResult.setNextContinuationToken("token");
        firstResult.getObjectSummaries().add(firstMatch);
        firstResult.getObjectSummaries().add(secondMatch);
        Mockito.when(s3FileSystem.getAmazonS3Client().listObjectsV2(ArgumentMatchers.argThat(new S3FileSystemTest.ListObjectsV2RequestArgumentMatches(firstRequest)))).thenReturn(firstResult);
        // Expect second request with continuation token
        ListObjectsV2Request secondRequest = new ListObjectsV2Request().withBucketName(path.getBucket()).withPrefix(path.getKeyNonWildcardPrefix()).withContinuationToken("token");
        // Expected to be returned; prefix and wildcard/regex match
        S3ObjectSummary thirdMatch = new S3ObjectSummary();
        thirdMatch.setBucketName(path.getBucket());
        thirdMatch.setKey("foo/bar2baz");
        thirdMatch.setSize(300);
        thirdMatch.setLastModified(new Date(1540000000003L));
        // Expected second request returns third prefix match and no continuation token
        ListObjectsV2Result secondResult = new ListObjectsV2Result();
        secondResult.setNextContinuationToken(null);
        secondResult.getObjectSummaries().add(thirdMatch);
        Mockito.when(s3FileSystem.getAmazonS3Client().listObjectsV2(ArgumentMatchers.argThat(new S3FileSystemTest.ListObjectsV2RequestArgumentMatches(secondRequest)))).thenReturn(secondResult);
        // Expect object metadata queries for content encoding
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentEncoding("");
        Mockito.when(s3FileSystem.getAmazonS3Client().getObjectMetadata(ArgumentMatchers.anyObject())).thenReturn(metadata);
        Assert.assertThat(s3FileSystem.matchGlobPaths(ImmutableList.of(path)).get(0), MatchResultMatcher.create(ImmutableList.of(Metadata.builder().setIsReadSeekEfficient(true).setResourceId(S3ResourceId.fromComponents(firstMatch.getBucketName(), firstMatch.getKey())).setSizeBytes(firstMatch.getSize()).setLastModifiedMillis(firstMatch.getLastModified().getTime()).build(), Metadata.builder().setIsReadSeekEfficient(true).setResourceId(S3ResourceId.fromComponents(thirdMatch.getBucketName(), thirdMatch.getKey())).setSizeBytes(thirdMatch.getSize()).setLastModifiedMillis(thirdMatch.getLastModified().getTime()).build())));
    }

    @Test
    public void matchGlobWithSlashes() throws IOException {
        S3FileSystem s3FileSystem = S3TestUtils.buildMockedS3FileSystem(S3TestUtils.s3Options());
        S3ResourceId path = S3ResourceId.fromUri("s3://testbucket/foo/bar\\baz*");
        ListObjectsV2Request request = new ListObjectsV2Request().withBucketName(path.getBucket()).withPrefix(path.getKeyNonWildcardPrefix()).withContinuationToken(null);
        // Expected to be returned; prefix and wildcard/regex match
        S3ObjectSummary firstMatch = new S3ObjectSummary();
        firstMatch.setBucketName(path.getBucket());
        firstMatch.setKey("foo/bar\\baz0");
        firstMatch.setSize(100);
        firstMatch.setLastModified(new Date(1540000000001L));
        // Expected to not be returned; prefix matches, but substring after wildcard does not
        S3ObjectSummary secondMatch = new S3ObjectSummary();
        secondMatch.setBucketName(path.getBucket());
        secondMatch.setKey("foo/bar/baz1");
        secondMatch.setSize(200);
        secondMatch.setLastModified(new Date(1540000000002L));
        // Expected first request returns continuation token
        ListObjectsV2Result result = new ListObjectsV2Result();
        result.getObjectSummaries().add(firstMatch);
        result.getObjectSummaries().add(secondMatch);
        Mockito.when(s3FileSystem.getAmazonS3Client().listObjectsV2(ArgumentMatchers.argThat(new S3FileSystemTest.ListObjectsV2RequestArgumentMatches(request)))).thenReturn(result);
        // Expect object metadata queries for content encoding
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentEncoding("");
        Mockito.when(s3FileSystem.getAmazonS3Client().getObjectMetadata(ArgumentMatchers.anyObject())).thenReturn(metadata);
        Assert.assertThat(s3FileSystem.matchGlobPaths(ImmutableList.of(path)).get(0), MatchResultMatcher.create(ImmutableList.of(Metadata.builder().setIsReadSeekEfficient(true).setResourceId(S3ResourceId.fromComponents(firstMatch.getBucketName(), firstMatch.getKey())).setSizeBytes(firstMatch.getSize()).setLastModifiedMillis(firstMatch.getLastModified().getTime()).build())));
    }

    @Test
    public void matchVariousInvokeThreadPool() throws IOException {
        S3FileSystem s3FileSystem = S3TestUtils.buildMockedS3FileSystem(S3TestUtils.s3Options());
        AmazonS3Exception notFoundException = new AmazonS3Exception("mock exception");
        notFoundException.setStatusCode(404);
        S3ResourceId pathNotExist = S3ResourceId.fromUri("s3://testbucket/testdirectory/nonexistentfile");
        Mockito.when(s3FileSystem.getAmazonS3Client().getObjectMetadata(ArgumentMatchers.argThat(new S3FileSystemTest.GetObjectMetadataRequestMatcher(new GetObjectMetadataRequest(pathNotExist.getBucket(), pathNotExist.getKey()))))).thenThrow(notFoundException);
        AmazonS3Exception forbiddenException = new AmazonS3Exception("mock exception");
        forbiddenException.setStatusCode(403);
        S3ResourceId pathForbidden = S3ResourceId.fromUri("s3://testbucket/testdirectory/forbiddenfile");
        Mockito.when(s3FileSystem.getAmazonS3Client().getObjectMetadata(ArgumentMatchers.argThat(new S3FileSystemTest.GetObjectMetadataRequestMatcher(new GetObjectMetadataRequest(pathForbidden.getBucket(), pathForbidden.getKey()))))).thenThrow(forbiddenException);
        S3ResourceId pathExist = S3ResourceId.fromUri("s3://testbucket/testdirectory/filethatexists");
        ObjectMetadata s3ObjectMetadata = new ObjectMetadata();
        s3ObjectMetadata.setContentLength(100);
        s3ObjectMetadata.setLastModified(new Date(1540000000000L));
        s3ObjectMetadata.setContentEncoding("not-gzip");
        Mockito.when(s3FileSystem.getAmazonS3Client().getObjectMetadata(ArgumentMatchers.argThat(new S3FileSystemTest.GetObjectMetadataRequestMatcher(new GetObjectMetadataRequest(pathExist.getBucket(), pathExist.getKey()))))).thenReturn(s3ObjectMetadata);
        S3ResourceId pathGlob = S3ResourceId.fromUri("s3://testbucket/path/part*");
        S3ObjectSummary foundListObject = new S3ObjectSummary();
        foundListObject.setBucketName(pathGlob.getBucket());
        foundListObject.setKey("path/part-0");
        foundListObject.setSize(200);
        foundListObject.setLastModified(new Date(1541000000000L));
        ListObjectsV2Result listObjectsResult = new ListObjectsV2Result();
        listObjectsResult.setNextContinuationToken(null);
        listObjectsResult.getObjectSummaries().add(foundListObject);
        Mockito.when(s3FileSystem.getAmazonS3Client().listObjectsV2(ArgumentMatchers.notNull(ListObjectsV2Request.class))).thenReturn(listObjectsResult);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentEncoding("");
        Mockito.when(s3FileSystem.getAmazonS3Client().getObjectMetadata(ArgumentMatchers.argThat(new S3FileSystemTest.GetObjectMetadataRequestMatcher(new GetObjectMetadataRequest(pathGlob.getBucket(), "path/part-0"))))).thenReturn(metadata);
        Assert.assertThat(s3FileSystem.match(ImmutableList.of(pathNotExist.toString(), pathForbidden.toString(), pathExist.toString(), pathGlob.toString())), Matchers.contains(MatchResultMatcher.create(NOT_FOUND, new FileNotFoundException()), MatchResultMatcher.create(ERROR, new IOException(forbiddenException)), MatchResultMatcher.create(100, 1540000000000L, pathExist, true), MatchResultMatcher.create(200, 1541000000000L, S3ResourceId.fromComponents(pathGlob.getBucket(), foundListObject.getKey()), true)));
    }

    @Test
    public void testWriteAndRead() throws IOException {
        S3FileSystem s3FileSystem = S3TestUtils.buildMockedS3FileSystem(S3TestUtils.s3Options(), S3FileSystemTest.client);
        S3FileSystemTest.client.createBucket("testbucket");
        byte[] writtenArray = new byte[]{ 0 };
        ByteBuffer bb = ByteBuffer.allocate(writtenArray.length);
        bb.put(writtenArray);
        // First create an object and write data to it
        S3ResourceId path = S3ResourceId.fromUri("s3://testbucket/foo/bar.txt");
        WritableByteChannel writableByteChannel = s3FileSystem.create(path, builder().setMimeType("application/text").build());
        writableByteChannel.write(bb);
        writableByteChannel.close();
        // Now read the same object
        ByteBuffer bb2 = ByteBuffer.allocate(writtenArray.length);
        ReadableByteChannel open = s3FileSystem.open(path);
        open.read(bb2);
        // And compare the content with the one that was written
        byte[] readArray = bb2.array();
        Assert.assertArrayEquals(readArray, writtenArray);
        open.close();
    }

    /**
     * A mockito argument matcher to implement equality on GetObjectMetadataRequest.
     */
    private static class GetObjectMetadataRequestMatcher extends ArgumentMatcher<GetObjectMetadataRequest> {
        private final GetObjectMetadataRequest expected;

        GetObjectMetadataRequestMatcher(GetObjectMetadataRequest expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(Object obj) {
            if (!(obj instanceof GetObjectMetadataRequest)) {
                return false;
            }
            GetObjectMetadataRequest actual = ((GetObjectMetadataRequest) (obj));
            return (actual.getBucketName().equals(expected.getBucketName())) && (actual.getKey().equals(expected.getKey()));
        }
    }
}

