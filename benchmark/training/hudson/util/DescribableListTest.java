/**
 * The MIT License
 *
 * Copyright 2018 CloudBees, Inc.
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


import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import hudson.model.Describable;
import hudson.model.Descriptor;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;


public class DescribableListTest {
    @Issue("JENKINS-49054")
    @Test
    public void exceptionDuringUnmarshalling() throws Exception {
        DescribableListTest.Data data = new DescribableListTest.Data();
        data.list.add(new DescribableListTest.Datum(1));
        data.list.add(new DescribableListTest.Datum(2));
        data.list.add(new DescribableListTest.Datum(3));
        XStream2 xs = new XStream2();
        xs.addCriticalField(DescribableListTest.Data.class, "list");
        String xml = xs.toXML(data);
        data = ((DescribableListTest.Data) (xs.fromXML(xml)));
        Assert.assertEquals("[1, 3]", data.toString());
    }

    private static final class Data {
        final DescribableList<DescribableListTest.Datum, Descriptor<DescribableListTest.Datum>> list = new DescribableList();

        @Override
        public String toString() {
            return list.toString();
        }
    }

    private static final class Datum implements Describable<DescribableListTest.Datum> {
        final int val;

        Datum(int val) {
            this.val = val;
        }

        @Override
        public Descriptor<DescribableListTest.Datum> getDescriptor() {
            return new Descriptor<DescribableListTest.Datum>(DescribableListTest.Datum.class) {};
        }

        @Override
        public String toString() {
            return Integer.toString(val);
        }

        public static final class ConverterImpl extends AbstractSingleValueConverter {
            @Override
            public boolean canConvert(Class type) {
                return type == (DescribableListTest.Datum.class);
            }

            @Override
            public Object fromString(String str) {
                int val = Integer.parseInt(str);
                if (val == 2) {
                    throw new IllegalStateException("oops");
                }
                return new DescribableListTest.Datum(val);
            }
        }
    }
}

