/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
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
package tk.mybatis.mapper.test.othres;


import Style.camelhump;
import Style.camelhumpAndLowercase;
import Style.camelhumpAndUppercase;
import Style.lowercase;
import Style.normal;
import Style.uppercase;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.util.StringUtil;


/**
 *
 *
 * @author liuzh
 * @since 2015-10-31 09:41
 */
public class StyleTest {
    private String[] fields = new String[]{ "hello", "hello_world", // "hello_World",
    "helloWorld", "hello1", "hello_1" };

    @Test
    public void testNormal() {
        for (String field : fields) {
            Assert.assertEquals(field, StringUtil.convertByStyle(field, normal));
        }
    }

    @Test
    public void testUppercase() {
        for (String field : fields) {
            Assert.assertEquals(field.toUpperCase(), StringUtil.convertByStyle(field, uppercase));
        }
    }

    @Test
    public void testLowercase() {
        for (String field : fields) {
            Assert.assertEquals(field.toLowerCase(), StringUtil.convertByStyle(field, lowercase));
        }
    }

    @Test
    public void testCamelhump() {
        for (String field : fields) {
            System.out.println(((field + " - ") + (StringUtil.convertByStyle(field, camelhump))));
        }
    }

    @Test
    public void testCamelhumpUppercase() {
        for (String field : fields) {
            System.out.println(((field + " - ") + (StringUtil.convertByStyle(field, camelhumpAndUppercase))));
        }
    }

    @Test
    public void testCamelhumpLowercase() {
        for (String field : fields) {
            System.out.println(((field + " - ") + (StringUtil.convertByStyle(field, camelhumpAndLowercase))));
        }
    }
}

