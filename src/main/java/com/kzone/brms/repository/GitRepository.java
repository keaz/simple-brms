package com.kzone.brms.repository;

public interface GitRepository {


    void commitAddPush(String ruleSetName, String message, String tag);

    void commitAddPush(String ruleSetName, String message);

    boolean isGitTagExists(String tag);


}
