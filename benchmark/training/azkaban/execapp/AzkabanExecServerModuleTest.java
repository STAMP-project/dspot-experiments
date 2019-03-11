package azkaban.execapp;


import azkaban.spi.AzkabanEventReporter;
import azkaban.utils.Props;
import org.junit.Test;


public class AzkabanExecServerModuleTest {
    /**
     * Verify that alternate implementation of the <code>AzkabanEventReporter</code>
     * is initialized.
     */
    @Test
    public void testCreateAzkabanEventReporter() {
        final AzkabanExecServerModule azkabanExecServerModule = new AzkabanExecServerModule();
        final Props props = new Props();
        props.put("azkaban.event.reporting.enabled", "true");
        props.put("azkaban.event.reporting.class", "azkaban.execapp.AzkabanEventReporterTest1");
        final AzkabanEventReporter azkabanEventReporter = azkabanExecServerModule.createAzkabanEventReporter(props);
        assertThat(azkabanEventReporter).isNotNull();
        assertThat(azkabanEventReporter).isInstanceOf(AzkabanEventReporterTest1.class);
    }

    /**
     * Verify that <code>IllegalArgumentException</code> is thrown when required properties
     * are missing.
     */
    @Test
    public void testAzkabanEventReporterInvalidProperties() {
        final AzkabanExecServerModule azkabanExecServerModule = new AzkabanExecServerModule();
        final Props props = new Props();
        props.put("azkaban.event.reporting.enabled", "true");
        props.put("azkaban.event.reporting.class", "azkaban.execapp.reporter.AzkabanKafkaAvroEventReporter");
        assertThatIllegalArgumentException().isThrownBy(() -> azkabanExecServerModule.createAzkabanEventReporter(props));
    }

    /**
     * Verify that a <code>RuntimeException</code> is thrown when valid constructor is
     * not found in the event reporter implementation.
     */
    @Test
    public void testAzkabanEventReporterInvalidConstructor() {
        final AzkabanExecServerModule azkabanExecServerModule = new AzkabanExecServerModule();
        final Props props = new Props();
        props.put("azkaban.event.reporting.enabled", "true");
        props.put("azkaban.event.reporting.class", "azkaban.execapp.AzkabanEventReporterTest3");
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> azkabanExecServerModule.createAzkabanEventReporter(props));
    }

    /**
     * Ensures that the event reporter is not initialized when the property 'event.reporter.enabled'
     * is not set.
     */
    @Test
    public void testEventReporterDisabled() {
        final AzkabanExecServerModule azkabanExecServerModule = new AzkabanExecServerModule();
        final AzkabanEventReporter azkabanEventReporter = azkabanExecServerModule.createAzkabanEventReporter(new Props());
        assertThat(azkabanEventReporter).isNull();
    }
}

