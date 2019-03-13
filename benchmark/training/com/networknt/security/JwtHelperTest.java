/**
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.networknt.security;


import Constants.USER_ID_STRING;
import JwtHelper.JWT_CERTIFICATE;
import JwtHelper.SECURITY_CONFIG;
import JwtIssuer.JWT_CONFIG;
import com.networknt.config.Config;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.jose4j.jwt.JwtClaims;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by steve on 01/09/16.
 */
public class JwtHelperTest {
    @Test
    public void testReadCertificate() {
        Map<String, Object> securityConfig = Config.getInstance().getJsonMapConfig(SECURITY_CONFIG);
        Map<String, Object> jwtConfig = ((Map<String, Object>) (securityConfig.get(JWT_CONFIG)));
        Map<String, Object> keyMap = ((Map<String, Object>) (jwtConfig.get(JWT_CERTIFICATE)));
        Map<String, X509Certificate> certMap = new HashMap<>();
        for (String kid : keyMap.keySet()) {
            X509Certificate cert = null;
            try {
                cert = JwtHelper.readCertificate(((String) (keyMap.get(kid))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            certMap.put(kid, cert);
        }
        Assert.assertEquals(2, certMap.size());
    }

    @Test
    public void testVerifyJwt() throws Exception {
        JwtClaims claims = ClaimsUtil.getTestClaims("steve", "EMPLOYEE", "f7d42348-c647-4efb-a52d-4c5787421e72", Arrays.asList("write:pets", "read:pets"));
        String jwt = JwtIssuer.getJwt(claims);
        claims = null;
        Assert.assertNotNull(jwt);
        try {
            claims = JwtHelper.verifyJwt(jwt, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(claims);
        Assert.assertEquals("steve", claims.getStringClaimValue(USER_ID_STRING));
        try {
            claims = JwtHelper.verifyJwt(jwt, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(("jwtClaims = " + claims));
    }
}

