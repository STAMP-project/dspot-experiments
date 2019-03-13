/**
 * Copyright 2014 Goldman Sachs.
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
package com.gs.collections.impl.lazy.iterator;


import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import java.util.NoSuchElementException;
import org.junit.Test;


public class SelectIteratorTest {
    @Test
    public void iterator() {
        MutableList<Boolean> list = FastList.newListWith(Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, null, null, Boolean.FALSE, Boolean.TRUE, null);
        this.assertElements(new SelectIterator(list.iterator(), Boolean.TRUE::equals));
        this.assertElements(new SelectIterator(list, Boolean.TRUE::equals));
    }

    @Test
    public void noSuchElementException() {
        Verify.assertThrows(NoSuchElementException.class, () -> new SelectIterator<>(Lists.fixedSize.of(), ( ignored) -> true).next());
    }

    @Test
    public void remove() {
        Verify.assertThrows(UnsupportedOperationException.class, () -> new SelectIterator<>(Lists.fixedSize.of(), ( ignored) -> true).remove());
    }
}

