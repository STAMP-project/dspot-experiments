/**
 * Copyright 2019-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amazonaws.retry;


import com.amazonaws.SdkBaseException;
import java.util.Date;
import org.junit.Test;


public class ClockSkewAdjusterTest {
    private static final int SKEWED_SECONDS = 60 * 60;

    private static final int SKEWED_MILLIS = (ClockSkewAdjusterTest.SKEWED_SECONDS) * 1000;

    private ClockSkewAdjuster adjuster = new ClockSkewAdjuster();

    private Date unskewedDate = new Date();

    private Date futureSkewedDate = new Date(((unskewedDate.getTime()) + (ClockSkewAdjusterTest.SKEWED_MILLIS)));

    private Date pastSkewedDate = new Date(((unskewedDate.getTime()) - (ClockSkewAdjusterTest.SKEWED_MILLIS)));

    @Test
    public void nonSkewErrorsDoNotAdjust() {
        assertDoNotAdjust(clientRequest(0), new SdkBaseException(""), httpResponse(futureSkewedDate));
        assertDoNotAdjust(clientRequest(0), amazonServiceException("", "NotFound", 404), httpResponse(unskewedDate));
        assertDoNotAdjust(clientRequest(1000), amazonServiceException("", "NotFound", 404), httpResponse(unskewedDate));
        assertDoNotAdjust(clientRequest(0), amazonServiceException("", "InternalServiceError", 500), httpResponse(unskewedDate));
        assertDoNotAdjust(clientRequest(0), amazonServiceException("", "NotFound", 404), httpResponse(futureSkewedDate));
        assertDoNotAdjust(clientRequest(0), amazonServiceException("", "NotFound", 404), httpResponse(pastSkewedDate));
    }

    @Test
    public void obviousSkewErrorsAdjust() {
        assertAdjusts((-(ClockSkewAdjusterTest.SKEWED_SECONDS)), clientRequest(0), amazonServiceException("", "RequestTimeTooSkewed", 400), httpResponse(futureSkewedDate));
        assertAdjusts(ClockSkewAdjusterTest.SKEWED_SECONDS, clientRequest(0), amazonServiceException("", "InvalidSignatureException", 400), httpResponse(pastSkewedDate));
        assertAdjusts(0, clientRequest(0), amazonServiceException("", "RequestTimeTooSkewed", 400), httpResponse(unskewedDate));
    }

    @Test
    public void noDateHeaderCanStillAdjust() {
        assertAdjusts((-(ClockSkewAdjusterTest.SKEWED_SECONDS)), clientRequest(0), amazonServiceException(sqsExceptionMessage(unskewedDate, futureSkewedDate), "RequestTimeTooSkewed", 400), httpResponse());
        assertAdjusts(ClockSkewAdjusterTest.SKEWED_SECONDS, clientRequest(0), amazonServiceException(sqsExceptionMessage(unskewedDate, pastSkewedDate), "RequestTimeTooSkewed", 400), httpResponse());
    }

    @Test
    public void invalidDateFormatsDontAdjust() {
        assertDoNotAdjust(clientRequest(0), amazonServiceException(sqsExceptionMessage("X", "Y"), "RequestTimeTooSkewed", 400), httpResponse());
        assertDoNotAdjust(clientRequest(0), amazonServiceException("", "RequestTimeTooSkewed", 400), httpResponse("X"));
    }

    @Test
    public void noDateNoAdjust() {
        assertDoNotAdjust(clientRequest(0), amazonServiceException("", "RequestTimeTooSkewed", 403), httpResponse());
    }

    @Test
    public void authenticationErrorsDontAdjustIfTimeIsntSkewed() {
        assertDoNotAdjust(clientRequest(0), amazonServiceException("", "", 403), httpResponse(unskewedDate));
        assertDoNotAdjust(clientRequest(0), amazonServiceException("", "", 401), httpResponse(unskewedDate));
    }

    @Test
    public void authenticationErrorsAdjustIfTimeIsSkewed() {
        assertAdjusts((-(ClockSkewAdjusterTest.SKEWED_SECONDS)), clientRequest(0), amazonServiceException("", "", 401), httpResponse(futureSkewedDate));
        assertAdjusts(ClockSkewAdjusterTest.SKEWED_SECONDS, clientRequest(0), amazonServiceException("", "", 401), httpResponse(pastSkewedDate));
        assertAdjusts((-(ClockSkewAdjusterTest.SKEWED_SECONDS)), clientRequest(0), amazonServiceException("", "", 403), httpResponse(futureSkewedDate));
        assertAdjusts(ClockSkewAdjusterTest.SKEWED_SECONDS, clientRequest(0), amazonServiceException("", "", 403), httpResponse(pastSkewedDate));
        assertAdjusts((-(ClockSkewAdjusterTest.SKEWED_SECONDS)), clientRequest(ClockSkewAdjusterTest.SKEWED_SECONDS), amazonServiceException("", "", 401), httpResponse(unskewedDate));
        assertAdjusts(ClockSkewAdjusterTest.SKEWED_SECONDS, clientRequest((-(ClockSkewAdjusterTest.SKEWED_SECONDS))), amazonServiceException("", "", 403), httpResponse(unskewedDate));
    }
}

