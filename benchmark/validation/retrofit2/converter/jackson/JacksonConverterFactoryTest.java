/**
 * Copyright (C) 2013 Square, Inc.
 *
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
 */
package retrofit2.converter.jackson;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;


public class JacksonConverterFactoryTest {
    interface AnInterface {
        String getName();
    }

    static class AnImplementation implements JacksonConverterFactoryTest.AnInterface {
        private String theName;

        AnImplementation() {
        }

        AnImplementation(String name) {
            theName = name;
        }

        @Override
        public String getName() {
            return theName;
        }
    }

    static class AnInterfaceSerializer extends StdSerializer<JacksonConverterFactoryTest.AnInterface> {
        AnInterfaceSerializer() {
            super(JacksonConverterFactoryTest.AnInterface.class);
        }

        @Override
        public void serialize(JacksonConverterFactoryTest.AnInterface anInterface, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeFieldName("name");
            jsonGenerator.writeString(anInterface.getName());
            jsonGenerator.writeEndObject();
        }
    }

    static class AnInterfaceDeserializer extends StdDeserializer<JacksonConverterFactoryTest.AnInterface> {
        AnInterfaceDeserializer() {
            super(JacksonConverterFactoryTest.AnInterface.class);
        }

        @Override
        public JacksonConverterFactoryTest.AnInterface deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            if ((jp.getCurrentToken()) != (JsonToken.START_OBJECT)) {
                throw new AssertionError("Expected start object.");
            }
            String name = null;
            while ((jp.nextToken()) != (JsonToken.END_OBJECT)) {
                switch (jp.getCurrentName()) {
                    case "name" :
                        name = jp.getValueAsString();
                        break;
                }
            } 
            return new JacksonConverterFactoryTest.AnImplementation(name);
        }
    }

    interface Service {
        @POST("/")
        Call<JacksonConverterFactoryTest.AnImplementation> anImplementation(@Body
        JacksonConverterFactoryTest.AnImplementation impl);

        @POST("/")
        Call<JacksonConverterFactoryTest.AnInterface> anInterface(@Body
        JacksonConverterFactoryTest.AnInterface impl);
    }

    @Rule
    public final MockWebServer server = new MockWebServer();

    private JacksonConverterFactoryTest.Service service;

    @Test
    public void anInterface() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setBody("{\"name\":\"value\"}"));
        Call<JacksonConverterFactoryTest.AnInterface> call = service.anInterface(new JacksonConverterFactoryTest.AnImplementation("value"));
        Response<JacksonConverterFactoryTest.AnInterface> response = call.execute();
        JacksonConverterFactoryTest.AnInterface body = response.body();
        assertThat(body.getName()).isEqualTo("value");
        RecordedRequest request = server.takeRequest();
        assertThat(request.getBody().readUtf8()).isEqualTo("{\"name\":\"value\"}");
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8");
    }

    @Test
    public void anImplementation() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setBody("{\"theName\":\"value\"}"));
        Call<JacksonConverterFactoryTest.AnImplementation> call = service.anImplementation(new JacksonConverterFactoryTest.AnImplementation("value"));
        Response<JacksonConverterFactoryTest.AnImplementation> response = call.execute();
        JacksonConverterFactoryTest.AnImplementation body = response.body();
        assertThat(body.theName).isEqualTo("value");
        RecordedRequest request = server.takeRequest();
        // TODO figure out how to get Jackson to stop using AnInterface's serializer here.
        assertThat(request.getBody().readUtf8()).isEqualTo("{\"name\":\"value\"}");
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8");
    }
}

