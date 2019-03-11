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
package org.eclipse.jetty.webapp;


import FragmentDescriptor.OtherType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.util.resource.Resource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static FragmentDescriptor.NAMELESS;


/**
 * OrderingTest
 */
public class OrderingTest {
    public class TestResource extends Resource {
        public String _name;

        public TestResource(String name) {
            _name = name;
        }

        /**
         *
         *
         * @see org.eclipse.jetty.util.resource.Resource#addPath(java.lang.String)
         */
        @Override
        public Resource addPath(String path) throws IOException, MalformedURLException {
            return null;
        }

        /**
         *
         *
         * @see org.eclipse.jetty.util.resource.Resource#delete()
         */
        @Override
        public boolean delete() throws SecurityException {
            return false;
        }

        /**
         *
         *
         * @see org.eclipse.jetty.util.resource.Resource#exists()
         */
        @Override
        public boolean exists() {
            return false;
        }

        /**
         *
         *
         * @see org.eclipse.jetty.util.resource.Resource#getFile()
         */
        @Override
        public File getFile() throws IOException {
            return null;
        }

        /**
         *
         *
         * @see org.eclipse.jetty.util.resource.Resource#getInputStream()
         */
        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public ReadableByteChannel getReadableByteChannel() throws IOException {
            return null;
        }

        /**
         *
         *
         * @see org.eclipse.jetty.util.resource.Resource#getName()
         */
        @Override
        public String getName() {
            return _name;
        }

        @Override
        public URL getURL() {
            return null;
        }

        /**
         *
         *
         * @see org.eclipse.jetty.util.resource.Resource#isContainedIn(org.eclipse.jetty.util.resource.Resource)
         */
        @Override
        public boolean isContainedIn(Resource r) throws MalformedURLException {
            return false;
        }

        /**
         *
         *
         * @see org.eclipse.jetty.util.resource.Resource#isDirectory()
         */
        @Override
        public boolean isDirectory() {
            return false;
        }

        /**
         *
         *
         * @see org.eclipse.jetty.util.resource.Resource#lastModified()
         */
        @Override
        public long lastModified() {
            return 0;
        }

        /**
         *
         *
         * @see org.eclipse.jetty.util.resource.Resource#length()
         */
        @Override
        public long length() {
            return 0;
        }

        /**
         *
         *
         * @see org.eclipse.jetty.util.resource.Resource#list()
         */
        @Override
        public String[] list() {
            return null;
        }

        /**
         *
         *
         * @see org.eclipse.jetty.util.resource.Resource#close()
         */
        @Override
        public void close() {
        }

        /**
         *
         *
         * @see org.eclipse.jetty.util.resource.Resource#renameTo(org.eclipse.jetty.util.resource.Resource)
         */
        @Override
        public boolean renameTo(Resource dest) throws SecurityException {
            return false;
        }
    }

    @Test
    public void testRelativeOrdering0() throws Exception {
        // Example from ServletSpec p.70
        MetaData metaData = new MetaData();
        List<Resource> resources = new ArrayList<Resource>();
        metaData._ordering = new RelativeOrdering(metaData);
        // A: after others, after C
        OrderingTest.TestResource jar1 = new OrderingTest.TestResource("A");
        resources.add(jar1);
        OrderingTest.TestResource r1 = new OrderingTest.TestResource("A/web-fragment.xml");
        FragmentDescriptor f1 = new FragmentDescriptor(r1);
        f1._name = "A";
        metaData._webFragmentNameMap.put(f1._name, f1);
        metaData._webFragmentResourceMap.put(jar1, f1);
        f1._otherType = OtherType.After;
        // ((RelativeOrdering)metaData._ordering).addAfterOthers(r1);
        f1._afters.add("C");
        // B: before others
        OrderingTest.TestResource jar2 = new OrderingTest.TestResource("B");
        resources.add(jar2);
        OrderingTest.TestResource r2 = new OrderingTest.TestResource("B/web-fragment.xml");
        FragmentDescriptor f2 = new FragmentDescriptor(r2);
        f2._name = "B";
        metaData._webFragmentNameMap.put(f2._name, f2);
        metaData._webFragmentResourceMap.put(jar2, f2);
        f2._otherType = OtherType.Before;
        // ((RelativeOrdering)metaData._ordering).addBeforeOthers(r2);
        // C: after others
        OrderingTest.TestResource jar3 = new OrderingTest.TestResource("C");
        resources.add(jar3);
        OrderingTest.TestResource r3 = new OrderingTest.TestResource("C/web-fragment.xml");
        FragmentDescriptor f3 = new FragmentDescriptor(r3);
        f3._name = "C";
        metaData._webFragmentNameMap.put(f3._name, f3);
        metaData._webFragmentResourceMap.put(jar3, f3);
        f3._otherType = OtherType.After;
        // ((RelativeOrdering)metaData._ordering).addAfterOthers(r3);
        // D: no ordering
        OrderingTest.TestResource jar4 = new OrderingTest.TestResource("D");
        resources.add(jar4);
        OrderingTest.TestResource r4 = new OrderingTest.TestResource("D/web-fragment.xml");
        FragmentDescriptor f4 = new FragmentDescriptor(r4);
        f4._name = "D";
        metaData._webFragmentNameMap.put(f4._name, f4);
        metaData._webFragmentResourceMap.put(jar4, f4);
        f4._otherType = OtherType.None;
        // ((RelativeOrdering)metaData._ordering).addNoOthers(r4);
        // E: no ordering
        OrderingTest.TestResource jar5 = new OrderingTest.TestResource("E");
        resources.add(jar5);
        OrderingTest.TestResource r5 = new OrderingTest.TestResource("E/web-fragment.xml");
        FragmentDescriptor f5 = new FragmentDescriptor(r5);
        f5._name = "E";
        metaData._webFragmentNameMap.put(f5._name, f5);
        metaData._webFragmentResourceMap.put(jar5, f5);
        f5._otherType = OtherType.None;
        // ((RelativeOrdering)metaData._ordering).addNoOthers(r5);
        // F: before others, before B
        OrderingTest.TestResource jar6 = new OrderingTest.TestResource("F");
        resources.add(jar6);
        OrderingTest.TestResource r6 = new OrderingTest.TestResource("F/web-fragment.xml");
        FragmentDescriptor f6 = new FragmentDescriptor(r6);
        f6._name = "F";
        metaData._webFragmentNameMap.put(f6._name, f6);
        metaData._webFragmentResourceMap.put(jar6, f6);
        f6._otherType = OtherType.Before;
        // ((RelativeOrdering)metaData._ordering).addBeforeOthers(r6);
        f6._befores.add("B");
        // 
        // p.70 outcome: F, B, D, E, C, A
        // 
        String[] outcomes = new String[]{ "FBDECA" };
        List<Resource> orderedList = metaData._ordering.order(resources);
        String result = "";
        for (Resource r : orderedList)
            result += ((OrderingTest.TestResource) (r))._name;

        if (!(checkResult(result, outcomes)))
            Assertions.fail(("No outcome matched " + result));

    }

    @Test
    public void testRelativeOrdering1() throws Exception {
        List<Resource> resources = new ArrayList<Resource>();
        MetaData metaData = new MetaData();
        metaData._ordering = new RelativeOrdering(metaData);
        // Example from ServletSpec p.70-71
        // No name: after others, before C
        OrderingTest.TestResource jar1 = new OrderingTest.TestResource("plain");
        resources.add(jar1);
        OrderingTest.TestResource r1 = new OrderingTest.TestResource("plain/web-fragment.xml");
        FragmentDescriptor f1 = new FragmentDescriptor(r1);
        f1._name = (NAMELESS) + "1";
        metaData._webFragmentNameMap.put(f1._name, f1);
        metaData._webFragmentResourceMap.put(jar1, f1);
        f1._otherType = OtherType.After;
        // ((RelativeOrdering)metaData._ordering).addAfterOthers(f1);
        f1._befores.add("C");
        // B: before others
        OrderingTest.TestResource jar2 = new OrderingTest.TestResource("B");
        resources.add(jar2);
        OrderingTest.TestResource r2 = new OrderingTest.TestResource("B/web-fragment.xml");
        FragmentDescriptor f2 = new FragmentDescriptor(r2);
        f2._name = "B";
        metaData._webFragmentNameMap.put(f2._name, f2);
        metaData._webFragmentResourceMap.put(jar2, f2);
        f2._otherType = OtherType.Before;
        // ((RelativeOrdering)metaData._ordering).addBeforeOthers(f2);
        // C: no ordering
        OrderingTest.TestResource jar3 = new OrderingTest.TestResource("C");
        resources.add(jar3);
        OrderingTest.TestResource r3 = new OrderingTest.TestResource("C/web-fragment.xml");
        FragmentDescriptor f3 = new FragmentDescriptor(r3);
        f3._name = "C";
        metaData._webFragmentNameMap.put(f3._name, f3);
        metaData._webFragmentResourceMap.put(jar3, f3);
        // ((RelativeOrdering)metaData._ordering).addNoOthers(f3);
        f3._otherType = OtherType.None;
        // D: after others
        OrderingTest.TestResource jar4 = new OrderingTest.TestResource("D");
        resources.add(jar4);
        OrderingTest.TestResource r4 = new OrderingTest.TestResource("D/web-fragment.xml");
        FragmentDescriptor f4 = new FragmentDescriptor(r4);
        f4._name = "D";
        metaData._webFragmentNameMap.put(f4._name, f4);
        metaData._webFragmentResourceMap.put(jar4, f4);
        // ((RelativeOrdering)metaData._ordering).addAfterOthers(f4);
        f4._otherType = OtherType.After;
        // E: before others
        OrderingTest.TestResource jar5 = new OrderingTest.TestResource("E");
        resources.add(jar5);
        OrderingTest.TestResource r5 = new OrderingTest.TestResource("E/web-fragment.xml");
        FragmentDescriptor f5 = new FragmentDescriptor(r5);
        f5._name = "E";
        metaData._webFragmentNameMap.put(f5._name, f5);
        metaData._webFragmentResourceMap.put(jar5, f5);
        // ((RelativeOrdering)metaData._ordering).addBeforeOthers(f5);
        f5._otherType = OtherType.Before;
        // F: no ordering
        OrderingTest.TestResource jar6 = new OrderingTest.TestResource("F");
        resources.add(jar6);
        OrderingTest.TestResource r6 = new OrderingTest.TestResource("F/web-fragment.xml");
        FragmentDescriptor f6 = new FragmentDescriptor(r6);
        f6._name = "F";
        metaData._webFragmentNameMap.put(f6._name, f6);
        metaData._webFragmentResourceMap.put(jar6, f6);
        // ((RelativeOrdering)metaData._ordering).addNoOthers(f6);
        f6._otherType = OtherType.None;
        List<Resource> orderedList = metaData._ordering.order(resources);
        // p.70-71 Possible outcomes are:
        // B, E, F, noname, C, D
        // B, E, F, noname, D, C
        // E, B, F, noname, C, D
        // E, B, F, noname, D, C
        // E, B, F, D, noname, C
        // 
        String[] outcomes = new String[]{ "BEFplainCD", "BEFplainDC", "EBFplainCD", "EBFplainDC", "EBFDplainC" };
        String orderedNames = "";
        for (Resource r : orderedList)
            orderedNames += ((OrderingTest.TestResource) (r))._name;

        if (!(checkResult(orderedNames, outcomes)))
            Assertions.fail(("No outcome matched " + orderedNames));

    }

    @Test
    public void testRelativeOrdering2() throws Exception {
        List<Resource> resources = new ArrayList<Resource>();
        MetaData metaData = new MetaData();
        metaData._ordering = new RelativeOrdering(metaData);
        // Example from Spec p. 71-72
        // A: after B
        OrderingTest.TestResource jar1 = new OrderingTest.TestResource("A");
        resources.add(jar1);
        OrderingTest.TestResource r1 = new OrderingTest.TestResource("A/web-fragment.xml");
        FragmentDescriptor f1 = new FragmentDescriptor(r1);
        f1._name = "A";
        metaData._webFragmentNameMap.put(f1._name, f1);
        metaData._webFragmentResourceMap.put(jar1, f1);
        // ((RelativeOrdering)metaData._ordering).addNoOthers(f1);
        f1._otherType = OtherType.None;
        f1._afters.add("B");
        // B: no order
        OrderingTest.TestResource jar2 = new OrderingTest.TestResource("B");
        resources.add(jar2);
        OrderingTest.TestResource r2 = new OrderingTest.TestResource("B/web-fragment.xml");
        FragmentDescriptor f2 = new FragmentDescriptor(r2);
        f2._name = "B";
        metaData._webFragmentNameMap.put(f2._name, f2);
        metaData._webFragmentResourceMap.put(jar2, f2);
        // ((RelativeOrdering)metaData._ordering).addNoOthers(f2);
        f2._otherType = OtherType.None;
        // C: before others
        OrderingTest.TestResource jar3 = new OrderingTest.TestResource("C");
        resources.add(jar3);
        OrderingTest.TestResource r3 = new OrderingTest.TestResource("C/web-fragment.xml");
        FragmentDescriptor f3 = new FragmentDescriptor(r3);
        f3._name = "C";
        metaData._webFragmentNameMap.put(f3._name, f3);
        metaData._webFragmentResourceMap.put(jar3, f3);
        // ((RelativeOrdering)metaData._ordering).addBeforeOthers(f3);
        f3._otherType = OtherType.Before;
        // D: no order
        OrderingTest.TestResource jar4 = new OrderingTest.TestResource("D");
        resources.add(jar4);
        OrderingTest.TestResource r4 = new OrderingTest.TestResource("D/web-fragment.xml");
        FragmentDescriptor f4 = new FragmentDescriptor(r4);
        f4._name = "D";
        metaData._webFragmentNameMap.put(f4._name, f4);
        metaData._webFragmentResourceMap.put(jar4, f4);
        // ((RelativeOrdering)metaData._ordering).addNoOthers(f4);
        f4._otherType = OtherType.None;
        // 
        // p.71-72 possible outcomes are:
        // C,B,D,A
        // C,D,B,A
        // C,B,A,D
        // 
        String[] outcomes = new String[]{ "CBDA", "CDBA", "CBAD" };
        List<Resource> orderedList = metaData._ordering.order(resources);
        String result = "";
        for (Resource r : orderedList)
            result += ((OrderingTest.TestResource) (r))._name;

        if (!(checkResult(result, outcomes)))
            Assertions.fail(("No outcome matched " + result));

    }

    @Test
    public void testRelativeOrdering3() throws Exception {
        List<Resource> resources = new ArrayList<Resource>();
        MetaData metaData = new MetaData();
        metaData._ordering = new RelativeOrdering(metaData);
        // A: after others, before C
        OrderingTest.TestResource jar1 = new OrderingTest.TestResource("A");
        resources.add(jar1);
        OrderingTest.TestResource r1 = new OrderingTest.TestResource("A/web-fragment.xml");
        FragmentDescriptor f1 = new FragmentDescriptor(r1);
        f1._name = "A";
        metaData._webFragmentNameMap.put(f1._name, f1);
        metaData._webFragmentResourceMap.put(jar1, f1);
        // ((RelativeOrdering)metaData._ordering).addAfterOthers(f1);
        f1._otherType = OtherType.After;
        f1._befores.add("C");
        // B: before others, before C
        OrderingTest.TestResource jar2 = new OrderingTest.TestResource("B");
        resources.add(jar2);
        OrderingTest.TestResource r2 = new OrderingTest.TestResource("B/web-fragment.xml");
        FragmentDescriptor f2 = new FragmentDescriptor(r2);
        f2._name = "B";
        metaData._webFragmentNameMap.put(f2._name, f2);
        metaData._webFragmentResourceMap.put(jar2, f2);
        // ((RelativeOrdering)metaData._ordering).addBeforeOthers(f2);
        f2._otherType = OtherType.Before;
        f2._befores.add("C");
        // C: no ordering
        OrderingTest.TestResource jar3 = new OrderingTest.TestResource("C");
        resources.add(jar3);
        OrderingTest.TestResource r3 = new OrderingTest.TestResource("C/web-fragment.xml");
        FragmentDescriptor f3 = new FragmentDescriptor(r3);
        f3._name = "C";
        metaData._webFragmentNameMap.put(f3._name, f3);
        metaData._webFragmentResourceMap.put(jar3, f3);
        // ((RelativeOrdering)metaData._ordering).addNoOthers(f3);
        f3._otherType = OtherType.None;
        // result: BAC
        String[] outcomes = new String[]{ "BAC" };
        List<Resource> orderedList = metaData._ordering.order(resources);
        String result = "";
        for (Resource r : orderedList)
            result += ((OrderingTest.TestResource) (r))._name;

        if (!(checkResult(result, outcomes)))
            Assertions.fail(("No outcome matched " + result));

    }

    @Test
    public void testOrderFragments() throws Exception {
        final MetaData metadata = new MetaData();
        final Resource jarResource = new OrderingTest.TestResource("A");
        metadata.setOrdering(new RelativeOrdering(metadata));
        metadata.addWebInfJar(jarResource);
        metadata.orderFragments();
        Assertions.assertEquals(1, metadata.getOrderedWebInfJars().size());
        metadata.orderFragments();
        Assertions.assertEquals(1, metadata.getOrderedWebInfJars().size());
    }

    @Test
    public void testCircular1() throws Exception {
        // A: after B
        // B: after A
        List<Resource> resources = new ArrayList<Resource>();
        MetaData metaData = new MetaData();
        metaData._ordering = new RelativeOrdering(metaData);
        // A: after B
        OrderingTest.TestResource jar1 = new OrderingTest.TestResource("A");
        resources.add(jar1);
        OrderingTest.TestResource r1 = new OrderingTest.TestResource("A/web-fragment.xml");
        FragmentDescriptor f1 = new FragmentDescriptor(r1);
        f1._name = "A";
        metaData._webFragmentNameMap.put(f1._name, f1);
        metaData._webFragmentResourceMap.put(jar1, f1);
        // ((RelativeOrdering)metaData._ordering).addNoOthers(f1);
        f1._otherType = OtherType.None;
        f1._afters.add("B");
        // B: after A
        OrderingTest.TestResource jar2 = new OrderingTest.TestResource("B");
        resources.add(jar2);
        OrderingTest.TestResource r2 = new OrderingTest.TestResource("B/web-fragment.xml");
        FragmentDescriptor f2 = new FragmentDescriptor(r2);
        f2._name = "B";
        metaData._webFragmentNameMap.put(f2._name, f2);
        metaData._webFragmentResourceMap.put(jar2, f2);
        // ((RelativeOrdering)metaData._ordering).addNoOthers(f2);
        f2._otherType = OtherType.None;
        f2._afters.add("A");
        Assertions.assertThrows(IllegalStateException.class, () -> {
            metaData._ordering.order(resources);
            Assertions.fail("No circularity detected");
        });
    }

    @Test
    public void testInvalid1() throws Exception {
        List<Resource> resources = new ArrayList<Resource>();
        MetaData metaData = new MetaData();
        metaData._ordering = new RelativeOrdering(metaData);
        // A: after others, before C
        OrderingTest.TestResource jar1 = new OrderingTest.TestResource("A");
        resources.add(jar1);
        OrderingTest.TestResource r1 = new OrderingTest.TestResource("A/web-fragment.xml");
        FragmentDescriptor f1 = new FragmentDescriptor(r1);
        f1._name = "A";
        metaData._webFragmentNameMap.put(f1._name, f1);
        metaData._webFragmentResourceMap.put(jar1, f1);
        // ((RelativeOrdering)metaData._ordering).addAfterOthers(r1);
        f1._otherType = OtherType.After;
        f1._befores.add("C");
        // B: before others, after C
        OrderingTest.TestResource jar2 = new OrderingTest.TestResource("B");
        resources.add(jar2);
        OrderingTest.TestResource r2 = new OrderingTest.TestResource("B/web-fragment.xml");
        FragmentDescriptor f2 = new FragmentDescriptor(r2);
        f2._name = "B";
        metaData._webFragmentNameMap.put(f2._name, f2);
        metaData._webFragmentResourceMap.put(jar2, f2);
        // ((RelativeOrdering)metaData._ordering).addBeforeOthers(r2);
        f2._otherType = OtherType.Before;
        f2._afters.add("C");
        // C: no ordering
        OrderingTest.TestResource jar3 = new OrderingTest.TestResource("C");
        resources.add(jar3);
        OrderingTest.TestResource r3 = new OrderingTest.TestResource("C/web-fragment.xml");
        FragmentDescriptor f3 = new FragmentDescriptor(r3);
        f3._name = "C";
        metaData._webFragmentNameMap.put(f3._name, f3);
        metaData._webFragmentResourceMap.put(jar3, f3);
        // ((RelativeOrdering)metaData._ordering).addNoOthers(r3);
        f3._otherType = OtherType.None;
        Assertions.assertThrows(IllegalStateException.class, () -> {
            List<Resource> orderedList = metaData._ordering.order(resources);
            String result = "";
            for (Resource r : orderedList)
                result += ((OrderingTest.TestResource) (r))._name;

            System.err.println(("Invalid Result = " + result));
            Assertions.fail("A and B have an impossible relationship to C");
        });
    }

    @Test
    public void testAbsoluteOrdering1() throws Exception {
        // 
        // A,B,C,others
        // 
        List<Resource> resources = new ArrayList<Resource>();
        MetaData metaData = new MetaData();
        metaData._ordering = new AbsoluteOrdering(metaData);
        add("A");
        add("B");
        add("C");
        addOthers();
        OrderingTest.TestResource jar1 = new OrderingTest.TestResource("A");
        resources.add(jar1);
        OrderingTest.TestResource r1 = new OrderingTest.TestResource("A/web-fragment.xml");
        FragmentDescriptor f1 = new FragmentDescriptor(r1);
        f1._name = "A";
        metaData._webFragmentNameMap.put(f1._name, f1);
        metaData._webFragmentResourceMap.put(jar1, f1);
        OrderingTest.TestResource jar2 = new OrderingTest.TestResource("B");
        resources.add(jar2);
        OrderingTest.TestResource r2 = new OrderingTest.TestResource("B/web-fragment.xml");
        FragmentDescriptor f2 = new FragmentDescriptor(r2);
        f2._name = "B";
        metaData._webFragmentNameMap.put(f2._name, f2);
        metaData._webFragmentResourceMap.put(jar2, f2);
        OrderingTest.TestResource jar3 = new OrderingTest.TestResource("C");
        resources.add(jar3);
        OrderingTest.TestResource r3 = new OrderingTest.TestResource("C/web-fragment.xml");
        FragmentDescriptor f3 = new FragmentDescriptor(r3);
        f3._name = "C";
        metaData._webFragmentNameMap.put(f3._name, f3);
        metaData._webFragmentResourceMap.put(jar3, f3);
        OrderingTest.TestResource jar4 = new OrderingTest.TestResource("D");
        resources.add(jar4);
        OrderingTest.TestResource r4 = new OrderingTest.TestResource("D/web-fragment.xml");
        FragmentDescriptor f4 = new FragmentDescriptor(((Resource) (null)));
        f4._name = "D";
        metaData._webFragmentNameMap.put(f4._name, f4);
        metaData._webFragmentResourceMap.put(jar4, f4);
        OrderingTest.TestResource jar5 = new OrderingTest.TestResource("E");
        resources.add(jar5);
        OrderingTest.TestResource r5 = new OrderingTest.TestResource("E/web-fragment.xml");
        FragmentDescriptor f5 = new FragmentDescriptor(((Resource) (null)));
        f5._name = "E";
        metaData._webFragmentNameMap.put(f5._name, f5);
        metaData._webFragmentResourceMap.put(jar5, f5);
        OrderingTest.TestResource jar6 = new OrderingTest.TestResource("plain");
        resources.add(jar6);
        OrderingTest.TestResource r6 = new OrderingTest.TestResource("plain/web-fragment.xml");
        FragmentDescriptor f6 = new FragmentDescriptor(((Resource) (null)));
        f6._name = (NAMELESS) + "1";
        metaData._webFragmentNameMap.put(f6._name, f6);
        metaData._webFragmentResourceMap.put(jar6, f6);
        List<Resource> list = metaData._ordering.order(resources);
        String[] outcomes = new String[]{ "ABCDEplain" };
        String result = "";
        for (Resource r : list)
            result += ((OrderingTest.TestResource) (r))._name;

        if (!(checkResult(result, outcomes)))
            Assertions.fail(("No outcome matched " + result));

    }

    @Test
    public void testAbsoluteOrdering2() throws Exception {
        // C,B,A
        List<Resource> resources = new ArrayList<Resource>();
        MetaData metaData = new MetaData();
        metaData._ordering = new AbsoluteOrdering(metaData);
        add("C");
        add("B");
        add("A");
        OrderingTest.TestResource jar1 = new OrderingTest.TestResource("A");
        resources.add(jar1);
        OrderingTest.TestResource r1 = new OrderingTest.TestResource("A/web-fragment.xml");
        FragmentDescriptor f1 = new FragmentDescriptor(r1);
        f1._name = "A";
        metaData._webFragmentNameMap.put(f1._name, f1);
        metaData._webFragmentResourceMap.put(jar1, f1);
        OrderingTest.TestResource jar2 = new OrderingTest.TestResource("B");
        resources.add(jar2);
        OrderingTest.TestResource r2 = new OrderingTest.TestResource("B/web-fragment.xml");
        FragmentDescriptor f2 = new FragmentDescriptor(r2);
        f2._name = "B";
        metaData._webFragmentNameMap.put(f2._name, f2);
        metaData._webFragmentResourceMap.put(jar2, f2);
        OrderingTest.TestResource jar3 = new OrderingTest.TestResource("C");
        resources.add(jar3);
        OrderingTest.TestResource r3 = new OrderingTest.TestResource("C/web-fragment.xml");
        FragmentDescriptor f3 = new FragmentDescriptor(r3);
        f3._name = "C";
        metaData._webFragmentNameMap.put(f3._name, f3);
        metaData._webFragmentResourceMap.put(jar3, f3);
        OrderingTest.TestResource jar4 = new OrderingTest.TestResource("D");
        resources.add(jar4);
        OrderingTest.TestResource r4 = new OrderingTest.TestResource("D/web-fragment.xml");
        FragmentDescriptor f4 = new FragmentDescriptor(r4);
        f4._name = "D";
        metaData._webFragmentNameMap.put(f4._name, f4);
        metaData._webFragmentResourceMap.put(jar4, f4);
        OrderingTest.TestResource jar5 = new OrderingTest.TestResource("E");
        resources.add(jar5);
        OrderingTest.TestResource r5 = new OrderingTest.TestResource("E/web-fragment.xml");
        FragmentDescriptor f5 = new FragmentDescriptor(r5);
        f5._name = "E";
        metaData._webFragmentNameMap.put(f5._name, f5);
        metaData._webFragmentResourceMap.put(jar5, f5);
        OrderingTest.TestResource jar6 = new OrderingTest.TestResource("plain");
        resources.add(jar6);
        OrderingTest.TestResource r6 = new OrderingTest.TestResource("plain/web-fragment.xml");
        FragmentDescriptor f6 = new FragmentDescriptor(r6);
        f6._name = (NAMELESS) + "1";
        metaData._webFragmentNameMap.put(f6._name, f6);
        metaData._webFragmentResourceMap.put(jar6, f6);
        List<Resource> list = metaData._ordering.order(resources);
        String[] outcomes = new String[]{ "CBA" };
        String result = "";
        for (Resource r : list)
            result += ((OrderingTest.TestResource) (r))._name;

        if (!(checkResult(result, outcomes)))
            Assertions.fail(("No outcome matched " + result));

    }

    @Test
    public void testAbsoluteOrdering3() throws Exception {
        // empty <absolute-ordering>
        MetaData metaData = new MetaData();
        metaData._ordering = new AbsoluteOrdering(metaData);
        List<Resource> resources = new ArrayList<Resource>();
        resources.add(new OrderingTest.TestResource("A"));
        resources.add(new OrderingTest.TestResource("B"));
        List<Resource> list = metaData._ordering.order(resources);
        MatcherAssert.assertThat(list, Matchers.is(Matchers.empty()));
    }

    @Test
    public void testRelativeOrderingWithPlainJars() throws Exception {
        // B,A,C other jars with no fragments
        List<Resource> resources = new ArrayList<Resource>();
        MetaData metaData = new MetaData();
        metaData._ordering = new RelativeOrdering(metaData);
        // A: after others, before C
        OrderingTest.TestResource jar1 = new OrderingTest.TestResource("A");
        resources.add(jar1);
        OrderingTest.TestResource r1 = new OrderingTest.TestResource("A/web-fragment.xml");
        FragmentDescriptor f1 = new FragmentDescriptor(r1);
        f1._name = "A";
        metaData._webFragmentNameMap.put(f1._name, f1);
        metaData._webFragmentResourceMap.put(jar1, f1);
        // ((RelativeOrdering)metaData._ordering).addAfterOthers(f1);
        f1._otherType = OtherType.After;
        f1._befores.add("C");
        // B: before others, before C
        OrderingTest.TestResource jar2 = new OrderingTest.TestResource("B");
        resources.add(jar2);
        OrderingTest.TestResource r2 = new OrderingTest.TestResource("B/web-fragment.xml");
        FragmentDescriptor f2 = new FragmentDescriptor(r2);
        f2._name = "B";
        metaData._webFragmentNameMap.put(f2._name, f2);
        metaData._webFragmentResourceMap.put(jar2, f2);
        // ((RelativeOrdering)metaData._ordering).addBeforeOthers(f2);
        f2._otherType = OtherType.Before;
        f2._befores.add("C");
        // C: after A
        OrderingTest.TestResource jar3 = new OrderingTest.TestResource("C");
        resources.add(jar3);
        OrderingTest.TestResource r3 = new OrderingTest.TestResource("C/web-fragment.xml");
        FragmentDescriptor f3 = new FragmentDescriptor(r3);
        f3._name = "C";
        metaData._webFragmentNameMap.put(f3._name, f3);
        metaData._webFragmentResourceMap.put(jar3, f3);
        // ((RelativeOrdering)metaData._ordering).addNoOthers(f3);
        f3._otherType = OtherType.None;
        f3._afters.add("A");
        // No fragment jar 1
        OrderingTest.TestResource r4 = new OrderingTest.TestResource("plain1");
        resources.add(r4);
        // No fragment jar 2
        OrderingTest.TestResource r5 = new OrderingTest.TestResource("plain2");
        resources.add(r5);
        // result: BAC
        String[] outcomes = new String[]{ "Bplain1plain2AC" };
        List<Resource> orderedList = metaData._ordering.order(resources);
        String result = "";
        for (Resource r : orderedList)
            result += ((OrderingTest.TestResource) (r))._name;

        if (!(checkResult(result, outcomes)))
            Assertions.fail(("No outcome matched " + result));

    }

    @Test
    public void testRelativeOrderingWithPlainJars2() throws Exception {
        // web.xml has no ordering, jar A has fragment after others, jar B is plain, jar C is plain
        List<Resource> resources = new ArrayList<Resource>();
        MetaData metaData = new MetaData();
        metaData._ordering = new RelativeOrdering(metaData);
        // A has after others
        OrderingTest.TestResource jar1 = new OrderingTest.TestResource("A");
        resources.add(jar1);
        OrderingTest.TestResource r1 = new OrderingTest.TestResource("A/web-fragment.xml");
        FragmentDescriptor f1 = new FragmentDescriptor(r1);
        f1._name = "A";
        metaData._webFragmentNameMap.put(f1._name, f1);
        metaData._webFragmentResourceMap.put(jar1, f1);
        f1._otherType = OtherType.After;
        // No fragment jar B
        OrderingTest.TestResource r4 = new OrderingTest.TestResource("plainB");
        resources.add(r4);
        // No fragment jar C
        OrderingTest.TestResource r5 = new OrderingTest.TestResource("plainC");
        resources.add(r5);
        List<Resource> orderedList = metaData._ordering.order(resources);
        String[] outcomes = new String[]{ "plainBplainCA" };
        String result = "";
        for (Resource r : orderedList)
            result += ((OrderingTest.TestResource) (r))._name;

        if (!(checkResult(result, outcomes)))
            Assertions.fail(("No outcome matched " + result));

    }

    @Test
    public void testAbsoluteOrderingWithPlainJars() throws Exception {
        // 
        // A,B,C,others
        // 
        List<Resource> resources = new ArrayList<Resource>();
        MetaData metaData = new MetaData();
        metaData._ordering = new AbsoluteOrdering(metaData);
        add("A");
        add("B");
        add("C");
        addOthers();
        OrderingTest.TestResource jar1 = new OrderingTest.TestResource("A");
        resources.add(jar1);
        OrderingTest.TestResource r1 = new OrderingTest.TestResource("A/web-fragment.xml");
        FragmentDescriptor f1 = new FragmentDescriptor(r1);
        f1._name = "A";
        metaData._webFragmentNameMap.put(f1._name, f1);
        metaData._webFragmentResourceMap.put(jar1, f1);
        OrderingTest.TestResource jar2 = new OrderingTest.TestResource("B");
        resources.add(jar2);
        OrderingTest.TestResource r2 = new OrderingTest.TestResource("B/web-fragment.xml");
        FragmentDescriptor f2 = new FragmentDescriptor(r2);
        f2._name = "B";
        metaData._webFragmentNameMap.put(f2._name, f2);
        metaData._webFragmentResourceMap.put(jar2, f2);
        OrderingTest.TestResource jar3 = new OrderingTest.TestResource("C");
        resources.add(jar3);
        OrderingTest.TestResource r3 = new OrderingTest.TestResource("C/web-fragment.xml");
        FragmentDescriptor f3 = new FragmentDescriptor(r3);
        f3._name = "C";
        metaData._webFragmentNameMap.put(f3._name, f3);
        metaData._webFragmentResourceMap.put(jar3, f3);
        OrderingTest.TestResource jar4 = new OrderingTest.TestResource("D");
        resources.add(jar4);
        OrderingTest.TestResource r4 = new OrderingTest.TestResource("D/web-fragment.xml");
        FragmentDescriptor f4 = new FragmentDescriptor(((Resource) (null)));
        f4._name = "D";
        metaData._webFragmentNameMap.put(f4._name, f4);
        metaData._webFragmentResourceMap.put(jar4, f4);
        OrderingTest.TestResource jar5 = new OrderingTest.TestResource("E");
        resources.add(jar5);
        OrderingTest.TestResource r5 = new OrderingTest.TestResource("E/web-fragment.xml");
        FragmentDescriptor f5 = new FragmentDescriptor(((Resource) (null)));
        f5._name = "E";
        metaData._webFragmentNameMap.put(f5._name, f5);
        metaData._webFragmentResourceMap.put(jar5, f5);
        OrderingTest.TestResource jar6 = new OrderingTest.TestResource("plain");
        resources.add(jar6);
        OrderingTest.TestResource r6 = new OrderingTest.TestResource("plain/web-fragment.xml");
        FragmentDescriptor f6 = new FragmentDescriptor(((Resource) (null)));
        f6._name = (NAMELESS) + "1";
        metaData._webFragmentNameMap.put(f6._name, f6);
        metaData._webFragmentResourceMap.put(jar6, f6);
        // plain jar
        OrderingTest.TestResource r7 = new OrderingTest.TestResource("plain1");
        resources.add(r7);
        OrderingTest.TestResource r8 = new OrderingTest.TestResource("plain2");
        resources.add(r8);
        List<Resource> list = metaData._ordering.order(resources);
        String[] outcomes = new String[]{ "ABCDEplainplain1plain2" };
        String result = "";
        for (Resource r : list)
            result += ((OrderingTest.TestResource) (r))._name;

        if (!(checkResult(result, outcomes)))
            Assertions.fail(("No outcome matched " + result));

    }
}

