package com.tinkerpop.blueprints;


import junit.framework.TestCase;


/**
 *
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class ParameterTest extends TestCase {
    public void testEquality() {
        Parameter<String, Long> a = new Parameter<String, Long>("blah", 7L);
        Parameter<String, Long> b = new Parameter<String, Long>("blah", 7L);
        TestCase.assertEquals(a, a);
        TestCase.assertEquals(b, b);
        TestCase.assertEquals(a, b);
        Parameter<String, Long> c = new Parameter<String, Long>("blah", 6L);
        TestCase.assertNotSame(a, c);
        TestCase.assertNotSame(b, c);
        Parameter<String, Long> d = new Parameter<String, Long>("boop", 7L);
        TestCase.assertNotSame(a, d);
        TestCase.assertNotSame(b, d);
        TestCase.assertNotSame(c, d);
    }
}

