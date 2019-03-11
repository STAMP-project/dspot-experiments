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
package org.apache.activemq.util;


import junit.framework.TestCase;


/**
 *
 *
 * @author chirino
 */
public class LinkedNodeTest extends TestCase {
    static class IntLinkedNode extends LinkedNode {
        public final int v;

        public IntLinkedNode(int v) {
            this.v = v;
        }

        @Override
        public String toString() {
            return "" + (v);
        }
    }

    LinkedNodeTest.IntLinkedNode i1 = new LinkedNodeTest.IntLinkedNode(1);

    LinkedNodeTest.IntLinkedNode i2 = new LinkedNodeTest.IntLinkedNode(2);

    LinkedNodeTest.IntLinkedNode i3 = new LinkedNodeTest.IntLinkedNode(3);

    LinkedNodeTest.IntLinkedNode i4 = new LinkedNodeTest.IntLinkedNode(4);

    LinkedNodeTest.IntLinkedNode i5 = new LinkedNodeTest.IntLinkedNode(5);

    LinkedNodeTest.IntLinkedNode i6 = new LinkedNodeTest.IntLinkedNode(6);

    public void testLinkAfter() {
        i1.linkAfter(linkAfter(i3));
        // Order should be 1,2,3
        TestCase.assertTrue(((getNext()) == (i2)));
        TestCase.assertTrue(((getNext()) == (i3)));
        TestCase.assertNull(getNext());
        TestCase.assertTrue(((getPrevious()) == (i2)));
        TestCase.assertTrue(((getPrevious()) == (i1)));
        TestCase.assertNull(getPrevious());
        TestCase.assertTrue(isHeadNode());
        TestCase.assertFalse(isTailNode());
        TestCase.assertFalse(isHeadNode());
        TestCase.assertFalse(isTailNode());
        TestCase.assertTrue(isTailNode());
        TestCase.assertFalse(isHeadNode());
        i1.linkAfter(linkAfter(i5));
        // Order should be 1,4,5,2,3
        TestCase.assertTrue(((getNext()) == (i4)));
        TestCase.assertTrue(((getNext()) == (i5)));
        TestCase.assertTrue(((getNext()) == (i2)));
        TestCase.assertTrue(((getNext()) == (i3)));
        TestCase.assertNull(getNext());
        TestCase.assertTrue(((getPrevious()) == (i2)));
        TestCase.assertTrue(((getPrevious()) == (i5)));
        TestCase.assertTrue(((getPrevious()) == (i4)));
        TestCase.assertTrue(((getPrevious()) == (i1)));
        TestCase.assertNull(getPrevious());
        TestCase.assertTrue(isHeadNode());
        TestCase.assertFalse(isTailNode());
        TestCase.assertFalse(isHeadNode());
        TestCase.assertFalse(isTailNode());
        TestCase.assertFalse(isHeadNode());
        TestCase.assertFalse(isTailNode());
        TestCase.assertFalse(isHeadNode());
        TestCase.assertFalse(isTailNode());
        TestCase.assertTrue(isTailNode());
        TestCase.assertFalse(isHeadNode());
    }

    public void testLinkBefore() {
        i3.linkBefore(linkBefore(i1));
        TestCase.assertTrue(((getNext()) == (i2)));
        TestCase.assertTrue(((getNext()) == (i3)));
        TestCase.assertNull(getNext());
        TestCase.assertTrue(((getPrevious()) == (i2)));
        TestCase.assertTrue(((getPrevious()) == (i1)));
        TestCase.assertNull(getPrevious());
        TestCase.assertTrue(isHeadNode());
        TestCase.assertFalse(isTailNode());
        TestCase.assertFalse(isHeadNode());
        TestCase.assertFalse(isTailNode());
        TestCase.assertTrue(isTailNode());
        TestCase.assertFalse(isHeadNode());
        i2.linkBefore(linkBefore(i4));
        // Order should be 1,4,5,2,3
        TestCase.assertTrue(((getNext()) == (i4)));
        TestCase.assertTrue(((getNext()) == (i5)));
        TestCase.assertTrue(((getNext()) == (i2)));
        TestCase.assertTrue(((getNext()) == (i3)));
        TestCase.assertNull(getNext());
        TestCase.assertTrue(((getPrevious()) == (i2)));
        TestCase.assertTrue(((getPrevious()) == (i5)));
        TestCase.assertTrue(((getPrevious()) == (i4)));
        TestCase.assertTrue(((getPrevious()) == (i1)));
        TestCase.assertNull(getPrevious());
        TestCase.assertTrue(isHeadNode());
        TestCase.assertFalse(isTailNode());
        TestCase.assertFalse(isHeadNode());
        TestCase.assertFalse(isTailNode());
        TestCase.assertFalse(isHeadNode());
        TestCase.assertFalse(isTailNode());
        TestCase.assertFalse(isHeadNode());
        TestCase.assertFalse(isTailNode());
        TestCase.assertTrue(isTailNode());
        TestCase.assertFalse(isHeadNode());
    }

    public void testUnlink() {
        i1.linkAfter(linkAfter(i3));
        linkAfter(i4);
        linkBefore(i5);
        linkAfter(i6);
        // Order should be 5,1,6,2,3,4
        unlink();
        unlink();
        unlink();
        // Order should be 1,2,3
        TestCase.assertTrue(((getNext()) == (i2)));
        TestCase.assertTrue(((getNext()) == (i3)));
        TestCase.assertNull(getNext());
        TestCase.assertTrue(((getPrevious()) == (i2)));
        TestCase.assertTrue(((getPrevious()) == (i1)));
        TestCase.assertNull(getPrevious());
        TestCase.assertTrue(isHeadNode());
        TestCase.assertFalse(isTailNode());
        TestCase.assertFalse(isHeadNode());
        TestCase.assertFalse(isTailNode());
        TestCase.assertTrue(isTailNode());
        TestCase.assertFalse(isHeadNode());
    }
}

