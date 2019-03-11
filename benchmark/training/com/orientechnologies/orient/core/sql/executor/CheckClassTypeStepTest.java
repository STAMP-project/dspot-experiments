package com.orientechnologies.orient.core.sql.executor;


import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by olena.kolesnyk on 30/07/2017.
 */
public class CheckClassTypeStepTest extends TestUtilsFixture {
    @Test
    public void shouldCheckSubclasses() {
        OBasicCommandContext context = new OBasicCommandContext();
        context.setDatabase(TestUtilsFixture.database);
        OClass parentClass = TestUtilsFixture.createClassInstance();
        OClass childClass = TestUtilsFixture.createChildClassInstance(parentClass);
        CheckClassTypeStep step = new CheckClassTypeStep(childClass.getName(), parentClass.getName(), context, false);
        OResultSet result = step.syncPull(context, 20);
        Assert.assertEquals(0, result.stream().count());
    }

    @Test
    public void shouldCheckOneType() {
        OBasicCommandContext context = new OBasicCommandContext();
        context.setDatabase(TestUtilsFixture.database);
        String className = TestUtilsFixture.createClassInstance().getName();
        CheckClassTypeStep step = new CheckClassTypeStep(className, className, context, false);
        OResultSet result = step.syncPull(context, 20);
        Assert.assertEquals(0, result.stream().count());
    }

    @Test(expected = OCommandExecutionException.class)
    public void shouldThrowExceptionWhenClassIsNotParent() {
        OBasicCommandContext context = new OBasicCommandContext();
        context.setDatabase(TestUtilsFixture.database);
        CheckClassTypeStep step = new CheckClassTypeStep(TestUtilsFixture.createClassInstance().getName(), TestUtilsFixture.createClassInstance().getName(), context, false);
        step.syncPull(context, 20);
    }
}

