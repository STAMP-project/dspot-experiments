package org.datatransferproject.api.action.datatype;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.datatransferproject.api.launcher.Monitor;
import org.datatransferproject.spi.api.auth.AuthServiceProviderRegistry;
import org.datatransferproject.types.client.datatype.DataTypes;
import org.datatransferproject.types.client.datatype.GetDataTypes;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DataTypesActionTest {
    @Test
    public void testGetRequestType() {
        AuthServiceProviderRegistry registry = Mockito.mock(AuthServiceProviderRegistry.class);
        DataTypesAction dataTypesAction = new DataTypesAction(registry, new Monitor() {});
        Class<GetDataTypes> actual = dataTypesAction.getRequestType();
        Assert.assertNotEquals(actual, null);
        Assert.assertEquals(actual, GetDataTypes.class);
    }

    @Test
    public void testHandle() {
        AuthServiceProviderRegistry registry = Mockito.mock(AuthServiceProviderRegistry.class);
        Set<String> dataTypes = new HashSet<>(Arrays.asList("CONTACTS", "PHOTOS"));
        Mockito.when(registry.getTransferDataTypes()).thenReturn(dataTypes);
        DataTypesAction dataTypesAction = new DataTypesAction(registry, new Monitor() {});
        GetDataTypes request = Mockito.mock(GetDataTypes.class);
        DataTypes actual = dataTypesAction.handle(request);
        Assert.assertEquals(actual.getDataTypes(), dataTypes);
    }
}

