package com.kzone.brms.repository;

import com.kzone.brms.model.RuleSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleSetRepository extends JpaRepository<RuleSet,String> {

    boolean existsByName(String name);

}
