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
package net.openhft.chronicle.map;


import net.openhft.chronicle.wire.AbstractMarshallable;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class AbstractMarshallableKeyValueTest {
    @Test
    public void shouldAcceptAbstractMarshallableComponents() throws Exception {
        final ChronicleMap<AbstractMarshallableKeyValueTest.Key, AbstractMarshallableKeyValueTest.Value> map = ChronicleMapBuilder.of(AbstractMarshallableKeyValueTest.Key.class, AbstractMarshallableKeyValueTest.Value.class).entries(10).averageKey(new AbstractMarshallableKeyValueTest.Key()).averageValue(new AbstractMarshallableKeyValueTest.Value()).create();
        map.put(new AbstractMarshallableKeyValueTest.Key(), new AbstractMarshallableKeyValueTest.Value());
        Assert.assertThat(map.get(new AbstractMarshallableKeyValueTest.Key()).number, CoreMatchers.is(new AbstractMarshallableKeyValueTest.Value().number));
    }

    private static final class Key extends AbstractMarshallable {
        private String k = "key";
    }

    private static final class Value extends AbstractMarshallable {
        private Integer number = Integer.valueOf(17);
    }
}

