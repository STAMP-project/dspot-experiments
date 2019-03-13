/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs3;


import HttpHeaders.ACCEPT;
import OpenAPIResponse.OPEN_API_MIME;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


public class ApiTest extends WFS3TestSupport {
    @Test
    public void testApiJson() throws Exception {
        MockHttpServletResponse response = getAsMockHttpServletResponse("wfs3/api", 200);
        Assert.assertEquals(OPEN_API_MIME, response.getContentType());
        String json = response.getContentAsString();
        LOGGER.log(Level.INFO, json);
        ObjectMapper mapper = Json.mapper();
        OpenAPI api = mapper.readValue(json, OpenAPI.class);
        validateApi(api);
    }

    @Test
    public void testApiHTML() throws Exception {
        MockHttpServletResponse response = getAsMockHttpServletResponse("wfs3/api?f=text/html", 200);
        Assert.assertEquals("text/html", response.getContentType());
        String html = response.getContentAsString();
        LOGGER.info(html);
        // check template expansion worked properly
        Assert.assertThat(html, CoreMatchers.containsString("<link rel=\"icon\" type=\"image/png\" href=\"http://localhost:8080/geoserver/swagger-ui/favicon-32x32.png\" sizes=\"32x32\" />"));
        Assert.assertThat(html, CoreMatchers.containsString("<link rel=\"icon\" type=\"image/png\" href=\"http://localhost:8080/geoserver/swagger-ui/favicon-16x16.png\" sizes=\"16x16\" />"));
        Assert.assertThat(html, CoreMatchers.containsString("<script src=\"http://localhost:8080/geoserver/swagger-ui/swagger-ui-bundle.js\">"));
        Assert.assertThat(html, CoreMatchers.containsString("<script src=\"http://localhost:8080/geoserver/swagger-ui/swagger-ui-standalone-preset.js\">"));
        Assert.assertThat(html, CoreMatchers.containsString("url: \"http://localhost:8080/geoserver/wfs3/api?f=application%2Fopenapi%2Bjson%3Bversion%3D3.0\""));
    }

    @Test
    public void testApiYaml() throws Exception {
        String yaml = getAsString("wfs3/api?f=application/x-yaml");
        LOGGER.log(Level.INFO, yaml);
        ObjectMapper mapper = Yaml.mapper();
        OpenAPI api = mapper.readValue(yaml, OpenAPI.class);
        validateApi(api);
    }

    @Test
    public void testYamlAsAcceptsHeader() throws Exception {
        MockHttpServletRequest request = createRequest("wfs3/api");
        request.setMethod("GET");
        request.setContent(new byte[]{  });
        request.addHeader(ACCEPT, "foo/bar, application/x-yaml, text/html");
        MockHttpServletResponse response = dispatch(request);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("application/x-yaml", response.getContentType());
        String yaml = string(new ByteArrayInputStream(response.getContentAsString().getBytes()));
        ObjectMapper mapper = Yaml.mapper();
        OpenAPI api = mapper.readValue(yaml, OpenAPI.class);
        validateApi(api);
    }

    @Test
    public void testWorkspaceQualifiedAPI() throws Exception {
        MockHttpServletRequest request = createRequest("cdf/wfs3/api");
        request.setMethod("GET");
        request.setContent(new byte[]{  });
        request.addHeader(ACCEPT, "foo/bar, application/x-yaml, text/html");
        MockHttpServletResponse response = dispatch(request);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("application/x-yaml", response.getContentType());
        String yaml = string(new ByteArrayInputStream(response.getContentAsString().getBytes()));
        ObjectMapper mapper = Yaml.mapper();
        OpenAPI api = mapper.readValue(yaml, OpenAPI.class);
        Map<String, Parameter> params = api.getComponents().getParameters();
        Parameter collectionId = params.get("collectionId");
        List<String> collectionIdValues = collectionId.getSchema().getEnum();
        List<String> expectedCollectionIds = getCatalog().getFeatureTypesByNamespace(getCatalog().getNamespaceByPrefix("cdf")).stream().map(( ft) -> ft.getName()).collect(Collectors.toList());
        Assert.assertThat(collectionIdValues, CoreMatchers.equalTo(expectedCollectionIds));
    }
}

