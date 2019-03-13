/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.domain.materials;


import com.thoughtworks.go.config.materials.mercurial.HgMaterialConfig;
import com.thoughtworks.go.domain.MaterialInstance;
import com.thoughtworks.go.domain.materials.mercurial.StringRevision;
import com.thoughtworks.go.domain.materials.packagematerial.PackageMaterialRevision;
import com.thoughtworks.go.domain.materials.scm.PluggableSCMMaterialRevision;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.helper.ModificationsMother;
import com.thoughtworks.go.util.json.JsonHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ModificationsTest {
    @Test
    public void shouldReturnUnknownForEmptyList() {
        Assert.assertThat(new Modifications().getUsername(), Matchers.is("Unknown"));
    }

    @Test
    public void shouldReturnFirstUsername() {
        Modification modification1 = new Modification("username1", "", null, new Date(), "1");
        Modification modification2 = new Modification("username2", "", null, new Date(), "2");
        Assert.assertThat(getUsername(), Matchers.is("username1"));
    }

    @Test
    public void shouldReturnUnknownRevisionForEmptyList() {
        Assert.assertThat(new Modifications().getRevision(), Matchers.is("Unknown"));
    }

    @Test
    public void shouldReturnFirstRevision() {
        Modification modification1 = new Modification(new Date(), "cruise/1.0/dev/1", "MOCK_LABEL-12", null);
        Modification modification2 = new Modification(new Date(), "cruise/1.0/dev/2", "MOCK_LABEL-12", null);
        Assert.assertThat(getRevision(), Matchers.is("cruise/1.0/dev/1"));
    }

    @Test
    public void shouldReturnRevisionsWithoutSpecifiedRevision() {
        final Modification modification1 = new Modification(new Date(), "1", "MOCK_LABEL-12", null);
        final Modification modification2 = new Modification(new Date(), "2", "MOCK_LABEL-12", null);
        List<Modification> modifications = new ArrayList<>();
        modifications.add(modification1);
        modifications.add(modification2);
        List<Modification> filtered = Modifications.filterOutRevision(modifications, new StringRevision("1"));
        Assert.assertThat(filtered.size(), Matchers.is(1));
        Assert.assertThat(filtered.get(0), Matchers.is(modification2));
    }

    @Test
    public void hasModifcationShouldReturnCorrectResults() {
        Modifications modifications = modificationWithIds();
        Assert.assertThat(modifications.hasModfication(3), Matchers.is(true));
        Assert.assertThat(modifications.hasModfication(2), Matchers.is(true));
        Assert.assertThat(modifications.hasModfication(5), Matchers.is(false));
        Assert.assertThat(modifications.hasModfication(0), Matchers.is(false));
    }

    @Test
    public void hasModifcationResults() {
        Modifications modifications = modificationWithIds();
        Assert.assertThat(modifications.since(3), Matchers.is(new Modifications(modifcation(4))));
        Assert.assertThat(modifications.since(2), Matchers.is(new Modifications(modifcation(4), modifcation(3))));
        try {
            modifications.since(10);
            Assert.fail("should throw exception");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is(("Could not find modification 10 in " + modifications)));
        }
        try {
            modifications.since(6);
            Assert.fail("should throw exception");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is(("Could not find modification 6 in " + modifications)));
        }
    }

    @Test
    public void shouldUnderstandIfContainsModificationWithSameRevision() {
        MaterialInstance materialInstance = MaterialsMother.hgMaterial().createMaterialInstance();
        final Modification modification = modificationWith(materialInstance, "1");
        final Modification sameModification = modificationWith(materialInstance, "1");
        final Modification modificationWithDifferentRev = modificationWith(materialInstance, "2");
        final Modification modificationWithDifferentMaterial = modificationWith(MaterialsMother.hgMaterial("http://foo.com").createMaterialInstance(), "1");
        Modifications modifications = new Modifications(modification);
        Assert.assertThat(modifications.containsRevisionFor(modification), Matchers.is(true));
        Assert.assertThat(modifications.containsRevisionFor(sameModification), Matchers.is(true));
        Assert.assertThat(modifications.containsRevisionFor(modificationWithDifferentRev), Matchers.is(false));
        Assert.assertThat(modifications.containsRevisionFor(modificationWithDifferentMaterial), Matchers.is(true));// note that its checking for revision and not material instance

    }

    @Test
    public void shouldGetLatestModificationsForPackageMaterial() {
        Date timestamp = new Date();
        String revisionString = "123";
        HashMap<String, String> data = new HashMap<>();
        data.put("1", "one");
        data.put("2", "two");
        Modification modification = new Modification(null, null, null, timestamp, revisionString, JsonHelper.toJsonString(data));
        Modifications modifications = new Modifications(modification);
        Revision revision = modifications.latestRevision(new PackageMaterial());
        Assert.assertThat((revision instanceof PackageMaterialRevision), Matchers.is(true));
        PackageMaterialRevision packageMaterialRevision = ((PackageMaterialRevision) (revision));
        Assert.assertThat(packageMaterialRevision.getRevision(), Matchers.is(revisionString));
        Assert.assertThat(packageMaterialRevision.getTimestamp(), Matchers.is(timestamp));
        Assert.assertThat(packageMaterialRevision.getData().size(), Matchers.is(data.size()));
        Assert.assertThat(packageMaterialRevision.getData().get("1"), Matchers.is(data.get("1")));
        Assert.assertThat(packageMaterialRevision.getData().get("2"), Matchers.is(data.get("2")));
    }

    @Test
    public void shouldGetLatestModificationsForPluggableSCMMaterial() {
        String revisionString = "123";
        Date timestamp = new Date();
        HashMap<String, String> data = new HashMap<>();
        data.put("1", "one");
        data.put("2", "two");
        Modification modification = new Modification(null, null, null, timestamp, revisionString, JsonHelper.toJsonString(data));
        Modifications modifications = new Modifications(modification);
        Revision revision = modifications.latestRevision(new PluggableSCMMaterial());
        Assert.assertThat((revision instanceof PluggableSCMMaterialRevision), Matchers.is(true));
        PluggableSCMMaterialRevision pluggableSCMMaterialRevision = ((PluggableSCMMaterialRevision) (revision));
        Assert.assertThat(pluggableSCMMaterialRevision.getRevision(), Matchers.is(revisionString));
        Assert.assertThat(pluggableSCMMaterialRevision.getTimestamp(), Matchers.is(timestamp));
        Assert.assertThat(pluggableSCMMaterialRevision.getData().size(), Matchers.is(data.size()));
        Assert.assertThat(pluggableSCMMaterialRevision.getData().get("1"), Matchers.is(data.get("1")));
        Assert.assertThat(pluggableSCMMaterialRevision.getData().get("2"), Matchers.is(data.get("2")));
    }

    @Test
    public void shouldNeverIgnorePackageMaterialModifications() {
        PackageMaterialConfig packageMaterialConfig = new PackageMaterialConfig();
        Filter filter = packageMaterialConfig.filter();
        MatcherAssert.assertThat(filter, Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(new Modifications().shouldBeIgnoredByFilterIn(packageMaterialConfig), Matchers.is(false));
    }

    @Test
    public void shouldIncludeModificationsIfAnyFileIsNotIgnored() {
        HgMaterialConfig materialConfig = MaterialConfigsMother.hgMaterialConfig();
        Filter filter = new Filter(Arrays.asList(new IgnoredFiles("*.doc"), new IgnoredFiles("*.pdf")));
        materialConfig.setFilter(filter);
        Modifications modifications = new Modifications(ModificationsMother.multipleCheckin(ModificationsMother.aCheckIn("100", "a.doc", "a.pdf", "a.java")));
        Assert.assertThat(modifications.shouldBeIgnoredByFilterIn(materialConfig), Matchers.is(false));
    }

    @Test
    public void shouldIncludeModificationsIfAnyFileIsNotIgnored1() {
        HgMaterialConfig materialConfig = MaterialConfigsMother.hgMaterialConfig();
        Filter filter = new Filter(Arrays.asList(new IgnoredFiles("*.doc"), new IgnoredFiles("*.pdf")));
        materialConfig.setFilter(filter);
        Modifications modifications = new Modifications(ModificationsMother.multipleCheckin(ModificationsMother.aCheckIn("100", "a.doc", "a.pdf"), ModificationsMother.aCheckIn("100", "a.java")));
        Assert.assertThat(modifications.shouldBeIgnoredByFilterIn(materialConfig), Matchers.is(false));
    }

    @Test
    public void shouldIgnoreModificationsIfAllTheIgnoresMatch() {
        HgMaterialConfig materialConfig = MaterialConfigsMother.hgMaterialConfig();
        Filter filter = new Filter(Arrays.asList(new IgnoredFiles("*.doc"), new IgnoredFiles("*.pdf")));
        materialConfig.setFilter(filter);
        Assert.assertThat(new Modifications(ModificationsMother.multipleCheckin(ModificationsMother.aCheckIn("100", "a.doc", "a.pdf"))).shouldBeIgnoredByFilterIn(materialConfig), Matchers.is(true));
        Assert.assertThat(new Modifications(ModificationsMother.multipleCheckin(ModificationsMother.aCheckIn("100", "a.doc", "a.doc"))).shouldBeIgnoredByFilterIn(materialConfig), Matchers.is(true));
        Assert.assertThat(new Modifications(ModificationsMother.multipleCheckin(ModificationsMother.aCheckIn("100", "a.pdf", "b.pdf"), ModificationsMother.aCheckIn("100", "a.doc", "b.doc"))).shouldBeIgnoredByFilterIn(materialConfig), Matchers.is(true));
    }

    @Test
    public void shouldIgnoreModificationsIfInvertFilterAndEmptyIgnoreList() {
        HgMaterialConfig materialConfig = MaterialConfigsMother.hgMaterialConfig();
        Filter filter = new Filter();
        materialConfig.setFilter(filter);
        materialConfig.setInvertFilter(true);
        Modifications modifications = new Modifications(ModificationsMother.multipleCheckin(ModificationsMother.aCheckIn("100", "a.doc", "a.pdf", "a.java")));
        Assert.assertThat(modifications.shouldBeIgnoredByFilterIn(materialConfig), Matchers.is(true));
    }

    @Test
    public void shouldIgnoreModificationsIfWildcardBlacklist() {
        HgMaterialConfig materialConfig = MaterialConfigsMother.hgMaterialConfig();
        Filter filter = new Filter(Arrays.asList(new IgnoredFiles("**/*")));
        materialConfig.setFilter(filter);
        Modifications modifications = new Modifications(ModificationsMother.multipleCheckin(ModificationsMother.aCheckIn("100", "a.doc", "a.pdf", "a.java")));
        Assert.assertThat(modifications.shouldBeIgnoredByFilterIn(materialConfig), Matchers.is(true));
    }

    @Test
    public void shouldIncludeModificationsIfInvertFilterAndWildcardBlacklist() {
        HgMaterialConfig materialConfig = MaterialConfigsMother.hgMaterialConfig();
        Filter filter = new Filter(Arrays.asList(new IgnoredFiles("**/*")));
        materialConfig.setFilter(filter);
        materialConfig.setInvertFilter(true);
        Modifications modifications = new Modifications(ModificationsMother.multipleCheckin(ModificationsMother.aCheckIn("100", "a.doc", "a.pdf", "a.java")));
        Assert.assertThat(modifications.shouldBeIgnoredByFilterIn(materialConfig), Matchers.is(false));
    }

    @Test
    public void shouldIgnoreModificationsIfInvertFilterAndSpecificFileNotChanged() {
        HgMaterialConfig materialConfig = MaterialConfigsMother.hgMaterialConfig();
        Filter filter = new Filter(Arrays.asList(new IgnoredFiles("*.foo")));
        materialConfig.setFilter(filter);
        materialConfig.setInvertFilter(true);
        Modifications modifications = new Modifications(ModificationsMother.multipleCheckin(ModificationsMother.aCheckIn("100", "a.doc", "a.pdf", "a.java")));
        Assert.assertThat(modifications.shouldBeIgnoredByFilterIn(materialConfig), Matchers.is(true));
    }

    @Test
    public void shouldIgnoreModificationsIfInvertFilterAndSpecificFileNotChanged2() {
        HgMaterialConfig materialConfig = MaterialConfigsMother.hgMaterialConfig();
        Filter filter = new Filter(Arrays.asList(new IgnoredFiles("foo/bar.baz")));
        materialConfig.setFilter(filter);
        materialConfig.setInvertFilter(true);
        Modifications modifications = new Modifications(ModificationsMother.multipleCheckin(ModificationsMother.aCheckIn("100", "a.java", "foo", "bar.baz", "foo/bar.qux")));
        Assert.assertThat(modifications.shouldBeIgnoredByFilterIn(materialConfig), Matchers.is(true));
    }

    @Test
    public void shouldIncludeModificationsIfInvertFilterAndSpecificIsChanged() {
        HgMaterialConfig materialConfig = MaterialConfigsMother.hgMaterialConfig();
        Filter filter = new Filter(Arrays.asList(new IgnoredFiles("foo/bar.baz")));
        materialConfig.setFilter(filter);
        materialConfig.setInvertFilter(true);
        Modifications modifications = new Modifications(ModificationsMother.multipleCheckin(ModificationsMother.aCheckIn("101", "foo/bar.baz")));
        Assert.assertThat(modifications.shouldBeIgnoredByFilterIn(materialConfig), Matchers.is(false));
    }
}

