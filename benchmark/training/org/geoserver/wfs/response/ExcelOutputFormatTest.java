/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.response;


import java.io.InputStream;
import junit.framework.Assert;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.geoserver.wfs.WFSTestSupport;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class ExcelOutputFormatTest extends WFSTestSupport {
    @Test
    public void testExcel97OutputFormat() throws Exception {
        // grab the real binary stream, avoiding mangling to due char conversion
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.0.0&typeName=sf:PrimitiveGeoFeature&outputFormat=excel");
        InputStream in = getBinaryInputStream(resp);
        // check the mime type
        Assert.assertEquals("application/msexcel", resp.getContentType());
        // check the content disposition
        Assert.assertEquals("attachment; filename=PrimitiveGeoFeature.xls", resp.getHeader("Content-Disposition"));
        HSSFWorkbook wb = new HSSFWorkbook(in);
        testExcelOutputFormat(wb);
    }

    @Test
    public void testExcel2007OutputFormat() throws Exception {
        // grab the real binary stream, avoiding mangling to due char conversion
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&version=1.0.0&typeName=sf:PrimitiveGeoFeature&outputFormat=excel2007");
        InputStream in = getBinaryInputStream(resp);
        // check the mime type
        Assert.assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", resp.getContentType());
        // check the content disposition
        Assert.assertEquals("attachment; filename=PrimitiveGeoFeature.xlsx", resp.getHeader("Content-Disposition"));
        XSSFWorkbook wb = new XSSFWorkbook(in);
        testExcelOutputFormat(wb);
    }

    @Test
    public void testExcel97MultipleFeatureTypes() throws Exception {
        // grab the real binary stream, avoiding mangling to due char conversion
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&typeName=sf:PrimitiveGeoFeature,sf:GenericEntity&outputFormat=excel");
        InputStream in = getBinaryInputStream(resp);
        Workbook wb = new HSSFWorkbook(in);
        testMultipleFeatureTypes(wb);
    }

    @Test
    public void testExcel2007MultipleFeatureTypes() throws Exception {
        // grab the real binary stream, avoiding mangling to due char conversion
        MockHttpServletResponse resp = getAsServletResponse("wfs?request=GetFeature&typeName=sf:PrimitiveGeoFeature,sf:GenericEntity&outputFormat=excel2007");
        InputStream in = getBinaryInputStream(resp);
        Workbook wb = new XSSFWorkbook(in);
        testMultipleFeatureTypes(wb);
    }
}

