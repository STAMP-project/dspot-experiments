package net.bytebuddy.utility;


import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Member;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;


public class JavaTypeTest {
    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    public void testConstable() throws Exception {
        MatcherAssert.assertThat(CONSTABLE.getTypeStub().getName(), CoreMatchers.is("java.lang.constant.Constable"));
        MatcherAssert.assertThat(CONSTABLE.getTypeStub().getModifiers(), CoreMatchers.is((((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_ABSTRACT)) | (Opcodes.ACC_INTERFACE))));
        MatcherAssert.assertThat(CONSTABLE.getTypeStub().getSuperClass(), CoreMatchers.nullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(CONSTABLE.getTypeStub().getInterfaces().size(), CoreMatchers.is(0));
    }

    @Test
    public void testTypeDescriptor() throws Exception {
        MatcherAssert.assertThat(TYPE_DESCRIPTOR.getTypeStub().getName(), CoreMatchers.is("java.lang.invoke.TypeDescriptor"));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR.getTypeStub().getModifiers(), CoreMatchers.is((((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_ABSTRACT)) | (Opcodes.ACC_INTERFACE))));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR.getTypeStub().getSuperClass(), CoreMatchers.nullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR.getTypeStub().getInterfaces().size(), CoreMatchers.is(0));
    }

    @Test
    public void testTypeDescriptorOfField() throws Exception {
        MatcherAssert.assertThat(TYPE_DESCRIPTOR_OF_FIELD.getTypeStub().getName(), CoreMatchers.is("java.lang.invoke.TypeDescriptor$OfField"));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR_OF_FIELD.getTypeStub().getModifiers(), CoreMatchers.is((((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_ABSTRACT)) | (Opcodes.ACC_INTERFACE))));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR_OF_FIELD.getTypeStub().getSuperClass(), CoreMatchers.nullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR_OF_FIELD.getTypeStub().getInterfaces().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR_OF_FIELD.getTypeStub().getInterfaces(), CoreMatchers.hasItems(TYPE_DESCRIPTOR.getTypeStub().asGenericType()));
    }

    @Test
    public void testTypeDescriptorOfMethod() throws Exception {
        MatcherAssert.assertThat(TYPE_DESCRIPTOR_OF_METHOD.getTypeStub().getName(), CoreMatchers.is("java.lang.invoke.TypeDescriptor$OfMethod"));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR_OF_METHOD.getTypeStub().getModifiers(), CoreMatchers.is((((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_ABSTRACT)) | (Opcodes.ACC_INTERFACE))));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR_OF_METHOD.getTypeStub().getSuperClass(), CoreMatchers.nullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR_OF_METHOD.getTypeStub().getInterfaces().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR_OF_METHOD.getTypeStub().getInterfaces(), CoreMatchers.hasItems(TYPE_DESCRIPTOR.getTypeStub().asGenericType()));
    }

    @Test
    public void testMethodHandle() throws Exception {
        MatcherAssert.assertThat(JavaType.METHOD_HANDLE.getTypeStub().getName(), CoreMatchers.is("java.lang.invoke.MethodHandle"));
        MatcherAssert.assertThat(JavaType.METHOD_HANDLE.getTypeStub().getModifiers(), CoreMatchers.is(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_ABSTRACT))));
        MatcherAssert.assertThat(JavaType.METHOD_HANDLE.getTypeStub().getSuperClass(), CoreMatchers.is(OBJECT));
        MatcherAssert.assertThat(JavaType.METHOD_HANDLE.getTypeStub().getInterfaces().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(JavaType.METHOD_HANDLE.getTypeStub().getInterfaces(), CoreMatchers.hasItems(CONSTABLE.getTypeStub().asGenericType()));
    }

    @Test
    public void testMethodHandles() throws Exception {
        MatcherAssert.assertThat(METHOD_HANDLES.getTypeStub().getName(), CoreMatchers.is("java.lang.invoke.MethodHandles"));
        MatcherAssert.assertThat(METHOD_HANDLES.getTypeStub().getModifiers(), CoreMatchers.is(Opcodes.ACC_PUBLIC));
        MatcherAssert.assertThat(METHOD_HANDLES.getTypeStub().getSuperClass(), CoreMatchers.is(OBJECT));
        MatcherAssert.assertThat(METHOD_HANDLES.getTypeStub().getInterfaces().size(), CoreMatchers.is(0));
    }

    @Test
    public void testMethodType() throws Exception {
        MatcherAssert.assertThat(JavaType.METHOD_TYPE.getTypeStub().getName(), CoreMatchers.is("java.lang.invoke.MethodType"));
        MatcherAssert.assertThat(JavaType.METHOD_TYPE.getTypeStub().getModifiers(), CoreMatchers.is(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_FINAL))));
        MatcherAssert.assertThat(JavaType.METHOD_TYPE.getTypeStub().getSuperClass(), CoreMatchers.is(OBJECT));
        MatcherAssert.assertThat(JavaType.METHOD_TYPE.getTypeStub().getInterfaces().size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(JavaType.METHOD_TYPE.getTypeStub().getInterfaces(), CoreMatchers.hasItems(CONSTABLE.getTypeStub().asGenericType(), TYPE_DESCRIPTOR_OF_METHOD.getTypeStub().asGenericType(), of(Serializable.class)));
    }

    @Test
    public void testMethodTypesLookup() throws Exception {
        MatcherAssert.assertThat(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().getName(), CoreMatchers.is("java.lang.invoke.MethodHandles$Lookup"));
        MatcherAssert.assertThat(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().getModifiers(), CoreMatchers.is((((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_STATIC)) | (Opcodes.ACC_FINAL))));
        MatcherAssert.assertThat(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().getSuperClass(), CoreMatchers.is(OBJECT));
        MatcherAssert.assertThat(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().getInterfaces().size(), CoreMatchers.is(0));
    }

    @Test
    public void testCallSite() throws Exception {
        MatcherAssert.assertThat(JavaType.CALL_SITE.getTypeStub().getName(), CoreMatchers.is("java.lang.invoke.CallSite"));
        MatcherAssert.assertThat(JavaType.CALL_SITE.getTypeStub().getModifiers(), CoreMatchers.is(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_ABSTRACT))));
        MatcherAssert.assertThat(JavaType.CALL_SITE.getTypeStub().getSuperClass(), CoreMatchers.is(OBJECT));
        MatcherAssert.assertThat(JavaType.CALL_SITE.getTypeStub().getInterfaces().size(), CoreMatchers.is(0));
    }

    @Test
    public void testVarHandle() throws Exception {
        MatcherAssert.assertThat(VAR_HANDLE.getTypeStub().getName(), CoreMatchers.is("java.lang.invoke.VarHandle"));
        MatcherAssert.assertThat(VAR_HANDLE.getTypeStub().getModifiers(), CoreMatchers.is(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_ABSTRACT))));
        MatcherAssert.assertThat(VAR_HANDLE.getTypeStub().getSuperClass(), CoreMatchers.is(OBJECT));
        MatcherAssert.assertThat(VAR_HANDLE.getTypeStub().getInterfaces().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(VAR_HANDLE.getTypeStub().getInterfaces(), CoreMatchers.hasItems(CONSTABLE.getTypeStub().asGenericType()));
    }

    @Test
    public void testParameter() throws Exception {
        MatcherAssert.assertThat(JavaType.PARAMETER.getTypeStub().getName(), CoreMatchers.is("java.lang.reflect.Parameter"));
        MatcherAssert.assertThat(JavaType.PARAMETER.getTypeStub().getModifiers(), CoreMatchers.is(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_FINAL))));
        MatcherAssert.assertThat(JavaType.PARAMETER.getTypeStub().getSuperClass(), CoreMatchers.is(OBJECT));
        MatcherAssert.assertThat(JavaType.PARAMETER.getTypeStub().getInterfaces().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(JavaType.PARAMETER.getTypeStub().getInterfaces(), CoreMatchers.hasItems(of(AnnotatedElement.class)));
    }

    @Test
    public void testExecutable() throws Exception {
        MatcherAssert.assertThat(JavaType.EXECUTABLE.getTypeStub().getName(), CoreMatchers.is("java.lang.reflect.Executable"));
        MatcherAssert.assertThat(JavaType.EXECUTABLE.getTypeStub().getModifiers(), CoreMatchers.is(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_ABSTRACT))));
        MatcherAssert.assertThat(JavaType.EXECUTABLE.getTypeStub().getSuperClass(), CoreMatchers.is(((TypeDefinition) (of(AccessibleObject.class)))));
        MatcherAssert.assertThat(JavaType.EXECUTABLE.getTypeStub().getInterfaces().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(JavaType.EXECUTABLE.getTypeStub().getInterfaces(), CoreMatchers.hasItems(of(Member.class), of(GenericDeclaration.class)));
    }

    @Test
    public void testModule() throws Exception {
        MatcherAssert.assertThat(JavaType.MODULE.getTypeStub().getName(), CoreMatchers.is("java.lang.Module"));
        MatcherAssert.assertThat(JavaType.MODULE.getTypeStub().getModifiers(), CoreMatchers.is(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_FINAL))));
        MatcherAssert.assertThat(JavaType.MODULE.getTypeStub().getSuperClass(), CoreMatchers.is(OBJECT));
        MatcherAssert.assertThat(JavaType.MODULE.getTypeStub().getInterfaces().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(JavaType.MODULE.getTypeStub().getInterfaces(), CoreMatchers.hasItems(of(AnnotatedElement.class)));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testJava7Types() throws Exception {
        MatcherAssert.assertThat(JavaType.METHOD_HANDLE.load(), CoreMatchers.notNullValue(Class.class));
        MatcherAssert.assertThat(loadAsDescription(), CoreMatchers.notNullValue(TypeDescription.class));
        MatcherAssert.assertThat(JavaType.METHOD_TYPE.load(), CoreMatchers.notNullValue(Class.class));
        MatcherAssert.assertThat(loadAsDescription(), CoreMatchers.notNullValue(TypeDescription.class));
        MatcherAssert.assertThat(JavaType.METHOD_HANDLES_LOOKUP.load(), CoreMatchers.notNullValue(Class.class));
        MatcherAssert.assertThat(loadAsDescription(), CoreMatchers.notNullValue(TypeDescription.class));
        MatcherAssert.assertThat(JavaType.CALL_SITE.load(), CoreMatchers.notNullValue(Class.class));
        MatcherAssert.assertThat(loadAsDescription(), CoreMatchers.notNullValue(TypeDescription.class));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testJava8Types() throws Exception {
        MatcherAssert.assertThat(JavaType.PARAMETER.load(), CoreMatchers.notNullValue(Class.class));
        MatcherAssert.assertThat(loadAsDescription(), CoreMatchers.notNullValue(TypeDescription.class));
        MatcherAssert.assertThat(JavaType.EXECUTABLE.load(), CoreMatchers.notNullValue(Class.class));
        MatcherAssert.assertThat(loadAsDescription(), CoreMatchers.notNullValue(TypeDescription.class));
    }

    @Test
    @JavaVersionRule.Enforce(9)
    public void testJava9Types() throws Exception {
        MatcherAssert.assertThat(VAR_HANDLE.load(), CoreMatchers.notNullValue(Class.class));
        MatcherAssert.assertThat(VAR_HANDLE.loadAsDescription(), CoreMatchers.notNullValue(TypeDescription.class));
        MatcherAssert.assertThat(JavaType.MODULE.load(), CoreMatchers.notNullValue(Class.class));
        MatcherAssert.assertThat(loadAsDescription(), CoreMatchers.notNullValue(TypeDescription.class));
    }

    @Test
    @JavaVersionRule.Enforce(12)
    public void testJava12Types() throws Exception {
        MatcherAssert.assertThat(CONSTABLE.load(), CoreMatchers.notNullValue(Class.class));
        MatcherAssert.assertThat(CONSTABLE.loadAsDescription(), CoreMatchers.notNullValue(TypeDescription.class));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR.load(), CoreMatchers.notNullValue(Class.class));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR.loadAsDescription(), CoreMatchers.notNullValue(TypeDescription.class));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR_OF_FIELD.load(), CoreMatchers.notNullValue(Class.class));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR_OF_FIELD.loadAsDescription(), CoreMatchers.notNullValue(TypeDescription.class));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR_OF_METHOD.load(), CoreMatchers.notNullValue(Class.class));
        MatcherAssert.assertThat(TYPE_DESCRIPTOR_OF_METHOD.loadAsDescription(), CoreMatchers.notNullValue(TypeDescription.class));
    }
}

