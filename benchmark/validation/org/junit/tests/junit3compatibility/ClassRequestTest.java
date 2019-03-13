package org.junit.tests.junit3compatibility;


import junit.framework.Test;
import org.junit.Assert;
import org.junit.internal.builders.SuiteMethodBuilder;


public class ClassRequestTest {
    public static class PrivateSuiteMethod {
        static Test suite() {
            return null;
        }
    }

    @org.junit.Test
    public void noSuiteMethodIfMethodPrivate() throws Throwable {
        Assert.assertNull(new SuiteMethodBuilder().runnerForClass(ClassRequestTest.PrivateSuiteMethod.class));
    }
}

