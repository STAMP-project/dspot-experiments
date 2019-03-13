package org.jabref.logic.integrity;


import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class PersonNamesCheckerTest {
    private PersonNamesChecker checker;

    @Test
    public void validNameFirstnameAuthor() throws Exception {
        Assertions.assertEquals(Optional.empty(), checker.checkValue("Kolb, Stefan"));
    }

    @Test
    public void validNameFirstnameAuthors() throws Exception {
        Assertions.assertEquals(Optional.empty(), checker.checkValue("Kolb, Stefan and Harrer, Simon"));
    }

    @Test
    public void validFirstnameNameAuthor() throws Exception {
        Assertions.assertEquals(Optional.empty(), checker.checkValue("Stefan Kolb"));
    }

    @Test
    public void validFirstnameNameAuthors() throws Exception {
        Assertions.assertEquals(Optional.empty(), checker.checkValue("Stefan Kolb and Simon Harrer"));
    }

    @Test
    public void complainAboutPersonStringWithTwoManyCommas() throws Exception {
        Assertions.assertEquals(Optional.of("Names are not in the standard BibTeX format."), checker.checkValue("Test1, Test2, Test3, Test4, Test5, Test6"));
    }

    @Test
    public void doNotComplainAboutSecondNameInFront() throws Exception {
        Assertions.assertEquals(Optional.empty(), checker.checkValue("M. J. Gotay"));
    }

    @Test
    public void validCorporateNameInBrackets() throws Exception {
        Assertions.assertEquals(Optional.empty(), checker.checkValue("{JabRef}"));
    }

    @Test
    public void validCorporateNameAndPerson() throws Exception {
        Assertions.assertEquals(Optional.empty(), checker.checkValue("{JabRef} and Stefan Kolb"));
        Assertions.assertEquals(Optional.empty(), checker.checkValue("{JabRef} and Kolb, Stefan"));
    }
}

