package net.bytebuddy.description.type;


import OpenedClassReader.ASM_API;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.bytebuddy.description.TypeVariableSource;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mockito;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.description.type.TypeDefinition.Sort.GENERIC_ARRAY;
import static net.bytebuddy.description.type.TypeDefinition.Sort.NON_GENERIC;
import static net.bytebuddy.description.type.TypeDefinition.Sort.PARAMETERIZED;
import static net.bytebuddy.description.type.TypeDefinition.Sort.VARIABLE;
import static net.bytebuddy.description.type.TypeDefinition.Sort.WILDCARD;
import static net.bytebuddy.description.type.TypeDefinition.Sort.describe;
import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.dynamic.loading.ByteArrayClassLoader.PersistenceHandler.MANIFEST;


public abstract class AbstractTypeDescriptionGenericTest {
    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    private static final String T = "T";

    private static final String S = "S";

    private static final String U = "U";

    private static final String V = "V";

    private static final String TYPE_ANNOTATION = "net.bytebuddy.test.precompiled.TypeAnnotation";

    private static final String OTHER_TYPE_ANNOTATION = "net.bytebuddy.test.precompiled.OtherTypeAnnotation";

    private static final String TYPE_ANNOTATION_SAMPLES = "net.bytebuddy.test.precompiled.TypeAnnotationSamples";

    private static final String TYPE_ANNOTATION_OTHER_SAMPLES = "net.bytebuddy.test.precompiled.TypeAnnotationOtherSamples";

    @Test
    public void testNonGenericTypeOwnerType() throws Exception {
        MatcherAssert.assertThat(describeType(AbstractTypeDescriptionGenericTest.NonGeneric.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getOwnerType(), CoreMatchers.nullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(describeType(AbstractTypeDescriptionGenericTest.NonGeneric.class.getDeclaredField(AbstractTypeDescriptionGenericTest.BAR)).getOwnerType(), CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.NonGeneric.class)));
    }

    @Test(expected = IllegalStateException.class)
    public void testNonGenericTypeNoTypeArguments() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.NonGeneric.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonGenericTypeNoBindLocation() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.NonGeneric.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).findBindingOf(Mockito.mock(TypeDescription.Generic.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testNonGenericTypeNoUpperBounds() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.NonGeneric.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getUpperBounds();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonGenericTypeNoLowerBounds() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.NonGeneric.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getLowerBounds();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonGenericTypeNoSymbol() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.NonGeneric.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getSymbol();
    }

    @Test
    public void testSimpleParameterizedType() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getActualName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.toString(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.hashCode(), CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType()).hashCode()));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType())));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.not(describe(AbstractTypeDescriptionGenericTest.SimpleGenericArrayType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getType())));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(typeDescription.equals(null), CoreMatchers.is(false));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().asErasure().represents(String.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.getOwnerType(), CoreMatchers.nullValue(TypeDescription.Generic.class));
    }

    @Test
    public void testParameterizedTypeIterator() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        Iterator<TypeDefinition> iterator = typeDescription.iterator();
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(((TypeDefinition) (typeDescription))));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testParameterizedTypeNoComponentType() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getComponentType();
    }

    @Test(expected = IllegalStateException.class)
    public void testParameterizedTypeNoVariableSource() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeVariableSource();
    }

    @Test(expected = IllegalStateException.class)
    public void testParameterizedTypeNoSymbol() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getSymbol();
    }

    @Test(expected = IllegalStateException.class)
    public void testParameterizedTypeNoUpperBounds() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getUpperBounds();
    }

    @Test(expected = IllegalStateException.class)
    public void testParameterizedTypeNoLowerBounds() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getLowerBounds();
    }

    @Test
    public void testUpperBoundWildcardParameterizedType() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getActualName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.toString(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.hashCode(), CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType()).hashCode()));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType())));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.not(describe(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getType())));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(typeDescription.equals(null), CoreMatchers.is(false));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getSort(), CoreMatchers.is(WILDCARD));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getUpperBounds().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getUpperBounds().getOnly().asErasure().represents(String.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getLowerBounds().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
    }

    @Test(expected = IllegalStateException.class)
    public void testUpperBoundWildcardParameterizedTypeNoComponentType() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getComponentType();
    }

    @Test(expected = IllegalStateException.class)
    public void testUpperBoundWildcardParameterizedTypeNoOwnerType() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getOwnerType();
    }

    @Test(expected = IllegalStateException.class)
    public void testUpperBoundWildcardParameterizedTypeNoVariableSource() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getTypeVariableSource();
    }

    @Test(expected = IllegalStateException.class)
    public void testUpperBoundWildcardParameterizedTypeNoSymbol() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getSymbol();
    }

    @Test(expected = IllegalStateException.class)
    public void testUpperBoundWildcardParameterizedTypeNoErasure() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().asErasure();
    }

    @Test(expected = IllegalStateException.class)
    public void testUpperBoundWildcardParameterizedTypeNoStackSize() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getStackSize();
    }

    @Test(expected = IllegalStateException.class)
    public void testUpperBoundWildcardParameterizedTypeNoSuperClass() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getSuperClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testUpperBoundWildcardParameterizedTypeNoInterfaces() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getInterfaces();
    }

    @Test(expected = IllegalStateException.class)
    public void testUpperBoundWildcardParameterizedTypeNoFields() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getDeclaredFields();
    }

    @Test(expected = IllegalStateException.class)
    public void testUpperBoundWildcardParameterizedTypeNoMethods() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getDeclaredMethods();
    }

    @Test(expected = IllegalStateException.class)
    public void testUpperBoundWildcardParameterizedTypeNoIterator() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().iterator();
    }

    @Test(expected = IllegalStateException.class)
    public void testUpperBoundWildcardTypeNoTypeArguments() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getTypeArguments();
    }

    @Test(expected = IllegalStateException.class)
    public void testUpperBoundWildcardTypeNoBindLocation() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UpperBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().findBindingOf(Mockito.mock(TypeDescription.Generic.class));
    }

    @Test
    public void testLowerBoundWildcardParameterizedType() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getActualName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.toString(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.hashCode(), CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType()).hashCode()));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType())));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.not(describe(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getType())));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(typeDescription.equals(null), CoreMatchers.is(false));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getSort(), CoreMatchers.is(WILDCARD));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getUpperBounds().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getLowerBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getLowerBounds().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getLowerBounds().getOnly().asErasure().represents(String.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
    }

    @Test(expected = IllegalStateException.class)
    public void testLowerBoundWildcardParameterizedTypeNoComponentType() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getComponentType();
    }

    @Test(expected = IllegalStateException.class)
    public void testLowerBoundWildcardParameterizedTypeNoOwnerType() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getOwnerType();
    }

    @Test(expected = IllegalStateException.class)
    public void testLowerBoundWildcardParameterizedTypeNoVariableSource() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getTypeVariableSource();
    }

    @Test(expected = IllegalStateException.class)
    public void testLowerBoundWildcardParameterizedTypeNoSymbol() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getSymbol();
    }

    @Test(expected = IllegalStateException.class)
    public void testLowerBoundWildcardParameterizedTypeNoErasure() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().asErasure();
    }

    @Test(expected = IllegalStateException.class)
    public void testLowerBoundWildcardParameterizedTypeNoStackSize() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getStackSize();
    }

    @Test(expected = IllegalStateException.class)
    public void testLowerBoundWildcardParameterizedTypeNoSuperClass() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getSuperClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testLowerBoundWildcardParameterizedTypeNoInterfaces() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getInterfaces();
    }

    @Test(expected = IllegalStateException.class)
    public void testLowerBoundWildcardParameterizedTypeNoFields() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getDeclaredFields();
    }

    @Test(expected = IllegalStateException.class)
    public void testLowerBoundWildcardParameterizedTypeNoMethods() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getDeclaredMethods();
    }

    @Test(expected = IllegalStateException.class)
    public void testLowerBoundWildcardParameterizedTypeNoIterator() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().iterator();
    }

    @Test(expected = IllegalStateException.class)
    public void testLowerBoundWildcardTypeNoTypeArguments() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getTypeArguments();
    }

    @Test(expected = IllegalStateException.class)
    public void testLowerBoundWildcardTypeNoBindLocation() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.LowerBoundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().findBindingOf(Mockito.mock(TypeDescription.Generic.class));
    }

    @Test
    public void testUnboundWildcardParameterizedType() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getActualName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.toString(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.hashCode(), CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType()).hashCode()));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType())));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.not(describe(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getType())));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(typeDescription.equals(null), CoreMatchers.is(false));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getSort(), CoreMatchers.is(WILDCARD));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getUpperBounds().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getUpperBounds().getOnly().asErasure().represents(Object.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getLowerBounds().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
    }

    @Test(expected = IllegalStateException.class)
    public void testUnboundWildcardParameterizedTypeNoComponentType() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getComponentType();
    }

    @Test(expected = IllegalStateException.class)
    public void testUnboundWildcardParameterizedTypeNoOwnerType() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getOwnerType();
    }

    @Test(expected = IllegalStateException.class)
    public void testUnboundWildcardParameterizedTypeNoVariableSource() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getTypeVariableSource();
    }

    @Test(expected = IllegalStateException.class)
    public void testUnboundWildcardParameterizedTypeNoSymbol() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getSymbol();
    }

    @Test(expected = IllegalStateException.class)
    public void testUnboundWildcardParameterizedTypeNoErasure() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().asErasure();
    }

    @Test(expected = IllegalStateException.class)
    public void testUnboundWildcardParameterizedTypeNoStackSize() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getStackSize();
    }

    @Test(expected = IllegalStateException.class)
    public void testUnboundWildcardParameterizedTypeNoSuperClass() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getSuperClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testUnboundWildcardParameterizedTypeNoInterfaces() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getInterfaces();
    }

    @Test(expected = IllegalStateException.class)
    public void testUnboundBoundWildcardParameterizedTypeNoFields() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getDeclaredFields();
    }

    @Test(expected = IllegalStateException.class)
    public void testUnboundBoundWildcardParameterizedTypeNoMethods() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getDeclaredMethods();
    }

    @Test(expected = IllegalStateException.class)
    public void testUnboundBoundWildcardParameterizedTypeNoIterator() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().iterator();
    }

    @Test(expected = IllegalStateException.class)
    public void testUnboundWildcardTypeNoTypeArguments() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getTypeArguments();
    }

    @Test(expected = IllegalStateException.class)
    public void testUnboundWildcardTypeNoBindLocation() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.UnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().findBindingOf(Mockito.mock(TypeDescription.Generic.class));
    }

    @Test
    public void testExplicitlyUnboundWildcardParameterizedType() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getActualName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.toString(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.hashCode(), CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType()).hashCode()));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType())));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.not(describe(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getType())));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(typeDescription.equals(null), CoreMatchers.is(false));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getSort(), CoreMatchers.is(WILDCARD));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getUpperBounds().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getUpperBounds().getOnly().asErasure().represents(Object.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getLowerBounds().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
    }

    @Test(expected = IllegalStateException.class)
    public void testExplicitlyUnboundWildcardParameterizedTypeNoComponentType() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getComponentType();
    }

    @Test(expected = IllegalStateException.class)
    public void testExplicitlyUnboundWildcardParameterizedTypeNoOwnerType() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getOwnerType();
    }

    @Test(expected = IllegalStateException.class)
    public void testExplicitlyUnboundWildcardParameterizedTypeNoVariableSource() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getTypeVariableSource();
    }

    @Test(expected = IllegalStateException.class)
    public void testExplicitlyUnboundWildcardParameterizedTypeNoSymbol() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getSymbol();
    }

    @Test(expected = IllegalStateException.class)
    public void testExplicitlyUnboundWildcardParameterizedTypeNoErasure() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().asErasure();
    }

    @Test(expected = IllegalStateException.class)
    public void testExplicitlyUnboundWildcardParameterizedTypeNoStackSize() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getStackSize();
    }

    @Test(expected = IllegalStateException.class)
    public void testExplicitlyUnboundWildcardParameterizedTypeNoSuperClass() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getSuperClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testExplicitlyUnboundWildcardParameterizedTypeNoInterfaces() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getInterfaces();
    }

    @Test(expected = IllegalStateException.class)
    public void testExplicitlyUnboundBoundWildcardParameterizedTypeNoFields() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getDeclaredFields();
    }

    @Test(expected = IllegalStateException.class)
    public void testExplicitlyUnboundBoundWildcardParameterizedTypeNoMethods() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getDeclaredMethods();
    }

    @Test(expected = IllegalStateException.class)
    public void testExplicitlyUnboundBoundWildcardParameterizedTypeNoIterator() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().iterator();
    }

    @Test(expected = IllegalStateException.class)
    public void testExplicitlyUnboundWildcardTypeNoTypeArguments() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().getTypeArguments();
    }

    @Test(expected = IllegalStateException.class)
    public void testExplicitlyUnboundWildcardTypeNoBindLocation() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.ExplicitlyUnboundWildcardParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments().getOnly().findBindingOf(Mockito.mock(TypeDescription.Generic.class));
    }

    @Test
    public void testNestedParameterizedType() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.NestedParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly().getTypeArguments().getOnly().asErasure().represents(AbstractTypeDescriptionGenericTest.Foo.class), CoreMatchers.is(true));
    }

    @Test
    public void testGenericArrayType() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.SimpleGenericArrayType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(GENERIC_ARRAY));
        MatcherAssert.assertThat(typeDescription.getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(typeDescription.getDeclaredFields().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(typeDescription.getSuperClass(), CoreMatchers.is(OBJECT));
        MatcherAssert.assertThat(typeDescription.getInterfaces(), CoreMatchers.is(TypeDescription.ARRAY_INTERFACES));
        MatcherAssert.assertThat(typeDescription.getActualName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.SimpleGenericArrayType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.SimpleGenericArrayType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.toString(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.SimpleGenericArrayType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.hashCode(), CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.SimpleGenericArrayType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType()).hashCode()));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.SimpleGenericArrayType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType())));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.not(describe(AbstractTypeDescriptionGenericTest.SimpleGenericArrayType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getType())));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(typeDescription.equals(null), CoreMatchers.is(false));
        MatcherAssert.assertThat(typeDescription.getComponentType().getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getComponentType().getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getComponentType().getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getComponentType().getTypeArguments().getOnly().asErasure().represents(String.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.SimpleGenericArrayType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
    }

    @Test
    public void testGenericArrayTypeIterator() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.SimpleGenericArrayType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        Iterator<TypeDefinition> iterator = typeDescription.iterator();
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(((TypeDefinition) (typeDescription))));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(((TypeDefinition) (TypeDescription.OBJECT))));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testGenericArrayTypeNoVariableSource() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleGenericArrayType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeVariableSource();
    }

    @Test(expected = IllegalStateException.class)
    public void testGenericArrayTypeNoSymbol() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleGenericArrayType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getSymbol();
    }

    @Test(expected = IllegalStateException.class)
    public void testGenericArrayTypeNoUpperBounds() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleGenericArrayType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getUpperBounds();
    }

    @Test(expected = IllegalStateException.class)
    public void testGenericArrayTypeNoLowerBounds() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleGenericArrayType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getLowerBounds();
    }

    @Test(expected = IllegalStateException.class)
    public void testGenericArrayTypeNoTypeArguments() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleGenericArrayType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments();
    }

    @Test(expected = IllegalStateException.class)
    public void testGenericArrayTypeNoBindLocation() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleGenericArrayType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).findBindingOf(Mockito.mock(TypeDescription.Generic.class));
    }

    @Test
    public void testGenericArrayOfGenericComponentType() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.GenericArrayOfGenericComponentType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(GENERIC_ARRAY));
        MatcherAssert.assertThat(typeDescription.getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(typeDescription.getDeclaredFields().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(typeDescription.getOwnerType(), CoreMatchers.nullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(typeDescription.getSuperClass(), CoreMatchers.is(OBJECT));
        MatcherAssert.assertThat(typeDescription.getInterfaces(), CoreMatchers.is(TypeDescription.ARRAY_INTERFACES));
        MatcherAssert.assertThat(typeDescription.getActualName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.GenericArrayOfGenericComponentType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.GenericArrayOfGenericComponentType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.toString(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.GenericArrayOfGenericComponentType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.hashCode(), CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.GenericArrayOfGenericComponentType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType()).hashCode()));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.GenericArrayOfGenericComponentType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType())));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.not(describe(AbstractTypeDescriptionGenericTest.GenericArrayOfGenericComponentType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getType())));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(typeDescription.equals(null), CoreMatchers.is(false));
        MatcherAssert.assertThat(typeDescription.getComponentType().getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getComponentType().getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getComponentType().getTypeArguments().getOnly().getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(typeDescription.getComponentType().getTypeArguments().getOnly().asErasure().represents(String.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.GenericArrayOfGenericComponentType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
    }

    @Test
    public void testGenericArrayOfGenericComponentTypeIterator() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.GenericArrayOfGenericComponentType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        Iterator<TypeDefinition> iterator = typeDescription.iterator();
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(((TypeDefinition) (typeDescription))));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(((TypeDefinition) (TypeDescription.OBJECT))));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testGenericArrayOfGenericComponentTypeNoVariableSource() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.GenericArrayOfGenericComponentType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeVariableSource();
    }

    @Test(expected = IllegalStateException.class)
    public void testGenericArrayOfGenericComponentTypeNoSymbol() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.GenericArrayOfGenericComponentType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getSymbol();
    }

    @Test(expected = IllegalStateException.class)
    public void testGenericArrayOfGenericComponentTypeNoUpperBounds() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.GenericArrayOfGenericComponentType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getUpperBounds();
    }

    @Test(expected = IllegalStateException.class)
    public void testGenericArrayOfGenericComponentTypeNoLowerBounds() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.GenericArrayOfGenericComponentType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getLowerBounds();
    }

    @Test
    public void testTypeVariableType() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(typeDescription.getActualName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.toString(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.hashCode(), CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType()).hashCode()));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.is(describe(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType())));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.not(describe(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getType())));
        MatcherAssert.assertThat(typeDescription, CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(typeDescription.equals(null), CoreMatchers.is(false));
        MatcherAssert.assertThat(typeDescription.getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.T));
        MatcherAssert.assertThat(typeDescription.getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getUpperBounds().getOnly(), CoreMatchers.is(OBJECT));
        MatcherAssert.assertThat(typeDescription.getUpperBounds().getOnly().getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.getTypeVariableSource(), CoreMatchers.is(((TypeVariableSource) (of(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class)))));
        MatcherAssert.assertThat(typeDescription.getTypeVariableSource().getTypeVariables().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeVariableSource().getTypeVariables().getOnly(), CoreMatchers.is(typeDescription));
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeVariableNoLowerBounds() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getLowerBounds();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeVariableNoComponentType() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getComponentType();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeVariableNoOwnerType() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getOwnerType();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeVariableTypeNoSuperClass() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getSuperClass();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeVariableTypeNoInterfaceTypes() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getInterfaces();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeVariableTypeNoFields() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getDeclaredFields();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeVariableTypeNoMethods() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getDeclaredMethods();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeVariableTypeNoIterator() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).iterator();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeVariableNoTypeArguments() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getTypeArguments();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeVariableNoBindLocation() throws Exception {
        describeType(AbstractTypeDescriptionGenericTest.SimpleTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).findBindingOf(Mockito.mock(TypeDescription.Generic.class));
    }

    @Test
    public void testSingleUpperBoundTypeVariableType() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.SingleUpperBoundTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(typeDescription.getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.T));
        MatcherAssert.assertThat(typeDescription.getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getUpperBounds().getOnly(), CoreMatchers.is(((TypeDefinition) (of(String.class)))));
        MatcherAssert.assertThat(typeDescription.getUpperBounds().getOnly().getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.SingleUpperBoundTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.getTypeVariableSource(), CoreMatchers.is(((TypeVariableSource) (of(AbstractTypeDescriptionGenericTest.SingleUpperBoundTypeVariableType.class)))));
        MatcherAssert.assertThat(typeDescription.getTypeVariableSource().getTypeVariables().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeVariableSource().getTypeVariables().getOnly(), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testMultipleUpperBoundTypeVariableType() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.MultipleUpperBoundTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(typeDescription.getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.T));
        MatcherAssert.assertThat(typeDescription.getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(typeDescription.getUpperBounds().size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(typeDescription.getUpperBounds().get(0), CoreMatchers.is(((TypeDefinition) (of(String.class)))));
        MatcherAssert.assertThat(typeDescription.getUpperBounds().get(1), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Foo.class)))));
        MatcherAssert.assertThat(typeDescription.getUpperBounds().get(2), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Bar.class)))));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.MultipleUpperBoundTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.getTypeVariableSource(), CoreMatchers.is(((TypeVariableSource) (of(AbstractTypeDescriptionGenericTest.MultipleUpperBoundTypeVariableType.class)))));
        MatcherAssert.assertThat(typeDescription.getTypeVariableSource().getTypeVariables().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeVariableSource().getTypeVariables().getOnly(), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testInterfaceOnlyMultipleUpperBoundTypeVariableType() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.InterfaceOnlyMultipleUpperBoundTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(typeDescription.getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.T));
        MatcherAssert.assertThat(typeDescription.getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(typeDescription.getUpperBounds().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(typeDescription.getUpperBounds().get(0), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Foo.class)))));
        MatcherAssert.assertThat(typeDescription.getUpperBounds().get(1), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Bar.class)))));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.InterfaceOnlyMultipleUpperBoundTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()));
        MatcherAssert.assertThat(typeDescription.getTypeVariableSource(), CoreMatchers.is(((TypeVariableSource) (of(AbstractTypeDescriptionGenericTest.InterfaceOnlyMultipleUpperBoundTypeVariableType.class)))));
        MatcherAssert.assertThat(typeDescription.getTypeVariableSource().getTypeVariables().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeVariableSource().getTypeVariables().getOnly(), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testShadowedTypeVariableType() throws Exception {
        TypeDescription.Generic typeDescription = describeReturnType(AbstractTypeDescriptionGenericTest.ShadowingTypeVariableType.class.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(typeDescription.getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.T));
        MatcherAssert.assertThat(typeDescription.getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(typeDescription.getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getUpperBounds().getOnly(), CoreMatchers.is(OBJECT));
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.ShadowingTypeVariableType.class.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO).getGenericReturnType().toString()));
        MatcherAssert.assertThat(typeDescription.getTypeVariableSource(), CoreMatchers.is(((TypeVariableSource) (new MethodDescription.ForLoadedMethod(AbstractTypeDescriptionGenericTest.ShadowingTypeVariableType.class.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO))))));
        MatcherAssert.assertThat(typeDescription.getTypeVariableSource().getTypeVariables().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeVariableSource().getTypeVariables().getOnly(), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testNestedTypeVariableType() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.NestedTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        // The toString implementation for parameterized types was changed within the Java 8 version range.
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.anyOf(CoreMatchers.is(AbstractTypeDescriptionGenericTest.NestedTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()), CoreMatchers.is("net.bytebuddy.description.type.AbstractTypeDescriptionGenericTest$NestedTypeVariableType<T>$Placeholder"), CoreMatchers.is(("net.bytebuddy.description.type.AbstractTypeDescriptionGenericTest" + ".net.bytebuddy.description.type.AbstractTypeDescriptionGenericTest$NestedTypeVariableType<T>.Placeholder"))));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(0));
        Type ownerType = ((ParameterizedType) (AbstractTypeDescriptionGenericTest.NestedTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType())).getOwnerType();
        MatcherAssert.assertThat(typeDescription.getOwnerType(), CoreMatchers.is(describe(ownerType)));
        MatcherAssert.assertThat(typeDescription.getOwnerType().getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getOwnerType().getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getOwnerType().getTypeArguments().getOnly().getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(typeDescription.getOwnerType().getTypeArguments().getOnly().getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.T));
    }

    @Test
    public void testNestedSpecifiedTypeVariableType() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.NestedSpecifiedTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        // The toString implementation for parameterized types was changed within the Java 8 version range.
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.anyOf(CoreMatchers.is(AbstractTypeDescriptionGenericTest.NestedSpecifiedTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()), CoreMatchers.is("net.bytebuddy.description.type.AbstractTypeDescriptionGenericTest$NestedSpecifiedTypeVariableType<java.lang.String>$Placeholder"), CoreMatchers.is(("net.bytebuddy.description.type.AbstractTypeDescriptionGenericTest" + ".net.bytebuddy.description.type.AbstractTypeDescriptionGenericTest$NestedSpecifiedTypeVariableType<java.lang.String>.Placeholder"))));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(0));
        Type ownerType = ((ParameterizedType) (AbstractTypeDescriptionGenericTest.NestedSpecifiedTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType())).getOwnerType();
        MatcherAssert.assertThat(typeDescription.getOwnerType(), CoreMatchers.is(describe(ownerType)));
        MatcherAssert.assertThat(typeDescription.getOwnerType().getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getOwnerType().getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getOwnerType().getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getOwnerType().getTypeArguments().getOnly(), CoreMatchers.is(((TypeDefinition) (of(String.class)))));
    }

    @Test
    public void testNestedStaticTypeVariableType() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.NestedStaticTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        // The toString implementation for parameterized types was changed within the Java 8 version range.
        MatcherAssert.assertThat(typeDescription.getTypeName(), CoreMatchers.anyOf(CoreMatchers.is(AbstractTypeDescriptionGenericTest.NestedStaticTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType().toString()), CoreMatchers.is("net.bytebuddy.description.type.AbstractTypeDescriptionGenericTest$NestedStaticTypeVariableType$Placeholder<java.lang.String>"), CoreMatchers.is(("net.bytebuddy.description.type.AbstractTypeDescriptionGenericTest$NestedStaticTypeVariableType" + ".net.bytebuddy.description.type.AbstractTypeDescriptionGenericTest$NestedStaticTypeVariableType$Placeholder<java.lang.String>"))));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getStackSize(), CoreMatchers.is(StackSize.SINGLE));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().getOnly(), CoreMatchers.is(((TypeDefinition) (of(String.class)))));
        Type ownerType = ((ParameterizedType) (AbstractTypeDescriptionGenericTest.NestedStaticTypeVariableType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType())).getOwnerType();
        MatcherAssert.assertThat(typeDescription.getOwnerType(), CoreMatchers.is(describe(ownerType)));
        MatcherAssert.assertThat(typeDescription.getOwnerType().getSort(), CoreMatchers.is(NON_GENERIC));
    }

    @Test
    public void testNestedInnerType() throws Exception {
        TypeDescription.Generic foo = describeReturnType(AbstractTypeDescriptionGenericTest.NestedInnerType.InnerType.class.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(foo.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(foo.getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.T));
        MatcherAssert.assertThat(foo.getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(foo.getUpperBounds().getOnly(), CoreMatchers.is(OBJECT));
        MatcherAssert.assertThat(foo.getTypeVariableSource(), CoreMatchers.is(((TypeVariableSource) (of(AbstractTypeDescriptionGenericTest.NestedInnerType.class)))));
        TypeDescription.Generic bar = describeReturnType(AbstractTypeDescriptionGenericTest.NestedInnerType.InnerType.class.getDeclaredMethod(AbstractTypeDescriptionGenericTest.BAR));
        MatcherAssert.assertThat(bar.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(bar.getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.S));
        MatcherAssert.assertThat(bar.getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(bar.getUpperBounds().getOnly(), CoreMatchers.is(foo));
        MatcherAssert.assertThat(bar.getTypeVariableSource(), CoreMatchers.is(((TypeVariableSource) (of(AbstractTypeDescriptionGenericTest.NestedInnerType.InnerType.class)))));
        TypeDescription.Generic qux = describeReturnType(AbstractTypeDescriptionGenericTest.NestedInnerType.InnerType.class.getDeclaredMethod(AbstractTypeDescriptionGenericTest.QUX));
        MatcherAssert.assertThat(qux.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(qux.getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.U));
        MatcherAssert.assertThat(qux.getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(qux.getUpperBounds().getOnly(), CoreMatchers.is(bar));
        MethodDescription quxMethod = new MethodDescription.ForLoadedMethod(AbstractTypeDescriptionGenericTest.NestedInnerType.InnerType.class.getDeclaredMethod(AbstractTypeDescriptionGenericTest.QUX));
        MatcherAssert.assertThat(qux.getTypeVariableSource(), CoreMatchers.is(((TypeVariableSource) (quxMethod))));
    }

    @Test
    public void testNestedInnerMethod() throws Exception {
        Class<?> innerType = new AbstractTypeDescriptionGenericTest.NestedInnerMethod().foo();
        TypeDescription.Generic foo = describeReturnType(innerType.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(foo.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(foo.getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.T));
        MatcherAssert.assertThat(foo.getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(foo.getUpperBounds().getOnly(), CoreMatchers.is(OBJECT));
        MatcherAssert.assertThat(foo.getTypeVariableSource(), CoreMatchers.is(((TypeVariableSource) (of(AbstractTypeDescriptionGenericTest.NestedInnerMethod.class)))));
        TypeDescription.Generic bar = describeReturnType(innerType.getDeclaredMethod(AbstractTypeDescriptionGenericTest.BAR));
        MatcherAssert.assertThat(bar.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(bar.getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.S));
        MatcherAssert.assertThat(bar.getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(bar.getUpperBounds().getOnly(), CoreMatchers.is(foo));
        MatcherAssert.assertThat(bar.getTypeVariableSource(), CoreMatchers.is(((TypeVariableSource) (new MethodDescription.ForLoadedMethod(AbstractTypeDescriptionGenericTest.NestedInnerMethod.class.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO))))));
        TypeDescription.Generic qux = describeReturnType(innerType.getDeclaredMethod(AbstractTypeDescriptionGenericTest.QUX));
        MatcherAssert.assertThat(qux.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(qux.getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.U));
        MatcherAssert.assertThat(qux.getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(qux.getUpperBounds().getOnly(), CoreMatchers.is(bar));
        MatcherAssert.assertThat(qux.getTypeVariableSource(), CoreMatchers.is(((TypeVariableSource) (of(innerType)))));
        TypeDescription.Generic baz = describeReturnType(innerType.getDeclaredMethod(AbstractTypeDescriptionGenericTest.BAZ));
        MatcherAssert.assertThat(baz.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(baz.getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.V));
        MatcherAssert.assertThat(baz.getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(baz.getUpperBounds().getOnly(), CoreMatchers.is(qux));
        MatcherAssert.assertThat(baz.getTypeVariableSource(), CoreMatchers.is(((TypeVariableSource) (new MethodDescription.ForLoadedMethod(innerType.getDeclaredMethod(AbstractTypeDescriptionGenericTest.BAZ))))));
    }

    @Test
    public void testRecursiveTypeVariable() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.RecursiveTypeVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(typeDescription.getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.T));
        MatcherAssert.assertThat(typeDescription.getUpperBounds().size(), CoreMatchers.is(1));
        TypeDescription.Generic upperBound = typeDescription.getUpperBounds().getOnly();
        MatcherAssert.assertThat(upperBound.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(upperBound.asErasure(), CoreMatchers.is(typeDescription.asErasure()));
        MatcherAssert.assertThat(upperBound.getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(upperBound.getTypeArguments().getOnly(), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testBackwardsReferenceTypeVariable() throws Exception {
        TypeDescription.Generic foo = describeType(AbstractTypeDescriptionGenericTest.BackwardsReferenceTypeVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(foo.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(foo.getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.S));
        MatcherAssert.assertThat(foo.getUpperBounds().size(), CoreMatchers.is(1));
        TypeDescription backwardsReference = TypeDescription.ForLoadedType.of(AbstractTypeDescriptionGenericTest.BackwardsReferenceTypeVariable.class);
        MatcherAssert.assertThat(foo.getUpperBounds().getOnly(), CoreMatchers.is(backwardsReference.getTypeVariables().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.T)).getOnly()));
        TypeDescription.Generic bar = describeType(AbstractTypeDescriptionGenericTest.BackwardsReferenceTypeVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.BAR));
        MatcherAssert.assertThat(bar.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(bar.getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericTest.T));
        MatcherAssert.assertThat(bar.getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(bar.getUpperBounds().getOnly(), CoreMatchers.is(OBJECT));
    }

    @Test
    public void testParameterizedTypeSuperClassResolution() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.TypeResolution.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        TypeDescription.Generic superClass = typeDescription.getSuperClass();
        MatcherAssert.assertThat(superClass.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(superClass.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.TypeResolution.Base.class)))));
        MatcherAssert.assertThat(superClass.getTypeArguments().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(superClass.getTypeArguments().get(0), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Foo.class)))));
        MatcherAssert.assertThat(superClass.getTypeArguments().get(1), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Bar.class)))));
        MatcherAssert.assertThat(superClass.getDeclaredFields().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(superClass.getDeclaredFields().getOnly().getDeclaringType(), CoreMatchers.is(superClass));
        TypeDescription.Generic fieldType = superClass.getDeclaredFields().getOnly().getType();
        MatcherAssert.assertThat(fieldType.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(fieldType.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.Qux.class)))));
        MatcherAssert.assertThat(fieldType.getTypeArguments().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(fieldType.getTypeArguments().get(0), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Foo.class)))));
        MatcherAssert.assertThat(fieldType.getTypeArguments().get(1), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Bar.class)))));
        MatcherAssert.assertThat(superClass.getDeclaredMethods().filter(ElementMatchers.isConstructor()).size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(superClass.getDeclaredMethods().filter(ElementMatchers.isMethod()).size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(superClass.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getDeclaringType(), CoreMatchers.is(superClass));
        MatcherAssert.assertThat(superClass.getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getDeclaringType(), CoreMatchers.is(superClass));
        TypeDescription.Generic methodReturnType = superClass.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getReturnType();
        MatcherAssert.assertThat(methodReturnType.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(methodReturnType.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.Qux.class)))));
        MatcherAssert.assertThat(methodReturnType.getTypeArguments().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(methodReturnType.getTypeArguments().get(0), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Foo.class)))));
        MatcherAssert.assertThat(methodReturnType.getTypeArguments().get(1), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Bar.class)))));
        TypeDescription.Generic methodParameterType = superClass.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getParameters().asTypeList().getOnly();
        MatcherAssert.assertThat(methodParameterType.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(methodParameterType.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.Qux.class)))));
        MatcherAssert.assertThat(methodParameterType.getTypeArguments().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(methodParameterType.getTypeArguments().get(0), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Foo.class)))));
        MatcherAssert.assertThat(methodParameterType.getTypeArguments().get(1), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Bar.class)))));
    }

    @Test
    public void testParameterizedTypeFindBoundValue() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.TypeResolution.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.findBindingOf(typeDescription.asErasure().getTypeVariables().getOnly()), CoreMatchers.is(typeDescription.getTypeArguments().getOnly()));
        MatcherAssert.assertThat(typeDescription.findBindingOf(typeDescription.getOwnerType().asErasure().getTypeVariables().getOnly()), CoreMatchers.is(typeDescription.getOwnerType().getTypeArguments().getOnly()));
        MatcherAssert.assertThat(typeDescription.findBindingOf(Mockito.mock(TypeDescription.Generic.class)), CoreMatchers.nullValue(TypeDescription.Generic.class));
    }

    @Test
    public void testParameterizedTypeInterfaceResolution() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.TypeResolution.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(typeDescription.getInterfaces().size(), CoreMatchers.is(1));
        TypeDescription.Generic interfaceType = typeDescription.getInterfaces().getOnly();
        MatcherAssert.assertThat(interfaceType.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(interfaceType.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.TypeResolution.BaseInterface.class)))));
        MatcherAssert.assertThat(interfaceType.getTypeArguments().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(interfaceType.getTypeArguments().get(0), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Foo.class)))));
        MatcherAssert.assertThat(interfaceType.getTypeArguments().get(1), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Bar.class)))));
        MatcherAssert.assertThat(interfaceType.getDeclaredFields().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(interfaceType.getDeclaredMethods().filter(ElementMatchers.isConstructor()).size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(interfaceType.getDeclaredMethods().filter(ElementMatchers.isMethod()).size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(interfaceType.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getDeclaringType(), CoreMatchers.is(interfaceType));
        TypeDescription.Generic methodReturnType = interfaceType.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getReturnType();
        MatcherAssert.assertThat(methodReturnType.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(methodReturnType.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.Qux.class)))));
        MatcherAssert.assertThat(methodReturnType.getTypeArguments().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(methodReturnType.getTypeArguments().get(0), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Foo.class)))));
        MatcherAssert.assertThat(methodReturnType.getTypeArguments().get(1), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Bar.class)))));
        TypeDescription.Generic methodParameterType = interfaceType.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getParameters().asTypeList().getOnly();
        MatcherAssert.assertThat(methodParameterType.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(methodParameterType.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.Qux.class)))));
        MatcherAssert.assertThat(methodParameterType.getTypeArguments().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(methodParameterType.getTypeArguments().get(0), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Foo.class)))));
        MatcherAssert.assertThat(methodParameterType.getTypeArguments().get(1), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Bar.class)))));
    }

    @Test
    public void testParameterizedTypeRawSuperClassResolution() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.TypeResolution.class.getDeclaredField(AbstractTypeDescriptionGenericTest.BAR));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        TypeDescription.Generic superClass = typeDescription.getSuperClass();
        MatcherAssert.assertThat(superClass.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(superClass.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.TypeResolution.Base.class)))));
        MatcherAssert.assertThat(superClass.getDeclaredFields().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(superClass.getDeclaredFields().getOnly().getDeclaringType().getDeclaredFields().getOnly().getType(), CoreMatchers.is(superClass.getDeclaredFields().getOnly().getType()));
        TypeDescription.Generic fieldType = superClass.getDeclaredFields().getOnly().getType();
        MatcherAssert.assertThat(fieldType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(fieldType.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.Qux.class)))));
        TypeDescription.Generic methodReturnType = superClass.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getReturnType();
        MatcherAssert.assertThat(methodReturnType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(methodReturnType.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.Qux.class)))));
        TypeDescription.Generic methodParameterType = superClass.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getParameters().asTypeList().getOnly();
        MatcherAssert.assertThat(methodParameterType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(methodParameterType.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.Qux.class)))));
        MatcherAssert.assertThat(superClass.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getDeclaringType().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getReturnType(), CoreMatchers.is(superClass.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getReturnType()));
        MatcherAssert.assertThat(superClass.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getDeclaringType().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getParameters().getOnly().getType(), CoreMatchers.is(superClass.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getParameters().getOnly().getType()));
    }

    @Test
    public void testParameterizedTypeRawInterfaceTypeResolution() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.TypeResolution.class.getDeclaredField(AbstractTypeDescriptionGenericTest.BAR));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        TypeDescription.Generic interfaceType = typeDescription.getInterfaces().getOnly();
        MatcherAssert.assertThat(interfaceType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(interfaceType.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.TypeResolution.BaseInterface.class)))));
        MatcherAssert.assertThat(interfaceType.getDeclaredFields().size(), CoreMatchers.is(0));
        TypeDescription.Generic methodReturnType = interfaceType.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getReturnType();
        MatcherAssert.assertThat(methodReturnType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(methodReturnType.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.Qux.class)))));
        TypeDescription.Generic methodParameterType = interfaceType.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().getParameters().asTypeList().getOnly();
        MatcherAssert.assertThat(methodParameterType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(methodParameterType.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.Qux.class)))));
        MatcherAssert.assertThat(interfaceType.getDeclaredMethods().getOnly().getDeclaringType().getDeclaredMethods().getOnly().getReturnType(), CoreMatchers.is(interfaceType.getDeclaredMethods().getOnly().getReturnType()));
        MatcherAssert.assertThat(interfaceType.getDeclaredMethods().getOnly().getDeclaringType().getDeclaredMethods().getOnly().getParameters().getOnly().getType(), CoreMatchers.is(interfaceType.getDeclaredMethods().getOnly().getParameters().getOnly().getType()));
    }

    @Test
    public void testParameterizedTypePartiallyRawSuperClassResolution() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.TypeResolution.class.getDeclaredField(AbstractTypeDescriptionGenericTest.QUX));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        TypeDescription.Generic superClass = typeDescription.getSuperClass();
        MatcherAssert.assertThat(superClass.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(superClass.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.TypeResolution.Intermediate.class)))));
        TypeDescription.Generic superSuperClass = superClass.getSuperClass();
        MatcherAssert.assertThat(superSuperClass.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(superSuperClass.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.TypeResolution.Base.class)))));
    }

    @Test
    public void testParameterizedTypePartiallyRawInterfaceTypeResolution() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.TypeResolution.class.getDeclaredField(AbstractTypeDescriptionGenericTest.QUX));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        TypeDescription.Generic superClass = typeDescription.getSuperClass();
        MatcherAssert.assertThat(superClass.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(superClass.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.TypeResolution.Intermediate.class)))));
        TypeDescription.Generic superInterfaceType = superClass.getInterfaces().getOnly();
        MatcherAssert.assertThat(superInterfaceType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(superInterfaceType.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.TypeResolution.BaseInterface.class)))));
    }

    @Test
    public void testParameterizedTypeNestedPartiallyRawSuperClassResolution() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.TypeResolution.class.getDeclaredField(AbstractTypeDescriptionGenericTest.BAZ));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        TypeDescription.Generic superClass = typeDescription.getSuperClass();
        MatcherAssert.assertThat(superClass.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(superClass.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.TypeResolution.NestedIntermediate.class)))));
        TypeDescription.Generic superSuperClass = superClass.getSuperClass();
        MatcherAssert.assertThat(superSuperClass.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(superSuperClass.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.TypeResolution.Base.class)))));
    }

    @Test
    public void testParameterizedTypeNestedPartiallyRawInterfaceTypeResolution() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.TypeResolution.class.getDeclaredField(AbstractTypeDescriptionGenericTest.BAZ));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(1));
        TypeDescription.Generic superClass = typeDescription.getSuperClass();
        MatcherAssert.assertThat(superClass.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(superClass.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.TypeResolution.NestedIntermediate.class)))));
        TypeDescription.Generic superInterfaceType = superClass.getInterfaces().getOnly();
        MatcherAssert.assertThat(superInterfaceType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(superInterfaceType.asErasure(), CoreMatchers.is(((TypeDescription) (of(AbstractTypeDescriptionGenericTest.TypeResolution.BaseInterface.class)))));
    }

    @Test
    public void testShadowedTypeSuperClassResolution() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.TypeResolution.class.getDeclaredField(((AbstractTypeDescriptionGenericTest.FOO) + (AbstractTypeDescriptionGenericTest.BAR))));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(2));
        TypeDescription.Generic superClass = typeDescription.getSuperClass();
        MatcherAssert.assertThat(superClass.getTypeArguments().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(superClass.getTypeArguments().get(0).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(superClass.getTypeArguments().get(0), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Bar.class)))));
        MatcherAssert.assertThat(superClass.getTypeArguments().get(1).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(superClass.getTypeArguments().get(1), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Foo.class)))));
    }

    @Test
    public void testShadowedTypeInterfaceTypeResolution() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.TypeResolution.class.getDeclaredField(((AbstractTypeDescriptionGenericTest.FOO) + (AbstractTypeDescriptionGenericTest.BAR))));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(2));
        TypeDescription.Generic interfaceType = typeDescription.getInterfaces().getOnly();
        MatcherAssert.assertThat(interfaceType.getTypeArguments().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(interfaceType.getTypeArguments().get(0).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(interfaceType.getTypeArguments().get(0), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Bar.class)))));
        MatcherAssert.assertThat(interfaceType.getTypeArguments().get(1).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(interfaceType.getTypeArguments().get(1), CoreMatchers.is(((TypeDefinition) (of(AbstractTypeDescriptionGenericTest.Foo.class)))));
    }

    @Test
    public void testMethodTypeVariableIsRetained() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().get(0).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().get(0).asErasure().represents(Number.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().get(1).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().get(1).asErasure().represents(Integer.class), CoreMatchers.is(true));
        MethodDescription methodDescription = typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly();
        MatcherAssert.assertThat(methodDescription.getReturnType().getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(methodDescription.getReturnType().getSymbol(), CoreMatchers.is("S"));
        MatcherAssert.assertThat(methodDescription.getReturnType().getTypeVariableSource(), CoreMatchers.is(((TypeVariableSource) (methodDescription.asDefined()))));
    }

    @Test
    public void testShadowedMethodTypeVariableIsRetained() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().get(0).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().get(0).asErasure().represents(Number.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().get(1).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().get(1).asErasure().represents(Integer.class), CoreMatchers.is(true));
        MethodDescription methodDescription = typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.BAR)).getOnly();
        MatcherAssert.assertThat(methodDescription.getReturnType().getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(methodDescription.getReturnType().getSymbol(), CoreMatchers.is("T"));
        MatcherAssert.assertThat(methodDescription.getReturnType().getTypeVariableSource(), CoreMatchers.is(((TypeVariableSource) (methodDescription.asDefined()))));
    }

    @Test
    public void testMethodTypeVariableWithExtensionIsRetained() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().get(0).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().get(0).asErasure().represents(Number.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().get(1).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getTypeArguments().get(1).asErasure().represents(Integer.class), CoreMatchers.is(true));
        MethodDescription methodDescription = typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.QUX)).getOnly();
        MatcherAssert.assertThat(methodDescription.getReturnType().getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(methodDescription.getReturnType().getSymbol(), CoreMatchers.is("S"));
        MatcherAssert.assertThat(methodDescription.getReturnType().getTypeVariableSource(), CoreMatchers.is(((TypeVariableSource) (methodDescription.asDefined()))));
        MatcherAssert.assertThat(methodDescription.getReturnType().getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(methodDescription.getReturnType().getUpperBounds().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(methodDescription.getReturnType().getUpperBounds().getOnly().asErasure().represents(Number.class), CoreMatchers.is(true));
    }

    @Test
    public void testMethodTypeVariableErasedBound() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.BAR)).getSuperClass();
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(NON_GENERIC));
        MethodDescription methodDescription = typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly();
        MatcherAssert.assertThat(methodDescription.getReturnType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(methodDescription.getReturnType().asErasure(), CoreMatchers.is(TypeDescription.OBJECT));
    }

    @Test
    public void testMethodTypeVariableWithExtensionErasedBound() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.BAR)).getSuperClass();
        MatcherAssert.assertThat(typeDescription.getSort(), CoreMatchers.is(NON_GENERIC));
        MethodDescription methodDescription = typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.QUX)).getOnly();
        MatcherAssert.assertThat(methodDescription.getReturnType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(methodDescription.getReturnType().asErasure(), CoreMatchers.is(TypeDescription.OBJECT));
    }

    @Test
    public void testGenericFieldHashCode() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getDeclaredFields().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly().hashCode(), CoreMatchers.not(new FieldDescription.ForLoadedField(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).hashCode()));
        MatcherAssert.assertThat(typeDescription.getDeclaredFields().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly().asDefined().hashCode(), CoreMatchers.is(new FieldDescription.ForLoadedField(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).hashCode()));
    }

    @Test
    public void testGenericFieldEquality() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getDeclaredFields().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly(), CoreMatchers.not(((FieldDescription) (new FieldDescription.ForLoadedField(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO))))));
        MatcherAssert.assertThat(typeDescription.getDeclaredFields().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly().asDefined(), CoreMatchers.is(((FieldDescription) (new FieldDescription.ForLoadedField(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO))))));
    }

    @Test
    public void testGenericMethodHashCode() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly().hashCode(), CoreMatchers.not(new MethodDescription.ForLoadedMethod(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO)).hashCode()));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly().asDefined().hashCode(), CoreMatchers.is(new MethodDescription.ForLoadedMethod(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO)).hashCode()));
    }

    @Test
    public void testGenericMethodEquality() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly(), CoreMatchers.not(((MethodDescription) (new MethodDescription.ForLoadedMethod(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO))))));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly().asDefined(), CoreMatchers.is(((MethodDescription) (new MethodDescription.ForLoadedMethod(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO))))));
    }

    @Test
    public void testGenericParameterHashCode() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.BAZ)).getOnly().getParameters().getOnly().hashCode(), CoreMatchers.not(new MethodDescription.ForLoadedMethod(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredMethod(AbstractTypeDescriptionGenericTest.BAZ, Object.class)).getParameters().getOnly().hashCode()));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.BAZ)).getOnly().getParameters().getOnly().asDefined().hashCode(), CoreMatchers.is(new MethodDescription.ForLoadedMethod(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredMethod(AbstractTypeDescriptionGenericTest.BAZ, Object.class)).getParameters().getOnly().hashCode()));
    }

    @Test
    public void testGenericParameterEquality() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.BAZ)).getOnly().getParameters().getOnly(), CoreMatchers.not(((ParameterDescription) (new MethodDescription.ForLoadedMethod(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredMethod(AbstractTypeDescriptionGenericTest.BAZ, Object.class)).getParameters().getOnly()))));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.BAZ)).getOnly().getParameters().getOnly().asDefined(), CoreMatchers.is(((ParameterDescription) (new MethodDescription.ForLoadedMethod(AbstractTypeDescriptionGenericTest.MemberVariable.class.getDeclaredMethod(AbstractTypeDescriptionGenericTest.BAZ, Object.class)).getParameters().getOnly()))));
    }

    @Test
    public void testGenericTypeInconsistency() throws Exception {
        TypeDescription.Generic typeDescription = describeType(AbstractTypeDescriptionGenericTest.GenericDisintegrator.make());
        MatcherAssert.assertThat(typeDescription.getInterfaces().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(typeDescription.getInterfaces().get(0).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getInterfaces().get(0).asErasure().represents(Callable.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getInterfaces().get(1).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getInterfaces().get(1).represents(Serializable.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly().getParameters().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly().getParameters().get(0).getType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly().getParameters().get(0).getType().asErasure().represents(Exception.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly().getParameters().get(1).getType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly().getParameters().get(1).getType().represents(Void.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly().getExceptionTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly().getExceptionTypes().get(0).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly().getExceptionTypes().get(0).asErasure().represents(Exception.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly().getExceptionTypes().get(1).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.FOO)).getOnly().getExceptionTypes().get(1).represents(RuntimeException.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getParameters().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getParameters().get(0).getType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getParameters().get(0).getType().asErasure().represents(Exception.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getParameters().get(1).getType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getParameters().get(1).getType().represents(Void.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getExceptionTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getExceptionTypes().get(0).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getExceptionTypes().get(0).asErasure().represents(Exception.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getExceptionTypes().get(1).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(typeDescription.getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getExceptionTypes().get(1).represents(RuntimeException.class), CoreMatchers.is(true));
    }

    @Test
    public void testRepresents() throws Exception {
        MatcherAssert.assertThat(describeType(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).represents(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO).getGenericType()), CoreMatchers.is(true));
        MatcherAssert.assertThat(describeType(AbstractTypeDescriptionGenericTest.SimpleParameterizedType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).represents(List.class), CoreMatchers.is(false));
    }

    @Test
    public void testRawType() throws Exception {
        TypeDescription.Generic type = describeType(AbstractTypeDescriptionGenericTest.RawType.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getSuperClass().getSuperClass();
        FieldDescription fieldDescription = type.getDeclaredFields().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.BAR)).getOnly();
        MatcherAssert.assertThat(fieldDescription.getType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(fieldDescription.getType().asErasure(), CoreMatchers.is(TypeDescription.OBJECT));
    }

    @Test
    public void testIntermediateRawType() throws Exception {
        TypeDescription.Generic type = describeType(AbstractTypeDescriptionGenericTest.IntermediateRaw.class.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO)).getSuperClass().getSuperClass().getSuperClass();
        FieldDescription fieldDescription = type.getDeclaredFields().filter(ElementMatchers.named(AbstractTypeDescriptionGenericTest.BAR)).getOnly();
        MatcherAssert.assertThat(fieldDescription.getType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(fieldDescription.getType().asErasure(), CoreMatchers.is(((TypeDescription) (of(Integer.class)))));
    }

    @Test
    public void testMixedTypeVariables() throws Exception {
        MethodDescription methodDescription = describeInterfaceType(AbstractTypeDescriptionGenericTest.MixedTypeVariables.Inner.class, 0).getDeclaredMethods().getOnly();
        MatcherAssert.assertThat(methodDescription.getParameters().getOnly().getType().getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(methodDescription.getParameters().getOnly().getType().asRawType().represents(AbstractTypeDescriptionGenericTest.MixedTypeVariables.SampleType.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(methodDescription.getParameters().getOnly().getType().getTypeArguments().get(0), CoreMatchers.is(methodDescription.getTypeVariables().getOnly()));
        MatcherAssert.assertThat(methodDescription.getParameters().getOnly().getType().getTypeArguments().get(1).represents(Void.class), CoreMatchers.is(true));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeAnnotationsFieldType() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        Class<?> samples = Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION_SAMPLES);
        TypeDescription.Generic fieldType = describeType(samples.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(fieldType.getSort(), CoreMatchers.is(GENERIC_ARRAY));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(24));
        MatcherAssert.assertThat(fieldType.getComponentType().getSort(), CoreMatchers.is(GENERIC_ARRAY));
        MatcherAssert.assertThat(fieldType.getComponentType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(fieldType.getComponentType().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(fieldType.getComponentType().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(25));
        MatcherAssert.assertThat(fieldType.getComponentType().getComponentType().getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(fieldType.getComponentType().getComponentType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(fieldType.getComponentType().getComponentType().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(fieldType.getComponentType().getComponentType().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(22));
        MatcherAssert.assertThat(fieldType.getComponentType().getComponentType().getTypeArguments().getOnly().getSort(), CoreMatchers.is(WILDCARD));
        MatcherAssert.assertThat(fieldType.getComponentType().getComponentType().getTypeArguments().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(fieldType.getComponentType().getComponentType().getTypeArguments().getOnly().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(fieldType.getComponentType().getComponentType().getTypeArguments().getOnly().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(23));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeAnnotationsMethodReturnType() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        Class<?> samples = Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION_SAMPLES);
        TypeDescription.Generic returnType = describeReturnType(samples.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO, Exception[][].class));
        MatcherAssert.assertThat(returnType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(returnType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(returnType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(returnType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(28));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeAnnotationsMethodParameterType() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        Class<?> samples = Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION_SAMPLES);
        TypeDescription.Generic parameterType = describeParameterType(samples.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO, Exception[][].class), 0);
        MatcherAssert.assertThat(parameterType.getSort(), CoreMatchers.is(GENERIC_ARRAY));
        MatcherAssert.assertThat(parameterType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(parameterType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(parameterType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(30));
        MatcherAssert.assertThat(parameterType.getComponentType().getSort(), CoreMatchers.is(GENERIC_ARRAY));
        MatcherAssert.assertThat(parameterType.getComponentType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(parameterType.getComponentType().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(parameterType.getComponentType().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(31));
        MatcherAssert.assertThat(parameterType.getComponentType().getComponentType().getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(parameterType.getComponentType().getComponentType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(parameterType.getComponentType().getComponentType().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(parameterType.getComponentType().getComponentType().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(29));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeAnnotationsSuperType() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        Class<?> samples = Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION_SAMPLES);
        TypeDescription.Generic superClass = describeSuperClass(samples);
        MatcherAssert.assertThat(superClass.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(superClass.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(superClass.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(superClass.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(18));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeAnnotationsInterfaceType() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        Class<?> samples = Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION_SAMPLES);
        TypeDescription.Generic firstInterfaceType = describeInterfaceType(samples, 0);
        MatcherAssert.assertThat(firstInterfaceType.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(firstInterfaceType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(firstInterfaceType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(firstInterfaceType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(19));
        MatcherAssert.assertThat(firstInterfaceType.getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(firstInterfaceType.getTypeArguments().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(firstInterfaceType.getTypeArguments().getOnly().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(firstInterfaceType.getTypeArguments().getOnly().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(20));
        TypeDescription.Generic secondInterfaceType = describeInterfaceType(samples, 1);
        MatcherAssert.assertThat(secondInterfaceType.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(secondInterfaceType.getDeclaredAnnotations().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(secondInterfaceType.getTypeArguments().get(0).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(secondInterfaceType.getTypeArguments().get(0).getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(secondInterfaceType.getTypeArguments().get(0).getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(secondInterfaceType.getTypeArguments().get(0).getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(21));
        MatcherAssert.assertThat(secondInterfaceType.getTypeArguments().get(1).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(secondInterfaceType.getTypeArguments().get(1).getDeclaredAnnotations().size(), CoreMatchers.is(0));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeAnnotationExceptionType() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        Class<?> samples = Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION_SAMPLES);
        TypeDescription.Generic firstExceptionType = describeExceptionType(samples.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO, Exception[][].class), 0);
        MatcherAssert.assertThat(firstExceptionType.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(firstExceptionType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(firstExceptionType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(firstExceptionType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(32));
        TypeDescription.Generic secondExceptionType = describeExceptionType(samples.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO, Exception[][].class), 1);
        MatcherAssert.assertThat(secondExceptionType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(secondExceptionType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(secondExceptionType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(secondExceptionType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(33));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeAnnotationOnNonGenericField() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        Class<?> samples = Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION_OTHER_SAMPLES);
        TypeDescription.Generic fieldType = describeType(samples.getDeclaredField(AbstractTypeDescriptionGenericTest.FOO));
        MatcherAssert.assertThat(fieldType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(0));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeAnnotationOnNonGenericReturnType() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        Class<?> samples = Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION_OTHER_SAMPLES);
        TypeDescription.Generic returnType = describeReturnType(samples.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO, Void.class));
        MatcherAssert.assertThat(returnType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(returnType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(returnType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(returnType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(9));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeAnnotationOnNonGenericParameterType() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        Class<?> samples = Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION_OTHER_SAMPLES);
        TypeDescription.Generic parameterType = describeParameterType(samples.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO, Void.class), 0);
        MatcherAssert.assertThat(parameterType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(parameterType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(parameterType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(parameterType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(10));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeAnnotationOnNonGenericExceptionType() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        Class<?> samples = Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION_OTHER_SAMPLES);
        TypeDescription.Generic exceptionType = describeExceptionType(samples.getDeclaredMethod(AbstractTypeDescriptionGenericTest.FOO, Void.class), 0);
        MatcherAssert.assertThat(exceptionType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(exceptionType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(exceptionType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(exceptionType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(11));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeAnnotationOnNonGenericArrayType() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        Class<?> samples = Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION_SAMPLES);
        TypeDescription.Generic returnType = describeReturnType(samples.getDeclaredMethod(AbstractTypeDescriptionGenericTest.BAR, Void[][].class));
        MatcherAssert.assertThat(returnType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(returnType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(returnType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(returnType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(35));
        MatcherAssert.assertThat(returnType.getComponentType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(returnType.getComponentType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(returnType.getComponentType().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(returnType.getComponentType().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(36));
        MatcherAssert.assertThat(returnType.getComponentType().getComponentType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(returnType.getComponentType().getComponentType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(returnType.getComponentType().getComponentType().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(returnType.getComponentType().getComponentType().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(34));
        TypeDescription.Generic parameterType = describeParameterType(samples.getDeclaredMethod(AbstractTypeDescriptionGenericTest.BAR, Void[][].class), 0);
        MatcherAssert.assertThat(parameterType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(parameterType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(parameterType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(parameterType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(38));
        MatcherAssert.assertThat(parameterType.getComponentType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(parameterType.getComponentType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(parameterType.getComponentType().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(parameterType.getComponentType().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(39));
        MatcherAssert.assertThat(parameterType.getComponentType().getComponentType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(parameterType.getComponentType().getComponentType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(parameterType.getComponentType().getComponentType().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(parameterType.getComponentType().getComponentType().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(37));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeAnnotationOnNonGenericArrayTypeWithGenericSignature() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        Class<?> samples = Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION_SAMPLES);
        TypeDescription.Generic returnType = describeReturnType(samples.getDeclaredMethod(AbstractTypeDescriptionGenericTest.QUX, Void[][].class));
        MatcherAssert.assertThat(returnType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(returnType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(returnType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(returnType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(41));
        MatcherAssert.assertThat(returnType.getComponentType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(returnType.getComponentType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(returnType.getComponentType().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(returnType.getComponentType().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(42));
        MatcherAssert.assertThat(returnType.getComponentType().getComponentType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(returnType.getComponentType().getComponentType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(returnType.getComponentType().getComponentType().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(returnType.getComponentType().getComponentType().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(40));
        TypeDescription.Generic parameterType = describeParameterType(samples.getDeclaredMethod(AbstractTypeDescriptionGenericTest.QUX, Void[][].class), 0);
        MatcherAssert.assertThat(parameterType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(parameterType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(parameterType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(parameterType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(44));
        MatcherAssert.assertThat(parameterType.getComponentType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(parameterType.getComponentType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(parameterType.getComponentType().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(parameterType.getComponentType().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(45));
        MatcherAssert.assertThat(parameterType.getComponentType().getComponentType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(parameterType.getComponentType().getComponentType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(parameterType.getComponentType().getComponentType().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(parameterType.getComponentType().getComponentType().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(43));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeAnnotationOwnerType() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        Class<?> samples = Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION_OTHER_SAMPLES);
        TypeDescription.Generic fieldType = describeType(samples.getDeclaredField(AbstractTypeDescriptionGenericTest.BAR));
        MatcherAssert.assertThat(fieldType.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(3));
        MatcherAssert.assertThat(fieldType.getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(fieldType.getTypeArguments().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(fieldType.getTypeArguments().getOnly().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(fieldType.getTypeArguments().getOnly().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(4));
        MatcherAssert.assertThat(fieldType.getOwnerType().getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(fieldType.getOwnerType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(fieldType.getOwnerType().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(fieldType.getOwnerType().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(1));
        MatcherAssert.assertThat(fieldType.getOwnerType().getTypeArguments().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(fieldType.getOwnerType().getTypeArguments().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(fieldType.getOwnerType().getTypeArguments().getOnly().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(fieldType.getOwnerType().getTypeArguments().getOnly().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(2));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeAnnotationTwoAnnotations() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        Class<? extends Annotation> otherTypeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericTest.OTHER_TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape otherValue = of(otherTypeAnnotation).getDeclaredMethods().getOnly();
        Class<?> samples = Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION_OTHER_SAMPLES);
        TypeDescription.Generic fieldType = describeType(samples.getDeclaredField(AbstractTypeDescriptionGenericTest.QUX));
        MatcherAssert.assertThat(fieldType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(5));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().isAnnotationPresent(otherTypeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().ofType(otherTypeAnnotation).getValue(otherValue).resolve(Integer.class), CoreMatchers.is(6));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeAnnotationNonGenericInnerType() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        Class<?> samples = Class.forName(AbstractTypeDescriptionGenericTest.TYPE_ANNOTATION_OTHER_SAMPLES);
        TypeDescription.Generic fieldType = describeType(samples.getDeclaredField(AbstractTypeDescriptionGenericTest.BAZ));
        MatcherAssert.assertThat(fieldType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(fieldType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(8));
        MatcherAssert.assertThat(fieldType.getOwnerType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(fieldType.getOwnerType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(fieldType.getOwnerType().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(fieldType.getOwnerType().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(7));
    }

    /* empty */
    @SuppressWarnings("unused")
    public interface Foo {}

    /* empty */
    @SuppressWarnings("unused")
    public interface Bar {}

    /* empty */
    @SuppressWarnings("unused")
    public interface Qux<T, U> {}

    @SuppressWarnings("unused")
    public static class NonGeneric {
        Object foo;

        AbstractTypeDescriptionGenericTest.NonGeneric.Inner bar;

        /* empty */
        class Inner {}
    }

    @SuppressWarnings("unused")
    public static class SimpleParameterizedType {
        List<String> foo;
    }

    @SuppressWarnings("unused")
    public static class UpperBoundWildcardParameterizedType {
        List<? extends String> foo;
    }

    @SuppressWarnings("unused")
    public static class LowerBoundWildcardParameterizedType {
        List<? super String> foo;
    }

    @SuppressWarnings("unused")
    public static class UnboundWildcardParameterizedType {
        List<?> foo;
    }

    @SuppressWarnings("all")
    public static class ExplicitlyUnboundWildcardParameterizedType {
        List<? extends Object> foo;
    }

    @SuppressWarnings("unused")
    public static class NestedParameterizedType {
        List<List<AbstractTypeDescriptionGenericTest.Foo>> foo;
    }

    @SuppressWarnings("unused")
    public static class SimpleGenericArrayType {
        List<String>[] foo;
    }

    @SuppressWarnings("unused")
    public static class GenericArrayOfGenericComponentType<T extends String> {
        List<T>[] foo;
    }

    @SuppressWarnings("unused")
    public static class SimpleTypeVariableType<T> {
        T foo;
    }

    @SuppressWarnings("unused")
    public static class SingleUpperBoundTypeVariableType<T extends String> {
        T foo;
    }

    @SuppressWarnings("unused")
    public static class MultipleUpperBoundTypeVariableType<T extends String & AbstractTypeDescriptionGenericTest.Foo & AbstractTypeDescriptionGenericTest.Bar> {
        T foo;
    }

    @SuppressWarnings("unused")
    public static class InterfaceOnlyMultipleUpperBoundTypeVariableType<T extends AbstractTypeDescriptionGenericTest.Foo & AbstractTypeDescriptionGenericTest.Bar> {
        T foo;
    }

    @SuppressWarnings("unused")
    public static class ShadowingTypeVariableType<T> {
        @SuppressWarnings("all")
        <T> T foo() {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static class NestedTypeVariableType<T> {
        AbstractTypeDescriptionGenericTest.NestedTypeVariableType<T>.Placeholder foo;

        /* empty */
        class Placeholder {}
    }

    @SuppressWarnings("unused")
    public static class NestedSpecifiedTypeVariableType<T> {
        AbstractTypeDescriptionGenericTest.NestedSpecifiedTypeVariableType<String>.Placeholder foo;

        /* empty */
        class Placeholder {}
    }

    @SuppressWarnings("unused")
    public static class NestedStaticTypeVariableType<T> {
        AbstractTypeDescriptionGenericTest.NestedStaticTypeVariableType.Placeholder<String> foo;

        /* empty */
        static class Placeholder<S> {}
    }

    @SuppressWarnings("unused")
    public static class NestedInnerType<T> {
        class InnerType<S extends T> {
            <U extends S> T foo() {
                return null;
            }

            <U extends S> S bar() {
                return null;
            }

            <U extends S> U qux() {
                return null;
            }
        }
    }

    @SuppressWarnings("unused")
    public static class NestedInnerMethod<T> {
        <S extends T> Class<?> foo() {
            class InnerType<U extends S> {
                <V extends U> T foo() {
                    return null;
                }

                <V extends U> S bar() {
                    return null;
                }

                <V extends U> U qux() {
                    return null;
                }

                <V extends U> V baz() {
                    return null;
                }
            }
            return InnerType.class;
        }
    }

    @SuppressWarnings("unused")
    public static class RecursiveTypeVariable<T extends AbstractTypeDescriptionGenericTest.RecursiveTypeVariable<T>> {
        T foo;
    }

    @SuppressWarnings("unused")
    public static class BackwardsReferenceTypeVariable<T, S extends T> {
        S foo;

        T bar;
    }

    @SuppressWarnings("unused")
    public static class TypeResolution<T> {
        private AbstractTypeDescriptionGenericTest.TypeResolution<AbstractTypeDescriptionGenericTest.Foo>.Inner<AbstractTypeDescriptionGenericTest.Bar> foo;

        private AbstractTypeDescriptionGenericTest.TypeResolution<AbstractTypeDescriptionGenericTest.Foo>.Raw<AbstractTypeDescriptionGenericTest.Bar> bar;

        private AbstractTypeDescriptionGenericTest.TypeResolution<AbstractTypeDescriptionGenericTest.Foo>.PartiallyRaw<AbstractTypeDescriptionGenericTest.Bar> qux;

        private AbstractTypeDescriptionGenericTest.TypeResolution<AbstractTypeDescriptionGenericTest.Foo>.NestedPartiallyRaw<AbstractTypeDescriptionGenericTest.Bar> baz;

        private AbstractTypeDescriptionGenericTest.TypeResolution<AbstractTypeDescriptionGenericTest.Foo>.Shadowed<AbstractTypeDescriptionGenericTest.Bar, AbstractTypeDescriptionGenericTest.Foo> foobar;

        public interface BaseInterface<V, W> {
            AbstractTypeDescriptionGenericTest.Qux<V, W> qux(AbstractTypeDescriptionGenericTest.Qux<V, W> qux);
        }

        /* empty */
        public static class Intermediate<V, W> extends AbstractTypeDescriptionGenericTest.TypeResolution.Base<List<V>, List<? extends W>> implements AbstractTypeDescriptionGenericTest.TypeResolution.BaseInterface<List<V>, List<? extends W>> {}

        /* empty */
        public static class NestedIntermediate<V, W> extends AbstractTypeDescriptionGenericTest.TypeResolution.Base<List<List<V>>, List<String>> implements AbstractTypeDescriptionGenericTest.TypeResolution.BaseInterface<List<List<V>>, List<String>> {}

        public static class Base<V, W> {
            AbstractTypeDescriptionGenericTest.Qux<V, W> qux;

            public AbstractTypeDescriptionGenericTest.Qux<V, W> qux(AbstractTypeDescriptionGenericTest.Qux<V, W> qux) {
                return null;
            }
        }

        /* empty */
        public class Inner<S> extends AbstractTypeDescriptionGenericTest.TypeResolution.Base<T, S> implements AbstractTypeDescriptionGenericTest.TypeResolution.BaseInterface<T, S> {}

        /* empty */
        @SuppressWarnings("unchecked")
        public class Raw<S> extends AbstractTypeDescriptionGenericTest.TypeResolution.Base implements AbstractTypeDescriptionGenericTest.TypeResolution.BaseInterface {}

        /* empty */
        @SuppressWarnings("unchecked")
        public class PartiallyRaw<S> extends AbstractTypeDescriptionGenericTest.TypeResolution.Intermediate {}

        /* empty */
        @SuppressWarnings("unchecked")
        public class NestedPartiallyRaw<S> extends AbstractTypeDescriptionGenericTest.TypeResolution.NestedIntermediate {}

        /* empty */
        @SuppressWarnings("all")
        public class Shadowed<T, S> extends AbstractTypeDescriptionGenericTest.TypeResolution.Base<T, S> implements AbstractTypeDescriptionGenericTest.TypeResolution.BaseInterface<T, S> {}
    }

    public static class RawType<T> {
        AbstractTypeDescriptionGenericTest.RawType.Extension foo;

        T bar;

        /* empty */
        public static class Intermediate<T extends Number> extends AbstractTypeDescriptionGenericTest.RawType<T> {}

        /* empty */
        public static class Extension extends AbstractTypeDescriptionGenericTest.RawType.Intermediate {}
    }

    public static class IntermediateRaw<T> {
        AbstractTypeDescriptionGenericTest.IntermediateRaw.Extension foo;

        T bar;

        /* empty */
        public static class NonGenericIntermediate extends AbstractTypeDescriptionGenericTest.IntermediateRaw<Integer> {}

        /* empty */
        public static class GenericIntermediate<T> extends AbstractTypeDescriptionGenericTest.IntermediateRaw.NonGenericIntermediate {}

        /* empty */
        public static class Extension extends AbstractTypeDescriptionGenericTest.IntermediateRaw.GenericIntermediate {}
    }

    @SuppressWarnings("unused")
    public static class MemberVariable<U, T extends U> {
        public AbstractTypeDescriptionGenericTest.MemberVariable<Number, Integer> foo;

        public AbstractTypeDescriptionGenericTest.MemberVariable.Raw bar;

        public <S> S foo() {
            return null;
        }

        @SuppressWarnings("all")
        public <T> T bar() {
            return null;
        }

        @SuppressWarnings("all")
        public <S extends U> S qux() {
            return null;
        }

        public U baz(U u) {
            return u;
        }

        /* empty */
        @SuppressWarnings("unchecked")
        public static class Raw extends AbstractTypeDescriptionGenericTest.MemberVariable {}
    }

    interface MixedTypeVariables<T> {
        /* empty */
        interface Inner extends AbstractTypeDescriptionGenericTest.MixedTypeVariables<Void> {}

        <S> void qux(AbstractTypeDescriptionGenericTest.MixedTypeVariables.SampleType<S, T> arg);

        /* empty */
        interface SampleType<U, V> {}
    }

    @SuppressWarnings("unused")
    public abstract static class InconsistentGenerics<T extends Exception> implements Callable<T> {
        InconsistentGenerics(T t) throws T {
            /* empty */
        }

        abstract void foo(T t) throws T;

        private AbstractTypeDescriptionGenericTest.InconsistentGenerics<T> foo;
    }

    public static class GenericDisintegrator extends ClassVisitor {
        public static Field make() throws IOException, ClassNotFoundException, NoSuchFieldException {
            ClassReader classReader = new ClassReader(AbstractTypeDescriptionGenericTest.InconsistentGenerics.class.getName());
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            classReader.accept(new AbstractTypeDescriptionGenericTest.GenericDisintegrator(classWriter), 0);
            return new ByteArrayClassLoader(ClassLoadingStrategy.BOOTSTRAP_LOADER, Collections.singletonMap(AbstractTypeDescriptionGenericTest.InconsistentGenerics.class.getName(), classWriter.toByteArray()), MANIFEST).loadClass(AbstractTypeDescriptionGenericTest.InconsistentGenerics.class.getName()).getDeclaredField(AbstractTypeDescriptionGenericTest.FOO);
        }

        public GenericDisintegrator(ClassVisitor classVisitor) {
            super(ASM_API, classVisitor);
        }

        @Override
        public void visit(int version, int modifiers, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, modifiers, name, signature, superName, new String[]{ Callable.class.getName().replace('.', '/'), Serializable.class.getName().replace('.', '/') });
        }

        @Override
        public void visitOuterClass(String owner, String name, String desc) {
            /* do nothing */
        }

        @Override
        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            /* do nothing */
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            return super.visitMethod(access, name, (((("(L" + (Exception.class.getName().replace('.', '/'))) + ";L") + (Void.class.getName().replace('.', '/'))) + ";)V"), signature, new String[]{ Exception.class.getName().replace('.', '/'), RuntimeException.class.getName().replace('.', '/') });
        }
    }
}

