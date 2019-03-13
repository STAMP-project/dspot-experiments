/**
 * Copyright the original author or authors
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
package org.jivesoftware.smackx.caps;


import StringUtils.SHA1;
import java.io.IOException;
import org.jivesoftware.smack.util.stringencoder.Base32;
import org.jivesoftware.smackx.InitExtensions;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.junit.Assert;
import org.junit.Test;
import org.jxmpp.stringprep.XmppStringprepException;

import static EntityCapsManager.persistentCache;


public class EntityCapsManagerTest extends InitExtensions {
    /**
     * <a href="http://xmpp.org/extensions/xep-0115.html#ver-gen-complex">XEP-
     * 0115 Complex Generation Example</a>.
     *
     * @throws XmppStringprepException
     * 		
     */
    @Test
    public void testComplexGenerationExample() throws XmppStringprepException {
        DiscoverInfo di = EntityCapsManagerTest.createComplexSamplePacket();
        CapsVersionAndHash versionAndHash = EntityCapsManager.generateVerificationString(di, SHA1);
        Assert.assertEquals("q07IKJEyjvHSyhy//CH0CxmKi8w=", versionAndHash.version);
    }

    @Test
    public void testSimpleDirectoryCacheBase32() throws IOException {
        persistentCache = null;
        EntityCapsManagerTest.testSimpleDirectoryCache(Base32.getStringEncoder());
    }

    @Test
    public void testVerificationDuplicateFeatures() throws XmppStringprepException {
        DiscoverInfo di = EntityCapsManagerTest.createMalformedDiscoverInfo();
        Assert.assertTrue(di.containsDuplicateFeatures());
    }

    @Test
    public void testVerificationDuplicateIdentities() throws XmppStringprepException {
        DiscoverInfo di = EntityCapsManagerTest.createMalformedDiscoverInfo();
        Assert.assertTrue(di.containsDuplicateIdentities());
    }
}

