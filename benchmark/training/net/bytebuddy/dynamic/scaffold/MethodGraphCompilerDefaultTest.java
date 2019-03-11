package net.bytebuddy.dynamic.scaffold;


import java.util.Collections;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeVariableToken;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.description.type.TypeDescription.Generic.VOID;
import static net.bytebuddy.dynamic.scaffold.InstrumentedType.Default.<init>;
import static net.bytebuddy.dynamic.scaffold.MethodGraph.Compiler.Default.forJVMHierarchy;
import static net.bytebuddy.dynamic.scaffold.MethodGraph.Compiler.Default.forJavaHierarchy;
import static net.bytebuddy.dynamic.scaffold.MethodGraph.Node.Sort.AMBIGUOUS;
import static net.bytebuddy.dynamic.scaffold.MethodGraph.Node.Sort.RESOLVED;
import static net.bytebuddy.dynamic.scaffold.MethodGraph.Node.Sort.UNRESOLVED;
import static net.bytebuddy.dynamic.scaffold.MethodGraph.Node.Sort.VISIBLE;
import static net.bytebuddy.dynamic.scaffold.TypeInitializer.None.INSTANCE;


public class MethodGraphCompilerDefaultTest {
    private static final String TYPE_VARIABLE_INTERFACE_BRIDGE = "net.bytebuddy.test.precompiled.TypeVariableInterfaceBridge";

    private static final String RETURN_TYPE_INTERFACE_BRIDGE = "net.bytebuddy.test.precompiled.ReturnTypeInterfaceBridge";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    public void testTrivialJavaHierarchy() throws Exception {
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(TypeDescription.OBJECT);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()));
        MatcherAssert.assertThat(methodGraph.getSuperClassGraph().listNodes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(methodGraph.getInterfaceGraph(Mockito.mock(TypeDescription.class)).listNodes().size(), CoreMatchers.is(0));
        for (MethodDescription methodDescription : TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual())) {
            MethodGraph.Node node = methodGraph.locate(methodDescription.asSignatureToken());
            MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
            MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
            MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
            MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(methodDescription));
            MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(methodDescription.getVisibility()));
            MatcherAssert.assertThat(methodGraph.listNodes().contains(node), CoreMatchers.is(true));
        }
    }

    @Test
    public void testTrivialJVMHierarchy() throws Exception {
        MethodGraph.Linked methodGraph = forJVMHierarchy().compile(TypeDescription.OBJECT);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()));
        MatcherAssert.assertThat(methodGraph.getSuperClassGraph().listNodes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(methodGraph.getInterfaceGraph(Mockito.mock(TypeDescription.class)).listNodes().size(), CoreMatchers.is(0));
        for (MethodDescription methodDescription : TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual())) {
            MethodGraph.Node node = methodGraph.locate(methodDescription.asSignatureToken());
            MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
            MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
            MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
            MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(methodDescription));
            MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(methodDescription.getVisibility()));
            MatcherAssert.assertThat(methodGraph.listNodes().contains(node), CoreMatchers.is(true));
        }
    }

    @Test
    public void testSimpleClass() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.SimpleClass.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()));
        MatcherAssert.assertThat(methodGraph.getSuperClassGraph().listNodes().size(), CoreMatchers.is(TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()));
        MatcherAssert.assertThat(methodGraph.getInterfaceGraph(Mockito.mock(TypeDescription.class)).listNodes().size(), CoreMatchers.is(0));
        for (MethodDescription methodDescription : TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual())) {
            MethodGraph.Node node = methodGraph.locate(methodDescription.asSignatureToken());
            MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
            MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
            MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
            MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(methodDescription));
            MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(methodDescription.getVisibility()));
            MatcherAssert.assertThat(methodGraph.listNodes().contains(node), CoreMatchers.is(true));
        }
    }

    @Test
    public void testSimpleInterface() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.SimpleInterface.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(0));
    }

    @Test
    public void testClassInheritance() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.ClassBase.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription method = typeDescription.getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly();
        MethodGraph.Node node = methodGraph.locate(method.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(node.getMethodTypes().contains(method.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(method));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(method.getVisibility()));
        MatcherAssert.assertThat(methodGraph.listNodes().contains(node), CoreMatchers.is(true));
        MethodGraph.Node baseNode = methodGraph.getSuperClassGraph().locate(method.asSignatureToken());
        MatcherAssert.assertThat(node, CoreMatchers.not(baseNode));
        MatcherAssert.assertThat(typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.is(baseNode.getRepresentative())).getOnly(), CoreMatchers.is(baseNode.getRepresentative()));
    }

    @Test
    public void testInterfaceImplementation() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.InterfaceBase.InnerClass.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription method = typeDescription.getInterfaces().getOnly().getDeclaredMethods().getOnly();
        MethodGraph.Node node = methodGraph.locate(method.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(node.getMethodTypes().contains(method.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(method));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(method.getVisibility()));
        MatcherAssert.assertThat(methodGraph.listNodes().contains(node), CoreMatchers.is(true));
    }

    @Test
    public void testInterfaceExtension() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.InterfaceBase.InnerInterface.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription method = typeDescription.getInterfaces().getOnly().getDeclaredMethods().getOnly();
        MethodGraph.Node node = methodGraph.locate(method.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(node.getMethodTypes().contains(method.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(method));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(method.getVisibility()));
        MatcherAssert.assertThat(methodGraph.listNodes().contains(node), CoreMatchers.is(true));
    }

    @Test
    public void testInterfaceDuplicateInHierarchyImplementation() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.InterfaceBase.InterfaceDuplicate.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription method = typeDescription.getInterfaces().filter(ElementMatchers.is(MethodGraphCompilerDefaultTest.InterfaceBase.class)).getOnly().getDeclaredMethods().getOnly();
        MethodGraph.Node node = methodGraph.locate(method.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(node.getMethodTypes().contains(method.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(method));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(method.getVisibility()));
        MatcherAssert.assertThat(methodGraph.listNodes().contains(node), CoreMatchers.is(true));
    }

    @Test
    public void testClassAndInterfaceDominantInheritance() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.ClassAndInterfaceInheritance.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription method = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly();
        MethodGraph.Node node = methodGraph.locate(method.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(node.getMethodTypes().contains(method.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(method));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(method.getVisibility()));
        MethodGraph.Node baseNode = methodGraph.getInterfaceGraph(TypeDescription.ForLoadedType.of(MethodGraphCompilerDefaultTest.InterfaceBase.class)).locate(method.asSignatureToken());
        MatcherAssert.assertThat(node, CoreMatchers.not(baseNode));
        MatcherAssert.assertThat(baseNode.getRepresentative(), CoreMatchers.is(((MethodDescription) (typeDescription.getInterfaces().getOnly().getDeclaredMethods().getOnly()))));
    }

    @Test
    public void testMultipleAmbiguousClassInheritance() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.ClassTarget.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription first = typeDescription.getInterfaces().filter(ElementMatchers.erasure(MethodGraphCompilerDefaultTest.InterfaceBase.class)).getOnly().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly();
        MethodDescription second = typeDescription.getInterfaces().filter(ElementMatchers.erasure(MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.class)).getOnly().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly();
        MethodGraph.Node node = methodGraph.locate(first.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(AMBIGUOUS));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(node.getMethodTypes().contains(first.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(second.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(first));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.not(second));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(first.getVisibility()));
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(second.asSignatureToken())));
        MethodGraph.Node firstBaseNode = methodGraph.getInterfaceGraph(TypeDescription.ForLoadedType.of(MethodGraphCompilerDefaultTest.InterfaceBase.class)).locate(first.asSignatureToken());
        MatcherAssert.assertThat(node, CoreMatchers.not(firstBaseNode));
        MatcherAssert.assertThat(firstBaseNode.getRepresentative(), CoreMatchers.is(first));
        MethodGraph.Node secondBaseNode = methodGraph.getInterfaceGraph(TypeDescription.ForLoadedType.of(MethodGraphCompilerDefaultTest.InterfaceBase.class)).locate(second.asSignatureToken());
        MatcherAssert.assertThat(node, CoreMatchers.not(secondBaseNode));
        MatcherAssert.assertThat(secondBaseNode.getRepresentative(), CoreMatchers.is(first));
    }

    @Test
    public void testMultipleAmbiguousInterfaceInheritance() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.InterfaceTarget.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription first = typeDescription.getInterfaces().filter(ElementMatchers.erasure(MethodGraphCompilerDefaultTest.InterfaceBase.class)).getOnly().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly();
        MethodDescription second = typeDescription.getInterfaces().filter(ElementMatchers.erasure(MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.class)).getOnly().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly();
        MethodGraph.Node node = methodGraph.locate(first.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(AMBIGUOUS));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(node.getMethodTypes().contains(first.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(second.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(first));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.not(second));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(first.getVisibility()));
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(second.asSignatureToken())));
        MethodGraph.Node firstBaseNode = methodGraph.getInterfaceGraph(TypeDescription.ForLoadedType.of(MethodGraphCompilerDefaultTest.InterfaceBase.class)).locate(first.asSignatureToken());
        MatcherAssert.assertThat(node, CoreMatchers.not(firstBaseNode));
        MatcherAssert.assertThat(firstBaseNode.getRepresentative(), CoreMatchers.is(first));
        MethodGraph.Node secondBaseNode = methodGraph.getInterfaceGraph(TypeDescription.ForLoadedType.of(MethodGraphCompilerDefaultTest.InterfaceBase.class)).locate(second.asSignatureToken());
        MatcherAssert.assertThat(node, CoreMatchers.not(secondBaseNode));
        MatcherAssert.assertThat(secondBaseNode.getRepresentative(), CoreMatchers.is(first));
    }

    @Test
    public void testDominantClassInheritance() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.DominantClassTarget.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription methodDescription = TypeDescription.ForLoadedType.of(MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.DominantIntermediate.class).getDeclaredMethods().getOnly();
        MethodGraph.Node node = methodGraph.locate(methodDescription.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(methodDescription));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(methodDescription.getVisibility()));
    }

    @Test
    public void testDominantInterfaceInheritanceLeft() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.DominantInterfaceTargetLeft.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription methodDescription = TypeDescription.ForLoadedType.of(MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.DominantIntermediate.class).getDeclaredMethods().getOnly();
        MethodGraph.Node node = methodGraph.locate(methodDescription.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(methodDescription));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(methodDescription.getVisibility()));
    }

    @Test
    public void testDominantInterfaceInheritanceRight() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.DominantInterfaceTargetRight.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription methodDescription = TypeDescription.ForLoadedType.of(MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.DominantIntermediate.class).getDeclaredMethods().getOnly();
        MethodGraph.Node node = methodGraph.locate(methodDescription.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(methodDescription));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(methodDescription.getVisibility()));
    }

    @Test
    public void testNonDominantInterfaceInheritanceLeft() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.NonDominantTargetLeft.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription methodDescription = TypeDescription.ForLoadedType.of(MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.DominantIntermediate.class).getDeclaredMethods().getOnly();
        MethodGraph.Node node = methodGraph.locate(methodDescription.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(AMBIGUOUS));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(methodDescription));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(methodDescription.getVisibility()));
    }

    @Test
    public void testNonDominantInterfaceInheritanceRight() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.NonDominantTargetRight.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription methodDescription = TypeDescription.ForLoadedType.of(MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.DominantIntermediate.class).getDeclaredMethods().getOnly();
        MethodGraph.Node node = methodGraph.locate(methodDescription.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(AMBIGUOUS));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(methodDescription));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(methodDescription.getVisibility()));
    }

    @Test
    public void testGenericClassSingleEvolution() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.GenericClassBase.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription.SignatureToken token = typeDescription.getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly().asSignatureToken();
        MethodGraph.Node node = methodGraph.locate(token);
        MethodDescription.SignatureToken bridgeToken = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().asDefined().asSignatureToken();
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(bridgeToken)));
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(token.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(bridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testGenericClassMultipleEvolution() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.GenericClassBase.Intermediate.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription.SignatureToken token = typeDescription.getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly().asSignatureToken();
        MethodGraph.Node node = methodGraph.locate(token);
        MethodDescription.SignatureToken firstBridgeToken = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly().asDefined().asSignatureToken();
        MethodDescription.SignatureToken secondBridgeToken = typeDescription.getSuperClass().getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().asDefined().asSignatureToken();
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(firstBridgeToken)));
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(secondBridgeToken)));
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(node.getMethodTypes().contains(token.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(firstBridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(secondBridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testReturnTypeClassSingleEvolution() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.ReturnTypeClassBase.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription.SignatureToken token = typeDescription.getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly().asSignatureToken();
        MethodGraph.Node node = methodGraph.locate(token);
        MethodDescription.SignatureToken bridgeToken = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().asSignatureToken();
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(bridgeToken)));
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(token.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(bridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testReturnTypeClassMultipleEvolution() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.ReturnTypeClassBase.Intermediate.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription.SignatureToken token = typeDescription.getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly().asSignatureToken();
        MethodGraph.Node node = methodGraph.locate(token);
        MethodDescription.SignatureToken firstBridgeToken = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly().asDefined().asSignatureToken();
        MethodDescription.SignatureToken secondBridgeToken = typeDescription.getSuperClass().getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().asDefined().asSignatureToken();
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(firstBridgeToken)));
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(secondBridgeToken)));
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(node.getMethodTypes().contains(token.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(firstBridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(secondBridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testGenericReturnTypeClassSingleEvolution() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.GenericReturnClassBase.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription.SignatureToken token = typeDescription.getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly().asSignatureToken();
        MethodGraph.Node node = methodGraph.locate(token);
        MethodDescription.SignatureToken bridgeToken = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().asSignatureToken();
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(bridgeToken)));
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(token.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(bridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testGenericReturnTypeClassMultipleEvolution() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.GenericReturnClassBase.Intermediate.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription.SignatureToken token = typeDescription.getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly().asSignatureToken();
        MethodGraph.Node node = methodGraph.locate(token);
        MethodDescription.SignatureToken firstBridgeToken = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly().asDefined().asSignatureToken();
        MethodDescription.SignatureToken secondBridgeToken = typeDescription.getSuperClass().getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().asDefined().asSignatureToken();
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(firstBridgeToken)));
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(secondBridgeToken)));
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(node.getMethodTypes().contains(token.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(firstBridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(secondBridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testGenericInterfaceSingleEvolution() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.GenericInterfaceBase.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription.SignatureToken token = typeDescription.getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly().asSignatureToken();
        MethodGraph.Node node = methodGraph.locate(token);
        MethodDescription.SignatureToken bridgeToken = typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().asDefined().asSignatureToken();
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(bridgeToken)));
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(token.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(bridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testGenericInterfaceMultipleEvolution() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.GenericInterfaceBase.Intermediate.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription.SignatureToken token = typeDescription.getDeclaredMethods().filter(ElementMatchers.not(ElementMatchers.isBridge())).getOnly().asSignatureToken();
        MethodGraph.Node node = methodGraph.locate(token);
        MethodDescription.SignatureToken firstBridgeToken = typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.not(ElementMatchers.isBridge())).getOnly().asDefined().asSignatureToken();
        MethodDescription.SignatureToken secondBridgeToken = typeDescription.getInterfaces().getOnly().getInterfaces().getOnly().getDeclaredMethods().getOnly().asDefined().asSignatureToken();
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(firstBridgeToken)));
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(secondBridgeToken)));
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(node.getMethodTypes().contains(token.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(firstBridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(secondBridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testReturnTypeInterfaceSingleEvolution() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.ReturnTypeInterfaceBase.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription.SignatureToken token = typeDescription.getDeclaredMethods().filter(ElementMatchers.not(ElementMatchers.isBridge())).getOnly().asSignatureToken();
        MethodGraph.Node node = methodGraph.locate(token);
        MethodDescription.SignatureToken bridgeToken = typeDescription.getInterfaces().getOnly().getDeclaredMethods().getOnly().asSignatureToken();
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(bridgeToken)));
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(token.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(bridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testReturnTypeInterfaceMultipleEvolution() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.ReturnTypeInterfaceBase.Intermediate.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription.SignatureToken token = typeDescription.getDeclaredMethods().filter(ElementMatchers.not(ElementMatchers.isBridge())).getOnly().asSignatureToken();
        MethodGraph.Node node = methodGraph.locate(token);
        MethodDescription.SignatureToken firstBridgeToken = typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.not(ElementMatchers.isBridge())).getOnly().asSignatureToken();
        MethodDescription.SignatureToken secondBridgeToken = typeDescription.getInterfaces().getOnly().getInterfaces().getOnly().getDeclaredMethods().getOnly().asSignatureToken();
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(firstBridgeToken)));
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(node.getMethodTypes().contains(token.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(firstBridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(secondBridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testGenericWithReturnTypeClassSingleEvolution() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.GenericWithReturnTypeClassBase.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription.SignatureToken token = typeDescription.getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly().asSignatureToken();
        MethodGraph.Node node = methodGraph.locate(token);
        MethodDescription.SignatureToken bridgeToken = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().asDefined().asSignatureToken();
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(bridgeToken)));
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(token.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(bridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testGenericWithReturnTypeClassMultipleEvolution() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.GenericWithReturnTypeClassBase.Intermediate.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription.SignatureToken token = typeDescription.getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly().asSignatureToken();
        MethodGraph.Node node = methodGraph.locate(token);
        MethodDescription.SignatureToken firstBridgeToken = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly().asDefined().asSignatureToken();
        MethodDescription.SignatureToken secondBridgeToken = typeDescription.getSuperClass().getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().asDefined().asSignatureToken();
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(firstBridgeToken)));
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(secondBridgeToken)));
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(node.getMethodTypes().contains(token.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(firstBridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(secondBridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testGenericWithReturnTypeInterfaceSingleEvolution() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.GenericWithReturnTypeInterfaceBase.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription.SignatureToken token = typeDescription.getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly().asSignatureToken();
        MethodGraph.Node node = methodGraph.locate(token);
        MethodDescription.SignatureToken bridgeToken = typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().asDefined().asSignatureToken();
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(bridgeToken)));
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(token.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(bridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testGenericWithReturnTypeInterfaceMultipleEvolution() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.GenericWithReturnTypeInterfaceBase.Intermediate.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription.SignatureToken token = typeDescription.getDeclaredMethods().filter(ElementMatchers.not(ElementMatchers.isBridge())).getOnly().asSignatureToken();
        MethodGraph.Node node = methodGraph.locate(token);
        MethodDescription.SignatureToken firstBridgeToken = typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.not(ElementMatchers.isBridge())).getOnly().asDefined().asSignatureToken();
        MethodDescription.SignatureToken secondBridgeToken = typeDescription.getInterfaces().getOnly().getInterfaces().getOnly().getDeclaredMethods().getOnly().asDefined().asSignatureToken();
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(firstBridgeToken)));
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(secondBridgeToken)));
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(node.getMethodTypes().contains(token.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(firstBridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(secondBridgeToken.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testGenericNonOverriddenClassExtension() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.GenericNonOverriddenClassBase.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription methodDescription = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly();
        MethodGraph.Node node = methodGraph.locate(methodDescription.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(methodDescription));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asDefined().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node, FieldByFieldComparison.hasPrototype(methodGraph.getSuperClassGraph().locate(methodDescription.asSignatureToken())));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testGenericNonOverriddenInterfaceExtension() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.GenericNonOverriddenInterfaceBase.InnerClass.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription methodDescription = typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly();
        MethodGraph.Node node = methodGraph.locate(methodDescription.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(methodDescription));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asDefined().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node, FieldByFieldComparison.hasPrototype(methodGraph.getInterfaceGraph(TypeDescription.ForLoadedType.of(MethodGraphCompilerDefaultTest.GenericNonOverriddenInterfaceBase.class)).locate(methodDescription.asSignatureToken())));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testGenericNonOverriddenInterfaceImplementation() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.GenericNonOverriddenInterfaceBase.InnerInterface.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription methodDescription = typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly();
        MethodGraph.Node node = methodGraph.locate(methodDescription.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(methodDescription));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asDefined().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node, FieldByFieldComparison.hasPrototype(methodGraph.getInterfaceGraph(TypeDescription.ForLoadedType.of(MethodGraphCompilerDefaultTest.GenericNonOverriddenInterfaceBase.class)).locate(methodDescription.asSignatureToken())));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testTypeVariableInterfaceBridge() throws Exception {
        TypeDescription typeDescription = of(Class.forName(MethodGraphCompilerDefaultTest.TYPE_VARIABLE_INTERFACE_BRIDGE));
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription methodDescription = typeDescription.getDeclaredMethods().filter(ElementMatchers.takesArguments(String.class)).getOnly();
        MethodGraph.Node node = methodGraph.locate(methodDescription.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(methodDescription));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(typeDescription.getDeclaredMethods().filter(ElementMatchers.takesArguments(Object.class)).getOnly().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testReturnTypeInterfaceBridge() throws Exception {
        TypeDescription typeDescription = of(Class.forName(MethodGraphCompilerDefaultTest.RETURN_TYPE_INTERFACE_BRIDGE));
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription methodDescription = typeDescription.getDeclaredMethods().filter(ElementMatchers.returns(String.class)).getOnly();
        MethodGraph.Node node = methodGraph.locate(methodDescription.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(methodDescription));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(typeDescription.getDeclaredMethods().filter(ElementMatchers.returns(Object.class)).getOnly().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testDuplicateNameClass() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.DuplicateNameClass.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 2)));
        MethodDescription objectMethod = typeDescription.getDeclaredMethods().filter(ElementMatchers.takesArguments(Object.class)).getOnly();
        MethodGraph.Node objectNode = methodGraph.locate(objectMethod.asSignatureToken());
        MatcherAssert.assertThat(objectNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(objectNode.getRepresentative(), CoreMatchers.is(objectMethod));
        MatcherAssert.assertThat(objectNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(objectNode.getMethodTypes().contains(objectMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(objectNode.getVisibility(), CoreMatchers.is(objectMethod.getVisibility()));
        MethodDescription voidMethod = typeDescription.getDeclaredMethods().filter(ElementMatchers.takesArguments(Integer.class)).getOnly();
        MethodGraph.Node voidNode = methodGraph.locate(voidMethod.asSignatureToken());
        MatcherAssert.assertThat(voidNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(voidNode.getRepresentative(), CoreMatchers.is(voidMethod));
        MatcherAssert.assertThat(voidNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(voidNode.getMethodTypes().contains(voidMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(voidNode.getVisibility(), CoreMatchers.is(voidMethod.getVisibility()));
    }

    @Test
    public void testDuplicateNameClassExtension() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.DuplicateNameClass.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 3)));
        MethodDescription objectMethod = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.takesArguments(Object.class)).getOnly();
        MethodGraph.Node objectNode = methodGraph.locate(objectMethod.asSignatureToken());
        MatcherAssert.assertThat(objectNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(objectNode.getRepresentative(), CoreMatchers.is(objectMethod));
        MatcherAssert.assertThat(objectNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(objectNode.getMethodTypes().contains(objectMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(objectNode.getVisibility(), CoreMatchers.is(objectMethod.getVisibility()));
        MethodDescription integerMethod = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.takesArguments(Integer.class)).getOnly();
        MethodGraph.Node integerNode = methodGraph.locate(integerMethod.asSignatureToken());
        MatcherAssert.assertThat(integerNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(integerNode.getRepresentative(), CoreMatchers.is(integerMethod));
        MatcherAssert.assertThat(integerNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(integerNode.getMethodTypes().contains(integerMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(integerNode.getVisibility(), CoreMatchers.is(integerMethod.getVisibility()));
        MethodDescription voidMethod = typeDescription.getDeclaredMethods().filter(ElementMatchers.takesArguments(Void.class)).getOnly();
        MethodGraph.Node voidNode = methodGraph.locate(voidMethod.asSignatureToken());
        MatcherAssert.assertThat(voidNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(voidNode.getRepresentative(), CoreMatchers.is(voidMethod));
        MatcherAssert.assertThat(voidNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(voidNode.getMethodTypes().contains(voidMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(voidNode.getVisibility(), CoreMatchers.is(voidMethod.getVisibility()));
    }

    @Test
    public void testDuplicateNameInterface() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.DuplicateNameInterface.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(2));
        MethodDescription objectMethod = typeDescription.getDeclaredMethods().filter(ElementMatchers.takesArguments(Object.class)).getOnly();
        MethodGraph.Node objectNode = methodGraph.locate(objectMethod.asSignatureToken());
        MatcherAssert.assertThat(objectNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(objectNode.getRepresentative(), CoreMatchers.is(objectMethod));
        MatcherAssert.assertThat(objectNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(objectNode.getMethodTypes().contains(objectMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(objectNode.getVisibility(), CoreMatchers.is(objectMethod.getVisibility()));
        MethodDescription voidMethod = typeDescription.getDeclaredMethods().filter(ElementMatchers.takesArguments(Integer.class)).getOnly();
        MethodGraph.Node voidNode = methodGraph.locate(voidMethod.asSignatureToken());
        MatcherAssert.assertThat(voidNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(voidNode.getRepresentative(), CoreMatchers.is(voidMethod));
        MatcherAssert.assertThat(voidNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(voidNode.getMethodTypes().contains(voidMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(voidNode.getVisibility(), CoreMatchers.is(voidMethod.getVisibility()));
    }

    @Test
    public void testDuplicateNameInterfaceImplementation() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.DuplicateNameInterface.InnerClass.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 3)));
        MethodDescription objectMethod = typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.takesArguments(Object.class)).getOnly();
        MethodGraph.Node objectNode = methodGraph.locate(objectMethod.asSignatureToken());
        MatcherAssert.assertThat(objectNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(objectNode.getRepresentative(), CoreMatchers.is(objectMethod));
        MatcherAssert.assertThat(objectNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(objectNode.getMethodTypes().contains(objectMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(objectMethod.getVisibility(), CoreMatchers.is(objectMethod.getVisibility()));
        MethodDescription integerMethod = typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.takesArguments(Integer.class)).getOnly();
        MethodGraph.Node integerNode = methodGraph.locate(integerMethod.asSignatureToken());
        MatcherAssert.assertThat(integerNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(integerNode.getRepresentative(), CoreMatchers.is(integerMethod));
        MatcherAssert.assertThat(integerNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(integerNode.getMethodTypes().contains(integerMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(integerNode.getVisibility(), CoreMatchers.is(integerMethod.getVisibility()));
        MethodDescription voidMethod = typeDescription.getDeclaredMethods().filter(ElementMatchers.takesArguments(Void.class)).getOnly();
        MethodGraph.Node voidNode = methodGraph.locate(voidMethod.asSignatureToken());
        MatcherAssert.assertThat(voidNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(voidNode.getRepresentative(), CoreMatchers.is(voidMethod));
        MatcherAssert.assertThat(voidNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(voidNode.getMethodTypes().contains(voidMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(voidNode.getVisibility(), CoreMatchers.is(voidMethod.getVisibility()));
    }

    @Test
    public void testDuplicateNameInterfaceExtension() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.DuplicateNameInterface.InnerInterface.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(3));
        MethodDescription objectMethod = typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.takesArguments(Object.class)).getOnly();
        MethodGraph.Node objectNode = methodGraph.locate(objectMethod.asSignatureToken());
        MatcherAssert.assertThat(objectNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(objectNode.getRepresentative(), CoreMatchers.is(objectMethod));
        MatcherAssert.assertThat(objectNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(objectNode.getMethodTypes().contains(objectMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(objectNode.getVisibility(), CoreMatchers.is(objectMethod.getVisibility()));
        MethodDescription integerMethod = typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.takesArguments(Integer.class)).getOnly();
        MethodGraph.Node integerNode = methodGraph.locate(integerMethod.asSignatureToken());
        MatcherAssert.assertThat(integerNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(integerNode.getRepresentative(), CoreMatchers.is(integerMethod));
        MatcherAssert.assertThat(integerNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(integerNode.getMethodTypes().contains(integerMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(integerMethod.getVisibility(), CoreMatchers.is(integerNode.getVisibility()));
        MethodDescription voidMethod = typeDescription.getDeclaredMethods().filter(ElementMatchers.takesArguments(Void.class)).getOnly();
        MethodGraph.Node voidNode = methodGraph.locate(voidMethod.asSignatureToken());
        MatcherAssert.assertThat(voidNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(voidNode.getRepresentative(), CoreMatchers.is(voidMethod));
        MatcherAssert.assertThat(voidNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(voidNode.getMethodTypes().contains(voidMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(voidNode.getVisibility(), CoreMatchers.is(voidMethod.getVisibility()));
    }

    @Test
    public void testDuplicateNameGenericClass() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.DuplicateNameGenericClass.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 2)));
        MethodDescription objectMethod = typeDescription.getDeclaredMethods().filter(ElementMatchers.takesArguments(Object.class)).getOnly();
        MethodGraph.Node objectNode = methodGraph.locate(objectMethod.asSignatureToken());
        MatcherAssert.assertThat(objectNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(objectNode.getRepresentative(), CoreMatchers.is(objectMethod));
        MatcherAssert.assertThat(objectNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(objectNode.getMethodTypes().contains(objectMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(objectNode.getVisibility(), CoreMatchers.is(objectMethod.getVisibility()));
        MethodDescription voidMethod = typeDescription.getDeclaredMethods().filter(ElementMatchers.takesArguments(Integer.class)).getOnly();
        MethodGraph.Node voidNode = methodGraph.locate(voidMethod.asSignatureToken());
        MatcherAssert.assertThat(voidNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(voidNode.getRepresentative(), CoreMatchers.is(voidMethod));
        MatcherAssert.assertThat(voidNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(voidNode.getMethodTypes().contains(voidMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(voidNode.getVisibility(), CoreMatchers.is(voidMethod.getVisibility()));
    }

    @Test
    public void testDuplicateNameGenericClassExtension() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.DuplicateNameGenericClass.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 3)));
        MethodDescription objectMethod = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.takesArguments(String.class)).getOnly();
        MethodGraph.Node objectNode = methodGraph.locate(objectMethod.asSignatureToken());
        MatcherAssert.assertThat(objectNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(objectNode.getRepresentative(), CoreMatchers.is(objectMethod));
        MatcherAssert.assertThat(objectNode.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(objectNode.getMethodTypes().contains(objectMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(objectNode.getMethodTypes().contains(objectMethod.asDefined().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(objectNode.getVisibility(), CoreMatchers.is(objectMethod.getVisibility()));
        MethodDescription integerMethod = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.takesArguments(Integer.class)).getOnly();
        MethodGraph.Node integerNode = methodGraph.locate(integerMethod.asSignatureToken());
        MatcherAssert.assertThat(integerNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(integerNode.getRepresentative(), CoreMatchers.is(integerMethod));
        MatcherAssert.assertThat(integerNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(integerNode.getMethodTypes().contains(integerMethod.asTypeToken()), CoreMatchers.is(true));
        MethodDescription voidMethod = typeDescription.getDeclaredMethods().filter(ElementMatchers.takesArguments(Void.class)).getOnly();
        MethodGraph.Node voidNode = methodGraph.locate(voidMethod.asSignatureToken());
        MatcherAssert.assertThat(voidNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(voidNode.getRepresentative(), CoreMatchers.is(voidMethod));
        MatcherAssert.assertThat(voidNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(voidNode.getMethodTypes().contains(voidMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(voidNode.getVisibility(), CoreMatchers.is(voidMethod.getVisibility()));
    }

    @Test
    public void testDuplicateNameGenericInterface() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.DuplicateNameGenericInterface.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(2));
        MethodDescription objectMethod = typeDescription.getDeclaredMethods().filter(ElementMatchers.takesArguments(Object.class)).getOnly();
        MethodGraph.Node objectNode = methodGraph.locate(objectMethod.asSignatureToken());
        MatcherAssert.assertThat(objectNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(objectNode.getRepresentative(), CoreMatchers.is(objectMethod));
        MatcherAssert.assertThat(objectNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(objectNode.getMethodTypes().contains(objectMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(objectNode.getVisibility(), CoreMatchers.is(objectMethod.getVisibility()));
        MethodDescription voidMethod = typeDescription.getDeclaredMethods().filter(ElementMatchers.takesArguments(Integer.class)).getOnly();
        MethodGraph.Node voidNode = methodGraph.locate(voidMethod.asSignatureToken());
        MatcherAssert.assertThat(voidNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(voidNode.getRepresentative(), CoreMatchers.is(voidMethod));
        MatcherAssert.assertThat(voidNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(voidNode.getMethodTypes().contains(voidMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(voidNode.getVisibility(), CoreMatchers.is(voidMethod.getVisibility()));
    }

    @Test
    public void testDuplicateNameGenericInterfaceImplementation() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.DuplicateNameGenericInterface.InnerClass.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 3)));
        MethodDescription objectMethod = typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.takesArguments(String.class)).getOnly();
        MethodGraph.Node objectNode = methodGraph.locate(objectMethod.asSignatureToken());
        MatcherAssert.assertThat(objectNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(objectNode.getRepresentative(), CoreMatchers.is(objectMethod));
        MatcherAssert.assertThat(objectNode.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(objectNode.getMethodTypes().contains(objectMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(objectNode.getMethodTypes().contains(objectMethod.asDefined().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(objectNode.getVisibility(), CoreMatchers.is(objectMethod.getVisibility()));
        MethodDescription integerMethod = typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.takesArguments(Integer.class)).getOnly();
        MethodGraph.Node integerNode = methodGraph.locate(integerMethod.asSignatureToken());
        MatcherAssert.assertThat(integerNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(integerNode.getRepresentative(), CoreMatchers.is(integerMethod));
        MatcherAssert.assertThat(integerNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(integerNode.getMethodTypes().contains(integerMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(integerNode.getMethodTypes().contains(integerMethod.asDefined().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(integerNode.getVisibility(), CoreMatchers.is(integerMethod.getVisibility()));
        MethodDescription voidMethod = typeDescription.getDeclaredMethods().filter(ElementMatchers.takesArguments(Void.class)).getOnly();
        MethodGraph.Node voidNode = methodGraph.locate(voidMethod.asSignatureToken());
        MatcherAssert.assertThat(voidNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(voidNode.getRepresentative(), CoreMatchers.is(voidMethod));
        MatcherAssert.assertThat(voidNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(voidNode.getMethodTypes().contains(voidMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(voidNode.getVisibility(), CoreMatchers.is(voidMethod.getVisibility()));
    }

    @Test
    public void testDuplicateNameGenericInterfaceExtension() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.DuplicateNameGenericInterface.InnerInterface.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(3));
        MethodDescription objectMethod = typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.takesArguments(String.class)).getOnly();
        MethodGraph.Node objectNode = methodGraph.locate(objectMethod.asSignatureToken());
        MatcherAssert.assertThat(objectNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(objectNode.getRepresentative(), CoreMatchers.is(objectMethod));
        MatcherAssert.assertThat(objectNode.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(objectNode.getMethodTypes().contains(objectMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(objectNode.getMethodTypes().contains(objectMethod.asDefined().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(objectNode.getVisibility(), CoreMatchers.is(objectMethod.getVisibility()));
        MethodDescription integerMethod = typeDescription.getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.takesArguments(Integer.class)).getOnly();
        MethodGraph.Node integerNode = methodGraph.locate(integerMethod.asSignatureToken());
        MatcherAssert.assertThat(integerNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(integerNode.getRepresentative(), CoreMatchers.is(integerMethod));
        MatcherAssert.assertThat(integerNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(integerNode.getMethodTypes().contains(integerMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(integerNode.getMethodTypes().contains(integerMethod.asDefined().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(integerNode.getVisibility(), CoreMatchers.is(integerMethod.getVisibility()));
        MethodDescription voidMethod = typeDescription.getDeclaredMethods().filter(ElementMatchers.takesArguments(Void.class)).getOnly();
        MethodGraph.Node voidNode = methodGraph.locate(voidMethod.asSignatureToken());
        MatcherAssert.assertThat(voidNode.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(voidNode.getRepresentative(), CoreMatchers.is(voidMethod));
        MatcherAssert.assertThat(voidNode.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(voidNode.getMethodTypes().contains(voidMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(voidNode.getVisibility(), CoreMatchers.is(voidMethod.getVisibility()));
    }

    @Test
    public void testVisibilityBridge() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.VisibilityBridgeTarget.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription methodDescription = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly();
        MethodGraph.Node node = methodGraph.locate(methodDescription.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(VISIBLE));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(methodDescription));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(methodDescription.getVisibility()));
    }

    @Test
    public void testGenericVisibilityBridge() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.GenericVisibilityBridgeTarget.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription methodDescription = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly();
        MethodDescription.SignatureToken bridgeToken = typeDescription.getSuperClass().getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly().asSignatureToken();
        MethodGraph.Node node = methodGraph.locate(methodDescription.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(VISIBLE));
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(bridgeToken)));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asDefined().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(methodDescription));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(methodDescription.getVisibility()));
    }

    @Test
    public void testMethodClassConvergence() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.MethodClassConvergence.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription methodDescription = typeDescription.getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly();
        MethodDescription genericMethod = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.definedMethod(ElementMatchers.takesArguments(Object.class)))).getOnly();
        MethodDescription nonGenericMethod = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.definedMethod(ElementMatchers.takesArguments(Void.class)))).getOnly();
        MethodGraph.Node node = methodGraph.locate(methodDescription.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(genericMethod.asDefined().asSignatureToken())));
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(nonGenericMethod.asDefined().asSignatureToken())));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(methodDescription.asDefined().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(methodDescription));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(methodDescription.getVisibility()));
        MethodGraph superGraph = methodGraph.getSuperClassGraph();
        MethodGraph.Node superNode = superGraph.locate(methodDescription.asSignatureToken());
        MatcherAssert.assertThat(superNode.getSort(), CoreMatchers.is(AMBIGUOUS));
        MatcherAssert.assertThat(superNode.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(superNode.getMethodTypes().contains(methodDescription.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(superNode.getMethodTypes().contains(methodDescription.asDefined().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(superNode.getRepresentative(), CoreMatchers.is(nonGenericMethod));
        MatcherAssert.assertThat(superNode.getRepresentative(), CoreMatchers.is(genericMethod));
        MatcherAssert.assertThat(superNode.getVisibility(), CoreMatchers.is(methodDescription.getVisibility()));
    }

    @Test
    public void testMethodInterfaceConvergence() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.MethodInterfaceConvergenceTarget.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription genericMethod = typeDescription.getInterfaces().filter(ElementMatchers.erasure(MethodGraphCompilerDefaultTest.MethodInterfaceConvergenceFirstBase.class)).getOnly().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly();
        MethodDescription nonGenericMethod = typeDescription.getInterfaces().filter(ElementMatchers.erasure(MethodGraphCompilerDefaultTest.MethodInterfaceConvergenceSecondBase.class)).getOnly().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly();
        MatcherAssert.assertThat(methodGraph.getSuperClassGraph().locate(genericMethod.asSignatureToken()).getSort(), CoreMatchers.is(UNRESOLVED));
        MatcherAssert.assertThat(methodGraph.getSuperClassGraph().locate(nonGenericMethod.asSignatureToken()).getSort(), CoreMatchers.is(UNRESOLVED));
        MethodGraph.Node node = methodGraph.locate(genericMethod.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(AMBIGUOUS));
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(genericMethod.asDefined().asSignatureToken())));
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(nonGenericMethod.asDefined().asSignatureToken())));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(genericMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(genericMethod.asDefined().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(genericMethod));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.not(nonGenericMethod));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(genericMethod.getVisibility()));
    }

    @Test
    public void testMethodConvergenceVisibilityTarget() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.MethodConvergenceVisibilityBridgeTarget.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription genericMethod = typeDescription.getSuperClass().getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.definedMethod(ElementMatchers.takesArguments(Object.class)))).getOnly();
        MethodDescription nonGenericMethod = typeDescription.getSuperClass().getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.definedMethod(ElementMatchers.takesArguments(Void.class)))).getOnly();
        MethodGraph.Node node = methodGraph.locate(genericMethod.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(VISIBLE));
        MatcherAssert.assertThat(node, CoreMatchers.is(methodGraph.locate(nonGenericMethod.asSignatureToken())));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(genericMethod.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(genericMethod.asDefined().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getRepresentative(), CoreMatchers.is(((MethodDescription) (typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly()))));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(genericMethod.getVisibility()));
    }

    @Test
    public void testDiamondInheritanceClass() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.GenericDiamondClassBase.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription diamondOverride = typeDescription.getInterfaces().getOnly().getDeclaredMethods().getOnly();
        MethodDescription explicitOverride = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isVirtual()).getOnly();
        MethodGraph.Node node = methodGraph.locate(diamondOverride.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(methodGraph.locate(explicitOverride.asDefined().asSignatureToken()), CoreMatchers.is(node));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(diamondOverride.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(explicitOverride.asDefined().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(explicitOverride.getVisibility()));
    }

    @Test
    public void testDiamondInheritanceInterface() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.GenericDiamondInterfaceBase.Inner.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is(1));
        MethodDescription diamondOverride = typeDescription.getInterfaces().get(0).getDeclaredMethods().getOnly();
        MethodDescription explicitOverride = typeDescription.getInterfaces().get(1).getDeclaredMethods().getOnly();
        MethodGraph.Node node = methodGraph.locate(diamondOverride.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(AMBIGUOUS));
        MatcherAssert.assertThat(methodGraph.locate(explicitOverride.asDefined().asSignatureToken()), CoreMatchers.is(node));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(diamondOverride.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(explicitOverride.asDefined().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(explicitOverride.getVisibility()));
    }

    @Test
    public void testVisibilityExtension() throws Exception {
        TypeDescription typeDescription = new InstrumentedType.Default("foo", Opcodes.ACC_PUBLIC, TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(MethodGraphCompilerDefaultTest.VisibilityExtension.Base.class), Collections.<TypeVariableToken>emptyList(), Collections.<TypeDescription.Generic>singletonList(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(MethodGraphCompilerDefaultTest.VisibilityExtension.class)), Collections.<FieldDescription.Token>emptyList(), Collections.<MethodDescription.Token>emptyList(), Collections.<AnnotationDescription>emptyList(), INSTANCE, LoadedTypeInitializer.NoOp.INSTANCE, TypeDescription.UNDEFINED, MethodDescription.UNDEFINED, TypeDescription.UNDEFINED, Collections.<TypeDescription>emptyList(), false, false, TargetType.DESCRIPTION, Collections.<TypeDescription>emptyList());
        MethodDescription.SignatureToken signatureToken = new MethodDescription.SignatureToken("foo", TypeDescription.ForLoadedType.of(void.class), Collections.<TypeDescription>emptyList());
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is((1 + (TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()))));
        MethodGraph.Node node = methodGraph.locate(signatureToken);
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getRepresentative().asSignatureToken(), CoreMatchers.is(signatureToken));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(node.getMethodTypes(), CoreMatchers.hasItem(signatureToken.asTypeToken()));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PUBLIC));
    }

    @Test
    public void testOrphanedBridge() throws Exception {
        MethodDescription.SignatureToken bridgeMethod = new MethodDescription.SignatureToken("foo", TypeDescription.VOID, Collections.<TypeDescription>emptyList());
        TypeDescription typeDescription = new InstrumentedType.Default("foo", Opcodes.ACC_PUBLIC, OBJECT, Collections.<TypeVariableToken>emptyList(), Collections.<TypeDescription.Generic>emptyList(), Collections.<FieldDescription.Token>emptyList(), Collections.singletonList(new MethodDescription.Token("foo", Opcodes.ACC_BRIDGE, VOID, Collections.<TypeDescription.Generic>emptyList())), Collections.<AnnotationDescription>emptyList(), INSTANCE, LoadedTypeInitializer.NoOp.INSTANCE, TypeDescription.UNDEFINED, MethodDescription.UNDEFINED, TypeDescription.UNDEFINED, Collections.<TypeDescription>emptyList(), false, false, TargetType.DESCRIPTION, Collections.<TypeDescription>emptyList());
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.listNodes().size(), CoreMatchers.is((1 + (TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()))));
        MethodGraph.Node node = methodGraph.locate(bridgeMethod);
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(node.getRepresentative().asSignatureToken(), CoreMatchers.is(bridgeMethod));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(node.getMethodTypes(), CoreMatchers.hasItem(bridgeMethod.asTypeToken()));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(Visibility.PACKAGE_PRIVATE));
    }

    @Test
    public void testRawType() throws Exception {
        TypeDescription typeDescription = of(MethodGraphCompilerDefaultTest.RawType.Raw.class);
        MethodGraph.Linked methodGraph = forJavaHierarchy().compile(typeDescription);
        MatcherAssert.assertThat(methodGraph.getSuperClassGraph().listNodes().size(), CoreMatchers.is(((TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isVirtual()).size()) + 1)));
        MethodDescription method = typeDescription.getSuperClass().getDeclaredMethods().filter(ElementMatchers.isMethod().and(ElementMatchers.not(ElementMatchers.isBridge()))).getOnly();
        MethodGraph.Node node = methodGraph.locate(method.asSignatureToken());
        MatcherAssert.assertThat(node.getSort(), CoreMatchers.is(RESOLVED));
        MatcherAssert.assertThat(methodGraph.locate(method.asDefined().asSignatureToken()), CoreMatchers.is(node));
        MatcherAssert.assertThat(node.getMethodTypes().size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(node.getMethodTypes().contains(method.asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getMethodTypes().contains(method.asDefined().asTypeToken()), CoreMatchers.is(true));
        MatcherAssert.assertThat(node.getVisibility(), CoreMatchers.is(method.getVisibility()));
    }

    /* empty */
    public interface SimpleInterface {}

    public interface InterfaceBase {
        void foo();

        /* empty */
        interface InnerInterface extends MethodGraphCompilerDefaultTest.InterfaceBase {}

        /* empty */
        abstract class InnerClass implements MethodGraphCompilerDefaultTest.InterfaceBase {}

        /* empty */
        interface InterfaceDuplicate extends MethodGraphCompilerDefaultTest.InterfaceBase , MethodGraphCompilerDefaultTest.InterfaceBase.InnerInterface {}
    }

    public interface AmbiguousInterfaceBase {
        void foo();

        /* empty */
        interface InterfaceTarget extends MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase , MethodGraphCompilerDefaultTest.InterfaceBase {}

        interface DominantIntermediate extends MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase , MethodGraphCompilerDefaultTest.InterfaceBase {
            void foo();
        }

        /* empty */
        interface DominantInterfaceTargetLeft extends MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.DominantIntermediate , MethodGraphCompilerDefaultTest.InterfaceBase {}

        /* empty */
        interface DominantInterfaceTargetRight extends MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.DominantIntermediate , MethodGraphCompilerDefaultTest.InterfaceBase {}

        interface NonDominantAmbiguous {
            void foo();
        }

        /* empty */
        interface NonDominantIntermediateLeft extends MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.NonDominantAmbiguous , MethodGraphCompilerDefaultTest.InterfaceBase {}

        /* empty */
        interface NonDominantIntermediateRight extends MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.NonDominantAmbiguous , MethodGraphCompilerDefaultTest.InterfaceBase {}

        /* empty */
        interface NonDominantTargetLeft extends MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.DominantIntermediate , MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.NonDominantIntermediateLeft {}

        /* empty */
        interface NonDominantTargetRight extends MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.DominantIntermediate , MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.NonDominantIntermediateRight {}

        /* empty */
        abstract class ClassTarget implements MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase , MethodGraphCompilerDefaultTest.InterfaceBase {}

        /* empty */
        abstract class DominantClassBase implements MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.DominantIntermediate {}

        /* empty */
        abstract class DominantClassTarget extends MethodGraphCompilerDefaultTest.AmbiguousInterfaceBase.DominantClassBase implements MethodGraphCompilerDefaultTest.InterfaceBase {}
    }

    public interface GenericInterfaceBase<T> {
        void foo(T t);

        interface Inner extends MethodGraphCompilerDefaultTest.GenericInterfaceBase<Void> {
            void foo(Void t);
        }

        interface Intermediate<T extends Number> extends MethodGraphCompilerDefaultTest.GenericInterfaceBase<T> {
            void foo(T t);

            interface Inner extends MethodGraphCompilerDefaultTest.GenericInterfaceBase.Intermediate<Integer> {
                void foo(Integer t);
            }
        }
    }

    public interface ReturnTypeInterfaceBase {
        Object foo();

        interface Inner extends MethodGraphCompilerDefaultTest.ReturnTypeInterfaceBase {
            Void foo();
        }

        interface Intermediate extends MethodGraphCompilerDefaultTest.ReturnTypeInterfaceBase {
            Number foo();

            interface Inner extends MethodGraphCompilerDefaultTest.ReturnTypeInterfaceBase.Intermediate {
                Integer foo();
            }
        }
    }

    public interface GenericWithReturnTypeInterfaceBase<T> {
        Object foo(T t);

        interface Inner extends MethodGraphCompilerDefaultTest.GenericWithReturnTypeInterfaceBase<Void> {
            Void foo(Void t);
        }

        interface Intermediate<T extends Number> extends MethodGraphCompilerDefaultTest.GenericWithReturnTypeInterfaceBase<T> {
            Number foo(T t);

            interface Inner extends MethodGraphCompilerDefaultTest.GenericWithReturnTypeInterfaceBase.Intermediate<Integer> {
                Integer foo(Integer t);
            }
        }
    }

    public interface GenericNonOverriddenInterfaceBase<T> {
        T foo(T t);

        /* empty */
        interface InnerInterface extends MethodGraphCompilerDefaultTest.GenericNonOverriddenInterfaceBase<Void> {}

        /* empty */
        abstract class InnerClass implements MethodGraphCompilerDefaultTest.GenericNonOverriddenInterfaceBase<Void> {}
    }

    public interface DuplicateNameInterface {
        void foo(Object o);

        void foo(Integer o);

        interface InnerInterface extends MethodGraphCompilerDefaultTest.DuplicateNameInterface {
            void foo(Void o);
        }

        abstract class InnerClass implements MethodGraphCompilerDefaultTest.DuplicateNameInterface {
            public abstract void foo(Void o);
        }
    }

    public interface DuplicateNameGenericInterface<T> {
        void foo(T o);

        void foo(Integer o);

        interface InnerInterface extends MethodGraphCompilerDefaultTest.DuplicateNameGenericInterface<String> {
            void foo(Void o);
        }

        @SuppressWarnings("unused")
        abstract class InnerClass implements MethodGraphCompilerDefaultTest.DuplicateNameGenericInterface<String> {
            public abstract void foo(Void o);
        }
    }

    public interface MethodInterfaceConvergenceFirstBase<T> {
        T foo();
    }

    public interface MethodInterfaceConvergenceSecondBase {
        Void foo();
    }

    /* empty */
    public interface MethodInterfaceConvergenceTarget extends MethodGraphCompilerDefaultTest.MethodInterfaceConvergenceFirstBase<Void> , MethodGraphCompilerDefaultTest.MethodInterfaceConvergenceSecondBase {}

    /* empty */
    public static class SimpleClass {}

    public static class ClassBase {
        public void foo() {
            /* empty */
        }

        static class Inner extends MethodGraphCompilerDefaultTest.ClassBase {
            public void foo() {
                /* empty */
            }
        }
    }

    /* empty */
    public static class ClassAndInterfaceInheritance extends MethodGraphCompilerDefaultTest.ClassBase implements MethodGraphCompilerDefaultTest.InterfaceBase {}

    public static class GenericClassBase<T> {
        public void foo(T t) {
            /* empty */
        }

        public static class Inner extends MethodGraphCompilerDefaultTest.GenericClassBase<Void> {
            public void foo(Void t) {
                /* empty */
            }
        }

        public static class Intermediate<T extends Number> extends MethodGraphCompilerDefaultTest.GenericClassBase<T> {
            public void foo(T t) {
                /* empty */
            }

            public static class Inner extends MethodGraphCompilerDefaultTest.GenericClassBase.Intermediate<Integer> {
                public void foo(Integer t) {
                    /* empty */
                }
            }
        }
    }

    public static class GenericReturnClassBase<T> {
        public T foo() {
            return null;
        }

        public static class Inner extends MethodGraphCompilerDefaultTest.GenericReturnClassBase<Void> {
            public Void foo() {
                return null;
            }
        }

        public static class Intermediate<T extends Number> extends MethodGraphCompilerDefaultTest.GenericReturnClassBase<T> {
            public T foo() {
                return null;
            }

            public static class Inner extends MethodGraphCompilerDefaultTest.GenericReturnClassBase.Intermediate<Integer> {
                public Integer foo() {
                    return null;
                }
            }
        }
    }

    public static class ReturnTypeClassBase {
        public Object foo() {
            return null;
        }

        public static class Inner extends MethodGraphCompilerDefaultTest.ReturnTypeClassBase {
            public Void foo() {
                return null;
            }
        }

        public static class Intermediate extends MethodGraphCompilerDefaultTest.ReturnTypeClassBase {
            public Number foo() {
                return null;
            }

            public static class Inner extends MethodGraphCompilerDefaultTest.ReturnTypeClassBase.Intermediate {
                public Integer foo() {
                    return null;
                }
            }
        }
    }

    public static class GenericWithReturnTypeClassBase<T> {
        public Object foo(T t) {
            return null;
        }

        public static class Inner extends MethodGraphCompilerDefaultTest.GenericWithReturnTypeClassBase<Void> {
            public Void foo(Void t) {
                return null;
            }
        }

        public static class Intermediate<T extends Number> extends MethodGraphCompilerDefaultTest.GenericWithReturnTypeClassBase<T> {
            public Number foo(T t) {
                return null;
            }

            public static class Inner extends MethodGraphCompilerDefaultTest.GenericWithReturnTypeClassBase.Intermediate<Integer> {
                public Integer foo(Integer t) {
                    return null;
                }
            }
        }
    }

    public static class GenericNonOverriddenClassBase<T> {
        public T foo(T t) {
            return null;
        }

        /* empty */
        public class Inner extends MethodGraphCompilerDefaultTest.GenericNonOverriddenClassBase<Void> {}
    }

    public static class GenericDiamondClassBase<T> {
        public T foo(T t) {
            return null;
        }

        public interface DiamondInterface {
            Void foo(Void t);
        }

        /* empty */
        public class Inner extends MethodGraphCompilerDefaultTest.GenericNonOverriddenClassBase<Void> implements MethodGraphCompilerDefaultTest.GenericDiamondClassBase.DiamondInterface {}
    }

    public interface GenericDiamondInterfaceBase<T> {
        T foo(T t);

        interface DiamondInterface {
            Void foo(Void s);
        }

        /* empty */
        interface Inner extends MethodGraphCompilerDefaultTest.GenericDiamondInterfaceBase<Void> , MethodGraphCompilerDefaultTest.GenericDiamondInterfaceBase.DiamondInterface {}
    }

    public interface VisibilityExtension {
        void foo();

        class Base {
            protected void foo() {
                /* do nothing */
            }
        }
    }

    public static class DuplicateNameClass {
        public void foo(Object o) {
            /* empty */
        }

        public void foo(Integer o) {
            /* empty */
        }

        public static class Inner extends MethodGraphCompilerDefaultTest.DuplicateNameClass {
            public void foo(Void o) {
                /* empty */
            }
        }
    }

    public static class DuplicateNameGenericClass<T> {
        public void foo(T o) {
            /* empty */
        }

        public void foo(Integer o) {
            /* empty */
        }

        public static class Inner extends MethodGraphCompilerDefaultTest.DuplicateNameGenericClass<String> {
            public void foo(Void o) {
                /* empty */
            }
        }
    }

    static class VisibilityBridgeBase {
        public void foo() {
            /* empty */
        }
    }

    /* empty */
    public static class VisibilityBridgeTarget extends MethodGraphCompilerDefaultTest.VisibilityBridgeBase {}

    public static class GenericVisibilityBridgeBase<T> {
        public void foo(T t) {
            /* empty */
        }
    }

    static class GenericVisibilityBridge extends MethodGraphCompilerDefaultTest.GenericVisibilityBridgeBase<Void> {
        public void foo(Void aVoid) {
            /* empty */
        }
    }

    /* empty */
    public static class GenericVisibilityBridgeTarget extends MethodGraphCompilerDefaultTest.GenericVisibilityBridge {}

    public static class MethodClassConvergence<T> {
        public T foo(T arg) {
            return null;
        }

        public Void foo(Void arg) {
            return null;
        }

        public static class Inner extends MethodGraphCompilerDefaultTest.MethodClassConvergence<Void> {
            public Void foo(Void arg) {
                return null;
            }
        }
    }

    static class MethodConvergenceVisibilityBridgeBase<T> {
        public T foo(T arg) {
            return null;
        }

        public Void foo(Void arg) {
            return null;
        }
    }

    static class MethodConvergenceVisibilityBridgeIntermediate extends MethodGraphCompilerDefaultTest.MethodConvergenceVisibilityBridgeBase<Void> {
        public Void foo(Void arg) {
            return null;
        }
    }

    /* empty */
    public static class MethodConvergenceVisibilityBridgeTarget extends MethodGraphCompilerDefaultTest.MethodConvergenceVisibilityBridgeIntermediate {}

    public static class RawType<T> {
        public void foo(T t) {
            /* empty */
        }

        public static class Intermediate<T extends Number> extends MethodGraphCompilerDefaultTest.RawType<T> {
            public void foo(T t) {
                /* empty */
            }
        }

        public static class Raw extends MethodGraphCompilerDefaultTest.RawType.Intermediate {
            public void foo(Number t) {
                /* empty */
            }
        }
    }
}

