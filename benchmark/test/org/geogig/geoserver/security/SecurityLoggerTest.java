/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geogig.geoserver.security;


import Ref.HEADS_PREFIX;
import Ref.REMOTES_PREFIX;
import org.geogig.geoserver.config.LogStore;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.geogig.model.Ref;
import org.locationtech.geogig.plumbing.RefParse;
import org.locationtech.geogig.porcelain.BlameOp;
import org.locationtech.geogig.porcelain.CleanOp;
import org.locationtech.geogig.porcelain.DiffOp;
import org.locationtech.geogig.remotes.CloneOp;
import org.locationtech.geogig.remotes.FetchOp;
import org.locationtech.geogig.remotes.PullOp;
import org.locationtech.geogig.remotes.PushOp;
import org.locationtech.geogig.remotes.RemoteAddOp;
import org.locationtech.geogig.remotes.RemoteRemoveOp;
import org.locationtech.geogig.repository.Remote;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SecurityLoggerTest {
    @SuppressWarnings("unused")
    private SecurityLogger logger;

    private LogStore mockStore;

    @Test
    public void testCommandsOfInterest() {
        Assert.assertTrue(SecurityLogger.interestedIn(RemoteAddOp.class));
        Assert.assertTrue(SecurityLogger.interestedIn(RemoteRemoveOp.class));
        Assert.assertTrue(SecurityLogger.interestedIn(PushOp.class));
        Assert.assertTrue(SecurityLogger.interestedIn(PullOp.class));
        Assert.assertTrue(SecurityLogger.interestedIn(FetchOp.class));
        Assert.assertTrue(SecurityLogger.interestedIn(CloneOp.class));
        Assert.assertFalse(SecurityLogger.interestedIn(BlameOp.class));
        Assert.assertFalse(SecurityLogger.interestedIn(CleanOp.class));
        Assert.assertFalse(SecurityLogger.interestedIn(DiffOp.class));
        Assert.assertFalse(SecurityLogger.interestedIn(RefParse.class));
    }

    @Test
    public void testRemoteAdd() {
        RemoteAddOp command = new RemoteAddOp();
        String remoteName = "upstream";
        String mappedBranch = "master";
        String username = "gabriel";
        String password = "passw0rd";
        String fetchurl = "http://demo.example.com/testrepo";
        String pushurl = fetchurl;
        String fetch = (("+" + (Ref.append(HEADS_PREFIX, mappedBranch))) + ":") + (Ref.append(Ref.append(REMOTES_PREFIX, remoteName), mappedBranch));
        boolean mapped = true;
        command.setName(remoteName).setBranch(mappedBranch).setMapped(mapped).setPassword(password).setURL(username).setURL(fetchurl);
        ArgumentCaptor<CharSequence> arg = ArgumentCaptor.forClass(CharSequence.class);
        SecurityLogger.logPre(command);
        Mockito.verify(mockStore).debug(ArgumentMatchers.anyString(), arg.capture());
        // Remote add: Parameters: name='upstream', url='http://demo.example.com/testrepo'
        String msg = String.valueOf(arg.getValue());
        Assert.assertTrue(msg.startsWith("Remote add"));
        Assert.assertTrue(msg.contains(remoteName));
        Assert.assertTrue(msg.contains(fetchurl));
        Remote retVal = new Remote(remoteName, fetchurl, pushurl, fetch, mapped, mappedBranch, username, password);
        SecurityLogger.logPost(command, retVal, null);
        Mockito.verify(mockStore).info(ArgumentMatchers.anyString(), arg.capture());
        msg = String.valueOf(arg.getValue());
        Assert.assertTrue(msg.startsWith("Remote add success"));
        Assert.assertTrue(msg.contains(remoteName));
        Assert.assertTrue(msg.contains(fetchurl));
        ArgumentCaptor<Throwable> exception = ArgumentCaptor.forClass(Throwable.class);
        SecurityLogger.logPost(command, null, new RuntimeException("test exception"));
        Mockito.verify(mockStore).error(ArgumentMatchers.anyString(), arg.capture(), exception.capture());
        msg = String.valueOf(arg.getValue());
        Assert.assertTrue(msg.startsWith("Remote add failed"));
        Assert.assertTrue(msg.contains(remoteName));
        Assert.assertTrue(msg.contains(fetchurl));
    }
}

