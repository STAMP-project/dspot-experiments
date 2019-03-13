package net.bytebuddy.dynamic.scaffold;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.description.type.TypeVariableToken;
import net.bytebuddy.dynamic.Transformer;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.matcher.ElementMatcher;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.LoadedTypeInitializer.NoOp.INSTANCE;


public class InstrumentedTypeFrozenTest {
    @Test
    public void testDelegation() throws Exception {
        for (Method method : TypeDescription.class.getDeclaredMethods()) {
            if ((((method.getParameterTypes().length) == 0) && (Modifier.isPublic(method.getModifiers()))) && (!(method.isSynthetic()))) {
                Assert.assertThat(method.invoke(new InstrumentedType.Frozen(TypeDescription.STRING, INSTANCE)), CoreMatchers.is(method.invoke(TypeDescription.STRING)));
            }
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testFieldToken() throws Exception {
        new InstrumentedType.Frozen(TypeDescription.STRING, INSTANCE).withField(Mockito.mock(FieldDescription.Token.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodToken() throws Exception {
        new InstrumentedType.Frozen(TypeDescription.STRING, INSTANCE).withMethod(Mockito.mock(MethodDescription.Token.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotation() throws Exception {
        new InstrumentedType.Frozen(TypeDescription.STRING, INSTANCE).withAnnotations(Collections.<AnnotationDescription>emptyList());
    }

    @Test(expected = IllegalStateException.class)
    public void testInitializer() throws Exception {
        new InstrumentedType.Frozen(TypeDescription.STRING, INSTANCE).withInitializer(Mockito.mock(ByteCodeAppender.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testWithTypeVariable() throws Exception {
        new InstrumentedType.Frozen(TypeDescription.STRING, INSTANCE).withTypeVariable(Mockito.mock(TypeVariableToken.class));
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testWithTypeVariables() throws Exception {
        new InstrumentedType.Frozen(TypeDescription.STRING, INSTANCE).withTypeVariables(Mockito.mock(ElementMatcher.class), Mockito.mock(Transformer.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testWithName() throws Exception {
        new InstrumentedType.Frozen(TypeDescription.STRING, INSTANCE).withName("foo");
    }

    @Test(expected = IllegalStateException.class)
    public void testWithDeclaringType() {
        withDeclaringType(Mockito.mock(TypeDescription.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testWithDeclaredType() {
        withDeclaredTypes(Mockito.mock(TypeList.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testWithEnclosingType() {
        withEnclosingType(Mockito.mock(TypeDescription.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testWithEnclosingMethod() {
        withEnclosingMethod(Mockito.mock(MethodDescription.InDefinedShape.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testWithNestHost() {
        withNestHost(Mockito.mock(TypeDescription.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testWithNestMember() {
        withNestMembers(Mockito.mock(TypeList.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testWithLocalClass() {
        withLocalClass(true);
    }

    @Test(expected = IllegalStateException.class)
    public void testWithAnonymousClass() {
        withAnonymousClass(true);
    }

    @Test
    public void testValidation() throws Exception {
        Assert.assertThat(new InstrumentedType.Frozen(TypeDescription.STRING, INSTANCE).validated(), CoreMatchers.is(TypeDescription.STRING));
    }

    @Test
    public void testTypeInitializer() throws Exception {
        Assert.assertThat(new InstrumentedType.Frozen(TypeDescription.STRING, INSTANCE).getTypeInitializer(), CoreMatchers.is(((TypeInitializer) (TypeInitializer.None.INSTANCE))));
    }

    @Test
    public void testLoadedTypeInitializer() throws Exception {
        LoadedTypeInitializer loadedTypeInitializer = Mockito.mock(LoadedTypeInitializer.class);
        Assert.assertThat(new InstrumentedType.Frozen(TypeDescription.STRING, loadedTypeInitializer).getLoadedTypeInitializer(), CoreMatchers.is(loadedTypeInitializer));
    }
}

