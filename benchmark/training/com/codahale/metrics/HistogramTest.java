package com.codahale.metrics;


import org.junit.Test;
import org.mockito.Mockito;


public class HistogramTest {
    private final Reservoir reservoir = Mockito.mock(Reservoir.class);

    private final Histogram histogram = new Histogram(reservoir);

    @Test
    public void updatesTheCountOnUpdates() {
        assertThat(histogram.getCount()).isZero();
        histogram.update(1);
        assertThat(histogram.getCount()).isEqualTo(1);
    }

    @Test
    public void returnsTheSnapshotFromTheReservoir() {
        final Snapshot snapshot = Mockito.mock(Snapshot.class);
        Mockito.when(reservoir.getSnapshot()).thenReturn(snapshot);
        assertThat(histogram.getSnapshot()).isEqualTo(snapshot);
    }

    @Test
    public void updatesTheReservoir() throws Exception {
        histogram.update(1);
        Mockito.verify(reservoir).update(1);
    }
}

