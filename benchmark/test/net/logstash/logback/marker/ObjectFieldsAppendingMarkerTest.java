/**
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
package net.logstash.logback.marker;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Test;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.ESCAPE_NON_ASCII;


public class ObjectFieldsAppendingMarkerTest {
    private static final JsonFactory FACTORY = new MappingJsonFactory().enable(ESCAPE_NON_ASCII);

    public static class MyClass {
        private String myField;

        public MyClass(String myField) {
            this.myField = myField;
        }

        public String getMyField() {
            return myField;
        }

        public void setMyField(String myField) {
            this.myField = myField;
        }
    }

    @Test
    public void testWriteTo() throws IOException {
        ObjectFieldsAppendingMarkerTest.MyClass myObject = new ObjectFieldsAppendingMarkerTest.MyClass("value");
        StringWriter writer = new StringWriter();
        JsonGenerator generator = ObjectFieldsAppendingMarkerTest.FACTORY.createGenerator(writer);
        LogstashMarker marker = Markers.appendFields(myObject);
        generator.writeStartObject();
        marker.writeTo(generator);
        generator.writeEndObject();
        generator.flush();
        assertThat(writer.toString()).isEqualTo("{\"myField\":\"value\"}");
    }

    @Test
    public void testWriteTo_nonUnwrappable() throws IOException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = ObjectFieldsAppendingMarkerTest.FACTORY.createGenerator(writer);
        LogstashMarker marker = Markers.appendFields(Long.valueOf(1L));
        generator.writeStartObject();
        marker.writeTo(generator);
        generator.writeEndObject();
        generator.flush();
        assertThat(writer.toString()).isEqualTo("{}");
    }

    @Test
    public void testEquals() {
        ObjectFieldsAppendingMarkerTest.MyClass myObject = new ObjectFieldsAppendingMarkerTest.MyClass("value");
        assertThat(Markers.appendFields(myObject)).isEqualTo(Markers.appendFields(myObject));
        assertThat(Markers.appendFields(myObject)).isNotEqualTo(Markers.appendFields(new ObjectFieldsAppendingMarkerTest.MyClass("value1")));
    }

    @Test
    public void testHashCode() {
        ObjectFieldsAppendingMarkerTest.MyClass myObject = new ObjectFieldsAppendingMarkerTest.MyClass("value");
        assertThat(Markers.appendFields(myObject).hashCode()).isEqualTo(Markers.appendFields(myObject).hashCode());
        assertThat(Markers.appendFields(myObject).hashCode()).isNotEqualTo(Markers.appendFields(new ObjectFieldsAppendingMarkerTest.MyClass("value1")).hashCode());
    }
}

