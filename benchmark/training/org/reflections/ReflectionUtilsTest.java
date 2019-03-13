package org.reflections;


import com.google.common.collect.Sets;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.reflections.scanners.FieldAnnotationsScanner;


/**
 *
 *
 * @author mamo
 */
@SuppressWarnings("unchecked")
public class ReflectionUtilsTest {
    @Test
    public void getAllTest() {
        Assert.assertThat(getAllSuperTypes(TestModel.C3.class, withAnnotation(TestModel.AI1.class)), ReflectionsTest.are(TestModel.I1.class));
        Set<Method> allMethods = getAllMethods(TestModel.C4.class, withModifier(Modifier.PUBLIC), withReturnType(void.class));
        Set<Method> allMethods1 = getAllMethods(TestModel.C4.class, withPattern("public.*.void .*"));
        Assert.assertTrue(((allMethods.containsAll(allMethods1)) && (allMethods1.containsAll(allMethods))));
        Assert.assertThat(allMethods1, names("m1"));
        Assert.assertThat(getAllMethods(TestModel.C4.class, withAnyParameterAnnotation(TestModel.AM1.class)), names("m4"));
        Assert.assertThat(getAllFields(TestModel.C4.class, withAnnotation(TestModel.AF1.class)), names("f1", "f2"));
        Assert.assertThat(getAllFields(TestModel.C4.class, withAnnotation(new TestModel.AF1() {
            public String value() {
                return "2";
            }

            public Class<? extends Annotation> annotationType() {
                return TestModel.AF1.class;
            }
        })), names("f2"));
        Assert.assertThat(getAllFields(TestModel.C4.class, withTypeAssignableTo(String.class)), names("f1", "f2", "f3"));
        Assert.assertThat(getAllConstructors(TestModel.C4.class, withParametersCount(0)), names(TestModel.C4.class.getName()));
        Assert.assertEquals(getAllAnnotations(TestModel.C3.class).size(), 5);
        Method m4 = getMethods(TestModel.C4.class, withName("m4")).iterator().next();
        Assert.assertEquals(m4.getName(), "m4");
        Assert.assertTrue(getAnnotations(m4).isEmpty());
    }

    @Test
    public void withParameter() throws Exception {
        Class target = Collections.class;
        Object arg1 = Arrays.asList(1, 2, 3);
        Set<Method> allMethods = Sets.newHashSet();
        for (Class<?> type : getAllSuperTypes(arg1.getClass())) {
            allMethods.addAll(getAllMethods(target, withModifier(Modifier.STATIC), withParameters(type)));
        }
        Set<Method> allMethods1 = getAllMethods(target, withModifier(Modifier.STATIC), withParametersAssignableTo(arg1.getClass()));
        Assert.assertEquals(allMethods, allMethods1);
        for (Method method : allMethods) {
            // effectively invokable
            // noinspection UnusedDeclaration
            Object invoke = method.invoke(null, arg1);
        }
    }

    @Test
    public void withParametersAssignableFromTest() throws Exception {
        // Check for null safe
        getAllMethods(Collections.class, withModifier(Modifier.STATIC), withParametersAssignableFrom());
        Class target = Collections.class;
        Object arg1 = Arrays.asList(1, 2, 3);
        Set<Method> allMethods = Sets.newHashSet();
        for (Class<?> type : getAllSuperTypes(arg1.getClass())) {
            allMethods.addAll(getAllMethods(target, withModifier(Modifier.STATIC), withParameters(type)));
        }
        Set<Method> allMethods1 = getAllMethods(target, withModifier(Modifier.STATIC), withParametersAssignableFrom(Iterable.class), withParametersAssignableTo(arg1.getClass()));
        Assert.assertEquals(allMethods, allMethods1);
        for (Method method : allMethods) {
            // effectively invokable
            // noinspection UnusedDeclaration
            Object invoke = method.invoke(null, arg1);
        }
    }

    @Test
    public void withReturn() throws Exception {
        Set<Method> returnMember = getAllMethods(Class.class, withReturnTypeAssignableTo(Member.class));
        Set<Method> returnsAssignableToMember = getAllMethods(Class.class, withReturnType(Method.class));
        Assert.assertTrue(returnMember.containsAll(returnsAssignableToMember));
        Assert.assertFalse(returnsAssignableToMember.containsAll(returnMember));
        returnsAssignableToMember = getAllMethods(Class.class, withReturnType(Field.class));
        Assert.assertTrue(returnMember.containsAll(returnsAssignableToMember));
        Assert.assertFalse(returnsAssignableToMember.containsAll(returnMember));
    }

    @Test
    public void getAllAndReflections() {
        Reflections reflections = new Reflections(TestModel.class, new FieldAnnotationsScanner());
        Set<Field> af1 = reflections.getFieldsAnnotatedWith(TestModel.AF1.class);
        Set<? extends Field> allFields = ReflectionUtils.ReflectionUtils.getAll(af1, withModifier(Modifier.PROTECTED));
        Assert.assertTrue(((allFields.size()) == 1));
        Assert.assertThat(allFields, names("f2"));
    }
}

