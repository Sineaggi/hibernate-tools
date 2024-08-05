package org.hibernate.tool.gradle.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.tools.ant.BuildException;
import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.ArtifactCollection;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.hibernate.tool.api.metadata.MetadataConstants;
import org.hibernate.tool.api.metadata.MetadataDescriptor;
import org.hibernate.tool.api.metadata.MetadataDescriptorFactory;
import org.hibernate.tool.api.reveng.RevengSettings;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.api.reveng.RevengStrategyFactory;
import org.hibernate.tool.gradle.Extension;

public abstract class AbstractTask extends DefaultTask {

	@Internal
	private final Extension extension;
	
	@Internal
	private Properties hibernateProperties = null;

	@Classpath
	public SetProperty<File> getProjectClasspath() {
		return this.projectClasspath;
	}

	private final SetProperty<File> projectClasspath = getProject().getObjects().setProperty(File.class);

	@InputFile
	private RegularFileProperty getPropertyFileProvider() {
		return this.propertyFileProvider;
	}

	private final RegularFileProperty propertyFileProvider = getProject().getObjects().fileProperty();

	public AbstractTask(Extension extension, ObjectFactory objects) {
		ConfigurationContainer cc = getProject().getConfigurations();
		Configuration defaultConf = cc.getByName("compileClasspath");
		ArtifactCollection ac = defaultConf.getIncoming().getArtifacts();
		projectClasspath.set(ac.getResolvedArtifacts().map(f -> f.stream().map(ResolvedArtifactResult::getFile).collect(Collectors.toSet())));
		propertyFileProvider.set(getProject().getProviders().provider(this::findPropertyFile));

		this.extension = extension;
		this.outputFolderProperty = objects.directoryProperty()
				.convention(extension.getOutputFolder());
		this.packageName = objects.property(String.class)
				.convention(extension.getPackageName());
		this.revengStrategy = objects.property(String.class)
				.convention(extension.getRevengStrategy());
	}

	private RegularFile findPropertyFile() {
		String hibernatePropertiesFile = getExtension().getHibernateProperties().getAsFile().get().getName();
		SourceSetContainer ssc = getProject().getExtensions().getByType(SourceSetContainer.class);
		SourceSet ss = ssc.getByName(SourceSet.MAIN_SOURCE_SET_NAME);
		SourceDirectorySet sds = ss.getResources();
		for (File f : sds.getFiles()) {
			if (hibernatePropertiesFile.equals(f.getName())) {
				return () -> f;
			}
		}
		throw new BuildException("File '" + hibernatePropertiesFile + "' could not be found");
	}
	
	private Extension getExtension() {
		return this.extension;
	}
	
	void perform() {
		getLogger().lifecycle("Starting Task '" + getName() + "'");
		ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(
					new URLClassLoader(
							resolveProjectClassPath(), 
							oldLoader));
			doWork();
		} finally {
			Thread.currentThread().setContextClassLoader(oldLoader);
			getLogger().lifecycle("Ending Task '" + getName() + "'");
		}
	}
	
	URL[] resolveProjectClassPath() {
		try {
			Set<File> ras = projectClasspath.get();
			File[] resolvedArtifacts = ras.toArray(File[]::new);
			URL[] urls = new URL[ras.size()];
			for (int i = 0; i < ras.size(); i++) {
				urls[i] = resolvedArtifacts[i].toURI().toURL();
			}
			return urls;
		} catch (MalformedURLException e) {
			getLogger().error("MalformedURLException while compiling project classpath");
			throw new BuildException(e);
		}
	}
	
	Properties getHibernateProperties() {
		if (hibernateProperties == null) {
			loadPropertiesFile(getPropertyFile());
		}
		return hibernateProperties;
	}
	
	String getHibernateProperty(String name) {
		return getHibernateProperties().getProperty(name);
	}
	
	MetadataDescriptor createJdbcDescriptor() {
		RevengStrategy strategy = setupReverseEngineeringStrategy();
		Properties hibernateProperties = getHibernateProperties();
		hibernateProperties.put(MetadataConstants.PREFER_BASIC_COMPOSITE_IDS, true);
		return MetadataDescriptorFactory.createReverseEngineeringDescriptor(strategy, hibernateProperties);
	}

	@OutputDirectory
	public DirectoryProperty getOutputFolderProperty() {
		return outputFolderProperty;
	}

	private final DirectoryProperty outputFolderProperty;

	@Internal
	File getOutputFolder() {
		return outputFolderProperty.getAsFile().get();
	}

	@Input
	public Property<String> getPackageName() {
		return packageName;
	}
	private final Property<String> packageName;

	@Input
	@Optional
	public Property<String> revengStrategy() {
		return revengStrategy;
	}
	private final Property<String> revengStrategy;
	
	RevengStrategy setupReverseEngineeringStrategy() {
		RevengStrategy result = RevengStrategyFactory
				.createReverseEngineeringStrategy(revengStrategy.getOrNull());
		RevengSettings settings = new RevengSettings(result);
		settings.setDefaultPackageName(packageName.get());
		result.setSettings(settings);
		return result;
	}
	
	private File getPropertyFile() {
		// String hibernatePropertiesFile = getExtension().hibernateProperties;
		// SourceSetContainer ssc = getProject().getExtensions().getByType(SourceSetContainer.class);
		// SourceSet ss = ssc.getByName(SourceSet.MAIN_SOURCE_SET_NAME);
		// SourceDirectorySet sds = ss.getResources();
		// for (File f : sds.getFiles()) {
		// 	if (hibernatePropertiesFile.equals(f.getName())) {
		// 		return f;
		// 	}
		// }
		// throw new BuildException("File '" + hibernatePropertiesFile + "' could not be found");
		return propertyFileProvider.getAsFile().get();
	}

	private void loadPropertiesFile(File propertyFile) {
		getLogger().lifecycle("Loading the properties file : " + propertyFile.getPath());
		try (FileInputStream is = new FileInputStream(propertyFile)) {
			hibernateProperties = new Properties();
			hibernateProperties.load(is);
			getLogger().lifecycle("Properties file is loaded");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new BuildException(propertyFile + " not found.", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildException("Problem while loading " + propertyFile, e);
		}
	}
	
	abstract void doWork();

}
