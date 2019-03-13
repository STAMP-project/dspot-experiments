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
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.spotify.apollo.Response;
import com.spotify.apollo.test.ServiceHelper;
import io.norberg.automatter.AutoMatter;
import io.norberg.automatter.jackson.AutoMatterModule;
import java.io.IOException;
import okio.ByteString;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class EntityCodecsTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES).registerModule(new AutoMatterModule());

    private static final ByteString JSON = ByteString.encodeUtf8("{\"naming_convention_used\":\"for this value\"}");

    private static final ByteString ENTITY = ByteString.of(new byte[]{ 69, 110, 116, 105, 116, 121 });

    @Test
    public void testWithCustomJacksonMapper() throws Exception {
        EntityMiddleware e = EntityMiddleware.forCodec(JacksonEntityCodec.forMapper(EntityCodecsTest.OBJECT_MAPPER));
        ServiceHelper service = ServiceHelper.create(entityApp(e), "entity-test");
        service.start();
        Response<ByteString> resp = EntityMiddlewareTest.await(service.request("GET", "/", EntityCodecsTest.JSON));
        Assert.assertThat(resp, hasStatus(withCode(Status.OK)));
        Assert.assertThat(resp, hasHeader("Content-Type", Matchers.equalTo("application/json")));
        Assert.assertThat(resp, hasPayload(JsonMatchers.asStr(JsonMatchers.hasJsonPath("naming_convention_used", Matchers.equalTo("override")))));
        service.close();
    }

    @Test
    public void testWithCustomContentType() throws Exception {
        EntityMiddleware e = EntityMiddleware.forCodec(JacksonEntityCodec.forMapper(EntityCodecsTest.OBJECT_MAPPER), "application/vnd+spotify.test+json");
        ServiceHelper service = ServiceHelper.create(entityApp(e), "entity-test");
        service.start();
        Response<ByteString> resp = EntityMiddlewareTest.await(service.request("GET", "/", EntityCodecsTest.JSON));
        Assert.assertThat(resp, hasStatus(withCode(Status.OK)));
        Assert.assertThat(resp, hasHeader("Content-Type", Matchers.equalTo("application/vnd+spotify.test+json")));
        Assert.assertThat(resp, hasPayload(JsonMatchers.asStr(JsonMatchers.hasJsonPath("naming_convention_used", Matchers.equalTo("override")))));
        service.close();
    }

    @Test
    public void testWithCustomCodec() throws Exception {
        EntityMiddleware e = EntityMiddleware.forCodec(new EntityCodecsTest.StringCodec());
        ServiceHelper service = ServiceHelper.create(stringApp(e), "entity-test");
        service.start();
        Response<ByteString> resp = EntityMiddlewareTest.await(service.request("GET", "/", EntityCodecsTest.ENTITY));
        Assert.assertThat(resp, hasStatus(withCode(Status.OK)));
        Assert.assertThat(resp, hasHeader("Content-Type", Matchers.equalTo("text/plain")));
        Assert.assertThat(resp, hasPayload(JsonMatchers.asStr(Matchers.equalTo("EntityMiddleware"))));
        service.close();
    }

    @Test
    public void testWithCustomCodecContentType() throws Exception {
        EntityMiddleware e = EntityMiddleware.forCodec(new EntityCodecsTest.StringCodec(), "text/vnd+spotify.test+plain");
        ServiceHelper service = ServiceHelper.create(stringApp(e), "entity-test");
        service.start();
        Response<ByteString> resp = EntityMiddlewareTest.await(service.request("GET", "/", EntityCodecsTest.ENTITY));
        Assert.assertThat(resp, hasStatus(withCode(Status.OK)));
        Assert.assertThat(resp, hasHeader("Content-Type", Matchers.equalTo("text/vnd+spotify.test+plain")));
        Assert.assertThat(resp, hasPayload(JsonMatchers.asStr(Matchers.equalTo("EntityMiddleware"))));
        service.close();
    }

    @AutoMatter
    interface Entity {
        String namingConventionUsed();
    }

    private static final class StringCodec implements EntityCodec {
        @Override
        public String defaultContentType() {
            return "text/plain";
        }

        @Override
        public <E> ByteString write(E entity, Class<? extends E> clazz) throws IOException {
            if (!(String.class.equals(clazz))) {
                throw new UnsupportedOperationException("Can only encode strings");
            }
            return ByteString.encodeUtf8(((String) (entity)));
        }

        @Override
        public <E> E read(ByteString data, Class<? extends E> clazz) throws IOException {
            if (!(String.class.equals(clazz))) {
                throw new UnsupportedOperationException("Can only encode strings");
            }
            // noinspection unchecked
            return ((E) (data.utf8()));
        }
    }
}

