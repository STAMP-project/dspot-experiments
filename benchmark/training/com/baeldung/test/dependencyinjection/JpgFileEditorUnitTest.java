package com.baeldung.test.dependencyinjection;


import com.baeldung.dependencyinjection.imagefileeditors.JpgFileEditor;
import org.junit.Test;


public class JpgFileEditorUnitTest {
    private static JpgFileEditor jpgFileUtil;

    @Test
    public void givenJpgFileEditorInstance_whenCalledopenFile_thenOneAssertion() {
        assertThat(JpgFileEditorUnitTest.jpgFileUtil.openFile("file1.jpg")).isEqualTo("Opening JPG file file1.jpg");
    }

    @Test
    public void givenJpgFileEditorlInstance_whenCallededitFile_thenOneAssertion() {
        assertThat(JpgFileEditorUnitTest.jpgFileUtil.editFile("file1.gif")).isEqualTo("Editing JPG file file1.gif");
    }

    @Test
    public void givenJpgFileEditorInstance_whenCalledwriteFile_thenOneAssertion() {
        assertThat(JpgFileEditorUnitTest.jpgFileUtil.writeFile("file1.jpg")).isEqualTo("Writing JPG file file1.jpg");
    }

    @Test
    public void givenJpgFileEditorInstance_whenCalledsaveFile_thenOneAssertion() {
        assertThat(JpgFileEditorUnitTest.jpgFileUtil.saveFile("file1.jpg")).isEqualTo("Saving JPG file file1.jpg");
    }
}

