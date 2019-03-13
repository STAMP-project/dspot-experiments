/**
 * Copyright 2010 david varnes.
 *
 * Licensed under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.freeswitch.esl.client.transport.message;


import java.util.ArrayList;
import java.util.List;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EslFrameDecoderTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private DecoderEmbedder<EslMessage> embedder;

    @Test
    public void simpleMessage() throws Exception {
        List<String> inputLines = new ArrayList<String>();
        inputLines.add("Content-Type: command/reply");
        inputLines.add("Reply-Text: +OK event listener enabled plain");
        inputLines.add("");
        embedder.offer(createInputBuffer(inputLines, true));
        embedder.finish();
        EslMessage result = embedder.poll();
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.getHeaders().size());
        Assert.assertFalse(result.hasContentLength());
    }

    @Test
    public void simpleMessageWithContent() throws Exception {
        List<String> inputLines = new ArrayList<String>();
        inputLines.add("Content-Type: api/response");
        inputLines.add("Content-Length: 694");
        inputLines.add("");
        inputLines.add("=================================================================================================");
        inputLines.add("                     Name     Type                               Data  State");
        inputLines.add("                 internal  profile   sip:mod_sofia@192.168.1.1:5060        RUNNING (0)");
        inputLines.add("                 external  profile   sip:mod_sofia@yyy.yyy.yyy.yyy:5080    RUNNING (0)");
        inputLines.add("                    iinet  gateway   sip:02xxxxxxxx@sip.nsw.iinet.net.au   REGED");
        inputLines.add("                   clinic  profile   sip:mod_sofia@yyy.yyy.yyy.yyy:5070    RUNNING (0)");
        inputLines.add("              192.168.1.1  alias                             internal  ALIASED");
        inputLines.add("=================================================================================================");
        embedder.offer(createInputBuffer(inputLines, true));
        EslMessage result = embedder.poll();
        embedder.finish();
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.getHeaders().size());
        Assert.assertTrue(result.hasContentLength());
        Assert.assertEquals(8, result.getBodyLines().size());
    }

    @Test
    public void eventWithSecondContentLength() {
        List<String> inputLines = new ArrayList<String>();
        inputLines.add("Content-Length: 582");
        inputLines.add("Content-Type: text/event-plain");
        inputLines.add("");
        inputLines.add("Job-UUID: 7f4db78a-17d7-11dd-b7a0-db4edd065621");
        inputLines.add("Job-Command: originate");
        inputLines.add("Job-Command-Arg: sofia/default/1005%20'%26park'");
        inputLines.add("Event-Name: BACKGROUND_JOB");
        inputLines.add("Core-UUID: 42bdf272-16e6-11dd-b7a0-db4edd065621");
        inputLines.add("FreeSWITCH-Hostname: ser");
        inputLines.add("FreeSWITCH-IPv4: 192.168.1.104");
        inputLines.add("FreeSWITCH-IPv6: 127.0.0.1");
        inputLines.add("Event-Date-Local: 2008-05-02%2007%3A37%3A03");
        inputLines.add("Event-Date-GMT: Thu,%2001%20May%202008%2023%3A37%3A03%20GMT");
        inputLines.add("Event-Date-timestamp: 1209685023894968");
        inputLines.add("Event-Calling-File: mod_event_socket.c");
        inputLines.add("Event-Calling-Function: api_exec");
        inputLines.add("Event-Calling-Line-Number: 609");
        inputLines.add("Content-Length: 41");
        inputLines.add("");
        inputLines.add("+OK 7f4de4bc-17d7-11dd-b7a0-db4edd065621");
        embedder.offer(createInputBuffer(inputLines, false));
        /* NB .. there is no trailing '\n' in this event */
        EslMessage result = embedder.poll();
        embedder.finish();
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.getHeaders().size());
        Assert.assertTrue(result.hasContentLength());
        Assert.assertEquals(17, result.getBodyLines().size());
    }
}

