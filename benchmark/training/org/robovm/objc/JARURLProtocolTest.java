/**
 * Copyright (C) 2015 RoboVM AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.objc;


import NSStringEncoding.UTF8;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSURL;


/**
 * Tests our native <code>NSURLProtocol</code> which handles <code>jar:</code>
 * URLs. This is registered on app startup by the native vm libs.
 */
public class JARURLProtocolTest {
    @Test
    public void testReadSimpleEntry() throws Exception {
        URL jarFile = JARURLProtocolTest.createJar("resource.txt", "Hello world!!!");
        URL url = JARURLProtocolTest.rel(jarFile, "resource.txt", true);
        NSURL nsUrl = new NSURL(url);
        String expected = JARURLProtocolTest.toString(url);
        String actual = NSString.readURL(nsUrl, UTF8);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReadEntryWithSpacesAndPlus() throws Exception {
        // NOTE: jar: entries that contain spaces in JAR URLs are not URL
        // encoded. Such URLs are illegal and will make NSURL fail. We need to
        // initialize the NSURL with a URL that has been properly encoded which
        // is why we pass encodePath=true to the NSURL but false to toString().
        URL jarFile = JARURLProtocolTest.createJar("resource with spaces and+.txt", "Hello world!!!");
        NSURL nsUrl = new NSURL(JARURLProtocolTest.rel(jarFile, "resource with spaces and+.txt", true));
        String expected = JARURLProtocolTest.toString(JARURLProtocolTest.rel(jarFile, "resource with spaces and+.txt", false));
        String actual = NSString.readURL(nsUrl, UTF8);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReadLargeEntry() throws Exception {
        // 8 MB of very compressable data
        URL jarFile = JARURLProtocolTest.createJar("resource.txt", new JARURLProtocolTest.RepeatingCharSequence("01234567", (1024 * 1024)));
        URL url = JARURLProtocolTest.rel(jarFile, "resource.txt", true);
        NSURL nsUrl = new NSURL(url);
        String expected = JARURLProtocolTest.toString(url);
        String actual = NSString.readURL(nsUrl, UTF8);
        Assert.assertEquals(expected, actual);
    }

    static class RepeatingCharSequence implements CharSequence {
        final String s;

        final int multiple;

        public RepeatingCharSequence(String s, int multiple) {
            this.s = s;
            this.multiple = multiple;
        }

        @Override
        public int length() {
            return (s.length()) * (multiple);
        }

        @Override
        public char charAt(int index) {
            return s.charAt((index % (s.length())));
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return null;
        }
    }
}

