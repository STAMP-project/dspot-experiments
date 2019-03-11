package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.dynamic.scaffold.MethodGraph.Compiler.ForDeclaredMethods.INSTANCE;


public class MethodGraphCompilerForDeclaredMethodsTest {
    @Test
    public void testCompilationInvisible() throws Exception {
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        MethodDescription.InDefinedShape methodDescription = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(typeDescription.getDeclaredMethods()).thenReturn(new MethodList.Explicit<MethodDescription.InDefinedShape>(methodDescription));
        Mockito.when(methodDescription.isVirtual()).thenReturn(true);
        Mockito.when(methodDescription.isBridge()).thenReturn(false);
        Mockito.when(methodDescription.isVisibleTo(typeDescription)).thenReturn(false);
        MethodGraph.Linked methodGraph = INSTANCE.compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(0));
    }

    @Test
    public void testCompilationNonVirtual() throws Exception {
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        MethodDescription.InDefinedShape methodDescription = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(typeDescription.getDeclaredMethods()).thenReturn(new MethodList.Explicit<MethodDescription.InDefinedShape>(methodDescription));
        Mockito.when(methodDescription.isVirtual()).thenReturn(false);
        Mockito.when(methodDescription.isBridge()).thenReturn(false);
        Mockito.when(methodDescription.isVisibleTo(typeDescription)).thenReturn(true);
        MethodGraph.Linked methodGraph = INSTANCE.compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(0));
    }

    @Test
    public void testCompilationNonBridge() throws Exception {
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        MethodDescription.InDefinedShape methodDescription = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(typeDescription.getDeclaredMethods()).thenReturn(new MethodList.Explicit<MethodDescription.InDefinedShape>(methodDescription));
        Mockito.when(methodDescription.isVirtual()).thenReturn(true);
        Mockito.when(methodDescription.getModifiers()).thenReturn(Opcodes.ACC_BRIDGE);
        Mockito.when(methodDescription.isVisibleTo(typeDescription)).thenReturn(true);
        MethodGraph.Linked methodGraph = INSTANCE.compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(0));
    }

    @Test
    public void testCompilation() throws Exception {
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        MethodDescription.InDefinedShape methodDescription = Mockito.mock(MethodDescription.InDefinedShape.class);
        MethodDescription.SignatureToken token = Mockito.mock(MethodDescription.SignatureToken.class);
        Mockito.when(methodDescription.asSignatureToken()).thenReturn(token);
        Mockito.when(typeDescription.getDeclaredMethods()).thenReturn(new MethodList.Explicit<MethodDescription.InDefinedShape>(methodDescription));
        Mockito.when(methodDescription.isVirtual()).thenReturn(true);
        Mockito.when(methodDescription.isBridge()).thenReturn(false);
        Mockito.when(methodDescription.isVisibleTo(typeDescription)).thenReturn(true);
        MethodGraph.Linked methodGraph = INSTANCE.compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(methodGraph.listNodes().getOnly().getRepresentative(), CoreMatchers.is(((MethodDescription) (methodDescription))));
    }
}

