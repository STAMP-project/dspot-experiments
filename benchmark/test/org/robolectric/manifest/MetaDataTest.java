package org.robolectric.manifest;


import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.robolectric.res.ResourceTable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Tests for {@link MetaData}
 */
@RunWith(JUnit4.class)
public class MetaDataTest {
    @Mock
    private ResourceTable resourceProvider;

    @Test(expected = RoboNotFoundException.class)
    public void testNonExistantResource_throwsResourceNotFoundException() throws Exception {
        Element metaDataElement = MetaDataTest.createMetaDataNode("aName", "@xml/non_existant_resource");
        MetaData metaData = new MetaData(ImmutableList.<Node>of(metaDataElement));
        metaData.init(resourceProvider, "a.package");
    }
}

