package com.airbnb.deeplinkdispatch;


import Documentor.DOC_OUTPUT_PROPERTY_NAME;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class DocumentorTest {
    private static final String FILE_PATH = (System.getProperty("user.dir")) + "/doc/deeplinks.txt";

    @Mock
    private ProcessingEnvironment processingEnv;

    @Mock
    private Messager messager;

    @Mock
    private Map<String, String> options;

    @Mock
    private Elements elements;

    @Test
    public void testInitWithRightFilePath() {
        Mockito.when(options.get(DOC_OUTPUT_PROPERTY_NAME)).thenReturn(DocumentorTest.FILE_PATH);
        Documentor documentor = new Documentor(processingEnv);
        assertThat(documentor.getFile()).isNotNull();
    }

    @Test
    public void testInitWithoutFilePath() {
        Documentor documentor = new Documentor(processingEnv);
        assertThat(documentor.getFile()).isNull();
    }

    @Test
    public void testWrite() throws IOException {
        Mockito.when(options.get(DOC_OUTPUT_PROPERTY_NAME)).thenReturn(DocumentorTest.FILE_PATH);
        Documentor documentor = new Documentor(processingEnv);
        documentor.write(getElements());
        String actual = Files.toString(new File(DocumentorTest.FILE_PATH), Charsets.UTF_8);
        String expected = "airbnb://example.com/{foo}/bar\\n|#|\\nSample doc\\n|#|\\nDocClass\\" + "n|##|\\nairbnb://example.com/{foo}/bar\\n|#|\\n\\n|#|\\nDocClass#DocMethod\\n|##|\\n";
        Assert.assertEquals(expected, actual);
    }
}

