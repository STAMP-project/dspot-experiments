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
package org.eclipse.jetty.servlet;


import Source.Origin;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.http.pathmap.MappedResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static Source.EMBEDDED;
import static Source.JAVAX_API;


public class ServletHandlerTest {
    FilterHolder fh1 = new FilterHolder(new Source(Origin.DESCRIPTOR, "foo.xml"));

    FilterMapping fm1 = new FilterMapping();

    FilterHolder fh2 = new FilterHolder(new Source(Origin.DESCRIPTOR, "foo.xml"));

    FilterMapping fm2 = new FilterMapping();

    FilterHolder fh3 = new FilterHolder(JAVAX_API);

    FilterMapping fm3 = new FilterMapping();

    FilterHolder fh4 = new FilterHolder(JAVAX_API);

    FilterMapping fm4 = new FilterMapping();

    FilterHolder fh5 = new FilterHolder(JAVAX_API);

    FilterMapping fm5 = new FilterMapping();

    ServletHolder sh1 = new ServletHolder(new Source(Origin.DESCRIPTOR, "foo.xml"));

    ServletMapping sm1 = new ServletMapping();

    ServletHolder sh2 = new ServletHolder(new Source(Origin.DESCRIPTOR, "foo.xml"));

    ServletMapping sm2 = new ServletMapping();

    ServletHolder sh3 = new ServletHolder(new Source(Origin.DESCRIPTOR, "foo.xml"));

    ServletMapping sm3 = new ServletMapping();

    @Test
    public void testAddFilterIgnoresDuplicates() throws Exception {
        ServletHandler handler = new ServletHandler();
        FilterHolder h = new FilterHolder();
        h.setName("x");
        handler.addFilter(h);
        FilterHolder[] holders = handler.getFilters();
        Assertions.assertNotNull(holders);
        Assertions.assertTrue(((holders[0]) == h));
        handler.addFilter(h);
        holders = handler.getFilters();
        Assertions.assertNotNull(holders);
        Assertions.assertTrue(((holders.length) == 1));
        Assertions.assertTrue(((holders[0]) == h));
        FilterHolder h2 = new FilterHolder();
        h2.setName("x");// not allowed by servlet spec, just here to test object equality

        handler.addFilter(h2);
        holders = handler.getFilters();
        Assertions.assertNotNull(holders);
        Assertions.assertTrue(((holders.length) == 2));
        Assertions.assertTrue(((holders[1]) == h2));
    }

    @Test
    public void testAddFilterIgnoresDuplicates2() throws Exception {
        ServletHandler handler = new ServletHandler();
        FilterHolder h = new FilterHolder();
        h.setName("x");
        FilterMapping m = new FilterMapping();
        m.setPathSpec("/*");
        m.setFilterHolder(h);
        handler.addFilter(h, m);
        FilterHolder[] holders = handler.getFilters();
        Assertions.assertNotNull(holders);
        Assertions.assertTrue(((holders[0]) == h));
        FilterMapping m2 = new FilterMapping();
        m2.setPathSpec("/*");
        m2.setFilterHolder(h);
        handler.addFilter(h, m2);
        holders = handler.getFilters();
        Assertions.assertNotNull(holders);
        Assertions.assertTrue(((holders.length) == 1));
        Assertions.assertTrue(((holders[0]) == h));
        FilterHolder h2 = new FilterHolder();
        h2.setName("x");// not allowed by servlet spec, just here to test object equality

        FilterMapping m3 = new FilterMapping();
        m3.setPathSpec("/*");
        m3.setFilterHolder(h);
        handler.addFilter(h2, m3);
        holders = handler.getFilters();
        Assertions.assertNotNull(holders);
        Assertions.assertTrue(((holders.length) == 2));
        Assertions.assertTrue(((holders[1]) == h2));
    }

    @Test
    public void testAddFilterWithMappingIgnoresDuplicateFilters() throws Exception {
        ServletHandler handler = new ServletHandler();
        FilterHolder h = new FilterHolder();
        h.setName("x");
        handler.addFilterWithMapping(h, "/*", 0);
        FilterHolder[] holders = handler.getFilters();
        Assertions.assertNotNull(holders);
        Assertions.assertTrue(((holders[0]) == h));
        handler.addFilterWithMapping(h, "/*", 1);
        holders = handler.getFilters();
        Assertions.assertNotNull(holders);
        Assertions.assertTrue(((holders.length) == 1));
        Assertions.assertTrue(((holders[0]) == h));
        FilterHolder h2 = new FilterHolder();
        h2.setName("x");// not allowed by servlet spec, just here to test object equality

        handler.addFilterWithMapping(h2, "/*", 0);
        holders = handler.getFilters();
        Assertions.assertNotNull(holders);
        Assertions.assertTrue(((holders.length) == 2));
        Assertions.assertTrue(((holders[1]) == h2));
    }

    @Test
    public void testAddFilterWithMappingIngoresDuplicateFilters2() throws Exception {
        ServletHandler handler = new ServletHandler();
        FilterHolder h = new FilterHolder();
        h.setName("x");
        handler.addFilterWithMapping(h, "/*", EnumSet.allOf(DispatcherType.class));
        FilterHolder[] holders = handler.getFilters();
        Assertions.assertNotNull(holders);
        Assertions.assertTrue(((holders[0]) == h));
        handler.addFilterWithMapping(h, "/x", EnumSet.allOf(DispatcherType.class));
        holders = handler.getFilters();
        Assertions.assertNotNull(holders);
        Assertions.assertTrue(((holders.length) == 1));
        Assertions.assertTrue(((holders[0]) == h));
        FilterHolder h2 = new FilterHolder();
        h2.setName("x");// not allowed by servlet spec, just here to test object equality

        handler.addFilterWithMapping(h2, "/*", EnumSet.allOf(DispatcherType.class));
        holders = handler.getFilters();
        Assertions.assertNotNull(holders);
        Assertions.assertTrue(((holders.length) == 2));
        Assertions.assertTrue(((holders[1]) == h2));
    }

    @Test
    public void testDuplicateMappingsForbidden() throws Exception {
        ServletHandler handler = new ServletHandler();
        handler.setAllowDuplicateMappings(false);
        handler.addServlet(sh1);
        handler.addServlet(sh2);
        handler.updateNameMappings();
        handler.addServletMapping(sm1);
        handler.addServletMapping(sm2);
        try {
            handler.updateMappings();
        } catch (IllegalStateException e) {
            // expected error
        }
    }

    @Test
    public void testDuplicateMappingsWithDefaults() throws Exception {
        ServletHandler handler = new ServletHandler();
        handler.setAllowDuplicateMappings(false);
        handler.addServlet(sh1);
        handler.addServlet(sh3);
        handler.updateNameMappings();
        handler.addServletMapping(sm3);
        handler.addServletMapping(sm1);
        handler.updateMappings();
        MappedResource<ServletHolder> entry = handler.getMappedServlet("/foo/*");
        Assertions.assertNotNull(entry);
        Assertions.assertEquals("s1", entry.getResource().getName());
    }

    @Test
    public void testDuplicateMappingsSameServlet() throws Exception {
        ServletHolder sh4 = new ServletHolder();
        sh4.setName("s1");
        ServletMapping sm4 = new ServletMapping();
        sm4.setPathSpec("/foo/*");
        sm4.setServletName("s1");
        ServletHandler handler = new ServletHandler();
        handler.setAllowDuplicateMappings(true);
        handler.addServlet(sh1);
        handler.addServlet(sh4);
        handler.updateNameMappings();
        handler.addServletMapping(sm1);
        handler.addServletMapping(sm4);
        handler.updateMappings();
    }

    @Test
    public void testDuplicateMappingsAllowed() throws Exception {
        ServletHandler handler = new ServletHandler();
        handler.setAllowDuplicateMappings(true);
        handler.addServlet(sh1);
        handler.addServlet(sh2);
        handler.updateNameMappings();
        handler.addServletMapping(sm1);
        handler.addServletMapping(sm2);
        handler.updateMappings();
        MappedResource<ServletHolder> entry = handler.getMappedServlet("/foo/*");
        Assertions.assertNotNull(entry);
        Assertions.assertEquals("s2", entry.getResource().getName());
    }

    @Test
    public void testAllNonProgrammaticFilterMappings() throws Exception {
        ServletHandler handler = new ServletHandler();
        handler.addFilter(fh1);
        handler.addFilter(fh2);
        // add some ordinary filter mappings
        handler.addFilterMapping(fm1);
        handler.addFilterMapping(fm2);
        FilterMapping[] mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertTrue(((fm1) == (mappings[0])));
        Assertions.assertTrue(((fm2) == (mappings[1])));
        // add another ordinary mapping
        FilterHolder of1 = new FilterHolder(new Source(Origin.DESCRIPTOR, "foo.xml"));
        FilterMapping ofm1 = new FilterMapping();
        ofm1.setFilterHolder(of1);
        ofm1.setPathSpec("/*");
        handler.addFilter(of1);
        handler.addFilterMapping(ofm1);
        mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertTrue(((fm1) == (mappings[0])));
        Assertions.assertTrue(((fm2) == (mappings[1])));
        Assertions.assertTrue((ofm1 == (mappings[2])));
    }

    @Test
    public void testAllBeforeFilterMappings() throws Exception {
        ServletHandler handler = new ServletHandler();
        // do equivalent of FilterRegistration.addMappingForUrlPatterns(isMatchAfter=false)
        handler.addFilter(fh4);
        handler.prependFilterMapping(fm4);
        FilterMapping[] mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(1, mappings.length);
        // add another with isMatchAfter=false
        handler.addFilter(fh5);
        handler.prependFilterMapping(fm5);
        mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(2, mappings.length);
        Assertions.assertTrue(((fm4) == (mappings[0])));
        Assertions.assertTrue(((fm5) == (mappings[1])));
    }

    @Test
    public void testAllAfterFilterMappings() throws Exception {
        ServletHandler handler = new ServletHandler();
        // do equivalent of FilterRegistration.addMappingForUrlPatterns(isMatchAfter=true)
        handler.addFilter(fh4);
        handler.addFilterMapping(fm4);
        FilterMapping[] mappings = handler.getFilterMappings();
        Assertions.assertEquals(1, mappings.length);
        Assertions.assertTrue(((fm4) == (mappings[0])));
        // do equivalent of FilterRegistration.addMappingForUrlPatterns(isMatchAfter=true)
        handler.addFilter(fh5);
        handler.addFilterMapping(fm5);
        mappings = handler.getFilterMappings();
        Assertions.assertEquals(2, mappings.length);
        Assertions.assertTrue(((fm4) == (mappings[0])));
        Assertions.assertTrue(((fm5) == (mappings[1])));
    }

    @Test
    public void testMatchAfterAndBefore() throws Exception {
        ServletHandler handler = new ServletHandler();
        // add a programmatic one, isMatchAfter=true
        handler.addFilter(fh3);
        handler.addFilterMapping(fm3);
        FilterMapping[] mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(1, mappings.length);
        Assertions.assertTrue(((fm3) == (mappings[0])));
        // add a programmatic one, isMatchAfter=false
        handler.addFilter(fh4);
        handler.prependFilterMapping(fm4);
        mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(2, mappings.length);
        Assertions.assertTrue(((fm4) == (mappings[0])));
        Assertions.assertTrue(((fm3) == (mappings[1])));
    }

    @Test
    public void testMatchBeforeAndAfter() throws Exception {
        ServletHandler handler = new ServletHandler();
        // add a programmatic one, isMatchAfter=false
        handler.addFilter(fh3);
        handler.prependFilterMapping(fm3);
        FilterMapping[] mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(1, mappings.length);
        Assertions.assertTrue(((fm3) == (mappings[0])));
        // add a programmatic one, isMatchAfter=true
        handler.addFilter(fh4);
        handler.addFilterMapping(fm4);
        mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(2, mappings.length);
        Assertions.assertTrue(((fm3) == (mappings[0])));
        Assertions.assertTrue(((fm4) == (mappings[1])));
    }

    @Test
    public void testExistingFilterMappings() throws Exception {
        ServletHandler handler = new ServletHandler();
        handler.addFilter(fh1);
        handler.addFilter(fh2);
        // add some ordinary filter mappings first
        handler.addFilterMapping(fm1);
        handler.addFilterMapping(fm2);
        FilterMapping[] mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertTrue(((fm1) == (mappings[0])));
        Assertions.assertTrue(((fm2) == (mappings[1])));
        // do equivalent of FilterRegistration.addMappingForUrlPatterns(isMatchAfter=false)
        handler.addFilter(fh4);
        handler.prependFilterMapping(fm4);
        mappings = handler.getFilterMappings();
        Assertions.assertEquals(3, mappings.length);
        Assertions.assertTrue(((fm4) == (mappings[0])));
        // do equivalent of FilterRegistration.addMappingForUrlPatterns(isMatchAfter=true)
        handler.addFilter(fh5);
        handler.addFilterMapping(fm5);
        mappings = handler.getFilterMappings();
        Assertions.assertEquals(4, mappings.length);
        Assertions.assertTrue(((fm5) == (mappings[((mappings.length) - 1)])));
    }

    @Test
    public void testFilterMappingsMix() throws Exception {
        ServletHandler handler = new ServletHandler();
        // add a non-programmatic one to begin with
        handler.addFilter(fh1);
        handler.addFilterMapping(fm1);
        FilterMapping[] mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertTrue(((fm1) == (mappings[0])));
        // add a programmatic one, isMatchAfter=false
        handler.addFilter(fh4);
        handler.prependFilterMapping(fm4);
        mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(2, mappings.length);
        Assertions.assertTrue(((fm4) == (mappings[0])));
        Assertions.assertTrue(((fm1) == (mappings[1])));
        // add a programmatic one, isMatchAfter=true
        handler.addFilter(fh3);
        handler.addFilterMapping(fm3);
        mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(3, mappings.length);
        Assertions.assertTrue(((fm4) == (mappings[0])));
        Assertions.assertTrue(((fm1) == (mappings[1])));
        Assertions.assertTrue(((fm3) == (mappings[2])));
        // add a programmatic one, isMatchAfter=false
        handler.addFilter(fh5);
        handler.prependFilterMapping(fm5);
        mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(4, mappings.length);
        Assertions.assertTrue(((fm4) == (mappings[0])));// isMatchAfter = false;

        Assertions.assertTrue(((fm5) == (mappings[1])));// isMatchAfter = false;

        Assertions.assertTrue(((fm1) == (mappings[2])));// ordinary

        Assertions.assertTrue(((fm3) == (mappings[3])));// isMatchAfter = true;

        // add a non-programmatic one
        FilterHolder f = new FilterHolder(EMBEDDED);
        f.setName("non-programmatic");
        FilterMapping fm = new FilterMapping();
        fm.setFilterHolder(f);
        fm.setPathSpec("/*");
        handler.addFilter(f);
        handler.addFilterMapping(fm);
        mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(5, mappings.length);
        Assertions.assertTrue(((fm4) == (mappings[0])));// isMatchAfter = false;

        Assertions.assertTrue(((fm5) == (mappings[1])));// isMatchAfter = false;

        Assertions.assertTrue(((fm1) == (mappings[2])));// ordinary

        Assertions.assertTrue((fm == (mappings[3])));// ordinary

        Assertions.assertTrue(((fm3) == (mappings[4])));// isMatchAfter = true;

        // add a programmatic one, isMatchAfter=true
        FilterHolder pf = new FilterHolder(JAVAX_API);
        pf.setName("programmaticA");
        FilterMapping pfm = new FilterMapping();
        pfm.setFilterHolder(pf);
        pfm.setPathSpec("/*");
        handler.addFilter(pf);
        handler.addFilterMapping(pfm);
        mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(6, mappings.length);
        Assertions.assertTrue(((fm4) == (mappings[0])));// isMatchAfter = false;

        Assertions.assertTrue(((fm5) == (mappings[1])));// isMatchAfter = false;

        Assertions.assertTrue(((fm1) == (mappings[2])));// ordinary

        Assertions.assertTrue((fm == (mappings[3])));// ordinary

        Assertions.assertTrue(((fm3) == (mappings[4])));// isMatchAfter = true;

        Assertions.assertTrue((pfm == (mappings[5])));// isMatchAfter = true;

        // add a programmatic one, isMatchAfter=false
        FilterHolder pf2 = new FilterHolder(JAVAX_API);
        pf2.setName("programmaticB");
        FilterMapping pfm2 = new FilterMapping();
        pfm2.setFilterHolder(pf2);
        pfm2.setPathSpec("/*");
        handler.addFilter(pf2);
        handler.prependFilterMapping(pfm2);
        mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(7, mappings.length);
        Assertions.assertTrue(((fm4) == (mappings[0])));// isMatchAfter = false;

        Assertions.assertTrue(((fm5) == (mappings[1])));// isMatchAfter = false;

        Assertions.assertTrue((pfm2 == (mappings[2])));// isMatchAfter = false;

        Assertions.assertTrue(((fm1) == (mappings[3])));// ordinary

        Assertions.assertTrue((fm == (mappings[4])));// ordinary

        Assertions.assertTrue(((fm3) == (mappings[5])));// isMatchAfter = true;

        Assertions.assertTrue((pfm == (mappings[6])));// isMatchAfter = true;

    }

    @Test
    public void testAddFilterWithMappingAPI() throws Exception {
        ServletHandler handler = new ServletHandler();
        // add a non-programmatic one to begin with
        handler.addFilterWithMapping(fh1, "/*", EnumSet.allOf(DispatcherType.class));
        handler.updateMappings();
        FilterMapping[] mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertTrue(((fh1) == (mappings[0].getFilterHolder())));
        // add a programmatic one, isMatchAfter=false
        fh4.setServletHandler(handler);
        handler.addFilter(fh4);
        fh4.getRegistration().addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
        handler.updateMappings();
        mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(2, mappings.length);
        Assertions.assertTrue(((fh4) == (mappings[0].getFilterHolder())));
        Assertions.assertTrue(((fh1) == (mappings[1].getFilterHolder())));
        // add a programmatic one, isMatchAfter=true
        fh3.setServletHandler(handler);
        handler.addFilter(fh3);
        fh3.getRegistration().addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        handler.updateMappings();
        mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(3, mappings.length);
        Assertions.assertTrue(((fh4) == (mappings[0].getFilterHolder())));
        Assertions.assertTrue(((fh1) == (mappings[1].getFilterHolder())));
        Assertions.assertTrue(((fh3) == (mappings[2].getFilterHolder())));
        // add a programmatic one, isMatchAfter=false
        fh5.setServletHandler(handler);
        handler.addFilter(fh5);
        fh5.getRegistration().addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
        handler.updateMappings();
        mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(4, mappings.length);
        Assertions.assertTrue(((fh4) == (mappings[0].getFilterHolder())));// isMatchAfter = false;

        Assertions.assertTrue(((fh5) == (mappings[1].getFilterHolder())));// isMatchAfter = false;

        Assertions.assertTrue(((fh1) == (mappings[2].getFilterHolder())));// ordinary

        Assertions.assertTrue(((fh3) == (mappings[3].getFilterHolder())));// isMatchAfter = true;

        // add a non-programmatic one
        FilterHolder f = new FilterHolder(EMBEDDED);
        f.setName("non-programmatic");
        handler.addFilterWithMapping(f, "/*", EnumSet.allOf(DispatcherType.class));
        handler.updateMappings();
        mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(5, mappings.length);
        Assertions.assertTrue(((fh4) == (mappings[0].getFilterHolder())));// isMatchAfter = false;

        Assertions.assertTrue(((fh5) == (mappings[1].getFilterHolder())));// isMatchAfter = false;

        Assertions.assertTrue(((fh1) == (mappings[2].getFilterHolder())));// ordinary

        Assertions.assertTrue((f == (mappings[3].getFilterHolder())));// ordinary

        Assertions.assertTrue(((fh3) == (mappings[4].getFilterHolder())));// isMatchAfter = true;

        // add a programmatic one, isMatchAfter=true
        FilterHolder pf = new FilterHolder(JAVAX_API);
        pf.setServletHandler(handler);
        pf.setName("programmaticA");
        handler.addFilter(pf);
        pf.getRegistration().addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        handler.updateMappings();
        mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(6, mappings.length);
        Assertions.assertTrue(((fh4) == (mappings[0].getFilterHolder())));// isMatchAfter = false;

        Assertions.assertTrue(((fh5) == (mappings[1].getFilterHolder())));// isMatchAfter = false;

        Assertions.assertTrue(((fh1) == (mappings[2].getFilterHolder())));// ordinary

        Assertions.assertTrue((f == (mappings[3].getFilterHolder())));// ordinary

        Assertions.assertTrue(((fh3) == (mappings[4].getFilterHolder())));// isMatchAfter = true;

        Assertions.assertTrue((pf == (mappings[5].getFilterHolder())));// isMatchAfter = true;

        // add a programmatic one, isMatchAfter=false
        FilterHolder pf2 = new FilterHolder(JAVAX_API);
        pf2.setServletHandler(handler);
        pf2.setName("programmaticB");
        handler.addFilter(pf2);
        pf2.getRegistration().addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
        handler.updateMappings();
        mappings = handler.getFilterMappings();
        Assertions.assertNotNull(mappings);
        Assertions.assertEquals(7, mappings.length);
        Assertions.assertTrue(((fh4) == (mappings[0].getFilterHolder())));// isMatchAfter = false;

        Assertions.assertTrue(((fh5) == (mappings[1].getFilterHolder())));// isMatchAfter = false;

        Assertions.assertTrue((pf2 == (mappings[2].getFilterHolder())));// isMatchAfter = false;

        Assertions.assertTrue(((fh1) == (mappings[3].getFilterHolder())));// ordinary

        Assertions.assertTrue((f == (mappings[4].getFilterHolder())));// ordinary

        Assertions.assertTrue(((fh3) == (mappings[5].getFilterHolder())));// isMatchAfter = true;

        Assertions.assertTrue((pf == (mappings[6].getFilterHolder())));// isMatchAfter = true;

    }
}

