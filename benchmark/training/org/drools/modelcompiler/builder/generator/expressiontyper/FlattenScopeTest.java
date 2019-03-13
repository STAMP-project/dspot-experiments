package org.drools.modelcompiler.builder.generator.expressiontyper;


import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;


public class FlattenScopeTest {
    @Test
    public void flattenUnaryExpression() {
        List<Node> actual = FlattenScope.flattenScope(expr("getMessageId"));
        List<Node> expected = Collections.singletonList(new NameExpr("getMessageId"));
        compareArrays(actual, expected);
    }

    @Test
    public void flattenFields() {
        List<Node> actual = FlattenScope.flattenScope(expr("Field.INT"));
        List<Node> expected = Arrays.asList(new NameExpr("Field"), new SimpleName("INT"));
        compareArrays(actual, expected);
    }

    @Test
    public void flattenMethodCall() {
        List<Node> actual = FlattenScope.flattenScope(expr("name.startsWith(\"M\")"));
        MethodCallExpr methodCallExpr = new MethodCallExpr(new NameExpr("name"), "startsWith", nodeList(new StringLiteralExpr("M")));
        methodCallExpr.setTypeArguments(NodeList.nodeList());
        List<Node> expected = Arrays.asList(new NameExpr("name"), methodCallExpr);
        compareArrays(actual, expected);
    }

    @Test
    public void flattenArrayAccess() {
        List<Node> actual = FlattenScope.flattenScope(expr("$p.getChildrenA()[0]"));
        NameExpr name = new NameExpr("$p");
        final MethodCallExpr mc = new MethodCallExpr(name, "getChildrenA", nodeList());
        mc.setTypeArguments(NodeList.nodeList());
        List<Node> expected = Arrays.asList(name, mc, new com.github.javaparser.ast.expr.ArrayAccessExpr(mc, new IntegerLiteralExpr(0)));
        compareArrays(actual, expected);
    }
}

