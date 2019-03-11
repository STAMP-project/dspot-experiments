/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.inputs;


import com.google.common.collect.ImmutableSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import java.util.List;
import org.graylog2.database.MongoConnectionRule;
import org.graylog2.database.NotFoundException;
import org.graylog2.events.ClusterEventBus;
import org.graylog2.inputs.converters.ConverterFactory;
import org.graylog2.inputs.extractors.ExtractorFactory;
import org.graylog2.shared.inputs.MessageInputFactory;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class InputServiceImplTest {
    @ClassRule
    public static final InMemoryMongoDb IN_MEMORY_MONGO_DB = newInMemoryMongoDbRule().build();

    @Rule
    public MongoConnectionRule mongoRule = MongoConnectionRule.build("inputs-test");

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ExtractorFactory extractorFactory;

    @Mock
    private ConverterFactory converterFactory;

    @Mock
    private MessageInputFactory messageInputFactory;

    private ClusterEventBus clusterEventBus;

    private InputServiceImpl inputService;

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void allReturnsAllInputs() {
        final List<Input> inputs = inputService.all();
        assertThat(inputs).hasSize(3);
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void allOfThisNodeReturnsAllLocalAndGlobalInputs() {
        final List<Input> inputs = inputService.allOfThisNode("cd03ee44-b2a7-cafe-babe-0000deadbeef");
        assertThat(inputs).hasSize(3);
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void allOfThisNodeReturnsGlobalInputsIfNodeIDDoesNotExist() {
        final List<Input> inputs = inputService.allOfThisNode("cd03ee44-b2a7-0000-0000-000000000000");
        assertThat(inputs).hasSize(1);
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void findByIdsReturnsRequestedInputs() {
        assertThat(inputService.findByIds(ImmutableSet.of())).isEmpty();
        assertThat(inputService.findByIds(ImmutableSet.of("54e300000000000000000000"))).isEmpty();
        assertThat(inputService.findByIds(ImmutableSet.of("54e3deadbeefdeadbeef0001"))).hasSize(1);
        assertThat(inputService.findByIds(ImmutableSet.of("54e3deadbeefdeadbeef0001", "54e3deadbeefdeadbeef0003"))).hasSize(2);
        assertThat(inputService.findByIds(ImmutableSet.of("54e3deadbeefdeadbeef0001", "54e3deadbeefdeadbeef0003", "54e300000000000000000000"))).hasSize(2);
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void findReturnsExistingInput() throws NotFoundException {
        final Input input = inputService.find("54e3deadbeefdeadbeef0002");
        assertThat(input.getId()).isEqualTo("54e3deadbeefdeadbeef0002");
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void findThrowsNotFoundExceptionIfInputDoesNotExist() {
        assertThatThrownBy(() -> inputService.find("54e300000000000000000000")).isExactlyInstanceOf(NotFoundException.class);
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void globalCountReturnsNumberOfGlobalInputs() {
        assertThat(inputService.globalCount()).isEqualTo(1);
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void localCountReturnsNumberOfLocalInputs() {
        assertThat(inputService.localCount()).isEqualTo(2);
    }

    @Test
    @UsingDataSet(loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void localCountForNodeReturnsNumberOfLocalInputs() {
        assertThat(inputService.localCountForNode("cd03ee44-b2a7-cafe-babe-0000deadbeef")).isEqualTo(2);
        assertThat(inputService.localCountForNode("cd03ee44-b2a7-0000-0000-000000000000")).isEqualTo(0);
    }
}

