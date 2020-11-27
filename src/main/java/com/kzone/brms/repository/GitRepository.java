package com.kzone.brms.repository;

import com.kzone.brms.exception.GitTagAlreadyExists;

public interface GitRepository {


    void commitAddPush(String ruleSetName,String message,String tag);

    boolean isGitTagExists(String tag)throws GitTagAlreadyExists;


}
