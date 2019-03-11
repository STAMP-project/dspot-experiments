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
package org.graylog2.security;


import java.util.Collections;
import java.util.Map;
import org.graylog2.plugin.security.PasswordAlgorithm;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class PasswordAlgorithmFactoryTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private PasswordAlgorithm passwordAlgorithm1;

    @Mock
    private PasswordAlgorithm passwordAlgorithm2;

    private Map<String, PasswordAlgorithm> passwordAlgorithms;

    @Test
    public void testForPasswordShouldReturnFirstAlgorithm() throws Exception {
        Mockito.when(passwordAlgorithm1.supports(ArgumentMatchers.anyString())).thenReturn(true);
        final PasswordAlgorithmFactory passwordAlgorithmFactory = new PasswordAlgorithmFactory(passwordAlgorithms, passwordAlgorithm2);
        assertThat(passwordAlgorithmFactory.forPassword("foobar")).isEqualTo(passwordAlgorithm1);
    }

    @Test
    public void testForPasswordShouldReturnSecondAlgorithm() throws Exception {
        Mockito.when(passwordAlgorithm1.supports(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(passwordAlgorithm2.supports(ArgumentMatchers.anyString())).thenReturn(true);
        final PasswordAlgorithmFactory passwordAlgorithmFactory = new PasswordAlgorithmFactory(passwordAlgorithms, passwordAlgorithm2);
        assertThat(passwordAlgorithmFactory.forPassword("foobar")).isEqualTo(passwordAlgorithm2);
    }

    @Test
    public void testForPasswordShouldReturnNull() throws Exception {
        Mockito.when(passwordAlgorithm1.supports(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(passwordAlgorithm2.supports(ArgumentMatchers.anyString())).thenReturn(false);
        final PasswordAlgorithmFactory passwordAlgorithmFactory = new PasswordAlgorithmFactory(passwordAlgorithms, passwordAlgorithm2);
        assertThat(passwordAlgorithmFactory.forPassword("foobar")).isNull();
    }

    @Test
    public void testDefaultPasswordAlgorithm() throws Exception {
        final PasswordAlgorithm defaultPasswordAlgorithm = Mockito.mock(PasswordAlgorithm.class);
        final PasswordAlgorithmFactory passwordAlgorithmFactory = new PasswordAlgorithmFactory(Collections.<String, PasswordAlgorithm>emptyMap(), defaultPasswordAlgorithm);
        assertThat(passwordAlgorithmFactory.defaultPasswordAlgorithm()).isEqualTo(defaultPasswordAlgorithm);
    }
}

