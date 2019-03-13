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
package retrofit2.converter.moshi;


import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonQualifier;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.ToJson;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.Charset;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import okio.ByteString;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


public final class MoshiConverterFactoryTest {
    @Retention(RetentionPolicy.RUNTIME)
    @JsonQualifier
    @interface Qualifier {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface NonQualifer {}

    interface AnInterface {
        String getName();
    }

    static class AnImplementation implements MoshiConverterFactoryTest.AnInterface {
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
        final String theName;

        Value(String theName) {
            this.theName = theName;
        }
    }

    static class Adapters {
        @ToJson
        public void write(JsonWriter jsonWriter, MoshiConverterFactoryTest.AnInterface anInterface) throws IOException {
            jsonWriter.beginObject();
            jsonWriter.name("name").value(anInterface.getName());
            jsonWriter.endObject();
        }

        @FromJson
        public MoshiConverterFactoryTest.AnInterface read(JsonReader jsonReader) throws IOException {
            jsonReader.beginObject();
            String name = null;
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case "name" :
                        name = jsonReader.nextString();
                        break;
                }
            } 
            jsonReader.endObject();
            return new MoshiConverterFactoryTest.AnImplementation(name);
        }

        @ToJson
        public void write(JsonWriter writer, @MoshiConverterFactoryTest.Qualifier
        String value) throws IOException {
            writer.value("qualified!");
        }

        @FromJson
        @MoshiConverterFactoryTest.Qualifier
        public String readQualified(JsonReader reader) throws IOException {
            String string = reader.nextString();
            if (string.equals("qualified!")) {
                return "it worked!";
            }
            throw new AssertionError(("Found: " + string));
        }

        @FromJson
        public MoshiConverterFactoryTest.Value readWithoutEndingObject(JsonReader reader) throws IOException {
            reader.beginObject();
            reader.skipName();
            String theName = reader.nextString();
            return new MoshiConverterFactoryTest.Value(theName);
        }
    }

    interface Service {
        @POST("/")
        Call<MoshiConverterFactoryTest.AnImplementation> anImplementation(@Body
        MoshiConverterFactoryTest.AnImplementation impl);

        @POST("/")
        Call<MoshiConverterFactoryTest.AnInterface> anInterface(@Body
        MoshiConverterFactoryTest.AnInterface impl);

        @GET("/")
        Call<MoshiConverterFactoryTest.Value> value();

        // 
        @POST("/")
        @MoshiConverterFactoryTest.Qualifier
        @MoshiConverterFactoryTest.NonQualifer
        Call<String> annotations(@Body
        @MoshiConverterFactoryTest.Qualifier
        @MoshiConverterFactoryTest.NonQualifer
        String body);
    }

    @Rule
    public final MockWebServer server = new MockWebServer();

    private MoshiConverterFactoryTest.Service service;

    private MoshiConverterFactoryTest.Service serviceLenient;

    private MoshiConverterFactoryTest.Service serviceNulls;

    private MoshiConverterFactoryTest.Service serviceFailOnUnknown;

    @Test
    public void anInterface() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setBody("{\"name\":\"value\"}"));
        Call<MoshiConverterFactoryTest.AnInterface> call = service.anInterface(new MoshiConverterFactoryTest.AnImplementation("value"));
        Response<MoshiConverterFactoryTest.AnInterface> response = call.execute();
        MoshiConverterFactoryTest.AnInterface body = response.body();
        assertThat(body.getName()).isEqualTo("value");
        RecordedRequest request = server.takeRequest();
        assertThat(request.getBody().readUtf8()).isEqualTo("{\"name\":\"value\"}");
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8");
    }

    @Test
    public void anImplementation() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setBody("{\"theName\":\"value\"}"));
        Call<MoshiConverterFactoryTest.AnImplementation> call = service.anImplementation(new MoshiConverterFactoryTest.AnImplementation("value"));
        Response<MoshiConverterFactoryTest.AnImplementation> response = call.execute();
        MoshiConverterFactoryTest.AnImplementation body = response.body();
        assertThat(body.theName).isEqualTo("value");
        RecordedRequest request = server.takeRequest();
        assertThat(request.getBody().readUtf8()).isEqualTo("{\"theName\":\"value\"}");
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8");
    }

    @Test
    public void annotations() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setBody("\"qualified!\""));
        Call<String> call = service.annotations("value");
        Response<String> response = call.execute();
        assertThat(response.body()).isEqualTo("it worked!");
        RecordedRequest request = server.takeRequest();
        assertThat(request.getBody().readUtf8()).isEqualTo("\"qualified!\"");
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8");
    }

    @Test
    public void asLenient() throws IOException, InterruptedException {
        MockResponse malformedResponse = new MockResponse().setBody("{\"theName\":value}");
        server.enqueue(malformedResponse);
        server.enqueue(malformedResponse);
        Call<MoshiConverterFactoryTest.AnImplementation> call = service.anImplementation(new MoshiConverterFactoryTest.AnImplementation("value"));
        try {
            call.execute();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertEquals(e.getMessage(), "Use JsonReader.setLenient(true) to accept malformed JSON at path $.theName");
        }
        Call<MoshiConverterFactoryTest.AnImplementation> call2 = serviceLenient.anImplementation(new MoshiConverterFactoryTest.AnImplementation("value"));
        Response<MoshiConverterFactoryTest.AnImplementation> response = call2.execute();
        MoshiConverterFactoryTest.AnImplementation body = response.body();
        assertThat(body.theName).isEqualTo("value");
    }

    @Test
    public void withNulls() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setBody("{}"));
        Call<MoshiConverterFactoryTest.AnImplementation> call = serviceNulls.anImplementation(new MoshiConverterFactoryTest.AnImplementation(null));
        call.execute();
        Assert.assertEquals("{\"theName\":null}", server.takeRequest().getBody().readUtf8());
    }

    @Test
    public void failOnUnknown() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setBody("{\"taco\":\"delicious\"}"));
        Call<MoshiConverterFactoryTest.AnImplementation> call = serviceFailOnUnknown.anImplementation(new MoshiConverterFactoryTest.AnImplementation(null));
        try {
            call.execute();
            Assert.fail();
        } catch (JsonDataException e) {
            assertThat(e).hasMessage("Cannot skip unexpected NAME at $.");
        }
    }

    @Test
    public void utf8BomSkipped() throws IOException {
        Buffer responseBody = new Buffer().write(ByteString.decodeHex("EFBBBF")).writeUtf8("{\"theName\":\"value\"}");
        MockResponse malformedResponse = new MockResponse().setBody(responseBody);
        server.enqueue(malformedResponse);
        Call<MoshiConverterFactoryTest.AnImplementation> call = service.anImplementation(new MoshiConverterFactoryTest.AnImplementation("value"));
        Response<MoshiConverterFactoryTest.AnImplementation> response = call.execute();
        MoshiConverterFactoryTest.AnImplementation body = response.body();
        assertThat(body.theName).isEqualTo("value");
    }

    @Test
    public void nonUtf8BomIsNotSkipped() throws IOException {
        Buffer responseBody = new Buffer().write(ByteString.decodeHex("FEFF")).writeString("{\"theName\":\"value\"}", Charset.forName("UTF-16"));
        MockResponse malformedResponse = new MockResponse().setBody(responseBody);
        server.enqueue(malformedResponse);
        Call<MoshiConverterFactoryTest.AnImplementation> call = service.anImplementation(new MoshiConverterFactoryTest.AnImplementation("value"));
        try {
            call.execute();
            Assert.fail();
        } catch (IOException expected) {
        }
    }

    @Test
    public void requireFullResponseDocumentConsumption() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"theName\":\"value\"}"));
        Call<MoshiConverterFactoryTest.Value> call = service.value();
        try {
            call.execute();
            Assert.fail();
        } catch (JsonDataException e) {
            assertThat(e).hasMessage("JSON document was not fully consumed.");
        }
    }
}

