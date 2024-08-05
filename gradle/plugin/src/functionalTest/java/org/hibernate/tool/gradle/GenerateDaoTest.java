package org.hibernate.tool.gradle;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.testkit.runner.BuildResult;
import org.hibernate.tool.gradle.test.func.utils.FuncTestConstants;
import org.hibernate.tool.gradle.test.func.utils.FuncTestTemplate;
import org.junit.jupiter.api.Test;

class GenerateDaoTest extends FuncTestTemplate implements FuncTestConstants {

    private static final String BUILD_FILE_HIBERNATE_TOOLS_SECTION = 
            "hibernateTools {\n" +
            "  packageName = 'foo.model'\n" +
            "}\n";

	@Override
	public String getBuildFileHibernateToolsSection() {
	    return BUILD_FILE_HIBERNATE_TOOLS_SECTION;
	}

    @Test 
    void testGenerateJava() throws IOException {
    	performTask("generateDao", true);
    }
    
    @Override
    protected void verifyBuild(BuildResult buildResult) {
    	try {
	        Path generatedSourcesFolder = projectDir.resolve("generated-sources");
	        assertTrue(buildResult.getOutput().contains(
	        		"Starting DAO export to directory: " + generatedSourcesFolder.toRealPath()));
	        assertTrue(Files.exists(generatedSourcesFolder));
	        assertTrue(Files.isDirectory(generatedSourcesFolder));
			Path fooFile = generatedSourcesFolder.resolve("foo/model/FooHome.java");
	        assertTrue(Files.exists(fooFile));
	        assertTrue(Files.isRegularFile(fooFile));
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }
    
  }
