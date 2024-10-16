package dev.zabi94.timetracker.gui;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import dev.zabi94.timetracker.db.DBSerializable;

public class ReloadHandler {
	
	private static List<Subscription> subscriptions = Collections.synchronizedList(new LinkedList<>());

	public static void subscribeType(Class<? extends DBSerializable> type, Consumer<DBSerializable> onChange, Object subscriber) {
		subscriptions.add(new AllSubscription(type, onChange, subscriber));
	}
	
	public static void subscribeObject(DBSerializable object, Consumer<DBSerializable> onChange, Object subscriber) {
		subscriptions.add(new SingleSubscription(object, onChange, subscriber));
	}
	
	public static void subscribeObjectAndChildren(DBSerializable object, Consumer<DBSerializable> onChange, Object subscriber) {
		subscriptions.add(new SingleSubscription(object, onChange, subscriber));
		object.getChildren().stream()
			.map(child -> new SingleSubscription(child, onChange, subscriber))
			.forEach(subscriptions::add);
	}
	
	public static void markChanged(DBSerializable obj) {
		System.gc();
		List<Subscription> shadow = List.copyOf(subscriptions);
		for (Subscription s: shadow) {
			if (s.shouldTrim()) {
				subscriptions.remove(s);
				System.out.println("LIST TRIM");
			} else {
				s.dispatch(obj);
			}
		}
	}
	
	private static class AllSubscription extends Subscription {
		
		private final Class<? extends DBSerializable> classType;
		
		public AllSubscription(Class<? extends DBSerializable> type, Consumer<DBSerializable> onChange, Object subscriber) {
			super(onChange, subscriber);
			this.classType = type;
		}

		@Override
		protected boolean isRelevant(DBSerializable element) {
			return element.getClass().isAssignableFrom(classType);
		}
		
	}
	
	private static class SingleSubscription extends Subscription {
		
		private final DBSerializable object;
		
		public SingleSubscription(DBSerializable object, Consumer<DBSerializable> onChange, Object subscriber) {
			super(onChange, subscriber);
			this.object = object;
		}

		@Override
		protected boolean isRelevant(DBSerializable element) {
			return element.equals(object);
		}
		
	}
	
	private static abstract class Subscription {
		
		private final Consumer<DBSerializable> onChange;
		private final WeakReference<Object> subscriber;
		
		public Subscription(Consumer<DBSerializable> onChange, Object subscriber) {
			this.onChange = onChange;
			this.subscriber = new WeakReference<Object>(subscriber);
		}
		
		protected abstract boolean isRelevant(DBSerializable obj);
		
		public void dispatch(DBSerializable obj) {
			if (isRelevant(obj)) onChange.accept(obj);
		}
		
		public boolean shouldTrim() {
			return subscriber.get() == null;
		}
	}
	
}
