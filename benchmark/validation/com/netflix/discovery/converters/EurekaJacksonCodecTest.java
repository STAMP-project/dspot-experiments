package com.netflix.discovery.converters;


import ActionType.ADDED;
import MediaType.APPLICATION_JSON_TYPE;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.util.EurekaEntityComparators;
import com.netflix.discovery.util.InstanceInfoGenerator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tomasz Bak
 */
public class EurekaJacksonCodecTest {
    public static final InstanceInfo INSTANCE_INFO_1_A1;

    public static final InstanceInfo INSTANCE_INFO_2_A1;

    public static final InstanceInfo INSTANCE_INFO_1_A2;

    public static final InstanceInfo INSTANCE_INFO_2_A2;

    public static final Application APPLICATION_1;

    public static final Application APPLICATION_2;

    public static final Applications APPLICATIONS;

    static {
        Iterator<InstanceInfo> infoIterator = InstanceInfoGenerator.newBuilder(4, 2).withMetaData(true).build().serviceIterator();
        INSTANCE_INFO_1_A1 = infoIterator.next();
        EurekaJacksonCodecTest.INSTANCE_INFO_1_A1.setActionType(ADDED);
        INSTANCE_INFO_1_A2 = infoIterator.next();
        EurekaJacksonCodecTest.INSTANCE_INFO_1_A2.setActionType(ADDED);
        INSTANCE_INFO_2_A1 = infoIterator.next();
        EurekaJacksonCodecTest.INSTANCE_INFO_1_A2.setActionType(ADDED);
        INSTANCE_INFO_2_A2 = infoIterator.next();
        EurekaJacksonCodecTest.INSTANCE_INFO_2_A2.setActionType(ADDED);
        APPLICATION_1 = new Application(EurekaJacksonCodecTest.INSTANCE_INFO_1_A1.getAppName());
        EurekaJacksonCodecTest.APPLICATION_1.addInstance(EurekaJacksonCodecTest.INSTANCE_INFO_1_A1);
        EurekaJacksonCodecTest.APPLICATION_1.addInstance(EurekaJacksonCodecTest.INSTANCE_INFO_2_A1);
        APPLICATION_2 = new Application(EurekaJacksonCodecTest.INSTANCE_INFO_1_A2.getAppName());
        EurekaJacksonCodecTest.APPLICATION_2.addInstance(EurekaJacksonCodecTest.INSTANCE_INFO_1_A2);
        EurekaJacksonCodecTest.APPLICATION_2.addInstance(EurekaJacksonCodecTest.INSTANCE_INFO_2_A2);
        APPLICATIONS = new Applications();
        EurekaJacksonCodecTest.APPLICATIONS.addApplication(EurekaJacksonCodecTest.APPLICATION_1);
        EurekaJacksonCodecTest.APPLICATIONS.addApplication(EurekaJacksonCodecTest.APPLICATION_2);
    }

    private final EurekaJacksonCodec codec = new EurekaJacksonCodec();

    @Test
    public void testInstanceInfoJacksonEncodeDecode() throws Exception {
        // Encode
        ByteArrayOutputStream captureStream = new ByteArrayOutputStream();
        codec.writeTo(EurekaJacksonCodecTest.INSTANCE_INFO_1_A1, captureStream);
        byte[] encoded = captureStream.toByteArray();
        // Decode
        InputStream source = new ByteArrayInputStream(encoded);
        InstanceInfo decoded = codec.readValue(InstanceInfo.class, source);
        Assert.assertTrue(EurekaEntityComparators.equal(decoded, EurekaJacksonCodecTest.INSTANCE_INFO_1_A1));
    }

    @Test
    public void testInstanceInfoJacksonEncodeDecodeWithoutMetaData() throws Exception {
        InstanceInfo noMetaDataInfo = InstanceInfoGenerator.newBuilder(1, 1).withMetaData(false).build().serviceIterator().next();
        // Encode
        ByteArrayOutputStream captureStream = new ByteArrayOutputStream();
        codec.writeTo(noMetaDataInfo, captureStream);
        byte[] encoded = captureStream.toByteArray();
        // Decode
        InputStream source = new ByteArrayInputStream(encoded);
        InstanceInfo decoded = codec.readValue(InstanceInfo.class, source);
        Assert.assertTrue(EurekaEntityComparators.equal(decoded, noMetaDataInfo));
    }

    @Test
    public void testInstanceInfoXStreamEncodeJacksonDecode() throws Exception {
        InstanceInfo original = EurekaJacksonCodecTest.INSTANCE_INFO_1_A1;
        // Encode
        ByteArrayOutputStream captureStream = new ByteArrayOutputStream();
        new EntityBodyConverter().write(original, captureStream, APPLICATION_JSON_TYPE);
        byte[] encoded = captureStream.toByteArray();
        // Decode
        InputStream source = new ByteArrayInputStream(encoded);
        InstanceInfo decoded = codec.readValue(InstanceInfo.class, source);
        Assert.assertTrue(EurekaEntityComparators.equal(decoded, original));
    }

    @Test
    public void testInstanceInfoJacksonEncodeXStreamDecode() throws Exception {
        // Encode
        ByteArrayOutputStream captureStream = new ByteArrayOutputStream();
        codec.writeTo(EurekaJacksonCodecTest.INSTANCE_INFO_1_A1, captureStream);
        byte[] encoded = captureStream.toByteArray();
        // Decode
        InputStream source = new ByteArrayInputStream(encoded);
        InstanceInfo decoded = ((InstanceInfo) (new EntityBodyConverter().read(source, InstanceInfo.class, APPLICATION_JSON_TYPE)));
        Assert.assertTrue(EurekaEntityComparators.equal(decoded, EurekaJacksonCodecTest.INSTANCE_INFO_1_A1));
    }

    @Test
    public void testApplicationJacksonEncodeDecode() throws Exception {
        // Encode
        ByteArrayOutputStream captureStream = new ByteArrayOutputStream();
        codec.writeTo(EurekaJacksonCodecTest.APPLICATION_1, captureStream);
        byte[] encoded = captureStream.toByteArray();
        // Decode
        InputStream source = new ByteArrayInputStream(encoded);
        Application decoded = codec.readValue(Application.class, source);
        Assert.assertTrue(EurekaEntityComparators.equal(decoded, EurekaJacksonCodecTest.APPLICATION_1));
    }

    @Test
    public void testApplicationXStreamEncodeJacksonDecode() throws Exception {
        Application original = EurekaJacksonCodecTest.APPLICATION_1;
        // Encode
        ByteArrayOutputStream captureStream = new ByteArrayOutputStream();
        new EntityBodyConverter().write(original, captureStream, APPLICATION_JSON_TYPE);
        byte[] encoded = captureStream.toByteArray();
        // Decode
        InputStream source = new ByteArrayInputStream(encoded);
        Application decoded = codec.readValue(Application.class, source);
        Assert.assertTrue(EurekaEntityComparators.equal(decoded, original));
    }

    @Test
    public void testApplicationJacksonEncodeXStreamDecode() throws Exception {
        // Encode
        ByteArrayOutputStream captureStream = new ByteArrayOutputStream();
        codec.writeTo(EurekaJacksonCodecTest.APPLICATION_1, captureStream);
        byte[] encoded = captureStream.toByteArray();
        // Decode
        InputStream source = new ByteArrayInputStream(encoded);
        Application decoded = ((Application) (new EntityBodyConverter().read(source, Application.class, APPLICATION_JSON_TYPE)));
        Assert.assertTrue(EurekaEntityComparators.equal(decoded, EurekaJacksonCodecTest.APPLICATION_1));
    }

    @Test
    public void testApplicationsJacksonEncodeDecode() throws Exception {
        // Encode
        ByteArrayOutputStream captureStream = new ByteArrayOutputStream();
        codec.writeTo(EurekaJacksonCodecTest.APPLICATIONS, captureStream);
        byte[] encoded = captureStream.toByteArray();
        // Decode
        InputStream source = new ByteArrayInputStream(encoded);
        Applications decoded = codec.readValue(Applications.class, source);
        Assert.assertTrue(EurekaEntityComparators.equal(decoded, EurekaJacksonCodecTest.APPLICATIONS));
    }

    @Test
    public void testApplicationsXStreamEncodeJacksonDecode() throws Exception {
        Applications original = EurekaJacksonCodecTest.APPLICATIONS;
        // Encode
        ByteArrayOutputStream captureStream = new ByteArrayOutputStream();
        new EntityBodyConverter().write(original, captureStream, APPLICATION_JSON_TYPE);
        byte[] encoded = captureStream.toByteArray();
        String encodedString = new String(encoded);
        // Decode
        InputStream source = new ByteArrayInputStream(encoded);
        Applications decoded = codec.readValue(Applications.class, source);
        Assert.assertTrue(EurekaEntityComparators.equal(decoded, original));
    }

    @Test
    public void testApplicationsJacksonEncodeXStreamDecode() throws Exception {
        // Encode
        ByteArrayOutputStream captureStream = new ByteArrayOutputStream();
        codec.writeTo(EurekaJacksonCodecTest.APPLICATIONS, captureStream);
        byte[] encoded = captureStream.toByteArray();
        // Decode
        InputStream source = new ByteArrayInputStream(encoded);
        Applications decoded = ((Applications) (new EntityBodyConverter().read(source, Applications.class, APPLICATION_JSON_TYPE)));
        Assert.assertTrue(EurekaEntityComparators.equal(decoded, EurekaJacksonCodecTest.APPLICATIONS));
    }

    @Test
    public void testJacksonWriteToString() throws Exception {
        String jsonValue = codec.writeToString(EurekaJacksonCodecTest.INSTANCE_INFO_1_A1);
        InstanceInfo decoded = codec.readValue(InstanceInfo.class, new ByteArrayInputStream(jsonValue.getBytes(Charset.defaultCharset())));
        Assert.assertTrue(EurekaEntityComparators.equal(decoded, EurekaJacksonCodecTest.INSTANCE_INFO_1_A1));
    }

    @Test
    public void testJacksonWrite() throws Exception {
        // Encode
        ByteArrayOutputStream captureStream = new ByteArrayOutputStream();
        codec.writeTo(EurekaJacksonCodecTest.INSTANCE_INFO_1_A1, captureStream);
        byte[] encoded = captureStream.toByteArray();
        // Decode value
        InputStream source = new ByteArrayInputStream(encoded);
        InstanceInfo decoded = codec.readValue(InstanceInfo.class, source);
        Assert.assertTrue(EurekaEntityComparators.equal(decoded, EurekaJacksonCodecTest.INSTANCE_INFO_1_A1));
    }
}

