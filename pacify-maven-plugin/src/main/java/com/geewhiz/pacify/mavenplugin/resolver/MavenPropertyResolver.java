package com.geewhiz.pacify.mavenplugin.resolver;

import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.resolver.BasePropertyResolver;

public class MavenPropertyResolver extends BasePropertyResolver {

	Properties properties;
	String encoding;

	public MavenPropertyResolver(Properties properties, String encoding) {
		this.properties = properties;
		this.encoding = encoding;
	}

	public boolean containsProperty(String key) {
		return properties.containsKey(key);
	}

	public String getPropertyValue(String key) {
		if (containsProperty(key)) {
			return properties.getProperty(key);
		}
		throw new IllegalArgumentException("Property [" + key + "] not defined within maven... Aborting!");
	}

	public String getPropertyResolverDescription() {
		return "maven";
	}

	public String getEncoding() {
		return encoding;
	}

	@Override
	public String getBeginToken() {
		return "${";
	}

	@Override
	public String getEndToken() {
		return "}";
	}

	@Override
	public Set<String> getPropertyKeys() {
		Set<String> result = new TreeSet<String>();

		for (Enumeration<Object> enumerator = properties.keys(); enumerator.hasMoreElements();) {
			result.add((String) enumerator.nextElement());
		}

		return result;
	}

	@Override
	public boolean isProtectedProperty(String property) {
		return false;
	}

	@Override
	public LinkedHashSet<Defect> checkForDuplicateEntry() {
		return new LinkedHashSet<Defect>();
	}

}
