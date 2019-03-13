/**
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */
package bisq.desktop.common.support;


import bisq.desktop.common.view.AbstractView;
import bisq.desktop.common.view.ViewLoader;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


public class CachingViewLoaderTests {
    @Test
    public void test() {
        ViewLoader delegateViewLoader = Mockito.mock(ViewLoader.class);
        ViewLoader cachingViewLoader = new bisq.desktop.common.view.CachingViewLoader(delegateViewLoader);
        cachingViewLoader.load(CachingViewLoaderTests.TestView1.class);
        cachingViewLoader.load(CachingViewLoaderTests.TestView1.class);
        cachingViewLoader.load(CachingViewLoaderTests.TestView2.class);
        BDDMockito.then(delegateViewLoader).should(Mockito.times(1)).load(CachingViewLoaderTests.TestView1.class);
        BDDMockito.then(delegateViewLoader).should(Mockito.times(1)).load(CachingViewLoaderTests.TestView2.class);
        BDDMockito.then(delegateViewLoader).should(Mockito.times(0)).load(CachingViewLoaderTests.TestView3.class);
    }

    static class TestView1 extends AbstractView {}

    static class TestView2 extends AbstractView {}

    static class TestView3 extends AbstractView {}
}

