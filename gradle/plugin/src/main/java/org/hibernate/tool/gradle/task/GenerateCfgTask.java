package org.hibernate.tool.gradle.task;

import java.io.File;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.TaskAction;
import org.hibernate.tool.api.export.Exporter;
import org.hibernate.tool.api.export.ExporterConstants;
import org.hibernate.tool.api.export.ExporterFactory;
import org.hibernate.tool.api.export.ExporterType;
import org.hibernate.tool.gradle.Extension;

import javax.inject.Inject;

public class GenerateCfgTask extends AbstractTask {

	@Inject
	public GenerateCfgTask(Extension extension, ObjectFactory objects) {
		super(extension, objects);
	}

	@TaskAction
	public void performTask() {
		super.perform();
	}

	void doWork() {
		getLogger().lifecycle("Creating CFG exporter");
		Exporter cfgExporter = ExporterFactory.createExporter(ExporterType.CFG);
		File outputFolder = getOutputFolder();
		cfgExporter.getProperties().put(ExporterConstants.METADATA_DESCRIPTOR, createJdbcDescriptor());
		cfgExporter.getProperties().put(ExporterConstants.DESTINATION_FOLDER, outputFolder);
		getLogger().lifecycle("Starting CFG export to directory: " + outputFolder + "...");
		cfgExporter.start();
		getLogger().lifecycle("CFG export finished");
	}

}
