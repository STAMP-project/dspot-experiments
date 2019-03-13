package org.baeldung.mocks.jmockit;


import java.util.ArrayList;
import java.util.List;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Invocation;
import mockit.Mock;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.baeldung.mocks.jmockit.AdvancedCollaborator.InnerAdvancedCollaborator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JMockit.class)
public class AdvancedCollaboratorIntegrationTest<MultiMock extends List<String> & Comparable<List<String>>> {
    @Tested
    private AdvancedCollaborator mock;

    @Mocked
    private MultiMock multiMock;

    @Test
    public void testToMockUpPrivateMethod() {
        new mockit.MockUp<AdvancedCollaborator>() {
            @Mock
            private String privateMethod() {
                return "mocked: ";
            }
        };
        String res = mock.methodThatCallsPrivateMethod(1);
        Assert.assertEquals("mocked: 1", res);
    }

    @Test
    public void testToMockUpDifficultConstructor() throws Exception {
        new mockit.MockUp<AdvancedCollaborator>() {
            @Mock
            public void $init(Invocation invocation, String string) {
                ((AdvancedCollaborator) (invocation.getInvokedInstance())).i = 1;
            }
        };
        AdvancedCollaborator coll = new AdvancedCollaborator(null);
        Assert.assertEquals(1, coll.i);
    }

    @Test
    public void testToCallPrivateMethodsDirectly() {
        Object value = Deencapsulation.invoke(mock, "privateMethod");
        Assert.assertEquals("default:", value);
    }

    @Test
    public void testToSetPrivateFieldDirectly() {
        Deencapsulation.setField(mock, "privateField", 10);
        Assert.assertEquals(10, mock.methodThatReturnsThePrivateField());
    }

    @Test
    public void testToGetPrivateFieldDirectly() {
        int value = Deencapsulation.getField(mock, "privateField");
        Assert.assertEquals(5, value);
    }

    @Test
    public void testToCreateNewInstanceDirectly() {
        AdvancedCollaborator coll = Deencapsulation.newInstance(AdvancedCollaborator.class, "foo");
        Assert.assertEquals(3, coll.i);
    }

    @Test
    public void testToCreateNewInnerClassInstanceDirectly() {
        InnerAdvancedCollaborator innerCollaborator = Deencapsulation.newInnerInstance(InnerAdvancedCollaborator.class, mock);
        Assert.assertNotNull(innerCollaborator);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMultipleInterfacesWholeTest() {
        new Expectations() {
            {
                multiMock.get(5);
                result = "foo";
                multiMock.compareTo(((List<String>) (any)));
                result = 0;
            }
        };
        Assert.assertEquals("foo", multiMock.get(5));
        Assert.assertEquals(0, multiMock.compareTo(new ArrayList<>()));
    }
}

