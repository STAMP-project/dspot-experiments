package buildcraft.test.transport.pipe;


import PipeEventItem.ModifySpeed;
import buildcraft.api.transport.pipe.PipeEventHandler;
import buildcraft.api.transport.pipe.PipeEventItem;
import buildcraft.transport.pipe.PipeEventBus;
import org.junit.Assert;
import org.junit.Test;


public class PipeEventBusTester {
    public static long dontInlineThis = 0;

    @Test
    public void testSimpleEvent() {
        PipeEventBus bus = new PipeEventBus();
        PipeEventItem.ModifySpeed event = new PipeEventItem.ModifySpeed(null, null, null, 1);
        bus.fireEvent(event);
        Assert.assertEquals(0, event.targetSpeed, 1.0E-5);
        bus.registerHandler(this);
        event = new PipeEventItem.ModifySpeed(null, null, null, 1);
        bus.fireEvent(event);
        Assert.assertEquals(1, event.targetSpeed, 1.0E-5);
        bus.unregisterHandler(this);
        event = new PipeEventItem.ModifySpeed(null, null, null, 1);
        bus.fireEvent(event);
        Assert.assertEquals(0, event.targetSpeed, 1.0E-5);
    }

    @Test
    public void testExtends() {
        PipeEventBus bus = new PipeEventBus();
        PipeEventItem.ModifySpeed event = new PipeEventItem.ModifySpeed(null, null, null, 1);
        bus.fireEvent(event);
        Assert.assertEquals(0, event.targetSpeed, 1.0E-5);
        bus.registerHandler(new PipeEventBusTester.Base());
        event = new PipeEventItem.ModifySpeed(null, null, null, 1);
        bus.fireEvent(event);
        Assert.assertEquals(2, event.targetSpeed, 1.0E-5);
        bus = new PipeEventBus();
        bus.registerHandler(new PipeEventBusTester.Sub());
        event = new PipeEventItem.ModifySpeed(null, null, null, 1);
        bus.fireEvent(event);
        Assert.assertEquals(3, event.targetSpeed, 1.0E-5);
    }

    public static class Base {
        @PipeEventHandler
        public void modifySpeed2(PipeEventItem.ModifySpeed event) {
            event.targetSpeed = 2;
        }
    }

    public static class Sub extends PipeEventBusTester.Base {
        @Override
        public void modifySpeed2(PipeEventItem.ModifySpeed event) {
            event.targetSpeed = 3;
        }
    }
}

