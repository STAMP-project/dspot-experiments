/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2015 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.rules;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ConfusionSetTest {
    @Test
    public void testGet() {
        ConfusionSet confusionSet = new ConfusionSet(1, "one", "two");
        Assert.assertThat(confusionSet.getSet().size(), CoreMatchers.is(2));
        Assert.assertTrue(confusionSet.getSet().toString().contains("one"));
        Assert.assertTrue(confusionSet.getSet().toString().contains("two"));
        Assert.assertThat(confusionSet.getUppercaseFirstCharSet().size(), CoreMatchers.is(2));
        Assert.assertTrue(confusionSet.getUppercaseFirstCharSet().toString().contains("One"));
        Assert.assertTrue(confusionSet.getUppercaseFirstCharSet().toString().contains("Two"));
    }

    @Test
    public void testEquals() {
        ConfusionSet confusionSet1a = new ConfusionSet(1, "one", "two");
        ConfusionSet confusionSet1b = new ConfusionSet(1, "two", "one");
        ConfusionSet confusionSet3 = new ConfusionSet(1, "Two", "one");
        ConfusionSet confusionSet4 = new ConfusionSet(2, "Two", "one");
        Assert.assertTrue(confusionSet1a.equals(confusionSet1b));
        Assert.assertFalse(confusionSet1a.equals(confusionSet3));
        Assert.assertFalse(confusionSet1b.equals(confusionSet3));
        Assert.assertFalse(confusionSet3.equals(confusionSet4));
    }
}

