/*******************************************************************************
 * Copyright (c) 2014 OSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jurgen Vinju - Implementation.
 *******************************************************************************/
package org.ossmeter.metricprovider.rascal;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ossmeter.platform.logging.OssmeterLoggerFactory;
import org.rascalmpl.interpreter.StackTrace;

public class Rasctivator implements BundleActivator {
	private static final Logger LOGGER = OssmeterLoggerFactory.getInstance().makeNewLoggerInstance("rascalLogger");
	private static BundleContext context;
	
	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Rasctivator.context = bundleContext;
	}

	public static void logException(Object message, Throwable cause) {
//		LOGGER.log(Priority.ERROR, message, cause);
		System.err.println(message);
		cause.printStackTrace();
	}
	
	public static void printRascalTrace(StackTrace trace) {
		System.err.println("Rascal stack trace:\n" + trace);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Rasctivator.context = null;
	}

	
}
