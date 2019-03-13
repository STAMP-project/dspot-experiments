/**
 * Copyright ? 2010-2017 Nokia
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
package org.jsonschema2pojo.integration;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.jsonschema2pojo.AbstractAnnotator;
import org.jsonschema2pojo.integration.util.Jsonschema2PojoRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


public class MediaIT {
    @ClassRule
    public static Jsonschema2PojoRule classSchemaRule = new Jsonschema2PojoRule();

    private static Class<?> classWithMediaProperties;

    private static Class<byte[]> BYTE_ARRAY = byte[].class;

    @Test
    public void shouldCreateByteArrayField() throws NoSuchFieldException, SecurityException {
        Field field = MediaIT.classWithMediaProperties.getDeclaredField("minimalBinary");
        Assert.assertThat("the minimal binary field has type byte[]", field.getType(), MediaIT.equalToType(MediaIT.BYTE_ARRAY));
    }

    @Test
    public void shouldCreateByteArrayGetter() throws NoSuchMethodException, SecurityException {
        Method getter = MediaIT.classWithMediaProperties.getDeclaredMethod("getMinimalBinary");
        Assert.assertThat("the minimal binary getter has return type byte[]", getter.getReturnType(), MediaIT.equalToType(MediaIT.BYTE_ARRAY));
    }

    @Test
    public void shouldCreateByteArraySetter() throws NoSuchMethodException, SecurityException {
        Method setter = MediaIT.classWithMediaProperties.getDeclaredMethod("setMinimalBinary", MediaIT.BYTE_ARRAY);
        Assert.assertThat("the minimal binary setter has return type void", setter.getReturnType(), MediaIT.equalToType(Void.TYPE));
    }

    @Test
    public void shouldCreateByteArrayFieldWithAnyEncoding() throws NoSuchFieldException, SecurityException {
        Field field = MediaIT.classWithMediaProperties.getDeclaredField("anyBinaryEncoding");
        JsonSerialize serAnnotation = field.getAnnotation(JsonSerialize.class);
        JsonDeserialize deserAnnotation = field.getAnnotation(JsonDeserialize.class);
        Assert.assertThat("any binary encoding field has type byte[]", field.getType(), MediaIT.equalToType(MediaIT.BYTE_ARRAY));
        Assert.assertThat("any binary encoding has a serializer", serAnnotation, notNullValue());
        Assert.assertThat("any binary encoding has a deserializer", deserAnnotation, notNullValue());
    }

    @Test
    public void shouldCreateByteArrayGetterWithAnyEncoding() throws NoSuchMethodException, SecurityException {
        Method getter = MediaIT.classWithMediaProperties.getDeclaredMethod("getAnyBinaryEncoding");
        Assert.assertThat("any binary encoding getter has return type byte[]", getter.getReturnType(), MediaIT.equalToType(MediaIT.BYTE_ARRAY));
    }

    @Test
    public void shouldCreateByteArraySetterWithAnyEncoding() throws NoSuchMethodException, SecurityException {
        Method setter = MediaIT.classWithMediaProperties.getDeclaredMethod("setAnyBinaryEncoding", MediaIT.BYTE_ARRAY);
        Assert.assertThat("any binary encoding setter has return type void", setter.getReturnType(), MediaIT.equalToType(Void.TYPE));
    }

    @Test
    public void shouldCreateStringFieldWithoutEncoding() throws NoSuchFieldException, SecurityException {
        Field field = MediaIT.classWithMediaProperties.getDeclaredField("unencoded");
        Assert.assertThat("unencoded field has type String", field.getType(), MediaIT.equalToType(String.class));
    }

    @Test
    public void shouldCreateStringGetterWithoutEncoding() throws NoSuchMethodException, SecurityException {
        Method getter = MediaIT.classWithMediaProperties.getDeclaredMethod("getUnencoded");
        Assert.assertThat("unencoded getter has return type String", getter.getReturnType(), MediaIT.equalToType(String.class));
    }

    @Test
    public void shouldCreateStringSetterWithoutEncoding() throws NoSuchMethodException, SecurityException {
        Method setter = MediaIT.classWithMediaProperties.getDeclaredMethod("setUnencoded", String.class);
        Assert.assertThat("unencoded setter has return type void", setter.getReturnType(), MediaIT.equalToType(Void.TYPE));
    }

    @Test
    public void shouldCreateUriFieldWithUriFormat() throws NoSuchFieldException, SecurityException {
        Field field = MediaIT.classWithMediaProperties.getDeclaredField("withUriFormat");
        Assert.assertThat("withUriFormat field has type URI", field.getType(), MediaIT.equalToType(URI.class));
    }

    @Test
    public void shouldHandleUnencodedDefault() throws Exception {
        Method getter = MediaIT.classWithMediaProperties.getDeclaredMethod("getUnencodedWithDefault");
        Object object = new ObjectMapper().readValue("{}", MediaIT.classWithMediaProperties);
        String value = ((String) (getter.invoke(object)));
        Assert.assertThat("unencodedWithDefault has the default value", value, equalTo("default value"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReasonablyHandleBase64Default() throws Exception {
        Method getter = MediaIT.classWithMediaProperties.getDeclaredMethod("getBase64WithDefault");
        Object object = new ObjectMapper().readValue("{}", MediaIT.classWithMediaProperties);
        byte[] value = ((byte[]) (getter.invoke(object)));
        // if we got here, then at least defaults do not blow up the code.  Make sure
        // we get null or the default.  Users should not depend on the functionality in
        // this situation, as it is unsupported.
        Assert.assertThat("base64WithDefault is null or the default value", value, anyOf(nullValue(), equalTo(new byte[]{ ((byte) (255)), ((byte) (240)), ((byte) (15)), ((byte) (0)) })));
    }

    @Test
    public void shouldRoundTripBase64Field() throws Exception {
        MediaIT.roundTripAssertions(new ObjectMapper(), "minimalBinary", "//APAA==", new byte[]{ ((byte) (255)), ((byte) (240)), ((byte) (15)), ((byte) (0)) });
    }

    @Test
    public void shouldRoundTripUnencodedField() throws Exception {
        MediaIT.roundTripAssertions(new ObjectMapper(), "unencoded", "some text", "some text");
    }

    @Test
    public void shouldRoundTripQuotedPrintableField() throws Exception {
        MediaIT.roundTripAssertions(new ObjectMapper(), "anyBinaryEncoding", "\"=E3=82=A8=E3=83=B3=E3=82=B3=E3=83=BC=E3=83=89=E3=81=95=E3=82=8C=E3=81=9F=E6=96=87=E5=AD=97=E5=88=97\" is Japanese for \"encoded string\"", "\"\u30a8\u30f3\u30b3\u30fc\u30c9\u3055\u308c\u305f\u6587\u5b57\u5217\" is Japanese for \"encoded string\"".getBytes("UTF-8"));
    }

    @Test
    public void shouldRoundTripQuotedPrintableFieldWithNoFieldVisibility() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getVisibilityChecker().withFieldVisibility(NONE));
        MediaIT.roundTripAssertions(new ObjectMapper(), "anyBinaryEncoding", "\"=E3=82=A8=E3=83=B3=E3=82=B3=E3=83=BC=E3=83=89=E3=81=95=E3=82=8C=E3=81=9F=E6=96=87=E5=AD=97=E5=88=97\" is Japanese for \"encoded string\"", "\"\u30a8\u30f3\u30b3\u30fc\u30c9\u3055\u308c\u305f\u6587\u5b57\u5217\" is Japanese for \"encoded string\"".getBytes("UTF-8"));
    }

    /**
     * An example annotator that supports the quoted printable encoding, from RFC 2045.
     *
     * @author Christian Trimble
     * @see <a href="http://tools.ietf.org/html/rfc2045#section-6.7">Quoted-Printable Content-Transfer-Encoding, Multipurpose Internet Mail Extensions (MIME) Part One: Format of Internet Message Bodies</a>
     */
    public static class QuotedPrintableAnnotator extends AbstractAnnotator {
        public static final String TYPE = "type";

        public static final String STRING = "string";

        public static final String MEDIA = "media";

        public static final String BINARY_ENCODING = "binaryEncoding";

        public static final String QUOTED_PRINTABLE = "quoted-printable";

        public static final String USING = "using";

        public static final String INCLUDE = "include";

        @Override
        public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName, JsonNode propertyNode) {
            if (MediaIT.QuotedPrintableAnnotator.isQuotedPrintableProperty(propertyNode)) {
                field.annotate(JsonSerialize.class).param(MediaIT.QuotedPrintableAnnotator.USING, MediaIT.QuotedPrintableSerializer.class);
                field.annotate(JsonInclude.class).param("value", NON_NULL);
                field.annotate(JsonDeserialize.class).param(MediaIT.QuotedPrintableAnnotator.USING, MediaIT.QuotedPrintableDeserializer.class);
            }
        }

        private static boolean isQuotedPrintableProperty(JsonNode propertyNode) {
            return (((propertyNode.has(MediaIT.QuotedPrintableAnnotator.TYPE)) && (MediaIT.QuotedPrintableAnnotator.STRING.equals(propertyNode.get(MediaIT.QuotedPrintableAnnotator.TYPE).asText()))) && (propertyNode.has(MediaIT.QuotedPrintableAnnotator.MEDIA))) && (MediaIT.QuotedPrintableAnnotator.isQuotedPrintable(propertyNode.get(MediaIT.QuotedPrintableAnnotator.MEDIA)));
        }

        private static boolean isQuotedPrintable(JsonNode mediaNode) {
            return (mediaNode.has(MediaIT.QuotedPrintableAnnotator.BINARY_ENCODING)) && (MediaIT.QuotedPrintableAnnotator.QUOTED_PRINTABLE.equalsIgnoreCase(mediaNode.get(MediaIT.QuotedPrintableAnnotator.BINARY_ENCODING).asText()));
        }
    }

    public static class QuotedPrintableSerializer extends StdSerializer<byte[]> {
        private static QuotedPrintableCodec codec = new QuotedPrintableCodec();

        public QuotedPrintableSerializer() {
            super(byte[].class);
        }

        @Override
        public void serialize(byte[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(new String(MediaIT.QuotedPrintableSerializer.codec.encode(value), "UTF-8"));
        }
    }

    @SuppressWarnings("serial")
    public static class QuotedPrintableDeserializer extends StdDeserializer<byte[]> {
        private static QuotedPrintableCodec codec = new QuotedPrintableCodec();

        public QuotedPrintableDeserializer() {
            super(byte[].class);
        }

        @Override
        public byte[] deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            try {
                return MediaIT.QuotedPrintableDeserializer.codec.decode(jp.getText().getBytes("UTF-8"));
            } catch (DecoderException e) {
                throw new IOException(String.format("could not decode quoted string in %s", jp.getCurrentName()), e);
            }
        }
    }
}

