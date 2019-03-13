/**
 * Copyright ? 2013-2015 Esko Luontola <www.orfjackal.net>
 */
/**
 * This software is released under the Apache License 2.0.
 */
/**
 * The license text is at http://www.apache.org/licenses/LICENSE-2.0
 */
package net.orfjackal.retrolambda.test;


import java.io.IOException;
import org.apache.commons.lang.SystemUtils;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class Java5BytecodeTest {
    @Test
    public void does_not_generate_stack_map_tables_for_Java_5() throws IOException {
        String javapOutput = Java5BytecodeTest.javap(Java5BytecodeTest.Dummy.class);
        if (SystemUtils.isJavaVersionAtLeast(1.6F)) {
            MatcherAssert.assertThat(javapOutput, containsString("StackMap"));
        } else {
            MatcherAssert.assertThat(javapOutput, not(containsString("StackMap")));
        }
    }

    public static class Dummy {
        public Dummy() {
            // cause this method to have a stack map table
            for (int i = 0; i < 3; i++) {
                System.out.println(i);
            }
        }
    }
}

