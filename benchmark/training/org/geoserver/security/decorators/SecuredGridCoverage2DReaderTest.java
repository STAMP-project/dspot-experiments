/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.decorators;


import ImageMosaicFormat.FILTER;
import org.geoserver.security.CatalogMode;
import org.geoserver.security.CoverageAccessLimits;
import org.geoserver.security.WrapperPolicy;
import org.geoserver.security.impl.SecureObjectsTest;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.StructuredGridCoverage2DReader;
import org.geotools.filter.text.ecql.ECQL;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.coverage.grid.Format;
import org.opengis.filter.Filter;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;


public class SecuredGridCoverage2DReaderTest extends SecureObjectsTest {
    @Test
    public void testFilter() throws Exception {
        final Filter securityFilter = ECQL.toFilter("A > 10");
        final Filter requestFilter = ECQL.toFilter("B < 10");
        // create the mocks we need
        Format format = setupFormat();
        GridCoverage2DReader reader = createNiceMock(GridCoverage2DReader.class);
        expect(reader.getFormat()).andReturn(format).anyTimes();
        SecuredGridCoverage2DReaderTest.setupReadAssertion(reader, requestFilter, securityFilter);
        CoverageAccessLimits accessLimits = new CoverageAccessLimits(CatalogMode.HIDE, securityFilter, null, null);
        SecuredGridCoverage2DReader secured = new SecuredGridCoverage2DReader(reader, WrapperPolicy.readOnlyHide(accessLimits));
        final ParameterValue pv = FILTER.createValue();
        pv.setValue(requestFilter);
        secured.read(new GeneralParameterValue[]{ pv });
    }

    @Test
    public void testFilterOnStructured() throws Exception {
        final Filter securityFilter = ECQL.toFilter("A > 10");
        final Filter requestFilter = ECQL.toFilter("B < 10");
        DefaultSecureDataFactory factory = new DefaultSecureDataFactory();
        // create the mocks we need
        Format format = setupFormat();
        StructuredGridCoverage2DReader reader = createNiceMock(StructuredGridCoverage2DReader.class);
        expect(reader.getFormat()).andReturn(format).anyTimes();
        SecuredGridCoverage2DReaderTest.setupReadAssertion(reader, requestFilter, securityFilter);
        CoverageAccessLimits accessLimits = new CoverageAccessLimits(CatalogMode.HIDE, securityFilter, null, null);
        Object securedObject = factory.secure(reader, WrapperPolicy.readOnlyHide(accessLimits));
        Assert.assertTrue((securedObject instanceof SecuredStructuredGridCoverage2DReader));
        SecuredStructuredGridCoverage2DReader secured = ((SecuredStructuredGridCoverage2DReader) (securedObject));
        final ParameterValue pv = FILTER.createValue();
        pv.setValue(requestFilter);
        secured.read(new GeneralParameterValue[]{ pv });
    }
}

