package com.baeldung.test.dependencyinjection;


import com.baeldung.dependencyinjection.imagefileeditors.PngFileEditor;
import com.baeldung.dependencyinjection.imageprocessors.ImageFileProcessor;
import com.baeldung.dependencyinjection.loggers.TimeLogger;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import org.junit.Test;


public class ImageProcessorUnitTest {
    private static ImageFileProcessor imageFileProcessor;

    @Test
    public void givenImageProcessorInstance_whenInjectedPngFileEditorandTimeLoggerInstances_thenTwoAssertions() {
        assertThat(ImageProcessorUnitTest.imageFileProcessor.getImageFileditor()).isInstanceOf(PngFileEditor.class);
        assertThat(ImageProcessorUnitTest.imageFileProcessor.getTimeLogger()).isInstanceOf(TimeLogger.class);
    }

    @Test
    public void givenImageProcessorInstance_whenCalledopenFile_thenOneAssertion() throws ParseException {
        LocalTime currentTime = LocalTime.now();
        String openFileLog = ImageProcessorUnitTest.imageFileProcessor.openFile("file1.png");
        assertThat(openFileLog).contains("Opening PNG file file1.png at: ");
        LocalTime loggedTime = getLoggedTime(openFileLog);
        assertThat(loggedTime).isCloseTo(currentTime, within(2, ChronoUnit.MINUTES));
    }

    @Test
    public void givenImageProcessorInstance_whenCallededitFile_thenOneAssertion() throws ParseException {
        LocalTime currentTime = LocalTime.now();
        String editFileLog = ImageProcessorUnitTest.imageFileProcessor.editFile("file1.png");
        assertThat(editFileLog).contains("Editing PNG file file1.png at: ");
        LocalTime loggedTime = getLoggedTime(editFileLog);
        assertThat(loggedTime).isCloseTo(currentTime, within(2, ChronoUnit.MINUTES));
    }

    @Test
    public void givenImageProcessorInstance_whenCalledwriteFile_thenOneAssertion() throws ParseException {
        LocalTime currentTime = LocalTime.now();
        String writeFileLog = ImageProcessorUnitTest.imageFileProcessor.writeFile("file1.png");
        assertThat(writeFileLog).contains("Writing PNG file file1.png at: ");
        LocalTime loggedTime = getLoggedTime(writeFileLog);
        assertThat(loggedTime).isCloseTo(currentTime, within(2, ChronoUnit.MINUTES));
    }

    @Test
    public void givenImageProcessorInstance_whenCalledsaveFile_thenOneAssertion() throws ParseException {
        LocalTime currentTime = LocalTime.now();
        String saveFileLog = ImageProcessorUnitTest.imageFileProcessor.saveFile("file1.png");
        assertThat(saveFileLog).contains("Saving PNG file file1.png at: ");
        LocalTime loggedTime = getLoggedTime(saveFileLog);
        assertThat(loggedTime).isCloseTo(currentTime, within(2, ChronoUnit.MINUTES));
    }
}

