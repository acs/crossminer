package org.ossmeter.metricprovider.rascal;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.osgi.framework.Bundle;

/**
 * This code is taken from
 * http://wiki.eclipse.org/BundleProxyClassLoader_recipe
 */
class BundleClassLoader extends ClassLoader {
	private Bundle bundle;
	private ClassLoader parent;

	public BundleClassLoader(Bundle bundle) {
		this.bundle = bundle;
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		return bundle.getResources(name);
	}

	@Override
	public URL findResource(String name) {
		return bundle.getResource(name);
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		return bundle.loadClass(name);
	}

	@Override
	public URL getResource(String name) {
		return (parent == null) ? findResource(name) : super
				.getResource(name);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		Class<?> clazz = (parent == null) ? findClass(name) : super
				.loadClass(name, false);
		if (resolve)
			super.resolveClass(clazz);

		return clazz;
	}
}