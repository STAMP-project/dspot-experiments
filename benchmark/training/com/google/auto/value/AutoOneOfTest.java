/**
 * Copyright (C) 2018 Google, Inc.
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
package com.google.auto.value;


import AutoValue.CopyAnnotations;
import com.google.common.testing.EqualsTester;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 *
 *
 * @author emcmanus@google.com (?amonn McManus)
 */
@RunWith(JUnit4.class)
public class AutoOneOfTest {
    @AutoValue
    public abstract static class Dog {
        public abstract String name();

        public static AutoOneOfTest.Dog create(String name) {
            return new AutoValue_AutoOneOfTest_Dog(name);
        }

        public void bark() {
        }
    }

    @AutoValue
    public abstract static class Cat {
        public static AutoOneOfTest.Cat create() {
            return new AutoValue_AutoOneOfTest_Cat();
        }

        public void meow() {
        }
    }

    @AutoValue
    public abstract static class TigerShark {
        public static AutoOneOfTest.TigerShark create() {
            return new AutoValue_AutoOneOfTest_TigerShark();
        }

        public void chomp() {
        }
    }

    @AutoOneOf(AutoOneOfTest.Pet.Kind.class)
    public abstract static class Pet {
        public enum Kind {

            DOG,
            CAT,
            TIGER_SHARK;}

        public abstract AutoOneOfTest.Dog dog();

        public abstract AutoOneOfTest.Cat cat();

        public abstract AutoOneOfTest.TigerShark tigerShark();

        public static AutoOneOfTest.Pet dog(AutoOneOfTest.Dog dog) {
            return AutoOneOf_AutoOneOfTest_Pet.dog(dog);
        }

        public static AutoOneOfTest.Pet cat(AutoOneOfTest.Cat cat) {
            return AutoOneOf_AutoOneOfTest_Pet.cat(cat);
        }

        public static AutoOneOfTest.Pet tigerShark(AutoOneOfTest.TigerShark shark) {
            return AutoOneOf_AutoOneOfTest_Pet.tigerShark(shark);
        }

        public abstract AutoOneOfTest.Pet.Kind getKind();
    }

    @Test
    public void equality() {
        AutoOneOfTest.Dog marvin1 = AutoOneOfTest.Dog.create("Marvin");
        AutoOneOfTest.Pet petMarvin1 = AutoOneOfTest.Pet.dog(marvin1);
        AutoOneOfTest.Dog marvin2 = AutoOneOfTest.Dog.create("Marvin");
        AutoOneOfTest.Pet petMarvin2 = AutoOneOfTest.Pet.dog(marvin2);
        AutoOneOfTest.Dog isis = AutoOneOfTest.Dog.create("Isis");
        AutoOneOfTest.Pet petIsis = AutoOneOfTest.Pet.dog(isis);
        AutoOneOfTest.Cat cat = AutoOneOfTest.Cat.create();
        new EqualsTester().addEqualityGroup(marvin1, marvin2).addEqualityGroup(petMarvin1, petMarvin2).addEqualityGroup(petIsis).addEqualityGroup(cat).addEqualityGroup("foo").testEquals();
    }

    @Test
    public void getCorrectType() {
        AutoOneOfTest.Dog marvin = AutoOneOfTest.Dog.create("Marvin");
        AutoOneOfTest.Pet petMarvin = AutoOneOfTest.Pet.dog(marvin);
        assertThat(petMarvin.dog()).isSameAs(marvin);
    }

    @Test
    public void getWrongType() {
        AutoOneOfTest.Cat cat = AutoOneOfTest.Cat.create();
        AutoOneOfTest.Pet petCat = AutoOneOfTest.Pet.cat(cat);
        try {
            petCat.tigerShark();
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            assertThat(e).hasMessageThat().containsMatch("(?i:cat)");
        }
    }

    @Test
    public void string() {
        AutoOneOfTest.Dog marvin = AutoOneOfTest.Dog.create("Marvin");
        AutoOneOfTest.Pet petMarvin = AutoOneOfTest.Pet.dog(marvin);
        assertThat(petMarvin.toString()).isEqualTo("Pet{dog=Dog{name=Marvin}}");
    }

    @Test
    public void getKind() {
        AutoOneOfTest.Dog marvin = AutoOneOfTest.Dog.create("Marvin");
        AutoOneOfTest.Pet petMarvin = AutoOneOfTest.Pet.dog(marvin);
        AutoOneOfTest.Cat cat = AutoOneOfTest.Cat.create();
        AutoOneOfTest.Pet petCat = AutoOneOfTest.Pet.cat(cat);
        AutoOneOfTest.TigerShark shark = AutoOneOfTest.TigerShark.create();
        AutoOneOfTest.Pet petShark = AutoOneOfTest.Pet.tigerShark(shark);
        assertThat(petMarvin.getKind()).isEqualTo(AutoOneOfTest.Pet.Kind.DOG);
        assertThat(petCat.getKind()).isEqualTo(AutoOneOfTest.Pet.Kind.CAT);
        assertThat(petShark.getKind()).isEqualTo(AutoOneOfTest.Pet.Kind.TIGER_SHARK);
    }

    @Test
    public void cannotBeNull() {
        try {
            AutoOneOfTest.Pet.dog(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    // Package-private case.
    @AutoOneOf(AutoOneOfTest.IntegerOrString.Kind.class)
    abstract static class IntegerOrString {
        enum Kind {

            INTEGER,
            STRING;}

        abstract AutoOneOfTest.IntegerOrString.Kind getKind();

        abstract int integer();

        abstract String string();

        static AutoOneOfTest.IntegerOrString of(int x) {
            return AutoOneOf_AutoOneOfTest_IntegerOrString.integer(x);
        }

        static AutoOneOfTest.IntegerOrString of(String x) {
            return AutoOneOf_AutoOneOfTest_IntegerOrString.string(x);
        }
    }

    @Test
    public void packagePrivate() {
        AutoOneOfTest.IntegerOrString integer = AutoOneOfTest.IntegerOrString.of(23);
        AutoOneOfTest.IntegerOrString string = AutoOneOfTest.IntegerOrString.of("23");
        assertThat(integer.getKind()).isEqualTo(AutoOneOfTest.IntegerOrString.Kind.INTEGER);
        assertThat(string.getKind()).isEqualTo(AutoOneOfTest.IntegerOrString.Kind.STRING);
        assertThat(integer.integer()).isEqualTo(23);
        assertThat(string.string()).isEqualTo("23");
        assertThat(integer).isNotEqualTo(string);
        try {
            integer.string();
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            assertThat(e).hasMessageThat().containsMatch("(?i:integer)");
        }
    }

    @AutoOneOf(AutoOneOfTest.Pet.Kind.class)
    public abstract static class PetWithGet {
        public abstract AutoOneOfTest.Dog getDog();

        public abstract AutoOneOfTest.Cat getCat();

        public abstract AutoOneOfTest.TigerShark getTigerShark();

        public static AutoOneOfTest.PetWithGet dog(AutoOneOfTest.Dog dog) {
            return AutoOneOf_AutoOneOfTest_PetWithGet.dog(dog);
        }

        public static AutoOneOfTest.PetWithGet cat(AutoOneOfTest.Cat cat) {
            return AutoOneOf_AutoOneOfTest_PetWithGet.cat(cat);
        }

        public static AutoOneOfTest.PetWithGet tigerShark(AutoOneOfTest.TigerShark shark) {
            return AutoOneOf_AutoOneOfTest_PetWithGet.tigerShark(shark);
        }

        public abstract AutoOneOfTest.Pet.Kind getKind();
    }

    @Test
    public void getPrefix() {
        AutoOneOfTest.Dog marvin = AutoOneOfTest.Dog.create("Marvin");
        AutoOneOfTest.PetWithGet petMarvin = AutoOneOfTest.PetWithGet.dog(marvin);
        assertThat(petMarvin.toString()).isEqualTo("PetWithGet{dog=Dog{name=Marvin}}");
    }

    @AutoOneOf(AutoOneOfTest.Primitive.Kind.class)
    public abstract static class Primitive {
        public enum Kind {

            A_BYTE,
            A_SHORT,
            AN_INT,
            A_LONG,
            A_FLOAT,
            A_DOUBLE,
            A_CHAR,
            A_BOOLEAN;}

        public abstract AutoOneOfTest.Primitive.Kind getKind();

        public abstract byte aByte();

        public abstract short aShort();

        public abstract int anInt();

        public abstract long aLong();

        public abstract float aFloat();

        public abstract double aDouble();

        public abstract char aChar();

        public abstract boolean aBoolean();

        public static AutoOneOfTest.Primitive of(byte x) {
            return AutoOneOf_AutoOneOfTest_Primitive.aByte(x);
        }

        public static AutoOneOfTest.Primitive of(short x) {
            return AutoOneOf_AutoOneOfTest_Primitive.aShort(x);
        }

        public static AutoOneOfTest.Primitive of(int x) {
            return AutoOneOf_AutoOneOfTest_Primitive.anInt(x);
        }

        public static AutoOneOfTest.Primitive of(long x) {
            return AutoOneOf_AutoOneOfTest_Primitive.aLong(x);
        }

        public static AutoOneOfTest.Primitive of(float x) {
            return AutoOneOf_AutoOneOfTest_Primitive.aFloat(x);
        }

        public static AutoOneOfTest.Primitive of(double x) {
            return AutoOneOf_AutoOneOfTest_Primitive.aDouble(x);
        }

        public static AutoOneOfTest.Primitive of(char x) {
            return AutoOneOf_AutoOneOfTest_Primitive.aChar(x);
        }

        public static AutoOneOfTest.Primitive of(boolean x) {
            return AutoOneOf_AutoOneOfTest_Primitive.aBoolean(x);
        }
    }

    @Test
    public void primitive() {
        AutoOneOfTest.Primitive primitive = AutoOneOfTest.Primitive.of(17);
        assertThat(primitive.anInt()).isEqualTo(17);
        assertThat(primitive.toString()).isEqualTo("Primitive{anInt=17}");
    }

    @AutoOneOf(AutoOneOfTest.OneOfOne.Kind.class)
    public abstract static class OneOfOne {
        public enum Kind {

            DOG;}

        public abstract AutoOneOfTest.Dog getDog();

        public static AutoOneOfTest.OneOfOne dog(AutoOneOfTest.Dog dog) {
            return AutoOneOf_AutoOneOfTest_OneOfOne.dog(dog);
        }

        public abstract AutoOneOfTest.OneOfOne.Kind getKind();
    }

    @Test
    public void oneOfOne() {
        AutoOneOfTest.Dog marvin = AutoOneOfTest.Dog.create("Marvin");
        AutoOneOfTest.OneOfOne oneOfMarvin = AutoOneOfTest.OneOfOne.dog(marvin);
        assertThat(oneOfMarvin.toString()).isEqualTo("OneOfOne{dog=Dog{name=Marvin}}");
        assertThat(oneOfMarvin.getKind()).isEqualTo(AutoOneOfTest.OneOfOne.Kind.DOG);
    }

    // We allow this for consistency, even though it's obviously pretty useless.
    // The generated code might be rubbish, but it compiles. No concrete implementation is generated
    // so there isn't really anything to test beyond that it compiles.
    @AutoOneOf(AutoOneOfTest.OneOfNone.Kind.class)
    public abstract static class OneOfNone {
        public enum Kind {
            ;
        }

        public abstract AutoOneOfTest.OneOfNone.Kind getKind();
    }

    // Testing generics. Typically generics will be a bit messy because the @AutoOneOf class must
    // have type parameters for every property that needs them, even though any given property
    // might not use all the type parameters.
    @AutoOneOf(AutoOneOfTest.TaskResult.Kind.class)
    public abstract static class TaskResult<V extends Serializable> {
        public enum Kind {

            VALUE,
            EXCEPTION;}

        public abstract AutoOneOfTest.TaskResult.Kind getKind();

        public abstract V value();

        public abstract Throwable exception();

        public V get() throws ExecutionException {
            switch (getKind()) {
                case VALUE :
                    return value();
                case EXCEPTION :
                    throw new ExecutionException(exception());
            }
            throw new AssertionError(getKind());
        }

        static <V extends Serializable> AutoOneOfTest.TaskResult<V> value(V value) {
            return AutoOneOf_AutoOneOfTest_TaskResult.value(value);
        }

        static AutoOneOfTest.TaskResult<?> exception(Throwable exception) {
            return AutoOneOf_AutoOneOfTest_TaskResult.exception(exception);
        }
    }

    @Test
    public void taskResultValue() throws Exception {
        AutoOneOfTest.TaskResult<String> result = AutoOneOfTest.TaskResult.value("foo");
        assertThat(result.get()).isEqualTo("foo");
    }

    @Test
    public void taskResultException() {
        Exception exception = new IllegalArgumentException("oops");
        AutoOneOfTest.TaskResult<?> result = AutoOneOfTest.TaskResult.exception(exception);
        try {
            result.get();
            Assert.fail();
        } catch (ExecutionException e) {
            assertThat(e).hasCauseThat().isEqualTo(exception);
        }
    }

    @AutoOneOf(AutoOneOfTest.CustomToString.Kind.class)
    public abstract static class CustomToString {
        public enum Kind {

            ACE;}

        public abstract AutoOneOfTest.CustomToString.Kind getKind();

        public abstract String ace();

        public static AutoOneOfTest.CustomToString ace(String ace) {
            return AutoOneOf_AutoOneOfTest_CustomToString.ace(ace);
        }

        @Override
        public String toString() {
            return "blim";
        }
    }

    // If you have an explicit toString() method, we won't override it.
    @Test
    public void customToString() {
        AutoOneOfTest.CustomToString x = AutoOneOfTest.CustomToString.ace("ceg");
        assertThat(x.toString()).isEqualTo("blim");
    }

    @AutoOneOf(AutoOneOfTest.AbstractToString.Kind.class)
    public abstract static class AbstractToString {
        public enum Kind {

            ACE;}

        public abstract AutoOneOfTest.AbstractToString.Kind getKind();

        public abstract String ace();

        public static AutoOneOfTest.AbstractToString ace(String ace) {
            return AutoOneOf_AutoOneOfTest_AbstractToString.ace(ace);
        }

        @Override
        public abstract String toString();
    }

    // If you have an explicit abstract toString() method, we will implement it.
    @Test
    public void abstractToString() {
        AutoOneOfTest.AbstractToString x = AutoOneOfTest.AbstractToString.ace("ceg");
        assertThat(x.toString()).isEqualTo("AbstractToString{ace=ceg}");
    }

    // "package" is a reserved word. You probably don't want to have a property with that name,
    // but if you insist, you can get one by using getFoo()-style methods. We leak our renaming
    // scheme here (package0) and for users that that bothers they can just avoid having properties
    // that are reserved words.
    @AutoOneOf(AutoOneOfTest.LetterOrPackage.Kind.class)
    public abstract static class LetterOrPackage {
        public enum Kind {

            LETTER,
            PACKAGE;}

        public abstract AutoOneOfTest.LetterOrPackage.Kind getKind();

        public abstract String getLetter();

        public abstract String getPackage();

        public static AutoOneOfTest.LetterOrPackage ofLetter(String letter) {
            return AutoOneOf_AutoOneOfTest_LetterOrPackage.letter(letter);
        }

        public static AutoOneOfTest.LetterOrPackage ofPackage(String pkg) {
            return AutoOneOf_AutoOneOfTest_LetterOrPackage.package0(pkg);
        }
    }

    @Test
    public void reservedWordProperty() {
        AutoOneOfTest.LetterOrPackage pkg = AutoOneOfTest.LetterOrPackage.ofPackage("pacquet");
        assertThat(pkg.toString()).isEqualTo("LetterOrPackage{package=pacquet}");
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface CopyTest {
        int value();
    }

    @AutoOneOf(AutoOneOfTest.AnnotationNotCopied.Kind.class)
    @AutoOneOfTest.CopyTest(23)
    public abstract static class AnnotationNotCopied {
        public enum Kind {

            ACE;}

        public abstract AutoOneOfTest.AnnotationNotCopied.Kind getKind();

        public abstract String ace();

        public static AutoOneOfTest.AnnotationNotCopied ace(String ace) {
            return AutoOneOf_AutoOneOfTest_AnnotationNotCopied.ace(ace);
        }
    }

    @Test
    public void classAnnotationsNotCopiedByDefault() {
        assertThat(AutoOneOfTest.AnnotationNotCopied.class.isAnnotationPresent(AutoOneOfTest.CopyTest.class)).isTrue();
        AutoOneOfTest.AnnotationNotCopied ace = AutoOneOfTest.AnnotationNotCopied.ace("ace");
        assertThat(ace.getClass().isAnnotationPresent(AutoOneOfTest.CopyTest.class)).isFalse();
    }

    @AutoOneOf(AutoOneOfTest.AnnotationCopied.Kind.class)
    @AutoOneOfTest.CopyTest(23)
    @AutoValue.CopyAnnotations
    public abstract static class AnnotationCopied {
        public enum Kind {

            ACE;}

        public abstract AutoOneOfTest.AnnotationCopied.Kind getKind();

        public abstract String ace();

        public static AutoOneOfTest.AnnotationCopied ace(String ace) {
            return AutoOneOf_AutoOneOfTest_AnnotationCopied.ace(ace);
        }
    }

    @Test
    public void classAnnotationsCopiedIfCopyAnnotations() {
        assertThat(AutoOneOfTest.AnnotationCopied.class.isAnnotationPresent(AutoOneOfTest.CopyTest.class)).isTrue();
        AutoOneOfTest.AnnotationCopied ace = AutoOneOfTest.AnnotationCopied.ace("ace");
        assertThat(ace.getClass().isAnnotationPresent(AutoOneOfTest.CopyTest.class)).isTrue();
        assertThat(ace.getClass().getAnnotation(AutoOneOfTest.CopyTest.class).value()).isEqualTo(23);
    }
}

