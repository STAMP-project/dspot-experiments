/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.restassured.itest.java;


import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.itest.java.support.WithJetty;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;


public class GivenWhenThenXsdITest extends WithJetty {
    @Test
    public void validatesXsdString() throws Exception {
        final InputStream inputstream = getClass().getResourceAsStream("/car-records.xsd");
        final String xsd = IOUtils.toString(inputstream);
        RestAssured.get("/carRecords").then().body(matchesXsd(xsd));
    }

    @Test
    public void validatesXsdStringInClasspathWhenPathStartsWithSlash() throws Exception {
        RestAssured.get("/carRecords").then().body(matchesXsdInClasspath("/car-records.xsd"));
    }

    @Test
    public void validatesXsdStringInClasspathWhenPathDoesntStartWithSlash() throws Exception {
        RestAssured.get("/carRecords").then().body(matchesXsdInClasspath("car-records.xsd"));
    }

    @Test
    public void validatesXsdInputStream() throws Exception {
        InputStream inputstream = GivenWhenThenXsdITest.load("car-records.xsd");
        RestAssured.get("/carRecords").then().body(matchesXsd(inputstream));
    }

    @Test
    public void validatesXsdStringAndPath() throws Exception {
        final InputStream inputstream = getClass().getResourceAsStream("/car-records.xsd");
        final String xsd = IOUtils.toString(inputstream);
        RestAssured.get("/carRecords").then().body(matchesXsd(xsd)).and().body("records.car.find { it.@name == 'HSV Maloo' }.@year", Matchers.equalTo("2006"));
    }

    @Test
    public void possibleToSpecifyAResourceResolverWhenMatchingXsd() throws IOException {
        RestAssured.given().filter(new Filter() {
            public io.restassured.response.Response filter(io.restassured.specification.FilterableRequestSpecification requestSpec, io.restassured.specification.FilterableResponseSpecification responseSpec, io.restassured.filter.FilterContext ctx) {
                return new io.restassured.builder.ResponseBuilder().setStatusCode(200).setBody(GivenWhenThenXsdITest.load("sample.xml")).build();
            }
        }).when().get("/somewhere").then().body(matchesXsd(GivenWhenThenXsdITest.load("main.xsd")).with(new GivenWhenThenXsdITest.ClasspathResourceResolver()));
    }

    private static class ClasspathResourceResolver implements LSResourceResolver {
        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
            return new LSInput() {
                @Override
                public Reader getCharacterStream() {
                    return null;
                }

                @Override
                public void setCharacterStream(Reader characterStream) {
                }

                @Override
                public InputStream getByteStream() {
                    return GivenWhenThenXsdITest.load(systemId);
                }

                @Override
                public void setByteStream(InputStream byteStream) {
                }

                @Override
                public String getStringData() {
                    return null;
                }

                @Override
                public void setStringData(String stringData) {
                }

                @Override
                public String getSystemId() {
                    return systemId;
                }

                @Override
                public void setSystemId(String systemId) {
                }

                @Override
                public String getPublicId() {
                    return publicId;
                }

                @Override
                public void setPublicId(String publicId) {
                }

                @Override
                public String getBaseURI() {
                    return baseURI;
                }

                @Override
                public void setBaseURI(String baseURI) {
                }

                @Override
                public String getEncoding() {
                    return null;
                }

                @Override
                public void setEncoding(String encoding) {
                }

                @Override
                public boolean getCertifiedText() {
                    return false;
                }

                @Override
                public void setCertifiedText(boolean certifiedText) {
                }
            };
        }
    }
}

