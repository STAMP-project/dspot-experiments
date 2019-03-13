/**
 * -\-\-
 * Spotify Apollo Entity Middleware
 * --
 * Copyright (C) 2013 - 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.apollo.entity;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.apollo.Response;
import com.spotify.apollo.test.ServiceHelper;
import io.norberg.automatter.AutoMatter;
import io.norberg.automatter.jackson.AutoMatterModule;
import java.util.List;
import okio.ByteString;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


public final class EntityMiddlewareTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new AutoMatterModule());

    private static final ByteString PERSON_JSON = ByteString.encodeUtf8("{\"name\":\"rouz\"}");

    private static final ByteString BAD_JSON = ByteString.encodeUtf8("I am not Jayson");

    private static final String DIRECT = "/direct";

    private static final String DIRECT_O = "/direct-o";

    private static final String RESPONSE = "/response";

    private static final String RESPONSE_O = "/response-o";

    private static final String RESPONSE_E = "/response-e";

    private static final String GET_DIRECT = "/person";

    private static final String GET_RESPONSE = "/person-resp";

    private static final String UNSERIALIZABLE = "/unserializable";

    @ClassRule
    public static ServiceHelper serviceHelper = ServiceHelper.create(EntityMiddlewareTest::init, "entity-test");

    @Test
    public void testDirectPerson() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", EntityMiddlewareTest.DIRECT, EntityMiddlewareTest.PERSON_JSON));
        Assert.assertThat(resp, hasStatus(withCode(Status.OK)));
        Assert.assertThat(resp, hasHeader("Content-Type", Matchers.equalTo("application/json")));
        Assert.assertThat(resp, hasPayload(JsonMatchers.asStr(JsonMatchers.hasJsonPath("name", Matchers.equalTo("hello rouz")))));
    }

    @Test
    public void testDirectOther() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", EntityMiddlewareTest.DIRECT_O, EntityMiddlewareTest.PERSON_JSON));
        Assert.assertThat(resp, hasStatus(withCode(Status.OK)));
        Assert.assertThat(resp, hasHeader("Content-Type", Matchers.equalTo("application/json")));
        Assert.assertThat(resp, hasPayload(JsonMatchers.asStr(JsonMatchers.hasJsonPath("people[0].name", Matchers.equalTo("rouz")))));
    }

    @Test
    public void testResponse() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", EntityMiddlewareTest.RESPONSE, EntityMiddlewareTest.PERSON_JSON));
        Assert.assertThat(resp, hasStatus(withCode(Status.ACCEPTED)));
        Assert.assertThat(resp, hasHeader("Content-Type", Matchers.equalTo("application/json")));
        Assert.assertThat(resp, hasHeader("Name-Length", Matchers.equalTo("4")));
        Assert.assertThat(resp, hasPayload(JsonMatchers.asStr(JsonMatchers.hasJsonPath("name", Matchers.equalTo("rouz-resp")))));
    }

    @Test
    public void testResponseOther() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", EntityMiddlewareTest.RESPONSE_O, EntityMiddlewareTest.PERSON_JSON));
        Assert.assertThat(resp, hasStatus(withCode(Status.ACCEPTED)));
        Assert.assertThat(resp, hasStatus(withReasonPhrase(Matchers.equalTo("was rouz"))));
        Assert.assertThat(resp, hasHeader("Content-Type", Matchers.equalTo("application/json")));
        Assert.assertThat(resp, hasPayload(JsonMatchers.asStr(JsonMatchers.hasJsonPath("people[0].name", Matchers.equalTo("rouz")))));
    }

    @Test
    public void testResponseEmpty() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", EntityMiddlewareTest.RESPONSE_E, EntityMiddlewareTest.PERSON_JSON));
        Assert.assertThat(resp, hasStatus(withCode(Status.ACCEPTED)));
        Assert.assertThat(resp, hasStatus(withReasonPhrase(Matchers.equalTo("was rouz"))));
        Assert.assertThat(resp, doesNotHaveHeader("Content-Type"));
        Assert.assertThat(resp, hasNoPayload());
    }

    @Test
    public void testGetPerson() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("GET", EntityMiddlewareTest.GET_DIRECT));
        Assert.assertThat(resp, hasStatus(withCode(Status.OK)));
        Assert.assertThat(resp, hasHeader("Content-Type", Matchers.equalTo("application/json")));
        Assert.assertThat(resp, hasPayload(JsonMatchers.asStr(JsonMatchers.hasJsonPath("name", Matchers.equalTo("static")))));
    }

    @Test
    public void testGetPersonResponse() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("GET", EntityMiddlewareTest.GET_RESPONSE));
        Assert.assertThat(resp, hasStatus(withCode(Status.OK)));
        Assert.assertThat(resp, hasHeader("Content-Type", Matchers.equalTo("application/json")));
        Assert.assertThat(resp, hasHeader("Reflection", Matchers.equalTo("in-use")));
        Assert.assertThat(resp, hasPayload(JsonMatchers.asStr(JsonMatchers.hasJsonPath("name", Matchers.equalTo("static-resp")))));
    }

    @Test
    public void testDirectAsync() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", ((EntityMiddlewareTest.DIRECT) + "-async"), EntityMiddlewareTest.PERSON_JSON));
        Assert.assertThat(resp, hasStatus(withCode(Status.OK)));
        Assert.assertThat(resp, hasHeader("Content-Type", Matchers.equalTo("application/json")));
        Assert.assertThat(resp, hasPayload(JsonMatchers.asStr(JsonMatchers.hasJsonPath("name", Matchers.equalTo("hello rouz")))));
    }

    @Test
    public void testDirectAsyncOther() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", ((EntityMiddlewareTest.DIRECT_O) + "-async"), EntityMiddlewareTest.PERSON_JSON));
        Assert.assertThat(resp, hasStatus(withCode(Status.OK)));
        Assert.assertThat(resp, hasHeader("Content-Type", Matchers.equalTo("application/json")));
        Assert.assertThat(resp, hasPayload(JsonMatchers.asStr(JsonMatchers.hasJsonPath("people[0].name", Matchers.equalTo("rouz")))));
    }

    @Test
    public void testResponseAsync() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", ((EntityMiddlewareTest.RESPONSE) + "-async"), EntityMiddlewareTest.PERSON_JSON));
        Assert.assertThat(resp, hasStatus(withCode(Status.ACCEPTED)));
        Assert.assertThat(resp, hasHeader("Content-Type", Matchers.equalTo("application/json")));
        Assert.assertThat(resp, hasHeader("Async", Matchers.equalTo("true")));
        Assert.assertThat(resp, hasPayload(JsonMatchers.asStr(JsonMatchers.hasJsonPath("name", Matchers.equalTo("hello rouz")))));
    }

    @Test
    public void testResponseAsyncOther() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", ((EntityMiddlewareTest.RESPONSE_O) + "-async"), EntityMiddlewareTest.PERSON_JSON));
        Assert.assertThat(resp, hasStatus(withCode(Status.ACCEPTED)));
        Assert.assertThat(resp, hasHeader("Async", Matchers.equalTo("true")));
        Assert.assertThat(resp, hasHeader("Content-Type", Matchers.equalTo("application/json")));
        Assert.assertThat(resp, hasPayload(JsonMatchers.asStr(JsonMatchers.hasJsonPath("people[0].name", Matchers.equalTo("rouz")))));
    }

    @Test
    public void testResponseAsyncEmpty() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", ((EntityMiddlewareTest.RESPONSE_E) + "-async"), EntityMiddlewareTest.PERSON_JSON));
        Assert.assertThat(resp, hasStatus(withCode(Status.ACCEPTED)));
        Assert.assertThat(resp, hasHeader("Async", Matchers.equalTo("true")));
        Assert.assertThat(resp, doesNotHaveHeader("Content-Type"));
        Assert.assertThat(resp, hasNoPayload());
    }

    @Test
    public void testGetAsyncPerson() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("GET", ((EntityMiddlewareTest.GET_DIRECT) + "-async")));
        Assert.assertThat(resp, hasStatus(withCode(Status.OK)));
        Assert.assertThat(resp, hasHeader("Content-Type", Matchers.equalTo("application/json")));
        Assert.assertThat(resp, hasPayload(JsonMatchers.asStr(JsonMatchers.hasJsonPath("name", Matchers.equalTo("static-async")))));
    }

    @Test
    public void testGetAsyncPersonResponse() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("GET", ((EntityMiddlewareTest.GET_RESPONSE) + "-async")));
        Assert.assertThat(resp, hasStatus(withCode(Status.OK)));
        Assert.assertThat(resp, hasHeader("Content-Type", Matchers.equalTo("application/json")));
        Assert.assertThat(resp, hasHeader("Async", Matchers.equalTo("true")));
        Assert.assertThat(resp, hasHeader("Reflection", Matchers.equalTo("in-use")));
        Assert.assertThat(resp, hasPayload(JsonMatchers.asStr(JsonMatchers.hasJsonPath("name", Matchers.equalTo("static-resp-async")))));
    }

    @Test
    public void testMissingPayloadDirect() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", EntityMiddlewareTest.DIRECT));
        assertBadRequest(resp, Matchers.equalTo("Missing payload"));
    }

    @Test
    public void testMissingPayloadDirectAsync() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", ((EntityMiddlewareTest.DIRECT) + "-async")));
        assertBadRequest(resp, Matchers.equalTo("Missing payload"));
    }

    @Test
    public void testMissingPayloadResponse() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", EntityMiddlewareTest.RESPONSE));
        assertBadRequest(resp, Matchers.equalTo("Missing payload"));
    }

    @Test
    public void testMissingPayloadResponseAsync() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", ((EntityMiddlewareTest.RESPONSE) + "-async")));
        assertBadRequest(resp, Matchers.equalTo("Missing payload"));
    }

    @Test
    public void testBadPayloadDirect() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", EntityMiddlewareTest.DIRECT, EntityMiddlewareTest.BAD_JSON));
        assertBadRequest(resp, Matchers.startsWith("Payload parsing failed"));
    }

    @Test
    public void testBadPayloadDirectAsync() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", ((EntityMiddlewareTest.DIRECT) + "-async"), EntityMiddlewareTest.BAD_JSON));
        assertBadRequest(resp, Matchers.startsWith("Payload parsing failed"));
    }

    @Test
    public void testBadPayloadResponse() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", EntityMiddlewareTest.RESPONSE, EntityMiddlewareTest.BAD_JSON));
        assertBadRequest(resp, Matchers.startsWith("Payload parsing failed"));
    }

    @Test
    public void testBadPayloadResponseAsync() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("POST", ((EntityMiddlewareTest.RESPONSE) + "-async"), EntityMiddlewareTest.BAD_JSON));
        assertBadRequest(resp, Matchers.startsWith("Payload parsing failed"));
    }

    @Test
    public void testBadResponse() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("GET", EntityMiddlewareTest.UNSERIALIZABLE));
        Assert.assertThat(resp, hasStatus(withCode(Status.INTERNAL_SERVER_ERROR)));
        Assert.assertThat(resp, hasStatus(withReasonPhrase(Matchers.startsWith("Payload serialization failed"))));
        Assert.assertThat(resp, doesNotHaveHeader("Content-Type"));
        Assert.assertThat(resp, hasNoPayload());
    }

    @Test
    public void testBadResponseAsync() throws Exception {
        Response<ByteString> resp = EntityMiddlewareTest.await(EntityMiddlewareTest.serviceHelper.request("GET", ((EntityMiddlewareTest.UNSERIALIZABLE) + "-async")));
        Assert.assertThat(resp, hasStatus(withCode(Status.INTERNAL_SERVER_ERROR)));
        Assert.assertThat(resp, hasStatus(withReasonPhrase(Matchers.startsWith("Payload serialization failed"))));
        Assert.assertThat(resp, doesNotHaveHeader("Content-Type"));
        Assert.assertThat(resp, hasNoPayload());
    }

    @AutoMatter
    public interface Person {
        String name();
    }

    @AutoMatter
    public interface Group {
        List<EntityMiddlewareTest.Person> people();
    }

    interface Unserializable {}
}

