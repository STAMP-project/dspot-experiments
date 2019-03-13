/**
 * The MIT License
 *
 * Copyright (c) 2015 Kanstantsin Shautsou
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.triggers;


import antlr.ANTLRException;
import hudson.scheduler.CronTabList;
import hudson.scheduler.Hash;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;


/**
 *
 *
 * @author Kanstantsin Shautsou
 */
public class TimerTriggerTest {
    @Issue("JENKINS-29790")
    @Test
    public void testNoNPE() throws ANTLRException {
        new TimerTrigger("").run();
    }

    @Issue("JENKINS-43328")
    @Test
    public void testTimeZoneOffset() throws Exception {
        TimeZone defaultTz = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"));
        try {
            String cron = "TZ=GMT\nH 0 * * *";
            CronTabList ctl = CronTabList.create(cron, Hash.from("whatever"));
            Assert.assertEquals("previous occurrence is in GMT", "GMT", ctl.previous().getTimeZone().getID());
            cron = "TZ=America/Denver\nH 0 * * *";
            ctl = CronTabList.create(cron, Hash.from("whatever"));
            Assert.assertEquals("next occurrence is in America/Denver", "America/Denver", ctl.next().getTimeZone().getID());
        } finally {
            TimeZone.setDefault(defaultTz);
        }
    }
}

