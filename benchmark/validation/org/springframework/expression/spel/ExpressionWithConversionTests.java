/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.expression.spel;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.spel.support.StandardEvaluationContext;


/**
 * Expression evaluation where the TypeConverter plugged in is the
 * {@link org.springframework.core.convert.support.GenericConversionService}.
 *
 * @author Andy Clement
 * @author Dave Syer
 */
public class ExpressionWithConversionTests extends AbstractExpressionTests {
    private static List<String> listOfString = new ArrayList<>();

    private static TypeDescriptor typeDescriptorForListOfString = null;

    private static List<Integer> listOfInteger = new ArrayList<>();

    private static TypeDescriptor typeDescriptorForListOfInteger = null;

    static {
        ExpressionWithConversionTests.listOfString.add("1");
        ExpressionWithConversionTests.listOfString.add("2");
        ExpressionWithConversionTests.listOfString.add("3");
        ExpressionWithConversionTests.listOfInteger.add(4);
        ExpressionWithConversionTests.listOfInteger.add(5);
        ExpressionWithConversionTests.listOfInteger.add(6);
    }

    /**
     * Test the service can convert what we are about to use in the expression evaluation tests.
     */
    @Test
    public void testConversionsAvailable() throws Exception {
        ExpressionWithConversionTests.TypeConvertorUsingConversionService tcs = new ExpressionWithConversionTests.TypeConvertorUsingConversionService();
        // ArrayList containing List<Integer> to List<String>
        Class<?> clazz = ExpressionWithConversionTests.typeDescriptorForListOfString.getElementTypeDescriptor().getType();
        Assert.assertEquals(String.class, clazz);
        List<?> l = ((List<?>) (tcs.convertValue(ExpressionWithConversionTests.listOfInteger, TypeDescriptor.forObject(ExpressionWithConversionTests.listOfInteger), ExpressionWithConversionTests.typeDescriptorForListOfString)));
        Assert.assertNotNull(l);
        // ArrayList containing List<String> to List<Integer>
        clazz = ExpressionWithConversionTests.typeDescriptorForListOfInteger.getElementTypeDescriptor().getType();
        Assert.assertEquals(Integer.class, clazz);
        l = ((List<?>) (tcs.convertValue(ExpressionWithConversionTests.listOfString, TypeDescriptor.forObject(ExpressionWithConversionTests.listOfString), ExpressionWithConversionTests.typeDescriptorForListOfString)));
        Assert.assertNotNull(l);
    }

    @Test
    public void testSetParameterizedList() throws Exception {
        StandardEvaluationContext context = TestScenarioCreator.getTestEvaluationContext();
        Expression e = parser.parseExpression("listOfInteger.size()");
        Assert.assertEquals(0, e.getValue(context, Integer.class).intValue());
        context.setTypeConverter(new ExpressionWithConversionTests.TypeConvertorUsingConversionService());
        // Assign a List<String> to the List<Integer> field - the component elements should be converted
        parser.parseExpression("listOfInteger").setValue(context, ExpressionWithConversionTests.listOfString);
        Assert.assertEquals(3, e.getValue(context, Integer.class).intValue());// size now 3

        Class<?> clazz = parser.parseExpression("listOfInteger[1].getClass()").getValue(context, Class.class);// element type correctly Integer

        Assert.assertEquals(Integer.class, clazz);
    }

    @Test
    public void testCoercionToCollectionOfPrimitive() throws Exception {
        class TestTarget {
            @SuppressWarnings("unused")
            public int sum(Collection<Integer> numbers) {
                int total = 0;
                for (int i : numbers) {
                    total += i;
                }
                return total;
            }
        }
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        TypeDescriptor collectionType = new TypeDescriptor(new MethodParameter(TestTarget.class.getDeclaredMethod("sum", Collection.class), 0));
        // The type conversion is possible
        Assert.assertTrue(evaluationContext.getTypeConverter().canConvert(TypeDescriptor.valueOf(String.class), collectionType));
        // ... and it can be done successfully
        Assert.assertEquals("[1, 2, 3, 4]", evaluationContext.getTypeConverter().convertValue("1,2,3,4", TypeDescriptor.valueOf(String.class), collectionType).toString());
        evaluationContext.setVariable("target", new TestTarget());
        // OK up to here, so the evaluation should be fine...
        // ... but this fails
        int result = ((Integer) (parser.parseExpression("#target.sum(#root)").getValue(evaluationContext, "1,2,3,4")));
        Assert.assertEquals(("Wrong result: " + result), 10, result);
    }

    @Test
    public void testConvert() {
        ExpressionWithConversionTests.Foo root = new ExpressionWithConversionTests.Foo("bar");
        Collection<String> foos = Collections.singletonList("baz");
        StandardEvaluationContext context = new StandardEvaluationContext(root);
        // property access
        Expression expression = parser.parseExpression("foos");
        expression.setValue(context, foos);
        ExpressionWithConversionTests.Foo baz = root.getFoos().iterator().next();
        Assert.assertEquals("baz", baz.value);
        // method call
        expression = parser.parseExpression("setFoos(#foos)");
        context.setVariable("foos", foos);
        expression.getValue(context);
        baz = root.getFoos().iterator().next();
        Assert.assertEquals("baz", baz.value);
        // method call with result from method call
        expression = parser.parseExpression("setFoos(getFoosAsStrings())");
        expression.getValue(context);
        baz = root.getFoos().iterator().next();
        Assert.assertEquals("baz", baz.value);
        // method call with result from method call
        expression = parser.parseExpression("setFoos(getFoosAsObjects())");
        expression.getValue(context);
        baz = root.getFoos().iterator().next();
        Assert.assertEquals("baz", baz.value);
    }

    /**
     * Type converter that uses the core conversion service.
     */
    private static class TypeConvertorUsingConversionService implements TypeConverter {
        private final ConversionService service = new DefaultConversionService();

        @Override
        public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return this.service.canConvert(sourceType, targetType);
        }

        @Override
        public Object convertValue(Object value, TypeDescriptor sourceType, TypeDescriptor targetType) throws EvaluationException {
            return this.service.convert(value, sourceType, targetType);
        }
    }

    public static class Foo {
        public final String value;

        private Collection<ExpressionWithConversionTests.Foo> foos;

        public Foo(String value) {
            this.value = value;
        }

        public void setFoos(Collection<ExpressionWithConversionTests.Foo> foos) {
            this.foos = foos;
        }

        public Collection<ExpressionWithConversionTests.Foo> getFoos() {
            return this.foos;
        }

        public Collection<String> getFoosAsStrings() {
            return Collections.singletonList("baz");
        }

        public Collection<?> getFoosAsObjects() {
            return Collections.singletonList("baz");
        }
    }
}

