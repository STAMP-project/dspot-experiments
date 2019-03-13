/**
 * Copyright 2012-2019 The Feign Authors
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
package feign;


import HttpMethod.GET;
import HttpMethod.POST;
import Util.UTF_8;
import feign.assertj.FeignAssertions;
import feign.template.UriUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class RequestTemplateTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void expandUrlEncoded() {
        for (String val : Arrays.asList("apples", "sp ace", "unic???de", "qu?stion")) {
            FeignAssertions.assertThat(RequestTemplateTest.expand("/users/{user}", RequestTemplateTest.mapOf("user", val))).isEqualTo(("/users/" + (UriUtils.encode(val, UTF_8))));
        }
    }

    @Test
    public void expandMultipleParams() {
        FeignAssertions.assertThat(RequestTemplateTest.expand("/users/{user}/{repo}", RequestTemplateTest.mapOf("user", "unic???de", "repo", "foo"))).isEqualTo("/users/unic%3F%3F%3Fde/foo");
    }

    @Test
    public void expandParamKeyHyphen() {
        FeignAssertions.assertThat(RequestTemplateTest.expand("/{user-dir}", RequestTemplateTest.mapOf("user-dir", "foo"))).isEqualTo("/foo");
    }

    @Test
    public void expandMissingParamProceeds() {
        FeignAssertions.assertThat(RequestTemplateTest.expand("/{user-dir}", RequestTemplateTest.mapOf("user_dir", "foo"))).isEqualTo("/");
    }

    @Test
    public void resolveTemplateWithParameterizedPathSkipsEncodingSlash() {
        RequestTemplate template = new RequestTemplate().method(GET).uri("{zoneId}");
        template = template.resolve(RequestTemplateTest.mapOf("zoneId", "/hostedzone/Z1PA6795UKMFR9"));
        FeignAssertions.assertThat(template).hasUrl("/hostedzone/Z1PA6795UKMFR9");
    }

    @Test
    public void resolveTemplateWithBinaryBody() {
        RequestTemplate template = new RequestTemplate().method(GET).uri("{zoneId}").body(new byte[]{ 7, 3, -3, -7 }, null);
        template = template.resolve(RequestTemplateTest.mapOf("zoneId", "/hostedzone/Z1PA6795UKMFR9"));
        FeignAssertions.assertThat(template).hasUrl("/hostedzone/Z1PA6795UKMFR9");
    }

    @Test
    public void canInsertAbsoluteHref() {
        RequestTemplate template = new RequestTemplate().method(GET).uri("/hostedzone/Z1PA6795UKMFR9");
        template.target("https://route53.amazonaws.com/2012-12-12");
        FeignAssertions.assertThat(template).hasUrl("https://route53.amazonaws.com/2012-12-12/hostedzone/Z1PA6795UKMFR9");
    }

    @Test
    public void resolveTemplateWithRelativeUriWithQuery() {
        RequestTemplate template = new RequestTemplate().method(GET).uri("/wsdl/testcase?wsdl").target("https://api.example.com");
        FeignAssertions.assertThat(template).hasUrl("https://api.example.com/wsdl/testcase?wsdl");
    }

    @Test
    public void resolveTemplateWithBaseAndParameterizedQuery() {
        RequestTemplate template = new RequestTemplate().method(GET).uri("/?Action=DescribeRegions").query("RegionName.1", "{region}");
        template = template.resolve(RequestTemplateTest.mapOf("region", "eu-west-1"));
        FeignAssertions.assertThat(template).hasQueries(entry("Action", Collections.singletonList("DescribeRegions")), entry("RegionName.1", Collections.singletonList("eu-west-1")));
    }

    @Test
    public void resolveTemplateWithBaseAndParameterizedIterableQuery() {
        RequestTemplate template = new RequestTemplate().method(GET).uri("/?Query=one").query("Queries", "{queries}");
        template = template.resolve(RequestTemplateTest.mapOf("queries", Arrays.asList("us-east-1", "eu-west-1")));
        FeignAssertions.assertThat(template).hasQueries(entry("Query", Collections.singletonList("one")), entry("Queries", Arrays.asList("us-east-1", "eu-west-1")));
    }

    @Test
    public void resolveTemplateWithHeaderSubstitutions() {
        RequestTemplate template = new RequestTemplate().method(GET).header("Auth-Token", "{authToken}");
        template = template.resolve(RequestTemplateTest.mapOf("authToken", "1234"));
        FeignAssertions.assertThat(template).hasHeaders(entry("Auth-Token", Collections.singletonList("1234")));
    }

    @Test
    public void resolveTemplateWithHeaderSubstitutionsNotAtStart() {
        RequestTemplate template = new RequestTemplate().method(GET).header("Authorization", "Bearer {token}");
        template = template.resolve(RequestTemplateTest.mapOf("token", "1234"));
        FeignAssertions.assertThat(template).hasHeaders(entry("Authorization", Collections.singletonList("Bearer 1234")));
    }

    @Test
    public void resolveTemplateWithHeaderWithEscapedCurlyBrace() {
        RequestTemplate template = new RequestTemplate().method(GET).header("Encoded", "{{{{dont_expand_me}}");
        template.resolve(RequestTemplateTest.mapOf("dont_expand_me", "1234"));
        FeignAssertions.assertThat(template).hasHeaders(entry("Encoded", Collections.singletonList("{{{{dont_expand_me}}")));
    }

    /**
     * This ensures we don't mess up vnd types
     */
    @Test
    public void resolveTemplateWithHeaderIncludingSpecialCharacters() {
        RequestTemplate template = new RequestTemplate().method(GET).header("Accept", "application/vnd.github.v3+{type}");
        template = template.resolve(RequestTemplateTest.mapOf("type", "json"));
        FeignAssertions.assertThat(template).hasHeaders(entry("Accept", Collections.singletonList("application/vnd.github.v3+json")));
    }

    @Test
    public void resolveTemplateWithHeaderEmptyResult() {
        RequestTemplate template = new RequestTemplate().method(GET).header("Encoded", "{var}");
        template = template.resolve(RequestTemplateTest.mapOf("var", ""));
        FeignAssertions.assertThat(template).hasNoHeader("Encoded");
    }

    @Test
    public void resolveTemplateWithMixedRequestLineParams() {
        RequestTemplate template = // 
        // 
        // 
        new RequestTemplate().method(GET).uri("/domains/{domainId}/records").query("name", "{name}").query("type", "{type}");
        template = template.resolve(RequestTemplateTest.mapOf("domainId", 1001, "name", "denominator.io", "type", "CNAME"));
        FeignAssertions.assertThat(template).hasQueries(entry("name", Collections.singletonList("denominator.io")), entry("type", Collections.singletonList("CNAME")));
    }

    @Test
    public void insertHasQueryParams() {
        RequestTemplate template = // 
        // 
        // 
        new RequestTemplate().method(GET).uri("/domains/1001/records").query("name", "denominator.io").query("type", "CNAME");
        template.target("https://host/v1.0/1234?provider=foo");
        FeignAssertions.assertThat(template).hasPath("https://host/v1.0/1234/domains/1001/records").hasQueries(entry("name", Collections.singletonList("denominator.io")), entry("type", Collections.singletonList("CNAME")), entry("provider", Collections.singletonList("foo")));
    }

    @Test
    public void resolveTemplateWithBodyTemplateSetsBodyAndContentLength() {
        RequestTemplate template = new RequestTemplate().method(POST).bodyTemplate(("%7B\"customer_name\": \"{customer_name}\", \"user_name\": \"{user_name}\", " + "\"password\": \"{password}\"%7D"));
        template = template.resolve(RequestTemplateTest.mapOf("customer_name", "netflix", "user_name", "denominator", "password", "password"));
        FeignAssertions.assertThat(template).hasBody("{\"customer_name\": \"netflix\", \"user_name\": \"denominator\", \"password\": \"password\"}").hasHeaders(entry("Content-Length", Collections.singletonList(String.valueOf(template.body().length))));
    }

    @Test
    public void resolveTemplateWithBodyTemplateDoesNotDoubleDecode() {
        RequestTemplate template = new RequestTemplate().method(POST).bodyTemplate("%7B\"customer_name\": \"{customer_name}\", \"user_name\": \"{user_name}\", \"password\": \"{password}\"%7D");
        template = template.resolve(RequestTemplateTest.mapOf("customer_name", "netflix", "user_name", "denominator", "password", "abc+123%25d8"));
        FeignAssertions.assertThat(template).hasBody("{\"customer_name\": \"netflix\", \"user_name\": \"denominator\", \"password\": \"abc 123%d8\"}");
    }

    @Test
    public void skipUnresolvedQueries() {
        RequestTemplate template = // 
        // 
        new RequestTemplate().method(GET).uri("/domains/{domainId}/records").query("optional", "{optional}").query("name", "{nameVariable}");
        template = template.resolve(RequestTemplateTest.mapOf("domainId", 1001, "nameVariable", "denominator.io"));
        FeignAssertions.assertThat(template).hasQueries(entry("name", Collections.singletonList("denominator.io")));
    }

    @Test
    public void allQueriesUnresolvable() {
        RequestTemplate template = // 
        // 
        // 
        new RequestTemplate().method(GET).uri("/domains/{domainId}/records").query("optional", "{optional}").query("optional2", "{optional2}");
        template = template.resolve(RequestTemplateTest.mapOf("domainId", 1001));
        FeignAssertions.assertThat(template).hasUrl("/domains/1001/records").hasQueries();
    }

    @Test
    public void spaceEncodingInUrlParam() {
        RequestTemplate template = // 
        new RequestTemplate().method(GET).uri("/api/{value1}?key={value2}");
        template = template.resolve(RequestTemplateTest.mapOf("value1", "ABC 123", "value2", "XYZ 123"));
        FeignAssertions.assertThat(template.request().url()).isEqualTo("/api/ABC%20123?key=XYZ%20123");
    }

    @Test
    public void useCaseInsensitiveHeaderFieldNames() {
        final RequestTemplate template = new RequestTemplate();
        final String value = "value1";
        template.header("TEST", value);
        final String value2 = "value2";
        template.header("tEST", value2);
        final Collection<String> test = template.headers().get("test");
        final String assertionMessage = "Header field names should be case insensitive";
        Assert.assertNotNull(assertionMessage, test);
        Assert.assertTrue(assertionMessage, test.contains(value));
        Assert.assertTrue(assertionMessage, test.contains(value2));
        Assert.assertEquals(1, template.headers().size());
        Assert.assertEquals(2, template.headers().get("tesT").size());
    }

    @Test
    public void encodeSlashTest() {
        RequestTemplate template = new RequestTemplate().method(GET).uri("/api/{vhost}").decodeSlash(false);
        template = template.resolve(RequestTemplateTest.mapOf("vhost", "/"));
        FeignAssertions.assertThat(template).hasUrl("/api/%2F");
    }

    /**
     * Implementations have a bug if they pass junk as the http method.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void uriStuffedIntoMethod() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid HTTP Method: /path?queryParam={queryParam}");
        new RequestTemplate().method("/path?queryParam={queryParam}");
    }

    @Test
    public void encodedQueryClearedOnNull() {
        RequestTemplate template = new RequestTemplate();
        template.query("param[]", "value");
        FeignAssertions.assertThat(template).hasQueries(entry("param[]", Collections.singletonList("value")));
        template.query("param[]", ((String[]) (null)));
        FeignAssertions.assertThat(template.queries()).isEmpty();
    }

    @Test
    public void encodedQuery() {
        RequestTemplate template = new RequestTemplate().query("params[]", "foo%20bar");
        FeignAssertions.assertThat(template.queryLine()).isEqualTo("?params%5B%5D=foo%20bar");
        FeignAssertions.assertThat(template).hasQueries(entry("params[]", Collections.singletonList("foo%20bar")));
    }

    @Test
    public void encodedQueryWithUnsafeCharactersMixedWithUnencoded() {
        RequestTemplate template = // stored as "param%5D%5B"
        new RequestTemplate().query("params[]", "not encoded").query("params[]", "encoded");// stored as "param[]"

        FeignAssertions.assertThat(template.queryLine()).isEqualTo("?params%5B%5D=not%20encoded&params%5B%5D=encoded");
        Map<String, Collection<String>> queries = template.queries();
        FeignAssertions.assertThat(queries).containsKey("params[]");
        FeignAssertions.assertThat(queries.get("params[]")).contains("encoded").contains("not encoded");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldRetrieveHeadersWithoutNull() {
        RequestTemplate template = new RequestTemplate().header("key1", ((String) (null))).header("key2", Collections.emptyList()).header("key3", ((Collection) (null))).header("key4", "valid").header("key5", "valid").header("key6", "valid").header("key7", "valid");
        FeignAssertions.assertThat(template.headers()).hasSize(4);
        FeignAssertions.assertThat(template.headers().keySet()).containsExactly("key4", "key5", "key6", "key7");
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotInsertHeadersImmutableMap() {
        RequestTemplate template = new RequestTemplate().header("key1", "valid");
        FeignAssertions.assertThat(template.headers()).hasSize(1);
        FeignAssertions.assertThat(template.headers().keySet()).containsExactly("key1");
        template.headers().put("key2", Collections.singletonList("other value"));
    }
}

