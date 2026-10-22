package it.univr.smartcontracts.syntax.classes;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import it.univr.smartcontracts.syntax.Type;

public class Class implements Type {
	private final String name;
	private final Optional<Class> superclass;
	private final java.util.List<Field> fields = new ArrayList<>();
	private final java.util.List<Method> methods = new ArrayList<>();
	public final static String NESTING = "  ";
	public final static Class OBJECT = new Class("Object");
	public final static Class CONTRACT = new Class("Contract", OBJECT);
	public final static Class LIST = new Class("List", OBJECT);

	public Class(String name) {
		this(name, null);
	}

	public Class(String name, Class superclass) {
		this.name = name;
		this.superclass = Optional.ofNullable(superclass);
	}

	public String name() {
		return name;
	}

	public Optional<Class> superclass() {
		return superclass;
	}

	public Stream<Field> fields() {
		return fields.stream();
	}

	public Stream<Method> methods() {
		return methods.stream();
	}
	
	@Override
	public boolean isSubtypeOf(Type other) {
		if (!(other instanceof Class))
			return false;

		Class cursor = this;
		while (true) {
			if (cursor.equals(other))
				return true;

			var maybeSuperclass = cursor.superclass;
			if (maybeSuperclass.isEmpty())
				return false;

			cursor = maybeSuperclass.get();
		}
	}

	@Override
	public boolean hasSharedSubtypeWith(Type other) {
		// this is OK since our minimal language has no interfaces nor multiple class inheritance
		return isSubtypeOf(other) || other.isSubtypeOf(this);
	}

	protected void add(Field field) {
		fields.add(field);
	}

	protected void add(Method method) {
		methods.add(method);
	}

	@Override
	public final String toString() {
		return name;
	}

	@Override
	public int compareTo(Type other) {
		if (other instanceof Class clazz)
			return name.compareTo(clazz.name);
		else
			return getClass().getName().compareTo(other.getClass().getName());
	}

	public final String toCode() {
		var sb = new StringBuilder();
		toCode(sb);
		return sb.toString();
	}

	public void toCode(StringBuilder sb) {
		sb.append("class " + name);
		superclass.ifPresent(sc -> sb.append(" extends " + sc.name));
		sb.append(" {\n");

		fields.stream().forEach(field -> {
			field.toCode(sb, NESTING);
			sb.append("\n");
		});

		methods.stream().forEach(method -> {
			method.toCode(sb, NESTING);
			sb.append("\n");
		});

		sb.append("}");
	}
}