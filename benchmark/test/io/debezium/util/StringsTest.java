/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.util;


import io.debezium.text.ParsingException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Randall Hauch
 * @author Horia Chiorean
 */
public class StringsTest {
    @Test
    public void splitLinesShouldWorkCorrectly() {
        compareSeparatedLines("Line 1", "Line 2", "Line 3", "Line 4");
    }

    @Test
    public void setLengthShouldTruncateStringsThatAreTooLong() {
        Assert.assertEquals("This is the st", Strings.setLength("This is the string", 14, ' '));
    }

    @Test
    public void setLengthShouldAppendCharacterForStringsThatAreTooShort() {
        Assert.assertEquals("This      ", Strings.setLength("This", 10, ' '));
    }

    @Test
    public void setLengthShouldNotRemoveLeadingWhitespace() {
        Assert.assertEquals(" This     ", Strings.setLength(" This", 10, ' '));
        Assert.assertEquals("\tThis     ", Strings.setLength("\tThis", 10, ' '));
    }

    @Test
    public void setLengthShouldAppendCharacterForEmptyStrings() {
        Assert.assertEquals("          ", Strings.setLength("", 10, ' '));
    }

    @Test
    public void setLengthShouldAppendCharacterForNullStrings() {
        Assert.assertEquals("          ", Strings.setLength(null, 10, ' '));
    }

    @Test
    public void setLengthShouldReturnStringsThatAreTheDesiredLength() {
        Assert.assertEquals("This is the string", Strings.setLength("This is the string", 18, ' '));
    }

    @Test
    public void justifyLeftShouldTruncateStringsThatAreTooLong() {
        Assert.assertEquals("This is the st", Strings.justifyLeft("This is the string", 14, ' '));
    }

    @Test
    public void justifyLeftShouldAppendCharacterForStringsThatAreTooShort() {
        Assert.assertEquals("This      ", Strings.justifyLeft("This", 10, ' '));
    }

    @Test
    public void justifyLeftShouldRemoveLeadingWhitespace() {
        Assert.assertEquals("This      ", Strings.justifyLeft(" This", 10, ' '));
        Assert.assertEquals("This      ", Strings.justifyLeft("\tThis", 10, ' '));
    }

    @Test
    public void justifyLeftShouldAppendCharacterForEmptyStrings() {
        Assert.assertEquals("          ", Strings.justifyLeft("", 10, ' '));
    }

    @Test
    public void justifyLeftShouldAppendCharacterForNullStrings() {
        Assert.assertEquals("          ", Strings.justifyLeft(null, 10, ' '));
    }

    @Test
    public void justifyLeftShouldReturnStringsThatAreTheDesiredLength() {
        Assert.assertEquals("This is the string", Strings.justifyLeft("This is the string", 18, ' '));
    }

    @Test
    public void justifyRightShouldTruncateStringsThatAreTooLong() {
        Assert.assertEquals(" is the string", Strings.justifyRight("This is the string", 14, ' '));
    }

    @Test
    public void justifyRightShouldPrependCharacterForStringsThatAreTooShort() {
        Assert.assertEquals("      This", Strings.justifyRight("This", 10, ' '));
    }

    @Test
    public void justifyRightShouldPrependCharacterForEmptyStrings() {
        Assert.assertEquals("          ", Strings.justifyRight("", 10, ' '));
    }

    @Test
    public void justifyRightShouldPrependCharacterForNullStrings() {
        Assert.assertEquals("          ", Strings.justifyRight(null, 10, ' '));
    }

    @Test
    public void justifyRightShouldReturnStringsThatAreTheDesiredLength() {
        Assert.assertEquals("This is the string", Strings.justifyRight("This is the string", 18, ' '));
    }

    @Test
    public void justifyCenterShouldTruncateStringsThatAreTooLong() {
        Assert.assertEquals("This is the st", Strings.justifyCenter("This is the string", 14, ' '));
    }

    @Test
    public void justifyCenterShouldPrependAndAppendSameNumberOfCharacterForStringsThatAreTooShortButOfAnEvenLength() {
        Assert.assertEquals("   This   ", Strings.justifyCenter("This", 10, ' '));
    }

    @Test
    public void justifyCenterShouldPrependOneMoreCharacterThanAppendingForStringsThatAreTooShortButOfAnOddLength() {
        Assert.assertEquals("   Thing  ", Strings.justifyCenter("Thing", 10, ' '));
    }

    @Test
    public void justifyCenterShouldPrependCharacterForEmptyStrings() {
        Assert.assertEquals("          ", Strings.justifyCenter("", 10, ' '));
    }

    @Test
    public void justifyCenterShouldPrependCharacterForNullStrings() {
        Assert.assertEquals("          ", Strings.justifyCenter(null, 10, ' '));
    }

    @Test
    public void justifyCenterShouldReturnStringsThatAreTheDesiredLength() {
        Assert.assertEquals("This is the string", Strings.justifyCenter("This is the string", 18, ' '));
    }

    @Test
    public void replaceVariablesShouldReplaceVariableThatHaveSameCase() {
        assertReplacement("some ${v1} text", vars("v1", "abc"), "some abc text");
        assertReplacement("some ${v1} text", vars("v1", "abc", "V1", "ABC"), "some abc text");
        assertReplacement("some ${v1:other} text", vars("V1", "ABC"), "some other text");
    }

    @Test
    public void replaceVariablesShouldNotReplaceVariableThatHasNoDefaultAndIsNotFound() {
        assertReplacement("some ${varName} text", vars("v1", "value1"), "some ${varName} text");
        assertReplacement("some${varName}text", vars("v1", "value1"), "some${varName}text");
        assertReplacement("${varName}", vars("v1", "value1"), "${varName}");
    }

    @Test
    public void replaceVariablesShouldReplaceVariablesWithNoDefault() {
        assertReplacement("${varName}", vars("varName", "replaced"), "replaced");
        assertReplacement("some${varName}text", vars("varName", "replaced"), "somereplacedtext");
        assertReplacement("some ${varName} text", vars("varName", "replaced"), "some replaced text");
        assertReplacement("some ${var1,var2,var3:other} text", vars("var1", "replaced"), "some replaced text");
        assertReplacement("some ${var1,var2,var3:other} text", vars("v1", "replaced", "var2", "new"), "some new text");
        assertReplacement("some ${var1,var2,var3:other} text", vars("v1", "replaced", "var3", "new"), "some new text");
    }

    @Test
    public void replaceVariablesShouldReplaceVariablesWithDefaultWhenNoReplacementIsFound() {
        assertReplacement("some${varName:other}text", vars("v1", "replaced"), "someothertext");
        assertReplacement("some ${varName:other} text", vars("v1", "replaced"), "some other text");
        assertReplacement("some ${var1,var2,var3:other} text", vars("var10", "replaced"), "some other text");
        assertReplacement("some ${var1,var2,var3:other} text", vars("var10", "replaced", "var11", "new"), "some other text");
    }

    @Test
    public void replaceVariablesShouldReplaceMultipleVariables() {
        assertReplacement("${v1}${v1}", vars("v1", "first", "v2", "second"), "firstfirst");
        assertReplacement("${v1}${v2}", vars("v1", "first", "v2", "second"), "firstsecond");
        assertReplacement("some${v1}text${v2}end", vars("v1", "first", "v2", "second"), "somefirsttextsecondend");
        assertReplacement("some ${v1} text ${v2} end", vars("v1", "first", "v2", "second"), "some first text second end");
        assertReplacement("some ${v1:other} text", vars("vx1", "replaced"), "some other text");
        assertReplacement("some ${v1,v2,v3:other} text ${v1,v2,v3:other}", vars("var10", "replaced", "v2", "s"), "some s text s");
        assertReplacement("some ${v1,v2:other}${v2,v3:other} text", vars("v1", "1", "v2", "2"), "some 12 text");
    }

    @Test
    public void isNullOrEmptyReturnsTrueForNull() {
        assertThat(Strings.isNullOrEmpty(null)).isTrue();
    }

    @Test
    public void isNullOrEmptyReturnsTrueForEmptyString() {
        assertThat(Strings.isNullOrEmpty("")).isTrue();
    }

    @Test
    public void isNullOrEmptyReturnsFalseForFalseForNonEmptyString() {
        assertThat(Strings.isNullOrEmpty("hello")).isFalse();
    }

    @Test
    public void isNullOrEmptyReturnsFalseForFalseForBlankString() {
        assertThat(Strings.isNullOrEmpty("     ")).isFalse();
    }

    @Test
    public void isNumericShouldReturnFalseForNull() {
        assertThat(Strings.isNumeric(null)).isFalse();
    }

    @Test
    public void isNumericShouldReturnFalseForEmptyString() {
        assertThat(Strings.isNumeric("")).isFalse();
    }

    @Test
    public void isNumericShouldReturnFalseForBlankString() {
        assertThat(Strings.isNumeric("     ")).isFalse();
    }

    @Test
    public void isNumericShouldReturnTrueForNumericString() {
        assertThat(Strings.isNumeric("123")).isTrue();
    }

    @Test
    public void unquoteIdentifierPartShouldReturnSameValueForUnquotedString() {
        assertThat(Strings.unquoteIdentifierPart("table")).isEqualTo("table");
    }

    @Test
    public void unquoteIdentifierPartShouldReturnEmptyStringForEmptyQuotedString() {
        assertThat(Strings.unquoteIdentifierPart("''")).isEqualTo("");
    }

    @Test
    public void unquoteIdentifierPartShouldReturnUnquotedString() {
        assertThat(Strings.unquoteIdentifierPart("'Table'")).isEqualTo("Table");
    }

    @Test
    public void unquoteIdentifierPartShouldUnescapeEscapedQuote() {
        assertThat(Strings.unquoteIdentifierPart("'Tab''le'")).isEqualTo("Tab'le");
    }

    @Test
    public void unquoteIdentifierPartShouldSupportDoubleQuotes() {
        assertThat(Strings.unquoteIdentifierPart("\"Tab\"\"le\"")).isEqualTo("Tab\"le");
    }

    @Test
    public void unquoteIdentifierPartShouldSupportBackTicks() {
        assertThat(Strings.unquoteIdentifierPart("`Tab``le`")).isEqualTo("Tab`le");
    }

    @Test
    public void hexStringToByteArrayShouldReturnCorrectByteArray() {
        assertThat(Strings.hexStringToByteArray(null)).isNull();
        assertThat(Strings.hexStringToByteArray("00")).isEqualTo(new byte[]{ 0 });
        assertThat(Strings.hexStringToByteArray("010203")).isEqualTo(new byte[]{ 1, 2, 3 });
        assertThat(Strings.hexStringToByteArray("CAFEBABE")).isEqualTo(new byte[]{ -54, -2, -70, -66 });
    }

    @Test
    public void regexSplit() {
        assertRegexSet("a,b", "a", "b");
        assertRegexSet("a\\,b", "a,b");
        assertRegexSet("a,b,", "a", "b");
        assertRegexSet("a,b\\,", "a", "b,");
        assertRegexSet("a\\\\\\,b", "a\\\\,b");
        assertRegexSet(("DROP TEMPORARY TABLE IF EXISTS .+ /\\\\* generated by server \\\\*/," + "INSERT INTO mysql.rds_heartbeat2\\(.*\\,.*\\) values \\(.*\\,.*\\) ON DUPLICATE KEY UPDATE value = .*"), "DROP TEMPORARY TABLE IF EXISTS .+ /\\\\* generated by server \\\\*/", "INSERT INTO mysql.rds_heartbeat2\\(.*,.*\\) values \\(.*,.*\\) ON DUPLICATE KEY UPDATE value = .*");
        assertRegexList("a,b", "a", "b");
        assertRegexList("a\\,b", "a,b");
        assertRegexList("a,b,", "a", "b");
        assertRegexList("a,b\\,", "a", "b,");
        assertRegexList("a\\\\\\,b", "a\\\\,b");
        assertRegexList(("DROP TEMPORARY TABLE IF EXISTS .+ /\\\\* generated by server \\\\*/," + "INSERT INTO mysql.rds_heartbeat2\\(.*\\,.*\\) values \\(.*\\,.*\\) ON DUPLICATE KEY UPDATE value = .*"), "DROP TEMPORARY TABLE IF EXISTS .+ /\\\\* generated by server \\\\*/", "INSERT INTO mysql.rds_heartbeat2\\(.*,.*\\) values \\(.*,.*\\) ON DUPLICATE KEY UPDATE value = .*");
    }

    @Test
    public void joinShouldExcludeFirstNullableElement() {
        assertThat(Strings.join(",", Arrays.asList(null, "b", "c"))).isEqualTo("b,c");
    }

    @Test
    public void joinShouldExcludeSecondNullableElement() {
        assertThat(Strings.join(",", Arrays.asList("a", null, "c"))).isEqualTo("a,c");
    }

    @Test
    public void joinShouldExcludeAllNullableElements() {
        assertThat(Strings.join(",", Arrays.asList(null, null, null))).isEqualTo("");
    }

    @Test
    public void joinWithConversionShouldConvertAllElements() {
        assertThat(Strings.join(",", Arrays.asList("a", "b", "c"), ( s) -> "_" + s)).isEqualTo("_a,_b,_c");
    }

    @Test(expected = ParsingException.class)
    public void regexSplitWrongEscape() {
        Strings.setOfRegex("a,b\\,c\\");
    }
}

