/**
 * !
 * Copyright 2010 - 2017 Hitachi Vantara.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pentaho.di.repository.pur;


import RepositoryMeta.XML_TAG;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.RepositoriesMeta;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


@RunWith(MockitoJUnitRunner.class)
public class PurRepositoryMetaTest {
    @Mock
    private RepositoriesMeta repositoriesMeta;

    private static final String URL_WITHOUT_TRAILING = "http://host:0000/pentaho-di";

    private static final String EXAMPLE_RESOURCES = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><repositories><repository><id>PentahoEnterpriseRepository</id><name>pentaho-di</name><description>pentaho-di</description><repository_location_url>http://host:0000/pentaho-di</repository_location_url><version_comment_mandatory>N</version_comment_mandatory></repository><repository><id>PentahoEnterpriseRepository</id><name>pentaho-di</name><description>pentaho-di</description><repository_location_url>http://host:0000/pentaho-di/</repository_location_url><version_comment_mandatory>N</version_comment_mandatory></repository></repositories>";

    /**
     * check URL trailing slash load throw exception when internal test resource unavailable
     *
     * @throws ParserConfigurationException
     * 		
     * @throws IOException
     * 		
     * @throws SAXException
     * 		
     * @throws KettleException
     * 		
     * @see <a href="http://jira.pentaho.com/browse/PDI-10401">http://jira.pentaho.com/browse/PDI-10401</a>
     */
    @Test
    public void testURLTralingSlashTolerante() throws IOException, ParserConfigurationException, KettleException, SAXException {
        InputStream stream = new ByteArrayInputStream(PurRepositoryMetaTest.EXAMPLE_RESOURCES.getBytes(StandardCharsets.UTF_8));
        DocumentBuilder db;
        Document doc;
        db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        doc = db.parse(stream);
        Node repsnode = XMLHandler.getSubNode(doc, "repositories");
        Node repnode = XMLHandler.getSubNodeByNr(repsnode, XML_TAG, 0);
        @SuppressWarnings("unchecked")
        List<DatabaseMeta> databases = Mockito.mock(List.class);
        // check with trailing
        PurRepositoryMeta repositoryMeta = new PurRepositoryMeta();
        repositoryMeta.loadXML(repnode, databases);
        Assert.assertEquals(repositoryMeta.getRepositoryLocation().getUrl(), PurRepositoryMetaTest.URL_WITHOUT_TRAILING);
        // check without trailing
        Node repnode2 = XMLHandler.getSubNodeByNr(repsnode, XML_TAG, 1);
        repositoryMeta = new PurRepositoryMeta();
        repositoryMeta.loadXML(repnode2, databases);
        Assert.assertEquals(repositoryMeta.getRepositoryLocation().getUrl(), PurRepositoryMetaTest.URL_WITHOUT_TRAILING);
    }

    @Test
    public void testPopulate() throws Exception {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("displayName", "Display Name");
        properties.put("url", "URL");
        properties.put("description", "Description");
        properties.put("isDefault", true);
        PurRepositoryMeta purRepositoryMeta = new PurRepositoryMeta();
        purRepositoryMeta.populate(properties, repositoriesMeta);
        Assert.assertEquals("Display Name", purRepositoryMeta.getName());
        Assert.assertEquals("URL", purRepositoryMeta.getRepositoryLocation().getUrl());
        Assert.assertEquals("Description", purRepositoryMeta.getDescription());
        Assert.assertEquals(true, purRepositoryMeta.isDefault());
    }
}

