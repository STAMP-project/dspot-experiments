package com.github.javaparser.symbolsolver.resolution;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.symbolsolver.AbstractSymbolResolutionTest;
import com.github.javaparser.symbolsolver.core.resolution.Context;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MethodsResolutionWithJavassistTest extends AbstractResolutionTest {
    @Test
    public void testOverloadedMethods() throws Exception {
        CompilationUnit cu = parseSample("OverloadedMethodCall");
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new JarTypeSolver(AbstractSymbolResolutionTest.adaptPath("src/test/resources/javaparser-core-3.0.0-alpha.2.jar")));
        typeSolver.add(new ReflectionTypeSolver());
        Context context = new com.github.javaparser.symbolsolver.javaparsermodel.contexts.CompilationUnitContext(cu, typeSolver);
        ClassOrInterfaceDeclaration classA = Navigator.demandClass(cu, "OverloadedMethodCall");
        MethodDeclaration method = Navigator.demandMethod(classA, "foo");
        List<MethodCallExpr> calls = method.findAll(MethodCallExpr.class, ( n) -> n.getNameAsString().equals("accept"));
        Assertions.assertEquals(2, calls.size());
        // node.accept((GenericVisitor) null, null);
        MethodUsage methodUsage1 = JavaParserFacade.get(typeSolver).solveMethodAsUsage(calls.get(0));
        Assertions.assertEquals("com.github.javaparser.ast.visitor.GenericVisitor<R, A>", methodUsage1.getParamType(0).describe());
        // node.accept((VoidVisitor) null, null);
        MethodUsage methodUsage2 = JavaParserFacade.get(typeSolver).solveMethodAsUsage(calls.get(1));
        Assertions.assertEquals("com.github.javaparser.ast.visitor.VoidVisitor<A>", methodUsage2.getParamType(0).describe());
    }
}

