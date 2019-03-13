package com.baeldung.test.dependencyinjection;


import com.baeldung.dependencyinjection.imagefileeditors.GifFileEditor;
import org.junit.Test;


public class GifFileEditorUnitTest {
    private static GifFileEditor gifFileEditor;

    @Test
    public void givenGifFileEditorlInstance_whenCalledopenFile_thenOneAssertion() {
        assertThat(GifFileEditorUnitTest.gifFileEditor.openFile("file1.gif")).isEqualTo("Opening GIF file file1.gif");
    }

    @Test
    public void givenGifFileEditorlInstance_whenCallededitFile_thenOneAssertion() {
        assertThat(GifFileEditorUnitTest.gifFileEditor.editFile("file1.gif")).isEqualTo("Editing GIF file file1.gif");
    }

    @Test
    public void givenGifFileEditorInstance_whenCalledwriteFile_thenOneAssertion() {
        assertThat(GifFileEditorUnitTest.gifFileEditor.writeFile("file1.gif")).isEqualTo("Writing GIF file file1.gif");
    }

    @Test
    public void givenGifFileEditorInstance_whenCalledsaveFile_thenOneAssertion() {
        assertThat(GifFileEditorUnitTest.gifFileEditor.saveFile("file1.gif")).isEqualTo("Saving GIF file file1.gif");
    }
}

