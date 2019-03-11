/**
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.slf4j;


import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.helpers.BasicMarkerFactory;


/**
 * Unit test BasicMarker
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Joern Huxhorn
 */
public class BasicMarkerTest {
    static final String BLUE_STR = "BLUE";

    static final String RED_STR = "RED";

    static final String GREEN_STR = "GREEN";

    static final String COMP_STR = "COMP";

    static final String MULTI_COMP_STR = "MULTI_COMP";

    static final String PARENT_MARKER_STR = "PARENT_MARKER";

    static final String CHILD_MARKER_STR = "CHILD_MARKER";

    static final String NOT_CONTAINED_MARKER_STR = "NOT_CONTAINED";

    final IMarkerFactory factory;

    final Marker blue;

    final Marker red;

    final Marker green;

    final Marker comp;

    final Marker multiComp;

    short diff = Differentiator.getDiffentiator();

    public BasicMarkerTest() {
        factory = new BasicMarkerFactory();
        blue = factory.getMarker(BasicMarkerTest.BLUE_STR);
        red = factory.getMarker(BasicMarkerTest.RED_STR);
        green = factory.getMarker(BasicMarkerTest.GREEN_STR);
        comp = factory.getMarker(BasicMarkerTest.COMP_STR);
        comp.add(blue);
        multiComp = factory.getMarker(BasicMarkerTest.MULTI_COMP_STR);
        multiComp.add(green);
        multiComp.add(comp);
    }

    @Test
    public void testPrimitive() {
        Assert.assertEquals(BasicMarkerTest.BLUE_STR, blue.getName());
        Assert.assertTrue(blue.contains(blue));
        Marker blue2 = factory.getMarker(BasicMarkerTest.BLUE_STR);
        Assert.assertEquals(BasicMarkerTest.BLUE_STR, blue2.getName());
        Assert.assertEquals(blue, blue2);
        Assert.assertTrue(blue.contains(blue2));
        Assert.assertTrue(blue2.contains(blue));
    }

    @Test
    public void testPrimitiveByName() {
        Assert.assertTrue(blue.contains(BasicMarkerTest.BLUE_STR));
    }

    @Test
    public void testComposite() {
        Assert.assertTrue(comp.contains(comp));
        Assert.assertTrue(comp.contains(blue));
    }

    @Test
    public void testCompositeByName() {
        Assert.assertTrue(comp.contains(BasicMarkerTest.COMP_STR));
        Assert.assertTrue(comp.contains(BasicMarkerTest.BLUE_STR));
    }

    @Test
    public void testMultiComposite() {
        Assert.assertTrue(multiComp.contains(comp));
        Assert.assertTrue(multiComp.contains(blue));
        Assert.assertTrue(multiComp.contains(green));
        Assert.assertFalse(multiComp.contains(red));
    }

    @Test
    public void testMultiCompositeByName() {
        Assert.assertTrue(multiComp.contains(BasicMarkerTest.COMP_STR));
        Assert.assertTrue(multiComp.contains(BasicMarkerTest.BLUE_STR));
        Assert.assertTrue(multiComp.contains(BasicMarkerTest.GREEN_STR));
        Assert.assertFalse(multiComp.contains(BasicMarkerTest.RED_STR));
    }

    @Test
    public void testMultiAdd() {
        Marker parent = factory.getMarker(BasicMarkerTest.PARENT_MARKER_STR);
        Marker child = factory.getMarker(BasicMarkerTest.CHILD_MARKER_STR);
        for (int i = 0; i < 10; i++) {
            parent.add(child);
        }
        // check that the child was added once and only once
        Iterator<Marker> iterator = parent.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(BasicMarkerTest.CHILD_MARKER_STR, iterator.next().toString());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testAddRemove() {
        final String NEW_PREFIX = "NEW_";
        Marker parent = factory.getMarker((NEW_PREFIX + (BasicMarkerTest.PARENT_MARKER_STR)));
        Marker child = factory.getMarker((NEW_PREFIX + (BasicMarkerTest.CHILD_MARKER_STR)));
        Assert.assertFalse(parent.contains(child));
        Assert.assertFalse(parent.contains((NEW_PREFIX + (BasicMarkerTest.CHILD_MARKER_STR))));
        Assert.assertFalse(parent.remove(child));
        parent.add(child);
        Assert.assertTrue(parent.contains(child));
        Assert.assertTrue(parent.contains((NEW_PREFIX + (BasicMarkerTest.CHILD_MARKER_STR))));
        Assert.assertTrue(parent.remove(child));
        Assert.assertFalse(parent.contains(child));
        Assert.assertFalse(parent.contains((NEW_PREFIX + (BasicMarkerTest.CHILD_MARKER_STR))));
        Assert.assertFalse(parent.remove(child));
    }

    @Test
    public void testSelfRecursion() {
        final String diffPrefix = "NEW_" + (diff);
        final String PARENT_NAME = diffPrefix + (BasicMarkerTest.PARENT_MARKER_STR);
        final String NOT_CONTAINED_NAME = diffPrefix + (BasicMarkerTest.NOT_CONTAINED_MARKER_STR);
        Marker parent = factory.getMarker(PARENT_NAME);
        Marker notContained = factory.getMarker(NOT_CONTAINED_NAME);
        parent.add(parent);
        Assert.assertTrue(parent.contains(parent));
        Assert.assertTrue(parent.contains(PARENT_NAME));
        Assert.assertFalse(parent.contains(notContained));
        Assert.assertFalse(parent.contains(BasicMarkerTest.NOT_CONTAINED_MARKER_STR));
    }

    @Test
    public void testIndirectRecursion() {
        final String diffPrefix = "NEW_" + (diff);
        final String PARENT_NAME = diffPrefix + (BasicMarkerTest.PARENT_MARKER_STR);
        final String CHILD_NAME = diffPrefix + (BasicMarkerTest.CHILD_MARKER_STR);
        final String NOT_CONTAINED_NAME = diffPrefix + (BasicMarkerTest.NOT_CONTAINED_MARKER_STR);
        Marker parent = factory.getMarker(PARENT_NAME);
        Marker child = factory.getMarker(CHILD_NAME);
        Marker notContained = factory.getMarker(NOT_CONTAINED_NAME);
        parent.add(child);
        child.add(parent);
        Assert.assertTrue(parent.contains(parent));
        Assert.assertTrue(parent.contains(child));
        Assert.assertTrue(parent.contains(PARENT_NAME));
        Assert.assertTrue(parent.contains(CHILD_NAME));
        Assert.assertFalse(parent.contains(notContained));
        Assert.assertFalse(parent.contains(BasicMarkerTest.NOT_CONTAINED_MARKER_STR));
    }

    @Test
    public void testHomonyms() {
        final String diffPrefix = "homonym" + (diff);
        final String PARENT_NAME = diffPrefix + (BasicMarkerTest.PARENT_MARKER_STR);
        final String CHILD_NAME = diffPrefix + (BasicMarkerTest.CHILD_MARKER_STR);
        Marker parent = factory.getMarker(PARENT_NAME);
        Marker child = factory.getMarker(CHILD_NAME);
        parent.add(child);
        IMarkerFactory otherFactory = new BasicMarkerFactory();
        Marker otherParent = otherFactory.getMarker(PARENT_NAME);
        Marker otherChild = otherFactory.getMarker(CHILD_NAME);
        Assert.assertTrue(parent.contains(otherParent));
        Assert.assertTrue(parent.contains(otherChild));
        Assert.assertTrue(parent.remove(otherChild));
    }
}

