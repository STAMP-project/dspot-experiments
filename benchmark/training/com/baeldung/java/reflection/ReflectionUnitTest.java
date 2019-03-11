package com.baeldung.java.reflection;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ReflectionUnitTest {
    @Test
    public void givenObject_whenGetsFieldNamesAtRuntime_thenCorrect() {
        final Object person = new Person();
        final Field[] fields = person.getClass().getDeclaredFields();
        final List<String> actualFieldNames = ReflectionUnitTest.getFieldNames(fields);
        Assert.assertTrue(Arrays.asList("name", "age").containsAll(actualFieldNames));
    }

    @Test
    public void givenObject_whenGetsClassName_thenCorrect() {
        final Object goat = new Goat("goat");
        final Class<?> clazz = goat.getClass();
        Assert.assertEquals("Goat", clazz.getSimpleName());
        Assert.assertEquals("com.baeldung.java.reflection.Goat", clazz.getName());
        Assert.assertEquals("com.baeldung.java.reflection.Goat", clazz.getCanonicalName());
    }

    @Test
    public void givenClassName_whenCreatesObject_thenCorrect() throws ClassNotFoundException {
        final Class<?> clazz = Class.forName("com.baeldung.java.reflection.Goat");
        Assert.assertEquals("Goat", clazz.getSimpleName());
        Assert.assertEquals("com.baeldung.java.reflection.Goat", clazz.getName());
        Assert.assertEquals("com.baeldung.java.reflection.Goat", clazz.getCanonicalName());
    }

    @Test
    public void givenClass_whenRecognisesModifiers_thenCorrect() throws ClassNotFoundException {
        final Class<?> goatClass = Class.forName("com.baeldung.java.reflection.Goat");
        final Class<?> animalClass = Class.forName("com.baeldung.java.reflection.Animal");
        final int goatMods = goatClass.getModifiers();
        final int animalMods = animalClass.getModifiers();
        Assert.assertTrue(Modifier.isPublic(goatMods));
        Assert.assertTrue(Modifier.isAbstract(animalMods));
        Assert.assertTrue(Modifier.isPublic(animalMods));
    }

    @Test
    public void givenClass_whenGetsPackageInfo_thenCorrect() {
        final Goat goat = new Goat("goat");
        final Class<?> goatClass = goat.getClass();
        final Package pkg = goatClass.getPackage();
        Assert.assertEquals("com.baeldung.java.reflection", pkg.getName());
    }

    @Test
    public void givenClass_whenGetsSuperClass_thenCorrect() {
        final Goat goat = new Goat("goat");
        final String str = "any string";
        final Class<?> goatClass = goat.getClass();
        final Class<?> goatSuperClass = goatClass.getSuperclass();
        Assert.assertEquals("Animal", goatSuperClass.getSimpleName());
        Assert.assertEquals("Object", str.getClass().getSuperclass().getSimpleName());
    }

    @Test
    public void givenClass_whenGetsImplementedInterfaces_thenCorrect() throws ClassNotFoundException {
        final Class<?> goatClass = Class.forName("com.baeldung.java.reflection.Goat");
        final Class<?> animalClass = Class.forName("com.baeldung.java.reflection.Animal");
        final Class<?>[] goatInterfaces = goatClass.getInterfaces();
        final Class<?>[] animalInterfaces = animalClass.getInterfaces();
        Assert.assertEquals(1, goatInterfaces.length);
        Assert.assertEquals(1, animalInterfaces.length);
        Assert.assertEquals("Locomotion", goatInterfaces[0].getSimpleName());
        Assert.assertEquals("Eating", animalInterfaces[0].getSimpleName());
    }

    @Test
    public void givenClass_whenGetsConstructor_thenCorrect() throws ClassNotFoundException {
        final Class<?> goatClass = Class.forName("com.baeldung.java.reflection.Goat");
        final Constructor<?>[] constructors = goatClass.getConstructors();
        Assert.assertEquals(1, constructors.length);
        Assert.assertEquals("com.baeldung.java.reflection.Goat", constructors[0].getName());
    }

    @Test
    public void givenClass_whenGetsFields_thenCorrect() throws ClassNotFoundException {
        final Class<?> animalClass = Class.forName("com.baeldung.java.reflection.Animal");
        final Field[] fields = animalClass.getDeclaredFields();
        final List<String> actualFields = ReflectionUnitTest.getFieldNames(fields);
        Assert.assertEquals(2, actualFields.size());
        Assert.assertTrue(actualFields.containsAll(Arrays.asList("name", "CATEGORY")));
    }

    @Test
    public void givenClass_whenGetsMethods_thenCorrect() throws ClassNotFoundException {
        final Class<?> animalClass = Class.forName("com.baeldung.java.reflection.Animal");
        final Method[] methods = animalClass.getDeclaredMethods();
        final List<String> actualMethods = ReflectionUnitTest.getMethodNames(methods);
        Assert.assertEquals(3, actualMethods.size());
        Assert.assertTrue(actualMethods.containsAll(Arrays.asList("getName", "setName", "getSound")));
    }

    @Test
    public void givenClass_whenGetsAllConstructors_thenCorrect() throws ClassNotFoundException {
        final Class<?> birdClass = Class.forName("com.baeldung.java.reflection.Bird");
        final Constructor<?>[] constructors = birdClass.getConstructors();
        Assert.assertEquals(3, constructors.length);
    }

    @Test
    public void givenClass_whenGetsEachConstructorByParamTypes_thenCorrect() throws Exception {
        final Class<?> birdClass = Class.forName("com.baeldung.java.reflection.Bird");
        birdClass.getConstructor();
        birdClass.getConstructor(String.class);
        birdClass.getConstructor(String.class, boolean.class);
    }

    @Test
    public void givenClass_whenInstantiatesObjectsAtRuntime_thenCorrect() throws Exception {
        final Class<?> birdClass = Class.forName("com.baeldung.java.reflection.Bird");
        final Constructor<?> cons1 = birdClass.getConstructor();
        final Constructor<?> cons2 = birdClass.getConstructor(String.class);
        final Constructor<?> cons3 = birdClass.getConstructor(String.class, boolean.class);
        final Bird bird1 = ((Bird) (cons1.newInstance()));
        final Bird bird2 = ((Bird) (cons2.newInstance("Weaver bird")));
        final Bird bird3 = ((Bird) (cons3.newInstance("dove", true)));
        Assert.assertEquals("bird", bird1.getName());
        Assert.assertEquals("Weaver bird", bird2.getName());
        Assert.assertEquals("dove", bird3.getName());
        Assert.assertFalse(bird1.walks());
        Assert.assertTrue(bird3.walks());
    }

    @Test
    public void givenClass_whenGetsPublicFields_thenCorrect() throws ClassNotFoundException {
        final Class<?> birdClass = Class.forName("com.baeldung.java.reflection.Bird");
        final Field[] fields = birdClass.getFields();
        Assert.assertEquals(1, fields.length);
        Assert.assertEquals("CATEGORY", fields[0].getName());
    }

    @Test
    public void givenClass_whenGetsPublicFieldByName_thenCorrect() throws Exception {
        final Class<?> birdClass = Class.forName("com.baeldung.java.reflection.Bird");
        final Field field = birdClass.getField("CATEGORY");
        Assert.assertEquals("CATEGORY", field.getName());
    }

    @Test
    public void givenClass_whenGetsDeclaredFields_thenCorrect() throws ClassNotFoundException {
        final Class<?> birdClass = Class.forName("com.baeldung.java.reflection.Bird");
        final Field[] fields = birdClass.getDeclaredFields();
        Assert.assertEquals(1, fields.length);
        Assert.assertEquals("walks", fields[0].getName());
    }

    @Test
    public void givenClass_whenGetsFieldsByName_thenCorrect() throws Exception {
        final Class<?> birdClass = Class.forName("com.baeldung.java.reflection.Bird");
        final Field field = birdClass.getDeclaredField("walks");
        Assert.assertEquals("walks", field.getName());
    }

    @Test
    public void givenClassField_whenGetsType_thenCorrect() throws Exception {
        final Field field = Class.forName("com.baeldung.java.reflection.Bird").getDeclaredField("walks");
        final Class<?> fieldClass = field.getType();
        Assert.assertEquals("boolean", fieldClass.getSimpleName());
    }

    @Test
    public void givenClassField_whenSetsAndGetsValue_thenCorrect() throws Exception {
        final Class<?> birdClass = Class.forName("com.baeldung.java.reflection.Bird");
        final Bird bird = ((Bird) (birdClass.newInstance()));
        final Field field = birdClass.getDeclaredField("walks");
        field.setAccessible(true);
        Assert.assertFalse(field.getBoolean(bird));
        Assert.assertFalse(bird.walks());
        field.set(bird, true);
        Assert.assertTrue(field.getBoolean(bird));
        Assert.assertTrue(bird.walks());
    }

    @Test
    public void givenClassField_whenGetsAndSetsWithNull_thenCorrect() throws Exception {
        final Class<?> birdClass = Class.forName("com.baeldung.java.reflection.Bird");
        final Field field = birdClass.getField("CATEGORY");
        field.setAccessible(true);
        Assert.assertEquals("domestic", field.get(null));
    }

    @Test
    public void givenClass_whenGetsAllPublicMethods_thenCorrect() throws ClassNotFoundException {
        final Class<?> birdClass = Class.forName("com.baeldung.java.reflection.Bird");
        final Method[] methods = birdClass.getMethods();
        final List<String> methodNames = ReflectionUnitTest.getMethodNames(methods);
        Assert.assertTrue(methodNames.containsAll(Arrays.asList("equals", "notifyAll", "hashCode", "walks", "eats", "toString")));
    }

    @Test
    public void givenClass_whenGetsOnlyDeclaredMethods_thenCorrect() throws ClassNotFoundException {
        final Class<?> birdClass = Class.forName("com.baeldung.java.reflection.Bird");
        final List<String> actualMethodNames = ReflectionUnitTest.getMethodNames(birdClass.getDeclaredMethods());
        final List<String> expectedMethodNames = Arrays.asList("setWalks", "walks", "getSound", "eats");
        Assert.assertEquals(expectedMethodNames.size(), actualMethodNames.size());
        Assert.assertTrue(expectedMethodNames.containsAll(actualMethodNames));
        Assert.assertTrue(actualMethodNames.containsAll(expectedMethodNames));
    }

    @Test
    public void givenMethodName_whenGetsMethod_thenCorrect() throws Exception {
        final Class<?> birdClass = Class.forName("com.baeldung.java.reflection.Bird");
        final Method walksMethod = birdClass.getDeclaredMethod("walks");
        final Method setWalksMethod = birdClass.getDeclaredMethod("setWalks", boolean.class);
        Assert.assertFalse(walksMethod.isAccessible());
        Assert.assertFalse(setWalksMethod.isAccessible());
        walksMethod.setAccessible(true);
        setWalksMethod.setAccessible(true);
        Assert.assertTrue(walksMethod.isAccessible());
        Assert.assertTrue(setWalksMethod.isAccessible());
    }

    @Test
    public void givenMethod_whenInvokes_thenCorrect() throws Exception {
        final Class<?> birdClass = Class.forName("com.baeldung.java.reflection.Bird");
        final Bird bird = ((Bird) (birdClass.newInstance()));
        final Method setWalksMethod = birdClass.getDeclaredMethod("setWalks", boolean.class);
        final Method walksMethod = birdClass.getDeclaredMethod("walks");
        final boolean walks = ((boolean) (walksMethod.invoke(bird)));
        Assert.assertFalse(walks);
        Assert.assertFalse(bird.walks());
        setWalksMethod.invoke(bird, true);
        final boolean walks2 = ((boolean) (walksMethod.invoke(bird)));
        Assert.assertTrue(walks2);
        Assert.assertTrue(bird.walks());
    }
}

