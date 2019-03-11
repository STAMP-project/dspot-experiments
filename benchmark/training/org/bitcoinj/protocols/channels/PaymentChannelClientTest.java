/**
 * Copyright by the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitcoinj.protocols.channels;


import IPaymentChannelClient.ClientChannelProperties;
import IPaymentChannelClient.ClientConnection;
import PaymentChannelClient.VersionSelector;
import java.util.HashMap;
import org.bitcoinj.wallet.Wallet;
import org.bouncycastle.crypto.params.KeyParameter;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class PaymentChannelClientTest {
    private Wallet wallet;

    private ECKey ecKey;

    private Sha256Hash serverHash;

    private ClientConnection connection;

    public Coin maxValue;

    public Capture<TwoWayChannelMessage> clientVersionCapture;

    public int defaultTimeWindow = 86340;

    @Parameterized.Parameter
    public ClientChannelProperties clientChannelProperties;

    @Test
    public void shouldSendClientVersionOnChannelOpen() throws Exception {
        PaymentChannelClient dut = new PaymentChannelClient(wallet, ecKey, maxValue, serverHash, null, clientChannelProperties, connection);
        connection.sendToServer(capture(clientVersionCapture));
        EasyMock.expect(wallet.getExtensions()).andReturn(new HashMap<String, org.bitcoinj.wallet.WalletExtension>());
        replay(connection, wallet);
        dut.connectionOpen();
        assertClientVersion(defaultTimeWindow);
    }

    @Test
    public void shouldSendTimeWindowInClientVersion() throws Exception {
        final long timeWindow = 4000;
        KeyParameter userKey = null;
        PaymentChannelClient dut = new PaymentChannelClient(wallet, ecKey, maxValue, serverHash, userKey, new PaymentChannelClient.DefaultClientChannelProperties() {
            @Override
            public long timeWindow() {
                return timeWindow;
            }

            @Override
            public VersionSelector versionSelector() {
                return clientChannelProperties.versionSelector();
            }
        }, connection);
        connection.sendToServer(capture(clientVersionCapture));
        EasyMock.expect(wallet.getExtensions()).andReturn(new HashMap<String, org.bitcoinj.wallet.WalletExtension>());
        replay(connection, wallet);
        dut.connectionOpen();
        assertClientVersion(4000);
    }
}

