/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.expression.spel.support;


import ArgumentsMatchKind.CLOSE;
import ArgumentsMatchKind.REQUIRES_CONVERSION;
import ReflectionHelper.ArgumentsMatchKind.EXACT;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ParseException;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.AbstractExpressionTests;
import org.springframework.expression.spel.SpelUtilities;
import org.springframework.expression.spel.standard.SpelExpression;


/**
 * Tests for reflection helper code.
 *
 * @author Andy Clement
 */
public class ReflectionHelperTests extends AbstractExpressionTests {
    @Test
    public void testUtilities() throws ParseException {
        SpelExpression expr = ((SpelExpression) (parser.parseExpression("3+4+5+6+7-2")));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        SpelUtilities.printAbstractSyntaxTree(ps, expr);
        ps.flush();
        String s = baos.toString();
        // ===> Expression '3+4+5+6+7-2' - AST start
        // OperatorMinus  value:(((((3 + 4) + 5) + 6) + 7) - 2)  #children:2
        // OperatorPlus  value:((((3 + 4) + 5) + 6) + 7)  #children:2
        // OperatorPlus  value:(((3 + 4) + 5) + 6)  #children:2
        // OperatorPlus  value:((3 + 4) + 5)  #children:2
        // OperatorPlus  value:(3 + 4)  #children:2
        // CompoundExpression  value:3
        // IntLiteral  value:3
        // CompoundExpression  value:4
        // IntLiteral  value:4
        // CompoundExpression  value:5
        // IntLiteral  value:5
        // CompoundExpression  value:6
        // IntLiteral  value:6
        // CompoundExpression  value:7
        // IntLiteral  value:7
        // CompoundExpression  value:2
        // IntLiteral  value:2
        // ===> Expression '3+4+5+6+7-2' - AST end
        Assert.assertTrue(s.contains("===> Expression '3+4+5+6+7-2' - AST start"));
        Assert.assertTrue(s.contains(" OpPlus  value:((((3 + 4) + 5) + 6) + 7)  #children:2"));
    }

    @Test
    public void testTypedValue() {
        TypedValue tv1 = new TypedValue("hello");
        TypedValue tv2 = new TypedValue("hello");
        TypedValue tv3 = new TypedValue("bye");
        Assert.assertEquals(String.class, tv1.getTypeDescriptor().getType());
        Assert.assertEquals("TypedValue: 'hello' of [java.lang.String]", tv1.toString());
        Assert.assertEquals(tv1, tv2);
        Assert.assertEquals(tv2, tv1);
        Assert.assertNotEquals(tv1, tv3);
        Assert.assertNotEquals(tv2, tv3);
        Assert.assertNotEquals(tv3, tv1);
        Assert.assertNotEquals(tv3, tv2);
        Assert.assertEquals(tv1.hashCode(), tv2.hashCode());
        Assert.assertNotEquals(tv1.hashCode(), tv3.hashCode());
        Assert.assertNotEquals(tv2.hashCode(), tv3.hashCode());
    }

    @Test
    public void testReflectionHelperCompareArguments_ExactMatching() {
        StandardTypeConverter tc = new StandardTypeConverter();
        // Calling foo(String) with (String) is exact match
        checkMatch(new Class<?>[]{ String.class }, new Class<?>[]{ String.class }, tc, EXACT);
        // Calling foo(String,Integer) with (String,Integer) is exact match
        checkMatch(new Class<?>[]{ String.class, Integer.class }, new Class<?>[]{ String.class, Integer.class }, tc, ArgumentsMatchKind.EXACT);
    }

    @Test
    public void testReflectionHelperCompareArguments_CloseMatching() {
        StandardTypeConverter tc = new StandardTypeConverter();
        // Calling foo(List) with (ArrayList) is close match (no conversion required)
        checkMatch(new Class<?>[]{ ArrayList.class }, new Class<?>[]{ List.class }, tc, CLOSE);
        // Passing (Sub,String) on call to foo(Super,String) is close match
        checkMatch(new Class<?>[]{ ReflectionHelperTests.Sub.class, String.class }, new Class<?>[]{ ReflectionHelperTests.Super.class, String.class }, tc, CLOSE);
        // Passing (String,Sub) on call to foo(String,Super) is close match
        checkMatch(new Class<?>[]{ String.class, ReflectionHelperTests.Sub.class }, new Class<?>[]{ String.class, ReflectionHelperTests.Super.class }, tc, CLOSE);
    }

    @Test
    public void testReflectionHelperCompareArguments_RequiresConversionMatching() {
        StandardTypeConverter tc = new StandardTypeConverter();
        // Calling foo(String,int) with (String,Integer) requires boxing conversion of argument one
        checkMatch(new Class<?>[]{ String.class, Integer.TYPE }, new Class<?>[]{ String.class, Integer.class }, tc, CLOSE);
        // Passing (int,String) on call to foo(Integer,String) requires boxing conversion of argument zero
        checkMatch(new Class<?>[]{ Integer.TYPE, String.class }, new Class<?>[]{ Integer.class, String.class }, tc, CLOSE);
        // Passing (int,Sub) on call to foo(Integer,Super) requires boxing conversion of argument zero
        checkMatch(new Class<?>[]{ Integer.TYPE, ReflectionHelperTests.Sub.class }, new Class<?>[]{ Integer.class, ReflectionHelperTests.Super.class }, tc, CLOSE);
        // Passing (int,Sub,boolean) on call to foo(Integer,Super,Boolean) requires boxing conversion of arguments zero and two
        // TODO checkMatch(new Class<?>[] {Integer.TYPE, Sub.class, Boolean.TYPE}, new Class<?>[] {Integer.class, Super.class, Boolean.class}, tc, ArgsMatchKind.REQUIRES_CONVERSION);
    }

    @Test
    public void testReflectionHelperCompareArguments_NotAMatch() {
        StandardTypeConverter typeConverter = new StandardTypeConverter();
        // Passing (Super,String) on call to foo(Sub,String) is not a match
        checkMatch(new Class<?>[]{ ReflectionHelperTests.Super.class, String.class }, new Class<?>[]{ ReflectionHelperTests.Sub.class, String.class }, typeConverter, null);
    }

    @Test
    public void testReflectionHelperCompareArguments_Varargs_ExactMatching() {
        StandardTypeConverter tc = new StandardTypeConverter();
        // Passing (String[]) on call to (String[]) is exact match
        checkMatch2(new Class<?>[]{ String[].class }, new Class<?>[]{ String[].class }, tc, ArgumentsMatchKind.EXACT);
        // Passing (Integer, String[]) on call to (Integer, String[]) is exact match
        checkMatch2(new Class<?>[]{ Integer.class, String[].class }, new Class<?>[]{ Integer.class, String[].class }, tc, ArgumentsMatchKind.EXACT);
        // Passing (String, Integer, String[]) on call to (String, String, String[]) is exact match
        checkMatch2(new Class<?>[]{ String.class, Integer.class, String[].class }, new Class<?>[]{ String.class, Integer.class, String[].class }, tc, ArgumentsMatchKind.EXACT);
        // Passing (Sub, String[]) on call to (Super, String[]) is exact match
        checkMatch2(new Class<?>[]{ ReflectionHelperTests.Sub.class, String[].class }, new Class<?>[]{ ReflectionHelperTests.Super.class, String[].class }, tc, CLOSE);
        // Passing (Integer, String[]) on call to (String, String[]) is exact match
        checkMatch2(new Class<?>[]{ Integer.class, String[].class }, new Class<?>[]{ String.class, String[].class }, tc, REQUIRES_CONVERSION);
        // Passing (Integer, Sub, String[]) on call to (String, Super, String[]) is exact match
        checkMatch2(new Class<?>[]{ Integer.class, ReflectionHelperTests.Sub.class, String[].class }, new Class<?>[]{ String.class, ReflectionHelperTests.Super.class, String[].class }, tc, REQUIRES_CONVERSION);
        // Passing (String) on call to (String[]) is exact match
        checkMatch2(new Class<?>[]{ String.class }, new Class<?>[]{ String[].class }, tc, ArgumentsMatchKind.EXACT);
        // Passing (Integer,String) on call to (Integer,String[]) is exact match
        checkMatch2(new Class<?>[]{ Integer.class, String.class }, new Class<?>[]{ Integer.class, String[].class }, tc, ArgumentsMatchKind.EXACT);
        // Passing (String) on call to (Integer[]) is conversion match (String to Integer)
        checkMatch2(new Class<?>[]{ String.class }, new Class<?>[]{ Integer[].class }, tc, REQUIRES_CONVERSION);
        // Passing (Sub) on call to (Super[]) is close match
        checkMatch2(new Class<?>[]{ ReflectionHelperTests.Sub.class }, new Class<?>[]{ ReflectionHelperTests.Super[].class }, tc, CLOSE);
        // Passing (Super) on call to (Sub[]) is not a match
        checkMatch2(new Class<?>[]{ ReflectionHelperTests.Super.class }, new Class<?>[]{ ReflectionHelperTests.Sub[].class }, tc, null);
        checkMatch2(new Class<?>[]{ ReflectionHelperTests.Unconvertable.class, String.class }, new Class<?>[]{ ReflectionHelperTests.Sub.class, ReflectionHelperTests.Super[].class }, tc, null);
        checkMatch2(new Class<?>[]{ Integer.class, Integer.class, String.class }, new Class<?>[]{ String.class, String.class, ReflectionHelperTests.Super[].class }, tc, null);
        checkMatch2(new Class<?>[]{ ReflectionHelperTests.Unconvertable.class, String.class }, new Class<?>[]{ ReflectionHelperTests.Sub.class, ReflectionHelperTests.Super[].class }, tc, null);
        checkMatch2(new Class<?>[]{ Integer.class, Integer.class, String.class }, new Class<?>[]{ String.class, String.class, ReflectionHelperTests.Super[].class }, tc, null);
        checkMatch2(new Class<?>[]{ Integer.class, Integer.class, ReflectionHelperTests.Sub.class }, new Class<?>[]{ String.class, String.class, ReflectionHelperTests.Super[].class }, tc, REQUIRES_CONVERSION);
        checkMatch2(new Class<?>[]{ Integer.class, Integer.class, Integer.class }, new Class<?>[]{ Integer.class, String[].class }, tc, REQUIRES_CONVERSION);
        // what happens on (Integer,String) passed to (Integer[]) ?
    }

    @Test
    public void testConvertArguments() throws Exception {
        StandardTypeConverter tc = new StandardTypeConverter();
        Method oneArg = ReflectionHelperTests.TestInterface.class.getMethod("oneArg", String.class);
        Method twoArg = ReflectionHelperTests.TestInterface.class.getMethod("twoArg", String.class, String[].class);
        // basic conversion int>String
        Object[] args = new Object[]{ 3 };
        ReflectionHelper.convertArguments(tc, args, oneArg, null);
        checkArguments(args, "3");
        // varargs but nothing to convert
        args = new Object[]{ 3 };
        ReflectionHelper.convertArguments(tc, args, twoArg, 1);
        checkArguments(args, "3");
        // varargs with nothing needing conversion
        args = new Object[]{ 3, "abc", "abc" };
        ReflectionHelper.convertArguments(tc, args, twoArg, 1);
        checkArguments(args, "3", "abc", "abc");
        // varargs with conversion required
        args = new Object[]{ 3, false, 3.0 };
        ReflectionHelper.convertArguments(tc, args, twoArg, 1);
        checkArguments(args, "3", "false", "3.0");
    }

    @Test
    public void testConvertArguments2() throws Exception {
        StandardTypeConverter tc = new StandardTypeConverter();
        Method oneArg = ReflectionHelperTests.TestInterface.class.getMethod("oneArg", String.class);
        Method twoArg = ReflectionHelperTests.TestInterface.class.getMethod("twoArg", String.class, String[].class);
        // Simple conversion: int to string
        Object[] args = new Object[]{ 3 };
        ReflectionHelper.convertAllArguments(tc, args, oneArg);
        checkArguments(args, "3");
        // varargs conversion
        args = new Object[]{ 3, false, 3.0F };
        ReflectionHelper.convertAllArguments(tc, args, twoArg);
        checkArguments(args, "3", "false", "3.0");
        // varargs conversion but no varargs
        args = new Object[]{ 3 };
        ReflectionHelper.convertAllArguments(tc, args, twoArg);
        checkArguments(args, "3");
        // null value
        args = new Object[]{ 3, null, 3.0F };
        ReflectionHelper.convertAllArguments(tc, args, twoArg);
        checkArguments(args, "3", null, "3.0");
    }

    @Test
    public void testSetupArguments() {
        Object[] newArray = ReflectionHelper.setupArgumentsForVarargsInvocation(new Class<?>[]{ String[].class }, "a", "b", "c");
        Assert.assertEquals(1, newArray.length);
        Object firstParam = newArray[0];
        Assert.assertEquals(String.class, firstParam.getClass().getComponentType());
        Object[] firstParamArray = ((Object[]) (firstParam));
        Assert.assertEquals(3, firstParamArray.length);
        Assert.assertEquals("a", firstParamArray[0]);
        Assert.assertEquals("b", firstParamArray[1]);
        Assert.assertEquals("c", firstParamArray[2]);
    }

    @Test
    public void testReflectivePropertyAccessor() throws Exception {
        ReflectivePropertyAccessor rpa = new ReflectivePropertyAccessor();
        ReflectionHelperTests.Tester t = new ReflectionHelperTests.Tester();
        t.setProperty("hello");
        EvaluationContext ctx = new StandardEvaluationContext(t);
        Assert.assertTrue(rpa.canRead(ctx, t, "property"));
        Assert.assertEquals("hello", rpa.read(ctx, t, "property").getValue());
        Assert.assertEquals("hello", rpa.read(ctx, t, "property").getValue());// cached accessor used

        Assert.assertTrue(rpa.canRead(ctx, t, "field"));
        Assert.assertEquals(3, rpa.read(ctx, t, "field").getValue());
        Assert.assertEquals(3, rpa.read(ctx, t, "field").getValue());// cached accessor used

        Assert.assertTrue(rpa.canWrite(ctx, t, "property"));
        rpa.write(ctx, t, "property", "goodbye");
        rpa.write(ctx, t, "property", "goodbye");// cached accessor used

        Assert.assertTrue(rpa.canWrite(ctx, t, "field"));
        rpa.write(ctx, t, "field", 12);
        rpa.write(ctx, t, "field", 12);
        // Attempted write as first activity on this field and property to drive testing
        // of populating type descriptor cache
        rpa.write(ctx, t, "field2", 3);
        rpa.write(ctx, t, "property2", "doodoo");
        Assert.assertEquals(3, rpa.read(ctx, t, "field2").getValue());
        // Attempted read as first activity on this field and property (no canRead before them)
        Assert.assertEquals(0, rpa.read(ctx, t, "field3").getValue());
        Assert.assertEquals("doodoo", rpa.read(ctx, t, "property3").getValue());
        // Access through is method
        Assert.assertEquals(0, rpa.read(ctx, t, "field3").getValue());
        Assert.assertEquals(false, rpa.read(ctx, t, "property4").getValue());
        Assert.assertTrue(rpa.canRead(ctx, t, "property4"));
        // repro SPR-9123, ReflectivePropertyAccessor JavaBean property names compliance tests
        Assert.assertEquals("iD", rpa.read(ctx, t, "iD").getValue());
        Assert.assertTrue(rpa.canRead(ctx, t, "iD"));
        Assert.assertEquals("id", rpa.read(ctx, t, "id").getValue());
        Assert.assertTrue(rpa.canRead(ctx, t, "id"));
        Assert.assertEquals("ID", rpa.read(ctx, t, "ID").getValue());
        Assert.assertTrue(rpa.canRead(ctx, t, "ID"));
        // note: "Id" is not a valid JavaBean name, nevertheless it is treated as "id"
        Assert.assertEquals("id", rpa.read(ctx, t, "Id").getValue());
        Assert.assertTrue(rpa.canRead(ctx, t, "Id"));
        // repro SPR-10994
        Assert.assertEquals("xyZ", rpa.read(ctx, t, "xyZ").getValue());
        Assert.assertTrue(rpa.canRead(ctx, t, "xyZ"));
        Assert.assertEquals("xY", rpa.read(ctx, t, "xY").getValue());
        Assert.assertTrue(rpa.canRead(ctx, t, "xY"));
        // SPR-10122, ReflectivePropertyAccessor JavaBean property names compliance tests - setters
        rpa.write(ctx, t, "pEBS", "Test String");
        Assert.assertEquals("Test String", rpa.read(ctx, t, "pEBS").getValue());
    }

    @Test
    public void testOptimalReflectivePropertyAccessor() throws Exception {
        ReflectivePropertyAccessor rpa = new ReflectivePropertyAccessor();
        ReflectionHelperTests.Tester t = new ReflectionHelperTests.Tester();
        t.setProperty("hello");
        EvaluationContext ctx = new StandardEvaluationContext(t);
        Assert.assertTrue(rpa.canRead(ctx, t, "property"));
        Assert.assertEquals("hello", rpa.read(ctx, t, "property").getValue());
        Assert.assertEquals("hello", rpa.read(ctx, t, "property").getValue());// cached accessor used

        PropertyAccessor optA = rpa.createOptimalAccessor(ctx, t, "property");
        Assert.assertTrue(optA.canRead(ctx, t, "property"));
        Assert.assertFalse(optA.canRead(ctx, t, "property2"));
        try {
            optA.canWrite(ctx, t, "property");
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
            // success
        }
        try {
            optA.canWrite(ctx, t, "property2");
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
            // success
        }
        Assert.assertEquals("hello", optA.read(ctx, t, "property").getValue());
        Assert.assertEquals("hello", optA.read(ctx, t, "property").getValue());// cached accessor used

        try {
            optA.getSpecificTargetClasses();
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
            // success
        }
        try {
            optA.write(ctx, t, "property", null);
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
            // success
        }
        optA = rpa.createOptimalAccessor(ctx, t, "field");
        Assert.assertTrue(optA.canRead(ctx, t, "field"));
        Assert.assertFalse(optA.canRead(ctx, t, "field2"));
        try {
            optA.canWrite(ctx, t, "field");
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
            // success
        }
        try {
            optA.canWrite(ctx, t, "field2");
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
            // success
        }
        Assert.assertEquals(3, optA.read(ctx, t, "field").getValue());
        Assert.assertEquals(3, optA.read(ctx, t, "field").getValue());// cached accessor used

        try {
            optA.getSpecificTargetClasses();
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
            // success
        }
        try {
            optA.write(ctx, t, "field", null);
            Assert.fail();
        } catch (UnsupportedOperationException uoe) {
            // success
        }
    }

    public interface TestInterface {
        void oneArg(String arg1);

        void twoArg(String arg1, String... arg2);
    }

    static class Super {}

    static class Sub extends ReflectionHelperTests.Super {}

    static class Unconvertable {}

    static class Tester {
        String property;

        public int field = 3;

        public int field2;

        public int field3 = 0;

        String property2;

        String property3 = "doodoo";

        boolean property4 = false;

        String iD = "iD";

        String id = "id";

        String ID = "ID";

        String pEBS = "pEBS";

        String xY = "xY";

        String xyZ = "xyZ";

        public String getProperty() {
            return property;
        }

        public void setProperty(String value) {
            property = value;
        }

        public void setProperty2(String value) {
            property2 = value;
        }

        public String getProperty3() {
            return property3;
        }

        public boolean isProperty4() {
            return property4;
        }

        public String getiD() {
            return iD;
        }

        public String getId() {
            return id;
        }

        public String getID() {
            return ID;
        }

        public String getXY() {
            return xY;
        }

        public String getXyZ() {
            return xyZ;
        }

        public String getpEBS() {
            return pEBS;
        }

        public void setpEBS(String pEBS) {
            this.pEBS = pEBS;
        }
    }
}

