/**
 * Copyright 2017 Paul Schaub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smackx.jingle;


import JingleReason.Busy;
import JingleReason.Cancel;
import JingleReason.ConnectivityError;
import JingleReason.Decline;
import JingleReason.Expired;
import JingleReason.FailedApplication;
import JingleReason.FailedTransport;
import JingleReason.GeneralError;
import JingleReason.Gone;
import JingleReason.IncompatibleParameters;
import JingleReason.MediaError;
import JingleReason.Reason;
import JingleReason.SecurityError;
import JingleReason.Success;
import JingleReason.Timeout;
import JingleReason.UnsupportedApplications;
import JingleReason.UnsupportedTransports;
import junit.framework.TestCase;
import org.jivesoftware.smack.test.util.SmackTestSuite;
import org.jivesoftware.smackx.jingle.element.JingleReason;
import org.junit.Test;


/**
 * Test JingleReason functionality.
 */
public class JingleReasonTest extends SmackTestSuite {
    @Test
    public void parserTest() {
        TestCase.assertEquals("<reason><success/></reason>", Success.toXML().toString());
        TestCase.assertEquals("<reason><busy/></reason>", Busy.toXML().toString());
        TestCase.assertEquals("<reason><cancel/></reason>", Cancel.toXML().toString());
        TestCase.assertEquals("<reason><connectivity-error/></reason>", ConnectivityError.toXML().toString());
        TestCase.assertEquals("<reason><decline/></reason>", Decline.toXML().toString());
        TestCase.assertEquals("<reason><expired/></reason>", Expired.toXML().toString());
        TestCase.assertEquals("<reason><unsupported-transports/></reason>", UnsupportedTransports.toXML().toString());
        TestCase.assertEquals("<reason><failed-transport/></reason>", FailedTransport.toXML().toString());
        TestCase.assertEquals("<reason><general-error/></reason>", GeneralError.toXML().toString());
        TestCase.assertEquals("<reason><gone/></reason>", Gone.toXML().toString());
        TestCase.assertEquals("<reason><media-error/></reason>", MediaError.toXML().toString());
        TestCase.assertEquals("<reason><security-error/></reason>", SecurityError.toXML().toString());
        TestCase.assertEquals("<reason><unsupported-applications/></reason>", UnsupportedApplications.toXML().toString());
        TestCase.assertEquals("<reason><timeout/></reason>", Timeout.toXML().toString());
        TestCase.assertEquals("<reason><failed-application/></reason>", FailedApplication.toXML().toString());
        TestCase.assertEquals("<reason><incompatible-parameters/></reason>", IncompatibleParameters.toXML().toString());
        TestCase.assertEquals("<reason><alternative-session><sid>1234</sid></alternative-session></reason>", JingleReason.AlternativeSession("1234").toXML().toString());
    }

    @Test(expected = NullPointerException.class)
    public void alternativeSessionEmptyStringTest() {
        // Alternative sessionID must not be empty
        JingleReason.AlternativeSession("");
    }

    @Test(expected = NullPointerException.class)
    public void alternativeSessionNullStringTest() {
        // Alternative sessionID must not be null
        JingleReason.AlternativeSession(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalArgumentTest() {
        Reason.fromString("illegal-reason");
    }
}

