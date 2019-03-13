package buildcraft.test.lib.expression;


import VecLong.ZERO;
import buildcraft.lib.expression.Argument;
import buildcraft.lib.expression.DefaultContexts;
import buildcraft.lib.expression.ExpressionDebugManager;
import buildcraft.lib.expression.FunctionContext;
import buildcraft.lib.expression.GenericExpressionCompiler;
import buildcraft.lib.expression.NodeStack;
import buildcraft.lib.expression.api.IExpressionNode.INodeBoolean;
import buildcraft.lib.expression.api.IExpressionNode.INodeDouble;
import buildcraft.lib.expression.api.IExpressionNode.INodeLong;
import buildcraft.lib.expression.api.INodeFunc.INodeFuncLong;
import buildcraft.lib.expression.api.InvalidExpressionException;
import buildcraft.lib.expression.node.value.NodeConstantDouble;
import buildcraft.lib.expression.node.value.NodeVariableDouble;
import buildcraft.lib.expression.node.value.NodeVariableLong;
import buildcraft.lib.expression.node.value.NodeVariableObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ExpressionTester {
    static {
        ExpressionDebugManager.debug = true;
    }

    @Test
    public void testLongBasics() {
        ExpressionTester.bakeAndCallLong("0x0", 0);
        ExpressionTester.bakeAndCallLong("0xa", 10);
        ExpressionTester.bakeAndCallLong("0xA", 10);
        ExpressionTester.bakeAndCallLong("0x10", 16);
        ExpressionTester.bakeAndCallLong("0x1_0", 16);
    }

    @Test
    public void testDoubleBasics() {
        // I COULD change all these to be in separate functions... except that's really long :/
        ExpressionTester.bakeAndCallDouble("0", 0);
        ExpressionTester.bakeAndCallDouble("-1", (-1));
        ExpressionTester.bakeAndCallDouble("0+1", (0 + 1));
        ExpressionTester.bakeAndCallDouble("   0   +    1    ", 1);
        ExpressionTester.bakeAndCallDouble("3-2", (3 - 2));
        ExpressionTester.bakeAndCallDouble("1+1+1", 3);
        ExpressionTester.bakeAndCallDouble("1+2-1", 2);
        ExpressionTester.bakeAndCallDouble("1-2+1", 0);
        ExpressionTester.bakeAndCallDouble("1-1", 0);
        ExpressionTester.bakeAndCallDouble("(1-1)", 0);
        ExpressionTester.bakeAndCallDouble("2--3", 5);
        ExpressionTester.bakeAndCallDouble("3--2", 5);
        ExpressionTester.bakeAndCallDouble("1-(-1)", 2);
        ExpressionTester.bakeAndCallDouble("-1-1", (-2));
        ExpressionTester.bakeAndCallDouble("(-1)-1", (-2));
        ExpressionTester.bakeAndCallDouble("1-(2+1)", (-2));
        ExpressionTester.bakeAndCallDouble("(1)-(2+1)", (-2));
        ExpressionTester.bakeAndCallDouble("1 | 2", 3);
        ExpressionTester.bakeAndCallDouble("3 & 5", 1);
        ExpressionTester.bakeAndCallDouble("2*(-3)", (-6));
        ExpressionTester.bakeAndCallDouble("2*-3", (-6));
        ExpressionTester.bakeAndCallDouble("1 << 0", (1 << 0));
        ExpressionTester.bakeAndCallDouble("1 >> 0", (1 >> 0));
        ExpressionTester.bakeAndCallDouble("100 >> 2", (100 >> 2));
        ExpressionTester.bakeAndCallDouble("1 << 2", (1 << 2));
        ExpressionTester.bakeAndCallDouble("1 << 10", (1 << 10));
    }

    @Test
    public void testStringBasics() {
        ExpressionTester.bakeAndCallString("'a'", "a");
        ExpressionTester.bakeAndCallString("'A'", "A");
        ExpressionTester.bakeAndCallString("'a' + 'b'", "ab");
        ExpressionTester.bakeAndCallString("'aA' + 'b'", "aAb");
        ExpressionTester.bakeAndCallString("'aAB'.toLowerCase()", "aab");
        ExpressionTester.bakeAndCallString("'aAB'.tolOwercase()", "aab");
        ExpressionTester.bakeAndCallBoolean("'a' == 'a'", true);
        ExpressionTester.bakeAndCallBoolean("'a' != 'b'", true);
    }

    @Test
    public void testBooleanBasics() {
        ExpressionTester.bakeAndCallBoolean("true", true);
        ExpressionTester.bakeAndCallBoolean("!true", false);
        ExpressionTester.bakeAndCallBoolean("1 == 2", false);
        ExpressionTester.bakeAndCallBoolean("1 <= 2", true);
        ExpressionTester.bakeAndCallBoolean("1 == 1", true);
        ExpressionTester.bakeAndCallBoolean("1 != 1", false);
        ExpressionTester.bakeAndCallBoolean("1 == 1 || 1 > 2", true);
        ExpressionTester.bakeAndCallBoolean("1 == 1 && 1 > 2", false);
        ExpressionTester.bakeAndCallString("true ? 'hi' : 'nope'", "hi");
        ExpressionTester.bakeAndCallString("true ? 'h'+'i' : 'no'+'pe'", "hi");
        ExpressionTester.bakeAndCallString("false ? 'hi' : 'nope'", "nope");
        ExpressionTester.bakeAndCallString("1 <= 5^2-1 ? 'larger' : 'smaller'", "larger");
        ExpressionTester.bakeAndCallLong("false ? 0 : true ? 1 : 2", 1);
        ExpressionTester.bakeAndCallLong("(true ? false : true) ? 0 : 1", 1);
        ExpressionTester.bakeAndCallLong("(false ? 0 : 2) - 1", 1);
        ExpressionTester.bakeAndCallDouble("false ? 1 : 0.4", 0.4);
    }

    @Test
    public void testMath() throws InvalidExpressionException {
        FunctionContext ctx2 = DefaultContexts.createWithAll();
        List<Class<?>> list_d = Collections.singletonList(double.class);
        List<Class<?>> list_l = Collections.singletonList(long.class);
        List<Class<?>> list_ll = Arrays.asList(long.class, long.class);
        System.out.println(ctx2.getFunctions("sin"));
        System.out.println(ctx2.getFunction("sin", list_d));
        System.out.println(ctx2.getFunction("cosh", list_d));
        System.out.println(ctx2.getFunction("round", list_d));
        System.out.println(ctx2.getFunction("ceil", list_d));
        System.out.println(ctx2.getFunction("max", list_d));
        System.out.println(ctx2.getFunction("max", list_l));
        System.out.println(ctx2.getFunction("max", list_ll));
        NodeStack stack4 = new NodeStack();
        stack4.push(new NodeConstantDouble(0.4));
        INodeLong out = ((INodeLong) (ctx2.getFunction("ceil", list_d).getNode(stack4)));
        System.out.println(((out + " = ") + (out.evaluate())));
        stack4.push(new NodeConstantDouble(0.4));
        out = ((INodeLong) (ctx2.getFunction("floor", list_d).getNode(stack4)));
        System.out.println(((out + " = ") + (out.evaluate())));
        INodeDouble nd = ((INodeDouble) (ctx2.getVariable("pi")));
        System.out.println(((nd + " = ") + (nd.evaluate())));
        nd = ((INodeDouble) (ctx2.getVariable("e")));
        System.out.println(((nd + " = ") + (nd.evaluate())));
        INodeFuncLong func3 = GenericExpressionCompiler.compileFunctionLong("input * 2 + 1", ctx2, Argument.argLong("input"));
        NodeStack stack3 = new NodeStack();
        NodeVariableLong input = stack3.push(new NodeVariableLong("input"));
        INodeLong node3 = func3.getNode(stack3);
        input.value = 1;
        System.out.println(((node3 + " = ") + (node3.evaluate())));
        input.value = 30;
        System.out.println(((node3 + " = ") + (node3.evaluate())));
        ctx2.put_ll_l("sub", ( a, b) -> a - b);
        ExpressionTester.testExpr("floor(ceil(0.5)+0.5)", ctx2);
        ExpressionTester.testExpr("sub(5, 6)", ctx2);
        ExpressionTester.testExpr("5.sub(6.4.round()) + 0.5.ceil()", ctx2);
        ExpressionTester.testExpr("5.sub(6) + 0.5.ceil() & ' -- ' & 45 + 2", ctx2);
        ExpressionTester.testExpr("165 + 15 - 6 * 46.sub(10)", ctx2);
        ExpressionTester.testExpr("log(10)", ctx2);
        ExpressionTester.testExpr("log10(10)", ctx2);
        ExpressionTester.testExpr("cos(radians(90))", ctx2);
        ExpressionTester.testExpr("cos(radians(90)).round_float()", ctx2);
        ExpressionTester.testExpr("cos(radians(91)).round_float()", ctx2);
        ExpressionTester.testExpr("cos(radians(92)).round_float()", ctx2);
        ExpressionTester.testExpr("cos(radians(93)).round_float()", ctx2);
        ExpressionTester.testExpr("cos(radians(94)).round_float()", ctx2);
        ExpressionTester.testExpr("floor(ceil(0.5)+0.5)", ctx2);
        ExpressionTester.testExpr("sub(5, 6)", ctx2);
        ExpressionTester.testExpr("5.sub(6.4.round()) + 0.5.ceil()", ctx2);
        ExpressionTester.testExpr("5.sub(6) + 0.5.ceil() & ' -- ' & 45 + 2", ctx2);
        ExpressionTester.testExpr("165 + 15 - 6 * 46.sub(10)", ctx2);
        ExpressionTester.testExpr("log(10)", ctx2);
        ExpressionTester.testExpr("log10(10)", ctx2);
        ExpressionTester.testExpr("cos(radians(90))", ctx2);
        ExpressionTester.testExpr("cos(radians(90)).round_float()", ctx2);
        ExpressionTester.testExpr("cos(radians(91)).round_float()", ctx2);
        ExpressionTester.testExpr("cos(radians(92)).round_float()", ctx2);
        ExpressionTester.testExpr("cos(radians(93)).round_float()", ctx2);
        ExpressionTester.testExpr("cos(radians(94)).round_float()", ctx2);
    }

    @Test
    public void testFunctions() throws InvalidExpressionException {
        FunctionContext ctx = DefaultContexts.createWithAll();
        ExpressionTester.compileFuncLong(ctx, "one", "1");
        ExpressionTester.compileFuncLong(ctx, "same", "value", Argument.argLong("value"));
        ExpressionTester.compileFuncDouble(ctx, "same", "value", Argument.argDouble("value"));
        ExpressionTester.compileFuncDouble(ctx, "powertwo", "pow(2,input)", Argument.argDouble("input"));
        ExpressionTester.compileFuncDouble(ctx, "subtract", "l - r", Argument.argDouble("l"), Argument.argDouble("r"));
        ExpressionTester.compileFuncDouble(ctx, "tuple", "a + b + c", Argument.argDouble("a"), Argument.argDouble("b"), Argument.argDouble("c"));
        ExpressionTester.compileFuncDouble(ctx, "powlong", "pow((same(a + 1) - 1) , (same(b) * one()))", Argument.argDouble("a"), Argument.argDouble("b"));
        ExpressionTester.bakeAndCallDouble("one()", 1, ctx);
        ExpressionTester.bakeAndCallDouble("oNe()", 1, ctx);
        ExpressionTester.bakeAndCallDouble("same(0)", 0, ctx);
        ExpressionTester.bakeAndCallDouble("same(one())", 1, ctx);
        ExpressionTester.bakeAndCallDouble("same(pow(2,5))", 32, ctx);
        ExpressionTester.bakeAndCallDouble("powerTwo(5)", 32, ctx);
        ExpressionTester.bakeAndCallDouble("powertwo(6)", 64, ctx);
        ExpressionTester.bakeAndCallDouble("subtract(3, 1)", 2, ctx);
        ExpressionTester.bakeAndCallDouble("subtract(1, 3)", (-2), ctx);
        ExpressionTester.bakeAndCallDouble("subtract(1, -3)", 4, ctx);
        ExpressionTester.bakeAndCallDouble("subtract(1, -3)", 4, ctx);
        ExpressionTester.bakeAndCallDouble("tuple(1, 2, 3)", 6, ctx);
        ExpressionTester.bakeAndCallDouble("tuple(3, 2, 1)", 6, ctx);
        ExpressionTester.bakeAndCallDouble("tuple(-7, 1, 0)", (-6), ctx);
        ExpressionTester.bakeAndCallDouble("tuple(1, 3, 2)", 6, ctx);
        ExpressionTester.bakeAndCallDouble("powLong(3, 3)", 27, ctx);
    }

    @Test
    public void testVariables() {
        FunctionContext ctx = new FunctionContext(DefaultContexts.createWithAll());
        NodeVariableDouble someVariable = ctx.putVariableDouble("something");
        someVariable.value = 0;
        ExpressionTester.bakeAndCallDouble("something", 0, ctx);
        someVariable.value = 1;
        ExpressionTester.bakeAndCallDouble("something", 1, ctx);
        NodeVariableObject<String> variant = ctx.putVariableString("variant");
        String exp = "variant == 'gold'";
        INodeBoolean expBool = ExpressionTester.bakeFunctionBoolean(exp, ctx);
        variant.value = "nether_brick";
        Assert.assertFalse(expBool.evaluate());
        variant.value = "gold";
        Assert.assertTrue(expBool.evaluate());
        variant.value = "iron";
        Assert.assertFalse(expBool.evaluate());
        exp = "variant == 'wood' ? 0 : variant == 'steel' ? 1 : variant == 'obsidian' ? 2 : 3";
        INodeLong expLong = ExpressionTester.bakeFunctionLong(exp, ctx);
        variant.value = "wood";
        Assert.assertEquals(expLong.evaluate(), 0);
        variant.value = "steel";
        Assert.assertEquals(expLong.evaluate(), 1);
        variant.value = "obsidian";
        Assert.assertEquals(expLong.evaluate(), 2);
        variant.value = "some_other_value";
        Assert.assertEquals(expLong.evaluate(), 3);
    }

    @Test
    public void testObjects() {
        FunctionContext ctx = new FunctionContext();
        ctx.putConstantLong("engine.rate", 6);
        ExpressionTester.bakeAndCallLong("engine.rate", 6, ctx);
        ctx.putConstantLong("engine.other_rate", 5);
        ExpressionTester.bakeAndCallBoolean("engine.rate != engine.other_rate", true, ctx);
        ctx.putConstant("engine.stage", String.class, "blue");
        ExpressionTester.bakeAndCallString("engine.stage.toUpperCase()", "BLUE", ctx);
    }

    @Test
    public void testVectors() {
        ExpressionTester.bakeAndCallString("VecLong.zero", ZERO.toString());
        ExpressionTester.bakeAndCallString("vec(0, 0, 0, 0)", ZERO.toString());
        ExpressionTester.bakeAndCallString("vec(3, 4) + vec(1, 2)", "{ 4, 6, 0, 0 }");
        ExpressionTester.bakeAndCallString("vec(3, 4) - vec(1, 2)", "{ 2, 2, 0, 0 }");
        ExpressionTester.bakeAndCallLong("vec(3, 4).dot2(vec(1, 2))", 11);
        ExpressionTester.bakeAndCallLong("vec(3, 4).dot3(vec(1, 2))", 11);
        ExpressionTester.bakeAndCallLong("vec(3, 4).dot4(vec(1, 2))", 11);
        ExpressionTester.bakeAndCallDouble("vec(3, 4).length()", Math.sqrt(((3 * 3) + (4 * 4))));
        ExpressionTester.bakeAndCallDouble("vec(3, 4).distanceTo(vec(3, 9))", 5);
    }
}

