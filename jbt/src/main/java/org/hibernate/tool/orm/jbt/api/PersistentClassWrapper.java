package org.hibernate.tool.orm.jbt.api;

import java.util.Iterator;
import java.util.List;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.internal.factory.PersistentClassWrapperFactory.RootClassWrapperImpl;
import org.hibernate.tool.orm.jbt.util.SpecialRootClass;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public interface PersistentClassWrapper extends Wrapper {

	default PersistentClass getWrappedObject() { return (PersistentClass)this; }
	default boolean isAssignableToRootClass() { return isInstanceOfRootClass(); }
	default boolean isRootClass() { return getWrappedObject().getClass() == RootClassWrapperImpl.class; }
	default boolean isInstanceOfRootClass() { return RootClass.class.isAssignableFrom(getWrappedObject().getClass()); }
	default boolean isInstanceOfSubclass() { return Subclass.class.isAssignableFrom(getWrappedObject().getClass()); }
	default boolean isInstanceOfJoinedSubclass() { return JoinedSubclass.class.isAssignableFrom(getWrappedObject().getClass()); }
	default Property getProperty() { throw new RuntimeException("getProperty() is only allowed on SpecialRootClass"); }
	default void setTable(Table table) { throw new RuntimeException("Method 'setTable(Table)' is not supported."); }
	default void setIdentifier(Value value) { throw new RuntimeException("Method 'setIdentifier(Value)' can only be called on RootClass instances"); }
	default void setKey(Value value) { throw new RuntimeException("setKey(Value) is only allowed on JoinedSubclass"); }
	default boolean isInstanceOfSpecialRootClass() { return SpecialRootClass.class.isAssignableFrom(getWrappedObject().getClass()); }
	default Property getParentProperty() { throw new RuntimeException("getParentProperty() is only allowed on SpecialRootClass"); }
	default void setIdentifierProperty(Property property) { throw new RuntimeException("setIdentifierProperty(Property) is only allowed on RootClass instances"); }
	default void setDiscriminator(Value value) { throw new RuntimeException("Method 'setDiscriminator(Value)' can only be called on RootClass instances"); }
	default boolean isLazyPropertiesCacheable() { throw new RuntimeException("Method 'isLazyPropertiesCacheable()' can only be called on RootClass instances"); }
	default Iterator<Property> getPropertyIterator() { return getProperties().iterator(); }
	default Iterator<Join> getJoinIterator() { return getJoins().iterator(); }
	default Iterator<Subclass> getSubclassIterator() { return getSubclasses().iterator(); }
	default Iterator<Property> getPropertyClosureIterator() { return getPropertyClosure().iterator(); }

	String getEntityName();
	String getClassName();
	Property getIdentifierProperty();
	boolean hasIdentifierProperty();
	PersistentClass getRootClass();
	PersistentClass getSuperclass();
	Property getProperty(String name);
	Table getTable();
	Boolean isAbstract();
	Value getDiscriminator();
	Value getIdentifier();
	Property getVersion();
	void setClassName(String name);
	void setEntityName(String name);
	void setDiscriminatorValue(String str);
	void setAbstract(Boolean b);
	void addProperty(Property p);
	void setProxyInterfaceName(String name);
	void setLazy(boolean b);
	boolean isCustomDeleteCallable();
	boolean isCustomInsertCallable();
	boolean isCustomUpdateCallable();
	boolean isDiscriminatorInsertable();
	boolean isDiscriminatorValueNotNull();
	boolean isDiscriminatorValueNull();
	boolean isExplicitPolymorphism();
	boolean isForceDiscriminator();
	boolean isInherited();
	boolean isJoinedSubclass();
	boolean isLazy();
	boolean isMutable();
	boolean isPolymorphic();
	boolean isVersioned();
	int getBatchSize();
	String getCacheConcurrencyStrategy();
	String getCustomSQLDelete();
	String getCustomSQLInsert();
	String getCustomSQLUpdate();
	String getDiscriminatorValue();
	String getLoaderName();
	int getOptimisticLockMode();
	String getWhere();
	Table getRootTable();
	List<Property> getProperties();
	List<Join> getJoins();
	List<Subclass> getSubclasses();
	List<Property> getPropertyClosure();
	
}
