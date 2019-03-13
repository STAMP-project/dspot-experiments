/**
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.bind;


import TestGroup.PERFORMANCE;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.tests.Assume;
import org.springframework.util.StopWatch;


/**
 *
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 06.08.2003
 */
public class ServletRequestUtilsTests {
    @Test
    public void testIntParameter() throws ServletRequestBindingException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("param1", "5");
        request.addParameter("param2", "e");
        request.addParameter("paramEmpty", "");
        Assert.assertEquals(ServletRequestUtils.getIntParameter(request, "param1"), new Integer(5));
        Assert.assertEquals(ServletRequestUtils.getIntParameter(request, "param1", 6), 5);
        Assert.assertEquals(ServletRequestUtils.getRequiredIntParameter(request, "param1"), 5);
        Assert.assertEquals(ServletRequestUtils.getIntParameter(request, "param2", 6), 6);
        try {
            ServletRequestUtils.getRequiredIntParameter(request, "param2");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
        Assert.assertEquals(ServletRequestUtils.getIntParameter(request, "param3"), null);
        Assert.assertEquals(ServletRequestUtils.getIntParameter(request, "param3", 6), 6);
        try {
            ServletRequestUtils.getRequiredIntParameter(request, "param3");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
        try {
            ServletRequestUtils.getRequiredIntParameter(request, "paramEmpty");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
    }

    @Test
    public void testIntParameters() throws ServletRequestBindingException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("param", new String[]{ "1", "2", "3" });
        request.addParameter("param2", "1");
        request.addParameter("param2", "2");
        request.addParameter("param2", "bogus");
        int[] array = new int[]{ 1, 2, 3 };
        int[] values = ServletRequestUtils.getRequiredIntParameters(request, "param");
        Assert.assertEquals(3, values.length);
        for (int i = 0; i < (array.length); i++) {
            Assert.assertEquals(array[i], values[i]);
        }
        try {
            ServletRequestUtils.getRequiredIntParameters(request, "param2");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
    }

    @Test
    public void testLongParameter() throws ServletRequestBindingException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("param1", "5");
        request.addParameter("param2", "e");
        request.addParameter("paramEmpty", "");
        Assert.assertEquals(ServletRequestUtils.getLongParameter(request, "param1"), new Long(5L));
        Assert.assertEquals(ServletRequestUtils.getLongParameter(request, "param1", 6L), 5L);
        Assert.assertEquals(ServletRequestUtils.getRequiredIntParameter(request, "param1"), 5L);
        Assert.assertEquals(ServletRequestUtils.getLongParameter(request, "param2", 6L), 6L);
        try {
            ServletRequestUtils.getRequiredLongParameter(request, "param2");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
        Assert.assertEquals(ServletRequestUtils.getLongParameter(request, "param3"), null);
        Assert.assertEquals(ServletRequestUtils.getLongParameter(request, "param3", 6L), 6L);
        try {
            ServletRequestUtils.getRequiredLongParameter(request, "param3");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
        try {
            ServletRequestUtils.getRequiredLongParameter(request, "paramEmpty");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
    }

    @Test
    public void testLongParameters() throws ServletRequestBindingException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("param", new String[]{ "1", "2", "3" });
        request.setParameter("param2", "0");
        request.setParameter("param2", "1");
        request.addParameter("param2", "2");
        request.addParameter("param2", "bogus");
        long[] array = new long[]{ 1L, 2L, 3L };
        long[] values = ServletRequestUtils.getRequiredLongParameters(request, "param");
        Assert.assertEquals(3, values.length);
        for (int i = 0; i < (array.length); i++) {
            Assert.assertEquals(array[i], values[i]);
        }
        try {
            ServletRequestUtils.getRequiredLongParameters(request, "param2");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
        request.setParameter("param2", new String[]{ "1", "2" });
        values = ServletRequestUtils.getRequiredLongParameters(request, "param2");
        Assert.assertEquals(2, values.length);
        Assert.assertEquals(1, values[0]);
        Assert.assertEquals(2, values[1]);
        request.removeParameter("param2");
        try {
            ServletRequestUtils.getRequiredLongParameters(request, "param2");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
    }

    @Test
    public void testFloatParameter() throws ServletRequestBindingException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("param1", "5.5");
        request.addParameter("param2", "e");
        request.addParameter("paramEmpty", "");
        Assert.assertTrue(ServletRequestUtils.getFloatParameter(request, "param1").equals(new Float(5.5F)));
        Assert.assertTrue(((ServletRequestUtils.getFloatParameter(request, "param1", 6.5F)) == 5.5F));
        Assert.assertTrue(((ServletRequestUtils.getRequiredFloatParameter(request, "param1")) == 5.5F));
        Assert.assertTrue(((ServletRequestUtils.getFloatParameter(request, "param2", 6.5F)) == 6.5F));
        try {
            ServletRequestUtils.getRequiredFloatParameter(request, "param2");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
        Assert.assertTrue(((ServletRequestUtils.getFloatParameter(request, "param3")) == null));
        Assert.assertTrue(((ServletRequestUtils.getFloatParameter(request, "param3", 6.5F)) == 6.5F));
        try {
            ServletRequestUtils.getRequiredFloatParameter(request, "param3");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
        try {
            ServletRequestUtils.getRequiredFloatParameter(request, "paramEmpty");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
    }

    @Test
    public void testFloatParameters() throws ServletRequestBindingException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("param", new String[]{ "1.5", "2.5", "3" });
        request.addParameter("param2", "1.5");
        request.addParameter("param2", "2");
        request.addParameter("param2", "bogus");
        float[] array = new float[]{ 1.5F, 2.5F, 3 };
        float[] values = ServletRequestUtils.getRequiredFloatParameters(request, "param");
        Assert.assertEquals(3, values.length);
        for (int i = 0; i < (array.length); i++) {
            Assert.assertEquals(array[i], values[i], 0);
        }
        try {
            ServletRequestUtils.getRequiredFloatParameters(request, "param2");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
    }

    @Test
    public void testDoubleParameter() throws ServletRequestBindingException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("param1", "5.5");
        request.addParameter("param2", "e");
        request.addParameter("paramEmpty", "");
        Assert.assertTrue(ServletRequestUtils.getDoubleParameter(request, "param1").equals(new Double(5.5)));
        Assert.assertTrue(((ServletRequestUtils.getDoubleParameter(request, "param1", 6.5)) == 5.5));
        Assert.assertTrue(((ServletRequestUtils.getRequiredDoubleParameter(request, "param1")) == 5.5));
        Assert.assertTrue(((ServletRequestUtils.getDoubleParameter(request, "param2", 6.5)) == 6.5));
        try {
            ServletRequestUtils.getRequiredDoubleParameter(request, "param2");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
        Assert.assertTrue(((ServletRequestUtils.getDoubleParameter(request, "param3")) == null));
        Assert.assertTrue(((ServletRequestUtils.getDoubleParameter(request, "param3", 6.5)) == 6.5));
        try {
            ServletRequestUtils.getRequiredDoubleParameter(request, "param3");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
        try {
            ServletRequestUtils.getRequiredDoubleParameter(request, "paramEmpty");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
    }

    @Test
    public void testDoubleParameters() throws ServletRequestBindingException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("param", new String[]{ "1.5", "2.5", "3" });
        request.addParameter("param2", "1.5");
        request.addParameter("param2", "2");
        request.addParameter("param2", "bogus");
        double[] array = new double[]{ 1.5, 2.5, 3 };
        double[] values = ServletRequestUtils.getRequiredDoubleParameters(request, "param");
        Assert.assertEquals(3, values.length);
        for (int i = 0; i < (array.length); i++) {
            Assert.assertEquals(array[i], values[i], 0);
        }
        try {
            ServletRequestUtils.getRequiredDoubleParameters(request, "param2");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
    }

    @Test
    public void testBooleanParameter() throws ServletRequestBindingException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("param1", "true");
        request.addParameter("param2", "e");
        request.addParameter("param4", "yes");
        request.addParameter("param5", "1");
        request.addParameter("paramEmpty", "");
        Assert.assertTrue(ServletRequestUtils.getBooleanParameter(request, "param1").equals(Boolean.TRUE));
        Assert.assertTrue(ServletRequestUtils.getBooleanParameter(request, "param1", false));
        Assert.assertTrue(ServletRequestUtils.getRequiredBooleanParameter(request, "param1"));
        Assert.assertFalse(ServletRequestUtils.getBooleanParameter(request, "param2", true));
        Assert.assertFalse(ServletRequestUtils.getRequiredBooleanParameter(request, "param2"));
        Assert.assertTrue(((ServletRequestUtils.getBooleanParameter(request, "param3")) == null));
        Assert.assertTrue(ServletRequestUtils.getBooleanParameter(request, "param3", true));
        try {
            ServletRequestUtils.getRequiredBooleanParameter(request, "param3");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
        Assert.assertTrue(ServletRequestUtils.getBooleanParameter(request, "param4", false));
        Assert.assertTrue(ServletRequestUtils.getRequiredBooleanParameter(request, "param4"));
        Assert.assertTrue(ServletRequestUtils.getBooleanParameter(request, "param5", false));
        Assert.assertTrue(ServletRequestUtils.getRequiredBooleanParameter(request, "param5"));
        Assert.assertFalse(ServletRequestUtils.getRequiredBooleanParameter(request, "paramEmpty"));
    }

    @Test
    public void testBooleanParameters() throws ServletRequestBindingException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("param", new String[]{ "true", "yes", "off", "1", "bogus" });
        request.addParameter("param2", "false");
        request.addParameter("param2", "true");
        request.addParameter("param2", "");
        boolean[] array = new boolean[]{ true, true, false, true, false };
        boolean[] values = ServletRequestUtils.getRequiredBooleanParameters(request, "param");
        Assert.assertEquals(array.length, values.length);
        for (int i = 0; i < (array.length); i++) {
            Assert.assertEquals(array[i], values[i]);
        }
        array = new boolean[]{ false, true, false };
        values = ServletRequestUtils.getRequiredBooleanParameters(request, "param2");
        Assert.assertEquals(array.length, values.length);
        for (int i = 0; i < (array.length); i++) {
            Assert.assertEquals(array[i], values[i]);
        }
    }

    @Test
    public void testStringParameter() throws ServletRequestBindingException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("param1", "str");
        request.addParameter("paramEmpty", "");
        Assert.assertEquals("str", ServletRequestUtils.getStringParameter(request, "param1"));
        Assert.assertEquals("str", ServletRequestUtils.getStringParameter(request, "param1", "string"));
        Assert.assertEquals("str", ServletRequestUtils.getRequiredStringParameter(request, "param1"));
        Assert.assertEquals(null, ServletRequestUtils.getStringParameter(request, "param3"));
        Assert.assertEquals("string", ServletRequestUtils.getStringParameter(request, "param3", "string"));
        Assert.assertNull(ServletRequestUtils.getStringParameter(request, "param3", null));
        try {
            ServletRequestUtils.getRequiredStringParameter(request, "param3");
            Assert.fail("Should have thrown ServletRequestBindingException");
        } catch (ServletRequestBindingException ex) {
            // expected
        }
        Assert.assertEquals("", ServletRequestUtils.getStringParameter(request, "paramEmpty"));
        Assert.assertEquals("", ServletRequestUtils.getRequiredStringParameter(request, "paramEmpty"));
    }

    @Test
    public void testGetIntParameterWithDefaultValueHandlingIsFastEnough() {
        Assume.group(PERFORMANCE);
        MockHttpServletRequest request = new MockHttpServletRequest();
        StopWatch sw = new StopWatch();
        sw.start();
        for (int i = 0; i < 1000000; i++) {
            ServletRequestUtils.getIntParameter(request, "nonExistingParam", 0);
        }
        sw.stop();
        System.out.println(sw.getTotalTimeMillis());
        Assert.assertTrue(("getStringParameter took too long: " + (sw.getTotalTimeMillis())), ((sw.getTotalTimeMillis()) < 250));
    }

    @Test
    public void testGetLongParameterWithDefaultValueHandlingIsFastEnough() {
        Assume.group(PERFORMANCE);
        MockHttpServletRequest request = new MockHttpServletRequest();
        StopWatch sw = new StopWatch();
        sw.start();
        for (int i = 0; i < 1000000; i++) {
            ServletRequestUtils.getLongParameter(request, "nonExistingParam", 0);
        }
        sw.stop();
        System.out.println(sw.getTotalTimeMillis());
        Assert.assertTrue(("getStringParameter took too long: " + (sw.getTotalTimeMillis())), ((sw.getTotalTimeMillis()) < 250));
    }

    @Test
    public void testGetFloatParameterWithDefaultValueHandlingIsFastEnough() {
        Assume.group(PERFORMANCE);
        MockHttpServletRequest request = new MockHttpServletRequest();
        StopWatch sw = new StopWatch();
        sw.start();
        for (int i = 0; i < 1000000; i++) {
            ServletRequestUtils.getFloatParameter(request, "nonExistingParam", 0.0F);
        }
        sw.stop();
        System.out.println(sw.getTotalTimeMillis());
        Assert.assertTrue(("getStringParameter took too long: " + (sw.getTotalTimeMillis())), ((sw.getTotalTimeMillis()) < 250));
    }

    @Test
    public void testGetDoubleParameterWithDefaultValueHandlingIsFastEnough() {
        Assume.group(PERFORMANCE);
        MockHttpServletRequest request = new MockHttpServletRequest();
        StopWatch sw = new StopWatch();
        sw.start();
        for (int i = 0; i < 1000000; i++) {
            ServletRequestUtils.getDoubleParameter(request, "nonExistingParam", 0.0);
        }
        sw.stop();
        System.out.println(sw.getTotalTimeMillis());
        Assert.assertTrue(("getStringParameter took too long: " + (sw.getTotalTimeMillis())), ((sw.getTotalTimeMillis()) < 250));
    }

    @Test
    public void testGetBooleanParameterWithDefaultValueHandlingIsFastEnough() {
        Assume.group(PERFORMANCE);
        MockHttpServletRequest request = new MockHttpServletRequest();
        StopWatch sw = new StopWatch();
        sw.start();
        for (int i = 0; i < 1000000; i++) {
            ServletRequestUtils.getBooleanParameter(request, "nonExistingParam", false);
        }
        sw.stop();
        System.out.println(sw.getTotalTimeMillis());
        Assert.assertTrue(("getStringParameter took too long: " + (sw.getTotalTimeMillis())), ((sw.getTotalTimeMillis()) < 250));
    }

    @Test
    public void testGetStringParameterWithDefaultValueHandlingIsFastEnough() {
        Assume.group(PERFORMANCE);
        MockHttpServletRequest request = new MockHttpServletRequest();
        StopWatch sw = new StopWatch();
        sw.start();
        for (int i = 0; i < 1000000; i++) {
            ServletRequestUtils.getStringParameter(request, "nonExistingParam", "defaultValue");
        }
        sw.stop();
        System.out.println(sw.getTotalTimeMillis());
        Assert.assertTrue(("getStringParameter took too long: " + (sw.getTotalTimeMillis())), ((sw.getTotalTimeMillis()) < 250));
    }
}

