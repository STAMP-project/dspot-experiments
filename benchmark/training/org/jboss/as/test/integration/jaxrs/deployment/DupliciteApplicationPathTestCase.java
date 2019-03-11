package org.jboss.as.test.integration.jaxrs.deployment;


import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@RunAsClient
public class DupliciteApplicationPathTestCase {
    static int initWarningsCount;

    @Test
    public void testDuplicationTwoAppTwoResourceSameMethodPath() throws Exception {
        int resultWarningsCount = DupliciteApplicationPathTestCase.getWarningCount("WFLYUT0101");
        Assert.assertEquals("Expected warning 'WFLYUT0101' not found.", 1, (resultWarningsCount - (DupliciteApplicationPathTestCase.initWarningsCount)));
    }
}

