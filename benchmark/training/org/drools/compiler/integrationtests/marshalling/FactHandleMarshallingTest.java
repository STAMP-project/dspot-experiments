/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests.marshalling;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import org.drools.compiler.integrationtests.marshalling.util.OldOutputMarshallerMethods;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.marshalling.impl.InputMarshaller;
import org.drools.core.marshalling.impl.MarshallerProviderImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.internal.marshalling.MarshallerFactory;


public class FactHandleMarshallingTest {
    @Test
    public void backwardsCompatibleEventFactHandleTest() throws IOException, ClassNotFoundException {
        InternalKnowledgeBase kBase = createKnowledgeBase();
        StatefulKnowledgeSessionImpl wm = createWorkingMemory(kBase);
        InternalFactHandle factHandle = createEventFactHandle(wm, kBase);
        // marshall/serialize workItem
        byte[] byteArray;
        {
            ObjectMarshallingStrategy[] strats = new ObjectMarshallingStrategy[]{ MarshallerFactory.newSerializeMarshallingStrategy(), new MarshallerProviderImpl().newIdentityMarshallingStrategy() };
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MarshallerWriteContext outContext = new MarshallerWriteContext(baos, null, null, null, new org.drools.core.marshalling.impl.ObjectMarshallingStrategyStoreImpl(strats), true, true, null);
            OldOutputMarshallerMethods.writeFactHandle_v1(outContext, ((ObjectOutputStream) (outContext)), outContext.objectMarshallingStrategyStore, 2, factHandle);
            outContext.close();
            byteArray = baos.toByteArray();
        }
        // unmarshall/deserialize workItem
        InternalFactHandle newFactHandle;
        {
            // Only put serialization strategy in
            ObjectMarshallingStrategy[] newStrats = new ObjectMarshallingStrategy[]{ MarshallerFactory.newSerializeMarshallingStrategy() };
            ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
            MarshallerReaderContext inContext = new MarshallerReaderContext(bais, null, null, new org.drools.core.marshalling.impl.ObjectMarshallingStrategyStoreImpl(newStrats), Collections.EMPTY_MAP, true, true, null);
            inContext.wm = wm;
            newFactHandle = InputMarshaller.readFactHandle(inContext);
            inContext.close();
        }
        Assert.assertTrue("Serialized FactHandle not the same as the original.", compareInstances(factHandle, newFactHandle));
    }
}

