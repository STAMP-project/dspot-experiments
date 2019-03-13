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
package org.fnlp.nlp.similarity;


import java.io.IOException;
import org.fnlp.data.reader.StringReader;
import org.fnlp.nlp.similarity.train.WordCluster;
import org.junit.Test;


public class WordClusterTest {
    @Test
    public void testStartClustering() {
        WordCluster wc = new WordCluster();
        wc.slotsize = 6;
        String[] strs = new String[]{ "??", "??", "??", "??", "??", "??" };
        StringReader r = new StringReader(strs);
        wc.read(r);
        try {
            Cluster root = wc.startClustering();
            DrawTree.printTree(root, "../tmp/t.png");
            wc.saveModel("../tmp/t.m");
            wc.saveTxt("../tmp/t.txt");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

