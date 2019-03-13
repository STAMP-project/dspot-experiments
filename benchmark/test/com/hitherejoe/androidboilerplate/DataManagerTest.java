package com.hitherejoe.androidboilerplate;


import com.hitherejoe.androidboilerplate.data.DataManager;
import com.hitherejoe.androidboilerplate.data.local.PreferencesHelper;
import com.hitherejoe.androidboilerplate.data.remote.AndroidBoilerplateService;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;
import rx.observers.TestSubscriber;


@RunWith(MockitoJUnitRunner.class)
public class DataManagerTest {
    @Mock
    AndroidBoilerplateService mMockAndroidBoilerplateService;

    @Mock
    PreferencesHelper mMockPreferencesHelper;

    DataManager mDataManager;

    @Test
    public void shouldLoadCharacters() throws Exception {
        int[] ids = new int[]{ 10034, 14050, 10435, 35093 };
        List<Character> characters = MockModelFabric.makeListOfMockCharacters(4);
        for (int i = 0; i < (ids.length); i++) {
            Mockito.when(mMockAndroidBoilerplateService.getCharacter(ids[i])).thenReturn(Observable.just(characters.get(i)));
        }
        TestSubscriber<List<Character>> result = new TestSubscriber();
        mDataManager.getCharacters(ids).subscribe(result);
        result.assertNoErrors();
        result.assertValue(characters);
    }
}

