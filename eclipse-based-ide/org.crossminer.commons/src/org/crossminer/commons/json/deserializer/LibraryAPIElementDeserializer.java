/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*    Zsolt J�nos Szamosv�lgyi
*    Endre Tam�s V�radi
*    Gerg� Balogh
**********************************************************************/
package org.crossminer.commons.json.deserializer;

import java.lang.reflect.Type;

import org.crossminer.commons.libraryapi.LibraryAPIElement;
import org.crossminer.commons.libraryapi.LibraryAPIElementKind;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Provides a specific deserializer class for {@link LibraryAPIElement}
 *
 */
public class LibraryAPIElementDeserializer implements JsonDeserializer<LibraryAPIElement>{

	@Override
	public LibraryAPIElement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException {
		
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		
		JsonElement jsonKind = jsonObject.get("kind");
		LibraryAPIElementKind kind = jsonDeserializationContext.deserialize(jsonKind, LibraryAPIElementKind.class);
				
		return jsonDeserializationContext.deserialize(jsonElement, kind.getType());		
	}
}
