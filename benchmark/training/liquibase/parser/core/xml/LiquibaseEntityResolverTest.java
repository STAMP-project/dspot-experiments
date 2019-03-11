package liquibase.parser.core.xml;


import java.io.IOException;
import java.io.InputStream;
import liquibase.resource.ResourceAccessor;
import liquibase.serializer.LiquibaseSerializer;
import liquibase.util.StreamUtil;
import liquibase.util.file.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ LiquibaseEntityResolver.class, StreamUtil.class })
public class LiquibaseEntityResolverTest {
    private static final String SYSTEM_ID = "http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd";

    private static final String SYSTEM_ID_WITH_MIGRATOR_PATH = "http://www.liquibase.org/xml/ns/migrator/dbchangelog-1.0.xsd";

    private static final String SYSTEM_ID_FROM_MIGRATOR_PATH = "http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-1.0.xsd";

    private static final String PUBLIC_ID = "publicId";

    private static final String NAME = "name";

    private static final String BASE_URI = "baseUri";

    private static final String BASE_PATH = "basePath";

    private static final String FILE_SYSTEM_ID = "fileSystemId";

    private static final String PATH_AND_SYSTEM_ID = FilenameUtils.concat(LiquibaseEntityResolverTest.BASE_PATH, LiquibaseEntityResolverTest.FILE_SYSTEM_ID);

    private LiquibaseEntityResolver liquibaseEntityResolver;

    @Mock
    private LiquibaseSchemaResolver liquibaseSchemaResolver;

    @Mock
    private ResourceAccessor resourceAccessor;

    @Mock
    private InputSource inputSource;

    @Mock
    private XMLChangeLogSAXParser parser;

    @Mock
    private InputStream inputStream;

    @Mock
    private LiquibaseSerializer serializer;

    @Test
    public void shouldReturnNullSystemIdIsNull() throws Exception {
        InputSource result = liquibaseEntityResolver.resolveEntity(LiquibaseEntityResolverTest.NAME, LiquibaseEntityResolverTest.PUBLIC_ID, LiquibaseEntityResolverTest.BASE_URI, null);
        assertThat(result).isNull();
    }

    @Test
    public void systemIdStartingWithMigratorShouldBeReplacedByDbChangelog() throws Exception {
        PowerMockito.whenNew(LiquibaseSchemaResolver.class).withArguments(LiquibaseEntityResolverTest.SYSTEM_ID_FROM_MIGRATOR_PATH, LiquibaseEntityResolverTest.PUBLIC_ID, resourceAccessor).thenReturn(liquibaseSchemaResolver);
        InputSource result = liquibaseEntityResolver.resolveEntity(LiquibaseEntityResolverTest.NAME, LiquibaseEntityResolverTest.PUBLIC_ID, LiquibaseEntityResolverTest.BASE_URI, LiquibaseEntityResolverTest.SYSTEM_ID_WITH_MIGRATOR_PATH);
        assertThat(result).isSameAs(inputSource);
    }

    @Test
    public void shouldReturnSchemaResolverResultWhenSystemIdIsValidXsd() throws IOException, SAXException {
        InputSource result = liquibaseEntityResolver.resolveEntity(LiquibaseEntityResolverTest.NAME, LiquibaseEntityResolverTest.PUBLIC_ID, LiquibaseEntityResolverTest.BASE_URI, LiquibaseEntityResolverTest.SYSTEM_ID);
        assertThat(result).isSameAs(inputSource);
    }

    @Test
    public void shouldReturnSchemaResolverResultWhenSystemIdIsValidXsdAndSerializerIsNotNull() throws IOException, SAXException {
        liquibaseEntityResolver = new LiquibaseEntityResolver(serializer);
        liquibaseEntityResolver.useResoureAccessor(resourceAccessor, LiquibaseEntityResolverTest.BASE_PATH);
        InputSource result = liquibaseEntityResolver.resolveEntity(LiquibaseEntityResolverTest.NAME, LiquibaseEntityResolverTest.PUBLIC_ID, LiquibaseEntityResolverTest.BASE_URI, LiquibaseEntityResolverTest.SYSTEM_ID);
        assertThat(result).isSameAs(inputSource);
    }

    @Test
    public void whenSystemIdIsNotXsdLoadResourceFromBasepathWithResourceAccessor() throws IOException, SAXException {
        PowerMockito.when(StreamUtil.singleInputStream(LiquibaseEntityResolverTest.PATH_AND_SYSTEM_ID, resourceAccessor)).thenReturn(inputStream);
        InputSource result = liquibaseEntityResolver.resolveEntity(LiquibaseEntityResolverTest.NAME, LiquibaseEntityResolverTest.PUBLIC_ID, LiquibaseEntityResolverTest.BASE_URI, LiquibaseEntityResolverTest.FILE_SYSTEM_ID);
        assertThat(result.getByteStream()).isEqualTo(inputStream);
    }

    @Test
    public void whenSystemIdIsNotXsdAndResourceCouldNotBeLoadedFromResourceAccessorReturnNull() throws IOException, SAXException {
        PowerMockito.when(StreamUtil.singleInputStream(LiquibaseEntityResolverTest.PATH_AND_SYSTEM_ID, resourceAccessor)).thenReturn(null);
        InputSource result = liquibaseEntityResolver.resolveEntity(LiquibaseEntityResolverTest.NAME, LiquibaseEntityResolverTest.PUBLIC_ID, LiquibaseEntityResolverTest.BASE_URI, LiquibaseEntityResolverTest.FILE_SYSTEM_ID);
        assertThat(result).isEqualTo(null);
    }

    @Test
    public void whenSystemIdIsNotXsdAndResourceExceptionOccursReturnNull() throws IOException, SAXException {
        PowerMockito.when(StreamUtil.singleInputStream(LiquibaseEntityResolverTest.PATH_AND_SYSTEM_ID, resourceAccessor)).thenThrow(new RuntimeException());
        InputSource result = liquibaseEntityResolver.resolveEntity(LiquibaseEntityResolverTest.NAME, LiquibaseEntityResolverTest.PUBLIC_ID, LiquibaseEntityResolverTest.BASE_URI, LiquibaseEntityResolverTest.FILE_SYSTEM_ID);
        assertThat(result).isEqualTo(null);
    }

    @Test
    public void resolveEntityWithOnlyPublicIdAndSystemIdDelegatesToSchemResolver() throws IOException, SAXException {
        InputSource result = liquibaseEntityResolver.resolveEntity(LiquibaseEntityResolverTest.PUBLIC_ID, LiquibaseEntityResolverTest.SYSTEM_ID);
        assertThat(result).isSameAs(inputSource);
    }

    @Test
    public void getExternalSubsetShouldReturnNull() throws IOException, SAXException {
        InputSource externalSubset = liquibaseEntityResolver.getExternalSubset(LiquibaseEntityResolverTest.NAME, LiquibaseEntityResolverTest.BASE_URI);
        assertThat(externalSubset).isNull();
    }
}

