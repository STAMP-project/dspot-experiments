/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.userguide.util;


import org.hibernate.jpa.internal.util.PessimisticNumberParser;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sanne Grinovero
 */
public class PessimisticNumberParserUnitTest {
    @Test
    public void testEmptyStringBehaviour() {
        Assert.assertNull(PessimisticNumberParser.toNumberOrNull(""));
    }

    @Test
    public void testPlusStringBehaviour() {
        Assert.assertNull(PessimisticNumberParser.toNumberOrNull("+"));
    }

    @Test
    public void testMinusStringBehaviour() {
        Assert.assertNull(PessimisticNumberParser.toNumberOrNull("-"));
    }

    @Test
    public void testLetterStringBehaviour() {
        Assert.assertNull(PessimisticNumberParser.toNumberOrNull("h"));
    }

    @Test
    public void testTextStringBehaviour() {
        Assert.assertNull(PessimisticNumberParser.toNumberOrNull("hello world!"));
    }

    @Test
    public void testFoolingPrefixStringBehaviour() {
        Assert.assertNull(PessimisticNumberParser.toNumberOrNull("+60000g"));
    }

    @Test
    public void testFiveStringBehaviour() {
        Assert.assertEquals(Integer.valueOf(5), PessimisticNumberParser.toNumberOrNull("5"));
    }

    @Test
    public void testNegativeStringBehaviour() {
        // technically illegal for the case of positional parameters, but we can parse it
        Assert.assertEquals(Integer.valueOf((-25)), PessimisticNumberParser.toNumberOrNull("-25"));
    }

    @Test
    public void testBigintegerStringBehaviour() {
        Assert.assertEquals(Integer.valueOf(60000), PessimisticNumberParser.toNumberOrNull("60000"));
    }

    @Test
    public void testPositiveStringBehaviour() {
        Assert.assertEquals(Integer.valueOf(60000), PessimisticNumberParser.toNumberOrNull("+60000"));
    }
}

