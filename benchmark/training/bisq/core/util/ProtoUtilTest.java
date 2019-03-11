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
package bisq.core.util;


import OfferPayload.Direction;
import OpenOffer.State.AVAILABLE;
import PB.OpenOffer.State;
import bisq.common.proto.ProtoUtil;
import io.bisq.generated.protobuffer.PB;
import io.bisq.generated.protobuffer.PB.OfferPayload;
import io.bisq.generated.protobuffer.PB.OpenOffer;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("UnusedAssignment")
public class ProtoUtilTest {
    // TODO Use NetworkProtoResolver, PersistenceProtoResolver or ProtoResolver which are all in bisq.common.
    @Test
    public void testEnum() {
        OfferPayload.Direction direction = Direction.SELL;
        OfferPayload.Direction direction2 = Direction.BUY;
        OfferPayload.Direction realDirection = ProtoUtilTest.getDirection(direction);
        OfferPayload.Direction realDirection2 = ProtoUtilTest.getDirection(direction2);
        Assert.assertEquals("SELL", realDirection.name());
        Assert.assertEquals("BUY", realDirection2.name());
    }

    @Test
    public void testUnknownEnum() {
        PB.OpenOffer.State result = State.PB_ERROR;
        try {
            bisq.core.offer.OpenOffer.State finalResult = OpenOffer.State.valueOf(result.name());
            Assert.fail();
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testUnknownEnumFix() {
        PB.OpenOffer.State result = State.PB_ERROR;
        try {
            bisq.core.offer.OpenOffer.State finalResult = ProtoUtil.enumFromProto(OpenOffer.State.class, result.name());
            Assert.assertEquals(AVAILABLE, ProtoUtil.enumFromProto(OpenOffer.State.class, "AVAILABLE"));
        } catch (IllegalArgumentException e) {
            Assert.fail();
        }
    }
}

