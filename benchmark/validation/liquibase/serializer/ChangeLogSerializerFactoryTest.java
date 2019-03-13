package liquibase.serializer;


import java.util.List;
import java.util.Map;
import liquibase.serializer.core.string.StringChangeLogSerializer;
import liquibase.serializer.core.xml.XMLChangeLogSerializer;
import org.junit.Assert;
import org.junit.Test;


public class ChangeLogSerializerFactoryTest {
    @Test
    public void getInstance() {
        Assert.assertNotNull(ChangeLogSerializerFactory.getInstance());
        Assert.assertTrue(((ChangeLogSerializerFactory.getInstance()) == (ChangeLogSerializerFactory.getInstance())));
    }

    @Test
    public void register() {
        ChangeLogSerializerFactory.getInstance().getSerializers().clear();
        Assert.assertEquals(0, ChangeLogSerializerFactory.getInstance().getSerializers().size());
        ChangeLogSerializerFactory.getInstance().register(new MockChangeLogSerializer("mock"));
        Assert.assertEquals(1, ChangeLogSerializerFactory.getInstance().getSerializers().size());
    }

    @Test
    public void unregister_instance() {
        ChangeLogSerializerFactory factory = ChangeLogSerializerFactory.getInstance();
        factory.getSerializers().clear();
        Assert.assertEquals(0, factory.getSerializers().size());
        XMLChangeLogSerializer changeLogSerializer = new XMLChangeLogSerializer();
        factory.register(new StringChangeLogSerializer());
        factory.register(changeLogSerializer);
        Assert.assertEquals(2, factory.getSerializers().size());
        factory.unregister(changeLogSerializer);
        Assert.assertEquals(1, factory.getSerializers().size());
    }

    @Test
    public void reset() {
        ChangeLogSerializerFactory instance1 = ChangeLogSerializerFactory.getInstance();
        ChangeLogSerializerFactory.reset();
        Assert.assertFalse((instance1 == (ChangeLogSerializerFactory.getInstance())));
    }

    @Test
    public void builtInSerializersAreFound() {
        Map<String, List<ChangeLogSerializer>> serializers = ChangeLogSerializerFactory.getInstance().getSerializers();
        Assert.assertEquals(6, serializers.size());
    }

    @Test
    public void getSerializers() {
        ChangeLogSerializer serializer = ChangeLogSerializerFactory.getInstance().getSerializer("xml");
        Assert.assertNotNull(serializer);
        Assert.assertSame(XMLChangeLogSerializer.class, serializer.getClass());
        Assert.assertEquals(1, ChangeLogSerializerFactory.getInstance().getSerializers("xml").size());
    }

    @Test
    public void highestPrioritySerializerReturned() {
        ChangeLogSerializerFactory factory = ChangeLogSerializerFactory.getInstance();
        XMLChangeLogSerializer highestPriority = new XMLChangeLogSerializer() {
            @Override
            public int getPriority() {
                return (super.getPriority()) + 4;
            }
        };
        factory.register(highestPriority);
        XMLChangeLogSerializer higherPriority = new XMLChangeLogSerializer() {
            @Override
            public int getPriority() {
                return (super.getPriority()) + 1;
            }
        };
        factory.register(higherPriority);
        Assert.assertSame(highestPriority, factory.getSerializer("xml"));
        Assert.assertEquals(3, factory.getSerializers().get("xml").size());
    }
}

