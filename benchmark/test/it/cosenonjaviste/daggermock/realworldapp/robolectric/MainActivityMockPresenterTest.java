/**
 * Copyright 2016 Fabio Collini.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.cosenonjaviste.daggermock.realworldapp.robolectric;


import R.id.reload;
import R.id.text;
import android.widget.TextView;
import it.cosenonjaviste.daggermock.DaggerMockRule;
import it.cosenonjaviste.daggermock.realworldapp.AppComponent;
import it.cosenonjaviste.daggermock.realworldapp.main.MainActivity;
import it.cosenonjaviste.daggermock.realworldapp.main.MainPresenter;
import it.cosenonjaviste.daggeroverride.BuildConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MainActivityMockPresenterTest {
    @Rule
    public final DaggerMockRule<AppComponent> rule = new RobolectricMockTestRule();

    @Mock
    MainPresenter presenter;

    @Test
    public void testOnCreate() {
        final MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                activity.showText("Hello mocked world");
                return null;
            }
        }).when(presenter).loadData();
        activity.findViewById(reload).performClick();
        TextView textView = ((TextView) (activity.findViewById(text)));
        assertThat(textView.getText()).isEqualTo("Hello mocked world");
    }
}

