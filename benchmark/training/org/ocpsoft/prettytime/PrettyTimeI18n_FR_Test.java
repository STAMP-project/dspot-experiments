/**
 * Copyright 2012 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.prettytime;


import java.util.Date;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


/**
 * All the tests for PrettyTime.
 *
 * @author Thomas Weitzel <tweitzel@synformation.com>
 */
public class PrettyTimeI18n_FR_Test {
    /* A note when you want to use the YourKit profiler: To use the YourKit
    profiler (http://yourkit.com), run with VM argument for profiling:
    -agentlib:yjpagent=onexit=snapshot,tracing
     */
    // Stores current locale so that it can be restored
    private Locale locale;

    @Test
    public void testPrettyTimeFRENCH() {
        // The FRENCH resource bundle should be used
        PrettyTime p = new PrettyTime(Locale.FRENCH);
        Assert.assertEquals("? l'instant", p.format(new Date()));
    }

    @Test
    public void testPrettyTimeFRENCHCenturies() {
        PrettyTime p = new PrettyTime(new Date((3155692597470L * 3L)), Locale.FRENCH);
        Assert.assertEquals(p.format(new Date(0)), "il y a 3 si?cles");
    }

    @Test
    public void testPrettyTimeViaDefaultLocaleFRENCH() {
        // The FRENCH resource bundle should be used
        Locale.setDefault(Locale.FRENCH);
        PrettyTime p = new PrettyTime();
        Assert.assertEquals(p.format(new Date()), "? l'instant");
    }

    @Test
    public void testPrettyTimeFRENCHLocale() {
        long t = 1L;
        PrettyTime p = new PrettyTime(new Date(0), Locale.FRENCH);
        while ((((((1000L * 60L) * 60L) * 24L) * 365L) * 1000000L) > t) {
            Assert.assertTrue(((p.format(new Date(0)).startsWith("dans")) || (p.format(new Date(0)).startsWith("? l'instant"))));
            t *= 2L;
        } 
    }
}

