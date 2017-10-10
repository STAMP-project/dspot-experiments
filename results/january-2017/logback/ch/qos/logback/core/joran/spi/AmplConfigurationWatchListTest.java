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


package ch.qos.logback.core.joran.spi;


/**
 * @author Ceki G&uuml;lc&uuml;
 */
public class AmplConfigurationWatchListTest {
    // See http://jira.qos.ch/browse/LBCORE-119
    @org.junit.Test
    public void fileToURLAndBack() throws java.net.MalformedURLException {
        java.io.File file = new java.io.File("a b.xml");
        java.net.URL url = file.toURI().toURL();
        ch.qos.logback.core.joran.spi.ConfigurationWatchList cwl = new ch.qos.logback.core.joran.spi.ConfigurationWatchList();
        java.io.File back = cwl.convertToFile(url);
        org.junit.Assert.assertEquals(file.getName(), back.getName());
    }

    // See http://jira.qos.ch/browse/LBCORE-119
    /* amplification of ch.qos.logback.core.joran.spi.ConfigurationWatchListTest#fileToURLAndBack */
    @org.junit.Test(timeout = 10000)
    public void fileToURLAndBack_cf7() throws java.net.MalformedURLException {
        java.io.File file = new java.io.File("a b.xml");
        java.net.URL url = file.toURI().toURL();
        ch.qos.logback.core.joran.spi.ConfigurationWatchList cwl = new ch.qos.logback.core.joran.spi.ConfigurationWatchList();
        java.io.File back = cwl.convertToFile(url);
        // AssertGenerator replace invocation
        boolean o_fileToURLAndBack_cf7__10 = // StatementAdderMethod cloned existing statement
cwl.changeDetected();
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(o_fileToURLAndBack_cf7__10);
        org.junit.Assert.assertEquals(file.getName(), back.getName());
    }

    // See http://jira.qos.ch/browse/LBCORE-119
    /* amplification of ch.qos.logback.core.joran.spi.ConfigurationWatchListTest#fileToURLAndBack */
    @org.junit.Test(timeout = 10000)
    public void fileToURLAndBack_cf22() throws java.net.MalformedURLException {
        java.io.File file = new java.io.File("a b.xml");
        java.net.URL url = file.toURI().toURL();
        ch.qos.logback.core.joran.spi.ConfigurationWatchList cwl = new ch.qos.logback.core.joran.spi.ConfigurationWatchList();
        java.io.File back = cwl.convertToFile(url);
        // AssertGenerator replace invocation
        java.net.URL o_fileToURLAndBack_cf22__10 = // StatementAdderMethod cloned existing statement
cwl.getMainURL();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_fileToURLAndBack_cf22__10);
        org.junit.Assert.assertEquals(file.getName(), back.getName());
    }

    // See http://jira.qos.ch/browse/LBCORE-119
    /* amplification of ch.qos.logback.core.joran.spi.ConfigurationWatchListTest#fileToURLAndBack */
    @org.junit.Test(timeout = 10000)
    public void fileToURLAndBack_cf45() throws java.net.MalformedURLException {
        java.io.File file = new java.io.File("a b.xml");
        java.net.URL url = file.toURI().toURL();
        ch.qos.logback.core.joran.spi.ConfigurationWatchList cwl = new ch.qos.logback.core.joran.spi.ConfigurationWatchList();
        java.io.File back = cwl.convertToFile(url);
        // StatementAdderOnAssert create null value
        java.net.URL vc_20 = (java.net.URL)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_20);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ConfigurationWatchList vc_19 = new ch.qos.logback.core.joran.spi.ConfigurationWatchList();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).getMainURL());
        // AssertGenerator add assertion
        java.util.ArrayList collection_2026204459 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2026204459, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).buildClone()).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).buildClone()).getContext());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1890976117 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1890976117, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_137156421 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_137156421, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).getCopyOfFileWatchList());;
        // StatementAdderMethod cloned existing statement
        vc_19.setMainURL(vc_20);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).getMainURL());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1486396952 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1486396952, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).buildClone()).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).buildClone()).getContext());
        // AssertGenerator add assertion
        java.util.ArrayList collection_789873002 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_789873002, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).buildClone()).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_882539430 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_882539430, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_19).getCopyOfFileWatchList());;
        org.junit.Assert.assertEquals(file.getName(), back.getName());
    }

    // See http://jira.qos.ch/browse/LBCORE-119
    /* amplification of ch.qos.logback.core.joran.spi.ConfigurationWatchListTest#fileToURLAndBack */
    @org.junit.Test(timeout = 10000)
    public void fileToURLAndBack_cf25() throws java.net.MalformedURLException {
        java.io.File file = new java.io.File("a b.xml");
        java.net.URL url = file.toURI().toURL();
        ch.qos.logback.core.joran.spi.ConfigurationWatchList cwl = new ch.qos.logback.core.joran.spi.ConfigurationWatchList();
        java.io.File back = cwl.convertToFile(url);
        // AssertGenerator replace invocation
        java.util.List<java.io.File> o_fileToURLAndBack_cf25__10 = // StatementAdderMethod cloned existing statement
cwl.getCopyOfFileWatchList();
        // AssertGenerator add assertion
        java.util.ArrayList collection_162821087 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_162821087, o_fileToURLAndBack_cf25__10);;
        org.junit.Assert.assertEquals(file.getName(), back.getName());
    }

    // See http://jira.qos.ch/browse/LBCORE-119
    /* amplification of ch.qos.logback.core.joran.spi.ConfigurationWatchListTest#fileToURLAndBack */
    @org.junit.Test(timeout = 10000)
    public void fileToURLAndBack_cf37() throws java.net.MalformedURLException {
        java.io.File file = new java.io.File("a b.xml");
        java.net.URL url = file.toURI().toURL();
        ch.qos.logback.core.joran.spi.ConfigurationWatchList cwl = new ch.qos.logback.core.joran.spi.ConfigurationWatchList();
        java.io.File back = cwl.convertToFile(url);
        // StatementAdderMethod cloned existing statement
        cwl.clear();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_894441858 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_894441858, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getContext());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1905631828 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1905631828, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_218494191 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_218494191, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getStatusManager());
        org.junit.Assert.assertEquals(file.getName(), back.getName());
    }

    // See http://jira.qos.ch/browse/LBCORE-119
    /* amplification of ch.qos.logback.core.joran.spi.ConfigurationWatchListTest#fileToURLAndBack */
    @org.junit.Test(timeout = 10000)
    public void fileToURLAndBack_cf25_cf374() throws java.net.MalformedURLException {
        java.io.File file = new java.io.File("a b.xml");
        java.net.URL url = file.toURI().toURL();
        ch.qos.logback.core.joran.spi.ConfigurationWatchList cwl = new ch.qos.logback.core.joran.spi.ConfigurationWatchList();
        java.io.File back = cwl.convertToFile(url);
        // AssertGenerator replace invocation
        java.util.List<java.io.File> o_fileToURLAndBack_cf25__10 = // StatementAdderMethod cloned existing statement
cwl.getCopyOfFileWatchList();
        // AssertGenerator add assertion
        java.util.ArrayList collection_162821087 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_162821087, o_fileToURLAndBack_cf25__10);;
        // StatementAdderOnAssert create null value
        java.net.URL vc_174 = (java.net.URL)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_174);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ConfigurationWatchList vc_173 = new ch.qos.logback.core.joran.spi.ConfigurationWatchList();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).getContext());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1519041765 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1519041765, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).getMainURL());
        // AssertGenerator add assertion
        java.util.ArrayList collection_558508092 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_558508092, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).buildClone()).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1143796005 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1143796005, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).changeDetected());
        // StatementAdderMethod cloned existing statement
        vc_173.setMainURL(vc_174);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).getContext());
        // AssertGenerator add assertion
        java.util.ArrayList collection_110177805 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_110177805, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).getMainURL());
        // AssertGenerator add assertion
        java.util.ArrayList collection_539595671 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_539595671, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).buildClone()).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).buildClone()).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        java.util.ArrayList collection_740948998 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_740948998, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_173).changeDetected());
        org.junit.Assert.assertEquals(file.getName(), back.getName());
    }

    // See http://jira.qos.ch/browse/LBCORE-119
    /* amplification of ch.qos.logback.core.joran.spi.ConfigurationWatchListTest#fileToURLAndBack */
    @org.junit.Test(timeout = 10000)
    public void fileToURLAndBack_cf22_cf246() throws java.net.MalformedURLException {
        java.io.File file = new java.io.File("a b.xml");
        java.net.URL url = file.toURI().toURL();
        ch.qos.logback.core.joran.spi.ConfigurationWatchList cwl = new ch.qos.logback.core.joran.spi.ConfigurationWatchList();
        java.io.File back = cwl.convertToFile(url);
        // AssertGenerator replace invocation
        java.net.URL o_fileToURLAndBack_cf22__10 = // StatementAdderMethod cloned existing statement
cwl.getMainURL();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(o_fileToURLAndBack_cf22__10);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ConfigurationWatchList vc_113 = new ch.qos.logback.core.joran.spi.ConfigurationWatchList();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_113).getStatusManager());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1573783412 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1573783412, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_113).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_113).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_113).buildClone()).getContext());
        // AssertGenerator add assertion
        java.util.ArrayList collection_2066981228 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2066981228, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_113).buildClone()).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_113).buildClone()).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_113).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_113).buildClone()).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_113).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1783537901 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1783537901, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_113).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_113).buildClone()).buildClone()).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_113).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_113).buildClone()).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_113).buildClone()).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_113).getMainURL());
        // AssertGenerator replace invocation
        ch.qos.logback.core.joran.spi.ConfigurationWatchList o_fileToURLAndBack_cf22_cf246__16 = // StatementAdderMethod cloned existing statement
vc_113.buildClone();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)o_fileToURLAndBack_cf22_cf246__16).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)o_fileToURLAndBack_cf22_cf246__16).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)o_fileToURLAndBack_cf22_cf246__16).buildClone()).buildClone()).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)o_fileToURLAndBack_cf22_cf246__16).buildClone()).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)o_fileToURLAndBack_cf22_cf246__16).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)o_fileToURLAndBack_cf22_cf246__16).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)o_fileToURLAndBack_cf22_cf246__16).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1857091282 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1857091282, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)o_fileToURLAndBack_cf22_cf246__16).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        java.util.ArrayList collection_435816849 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_435816849, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)o_fileToURLAndBack_cf22_cf246__16).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)o_fileToURLAndBack_cf22_cf246__16).buildClone()).buildClone()).getMainURL());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1547824818 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1547824818, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)o_fileToURLAndBack_cf22_cf246__16).buildClone()).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)o_fileToURLAndBack_cf22_cf246__16).buildClone()).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)o_fileToURLAndBack_cf22_cf246__16).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)o_fileToURLAndBack_cf22_cf246__16).buildClone()).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)o_fileToURLAndBack_cf22_cf246__16).getStatusManager());
        org.junit.Assert.assertEquals(file.getName(), back.getName());
    }

    // See http://jira.qos.ch/browse/LBCORE-119
    /* amplification of ch.qos.logback.core.joran.spi.ConfigurationWatchListTest#fileToURLAndBack */
    @org.junit.Test(timeout = 10000)
    public void fileToURLAndBack_cf37_cf461_cf1861() throws java.net.MalformedURLException {
        java.io.File file = new java.io.File("a b.xml");
        java.net.URL url = file.toURI().toURL();
        ch.qos.logback.core.joran.spi.ConfigurationWatchList cwl = new ch.qos.logback.core.joran.spi.ConfigurationWatchList();
        java.io.File back = cwl.convertToFile(url);
        // StatementAdderMethod cloned existing statement
        cwl.clear();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_651528158 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_651528158, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getContext());
        // AssertGenerator add assertion
        java.util.ArrayList collection_747753293 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_747753293, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_200152305 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_200152305, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_236461374 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_236461374, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getContext());
        // AssertGenerator add assertion
        java.util.ArrayList collection_2047055388 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_2047055388, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1240272343 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1240272343, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_894441858 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_894441858, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getContext());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1905631828 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1905631828, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_218494191 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_218494191, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getStatusManager());
        // StatementAdderMethod cloned existing statement
        cwl.clear();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_887295803 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_887295803, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getContext());
        // AssertGenerator add assertion
        java.util.ArrayList collection_472804278 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_472804278, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1452520285 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1452520285, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_612139913 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_612139913, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getContext());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1621021928 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1621021928, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_741263933 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_741263933, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)cwl).buildClone()).buildClone()).getStatusManager());
        // StatementAdderOnAssert create null value
        java.net.URL vc_856 = (java.net.URL)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_856);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.joran.spi.ConfigurationWatchList vc_855 = new ch.qos.logback.core.joran.spi.ConfigurationWatchList();
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_616114417 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_616114417, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).getContext());
        // AssertGenerator add assertion
        java.util.ArrayList collection_661501678 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_661501678, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).buildClone()).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_628104189 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_628104189, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).getMainURL());
        // StatementAdderMethod cloned existing statement
        vc_855.setMainURL(vc_856);
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_1384304146 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_1384304146, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).buildClone()).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).buildClone()).getContext());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).getContext());
        // AssertGenerator add assertion
        java.util.ArrayList collection_706250418 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_706250418, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).changeDetected());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).getStatusManager());
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).getMainURL());
        // AssertGenerator add assertion
        org.junit.Assert.assertFalse(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).buildClone()).changeDetected());
        // AssertGenerator add assertion
        java.util.ArrayList collection_399362980 = new java.util.ArrayList<Object>();
	org.junit.Assert.assertEquals(collection_399362980, ((ch.qos.logback.core.joran.spi.ConfigurationWatchList)((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).buildClone()).getCopyOfFileWatchList());;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(((ch.qos.logback.core.joran.spi.ConfigurationWatchList)vc_855).getMainURL());
        org.junit.Assert.assertEquals(file.getName(), back.getName());
    }
}

