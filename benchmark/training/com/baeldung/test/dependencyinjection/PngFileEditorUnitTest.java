package com.baeldung.test.dependencyinjection;


import com.baeldung.dependencyinjection.imagefileeditors.PngFileEditor;
import com.baeldung.dependencyinjection.qualifiers.PngFileEditorQualifier;
import org.junit.Test;


@PngFileEditorQualifier
public class PngFileEditorUnitTest {
    private static PngFileEditor pngFileEditor;

    @Test
    public void givenPngFileEditorInstance_whenCalledopenFile_thenOneAssertion() {
        assertThat(PngFileEditorUnitTest.pngFileEditor.openFile("file1.png")).isEqualTo("Opening PNG file file1.png");
    }

    @Test
    public void givenPngFileEditorInstance_whenCallededitFile_thenOneAssertion() {
        assertThat(PngFileEditorUnitTest.pngFileEditor.editFile("file1.png")).isEqualTo("Editing PNG file file1.png");
    }

    @Test
    public void givenPngFileEditorInstance_whenCalledwriteFile_thenOneAssertion() {
        assertThat(PngFileEditorUnitTest.pngFileEditor.writeFile("file1.png")).isEqualTo("Writing PNG file file1.png");
    }

    @Test
    public void givenPngFileEditorInstance_whenCalledsaveFile_thenOneAssertion() {
        assertThat(PngFileEditorUnitTest.pngFileEditor.saveFile("file1.png")).isEqualTo("Saving PNG file file1.png");
    }
}

