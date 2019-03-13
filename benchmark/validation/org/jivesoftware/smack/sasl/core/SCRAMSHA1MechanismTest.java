/**
 * Copyright 2014-2016 Florian Schmaus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smack.sasl.core;


import JidTestUtil.DOMAIN_BARE_JID_1;
import SCRAMSHA1Mechanism.NAME;
import org.jivesoftware.smack.DummyConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.sasl.packet.SaslStreamElements.AuthMechanism;
import org.jivesoftware.smack.sasl.packet.SaslStreamElements.Response;
import org.jivesoftware.smack.test.util.SmackTestSuite;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.junit.Assert;
import org.junit.Test;


public class SCRAMSHA1MechanismTest extends SmackTestSuite {
    public static final String USERNAME = "user";

    public static final String PASSWORD = "pencil";

    public static final String CLIENT_FIRST_MESSAGE = "n,,n=user,r=fyko+d2lbbFgONRv9qkxdawL";

    public static final String SERVER_FIRST_MESSAGE = "r=fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j,s=QSXCR+Q6sek8bf92,i=4096";

    public static final String CLIENT_FINAL_MESSAGE = "c=biws,r=fyko+d2lbbFgONRv9qkxdawL3rfcNHYJY1ZVvWVs7j,p=v0X8v3Bz2T0CJGbJQyF0X+HI4Ts=";

    public static final String SERVER_FINAL_MESSAGE = "v=rmF9pqV8S7suAoZWja4dJRkFsKQ=";

    @Test
    public void testScramSha1Mechanism() throws InterruptedException, SmackException, NotConnectedException {
        final DummyConnection con = new DummyConnection();
        SCRAMSHA1Mechanism mech = new SCRAMSHA1Mechanism() {
            @Override
            public String getRandomAscii() {
                this.connection = con;
                return "fyko+d2lbbFgONRv9qkxdawL";
            }
        };
        mech.authenticate(SCRAMSHA1MechanismTest.USERNAME, "unusedFoo", DOMAIN_BARE_JID_1, SCRAMSHA1MechanismTest.PASSWORD, null, null);
        AuthMechanism authMechanism = con.getSentPacket();
        Assert.assertEquals(NAME, authMechanism.getMechanism());
        Assert.assertEquals(SCRAMSHA1MechanismTest.CLIENT_FIRST_MESSAGE, SCRAMSHA1MechanismTest.saslLayerString(authMechanism.getAuthenticationText()));
        mech.challengeReceived(Base64.encode(SCRAMSHA1MechanismTest.SERVER_FIRST_MESSAGE), false);
        Response response = con.getSentPacket();
        Assert.assertEquals(SCRAMSHA1MechanismTest.CLIENT_FINAL_MESSAGE, SCRAMSHA1MechanismTest.saslLayerString(response.getAuthenticationText()));
        mech.challengeReceived(Base64.encode(SCRAMSHA1MechanismTest.SERVER_FINAL_MESSAGE), true);
        mech.checkIfSuccessfulOrThrow();
    }
}

