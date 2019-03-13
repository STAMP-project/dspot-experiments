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


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;


@SuppressWarnings("rawtypes")
public class IndexingTests {
    @Test
    public void indexIntoGenericPropertyContainingMap() {
        Map<String, String> property = new HashMap<>();
        property.put("foo", "bar");
        this.property = property;
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("property");
        Assert.assertEquals("@org.springframework.expression.spel.IndexingTests$FieldAnnotation java.util.HashMap<?, ?>", expression.getValueTypeDescriptor(this).toString());
        Assert.assertEquals(property, expression.getValue(this));
        Assert.assertEquals(property, expression.getValue(this, Map.class));
        expression = parser.parseExpression("property['foo']");
        Assert.assertEquals("bar", expression.getValue(this));
    }

    @IndexingTests.FieldAnnotation
    public Object property;

    @Test
    public void indexIntoGenericPropertyContainingMapObject() {
        Map<String, Map<String, String>> property = new HashMap<>();
        Map<String, String> map = new HashMap<>();
        map.put("foo", "bar");
        property.put("property", map);
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.addPropertyAccessor(new IndexingTests.MapAccessor());
        context.setRootObject(property);
        Expression expression = parser.parseExpression("property");
        Assert.assertEquals("java.util.HashMap<?, ?>", expression.getValueTypeDescriptor(context).toString());
        Assert.assertEquals(map, expression.getValue(context));
        Assert.assertEquals(map, expression.getValue(context, Map.class));
        expression = parser.parseExpression("property['foo']");
        Assert.assertEquals("bar", expression.getValue(context));
    }

    public static class MapAccessor implements PropertyAccessor {
        @Override
        public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
            return ((Map<?, ?>) (target)).containsKey(name);
        }

        @Override
        public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
            return new TypedValue(((Map<?, ?>) (target)).get(name));
        }

        @Override
        public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
            ((Map) (target)).put(name, newValue);
        }

        @Override
        public Class<?>[] getSpecificTargetClasses() {
            return new Class<?>[]{ Map.class };
        }
    }

    @Test
    public void setGenericPropertyContainingMap() {
        Map<String, String> property = new HashMap<>();
        property.put("foo", "bar");
        this.property = property;
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("property");
        Assert.assertEquals("@org.springframework.expression.spel.IndexingTests$FieldAnnotation java.util.HashMap<?, ?>", expression.getValueTypeDescriptor(this).toString());
        Assert.assertEquals(property, expression.getValue(this));
        expression = parser.parseExpression("property['foo']");
        Assert.assertEquals("bar", expression.getValue(this));
        expression.setValue(this, "baz");
        Assert.assertEquals("baz", expression.getValue(this));
    }

    @Test
    public void setPropertyContainingMap() {
        Map<Integer, Integer> property = new HashMap<>();
        property.put(9, 3);
        this.parameterizedMap = property;
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("parameterizedMap");
        Assert.assertEquals("java.util.HashMap<java.lang.Integer, java.lang.Integer>", expression.getValueTypeDescriptor(this).toString());
        Assert.assertEquals(property, expression.getValue(this));
        expression = parser.parseExpression("parameterizedMap['9']");
        Assert.assertEquals(3, expression.getValue(this));
        expression.setValue(this, "37");
        Assert.assertEquals(37, expression.getValue(this));
    }

    public Map<Integer, Integer> parameterizedMap;

    @Test
    public void setPropertyContainingMapAutoGrow() {
        SpelExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, false));
        Expression expression = parser.parseExpression("parameterizedMap");
        Assert.assertEquals("java.util.Map<java.lang.Integer, java.lang.Integer>", expression.getValueTypeDescriptor(this).toString());
        Assert.assertEquals(property, expression.getValue(this));
        expression = parser.parseExpression("parameterizedMap['9']");
        Assert.assertEquals(null, expression.getValue(this));
        expression.setValue(this, "37");
        Assert.assertEquals(37, expression.getValue(this));
    }

    @Test
    public void indexIntoGenericPropertyContainingList() {
        List<String> property = new ArrayList<>();
        property.add("bar");
        this.property = property;
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("property");
        Assert.assertEquals("@org.springframework.expression.spel.IndexingTests$FieldAnnotation java.util.ArrayList<?>", expression.getValueTypeDescriptor(this).toString());
        Assert.assertEquals(property, expression.getValue(this));
        expression = parser.parseExpression("property[0]");
        Assert.assertEquals("bar", expression.getValue(this));
    }

    @Test
    public void setGenericPropertyContainingList() {
        List<Integer> property = new ArrayList<>();
        property.add(3);
        this.property = property;
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("property");
        Assert.assertEquals("@org.springframework.expression.spel.IndexingTests$FieldAnnotation java.util.ArrayList<?>", expression.getValueTypeDescriptor(this).toString());
        Assert.assertEquals(property, expression.getValue(this));
        expression = parser.parseExpression("property[0]");
        Assert.assertEquals(3, expression.getValue(this));
        expression.setValue(this, "4");
        Assert.assertEquals("4", expression.getValue(this));
    }

    @Test
    public void setGenericPropertyContainingListAutogrow() {
        List<Integer> property = new ArrayList<>();
        this.property = property;
        SpelExpressionParser parser = new SpelExpressionParser(new SpelParserConfiguration(true, true));
        Expression expression = parser.parseExpression("property");
        Assert.assertEquals("@org.springframework.expression.spel.IndexingTests$FieldAnnotation java.util.ArrayList<?>", expression.getValueTypeDescriptor(this).toString());
        Assert.assertEquals(property, expression.getValue(this));
        expression = parser.parseExpression("property[0]");
        try {
            expression.setValue(this, "4");
        } catch (EvaluationException ex) {
            Assert.assertTrue(ex.getMessage().startsWith("EL1053E"));
        }
    }

    @Test
    public void indexIntoPropertyContainingList() {
        List<Integer> property = new ArrayList<>();
        property.add(3);
        this.parameterizedList = property;
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("parameterizedList");
        Assert.assertEquals("java.util.ArrayList<java.lang.Integer>", expression.getValueTypeDescriptor(this).toString());
        Assert.assertEquals(property, expression.getValue(this));
        expression = parser.parseExpression("parameterizedList[0]");
        Assert.assertEquals(3, expression.getValue(this));
    }

    public List<Integer> parameterizedList;

    @Test
    public void indexIntoPropertyContainingListOfList() {
        List<List<Integer>> property = new ArrayList<>();
        property.add(Arrays.asList(3));
        this.parameterizedListOfList = property;
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("parameterizedListOfList[0]");
        Assert.assertEquals("java.util.Arrays$ArrayList<java.lang.Integer>", expression.getValueTypeDescriptor(this).toString());
        Assert.assertEquals(property.get(0), expression.getValue(this));
        expression = parser.parseExpression("parameterizedListOfList[0][0]");
        Assert.assertEquals(3, expression.getValue(this));
    }

    public List<List<Integer>> parameterizedListOfList;

    @Test
    public void setPropertyContainingList() {
        List<Integer> property = new ArrayList<>();
        property.add(3);
        this.parameterizedList = property;
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("parameterizedList");
        Assert.assertEquals("java.util.ArrayList<java.lang.Integer>", expression.getValueTypeDescriptor(this).toString());
        Assert.assertEquals(property, expression.getValue(this));
        expression = parser.parseExpression("parameterizedList[0]");
        Assert.assertEquals(3, expression.getValue(this));
        expression.setValue(this, "4");
        Assert.assertEquals(4, expression.getValue(this));
    }

    @Test
    public void indexIntoGenericPropertyContainingNullList() {
        SpelParserConfiguration configuration = new SpelParserConfiguration(true, true);
        SpelExpressionParser parser = new SpelExpressionParser(configuration);
        Expression expression = parser.parseExpression("property");
        Assert.assertEquals("@org.springframework.expression.spel.IndexingTests$FieldAnnotation java.lang.Object", expression.getValueTypeDescriptor(this).toString());
        Assert.assertEquals(property, expression.getValue(this));
        expression = parser.parseExpression("property[0]");
        try {
            Assert.assertEquals("bar", expression.getValue(this));
        } catch (EvaluationException ex) {
            Assert.assertTrue(ex.getMessage().startsWith("EL1027E"));
        }
    }

    @Test
    public void indexIntoGenericPropertyContainingGrowingList() {
        List<String> property = new ArrayList<>();
        this.property = property;
        SpelParserConfiguration configuration = new SpelParserConfiguration(true, true);
        SpelExpressionParser parser = new SpelExpressionParser(configuration);
        Expression expression = parser.parseExpression("property");
        Assert.assertEquals("@org.springframework.expression.spel.IndexingTests$FieldAnnotation java.util.ArrayList<?>", expression.getValueTypeDescriptor(this).toString());
        Assert.assertEquals(property, expression.getValue(this));
        expression = parser.parseExpression("property[0]");
        try {
            Assert.assertEquals("bar", expression.getValue(this));
        } catch (EvaluationException ex) {
            Assert.assertTrue(ex.getMessage().startsWith("EL1053E"));
        }
    }

    @Test
    public void indexIntoGenericPropertyContainingGrowingList2() {
        List<String> property2 = new ArrayList<>();
        this.property2 = property2;
        SpelParserConfiguration configuration = new SpelParserConfiguration(true, true);
        SpelExpressionParser parser = new SpelExpressionParser(configuration);
        Expression expression = parser.parseExpression("property2");
        Assert.assertEquals("java.util.ArrayList<?>", expression.getValueTypeDescriptor(this).toString());
        Assert.assertEquals(property2, expression.getValue(this));
        expression = parser.parseExpression("property2[0]");
        try {
            Assert.assertEquals("bar", expression.getValue(this));
        } catch (EvaluationException ex) {
            Assert.assertTrue(ex.getMessage().startsWith("EL1053E"));
        }
    }

    public List property2;

    @Test
    public void indexIntoGenericPropertyContainingArray() {
        String[] property = new String[]{ "bar" };
        this.property = property;
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("property");
        Assert.assertEquals("@org.springframework.expression.spel.IndexingTests$FieldAnnotation java.lang.String[]", expression.getValueTypeDescriptor(this).toString());
        Assert.assertEquals(property, expression.getValue(this));
        expression = parser.parseExpression("property[0]");
        Assert.assertEquals("bar", expression.getValue(this));
    }

    @Test
    public void emptyList() {
        listOfScalarNotGeneric = new ArrayList();
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("listOfScalarNotGeneric");
        Assert.assertEquals("java.util.ArrayList<?>", expression.getValueTypeDescriptor(this).toString());
        Assert.assertEquals("", expression.getValue(this, String.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void resolveCollectionElementType() {
        listNotGeneric = new ArrayList(2);
        listNotGeneric.add(5);
        listNotGeneric.add(6);
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("listNotGeneric");
        Assert.assertEquals("@org.springframework.expression.spel.IndexingTests$FieldAnnotation java.util.ArrayList<?>", expression.getValueTypeDescriptor(this).toString());
        Assert.assertEquals("5,6", expression.getValue(this, String.class));
    }

    @Test
    public void resolveCollectionElementTypeNull() {
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("listNotGeneric");
        Assert.assertEquals("@org.springframework.expression.spel.IndexingTests$FieldAnnotation java.util.List<?>", expression.getValueTypeDescriptor(this).toString());
    }

    @IndexingTests.FieldAnnotation
    public List listNotGeneric;

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FieldAnnotation {}

    @SuppressWarnings("unchecked")
    @Test
    public void resolveMapKeyValueTypes() {
        mapNotGeneric = new HashMap();
        mapNotGeneric.put("baseAmount", 3.11);
        mapNotGeneric.put("bonusAmount", 7.17);
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("mapNotGeneric");
        Assert.assertEquals("@org.springframework.expression.spel.IndexingTests$FieldAnnotation java.util.HashMap<?, ?>", expression.getValueTypeDescriptor(this).toString());
    }

    @IndexingTests.FieldAnnotation
    public Map mapNotGeneric;

    @SuppressWarnings("unchecked")
    @Test
    public void testListOfScalar() {
        listOfScalarNotGeneric = new ArrayList(1);
        listOfScalarNotGeneric.add("5");
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("listOfScalarNotGeneric[0]");
        Assert.assertEquals(new Integer(5), expression.getValue(this, Integer.class));
    }

    public List listOfScalarNotGeneric;

    @SuppressWarnings("unchecked")
    @Test
    public void testListsOfMap() {
        listOfMapsNotGeneric = new ArrayList();
        Map map = new HashMap();
        map.put("fruit", "apple");
        listOfMapsNotGeneric.add(map);
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("listOfMapsNotGeneric[0]['fruit']");
        Assert.assertEquals("apple", expression.getValue(this, String.class));
    }

    public List listOfMapsNotGeneric;
}

