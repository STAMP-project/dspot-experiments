package com.orientechnologies.orient.core.sql.parser;


import org.junit.Test;


public class OCreateEdgeStatementTest {
    @Test
    public void testSimpleCreate() {
        checkRightSyntax("create edge Foo from (Select from a) to (Select from b)");
    }

    @Test
    public void testCreateFromRid() {
        checkRightSyntax("create edge Foo from #11:0 to #11:1");
    }

    @Test
    public void testCreateFromRidArray() {
        checkRightSyntax("create edge Foo from [#11:0, #11:3] to [#11:1, #12:0]");
    }

    @Test
    public void testRetry() {
        checkRightSyntax("create edge Foo from [#11:0, #11:3] to [#11:1, #12:0] retry 3 wait 20");
    }

    @Test
    public void testCreateFromRidSet() {
        checkRightSyntax("create edge Foo from #11:0 to #11:1 set foo='bar', bar=2");
    }

    @Test
    public void testCreateFromRidArraySet() {
        checkRightSyntax("create edge Foo from [#11:0, #11:3] to [#11:1, #12:0] set foo='bar', bar=2");
    }

    @Test
    public void testRetrySet() {
        checkRightSyntax("create edge Foo from [#11:0, #11:3] to [#11:1, #12:0] set foo='bar', bar=2 retry 3 wait 20");
    }

    @Test
    public void testBatch() {
        checkRightSyntax("create edge Foo from [#11:0, #11:3] to [#11:1, #12:0] set foo='bar', bar=2 retry 3 wait 20 batch 10");
    }
}

