package cc.blynk.integration.tcp;


import cc.blynk.integration.SingleServerInstancePerTest;
import cc.blynk.integration.TestUtil;
import cc.blynk.integration.model.tcp.BaseTestAppClient;
import cc.blynk.integration.model.tcp.ClientPair;
import cc.blynk.server.core.model.enums.PinType;
import cc.blynk.server.core.model.widgets.ui.table.Row;
import cc.blynk.server.core.model.widgets.ui.table.Table;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 7/09/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class TableCommandsTest extends SingleServerInstancePerTest {
    @Test
    public void testAllTableCommands() throws Exception {
        Table table = new Table();
        table.pin = 123;
        table.pinType = PinType.VIRTUAL;
        table.isClickableRows = true;
        table.isReoderingAllowed = true;
        table.height = 2;
        table.width = 2;
        clientPair.appClient.createWidget(1, table);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 123 clr");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("1-0 vw 123 clr"))));
        clientPair.hardwareClient.send("hardware vw 123 add 0 Row0 row0");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, HARDWARE, TestUtil.b("1-0 vw 123 add 0 Row0 row0"))));
        table = loadTable();
        Row row;
        Assert.assertNotNull(table);
        Assert.assertNotNull(table.rows);
        Assert.assertEquals(1, table.rows.size());
        row = table.rows.poll();
        Assert.assertNotNull(row);
        Assert.assertEquals(0, row.id);
        Assert.assertEquals("Row0", row.name);
        Assert.assertEquals("row0", row.value);
        Assert.assertTrue(row.isSelected);
        Assert.assertEquals(0, table.currentRowIndex);
        clientPair.hardwareClient.send("hardware vw 123 pick 2");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(3, HARDWARE, TestUtil.b("1-0 vw 123 pick 2"))));
        table = loadTable();
        Assert.assertNotNull(table);
        Assert.assertNotNull(table.rows);
        Assert.assertEquals(1, table.rows.size());
        row = table.rows.poll();
        Assert.assertNotNull(row);
        Assert.assertEquals(2, table.currentRowIndex);
        clientPair.hardwareClient.send("hardware vw 123 add 1 Row1 row1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(4, HARDWARE, TestUtil.b("1-0 vw 123 add 1 Row1 row1"))));
        clientPair.hardwareClient.send("hardware vw 123 add 2 Row2 row2");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(5, HARDWARE, TestUtil.b("1-0 vw 123 add 2 Row2 row2"))));
        clientPair.hardwareClient.send("hardware vw 123 pick 2");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(6, HARDWARE, TestUtil.b("1-0 vw 123 pick 2"))));
        table = loadTable();
        Assert.assertNotNull(table);
        Assert.assertNotNull(table.rows);
        Assert.assertEquals(3, table.rows.size());
        row = table.rows.poll();
        Assert.assertNotNull(row);
        Assert.assertEquals(2, table.currentRowIndex);
        clientPair.hardwareClient.send("hardware vw 123 deselect 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(7, HARDWARE, TestUtil.b("1-0 vw 123 deselect 1"))));
        table = loadTable();
        Assert.assertNotNull(table);
        Assert.assertNotNull(table.rows);
        Assert.assertEquals(3, table.rows.size());
        table.rows.poll();
        row = table.rows.poll();
        Assert.assertNotNull(row);
        Assert.assertFalse(row.isSelected);
        clientPair.hardwareClient.send("hardware vw 123 select 1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(8, HARDWARE, TestUtil.b("1-0 vw 123 select 1"))));
        table = loadTable();
        Assert.assertNotNull(table);
        Assert.assertNotNull(table.rows);
        Assert.assertEquals(3, table.rows.size());
        table.rows.poll();
        row = table.rows.poll();
        Assert.assertNotNull(row);
        Assert.assertTrue(row.isSelected);
        /* clientPair.hardwareClient.send("hardware vw 123 order 0 2");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(produce(9, HARDWARE, b("1-0 vw 123 order 0 2"))));

        table = loadTable();
        assertNotNull(table);
        assertNotNull(table.rows);
        assertEquals(3, table.rows.size());

        assertEquals(1, table.rows.poll().id);
        assertEquals(2, table.rows.poll().id);
        assertEquals(0, table.rows.poll().id);
         */
        clientPair.hardwareClient.send("hardware vw 123 clr");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(9, HARDWARE, TestUtil.b("1-0 vw 123 clr"))));
        table = loadTable();
        Assert.assertNotNull(table);
        Assert.assertNotNull(table.rows);
        Assert.assertEquals(0, table.rows.size());
    }

    @Test
    public void testTableUpdateExistingRow() throws Exception {
        Table table = new Table();
        table.pin = 123;
        table.pinType = PinType.VIRTUAL;
        table.isClickableRows = true;
        table.isReoderingAllowed = true;
        table.height = 2;
        table.width = 2;
        clientPair.appClient.createWidget(1, table);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 123 clr");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("1-0 vw 123 clr"))));
        clientPair.hardwareClient.send("hardware vw 123 add 0 Row0 row0");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, HARDWARE, TestUtil.b("1-0 vw 123 add 0 Row0 row0"))));
        table = loadTable();
        Row row;
        Assert.assertNotNull(table);
        Assert.assertNotNull(table.rows);
        Assert.assertEquals(1, table.rows.size());
        row = table.rows.poll();
        Assert.assertNotNull(row);
        Assert.assertEquals(0, row.id);
        Assert.assertEquals("Row0", row.name);
        Assert.assertEquals("row0", row.value);
        Assert.assertTrue(row.isSelected);
        Assert.assertEquals(0, table.currentRowIndex);
        clientPair.hardwareClient.send("hardware vw 123 update 0 Row0Updated row0Updated");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(3, HARDWARE, TestUtil.b("1-0 vw 123 update 0 Row0Updated row0Updated"))));
        table = loadTable();
        Assert.assertNotNull(table);
        Assert.assertNotNull(table.rows);
        Assert.assertEquals(1, table.rows.size());
        row = table.rows.poll();
        Assert.assertNotNull(row);
        Assert.assertEquals(0, row.id);
        Assert.assertEquals("Row0Updated", row.name);
        Assert.assertEquals("row0Updated", row.value);
        Assert.assertTrue(row.isSelected);
        Assert.assertEquals(0, table.currentRowIndex);
    }

    @Test
    public void testTableRowLimit() throws Exception {
        Table table = new Table();
        table.pin = 123;
        table.pinType = PinType.VIRTUAL;
        table.isClickableRows = true;
        table.isReoderingAllowed = true;
        table.width = 2;
        table.height = 2;
        clientPair.appClient.createWidget(1, table);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 123 clr");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("1-0 vw 123 clr"))));
        for (int i = 1; i <= 101; i++) {
            String cmd = ("vw 123 add " + i) + " Row0 row0";
            clientPair.hardwareClient.send(("hardware " + cmd));
            Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(700)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce((i + 1), HARDWARE, TestUtil.b(("1-0 " + cmd)))));
        }
        table = loadTable();
        Row row;
        Assert.assertNotNull(table);
        Assert.assertNotNull(table.rows);
        Assert.assertEquals(100, table.rows.size());
        for (int i = 2; i <= 101; i++) {
            row = table.rows.poll();
            Assert.assertNotNull(row);
            Assert.assertEquals(i, row.id);
        }
    }

    @Test
    public void testTableAcceptsOnlyUniqueIds() throws Exception {
        Table table = new Table();
        table.pin = 123;
        table.pinType = PinType.VIRTUAL;
        table.isClickableRows = true;
        table.isReoderingAllowed = true;
        table.width = 2;
        table.height = 2;
        clientPair.appClient.createWidget(1, table);
        clientPair.appClient.verifyResult(TestUtil.ok(1));
        clientPair.hardwareClient.send("hardware vw 123 clr");
        channelRead(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(1, HARDWARE, TestUtil.b("1-0 vw 123 clr"))));
        clientPair.hardwareClient.send("hardware vw 123 add 0 row0 val0");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(2, HARDWARE, TestUtil.b("1-0 vw 123 add 0 row0 val0"))));
        clientPair.hardwareClient.send("hardware vw 123 add 0 row1 val1");
        Mockito.verify(clientPair.appClient.responseMock, Mockito.timeout(500)).channelRead(ArgumentMatchers.any(), ArgumentMatchers.eq(produce(3, HARDWARE, TestUtil.b("1-0 vw 123 add 0 row1 val1"))));
        table = loadTable();
        Assert.assertEquals(1, table.rows.size());
        Assert.assertEquals("row1", table.rows.peek().name);
        Assert.assertEquals("val1", table.rows.peek().value);
    }
}

