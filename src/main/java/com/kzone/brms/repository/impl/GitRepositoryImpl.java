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
    public void commitAddPush(String ruleSetName,String message, String version) throws GitTagAlreadyExists{
        File file = new File(SOURCE_DIR, gitConfigs.getDir());

        try {
            Git git = Git.open(file);
            git.pull();
            List<Ref> tags = git.tagList().call();
            boolean hasTag = tags.stream().anyMatch(ref -> ref.getName().contains(version));
            if(hasTag){
                log.info("Git Tag {} already exists",version);
                throw new GitTagAlreadyExists(String.format("Git tag %s already exists ",version));
            }
            AddCommand add = git.add();
            add.addFilepattern(".").call();
            CommitCommand commit = git.commit();
            commit.setAuthor("brms service","kasun.ranasingh@icloud.com");
            commit.setMessage(message);
            commit.setSign(false);
            commit.call();
            TagCommand tag = git.tag();
            tag.setName(version);
            tag.setMessage(message);
            tag.setSigned(false);
            tag.call();
            PushCommand push = git.push();
            push.setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitConfigs.getUsername(), gitConfigs.getToken()));
            push.setPushAll().setPushTags().call();

        } catch (IOException e) {
            log.error("Failed to open git repo");
            throw new GitTagAlreadyExists(e.getMessage());
        } catch (GitAPIException e) {
            log.error("Error searching git tags");
            throw new GenericGitException(e);
        }
    }

    @Override
    public boolean isGitTagExists(String tag) throws GitTagAlreadyExists {
        File file = new File(SOURCE_DIR, gitConfigs.getDir());
        try {
            Git git = Git.open(file);
            git.pull();
            List<Ref> tags = git.tagList().call();
            return tags.stream().anyMatch(ref -> ref.getName().contentEquals("refs/tags/"+tag));
        } catch (IOException e) {
            log.error("Failed to open git repo");
            throw new GenericGitException(e);
        } catch (GitAPIException e) {
            log.error("Error searching git tags");
            throw new GenericGitException(e);
        }
    }
}
