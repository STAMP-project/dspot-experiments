/**
 * Copyright (C) 2015 Square, Inc.
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
package retrofit2.converter.gson;


import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


public final class GsonConverterFactoryTest {
    interface AnInterface {
        String getName();
    }

    static class AnImplementation implements GsonConverterFactoryTest.AnInterface {
        private final String theName;

        AnImplementation(String name) {
            theName = name;
        }

        @Override
        public String getName() {
            return theName;
        }
    }

    static final class Value {
        static final TypeAdapter<GsonConverterFactoryTest.Value> BROKEN_ADAPTER = new TypeAdapter<GsonConverterFactoryTest.Value>() {
            @Override
            public void write(JsonWriter out, GsonConverterFactoryTest.Value value) {
                throw new AssertionError();
            }

            @Override
            public GsonConverterFactoryTest.Value read(JsonReader reader) throws IOException {
                reader.beginObject();
                reader.nextName();
                String theName = reader.nextString();
                return new GsonConverterFactoryTest.Value(theName);
            }
        };

        final String theName;

        Value(String theName) {
            this.theName = theName;
        }
    }

    static class AnInterfaceAdapter extends TypeAdapter<GsonConverterFactoryTest.AnInterface> {
        @Override
        public void write(JsonWriter jsonWriter, GsonConverterFactoryTest.AnInterface anInterface) throws IOException {
            jsonWriter.beginObject();
            jsonWriter.name("name").value(anInterface.getName());
            jsonWriter.endObject();
        }

        @Override
        public GsonConverterFactoryTest.AnInterface read(JsonReader jsonReader) throws IOException {
            jsonReader.beginObject();
            String name = null;
            while ((jsonReader.peek()) != (JsonToken.END_OBJECT)) {
                switch (jsonReader.nextName()) {
                    case "name" :
                        name = jsonReader.nextString();
                        break;
                }
            } 
            jsonReader.endObject();
            return new GsonConverterFactoryTest.AnImplementation(name);
        }
    }

    interface Service {
        @POST("/")
        Call<GsonConverterFactoryTest.AnImplementation> anImplementation(@Body
        GsonConverterFactoryTest.AnImplementation impl);

        @POST("/")
        Call<GsonConverterFactoryTest.AnInterface> anInterface(@Body
        GsonConverterFactoryTest.AnInterface impl);

        @GET("/")
        Call<GsonConverterFactoryTest.Value> value();
    }

    @Rule
    public final MockWebServer server = new MockWebServer();

    private GsonConverterFactoryTest.Service service;

    @Test
    public void anInterface() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setBody("{\"name\":\"value\"}"));
        Call<GsonConverterFactoryTest.AnInterface> call = service.anInterface(new GsonConverterFactoryTest.AnImplementation("value"));
        Response<GsonConverterFactoryTest.AnInterface> response = call.execute();
        GsonConverterFactoryTest.AnInterface body = response.body();
        assertThat(body.getName()).isEqualTo("value");
        RecordedRequest request = server.takeRequest();
        assertThat(request.getBody().readUtf8()).isEqualTo("{\"name\":\"value\"}");
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8");
    }

    @Test
    public void anImplementation() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setBody("{\"theName\":\"value\"}"));
        Call<GsonConverterFactoryTest.AnImplementation> call = service.anImplementation(new GsonConverterFactoryTest.AnImplementation("value"));
        Response<GsonConverterFactoryTest.AnImplementation> response = call.execute();
        GsonConverterFactoryTest.AnImplementation body = response.body();
        assertThat(body.theName).isEqualTo("value");
        RecordedRequest request = server.takeRequest();
        assertThat(request.getBody().readUtf8()).isEqualTo("{\"theName\":\"value\"}");
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8");
    }

    @Test
    public void serializeUsesConfiguration() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setBody("{}"));
        service.anImplementation(new GsonConverterFactoryTest.AnImplementation(null)).execute();
        RecordedRequest request = server.takeRequest();
        assertThat(request.getBody().readUtf8()).isEqualTo("{}");// Null value was not serialized.

        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8");
    }

    @Test
    public void deserializeUsesConfiguration() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setBody("{/* a comment! */}"));
        Response<GsonConverterFactoryTest.AnImplementation> response = service.anImplementation(new GsonConverterFactoryTest.AnImplementation("value")).execute();
        assertThat(response.body().getName()).isNull();
    }

    @Test
    public void requireFullResponseDocumentConsumption() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"theName\":\"value\"}"));
        Call<GsonConverterFactoryTest.Value> call = service.value();
        try {
            call.execute();
            Assert.fail();
        } catch (JsonIOException e) {
            assertThat(e).hasMessage("JSON document was not fully consumed.");
        }
    }
}

