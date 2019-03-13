/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
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
package org.keycloak;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.keycloak.representations.oidc.OIDCClientRepresentation;
import org.keycloak.util.JsonSerialization;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class JsonParserTest {
    @Test
    public void regex() throws Exception {
        Pattern p = Pattern.compile(".*(?!\\.pdf)");
        if (p.matcher("foo.pdf").matches()) {
            System.out.println(".pdf no match");
        }
        if (p.matcher("foo.txt").matches()) {
            System.out.println("foo.txt matches");
        }
    }

    @Test
    public void testOtherClaims() throws Exception {
        String json = "{ \"floatData\" : 555.5," + (("\"boolData\": true, " + "\"intData\": 1234,") + "\"array\": [ \"val\", \"val2\"] }");
        JsonWebToken token = JsonSerialization.readValue(json, JsonWebToken.class);
        System.out.println(token.getOtherClaims().get("floatData").getClass().getName());
        System.out.println(token.getOtherClaims().get("boolData").getClass().getName());
        System.out.println(token.getOtherClaims().get("intData").getClass().getName());
        System.out.println(token.getOtherClaims().get("array").getClass().getName());
    }

    @Test
    public void testUnwrap() throws Exception {
        // just experimenting with unwrapped and any properties
        IDToken test = new IDToken();
        test.getOtherClaims().put("phone_number", "978-666-0000");
        test.getOtherClaims().put("email_verified", "true");
        test.getOtherClaims().put("yo", "true");
        Map<String, String> nested = new HashMap<String, String>();
        nested.put("foo", "bar");
        test.getOtherClaims().put("nested", nested);
        String json = JsonSerialization.writeValueAsPrettyString(test);
        System.out.println(json);
        test = JsonSerialization.readValue(json, IDToken.class);
        System.out.println(("email_verified property: " + (test.getEmailVerified())));
        System.out.println(("property: " + (test.getPhoneNumber())));
        System.out.println(("map: " + (test.getOtherClaims().get("phone_number"))));
        Assert.assertNotNull(test.getPhoneNumber());
        Assert.assertNotNull(test.getOtherClaims().get("yo"));
        Assert.assertNull(test.getOtherClaims().get("phone_number"));
        nested = ((Map<String, String>) (test.getOtherClaims().get("nested")));
        Assert.assertNotNull(nested);
        Assert.assertNotNull(nested.get("foo"));
    }

    @Test
    public void testParsingSystemProps() throws IOException {
        System.setProperty("my.host", "foo");
        System.setProperty("con.pool.size", "200");
        System.setProperty("allow.any.hostname", "true");
        InputStream is = getClass().getClassLoader().getResourceAsStream("keycloak.json");
        AdapterConfig config = JsonSerialization.readValue(is, AdapterConfig.class, true);
        Assert.assertEquals("http://foo:8080/auth", config.getAuthServerUrl());
        Assert.assertEquals("external", config.getSslRequired());
        Assert.assertEquals("angular-product${non.existing}", config.getResource());
        Assert.assertTrue(config.isPublicClient());
        Assert.assertTrue(config.isAllowAnyHostname());
        Assert.assertEquals(100, config.getCorsMaxAge());
        Assert.assertEquals(200, config.getConnectionPoolSize());
    }

    static Pattern substitution = Pattern.compile("\\$\\{([^}]+)\\}");

    @Test
    public void testSub() {
        String pattern = "${ALIAS}.${CRAP}";
        Matcher m = JsonParserTest.substitution.matcher(pattern);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            System.out.println(("GROUP: " + (m.group(1))));
            m.appendReplacement(sb, m.group(1));
        } 
        m.appendTail(sb);
        System.out.println(sb.toString());
    }

    @Test
    public void testReadOIDCClientRep() throws IOException {
        String stringRep = "{\"subject_type\": \"public\", \"jwks_uri\": \"https://op.certification.openid.net:60720/export/jwk_60720.json\", \"contacts\": [\"roland.hedberg@umu.se\"], \"application_type\": \"web\", \"grant_types\": [\"authorization_code\"], \"post_logout_redirect_uris\": [\"https://op.certification.openid.net:60720/logout\"], \"redirect_uris\": [\"https://op.certification.openid.net:60720/authz_cb\"], \"response_types\": [\"code\"], \"require_auth_time\": true, \"default_max_age\": 3600}";
        OIDCClientRepresentation clientRep = JsonSerialization.readValue(stringRep, OIDCClientRepresentation.class);
        Assert.assertEquals("public", clientRep.getSubjectType());
        Assert.assertTrue(clientRep.getRequireAuthTime());
        Assert.assertEquals(3600, clientRep.getDefaultMaxAge().intValue());
        Assert.assertEquals(1, clientRep.getRedirectUris().size());
        Assert.assertEquals("https://op.certification.openid.net:60720/authz_cb", clientRep.getRedirectUris().get(0));
        Assert.assertNull(clientRep.getJwks());
    }

    @Test
    public void testReadOIDCClientRepWithPairwise() throws IOException {
        String stringRep = "{\"subject_type\": \"pairwise\", \"jwks_uri\": \"https://op.certification.openid.net:60720/export/jwk_60720.json\", \"contacts\": [\"roland.hedberg@umu.se\"], \"application_type\": \"web\", \"grant_types\": [\"authorization_code\"], \"post_logout_redirect_uris\": [\"https://op.certification.openid.net:60720/logout\"], \"redirect_uris\": [\"https://op.certification.openid.net:60720/authz_cb\"], \"response_types\": [\"code\"], \"require_auth_time\": true, \"default_max_age\": 3600}";
        OIDCClientRepresentation clientRep = JsonSerialization.readValue(stringRep, OIDCClientRepresentation.class);
        Assert.assertEquals("pairwise", clientRep.getSubjectType());
        Assert.assertTrue(clientRep.getRequireAuthTime());
        Assert.assertEquals(3600, clientRep.getDefaultMaxAge().intValue());
        Assert.assertEquals(1, clientRep.getRedirectUris().size());
        Assert.assertEquals("https://op.certification.openid.net:60720/authz_cb", clientRep.getRedirectUris().get(0));
        Assert.assertNull(clientRep.getJwks());
    }

    @Test
    public void testReadOIDCClientRepWithJWKS() throws IOException {
        String stringRep = "{\"token_endpoint_auth_method\": \"private_key_jwt\", \"subject_type\": \"public\", \"jwks_uri\": null, \"jwks\": {\"keys\": [{\"use\": \"enc\", \"e\": \"AQAB\", \"d\": \"lZQv0_81euRLeUYU84Aodh0ar7ymDlzWP5NMra4Jklkb-lTBWkI-u4RMsPqGYyW3KHRoL_pgzZXSzQx8RLQfER6timRWb--NxMMKllZubByU3RqH2ooNuocJurspYiXkznPW1Mg9DaNXL0C2hwWPQHTeUVISpjgi5TCOV1ccWVyksFruya_VNL1CIByB-L0GL1rqbKv32cDwi2A3_jJa61cpzfLSIBe-lvCO6tuiDsR4qgJnUwnndQFwEI_4mLmD3iNWXrc8N-poleV8mBfMqBB5fWwy_ZTFCpmQ5AywGmctaik_wNhMoWuA4tUfY6_1LdKld-5Cjq55eLtuJjtvuQ\", \"n\": \"tx3Hjdbc19lkTiohbJrNj4jf2_90MEE122CRrwtFu6saDywKcG7Bi7w2FMAK2oTkuWfqhWRb5BEGmnSXdiCEPO5d-ytqP3nwlZXHaCDYscpP8bB4YLhvCn7R8Efw6gwQle24QPRP3lYoFeuUbDUq7GKA5SfaZUvWoeWjqyLIaBspKQsC26_Umx1E4IXLrMSL6nkRnrYcVZBAXrYCeTP1XtsV38_lZVJfHSaJaUy4PKaj3yvgm93EV2CXybPti7CCMXZ34VqqWiF64pQjZsPu3ZTr7ha_TTQq499-zYRQNDvIVsBDLQQIgrbctuGqj6lrXb31Jj3JIEYqH_4h5X9d0Q\", \"q\": \"1q-r-bmMFbIzrLK2U3elksZq8CqUqZxlSfkGMZuVkxgYMS-e4FPzEp2iirG-eO11aa0cpMMoBdTnVdGJ_ZUR93w0lGf9XnQAJqxP7eOsrUoiW4VWlWH4WfOiLgpO-pFtyTz_JksYYaotc_Z3Zy-Szw6a39IDbuYGy1qL-15oQuc\", \"p\": \"2lrYPppRbcQWu4LtWN6tOVUrtCOPv1eLTKTc7q8vCMcem1Ox5QFB7KnUtNZ5Ni7wnZUeVDfimNebtjNsGvDSrpgIlo9dEnFBQsQIkzZ2SkoYfgmF8hNdi6P-BfRjdgYouy4c6xAnGDgSMTip1YnPRyvbMaoYT9E_tEcBW5wOeoc\", \"kid\": \"a0\", \"kty\": \"RSA\"}, {\"use\": \"sig\", \"e\": \"AQAB\", \"d\": \"DodXDEtkovWWGsMEXYy_nEEMCWyROMOebCnCv0ey3i4M4bh2dmwqgz0e-IKQAFlGiMkidGL1lNbq0uFS04FbuRAR06dYw1cbrNbDdhrWFxKTd1L5D9p-x-gW-YDWhpI8rUGRa76JXkOSxZUbg09_QyUd99CXAHh-FXi_ZkIKD8hK6FrAs68qhLf8MNkUv63DTduw7QgeFfQivdopePxyGuMk5n8veqwsUZsklQkhNlTYQqeM1xb2698ZQcNYkl0OssEsSJKRjXt-LRPowKrdvTuTo2p--HMI0pIEeFs7H_u5OW3jihjvoFClGPynHQhgWmQzlQRvWRXh6FhDVqFeGQ\", \"n\": \"zfZzttF7HmnTYwSMPdxKs5AoczbNS2mOPz-tN1g4ljqI_F1DG8cgQDcN_VDufxoFGRERo2FK6WEN41LhbGEyP6uL6wW6Cy29qE9QZcvY5mXrncndRSOkNcMizvuEJes_fMYrmP_lPiC6kWiqItTk9QBWqJfiYKhCx9cSDXsBmJXn3KWQCVHvj1ANFWW0CWLMKlWN-_NMNLIWJN_pEAocTZMzxSFBK1b5_5J8ZS7hfWRF6MQmjsJcz2jzA21SQZNpre3kwnTGRSwo05sAS-TyeadDqQPWgbqX69UzcGq5irhzN8cpZ_JaTk3Y_uV6owanTZLVvCgdjaAnMYeZhb0KFw\", \"q\": \"5E5XKK5njT-zzRqqTeY2tgP9PJBACeaH_xQRHZ_1ydE7tVd7HdgdaEHfQ1jvKIHFkknWWOBAY1mlBc4YDirLShB_voShD8C-Hx3nF5sne5fleVfU-sZy6Za4B2U75PcE62oZgCPauOTAEm9Xuvrt5aMMovyzR8ecJZhm9bw7naU\", \"p\": \"5vJHCSM3H3q4RltYzENC9RyZZV8EUmpkv9moyguT5t-BUGA-T4W_FGIxzOPXRWOckIplKkoDKhavUeNmTZMCUcue0nkICSJpvNE4Nb2p5PZk_QqSdQNvCasQtdojEG0AmfVD85SU551CYxJdLdDFOqyK2entpMr8lhokem189As\", \"kid\": \"a1\", \"kty\": \"RSA\"}, {\"d\": \"S4_OufhLBgXFMgIDMI1zlVe2uCExpcEAQ80J_lXfS8I\", \"use\": \"sig\", \"crv\": \"P-256\", \"kty\": \"EC\", \"y\": \"DBdNyq30mXmUs_BIvKMqaTTNO7HDhCi0YiC8GciwNYk\", \"x\": \"cYwzBoyjRjxj334bRTqanONf7DUYK-6TgiuN0DixJAk\", \"kid\": \"a2\"}, {\"d\": \"33TnYgdJtWAiVosKqUnz0zSmvWTbsx5-6pceynW6Xck\", \"use\": \"enc\", \"crv\": \"P-256\", \"kty\": \"EC\", \"y\": \"Cula95Eix1Ia77St3OULe6-UKWs5I06nmdfUzhXUQTs\", \"x\": \"wk8HBVxNNzj1gJBxPmmx9XYW1L61ObBGzxpRa6_OqWU\", \"kid\": \"a3\"}]}, \"application_type\": \"web\", \"contacts\": [\"roland.hedberg@umu.se\"], \"post_logout_redirect_uris\": [\"https://op.certification.openid.net:60784/logout\"], \"redirect_uris\": [\"https://op.certification.openid.net:60784/authz_cb\"], \"response_types\": [\"code\"], \"require_auth_time\": true, \"grant_types\": [\"authorization_code\"], \"default_max_age\": 3600}";
        OIDCClientRepresentation clientRep = JsonSerialization.readValue(stringRep, OIDCClientRepresentation.class);
        Assert.assertNotNull(clientRep.getJwks());
    }

    @Test
    public void testResourceRepresentationParsing() throws Exception {
        Map<String, Object> resource = parseResourceRepresentation("{ \"_id\": \"123\", \"name\": \"foo\" }");
        Assert.assertFalse(resource.containsKey("uri"));
        Assert.assertFalse(resource.containsKey("uris"));
        resource = parseResourceRepresentation("{ \"_id\": \"123\", \"name\": \"foo\", \"uris\": [ \"uri1\", \"uri2\" ] }");
        Assert.assertFalse(resource.containsKey("uri"));
        Assert.assertTrue(resource.containsKey("uris"));
        Collection<String> uris = ((Collection) (resource.get("uris")));
        Assert.assertEquals(2, uris.size());
        Assert.assertTrue(uris.contains("uri1"));
        Assert.assertTrue(uris.contains("uri2"));
        // Backwards compatibility (using old property "uri")
        resource = parseResourceRepresentation("{ \"_id\": \"123\", \"name\": \"foo\", \"uri\": \"uri1\" }");
        Assert.assertFalse(resource.containsKey("uri"));
        Assert.assertTrue(resource.containsKey("uris"));
        uris = ((Collection) (resource.get("uris")));
        Assert.assertEquals(1, uris.size());
        Assert.assertTrue(uris.contains("uri1"));
    }
}

