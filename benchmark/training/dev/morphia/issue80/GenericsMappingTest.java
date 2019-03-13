/**
 * Copyright (C) 2010 Olafur Gauti Gudmundsson
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package dev.morphia.issue80;


import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import dev.morphia.query.FindOptions;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Scott Hernandez
 */
public class GenericsMappingTest extends TestBase {
    @Test
    public void testBoundGenerics() {
        getMorphia().map(GenericsMappingTest.Element.class, GenericsMappingTest.AudioElement.class);
    }

    @Test
    public void testIt() {
        getMorphia().map(GenericsMappingTest.HoldsAnInteger.class, GenericsMappingTest.HoldsAString.class, GenericsMappingTest.ContainsThings.class);
        final GenericsMappingTest.ContainsThings ct = new GenericsMappingTest.ContainsThings();
        final GenericsMappingTest.HoldsAnInteger hai = new GenericsMappingTest.HoldsAnInteger();
        hai.setThing(7);
        final GenericsMappingTest.HoldsAString has = new GenericsMappingTest.HoldsAString();
        has.setThing("tr");
        ct.stringThing = has;
        ct.integerThing = hai;
        getDs().save(ct);
        Assert.assertNotNull(ct.id);
        Assert.assertEquals(1, getDs().getCount(GenericsMappingTest.ContainsThings.class));
        final GenericsMappingTest.ContainsThings ctLoaded = getDs().find(GenericsMappingTest.ContainsThings.class).find(new FindOptions().limit(1)).next();
        Assert.assertNotNull(ctLoaded);
        Assert.assertNotNull(ctLoaded.id);
        Assert.assertNotNull(ctLoaded.stringThing);
        Assert.assertNotNull(ctLoaded.integerThing);
    }

    public static class GenericHolder<T> {
        @Property
        private T thing;

        public T getThing() {
            return thing;
        }

        public void setThing(final T thing) {
            this.thing = thing;
        }
    }

    @Embedded
    static class HoldsAString extends GenericsMappingTest.GenericHolder<String> {}

    @Embedded
    static class HoldsAnInteger extends GenericsMappingTest.GenericHolder<Integer> {}

    @Entity
    static class ContainsThings {
        @Id
        private String id;

        private GenericsMappingTest.HoldsAString stringThing;

        private GenericsMappingTest.HoldsAnInteger integerThing;
    }

    public abstract static class Element<T extends Number> {
        @Id
        private ObjectId id;

        private T[] resources;
    }

    public static class AudioElement extends GenericsMappingTest.Element<Long> {}
}

