/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.mail;


import Comparison.EQ;
import Comparison.GE;
import Comparison.GT;
import Comparison.LE;
import Comparison.LT;
import Comparison.NE;
import java.util.Date;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.mock_javamail.Mailbox;

import static Op.or;


/**
 *
 */
public class SearchTermBuilderTest extends Assert {
    @Test
    public void testSearchTermBuilderFromAndSubject() throws Exception {
        SearchTermBuilder build = new SearchTermBuilder();
        SearchTerm st = build.from("someone@somewhere.com").subject("Camel").build();
        Assert.assertNotNull(st);
        // create dummy message
        Mailbox.clearAll();
        JavaMailSender sender = new DefaultJavaMailSender();
        MimeMessage msg = new MimeMessage(sender.getSession());
        msg.setSubject("Yeah Camel rocks");
        msg.setText("Apache Camel is a cool project. Have a fun ride.");
        msg.setFrom(new InternetAddress("someone@somewhere.com"));
        Assert.assertTrue("Should match message", st.match(msg));
        MimeMessage msg2 = new MimeMessage(sender.getSession());
        msg2.setSubject("Apache Camel is fantastic");
        msg2.setText("I like Camel.");
        msg2.setFrom(new InternetAddress("donotreply@somewhere.com"));
        Assert.assertFalse("Should not match message, as from doesn't match", st.match(msg2));
    }

    @Test
    public void testSearchTermBuilderFromOrSubject() throws Exception {
        SearchTermBuilder build = new SearchTermBuilder();
        SearchTerm st = build.subject("Camel").from(or, "admin@apache.org").build();
        Assert.assertNotNull(st);
        // create dummy message
        Mailbox.clearAll();
        JavaMailSender sender = new DefaultJavaMailSender();
        MimeMessage msg = new MimeMessage(sender.getSession());
        msg.setSubject("Yeah Camel rocks");
        msg.setText("Apache Camel is a cool project. Have a fun ride.");
        msg.setFrom(new InternetAddress("someone@somewhere.com"));
        Assert.assertTrue("Should match message", st.match(msg));
        MimeMessage msg2 = new MimeMessage(sender.getSession());
        msg2.setSubject("Beware");
        msg2.setText("This is from the administrator.");
        msg2.setFrom(new InternetAddress("admin@apache.org"));
        Assert.assertTrue("Should match message, as its from admin", st.match(msg2));
    }

    @Test
    public void testSearchTermSentLast24Hours() throws Exception {
        SearchTermBuilder build = new SearchTermBuilder();
        long offset = (-1) * (((24 * 60) * 60) * 1000L);
        SearchTerm st = build.subject("Camel").sentNow(GE, offset).build();
        Assert.assertNotNull(st);
        // create dummy message
        Mailbox.clearAll();
        JavaMailSender sender = new DefaultJavaMailSender();
        MimeMessage msg = new MimeMessage(sender.getSession());
        msg.setSubject("Yeah Camel rocks");
        msg.setText("Apache Camel is a cool project. Have a fun ride.");
        msg.setFrom(new InternetAddress("someone@somewhere.com"));
        msg.setSentDate(new Date());
        Assert.assertTrue("Should match message", st.match(msg));
        MimeMessage msg2 = new MimeMessage(sender.getSession());
        msg2.setSubject("Camel in Action");
        msg2.setText("Hey great book");
        msg2.setFrom(new InternetAddress("dude@apache.org"));
        // mark it as sent 2 days ago
        long twoDays = (((2 * 24) * 60) * 60) * 1000L;
        long time = (new Date().getTime()) - twoDays;
        msg2.setSentDate(new Date(time));
        Assert.assertFalse("Should not match message as its too old", st.match(msg2));
    }

    @Test
    public void testComparison() throws Exception {
        Assert.assertEquals(1, LE.asNum());
        Assert.assertEquals(2, LT.asNum());
        Assert.assertEquals(3, EQ.asNum());
        Assert.assertEquals(4, NE.asNum());
        Assert.assertEquals(5, GT.asNum());
        Assert.assertEquals(6, GE.asNum());
    }
}

