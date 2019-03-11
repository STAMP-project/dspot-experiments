package com.orientechnologies.orient.server.token;


import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.metadata.security.OToken;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.metadata.security.jwt.OJwtHeader;
import com.orientechnologies.orient.core.metadata.security.jwt.OJwtPayload;
import com.orientechnologies.orient.server.network.protocol.ONetworkProtocolData;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.junit.Assert;
import org.junit.Test;


public class OTokenHandlerImplTest {
    @Test(expected = Exception.class)
    public void testInvalidToken() throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        OTokenHandlerImpl handler = new OTokenHandlerImpl("any key".getBytes(), 60, "HmacSHA256");
        handler.parseWebToken("random".getBytes());
    }

    @Test
    public void testSerializeDeserializeWebHeader() throws Exception {
        OJwtHeader header = new OrientJwtHeader();
        header.setType("Orient");
        header.setAlgorithm("some");
        header.setKeyId("the_key");
        OTokenHandlerImpl handler = new OTokenHandlerImpl();
        byte[] headerbytes = handler.serializeWebHeader(header);
        OJwtHeader des = handler.deserializeWebHeader(headerbytes);
        Assert.assertNotNull(des);
        Assert.assertEquals(header.getType(), des.getType());
        Assert.assertEquals(header.getKeyId(), des.getKeyId());
        Assert.assertEquals(header.getAlgorithm(), des.getAlgorithm());
        Assert.assertEquals(header.getType(), des.getType());
    }

    @Test
    public void testSerializeDeserializeWebPayload() throws Exception {
        OrientJwtPayload payload = new OrientJwtPayload();
        String ptype = "OrientDB";
        payload.setAudience("audiance");
        payload.setExpiry(1L);
        payload.setIssuedAt(2L);
        payload.setIssuer("orient");
        payload.setNotBefore(3L);
        payload.setUserName("the subject");
        payload.setTokenId("aaa");
        payload.setUserRid(new ORecordId(3, 4));
        OTokenHandlerImpl handler = new OTokenHandlerImpl();
        byte[] payloadbytes = handler.serializeWebPayload(payload);
        OJwtPayload des = handler.deserializeWebPayload(ptype, payloadbytes);
        Assert.assertNotNull(des);
        Assert.assertEquals(payload.getAudience(), des.getAudience());
        Assert.assertEquals(payload.getExpiry(), des.getExpiry());
        Assert.assertEquals(payload.getIssuedAt(), des.getIssuedAt());
        Assert.assertEquals(payload.getIssuer(), des.getIssuer());
        Assert.assertEquals(payload.getNotBefore(), des.getNotBefore());
        Assert.assertEquals(payload.getTokenId(), des.getTokenId());
    }

    @Test
    public void testTokenForge() throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:" + (OTokenHandlerImplTest.class.getSimpleName())));
        db.create();
        try {
            OSecurityUser original = db.getUser();
            OTokenHandlerImpl handler = new OTokenHandlerImpl("any key".getBytes(), 60, "HmacSHA256");
            byte[] token = handler.getSignedWebToken(db, original);
            byte[] token2 = handler.getSignedWebToken(db, original);
            String s = new String(token);
            String s2 = new String(token2);
            String newS = (s.substring(0, s.lastIndexOf('.'))) + (s2.substring(s2.lastIndexOf('.')));
            OToken tok = handler.parseWebToken(newS.getBytes());
            Assert.assertNotNull(tok);
            Assert.assertFalse(tok.getIsVerified());
        } finally {
            db.drop();
        }
    }

    @Test
    public void testBinartTokenCreationValidation() throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:" + (OTokenHandlerImplTest.class.getSimpleName())));
        db.create();
        try {
            OSecurityUser original = db.getUser();
            OTokenHandlerImpl handler = new OTokenHandlerImpl("any key".getBytes(), 60, "HmacSHA256");
            ONetworkProtocolData data = new ONetworkProtocolData();
            data.driverName = "aa";
            data.driverVersion = "aa";
            data.setSerializationImpl("a");
            data.protocolVersion = 2;
            byte[] token = handler.getSignedBinaryToken(db, original, data);
            OToken tok = handler.parseBinaryToken(token);
            Assert.assertNotNull(tok);
            Assert.assertTrue(tok.getIsVerified());
            OUser user = tok.getUser(db);
            Assert.assertEquals(user.getName(), original.getName());
            boolean boole = handler.validateBinaryToken(tok);
            Assert.assertTrue(boole);
            Assert.assertTrue(tok.getIsValid());
        } finally {
            db.drop();
        }
    }

    @Test
    public void testTokenNotRenew() {
        ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:" + (OTokenHandlerImplTest.class.getSimpleName())));
        db.create();
        try {
            OSecurityUser original = db.getUser();
            OTokenHandlerImpl handler = new OTokenHandlerImpl("any key".getBytes(), 60, "HmacSHA256");
            ONetworkProtocolData data = new ONetworkProtocolData();
            data.driverName = "aa";
            data.driverVersion = "aa";
            data.setSerializationImpl("a");
            data.protocolVersion = 2;
            byte[] token = handler.getSignedBinaryToken(db, original, data);
            OToken tok = handler.parseBinaryToken(token);
            token = handler.renewIfNeeded(tok);
            Assert.assertEquals(0, token.length);
        } finally {
            db.drop();
        }
    }

    @Test
    public void testTokenRenew() {
        ODatabaseDocumentTx db = new ODatabaseDocumentTx(("memory:" + (OTokenHandlerImplTest.class.getSimpleName())));
        db.create();
        try {
            OSecurityUser original = db.getUser();
            OTokenHandlerImpl handler = new OTokenHandlerImpl("any key".getBytes(), 60, "HmacSHA256");
            ONetworkProtocolData data = new ONetworkProtocolData();
            data.driverName = "aa";
            data.driverVersion = "aa";
            data.setSerializationImpl("a");
            data.protocolVersion = 2;
            byte[] token = handler.getSignedBinaryToken(db, original, data);
            OToken tok = handler.parseBinaryToken(token);
            tok.setExpiry((((System.currentTimeMillis()) + ((handler.getSessionInMills()) / 2)) - 1));
            token = handler.renewIfNeeded(tok);
            Assert.assertTrue(((token.length) != 0));
        } finally {
            db.drop();
        }
    }
}

