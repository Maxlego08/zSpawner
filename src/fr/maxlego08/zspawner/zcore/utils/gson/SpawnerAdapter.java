package fr.maxlego08.zspawner.zcore.utils.gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import fr.maxlego08.zspawner.SpawnerObject;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.zcore.ZPlugin;

public class SpawnerAdapter extends TypeAdapter<Spawner> {

	private static Type seriType = new TypeToken<Map<String, Object>>() {
	}.getType();

	private final String UUIDNAME = "uuid";
	private final String UUIDOWNER = "uuid";
	private final String LOCATION = "location";
	private final String TYPE = "type";

	@Override
	public Spawner read(JsonReader jsonReader) throws IOException {
		if (jsonReader.peek() == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		}
		return fromRaw(jsonReader.nextString());
	}

	@Override
	public void write(JsonWriter jsonWriter, Spawner playerSpawner) throws IOException {
		if (playerSpawner == null) {
			jsonWriter.nullValue();
			return;
		}
		jsonWriter.value(getRaw(playerSpawner));
	}

	/**
	 * 
	 * @param playerSpawner
	 * @return
	 */
	private String getRaw(Spawner spawner) {
		Map<String, Object> serial = new HashMap<String, Object>();
		serial.put(UUIDNAME, spawner.getUniqueId());
		serial.put(UUIDOWNER, spawner.getOwner());
		serial.put(LOCATION, spawner.getLocation());
		serial.put(TYPE, spawner.getType().name());
		return ZPlugin.z().getGson().toJson(serial);
	}

	/**
	 * 
	 * @param raw
	 * @return
	 */
	private Spawner fromRaw(String raw) {
		Map<String, Object> keys = ZPlugin.z().getGson().fromJson(raw, seriType);
		UUID uuid = UUID.fromString((String) keys.get(UUIDNAME));
		UUID owner = UUID.fromString((String) keys.get(UUIDOWNER));
		Location location = (Location) keys.get(LOCATION);
		EntityType type = EntityType.valueOf((String) keys.get(TYPE));
		return new SpawnerObject(uuid, type, owner, location);
	}

}
