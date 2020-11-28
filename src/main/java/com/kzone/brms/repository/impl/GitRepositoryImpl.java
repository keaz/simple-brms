package com.kzone.brms.repository.impl;

import com.kzone.brms.config.GitConfigs;
import com.kzone.brms.exception.GenericGitException;
import com.kzone.brms.exception.GitTagAlreadyExists;
import com.kzone.brms.repository.GitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.kzone.brms.repository.FileRepository.SOURCE_DIR;

@RequiredArgsConstructor
@Log4j2
@Repository
public class GitRepositoryImpl implements GitRepository {

    @Autowired
    private final GitConfigs gitConfigs;

    @Override
    public void commitAddPush(String ruleSetName,String message, String version){
        try {
            Git git = getGit();
            if(gitTaxExists(version,git)){
                log.info("Git Tag {} already exists",version);
                throw new GitTagAlreadyExists(String.format("Git tag %s already exists ",version));
            }
            addAll(git);
            createCommit(message,git);
            createTag(version,message,git);
            push(git);

        } catch (GitAPIException e) {
            log.error("Error searching git tags");
            throw new GenericGitException(e);
        }
    }

    private void addAll(Git git) throws GitAPIException {
        AddCommand add = git.add();
        add.addFilepattern(".").call();
    }

    private Git getGit()  {
        File file = new File(SOURCE_DIR, gitConfigs.getDir());
        try {
            Git git = Git.open(file);
            log.debug("Pulling repo in to {}",gitConfigs.getDir());
            git.pull();
            return git;
        } catch (IOException e) {
            log.error("Failed to open the git repo",e);
            throw new GenericGitException("Failed to open the git repo");
        }

    }

    @Override
    public void commitAddPush(String ruleSetName, String message) {
        try {
            Git git = getGit();
            addAll(git);
            createCommit(message,git);
            push(git);
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    private void push(Git git) throws GitAPIException {
        PushCommand push = git.push();
        push.setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitConfigs.getUsername(), gitConfigs.getToken()));
        push.setPushAll().setPushTags().call();
    }

    private void createCommit(String message,Git git) throws GitAPIException {
        CommitCommand commit = git.commit();
        commit.setAuthor("brms service","kasun.ranasingh@icloud.com");
        commit.setMessage(message);
        commit.setSign(false);
        commit.call();
    }

    private void createTag(String version,String message,Git git) throws GitAPIException {
        TagCommand tag = git.tag();
        tag.setName(version);
        tag.setMessage(message);
        tag.setSigned(false);
        tag.call();
    }

    @Override
    public boolean isGitTagExists(String tag)  {
        try {
            Git git = getGit();
            return gitTaxExists(tag, git);
        } catch (GitAPIException e) {
            log.error("Error searching git tags");
            throw new GenericGitException(e);
        }
    }

    private boolean gitTaxExists(String tag, Git git) throws GitAPIException {
        List<Ref> tags = git.tagList().call();
        return tags.stream().anyMatch(ref -> ref.getName().contentEquals("refs/tags/"+ tag));
    }


}
