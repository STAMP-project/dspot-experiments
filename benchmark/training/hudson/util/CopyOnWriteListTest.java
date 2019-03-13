/**
 * The MIT License
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., Kohsuke Kawaguchi
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
package hudson.util;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Kohsuke Kawaguchi, Alan Harder
 */
public class CopyOnWriteListTest {
    public static final class TestData {
        CopyOnWriteList list1 = new CopyOnWriteList();

        List list2 = new ArrayList();
    }

    /**
     * Verify that the serialization form of List and CopyOnWriteList are the same.
     */
    @Test
    public void serialization() {
        XStream2 xs = new XStream2();
        CopyOnWriteListTest.TestData td = new CopyOnWriteListTest.TestData();
        String out = xs.toXML(td);
        Assert.assertEquals("empty lists", ("<hudson.util.CopyOnWriteListTest_-TestData>" + "<list1/><list2/></hudson.util.CopyOnWriteListTest_-TestData>"), out.replaceAll("\\s+", ""));
        CopyOnWriteListTest.TestData td2 = ((CopyOnWriteListTest.TestData) (xs.fromXML(out.toString())));
        Assert.assertTrue(td2.list1.isEmpty());
        Assert.assertTrue(td2.list2.isEmpty());
        td.list1.add("foobar1");
        td.list2.add("foobar2");
        out = xs.toXML(td);
        Assert.assertEquals("lists", ("<hudson.util.CopyOnWriteListTest_-TestData>" + ("<list1><string>foobar1</string></list1><list2><string>foobar2" + "</string></list2></hudson.util.CopyOnWriteListTest_-TestData>")), out.replaceAll("\\s+", ""));
        td2 = ((CopyOnWriteListTest.TestData) (xs.fromXML(out.toString())));
        Assert.assertEquals("foobar1", td2.list1.getView().get(0));
        Assert.assertEquals("foobar2", td2.list2.get(0));
    }
}

