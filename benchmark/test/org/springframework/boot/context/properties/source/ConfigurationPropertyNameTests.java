/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.context.properties.source;


import Form.ORIGINAL;
import Form.UNIFORM;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;

import static ConfigurationPropertyName.EMPTY;


/**
 * Tests for {@link ConfigurationPropertyName}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 * @author Edd? Mel?ndez
 */
public class ConfigurationPropertyNameTests {
    @Test
    public void ofNameShouldNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> ConfigurationPropertyName.of(null)).withMessageContaining("Name must not be null");
    }

    @Test
    public void ofNameShouldNotStartWithDash() {
        assertThatExceptionOfType(InvalidConfigurationPropertyNameException.class).isThrownBy(() -> ConfigurationPropertyName.of("-foo")).withMessageContaining("is not valid");
    }

    @Test
    public void ofNameShouldNotStartWithDot() {
        assertThatExceptionOfType(InvalidConfigurationPropertyNameException.class).isThrownBy(() -> ConfigurationPropertyName.of(".foo")).withMessageContaining("is not valid");
    }

    @Test
    public void ofNameShouldNotEndWithDot() {
        assertThatExceptionOfType(InvalidConfigurationPropertyNameException.class).isThrownBy(() -> ConfigurationPropertyName.of("foo.")).withMessageContaining("is not valid");
    }

    @Test
    public void ofNameShouldNotContainUppercase() {
        assertThatExceptionOfType(InvalidConfigurationPropertyNameException.class).isThrownBy(() -> ConfigurationPropertyName.of("fOo")).withMessageContaining("is not valid");
    }

    @Test
    public void ofNameShouldNotContainInvalidChars() {
        String invalid = "_@$%*+=':;";
        for (char c : invalid.toCharArray()) {
            assertThatExceptionOfType(InvalidConfigurationPropertyNameException.class).isThrownBy(() -> ConfigurationPropertyName.of(("foo" + c))).satisfies(( ex) -> assertThat(ex.getMessage()).contains("is not valid"));
        }
    }

    @Test
    public void ofNameWhenSimple() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("name");
        assertThat(name.toString()).isEqualTo("name");
        assertThat(name.getNumberOfElements()).isEqualTo(1);
        assertThat(name.getElement(0, ORIGINAL)).isEqualTo("name");
        assertThat(name.isIndexed(0)).isFalse();
    }

    @Test
    public void ofNameWhenStartsWithNumber() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("1foo");
        assertThat(name.toString()).isEqualTo("1foo");
        assertThat(name.getNumberOfElements()).isEqualTo(1);
        assertThat(name.getElement(0, ORIGINAL)).isEqualTo("1foo");
        assertThat(name.isIndexed(0)).isFalse();
    }

    @Test
    public void ofNameWhenRunOnAssociative() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo[bar]");
        assertThat(name.toString()).isEqualTo("foo[bar]");
        assertThat(name.getElement(0, ORIGINAL)).isEqualTo("foo");
        assertThat(name.getElement(1, ORIGINAL)).isEqualTo("bar");
        assertThat(name.isIndexed(0)).isFalse();
        assertThat(name.isIndexed(1)).isTrue();
    }

    @Test
    public void ofNameWhenDotOnAssociative() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo.bar");
        assertThat(name.toString()).isEqualTo("foo.bar");
        assertThat(name.getElement(0, ORIGINAL)).isEqualTo("foo");
        assertThat(name.getElement(1, ORIGINAL)).isEqualTo("bar");
        assertThat(name.isIndexed(0)).isFalse();
        assertThat(name.isIndexed(1)).isFalse();
    }

    @Test
    public void ofNameWhenDotAndAssociative() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo.[bar]");
        assertThat(name.toString()).isEqualTo("foo[bar]");
        assertThat(name.getElement(0, ORIGINAL)).isEqualTo("foo");
        assertThat(name.getElement(1, ORIGINAL)).isEqualTo("bar");
        assertThat(name.isIndexed(0)).isFalse();
        assertThat(name.isIndexed(1)).isTrue();
    }

    @Test
    public void ofNameWhenDoubleRunOnAndAssociative() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo[bar]baz");
        assertThat(name.toString()).isEqualTo("foo[bar].baz");
        assertThat(name.getElement(0, ORIGINAL)).isEqualTo("foo");
        assertThat(name.getElement(1, ORIGINAL)).isEqualTo("bar");
        assertThat(name.getElement(2, ORIGINAL)).isEqualTo("baz");
        assertThat(name.isIndexed(0)).isFalse();
        assertThat(name.isIndexed(1)).isTrue();
        assertThat(name.isIndexed(2)).isFalse();
    }

    @Test
    public void ofNameWhenDoubleDotAndAssociative() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo.[bar].baz");
        assertThat(name.toString()).isEqualTo("foo[bar].baz");
        assertThat(name.getElement(0, ORIGINAL)).isEqualTo("foo");
        assertThat(name.getElement(1, ORIGINAL)).isEqualTo("bar");
        assertThat(name.getElement(2, ORIGINAL)).isEqualTo("baz");
        assertThat(name.isIndexed(0)).isFalse();
        assertThat(name.isIndexed(1)).isTrue();
        assertThat(name.isIndexed(2)).isFalse();
    }

    @Test
    public void ofNameWhenMissingCloseBracket() {
        assertThatExceptionOfType(InvalidConfigurationPropertyNameException.class).isThrownBy(() -> ConfigurationPropertyName.of("[bar")).withMessageContaining("is not valid");
    }

    @Test
    public void ofNameWhenMissingOpenBracket() {
        assertThatExceptionOfType(InvalidConfigurationPropertyNameException.class).isThrownBy(() -> ConfigurationPropertyName.of("bar]")).withMessageContaining("is not valid");
    }

    @Test
    public void ofNameWhenMultipleMismatchedBrackets() {
        assertThatExceptionOfType(InvalidConfigurationPropertyNameException.class).isThrownBy(() -> ConfigurationPropertyName.of("[a[[[b]ar]")).withMessageContaining("is not valid");
    }

    @Test
    public void ofNameWhenNestedBrackets() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo[a[c][[b]ar]]");
        assertThat(name.toString()).isEqualTo("foo[a[c][[b]ar]]");
        assertThat(name.getElement(0, ORIGINAL)).isEqualTo("foo");
        assertThat(name.getElement(1, ORIGINAL)).isEqualTo("a[c][[b]ar]");
    }

    @Test
    public void ofNameWithWhitespaceInName() {
        assertThatExceptionOfType(InvalidConfigurationPropertyNameException.class).isThrownBy(() -> ConfigurationPropertyName.of("foo. bar")).withMessageContaining("is not valid");
    }

    @Test
    public void ofNameWithWhitespaceInAssociativeElement() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo[b a r]");
        assertThat(name.toString()).isEqualTo("foo[b a r]");
        assertThat(name.getElement(0, ORIGINAL)).isEqualTo("foo");
        assertThat(name.getElement(1, ORIGINAL)).isEqualTo("b a r");
        assertThat(name.isIndexed(0)).isFalse();
        assertThat(name.isIndexed(1)).isTrue();
    }

    @Test
    public void ofNameWithUppercaseInAssociativeElement() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo[BAR]");
        assertThat(name.toString()).isEqualTo("foo[BAR]");
        assertThat(name.getElement(0, ORIGINAL)).isEqualTo("foo");
        assertThat(name.getElement(1, ORIGINAL)).isEqualTo("BAR");
        assertThat(name.isIndexed(0)).isFalse();
        assertThat(name.isIndexed(1)).isTrue();
    }

    @Test
    public void ofWhenNameIsEmptyShouldReturnEmptyName() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("");
        assertThat(name.toString()).isEqualTo("");
        assertThat(name.append("foo").toString()).isEqualTo("foo");
    }

    @Test
    public void adaptWhenNameIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> ConfigurationPropertyName.adapt(null, '.')).withMessageContaining("Name must not be null");
    }

    @Test
    public void adaptWhenElementValueProcessorIsNullShouldAdapt() {
        ConfigurationPropertyName name = ConfigurationPropertyName.adapt("foo", '.', null);
        assertThat(name.toString()).isEqualTo("foo");
    }

    @Test
    public void adaptShouldCreateName() {
        ConfigurationPropertyName expected = ConfigurationPropertyName.of("foo.bar.baz");
        ConfigurationPropertyName name = ConfigurationPropertyName.adapt("foo.bar.baz", '.');
        assertThat(name).isEqualTo(expected);
    }

    @Test
    public void adaptShouldStripInvalidChars() {
        ConfigurationPropertyName name = ConfigurationPropertyName.adapt("f@@.b%r", '.');
        assertThat(name.getElement(0, UNIFORM)).isEqualTo("f");
        assertThat(name.getElement(0, ORIGINAL)).isEqualTo("f");
        assertThat(name.getElement(1, UNIFORM)).isEqualTo("br");
        assertThat(name.getElement(1, ORIGINAL)).isEqualTo("br");
        assertThat(name.toString()).isEqualTo("f.br");
    }

    @Test
    public void adaptShouldSupportUnderscore() {
        ConfigurationPropertyName name = ConfigurationPropertyName.adapt("f-_o.b_r", '.');
        assertThat(name.getElement(0, UNIFORM)).isEqualTo("fo");
        assertThat(name.getElement(0, ORIGINAL)).isEqualTo("f-_o");
        assertThat(name.getElement(1, UNIFORM)).isEqualTo("br");
        assertThat(name.getElement(1, ORIGINAL)).isEqualTo("b_r");
        assertThat(name.toString()).isEqualTo("f-o.br");
    }

    @Test
    public void adaptShouldSupportMixedCase() {
        ConfigurationPropertyName name = ConfigurationPropertyName.adapt("fOo.bAr", '.');
        assertThat(name.getElement(0, UNIFORM)).isEqualTo("foo");
        assertThat(name.getElement(0, ORIGINAL)).isEqualTo("fOo");
        assertThat(name.getElement(1, UNIFORM)).isEqualTo("bar");
        assertThat(name.getElement(1, ORIGINAL)).isEqualTo("bAr");
        assertThat(name.toString()).isEqualTo("foo.bar");
    }

    @Test
    public void adaptShouldUseElementValueProcessor() {
        ConfigurationPropertyName name = ConfigurationPropertyName.adapt("FOO_THE-BAR", '_', ( c) -> c.toString().replace("-", ""));
        assertThat(name.toString()).isEqualTo("foo.thebar");
    }

    @Test
    public void adaptShouldSupportIndexedElements() {
        ConfigurationPropertyName name = ConfigurationPropertyName.adapt("foo", '.');
        assertThat(name.toString()).isEqualTo("foo");
        assertThat(name.getNumberOfElements()).isEqualTo(1);
        name = ConfigurationPropertyName.adapt("[foo]", '.');
        assertThat(name.toString()).isEqualTo("[foo]");
        assertThat(name.getNumberOfElements()).isEqualTo(1);
        name = ConfigurationPropertyName.adapt("foo.bar", '.');
        assertThat(name.toString()).isEqualTo("foo.bar");
        assertThat(name.getNumberOfElements()).isEqualTo(2);
        name = ConfigurationPropertyName.adapt("foo[foo.bar]", '.');
        assertThat(name.toString()).isEqualTo("foo[foo.bar]");
        assertThat(name.getNumberOfElements()).isEqualTo(2);
        name = ConfigurationPropertyName.adapt("foo.[bar].baz", '.');
        assertThat(name.toString()).isEqualTo("foo[bar].baz");
        assertThat(name.getNumberOfElements()).isEqualTo(3);
    }

    @Test
    public void adaptUnderscoreShouldReturnEmpty() {
        assertThat(ConfigurationPropertyName.adapt("_", '_').isEmpty()).isTrue();
        assertThat(ConfigurationPropertyName.adapt("_", '.').isEmpty()).isTrue();
    }

    @Test
    public void isEmptyWhenEmptyShouldReturnTrue() {
        assertThat(ConfigurationPropertyName.of("").isEmpty()).isTrue();
    }

    @Test
    public void isEmptyWhenNotEmptyShouldReturnFalse() {
        assertThat(ConfigurationPropertyName.of("x").isEmpty()).isFalse();
    }

    @Test
    public void isLastElementIndexedWhenIndexedShouldReturnTrue() {
        assertThat(ConfigurationPropertyName.of("foo[0]").isLastElementIndexed()).isTrue();
    }

    @Test
    public void isLastElementIndexedWhenNotIndexedShouldReturnFalse() {
        assertThat(ConfigurationPropertyName.of("foo.bar").isLastElementIndexed()).isFalse();
        assertThat(ConfigurationPropertyName.of("foo[0].bar").isLastElementIndexed()).isFalse();
    }

    @Test
    public void getLastElementShouldGetLastElement() {
        ConfigurationPropertyName name = ConfigurationPropertyName.adapt("foo.bAr", '.');
        assertThat(name.getLastElement(ORIGINAL)).isEqualTo("bAr");
        assertThat(name.getLastElement(UNIFORM)).isEqualTo("bar");
    }

    @Test
    public void getLastElementWhenEmptyShouldReturnEmptyString() {
        ConfigurationPropertyName name = EMPTY;
        assertThat(name.getLastElement(ORIGINAL)).isEqualTo("");
        assertThat(name.getLastElement(UNIFORM)).isEqualTo("");
    }

    @Test
    public void getElementShouldNotIncludeAngleBrackets() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("[foo]");
        assertThat(name.getElement(0, ORIGINAL)).isEqualTo("foo");
        assertThat(name.getElement(0, UNIFORM)).isEqualTo("foo");
    }

    @Test
    public void getElementInUniformFormShouldNotIncludeDashes() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("f-o-o");
        assertThat(name.getElement(0, ORIGINAL)).isEqualTo("f-o-o");
        assertThat(name.getElement(0, UNIFORM)).isEqualTo("foo");
    }

    @Test
    public void getElementInOriginalFormShouldReturnElement() {
        assertThat(getElements("foo.bar", ORIGINAL)).containsExactly("foo", "bar");
        assertThat(getElements("foo[0]", ORIGINAL)).containsExactly("foo", "0");
        assertThat(getElements("foo.[0]", ORIGINAL)).containsExactly("foo", "0");
        assertThat(getElements("foo[baz]", ORIGINAL)).containsExactly("foo", "baz");
        assertThat(getElements("foo.baz", ORIGINAL)).containsExactly("foo", "baz");
        assertThat(getElements("foo[baz].bar", ORIGINAL)).containsExactly("foo", "baz", "bar");
        assertThat(getElements("foo.baz.bar", ORIGINAL)).containsExactly("foo", "baz", "bar");
        assertThat(getElements("foo.baz-bar", ORIGINAL)).containsExactly("foo", "baz-bar");
    }

    @Test
    public void getElementInUniformFormShouldReturnElement() {
        assertThat(getElements("foo.bar", UNIFORM)).containsExactly("foo", "bar");
        assertThat(getElements("foo[0]", UNIFORM)).containsExactly("foo", "0");
        assertThat(getElements("foo.[0]", UNIFORM)).containsExactly("foo", "0");
        assertThat(getElements("foo[baz]", UNIFORM)).containsExactly("foo", "baz");
        assertThat(getElements("foo.baz", UNIFORM)).containsExactly("foo", "baz");
        assertThat(getElements("foo[baz].bar", UNIFORM)).containsExactly("foo", "baz", "bar");
        assertThat(getElements("foo.baz.bar", UNIFORM)).containsExactly("foo", "baz", "bar");
        assertThat(getElements("foo.baz-bar", UNIFORM)).containsExactly("foo", "bazbar");
    }

    @Test
    public void getNumberOfElementsShouldReturnNumberOfElement() {
        assertThat(ConfigurationPropertyName.of("").getNumberOfElements()).isEqualTo(0);
        assertThat(ConfigurationPropertyName.of("x").getNumberOfElements()).isEqualTo(1);
        assertThat(ConfigurationPropertyName.of("x.y").getNumberOfElements()).isEqualTo(2);
        assertThat(ConfigurationPropertyName.of("x[0].y").getNumberOfElements()).isEqualTo(3);
    }

    @Test
    public void appendWhenNotIndexedShouldAppendWithDot() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo");
        assertThat(name.append("bar").toString()).isEqualTo("foo.bar");
    }

    @Test
    public void appendWhenIndexedShouldAppendWithBrackets() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo").append("[bar]");
        assertThat(name.isLastElementIndexed()).isTrue();
        assertThat(name.toString()).isEqualTo("foo[bar]");
    }

    @Test
    public void appendWhenElementNameIsNotValidShouldThrowException() {
        assertThatExceptionOfType(InvalidConfigurationPropertyNameException.class).isThrownBy(() -> ConfigurationPropertyName.of("foo").append("-bar")).withMessageContaining("Configuration property name '-bar' is not valid");
    }

    @Test
    public void appendWhenElementNameMultiDotShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> ConfigurationPropertyName.of("foo").append("bar.baz")).withMessageContaining("Element value 'bar.baz' must be a single item");
    }

    @Test
    public void appendWhenElementNameIsNullShouldReturnName() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo");
        assertThat(((Object) (name.append(((String) (null)))))).isSameAs(name);
    }

    @Test
    public void chopWhenLessThenSizeShouldReturnChopped() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo.bar.baz");
        assertThat(name.chop(1).toString()).isEqualTo("foo");
        assertThat(name.chop(2).toString()).isEqualTo("foo.bar");
    }

    @Test
    public void chopWhenGreaterThanSizeShouldReturnExisting() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo.bar.baz");
        assertThat(name.chop(4)).isEqualTo(name);
    }

    @Test
    public void chopWhenEqualToSizeShouldReturnExisting() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo.bar.baz");
        assertThat(name.chop(3)).isEqualTo(name);
    }

    @Test
    public void isParentOfWhenSameShouldReturnFalse() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo");
        assertThat(name.isParentOf(name)).isFalse();
    }

    @Test
    public void isParentOfWhenParentShouldReturnTrue() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo");
        ConfigurationPropertyName child = ConfigurationPropertyName.of("foo.bar");
        assertThat(name.isParentOf(child)).isTrue();
        assertThat(child.isParentOf(name)).isFalse();
    }

    @Test
    public void isParentOfWhenGrandparentShouldReturnFalse() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo");
        ConfigurationPropertyName grandchild = ConfigurationPropertyName.of("foo.bar.baz");
        assertThat(name.isParentOf(grandchild)).isFalse();
        assertThat(grandchild.isParentOf(name)).isFalse();
    }

    @Test
    public void isParentOfWhenRootReturnTrue() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("");
        ConfigurationPropertyName child = ConfigurationPropertyName.of("foo");
        ConfigurationPropertyName grandchild = ConfigurationPropertyName.of("foo.bar");
        assertThat(name.isParentOf(child)).isTrue();
        assertThat(name.isParentOf(grandchild)).isFalse();
        assertThat(child.isAncestorOf(name)).isFalse();
    }

    @Test
    public void isAncestorOfWhenSameShouldReturnFalse() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo");
        assertThat(name.isAncestorOf(name)).isFalse();
    }

    @Test
    public void isAncestorOfWhenParentShouldReturnTrue() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo");
        ConfigurationPropertyName child = ConfigurationPropertyName.of("foo.bar");
        assertThat(name.isAncestorOf(child)).isTrue();
        assertThat(child.isAncestorOf(name)).isFalse();
    }

    @Test
    public void isAncestorOfWhenGrandparentShouldReturnTrue() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo");
        ConfigurationPropertyName grandchild = ConfigurationPropertyName.of("foo.bar.baz");
        assertThat(name.isAncestorOf(grandchild)).isTrue();
        assertThat(grandchild.isAncestorOf(name)).isFalse();
    }

    @Test
    public void isAncestorOfWhenRootShouldReturnTrue() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("");
        ConfigurationPropertyName grandchild = ConfigurationPropertyName.of("foo.bar.baz");
        assertThat(name.isAncestorOf(grandchild)).isTrue();
        assertThat(grandchild.isAncestorOf(name)).isFalse();
    }

    @Test
    public void compareShouldSortNames() {
        List<ConfigurationPropertyName> names = new ArrayList<>();
        names.add(ConfigurationPropertyName.of("foo[10]"));
        names.add(ConfigurationPropertyName.of("foo.bard"));
        names.add(ConfigurationPropertyName.of("foo[2]"));
        names.add(ConfigurationPropertyName.of("foo.bar"));
        names.add(ConfigurationPropertyName.of("foo.baz"));
        names.add(ConfigurationPropertyName.of("foo"));
        Collections.sort(names);
        assertThat(names.stream().map(ConfigurationPropertyName::toString).collect(Collectors.toList())).containsExactly("foo", "foo[2]", "foo[10]", "foo.bar", "foo.bard", "foo.baz");
    }

    @Test
    public void compareDifferentLengthsShouldSortNames() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("spring.resources.chain.strategy.content");
        ConfigurationPropertyName other = ConfigurationPropertyName.of("spring.resources.chain.strategy.content.enabled");
        assertThat(name.compareTo(other)).isLessThan(0);
    }

    @Test
    public void toStringShouldBeLowerCaseDashed() {
        ConfigurationPropertyName name = ConfigurationPropertyName.adapt("fOO.b_-a-r", '.');
        assertThat(name.toString()).isEqualTo("foo.b-a-r");
    }

    @Test
    public void toStringFromOfShouldBeLowerCaseDashed() {
        ConfigurationPropertyName name = ConfigurationPropertyName.of("foo.bar-baz");
        assertThat(name.toString()).isEqualTo("foo.bar-baz");
    }

    @Test
    public void equalsAndHashCode() {
        ConfigurationPropertyName n01 = ConfigurationPropertyName.of("foo[bar]");
        ConfigurationPropertyName n02 = ConfigurationPropertyName.of("foo[bar]");
        ConfigurationPropertyName n03 = ConfigurationPropertyName.of("foo.bar");
        ConfigurationPropertyName n04 = ConfigurationPropertyName.of("f-o-o.b-a-r");
        ConfigurationPropertyName n05 = ConfigurationPropertyName.of("foo[BAR]");
        ConfigurationPropertyName n06 = ConfigurationPropertyName.of("oof[bar]");
        ConfigurationPropertyName n07 = ConfigurationPropertyName.of("foo.bar");
        ConfigurationPropertyName n08 = EMPTY;
        ConfigurationPropertyName n09 = ConfigurationPropertyName.of("foo");
        ConfigurationPropertyName n10 = ConfigurationPropertyName.of("fo");
        ConfigurationPropertyName n11 = ConfigurationPropertyName.adapt("foo.BaR", '.');
        ConfigurationPropertyName n12 = ConfigurationPropertyName.of("f-o-o[b-a-r]");
        ConfigurationPropertyName n13 = ConfigurationPropertyName.of("f-o-o[b-a-r--]");
        ConfigurationPropertyName n14 = ConfigurationPropertyName.of("[1]");
        ConfigurationPropertyName n15 = ConfigurationPropertyName.of("[-1]");
        assertThat(n01.hashCode()).isEqualTo(n02.hashCode());
        assertThat(n01.hashCode()).isEqualTo(n02.hashCode());
        assertThat(n01.hashCode()).isEqualTo(n03.hashCode());
        assertThat(n01.hashCode()).isEqualTo(n04.hashCode());
        assertThat(n01.hashCode()).isEqualTo(n11.hashCode());
        assertThat(((Object) (n01))).isEqualTo(n01);
        assertThat(((Object) (n01))).isEqualTo(n02);
        assertThat(((Object) (n01))).isEqualTo(n03);
        assertThat(((Object) (n01))).isEqualTo(n04);
        assertThat(((Object) (n11))).isEqualTo(n03);
        assertThat(((Object) (n03))).isEqualTo(n11);
        assertThat(((Object) (n01))).isNotEqualTo(n05);
        assertThat(((Object) (n01))).isNotEqualTo(n06);
        assertThat(((Object) (n07))).isNotEqualTo(n08);
        assertThat(((Object) (n09))).isNotEqualTo(n10);
        assertThat(((Object) (n10))).isNotEqualTo(n09);
        assertThat(((Object) (n12))).isNotEqualTo(n13);
        assertThat(((Object) (n14))).isNotEqualTo(n15);
    }

    @Test
    public void equalsWhenStartsWith() {
        // gh-14665
        ConfigurationPropertyName n1 = ConfigurationPropertyName.of("my.sources[0].xame");
        ConfigurationPropertyName n2 = ConfigurationPropertyName.of("my.sources[0].xamespace");
        assertThat(n1).isNotEqualTo(n2);
    }

    @Test
    public void equalsWhenStartsWithOfAdaptedName() {
        // gh-15152
        ConfigurationPropertyName n1 = ConfigurationPropertyName.adapt("example.mymap.ALPHA", '.');
        ConfigurationPropertyName n2 = ConfigurationPropertyName.adapt("example.mymap.ALPHA_BRAVO", '.');
        assertThat(n1).isNotEqualTo(n2);
    }

    @Test
    public void equalsWhenStartsWithOfAdaptedNameOfIllegalChars() {
        // gh-15152
        ConfigurationPropertyName n1 = ConfigurationPropertyName.adapt("example.mymap.ALPH!", '.');
        ConfigurationPropertyName n2 = ConfigurationPropertyName.adapt("example.mymap.ALPHA!BRAVO", '.');
        assertThat(n1).isNotEqualTo(n2);
    }

    @Test
    public void isValidWhenValidShouldReturnTrue() {
        assertThat(ConfigurationPropertyName.isValid("")).isTrue();
        assertThat(ConfigurationPropertyName.isValid("foo")).isTrue();
        assertThat(ConfigurationPropertyName.isValid("foo.bar")).isTrue();
        assertThat(ConfigurationPropertyName.isValid("foo[0]")).isTrue();
        assertThat(ConfigurationPropertyName.isValid("foo[0].baz")).isTrue();
        assertThat(ConfigurationPropertyName.isValid("foo.b1")).isTrue();
        assertThat(ConfigurationPropertyName.isValid("foo.b-a-r")).isTrue();
        assertThat(ConfigurationPropertyName.isValid("foo[FooBar].baz")).isTrue();
    }

    @Test
    public void isValidWhenNotValidShouldReturnFalse() {
        assertThat(ConfigurationPropertyName.isValid(null)).isFalse();
        assertThat(ConfigurationPropertyName.isValid("-foo")).isFalse();
        assertThat(ConfigurationPropertyName.isValid("FooBar")).isFalse();
        assertThat(ConfigurationPropertyName.isValid("foo!bar")).isFalse();
    }
}

