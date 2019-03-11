/**
 * This file is part of FNLP (formerly FudanNLP).
 *
 *  FNLP is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  FNLP is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with FudanNLP.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2009-2014 www.fnlp.org. All rights reserved.
 */
package org.fnlp.nlp.parser.dep.yamada;


import org.junit.Test;


public class YamadaParserTest {
    @Test
    public void testParseStringArrayStringArray() throws Exception {
        // YamadaParser parser = new YamadaParser("../models/dep.m");
        // 
        // System.out.println("??????????");
        // String word = "????????????????";
        // POSTagger tag = new POSTagger("../models/pos.m");
        // String[][] s = tag.tag2Array(word);
        // int[] heads = null;
        // try {
        // //			parser.getBestParse(s[0], s[1], heads);
        // heads = parser.parse(s[0], s[1]);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // 
        // for(int i = 0; i < heads.length; i++)
        // System.out.printf("%s\t%s\t%d\n", s[0][i], s[1][i], heads[i]);
        // 
        // System.out.println("???????????");
        // 
        // String[] words = new String[]{"??", "???", "??", "?", "??", "??", "??", "??"};
        // String[] pos = new String[]{"NR", "NN", "NN", "CC", "NR", "NN", "VV", "NN"};
        // heads = null;
        // 
        // try {
        // heads = parser.parse(words, pos);
        // } catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // 
        // for(int i = 0; i < heads.length; i++)
        // System.out.printf("%s\t%s\t%d\n", words[i], pos[i], heads[i]);
    }
}

