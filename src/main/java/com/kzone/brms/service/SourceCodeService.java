package com.kzone.brms.service;

import com.kzone.brms.dto.request.DomainObject;
import com.kzone.brms.model.RuleSet;

public interface SourceCodeService {

    String createSourceCode(RuleSet ruleSet, DomainObject domainObject);

}
