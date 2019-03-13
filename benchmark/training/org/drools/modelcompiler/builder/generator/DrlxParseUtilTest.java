package org.drools.modelcompiler.builder.generator;


import ClassUtil.NullType;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Function;
import org.drools.modelcompiler.domain.Person;
import org.junit.Assert;
import org.junit.Test;
import org.kie.soup.project.datamodel.commons.types.ClassTypeResolver;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;


public class DrlxParseUtilTest {
    @Test
    public void prependTest() {
        final Expression expr = JavaParser.parseExpression("getAddressName().startsWith(\"M\")");
        final NameExpr nameExpr = new NameExpr("_this");
        final Expression concatenated = DrlxParseUtil.prepend(nameExpr, expr);
        Assert.assertEquals("_this.getAddressName().startsWith(\"M\")", concatenated.toString());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void throwExceptionWhenMissingNode() {
        final Expression expr = JavaParser.parseExpression("this");
        DrlxParseUtil.prepend(null, expr);
    }

    final TypeResolver typeResolver = new ClassTypeResolver(new HashSet(), getClass().getClassLoader());

    @Test
    public void transformMethodExpressionToMethodCallExpressionTypeSafe() {
        final Expression expr = JavaParser.parseExpression("address.city.startsWith(\"M\")");
        final Expression expr1 = JavaParser.parseExpression("getAddress().city.startsWith(\"M\")");
        final Expression expr2 = JavaParser.parseExpression("address.getCity().startsWith(\"M\")");
        final MethodCallExpr expected = JavaParser.parseExpression("getAddress().getCity().startsWith(\"M\")");
        Assert.assertEquals(expected.toString(), DrlxParseUtil.toMethodCallWithClassCheck(null, expr, null, Person.class, typeResolver).getExpression().toString());
        Assert.assertEquals(expected.toString(), DrlxParseUtil.toMethodCallWithClassCheck(null, expr1, null, Person.class, typeResolver).getExpression().toString());
        Assert.assertEquals(expected.toString(), DrlxParseUtil.toMethodCallWithClassCheck(null, expr2, null, Person.class, typeResolver).getExpression().toString());
    }

    @Test
    public void getExpressionTypeTest() {
        Assert.assertEquals(Double.class, DrlxParseUtil.getExpressionType(null, typeResolver, JavaParser.parseExpression("new Double[]{2.0d, 3.0d}[1]"), null));
        Assert.assertEquals(Float.class, DrlxParseUtil.getExpressionType(null, typeResolver, JavaParser.parseExpression("new Float[]{2.0d, 3.0d}"), null));
        Assert.assertEquals(boolean.class, DrlxParseUtil.getExpressionType(null, typeResolver, new BooleanLiteralExpr(true), null));
        Assert.assertEquals(char.class, DrlxParseUtil.getExpressionType(null, typeResolver, new CharLiteralExpr('a'), null));
        Assert.assertEquals(double.class, DrlxParseUtil.getExpressionType(null, typeResolver, new DoubleLiteralExpr(2.0), null));
        Assert.assertEquals(int.class, DrlxParseUtil.getExpressionType(null, typeResolver, new IntegerLiteralExpr(2), null));
        Assert.assertEquals(long.class, DrlxParseUtil.getExpressionType(null, typeResolver, new LongLiteralExpr(2L), null));
        Assert.assertEquals(NullType.class, DrlxParseUtil.getExpressionType(null, typeResolver, new NullLiteralExpr(), null));
        Assert.assertEquals(String.class, DrlxParseUtil.getExpressionType(null, typeResolver, new StringLiteralExpr(""), null));
    }

    @Test
    public void test_forceCastForName() {
        Function<String, String> c = (String input) -> {
            Expression expr = JavaParser.parseExpression(input);
            DrlxParseUtil.forceCastForName("$my", JavaParser.parseType("Integer"), expr);
            return expr.toString();
        };
        Assert.assertEquals("ciao += ((Integer) $my)", c.apply("ciao += $my"));
        Assert.assertEquals("ciao.add(((Integer) $my))", c.apply("ciao.add($my)"));
        Assert.assertEquals("ciao.asd.add(((Integer) $my))", c.apply("ciao.asd.add($my)"));
    }

    @Test
    public void test_rescopeNamesToNewScope() {
        Function<String, String> c = (String input) -> {
            Expression expr = JavaParser.parseExpression(input);
            DrlxParseUtil.rescopeNamesToNewScope(new NameExpr("nscope"), Arrays.asList("name", "surname"), expr);
            return expr.toString();
        };
        Assert.assertEquals("nscope.name = \"John\"", c.apply("name = \"John\" "));
        Assert.assertEquals("nscope.name = nscope.surname", c.apply("name = surname"));
    }

    @Test
    public void test_rescopeAlsoArgumentsToNewScope() {
        Function<String, String> c = (String input) -> {
            Expression expr = JavaParser.parseExpression(input);
            DrlxParseUtil.rescopeNamesToNewScope(new NameExpr("nscope"), Collections.singletonList("total"), expr);
            return expr.toString();
        };
        Assert.assertEquals("new Integer(nscope.total)", c.apply("new Integer(total) "));
    }

    @Test
    public void removeRootNodeTest() {
        Assert.assertEquals(new org.drools.modelcompiler.builder.generator.DrlxParseUtil.RemoveRootNodeResult(Optional.of(expr("sum")), expr("sum")), DrlxParseUtil.findRemoveRootNodeViaScope(expr("sum")));
        Assert.assertEquals(new org.drools.modelcompiler.builder.generator.DrlxParseUtil.RemoveRootNodeResult(Optional.of(expr("$a")), expr("getAge()")), DrlxParseUtil.findRemoveRootNodeViaScope(expr("$a.getAge()")));
        Assert.assertEquals(new org.drools.modelcompiler.builder.generator.DrlxParseUtil.RemoveRootNodeResult(Optional.of(expr("$c")), expr("convert($length)")), DrlxParseUtil.findRemoveRootNodeViaScope(expr("$c.convert($length)")));
        Assert.assertEquals(new org.drools.modelcompiler.builder.generator.DrlxParseUtil.RemoveRootNodeResult(Optional.of(expr("$data")), expr("getValues().get(0)")), DrlxParseUtil.findRemoveRootNodeViaScope(expr("$data.getValues().get(0)")));
        Assert.assertEquals(new org.drools.modelcompiler.builder.generator.DrlxParseUtil.RemoveRootNodeResult(Optional.of(expr("$data")), expr("getIndexes().getValues().get(0)")), DrlxParseUtil.findRemoveRootNodeViaScope(expr("$data.getIndexes().getValues().get(0)")));
    }
}

