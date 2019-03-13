/**
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.wire.schema;


import ProtoType.BYTES;
import ProtoType.INT32;
import ProtoType.STRING;
import org.junit.Assert;
import org.junit.Test;


public final class ProtoTypeTest {
    @Test
    public void get() throws Exception {
        assertThat(ProtoType.get("int32")).isSameAs(INT32);
        assertThat(ProtoType.get("Person")).isEqualTo(ProtoType.get("Person"));
        assertThat(ProtoType.get("squareup.protos.person", "Person")).isEqualTo(ProtoType.get("squareup.protos.person.Person"));
    }

    @Test
    public void simpleName() throws Exception {
        ProtoType person = ProtoType.get("squareup.protos.person.Person");
        assertThat(person.simpleName()).isEqualTo("Person");
    }

    @Test
    public void scalarToString() throws Exception {
        assertThat(INT32.toString()).isEqualTo("int32");
        assertThat(STRING.toString()).isEqualTo("string");
        assertThat(BYTES.toString()).isEqualTo("bytes");
    }

    @Test
    public void nestedType() throws Exception {
        assertThat(ProtoType.get("squareup.protos.person.Person").nestedType("PhoneType")).isEqualTo(ProtoType.get("squareup.protos.person.Person.PhoneType"));
    }

    @Test
    public void primitivesCannotNest() throws Exception {
        try {
            INT32.nestedType("PhoneType");
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test
    public void mapsCannotNest() throws Exception {
        try {
            ProtoType.get("map<string, string>").nestedType("PhoneType");
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
    }

    @Test
    public void mapFormat() throws Exception {
        try {
            ProtoType.get("map<string>");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("expected ',' in map type: map<string>");
        }
    }

    @Test
    public void mapKeyScalarType() throws Exception {
        try {
            ProtoType.get("map<bytes, string>");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            ProtoType.get("map<double, string>");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            ProtoType.get("map<float, string>");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            ProtoType.get("map<some.Message, string>");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void messageToString() throws Exception {
        ProtoType person = ProtoType.get("squareup.protos.person.Person");
        assertThat(person.toString()).isEqualTo("squareup.protos.person.Person");
        ProtoType phoneType = person.nestedType("PhoneType");
        assertThat(phoneType.toString()).isEqualTo("squareup.protos.person.Person.PhoneType");
    }

    @Test
    public void enclosingTypeOrPackage() throws Exception {
        assertThat(STRING.enclosingTypeOrPackage()).isNull();
        ProtoType person = ProtoType.get("squareup.protos.person.Person");
        assertThat(person.enclosingTypeOrPackage()).isEqualTo("squareup.protos.person");
        ProtoType phoneType = person.nestedType("PhoneType");
        assertThat(phoneType.enclosingTypeOrPackage()).isEqualTo("squareup.protos.person.Person");
    }

    @Test
    public void isScalar() throws Exception {
        assertThat(INT32.isScalar()).isTrue();
        assertThat(STRING.isScalar()).isTrue();
        assertThat(BYTES.isScalar()).isTrue();
        assertThat(ProtoType.get("squareup.protos.person.Person").isScalar()).isFalse();
    }
}

