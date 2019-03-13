package net.bytebuddy.build;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.ClassFileVersion.ofThisVm;
import static net.bytebuddy.build.EntryPoint.Default.REBASE;
import static net.bytebuddy.build.EntryPoint.Default.REDEFINE;
import static net.bytebuddy.build.EntryPoint.Default.REDEFINE_LOCAL;
import static net.bytebuddy.implementation.Implementation.Context.Disabled.Factory.INSTANCE;


public class EntryPointDefaultTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private ByteBuddy byteBuddy;

    @Mock
    private ClassFileLocator classFileLocator;

    @Mock
    private MethodNameTransformer methodNameTransformer;

    @Mock
    private DynamicType.Builder<?> builder;

    @Mock
    private DynamicType.Builder<?> otherBuilder;

    @Test
    @SuppressWarnings("unchecked")
    public void testRebase() throws Exception {
        Assert.assertThat(byteBuddy(ofThisVm()), FieldByFieldComparison.hasPrototype(new ByteBuddy()));
        Mockito.when(byteBuddy.rebase(typeDescription, classFileLocator, methodNameTransformer)).thenReturn(((DynamicType.Builder) (builder)));
        Assert.assertThat(REBASE.transform(typeDescription, byteBuddy, classFileLocator, methodNameTransformer), FieldByFieldComparison.hasPrototype(((DynamicType.Builder) (builder))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRedefine() throws Exception {
        Assert.assertThat(byteBuddy(ofThisVm()), FieldByFieldComparison.hasPrototype(new ByteBuddy()));
        Mockito.when(byteBuddy.redefine(typeDescription, classFileLocator)).thenReturn(((DynamicType.Builder) (builder)));
        Assert.assertThat(REDEFINE.transform(typeDescription, byteBuddy, classFileLocator, methodNameTransformer), FieldByFieldComparison.hasPrototype(((DynamicType.Builder) (builder))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRedefineLocal() throws Exception {
        Assert.assertThat(byteBuddy(ofThisVm()), FieldByFieldComparison.hasPrototype(new ByteBuddy().with(INSTANCE)));
        Mockito.when(byteBuddy.redefine(typeDescription, classFileLocator)).thenReturn(((DynamicType.Builder) (builder)));
        Mockito.when(builder.ignoreAlso(FieldByFieldComparison.matchesPrototype(ElementMatchers.not(ElementMatchers.isDeclaredBy(typeDescription))))).thenReturn(((DynamicType.Builder) (otherBuilder)));
        Assert.assertThat(REDEFINE_LOCAL.transform(typeDescription, byteBuddy, classFileLocator, methodNameTransformer), FieldByFieldComparison.hasPrototype(((DynamicType.Builder) (otherBuilder))));
    }
}

