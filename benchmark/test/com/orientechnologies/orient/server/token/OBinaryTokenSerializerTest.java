package com.orientechnologies.orient.server.token;


import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.server.binary.impl.OBinaryToken;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


public class OBinaryTokenSerializerTest {
    private OBinaryTokenSerializer ser = new OBinaryTokenSerializer(new String[]{ "plocal", "memory" }, new String[]{ "key" }, new String[]{ "HmacSHA256" }, new String[]{ "OrientDB" });

    @Test
    public void testSerializerDeserializeToken() throws IOException {
        OBinaryToken token = new OBinaryToken();
        token.setDatabase("test");
        token.setDatabaseType("plocal");
        token.setUserRid(new ORecordId(43, 234));
        OrientJwtHeader header = new OrientJwtHeader();
        header.setKeyId("key");
        header.setAlgorithm("HmacSHA256");
        header.setType("OrientDB");
        token.setHeader(header);
        token.setExpiry(20L);
        token.setProtocolVersion(((short) (2)));
        token.setSerializer("ser");
        token.setDriverName("aa");
        token.setDriverVersion("aa");
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        ser.serialize(token, bas);
        ByteArrayInputStream input = new ByteArrayInputStream(bas.toByteArray());
        OBinaryToken tok = ser.deserialize(input);
        Assert.assertEquals("test", token.getDatabase());
        Assert.assertEquals("plocal", token.getDatabaseType());
        ORID id = token.getUserId();
        Assert.assertEquals(43, id.getClusterId());
        Assert.assertEquals(20L, tok.getExpiry());
        Assert.assertEquals("OrientDB", tok.getHeader().getType());
        Assert.assertEquals("HmacSHA256", tok.getHeader().getAlgorithm());
        Assert.assertEquals("key", tok.getHeader().getKeyId());
        Assert.assertEquals(((short) (2)), tok.getProtocolVersion());
        Assert.assertEquals("ser", tok.getSerializer());
        Assert.assertEquals("aa", tok.getDriverName());
        Assert.assertEquals("aa", tok.getDriverVersion());
    }

    @Test
    public void testSerializerDeserializeServerUserToken() throws IOException {
        OBinaryToken token = new OBinaryToken();
        token.setDatabase("test");
        token.setDatabaseType("plocal");
        token.setUserRid(new ORecordId(43, 234));
        OrientJwtHeader header = new OrientJwtHeader();
        header.setKeyId("key");
        header.setAlgorithm("HmacSHA256");
        header.setType("OrientDB");
        token.setHeader(header);
        token.setExpiry(20L);
        token.setServerUser(true);
        token.setUserName("aaa");
        token.setProtocolVersion(((short) (2)));
        token.setSerializer("ser");
        token.setDriverName("aa");
        token.setDriverVersion("aa");
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        ser.serialize(token, bas);
        ByteArrayInputStream input = new ByteArrayInputStream(bas.toByteArray());
        OBinaryToken tok = ser.deserialize(input);
        Assert.assertEquals("test", token.getDatabase());
        Assert.assertEquals("plocal", token.getDatabaseType());
        ORID id = token.getUserId();
        Assert.assertEquals(43, id.getClusterId());
        Assert.assertEquals(20L, tok.getExpiry());
        TestCase.assertTrue(token.isServerUser());
        Assert.assertEquals("aaa", tok.getUserName());
        Assert.assertEquals("OrientDB", tok.getHeader().getType());
        Assert.assertEquals("HmacSHA256", tok.getHeader().getAlgorithm());
        Assert.assertEquals("key", tok.getHeader().getKeyId());
        Assert.assertEquals(((short) (2)), tok.getProtocolVersion());
        Assert.assertEquals("ser", tok.getSerializer());
        Assert.assertEquals("aa", tok.getDriverName());
        Assert.assertEquals("aa", tok.getDriverVersion());
    }

    @Test
    public void testSerializerDeserializeNullInfoUserToken() throws IOException {
        OBinaryToken token = new OBinaryToken();
        token.setDatabase(null);
        token.setDatabaseType(null);
        token.setUserRid(null);
        OrientJwtHeader header = new OrientJwtHeader();
        header.setKeyId("key");
        header.setAlgorithm("HmacSHA256");
        header.setType("OrientDB");
        token.setHeader(header);
        token.setExpiry(20L);
        token.setServerUser(true);
        token.setUserName("aaa");
        token.setProtocolVersion(((short) (2)));
        token.setSerializer("ser");
        token.setDriverName("aa");
        token.setDriverVersion("aa");
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        ser.serialize(token, bas);
        ByteArrayInputStream input = new ByteArrayInputStream(bas.toByteArray());
        OBinaryToken tok = ser.deserialize(input);
        Assert.assertNull(token.getDatabase());
        Assert.assertNull(token.getDatabaseType());
        ORID id = token.getUserId();
        Assert.assertNull(id);
        Assert.assertEquals(20L, tok.getExpiry());
        TestCase.assertTrue(token.isServerUser());
        Assert.assertEquals("aaa", tok.getUserName());
        Assert.assertEquals("OrientDB", tok.getHeader().getType());
        Assert.assertEquals("HmacSHA256", tok.getHeader().getAlgorithm());
        Assert.assertEquals("key", tok.getHeader().getKeyId());
        Assert.assertEquals(((short) (2)), tok.getProtocolVersion());
        Assert.assertEquals("ser", tok.getSerializer());
        Assert.assertEquals("aa", tok.getDriverName());
        Assert.assertEquals("aa", tok.getDriverVersion());
    }
}

