/**
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, ???, yang-shangchuan@qq.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.apdplat.word.analysis;


import junit.framework.TestCase;
import org.junit.Test;


/**
 * ??? ??????? ?????
 *
 * @author ???
 */
public class TextSimilarityTest {
    private static final String TEXT1 = "QuestionAnsweringSystem???Java??????????????????????????";

    private static final String TEXT2 = "superword???Java????????????????????????????????????????????????";

    private static final String TEXT3 = "HtmlExtractor???Java??????????????????????";

    private static final String TEXT4 = "APDPlat?Application Product Development Platform???????????????";

    private static final String TEXT5 = "word?????Java?????????????";

    private static final String TEXT6 = "jsearch????????????????????????java8????lucene???????";

    private static final String TEXT7 = "rank???seo???????????????????";

    private static final String TEXT8 = "Java????cws_evaluation??????????????";

    private static final String TEXT9 = "word_web - ??web????word?????????????";

    private static final String TEXT10 = "??????????borm";

    private static final String TEXT11 = "??????search";

    @Test
    public void testCosine() {
        TextSimilarity textSimilarity = new CosineTextSimilarity();
        testBasic(textSimilarity);
        testRank(textSimilarity);
        TestCase.assertEquals(1.0, textSimilarity.similarScore(TextSimilarityTest.TEXT1, TextSimilarityTest.TEXT1));
    }

    @Test
    public void testEditDistance() {
        TextSimilarity textSimilarity = new EditDistanceTextSimilarity();
        testBasic(textSimilarity);
        testRank(textSimilarity);
    }

    @Test
    public void testEuclideanDistance() {
        TextSimilarity textSimilarity = new EuclideanDistanceTextSimilarity();
        testBasic(textSimilarity);
        testRank(textSimilarity);
    }

    @Test
    public void testJaccard() {
        TextSimilarity textSimilarity = new JaccardTextSimilarity();
        testBasic(textSimilarity);
        testRank(textSimilarity);
    }

    @Test
    public void testJaroDistance() {
        TextSimilarity textSimilarity = new JaroDistanceTextSimilarity();
        testBasic(textSimilarity);
        // JaroDistanceTextSimilarity ??????????????????????
        // ??????????
        // testRank(textSimilarity);
    }

    @Test
    public void testJaroWinklerDistance() {
        TextSimilarity textSimilarity = new JaroWinklerDistanceTextSimilarity();
        testBasic(textSimilarity);
        // JaroWinklerDistanceTextSimilarity ??????????????????????
        // ??????????
        // testRank(textSimilarity);
    }

    @Test
    public void testManhattanDistance() {
        TextSimilarity textSimilarity = new ManhattanDistanceTextSimilarity();
        testBasic(textSimilarity);
        testRank(textSimilarity);
    }

    @Test
    public void testSimHashPlusHammingDistance() {
        TextSimilarity textSimilarity = new SimHashPlusHammingDistanceTextSimilarity();
        testBasic(textSimilarity);
        // SimHashPlusHammingDistanceTextSimilarity ??????????????????????
        // ??????????
        // testRank(textSimilarity);
    }

    @Test
    public void testSimple() {
        TextSimilarity textSimilarity = new SimpleTextSimilarity();
        testBasic(textSimilarity);
        testRank(textSimilarity);
    }

    @Test
    public void testS?rensenDiceCoefficient() {
        TextSimilarity textSimilarity = new S?rensenDiceCoefficientTextSimilarity();
        testBasic(textSimilarity);
        testRank(textSimilarity);
    }
}

