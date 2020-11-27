package com.kzone.brms.execution;

public interface Rule<T> {

    <R> R execute(T ruleDto);

}
