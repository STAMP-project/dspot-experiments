/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.guice;


import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class ConditionalMultibindTest {
    private static final String ANIMAL_TYPE = "animal.type";

    private Properties props;

    @Test
    public void testMultiConditionalBind_cat() {
        props.setProperty("animal.type", "cat");
        Injector injector = Guice.createInjector(new Module() {
            @Override
            public void configure(Binder binder) {
                ConditionalMultibind.create(props, binder, ConditionalMultibindTest.Animal.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("cat"), ConditionalMultibindTest.Cat.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("dog"), ConditionalMultibindTest.Dog.class);
            }
        });
        Set<ConditionalMultibindTest.Animal> animalSet = injector.getInstance(Key.get(new com.google.inject.TypeLiteral<Set<ConditionalMultibindTest.Animal>>() {}));
        Assert.assertEquals(1, animalSet.size());
        Assert.assertEquals(animalSet, ImmutableSet.<ConditionalMultibindTest.Animal>of(new ConditionalMultibindTest.Cat()));
    }

    @Test
    public void testMultiConditionalBind_cat_dog() {
        props.setProperty("animal.type", "pets");
        Injector injector = Guice.createInjector(new Module() {
            @Override
            public void configure(Binder binder) {
                ConditionalMultibind.create(props, binder, ConditionalMultibindTest.Animal.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), ConditionalMultibindTest.Cat.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), ConditionalMultibindTest.Dog.class);
            }
        });
        Set<ConditionalMultibindTest.Animal> animalSet = injector.getInstance(Key.get(new com.google.inject.TypeLiteral<Set<ConditionalMultibindTest.Animal>>() {}));
        Assert.assertEquals(2, animalSet.size());
        Assert.assertEquals(animalSet, ImmutableSet.of(new ConditionalMultibindTest.Cat(), new ConditionalMultibindTest.Dog()));
    }

    @Test
    public void testMultiConditionalBind_cat_dog_non_continuous_syntax() {
        props.setProperty("animal.type", "pets");
        Injector injector = Guice.createInjector(new Module() {
            @Override
            public void configure(Binder binder) {
                ConditionalMultibind.create(props, binder, ConditionalMultibindTest.Animal.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), ConditionalMultibindTest.Cat.class);
                ConditionalMultibind.create(props, binder, ConditionalMultibindTest.Animal.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), ConditionalMultibindTest.Dog.class);
            }
        });
        Set<ConditionalMultibindTest.Animal> animalSet = injector.getInstance(Key.get(new com.google.inject.TypeLiteral<Set<ConditionalMultibindTest.Animal>>() {}));
        Assert.assertEquals(2, animalSet.size());
        Assert.assertEquals(animalSet, ImmutableSet.of(new ConditionalMultibindTest.Cat(), new ConditionalMultibindTest.Dog()));
    }

    @Test
    public void testMultiConditionalBind_multiple_modules() {
        props.setProperty("animal.type", "pets");
        Injector injector = Guice.createInjector(new Module() {
            @Override
            public void configure(Binder binder) {
                ConditionalMultibind.create(props, binder, ConditionalMultibindTest.Animal.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), ConditionalMultibindTest.Cat.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), ConditionalMultibindTest.Dog.class);
            }
        }, new Module() {
            @Override
            public void configure(Binder binder) {
                ConditionalMultibind.create(props, binder, ConditionalMultibindTest.Animal.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("not_match"), ConditionalMultibindTest.Tiger.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), ConditionalMultibindTest.Fish.class);
            }
        });
        Set<ConditionalMultibindTest.Animal> animalSet = injector.getInstance(Key.get(new com.google.inject.TypeLiteral<Set<ConditionalMultibindTest.Animal>>() {}));
        Assert.assertEquals(3, animalSet.size());
        Assert.assertEquals(animalSet, ImmutableSet.of(new ConditionalMultibindTest.Cat(), new ConditionalMultibindTest.Dog(), new ConditionalMultibindTest.Fish()));
    }

    @Test
    public void testMultiConditionalBind_multiple_modules_with_annotation() {
        props.setProperty("animal.type", "pets");
        Injector injector = Guice.createInjector(new Module() {
            @Override
            public void configure(Binder binder) {
                ConditionalMultibind.create(props, binder, ConditionalMultibindTest.Animal.class, ConditionalMultibindTest.SanDiego.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), ConditionalMultibindTest.Cat.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), ConditionalMultibindTest.Dog.class);
            }
        }, new Module() {
            @Override
            public void configure(Binder binder) {
                ConditionalMultibind.create(props, binder, ConditionalMultibindTest.Animal.class, ConditionalMultibindTest.SanDiego.class).addBinding(new ConditionalMultibindTest.Bird()).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), ConditionalMultibindTest.Tiger.class);
                ConditionalMultibind.create(props, binder, ConditionalMultibindTest.Animal.class, ConditionalMultibindTest.SanJose.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), ConditionalMultibindTest.Fish.class);
            }
        });
        Set<ConditionalMultibindTest.Animal> animalSet_1 = injector.getInstance(Key.get(new com.google.inject.TypeLiteral<Set<ConditionalMultibindTest.Animal>>() {}, ConditionalMultibindTest.SanDiego.class));
        Assert.assertEquals(4, animalSet_1.size());
        Assert.assertEquals(animalSet_1, ImmutableSet.of(new ConditionalMultibindTest.Bird(), new ConditionalMultibindTest.Cat(), new ConditionalMultibindTest.Dog(), new ConditionalMultibindTest.Tiger()));
        Set<ConditionalMultibindTest.Animal> animalSet_2 = injector.getInstance(Key.get(new com.google.inject.TypeLiteral<Set<ConditionalMultibindTest.Animal>>() {}, ConditionalMultibindTest.SanJose.class));
        Assert.assertEquals(1, animalSet_2.size());
        Assert.assertEquals(animalSet_2, ImmutableSet.<ConditionalMultibindTest.Animal>of(new ConditionalMultibindTest.Fish()));
    }

    @Test
    public void testMultiConditionalBind_inject() {
        props.setProperty("animal.type", "pets");
        Injector injector = Guice.createInjector(new Module() {
            @Override
            public void configure(Binder binder) {
                ConditionalMultibind.create(props, binder, ConditionalMultibindTest.Animal.class).addBinding(ConditionalMultibindTest.Bird.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), ConditionalMultibindTest.Cat.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), ConditionalMultibindTest.Dog.class);
            }
        }, new Module() {
            @Override
            public void configure(Binder binder) {
                ConditionalMultibind.create(props, binder, ConditionalMultibindTest.Animal.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("not_match"), ConditionalMultibindTest.Tiger.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), ConditionalMultibindTest.Fish.class);
            }
        });
        ConditionalMultibindTest.PetShotAvails shop = new ConditionalMultibindTest.PetShotAvails();
        injector.injectMembers(shop);
        Assert.assertEquals(4, shop.animals.size());
        Assert.assertEquals(shop.animals, ImmutableSet.of(new ConditionalMultibindTest.Bird(), new ConditionalMultibindTest.Cat(), new ConditionalMultibindTest.Dog(), new ConditionalMultibindTest.Fish()));
    }

    @Test
    public void testMultiConditionalBind_typeLiteral() {
        props.setProperty("animal.type", "pets");
        final Set<ConditionalMultibindTest.Animal> set1 = ImmutableSet.of(new ConditionalMultibindTest.Dog(), new ConditionalMultibindTest.Tiger());
        final Set<ConditionalMultibindTest.Animal> set2 = ImmutableSet.of(new ConditionalMultibindTest.Cat(), new ConditionalMultibindTest.Fish());
        final Set<ConditionalMultibindTest.Animal> set3 = ImmutableSet.of(new ConditionalMultibindTest.Cat());
        final Set<ConditionalMultibindTest.Animal> union = new HashSet<>();
        union.addAll(set1);
        union.addAll(set2);
        final ConditionalMultibindTest.Zoo<ConditionalMultibindTest.Animal> zoo1 = new ConditionalMultibindTest.Zoo<>(set1);
        final ConditionalMultibindTest.Zoo<ConditionalMultibindTest.Animal> zoo2 = new ConditionalMultibindTest.Zoo<>();
        Injector injector = Guice.createInjector(new Module() {
            @Override
            public void configure(Binder binder) {
                ConditionalMultibind.create(props, binder, new com.google.inject.TypeLiteral<Set<ConditionalMultibindTest.Animal>>() {}).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), set1).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), set2);
                ConditionalMultibind.create(props, binder, new com.google.inject.TypeLiteral<ConditionalMultibindTest.Zoo<ConditionalMultibindTest.Animal>>() {}).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), zoo1);
            }
        }, new Module() {
            @Override
            public void configure(Binder binder) {
                ConditionalMultibind.create(props, binder, new com.google.inject.TypeLiteral<Set<ConditionalMultibindTest.Animal>>() {}).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), set3);
                ConditionalMultibind.create(props, binder, new com.google.inject.TypeLiteral<Set<ConditionalMultibindTest.Animal>>() {}, ConditionalMultibindTest.SanDiego.class).addConditionBinding(ConditionalMultibindTest.ANIMAL_TYPE, Predicates.equalTo("pets"), union);
                ConditionalMultibind.create(props, binder, new com.google.inject.TypeLiteral<ConditionalMultibindTest.Zoo<ConditionalMultibindTest.Animal>>() {}).addBinding(new com.google.inject.TypeLiteral<ConditionalMultibindTest.Zoo<ConditionalMultibindTest.Animal>>() {});
            }
        });
        Set<Set<ConditionalMultibindTest.Animal>> actualAnimalSet = injector.getInstance(Key.get(new com.google.inject.TypeLiteral<Set<Set<ConditionalMultibindTest.Animal>>>() {}));
        Assert.assertEquals(3, actualAnimalSet.size());
        Assert.assertEquals(ImmutableSet.of(set1, set2, set3), actualAnimalSet);
        actualAnimalSet = injector.getInstance(Key.get(new com.google.inject.TypeLiteral<Set<Set<ConditionalMultibindTest.Animal>>>() {}, ConditionalMultibindTest.SanDiego.class));
        Assert.assertEquals(1, actualAnimalSet.size());
        Assert.assertEquals(ImmutableSet.of(union), actualAnimalSet);
        final Set<ConditionalMultibindTest.Zoo<ConditionalMultibindTest.Animal>> actualZooSet = injector.getInstance(Key.get(new com.google.inject.TypeLiteral<Set<ConditionalMultibindTest.Zoo<ConditionalMultibindTest.Animal>>>() {}));
        Assert.assertEquals(2, actualZooSet.size());
        Assert.assertEquals(ImmutableSet.of(zoo1, zoo2), actualZooSet);
    }

    abstract static class Animal {
        private final String type;

        Animal(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return ((("Animal{" + "type='") + (type)) + '\'') + '}';
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ConditionalMultibindTest.Animal animal = ((ConditionalMultibindTest.Animal) (o));
            return (type) != null ? type.equals(animal.type) : (animal.type) == null;
        }

        @Override
        public int hashCode() {
            return (type) != null ? type.hashCode() : 0;
        }
    }

    static class PetShotAvails {
        @Inject
        Set<ConditionalMultibindTest.Animal> animals;
    }

    static class Dog extends ConditionalMultibindTest.Animal {
        Dog() {
            super("dog");
        }
    }

    static class Cat extends ConditionalMultibindTest.Animal {
        Cat() {
            super("cat");
        }
    }

    static class Fish extends ConditionalMultibindTest.Animal {
        Fish() {
            super("fish");
        }
    }

    static class Tiger extends ConditionalMultibindTest.Animal {
        Tiger() {
            super("tiger");
        }
    }

    static class Bird extends ConditionalMultibindTest.Animal {
        Bird() {
            super("bird");
        }
    }

    static class Zoo<T> {
        Set<T> animals;

        public Zoo() {
            animals = new HashSet<>();
        }

        public Zoo(Set<T> animals) {
            this.animals = animals;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ConditionalMultibindTest.Zoo<?> zoo = ((ConditionalMultibindTest.Zoo<?>) (o));
            return (animals) != null ? animals.equals(zoo.animals) : (zoo.animals) == null;
        }

        @Override
        public int hashCode() {
            return (animals) != null ? animals.hashCode() : 0;
        }

        @Override
        public String toString() {
            return (("Zoo{" + "animals=") + (animals)) + '}';
        }
    }

    @Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @BindingAnnotation
    @interface SanDiego {}

    @Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @BindingAnnotation
    @interface SanJose {}
}

