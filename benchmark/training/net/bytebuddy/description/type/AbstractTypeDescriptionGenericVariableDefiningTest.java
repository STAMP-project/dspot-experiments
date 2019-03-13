package net.bytebuddy.description.type;


import java.lang.annotation.Annotation;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.description.type.TypeDefinition.Sort.NON_GENERIC;
import static net.bytebuddy.description.type.TypeDefinition.Sort.PARAMETERIZED;
import static net.bytebuddy.description.type.TypeDefinition.Sort.VARIABLE;
import static net.bytebuddy.description.type.TypeDefinition.Sort.WILDCARD;
import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;


public abstract class AbstractTypeDescriptionGenericVariableDefiningTest extends AbstractTypeDescriptionGenericTest {
    private static final String FOO = "foo";

    private static final String T = "T";

    private static final String S = "S";

    private static final String U = "U";

    private static final String V = "V";

    private static final String W = "W";

    private static final String X = "X";

    private static final String TYPE_ANNOTATION = "net.bytebuddy.test.precompiled.TypeAnnotation";

    private static final String TYPE_ANNOTATION_SAMPLES = "net.bytebuddy.test.precompiled.TypeAnnotationSamples";

    private static final String RECEIVER_TYPE_SAMPLE = "net.bytebuddy.test.precompiled.ReceiverTypeSample";

    private static final String INNER = "Inner";

    private static final String NESTED = "Nested";

    private static final String GENERIC = "Generic";

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeVariableT() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        TypeDescription typeDescription = describe(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION_SAMPLES));
        TypeDescription.Generic t = typeDescription.getTypeVariables().filter(ElementMatchers.named(AbstractTypeDescriptionGenericVariableDefiningTest.T)).getOnly();
        MatcherAssert.assertThat(t.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(t.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(t.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(t.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(0));
        MatcherAssert.assertThat(t.getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(t.getUpperBounds().contains(OBJECT), CoreMatchers.is(true));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeVariableS() throws Exception {
        TypeDescription typeDescription = describe(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION_SAMPLES));
        TypeDescription.Generic t = typeDescription.getTypeVariables().filter(ElementMatchers.named(AbstractTypeDescriptionGenericVariableDefiningTest.S)).getOnly();
        MatcherAssert.assertThat(t.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(t.getDeclaredAnnotations().size(), CoreMatchers.is(0));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeVariableU() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        TypeDescription typeDescription = describe(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION_SAMPLES));
        TypeDescription.Generic u = typeDescription.getTypeVariables().filter(ElementMatchers.named(AbstractTypeDescriptionGenericVariableDefiningTest.U)).getOnly();
        MatcherAssert.assertThat(u.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(u.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(u.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(u.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(2));
        MatcherAssert.assertThat(u.getUpperBounds().get(0).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(u.getUpperBounds().get(0).getDeclaredAnnotations().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(u.getUpperBounds().get(1).getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(u.getUpperBounds().get(1).getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(u.getUpperBounds().get(1).getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(u.getUpperBounds().get(1).getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(3));
        MatcherAssert.assertThat(u.getUpperBounds().get(1).getTypeArguments().get(0).getSort(), CoreMatchers.is(WILDCARD));
        MatcherAssert.assertThat(u.getUpperBounds().get(1).getTypeArguments().get(0).getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(u.getUpperBounds().get(1).getTypeArguments().get(0).getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(u.getUpperBounds().get(1).getTypeArguments().get(0).getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(4));
        MatcherAssert.assertThat(u.getUpperBounds().get(2).getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(u.getUpperBounds().get(2).getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(u.getUpperBounds().get(2).getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(u.getUpperBounds().get(2).getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(5));
        MatcherAssert.assertThat(u.getUpperBounds().get(2).getTypeArguments().get(0).getSort(), CoreMatchers.is(WILDCARD));
        MatcherAssert.assertThat(u.getUpperBounds().get(2).getTypeArguments().get(0).getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(u.getUpperBounds().get(2).getTypeArguments().get(0).getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(u.getUpperBounds().get(2).getTypeArguments().get(0).getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(6));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeVariableV() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        TypeDescription typeDescription = describe(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION_SAMPLES));
        TypeDescription.Generic v = typeDescription.getTypeVariables().filter(ElementMatchers.named(AbstractTypeDescriptionGenericVariableDefiningTest.V)).getOnly();
        MatcherAssert.assertThat(v.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(v.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(v.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(v.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(7));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getDeclaredAnnotations().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(0).getSort(), CoreMatchers.is(WILDCARD));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(0).getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(0).getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(0).getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(8));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(0).getUpperBounds().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(0).getUpperBounds().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(0).getUpperBounds().getOnly().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(0).getUpperBounds().getOnly().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(9));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(10));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getTypeArguments().getOnly().getSort(), CoreMatchers.is(WILDCARD));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getTypeArguments().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getTypeArguments().getOnly().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getTypeArguments().getOnly().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(11));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getTypeArguments().getOnly().getLowerBounds().getOnly().getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getTypeArguments().getOnly().getLowerBounds().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getTypeArguments().getOnly().getLowerBounds().getOnly().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getTypeArguments().getOnly().getLowerBounds().getOnly().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(12));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getTypeArguments().getOnly().getLowerBounds().getOnly().getUpperBounds().get(0).getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getTypeArguments().getOnly().getLowerBounds().getOnly().getUpperBounds().get(0).getDeclaredAnnotations().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getTypeArguments().getOnly().getLowerBounds().getOnly().getUpperBounds().get(1).getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getTypeArguments().getOnly().getLowerBounds().getOnly().getUpperBounds().get(1).getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getTypeArguments().getOnly().getLowerBounds().getOnly().getUpperBounds().get(1).getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(v.getUpperBounds().get(0).getTypeArguments().get(1).getTypeArguments().getOnly().getLowerBounds().getOnly().getUpperBounds().get(1).getDeclaredAnnotations().getOnly().prepare(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(3));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeVariableW() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        TypeDescription typeDescription = describe(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION_SAMPLES));
        TypeDescription.Generic t = typeDescription.getTypeVariables().filter(ElementMatchers.named(AbstractTypeDescriptionGenericVariableDefiningTest.W)).getOnly();
        MatcherAssert.assertThat(t.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(t.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(t.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(t.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(13));
        MatcherAssert.assertThat(t.getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(t.getUpperBounds().getOnly().getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(t.getUpperBounds().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(t.getUpperBounds().getOnly().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(t.getUpperBounds().getOnly().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(14));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    @SuppressWarnings("unchecked")
    public void testTypeVariableX() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        TypeDescription typeDescription = describe(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION_SAMPLES));
        TypeDescription.Generic t = typeDescription.getTypeVariables().filter(ElementMatchers.named(AbstractTypeDescriptionGenericVariableDefiningTest.X)).getOnly();
        MatcherAssert.assertThat(t.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(t.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(t.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(t.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(15));
        MatcherAssert.assertThat(t.getUpperBounds().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(t.getUpperBounds().getOnly().getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(t.getUpperBounds().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(t.getUpperBounds().getOnly().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(t.getUpperBounds().getOnly().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(16));
        MatcherAssert.assertThat(t.getUpperBounds().getOnly().getTypeArguments().getOnly().getSort(), CoreMatchers.is(WILDCARD));
        MatcherAssert.assertThat(t.getUpperBounds().getOnly().getTypeArguments().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(t.getUpperBounds().getOnly().getTypeArguments().getOnly().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(t.getUpperBounds().getOnly().getTypeArguments().getOnly().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(17));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(8)
    public void testMethodVariableT() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        MethodDescription methodDescription = describe(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION_SAMPLES)).getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericVariableDefiningTest.FOO)).getOnly();
        TypeDescription.Generic t = methodDescription.getTypeVariables().getOnly();
        MatcherAssert.assertThat(t.getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(t.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(t.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(t.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(26));
        MatcherAssert.assertThat(t.getUpperBounds().getOnly().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(t.getUpperBounds().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(t.getUpperBounds().getOnly().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(t.getUpperBounds().getOnly().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(27));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(8)
    public void testNonGenericTypeAnnotationReceiverTypeOnMethod() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        TypeDescription.Generic receiverType = describe(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE)).getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericVariableDefiningTest.FOO)).getOnly().getReceiverType();
        MatcherAssert.assertThat(receiverType, CoreMatchers.notNullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(receiverType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(receiverType.represents(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE)), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(0));
        MatcherAssert.assertThat(receiverType.getOwnerType(), CoreMatchers.nullValue(TypeDescription.Generic.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(8)
    public void testNonGenericTypeAnnotationReceiverTypeOnConstructor() throws Exception {
        TypeDescription.Generic receiverType = describe(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE)).getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getReceiverType();
        MatcherAssert.assertThat(receiverType, CoreMatchers.notNullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(receiverType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(receiverType.represents(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE)), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(receiverType.getOwnerType(), CoreMatchers.nullValue(TypeDescription.Generic.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(8)
    public void testNonGenericInnerTypeAnnotationReceiverTypeOnMethod() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        TypeDescription.Generic receiverType = describe(Class.forName((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.INNER)))).getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericVariableDefiningTest.FOO)).getOnly().getReceiverType();
        MatcherAssert.assertThat(receiverType, CoreMatchers.notNullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(receiverType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(receiverType.represents(Class.forName((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.INNER)))), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(1));
        MatcherAssert.assertThat(receiverType.getOwnerType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(receiverType.getOwnerType().represents(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE)), CoreMatchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(8)
    public void testNonGenericInnerTypeAnnotationReceiverTypeOnConstructor() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        TypeDescription.Generic receiverType = describe(Class.forName((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.INNER)))).getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getReceiverType();
        MatcherAssert.assertThat(receiverType, CoreMatchers.notNullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(receiverType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(receiverType.represents(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE)), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(2));
        MatcherAssert.assertThat(receiverType.getOwnerType(), CoreMatchers.nullValue(TypeDescription.Generic.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(8)
    public void testNonGenericNestedTypeAnnotationReceiverTypeOnMethod() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        TypeDescription.Generic receiverType = describe(Class.forName((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.NESTED)))).getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericVariableDefiningTest.FOO)).getOnly().getReceiverType();
        MatcherAssert.assertThat(receiverType, CoreMatchers.notNullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(receiverType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(receiverType.represents(Class.forName((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.NESTED)))), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(3));
        MatcherAssert.assertThat(receiverType.getOwnerType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(receiverType.getOwnerType().represents(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE)), CoreMatchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(8)
    public void testNonGenericNestedTypeAnnotationReceiverTypeOnConstructor() throws Exception {
        TypeDescription.Generic receiverType = describe(Class.forName((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.NESTED)))).getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getReceiverType();
        MatcherAssert.assertThat(receiverType, CoreMatchers.notNullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(receiverType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(receiverType.represents(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE)), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(receiverType.getOwnerType(), CoreMatchers.nullValue(TypeDescription.Generic.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(8)
    public void testGenericTypeAnnotationReceiverTypeOnMethod() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        TypeDescription.Generic receiverType = describe(Class.forName((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.GENERIC)))).getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericVariableDefiningTest.FOO)).getOnly().getReceiverType();
        MatcherAssert.assertThat(receiverType, CoreMatchers.notNullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(receiverType.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(receiverType.asErasure().represents(Class.forName((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.GENERIC)))), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(4));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericVariableDefiningTest.T));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(5));
        MatcherAssert.assertThat(receiverType.getOwnerType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(receiverType.getOwnerType().represents(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE)), CoreMatchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(8)
    public void testGenericTypeAnnotationReceiverTypeOnConstructor() throws Exception {
        TypeDescription.Generic receiverType = describe(Class.forName((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.GENERIC)))).getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getReceiverType();
        MatcherAssert.assertThat(receiverType, CoreMatchers.notNullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(receiverType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(receiverType.asErasure().represents(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE)), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(receiverType.getOwnerType(), CoreMatchers.nullValue(TypeDescription.Generic.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(8)
    public void testGenericInnerTypeAnnotationReceiverTypeOnMethod() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        TypeDescription.Generic receiverType = describe(Class.forName((((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.GENERIC)) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.INNER)))).getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericVariableDefiningTest.FOO)).getOnly().getReceiverType();
        MatcherAssert.assertThat(receiverType, CoreMatchers.notNullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(receiverType.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(receiverType.asErasure().represents(Class.forName((((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.GENERIC)) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.INNER)))), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(8));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericVariableDefiningTest.S));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(9));
        MatcherAssert.assertThat(receiverType.getOwnerType().getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(receiverType.getOwnerType().asErasure().represents(Class.forName((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.GENERIC)))), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getOwnerType().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(receiverType.getOwnerType().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getOwnerType().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(6));
        MatcherAssert.assertThat(receiverType.getOwnerType().getTypeArguments().getOnly().getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(receiverType.getOwnerType().getTypeArguments().getOnly().getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericVariableDefiningTest.T));
        MatcherAssert.assertThat(receiverType.getOwnerType().getTypeArguments().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(receiverType.getOwnerType().getTypeArguments().getOnly().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getOwnerType().getTypeArguments().getOnly().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(7));
        MatcherAssert.assertThat(receiverType.getOwnerType().getOwnerType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(receiverType.getOwnerType().getOwnerType().represents(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE)), CoreMatchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(8)
    public void testGenericInnerTypeAnnotationReceiverTypeOnConstructor() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        TypeDescription.Generic receiverType = describe(Class.forName((((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.GENERIC)) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.INNER)))).getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getReceiverType();
        MatcherAssert.assertThat(receiverType, CoreMatchers.notNullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(receiverType.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(receiverType.asErasure().represents(Class.forName((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.GENERIC)))), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(10));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getSort(), CoreMatchers.is(VARIABLE));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getSymbol(), CoreMatchers.is(AbstractTypeDescriptionGenericVariableDefiningTest.T));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(11));
        MatcherAssert.assertThat(receiverType.getOwnerType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(receiverType.getOwnerType().represents(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE)), CoreMatchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(8)
    public void testGenericNestedTypeAnnotationReceiverTypeOnMethod() throws Exception {
        Class<? extends Annotation> typeAnnotation = ((Class<? extends Annotation>) (Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.TYPE_ANNOTATION)));
        MethodDescription.InDefinedShape value = of(typeAnnotation).getDeclaredMethods().getOnly();
        TypeDescription.Generic receiverType = describe(Class.forName((((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.GENERIC)) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.NESTED)))).getDeclaredMethods().filter(ElementMatchers.named(AbstractTypeDescriptionGenericVariableDefiningTest.FOO)).getOnly().getReceiverType();
        MatcherAssert.assertThat(receiverType, CoreMatchers.notNullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(receiverType.getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(receiverType.asErasure().represents(Class.forName((((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.GENERIC)) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.NESTED)))), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(12));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getDeclaredAnnotations().isAnnotationPresent(typeAnnotation), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getTypeArguments().getOnly().getDeclaredAnnotations().ofType(typeAnnotation).getValue(value).resolve(Integer.class), CoreMatchers.is(13));
        MatcherAssert.assertThat(receiverType.getOwnerType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(receiverType.getOwnerType().represents(Class.forName((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.GENERIC)))), CoreMatchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    @JavaVersionRule.Enforce(8)
    public void testGenericNestedTypeAnnotationReceiverTypeOnConstructor() throws Exception {
        TypeDescription.Generic receiverType = describe(Class.forName((((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.GENERIC)) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.NESTED)))).getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly().getReceiverType();
        MatcherAssert.assertThat(receiverType, CoreMatchers.notNullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(receiverType.getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(receiverType.asErasure().represents(Class.forName((((AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE) + "$") + (AbstractTypeDescriptionGenericVariableDefiningTest.GENERIC)))), CoreMatchers.is(true));
        MatcherAssert.assertThat(receiverType.getDeclaredAnnotations().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(receiverType.getOwnerType().getSort(), CoreMatchers.is(NON_GENERIC));
        MatcherAssert.assertThat(receiverType.getOwnerType().represents(Class.forName(AbstractTypeDescriptionGenericVariableDefiningTest.RECEIVER_TYPE_SAMPLE)), CoreMatchers.is(true));
    }
}

