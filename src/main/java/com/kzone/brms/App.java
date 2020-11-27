package com.kzone.brms;

import com.kzone.brms.config.GitConfigs;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
@RequiredArgsConstructor
@SpringBootApplication
@Log4j2
public class App implements ApplicationListener<ApplicationReadyEvent> {


    private static final String SOURCE_DIR = "source";
    private static final String CLASS_DIR = "compiled";
    private static final File ROOT = new File(SOURCE_DIR);
    private static final File CLASS = new File(CLASS_DIR);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);

//        File ruleSet1 = new File(ROOT, "ruleSet1");
//        File ruleSet2 = new File(ROOT, "ruleSet2");
//        File ruleSet1ClassPath = createClassDirectory("ruleSet1");
//        File ruleSet2ClassPath = createClassDirectory("ruleSet2");
//        compileRuleSets(ruleSet1);
//        compileRuleSets(ruleSet2);
//
//        ClassLoader callPool1 = loadClasses(ruleSet1ClassPath);
//        ClassLoader callPool2 = loadClasses(ruleSet2ClassPath);
//
//        Class<?> aClass = callPool1.loadClass("com.demo.brms.Test");
//        Object o1 = aClass.newInstance();
//        Method doSome1 = aClass.getDeclaredMethod("doSome");
//        Object invoke1 = doSome1.invoke(o1);
//        System.out.println("Result 1 :: " + invoke1);
//
//        Class<?> aClass2 = callPool2.loadClass("com.demo.brms.Test");
//        Object o2 = aClass2.newInstance();
//        Method doSome2 = aClass2.getDeclaredMethod("doSome");
//        Object invoke2 = doSome2.invoke(o2);
//        System.out.println("Result 2 :: " + invoke2);

    }

    public static ClassLoader loadClasses(File ruleSet) {
        try {
            return URLClassLoader.newInstance(new URL[]{ruleSet.toURI().toURL()});
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File createClassDirectory(String ruleSetName) {
        File ruleSetDir = new File(CLASS, ruleSetName);
        if (!ruleSetDir.exists()) {
            ruleSetDir.mkdirs();
        }

        return ruleSetDir;
    }

    public static void compileRuleSets(File ruleSet) {
        List<File> javSourceCodes = new ArrayList<>();
        javaSourceFiles(ruleSet, javSourceCodes);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        List<String> compilerArguments = javSourceCodes.stream().map(File::getPath).collect(Collectors.toList());
        compilerArguments.add(0, "-d");
        compilerArguments.add(1, CLASS_DIR + File.separator + ruleSet.getName());

        int exitCode = compiler.run(null, null, null, compilerArguments.toArray(new String[0]));
        if (exitCode != 0) {
            throw new RuntimeException("Failed to compile source files");
        }
    }

    public static void javaSourceFiles(File directory, List<File> files) {

        // Get all java files from a directory.
        File[] fList = directory.listFiles();
        if (fList != null) {
            for (File file : fList) {
                if (file.isFile() && file.getName().endsWith(".java")) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    javaSourceFiles(file, files);
                }
            }
        }
    }

    public static void javaClassFiles(File directory, List<File> files) {
        // Get all class files from a directory.
        File[] fList = directory.listFiles();
        if (fList != null) {
            for (File file : fList) {
                if (file.isFile() && file.getName().endsWith(".class")) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    javaClassFiles(file, files);
                }
            }
        }
    }

    private static void loadAllClasses(List<File> files, ClassPool aDefault, String ruleSetDir) {
        files.stream().map(file -> file.getPath())
                .map(path -> path.replace(SOURCE_DIR + "/", ""))
                .map(path -> path.replace('/', '.'))
                .map(path -> path.replace(ruleSetDir + ".", "").replace(".class", ""))
                .forEach(classPath -> {
                    try {
                        aDefault.get(classPath).toClass();
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    } catch (CannotCompileException e) {
                        e.printStackTrace();
                    }
                });

    }

    @Autowired
    private final GitConfigs gitConfigs;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        File file = new File(SOURCE_DIR, gitConfigs.getDir());
        try {
            if (file.exists()) {
                log.debug("Repo exists locally, pulling the code");
                Git.open(file).pull();
            } else {
                log.debug("Repo not exists locally, cloning the code");
                Git.cloneRepository()
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitConfigs.getUsername(), gitConfigs.getToken()))
                        .setURI(gitConfigs.getUrl()).setDirectory(file).call();
            }
        } catch (GitAPIException | IOException e) {
            log.error("Failed to close/pull repo ", e);
        }
        log.debug("Successfully cloned/pulled the repo {} ", gitConfigs.getUrl());

    }
}
