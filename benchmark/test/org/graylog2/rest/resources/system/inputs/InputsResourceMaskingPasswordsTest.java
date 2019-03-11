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
package org.graylog2.rest.resources.system.inputs;


import TextField.Attribute.IS_PASSWORD;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import org.apache.shiro.subject.Subject;
import org.graylog2.database.NotFoundException;
import org.graylog2.inputs.Input;
import org.graylog2.inputs.InputService;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.configuration.fields.TextField;
import org.graylog2.rest.models.system.inputs.responses.InputSummary;
import org.graylog2.rest.models.system.inputs.responses.InputsList;
import org.graylog2.shared.inputs.InputDescription;
import org.graylog2.shared.inputs.MessageInputFactory;
import org.graylog2.shared.security.RestPermissions;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class InputsResourceMaskingPasswordsTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private InputService inputService;

    @Mock
    private MessageInputFactory messageInputFactory;

    @Mock
    private Subject currentSubject;

    private Map<String, InputDescription> availableInputs;

    private InputsResource inputsResource;

    class InputsTestResource extends InputsResource {
        public InputsTestResource(InputService inputService, MessageInputFactory messageInputFactory) {
            super(inputService, messageInputFactory);
        }

        @Override
        protected Subject getSubject() {
            return currentSubject;
        }
    }

    @Test
    public void testMaskingOfPasswordFields() {
        final ConfigurationField fooInput = Mockito.mock(ConfigurationField.class);
        final TextField passwordInput = Mockito.mock(TextField.class);
        Mockito.when(fooInput.getName()).thenReturn("foo");
        Mockito.when(passwordInput.getName()).thenReturn("password");
        Mockito.when(passwordInput.getAttributes()).thenReturn(ImmutableList.of(IS_PASSWORD.toString().toLowerCase(Locale.ENGLISH)));
        final ConfigurationRequest configurationRequest = ConfigurationRequest.createWithFields(fooInput, passwordInput);
        final Map<String, Object> configuration = ImmutableMap.of("foo", 42, "password", "verysecret");
        final Map<String, Object> resultingAttributes = this.inputsResource.maskPasswordsInConfiguration(configuration, configurationRequest);
        assertThat(resultingAttributes).hasSize(2);
        assertThat(resultingAttributes).containsEntry("password", "<password set>");
        assertThat(resultingAttributes).containsEntry("foo", 42);
    }

    @Test
    public void testMaskingOfNonPasswordFields() {
        final TextField passwordInput = Mockito.mock(TextField.class);
        Mockito.when(passwordInput.getName()).thenReturn("nopassword");
        Mockito.when(passwordInput.getAttributes()).thenReturn(ImmutableList.of());
        final ConfigurationRequest configurationRequest = ConfigurationRequest.createWithFields(passwordInput);
        final Map<String, Object> configuration = ImmutableMap.of("nopassword", "lasers in space");
        final Map<String, Object> resultingAttributes = this.inputsResource.maskPasswordsInConfiguration(configuration, configurationRequest);
        assertThat(resultingAttributes).hasSize(1);
        assertThat(resultingAttributes).containsEntry("nopassword", "lasers in space");
    }

    @Test
    public void testMaskingOfFieldWithoutType() {
        final ConfigurationRequest configurationRequest = ConfigurationRequest.createWithFields();
        final Map<String, Object> configuration = ImmutableMap.of("nopassword", "lasers in space");
        final Map<String, Object> resultingAttributes = this.inputsResource.maskPasswordsInConfiguration(configuration, configurationRequest);
        assertThat(resultingAttributes).hasSize(1);
        assertThat(resultingAttributes).containsEntry("nopassword", "lasers in space");
    }

    @Test
    public void testMaskingOfEmptyMap() {
        final ConfigurationRequest configurationRequest = ConfigurationRequest.createWithFields();
        final Map<String, Object> configuration = Collections.emptyMap();
        final Map<String, Object> resultingAttributes = this.inputsResource.maskPasswordsInConfiguration(configuration, configurationRequest);
        assertThat(resultingAttributes).isEmpty();
    }

    @Test
    public void testMaskingOfNullValueInMap() {
        final TextField passwordInput = Mockito.mock(TextField.class);
        Mockito.when(passwordInput.getName()).thenReturn("nopassword");
        Mockito.when(passwordInput.getAttributes()).thenReturn(ImmutableList.of());
        final ConfigurationRequest configurationRequest = ConfigurationRequest.createWithFields(passwordInput);
        final Map<String, Object> configuration = Collections.singletonMap("nopassword", null);
        final Map<String, Object> resultingAttributes = this.inputsResource.maskPasswordsInConfiguration(configuration, configurationRequest);
        assertThat(resultingAttributes).hasSize(1);
        assertThat(resultingAttributes).containsEntry("nopassword", null);
    }

    @Test
    public void testRetrievalOfInputWithPasswordFieldIfUserIsNotAllowedToEditInput() throws NotFoundException {
        final String inputId = "myinput";
        final String inputType = "dummyinput";
        final Input input = getInput(inputId, inputType);
        Mockito.when(inputService.find(inputId)).thenReturn(input);
        final ConfigurationField fooInput = Mockito.mock(ConfigurationField.class);
        Mockito.when(fooInput.getName()).thenReturn("foo");
        final TextField passwordInput = getPasswordField("password");
        final ConfigurationRequest configurationRequest = ConfigurationRequest.createWithFields(fooInput, passwordInput);
        final InputDescription inputDescription = getInputDescription(configurationRequest);
        this.availableInputs.put(inputType, inputDescription);
        Mockito.when(currentSubject.isPermitted((((RestPermissions.INPUTS_READ) + ":") + inputId))).thenReturn(true);
        Mockito.when(currentSubject.isPermitted((((RestPermissions.INPUTS_EDIT) + ":") + inputId))).thenReturn(false);
        final Map<String, Object> configuration = ImmutableMap.of("foo", 42, "password", "verysecret");
        Mockito.when(input.getConfiguration()).thenReturn(configuration);
        final InputSummary summary = this.inputsResource.get(inputId);
        assertThat(summary.attributes()).hasSize(2);
        assertThat(summary.attributes()).containsEntry("password", "<password set>");
        assertThat(summary.attributes()).containsEntry("foo", 42);
    }

    @Test
    public void testRetrievalOfInputWithPasswordFieldIfUserIsAllowedToEditInput() throws NotFoundException {
        final String inputId = "myinput";
        final String inputType = "dummyinput";
        final Input input = getInput(inputId, inputType);
        Mockito.when(inputService.find(inputId)).thenReturn(input);
        final ConfigurationField fooInput = Mockito.mock(ConfigurationField.class);
        Mockito.when(fooInput.getName()).thenReturn("foo");
        final TextField passwordInput = getPasswordField("password");
        final ConfigurationRequest configurationRequest = ConfigurationRequest.createWithFields(fooInput, passwordInput);
        final InputDescription inputDescription = getInputDescription(configurationRequest);
        this.availableInputs.put(inputType, inputDescription);
        Mockito.when(currentSubject.isPermitted((((RestPermissions.INPUTS_READ) + ":") + inputId))).thenReturn(true);
        Mockito.when(currentSubject.isPermitted((((RestPermissions.INPUTS_EDIT) + ":") + inputId))).thenReturn(true);
        final Map<String, Object> configuration = ImmutableMap.of("foo", 42, "password", "verysecret");
        Mockito.when(input.getConfiguration()).thenReturn(configuration);
        final InputSummary summary = this.inputsResource.get(inputId);
        assertThat(summary.attributes()).hasSize(2);
        assertThat(summary.attributes()).containsEntry("password", "verysecret");
        assertThat(summary.attributes()).containsEntry("foo", 42);
    }

    @Test
    public void testRetrievalOfAllInputsWithPasswordFieldForUserNotAllowedToEditInput() throws NotFoundException {
        final String inputId = "myinput";
        final String inputType = "dummyinput";
        final Input input = getInput(inputId, inputType);
        final ConfigurationField fooInput = Mockito.mock(ConfigurationField.class);
        Mockito.when(fooInput.getName()).thenReturn("foo");
        final TextField passwordInput = getPasswordField("password");
        final ConfigurationRequest configurationRequest = ConfigurationRequest.createWithFields(fooInput, passwordInput);
        final InputDescription inputDescription = getInputDescription(configurationRequest);
        this.availableInputs.put(inputType, inputDescription);
        Mockito.when(currentSubject.isPermitted((((RestPermissions.INPUTS_READ) + ":") + inputId))).thenReturn(true);
        Mockito.when(currentSubject.isPermitted((((RestPermissions.INPUTS_EDIT) + ":") + inputId))).thenReturn(false);
        final Map<String, Object> configuration = ImmutableMap.of("foo", 42, "password", "verysecret");
        Mockito.when(input.getConfiguration()).thenReturn(configuration);
        Mockito.when(inputService.all()).thenReturn(Collections.singletonList(input));
        final InputsList inputsList = this.inputsResource.list();
        assertThat(inputsList.inputs()).isNotEmpty();
        assertThat(inputsList.inputs()).allMatch(( summary) -> {
            assertThat(summary.attributes()).hasSize(2);
            assertThat(summary.attributes()).containsEntry("password", "<password set>");
            assertThat(summary.attributes()).containsEntry("foo", 42);
            return true;
        });
    }

    @Test
    public void testRetrievalOfAllInputsWithPasswordFieldForUserAllowedToEditInput() throws NotFoundException {
        final String inputId = "myinput";
        final String inputType = "dummyinput";
        final Input input = getInput(inputId, inputType);
        final ConfigurationField fooInput = Mockito.mock(ConfigurationField.class);
        Mockito.when(fooInput.getName()).thenReturn("foo");
        final TextField passwordInput = getPasswordField("password");
        final ConfigurationRequest configurationRequest = ConfigurationRequest.createWithFields(fooInput, passwordInput);
        final InputDescription inputDescription = getInputDescription(configurationRequest);
        this.availableInputs.put(inputType, inputDescription);
        Mockito.when(currentSubject.isPermitted((((RestPermissions.INPUTS_READ) + ":") + inputId))).thenReturn(true);
        Mockito.when(currentSubject.isPermitted((((RestPermissions.INPUTS_EDIT) + ":") + inputId))).thenReturn(true);
        final Map<String, Object> configuration = ImmutableMap.of("foo", 42, "password", "verysecret");
        Mockito.when(input.getConfiguration()).thenReturn(configuration);
        Mockito.when(inputService.all()).thenReturn(Collections.singletonList(input));
        final InputsList inputsList = this.inputsResource.list();
        assertThat(inputsList.inputs()).isNotEmpty();
        assertThat(inputsList.inputs()).allMatch(( summary) -> {
            assertThat(summary.attributes()).hasSize(2);
            assertThat(summary.attributes()).containsEntry("password", "verysecret");
            assertThat(summary.attributes()).containsEntry("foo", 42);
            return true;
        });
    }
}

