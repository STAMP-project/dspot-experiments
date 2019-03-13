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
package org.springframework.core.convert.support;


import GenericConverter.ConvertiblePair;
import java.awt.Color;
import java.awt.SystemColor;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.tests.Assume;
import org.springframework.tests.TestGroup;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;


/**
 * Unit tests for {@link GenericConversionService}.
 *
 * <p>In this package for access to package-local converter implementations.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @author David Haraburda
 * @author Sam Brannen
 */
public class GenericConversionServiceTests {
    private final GenericConversionService conversionService = new GenericConversionService();

    @Test
    public void canConvert() {
        Assert.assertFalse(conversionService.canConvert(String.class, Integer.class));
        conversionService.addConverterFactory(new StringToNumberConverterFactory());
        Assert.assertTrue(conversionService.canConvert(String.class, Integer.class));
    }

    @Test
    public void canConvertAssignable() {
        Assert.assertTrue(conversionService.canConvert(String.class, String.class));
        Assert.assertTrue(conversionService.canConvert(Integer.class, Number.class));
        Assert.assertTrue(conversionService.canConvert(boolean.class, boolean.class));
        Assert.assertTrue(conversionService.canConvert(boolean.class, Boolean.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void canConvertFromClassSourceTypeToNullTargetType() {
        conversionService.canConvert(String.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void canConvertFromTypeDescriptorSourceTypeToNullTargetType() {
        conversionService.canConvert(TypeDescriptor.valueOf(String.class), null);
    }

    @Test
    public void canConvertNullSourceType() {
        Assert.assertTrue(conversionService.canConvert(null, Integer.class));
        Assert.assertTrue(conversionService.canConvert(null, TypeDescriptor.valueOf(Integer.class)));
    }

    @Test
    public void convert() {
        conversionService.addConverterFactory(new StringToNumberConverterFactory());
        Assert.assertEquals(Integer.valueOf(3), conversionService.convert("3", Integer.class));
    }

    @Test
    public void convertNullSource() {
        Assert.assertEquals(null, conversionService.convert(null, Integer.class));
    }

    @Test(expected = ConversionFailedException.class)
    public void convertNullSourcePrimitiveTarget() {
        conversionService.convert(null, int.class);
    }

    @Test(expected = ConversionFailedException.class)
    public void convertNullSourcePrimitiveTargetTypeDescriptor() {
        conversionService.convert(null, TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(int.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertNotNullSourceNullSourceTypeDescriptor() {
        conversionService.convert("3", null, TypeDescriptor.valueOf(int.class));
    }

    @Test
    public void convertAssignableSource() {
        Assert.assertEquals(Boolean.FALSE, conversionService.convert(false, boolean.class));
        Assert.assertEquals(Boolean.FALSE, conversionService.convert(false, Boolean.class));
    }

    @Test(expected = ConverterNotFoundException.class)
    public void converterNotFound() {
        conversionService.convert("3", Integer.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addConverterNoSourceTargetClassInfoAvailable() {
        conversionService.addConverter(new GenericConversionServiceTests.UntypedConverter());
    }

    @Test
    public void sourceTypeIsVoid() {
        Assert.assertFalse(conversionService.canConvert(void.class, String.class));
    }

    @Test
    public void targetTypeIsVoid() {
        Assert.assertFalse(conversionService.canConvert(String.class, void.class));
    }

    @Test
    public void convertNull() {
        Assert.assertNull(conversionService.convert(null, Integer.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertToNullTargetClass() {
        conversionService.convert("3", ((Class<?>) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertToNullTargetTypeDescriptor() {
        conversionService.convert("3", TypeDescriptor.valueOf(String.class), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertWrongSourceTypeDescriptor() {
        conversionService.convert("3", TypeDescriptor.valueOf(Integer.class), TypeDescriptor.valueOf(Long.class));
    }

    @Test(expected = ConversionFailedException.class)
    public void convertWrongTypeArgument() {
        conversionService.addConverterFactory(new StringToNumberConverterFactory());
        conversionService.convert("BOGUS", Integer.class);
    }

    @Test
    public void convertSuperSourceType() {
        conversionService.addConverter(new org.springframework.core.convert.converter.Converter<CharSequence, Integer>() {
            @Override
            public Integer convert(CharSequence source) {
                return Integer.valueOf(source.toString());
            }
        });
        Integer result = conversionService.convert("3", Integer.class);
        Assert.assertEquals(Integer.valueOf(3), result);
    }

    // SPR-8718
    @Test(expected = ConverterNotFoundException.class)
    public void convertSuperTarget() {
        conversionService.addConverter(new GenericConversionServiceTests.ColorConverter());
        conversionService.convert("#000000", SystemColor.class);
    }

    @Test
    public void convertObjectToPrimitive() {
        Assert.assertFalse(conversionService.canConvert(String.class, boolean.class));
        conversionService.addConverter(new StringToBooleanConverter());
        Assert.assertTrue(conversionService.canConvert(String.class, boolean.class));
        Boolean b = conversionService.convert("true", boolean.class);
        Assert.assertTrue(b);
        Assert.assertTrue(conversionService.canConvert(TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(boolean.class)));
        b = ((Boolean) (conversionService.convert("true", TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(boolean.class))));
        Assert.assertTrue(b);
    }

    @Test
    public void convertObjectToPrimitiveViaConverterFactory() {
        Assert.assertFalse(conversionService.canConvert(String.class, int.class));
        conversionService.addConverterFactory(new StringToNumberConverterFactory());
        Assert.assertTrue(conversionService.canConvert(String.class, int.class));
        Integer three = conversionService.convert("3", int.class);
        Assert.assertEquals(3, three.intValue());
    }

    @Test(expected = ConverterNotFoundException.class)
    public void genericConverterDelegatingBackToConversionServiceConverterNotFound() {
        conversionService.addConverter(new ObjectToArrayConverter(conversionService));
        Assert.assertFalse(conversionService.canConvert(String.class, Integer[].class));
        conversionService.convert("3,4,5", Integer[].class);
    }

    @Test
    public void testListToIterableConversion() {
        List<Object> raw = new ArrayList<>();
        raw.add("one");
        raw.add("two");
        Object converted = conversionService.convert(raw, Iterable.class);
        Assert.assertSame(raw, converted);
    }

    @Test
    public void testListToObjectConversion() {
        List<Object> raw = new ArrayList<>();
        raw.add("one");
        raw.add("two");
        Object converted = conversionService.convert(raw, Object.class);
        Assert.assertSame(raw, converted);
    }

    @Test
    public void testMapToObjectConversion() {
        Map<Object, Object> raw = new HashMap<>();
        raw.put("key", "value");
        Object converted = conversionService.convert(raw, Object.class);
        Assert.assertSame(raw, converted);
    }

    @Test
    public void testInterfaceToString() {
        conversionService.addConverter(new GenericConversionServiceTests.MyBaseInterfaceToStringConverter());
        conversionService.addConverter(new ObjectToStringConverter());
        Object converted = conversionService.convert(new GenericConversionServiceTests.MyInterfaceImplementer(), String.class);
        Assert.assertEquals("RESULT", converted);
    }

    @Test
    public void testInterfaceArrayToStringArray() {
        conversionService.addConverter(new GenericConversionServiceTests.MyBaseInterfaceToStringConverter());
        conversionService.addConverter(new ArrayToArrayConverter(conversionService));
        String[] converted = conversionService.convert(new GenericConversionServiceTests.MyInterface[]{ new GenericConversionServiceTests.MyInterfaceImplementer() }, String[].class);
        Assert.assertEquals("RESULT", converted[0]);
    }

    @Test
    public void testObjectArrayToStringArray() {
        conversionService.addConverter(new GenericConversionServiceTests.MyBaseInterfaceToStringConverter());
        conversionService.addConverter(new ArrayToArrayConverter(conversionService));
        String[] converted = conversionService.convert(new GenericConversionServiceTests.MyInterfaceImplementer[]{ new GenericConversionServiceTests.MyInterfaceImplementer() }, String[].class);
        Assert.assertEquals("RESULT", converted[0]);
    }

    @Test
    public void testStringArrayToResourceArray() {
        conversionService.addConverter(new GenericConversionServiceTests.MyStringArrayToResourceArrayConverter());
        Resource[] converted = conversionService.convert(new String[]{ "x1", "z3" }, Resource[].class);
        List<String> descriptions = Arrays.stream(converted).map(Resource::getDescription).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("1", "3"), descriptions);
    }

    @Test
    public void testStringArrayToIntegerArray() {
        conversionService.addConverter(new GenericConversionServiceTests.MyStringArrayToIntegerArrayConverter());
        Integer[] converted = conversionService.convert(new String[]{ "x1", "z3" }, Integer[].class);
        Assert.assertArrayEquals(new Integer[]{ 1, 3 }, converted);
    }

    @Test
    public void testStringToIntegerArray() {
        conversionService.addConverter(new GenericConversionServiceTests.MyStringToIntegerArrayConverter());
        Integer[] converted = conversionService.convert("x1,z3", Integer[].class);
        Assert.assertArrayEquals(new Integer[]{ 1, 3 }, converted);
    }

    @Test
    public void testWildcardMap() throws Exception {
        Map<String, String> input = new LinkedHashMap<>();
        input.put("key", "value");
        Object converted = conversionService.convert(input, TypeDescriptor.forObject(input), new TypeDescriptor(getClass().getField("wildcardMap")));
        Assert.assertEquals(input, converted);
    }

    @Test
    public void testStringToString() {
        String value = "myValue";
        String result = conversionService.convert(value, String.class);
        Assert.assertSame(value, result);
    }

    @Test
    public void testStringToObject() {
        String value = "myValue";
        Object result = conversionService.convert(value, Object.class);
        Assert.assertSame(value, result);
    }

    @Test
    public void testIgnoreCopyConstructor() {
        GenericConversionServiceTests.WithCopyConstructor value = new GenericConversionServiceTests.WithCopyConstructor();
        Object result = conversionService.convert(value, GenericConversionServiceTests.WithCopyConstructor.class);
        Assert.assertSame(value, result);
    }

    @Test
    public void testPerformance2() throws Exception {
        Assume.group(TestGroup.PERFORMANCE);
        StopWatch watch = new StopWatch("list<string> -> list<integer> conversionPerformance");
        watch.start("convert 4,000,000 with conversion service");
        List<String> source = new LinkedList<>();
        source.add("1");
        source.add("2");
        source.add("3");
        TypeDescriptor td = new TypeDescriptor(getClass().getField("list"));
        for (int i = 0; i < 1000000; i++) {
            conversionService.convert(source, TypeDescriptor.forObject(source), td);
        }
        watch.stop();
        watch.start("convert 4,000,000 manually");
        for (int i = 0; i < 4000000; i++) {
            List<Integer> target = new ArrayList<>(source.size());
            for (String element : source) {
                target.add(Integer.valueOf(element));
            }
        }
        watch.stop();
        // System.out.println(watch.prettyPrint());
    }

    @Test
    public void testPerformance3() throws Exception {
        Assume.group(TestGroup.PERFORMANCE);
        StopWatch watch = new StopWatch("map<string, string> -> map<string, integer> conversionPerformance");
        watch.start("convert 4,000,000 with conversion service");
        Map<String, String> source = new HashMap<>();
        source.put("1", "1");
        source.put("2", "2");
        source.put("3", "3");
        TypeDescriptor td = new TypeDescriptor(getClass().getField("map"));
        for (int i = 0; i < 1000000; i++) {
            conversionService.convert(source, TypeDescriptor.forObject(source), td);
        }
        watch.stop();
        watch.start("convert 4,000,000 manually");
        for (int i = 0; i < 4000000; i++) {
            Map<String, Integer> target = new HashMap<>(source.size());
            source.forEach(( k, v) -> target.put(k, Integer.valueOf(v)));
        }
        watch.stop();
        // System.out.println(watch.prettyPrint());
    }

    @Test
    public void emptyListToArray() {
        conversionService.addConverter(new CollectionToArrayConverter(conversionService));
        conversionService.addConverterFactory(new StringToNumberConverterFactory());
        List<String> list = new ArrayList<>();
        TypeDescriptor sourceType = TypeDescriptor.forObject(list);
        TypeDescriptor targetType = TypeDescriptor.valueOf(String[].class);
        Assert.assertTrue(conversionService.canConvert(sourceType, targetType));
        Assert.assertEquals(0, ((String[]) (conversionService.convert(list, sourceType, targetType))).length);
    }

    @Test
    public void emptyListToObject() {
        conversionService.addConverter(new CollectionToObjectConverter(conversionService));
        conversionService.addConverterFactory(new StringToNumberConverterFactory());
        List<String> list = new ArrayList<>();
        TypeDescriptor sourceType = TypeDescriptor.forObject(list);
        TypeDescriptor targetType = TypeDescriptor.valueOf(Integer.class);
        Assert.assertTrue(conversionService.canConvert(sourceType, targetType));
        Assert.assertNull(conversionService.convert(list, sourceType, targetType));
    }

    @Test
    public void stringToArrayCanConvert() {
        conversionService.addConverter(new StringToArrayConverter(conversionService));
        Assert.assertFalse(conversionService.canConvert(String.class, Integer[].class));
        conversionService.addConverterFactory(new StringToNumberConverterFactory());
        Assert.assertTrue(conversionService.canConvert(String.class, Integer[].class));
    }

    @Test
    public void stringToCollectionCanConvert() throws Exception {
        conversionService.addConverter(new StringToCollectionConverter(conversionService));
        Assert.assertTrue(conversionService.canConvert(String.class, Collection.class));
        TypeDescriptor targetType = new TypeDescriptor(getClass().getField("integerCollection"));
        Assert.assertFalse(conversionService.canConvert(TypeDescriptor.valueOf(String.class), targetType));
        conversionService.addConverterFactory(new StringToNumberConverterFactory());
        Assert.assertTrue(conversionService.canConvert(TypeDescriptor.valueOf(String.class), targetType));
    }

    @Test
    public void testConvertiblePairsInSet() {
        Set<GenericConverter.ConvertiblePair> set = new HashSet<>();
        set.add(new GenericConverter.ConvertiblePair(Number.class, String.class));
        assert set.contains(new GenericConverter.ConvertiblePair(Number.class, String.class));
    }

    @Test
    public void testConvertiblePairEqualsAndHash() {
        GenericConverter.ConvertiblePair pair = new GenericConverter.ConvertiblePair(Number.class, String.class);
        GenericConverter.ConvertiblePair pairEqual = new GenericConverter.ConvertiblePair(Number.class, String.class);
        Assert.assertEquals(pair, pairEqual);
        Assert.assertEquals(pair.hashCode(), pairEqual.hashCode());
    }

    @Test
    public void testConvertiblePairDifferentEqualsAndHash() {
        GenericConverter.ConvertiblePair pair = new GenericConverter.ConvertiblePair(Number.class, String.class);
        GenericConverter.ConvertiblePair pairOpposite = new GenericConverter.ConvertiblePair(String.class, Number.class);
        Assert.assertFalse(pair.equals(pairOpposite));
        Assert.assertFalse(((pair.hashCode()) == (pairOpposite.hashCode())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void canConvertIllegalArgumentNullTargetTypeFromClass() {
        conversionService.canConvert(String.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void canConvertIllegalArgumentNullTargetTypeFromTypeDescriptor() {
        conversionService.canConvert(TypeDescriptor.valueOf(String.class), null);
    }

    @Test
    public void removeConvertible() {
        conversionService.addConverter(new GenericConversionServiceTests.ColorConverter());
        Assert.assertTrue(conversionService.canConvert(String.class, Color.class));
        conversionService.removeConvertible(String.class, Color.class);
        Assert.assertFalse(conversionService.canConvert(String.class, Color.class));
    }

    @Test
    public void conditionalConverter() {
        GenericConversionServiceTests.MyConditionalConverter converter = new GenericConversionServiceTests.MyConditionalConverter();
        conversionService.addConverter(new GenericConversionServiceTests.ColorConverter());
        conversionService.addConverter(converter);
        Assert.assertEquals(Color.BLACK, conversionService.convert("#000000", Color.class));
        Assert.assertTrue(((converter.getMatchAttempts()) > 0));
    }

    @Test
    public void conditionalConverterFactory() {
        GenericConversionServiceTests.MyConditionalConverterFactory converter = new GenericConversionServiceTests.MyConditionalConverterFactory();
        conversionService.addConverter(new GenericConversionServiceTests.ColorConverter());
        conversionService.addConverterFactory(converter);
        Assert.assertEquals(Color.BLACK, conversionService.convert("#000000", Color.class));
        Assert.assertTrue(((converter.getMatchAttempts()) > 0));
        Assert.assertTrue(((converter.getNestedMatchAttempts()) > 0));
    }

    @Test
    public void conditionalConverterCachingForDifferentAnnotationAttributes() throws Exception {
        conversionService.addConverter(new GenericConversionServiceTests.ColorConverter());
        conversionService.addConverter(new GenericConversionServiceTests.MyConditionalColorConverter());
        Assert.assertEquals(Color.BLACK, conversionService.convert("000000xxxx", new TypeDescriptor(getClass().getField("activeColor"))));
        Assert.assertEquals(Color.BLACK, conversionService.convert(" #000000 ", new TypeDescriptor(getClass().getField("inactiveColor"))));
        Assert.assertEquals(Color.BLACK, conversionService.convert("000000yyyy", new TypeDescriptor(getClass().getField("activeColor"))));
        Assert.assertEquals(Color.BLACK, conversionService.convert("  #000000  ", new TypeDescriptor(getClass().getField("inactiveColor"))));
    }

    @Test
    public void shouldNotSupportNullConvertibleTypesFromNonConditionalGenericConverter() {
        GenericConverter converter = new GenericConversionServiceTests.NonConditionalGenericConverter();
        try {
            conversionService.addConverter(converter);
            Assert.fail("Did not throw IllegalStateException");
        } catch (IllegalStateException ex) {
            Assert.assertEquals("Only conditional converters may return null convertible types", ex.getMessage());
        }
    }

    @Test
    public void conditionalConversionForAllTypes() {
        GenericConversionServiceTests.MyConditionalGenericConverter converter = new GenericConversionServiceTests.MyConditionalGenericConverter();
        conversionService.addConverter(converter);
        Assert.assertEquals(((Integer) (3)), conversionService.convert(3, Integer.class));
        Assert.assertThat(converter.getSourceTypes().size(), greaterThan(2));
        Assert.assertTrue(converter.getSourceTypes().stream().allMatch(( td) -> .class.equals(td.getType())));
    }

    @Test
    public void convertOptimizeArray() {
        // SPR-9566
        byte[] byteArray = new byte[]{ 1, 2, 3 };
        byte[] converted = conversionService.convert(byteArray, byte[].class);
        Assert.assertSame(byteArray, converted);
    }

    @Test
    public void testEnumToStringConversion() {
        conversionService.addConverter(new EnumToStringConverter(conversionService));
        Assert.assertEquals("A", conversionService.convert(GenericConversionServiceTests.MyEnum.A, String.class));
    }

    @Test
    public void testSubclassOfEnumToString() throws Exception {
        conversionService.addConverter(new EnumToStringConverter(conversionService));
        Assert.assertEquals("FIRST", conversionService.convert(GenericConversionServiceTests.EnumWithSubclass.FIRST, String.class));
    }

    @Test
    public void testEnumWithInterfaceToStringConversion() {
        // SPR-9692
        conversionService.addConverter(new EnumToStringConverter(conversionService));
        conversionService.addConverter(new GenericConversionServiceTests.MyEnumInterfaceToStringConverter<GenericConversionServiceTests.MyEnum>());
        Assert.assertEquals("1", conversionService.convert(GenericConversionServiceTests.MyEnum.A, String.class));
    }

    @Test
    public void testStringToEnumWithInterfaceConversion() {
        conversionService.addConverterFactory(new StringToEnumConverterFactory());
        conversionService.addConverterFactory(new GenericConversionServiceTests.StringToMyEnumInterfaceConverterFactory());
        Assert.assertEquals(GenericConversionServiceTests.MyEnum.A, conversionService.convert("1", GenericConversionServiceTests.MyEnum.class));
    }

    @Test
    public void testStringToEnumWithBaseInterfaceConversion() {
        conversionService.addConverterFactory(new StringToEnumConverterFactory());
        conversionService.addConverterFactory(new GenericConversionServiceTests.StringToMyEnumBaseInterfaceConverterFactory());
        Assert.assertEquals(GenericConversionServiceTests.MyEnum.A, conversionService.convert("base1", GenericConversionServiceTests.MyEnum.class));
    }

    @Test
    public void convertNullAnnotatedStringToString() throws Exception {
        String source = null;
        TypeDescriptor sourceType = new TypeDescriptor(getClass().getField("annotatedString"));
        TypeDescriptor targetType = TypeDescriptor.valueOf(String.class);
        conversionService.convert(source, sourceType, targetType);
    }

    @Test
    public void multipleCollectionTypesFromSameSourceType() throws Exception {
        conversionService.addConverter(new GenericConversionServiceTests.MyStringToRawCollectionConverter());
        conversionService.addConverter(new GenericConversionServiceTests.MyStringToGenericCollectionConverter());
        conversionService.addConverter(new GenericConversionServiceTests.MyStringToStringCollectionConverter());
        conversionService.addConverter(new GenericConversionServiceTests.MyStringToIntegerCollectionConverter());
        Assert.assertEquals(Collections.singleton("testX"), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("stringCollection"))));
        Assert.assertEquals(Collections.singleton(4), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("integerCollection"))));
        Assert.assertEquals(Collections.singleton(4), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("rawCollection"))));
        Assert.assertEquals(Collections.singleton(4), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("genericCollection"))));
        Assert.assertEquals(Collections.singleton(4), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("rawCollection"))));
        Assert.assertEquals(Collections.singleton("testX"), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("stringCollection"))));
    }

    @Test
    public void adaptedCollectionTypesFromSameSourceType() throws Exception {
        conversionService.addConverter(new GenericConversionServiceTests.MyStringToStringCollectionConverter());
        Assert.assertEquals(Collections.singleton("testX"), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("stringCollection"))));
        Assert.assertEquals(Collections.singleton("testX"), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("genericCollection"))));
        Assert.assertEquals(Collections.singleton("testX"), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("rawCollection"))));
        Assert.assertEquals(Collections.singleton("testX"), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("genericCollection"))));
        Assert.assertEquals(Collections.singleton("testX"), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("stringCollection"))));
        Assert.assertEquals(Collections.singleton("testX"), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("rawCollection"))));
        try {
            conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("integerCollection")));
            Assert.fail("Should have thrown ConverterNotFoundException");
        } catch (ConverterNotFoundException ex) {
            // expected
        }
    }

    @Test
    public void genericCollectionAsSource() throws Exception {
        conversionService.addConverter(new GenericConversionServiceTests.MyStringToGenericCollectionConverter());
        Assert.assertEquals(Collections.singleton("testX"), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("stringCollection"))));
        Assert.assertEquals(Collections.singleton("testX"), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("genericCollection"))));
        Assert.assertEquals(Collections.singleton("testX"), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("rawCollection"))));
        // The following is unpleasant but a consequence of the generic collection converter above...
        Assert.assertEquals(Collections.singleton("testX"), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("integerCollection"))));
    }

    @Test
    public void rawCollectionAsSource() throws Exception {
        conversionService.addConverter(new GenericConversionServiceTests.MyStringToRawCollectionConverter());
        Assert.assertEquals(Collections.singleton("testX"), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("stringCollection"))));
        Assert.assertEquals(Collections.singleton("testX"), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("genericCollection"))));
        Assert.assertEquals(Collections.singleton("testX"), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("rawCollection"))));
        // The following is unpleasant but a consequence of the raw collection converter above...
        Assert.assertEquals(Collections.singleton("testX"), conversionService.convert("test", TypeDescriptor.valueOf(String.class), new TypeDescriptor(getClass().getField("integerCollection"))));
    }

    @GenericConversionServiceTests.ExampleAnnotation(active = true)
    public String annotatedString;

    @GenericConversionServiceTests.ExampleAnnotation(active = true)
    public Color activeColor;

    @GenericConversionServiceTests.ExampleAnnotation(active = false)
    public Color inactiveColor;

    public List<Integer> list;

    public Map<String, Integer> map;

    public Map<String, ?> wildcardMap;

    @SuppressWarnings("rawtypes")
    public Collection rawCollection;

    public Collection<?> genericCollection;

    public Collection<String> stringCollection;

    public Collection<Integer> integerCollection;

    @Retention(RetentionPolicy.RUNTIME)
    private @interface ExampleAnnotation {
        boolean active();
    }

    private interface MyBaseInterface {}

    private interface MyInterface extends GenericConversionServiceTests.MyBaseInterface {}

    private static class MyInterfaceImplementer implements GenericConversionServiceTests.MyInterface {}

    private static class MyBaseInterfaceToStringConverter implements org.springframework.core.convert.converter.Converter<GenericConversionServiceTests.MyBaseInterface, String> {
        @Override
        public String convert(GenericConversionServiceTests.MyBaseInterface source) {
            return "RESULT";
        }
    }

    private static class MyStringArrayToResourceArrayConverter implements org.springframework.core.convert.converter.Converter<String[], Resource[]> {
        @Override
        public Resource[] convert(String[] source) {
            return Arrays.stream(source).map(( s) -> s.substring(1)).map(DescriptiveResource::new).toArray(Resource[]::new);
        }
    }

    private static class MyStringArrayToIntegerArrayConverter implements org.springframework.core.convert.converter.Converter<String[], Integer[]> {
        @Override
        public Integer[] convert(String[] source) {
            return Arrays.stream(source).map(( s) -> s.substring(1)).map(Integer::valueOf).toArray(Integer[]::new);
        }
    }

    private static class MyStringToIntegerArrayConverter implements org.springframework.core.convert.converter.Converter<String, Integer[]> {
        @Override
        public Integer[] convert(String source) {
            String[] srcArray = StringUtils.commaDelimitedListToStringArray(source);
            return Arrays.stream(srcArray).map(( s) -> s.substring(1)).map(Integer::valueOf).toArray(Integer[]::new);
        }
    }

    private static class WithCopyConstructor {
        WithCopyConstructor() {
        }

        @SuppressWarnings("unused")
        WithCopyConstructor(GenericConversionServiceTests.WithCopyConstructor value) {
        }
    }

    private static class MyConditionalConverter implements ConditionalConverter , org.springframework.core.convert.converter.Converter<String, Color> {
        private int matchAttempts = 0;

        @Override
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            (matchAttempts)++;
            return false;
        }

        @Override
        public Color convert(String source) {
            throw new IllegalStateException();
        }

        public int getMatchAttempts() {
            return matchAttempts;
        }
    }

    private static class NonConditionalGenericConverter implements GenericConverter {
        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return null;
        }

        @Override
        @Nullable
        public Object convert(@Nullable
        Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            return null;
        }
    }

    private static class MyConditionalGenericConverter implements ConditionalConverter , GenericConverter {
        private final List<TypeDescriptor> sourceTypes = new ArrayList<>();

        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return null;
        }

        @Override
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            sourceTypes.add(sourceType);
            return false;
        }

        @Override
        @Nullable
        public Object convert(@Nullable
        Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            return null;
        }

        public List<TypeDescriptor> getSourceTypes() {
            return sourceTypes;
        }
    }

    private static class MyConditionalConverterFactory implements ConditionalConverter , ConverterFactory<String, Color> {
        private GenericConversionServiceTests.MyConditionalConverter converter = new GenericConversionServiceTests.MyConditionalConverter();

        private int matchAttempts = 0;

        @Override
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            (matchAttempts)++;
            return true;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends Color> org.springframework.core.convert.converter.Converter<String, T> getConverter(Class<T> targetType) {
            return ((org.springframework.core.convert.converter.Converter<String, T>) (converter));
        }

        public int getMatchAttempts() {
            return matchAttempts;
        }

        public int getNestedMatchAttempts() {
            return converter.getMatchAttempts();
        }
    }

    private static interface MyEnumBaseInterface {
        String getBaseCode();
    }

    private interface MyEnumInterface extends GenericConversionServiceTests.MyEnumBaseInterface {
        String getCode();
    }

    private enum MyEnum implements GenericConversionServiceTests.MyEnumInterface {

        A("1"),
        B("2"),
        C("3");
        private final String code;

        MyEnum(String code) {
            this.code = code;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getBaseCode() {
            return "base" + (code);
        }
    }

    private enum EnumWithSubclass {

        FIRST() {
            @Override
            public String toString() {
                return "1st";
            }
        };}

    @SuppressWarnings("rawtypes")
    private static class MyStringToRawCollectionConverter implements org.springframework.core.convert.converter.Converter<String, Collection> {
        @Override
        public Collection convert(String source) {
            return Collections.singleton((source + "X"));
        }
    }

    private static class MyStringToGenericCollectionConverter implements org.springframework.core.convert.converter.Converter<String, Collection<?>> {
        @Override
        public Collection<?> convert(String source) {
            return Collections.singleton((source + "X"));
        }
    }

    private static class MyEnumInterfaceToStringConverter<T extends GenericConversionServiceTests.MyEnumInterface> implements org.springframework.core.convert.converter.Converter<T, String> {
        @Override
        public String convert(T source) {
            return source.getCode();
        }
    }

    private static class StringToMyEnumInterfaceConverterFactory implements ConverterFactory<String, GenericConversionServiceTests.MyEnumInterface> {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public <T extends GenericConversionServiceTests.MyEnumInterface> org.springframework.core.convert.converter.Converter<String, T> getConverter(Class<T> targetType) {
            return new GenericConversionServiceTests.StringToMyEnumInterfaceConverterFactory.StringToMyEnumInterfaceConverter(targetType);
        }

        private static class StringToMyEnumInterfaceConverter<T extends Enum<?> & GenericConversionServiceTests.MyEnumInterface> implements org.springframework.core.convert.converter.Converter<String, T> {
            private final Class<T> enumType;

            public StringToMyEnumInterfaceConverter(Class<T> enumType) {
                this.enumType = enumType;
            }

            public T convert(String source) {
                for (T value : enumType.getEnumConstants()) {
                    if (value.getCode().equals(source)) {
                        return value;
                    }
                }
                return null;
            }
        }
    }

    private static class StringToMyEnumBaseInterfaceConverterFactory implements ConverterFactory<String, GenericConversionServiceTests.MyEnumBaseInterface> {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public <T extends GenericConversionServiceTests.MyEnumBaseInterface> org.springframework.core.convert.converter.Converter<String, T> getConverter(Class<T> targetType) {
            return new GenericConversionServiceTests.StringToMyEnumBaseInterfaceConverterFactory.StringToMyEnumBaseInterfaceConverter(targetType);
        }

        private static class StringToMyEnumBaseInterfaceConverter<T extends Enum<?> & GenericConversionServiceTests.MyEnumBaseInterface> implements org.springframework.core.convert.converter.Converter<String, T> {
            private final Class<T> enumType;

            public StringToMyEnumBaseInterfaceConverter(Class<T> enumType) {
                this.enumType = enumType;
            }

            public T convert(String source) {
                for (T value : enumType.getEnumConstants()) {
                    if (value.getBaseCode().equals(source)) {
                        return value;
                    }
                }
                return null;
            }
        }
    }

    private static class MyStringToStringCollectionConverter implements org.springframework.core.convert.converter.Converter<String, Collection<String>> {
        @Override
        public Collection<String> convert(String source) {
            return Collections.singleton((source + "X"));
        }
    }

    private static class MyStringToIntegerCollectionConverter implements org.springframework.core.convert.converter.Converter<String, Collection<Integer>> {
        @Override
        public Collection<Integer> convert(String source) {
            return Collections.singleton(source.length());
        }
    }

    @SuppressWarnings("rawtypes")
    private static class UntypedConverter implements org.springframework.core.convert.converter.Converter {
        @Override
        public Object convert(Object source) {
            return source;
        }
    }

    private static class ColorConverter implements org.springframework.core.convert.converter.Converter<String, Color> {
        @Override
        public Color convert(String source) {
            return Color.decode(source.trim());
        }
    }

    private static class MyConditionalColorConverter implements ConditionalConverter , org.springframework.core.convert.converter.Converter<String, Color> {
        @Override
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            GenericConversionServiceTests.ExampleAnnotation ann = targetType.getAnnotation(GenericConversionServiceTests.ExampleAnnotation.class);
            return (ann != null) && (ann.active());
        }

        @Override
        public Color convert(String source) {
            return Color.decode(source.substring(0, 6));
        }
    }
}

