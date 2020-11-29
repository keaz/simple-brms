package com.kzone.brms.repository.impl;

import com.kzone.brms.config.GitConfigs;
import com.kzone.brms.exception.GenericGitException;
import com.kzone.brms.exception.GitTagAlreadyExists;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.SymbolicRef;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GitRepositoryImplTest {

    @InjectMocks
    @Spy
    private GitRepositoryImpl gitRepository;

    @Mock
    private GitConfigs gitConfigs;
    @Mock
    private CommitCommand commitCommand;
    @Mock
    private TagCommand tagCommand;
    @Mock
    private AddCommand addCommand;
    @Mock
    private PushCommand pushCommand;
    @Mock
    private Git git;
    @Mock
    private ListTagCommand listTagCommand;
    @Mock
    private Ref ref;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    private String message = "Commit Message";
    private String version = "0.0.0";
    private String ruleSetName = "Test";

    @Before
    public void setup() {

    }

    @Test
    public void createCommitTest() throws GitAPIException {
        Mockito.when(git.commit()).thenReturn(commitCommand);
        gitRepository.createCommit(message, git);
        Mockito.verify(commitCommand).setMessage(stringArgumentCaptor.capture());

        Assert.assertEquals(message, stringArgumentCaptor.getValue());

    }

    @Test
    public void createTagTest() throws GitAPIException {
        Mockito.when(git.tag()).thenReturn(tagCommand);
        gitRepository.createTag(version, message, git);

        Mockito.verify(tagCommand).setName(stringArgumentCaptor.capture());
        Mockito.verify(tagCommand).setMessage(stringArgumentCaptor.capture());

        List<String> allValues = stringArgumentCaptor.getAllValues();
        Assert.assertEquals(version, allValues.get(0));
        Assert.assertEquals(message, allValues.get(1));
    }


    @Test(expected = GitAPIException.class)
    public void addAllTest() throws GitAPIException {
        Mockito.when(git.add()).thenReturn(addCommand);
        Mockito.when(addCommand.addFilepattern(".")).thenReturn(addCommand);
        Mockito.doThrow(NoFilepatternException.class).when(addCommand).call();
        gitRepository.addAll(git);
    }

    @Test(expected = GitAPIException.class)
    public void pushTest() throws GitAPIException {
        Mockito.when(gitConfigs.getToken()).thenReturn("");
        Mockito.when(gitConfigs.getUsername()).thenReturn("");
        Mockito.when(git.push()).thenReturn(pushCommand);
        Mockito.when(pushCommand.setPushAll()).thenReturn(pushCommand);
        Mockito.when(pushCommand.setCredentialsProvider(Mockito.any(UsernamePasswordCredentialsProvider.class))).thenReturn(pushCommand);
        Mockito.when(pushCommand.setPushTags()).thenReturn(pushCommand);
        Mockito.doThrow(InvalidRemoteException.class).when(pushCommand).call();
        gitRepository.push(git);
    }

    @Test(expected = GenericGitException.class)
    public void commitAddPushErrorTest() throws GitAPIException {

        Mockito.doReturn(git).when(gitRepository).getGit();
        Mockito.doNothing().when(gitRepository).addAll(git);
        Mockito.doNothing().when(gitRepository).createCommit(message,git);
        Mockito.doThrow(InvalidRemoteException.class).when(gitRepository).push(git);

        gitRepository.commitAddPush(ruleSetName,message);
    }

    @Test
    public void commitAddPushTest() throws GitAPIException {

        Mockito.doReturn(git).when(gitRepository).getGit();
        Mockito.doNothing().when(gitRepository).addAll(git);
        Mockito.doNothing().when(gitRepository).createCommit(message,git);
        Mockito.doNothing().when(gitRepository).push(git);

        gitRepository.commitAddPush(ruleSetName,message);

        Mockito.verify(gitRepository).createCommit(stringArgumentCaptor.capture(),Mockito.any(Git.class));
        Assert.assertEquals(message,stringArgumentCaptor.getValue());
    }

    @Test(expected = GenericGitException.class)
    public void isGitTagExistsErrorTest() throws GitAPIException {
        Mockito.doReturn(git).when(gitRepository).getGit();
        Mockito.doThrow(InvalidRemoteException.class).when(gitRepository).gitTaxExists(version,git);

        gitRepository.isGitTagExists(version);
    }

    @Test(expected = GitTagAlreadyExists.class)
    public void commitAddPushAllArgGitTagAlreadyExistsTest() throws GitAPIException{
        Mockito.doReturn(git).when(gitRepository).getGit();
        Mockito.doReturn(true).when(gitRepository).gitTaxExists(version,git);
        gitRepository.commitAddPush(ruleSetName,message,version);
    }

    @Test(expected = GenericGitException.class)
    public void commitAddPushAllArgGenericGitExceptionTest() throws GitAPIException{
        Mockito.doReturn(git).when(gitRepository).getGit();
        Mockito.doReturn(false).when(gitRepository).gitTaxExists(version,git);
        Mockito.doThrow(InvalidRemoteException.class).when(gitRepository).addAll(git);
        gitRepository.commitAddPush(ruleSetName,message,version);
    }

    @Test
    public void commitAddPushAllArgTest() throws GitAPIException{
        Mockito.doReturn(git).when(gitRepository).getGit();
        Mockito.doReturn(false).when(gitRepository).gitTaxExists(version,git);
        Mockito.doNothing().when(gitRepository).addAll(git);
        Mockito.doNothing().when(gitRepository).createCommit(message,git);
        Mockito.doNothing().when(gitRepository).createTag(version,message,git);
        Mockito.doNothing().when(gitRepository).push(git);

        gitRepository.commitAddPush(ruleSetName,message,version);

        Mockito.verify(gitRepository).createCommit(stringArgumentCaptor.capture(),Mockito.any(Git.class));
        Assert.assertEquals(message,stringArgumentCaptor.getValue());
    }

    @Test
    public void gitTaxExistsTests() throws GitAPIException {
        List<Ref> tags = new ArrayList<>();
        tags.add(ref);
        Mockito.when(ref.getName()).thenReturn("refs/tags/"+version);
        Mockito.doReturn(listTagCommand).when(git).tagList();
        Mockito.doReturn(tags).when(listTagCommand).call();

        boolean actual = gitRepository.gitTaxExists(version, git);
        Assert.assertTrue(actual);
    }

}
