package com.baeldung.jhipster.gateway.web.rest.util;


import HttpHeaders.LINK;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;


/**
 * Tests based on parsing algorithm in app/components/util/pagination-util.service.js
 *
 * @see PaginationUtil
 */
public class PaginationUtilUnitTest {
    @Test
    public void generatePaginationHttpHeadersTest() {
        String baseUrl = "/api/_search/example";
        List<String> content = new ArrayList<>();
        Page<String> page = new org.springframework.data.domain.PageImpl(content, PageRequest.of(6, 50), 400L);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, baseUrl);
        List<String> strHeaders = headers.get(LINK);
        Assert.assertNotNull(strHeaders);
        Assert.assertTrue(((strHeaders.size()) == 1));
        String headerData = strHeaders.get(0);
        Assert.assertTrue(((headerData.split(",").length) == 4));
        String expectedData = "</api/_search/example?page=7&size=50>; rel=\"next\"," + (("</api/_search/example?page=5&size=50>; rel=\"prev\"," + "</api/_search/example?page=7&size=50>; rel=\"last\",") + "</api/_search/example?page=0&size=50>; rel=\"first\"");
        Assert.assertEquals(expectedData, headerData);
        List<String> xTotalCountHeaders = headers.get("X-Total-Count");
        Assert.assertTrue(((xTotalCountHeaders.size()) == 1));
        Assert.assertTrue(Long.valueOf(xTotalCountHeaders.get(0)).equals(400L));
    }
}

