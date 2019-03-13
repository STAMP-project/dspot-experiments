/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.joran;


import CoreConstants.XML_PARSING;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ext.IncAction;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.testUtil.CoreTestConstants;
import ch.qos.logback.core.testUtil.TrivialStatusListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;


public class TrivialConfiguratorTest {
    Context context = new ContextBase();

    HashMap<ElementSelector, Action> rulesMap = new HashMap<ElementSelector, Action>();

    @Test
    public void smoke() throws Exception {
        int oldBeginCount = IncAction.beginCount;
        int oldEndCount = IncAction.endCount;
        int oldErrorCount = IncAction.errorCount;
        doTest((((CoreTestConstants.TEST_SRC_PREFIX) + "input/joran/") + "inc.xml"));
        Assert.assertEquals(oldErrorCount, IncAction.errorCount);
        Assert.assertEquals((oldBeginCount + 1), IncAction.beginCount);
        Assert.assertEquals((oldEndCount + 1), IncAction.endCount);
    }

    @Test
    public void inexistentFile() {
        TrivialStatusListener tsl = new TrivialStatusListener();
        tsl.start();
        String filename = ((CoreTestConstants.TEST_SRC_PREFIX) + "input/joran/") + "nothereBLAH.xml";
        context.getStatusManager().add(tsl);
        try {
            doTest(filename);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().startsWith("Could not open ["));
        }
        Assert.assertTrue(((tsl.list.size()) + " should be greater than or equal to 1"), ((tsl.list.size()) >= 1));
        Status s0 = tsl.list.get(0);
        Assert.assertTrue(s0.getMessage().startsWith("Could not open ["));
    }

    @Test
    public void illFormedXML() {
        TrivialStatusListener tsl = new TrivialStatusListener();
        tsl.start();
        String filename = ((CoreTestConstants.TEST_SRC_PREFIX) + "input/joran/") + "illformed.xml";
        context.getStatusManager().add(tsl);
        try {
            doTest(filename);
        } catch (Exception e) {
        }
        Assert.assertEquals(2, tsl.list.size());
        Status s0 = tsl.list.get(0);
        Assert.assertTrue(s0.getMessage().startsWith(XML_PARSING));
    }

    @Test
    public void lbcore105() throws JoranException, IOException {
        String jarEntry = "buzz.xml";
        File jarFile = makeRandomJarFile();
        fillInJarFile(jarFile, jarEntry);
        URL url = asURL(jarFile, jarEntry);
        TrivialConfigurator tc = new TrivialConfigurator(rulesMap);
        tc.setContext(context);
        doConfigure(url);
        // deleting an open file fails
        Assert.assertTrue(jarFile.delete());
        Assert.assertFalse(jarFile.exists());
    }

    @Test
    public void lbcore127() throws JoranException, IOException {
        String jarEntry = "buzz.xml";
        String jarEntry2 = "lightyear.xml";
        File jarFile = makeRandomJarFile();
        fillInJarFile(jarFile, jarEntry, jarEntry2);
        URL url1 = asURL(jarFile, jarEntry);
        URL url2 = asURL(jarFile, jarEntry2);
        URLConnection urlConnection2 = url2.openConnection();
        urlConnection2.setUseCaches(false);
        InputStream is = urlConnection2.getInputStream();
        TrivialConfigurator tc = new TrivialConfigurator(rulesMap);
        tc.setContext(context);
        doConfigure(url1);
        is.read();
        is.close();
        // deleting an open file fails
        Assert.assertTrue(jarFile.delete());
        Assert.assertFalse(jarFile.exists());
    }
}

