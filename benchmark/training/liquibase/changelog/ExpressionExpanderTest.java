package liquibase.changelog;


import ChangeLogParameters.ExpressionExpander;
import liquibase.configuration.LiquibaseConfiguration;
import liquibase.parser.ChangeLogParserCofiguration;
import org.junit.Assert;
import org.junit.Test;


public class ExpressionExpanderTest {
    private ExpressionExpander handler;

    private ChangeLogParameters changeLogParameters;

    @Test
    public void expandExpressions_nullValue() {
        Assert.assertNull(handler.expandExpressions(null, null));
    }

    @Test
    public void expandExpressions_emptyString() {
        Assert.assertEquals("", handler.expandExpressions("", null));
    }

    @Test
    public void expandExpressions_noExpression() {
        Assert.assertEquals("A Simple String", handler.expandExpressions("A Simple String", null));
    }

    @Test
    public void expandExpressions_singleObjectExpression() {
        changeLogParameters.set("here", 4);
        Assert.assertEquals("A string with one expression 4 set", handler.expandExpressions("A string with one expression ${here} set", null));
    }

    @Test
    public void expandExpressions_doubleObjectExpression() {
        changeLogParameters.set("here", 4);
        changeLogParameters.set("there", 15);
        Assert.assertEquals("A string with two expressions 4 and 15 set", handler.expandExpressions("A string with two expressions ${here} and ${there} set", null));
    }

    @Test
    public void expandExpressions_nomatchExpression() {
        Assert.assertEquals("A string no expressions ${notset} set", handler.expandExpressions("A string no expressions ${notset} set", null));
        Assert.assertEquals("A string no expressions ${notset.orParams} set", handler.expandExpressions("A string no expressions ${notset.orParams} set", null));
    }

    @Test
    public void expandExpressions_escapedSimple() {
        LiquibaseConfiguration.getInstance().getConfiguration(ChangeLogParserCofiguration.class).setSupportPropertyEscaping(true);
        this.handler = new ChangeLogParameters.ExpressionExpander(changeLogParameters);
        Assert.assertEquals("${user.name}", handler.expandExpressions("${:user.name}", null));
    }

    @Test
    public void expandExpressions_escapedNonGreedy() {
        LiquibaseConfiguration.getInstance().getConfiguration(ChangeLogParserCofiguration.class).setSupportPropertyEscaping(true);
        this.handler = new ChangeLogParameters.ExpressionExpander(changeLogParameters);
        Assert.assertEquals("${user.name}${user.name}", handler.expandExpressions("${:user.name}${:user.name}", null));
    }

    @Test
    public void expandExpressions_escapedMultipleSimple() {
        LiquibaseConfiguration.getInstance().getConfiguration(ChangeLogParserCofiguration.class).setSupportPropertyEscaping(true);
        this.handler = new ChangeLogParameters.ExpressionExpander(changeLogParameters);
        Assert.assertEquals("${user.name} and ${user.name} are literals", handler.expandExpressions("${:user.name} and ${:user.name} are literals", null));
    }

    @Test
    public void expandExpressions_escapedMultipleComplex() {
        LiquibaseConfiguration.getInstance().getConfiguration(ChangeLogParserCofiguration.class).setSupportPropertyEscaping(true);
        this.handler = new ChangeLogParameters.ExpressionExpander(changeLogParameters);
        Assert.assertEquals(("${user.name} and ${user.name} are literals but this isn't: " + (System.getProperty("user.name"))), handler.expandExpressions("${:user.name} and ${:user.name} are literals but this isn't: ${user.name}", null));
    }

    @Test
    public void expandExpressions_escapedBeforeVariable() {
        LiquibaseConfiguration.getInstance().getConfiguration(ChangeLogParserCofiguration.class).setSupportPropertyEscaping(true);
        this.handler = new ChangeLogParameters.ExpressionExpander(changeLogParameters);
        Assert.assertEquals((("${user.name} is a literal, " + (System.getProperty("user.name"))) + " is a variable"), handler.expandExpressions("${:user.name} is a literal, ${user.name} is a variable", null));
    }

    @Test
    public void expandExpressions_escapedAfterVariable() {
        LiquibaseConfiguration.getInstance().getConfiguration(ChangeLogParserCofiguration.class).setSupportPropertyEscaping(true);
        this.handler = new ChangeLogParameters.ExpressionExpander(changeLogParameters);
        Assert.assertEquals(((System.getProperty("user.name")) + " is a variable, ${user.name} is a literal"), handler.expandExpressions("${user.name} is a variable, ${:user.name} is a literal", null));
    }

    @Test
    public void expandExpressions_escapedMultipleComplexVariant() {
        changeLogParameters.set("a", "Value A");
        changeLogParameters.set("b", "Value B");
        LiquibaseConfiguration.getInstance().getConfiguration(ChangeLogParserCofiguration.class).setSupportPropertyEscaping(true);
        this.handler = new ChangeLogParameters.ExpressionExpander(changeLogParameters);
        Assert.assertEquals("Value A is a variable, ${a} and ${b} are literals but this isn't: Value B", handler.expandExpressions("${a} is a variable, ${:a} and ${:b} are literals but this isn't: ${b}", null));
    }
}

