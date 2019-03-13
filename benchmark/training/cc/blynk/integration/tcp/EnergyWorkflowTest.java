package cc.blynk.integration.tcp;


import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.ClientPair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class EnergyWorkflowTest extends SingleServerInstancePerTest {
    @Test
    public void testReach1500LimitOfEnergy() throws Exception {
        clientPair.appClient.createDash("{\"id\":2, \"createdAt\":1458856800001, \"name\":\"test board\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        for (int i = 2; i < 12; i++) {
            clientPair.appClient.createWidget(2, "{\"id\":X, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":2}".replace("X", ("" + i)));
            Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(i, OK)));
        }
        clientPair.appClient.createWidget(2, "{\"id\":100, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":2}");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(12, ENERGY_LIMIT)));
    }

    @Test
    public void testGetEnergy() throws Exception {
        clientPair.appClient.send("getEnergy");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, GET_ENERGY, "2000")));
    }

    @Test
    public void testAddEnergy() throws Exception {
        clientPair.appClient.send(("addEnergy 1000" + ("\u0000" + "random123")));
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("getEnergy");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, GET_ENERGY, "3000")));
    }

    @Test
    public void testEnergyAfterCreateRemoveProject() throws Exception {
        clientPair.appClient.createDash("{\"id\":2, \"createdAt\":1458856800001, \"name\":\"test board\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("getEnergy");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, GET_ENERGY, "2000")));
        clientPair.appClient.deleteDash(2);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(3)));
        clientPair.appClient.send("getEnergy");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(4, GET_ENERGY, "2000")));
    }

    @Test
    public void testEnergyAfterCreateRemoveWidget() throws Exception {
        clientPair.appClient.createDash("{\"id\":2, \"createdAt\":1458856800001, \"name\":\"test board\"}");
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.appClient.send("getEnergy");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, GET_ENERGY, "2000")));
        clientPair.appClient.createWidget(2, "{\"id\":2, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"LCD\", \"pinType\":\"DIGITAL\", \"pin\":2}");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(3)));
        clientPair.appClient.createWidget(2, "{\"id\":3, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"LCD\", \"pinType\":\"DIGITAL\", \"pin\":2}");
        clientPair.appClient.verifyResult(TestUtil.ok(4));
        clientPair.appClient.createWidget(2, "{\"id\":4, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"LCD\", \"pinType\":\"DIGITAL\", \"pin\":2}");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(5)));
        clientPair.appClient.createWidget(2, "{\"id\":5, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"LCD\", \"pinType\":\"DIGITAL\", \"pin\":2}");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(6)));
        clientPair.appClient.createWidget(2, "{\"id\":6, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"LCD\", \"pinType\":\"DIGITAL\", \"pin\":2}");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(7)));
        clientPair.appClient.createWidget(2, "{\"id\":7, \"width\":1, \"height\":1, \"x\":2, \"y\":2, \"label\":\"Some Text 2\", \"type\":\"BUTTON\", \"pinType\":\"DIGITAL\", \"pin\":2}");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(new cc.blynk.server.core.protocol.model.messages.ResponseMessage(8, ENERGY_LIMIT)));
        clientPair.appClient.deleteDash(2);
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(TestUtil.ok(9)));
        clientPair.appClient.send("getEnergy");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(10, GET_ENERGY, "2000")));
    }
}

