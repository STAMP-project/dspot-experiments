/**
 * *****************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * *****************************************************************************
 */
package org.springside.modules.metrics;


import org.junit.Test;
import org.springside.modules.metrics.metric.Histogram;
import org.springside.modules.metrics.metric.HistogramMetric;


public class HistogramTest {
    @Test
    public void normal() {
        Histogram histogram = new Histogram(90.0, 95.0);
        for (int i = 1; i <= 100; i++) {
            histogram.update(i);
        }
        HistogramMetric metric = histogram.calculateMetric();
        assertThat(metric.min).isEqualTo(1);
        assertThat(metric.max).isEqualTo(100);
        assertThat(metric.avg).isEqualTo(50.5);
        assertThat(metric.pcts.get(90.0)).isEqualTo(90);
        assertThat(metric.pcts.get(95.0)).isEqualTo(95);
        for (int i = 1; i <= 100; i++) {
            histogram.update((i * 2));
        }
        metric = histogram.calculateMetric();
        assertThat(metric.min).isEqualTo(2);
        assertThat(metric.max).isEqualTo(200);
        assertThat(metric.avg).isEqualTo(101);
        assertThat(metric.pcts.get(90.0)).isEqualTo(180);
        assertThat(metric.pcts.get(95.0)).isEqualTo(190);
    }

    @Test
    public void fewData() {
        Histogram histogram = new Histogram(90.0, 95.0);
        histogram.update(1);
        HistogramMetric metric = histogram.calculateMetric();
        assertThat(metric.pcts.get(90.0)).isEqualTo(1);
        assertThat(metric.pcts.get(95.0)).isEqualTo(1);
        for (int i = 1; i <= 3; i++) {
            histogram.update(i);
        }
        metric = histogram.calculateMetric();
        assertThat(metric.min).isEqualTo(1);
        assertThat(metric.max).isEqualTo(3);
        assertThat(metric.avg).isEqualTo(2);
        assertThat(metric.pcts.get(90.0)).isEqualTo(3);
        assertThat(metric.pcts.get(95.0)).isEqualTo(3);
    }

    @Test
    public void emptyMesures() {
        Histogram histogram = new Histogram(90.0, 95.0);
        HistogramMetric metric = histogram.calculateMetric();
        assertThat(metric.min).isZero();
        assertThat(metric.max).isZero();
        assertThat(metric.avg).isZero();
        assertThat(metric.pcts.get(90.0)).isZero();
    }

    @Test
    public void emptyPcts() {
        Histogram histogram = new Histogram();
        for (int i = 1; i <= 3; i++) {
            histogram.update(i);
        }
        HistogramMetric metric = histogram.calculateMetric();
        assertThat(metric.max).isEqualTo(3);
        assertThat(metric.pcts).isEmpty();
        assertThat(metric.pcts.get(90.0)).isNull();
    }
}

