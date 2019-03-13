package com.orientechnologies.orient.client.remote.message;


import OChannelBinaryProtocol.CURRENT_PROTOCOL_VERSION;
import com.orientechnologies.orient.client.remote.message.push.OStorageConfigurationPayload;
import com.orientechnologies.orient.core.config.OStorageConfiguration;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class OReloadMessageTest {
    private OrientDB orientDB;

    private ODatabaseSession session;

    @Test
    public void testWriteReadResponse() throws IOException {
        OStorageConfiguration configuration = getStorage().getConfiguration();
        OReloadResponse37 responseWrite = new OReloadResponse37(configuration);
        MockChannel channel = new MockChannel();
        responseWrite.write(channel, CURRENT_PROTOCOL_VERSION, null);
        channel.close();
        OReloadResponse37 responseRead = new OReloadResponse37();
        responseRead.read(channel, null);
        OStorageConfigurationPayload payload = responseRead.getPayload();
        Assert.assertEquals(configuration.getProperties().size(), payload.getProperties().size());
        for (int i = 0; i < (configuration.getProperties().size()); i++) {
            Assert.assertEquals(configuration.getProperties().get(i).name, payload.getProperties().get(i).name);
            Assert.assertEquals(configuration.getProperties().get(i).value, payload.getProperties().get(i).value);
        }
        Assert.assertEquals(configuration.getDateFormat(), payload.getDateFormat());
        Assert.assertEquals(configuration.getDateTimeFormat(), payload.getDateTimeFormat());
        Assert.assertEquals(configuration.getName(), payload.getName());
        Assert.assertEquals(configuration.getVersion(), payload.getVersion());
        Assert.assertEquals(configuration.getDirectory(), payload.getDirectory());
        Assert.assertEquals(configuration.getSchemaRecordId(), payload.getSchemaRecordId().toString());
        Assert.assertEquals(configuration.getIndexMgrRecordId(), payload.getIndexMgrRecordId().toString());
        Assert.assertEquals(configuration.getClusterSelection(), payload.getClusterSelection());
        Assert.assertEquals(configuration.getConflictStrategy(), payload.getConflictStrategy());
        Assert.assertEquals(configuration.isValidationEnabled(), payload.isValidationEnabled());
        Assert.assertEquals(configuration.getLocaleLanguage(), payload.getLocaleLanguage());
        Assert.assertEquals(configuration.getMinimumClusters(), payload.getMinimumClusters());
        Assert.assertEquals(configuration.isStrictSql(), payload.isStrictSql());
        Assert.assertEquals(configuration.getCharset(), payload.getCharset());
        Assert.assertEquals(configuration.getLocaleCountry(), payload.getLocaleCountry());
        Assert.assertEquals(configuration.getTimeZone(), payload.getTimeZone());
        Assert.assertEquals(configuration.getRecordSerializer(), payload.getRecordSerializer());
        Assert.assertEquals(configuration.getRecordSerializerVersion(), payload.getRecordSerializerVersion());
        Assert.assertEquals(configuration.getBinaryFormatVersion(), payload.getBinaryFormatVersion());
        Assert.assertEquals(configuration.getClusters().size(), payload.getClusters().size());
        for (int i = 0; i < (configuration.getClusters().size()); i++) {
            Assert.assertEquals(configuration.getClusters().get(i).getId(), payload.getClusters().get(i).getId());
            Assert.assertEquals(configuration.getClusters().get(i).getName(), payload.getClusters().get(i).getName());
        }
    }
}

