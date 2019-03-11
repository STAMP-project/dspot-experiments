/**
 * Copyright 2012-2018 Chronicle Map Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.openhft.chronicle.map.example;


import java.util.ArrayList;
import java.util.List;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesMarshallable;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.hash.serialization.BytesReader;
import net.openhft.chronicle.hash.serialization.BytesWriter;
import net.openhft.chronicle.hash.serialization.impl.EnumMarshallable;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;


public class PointListSerializationTest {
    @Test
    public void testComplexSerialization() {
        try (ChronicleMap<String, PointListSerializationTest.A> map = ChronicleMapBuilder.of(String.class, PointListSerializationTest.A.class).valueMarshaller(PointListSerializationTest.AMarshaller.INSTANCE).entries(5).averageKeySize(4).averageValueSize(1000).create()) {
            PointListSerializationTest.A obj_A = new PointListSerializationTest.A();
            obj_A.str_ = "a";
            obj_A.list_ = new ArrayList<>();
            PointListSerializationTest.B b = new PointListSerializationTest.B();
            b.str_ = "b";
            obj_A.list_.add(b);
            map.put("KEY1", obj_A);
            map.get("KEY1");
        }
    }

    static final class AMarshaller implements BytesReader<PointListSerializationTest.A> , BytesWriter<PointListSerializationTest.A> , EnumMarshallable<PointListSerializationTest.AMarshaller> {
        public static final PointListSerializationTest.AMarshaller INSTANCE = new PointListSerializationTest.AMarshaller();

        private AMarshaller() {
        }

        @Override
        public void write(Bytes out, @NotNull
        PointListSerializationTest.A toWrite) {
            out.writeUtf8(toWrite.str_);
            if ((toWrite.list_) != null) {
                int size = toWrite.list_.size();
                out.writeStopBit(size);
                for (int i = 0; i < size; i++) {
                    toWrite.list_.get(i).writeMarshallable(out);
                }
            } else {
                out.writeStopBit((-1));
            }
        }

        @NotNull
        @Override
        public PointListSerializationTest.A read(Bytes in, PointListSerializationTest.A using) {
            if (using == null)
                using = new PointListSerializationTest.A();

            using.str_ = in.readUtf8();
            int size = ((int) (in.readStopBit()));
            if (size >= 0) {
                if ((using.list_) == null) {
                    using.list_ = new ArrayList<>(size);
                } else {
                    using.list_.clear();
                    if ((using.list_) instanceof ArrayList)
                        ((ArrayList) (using.list_)).ensureCapacity(size);

                }
                for (int i = 0; i < size; i++) {
                    PointListSerializationTest.B b = new PointListSerializationTest.B();
                    b.readMarshallable(in);
                    using.list_.add(b);
                }
            } else {
                assert size == (-1);
                using.list_ = null;
            }
            return using;
        }

        @Override
        public PointListSerializationTest.AMarshaller readResolve() {
            return PointListSerializationTest.AMarshaller.INSTANCE;
        }
    }

    static class A {
        String str_;

        List<PointListSerializationTest.B> list_;
    }

    static class B implements BytesMarshallable {
        String str_;

        @Override
        public void readMarshallable(BytesIn in) {
            str_ = in.readUtf8();
        }

        @Override
        public void writeMarshallable(BytesOut out) {
            out.writeUtf8(str_);
        }
    }
}

