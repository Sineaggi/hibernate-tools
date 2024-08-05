package org.hibernate.tool.gradle;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

public class Extension {

	public Property<String> getSqlToRun() {
		return sqlToRun;
	}
	private final Property<String> sqlToRun;

	public RegularFileProperty getHibernateProperties() {
		return hibernateProperties;
	}
	private final RegularFileProperty hibernateProperties;
	public DirectoryProperty getOutputFolder() {
		return outputFolder;
	}
	private final DirectoryProperty outputFolder;

	public Property<String> getPackageName() {
		return packageName;
	}
	private final Property<String> packageName;

	public Property<String> getRevengStrategy() {
		return revengStrategy;
	}
	private final Property<String> revengStrategy;

	public Extension(ProjectLayout layout, ObjectFactory objects) {
		outputFolder = objects.directoryProperty()
				.convention(layout.getProjectDirectory().dir("generated-sources"));
		packageName = objects.property(String.class)
				.convention("");
		sqlToRun = objects.property(String.class)
				.convention("");
		revengStrategy = objects.property(String.class);
		hibernateProperties = objects.fileProperty()
				.convention(layout.getProjectDirectory().file("hibernate.properties"));
	}
	
}
