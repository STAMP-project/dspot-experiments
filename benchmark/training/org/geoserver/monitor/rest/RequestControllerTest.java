/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.monitor.rest;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.geoserver.monitor.Monitor;
import org.geoserver.monitor.RequestData;
import org.geoserver.rest.RestBaseController;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geotools.feature.type.DateUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class RequestControllerTest extends GeoServerSystemTestSupport {
    private Monitor monitor;

    @Test
    public void testGetAllHTML() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests.html"));
        Assert.assertEquals(200, response.getStatus());
        // System.out.println(response.getContentAsString());
        // this check is's actual XML
        Document document = Jsoup.parse(response.getContentAsString());
        // testing the first element
        Assert.assertEquals((("http://localhost:8080/geoserver" + (RestBaseController.ROOT_PATH)) + "/monitor/requests/1.html"), document.select("a:contains(1)").attr("href"));
        Assert.assertEquals("RUNNING", document.select("tr.even > td").get(1).text());
    }

    @Test
    public void testGetHTMLById() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests/5.html"));
        Assert.assertEquals(200, response.getStatus());
        // System.out.println(response.getContentAsString());
        // this check is's actual XML
        Document document = Jsoup.parse(response.getContentAsString());
        // the structure is different, check the title
        Assert.assertEquals("Request 5", document.select("#content > h1 > span").text());
    }

    /**
     * This is undocumented/accidental behavior of 2.10.x (and previous) that actually got used by
     * other projects, adding it back preserving its original structure (pure XStream reflection)
     * even if it's really hard on the eyes....
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetXMLById() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests/5.xml"));
        Assert.assertEquals(200, response.getStatus());
        org.w3c.dom.Document dom = dom(new ByteArrayInputStream(response.getContentAsByteArray()));
        // print(dom);
        assertXpathEvaluatesTo("5", "/org.geoserver.monitor.RequestData/id", dom);
        assertXpathEvaluatesTo("RUNNING", "/org.geoserver.monitor.RequestData/status", dom);
    }

    /**
     * This is undocumented/accidental behavior of 2.10.x (and previous) that actually got used by
     * other projects, adding it back preserving its original structure (pure XStream reflection)
     * even if it's really hard on the eyes....
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetJSONById() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests/5.json"));
        Assert.assertEquals(200, response.getStatus());
        JSONObject json = ((JSONObject) (json(response)));
        // print(json);
        JSONObject data = json.getJSONObject("org.geoserver.monitor.RequestData");
        Assert.assertNotNull(data);
        Assert.assertEquals("5", data.getString("id"));
        Assert.assertEquals("RUNNING", data.getString("status"));
    }

    @Test
    public void testGetAllCSV() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests.csv?fields=id;path;startTime"));
        Assert.assertEquals(200, response.getStatus());
        // System.out.println(response.getContentAsString());
        BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.getContentAsByteArray())));
        String line = in.readLine();
        Assert.assertEquals("id,path,startTime", line);
        Iterator<RequestData> it = monitor.getDAO().getRequests().iterator();
        while ((line = in.readLine()) != null) {
            Assert.assertTrue(it.hasNext());
            RequestData data = it.next();
            String expected = ((((data.getId()) + ",") + (data.getPath())) + ",") + (DateUtil.serializeDateTime(data.getStartTime()));
            Assert.assertEquals(expected, line);
        } 
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void testDelete() throws Exception {
        // delete all
        MockHttpServletResponse response = deleteAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests"));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(0, monitor.getDAO().getRequests().size());
    }

    @Test
    public void testGetAllExcel() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests.xls?fields=id;path;startTime"));
        Assert.assertEquals(200, response.getStatus());
        HSSFWorkbook wb = new HSSFWorkbook(new ByteArrayInputStream(response.getContentAsByteArray()));
        HSSFSheet sheet = wb.getSheet("requests");
        Iterator<Row> rows = sheet.iterator();
        Iterator<RequestData> it = monitor.getDAO().getRequests().iterator();
        Assert.assertTrue(rows.hasNext());
        Row row = rows.next();
        Assert.assertEquals("id", row.getCell(0).getStringCellValue());
        Assert.assertEquals("path", row.getCell(1).getStringCellValue());
        Assert.assertEquals("startTime", row.getCell(2).getStringCellValue());
        while (rows.hasNext()) {
            row = rows.next();
            Assert.assertTrue(it.hasNext());
            RequestData data = it.next();
            Assert.assertEquals(((double) (data.getId())), row.getCell(0).getNumericCellValue(), 0.1);
            Assert.assertEquals(data.getPath(), row.getCell(1).getStringCellValue());
            Assert.assertEquals(data.getStartTime(), row.getCell(2).getDateCellValue());
        } 
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void testGetZIP() throws Exception {
        // setup a single value in the DAO
        Date startTime = new Date();
        Throwable throwable = new Throwable();
        RequestData data = new RequestData();
        data.setId(12345);
        data.setPath("/foo");
        data.setStartTime(startTime);
        data.setBody("<foo></foo>".getBytes());
        data.setError(throwable);
        monitor.getDAO().dispose();
        monitor.getDAO().add(data);
        // running request
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests/12345.zip?fields=id;path;startTime;Error;Body"));
        Assert.assertEquals(200, response.getStatus());
        ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(response.getContentAsByteArray()));
        ZipEntry entry = null;
        boolean requests = false;
        boolean body = false;
        boolean error = false;
        while ((entry = zin.getNextEntry()) != null) {
            if ("requests.csv".equals(entry.getName())) {
                requests = true;
                String expected = "id,path,startTime\n12345,/foo," + (DateUtil.serializeDateTime(startTime));
                Assert.assertEquals(expected, readEntry(zin));
            } else
                if ("body.txt".equals(entry.getName())) {
                    body = true;
                    Assert.assertEquals("<foo></foo>", readEntry(zin));
                } else
                    if ("error.txt".equals(entry.getName())) {
                        error = true;
                        ByteArrayOutputStream bout = new ByteArrayOutputStream();
                        PrintStream stream = new PrintStream(bout);
                        throwable.printStackTrace(stream);
                        stream.flush();
                        Assert.assertEquals(new String(bout.toByteArray()).trim(), readEntry(zin));
                    }


        } 
        Assert.assertTrue(requests);
        Assert.assertTrue(body);
        Assert.assertTrue(error);
    }

    @Test
    public void testGetDateRange() throws Exception {
        // running request
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests.csv?fields=id;path;startTime&from=2010-07-23T15:56:44&to=2010-07-23T16:16:44"));
        Assert.assertEquals(200, response.getStatus());
        assertCoveredInOrder(response, 6, 5, 4);
    }

    @Test
    public void testGetDateRangeWithTimeZone() throws Exception {
        // running request
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests.csv?fields=id;path;startTime&from=2010-07-23T15:56:44+0000&to=2010-07-23T16:16:44+0000"));
        Assert.assertEquals(200, response.getStatus());
        assertCoveredInOrder(response, 6, 5, 4);
    }

    @Test
    public void testFilter() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests.csv?fields=id;path;startTime&filter=path:EQ:/seven"));
        Assert.assertEquals(200, response.getStatus());
        assertCoveredInOrder(response, 7);
    }

    @Test
    public void testFilterIn() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests.csv?fields=id;path;startTime&filter=path:IN:/seven,/six,/five"));
        Assert.assertEquals(200, response.getStatus());
        RequestControllerTest.assertCovered(response, 5, 6, 7);
    }

    @Test
    public void testFilterStatus() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests.csv?fields=id;path;startTime&filter=status:EQ:WAITING"));
        Assert.assertEquals(200, response.getStatus());
        RequestControllerTest.assertCovered(response, 2, 6);
    }

    @Test
    public void testSorting() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests.csv?fields=id;path;startTime&order=path"));
        Assert.assertEquals(200, response.getStatus());
        assertCoveredInOrder(response, 8, 5, 4, 9, 1, 7, 6, 10, 3, 2);
        response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests.csv?fields=id;path;startTime&order=path;ASC"));
        Assert.assertEquals(200, response.getStatus());
        assertCoveredInOrder(response, 8, 5, 4, 9, 1, 7, 6, 10, 3, 2);
        response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests.csv?fields=id;path;startTime&order=path;DESC"));
        Assert.assertEquals(200, response.getStatus());
        assertCoveredInOrder(response, 2, 3, 10, 6, 7, 1, 9, 4, 5, 8);
    }

    @Test
    public void testPaging() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests.csv?fields=id;path;startTime&order=startTime&offset=5&count=2"));
        Assert.assertEquals(200, response.getStatus());
        assertCoveredInOrder(response, 6, 7);
    }

    @Test
    public void testLive() throws Exception {
        MockHttpServletResponse response = getAsServletResponse(((RestBaseController.ROOT_PATH) + "/monitor/requests.csv?fields=id;path;startTime&live=yes"));
        Assert.assertEquals(200, response.getStatus());
        RequestControllerTest.assertCovered(response, 1, 2, 5, 6, 9, 10);
    }
}

