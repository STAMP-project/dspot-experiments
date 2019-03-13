/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.function;


import org.hibernate.dialect.function.AnsiTrimEmulationFunction;
import org.junit.Assert;
import org.junit.Test;


/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public class AnsiTrimEmulationFunctionTest {
    private static final String trimSource = "a.column";

    @Test
    public void testBasicSqlServerProcessing() {
        AnsiTrimEmulationFunction function = new AnsiTrimEmulationFunction();
        performBasicSpaceTrimmingTests(function);
        final String expectedTrimPrep = "replace(replace(a.column,' ','${space}$'),'-',' ')";
        final String expectedPostTrimPrefix = "replace(replace(";
        final String expectedPostTrimSuffix = ",' ','-'),'${space}$',' ')";
        // -> trim(LEADING '-' FROM a.column)
        String rendered = function.render(null, argList("LEADING", "'-'", "FROM", AnsiTrimEmulationFunctionTest.trimSource), null);
        String expected = (((expectedPostTrimPrefix + "ltrim(") + expectedTrimPrep) + ")") + expectedPostTrimSuffix;
        Assert.assertEquals(expected, rendered);
        // -> trim(TRAILING '-' FROM a.column)
        rendered = function.render(null, argList("TRAILING", "'-'", "FROM", AnsiTrimEmulationFunctionTest.trimSource), null);
        expected = (((expectedPostTrimPrefix + "rtrim(") + expectedTrimPrep) + ")") + expectedPostTrimSuffix;
        Assert.assertEquals(expected, rendered);
        // -> trim(BOTH '-' FROM a.column)
        rendered = function.render(null, argList("BOTH", "'-'", "FROM", AnsiTrimEmulationFunctionTest.trimSource), null);
        expected = (((expectedPostTrimPrefix + "ltrim(rtrim(") + expectedTrimPrep) + "))") + expectedPostTrimSuffix;
        Assert.assertEquals(expected, rendered);
        // -> trim('-' FROM a.column)
        rendered = function.render(null, argList("'-'", "FROM", AnsiTrimEmulationFunctionTest.trimSource), null);
        expected = (((expectedPostTrimPrefix + "ltrim(rtrim(") + expectedTrimPrep) + "))") + expectedPostTrimSuffix;
        Assert.assertEquals(expected, rendered);
    }

    @Test
    public void testBasicSybaseProcessing() {
        AnsiTrimEmulationFunction function = new AnsiTrimEmulationFunction(AnsiTrimEmulationFunction.LTRIM, AnsiTrimEmulationFunction.RTRIM, "str_replace");
        performBasicSpaceTrimmingTests(function);
        final String expectedTrimPrep = "str_replace(str_replace(a.column,' ','${space}$'),'-',' ')";
        final String expectedPostTrimPrefix = "str_replace(str_replace(";
        final String expectedPostTrimSuffix = ",' ','-'),'${space}$',' ')";
        // -> trim(LEADING '-' FROM a.column)
        String rendered = function.render(null, argList("LEADING", "'-'", "FROM", AnsiTrimEmulationFunctionTest.trimSource), null);
        String expected = (((expectedPostTrimPrefix + "ltrim(") + expectedTrimPrep) + ")") + expectedPostTrimSuffix;
        Assert.assertEquals(expected, rendered);
        // -> trim(TRAILING '-' FROM a.column)
        rendered = function.render(null, argList("TRAILING", "'-'", "FROM", AnsiTrimEmulationFunctionTest.trimSource), null);
        expected = (((expectedPostTrimPrefix + "rtrim(") + expectedTrimPrep) + ")") + expectedPostTrimSuffix;
        Assert.assertEquals(expected, rendered);
        // -> trim(BOTH '-' FROM a.column)
        rendered = function.render(null, argList("BOTH", "'-'", "FROM", AnsiTrimEmulationFunctionTest.trimSource), null);
        expected = (((expectedPostTrimPrefix + "ltrim(rtrim(") + expectedTrimPrep) + "))") + expectedPostTrimSuffix;
        Assert.assertEquals(expected, rendered);
        // -> trim('-' FROM a.column)
        rendered = function.render(null, argList("'-'", "FROM", AnsiTrimEmulationFunctionTest.trimSource), null);
        expected = (((expectedPostTrimPrefix + "ltrim(rtrim(") + expectedTrimPrep) + "))") + expectedPostTrimSuffix;
        Assert.assertEquals(expected, rendered);
    }
}

