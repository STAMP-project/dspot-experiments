/**
 * Copyright 2015-2018 The OpenZipkin Authors
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
package zipkin2.internal;


import java.util.Arrays;
import org.junit.Test;
import zipkin2.internal.Proto3Fields.BooleanField;
import zipkin2.internal.Proto3Fields.BytesField;
import zipkin2.internal.Proto3Fields.Fixed64Field;
import zipkin2.internal.Proto3Fields.Utf8Field;
import zipkin2.internal.Proto3Fields.VarintField;


public class Proto3FieldsTest {
    Buffer buf = new Buffer(2048);// bigger than needed to test sizeOf


    /**
     * Shows we can reliably look at a byte zero to tell if we are decoding proto3 repeated fields.
     */
    @Test
    public void field_key_fieldOneLengthDelimited() {
        Proto3Fields.Field field = new Proto3Fields.Field(((1 << 3) | (Proto3Fields.WIRETYPE_LENGTH_DELIMITED)));
        // (field_number << 3) | wire_type = 1 << 3 | 2
        assertThat(field.key).isEqualTo(10).isEqualTo(10);// for sanity of those looking at debugger, 4th bit + 2nd bit = 10

        assertThat(field.fieldNumber).isEqualTo(1);
        assertThat(field.wireType).isEqualTo(Proto3Fields.WIRETYPE_LENGTH_DELIMITED);
    }

    @Test
    public void varint_sizeInBytes() {
        VarintField field = new VarintField(((1 << 3) | (Proto3Fields.WIRETYPE_VARINT)));
        assertThat(field.sizeInBytes(0)).isZero();
        // max size of varint32
        assertThat(field.sizeInBytes(-1)).isEqualTo(((0 + 1)/* tag of varint field */
         + 5));
        assertThat(field.sizeInBytes(0L)).isZero();
        // max size of varint64
        assertThat(field.sizeInBytes(-1L)).isEqualTo(((0 + 1)/* tag of varint field */
         + 10));
    }

    @Test
    public void boolean_sizeInBytes() {
        BooleanField field = new BooleanField(((1 << 3) | (Proto3Fields.WIRETYPE_VARINT)));
        assertThat(field.sizeInBytes(false)).isZero();
        // size of 1
        assertThat(field.sizeInBytes(true)).isEqualTo(((0 + 1)/* tag of varint field */
         + 1));
    }

    @Test
    public void utf8_sizeInBytes() {
        Utf8Field field = new Utf8Field(((1 << 3) | (Proto3Fields.WIRETYPE_LENGTH_DELIMITED)));
        // 12345678
        assertThat(field.sizeInBytes("12345678")).isEqualTo((((0 + 1)/* tag of string field */
         + 1)/* len */
         + 8));
    }

    @Test
    public void fixed64_sizeInBytes() {
        Fixed64Field field = new Fixed64Field(((1 << 3) | (Proto3Fields.WIRETYPE_FIXED64)));
        assertThat(field.sizeInBytes(Long.MIN_VALUE)).isEqualTo(9);
    }

    @Test
    public void fixed32_sizeInBytes() {
        Proto3Fields.Fixed32Field field = new Proto3Fields.Fixed32Field(((1 << 3) | (Proto3Fields.WIRETYPE_FIXED32)));
        assertThat(field.sizeInBytes(Integer.MIN_VALUE)).isEqualTo(5);
    }

    @Test
    public void supportedFields() {
        for (Proto3Fields.Field field : Arrays.asList(new VarintField(((128 << 3) | (Proto3Fields.WIRETYPE_VARINT))), new BooleanField(((128 << 3) | (Proto3Fields.WIRETYPE_VARINT))), new Proto3Fields.HexField(((128 << 3) | (Proto3Fields.WIRETYPE_LENGTH_DELIMITED))), new Utf8Field(((128 << 3) | (Proto3Fields.WIRETYPE_LENGTH_DELIMITED))), new BytesField(((128 << 3) | (Proto3Fields.WIRETYPE_LENGTH_DELIMITED))), new Proto3Fields.Fixed32Field(((128 << 3) | (Proto3Fields.WIRETYPE_FIXED32))), new Fixed64Field(((128 << 3) | (Proto3Fields.WIRETYPE_FIXED64))))) {
            assertThat(Proto3Fields.Field.fieldNumber(field.key, 1)).isEqualTo(field.fieldNumber);
            assertThat(Proto3Fields.Field.wireType(field.key, 1)).isEqualTo(field.wireType);
        }
    }

    @Test
    public void fieldNumber_malformed() {
        try {
            Proto3Fields.Field.fieldNumber(0, 2);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("Malformed: fieldNumber was zero at byte 2");
        }
    }

    @Test
    public void wireType_unsupported() {
        for (int unsupported : Arrays.asList(3, 4, 6)) {
            try {
                Proto3Fields.Field.wireType(((1 << 3) | unsupported), 2);
                failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
            } catch (IllegalArgumentException e) {
                assertThat(e).hasMessage((("Malformed: invalid wireType " + unsupported) + " at byte 2"));
            }
        }
    }

    @Test
    public void field_skipValue_VARINT() {
        VarintField field = new VarintField(((128 << 3) | (Proto3Fields.WIRETYPE_VARINT)));
        field.write(buf, -1L);
        buf.pos = 1;// skip the key

        skipValue(Proto3Fields.WIRETYPE_VARINT);
    }

    @Test
    public void field_skipValue_LENGTH_DELIMITED() {
        Utf8Field field = new Utf8Field(((128 << 3) | (Proto3Fields.WIRETYPE_LENGTH_DELIMITED)));
        field.write(buf, "??????");
        buf.pos = 1;// skip the key

        skipValue(Proto3Fields.WIRETYPE_LENGTH_DELIMITED);
    }

    @Test
    public void field_skipValue_FIXED64() {
        Fixed64Field field = new Fixed64Field(((128 << 3) | (Proto3Fields.WIRETYPE_FIXED64)));
        field.write(buf, -1L);
        buf.pos = 1;// skip the key

        skipValue(Proto3Fields.WIRETYPE_FIXED64);
    }

    @Test
    public void field_skipValue_FIXED32() {
        Proto3Fields.Fixed32Field field = new Proto3Fields.Fixed32Field(((128 << 3) | (Proto3Fields.WIRETYPE_FIXED32)));
        buf.writeByte(field.key);
        buf.writeByte(255).writeByte(255).writeByte(255).writeByte(255);
        buf.pos = 1;// skip the key

        skipValue(Proto3Fields.WIRETYPE_FIXED32);
    }

    @Test
    public void field_readLengthPrefix_LENGTH_DELIMITED() {
        BytesField field = new BytesField(((128 << 3) | (Proto3Fields.WIRETYPE_LENGTH_DELIMITED)));
        field.write(buf, new byte[10]);
        buf.pos = 1;// skip the key

        assertThat(field.readLengthPrefix(buf)).isEqualTo(10);
    }

    @Test
    public void field_readLengthPrefix_LENGTH_DELIMITED_truncated() {
        BytesField field = new BytesField(((128 << 3) | (Proto3Fields.WIRETYPE_LENGTH_DELIMITED)));
        buf = new Buffer(10);
        buf.writeVarint(100);// much larger than the buffer size

        buf.pos = 0;// reset

        try {
            field.readLengthPrefix(buf);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("Truncated: length 100 > bytes remaining 9");
        }
    }

    @Test
    public void field_read_FIXED64() {
        Fixed64Field field = new Fixed64Field(((128 << 3) | (Proto3Fields.WIRETYPE_FIXED64)));
        field.write(buf, -1L);
        buf.pos = 1;// skip the key

        assertThat(field.readValue(buf)).isEqualTo(-1L);
    }
}

