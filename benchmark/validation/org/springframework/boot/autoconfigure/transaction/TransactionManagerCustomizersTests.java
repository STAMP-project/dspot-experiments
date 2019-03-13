/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.autoconfigure.transaction;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;


/**
 * Tests for {@link TransactionManagerCustomizers}.
 *
 * @author Phillip Webb
 */
public class TransactionManagerCustomizersTests {
    @Test
    public void customizeWithNullCustomizersShouldDoNothing() {
        new TransactionManagerCustomizers(null).customize(Mockito.mock(PlatformTransactionManager.class));
    }

    @Test
    public void customizeShouldCheckGeneric() {
        List<TransactionManagerCustomizersTests.TestCustomizer<?>> list = new ArrayList<>();
        list.add(new TransactionManagerCustomizersTests.TestCustomizer<>());
        list.add(new TransactionManagerCustomizersTests.TestJtaCustomizer());
        TransactionManagerCustomizers customizers = new TransactionManagerCustomizers(list);
        customizers.customize(Mockito.mock(PlatformTransactionManager.class));
        customizers.customize(Mockito.mock(JtaTransactionManager.class));
        assertThat(list.get(0).getCount()).isEqualTo(2);
        assertThat(list.get(1).getCount()).isEqualTo(1);
    }

    private static class TestCustomizer<T extends PlatformTransactionManager> implements PlatformTransactionManagerCustomizer<T> {
        private int count;

        @Override
        public void customize(T transactionManager) {
            (this.count)++;
        }

        public int getCount() {
            return this.count;
        }
    }

    private static class TestJtaCustomizer extends TransactionManagerCustomizersTests.TestCustomizer<JtaTransactionManager> {}
}

