/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
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
 * </p>
 */
package io.elasticjob.lite.lifecycle.internal.reg;


import com.google.common.base.Optional;
import io.elasticjob.lite.lifecycle.AbstractEmbedZookeeperBaseTest;
import io.elasticjob.lite.reg.zookeeper.ZookeeperConfiguration;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public final class RegistryCenterFactoryTest extends AbstractEmbedZookeeperBaseTest {
    @Test
    public void assertCreateCoordinatorRegistryCenterWithoutDigest() throws ReflectiveOperationException {
        ZookeeperConfiguration zkConfig = getZookeeperConfiguration(RegistryCenterFactory.createCoordinatorRegistryCenter(AbstractEmbedZookeeperBaseTest.getConnectionString(), "namespace", Optional.<String>absent()));
        Assert.assertThat(zkConfig.getNamespace(), Is.is("namespace"));
        Assert.assertNull(zkConfig.getDigest());
    }

    @Test
    public void assertCreateCoordinatorRegistryCenterWithDigest() throws ReflectiveOperationException {
        ZookeeperConfiguration zkConfig = getZookeeperConfiguration(RegistryCenterFactory.createCoordinatorRegistryCenter(AbstractEmbedZookeeperBaseTest.getConnectionString(), "namespace", Optional.of("digest")));
        Assert.assertThat(zkConfig.getNamespace(), Is.is("namespace"));
        Assert.assertThat(zkConfig.getDigest(), Is.is("digest"));
    }

    @Test
    public void assertCreateCoordinatorRegistryCenterFromCache() throws ReflectiveOperationException {
        RegistryCenterFactory.createCoordinatorRegistryCenter(AbstractEmbedZookeeperBaseTest.getConnectionString(), "otherNamespace", Optional.<String>absent());
        ZookeeperConfiguration zkConfig = getZookeeperConfiguration(RegistryCenterFactory.createCoordinatorRegistryCenter(AbstractEmbedZookeeperBaseTest.getConnectionString(), "otherNamespace", Optional.<String>absent()));
        Assert.assertThat(zkConfig.getNamespace(), Is.is("otherNamespace"));
        Assert.assertNull(zkConfig.getDigest());
    }
}

