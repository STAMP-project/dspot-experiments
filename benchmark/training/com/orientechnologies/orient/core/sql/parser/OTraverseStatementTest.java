package com.orientechnologies.orient.core.sql.parser;


import org.junit.Test;


public class OTraverseStatementTest {
    // issue #4031
    @Test
    public void testDepthFirst() {
        checkRightSyntax("traverse out() from #9:0 while $depth <= 2 strategy DEPTH_FIRST");
        checkRightSyntax("traverse out() from #9:0 while $depth <= 2 strategy depth_first");
    }
}

