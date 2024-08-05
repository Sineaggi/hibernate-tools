package org.hibernate.tool.gradle.test.func.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.io.TempDir;

public class FuncTestTemplate implements FuncTestConstants {

	@TempDir
    protected Path projectDir;

	protected Path getBuildFile() {
        return projectDir.resolve(GRADLE_BUILD_FILE_NAME);
    }

	protected Path getSettingsFile() {
        return projectDir.resolve(GRADLE_SETTINGS_FILE_NAME);
    }
    
	protected Path getDatabaseFile() {
    	Path databaseDir = projectDir.resolve(DATABASE_FOLDER_NAME);
        try {
            Files.createDirectories(databaseDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    	return databaseDir.resolve(DATABASE_FILE_NAME);
    }
    
	protected Path getHibernatePropertiesFile() {
    	Path resourcesDir = projectDir.resolve(RESOURCES_FOLDER_PATH);
        try {
            Files.createDirectories(resourcesDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    	return resourcesDir.resolve(getHibernatePropertiesFileName());
    }
	
	protected String getHibernatePropertiesContents() {
        System.out.println("KKEK");

        Properties properties = new Properties();
        properties.put("hibernate.connection.driver_class", "org.h2.Driver");
        properties.put("hibernate.connection.url", projectDir.resolve(DATABASE_PATH).toString());
        properties.put("hibernate.connection.username", "sa");
        properties.put("hibernate.connection.password", "");
        StringWriter sw = new StringWriter();
        try {
            properties.store(sw, null);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return sw.toString();
        //        "hibernate.connection.driver_class=org.h2.Driver\n" +
        //        "hibernate.connection.url=jdbc:h2:" + DATABASE_DIR_PLACEHOLDER + "\n" +
        //        "hibernate.connection.username=sa\n" +
        //        "hibernate.connection.password=\n"
        //        ;

        //System.out.println(HIBERNATE_PROPERTIES_CONTENTS.replace(DATABASE_DIR_PLACEHOLDER, new File(projectDir.getAbsolutePath(), DATABASE_PATH).getAbsolutePath()));
//
        //Properties properties = new Properties();
        //properties.
        //
        //return HIBERNATE_PROPERTIES_CONTENTS.replace(DATABASE_DIR_PLACEHOLDER, new File(projectDir.getAbsolutePath(), DATABASE_PATH).getAbsolutePath());
	}
    
	protected void copyDatabase() {
    	try (InputStream is = getClass().getClassLoader().getResourceAsStream(DATABASE_FILE_NAME)) {
            Objects.requireNonNull(is, DATABASE_FILE_NAME + " not found");
            Files.copy(is, getDatabaseFile());
    	} catch (IOException e) {
    		throw new RuntimeException(e);
    	}
    }
	    
    protected void writeString(Path file, String string) {
        try {
            Files.writeString(file, string);
        } catch (IOException e) {
        	throw new RuntimeException(e);
        }
    }
    
    protected void performTask(String taskName, boolean needDatabase) {
    	prepareBuild(needDatabase);
    	verifyBuild(runBuild(taskName));
    }
    
    protected void prepareBuild(boolean needDatabase) {
        writeString(getSettingsFile(), "");
        writeString(getBuildFile(),getBuildFileContents());
        writeString(getHibernatePropertiesFile(), getHibernatePropertiesContents());
        if (needDatabase) {
        	copyDatabase();
        }
    }
    
    protected BuildResult runBuild(String taskName) {
        GradleRunner runner = GradleRunner.create();
        runner.forwardOutput();
        runner.withPluginClasspath();
        runner.withArguments(taskName, "--stacktrace");
        runner.withProjectDir(projectDir.toFile());
        return runner.build();
    }
    
    protected void verifyBuild(BuildResult buildResult) {}
    
}
