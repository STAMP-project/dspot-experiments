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
package org.eclipse.jetty.util.ajax;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.Test;


public class JSONCollectionConvertorTest {
    @Test
    public void testArrayList() throws Exception {
        List<String> list = new ArrayList<String>();
        Collections.addAll(list, "one", "two");
        testList(list);
    }

    @Test
    public void testLinkedList() throws Exception {
        List<String> list = new LinkedList<String>();
        Collections.addAll(list, "one", "two");
        testList(list);
    }

    @Test
    public void testCopyOnWriteArrayList() throws Exception {
        List<String> list = new CopyOnWriteArrayList<String>();
        Collections.addAll(list, "one", "two");
        testList(list);
    }

    @Test
    public void testHashSet() throws Exception {
        Set<String> set = new HashSet<String>();
        Collections.addAll(set, "one", "two", "three");
        testSet(set);
    }

    @Test
    public void testTreeSet() throws Exception {
        Set<String> set = new TreeSet<String>();
        Collections.addAll(set, "one", "two", "three");
        testSet(set);
    }
}

