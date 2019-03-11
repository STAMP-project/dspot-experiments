package org.jabref.logic.formatter.bibtexfields;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests in addition to the general tests from {@link org.jabref.logic.formatter.FormatterTest}
 */
public class NormalizeNamesFormatterTest {
    private NormalizeNamesFormatter formatter;

    @Test
    public void testNormalizeAuthorList() {
        Assertions.assertEquals("Bilbo, Staci D.", formatter.format("Staci D Bilbo"));
        Assertions.assertEquals("Bilbo, Staci D.", formatter.format("Staci D. Bilbo"));
        Assertions.assertEquals("Bilbo, Staci D. and Smith, S. H. and Schwarz, Jaclyn M.", formatter.format("Staci D Bilbo and Smith SH and Jaclyn M Schwarz"));
        Assertions.assertEquals("?lver, M. A.", formatter.format("?lver MA"));
        Assertions.assertEquals("?lver, M. A. and ?ie, G. G. and ?ie, G. G. and Alfredsen, J. ?. ?. and Alfredsen, Jo and Olsen, Y. Y. and Olsen, Y. Y.", formatter.format("?lver MA; GG ?ie; ?ie GG; Alfredsen J??; Jo Alfredsen; Olsen Y.Y. and Olsen YY."));
        Assertions.assertEquals("?lver, M. A. and ?ie, G. G. and ?ie, G. G. and Alfredsen, J. ?. ?. and Alfredsen, Jo and Olsen, Y. Y. and Olsen, Y. Y.", formatter.format("?lver MA; GG ?ie; ?ie GG; Alfredsen J??; Jo Alfredsen; Olsen Y.Y.; Olsen YY."));
        Assertions.assertEquals("Alver, Morten and Alver, Morten O. and Alfredsen, J. A. and Olsen, Y. Y.", formatter.format("Alver, Morten and Alver, Morten O and Alfredsen, JA and Olsen, Y.Y."));
        Assertions.assertEquals("Alver, M. A. and Alfredsen, J. A. and Olsen, Y. Y.", formatter.format("Alver, MA; Alfredsen, JA; Olsen Y.Y."));
        Assertions.assertEquals("Kolb, Stefan and Lenhard, J{\\\"o}rg and Wirtz, Guido", formatter.format("Kolb, Stefan and J{\\\"o}rg Lenhard and Wirtz, Guido"));
    }

    @Test
    public void twoAuthorsSeperatedByColon() {
        Assertions.assertEquals("Bilbo, Staci and Alver, Morten", formatter.format("Staci Bilbo; Morten Alver"));
    }

    @Test
    public void threeAuthorsSeperatedByColon() {
        Assertions.assertEquals("Bilbo, Staci and Alver, Morten and Name, Test", formatter.format("Staci Bilbo; Morten Alver; Test Name"));
    }

    // Test for https://github.com/JabRef/jabref/issues/318
    @Test
    public void threeAuthorsSeperatedByAnd() {
        Assertions.assertEquals("Kolb, Stefan and Lenhard, J{\\\"o}rg and Wirtz, Guido", formatter.format("Stefan Kolb and J{\\\"o}rg Lenhard and Guido Wirtz"));
    }

    // Test for https://github.com/JabRef/jabref/issues/318
    @Test
    public void threeAuthorsSeperatedByAndWithDash() {
        Assertions.assertEquals("Jian, Heng-Yu and Xu, Z. and Chang, M.-C. F.", formatter.format("Heng-Yu Jian and Xu, Z. and Chang, M.-C.F."));
    }

    // Test for https://github.com/JabRef/jabref/issues/318
    @Test
    public void threeAuthorsSeperatedByAndWithLatex() {
        Assertions.assertEquals("Gustafsson, Oscar and DeBrunner, Linda S. and DeBrunner, Victor and Johansson, H{\\aa}kan", formatter.format("Oscar Gustafsson and Linda S. DeBrunner and Victor DeBrunner and H{\\aa}kan Johansson"));
    }

    @Test
    public void lastThenInitial() {
        Assertions.assertEquals("Smith, S.", formatter.format("Smith S"));
    }

    @Test
    public void lastThenInitials() {
        Assertions.assertEquals("Smith, S. H.", formatter.format("Smith SH"));
    }

    @Test
    public void initialThenLast() {
        Assertions.assertEquals("Smith, S.", formatter.format("S Smith"));
    }

    @Test
    public void initialDotThenLast() {
        Assertions.assertEquals("Smith, S.", formatter.format("S. Smith"));
    }

    @Test
    public void initialsThenLast() {
        Assertions.assertEquals("Smith, S. H.", formatter.format("SH Smith"));
    }

    @Test
    public void lastThenJuniorThenFirst() {
        Assertions.assertEquals("Name, della, first", formatter.format("Name, della, first"));
    }

    @Test
    public void testConcatenationOfAuthorsWithCommas() {
        Assertions.assertEquals("Ali Babar, M. and Dings?yr, T. and Lago, P. and van der Vliet, H.", formatter.format("Ali Babar, M., Dings?yr, T., Lago, P., van der Vliet, H."));
        Assertions.assertEquals("Ali Babar, M.", formatter.format("Ali Babar, M."));
    }

    @Test
    public void testOddCountOfCommas() {
        Assertions.assertEquals("Ali Babar, M., Dings?yr T. Lago P.", formatter.format("Ali Babar, M., Dings?yr, T., Lago P."));
    }

    @Test
    public void formatExample() {
        Assertions.assertEquals("Einstein, Albert and Turing, Alan", formatter.format(formatter.getExampleInput()));
    }

    @Test
    public void testNameAffixe() {
        Assertions.assertEquals("Surname, jr, First and Surname2, First2", formatter.format("Surname, jr, First, Surname2, First2"));
    }

    @Test
    public void testAvoidSpecialCharacter() {
        Assertions.assertEquals("Surname, {, First; Surname2, First2", formatter.format("Surname, {, First; Surname2, First2"));
    }

    @Test
    public void testAndInName() {
        Assertions.assertEquals("Surname and , First, Surname2 First2", formatter.format("Surname, and , First, Surname2, First2"));
    }

    @Test
    public void testMultipleNameAffixes() {
        Assertions.assertEquals("Mair, Jr, Daniel and Br?hl, Sr, Daniel", formatter.format("Mair, Jr, Daniel, Br?hl, Sr, Daniel"));
    }

    @Test
    public void testCommaSeperatedNames() {
        Assertions.assertEquals("Bosoi, Cristina and Oliveira, Mariana and Sanchez, Rafael Ochoa and Tremblay, M?lanie and TenHave, Gabrie and Deutz, Nicoolas and Rose, Christopher F. and Bemeur, Chantal", formatter.format("Cristina Bosoi, Mariana Oliveira, Rafael Ochoa Sanchez, M?lanie Tremblay, Gabrie TenHave, Nicoolas Deutz, Christopher F. Rose, Chantal Bemeur"));
    }

    @Test
    public void testMultipleSpaces() {
        Assertions.assertEquals("Bosoi, Cristina and Oliveira, Mariana and Sanchez, Rafael Ochoa and Tremblay, M?lanie and TenHave, Gabrie and Deutz, Nicoolas and Rose, Christopher F. and Bemeur, Chantal", formatter.format("Cristina    Bosoi,    Mariana Oliveira, Rafael Ochoa Sanchez   ,   M?lanie Tremblay  , Gabrie TenHave, Nicoolas Deutz, Christopher F. Rose, Chantal Bemeur"));
    }

    @Test
    public void testAvoidPreposition() {
        Assertions.assertEquals("von Zimmer, Hans and van Oberbergern, Michael and zu Berger, Kevin", formatter.format("Hans von Zimmer, Michael van Oberbergern, Kevin zu Berger"));
    }

    @Test
    public void testPreposition() {
        Assertions.assertEquals("von Zimmer, Hans and van Oberbergern, Michael and zu Berger, Kevin", formatter.format("Hans von Zimmer, Michael van Oberbergern, Kevin zu Berger"));
    }

    @Test
    public void testOneCommaUntouched() {
        Assertions.assertEquals("Canon der Barbar, Alexander der Gro?e", formatter.format("Canon der Barbar, Alexander der Gro?e"));
    }

    @Test
    public void testAvoidNameAffixes() {
        Assertions.assertEquals("der Barbar, Canon and der Gro?e, Alexander and der Alexander, Peter", formatter.format("Canon der Barbar, Alexander der Gro?e, Peter der Alexander"));
    }

    @Test
    public void testUpperCaseSensitiveList() {
        Assertions.assertEquals("der Barbar, Canon and der Gro?e, Alexander", formatter.format("Canon der Barbar AND Alexander der Gro?e"));
        Assertions.assertEquals("der Barbar, Canon and der Gro?e, Alexander", formatter.format("Canon der Barbar aNd Alexander der Gro?e"));
        Assertions.assertEquals("der Barbar, Canon and der Gro?e, Alexander", formatter.format("Canon der Barbar AnD Alexander der Gro?e"));
    }

    @Test
    public void testSemiCorrectNamesWithSemicolon() {
        Assertions.assertEquals("Last, First and Last2, First2 and Last3, First3", formatter.format("Last, First; Last2, First2; Last3, First3"));
        Assertions.assertEquals("Last, Jr, First and Last2, First2", formatter.format("Last, Jr, First; Last2, First2"));
        Assertions.assertEquals("Last, First and Last2, First2 and Last3, First3 and Last4, First4", formatter.format("Last, First; Last2, First2; Last3, First3; First4 Last4"));
        Assertions.assertEquals("Last and Last2, First2 and Last3, First3 and Last4, First4", formatter.format("Last; Last2, First2; Last3, First3; Last4, First4"));
    }
}

