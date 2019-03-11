/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.properties;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.properties.constraints.NumericConstraints;
import net.sourceforge.pmd.properties.constraints.PropertyConstraint;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.net.sourceforge.pmd.Rule;
import org.junit.rules.ExpectedException;


/**
 * Mostly TODO, I'd rather implement tests on the final version of the framework.
 *
 * @author Cl?ment Fournier
 * @since 7.0.0
 */
public class PropertyDescriptorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConstraintViolationCausesDysfunctionalRule() {
        PropertyDescriptor<Integer> intProperty = PropertyFactory.intProperty("fooProp").desc("hello").defaultValue(4).require(NumericConstraints.inRange(1, 10)).build();
        FooRule rule = new FooRule();
        rule.definePropertyDescriptor(intProperty);
        rule.setProperty(intProperty, 1000);
        RuleSet ruleSet = new RuleSetFactory().createSingleRuleRuleSet(rule);
        List<net.sourceforge.pmd.Rule> dysfunctional = new ArrayList<>();
        ruleSet.removeDysfunctionalRules(dysfunctional);
        Assert.assertEquals(1, dysfunctional.size());
        Assert.assertThat(dysfunctional, Matchers.hasItem(rule));
    }

    @Test
    public void testConstraintViolationCausesDysfunctionalRuleMulti() {
        PropertyDescriptor<List<Double>> descriptor = // 11. is in range
        PropertyFactory.doubleListProperty("fooProp").desc("hello").defaultValues(2.0, 11.0).requireEach(NumericConstraints.inRange(1.0, 20.0)).build();
        FooRule rule = new FooRule();
        rule.definePropertyDescriptor(descriptor);
        rule.setProperty(descriptor, Collections.singletonList(1000.0));// not in range

        RuleSet ruleSet = new RuleSetFactory().createSingleRuleRuleSet(rule);
        List<net.sourceforge.pmd.Rule> dysfunctional = new ArrayList<>();
        ruleSet.removeDysfunctionalRules(dysfunctional);
        Assert.assertEquals(1, dysfunctional.size());
        Assert.assertThat(dysfunctional, Matchers.hasItem(rule));
    }

    @Test
    public void testDefaultValueConstraintViolationCausesFailure() {
        PropertyConstraint<Integer> constraint = NumericConstraints.inRange(1, 10);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Matchers.allOf(/* -ed or -ion */
        PropertyDescriptorTest.containsIgnoreCase("Constraint violat"), PropertyDescriptorTest.containsIgnoreCase(constraint.getConstraintDescription())));
        PropertyFactory.intProperty("fooProp").desc("hello").defaultValue(1000).require(constraint).build();
    }

    @Test
    public void testDefaultValueConstraintViolationCausesFailureMulti() {
        PropertyConstraint<Double> constraint = NumericConstraints.inRange(1.0, 10.0);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(Matchers.allOf(/* -ed or -ion */
        PropertyDescriptorTest.containsIgnoreCase("Constraint violat"), PropertyDescriptorTest.containsIgnoreCase(constraint.getConstraintDescription())));
        // 11. is out of range
        PropertyFactory.doubleListProperty("fooProp").desc("hello").defaultValues(2.0, 11.0).requireEach(constraint).build();
    }

    @Test
    public void testNoConstraintViolationCausesIsOkMulti() {
        PropertyDescriptor<List<Double>> descriptor = // 11. is in range
        PropertyFactory.doubleListProperty("fooProp").desc("hello").defaultValues(2.0, 11.0).requireEach(NumericConstraints.inRange(1.0, 20.0)).build();
        Assert.assertEquals("fooProp", descriptor.name());
        Assert.assertEquals("hello", descriptor.description());
        Assert.assertThat(descriptor.defaultValue(), Matchers.contains(2.0, 11.0));
    }

    @Test
    public void testNoConstraintViolationCausesIsOk() {
        PropertyDescriptor<String> descriptor = PropertyFactory.stringProperty("fooProp").desc("hello").defaultValue("bazooli").build();
        Assert.assertEquals("fooProp", descriptor.name());
        Assert.assertEquals("hello", descriptor.description());
        Assert.assertEquals("bazooli", descriptor.defaultValue());
    }

    @Test
    public void testIntProperty() {
        PropertyDescriptor<Integer> descriptor = PropertyFactory.intProperty("intProp").desc("hello").defaultValue(1).build();
        Assert.assertEquals("intProp", descriptor.name());
        Assert.assertEquals("hello", descriptor.description());
        Assert.assertEquals(Integer.valueOf(1), descriptor.defaultValue());
        Assert.assertEquals(Integer.valueOf(5), descriptor.valueFrom("5"));
        PropertyDescriptor<List<Integer>> listDescriptor = PropertyFactory.intListProperty("intListProp").desc("hello").defaultValues(1, 2).build();
        Assert.assertEquals("intListProp", listDescriptor.name());
        Assert.assertEquals("hello", listDescriptor.description());
        Assert.assertEquals(Arrays.asList(1, 2), listDescriptor.defaultValue());
        Assert.assertEquals(Arrays.asList(5, 7), listDescriptor.valueFrom("5,7"));
    }

    @Test
    public void testIntPropertyInvalidValue() {
        PropertyDescriptor<Integer> descriptor = PropertyFactory.intProperty("intProp").desc("hello").defaultValue(1).build();
        thrown.expect(NumberFormatException.class);
        thrown.expectMessage("not a number");
        descriptor.valueFrom("not a number");
    }

    @Test
    public void testDoubleProperty() {
        PropertyDescriptor<Double> descriptor = PropertyFactory.doubleProperty("doubleProp").desc("hello").defaultValue(1.0).build();
        Assert.assertEquals("doubleProp", descriptor.name());
        Assert.assertEquals("hello", descriptor.description());
        Assert.assertEquals(Double.valueOf(1.0), descriptor.defaultValue());
        Assert.assertEquals(Double.valueOf(2.0), descriptor.valueFrom("2.0"));
        PropertyDescriptor<List<Double>> listDescriptor = PropertyFactory.doubleListProperty("doubleListProp").desc("hello").defaultValues(1.0, 2.0).build();
        Assert.assertEquals("doubleListProp", listDescriptor.name());
        Assert.assertEquals("hello", listDescriptor.description());
        Assert.assertEquals(Arrays.asList(1.0, 2.0), listDescriptor.defaultValue());
        Assert.assertEquals(Arrays.asList(2.0, 3.0), listDescriptor.valueFrom("2.0,3.0"));
    }

    @Test
    public void testDoublePropertyInvalidValue() {
        PropertyDescriptor<Double> descriptor = PropertyFactory.doubleProperty("doubleProp").desc("hello").defaultValue(1.0).build();
        thrown.expect(NumberFormatException.class);
        thrown.expectMessage("this is not a number");
        descriptor.valueFrom("this is not a number");
    }

    @Test
    public void testStringProperty() {
        PropertyDescriptor<String> descriptor = PropertyFactory.stringProperty("stringProp").desc("hello").defaultValue("default value").build();
        Assert.assertEquals("stringProp", descriptor.name());
        Assert.assertEquals("hello", descriptor.description());
        Assert.assertEquals("default value", descriptor.defaultValue());
        Assert.assertEquals("foo", descriptor.valueFrom("foo"));
        PropertyDescriptor<List<String>> listDescriptor = PropertyFactory.stringListProperty("stringListProp").desc("hello").defaultValues("v1", "v2").build();
        Assert.assertEquals("stringListProp", listDescriptor.name());
        Assert.assertEquals("hello", listDescriptor.description());
        Assert.assertEquals(Arrays.asList("v1", "v2"), listDescriptor.defaultValue());
        Assert.assertEquals(Arrays.asList("foo", "bar"), listDescriptor.valueFrom("foo|bar"));
    }

    private enum SampleEnum {

        A,
        B,
        C;}

    private static Map<String, PropertyDescriptorTest.SampleEnum> nameMap = new LinkedHashMap<>();

    static {
        PropertyDescriptorTest.nameMap.put("TEST_A", PropertyDescriptorTest.SampleEnum.A);
        PropertyDescriptorTest.nameMap.put("TEST_B", PropertyDescriptorTest.SampleEnum.B);
        PropertyDescriptorTest.nameMap.put("TEST_C", PropertyDescriptorTest.SampleEnum.C);
    }

    @Test
    public void testEnumProperty() {
        PropertyDescriptor<PropertyDescriptorTest.SampleEnum> descriptor = PropertyFactory.enumProperty("enumProp", PropertyDescriptorTest.nameMap).desc("hello").defaultValue(PropertyDescriptorTest.SampleEnum.B).build();
        Assert.assertEquals("enumProp", descriptor.name());
        Assert.assertEquals("hello", descriptor.description());
        Assert.assertEquals(PropertyDescriptorTest.SampleEnum.B, descriptor.defaultValue());
        Assert.assertEquals(PropertyDescriptorTest.SampleEnum.C, descriptor.valueFrom("TEST_C"));
        PropertyDescriptor<List<PropertyDescriptorTest.SampleEnum>> listDescriptor = PropertyFactory.enumListProperty("enumListProp", PropertyDescriptorTest.nameMap).desc("hello").defaultValues(PropertyDescriptorTest.SampleEnum.A, PropertyDescriptorTest.SampleEnum.B).build();
        Assert.assertEquals("enumListProp", listDescriptor.name());
        Assert.assertEquals("hello", listDescriptor.description());
        Assert.assertEquals(Arrays.asList(PropertyDescriptorTest.SampleEnum.A, PropertyDescriptorTest.SampleEnum.B), listDescriptor.defaultValue());
        Assert.assertEquals(Arrays.asList(PropertyDescriptorTest.SampleEnum.B, PropertyDescriptorTest.SampleEnum.C), listDescriptor.valueFrom("TEST_B|TEST_C"));
    }

    @Test
    public void testEnumPropertyNullValueFailsBuild() {
        Map<String, PropertyDescriptorTest.SampleEnum> map = new HashMap<>(PropertyDescriptorTest.nameMap);
        map.put("TEST_NULL", null);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(PropertyDescriptorTest.containsIgnoreCase("null value"));
        PropertyFactory.enumProperty("enumProp", map);
    }

    @Test
    public void testEnumListPropertyNullValueFailsBuild() {
        Map<String, PropertyDescriptorTest.SampleEnum> map = new HashMap<>(PropertyDescriptorTest.nameMap);
        map.put("TEST_NULL", null);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(PropertyDescriptorTest.containsIgnoreCase("null value"));
        PropertyFactory.enumListProperty("enumProp", map);
    }

    @Test
    public void testEnumPropertyInvalidValue() {
        PropertyDescriptor<PropertyDescriptorTest.SampleEnum> descriptor = PropertyFactory.enumProperty("enumProp", PropertyDescriptorTest.nameMap).desc("hello").defaultValue(PropertyDescriptorTest.SampleEnum.B).build();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Value was not in the set [TEST_A, TEST_B, TEST_C]");
        descriptor.valueFrom("InvalidEnumValue");
    }

    @Test
    public void testRegexProperty() {
        PropertyDescriptor<Pattern> descriptor = PropertyFactory.regexProperty("regexProp").desc("hello").defaultValue("^[A-Z].*$").build();
        Assert.assertEquals("regexProp", descriptor.name());
        Assert.assertEquals("hello", descriptor.description());
        Assert.assertEquals("^[A-Z].*$", descriptor.defaultValue().toString());
        Assert.assertEquals("[0-9]+", descriptor.valueFrom("[0-9]+").toString());
    }

    @Test
    public void testRegexPropertyInvalidValue() {
        PropertyDescriptor<Pattern> descriptor = PropertyFactory.regexProperty("regexProp").desc("hello").defaultValue("^[A-Z].*$").build();
        thrown.expect(PatternSyntaxException.class);
        thrown.expectMessage("Unclosed character class");
        descriptor.valueFrom("[open class");
    }

    @Test
    public void testRegexPropertyInvalidDefaultValue() {
        thrown.expect(PatternSyntaxException.class);
        thrown.expectMessage("Unclosed character class");
        PropertyDescriptor<Pattern> descriptor = PropertyFactory.regexProperty("regexProp").desc("hello").defaultValue("[open class").build();
    }
}

