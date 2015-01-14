/*******************************************************************************
 * Copyright (c) 2014 OSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Yannis Korkontzelos - Implementation.
 *******************************************************************************/
package org.ossmeter.platform.communicationchannel.cache.provider;

import java.util.Iterator;

import org.ossmeter.platform.communicationchannel.cache.CacheProvider;
import org.ossmeter.repository.model.CommunicationChannel;

public abstract class BasicCacheProvider<T, K> extends CacheProvider<T, K> {
	public abstract Iterator<T> getItems(CommunicationChannel communicationChannel) throws Exception;
}
