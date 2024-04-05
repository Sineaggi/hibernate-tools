package org.hibernate.tool.orm.jbt.internal.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;

import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Value;
import org.hibernate.tool.orm.jbt.api.PersistentClassWrapper;
import org.hibernate.tool.orm.jbt.util.DummyMetadataBuildingContext;
import org.hibernate.tool.orm.jbt.util.SpecialRootClass;
import org.hibernate.tool.orm.jbt.wrp.PropertyWrapperFactory;
import org.hibernate.tool.orm.jbt.wrp.PropertyWrapperFactory.PropertyWrapper;
import org.hibernate.tool.orm.jbt.wrp.ValueWrapperFactory;
import org.hibernate.tool.orm.jbt.wrp.Wrapper;

public class PersistentClassWrapperFactory {
	
	public static PersistentClassWrapper createRootClassWrapper() {
		return new RootClassWrapperImpl();
	}
	
	public static PersistentClassWrapper createSingleTableSubclassWrapper(PersistentClassWrapper superClassWrapper) {
		return new SingleTableSubclassWrapperImpl(superClassWrapper.getWrappedObject());
	}
	
	public static PersistentClassWrapper createJoinedSubclassWrapper(PersistentClassWrapper superClassWrapper) {
		return new JoinedSubclassWrapperImpl(superClassWrapper.getWrappedObject());
	}
	
	public static PersistentClassWrapper createSpecialRootClassWrapper(PropertyWrapper property) {
		return new SpecialRootClassWrapperImpl(property.getWrappedObject());
	}
	
	public static PersistentClassWrapper createPersistentClassWrapper(PersistentClassWrapper persistentClassWrapper) {
		return (PersistentClassWrapper)Proxy.newProxyInstance(
				PersistentClassWrapperFactory.class.getClassLoader(), 
				new Class[] { PersistentClassWrapper.class }, 
				new PersistentClassWrapperInvocationHandler(
						persistentClassWrapper));
	}
	
	static class PersistentClassWrapperInvocationHandler implements InvocationHandler {
		
		private PersistentClassWrapper wrapper = null;

		public PersistentClassWrapperInvocationHandler(PersistentClassWrapper wrapper) {
			this.wrapper = wrapper;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			try {
				return method.invoke(wrapper, args);
			} catch (InvocationTargetException t) {
				throw t.getTargetException();
			}
		}	
		
	}
	
	public static class RootClassWrapperImpl extends RootClass implements PersistentClassWrapper {		
		public RootClassWrapperImpl() {
			super(DummyMetadataBuildingContext.INSTANCE);
		}
		@Override
		public Value getDiscriminator() {
			return wrapValueIfNeeded(super.getDiscriminator());
		}
		@Override
		public KeyValue getIdentifier() {
			return (KeyValue)wrapValueIfNeeded(super.getIdentifier());
		}
		@Override
		public void setIdentifier(Value v) {
			if (v instanceof Wrapper) {
				v = (Value)((Wrapper)v).getWrappedObject();
			} 
			super.setIdentifier(((KeyValue)v));
		}
	}
	
	public static class SingleTableSubclassWrapperImpl 
			extends SingleTableSubclass 
			implements PersistentClassWrapper {
		public SingleTableSubclassWrapperImpl(PersistentClass superclass) {
			super(superclass, DummyMetadataBuildingContext.INSTANCE);
		}
	}
	
	public static class JoinedSubclassWrapperImpl
			extends JoinedSubclass
			implements PersistentClassWrapper {
		public JoinedSubclassWrapperImpl(PersistentClass superclass) {
			super(superclass, DummyMetadataBuildingContext.INSTANCE);
		}
		@Override
		public void setKey(Value v) {
			if (v instanceof Wrapper) {
				v = (Value)((Wrapper)v).getWrappedObject();
			} 
			super.setKey(((KeyValue)v));
		}
	}
	
	public static class SpecialRootClassWrapperImpl 
			extends SpecialRootClass
			implements PersistentClassWrapper {
		public SpecialRootClassWrapperImpl(Property property) {
			super(property);
		}
		@Override
		public Value getDiscriminator() {
			return wrapValueIfNeeded(super.getDiscriminator());
		}
		@Override
		public KeyValue getIdentifier() {
			return (KeyValue)wrapValueIfNeeded(super.getIdentifier());
		}
		@Override
		public void setIdentifier(Value v) {
			if (v instanceof Wrapper) {
				v = (Value)((Wrapper)v).getWrappedObject();
			} 
			super.setIdentifier(((KeyValue)v));
		}
		@Override
		public Property getProperty() {
			return wrapPropertyIfNeeded(super.getProperty());
		}
		@Override
		public Property getParentProperty() {
			return wrapPropertyIfNeeded(super.getParentProperty());
		}
		@Override 
		public Iterator<Property> getPropertyIterator() {
			final Iterator<Property> iterator = getProperties().iterator();
			return new Iterator<Property>() {
				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}
				@Override
				public Property next() {
					return wrapPropertyIfNeeded(iterator.next());
				}
				
			};
		}
		@Override 
		public Iterator<Property> getPropertyClosureIterator() {
			final Iterator<Property> iterator = getPropertyClosure().iterator();
			return new Iterator<Property>() {
				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}
				@Override
				public Property next() {
					return wrapPropertyIfNeeded(iterator.next());
				}
				
			};
		}

	}
	
	private static Value wrapValueIfNeeded(Value v) {
		return (v != null) && !(v instanceof Wrapper) ? ValueWrapperFactory.createValueWrapper(v) : v;
	}
	
	private static Property wrapPropertyIfNeeded(Property p) {
		return (p != null) && !(p instanceof Wrapper) ? PropertyWrapperFactory.createPropertyWrapper(p) : p;
	}
	
}
