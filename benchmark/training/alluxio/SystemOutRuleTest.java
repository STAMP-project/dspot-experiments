/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.model.Statement;


/**
 * Unit tests for {@link SystemOutRule}.
 */
public class SystemOutRuleTest {
    private static final ByteArrayOutputStream OUTPUT = new ByteArrayOutputStream();

    private static final PrintStream ORIGINAL_SYSTEM_OUT = System.out;

    private Statement mStatement = new Statement() {
        @Override
        public void evaluate() throws Throwable {
            System.out.println("2048");
            Assert.assertEquals("2048\n", SystemOutRuleTest.OUTPUT.toString());
            SystemOutRuleTest.OUTPUT.reset();
            System.out.println("1234");
            Assert.assertEquals("1234\n", SystemOutRuleTest.OUTPUT.toString());
        }
    };

    @Test
    public void testSystemOutRule() throws Throwable {
        new SystemOutRule(SystemOutRuleTest.OUTPUT).apply(mStatement, null).evaluate();
        Assert.assertEquals(System.out, SystemOutRuleTest.ORIGINAL_SYSTEM_OUT);
    }
}

