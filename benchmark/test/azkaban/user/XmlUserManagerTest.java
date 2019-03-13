/**
 * Copyright 2014 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.user;


import azkaban.utils.Props;
import azkaban.utils.UndefinedPropertyException;
import org.junit.Assert;
import org.junit.Test;


public class XmlUserManagerTest {
    private final Props baseProps = new Props();

    /**
     * Testing for when the xml path isn't set in properties.
     */
    @Test
    public void testFilePropNotSet() throws Exception {
        final Props props = new Props(this.baseProps);
        // Should throw
        try {
            final XmlUserManager manager = new XmlUserManager(props);
        } catch (final UndefinedPropertyException e) {
            return;
        }
        Assert.fail("XmlUserManager should throw an exception when the file property isn't set");
    }
}

