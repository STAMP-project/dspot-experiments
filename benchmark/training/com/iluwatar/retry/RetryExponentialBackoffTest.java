/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 Ilkka Sepp??l??
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.iluwatar.retry;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for {@link Retry}.
 *
 * @author George Aristy (george.aristy@gmail.com)
 */
public class RetryExponentialBackoffTest {
    /**
     * Should contain all errors thrown.
     */
    @Test
    public void errors() throws Exception {
        final BusinessException e = new BusinessException("unhandled");
        final RetryExponentialBackoff<String> retry = new RetryExponentialBackoff(() -> {
            throw e;
        }, 2, 0);
        try {
            retry.perform();
        } catch (BusinessException ex) {
            // ignore
        }
        MatcherAssert.assertThat(retry.errors(), CoreMatchers.hasItem(e));
    }

    /**
     * No exceptions will be ignored, hence final number of attempts should be 1 even if we're asking
     * it to attempt twice.
     */
    @Test
    public void attempts() {
        final BusinessException e = new BusinessException("unhandled");
        final RetryExponentialBackoff<String> retry = new RetryExponentialBackoff(() -> {
            throw e;
        }, 2, 0);
        try {
            retry.perform();
        } catch (BusinessException ex) {
            // ignore
        }
        MatcherAssert.assertThat(retry.attempts(), CoreMatchers.is(1));
    }

    /**
     * Final number of attempts should be equal to the number of attempts asked because we are
     * asking it to ignore the exception that will be thrown.
     */
    @Test
    public void ignore() throws Exception {
        final BusinessException e = new CustomerNotFoundException("customer not found");
        final RetryExponentialBackoff<String> retry = new RetryExponentialBackoff(() -> {
            throw e;
        }, 2, 0, ( ex) -> .class.isAssignableFrom(ex.getClass()));
        try {
            retry.perform();
        } catch (BusinessException ex) {
            // ignore
        }
        MatcherAssert.assertThat(retry.attempts(), CoreMatchers.is(2));
    }
}

