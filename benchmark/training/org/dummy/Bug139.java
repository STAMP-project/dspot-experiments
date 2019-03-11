/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.dummy;


import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.apache.log4j.Category;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.log4j.Level.DEBUG;
import static org.apache.log4j.Logger.getLogger;


public class Bug139 {
    @Test
    public void test() {
        ListHandler listHandler = new ListHandler();
        Logger root = Logger.getLogger("");
        root.addHandler(listHandler);
        root.setLevel(Level.FINEST);
        org.apache.log4j.Logger log4jLogger = getLogger("a");
        Category log4jCategory = getLogger("b");
        int n = 0;
        log4jLogger.log(DEBUG, ("hello" + (++n)));
        log4jCategory.log(DEBUG, ("world" + (++n)));
        Assert.assertEquals(n, listHandler.list.size());
        for (int i = 0; i < n; i++) {
            LogRecord logRecord = ((LogRecord) (listHandler.list.get(i)));
            Assert.assertEquals("test", logRecord.getSourceMethodName());
        }
    }
}

