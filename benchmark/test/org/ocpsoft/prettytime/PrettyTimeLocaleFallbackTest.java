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


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class PrettyTimeLocaleFallbackTest {
    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    // Stores current locale so that it can be restored
    private Locale locale;

    @Test
    public void testCeilingInterval() throws Exception {
        Assert.assertEquals(new Locale("Foo", "Bar"), Locale.getDefault());
        Date then = format.parse("5/20/2009");
        Date ref = format.parse("6/17/2009");
        PrettyTime t = new PrettyTime(ref);
        Assert.assertEquals("1 month ago", t.format(then));
    }
}

