/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.cluster.impl.handlers;


import com.thoughtworks.xstream.XStream;
import java.io.File;
import org.custommonkey.xmlunit.XMLUnit;
import org.geoserver.platform.resource.FileSystemResourceStore;
import org.geoserver.platform.resource.Resource;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DocumentFileTest {
    private File rootDirectory;

    private FileSystemResourceStore resourceStore;

    @Test
    public void testSerializeDocumentFile() throws Exception {
        // creating a style in data directory
        Resource resource = addResourceToDataDir("styles/style.sld", "some style definition");
        // instantiating a document file representing the style file
        DocumentFile documentFile = new DocumentFile(resource);
        // serialising the file document
        DocumentFileHandlerSPI handler = new DocumentFileHandlerSPI(0, new XStream());
        String result = handler.createHandler().serialize(documentFile);
        // checking the serialization result
        Assert.assertThat(result, CoreMatchers.notNullValue());
        Assert.assertThat(XMLUnit.compareXML(readResourceFileContent("document_file_1.xml"), result).similar(), CoreMatchers.is(true));
    }
}

