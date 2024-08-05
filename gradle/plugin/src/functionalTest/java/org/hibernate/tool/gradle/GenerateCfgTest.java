package org.hibernate.tool.gradle;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.testkit.runner.BuildResult;
import org.hibernate.tool.gradle.test.func.utils.FuncTestConstants;
import org.hibernate.tool.gradle.test.func.utils.FuncTestTemplate;
import org.junit.jupiter.api.Test;

class GenerateCfgTest extends FuncTestTemplate implements FuncTestConstants {

    @Test 
    void testGenerateCfg() throws IOException {
    	performTask("generateCfg", true);
    }
    
    @Override
    public void verifyBuild(BuildResult buildResult) {
    	try {
	        Path generatedSourcesFolder = projectDir.resolve("generated-sources");
	        assertTrue(buildResult.getOutput().contains("Starting CFG export to directory: " + generatedSourcesFolder.toRealPath()));
	        assertTrue(Files.exists(generatedSourcesFolder));
	        assertTrue(Files.isDirectory(generatedSourcesFolder));
	        Path cfgFile = generatedSourcesFolder.resolve("hibernate.cfg.xml");
	        assertTrue(Files.exists(cfgFile));
	        assertTrue(Files.isRegularFile(cfgFile));
	        String cfgContents = Files.readString(cfgFile);
	        assertTrue(cfgContents.contains("<mapping resource=\"Foo.hbm.xml\"/>"));
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }

 }
