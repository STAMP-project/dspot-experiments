package liquibase.parser.core.xml;


import java.io.IOException;
import java.io.InputStream;
import liquibase.resource.ResourceAccessor;
import liquibase.util.StreamUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(StreamUtil.class)
public class ResourceAccessorXsdStreamResolverTest {
    private static final String XSD_FILE = "xsdFile";

    @InjectMocks
    private ResourceAccessorXsdStreamResolver resourceAccessorXsdStreamResolver;

    @Mock
    private XsdStreamResolver successor;

    @Mock
    private ResourceAccessor resourceAccessor;

    @Mock
    private InputStream inputStream;

    @Mock
    private InputStream successorValue;

    @Test
    public void whenResourceStreamIsNotNullThenReturnStream() throws IOException {
        Mockito.when(StreamUtil.singleInputStream(ResourceAccessorXsdStreamResolverTest.XSD_FILE, resourceAccessor)).thenReturn(inputStream);
        InputStream returnValue = resourceAccessorXsdStreamResolver.getResourceAsStream(ResourceAccessorXsdStreamResolverTest.XSD_FILE);
        assertThat(returnValue).isSameAs(inputStream);
    }

    @Test
    public void whenResourceStreamIsNullThenReturnSuccessorValue() throws IOException {
        Mockito.when(StreamUtil.singleInputStream(ResourceAccessorXsdStreamResolverTest.XSD_FILE, resourceAccessor)).thenReturn(null);
        InputStream returnValue = resourceAccessorXsdStreamResolver.getResourceAsStream(ResourceAccessorXsdStreamResolverTest.XSD_FILE);
        assertThat(returnValue).isSameAs(successorValue);
    }

    @Test
    public void whenIOExceptionOccursThenReturnSuccessorValue() throws IOException {
        Mockito.when(StreamUtil.singleInputStream(ResourceAccessorXsdStreamResolverTest.XSD_FILE, resourceAccessor)).thenThrow(new IOException());
        InputStream returnValue = resourceAccessorXsdStreamResolver.getResourceAsStream(ResourceAccessorXsdStreamResolverTest.XSD_FILE);
        assertThat(returnValue).isSameAs(successorValue);
    }
}

