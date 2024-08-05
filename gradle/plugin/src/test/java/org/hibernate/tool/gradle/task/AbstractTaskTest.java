package org.hibernate.tool.gradle.task;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;
import java.net.URL;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.hibernate.tool.api.reveng.RevengStrategy;
import org.hibernate.tool.gradle.Extension;
import org.hibernate.tool.internal.reveng.strategy.AbstractStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class AbstractTaskTest {
	
	private static ClassLoader USED_CLASS_LOADER;
	private static final URL[] URLS = new URL[] {};
	
	private AbstractTask abstractTask = null;
	
	private Field extensionField = null;
	private Extension extension = null;
	
	@BeforeEach
	void beforeEach() throws Exception {
		USED_CLASS_LOADER = null;
		Project project = ProjectBuilder.builder().build();
		project.getPlugins().apply("java");
		extension = new Extension(project.getLayout(), project.getObjects());
		abstractTask = project.getTasks().create("foo", FooTask.class, extension);
		extensionField = AbstractTask.class.getDeclaredField("extension");
		extensionField.setAccessible(true);
	}
	
	@Test
	void testInitialize() throws Exception {
		//assertNull(extensionField.get(abstractTask));
		// abstractTask.initialize(extension);
		//assertSame(extension, extensionField.get(abstractTask));
	}
	
	@Test
	void testGetExtension() throws Exception {
		//assertNull(abstractTask.getExtension());
		//extensionField.set(abstractTask, extension);
		//assertSame(extension, abstractTask.getExtension());
	}
	
	@Test
	void testPerform() {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		assertNull(USED_CLASS_LOADER);
		abstractTask.perform();
		assertNotNull(USED_CLASS_LOADER);
		assertNotSame(contextClassLoader, USED_CLASS_LOADER);
	}
	
	@Test
	void testResolveProjectClassPath() {
		assertSame(URLS, abstractTask.resolveProjectClassPath());
	}
	
	@Test
	public void testSetupReverseEngineeringStrategy() throws Exception {
		extension.getRevengStrategy().set(FooStrategy.class.getName());
		extensionField.set(abstractTask, extension);
		RevengStrategy revengStrategy = abstractTask.setupReverseEngineeringStrategy();
		assertInstanceOf(FooStrategy.class, revengStrategy);
	}
	
	public static class FooStrategy extends AbstractStrategy {}
	
	public static class FooTask extends AbstractTask {
		@Inject
		public FooTask(Extension extension) {
			super(extension);
		}
		void doWork() {
			USED_CLASS_LOADER = Thread.currentThread().getContextClassLoader();
		}
		URL[] resolveProjectClassPath() {
			return URLS;
		}
	}

}
