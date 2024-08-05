package org.hibernate.tool.gradle.task;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.hibernate.tool.gradle.Extension;

import javax.inject.Inject;

public class RunSqlTask extends AbstractTask {

	@Inject
	public RunSqlTask(Extension extension) {
		super(extension);
		sqlToRun = getProject().getObjects().property(String.class)
				.convention(extension.getSqlToRun());
	}

	@Input
	public Property<String> getSqlToRun() {
		return sqlToRun;
	}
	private final Property<String> sqlToRun;

	@TaskAction
	public void performTask() {
		super.perform();
	}
	
	void doWork() {
		registerDriver();
		runSql();
	}
	
	private void registerDriver() {
		String driverClassName = getHibernateProperty("hibernate.connection.driver_class");
		getLogger().lifecycle("Registering the database driver: " + driverClassName);
		try {
			Class<?> driverClass = Thread.currentThread().getContextClassLoader().loadClass(driverClassName);
			Constructor<?> constructor = driverClass.getDeclaredConstructor();
			DriverManager.registerDriver(createDelegatingDriver((Driver)constructor.newInstance()));
			getLogger().lifecycle("Database driver is registered");
		} catch (Exception e) {
			getLogger().error("Exception while registering the database driver: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	private void runSql() {
		String databaseUrl = getHibernateProperty("hibernate.connection.url");
		getLogger().lifecycle("Connecting to database: " + databaseUrl);
		try (Connection connection = DriverManager
				.getConnection(databaseUrl, "sa", "")) {
			try (Statement statement = connection.createStatement()) {
				String sqlToRun = this.sqlToRun.get();
				getLogger().lifecycle("Running SQL: " + sqlToRun);
				statement.execute(sqlToRun);
			}
		} catch (SQLException e) {
			getLogger().error("SQLException");
			throw new RuntimeException(e);
		}
	}
		
	private Driver createDelegatingDriver(Driver driver) {
		return (Driver)Proxy.newProxyInstance(
				DriverManager.class.getClassLoader(), 
				new Class[] { Driver.class}, 
				new InvocationHandler() {					
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return method.invoke(driver, args);
					}
				});
	}
	
}
