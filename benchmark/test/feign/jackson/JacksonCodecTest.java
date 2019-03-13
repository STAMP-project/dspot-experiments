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
package feign.jackson;


import HttpMethod.GET;
import Util.UTF_8;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import feign.Util;
import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class JacksonCodecTest {
    private String zonesJson = (((((((((((((((((""// 
     + "[") + (System.lineSeparator()))// 
     + "  {") + (System.lineSeparator()))// 
     + "    \"name\": \"denominator.io.\"") + (System.lineSeparator()))// 
     + "  },") + (System.lineSeparator()))// 
     + "  {") + (System.lineSeparator()))// 
     + "    \"name\": \"denominator.io.\",") + (System.lineSeparator()))// 
     + "    \"id\": \"ABCD\"") + (System.lineSeparator()))// 
     + "  }") + (System.lineSeparator()))// 
     + "]") + (System.lineSeparator());

    @Test
    public void encodesMapObjectNumericalValuesAsInteger() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("foo", 1);
        RequestTemplate template = new RequestTemplate();
        new JacksonEncoder().encode(map, map.getClass(), template);
        assertThat(template).hasBody((((((""// 
         + "{") + (System.lineSeparator()))// 
         + "  \"foo\" : 1") + (System.lineSeparator()))// 
         + "}"));
    }

    @Test
    public void encodesFormParams() {
        Map<String, Object> form = new LinkedHashMap<String, Object>();
        form.put("foo", 1);
        form.put("bar", Arrays.asList(2, 3));
        RequestTemplate template = new RequestTemplate();
        new JacksonEncoder().encode(form, new TypeReference<Map<String, ?>>() {}.getType(), template);
        assertThat(template).hasBody((((((((""// 
         + "{") + (System.lineSeparator()))// 
         + "  \"foo\" : 1,") + (System.lineSeparator()))// 
         + "  \"bar\" : [ 2, 3 ]") + (System.lineSeparator()))// 
         + "}"));
    }

    @Test
    public void decodes() throws Exception {
        List<JacksonCodecTest.Zone> zones = new LinkedList<>();
        zones.add(new JacksonCodecTest.Zone("denominator.io."));
        zones.add(new JacksonCodecTest.Zone("denominator.io.", "ABCD"));
        Response response = Response.builder().status(200).reason("OK").request(Request.create(GET, "/api", Collections.emptyMap(), null, UTF_8)).headers(Collections.emptyMap()).body(zonesJson, Util.UTF_8).build();
        Assert.assertEquals(zones, new JacksonDecoder().decode(response, new TypeReference<List<JacksonCodecTest.Zone>>() {}.getType()));
    }

    @Test
    public void nullBodyDecodesToNull() throws Exception {
        Response response = Response.builder().status(204).reason("OK").request(Request.create(GET, "/api", Collections.emptyMap(), null, UTF_8)).headers(Collections.emptyMap()).build();
        Assert.assertNull(new JacksonDecoder().decode(response, String.class));
    }

    @Test
    public void emptyBodyDecodesToNull() throws Exception {
        Response response = Response.builder().status(204).reason("OK").request(Request.create(GET, "/api", Collections.emptyMap(), null, UTF_8)).headers(Collections.emptyMap()).body(new byte[0]).build();
        Assert.assertNull(new JacksonDecoder().decode(response, String.class));
    }

    @Test
    public void customDecoder() throws Exception {
        JacksonDecoder decoder = new JacksonDecoder(Arrays.asList(new SimpleModule().addDeserializer(JacksonCodecTest.Zone.class, new JacksonCodecTest.ZoneDeserializer())));
        List<JacksonCodecTest.Zone> zones = new LinkedList<JacksonCodecTest.Zone>();
        zones.add(new JacksonCodecTest.Zone("DENOMINATOR.IO."));
        zones.add(new JacksonCodecTest.Zone("DENOMINATOR.IO.", "ABCD"));
        Response response = Response.builder().status(200).reason("OK").request(Request.create(GET, "/api", Collections.emptyMap(), null, UTF_8)).headers(Collections.emptyMap()).body(zonesJson, Util.UTF_8).build();
        Assert.assertEquals(zones, decoder.decode(response, new TypeReference<List<JacksonCodecTest.Zone>>() {}.getType()));
    }

    @Test
    public void customEncoder() {
        JacksonEncoder encoder = new JacksonEncoder(Arrays.asList(new SimpleModule().addSerializer(JacksonCodecTest.Zone.class, new JacksonCodecTest.ZoneSerializer())));
        List<JacksonCodecTest.Zone> zones = new LinkedList<JacksonCodecTest.Zone>();
        zones.add(new JacksonCodecTest.Zone("denominator.io."));
        zones.add(new JacksonCodecTest.Zone("denominator.io.", "abcd"));
        RequestTemplate template = new RequestTemplate();
        encoder.encode(zones, new TypeReference<List<JacksonCodecTest.Zone>>() {}.getType(), template);
        assertThat(template).hasBody((((((((((((""// 
         + "[ {") + (System.lineSeparator())) + "  \"name\" : \"DENOMINATOR.IO.\"") + (System.lineSeparator())) + "}, {") + (System.lineSeparator())) + "  \"name\" : \"DENOMINATOR.IO.\",") + (System.lineSeparator())) + "  \"id\" : \"ABCD\"") + (System.lineSeparator())) + "} ]"));
    }

    @Test
    public void decodesIterator() throws Exception {
        List<JacksonCodecTest.Zone> zones = new LinkedList<JacksonCodecTest.Zone>();
        zones.add(new JacksonCodecTest.Zone("denominator.io."));
        zones.add(new JacksonCodecTest.Zone("denominator.io.", "ABCD"));
        Response response = Response.builder().status(200).reason("OK").request(Request.create(GET, "/api", Collections.emptyMap(), null, UTF_8)).headers(Collections.emptyMap()).body(zonesJson, Util.UTF_8).build();
        Object decoded = JacksonIteratorDecoder.create().decode(response, new TypeReference<Iterator<JacksonCodecTest.Zone>>() {}.getType());
        Assert.assertTrue(Iterator.class.isAssignableFrom(decoded.getClass()));
        Assert.assertTrue(Closeable.class.isAssignableFrom(decoded.getClass()));
        Assert.assertEquals(zones, asList(((Iterator<?>) (decoded))));
    }

    @Test
    public void nullBodyDecodesToNullIterator() throws Exception {
        Response response = Response.builder().status(204).reason("OK").request(Request.create(GET, "/api", Collections.emptyMap(), null, UTF_8)).headers(Collections.emptyMap()).build();
        Assert.assertNull(JacksonIteratorDecoder.create().decode(response, Iterator.class));
    }

    @Test
    public void emptyBodyDecodesToNullIterator() throws Exception {
        Response response = Response.builder().status(204).reason("OK").request(Request.create(GET, "/api", Collections.emptyMap(), null, UTF_8)).headers(Collections.emptyMap()).body(new byte[0]).build();
        Assert.assertNull(JacksonIteratorDecoder.create().decode(response, Iterator.class));
    }

    static class Zone extends LinkedHashMap<String, Object> {
        private static final long serialVersionUID = 1L;

        Zone() {
            // for reflective instantiation.
        }

        Zone(String name) {
            this(name, null);
        }

        Zone(String name, String id) {
            put("name", name);
            if (id != null) {
                put("id", id);
            }
        }
    }

    static class ZoneDeserializer extends StdDeserializer<JacksonCodecTest.Zone> {
        public ZoneDeserializer() {
            super(JacksonCodecTest.Zone.class);
        }

        @Override
        public JacksonCodecTest.Zone deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            JacksonCodecTest.Zone zone = new JacksonCodecTest.Zone();
            jp.nextToken();
            while ((jp.nextToken()) != (JsonToken.END_OBJECT)) {
                String name = jp.getCurrentName();
                String value = jp.getValueAsString();
                if (value != null) {
                    zone.put(name, value.toUpperCase());
                }
            } 
            return zone;
        }
    }

    static class ZoneSerializer extends StdSerializer<JacksonCodecTest.Zone> {
        public ZoneSerializer() {
            super(JacksonCodecTest.Zone.class);
        }

        @Override
        public void serialize(JacksonCodecTest.Zone value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStartObject();
            for (Map.Entry<String, Object> entry : value.entrySet()) {
                jgen.writeFieldName(entry.getKey());
                jgen.writeString(entry.getValue().toString().toUpperCase());
            }
            jgen.writeEndObject();
        }
    }

    /**
     * Enabled via {@link feign.Feign.Builder#decode404()}
     */
    @Test
    public void notFoundDecodesToEmpty() throws Exception {
        Response response = Response.builder().status(404).reason("NOT FOUND").request(Request.create(GET, "/api", Collections.emptyMap(), null, UTF_8)).headers(Collections.emptyMap()).build();
        assertThat(((byte[]) (new JacksonDecoder().decode(response, byte[].class)))).isEmpty();
    }

    /**
     * Enabled via {@link feign.Feign.Builder#decode404()}
     */
    @Test
    public void notFoundDecodesToEmptyIterator() throws Exception {
        Response response = Response.builder().status(404).reason("NOT FOUND").request(Request.create(GET, "/api", Collections.emptyMap(), null, UTF_8)).headers(Collections.emptyMap()).build();
        assertThat(((byte[]) (JacksonIteratorDecoder.create().decode(response, byte[].class)))).isEmpty();
    }
}

