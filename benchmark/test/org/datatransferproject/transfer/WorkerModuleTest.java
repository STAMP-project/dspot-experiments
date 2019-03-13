package org.datatransferproject.transfer;


import com.google.common.collect.ImmutableList;
import org.datatransferproject.spi.transfer.extension.TransferExtension;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class WorkerModuleTest {
    private static TransferExtension FOO_UPPER = WorkerModuleTest.createTransferExtension("FOO");

    private static TransferExtension FOO_LOWER = WorkerModuleTest.createTransferExtension("foo");

    private static TransferExtension BAR_UPPER = WorkerModuleTest.createTransferExtension("BAR");

    private static TransferExtension BAR_LOWER = WorkerModuleTest.createTransferExtension("bar");

    @Test
    public void findTransferExtension() {
        ImmutableList<TransferExtension> transferExtensions = ImmutableList.of(WorkerModuleTest.FOO_UPPER, WorkerModuleTest.BAR_UPPER);
        assertThat(WorkerModule.findTransferExtension(transferExtensions, "FOO")).isEqualTo(WorkerModuleTest.FOO_UPPER);
        assertThat(WorkerModule.findTransferExtension(transferExtensions, "foo")).isEqualTo(WorkerModuleTest.FOO_UPPER);
    }

    @Test
    public void findTransferExtension_mixedCasing() {
        ImmutableList<TransferExtension> transferExtensions = ImmutableList.of(WorkerModuleTest.FOO_UPPER, WorkerModuleTest.BAR_LOWER);
        assertThat(WorkerModule.findTransferExtension(transferExtensions, "FOO")).isEqualTo(WorkerModuleTest.FOO_UPPER);
        assertThat(WorkerModule.findTransferExtension(transferExtensions, "foo")).isEqualTo(WorkerModuleTest.FOO_UPPER);
        assertThat(WorkerModule.findTransferExtension(transferExtensions, "BAR")).isEqualTo(WorkerModuleTest.BAR_LOWER);
        assertThat(WorkerModule.findTransferExtension(transferExtensions, "bar")).isEqualTo(WorkerModuleTest.BAR_LOWER);
    }

    @Test
    public void findTransferExtension_noMatch() {
        ImmutableList<TransferExtension> transferExtensions = ImmutableList.of(WorkerModuleTest.FOO_UPPER, WorkerModuleTest.BAR_UPPER);
        Assertions.assertThrows(IllegalStateException.class, () -> WorkerModule.findTransferExtension(transferExtensions, "BAZ"));
    }

    @Test
    public void findTransferExtension_duplicateMatches() {
        ImmutableList<TransferExtension> transferExtensions = ImmutableList.of(WorkerModuleTest.FOO_UPPER, WorkerModuleTest.FOO_LOWER);
        Assertions.assertThrows(IllegalStateException.class, () -> WorkerModule.findTransferExtension(transferExtensions, "FOO"));
    }
}

