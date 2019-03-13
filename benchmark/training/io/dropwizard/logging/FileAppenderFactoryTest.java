package io.dropwizard.logging;


import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationValidationException;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.logging.async.AsyncLoggingEventAppenderFactory;
import io.dropwizard.logging.layout.DropwizardLayoutFactory;
import io.dropwizard.util.Resources;
import io.dropwizard.util.Size;
import io.dropwizard.validation.BaseValidator;
import io.dropwizard.validation.ConstraintViolations;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;


public class FileAppenderFactoryTest {
    static {
        BootstrapLogging.bootstrap();
    }

    private final ObjectMapper mapper = Jackson.newObjectMapper();

    private final Validator validator = BaseValidator.newValidator();

    @Test
    public void isDiscoverable() throws Exception {
        assertThat(new DiscoverableSubtypeResolver().getDiscoveredSubtypes()).contains(FileAppenderFactory.class);
    }

    @Test
    public void includesCallerData() {
        FileAppenderFactory<ILoggingEvent> fileAppenderFactory = new FileAppenderFactory();
        fileAppenderFactory.setArchive(false);
        AsyncAppender asyncAppender = ((AsyncAppender) (fileAppenderFactory.build(new LoggerContext(), "test", new DropwizardLayoutFactory(), new io.dropwizard.logging.filter.NullLevelFilterFactory(), new AsyncLoggingEventAppenderFactory())));
        assertThat(asyncAppender.isIncludeCallerData()).isFalse();
        fileAppenderFactory.setIncludeCallerData(true);
        asyncAppender = ((AsyncAppender) (fileAppenderFactory.build(new LoggerContext(), "test", new DropwizardLayoutFactory(), new io.dropwizard.logging.filter.NullLevelFilterFactory(), new AsyncLoggingEventAppenderFactory())));
        assertThat(asyncAppender.isIncludeCallerData()).isTrue();
    }

    @Test
    public void testCurrentFileNameErrorWhenArchiveIsNotEnabled() throws Exception {
        FileAppenderFactory<?> fileAppenderFactory = new FileAppenderFactory();
        fileAppenderFactory.setArchive(false);
        Collection<String> errors = ConstraintViolations.format(validator.validate(fileAppenderFactory));
        assertThat(errors).containsOnly("currentLogFilename can only be null when archiving is enabled");
        fileAppenderFactory.setCurrentLogFilename("test");
        errors = ConstraintViolations.format(validator.validate(fileAppenderFactory));
        assertThat(errors).isEmpty();
    }

    @Test
    public void testCurrentFileNameCanBeNullWhenArchiveIsEnabled() throws Exception {
        FileAppenderFactory<?> fileAppenderFactory = new FileAppenderFactory();
        fileAppenderFactory.setArchive(true);
        fileAppenderFactory.setArchivedLogFilenamePattern("name-to-be-used");
        fileAppenderFactory.setCurrentLogFilename(null);
        Collection<String> errors = ConstraintViolations.format(validator.validate(fileAppenderFactory));
        assertThat(errors).isEmpty();
    }

    @Test
    public void isNeverBlock() throws Exception {
        FileAppenderFactory<ILoggingEvent> fileAppenderFactory = new FileAppenderFactory();
        fileAppenderFactory.setArchive(false);
        fileAppenderFactory.setNeverBlock(true);
        AsyncAppender asyncAppender = ((AsyncAppender) (fileAppenderFactory.build(new LoggerContext(), "test", new DropwizardLayoutFactory(), new io.dropwizard.logging.filter.NullLevelFilterFactory(), new AsyncLoggingEventAppenderFactory())));
        assertThat(asyncAppender.isNeverBlock()).isTrue();
    }

    @Test
    public void isNotNeverBlock() throws Exception {
        FileAppenderFactory<ILoggingEvent> fileAppenderFactory = new FileAppenderFactory();
        fileAppenderFactory.setArchive(false);
        fileAppenderFactory.setNeverBlock(false);
        AsyncAppender asyncAppender = ((AsyncAppender) (fileAppenderFactory.build(new LoggerContext(), "test", new DropwizardLayoutFactory(), new io.dropwizard.logging.filter.NullLevelFilterFactory(), new AsyncLoggingEventAppenderFactory())));
        assertThat(asyncAppender.isNeverBlock()).isFalse();
    }

    @Test
    public void defaultIsNotNeverBlock() throws Exception {
        FileAppenderFactory<ILoggingEvent> fileAppenderFactory = new FileAppenderFactory();
        fileAppenderFactory.setArchive(false);
        // default neverBlock
        AsyncAppender asyncAppender = ((AsyncAppender) (fileAppenderFactory.build(new LoggerContext(), "test", new DropwizardLayoutFactory(), new io.dropwizard.logging.filter.NullLevelFilterFactory(), new AsyncLoggingEventAppenderFactory())));
        assertThat(asyncAppender.isNeverBlock()).isFalse();
    }

    @Test
    public void overrideBufferSize() throws IllegalAccessException, NoSuchFieldException {
        FileAppenderFactory<ILoggingEvent> fileAppenderFactory = new FileAppenderFactory();
        fileAppenderFactory.setArchive(false);
        fileAppenderFactory.setBufferSize(Size.kilobytes(256));
        AsyncAppender asyncAppender = ((AsyncAppender) (fileAppenderFactory.build(new LoggerContext(), "test", new DropwizardLayoutFactory(), new io.dropwizard.logging.filter.NullLevelFilterFactory(), new AsyncLoggingEventAppenderFactory())));
        final Appender<ILoggingEvent> fileAppender = asyncAppender.getAppender("file-appender");
        assertThat(fileAppender).isInstanceOf(FileAppender.class);
        final Field bufferSizeField = FileAppender.class.getDeclaredField("bufferSize");
        bufferSizeField.setAccessible(true);
        FileSize bufferSizeFromAppender = ((FileSize) (bufferSizeField.get(fileAppender)));
        assertThat(bufferSizeFromAppender.getSize()).isEqualTo(fileAppenderFactory.getBufferSize().toBytes());
    }

    @Test
    public void isImmediateFlushed() throws Exception {
        FileAppenderFactory<ILoggingEvent> fileAppenderFactory = new FileAppenderFactory();
        fileAppenderFactory.setArchive(false);
        Field isImmediateFlushField = OutputStreamAppender.class.getDeclaredField("immediateFlush");
        isImmediateFlushField.setAccessible(true);
        fileAppenderFactory.setImmediateFlush(false);
        AsyncAppender asyncAppender = ((AsyncAppender) (fileAppenderFactory.build(new LoggerContext(), "test", new DropwizardLayoutFactory(), new io.dropwizard.logging.filter.NullLevelFilterFactory(), new AsyncLoggingEventAppenderFactory())));
        Appender<ILoggingEvent> fileAppender = asyncAppender.getAppender("file-appender");
        assertThat(((Boolean) (isImmediateFlushField.get(fileAppender)))).isEqualTo(fileAppenderFactory.isImmediateFlush());
        fileAppenderFactory.setImmediateFlush(true);
        asyncAppender = ((AsyncAppender) (fileAppenderFactory.build(new LoggerContext(), "test", new DropwizardLayoutFactory(), new io.dropwizard.logging.filter.NullLevelFilterFactory(), new AsyncLoggingEventAppenderFactory())));
        fileAppender = asyncAppender.getAppender("file-appender");
        assertThat(((Boolean) (isImmediateFlushField.get(fileAppender)))).isEqualTo(fileAppenderFactory.isImmediateFlush());
    }

    @Test
    public void validSetTotalSizeCap() throws ConfigurationException, IOException, NoSuchFieldException {
        final Field totalSizeCap = TimeBasedRollingPolicy.class.getDeclaredField("totalSizeCap");
        totalSizeCap.setAccessible(true);
        final Field maxFileSize = SizeAndTimeBasedRollingPolicy.class.getDeclaredField("maxFileSize");
        maxFileSize.setAccessible(true);
        final YamlConfigurationFactory<FileAppenderFactory> factory = new YamlConfigurationFactory(FileAppenderFactory.class, validator, mapper, "dw");
        final FileAppenderFactory appenderFactory = factory.build(new File(Resources.getResource("yaml/appender_file_cap.yaml").getFile()));
        final FileAppender appender = appenderFactory.buildAppender(new LoggerContext());
        assertThat(appender).isInstanceOfSatisfying(RollingFileAppender.class, ( roller) -> {
            assertThat(roller.getRollingPolicy()).isInstanceOfSatisfying(.class, ( policy) -> {
                try {
                    assertThat(totalSizeCap.get(policy)).isInstanceOfSatisfying(.class, ( x) -> assertThat(x.getSize()).isEqualTo(Size.megabytes(50).toBytes()));
                    assertThat(maxFileSize.get(policy)).isInstanceOfSatisfying(.class, ( x) -> assertThat(x.getSize()).isEqualTo(Size.megabytes(10).toBytes()));
                    assertThat(policy.getMaxHistory()).isEqualTo(5);
                } catch ( e) {
                    throw new <e>RuntimeException("Unexpected illegal access");
                }
            });
        });
    }

    @Test
    public void validSetTotalSizeCapNoMaxFileSize() throws ConfigurationException, IOException, NoSuchFieldException {
        final Field totalSizeCap = TimeBasedRollingPolicy.class.getDeclaredField("totalSizeCap");
        totalSizeCap.setAccessible(true);
        final YamlConfigurationFactory<FileAppenderFactory> factory = new YamlConfigurationFactory(FileAppenderFactory.class, validator, mapper, "dw");
        final FileAppenderFactory appenderFactory = factory.build(new File(Resources.getResource("yaml/appender_file_cap2.yaml").getFile()));
        final FileAppender appender = appenderFactory.buildAppender(new LoggerContext());
        assertThat(appender).isInstanceOfSatisfying(RollingFileAppender.class, ( roller) -> {
            assertThat(roller.getRollingPolicy()).isInstanceOfSatisfying(.class, ( policy) -> {
                try {
                    assertThat(totalSizeCap.get(policy)).isInstanceOfSatisfying(.class, ( x) -> assertThat(x.getSize()).isEqualTo(Size.megabytes(50).toBytes()));
                    assertThat(policy.getMaxHistory()).isEqualTo(5);
                } catch ( e) {
                    throw new <e>RuntimeException("Unexpected illegal access");
                }
            });
        });
    }

    @Test
    public void invalidUseOfTotalSizeCap() {
        final YamlConfigurationFactory<FileAppenderFactory> factory = new YamlConfigurationFactory(FileAppenderFactory.class, validator, mapper, "dw");
        assertThatThrownBy(() -> factory.build(new File(Resources.getResource("yaml/appender_file_cap_invalid.yaml").getFile()))).isExactlyInstanceOf(ConfigurationValidationException.class).hasMessageContaining(("totalSizeCap has no effect when using maxFileSize and an archivedLogFilenamePattern " + "without %d, as archivedFileCount implicitly controls the total size cap"));
    }
}

