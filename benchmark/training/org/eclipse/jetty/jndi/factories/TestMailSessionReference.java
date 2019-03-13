/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.jndi.factories;


import java.util.Properties;
import javax.mail.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.LinkRef;
import javax.naming.Name;
import javax.naming.NameParser;
import org.eclipse.jetty.jndi.NamingUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 */
public class TestMailSessionReference {
    @Test
    public void testMailSessionReference() throws Exception {
        InitialContext icontext = new InitialContext();
        MailSessionReference sref = new MailSessionReference();
        sref.setUser("janb");
        sref.setPassword("OBF:1xmk1w261z0f1w1c1xmq");
        Properties props = new Properties();
        props.put("mail.smtp.host", "xxx");
        props.put("mail.debug", "true");
        sref.setProperties(props);
        NamingUtil.bind(icontext, "mail/Session", sref);
        Object x = icontext.lookup("mail/Session");
        Assertions.assertNotNull(x);
        Assertions.assertTrue((x instanceof Session));
        Session session = ((Session) (x));
        Properties sessionProps = session.getProperties();
        Assertions.assertEquals(props, sessionProps);
        Assertions.assertTrue(session.getDebug());
        Context foo = icontext.createSubcontext("foo");
        NameParser parser = icontext.getNameParser("");
        Name objectNameInNamespace = parser.parse(icontext.getNameInNamespace());
        objectNameInNamespace.addAll(parser.parse("mail/Session"));
        NamingUtil.bind(foo, "mail/Session", new LinkRef(objectNameInNamespace.toString()));
        Object o = foo.lookup("mail/Session");
        Assertions.assertNotNull(o);
        Session fooSession = ((Session) (o));
        Assertions.assertEquals(props, fooSession.getProperties());
        Assertions.assertTrue(fooSession.getDebug());
        icontext.destroySubcontext("mail");
        icontext.destroySubcontext("foo");
    }
}

