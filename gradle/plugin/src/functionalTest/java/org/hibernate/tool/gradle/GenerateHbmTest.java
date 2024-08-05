package org.hibernate.tool.gradle;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.testkit.runner.BuildResult;
import org.hibernate.tool.gradle.test.func.utils.FuncTestConstants;
import org.hibernate.tool.gradle.test.func.utils.FuncTestTemplate;
import org.junit.jupiter.api.Test;

class GenerateHbmTest extends FuncTestTemplate implements FuncTestConstants {

    private static final String BUILD_FILE_HIBERNATE_TOOLS_SECTION = 
            "hibernateTools {\n" +
            "  packageName = 'foo.model'\n" +
            "}\n";

	@Override
	public String getBuildFileHibernateToolsSection() {
	    return BUILD_FILE_HIBERNATE_TOOLS_SECTION;
	}

    @Test 
    void testGenerateHbm() throws IOException {
    	performTask("generateHbm", true);
    }
    
    @Override
    protected void verifyBuild(BuildResult buildResult) {
    	try {
	        Path generatedSourcesFolder = projectDir.resolve("generated-sources");
	        assertTrue(buildResult.getOutput().contains(
	        		"Starting HBM export to directory: " + generatedSourcesFolder.toRealPath()));
	        assertTrue(Files.exists(generatedSourcesFolder));
	        assertTrue(Files.isDirectory(generatedSourcesFolder));
			Path fooFile = generatedSourcesFolder.resolve("foo/model/Foo.hbm.xml");
	        assertTrue(Files.exists(fooFile));
	        assertTrue(Files.isRegularFile(fooFile));
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }
    
  }
