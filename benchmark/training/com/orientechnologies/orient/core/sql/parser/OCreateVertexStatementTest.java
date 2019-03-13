package com.orientechnologies.orient.core.sql.parser;


import org.junit.Test;


public class OCreateVertexStatementTest {
    @Test
    public void testSimpleCreate() {
        checkRightSyntax("create vertex");
        checkRightSyntax("create vertex V");
        checkRightSyntax("create vertex x cluster t");
        checkWrongSyntax("create vertex V foo");
        checkRightSyntax("create vertex Foo (a) values (1)");
        checkRightSyntax("create vertex Foo (a) values ('1')");
        checkRightSyntax("create vertex Foo (a) values (\"1\")");
        checkRightSyntax("create vertex Foo (a,b) values (1, 2)");
        checkRightSyntax("create vertex Foo (a,b) values ('1', '2')");
        checkRightSyntax("create vertex (a,b) values (\"1\", \"2\")");
        printTree("create vertex (a,b) values (\"1\", \"2\")");
    }

    @Test
    public void testSimpleCreateSet() {
        checkRightSyntax("create vertex Foo set a = 1");
        checkRightSyntax("create vertex Foo set a = '1'");
        checkRightSyntax("create vertex Foo set a = \"1\"");
        checkRightSyntax("create vertex Foo set a = 1, b = 2");
    }

    @Test
    public void testEmptyArrayCreate() {
        checkRightSyntax("create vertex Foo set a = 'foo'");
        checkRightSyntax("create vertex Foo set a = []");
        // checkRightSyntax("create vertex Foo set a = [ ]");
    }

    @Test
    public void testEmptyMapCreate() {
        checkRightSyntax("create vertex Foo set a = {}");
        checkRightSyntax("create vertex Foo SET a = { }");
    }

    @Test
    public void testInsertIntoCluster() {
        checkRightSyntax("create vertex cluster:default (equaledges, name, list) values ('yes', 'square', ['bottom', 'top','left','right'] )");
    }
}

