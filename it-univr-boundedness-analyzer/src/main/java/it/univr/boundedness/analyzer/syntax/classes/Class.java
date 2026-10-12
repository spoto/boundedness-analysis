package it.univr.boundedness.analyzer.syntax.classes;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import it.univr.boundedness.analyzer.syntax.Type;

public class Class implements Type {
	private final String name;
	private final Optional<Class> superclass;
	private final Set<Field> fields = new HashSet<>();
	private final Set<Method> methods = new HashSet<>();
	public final static String NESTING = "  ";

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