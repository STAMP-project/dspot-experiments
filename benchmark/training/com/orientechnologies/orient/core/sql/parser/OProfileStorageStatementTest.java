package com.orientechnologies.orient.core.sql.parser;


import org.junit.Assert;
import org.junit.Test;


public class OProfileStorageStatementTest {
    @Test
    public void testParserSimple1() {
        SimpleNode stm = checkRightSyntax("profile storage on");
        Assert.assertTrue((stm instanceof OProfileStorageStatement));
    }

    @Test
    public void testParserSimple2() {
        SimpleNode stm = checkRightSyntax("profile storage off");
        Assert.assertTrue((stm instanceof OProfileStorageStatement));
    }

    @Test
    public void testParserSimpleUpper1() {
        SimpleNode stm = checkRightSyntax("PROFILE STORAGE ON");
        Assert.assertTrue((stm instanceof OProfileStorageStatement));
    }

    @Test
    public void testParserSimpleUpper2() {
        SimpleNode stm = checkRightSyntax("PROFILE STORAGE OFF");
        Assert.assertTrue((stm instanceof OProfileStorageStatement));
    }

    @Test
    public void testWrong() {
        checkWrongSyntax("PROFILE STORAGE");
        checkWrongSyntax("PROFILE x STORAGE OFF");
        checkWrongSyntax("PROFILE STORAGE x OFF");
        checkWrongSyntax("PROFILE STORAGE of");
        checkWrongSyntax("PROFILE STORAGE onn");
        checkWrongSyntax("PROFILE STORAGE off foo bar");
    }
}

