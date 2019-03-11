/**
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */
package bisq.core.message;


import PB.NetworkEnvelope;
import PB.Ping;
import PB.Pong;
import io.bisq.generated.protobuffer.PB;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;


@Slf4j
public class MarshallerTest {
    @Test
    public void getBaseEnvelopeTest() {
        PB.Ping Ping = Ping.newBuilder().setNonce(100).build();
        PB.Pong Pong = Pong.newBuilder().setRequestNonce(1000).build();
        PB.NetworkEnvelope envelope1 = NetworkEnvelope.newBuilder().setPing(Ping).build();
        PB.NetworkEnvelope envelope2 = NetworkEnvelope.newBuilder().setPong(Pong).build();
        log.info(Ping.toString());
        log.info(Pong.toString());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            envelope1.writeDelimitedTo(outputStream);
            envelope2.writeDelimitedTo(outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            PB.NetworkEnvelope envelope3 = NetworkEnvelope.parseDelimitedFrom(inputStream);
            PB.NetworkEnvelope envelope4 = NetworkEnvelope.parseDelimitedFrom(inputStream);
            log.info("message: {}", envelope3.getPing());
            // log.info("peerseesd empty: '{}'",envelope3.getPong().equals(PB.NetworkEnvelope.) == "");
            Assert.assertTrue(isPing(envelope3));
            Assert.assertTrue((!(isPing(envelope4))));
            log.info("3 = {} 4 = {}", isPing(envelope3), isPing(envelope4));
            log.info(envelope3.toString());
            log.info(envelope4.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

