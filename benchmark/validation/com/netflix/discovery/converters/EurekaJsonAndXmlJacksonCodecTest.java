package com.netflix.discovery.converters;


import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.converters.jackson.EurekaJsonJacksonCodec;
import com.netflix.discovery.converters.jackson.EurekaXmlJacksonCodec;
import com.netflix.discovery.util.InstanceInfoGenerator;
import java.util.Iterator;
import org.junit.Test;


/**
 *
 *
 * @author Tomasz Bak
 */
public class EurekaJsonAndXmlJacksonCodecTest {
    private final InstanceInfoGenerator infoGenerator = InstanceInfoGenerator.newBuilder(4, 2).withMetaData(true).build();

    private final Iterator<InstanceInfo> infoIterator = infoGenerator.serviceIterator();

    @Test
    public void testAmazonInfoEncodeDecodeWithJson() throws Exception {
        doAmazonInfoEncodeDecodeTest(new EurekaJsonJacksonCodec());
    }

    @Test
    public void testAmazonInfoEncodeDecodeWithXml() throws Exception {
        doAmazonInfoEncodeDecodeTest(new EurekaXmlJacksonCodec());
    }

    @Test
    public void testAmazonInfoCompactEncodeDecodeWithJson() throws Exception {
        doAmazonInfoCompactEncodeDecodeTest(new EurekaJsonJacksonCodec(KeyFormatter.defaultKeyFormatter(), true));
    }

    @Test
    public void testAmazonInfoCompactEncodeDecodeWithXml() throws Exception {
        doAmazonInfoCompactEncodeDecodeTest(new EurekaXmlJacksonCodec(KeyFormatter.defaultKeyFormatter(), true));
    }

    @Test
    public void testMyDataCenterInfoEncodeDecodeWithJson() throws Exception {
        doMyDataCenterInfoEncodeDecodeTest(new EurekaJsonJacksonCodec());
    }

    @Test
    public void testMyDataCenterInfoEncodeDecodeWithXml() throws Exception {
        doMyDataCenterInfoEncodeDecodeTest(new EurekaXmlJacksonCodec());
    }

    @Test
    public void testLeaseInfoEncodeDecodeWithJson() throws Exception {
        doLeaseInfoEncodeDecode(new EurekaJsonJacksonCodec());
    }

    @Test
    public void testLeaseInfoEncodeDecodeWithXml() throws Exception {
        doLeaseInfoEncodeDecode(new EurekaXmlJacksonCodec());
    }

    @Test
    public void testInstanceInfoEncodeDecodeWithJson() throws Exception {
        doInstanceInfoEncodeDecode(new EurekaJsonJacksonCodec());
    }

    @Test
    public void testInstanceInfoEncodeDecodeWithXml() throws Exception {
        doInstanceInfoEncodeDecode(new EurekaXmlJacksonCodec());
    }

    @Test
    public void testInstanceInfoCompactEncodeDecodeWithJson() throws Exception {
        doInstanceInfoCompactEncodeDecode(new EurekaJsonJacksonCodec(KeyFormatter.defaultKeyFormatter(), true), true);
    }

    @Test
    public void testInstanceInfoCompactEncodeDecodeWithXml() throws Exception {
        doInstanceInfoCompactEncodeDecode(new EurekaXmlJacksonCodec(KeyFormatter.defaultKeyFormatter(), true), false);
    }

    @Test
    public void testInstanceInfoIgnoredFieldsAreFilteredOutDuringDeserializationProcessWithJson() throws Exception {
        doInstanceInfoIgnoredFieldsAreFilteredOutDuringDeserializationProcess(new EurekaJsonJacksonCodec(), new EurekaJsonJacksonCodec(KeyFormatter.defaultKeyFormatter(), true));
    }

    @Test
    public void testInstanceInfoIgnoredFieldsAreFilteredOutDuringDeserializationProcessWithXml() throws Exception {
        doInstanceInfoIgnoredFieldsAreFilteredOutDuringDeserializationProcess(new EurekaXmlJacksonCodec(), new EurekaXmlJacksonCodec(KeyFormatter.defaultKeyFormatter(), true));
    }

    @Test
    public void testInstanceInfoWithNoMetaEncodeDecodeWithJson() throws Exception {
        doInstanceInfoWithNoMetaEncodeDecode(new EurekaJsonJacksonCodec(), true);
    }

    @Test
    public void testInstanceInfoWithNoMetaEncodeDecodeWithXml() throws Exception {
        doInstanceInfoWithNoMetaEncodeDecode(new EurekaXmlJacksonCodec(), false);
    }

    @Test
    public void testApplicationEncodeDecodeWithJson() throws Exception {
        doApplicationEncodeDecode(new EurekaJsonJacksonCodec());
    }

    @Test
    public void testApplicationEncodeDecodeWithXml() throws Exception {
        doApplicationEncodeDecode(new EurekaXmlJacksonCodec());
    }

    @Test
    public void testApplicationsEncodeDecodeWithJson() throws Exception {
        doApplicationsEncodeDecode(new EurekaJsonJacksonCodec());
    }

    @Test
    public void testApplicationsEncodeDecodeWithXml() throws Exception {
        doApplicationsEncodeDecode(new EurekaXmlJacksonCodec());
    }
}

