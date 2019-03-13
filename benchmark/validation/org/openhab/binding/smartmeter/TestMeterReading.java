/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.smartmeter;


import ConnectorBase.NUMBER_OF_RETRIES;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openhab.binding.smartmeter.connectors.ConnectorBase;
import org.openhab.binding.smartmeter.internal.MeterDevice;
import org.openhab.binding.smartmeter.internal.MeterValueListener;


/**
 *
 *
 * @author Matthias Steigenberger - Initial contribution
 */
public class TestMeterReading {
    @Test
    public void testContinousReading() throws Exception {
        final Duration period = Duration.ofSeconds(1);
        final int executionCount = 5;
        MockMeterReaderConnector connector = getMockedConnector(false, () -> new Object());
        MeterDevice<Object> meter = getMeterDevice(connector);
        MeterValueListener changeListener = Mockito.mock(MeterValueListener.class);
        meter.addValueChangeListener(changeListener);
        Disposable disposable = meter.readValues(5000, Executors.newScheduledThreadPool(1), period);
        try {
            Mockito.verify(changeListener, Mockito.after(((executionCount * (period.toMillis())) + ((period.toMillis()) / 2))).never()).errorOccurred(ArgumentMatchers.any());
            Mockito.verify(changeListener, Mockito.times(executionCount)).valueChanged(ArgumentMatchers.any());
        } finally {
            disposable.dispose();
        }
    }

    @Test
    public void testRetryHandling() {
        final Duration period = Duration.ofSeconds(1);
        MockMeterReaderConnector connector = Mockito.spy(getMockedConnector(true, () -> {
            throw new IllegalArgumentException();
        }));
        MeterDevice<Object> meter = getMeterDevice(connector);
        MeterValueListener changeListener = Mockito.mock(MeterValueListener.class);
        meter.addValueChangeListener(changeListener);
        Disposable disposable = meter.readValues(5000, Executors.newScheduledThreadPool(1), period);
        try {
            Mockito.verify(changeListener, Mockito.after((((period.toMillis()) + ((2 * (period.toMillis())) * (ConnectorBase.NUMBER_OF_RETRIES))) + ((period.toMillis()) / 2))).times(1)).errorOccurred(ArgumentMatchers.any());
            Mockito.verify(connector, Mockito.times(NUMBER_OF_RETRIES)).retryHook(ArgumentMatchers.anyInt());
        } finally {
            disposable.dispose();
        }
    }

    @Test
    public void testTimeoutHandling() {
        final Duration period = Duration.ofSeconds(2);
        final int timeout = 5000;
        MockMeterReaderConnector connector = Mockito.spy(getMockedConnector(true, () -> {
            try {
                Thread.sleep((timeout + 2000));
            } catch (InterruptedException e) {
            }
            return new Object();
        }));
        MeterDevice<Object> meter = getMeterDevice(connector);
        MeterValueListener changeListener = Mockito.mock(MeterValueListener.class);
        meter.addValueChangeListener(changeListener);
        Disposable disposable = meter.readValues(5000, Executors.newScheduledThreadPool(2), period);
        try {
            Mockito.verify(changeListener, Mockito.after((timeout + 3000)).times(1)).errorOccurred(ArgumentMatchers.any(TimeoutException.class));
        } finally {
            disposable.dispose();
        }
    }

    @Test
    public void shouldNotReportToFallbackException() {
        final Duration period = Duration.ofSeconds(2);
        final int timeout = 5000;
        MockMeterReaderConnector connector = Mockito.spy(getMockedConnector(true, () -> {
            try {
                Thread.sleep((timeout + 2000));
            } catch (InterruptedException e) {
            }
            throw new RuntimeException(new IOException("fucked up"));
        }));
        MeterDevice<Object> meter = getMeterDevice(connector);
        Consumer<Throwable> errorHandler = Mockito.mock(Consumer.class);
        RxJavaPlugins.setErrorHandler(errorHandler);
        MeterValueListener changeListener = Mockito.mock(MeterValueListener.class);
        meter.addValueChangeListener(changeListener);
        Disposable disposable = meter.readValues(5000, Executors.newScheduledThreadPool(2), period);
        try {
            Mockito.verify(changeListener, Mockito.after((timeout + 3000)).times(1)).errorOccurred(ArgumentMatchers.any(TimeoutException.class));
            Mockito.verifyNoMoreInteractions(errorHandler);
        } finally {
            disposable.dispose();
        }
    }
}

