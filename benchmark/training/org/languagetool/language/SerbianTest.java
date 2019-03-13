/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2017 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.language;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class SerbianTest {
    private final Serbian serbian = new Serbian();

    private final List<String> ruleFiles = new ArrayList<>();

    private static final List<String> RULE_FILE_NAMES = // Tested method adds this by default
    Arrays.asList("grammar.xml", "grammar-barbarism.xml", "grammar-logical.xml", "grammar-punctuation.xml", "grammar-spelling.xml", "grammar-style.xml");

    @Test
    public void getRuleFileNames() throws Exception {
        Assert.assertEquals(ruleFiles, serbian.getRuleFileNames());
    }
}

