package com.github.dockerjava.cmd;


import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.AuthConfigurations;
import com.github.dockerjava.core.RemoteApiVersion;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.github.dockerjava.core.util.CompressArchiveUtil;
import com.github.dockerjava.junit.DockerMatchers;
import com.github.dockerjava.utils.RegistryUtils;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.jcip.annotations.NotThreadSafe;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Kanstantsin Shautsou
 */
@NotThreadSafe
public class BuildImageCmdIT extends CmdIT {
    public static final Logger LOG = LoggerFactory.getLogger(BuildImageCmd.class);

    @Rule
    public TemporaryFolder folder = new TemporaryFolder(new File("target/"));

    @Test
    public void author() throws Exception {
        String imageId = dockerRule.buildImage(fileFromBuildTestResource("AUTHOR"));
        InspectImageResponse inspectImageResponse = dockerRule.getClient().inspectImageCmd(imageId).exec();
        MatcherAssert.assertThat(inspectImageResponse, Matchers.not(Matchers.nullValue()));
        BuildImageCmdIT.LOG.info("Image Inspect: {}", inspectImageResponse.toString());
        MatcherAssert.assertThat(inspectImageResponse.getAuthor(), Matchers.equalTo("Guillaume J. Charmes \"guillaume@dotcloud.com\""));
    }

    @Test
    public void buildImageFromTar() throws Exception {
        File baseDir = fileFromBuildTestResource("ADD/file");
        Collection<File> files = FileUtils.listFiles(baseDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        File tarFile = CompressArchiveUtil.archiveTARFiles(baseDir, files, UUID.randomUUID().toString());
        String response = dockerfileBuild(new FileInputStream(tarFile));
        MatcherAssert.assertThat(response, Matchers.containsString("Successfully executed testrun.sh"));
    }

    @Test
    public void buildImageFromTarWithDockerfileNotInBaseDirectory() throws Exception {
        File baseDir = fileFromBuildTestResource("dockerfileNotInBaseDirectory");
        Collection<File> files = FileUtils.listFiles(baseDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        File tarFile = CompressArchiveUtil.archiveTARFiles(baseDir, files, UUID.randomUUID().toString());
        String response = dockerfileBuild(new FileInputStream(tarFile), "dockerfileFolder/Dockerfile");
        MatcherAssert.assertThat(response, Matchers.containsString("Successfully executed testrun.sh"));
    }

    @Test
    public void onBuild() throws Exception {
        File baseDir = fileFromBuildTestResource("ONBUILD/parent");
        dockerRule.getClient().buildImageCmd(baseDir).withNoCache(true).withTag("docker-java-onbuild").exec(new BuildImageResultCallback()).awaitImageId();
        baseDir = fileFromBuildTestResource("ONBUILD/child");
        String response = dockerfileBuild(baseDir);
        MatcherAssert.assertThat(response, Matchers.containsString("Successfully executed testrun.sh"));
    }

    @Test
    public void addUrl() throws Exception {
        File baseDir = fileFromBuildTestResource("ADD/url");
        String response = dockerfileBuild(baseDir);
        MatcherAssert.assertThat(response, Matchers.containsString("Example Domain"));
    }

    @Test
    public void addFileInSubfolder() throws Exception {
        File baseDir = fileFromBuildTestResource("ADD/fileInSubfolder");
        String response = dockerfileBuild(baseDir);
        MatcherAssert.assertThat(response, Matchers.containsString("Successfully executed testrun.sh"));
    }

    @Test
    public void addFilesViaWildcard() throws Exception {
        File baseDir = fileFromBuildTestResource("ADD/filesViaWildcard");
        String response = dockerfileBuild(baseDir);
        MatcherAssert.assertThat(response, Matchers.containsString("Successfully executed testinclude1.sh"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Successfully executed testinclude2.sh")));
    }

    @Test
    public void addFolder() throws Exception {
        File baseDir = fileFromBuildTestResource("ADD/folder");
        String response = dockerfileBuild(baseDir);
        MatcherAssert.assertThat(response, Matchers.containsString("Successfully executed testAddFolder.sh"));
    }

    @Test(expected = DockerClientException.class)
    public void dockerignoreDockerfileIgnored() throws Exception {
        File baseDir = fileFromBuildTestResource("dockerignore/DockerfileIgnored");
        dockerRule.getClient().buildImageCmd(baseDir).withNoCache(true).exec(new BuildImageResultCallback()).awaitImageId();
    }

    @Test
    public void dockerignoreDockerfileNotIgnored() throws Exception {
        File baseDir = fileFromBuildTestResource("dockerignore/DockerfileNotIgnored");
        dockerRule.getClient().buildImageCmd(baseDir).withNoCache(true).exec(new BuildImageResultCallback()).awaitImageId();
    }

    @Test(expected = DockerClientException.class)
    public void dockerignoreInvalidDockerIgnorePattern() throws Exception {
        File baseDir = fileFromBuildTestResource("dockerignore/InvalidDockerignorePattern");
        dockerRule.getClient().buildImageCmd(baseDir).withNoCache(true).exec(new BuildImageResultCallback()).awaitImageId();
    }

    @Test
    public void dockerignoreValidDockerIgnorePattern() throws Exception {
        File baseDir = fileFromBuildTestResource("dockerignore/ValidDockerignorePattern");
        String response = dockerfileBuild(baseDir);
        MatcherAssert.assertThat(response, Matchers.containsString("/tmp/a/a /tmp/a/c /tmp/a/d"));
    }

    @Test
    public void env() throws Exception {
        File baseDir = fileFromBuildTestResource("ENV");
        String response = dockerfileBuild(baseDir);
        MatcherAssert.assertThat(response, Matchers.containsString("testENVSubstitution successfully completed"));
    }

    @Test
    public void fromPrivateRegistry() throws Exception {
        AuthConfig authConfig = RegistryUtils.runPrivateRegistry(dockerRule.getClient());
        String imgName = (authConfig.getRegistryAddress()) + "/testuser/busybox";
        File dockerfile = folder.newFile("Dockerfile");
        FileUtils.writeStringToFile(dockerfile, ("FROM " + imgName));
        File baseDir;
        InspectImageResponse inspectImageResponse;
        dockerRule.getClient().authCmd().withAuthConfig(authConfig).exec();
        dockerRule.getClient().tagImageCmd("busybox:latest", imgName, "latest").withForce().exec();
        dockerRule.getClient().pushImageCmd(imgName).withTag("latest").withAuthConfig(authConfig).exec(new PushImageResultCallback()).awaitCompletion(30, TimeUnit.SECONDS);
        dockerRule.getClient().removeImageCmd(imgName).withForce(true).exec();
        // baseDir = fileFromBuildTestResource("FROM/privateRegistry");
        baseDir = folder.getRoot();
        AuthConfigurations authConfigurations = new AuthConfigurations();
        authConfigurations.addConfig(authConfig);
        String imageId = dockerRule.getClient().buildImageCmd(baseDir).withNoCache(true).withBuildAuthConfigs(authConfigurations).exec(new BuildImageResultCallback()).awaitImageId();
        inspectImageResponse = dockerRule.getClient().inspectImageCmd(imageId).exec();
        MatcherAssert.assertThat(inspectImageResponse, Matchers.not(Matchers.nullValue()));
        BuildImageCmdIT.LOG.info("Image Inspect: {}", inspectImageResponse.toString());
    }

    @Test
    public void buildArgs() throws Exception {
        File baseDir = fileFromBuildTestResource("buildArgs");
        String imageId = dockerRule.getClient().buildImageCmd(baseDir).withNoCache(true).withBuildArg("testArg", "abc !@#$%^&*()_+").exec(new BuildImageResultCallback()).awaitImageId();
        InspectImageResponse inspectImageResponse = dockerRule.getClient().inspectImageCmd(imageId).exec();
        MatcherAssert.assertThat(inspectImageResponse, Matchers.not(Matchers.nullValue()));
        BuildImageCmdIT.LOG.info("Image Inspect: {}", inspectImageResponse.toString());
        MatcherAssert.assertThat(inspectImageResponse.getConfig().getLabels().get("test"), Matchers.equalTo("abc !@#$%^&*()_+"));
    }

    @Test
    public void labels() throws Exception {
        Assume.assumeThat("API version should be >= 1.23", dockerRule, DockerMatchers.isGreaterOrEqual(RemoteApiVersion.VERSION_1_23));
        File baseDir = fileFromBuildTestResource("labels");
        String imageId = dockerRule.getClient().buildImageCmd(baseDir).withNoCache(true).withLabels(Collections.singletonMap("test", "abc")).exec(new BuildImageResultCallback()).awaitImageId();
        InspectImageResponse inspectImageResponse = dockerRule.getClient().inspectImageCmd(imageId).exec();
        MatcherAssert.assertThat(inspectImageResponse, Matchers.not(Matchers.nullValue()));
        BuildImageCmdIT.LOG.info("Image Inspect: {}", inspectImageResponse.toString());
        MatcherAssert.assertThat(inspectImageResponse.getConfig().getLabels().get("test"), Matchers.equalTo("abc"));
    }

    @Test
    public void multipleTags() throws Exception {
        Assume.assumeThat("API version should be >= 1.23", dockerRule, DockerMatchers.isGreaterOrEqual(RemoteApiVersion.VERSION_1_21));
        File baseDir = fileFromBuildTestResource("labels");
        String imageId = dockerRule.getClient().buildImageCmd(baseDir).withNoCache(true).withTag("fallback-when-withTags-not-called").withTags(new HashSet(Arrays.asList("docker-java-test:tag1", "docker-java-test:tag2"))).exec(new BuildImageResultCallback()).awaitImageId();
        InspectImageResponse inspectImageResponse = dockerRule.getClient().inspectImageCmd(imageId).exec();
        MatcherAssert.assertThat(inspectImageResponse, Matchers.not(Matchers.nullValue()));
        BuildImageCmdIT.LOG.info("Image Inspect: {}", inspectImageResponse.toString());
        MatcherAssert.assertThat(inspectImageResponse.getRepoTags().size(), Matchers.equalTo(2));
        MatcherAssert.assertThat(inspectImageResponse.getRepoTags(), Matchers.containsInAnyOrder("docker-java-test:tag1", "docker-java-test:tag2"));
    }

    @Test
    public void cacheFrom() throws Exception {
        Assume.assumeThat(dockerRule, DockerMatchers.isGreaterOrEqual(RemoteApiVersion.VERSION_1_27));
        File baseDir1 = fileFromBuildTestResource("CacheFrom/test1");
        String imageId1 = dockerRule.getClient().buildImageCmd(baseDir1).exec(new BuildImageResultCallback()).awaitImageId();
        InspectImageResponse inspectImageResponse1 = dockerRule.getClient().inspectImageCmd(imageId1).exec();
        MatcherAssert.assertThat(inspectImageResponse1, Matchers.not(Matchers.nullValue()));
        File baseDir2 = fileFromBuildTestResource("CacheFrom/test2");
        String imageId2 = dockerRule.getClient().buildImageCmd(baseDir2).withCacheFrom(new HashSet(Arrays.asList(imageId1))).exec(new BuildImageResultCallback()).awaitImageId();
        InspectImageResponse inspectImageResponse2 = dockerRule.getClient().inspectImageCmd(imageId2).exec();
        MatcherAssert.assertThat(inspectImageResponse2, Matchers.not(Matchers.nullValue()));
        // Compare whether the image2's parent layer is from image1 so that cache is used
        MatcherAssert.assertThat(inspectImageResponse2.getParent(), Matchers.equalTo(inspectImageResponse1.getId()));
    }
}

