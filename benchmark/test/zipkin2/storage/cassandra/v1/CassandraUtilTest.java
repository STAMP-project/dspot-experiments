/**
 * Copyright 2015-2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.storage.cassandra.v1;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import zipkin2.Span;
import zipkin2.TestObjects;
import zipkin2.storage.QueryRequest;
import zipkin2.v1.V1Span;
import zipkin2.v1.V2SpanConverter;


public class CassandraUtilTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    V2SpanConverter converter = V2SpanConverter.create();

    QueryRequest request = QueryRequest.newBuilder().endTs(1).limit(1).lookback(1).build();

    @Test
    public void annotationKeys_emptyRequest() {
        assertThat(CassandraUtil.annotationKeys(request)).isEmpty();
    }

    @Test
    public void annotationKeys_serviceNameRequired() {
        thrown.expect(IllegalArgumentException.class);
        CassandraUtil.annotationKeys(request.toBuilder().parseAnnotationQuery("sr").build());
    }

    @Test
    public void annotationKeys() {
        assertThat(CassandraUtil.annotationKeys(request.toBuilder().serviceName("service").parseAnnotationQuery("error and http.method=GET").build())).containsExactly("service:error", "service:http.method:GET");
    }

    @Test
    public void annotationKeys_dedupes() {
        assertThat(CassandraUtil.annotationKeys(request.toBuilder().serviceName("service").parseAnnotationQuery("error and error").build())).containsExactly("service:error");
    }

    @Test
    public void annotationKeys_skipsCoreAndAddressAnnotations() {
        V1Span v1 = converter.convert(TestObjects.CLIENT_SPAN);
        assertThat(CassandraUtil.annotationKeys(v1)).containsExactly("frontend:foo", "frontend:clnt/finagle.version", "frontend:clnt/finagle.version:6.45.0", "frontend:http.path", "frontend:http.path:/api");
    }

    @Test
    public void annotationKeys_skipsBinaryAnnotationsLongerThan256chars() {
        // example long value
        String arn = "arn:aws:acm:us-east-1:123456789012:certificate/12345678-1234-1234-1234-123456789012";
        // example too long value
        String url = "http://webservices.amazon.com/onca/xml?AWSAccessKeyId=AKIAIOSFODNN7EXAMPLE&AssociateTag=mytag-20&ItemId=0679722769&Operation=ItemLookup&ResponseGroup=Images%2CItemAttributes%2COffers%2CReviews&Service=AWSECommerceService&Timestamp=2014-08-18T12%3A00%3A00Z&Version=2013-08-01&Signature=j7bZM0LXZ9eXeZruTqWm2DIvDYVUU3wxPPpp%2BiXxzQc%3D";
        Span span = Span.newBuilder().traceId("1").id("1").localEndpoint(TestObjects.FRONTEND).putTag("aws.arn", arn).putTag("http.url", url).build();
        assertThat(CassandraUtil.annotationKeys(converter.convert(span))).containsOnly("frontend:aws.arn", ("frontend:aws.arn:" + arn));
    }
}

