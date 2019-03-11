/**
 * *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package smile.math.distance;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class EditDistanceTest {
    String H1N1 = "ATGGAGAGAATAAAAGAACTGAGAGATCTAATGTCGCAGTCCCGCACTCGCGAGATACTCACTAAGACCACTGTGGACCATATGGCCATAATCAAAAAGTACACATCAGGAAGGCAAGAGAAGAACCCCGCACTCAGAATGAAGTGGATGATGGCAATGAGATACCCAATTACAGCAGACAAGAGAATAATGGACATGATTCCAGAGAGGAATGAACAAGGACAAACCCTCTGGAGCAAAACAAACGATGCTGGATCAGACCGAGTGATGGTATCACCTCTGGCCGTAACATGGTGGAATAGGAATGGCCCAACAACAAGTACAGTTCATTACCCTAAGGTATATAAAACTTATTTCGAAAAGGTCGAAAGGTTGAAACATGGTACCTTCGGCCCTGTCCACTTCAGAAATCAAGTTAAAATAAGGAGGAGAGTTGATACAAACCCTGGCCATGCAGATCTCAGTGCCAAGGAGGCACAGGATGTGATTATGGAAGTTGTTTTCCCAAATGAAGTGGGGGCAAGAATACTGACATCAGAGTCACAGCTGGCAATAACAAAAGAGAAGAAAGAAGAGCTCCAGGATTGTAAAATTGCTCCCTTGATGGTGGCGTACATGCTAGAAAGAGAATTGGTCCGTAAAACAAGGTTTCTCCCAGTAGCCGGCGGAACAGGCAGTGTTTATATTGAAGTGTTGCACTTAACCCAAGGGACGTGCTGGGAGCAGATGTACACTCCAGGAGGAGAAGTGAGAAATGATGATGTTGACCAAAGTTTGATTATCGCTGCTAGAAACATAGTAAGAAGAGCAGCAGTGTCAGCAGACCCATTAGCATCTCTCTTGGAAATGTGCCACAGCACACAGATTGGAGGAGTAAGGATGGTGGACATCCTTAGACAGAATCCAACTGAGGAACAAGCCGTAGACATATGCAAGGCAGCAATAGGGTTGAGGATTAGCTCATCTTTCAGTTTTGGTGGGTTCACTTTCAAAAGGACAAGCGGATCATCAGTCAAGAAAGAAGAAGAAGTGCTAACGGGCAACCTCCAAACACTGAAAATAAGAGTACATGAAGGGTATGAAGAATTCACAATGGTTGGGAGAAGAGCAACAGCTATTCTCAGAAAGGCAACCAGGAGATTGATCCAGTTGATAGTAAGCGGGAGAGACGAGCAGTCAATTGCTGAGGCAATAATTGTGGCCATGGTATTCTCACAAGAGGATTGCATGATCAAGGCAGTTAGGGGCGATCTGAACTTTGTCAATAGGGCAAACCAGCGACTGAACCCCATGCACCAACTCTTGAGGCATTTCCAAAAAGATGCAAAAGTGCTTTTCCAGAACTGGGGAATTGAATCCATCGACAATGTGATGGGAATGATCGGAATACTGCCCGACATGACCCCAAGCACGGAGATGTCGCTGAGAGGGATAAGAGTCAGCAAAATGGGAGTAGATGAATACTCCAGCACGGAGAGAGTGGTAGTGAGTATTGACCGATTTTTAAGGGTTAGAGATCAAAGAGGGAACGTACTATTGTCTCCCGAAGAAGTCAGTGAAACGCAAGGAACTGAGAAGTTGACAATAACTTATTCGTCATCAATGATGTGGGAGATCAATGGCCCTGAGTCAGTGCTAGTCAACACTTATCAATGGATAATCAGGAACTGGGAAATTGTGAAAATTCAATGGTCACAAGATCCCACAATGCTATACAACAAAATGGAATTTGAACCATTTCAGTCTCTTGTCCCTAAGGCAACCAGAAGCCGGTACAGTGGATTCGTAAGGACACTGTTCCAGCAAATGCGGGATGTGCTTGGGACATTTGACACTGTCCAAATAATAAAACTTCTCCCCTTTGCTGCTGCTCCACCAGAACAGAGTAGGATGCAATTTTCCTCATTGACTGTGAATGTGAGAGGATCAGGGTTGAGGATACTGATAAGAGGCAATTCTCCAGTATTCAATTACAACAAGGCAACCAAACGACTTACAGTTCTTGGAAAGGATGCAGGTGCATTGACTGAAGATCCAGATGAAGGCACATCTGGGGTGGAGTCTGCTGTCCTGAGAGGATTTCTCATTTTGGGCAAAGAAGACAAGAGATATGGCCCAGCATTAAGCATCAATGAACTGAGCAATCTTGCAAAAGGAGAGAAGGCTAATGTGCTAATTGGGCAAGGGGACGTAGTGTTGGTAATGAAACGAAAACGGGACTCTAGCATACTTACTGACAGCCAGACAGCGACCAAAAGAATTCGGATGGCCATCAATTAG";

    String H1N5 = "AGCGAAAGCAGGTCAAATATATTCAATATGGAGAGAATAAAAGAACTAAGAGATCTAATGTCACAGTCCCGCACCCGCGAGATACTCACCAAAACCACTGTGGACCACATGGCCATAATCAAAAAATACACATCAGGAAGGCAAGAGAAGAACCCCGCACTCAGGATGAAGTGGATGATGGCAATGAAATATCCAATTACGGCAGATAAGAGAATAATGGAAATGATTCCTGAAAGGAATGAACAAGGACAAACCCTCTGGAGCAAAACAAACGATGCCGGCTCAGACCGAGTGATGGTATCACCTCTGGCCGTAACATGGTGGAATAGGAATGGACCAACAACAAGTACAGTCCACTACCCAAAGGTATATAAAACTTACTTCGAAAAAGTCGAAAGGTTGAAACACGGGACCTTTGGCCCTGTCCACTTCAGAAATCAAGTTAAGATAAGACGGAGGGTTGACATAAACCCTGGCCACGCAGACCTCAGTGCCAAAGAGGCACAGGATGTAATCATGGAAGTTGTTTTCCCAAATGAAGTGGGAGCTAGAATACTAACATCGGAGTCACAACTGACAATAACAAAAGAGAAGAAGGAAGAACTCCAGGACTGTAAAATTGCCCCCTTGATGGTAGCATACATGCTAGAAAGAGAGTTGGTCCGCAAAACGAGGTTCCTCCCAGTGGCTGGTGGAACAAGCAGTGTCTATATTGAGGTGTTGCATTTAACCCAGGGGACATGCTGGGAGCAGATGTACACTCCAGGAGGGGAAGTGAGAAATGATGATGTTGACCAAAGCTTGATTATCGCTGCCAGGAACATAGTAAGAAGAGCAACGGTATCAGCAGACCCACTAGCATCTCTATTGGAGATGTGCCACAGCACACAGATTGGGGGAATAAGGATGGTAGACATCCTTCGGCAAAATCCAACAGAGGAACAAGCCGTGGACATATGCAAGGCAGCAATGGGATTGAGGATTAGCTCATCTTTCAGCTTTGGTGGATTCACTTTCAAAAGAACAAGCGGGTCGTCAGTTAAGAGAGAAGAAGAAGTGCTTACGGGCAACCTTCAAACATTGAAAATAAGAGTACATGAGGGGTATGAAGAGTTCACAATGGTTGGGAGAAGAGCAACAGCTATTCTCAGAAAAGCAACCAGGAGATTGATCCAGCTAATAGTAAGTGGGAGAGACGAGCAGTCAATTGCTGAAGCAATAATTGTGGCCATGGTATTTTCACAAGAGGATTGCATGATCAAGGCAGTTCGGGGTGATCTGAACTTTGTCAATAGGGCAAACCAGCGACTGAACCCCATGCATCAACTCTTGAGACACTTCCAAAAGGATGCAAAAGTGCTTTTCCAAAACTGGGGAATTGAACCCATTGACAATGTGATGGGAATGATCGGAATATTGCCCGACATGACCCCAAGTACTGAAATGTCGCTGAGGGGAATAAGAGTCAGCAAAATGGGAGTAGATGAATACTCCAGCACAGAGAGGGTGGTGGTGAGCATTGACCGATTTTTAAGGGTTCGGGATCAAAGGGGAAACGTACTATTGTCACCCGAAGAAGTCAGCGAGACACAAGGAACGGAGAAGCTGACGATAACTTATTCGTCATCAATGATGTGGGAGATCAATGGTCCTGAGTCGGTGTTGGTCAATACTTATCAGTGGATCATAAGGAACTGGGAGACTGTGAAAATTCAATGGTCACAGGATCCCACAATGTTATATAATAAGATGGAATTCGAGCCATTTCAGTCTCTGGTCCCTAAGGCAGCCAGAGGTCAATACAGCGGATTCGTGAGGACACTGTTCCAGCAGATGCGGGATGTGCTTGGAACATTTGACACTGTTCAGATAATAAAACTTCTCCCCTTTGCTGCTGCTCCACCAGAACAGAGTAGGATGCAGTTCTCCTCCCTGACTGTGAATGTAAGAGGATCAGGAATGAGGATACTGGTAAGAGGCAATTCTCCAGTGTTCAATTACAACAAGGCCACCAAGAGGCTTACAGTCCTCGGGAAAGATGCAGGTGCATTGACCGAAGATCCAGATGAAGGTACAGCTGGAGTGGAGTCTGCTGTTCTAAGAGGATTCCTCATTTTGGGCAAAGAAGACAAGAGATATGGCCCAGCATTAAGCATCAATGAGCTGAGCAATCTTGCAAAAGGAGAGAAGGCTAATGTGCTAATTGGGCAAGGAGACGTGGTGTTGGTAATGAAACGGAAACGGGACTCTAGCATACTTACTGACAGCCAGACAGCGACCAAAAGAATTCGGATGGCCATCAATTAGTGTCGAATTGTTTAAAAACGACCTTGTTTCTACT";

    public EditDistanceTest() {
    }

    /**
     * Test of distance method, of class EditDistance.
     */
    @Test
    public void testDistance() {
        System.out.println("distance");
        String x = "Levenshtein";
        String y = "Laeveshxtin";
        String z = "Laeveshetin";
        for (int i = 0; i < 100000; i++)
            EditDistance.levenshtein(x, y);

        // EditDistance.levenshtein("abcdefghi", "jklmnopqrst");
        Assert.assertEquals(0, EditDistance.levenshtein(x, x));
        Assert.assertEquals(4, EditDistance.levenshtein(x, y));
        Assert.assertEquals(4, EditDistance.levenshtein(x, z));
        Assert.assertEquals(5, EditDistance.levenshtein("adcroft", "addessi"));
        Assert.assertEquals(3, EditDistance.levenshtein("baird", "baisden"));
        Assert.assertEquals(2, EditDistance.levenshtein("boggan", "boggs"));
        Assert.assertEquals(5, EditDistance.levenshtein("clayton", "cleary"));
        Assert.assertEquals(4, EditDistance.levenshtein("dybas", "dyckman"));
        Assert.assertEquals(4, EditDistance.levenshtein("emineth", "emmert"));
        Assert.assertEquals(4, EditDistance.levenshtein("galante", "galicki"));
        Assert.assertEquals(1, EditDistance.levenshtein("hardin", "harding"));
        Assert.assertEquals(2, EditDistance.levenshtein("kehoe", "kehr"));
        Assert.assertEquals(5, EditDistance.levenshtein("lowry", "lubarsky"));
        Assert.assertEquals(3, EditDistance.levenshtein("magallan", "magana"));
        Assert.assertEquals(1, EditDistance.levenshtein("mayo", "mays"));
        Assert.assertEquals(4, EditDistance.levenshtein("moeny", "moffett"));
        Assert.assertEquals(2, EditDistance.levenshtein("pare", "parent"));
        Assert.assertEquals(2, EditDistance.levenshtein("ramey", "ramfrey"));
        Assert.assertEquals(0, EditDistance.damerau(x, x));
        Assert.assertEquals(4, EditDistance.damerau(x, y));
        Assert.assertEquals(3, EditDistance.damerau(x, z));
        Assert.assertEquals(5, EditDistance.damerau("adcroft", "addessi"));
        Assert.assertEquals(3, EditDistance.damerau("baird", "baisden"));
        Assert.assertEquals(2, EditDistance.damerau("boggan", "boggs"));
        Assert.assertEquals(5, EditDistance.damerau("clayton", "cleary"));
        Assert.assertEquals(4, EditDistance.damerau("dybas", "dyckman"));
        Assert.assertEquals(4, EditDistance.damerau("emineth", "emmert"));
        Assert.assertEquals(4, EditDistance.damerau("galante", "galicki"));
        Assert.assertEquals(1, EditDistance.damerau("hardin", "harding"));
        Assert.assertEquals(2, EditDistance.damerau("kehoe", "kehr"));
        Assert.assertEquals(5, EditDistance.damerau("lowry", "lubarsky"));
        Assert.assertEquals(3, EditDistance.damerau("magallan", "magana"));
        Assert.assertEquals(1, EditDistance.damerau("mayo", "mays"));
        Assert.assertEquals(4, EditDistance.damerau("moeny", "moffett"));
        Assert.assertEquals(2, EditDistance.damerau("pare", "parent"));
        Assert.assertEquals(2, EditDistance.damerau("ramey", "ramfrey"));
    }

    /**
     * Test of distance method, of class EditDistance.
     */
    @Test
    public void testDistance2() {
        System.out.println("distance");
        String x = "Levenshtein";
        String y = "Laeveshxtin";
        String z = "Laeveshetin";
        EditDistance edit = new EditDistance(20, false);
        for (int i = 0; i < 100000; i++)
            edit.d(x, y);

        // edit.d("abcdefghi", "jklmnopqrst");
        Assert.assertEquals(0, edit.d(x, x), 1.0E-7);
        Assert.assertEquals(4, edit.d(x, y), 1.0E-7);
        Assert.assertEquals(4, edit.d(x, z), 1.0E-7);
        Assert.assertEquals(5, edit.d("adcroft", "addessi"), 1.0E-7);
        Assert.assertEquals(3, edit.d("baird", "baisden"), 1.0E-7);
        Assert.assertEquals(2, edit.d("boggan", "boggs"), 1.0E-7);
        Assert.assertEquals(5, edit.d("clayton", "cleary"), 1.0E-7);
        Assert.assertEquals(4, edit.d("dybas", "dyckman"), 1.0E-7);
        Assert.assertEquals(4, edit.d("emineth", "emmert"), 1.0E-7);
        Assert.assertEquals(4, edit.d("galante", "galicki"), 1.0E-7);
        Assert.assertEquals(1, edit.d("hardin", "harding"), 1.0E-7);
        Assert.assertEquals(2, edit.d("kehoe", "kehr"), 1.0E-7);
        Assert.assertEquals(5, edit.d("lowry", "lubarsky"), 1.0E-7);
        Assert.assertEquals(3, edit.d("magallan", "magana"), 1.0E-7);
        Assert.assertEquals(1, edit.d("mayo", "mays"), 1.0E-7);
        Assert.assertEquals(4, edit.d("moeny", "moffett"), 1.0E-7);
        Assert.assertEquals(2, edit.d("pare", "parent"), 1.0E-7);
        Assert.assertEquals(2, edit.d("ramey", "ramfrey"), 1.0E-7);
        edit = new EditDistance(20, true);
        Assert.assertEquals(0, edit.d(x, x), 1.0E-7);
        Assert.assertEquals(4, edit.d(x, y), 1.0E-7);
        Assert.assertEquals(3, edit.d(x, z), 1.0E-7);
        Assert.assertEquals(5, edit.d("adcroft", "addessi"), 1.0E-7);
        Assert.assertEquals(3, edit.d("baird", "baisden"), 1.0E-7);
        Assert.assertEquals(2, edit.d("boggan", "boggs"), 1.0E-7);
        Assert.assertEquals(6, edit.d("lcayton", "cleary"), 1.0E-7);
        Assert.assertEquals(5, edit.d("ydbas", "dyckman"), 1.0E-7);
        Assert.assertEquals(4, edit.d("emineth", "emmert"), 1.0E-7);
        Assert.assertEquals(4, edit.d("galante", "galicki"), 1.0E-7);
        Assert.assertEquals(1, edit.d("hardin", "harding"), 1.0E-7);
        Assert.assertEquals(2, edit.d("kehoe", "kehr"), 1.0E-7);
        Assert.assertEquals(5, edit.d("lowry", "lubarsky"), 1.0E-7);
        Assert.assertEquals(3, edit.d("magallan", "magana"), 1.0E-7);
        Assert.assertEquals(1, edit.d("mayo", "mays"), 1.0E-7);
        Assert.assertEquals(4, edit.d("moeny", "moffett"), 1.0E-7);
        Assert.assertEquals(2, edit.d("pare", "parent"), 1.0E-7);
        Assert.assertEquals(2, edit.d("ramey", "ramfrey"), 1.0E-7);
    }

    /**
     * Test of distance method, of class EditDistance.
     */
    @Test
    public void testDistance3() {
        System.out.println("distance");
        System.out.println(EditDistance.levenshtein(H1N1, H1N5));
    }

    EditDistance ed = new EditDistance(Math.max(H1N1.length(), H1N5.length()));

    /**
     * Test of distance method, of class EditDistance.
     */
    @Test
    public void testDistance4() {
        System.out.println("distance");
        System.out.println(ed.d(H1N1, H1N5));
    }

    /**
     * Test of distance method, of class EditDistance.
     */
    @Test
    public void testDistance5() {
        System.out.println("distance");
        System.out.println(EditDistance.damerau(H1N1, H1N5));
    }

    EditDistance ed2 = new EditDistance(Math.max(H1N1.length(), H1N5.length()), true);

    /**
     * Test of distance method, of class EditDistance.
     */
    @Test
    public void testDistance6() {
        System.out.println("distance");
        System.out.println(ed2.d(H1N1, H1N5));
    }
}

