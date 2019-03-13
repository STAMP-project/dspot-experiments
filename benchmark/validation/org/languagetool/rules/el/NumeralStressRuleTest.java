/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2016 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.rules.el;


import java.io.IOException;
import org.junit.Test;
import org.languagetool.JLanguageTool;


/**
 * NumeralStressRule TestCase.
 *
 * @author Panagiotis Minos
 * @since 3.3
 */
public class NumeralStressRuleTest {
    private NumeralStressRule rule;

    private JLanguageTool langTool;

    @Test
    public void testRule() throws IOException {
        assertCorrect("1??");
        assertCorrect("2?");
        assertCorrect("3?");
        assertCorrect("20??");
        assertCorrect("30?");
        assertCorrect("40?");
        assertCorrect("1000??");
        assertCorrect("1010??");
        assertIncorrect("4??", "4??");
        assertIncorrect("5?", "5?");
        assertIncorrect("6?", "6?");
        assertIncorrect("100??", "100??");
        assertIncorrect("200?", "200?");
        assertIncorrect("300?", "300?");
        assertIncorrect("2000??", "2000??");
        assertIncorrect("2010??", "2010??");
        assertIncorrect("2020?", "2020?");
    }
}

