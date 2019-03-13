/**
 * This file is part of the Heritrix web crawler (crawler.archive.org).
 *
 *  Licensed to the Internet Archive (IA) by one or more individual
 *  contributors.
 *
 *  The IA licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.archive.modules.net;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import junit.framework.TestCase;
import org.archive.bdb.AutoKryo;

import static Robotstxt.MAX_SIZE;


public class RobotstxtTest extends TestCase {
    public void testParseRobots() throws IOException {
        Reader reader = new StringReader("BLAH");
        Robotstxt r = new Robotstxt(reader);
        TestCase.assertFalse(r.hasErrors);
        TestCase.assertEquals(0, r.getNamedUserAgents().size());
        // Parse archive robots.txt with heritrix agent.
        String agent = "archive.org_bot";
        reader = new BufferedReader(new StringReader((((("User-agent: " + agent) + "\n") + "Disallow: /cgi-bin/\n") + "Disallow: /details/software\n")));
        r = new Robotstxt(reader);
        TestCase.assertFalse(r.hasErrors);
        TestCase.assertEquals(1, r.getNamedUserAgents().size());
        TestCase.assertEquals(1, r.agentsToDirectives.size());
        TestCase.assertEquals(agent, r.getNamedUserAgents().get(0));
        // Parse archive robots.txt with star agent.
        agent = "*";
        reader = new BufferedReader(new StringReader((((("User-agent: " + agent) + "\n") + "Disallow: /cgi-bin/\n") + "Disallow: /details/software\n")));
        r = new Robotstxt(reader);
        TestCase.assertFalse(r.hasErrors);
        TestCase.assertEquals(0, r.getNamedUserAgents().size());
        TestCase.assertEquals(0, r.agentsToDirectives.size());
    }

    public void testValidRobots() throws IOException {
        Robotstxt r = RobotstxtTest.sampleRobots1();
        evalRobots(r);
    }

    public void testWhitespaceFlawedRobots() throws IOException {
        Robotstxt r = whitespaceFlawedRobots();
        evalRobots(r);
    }

    /**
     * Test handling of a robots.txt with extraneous HTML markup
     *
     * @throws IOException
     * 		
     */
    public void testHtmlMarkupRobots() throws IOException {
        Robotstxt r = htmlMarkupRobots();
        TestCase.assertFalse(r.getDirectivesFor("anybot").allows("/index.html"));
        TestCase.assertEquals(30.0F, r.getDirectivesFor("anybot").getCrawlDelay());
    }

    /**
     * Test serialization/deserialization of Robotstxt object.
     * Improper behavior, such as failure to restore shared RobotsDirectives objects,
     * can lead to excess memory usage and CPU cycles. In one case, 450KB robots.txt
     * exploded into 450MB. See [HER-1912].
     *
     * @throws IOException
     * 		
     */
    public void testCompactSerialization() throws IOException {
        AutoKryo kryo = new AutoKryo();
        kryo.autoregister(Robotstxt.class);
        final String TEST_ROBOTS_TXT = "User-Agent:a\n" + ((("User-Agent:b\n" + "User-Agent:c\n") + "User-Agent:d\n") + "Disallow:/service\n");
        StringReader sr = new StringReader(TEST_ROBOTS_TXT);
        Robotstxt rt = new Robotstxt(sr);
        {
            RobotsDirectives da = rt.getDirectivesFor("a", false);
            RobotsDirectives db = rt.getDirectivesFor("b", false);
            TestCase.assertTrue("user-agent a and b shares the same RobotsDirectives before serialization", (da == db));
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        kryo.writeObject(buffer, rt);
        buffer.flip();
        Robotstxt rt2 = kryo.readObject(buffer, Robotstxt.class);
        TestCase.assertNotNull(rt2);
        {
            RobotsDirectives da = rt2.getDirectivesFor("a", false);
            RobotsDirectives db = rt2.getDirectivesFor("b", false);
            TestCase.assertTrue("user-agent a and b shares the same RobotsDirectives after deserialization", (da == db));
        }
    }

    public void testSeparatedSections() throws IOException {
        final String TEST_ROBOTS_TXT = "User-agent: *\n" + (((((("Crawl-delay: 5\n" + "User-agent: a\n") + "Disallow: /\n") + "User-agent: *\n") + "Disallow: /disallowed\n") + "User-agent: a\n") + "Crawl-delay: 99\n");
        StringReader sr = new StringReader(TEST_ROBOTS_TXT);
        Robotstxt rt = new Robotstxt(sr);
        TestCase.assertFalse(rt.getDirectivesFor("a").allows("/foo"));
        TestCase.assertTrue(rt.getDirectivesFor("c").allows("/foo"));
        TestCase.assertFalse(rt.getDirectivesFor("c").allows("/disallowed"));
        TestCase.assertEquals(5.0F, rt.getDirectivesFor("c").getCrawlDelay());
        TestCase.assertEquals(99.0F, rt.getDirectivesFor("a").getCrawlDelay());
    }

    public void testSizeLimit() throws IOException {
        StringBuilder builder = new StringBuilder(("User-agent: a\n" + ("  Disallow: /\n" + "User-Agent: b\nDisallow: /")));
        for (int i = 0; i < (MAX_SIZE); i++) {
            builder.append(' ');
        }
        builder.append("\nUser-Agent: c\nDisallow: /\n");
        Robotstxt rt = new Robotstxt(new StringReader(builder.toString()));
        TestCase.assertFalse("we should parse the first few lines", rt.getDirectivesFor("a").allows("/foo"));
        TestCase.assertTrue("ignore the line that breaks the size limit", rt.getDirectivesFor("b").allows("/foo"));
        TestCase.assertTrue("and also ignore any lines after the size limit", rt.getDirectivesFor("c").allows("/foo"));
    }

    public void testAllBlankLines() throws IOException {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < (MAX_SIZE); i++) {
            builder.append('\n');
        }
        new Robotstxt(new StringReader(builder.toString()));
    }
}

