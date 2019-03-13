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


import Contract.Default;
import com.google.gson.reflect.TypeToken;
import feign.assertj.FeignAssertions;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import org.assertj.core.api.Fail;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests interfaces defined per {@link Contract.Default} are interpreted into expected
 * {@link feign .RequestTemplate template} instances.
 */
public class DefaultContractTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    Default contract = new Contract.Default();

    @Test
    public void httpMethods() throws Exception {
        FeignAssertions.assertThat(parseAndValidateMetadata(DefaultContractTest.Methods.class, "post").template()).hasMethod("POST");
        FeignAssertions.assertThat(parseAndValidateMetadata(DefaultContractTest.Methods.class, "put").template()).hasMethod("PUT");
        FeignAssertions.assertThat(parseAndValidateMetadata(DefaultContractTest.Methods.class, "get").template()).hasMethod("GET");
        FeignAssertions.assertThat(parseAndValidateMetadata(DefaultContractTest.Methods.class, "delete").template()).hasMethod("DELETE");
    }

    @Test
    public void bodyParamIsGeneric() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.BodyParams.class, "post", List.class);
        FeignAssertions.assertThat(md.bodyIndex()).isEqualTo(0);
        FeignAssertions.assertThat(md.bodyType()).isEqualTo(new TypeToken<List<String>>() {}.getType());
    }

    @Test
    public void bodyParamWithPathParam() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.BodyParams.class, "post", int.class, List.class);
        FeignAssertions.assertThat(md.bodyIndex()).isEqualTo(1);
        FeignAssertions.assertThat(md.indexToName()).containsOnly(entry(0, Arrays.asList("id")));
    }

    @Test
    public void tooManyBodies() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Method has too many Body");
        parseAndValidateMetadata(DefaultContractTest.BodyParams.class, "tooMany", List.class, List.class);
    }

    @Test
    public void customMethodWithoutPath() throws Exception {
        FeignAssertions.assertThat(parseAndValidateMetadata(DefaultContractTest.CustomMethod.class, "patch").template()).hasMethod("PATCH").hasUrl("/");
    }

    @Test
    public void queryParamsInPathExtract() throws Exception {
        FeignAssertions.assertThat(parseAndValidateMetadata(DefaultContractTest.WithQueryParamsInPath.class, "none").template()).hasUrl("/").hasQueries();
        FeignAssertions.assertThat(parseAndValidateMetadata(DefaultContractTest.WithQueryParamsInPath.class, "one").template()).hasPath("/").hasQueries(entry("Action", Arrays.asList("GetUser")));
        FeignAssertions.assertThat(parseAndValidateMetadata(DefaultContractTest.WithQueryParamsInPath.class, "two").template()).hasPath("/").hasQueries(entry("Action", Arrays.asList("GetUser")), entry("Version", Arrays.asList("2010-05-08")));
        FeignAssertions.assertThat(parseAndValidateMetadata(DefaultContractTest.WithQueryParamsInPath.class, "three").template()).hasPath("/").hasQueries(entry("Action", Arrays.asList("GetUser")), entry("Version", Arrays.asList("2010-05-08")), entry("limit", Arrays.asList("1")));
        FeignAssertions.assertThat(parseAndValidateMetadata(DefaultContractTest.WithQueryParamsInPath.class, "twoAndOneEmpty").template()).hasPath("/").hasQueries(entry("flag", new ArrayList()), entry("Action", Arrays.asList("GetUser")), entry("Version", Arrays.asList("2010-05-08")));
        FeignAssertions.assertThat(parseAndValidateMetadata(DefaultContractTest.WithQueryParamsInPath.class, "oneEmpty").template()).hasPath("/").hasQueries(entry("flag", new ArrayList()));
        FeignAssertions.assertThat(parseAndValidateMetadata(DefaultContractTest.WithQueryParamsInPath.class, "twoEmpty").template()).hasPath("/").hasQueries(entry("flag", new ArrayList()), entry("NoErrors", new ArrayList()));
    }

    @Test
    public void bodyWithoutParameters() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.BodyWithoutParameters.class, "post");
        FeignAssertions.assertThat(md.template()).hasBody("<v01:getAccountsListOfUser/>");
    }

    @Test
    public void headersOnMethodAddsContentTypeHeader() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.BodyWithoutParameters.class, "post");
        FeignAssertions.assertThat(md.template()).hasHeaders(entry("Content-Type", Arrays.asList("application/xml")), entry("Content-Length", Arrays.asList(String.valueOf(md.template().requestBody().asBytes().length))));
    }

    @Test
    public void headersOnTypeAddsContentTypeHeader() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.HeadersOnType.class, "post");
        FeignAssertions.assertThat(md.template()).hasHeaders(entry("Content-Type", Arrays.asList("application/xml")), entry("Content-Length", Arrays.asList(String.valueOf(md.template().requestBody().asBytes().length))));
    }

    @Test
    public void headersContainsWhitespaces() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.HeadersContainsWhitespaces.class, "post");
        FeignAssertions.assertThat(md.template()).hasHeaders(entry("Content-Type", Arrays.asList("application/xml")), entry("Content-Length", Arrays.asList(String.valueOf(md.template().requestBody().asBytes().length))));
    }

    @Test
    public void withPathAndURIParam() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.WithURIParam.class, "uriParam", String.class, URI.class, String.class);
        // Skips 1 as it is a url index!
        FeignAssertions.assertThat(md.indexToName()).containsExactly(entry(0, Arrays.asList("1")), entry(2, Arrays.asList("2")));
        FeignAssertions.assertThat(md.urlIndex()).isEqualTo(1);
    }

    @Test
    public void pathAndQueryParams() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.WithPathAndQueryParams.class, "recordsByNameAndType", int.class, String.class, String.class);
        FeignAssertions.assertThat(md.template()).hasQueries(entry("name", Arrays.asList("{name}")), entry("type", Arrays.asList("{type}")));
        FeignAssertions.assertThat(md.indexToName()).containsExactly(entry(0, Arrays.asList("domainId")), entry(1, Arrays.asList("name")), entry(2, Arrays.asList("type")));
    }

    @Test
    public void bodyWithTemplate() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.FormParams.class, "login", String.class, String.class, String.class);
        FeignAssertions.assertThat(md.template()).hasBodyTemplate("%7B\"customer_name\": \"{customer_name}\", \"user_name\": \"{user_name}\", \"password\": \"{password}\"%7D");
    }

    @Test
    public void formParamsParseIntoIndexToName() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.FormParams.class, "login", String.class, String.class, String.class);
        FeignAssertions.assertThat(md.formParams()).containsExactly("customer_name", "user_name", "password");
        FeignAssertions.assertThat(md.indexToName()).containsExactly(entry(0, Arrays.asList("customer_name")), entry(1, Arrays.asList("user_name")), entry(2, Arrays.asList("password")));
    }

    /**
     * Body type is only for the body param.
     */
    @Test
    public void formParamsDoesNotSetBodyType() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.FormParams.class, "login", String.class, String.class, String.class);
        FeignAssertions.assertThat(md.bodyType()).isNull();
    }

    @Test
    public void headerParamsParseIntoIndexToName() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.HeaderParams.class, "logout", String.class);
        FeignAssertions.assertThat(md.template()).hasHeaders(entry("Auth-Token", Arrays.asList("{authToken}", "Foo")));
        FeignAssertions.assertThat(md.indexToName()).containsExactly(entry(0, Arrays.asList("authToken")));
        FeignAssertions.assertThat(md.formParams()).isEmpty();
    }

    @Test
    public void headerParamsParseIntoIndexToNameNotAtStart() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.HeaderParamsNotAtStart.class, "logout", String.class);
        FeignAssertions.assertThat(md.template()).hasHeaders(entry("Authorization", Arrays.asList("Bearer {authToken}", "Foo")));
        FeignAssertions.assertThat(md.indexToName()).containsExactly(entry(0, Arrays.asList("authToken")));
        FeignAssertions.assertThat(md.formParams()).isEmpty();
    }

    @Test
    public void customExpander() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.CustomExpander.class, "date", Date.class);
        FeignAssertions.assertThat(md.indexToExpanderClass()).containsExactly(entry(0, DefaultContractTest.DateToMillis.class));
    }

    @Test
    public void queryMap() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.QueryMapTestInterface.class, "queryMap", Map.class);
        FeignAssertions.assertThat(md.queryMapIndex()).isEqualTo(0);
    }

    @Test
    public void queryMapEncodedDefault() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.QueryMapTestInterface.class, "queryMap", Map.class);
        FeignAssertions.assertThat(md.queryMapEncoded()).isFalse();
    }

    @Test
    public void queryMapEncodedTrue() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.QueryMapTestInterface.class, "queryMapEncoded", Map.class);
        FeignAssertions.assertThat(md.queryMapEncoded()).isTrue();
    }

    @Test
    public void queryMapEncodedFalse() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.QueryMapTestInterface.class, "queryMapNotEncoded", Map.class);
        FeignAssertions.assertThat(md.queryMapEncoded()).isFalse();
    }

    @Test
    public void queryMapMapSubclass() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.QueryMapTestInterface.class, "queryMapMapSubclass", SortedMap.class);
        FeignAssertions.assertThat(md.queryMapIndex()).isEqualTo(0);
    }

    @Test
    public void onlyOneQueryMapAnnotationPermitted() throws Exception {
        try {
            parseAndValidateMetadata(DefaultContractTest.QueryMapTestInterface.class, "multipleQueryMap", Map.class, Map.class);
            Fail.failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException ex) {
            FeignAssertions.assertThat(ex).hasMessage("QueryMap annotation was present on multiple parameters.");
        }
    }

    @Test
    public void queryMapKeysMustBeStrings() throws Exception {
        try {
            parseAndValidateMetadata(DefaultContractTest.QueryMapTestInterface.class, "nonStringKeyQueryMap", Map.class);
            Fail.failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException ex) {
            FeignAssertions.assertThat(ex).hasMessage("QueryMap key must be a String: Integer");
        }
    }

    @Test
    public void queryMapPojoObject() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.QueryMapTestInterface.class, "pojoObject", Object.class);
        FeignAssertions.assertThat(md.queryMapIndex()).isEqualTo(0);
    }

    @Test
    public void queryMapPojoObjectEncoded() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.QueryMapTestInterface.class, "pojoObjectEncoded", Object.class);
        FeignAssertions.assertThat(md.queryMapIndex()).isEqualTo(0);
        FeignAssertions.assertThat(md.queryMapEncoded()).isTrue();
    }

    @Test
    public void queryMapPojoObjectNotEncoded() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.QueryMapTestInterface.class, "pojoObjectNotEncoded", Object.class);
        FeignAssertions.assertThat(md.queryMapIndex()).isEqualTo(0);
        FeignAssertions.assertThat(md.queryMapEncoded()).isFalse();
    }

    @Test
    public void slashAreEncodedWhenNeeded() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.SlashNeedToBeEncoded.class, "getQueues", String.class);
        FeignAssertions.assertThat(md.template().decodeSlash()).isFalse();
        md = parseAndValidateMetadata(DefaultContractTest.SlashNeedToBeEncoded.class, "getZone", String.class);
        FeignAssertions.assertThat(md.template().decodeSlash()).isTrue();
    }

    @Test
    public void onlyOneHeaderMapAnnotationPermitted() throws Exception {
        try {
            parseAndValidateMetadata(DefaultContractTest.HeaderMapInterface.class, "multipleHeaderMap", Map.class, Map.class);
            Fail.failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException ex) {
            FeignAssertions.assertThat(ex).hasMessage("HeaderMap annotation was present on multiple parameters.");
        }
    }

    @Test
    public void headerMapSubclass() throws Exception {
        MethodMetadata md = parseAndValidateMetadata(DefaultContractTest.HeaderMapInterface.class, "headerMapSubClass", DefaultContractTest.SubClassHeaders.class);
        FeignAssertions.assertThat(md.headerMapIndex()).isEqualTo(0);
    }

    interface Methods {
        @RequestLine("POST /")
        void post();

        @RequestLine("PUT /")
        void put();

        @RequestLine("GET /")
        void get();

        @RequestLine("DELETE /")
        void delete();
    }

    interface BodyParams {
        @RequestLine("POST")
        Response post(List<String> body);

        @RequestLine("PUT /offers/{id}")
        void post(@Param("id")
        int id, List<String> body);

        @RequestLine("POST")
        Response tooMany(List<String> body, List<String> body2);
    }

    interface CustomMethod {
        @RequestLine("PATCH")
        Response patch();
    }

    interface WithQueryParamsInPath {
        @RequestLine("GET /")
        Response none();

        @RequestLine("GET /?Action=GetUser")
        Response one();

        @RequestLine("GET /?Action=GetUser&Version=2010-05-08")
        Response two();

        @RequestLine("GET /?Action=GetUser&Version=2010-05-08&limit=1")
        Response three();

        @RequestLine("GET /?flag&Action=GetUser&Version=2010-05-08")
        Response twoAndOneEmpty();

        @RequestLine("GET /?flag")
        Response oneEmpty();

        @RequestLine("GET /?flag&NoErrors")
        Response twoEmpty();
    }

    interface BodyWithoutParameters {
        @RequestLine("POST /")
        @Headers("Content-Type: application/xml")
        @Body("<v01:getAccountsListOfUser/>")
        Response post();
    }

    @Headers("Content-Type: application/xml")
    interface HeadersOnType {
        @RequestLine("POST /")
        @Body("<v01:getAccountsListOfUser/>")
        Response post();
    }

    @Headers("Content-Type:    application/xml   ")
    interface HeadersContainsWhitespaces {
        @RequestLine("POST /")
        @Body("<v01:getAccountsListOfUser/>")
        Response post();
    }

    interface WithURIParam {
        @RequestLine("GET /{1}/{2}")
        Response uriParam(@Param("1")
        String one, URI endpoint, @Param("2")
        String two);
    }

    interface WithPathAndQueryParams {
        @RequestLine("GET /domains/{domainId}/records?name={name}&type={type}")
        Response recordsByNameAndType(@Param("domainId")
        int id, @Param("name")
        String nameFilter, @Param("type")
        String typeFilter);
    }

    interface FormParams {
        @RequestLine("POST /")
        @Body("%7B\"customer_name\": \"{customer_name}\", \"user_name\": \"{user_name}\", \"password\": \"{password}\"%7D")
        void login(@Param("customer_name")
        String customer, @Param("user_name")
        String user, @Param("password")
        String password);
    }

    interface HeaderMapInterface {
        @RequestLine("POST /")
        void multipleHeaderMap(@HeaderMap
        Map<String, String> headers, @HeaderMap
        Map<String, String> queries);

        @RequestLine("POST /")
        void headerMapSubClass(@HeaderMap
        DefaultContractTest.SubClassHeaders httpHeaders);
    }

    interface HeaderParams {
        @RequestLine("POST /")
        @Headers({ "Auth-Token: {authToken}", "Auth-Token: Foo" })
        void logout(@Param("authToken")
        String token);
    }

    interface HeaderParamsNotAtStart {
        @RequestLine("POST /")
        @Headers({ "Authorization: Bearer {authToken}", "Authorization: Foo" })
        void logout(@Param("authToken")
        String token);
    }

    interface CustomExpander {
        @RequestLine("POST /?date={date}")
        void date(@Param(value = "date", expander = DefaultContractTest.DateToMillis.class)
        Date date);
    }

    class DateToMillis implements Param.Expander {
        @Override
        public String expand(Object value) {
            return String.valueOf(((Date) (value)).getTime());
        }
    }

    interface QueryMapTestInterface {
        @RequestLine("POST /")
        void queryMap(@QueryMap
        Map<String, String> queryMap);

        @RequestLine("POST /")
        void queryMapMapSubclass(@QueryMap
        SortedMap<String, String> queryMap);

        @RequestLine("POST /")
        void queryMapEncoded(@QueryMap(encoded = true)
        Map<String, String> queryMap);

        @RequestLine("POST /")
        void queryMapNotEncoded(@QueryMap(encoded = false)
        Map<String, String> queryMap);

        @RequestLine("POST /")
        void pojoObject(@QueryMap
        Object object);

        @RequestLine("POST /")
        void pojoObjectEncoded(@QueryMap(encoded = true)
        Object object);

        @RequestLine("POST /")
        void pojoObjectNotEncoded(@QueryMap(encoded = false)
        Object object);

        // invalid
        @RequestLine("POST /")
        void multipleQueryMap(@QueryMap
        Map<String, String> mapOne, @QueryMap
        Map<String, String> mapTwo);

        // invalid
        @RequestLine("POST /")
        void nonStringKeyQueryMap(@QueryMap
        Map<Integer, String> queryMap);
    }

    interface SlashNeedToBeEncoded {
        @RequestLine(value = "GET /api/queues/{vhost}", decodeSlash = false)
        String getQueues(@Param("vhost")
        String vhost);

        @RequestLine("GET /api/{zoneId}")
        String getZone(@Param("ZoneId")
        String vhost);
    }

    @Headers("Foo: Bar")
    interface SimpleParameterizedBaseApi<M> {
        @RequestLine("GET /api/{zoneId}")
        M get(@Param("key")
        String key);
    }

    interface SimpleParameterizedApi extends DefaultContractTest.SimpleParameterizedBaseApi<String> {}

    @Test
    public void simpleParameterizedBaseApi() throws Exception {
        List<MethodMetadata> md = contract.parseAndValidatateMetadata(DefaultContractTest.SimpleParameterizedApi.class);
        FeignAssertions.assertThat(md).hasSize(1);
        FeignAssertions.assertThat(md.get(0).configKey()).isEqualTo("SimpleParameterizedApi#get(String)");
        FeignAssertions.assertThat(md.get(0).returnType()).isEqualTo(String.class);
        FeignAssertions.assertThat(md.get(0).template()).hasHeaders(entry("Foo", Arrays.asList("Bar")));
    }

    @Test
    public void parameterizedApiUnsupported() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Parameterized types unsupported: SimpleParameterizedBaseApi");
        contract.parseAndValidatateMetadata(DefaultContractTest.SimpleParameterizedBaseApi.class);
    }

    interface OverrideParameterizedApi extends DefaultContractTest.SimpleParameterizedBaseApi<String> {
        @Override
        @RequestLine("GET /api/{zoneId}")
        String get(@Param("key")
        String key);
    }

    @Test
    public void overrideBaseApiUnsupported() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Overrides unsupported: OverrideParameterizedApi#get(String)");
        contract.parseAndValidatateMetadata(DefaultContractTest.OverrideParameterizedApi.class);
    }

    interface Child<T> extends DefaultContractTest.SimpleParameterizedBaseApi<List<T>> {}

    interface GrandChild extends DefaultContractTest.Child<String> {}

    @Test
    public void onlySingleLevelInheritanceSupported() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Only single-level inheritance supported: GrandChild");
        contract.parseAndValidatateMetadata(DefaultContractTest.GrandChild.class);
    }

    @Headers("Foo: Bar")
    interface ParameterizedBaseApi<K, M> {
        @RequestLine("GET /api/{key}")
        DefaultContractTest.Entity<K, M> get(@Param("key")
        K key);

        @RequestLine("POST /api")
        DefaultContractTest.Entities<K, M> getAll(DefaultContractTest.Keys<K> keys);
    }

    static class Keys<K> {
        List<K> keys;
    }

    static class Entity<K, M> {
        K key;

        M model;
    }

    static class Entities<K, M> {
        private List<DefaultContractTest.Entity<K, M>> entities;
    }

    interface SubClassHeaders extends Map<String, String> {}

    @Headers("Version: 1")
    interface ParameterizedApi extends DefaultContractTest.ParameterizedBaseApi<String, Long> {}

    @Test
    public void parameterizedBaseApi() throws Exception {
        List<MethodMetadata> md = contract.parseAndValidatateMetadata(DefaultContractTest.ParameterizedApi.class);
        Map<String, MethodMetadata> byConfigKey = new LinkedHashMap<String, MethodMetadata>();
        for (MethodMetadata m : md) {
            byConfigKey.put(m.configKey(), m);
        }
        FeignAssertions.assertThat(byConfigKey).containsOnlyKeys("ParameterizedApi#get(String)", "ParameterizedApi#getAll(Keys)");
        FeignAssertions.assertThat(byConfigKey.get("ParameterizedApi#get(String)").returnType()).isEqualTo(new TypeToken<DefaultContractTest.Entity<String, Long>>() {}.getType());
        FeignAssertions.assertThat(byConfigKey.get("ParameterizedApi#get(String)").template()).hasHeaders(entry("Version", Arrays.asList("1")), entry("Foo", Arrays.asList("Bar")));
        FeignAssertions.assertThat(byConfigKey.get("ParameterizedApi#getAll(Keys)").returnType()).isEqualTo(new TypeToken<DefaultContractTest.Entities<String, Long>>() {}.getType());
        FeignAssertions.assertThat(byConfigKey.get("ParameterizedApi#getAll(Keys)").bodyType()).isEqualTo(new TypeToken<DefaultContractTest.Keys<String>>() {}.getType());
        FeignAssertions.assertThat(byConfigKey.get("ParameterizedApi#getAll(Keys)").template()).hasHeaders(entry("Version", Arrays.asList("1")), entry("Foo", Arrays.asList("Bar")));
    }

    @Headers("Authorization: {authHdr}")
    interface ParameterizedHeaderExpandApi {
        @RequestLine("GET /api/{zoneId}")
        @Headers("Accept: application/json")
        String getZone(@Param("zoneId")
        String vhost, @Param("authHdr")
        String authHdr);
    }

    @Test
    public void parameterizedHeaderExpandApi() throws Exception {
        List<MethodMetadata> md = contract.parseAndValidatateMetadata(DefaultContractTest.ParameterizedHeaderExpandApi.class);
        FeignAssertions.assertThat(md).hasSize(1);
        FeignAssertions.assertThat(md.get(0).configKey()).isEqualTo("ParameterizedHeaderExpandApi#getZone(String,String)");
        FeignAssertions.assertThat(md.get(0).returnType()).isEqualTo(String.class);
        FeignAssertions.assertThat(md.get(0).template()).hasHeaders(entry("Authorization", Arrays.asList("{authHdr}")), entry("Accept", Arrays.asList("application/json")));
        // Ensure that the authHdr expansion was properly detected and did not create a formParam
        FeignAssertions.assertThat(md.get(0).formParams()).isEmpty();
    }

    @Test
    public void parameterizedHeaderNotStartingWithCurlyBraceExpandApi() throws Exception {
        List<MethodMetadata> md = contract.parseAndValidatateMetadata(DefaultContractTest.ParameterizedHeaderNotStartingWithCurlyBraceExpandApi.class);
        FeignAssertions.assertThat(md).hasSize(1);
        FeignAssertions.assertThat(md.get(0).configKey()).isEqualTo("ParameterizedHeaderNotStartingWithCurlyBraceExpandApi#getZone(String,String)");
        FeignAssertions.assertThat(md.get(0).returnType()).isEqualTo(String.class);
        FeignAssertions.assertThat(md.get(0).template()).hasHeaders(entry("Authorization", Arrays.asList("Bearer {authHdr}")), entry("Accept", Arrays.asList("application/json")));
        // Ensure that the authHdr expansion was properly detected and did not create a formParam
        FeignAssertions.assertThat(md.get(0).formParams()).isEmpty();
    }

    @Headers("Authorization: Bearer {authHdr}")
    interface ParameterizedHeaderNotStartingWithCurlyBraceExpandApi {
        @RequestLine("GET /api/{zoneId}")
        @Headers("Accept: application/json")
        String getZone(@Param("zoneId")
        String vhost, @Param("authHdr")
        String authHdr);
    }

    @Headers("Authorization: {authHdr}")
    interface ParameterizedHeaderBase {}

    interface ParameterizedHeaderExpandInheritedApi extends DefaultContractTest.ParameterizedHeaderBase {
        @RequestLine("GET /api/{zoneId}")
        @Headers("Accept: application/json")
        String getZoneAccept(@Param("zoneId")
        String vhost, @Param("authHdr")
        String authHdr);

        @RequestLine("GET /api/{zoneId}")
        String getZone(@Param("zoneId")
        String vhost, @Param("authHdr")
        String authHdr);
    }

    @Test
    public void parameterizedHeaderExpandApiBaseClass() throws Exception {
        List<MethodMetadata> mds = contract.parseAndValidatateMetadata(DefaultContractTest.ParameterizedHeaderExpandInheritedApi.class);
        Map<String, MethodMetadata> byConfigKey = new LinkedHashMap<String, MethodMetadata>();
        for (MethodMetadata m : mds) {
            byConfigKey.put(m.configKey(), m);
        }
        FeignAssertions.assertThat(byConfigKey).containsOnlyKeys("ParameterizedHeaderExpandInheritedApi#getZoneAccept(String,String)", "ParameterizedHeaderExpandInheritedApi#getZone(String,String)");
        MethodMetadata md = byConfigKey.get("ParameterizedHeaderExpandInheritedApi#getZoneAccept(String,String)");
        FeignAssertions.assertThat(md.returnType()).isEqualTo(String.class);
        FeignAssertions.assertThat(md.template()).hasHeaders(entry("Authorization", Arrays.asList("{authHdr}")), entry("Accept", Arrays.asList("application/json")));
        // Ensure that the authHdr expansion was properly detected and did not create a formParam
        FeignAssertions.assertThat(md.formParams()).isEmpty();
        md = byConfigKey.get("ParameterizedHeaderExpandInheritedApi#getZone(String,String)");
        FeignAssertions.assertThat(md.returnType()).isEqualTo(String.class);
        FeignAssertions.assertThat(md.template()).hasHeaders(entry("Authorization", Arrays.asList("{authHdr}")));
        FeignAssertions.assertThat(md.formParams()).isEmpty();
    }

    interface MissingMethod {
        @RequestLine("/path?queryParam={queryParam}")
        Response updateSharing(@Param("queryParam")
        long queryParam, String bodyParam);
    }

    /**
     * Let's help folks not lose time when they mistake request line for a URI!
     */
    @Test
    public void missingMethod() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("RequestLine annotation didn't start with an HTTP verb on method updateSharing");
        contract.parseAndValidatateMetadata(DefaultContractTest.MissingMethod.class);
    }

    interface StaticMethodOnInterface {
        @RequestLine("GET /api/{key}")
        String get(@Param("key")
        String key);

        static String staticMethod() {
            return "value";
        }
    }

    @Test
    public void staticMethodsOnInterfaceIgnored() throws Exception {
        List<MethodMetadata> mds = contract.parseAndValidatateMetadata(DefaultContractTest.StaticMethodOnInterface.class);
        FeignAssertions.assertThat(mds).hasSize(1);
        MethodMetadata md = mds.get(0);
        FeignAssertions.assertThat(md.configKey()).isEqualTo("StaticMethodOnInterface#get(String)");
    }

    interface DefaultMethodOnInterface {
        @RequestLine("GET /api/{key}")
        String get(@Param("key")
        String key);

        default String defaultGet(String key) {
            return get(key);
        }
    }

    @Test
    public void defaultMethodsOnInterfaceIgnored() throws Exception {
        List<MethodMetadata> mds = contract.parseAndValidatateMetadata(DefaultContractTest.DefaultMethodOnInterface.class);
        FeignAssertions.assertThat(mds).hasSize(1);
        MethodMetadata md = mds.get(0);
        FeignAssertions.assertThat(md.configKey()).isEqualTo("DefaultMethodOnInterface#get(String)");
    }

    interface SubstringQuery {
        @RequestLine("GET /_search?q=body:{body}")
        String paramIsASubstringOfAQuery(@Param("body")
        String body);
    }

    @Test
    public void paramIsASubstringOfAQuery() throws Exception {
        List<MethodMetadata> mds = contract.parseAndValidatateMetadata(DefaultContractTest.SubstringQuery.class);
        FeignAssertions.assertThat(mds.get(0).template().queries()).containsExactly(entry("q", Arrays.asList("body:{body}")));
        FeignAssertions.assertThat(mds.get(0).formParams()).isEmpty();// Prevent issue 424

    }
}

