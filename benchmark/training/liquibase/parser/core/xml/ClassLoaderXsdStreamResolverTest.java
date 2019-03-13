package liquibase.parser.core.xml;


import java.io.IOException;
import java.io.InputStream;
import liquibase.resource.ResourceAccessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
public class ClassLoaderXsdStreamResolverTest {
    private static final String EXISTING_XSD_FILE = "liquibase/parser/core/xml/unused.xsd";

    private static final String NON_EXISTING_XSD_FILE = "xsdFile";

    @InjectMocks
    private ClassLoaderXsdStreamResolver classLoaderXsdStreamResolver;

    @Mock
    private XsdStreamResolver successor;

    @Mock
    private ResourceAccessor resourceAccessor;

    @Mock
    private InputStream successorValue;

    @Test
    public void whenResourceStreamIsNotNullThenReturnStream() throws IOException {
        InputStream returnValue = classLoaderXsdStreamResolver.getResourceAsStream(ClassLoaderXsdStreamResolverTest.EXISTING_XSD_FILE);
        assertThat(returnValue).isInstanceOf(InputStream.class);
    }

    @Test
    public void whenContextClassLoaderIsNullThenReturnSuccessorValue() throws IOException {
        InputStream returnValue = classLoaderXsdStreamResolver.getResourceAsStream(ClassLoaderXsdStreamResolverTest.NON_EXISTING_XSD_FILE);
        assertThat(returnValue).isSameAs(successorValue);
    }
}

