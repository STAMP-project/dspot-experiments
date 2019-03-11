/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core;


import java.sql.SQLException;
import org.jdbi.v3.core.extension.ExtensionFactory;
import org.jdbi.v3.core.extension.HandleSupplier;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class TestOnDemandMethodBehavior {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ExtensionFactory mockExtensionFactory;

    @Mock
    private TestOnDemandMethodBehavior.UselessDao mockDao;

    private Jdbi db;

    private TestOnDemandMethodBehavior.UselessDao onDemand;

    private TestOnDemandMethodBehavior.UselessDao anotherOnDemand;

    public interface UselessDao {
        default void run(Runnable runnable) {
            runnable.run();
        }

        default void blowUp() throws SQLException {
            throw new SQLException("boom");
        }

        void foo();
    }

    public class UselessDaoExtension implements ExtensionFactory {
        @Override
        public boolean accepts(Class<?> extensionType) {
            return TestOnDemandMethodBehavior.UselessDao.class.equals(extensionType);
        }

        @Override
        public <E> E attach(Class<E> extensionType, HandleSupplier handle) {
            return extensionType.cast(((TestOnDemandMethodBehavior.UselessDao) (() -> {
            })));
        }
    }

    @Test
    public void testEqualsDoesntAttach() {
        assertThat(onDemand).isEqualTo(onDemand);
        assertThat(onDemand).isNotEqualTo(anotherOnDemand);
        Mockito.verify(mockExtensionFactory, Mockito.never()).attach(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testHashCodeDoesntAttach() {
        assertThat(onDemand.hashCode()).isEqualTo(onDemand.hashCode());
        assertThat(onDemand.hashCode()).isNotEqualTo(anotherOnDemand.hashCode());
        Mockito.verify(mockExtensionFactory, Mockito.never()).attach(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testToStringDoesntAttach() {
        assertThat(onDemand.toString()).isNotNull();
        Mockito.verify(mockExtensionFactory, Mockito.never()).attach(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testReentrantCallReusesExtension() {
        Mockito.when(mockExtensionFactory.attach(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockDao).thenThrow(IllegalStateException.class);
        Mockito.doCallRealMethod().when(mockDao).run(ArgumentMatchers.any());
        onDemand.run(onDemand::foo);
        Mockito.verify(mockExtensionFactory).attach(ArgumentMatchers.eq(TestOnDemandMethodBehavior.UselessDao.class), ArgumentMatchers.any());
        Mockito.verify(mockDao).run(ArgumentMatchers.any());
        Mockito.verify(mockDao).foo();
    }

    @Test
    public void testExceptionThrown() {
        db.registerExtension(new TestOnDemandMethodBehavior.UselessDaoExtension());
        TestOnDemandMethodBehavior.UselessDao uselessDao = db.onDemand(TestOnDemandMethodBehavior.UselessDao.class);
        assertThatThrownBy(uselessDao::blowUp).isInstanceOf(SQLException.class);
    }
}

