/**
 * Copyright 2017 MovingBlocks
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
package org.terasology.persistence.typeHandling.gson;


import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Test;


public class PolymorphicTypeAdapterFactoryTest {
    private static final PolymorphicTypeAdapterFactoryTest.Dog DOG = new PolymorphicTypeAdapterFactoryTest.Dog(1.25F);

    private static final PolymorphicTypeAdapterFactoryTest.Animal CAT = new PolymorphicTypeAdapterFactoryTest.Animal("Cat");

    private static final PolymorphicTypeAdapterFactoryTest.Cheetah CHEETAH = new PolymorphicTypeAdapterFactoryTest.Cheetah(21);

    private final Gson baseClassGson = new GsonBuilder().registerTypeAdapterFactory(PolymorphicTypeAdapterFactory.of(PolymorphicTypeAdapterFactoryTest.Animal.class)).create();

    private final Gson interfaceGson = new GsonBuilder().registerTypeAdapterFactory(PolymorphicTypeAdapterFactory.of(PolymorphicTypeAdapterFactoryTest.Walker.class)).create();

    @Test
    public void testInterfaceReference() {
        PolymorphicTypeAdapterFactoryTest.Walker walker = PolymorphicTypeAdapterFactoryTest.CAT;
        String json = interfaceGson.toJson(walker);
        PolymorphicTypeAdapterFactoryTest.Walker newAnimal = interfaceGson.fromJson(json, PolymorphicTypeAdapterFactoryTest.Walker.class);
        Assert.assertTrue((newAnimal instanceof PolymorphicTypeAdapterFactoryTest.Animal));
    }

    @Test
    public void testBaseClassReference() {
        PolymorphicTypeAdapterFactoryTest.Animal animal = PolymorphicTypeAdapterFactoryTest.CHEETAH;
        String json = baseClassGson.toJson(animal);
        PolymorphicTypeAdapterFactoryTest.Animal newAnimal = baseClassGson.fromJson(json, PolymorphicTypeAdapterFactoryTest.Animal.class);
        Assert.assertTrue((newAnimal instanceof PolymorphicTypeAdapterFactoryTest.Cheetah));
    }

    @Test
    public void testInnerField() {
        PolymorphicTypeAdapterFactoryTest.Capsule capsule = new PolymorphicTypeAdapterFactoryTest.Capsule(PolymorphicTypeAdapterFactoryTest.DOG);
        String json = baseClassGson.toJson(capsule);
        PolymorphicTypeAdapterFactoryTest.Capsule newCapsule = baseClassGson.fromJson(json, PolymorphicTypeAdapterFactoryTest.Capsule.class);
        Assert.assertTrue(((newCapsule.animal) instanceof PolymorphicTypeAdapterFactoryTest.Dog));
    }

    @Test
    public void testBaseClassList() {
        List<PolymorphicTypeAdapterFactoryTest.Animal> animals = Lists.newArrayList(PolymorphicTypeAdapterFactoryTest.CAT, PolymorphicTypeAdapterFactoryTest.DOG, PolymorphicTypeAdapterFactoryTest.CHEETAH);
        String json = baseClassGson.toJson(animals);
        List<PolymorphicTypeAdapterFactoryTest.Animal> newAnimals = baseClassGson.fromJson(json, new TypeToken<List<PolymorphicTypeAdapterFactoryTest.Animal>>() {}.getType());
        Assert.assertTrue(((newAnimals.get(1)) instanceof PolymorphicTypeAdapterFactoryTest.Dog));
        Assert.assertTrue(((newAnimals.get(2)) instanceof PolymorphicTypeAdapterFactoryTest.Cheetah));
    }

    @Test
    public void testInterfaceList() {
        List<PolymorphicTypeAdapterFactoryTest.Walker> walkers = Lists.newArrayList(PolymorphicTypeAdapterFactoryTest.CAT, PolymorphicTypeAdapterFactoryTest.DOG, PolymorphicTypeAdapterFactoryTest.CHEETAH);
        String json = interfaceGson.toJson(walkers);
        List<PolymorphicTypeAdapterFactoryTest.Walker> newWalkers = interfaceGson.fromJson(json, new TypeToken<List<PolymorphicTypeAdapterFactoryTest.Walker>>() {}.getType());
        Assert.assertTrue(((newWalkers.get(0)) instanceof PolymorphicTypeAdapterFactoryTest.Animal));
        Assert.assertTrue(((newWalkers.get(1)) instanceof PolymorphicTypeAdapterFactoryTest.Dog));
        Assert.assertTrue(((newWalkers.get(2)) instanceof PolymorphicTypeAdapterFactoryTest.Cheetah));
    }

    private static class Capsule {
        private final PolymorphicTypeAdapterFactoryTest.Animal animal;

        private Capsule(PolymorphicTypeAdapterFactoryTest.Animal animal) {
            this.animal = animal;
        }
    }

    private interface Walker {}

    private static class Animal implements PolymorphicTypeAdapterFactoryTest.Walker {
        private final String name;

        private Animal(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            PolymorphicTypeAdapterFactoryTest.Animal animal = ((PolymorphicTypeAdapterFactoryTest.Animal) (o));
            return Objects.equals(name, animal.name);
        }
    }

    private static class Dog extends PolymorphicTypeAdapterFactoryTest.Animal {
        private final float tailLength;

        private Dog(float tailLength) {
            super("Dog");
            this.tailLength = tailLength;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            if (!(super.equals(o)))
                return false;

            PolymorphicTypeAdapterFactoryTest.Dog dog = ((PolymorphicTypeAdapterFactoryTest.Dog) (o));
            return (Float.compare(dog.tailLength, tailLength)) == 0;
        }
    }

    private static class Cheetah extends PolymorphicTypeAdapterFactoryTest.Animal {
        private final int spotCount;

        private Cheetah(int spotCount) {
            super("Cheetah");
            this.spotCount = spotCount;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            if (!(super.equals(o)))
                return false;

            PolymorphicTypeAdapterFactoryTest.Cheetah cheetah = ((PolymorphicTypeAdapterFactoryTest.Cheetah) (o));
            return (spotCount) == (cheetah.spotCount);
        }
    }
}

