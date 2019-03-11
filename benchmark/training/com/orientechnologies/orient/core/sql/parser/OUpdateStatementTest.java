package com.orientechnologies.orient.core.sql.parser;


import org.junit.Test;


public class OUpdateStatementTest {
    @Test
    public void testSimpleInsert() {
        checkRightSyntax("update  Foo set a = b");
        checkRightSyntax("update  Foo set a = 'b'");
        checkRightSyntax("update  Foo set a = 1");
        checkRightSyntax("update  Foo set a = 1+1");
        checkRightSyntax("update  Foo set a = a.b.toLowerCase()");
        checkRightSyntax("update  Foo set a = b, b=c");
        checkRightSyntax("update  Foo set a = 'b', b=1");
        checkRightSyntax("update  Foo set a = 1, c=k");
        checkRightSyntax("update  Foo set a = 1+1, c=foo, d='bar'");
        checkRightSyntax("update  Foo set a = a.b.toLowerCase(), b=out('pippo')[0]");
        printTree("update  Foo set a = a.b.toLowerCase(), b=out('pippo')[0]");
    }

    @Test
    public void testCollections() {
        checkRightSyntax("update Foo add a = b");
        checkWrongSyntax("update Foo add 'a' = b");
        checkRightSyntax("update Foo add a = 'a'");
        checkWrongSyntax("update Foo put a = b");
        checkRightSyntax("update Foo put a = b, c");
        checkRightSyntax("update Foo put a = 'b', 1.34");
        checkRightSyntax("update Foo put a = 'b', 'c'");
    }

    @Test
    public void testJson() {
        checkRightSyntax("update Foo merge {'a':'b', 'c':{'d':'e'}} where name = 'foo'");
        checkRightSyntax("update Foo content {'a':'b', 'c':{'d':'e', 'f': ['a', 'b', 4]}} where name = 'foo'");
    }

    @Test
    public void testTargetQuery() {
        // issue #4415
        checkRightSyntax("update (select from (traverse References from ( select from Node WHERE Email = 'julia@local'  ) ) WHERE @class = 'Node' and $depth <= 1 and Active = true ) set Points = 0 RETURN BEFORE $current.Points");
    }

    @Test
    public void testTargetMultipleRids() {
        checkRightSyntax("update [#9:0, #9:1] set foo = 'bar'");
    }

    @Test
    public void testDottedTarget() {
        // issue #5397
        checkRightSyntax("update $publishedVersionEdge.row set isPublished = false");
    }

    @Test
    public void testLockRecord() {
        checkRightSyntax("update foo set bar = 1 lock record");
        checkRightSyntax("update foo set bar = 1 lock none");
        checkRightSyntax("update foo set bar = 1 lock shared");
        checkRightSyntax("update foo set bar = 1 lock default");
        checkRightSyntax("update foo set bar = 1 LOCK RECORD");
        checkRightSyntax("update foo set bar = 1 LOCK NONE");
        checkRightSyntax("update foo set bar = 1 LOCK SHARED");
        checkRightSyntax("update foo set bar = 1 LOCK DEFAULT");
        checkWrongSyntax("update foo set bar = 1 LOCK Foo");
    }

    @Test
    public void testReturnCount() {
        checkRightSyntax("update foo set bar = 1 RETURN COUNT");
        checkRightSyntax("update foo set bar = 1 return count");
    }
}

