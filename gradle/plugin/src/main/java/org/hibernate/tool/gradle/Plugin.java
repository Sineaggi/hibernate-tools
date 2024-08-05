package org.hibernate.tool.gradle;

import java.util.Map;

import org.gradle.api.Project;
import org.hibernate.tool.gradle.task.AbstractTask;
import org.hibernate.tool.gradle.task.GenerateCfgTask;
import org.hibernate.tool.gradle.task.GenerateDaoTask;
import org.hibernate.tool.gradle.task.GenerateHbmTask;
import org.hibernate.tool.gradle.task.GenerateJavaTask;
import org.hibernate.tool.gradle.task.RunSqlTask;

public class Plugin implements org.gradle.api.Plugin<Project> {

	private static final Map<String, Class<? extends AbstractTask>> PLUGIN_TASK_MAP = Map.of(
			"runSql", RunSqlTask.class,
			"generateJava", GenerateJavaTask.class,
			"generateCfg", GenerateCfgTask.class,
			"generateHbm", GenerateHbmTask.class,
			"generateDao", GenerateDaoTask.class
		);

	@SuppressWarnings("unchecked")
	public void apply(Project project) {
		Extension extension =  project.getExtensions().create("hibernateTools", Extension.class);
		for (Map.Entry<String, Class<? extends AbstractTask>> entry: PLUGIN_TASK_MAP.entrySet()) {
			String key = entry.getKey();
			Class<? extends AbstractTask> taskClass = entry.getValue();
			project.getTasks().register(key, taskClass, extension);
		}
	}

}
