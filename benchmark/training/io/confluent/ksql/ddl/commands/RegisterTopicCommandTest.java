/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.ddl.commands;


import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.function.InternalFunctionRegistry;
import io.confluent.ksql.metastore.MutableMetaStore;
import io.confluent.ksql.parser.tree.RegisterTopic;
import io.confluent.ksql.util.MetaStoreFixture;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class RegisterTopicCommandTest {
    @Mock(NICE)
    private RegisterTopic registerTopicStatement;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final MutableMetaStore metaStore = MetaStoreFixture.getNewMetaStore(new InternalFunctionRegistry());

    @Test
    public void testRegisterAlreadyRegisteredTopicThrowsException() {
        final RegisterTopicCommand cmd;
        // Given:
        givenProperties(RegisterTopicCommandTest.propsWith(ImmutableMap.of()));
        cmd = createCmd();
        cmd.run(metaStore);
        // Then:
        expectedException.expectMessage("A topic with name 'name' already exists");
        // When:
        cmd.run(metaStore);
    }
}

