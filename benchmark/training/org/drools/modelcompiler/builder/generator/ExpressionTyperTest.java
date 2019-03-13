package org.drools.modelcompiler.builder.generator;


import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.constraint.parser.ast.expr.PointFreeExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.expressiontyper.TypedExpressionResult;
import org.drools.modelcompiler.domain.Overloaded;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.inlinecast.ICA;
import org.junit.Assert;
import org.junit.Test;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;


public class ExpressionTyperTest {
    private HashSet<String> imports;

    private PackageModel packageModel;

    private TypeResolver typeResolver;

    private RuleContext ruleContext;

    private KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();

    private RuleDescr ruleDescr = new RuleDescr("testRule");

    @Test
    public void toTypedExpressionTest() {
        Assert.assertEquals("$mark.getAge()", toTypedExpression("$mark.age", null, aPersonDecl("$mark")).getExpression().toString());
        Assert.assertEquals("$p.getName()", toTypedExpression("$p.name", null, aPersonDecl("$p")).getExpression().toString());
        Assert.assertEquals("_this.getName().length()", toTypedExpression("name.length", Person.class).getExpression().toString());
        Assert.assertEquals("_this.method(5, 9, \"x\")", toTypedExpression("method(5,9,\"x\")", Overloaded.class).getExpression().toString());
        Assert.assertEquals("_this.getAddress().getCity().length()", toTypedExpression("address.getCity().length", Person.class).getExpression().toString());
    }

    @Test
    public void inlineCastTest() {
        String result = "((org.drools.modelcompiler.domain.Person) _this).getName()";
        Assert.assertEquals(result, toTypedExpression("this#Person.name", Object.class).getExpression().toString());
    }

    @Test
    public void inlineCastTest2() {
        addInlineCastImport();
        String result = "((org.drools.modelcompiler.inlinecast.ICC) ((org.drools.modelcompiler.inlinecast.ICB) _this.getSomeB()).getSomeC()).onlyConcrete()";
        Assert.assertEquals(result, toTypedExpression("someB#ICB.someC#ICC.onlyConcrete() ", ICA.class).getExpression().toString());
    }

    @Test
    public void inlineCastTest3() {
        addInlineCastImport();
        String result = "((org.drools.modelcompiler.inlinecast.ICB) _this.getSomeB()).onlyConcrete()";
        Assert.assertEquals(result, toTypedExpression("someB#ICB.onlyConcrete()", ICA.class).getExpression().toString());
    }

    @Test
    public void pointFreeTest() {
        final PointFreeExpr expression = new PointFreeExpr(null, new NameExpr("name"), NodeList.nodeList(new StringLiteralExpr("[A-Z]")), new SimpleName("matches"), false, null, null, null, null);
        TypedExpressionResult typedExpressionResult = new org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyper(ruleContext, Person.class, null, true).toTypedExpression(expression);
        final TypedExpression actual = typedExpressionResult.getTypedExpression().get();
        final TypedExpression expected = typedResult("D.eval(org.drools.model.operators.MatchesOperator.INSTANCE, _this.getName(), \"[A-Z]\")", String.class);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testBigDecimalConstant() {
        final TypedExpression expected = typedResult("java.math.BigDecimal.ONE", BigDecimal.class);
        final TypedExpression actual = toTypedExpression("java.math.BigDecimal.ONE", null);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testBigDecimalLiteral() {
        final TypedExpression expected = typedResult("13.111B", BigDecimal.class);
        final TypedExpression actual = toTypedExpression("13.111B", null);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testBooleanComparison() {
        final TypedExpression expected = typedResult("_this.getAge() == 18", int.class);
        final TypedExpression actual = toTypedExpression("age == 18", Person.class);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testAssignment() {
        final TypedExpression expected = typedResult("total = total + $cheese.getPrice()", Integer.class);
        final TypedExpression actual = toTypedExpression("total = total + $cheese.price", Object.class, new DeclarationSpec("$cheese", ExpressionTyperTest.Cheese.class), new DeclarationSpec("total", Integer.class));
        Assert.assertEquals(expected, actual);
    }

    public static class Cheese {
        private Integer price;

        public Integer getPrice() {
            return price;
        }
    }

    @Test
    public void arrayAccessExpr() {
        final TypedExpression expected = typedResult("_this.getItems().get(1)", Object.class);
        final TypedExpression actual = toTypedExpression("items[1]", Person.class);
        Assert.assertEquals(expected, actual);
        final TypedExpression expected2 = typedResult("_this.getItems().get(((Integer)1))", Object.class);
        final TypedExpression actual2 = toTypedExpression("items[(Integer)1]", Person.class);
        Assert.assertEquals(expected2, actual2);
    }

    @Test
    public void mapAccessExpr() {
        final TypedExpression expected3 = typedResult("_this.get(\"type\")", Object.class);
        final TypedExpression actual3 = toTypedExpression("this[\"type\"]", Map.class);
        Assert.assertEquals(expected3, actual3);
    }

    @Test
    public void mapAccessExpr2() {
        final TypedExpression expected3 = typedResult("$p.getItems().get(\"type\")", Integer.class, "$p.items[\"type\"]");
        final TypedExpression actual3 = toTypedExpression("$p.items[\"type\"]", Object.class, new DeclarationSpec("$p", Person.class));
        Assert.assertEquals(expected3, actual3);
    }

    @Test
    public void mapAccessExpr3() {
        final TypedExpression expected = typedResult("$p.getItems().get(1)", Integer.class, "$p.items[1]");
        final TypedExpression actual = toTypedExpression("$p.items[1]", Object.class, new DeclarationSpec("$p", Person.class));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void arrayAccessExprDeclaration() {
        final TypedExpression expected = typedResult("$data.getValues().get(0)", Integer.class, "$data.values[0]");
        final TypedExpression actual = toTypedExpression("$data.values[0]", Object.class, new DeclarationSpec("$data", ExpressionTyperTest.Data.class));
        Assert.assertEquals(expected, actual);
    }

    public static class Data {
        private List<Integer> values;

        public Data(List<Integer> values) {
            this.values = values;
        }

        public List<Integer> getValues() {
            return values;
        }
    }

    @Test
    public void testAssignment2() {
        Assert.assertEquals("_this.getName().length()", toTypedExpression("name.length", Person.class).getExpression().toString());
    }
}

