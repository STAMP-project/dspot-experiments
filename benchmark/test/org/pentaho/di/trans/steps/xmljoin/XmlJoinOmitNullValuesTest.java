/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.xmljoin;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * Test for XmlJoin step
 *
 * @author Pavel Sakun
 * @see XMLJoin
 */
@RunWith(MockitoJUnitRunner.class)
public class XmlJoinOmitNullValuesTest {
    StepMockHelper<XMLJoinMeta, XMLJoinData> smh;

    @Test
    public void testRemoveEmptyNodes() throws KettleException {
        doTest("<child><empty/><subChild a=\"\"><empty/></subChild><subChild><empty/></subChild><subChild><subSubChild a=\"\"/></subChild></child>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root xmlns=\"http://www.myns1.com\" xmlns:xsi=\"http://www.myns2.com\" xsi:schemalocation=\"http://www.mysl1.com\"></root>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root xmlns:xsi=\"http://www.myns2.com\" xsi:schemalocation=\"http://www.mysl1.com\"><child><subChild a=\"\"/><subChild><subSubChild a=\"\"/></subChild></child></root>");
    }
}

