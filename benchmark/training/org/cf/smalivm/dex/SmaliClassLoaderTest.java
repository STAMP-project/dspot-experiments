package org.cf.smalivm.dex;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.crypto.CipherSpi;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEKeySpec;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.ClassManager;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class SmaliClassLoaderTest {
    private static final String[] OBJECT_METHODS = new String[]{ "public boolean java.lang.Object.equals(java.lang.Object)", "public final native java.lang.Class java.lang.Object.getClass()", "public final native void java.lang.Object.notify()", "public final native void java.lang.Object.notifyAll()", "public final native void java.lang.Object.wait(long) throws java.lang.InterruptedException", "public final void java.lang.Object.wait() throws java.lang.InterruptedException", "public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException", "public java.lang.String java.lang.Object.toString()", "public native int java.lang.Object.hashCode()" };

    private static final String TEST_SMALI_PATH = (VMTester.TEST_CLASS_PATH) + "/class_builder";

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private SmaliClassLoader classLoader;

    private ClassManager classManager;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void canLoadEnumClassAndGetEnumValue() throws ClassNotFoundException {
        Class<?> klazz = classLoader.loadClass("android.annotation.SdkConstant$SdkConstantType");
        Object enumConstant = Enum.valueOf(((Class<? extends Enum>) (klazz)), "ACTIVITY_INTENT_ACTION");
        Assert.assertEquals(klazz, enumConstant.getClass());
        String[] enumStrings = Arrays.stream(klazz.getEnumConstants()).map(Object::toString).toArray(String[]::new);
        String[] expectedEnumStrings = new String[]{ "ACTIVITY_INTENT_ACTION", "BROADCAST_INTENT_ACTION", "FEATURE", "INTENT_CATEGORY", "SERVICE_ACTION", "$shadow_instance" };
        Assert.assertArrayEquals(expectedEnumStrings, enumStrings);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void canLoadEnumClassWithNonEnumField() throws ClassNotFoundException {
        Class<?> klazz = classLoader.loadClass("android.net.wifi.SupplicantState");
        Object enumConstant = Enum.valueOf(((Class<? extends Enum>) (klazz)), "ASSOCIATED");
        Assert.assertEquals(klazz, enumConstant.getClass());
    }

    @Test
    public void loadingClassWhichReferencesNonExistentClassThrowsExceptionDuringVerification() throws ClassNotFoundException {
        String className = "org.cf.test.NonExistentReference";
        Class<?> klazz = classLoader.loadClass(className);
        exception.expect(NoClassDefFoundError.class);
        exception.expectMessage("does/not/exist");
        klazz.getMethods();
    }

    @Test
    public void canLoadClassWithCircularReferences() throws Exception {
        String className1 = "org.cf.test.CircularReference1";
        Class<?> klazz1 = classLoader.loadClass(className1);
        String className2 = "org.cf.test.CircularReference2";
        Class<?> klazz2 = classLoader.loadClass(className2);
        assertHasObjectMethods(klazz1);
        assertHasObjectMethods(klazz2);
        List<Method> methods1 = getFilteredMethods(klazz1);
        Assert.assertEquals(1, methods1.size());
        List<Method> methods2 = getFilteredMethods(klazz2);
        Assert.assertEquals(1, methods2.size());
        Assert.assertEquals(methods1.get(0).getReturnType(), klazz2);
        Assert.assertEquals(methods2.get(0).getReturnType(), klazz1);
    }

    @Test
    public void canLoadClassWithOverloadedFields() throws Exception {
        String className = "org.cf.test.OverloadedFields";
        Class<?> klazz = classLoader.loadClass(className);
        assertHasObjectMethods(klazz);
        List<Field> fields = getFilteredFields(klazz);
        Assert.assertEquals(2, fields.size());
        Assert.assertEquals("public static int org.cf.test.OverloadedFields.field1", fields.get(0).toString());
        Assert.assertEquals("public static java.lang.Object org.cf.test.OverloadedFields.field1", fields.get(1).toString());
    }

    @Test
    public void canLoadClassWithIllegalFieldModifiers() throws Exception {
        String className = "org.cf.test.IllegalFieldModifiers";
        Class<?> klazz = classLoader.loadClass(className);
    }

    @Test
    public void canLoadComplexClass() throws Exception {
        String className = "org.cf.test.ComplexClass";
        Class<?> klazz = classLoader.loadClass(className);
        assertHasObjectMethods(klazz);
        String superName = "org.cf.test.SuperClass";
        Class<?> superClass = classLoader.loadClass(superName);
        Assert.assertEquals(superName, superClass.getName());
        Assert.assertEquals(superClass, klazz.getSuperclass());
        List<Method> methods = getFilteredMethods(klazz);
        Assert.assertEquals(6, methods.size());
        List<Field> fields = getFilteredFields(klazz);
        Assert.assertEquals(1, fields.size());
    }

    @Test
    public void loadingProtectedJavaClassesReturnsRunningJVMClass() throws ClassNotFoundException {
        Class<?>[] expectedClasses = new Class<?>[]{ String.class, Integer.class, Object.class };
        for (Class<?> expected : expectedClasses) {
            Class<?> actual = classLoader.loadClass(expected.getName());
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void loadingNonProtectedFrameworkClassesWhichAreAlsoInJVMReturnsFrameworkJarClass() throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String[] expectedClassNames = new String[]{ PBEKeySpec.class.getName(), PBEKey.class.getName(), CipherSpi.class.getName() };
        for (String expectedClassName : expectedClassNames) {
            Class<?> actual = classLoader.loadClass(expectedClassName);
            Assert.assertEquals(expectedClassName, actual.getName());
            Class<?> jvmClass = classLoader.getParent().loadClass(expectedClassName);
            Assert.assertNotEquals(jvmClass, actual);
        }
    }

    @Test
    public void canLoadSelfReferencingClass() throws Exception {
        String className = "org.cf.test.SelfReference";
        Class<?> klazz = classLoader.loadClass(className);
        assertHasObjectMethods(klazz);
        List<Field> fields = getFilteredFields(klazz);
        Assert.assertEquals(1, fields.size());
        Class<?> arrayClass = Array.newInstance(klazz, 1).getClass();
        Assert.assertEquals(arrayClass, fields.get(0).getType());
        List<Method> methods = getFilteredMethods(klazz);
        Assert.assertEquals(1, methods.size());
        Class<?>[] parameterTypes = methods.get(0).getParameterTypes();
        Assert.assertEquals(1, parameterTypes.length);
        Assert.assertEquals(klazz, parameterTypes[0]);
    }

    @Test
    public void canLoadSimpleClass() throws ClassNotFoundException {
        String className = "org.cf.test.SimpleClass";
        Class<?> klazz = classLoader.loadClass(className);
        assertHasObjectMethods(klazz);
        List<Method> methods = getFilteredMethods(klazz);
        Assert.assertEquals(1, methods.size());
        Assert.assertEquals((("public static void " + className) + ".simpleLoop(int,java.lang.Object)"), methods.get(0).toString());
    }

    @Test
    public void throwsExceptionWhenLoadingNonExistentAndReferencedClass() throws ClassNotFoundException {
        String nonExistentClassName = "does.not.exist";
        exception.expect(ClassNotFoundException.class);
        exception.expectMessage(nonExistentClassName);
        classLoader.loadClass(nonExistentClassName);
    }

    @Test
    public void throwsExceptionWhenLoadingNonExistentAndUnreferencedClass() throws ClassNotFoundException {
        String nonExistentClassName = "asdfasdf";
        exception.expect(ClassNotFoundException.class);
        exception.expectMessage(nonExistentClassName);
        classLoader.loadClass(nonExistentClassName);
    }
}

