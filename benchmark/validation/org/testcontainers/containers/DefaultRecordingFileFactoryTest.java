package org.testcontainers.containers;


import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import lombok.Value;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Value
public class DefaultRecordingFileFactoryTest {
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("YYYYMMdd-HHmmss");

    private final DefaultRecordingFileFactory factory = new DefaultRecordingFileFactory();

    private final String methodName;

    private final String prefix;

    private final boolean success;

    @Test
    public void recordingFileThatShouldDescribeTheTestResultAtThePresentTime() throws Exception {
        File vncRecordingDirectory = Files.createTempDirectory("recording").toFile();
        Description description = Description.createTestDescription(getClass().getCanonicalName(), methodName, Test.class);
        File recordingFile = factory.recordingFileForTest(vncRecordingDirectory, description, success);
        String expectedFilePrefix = String.format("%s-%s-%s", prefix, getClass().getSimpleName(), methodName);
        List<File> expectedPossibleFileNames = Arrays.asList(new File(vncRecordingDirectory, String.format("%s-%s.flv", expectedFilePrefix, LocalDateTime.now().format(DefaultRecordingFileFactoryTest.DATETIME_FORMATTER))), new File(vncRecordingDirectory, String.format("%s-%s.flv", expectedFilePrefix, LocalDateTime.now().minusSeconds(1L).format(DefaultRecordingFileFactoryTest.DATETIME_FORMATTER))));
        Assert.assertThat(expectedPossibleFileNames, IsCollectionContaining.hasItem(recordingFile));
    }
}

