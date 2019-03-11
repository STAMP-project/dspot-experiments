package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.common.concur.OTimeoutException;
import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class CheckSafeDeleteStepTest extends TestUtilsFixture {
    private static final String VERTEX_CLASS_NAME = "VertexTestClass";

    private static final String EDGE_CLASS_NAME = "EdgeTestClass";

    private String className;

    public CheckSafeDeleteStepTest(String className) {
        this.className = className;
    }

    @Test(expected = OCommandExecutionException.class)
    public void shouldNotDeleteVertexAndEdge() {
        OCommandContext context = new OBasicCommandContext();
        switch (className) {
            case CheckSafeDeleteStepTest.VERTEX_CLASS_NAME :
                TestUtilsFixture.database.createVertexClass(CheckSafeDeleteStepTest.VERTEX_CLASS_NAME);
                break;
            case CheckSafeDeleteStepTest.EDGE_CLASS_NAME :
                TestUtilsFixture.database.createEdgeClass(CheckSafeDeleteStepTest.EDGE_CLASS_NAME);
                break;
        }
        CheckSafeDeleteStep step = new CheckSafeDeleteStep(context, false);
        AbstractExecutionStep previous = new AbstractExecutionStep(context, false) {
            boolean done = false;

            @Override
            public OResultSet syncPull(OCommandContext ctx, int nRecords) throws OTimeoutException {
                OInternalResultSet result = new OInternalResultSet();
                String simpleClassName = TestUtilsFixture.createClassInstance().getName();
                if (!(done)) {
                    for (int i = 0; i < 10; i++) {
                        OResultInternal item = new OResultInternal();
                        item.setElement(new ODocument(((i % 2) == 0 ? simpleClassName : className)));
                        result.add(item);
                    }
                    done = true;
                }
                return result;
            }
        };
        step.setPrevious(previous);
        OResultSet result = step.syncPull(context, 20);
        while (result.hasNext()) {
            result.next();
        } 
    }

    @Test
    public void shouldSafelyDeleteRecord() {
        OCommandContext context = new OBasicCommandContext();
        CheckSafeDeleteStep step = new CheckSafeDeleteStep(context, false);
        AbstractExecutionStep previous = new AbstractExecutionStep(context, false) {
            boolean done = false;

            @Override
            public OResultSet syncPull(OCommandContext ctx, int nRecords) throws OTimeoutException {
                OInternalResultSet result = new OInternalResultSet();
                if (!(done)) {
                    for (int i = 0; i < 10; i++) {
                        OResultInternal item = new OResultInternal();
                        item.setElement(new ODocument(TestUtilsFixture.createClassInstance().getName()));
                        result.add(item);
                    }
                    done = true;
                }
                return result;
            }
        };
        step.setPrevious(previous);
        OResultSet result = step.syncPull(context, 10);
        Assert.assertEquals(10, result.stream().count());
        Assert.assertFalse(result.hasNext());
    }
}

