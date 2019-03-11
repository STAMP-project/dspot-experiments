/**
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
package org.jdbi.v3.core.collector;


import java.lang.reflect.Type;
import java.util.stream.Collector;
import java.util.stream.Stream;
import org.jdbi.v3.core.generic.GenericType;
import org.jdbi.v3.core.generic.GenericTypes;
import org.junit.Test;


public class EnumSetCollectorFactoryTest {
    private EnumSetCollectorFactory factory = new EnumSetCollectorFactory();

    @Test
    public void enumSet() {
        GenericType genericType = new GenericType<java.util.EnumSet<EnumSetCollectorFactoryTest.Color>>() {};
        Type containerType = genericType.getType();
        Class<?> erasedType = GenericTypes.getErasedType(containerType);
        assertThat(factory.accepts(containerType)).isTrue();
        assertThat(factory.accepts(erasedType)).isFalse();
        assertThat(factory.elementType(containerType)).contains(EnumSetCollectorFactoryTest.Color.class);
        Collector<EnumSetCollectorFactoryTest.Color, ?, java.util.EnumSet<EnumSetCollectorFactoryTest.Color>> collector = ((Collector<EnumSetCollectorFactoryTest.Color, ?, java.util.EnumSet<EnumSetCollectorFactoryTest.Color>>) (factory.build(containerType)));
        assertThat(Stream.of(EnumSetCollectorFactoryTest.Color.RED, EnumSetCollectorFactoryTest.Color.BLUE).collect(collector)).isInstanceOf(erasedType).containsExactly(EnumSetCollectorFactoryTest.Color.RED, EnumSetCollectorFactoryTest.Color.BLUE);
    }

    /**
     * For EnumSet test only.
     */
    private enum Color {

        RED,
        GREEN,
        BLUE;}
}

