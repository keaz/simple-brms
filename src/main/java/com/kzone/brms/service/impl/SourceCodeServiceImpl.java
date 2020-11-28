package com.kzone.brms.service.impl;

import com.kzone.brms.dto.request.CreateDomainAttributeRequest;
import com.kzone.brms.dto.request.DomainObject;
import com.kzone.brms.model.RuleSet;
import com.kzone.brms.service.SourceCodeService;
import lombok.extern.log4j.Log4j2;
import org.ainslec.picocog.PicoWriter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class SourceCodeServiceImpl implements SourceCodeService {

    @Override
    public String createSourceCode(RuleSet ruleSet, DomainObject domainObject) {
        PicoWriter topWriter = new PicoWriter();

        createImports(topWriter, ruleSet.getPackageName());
        createClassDefinition(topWriter, domainObject.getName());

        PicoWriter attributeWriter = topWriter.createDeferredWriter();
        PicoWriter methodWriter = topWriter.createDeferredWriter();
        List<CreateDomainAttributeRequest> attributes = domainObject.getAttributes();

        for (CreateDomainAttributeRequest attribute : attributes) {

            String name = attribute.getName();
            String type = attribute.getDataType();

            createAttributes(attributeWriter, name, type);
            createSetter(methodWriter, name, type);

            methodWriter.writeln("");

            createGetter(methodWriter, name, type);
        }

        closeClass(topWriter);
        String sourceCode = topWriter.toString(0);
        log.debug("Generated source code {}",sourceCode);
        return sourceCode;
    }

    private void createImports(PicoWriter topWriter, String packageName) {
        topWriter.writeln("package " + packageName + ";");
        topWriter.writeln("");
    }

    private void createClassDefinition(PicoWriter topWriter, String className) {
        topWriter.writeln_r("public class " + className + " {");
        topWriter.writeln("");
    }

    private void createAttributes(PicoWriter attributeWriter, String name, String type) {
        attributeWriter.writeln("private " + type + " " + name + ";");
    }

    private void createSetter(PicoWriter methodWriter, String name, String type) {
        methodWriter.writeln("");
        methodWriter.writeln_r("public void set" + name.substring(0, 1).toUpperCase() + name.substring(1) + "(" + type + " " + name + "){");
        methodWriter.writeln("this." + name + " = " + name + ";");
        methodWriter.writeln_l("}");
    }

    private void createGetter(PicoWriter methodWriter, String name, String type) {
        methodWriter.writeln_r("public " + type + " get" + name.substring(0, 1).toUpperCase() + name.substring(1) + "(){");
        methodWriter.writeln("return this." + name + ";");
        methodWriter.writeln_l("}");
    }

    protected void closeClass(PicoWriter topWriter) {
        topWriter.writeln("");
        topWriter.writeln_l("}");
    }

}
