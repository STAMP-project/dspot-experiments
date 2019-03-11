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
package ch.qos.logback.core.joran.action;


import Status.ERROR;
import Status.INFO;
import Status.WARN;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.joran.TrivialConfigurator;
import ch.qos.logback.core.joran.action.ext.StackAction;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.testUtil.CoreTestConstants;
import ch.qos.logback.core.testUtil.RandomUtil;
import ch.qos.logback.core.testUtil.StatusChecker;
import ch.qos.logback.core.util.StatusPrinter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Stack;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXParseException;


public class IncludeActionTest {
    static final String INCLUDE_KEY = "includeKey";

    static final String SUB_FILE_KEY = "subFileKey";

    static final String SECOND_FILE_KEY = "secondFileKey";

    Context context = new ContextBase();

    StatusChecker statusChecker = new StatusChecker(context);

    TrivialConfigurator tc;

    static final String INCLUSION_DIR_PREFIX = (CoreTestConstants.JORAN_INPUT_PREFIX) + "inclusion/";

    static final String TOP_BY_FILE = (IncludeActionTest.INCLUSION_DIR_PREFIX) + "topByFile.xml";

    static final String TOP_OPTIONAL = (IncludeActionTest.INCLUSION_DIR_PREFIX) + "topOptional.xml";

    static final String TOP_OPTIONAL_RESOURCE = (IncludeActionTest.INCLUSION_DIR_PREFIX) + "topOptionalResource.xml";

    static final String INTERMEDIARY_FILE = (IncludeActionTest.INCLUSION_DIR_PREFIX) + "intermediaryByFile.xml";

    static final String SUB_FILE = (IncludeActionTest.INCLUSION_DIR_PREFIX) + "subByFile.xml";

    static final String MULTI_INCLUDE_BY_FILE = (IncludeActionTest.INCLUSION_DIR_PREFIX) + "multiIncludeByFile.xml";

    static final String SECOND_FILE = (IncludeActionTest.INCLUSION_DIR_PREFIX) + "second.xml";

    static final String TOP_BY_URL = (IncludeActionTest.INCLUSION_DIR_PREFIX) + "topByUrl.xml";

    static final String TOP_BY_ENTITY = (IncludeActionTest.INCLUSION_DIR_PREFIX) + "topByEntity.xml";

    static final String INCLUDE_BY_RESOURCE = (IncludeActionTest.INCLUSION_DIR_PREFIX) + "topByResource.xml";

    static final String INCLUDED_FILE = (IncludeActionTest.INCLUSION_DIR_PREFIX) + "included.xml";

    static final String URL_TO_INCLUDE = "file:./" + (IncludeActionTest.INCLUDED_FILE);

    static final String INVALID = (IncludeActionTest.INCLUSION_DIR_PREFIX) + "invalid.xml";

    static final String INCLUDED_AS_RESOURCE = "asResource/joran/inclusion/includedAsResource.xml";

    int diff = RandomUtil.getPositiveInt();

    StackAction stackAction = new StackAction();

    @Test
    public void basicFile() throws JoranException {
        System.setProperty(IncludeActionTest.INCLUDE_KEY, IncludeActionTest.INCLUDED_FILE);
        doConfigure(IncludeActionTest.TOP_BY_FILE);
        verifyConfig(new String[]{ "IA", "IB" });
    }

    @Test
    public void optionalFile() throws JoranException {
        doConfigure(IncludeActionTest.TOP_OPTIONAL);
        verifyConfig(new String[]{ "IA", "IB" });
        StatusPrinter.print(context);
    }

    @Test
    public void optionalResource() throws JoranException {
        doConfigure(IncludeActionTest.TOP_OPTIONAL_RESOURCE);
        verifyConfig(new String[]{ "IA", "IB" });
        StatusPrinter.print(context);
        Assert.assertEquals(INFO, getHighestLevel(0));
    }

    @Test
    public void basicResource() throws JoranException {
        System.setProperty(IncludeActionTest.INCLUDE_KEY, IncludeActionTest.INCLUDED_AS_RESOURCE);
        doConfigure(IncludeActionTest.INCLUDE_BY_RESOURCE);
        verifyConfig(new String[]{ "AR_A", "AR_B" });
    }

    @Test
    public void basicURL() throws JoranException {
        System.setProperty(IncludeActionTest.INCLUDE_KEY, IncludeActionTest.URL_TO_INCLUDE);
        doConfigure(IncludeActionTest.TOP_BY_URL);
        verifyConfig(new String[]{ "IA", "IB" });
    }

    @Test
    public void noFileFound() throws JoranException {
        System.setProperty(IncludeActionTest.INCLUDE_KEY, "toto");
        doConfigure(IncludeActionTest.TOP_BY_FILE);
        Assert.assertEquals(WARN, getHighestLevel(0));
    }

    @Test
    public void withCorruptFile() throws JoranException, IOException {
        String tmpOut = copyToTemp(IncludeActionTest.INVALID);
        System.setProperty(IncludeActionTest.INCLUDE_KEY, tmpOut);
        doConfigure(IncludeActionTest.TOP_BY_FILE);
        Assert.assertEquals(ERROR, getHighestLevel(0));
        StatusPrinter.print(context);
        Assert.assertTrue(containsException(SAXParseException.class));
        // we like to erase the temp file in order to see
        // if http://jira.qos.ch/browse/LBCORE-122 was fixed
        File f = new File(tmpOut);
        Assert.assertTrue(f.exists());
        Assert.assertTrue(f.delete());
    }

    @Test
    public void malformedURL() throws JoranException {
        System.setProperty(IncludeActionTest.INCLUDE_KEY, "htp://logback.qos.ch");
        doConfigure(IncludeActionTest.TOP_BY_URL);
        Assert.assertEquals(ERROR, getHighestLevel(0));
        Assert.assertTrue(containsException(MalformedURLException.class));
    }

    @Test
    public void unknownURL() throws JoranException {
        System.setProperty(IncludeActionTest.INCLUDE_KEY, "http://logback2345.qos.ch");
        doConfigure(IncludeActionTest.TOP_BY_URL);
        Assert.assertEquals(WARN, getHighestLevel(0));
    }

    @Test
    public void nestedInclude() throws JoranException {
        System.setProperty(IncludeActionTest.SUB_FILE_KEY, IncludeActionTest.SUB_FILE);
        System.setProperty(IncludeActionTest.INCLUDE_KEY, IncludeActionTest.INTERMEDIARY_FILE);
        doConfigure(IncludeActionTest.TOP_BY_FILE);
        Stack<String> witness = new Stack<String>();
        witness.push("a");
        witness.push("b");
        witness.push("c");
        Assert.assertEquals(witness, stackAction.getStack());
    }

    @Test
    public void multiInclude() throws JoranException {
        System.setProperty(IncludeActionTest.INCLUDE_KEY, IncludeActionTest.INCLUDED_FILE);
        System.setProperty(IncludeActionTest.SECOND_FILE_KEY, IncludeActionTest.SECOND_FILE);
        doConfigure(IncludeActionTest.MULTI_INCLUDE_BY_FILE);
        verifyConfig(new String[]{ "IA", "IB", "SECOND" });
    }

    @Test
    public void includeAsEntity() throws JoranException {
        doConfigure(IncludeActionTest.TOP_BY_ENTITY);
        verifyConfig(new String[]{ "EA", "EB" });
    }
}

