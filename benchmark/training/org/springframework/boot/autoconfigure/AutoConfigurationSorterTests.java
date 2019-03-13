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
package org.springframework.boot.autoconfigure;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.Ordered;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;


/**
 * Tests for {@link AutoConfigurationSorter}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class AutoConfigurationSorterTests {
    private static final String DEFAULT = AutoConfigurationSorterTests.OrderUnspecified.class.getName();

    private static final String LOWEST = AutoConfigurationSorterTests.OrderLowest.class.getName();

    private static final String HIGHEST = AutoConfigurationSorterTests.OrderHighest.class.getName();

    private static final String A = AutoConfigurationSorterTests.AutoConfigureA.class.getName();

    private static final String B = AutoConfigurationSorterTests.AutoConfigureB.class.getName();

    private static final String C = AutoConfigurationSorterTests.AutoConfigureC.class.getName();

    private static final String D = AutoConfigurationSorterTests.AutoConfigureD.class.getName();

    private static final String E = AutoConfigurationSorterTests.AutoConfigureE.class.getName();

    private static final String W = AutoConfigurationSorterTests.AutoConfigureW.class.getName();

    private static final String X = AutoConfigurationSorterTests.AutoConfigureX.class.getName();

    private static final String Y = AutoConfigurationSorterTests.AutoConfigureY.class.getName();

    private static final String Z = AutoConfigurationSorterTests.AutoConfigureZ.class.getName();

    private static final String A2 = AutoConfigurationSorterTests.AutoConfigureA2.class.getName();

    private static final String W2 = AutoConfigurationSorterTests.AutoConfigureW2.class.getName();

    private AutoConfigurationSorter sorter;

    private AutoConfigurationMetadata autoConfigurationMetadata = Mockito.mock(AutoConfigurationMetadata.class);

    @Test
    public void byOrderAnnotation() {
        List<String> actual = this.sorter.getInPriorityOrder(Arrays.asList(AutoConfigurationSorterTests.LOWEST, AutoConfigurationSorterTests.HIGHEST, AutoConfigurationSorterTests.DEFAULT));
        assertThat(actual).containsExactly(AutoConfigurationSorterTests.HIGHEST, AutoConfigurationSorterTests.DEFAULT, AutoConfigurationSorterTests.LOWEST);
    }

    @Test
    public void byAutoConfigureAfter() {
        List<String> actual = this.sorter.getInPriorityOrder(Arrays.asList(AutoConfigurationSorterTests.A, AutoConfigurationSorterTests.B, AutoConfigurationSorterTests.C));
        assertThat(actual).containsExactly(AutoConfigurationSorterTests.C, AutoConfigurationSorterTests.B, AutoConfigurationSorterTests.A);
    }

    @Test
    public void byAutoConfigureBefore() {
        List<String> actual = this.sorter.getInPriorityOrder(Arrays.asList(AutoConfigurationSorterTests.X, AutoConfigurationSorterTests.Y, AutoConfigurationSorterTests.Z));
        assertThat(actual).containsExactly(AutoConfigurationSorterTests.Z, AutoConfigurationSorterTests.Y, AutoConfigurationSorterTests.X);
    }

    @Test
    public void byAutoConfigureAfterDoubles() {
        List<String> actual = this.sorter.getInPriorityOrder(Arrays.asList(AutoConfigurationSorterTests.A, AutoConfigurationSorterTests.B, AutoConfigurationSorterTests.C, AutoConfigurationSorterTests.E));
        assertThat(actual).containsExactly(AutoConfigurationSorterTests.C, AutoConfigurationSorterTests.E, AutoConfigurationSorterTests.B, AutoConfigurationSorterTests.A);
    }

    @Test
    public void byAutoConfigureMixedBeforeAndAfter() {
        List<String> actual = this.sorter.getInPriorityOrder(Arrays.asList(AutoConfigurationSorterTests.A, AutoConfigurationSorterTests.B, AutoConfigurationSorterTests.C, AutoConfigurationSorterTests.W, AutoConfigurationSorterTests.X));
        assertThat(actual).containsExactly(AutoConfigurationSorterTests.C, AutoConfigurationSorterTests.W, AutoConfigurationSorterTests.B, AutoConfigurationSorterTests.A, AutoConfigurationSorterTests.X);
    }

    @Test
    public void byAutoConfigureMixedBeforeAndAfterWithClassNames() {
        List<String> actual = this.sorter.getInPriorityOrder(Arrays.asList(AutoConfigurationSorterTests.A2, AutoConfigurationSorterTests.B, AutoConfigurationSorterTests.C, AutoConfigurationSorterTests.W2, AutoConfigurationSorterTests.X));
        assertThat(actual).containsExactly(AutoConfigurationSorterTests.C, AutoConfigurationSorterTests.W2, AutoConfigurationSorterTests.B, AutoConfigurationSorterTests.A2, AutoConfigurationSorterTests.X);
    }

    @Test
    public void byAutoConfigureMixedBeforeAndAfterWithDifferentInputOrder() {
        List<String> actual = this.sorter.getInPriorityOrder(Arrays.asList(AutoConfigurationSorterTests.W, AutoConfigurationSorterTests.X, AutoConfigurationSorterTests.A, AutoConfigurationSorterTests.B, AutoConfigurationSorterTests.C));
        assertThat(actual).containsExactly(AutoConfigurationSorterTests.C, AutoConfigurationSorterTests.W, AutoConfigurationSorterTests.B, AutoConfigurationSorterTests.A, AutoConfigurationSorterTests.X);
    }

    @Test
    public void byAutoConfigureAfterWithMissing() {
        List<String> actual = this.sorter.getInPriorityOrder(Arrays.asList(AutoConfigurationSorterTests.A, AutoConfigurationSorterTests.B));
        assertThat(actual).containsExactly(AutoConfigurationSorterTests.B, AutoConfigurationSorterTests.A);
    }

    @Test
    public void byAutoConfigureAfterWithCycle() {
        this.sorter = new AutoConfigurationSorter(new CachingMetadataReaderFactory(), this.autoConfigurationMetadata);
        assertThatIllegalStateException().isThrownBy(() -> this.sorter.getInPriorityOrder(Arrays.asList(A, B, C, D))).withMessageContaining("AutoConfigure cycle detected");
    }

    @Test
    public void usesAnnotationPropertiesWhenPossible() throws Exception {
        MetadataReaderFactory readerFactory = new AutoConfigurationSorterTests.SkipCycleMetadataReaderFactory();
        this.autoConfigurationMetadata = getAutoConfigurationMetadata(AutoConfigurationSorterTests.A2, AutoConfigurationSorterTests.B, AutoConfigurationSorterTests.C, AutoConfigurationSorterTests.W2, AutoConfigurationSorterTests.X);
        this.sorter = new AutoConfigurationSorter(readerFactory, this.autoConfigurationMetadata);
        List<String> actual = this.sorter.getInPriorityOrder(Arrays.asList(AutoConfigurationSorterTests.A2, AutoConfigurationSorterTests.B, AutoConfigurationSorterTests.C, AutoConfigurationSorterTests.W2, AutoConfigurationSorterTests.X));
        assertThat(actual).containsExactly(AutoConfigurationSorterTests.C, AutoConfigurationSorterTests.W2, AutoConfigurationSorterTests.B, AutoConfigurationSorterTests.A2, AutoConfigurationSorterTests.X);
    }

    @Test
    public void useAnnotationWithNoDirectLink() throws Exception {
        MetadataReaderFactory readerFactory = new AutoConfigurationSorterTests.SkipCycleMetadataReaderFactory();
        this.autoConfigurationMetadata = getAutoConfigurationMetadata(AutoConfigurationSorterTests.A, AutoConfigurationSorterTests.B, AutoConfigurationSorterTests.E);
        this.sorter = new AutoConfigurationSorter(readerFactory, this.autoConfigurationMetadata);
        List<String> actual = this.sorter.getInPriorityOrder(Arrays.asList(AutoConfigurationSorterTests.A, AutoConfigurationSorterTests.E));
        assertThat(actual).containsExactly(AutoConfigurationSorterTests.E, AutoConfigurationSorterTests.A);
    }

    @Test
    public void useAnnotationWithNoDirectLinkAndCycle() throws Exception {
        MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory();
        this.autoConfigurationMetadata = getAutoConfigurationMetadata(AutoConfigurationSorterTests.A, AutoConfigurationSorterTests.B, AutoConfigurationSorterTests.D);
        this.sorter = new AutoConfigurationSorter(readerFactory, this.autoConfigurationMetadata);
        assertThatIllegalStateException().isThrownBy(() -> this.sorter.getInPriorityOrder(Arrays.asList(D, B))).withMessageContaining("AutoConfigure cycle detected");
    }

    @AutoConfigureOrder
    public static class OrderUnspecified {}

    @AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
    public static class OrderLowest {}

    @AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
    public static class OrderHighest {}

    @AutoConfigureAfter(AutoConfigurationSorterTests.AutoConfigureB.class)
    public static class AutoConfigureA {}

    @AutoConfigureAfter(name = "org.springframework.boot.autoconfigure.AutoConfigurationSorterTests$AutoConfigureB")
    public static class AutoConfigureA2 {}

    @AutoConfigureAfter({ AutoConfigurationSorterTests.AutoConfigureC.class, AutoConfigurationSorterTests.AutoConfigureD.class, AutoConfigurationSorterTests.AutoConfigureE.class })
    public static class AutoConfigureB {}

    public static class AutoConfigureC {}

    @AutoConfigureAfter(AutoConfigurationSorterTests.AutoConfigureA.class)
    public static class AutoConfigureD {}

    public static class AutoConfigureE {}

    @AutoConfigureBefore(AutoConfigurationSorterTests.AutoConfigureB.class)
    public static class AutoConfigureW {}

    @AutoConfigureBefore(name = "org.springframework.boot.autoconfigure.AutoConfigurationSorterTests$AutoConfigureB")
    public static class AutoConfigureW2 {}

    public static class AutoConfigureX {}

    @AutoConfigureBefore(AutoConfigurationSorterTests.AutoConfigureX.class)
    public static class AutoConfigureY {}

    @AutoConfigureBefore(AutoConfigurationSorterTests.AutoConfigureY.class)
    public static class AutoConfigureZ {}

    private static class SkipCycleMetadataReaderFactory extends CachingMetadataReaderFactory {
        @Override
        public MetadataReader getMetadataReader(String className) throws IOException {
            if (className.equals(AutoConfigurationSorterTests.D)) {
                throw new IOException();
            }
            return super.getMetadataReader(className);
        }
    }
}

