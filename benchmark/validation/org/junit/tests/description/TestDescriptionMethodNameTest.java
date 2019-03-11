package org.junit.tests.description;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author Dmitry Baev charlie@yandex-team.ru
Date: 03.05.14
 */
@RunWith(Parameterized.class)
public class TestDescriptionMethodNameTest {
    private String methodName;

    public TestDescriptionMethodNameTest(String methodName) {
        this.methodName = methodName;
    }

    @Test
    public void methodNameTest() throws Exception {
        Description description = Description.createTestDescription("some-class-name", methodName);
        Assert.assertNotNull("Method name should be not null", description.getMethodName());
        Assert.assertEquals(methodName, description.getMethodName());
    }
}

