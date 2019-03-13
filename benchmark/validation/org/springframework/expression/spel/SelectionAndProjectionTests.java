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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;


/**
 *
 *
 * @author Mark Fisher
 * @author Sam Brannen
 * @author Juergen Hoeller
 */
public class SelectionAndProjectionTests {
    @Test
    public void selectionWithList() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("integers.?[#this<5]");
        EvaluationContext context = new StandardEvaluationContext(new SelectionAndProjectionTests.ListTestBean());
        Object value = expression.getValue(context);
        Assert.assertTrue((value instanceof List));
        List<?> list = ((List<?>) (value));
        Assert.assertEquals(5, list.size());
        Assert.assertEquals(0, list.get(0));
        Assert.assertEquals(1, list.get(1));
        Assert.assertEquals(2, list.get(2));
        Assert.assertEquals(3, list.get(3));
        Assert.assertEquals(4, list.get(4));
    }

    @Test
    public void selectFirstItemInList() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("integers.^[#this<5]");
        EvaluationContext context = new StandardEvaluationContext(new SelectionAndProjectionTests.ListTestBean());
        Object value = expression.getValue(context);
        Assert.assertTrue((value instanceof Integer));
        Assert.assertEquals(0, value);
    }

    @Test
    public void selectLastItemInList() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("integers.$[#this<5]");
        EvaluationContext context = new StandardEvaluationContext(new SelectionAndProjectionTests.ListTestBean());
        Object value = expression.getValue(context);
        Assert.assertTrue((value instanceof Integer));
        Assert.assertEquals(4, value);
    }

    @Test
    public void selectionWithSet() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("integers.?[#this<5]");
        EvaluationContext context = new StandardEvaluationContext(new SelectionAndProjectionTests.SetTestBean());
        Object value = expression.getValue(context);
        Assert.assertTrue((value instanceof List));
        List<?> list = ((List<?>) (value));
        Assert.assertEquals(5, list.size());
        Assert.assertEquals(0, list.get(0));
        Assert.assertEquals(1, list.get(1));
        Assert.assertEquals(2, list.get(2));
        Assert.assertEquals(3, list.get(3));
        Assert.assertEquals(4, list.get(4));
    }

    @Test
    public void selectFirstItemInSet() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("integers.^[#this<5]");
        EvaluationContext context = new StandardEvaluationContext(new SelectionAndProjectionTests.SetTestBean());
        Object value = expression.getValue(context);
        Assert.assertTrue((value instanceof Integer));
        Assert.assertEquals(0, value);
    }

    @Test
    public void selectLastItemInSet() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("integers.$[#this<5]");
        EvaluationContext context = new StandardEvaluationContext(new SelectionAndProjectionTests.SetTestBean());
        Object value = expression.getValue(context);
        Assert.assertTrue((value instanceof Integer));
        Assert.assertEquals(4, value);
    }

    @Test
    public void selectionWithIterable() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("integers.?[#this<5]");
        EvaluationContext context = new StandardEvaluationContext(new SelectionAndProjectionTests.IterableTestBean());
        Object value = expression.getValue(context);
        Assert.assertTrue((value instanceof List));
        List<?> list = ((List<?>) (value));
        Assert.assertEquals(5, list.size());
        Assert.assertEquals(0, list.get(0));
        Assert.assertEquals(1, list.get(1));
        Assert.assertEquals(2, list.get(2));
        Assert.assertEquals(3, list.get(3));
        Assert.assertEquals(4, list.get(4));
    }

    @Test
    public void selectionWithArray() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("integers.?[#this<5]");
        EvaluationContext context = new StandardEvaluationContext(new SelectionAndProjectionTests.ArrayTestBean());
        Object value = expression.getValue(context);
        Assert.assertTrue(value.getClass().isArray());
        TypedValue typedValue = new TypedValue(value);
        Assert.assertEquals(Integer.class, typedValue.getTypeDescriptor().getElementTypeDescriptor().getType());
        Integer[] array = ((Integer[]) (value));
        Assert.assertEquals(5, array.length);
        Assert.assertEquals(new Integer(0), array[0]);
        Assert.assertEquals(new Integer(1), array[1]);
        Assert.assertEquals(new Integer(2), array[2]);
        Assert.assertEquals(new Integer(3), array[3]);
        Assert.assertEquals(new Integer(4), array[4]);
    }

    @Test
    public void selectFirstItemInArray() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("integers.^[#this<5]");
        EvaluationContext context = new StandardEvaluationContext(new SelectionAndProjectionTests.ArrayTestBean());
        Object value = expression.getValue(context);
        Assert.assertTrue((value instanceof Integer));
        Assert.assertEquals(0, value);
    }

    @Test
    public void selectLastItemInArray() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("integers.$[#this<5]");
        EvaluationContext context = new StandardEvaluationContext(new SelectionAndProjectionTests.ArrayTestBean());
        Object value = expression.getValue(context);
        Assert.assertTrue((value instanceof Integer));
        Assert.assertEquals(4, value);
    }

    @Test
    public void selectionWithPrimitiveArray() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("ints.?[#this<5]");
        EvaluationContext context = new StandardEvaluationContext(new SelectionAndProjectionTests.ArrayTestBean());
        Object value = expression.getValue(context);
        Assert.assertTrue(value.getClass().isArray());
        TypedValue typedValue = new TypedValue(value);
        Assert.assertEquals(Integer.class, typedValue.getTypeDescriptor().getElementTypeDescriptor().getType());
        Integer[] array = ((Integer[]) (value));
        Assert.assertEquals(5, array.length);
        Assert.assertEquals(new Integer(0), array[0]);
        Assert.assertEquals(new Integer(1), array[1]);
        Assert.assertEquals(new Integer(2), array[2]);
        Assert.assertEquals(new Integer(3), array[3]);
        Assert.assertEquals(new Integer(4), array[4]);
    }

    @Test
    public void selectFirstItemInPrimitiveArray() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("ints.^[#this<5]");
        EvaluationContext context = new StandardEvaluationContext(new SelectionAndProjectionTests.ArrayTestBean());
        Object value = expression.getValue(context);
        Assert.assertTrue((value instanceof Integer));
        Assert.assertEquals(0, value);
    }

    @Test
    public void selectLastItemInPrimitiveArray() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("ints.$[#this<5]");
        EvaluationContext context = new StandardEvaluationContext(new SelectionAndProjectionTests.ArrayTestBean());
        Object value = expression.getValue(context);
        Assert.assertTrue((value instanceof Integer));
        Assert.assertEquals(4, value);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void selectionWithMap() {
        EvaluationContext context = new StandardEvaluationContext(new SelectionAndProjectionTests.MapTestBean());
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("colors.?[key.startsWith('b')]");
        Map<String, String> colorsMap = ((Map<String, String>) (exp.getValue(context)));
        Assert.assertEquals(3, colorsMap.size());
        Assert.assertTrue(colorsMap.containsKey("beige"));
        Assert.assertTrue(colorsMap.containsKey("blue"));
        Assert.assertTrue(colorsMap.containsKey("brown"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void selectFirstItemInMap() {
        EvaluationContext context = new StandardEvaluationContext(new SelectionAndProjectionTests.MapTestBean());
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("colors.^[key.startsWith('b')]");
        Map<String, String> colorsMap = ((Map<String, String>) (exp.getValue(context)));
        Assert.assertEquals(1, colorsMap.size());
        Assert.assertEquals("beige", colorsMap.keySet().iterator().next());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void selectLastItemInMap() {
        EvaluationContext context = new StandardEvaluationContext(new SelectionAndProjectionTests.MapTestBean());
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("colors.$[key.startsWith('b')]");
        Map<String, String> colorsMap = ((Map<String, String>) (exp.getValue(context)));
        Assert.assertEquals(1, colorsMap.size());
        Assert.assertEquals("brown", colorsMap.keySet().iterator().next());
    }

    @Test
    public void projectionWithList() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("#testList.![wrapper.value]");
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("testList", SelectionAndProjectionTests.IntegerTestBean.createList());
        Object value = expression.getValue(context);
        Assert.assertTrue((value instanceof List));
        List<?> list = ((List<?>) (value));
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(5, list.get(0));
        Assert.assertEquals(6, list.get(1));
        Assert.assertEquals(7, list.get(2));
    }

    @Test
    public void projectionWithSet() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("#testList.![wrapper.value]");
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("testList", SelectionAndProjectionTests.IntegerTestBean.createSet());
        Object value = expression.getValue(context);
        Assert.assertTrue((value instanceof List));
        List<?> list = ((List<?>) (value));
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(5, list.get(0));
        Assert.assertEquals(6, list.get(1));
        Assert.assertEquals(7, list.get(2));
    }

    @Test
    public void projectionWithIterable() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("#testList.![wrapper.value]");
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("testList", SelectionAndProjectionTests.IntegerTestBean.createIterable());
        Object value = expression.getValue(context);
        Assert.assertTrue((value instanceof List));
        List<?> list = ((List<?>) (value));
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(5, list.get(0));
        Assert.assertEquals(6, list.get(1));
        Assert.assertEquals(7, list.get(2));
    }

    @Test
    public void projectionWithArray() throws Exception {
        Expression expression = new SpelExpressionParser().parseRaw("#testArray.![wrapper.value]");
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("testArray", SelectionAndProjectionTests.IntegerTestBean.createArray());
        Object value = expression.getValue(context);
        Assert.assertTrue(value.getClass().isArray());
        TypedValue typedValue = new TypedValue(value);
        Assert.assertEquals(Number.class, typedValue.getTypeDescriptor().getElementTypeDescriptor().getType());
        Number[] array = ((Number[]) (value));
        Assert.assertEquals(3, array.length);
        Assert.assertEquals(new Integer(5), array[0]);
        Assert.assertEquals(5.9F, array[1]);
        Assert.assertEquals(new Integer(7), array[2]);
    }

    static class ListTestBean {
        private final List<Integer> integers = new ArrayList<>();

        ListTestBean() {
            for (int i = 0; i < 10; i++) {
                integers.add(i);
            }
        }

        public List<Integer> getIntegers() {
            return integers;
        }
    }

    static class SetTestBean {
        private final Set<Integer> integers = new LinkedHashSet<>();

        SetTestBean() {
            for (int i = 0; i < 10; i++) {
                integers.add(i);
            }
        }

        public Set<Integer> getIntegers() {
            return integers;
        }
    }

    static class IterableTestBean {
        private final Set<Integer> integers = new LinkedHashSet<>();

        IterableTestBean() {
            for (int i = 0; i < 10; i++) {
                integers.add(i);
            }
        }

        public Iterable<Integer> getIntegers() {
            return new Iterable<Integer>() {
                @Override
                public Iterator<Integer> iterator() {
                    return integers.iterator();
                }
            };
        }
    }

    static class ArrayTestBean {
        private final int[] ints = new int[10];

        private final Integer[] integers = new Integer[10];

        ArrayTestBean() {
            for (int i = 0; i < 10; i++) {
                ints[i] = i;
                integers[i] = i;
            }
        }

        public int[] getInts() {
            return ints;
        }

        public Integer[] getIntegers() {
            return integers;
        }
    }

    static class MapTestBean {
        private final Map<String, String> colors = new TreeMap<>();

        MapTestBean() {
            // colors.put("black", "schwarz");
            colors.put("red", "rot");
            colors.put("brown", "braun");
            colors.put("blue", "blau");
            colors.put("yellow", "gelb");
            colors.put("beige", "beige");
        }

        public Map<String, String> getColors() {
            return colors;
        }
    }

    static class IntegerTestBean {
        private final SelectionAndProjectionTests.IntegerWrapper wrapper;

        IntegerTestBean(Number value) {
            this.wrapper = new SelectionAndProjectionTests.IntegerWrapper(value);
        }

        public SelectionAndProjectionTests.IntegerWrapper getWrapper() {
            return this.wrapper;
        }

        static List<SelectionAndProjectionTests.IntegerTestBean> createList() {
            List<SelectionAndProjectionTests.IntegerTestBean> list = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                list.add(new SelectionAndProjectionTests.IntegerTestBean((i + 5)));
            }
            return list;
        }

        static Set<SelectionAndProjectionTests.IntegerTestBean> createSet() {
            Set<SelectionAndProjectionTests.IntegerTestBean> set = new LinkedHashSet<>();
            for (int i = 0; i < 3; i++) {
                set.add(new SelectionAndProjectionTests.IntegerTestBean((i + 5)));
            }
            return set;
        }

        static Iterable<SelectionAndProjectionTests.IntegerTestBean> createIterable() {
            final Set<SelectionAndProjectionTests.IntegerTestBean> set = SelectionAndProjectionTests.IntegerTestBean.createSet();
            return new Iterable<SelectionAndProjectionTests.IntegerTestBean>() {
                @Override
                public Iterator<SelectionAndProjectionTests.IntegerTestBean> iterator() {
                    return set.iterator();
                }
            };
        }

        static SelectionAndProjectionTests.IntegerTestBean[] createArray() {
            SelectionAndProjectionTests.IntegerTestBean[] array = new SelectionAndProjectionTests.IntegerTestBean[3];
            for (int i = 0; i < 3; i++) {
                if (i == 1) {
                    array[i] = new SelectionAndProjectionTests.IntegerTestBean(5.9F);
                } else {
                    array[i] = new SelectionAndProjectionTests.IntegerTestBean((i + 5));
                }
            }
            return array;
        }
    }

    static class IntegerWrapper {
        private final Number value;

        IntegerWrapper(Number value) {
            this.value = value;
        }

        public Number getValue() {
            return this.value;
        }
    }
}

