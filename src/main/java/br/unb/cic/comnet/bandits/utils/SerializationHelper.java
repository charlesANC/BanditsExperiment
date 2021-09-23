package br.unb.cic.comnet.bandits.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SerializationHelper {
	
	public static String serialize(Object object) {
		return new Gson().toJson(object);
	}
	
	public static <T> T unserialize(String obj, TypeToken<T> type) {
		return new Gson().fromJson(obj, type.getType());		
	}
}
