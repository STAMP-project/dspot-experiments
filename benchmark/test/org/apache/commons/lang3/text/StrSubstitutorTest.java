/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.text;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Test class for StrSubstitutor.
 */
@Deprecated
public class StrSubstitutorTest {
    private Map<String, String> values;

    // -----------------------------------------------------------------------
    /**
     * Tests simple key replace.
     */
    @Test
    public void testReplaceSimple() {
        doTestReplace("The quick brown fox jumps over the lazy dog.", "The ${animal} jumps over the ${target}.", true);
    }

    /**
     * Tests simple key replace.
     */
    @Test
    public void testReplaceSolo() {
        doTestReplace("quick brown fox", "${animal}", false);
    }

    /**
     * Tests replace with no variables.
     */
    @Test
    public void testReplaceNoVariables() {
        doTestNoReplace("The balloon arrived.");
    }

    /**
     * Tests replace with null.
     */
    @Test
    public void testReplaceNull() {
        doTestNoReplace(null);
    }

    /**
     * Tests replace with null.
     */
    @Test
    public void testReplaceEmpty() {
        doTestNoReplace("");
    }

    /**
     * Tests key replace changing map after initialization (not recommended).
     */
    @Test
    public void testReplaceChangedMap() {
        final StrSubstitutor sub = new StrSubstitutor(values);
        values.put("target", "moon");
        Assertions.assertEquals("The quick brown fox jumps over the moon.", sub.replace("The ${animal} jumps over the ${target}."));
    }

    /**
     * Tests unknown key replace.
     */
    @Test
    public void testReplaceUnknownKey() {
        doTestReplace("The ${person} jumps over the lazy dog.", "The ${person} jumps over the ${target}.", true);
        doTestReplace("The ${person} jumps over the lazy dog. 1234567890.", "The ${person} jumps over the ${target}. ${undefined.number:-1234567890}.", true);
    }

    /**
     * Tests adjacent keys.
     */
    @Test
    public void testReplaceAdjacentAtStart() {
        values.put("code", "GBP");
        values.put("amount", "12.50");
        final StrSubstitutor sub = new StrSubstitutor(values);
        Assertions.assertEquals("GBP12.50 charged", sub.replace("${code}${amount} charged"));
    }

    /**
     * Tests adjacent keys.
     */
    @Test
    public void testReplaceAdjacentAtEnd() {
        values.put("code", "GBP");
        values.put("amount", "12.50");
        final StrSubstitutor sub = new StrSubstitutor(values);
        Assertions.assertEquals("Amount is GBP12.50", sub.replace("Amount is ${code}${amount}"));
    }

    /**
     * Tests simple recursive replace.
     */
    @Test
    public void testReplaceRecursive() {
        values.put("animal", "${critter}");
        values.put("target", "${pet}");
        values.put("pet", "${petCharacteristic} dog");
        values.put("petCharacteristic", "lazy");
        values.put("critter", "${critterSpeed} ${critterColor} ${critterType}");
        values.put("critterSpeed", "quick");
        values.put("critterColor", "brown");
        values.put("critterType", "fox");
        doTestReplace("The quick brown fox jumps over the lazy dog.", "The ${animal} jumps over the ${target}.", true);
        values.put("pet", "${petCharacteristicUnknown:-lazy} dog");
        doTestReplace("The quick brown fox jumps over the lazy dog.", "The ${animal} jumps over the ${target}.", true);
    }

    /**
     * Tests escaping.
     */
    @Test
    public void testReplaceEscaping() {
        doTestReplace("The ${animal} jumps over the lazy dog.", "The $${animal} jumps over the ${target}.", true);
    }

    /**
     * Tests escaping.
     */
    @Test
    public void testReplaceSoloEscaping() {
        doTestReplace("${animal}", "$${animal}", false);
    }

    /**
     * Tests complex escaping.
     */
    @Test
    public void testReplaceComplexEscaping() {
        doTestReplace("The ${quick brown fox} jumps over the lazy dog.", "The $${${animal}} jumps over the ${target}.", true);
        doTestReplace("The ${quick brown fox} jumps over the lazy dog. ${1234567890}.", "The $${${animal}} jumps over the ${target}. $${${undefined.number:-1234567890}}.", true);
    }

    /**
     * Tests when no prefix or suffix.
     */
    @Test
    public void testReplaceNoPrefixNoSuffix() {
        doTestReplace("The animal jumps over the lazy dog.", "The animal jumps over the ${target}.", true);
    }

    /**
     * Tests when no incomplete prefix.
     */
    @Test
    public void testReplaceIncompletePrefix() {
        doTestReplace("The {animal} jumps over the lazy dog.", "The {animal} jumps over the ${target}.", true);
    }

    /**
     * Tests when prefix but no suffix.
     */
    @Test
    public void testReplacePrefixNoSuffix() {
        doTestReplace("The ${animal jumps over the ${target} lazy dog.", "The ${animal jumps over the ${target} ${target}.", true);
    }

    /**
     * Tests when suffix but no prefix.
     */
    @Test
    public void testReplaceNoPrefixSuffix() {
        doTestReplace("The animal} jumps over the lazy dog.", "The animal} jumps over the ${target}.", true);
    }

    /**
     * Tests when no variable name.
     */
    @Test
    public void testReplaceEmptyKeys() {
        doTestReplace("The ${} jumps over the lazy dog.", "The ${} jumps over the ${target}.", true);
        doTestReplace("The animal jumps over the lazy dog.", "The ${:-animal} jumps over the ${target}.", true);
    }

    /**
     * Tests replace creates output same as input.
     */
    @Test
    public void testReplaceToIdentical() {
        values.put("animal", "$${${thing}}");
        values.put("thing", "animal");
        doTestReplace("The ${animal} jumps.", "The ${animal} jumps.", true);
    }

    /**
     * Tests a cyclic replace operation.
     * The cycle should be detected and cause an exception to be thrown.
     */
    @Test
    public void testCyclicReplacement() {
        final Map<String, String> map = new HashMap<>();
        map.put("animal", "${critter}");
        map.put("target", "${pet}");
        map.put("pet", "${petCharacteristic} dog");
        map.put("petCharacteristic", "lazy");
        map.put("critter", "${critterSpeed} ${critterColor} ${critterType}");
        map.put("critterSpeed", "quick");
        map.put("critterColor", "brown");
        map.put("critterType", "${animal}");
        StrSubstitutor sub = new StrSubstitutor(map);
        Assertions.assertThrows(IllegalStateException.class, () -> sub.replace("The ${animal} jumps over the ${target}."), "Cyclic replacement was not detected!");
        // also check even when default value is set.
        map.put("critterType", "${animal:-fox}");
        StrSubstitutor sub2 = new StrSubstitutor(map);
        Assertions.assertThrows(IllegalStateException.class, () -> sub2.replace("The ${animal} jumps over the ${target}."), "Cyclic replacement was not detected!");
    }

    /**
     * Tests interpolation with weird boundary patterns.
     */
    @Test
    public void testReplaceWeirdPattens() {
        doTestNoReplace("");
        doTestNoReplace("${}");
        doTestNoReplace("${ }");
        doTestNoReplace("${\t}");
        doTestNoReplace("${\n}");
        doTestNoReplace("${\b}");
        doTestNoReplace("${");
        doTestNoReplace("$}");
        doTestNoReplace("}");
        doTestNoReplace("${}$");
        doTestNoReplace("${${");
        doTestNoReplace("${${}}");
        doTestNoReplace("${$${}}");
        doTestNoReplace("${$$${}}");
        doTestNoReplace("${$$${$}}");
        doTestNoReplace("${${}}");
        doTestNoReplace("${${ }}");
    }

    /**
     * Tests simple key replace.
     */
    @Test
    public void testReplacePartialString_noReplace() {
        final StrSubstitutor sub = new StrSubstitutor();
        Assertions.assertEquals("${animal} jumps", sub.replace("The ${animal} jumps over the ${target}.", 4, 15));
    }

    /**
     * Tests whether a variable can be replaced in a variable name.
     */
    @Test
    public void testReplaceInVariable() {
        values.put("animal.1", "fox");
        values.put("animal.2", "mouse");
        values.put("species", "2");
        final StrSubstitutor sub = new StrSubstitutor(values);
        sub.setEnableSubstitutionInVariables(true);
        Assertions.assertEquals("The mouse jumps over the lazy dog.", sub.replace("The ${animal.${species}} jumps over the ${target}."), "Wrong result (1)");
        values.put("species", "1");
        Assertions.assertEquals("The fox jumps over the lazy dog.", sub.replace("The ${animal.${species}} jumps over the ${target}."), "Wrong result (2)");
        Assertions.assertEquals("The fox jumps over the lazy dog.", sub.replace("The ${unknown.animal.${unknown.species:-1}:-fox} jumps over the ${unknown.target:-lazy dog}."), "Wrong result (3)");
    }

    /**
     * Tests whether substitution in variable names is disabled per default.
     */
    @Test
    public void testReplaceInVariableDisabled() {
        values.put("animal.1", "fox");
        values.put("animal.2", "mouse");
        values.put("species", "2");
        final StrSubstitutor sub = new StrSubstitutor(values);
        Assertions.assertEquals("The ${animal.${species}} jumps over the lazy dog.", sub.replace("The ${animal.${species}} jumps over the ${target}."), "Wrong result (1)");
        Assertions.assertEquals("The ${animal.${species:-1}} jumps over the lazy dog.", sub.replace("The ${animal.${species:-1}} jumps over the ${target}."), "Wrong result (2)");
    }

    /**
     * Tests complex and recursive substitution in variable names.
     */
    @Test
    public void testReplaceInVariableRecursive() {
        values.put("animal.2", "brown fox");
        values.put("animal.1", "white mouse");
        values.put("color", "white");
        values.put("species.white", "1");
        values.put("species.brown", "2");
        final StrSubstitutor sub = new StrSubstitutor(values);
        sub.setEnableSubstitutionInVariables(true);
        Assertions.assertEquals("The white mouse jumps over the lazy dog.", sub.replace("The ${animal.${species.${color}}} jumps over the ${target}."), "Wrong result (1)");
        Assertions.assertEquals("The brown fox jumps over the lazy dog.", sub.replace("The ${animal.${species.${unknownColor:-brown}}} jumps over the ${target}."), "Wrong result (2)");
    }

    @Test
    public void testDefaultValueDelimiters() {
        final Map<String, String> map = new HashMap<>();
        map.put("animal", "fox");
        map.put("target", "dog");
        StrSubstitutor sub = new StrSubstitutor(map, "${", "}", '$');
        Assertions.assertEquals("The fox jumps over the lazy dog. 1234567890.", sub.replace("The ${animal} jumps over the lazy ${target}. ${undefined.number:-1234567890}."));
        sub = new StrSubstitutor(map, "${", "}", '$', "?:");
        Assertions.assertEquals("The fox jumps over the lazy dog. 1234567890.", sub.replace("The ${animal} jumps over the lazy ${target}. ${undefined.number?:1234567890}."));
        sub = new StrSubstitutor(map, "${", "}", '$', "||");
        Assertions.assertEquals("The fox jumps over the lazy dog. 1234567890.", sub.replace("The ${animal} jumps over the lazy ${target}. ${undefined.number||1234567890}."));
        sub = new StrSubstitutor(map, "${", "}", '$', "!");
        Assertions.assertEquals("The fox jumps over the lazy dog. 1234567890.", sub.replace("The ${animal} jumps over the lazy ${target}. ${undefined.number!1234567890}."));
        sub = new StrSubstitutor(map, "${", "}", '$', "");
        sub.setValueDelimiterMatcher(null);
        Assertions.assertEquals("The fox jumps over the lazy dog. ${undefined.number!1234567890}.", sub.replace("The ${animal} jumps over the lazy ${target}. ${undefined.number!1234567890}."));
        sub = new StrSubstitutor(map, "${", "}", '$');
        sub.setValueDelimiterMatcher(null);
        Assertions.assertEquals("The fox jumps over the lazy dog. ${undefined.number!1234567890}.", sub.replace("The ${animal} jumps over the lazy ${target}. ${undefined.number!1234567890}."));
    }

    // -----------------------------------------------------------------------
    /**
     * Tests protected.
     */
    @Test
    public void testResolveVariable() {
        final StrBuilder builder = new StrBuilder("Hi ${name}!");
        final Map<String, String> map = new HashMap<>();
        map.put("name", "commons");
        final StrSubstitutor sub = new StrSubstitutor(map) {
            @Override
            protected String resolveVariable(final String variableName, final StrBuilder buf, final int startPos, final int endPos) {
                Assertions.assertEquals("name", variableName);
                Assertions.assertSame(builder, buf);
                Assertions.assertEquals(3, startPos);
                Assertions.assertEquals(10, endPos);
                return "jakarta";
            }
        };
        sub.replaceIn(builder);
        Assertions.assertEquals("Hi jakarta!", builder.toString());
    }

    // -----------------------------------------------------------------------
    /**
     * Tests constructor.
     */
    @Test
    public void testConstructorNoArgs() {
        final StrSubstitutor sub = new StrSubstitutor();
        Assertions.assertEquals("Hi ${name}", sub.replace("Hi ${name}"));
    }

    /**
     * Tests constructor.
     */
    @Test
    public void testConstructorMapPrefixSuffix() {
        final Map<String, String> map = new HashMap<>();
        map.put("name", "commons");
        final StrSubstitutor sub = new StrSubstitutor(map, "<", ">");
        Assertions.assertEquals("Hi < commons", sub.replace("Hi $< <name>"));
    }

    /**
     * Tests constructor.
     */
    @Test
    public void testConstructorMapFull() {
        final Map<String, String> map = new HashMap<>();
        map.put("name", "commons");
        StrSubstitutor sub = new StrSubstitutor(map, "<", ">", '!');
        Assertions.assertEquals("Hi < commons", sub.replace("Hi !< <name>"));
        sub = new StrSubstitutor(map, "<", ">", '!', "||");
        Assertions.assertEquals("Hi < commons", sub.replace("Hi !< <name2||commons>"));
    }

    // -----------------------------------------------------------------------
    /**
     * Tests get set.
     */
    @Test
    public void testGetSetEscape() {
        final StrSubstitutor sub = new StrSubstitutor();
        Assertions.assertEquals('$', sub.getEscapeChar());
        sub.setEscapeChar('<');
        Assertions.assertEquals('<', sub.getEscapeChar());
    }

    /**
     * Tests get set.
     */
    @Test
    public void testGetSetPrefix() {
        final StrSubstitutor sub = new StrSubstitutor();
        Assertions.assertTrue(((sub.getVariablePrefixMatcher()) instanceof StrMatcher.StringMatcher));
        sub.setVariablePrefix('<');
        Assertions.assertTrue(((sub.getVariablePrefixMatcher()) instanceof StrMatcher.CharMatcher));
        sub.setVariablePrefix("<<");
        Assertions.assertTrue(((sub.getVariablePrefixMatcher()) instanceof StrMatcher.StringMatcher));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sub.setVariablePrefix(null));
        Assertions.assertTrue(((sub.getVariablePrefixMatcher()) instanceof StrMatcher.StringMatcher));
        final StrMatcher matcher = StrMatcher.commaMatcher();
        sub.setVariablePrefixMatcher(matcher);
        Assertions.assertSame(matcher, sub.getVariablePrefixMatcher());
        Assertions.assertThrows(IllegalArgumentException.class, () -> sub.setVariablePrefixMatcher(null));
        Assertions.assertSame(matcher, sub.getVariablePrefixMatcher());
    }

    /**
     * Tests get set.
     */
    @Test
    public void testGetSetSuffix() {
        final StrSubstitutor sub = new StrSubstitutor();
        Assertions.assertTrue(((sub.getVariableSuffixMatcher()) instanceof StrMatcher.StringMatcher));
        sub.setVariableSuffix('<');
        Assertions.assertTrue(((sub.getVariableSuffixMatcher()) instanceof StrMatcher.CharMatcher));
        sub.setVariableSuffix("<<");
        Assertions.assertTrue(((sub.getVariableSuffixMatcher()) instanceof StrMatcher.StringMatcher));
        Assertions.assertThrows(IllegalArgumentException.class, () -> sub.setVariableSuffix(null));
        Assertions.assertTrue(((sub.getVariableSuffixMatcher()) instanceof StrMatcher.StringMatcher));
        final StrMatcher matcher = StrMatcher.commaMatcher();
        sub.setVariableSuffixMatcher(matcher);
        Assertions.assertSame(matcher, sub.getVariableSuffixMatcher());
        Assertions.assertThrows(IllegalArgumentException.class, () -> sub.setVariableSuffixMatcher(null));
        Assertions.assertSame(matcher, sub.getVariableSuffixMatcher());
    }

    /**
     * Tests get set.
     */
    @Test
    public void testGetSetValueDelimiter() {
        final StrSubstitutor sub = new StrSubstitutor();
        Assertions.assertTrue(((sub.getValueDelimiterMatcher()) instanceof StrMatcher.StringMatcher));
        sub.setValueDelimiter(':');
        Assertions.assertTrue(((sub.getValueDelimiterMatcher()) instanceof StrMatcher.CharMatcher));
        sub.setValueDelimiter("||");
        Assertions.assertTrue(((sub.getValueDelimiterMatcher()) instanceof StrMatcher.StringMatcher));
        sub.setValueDelimiter(null);
        Assertions.assertNull(sub.getValueDelimiterMatcher());
        final StrMatcher matcher = StrMatcher.commaMatcher();
        sub.setValueDelimiterMatcher(matcher);
        Assertions.assertSame(matcher, sub.getValueDelimiterMatcher());
        sub.setValueDelimiterMatcher(null);
        Assertions.assertNull(sub.getValueDelimiterMatcher());
    }

    // -----------------------------------------------------------------------
    /**
     * Tests static.
     */
    @Test
    public void testStaticReplace() {
        final Map<String, String> map = new HashMap<>();
        map.put("name", "commons");
        Assertions.assertEquals("Hi commons!", StrSubstitutor.replace("Hi ${name}!", map));
    }

    /**
     * Tests static.
     */
    @Test
    public void testStaticReplacePrefixSuffix() {
        final Map<String, String> map = new HashMap<>();
        map.put("name", "commons");
        Assertions.assertEquals("Hi commons!", StrSubstitutor.replace("Hi <name>!", map, "<", ">"));
    }

    /**
     * Tests interpolation with system properties.
     */
    @Test
    public void testStaticReplaceSystemProperties() {
        final StrBuilder buf = new StrBuilder();
        buf.append("Hi ").append(System.getProperty("user.name"));
        buf.append(", you are working with ");
        buf.append(System.getProperty("os.name"));
        buf.append(", your home directory is ");
        buf.append(System.getProperty("user.home")).append('.');
        Assertions.assertEquals(buf.toString(), StrSubstitutor.replaceSystemProperties(("Hi ${user.name}, you are " + ("working with ${os.name}, your home " + "directory is ${user.home}."))));
    }

    /**
     * Test for LANG-1055: StrSubstitutor.replaceSystemProperties does not work consistently
     */
    @Test
    public void testLANG1055() {
        System.setProperty("test_key", "test_value");
        final String expected = StrSubstitutor.replace("test_key=${test_key}", System.getProperties());
        final String actual = StrSubstitutor.replaceSystemProperties("test_key=${test_key}");
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Test the replace of a properties object
     */
    @Test
    public void testSubstituteDefaultProperties() {
        final String org = "${doesnotwork}";
        System.setProperty("doesnotwork", "It works!");
        // create a new Properties object with the System.getProperties as default
        final Properties props = new Properties(System.getProperties());
        Assertions.assertEquals("It works!", StrSubstitutor.replace(org, props));
    }

    @Test
    public void testSamePrefixAndSuffix() {
        final Map<String, String> map = new HashMap<>();
        map.put("greeting", "Hello");
        map.put(" there ", "XXX");
        map.put("name", "commons");
        Assertions.assertEquals("Hi commons!", StrSubstitutor.replace("Hi @name@!", map, "@", "@"));
        Assertions.assertEquals("Hello there commons!", StrSubstitutor.replace("@greeting@ there @name@!", map, "@", "@"));
    }

    @Test
    public void testSubstitutePreserveEscape() {
        final String org = "${not-escaped} $${escaped}";
        final Map<String, String> map = new HashMap<>();
        map.put("not-escaped", "value");
        final StrSubstitutor sub = new StrSubstitutor(map, "${", "}", '$');
        Assertions.assertFalse(sub.isPreserveEscapes());
        Assertions.assertEquals("value ${escaped}", sub.replace(org));
        sub.setPreserveEscapes(true);
        Assertions.assertTrue(sub.isPreserveEscapes());
        Assertions.assertEquals("value $${escaped}", sub.replace(org));
    }
}

