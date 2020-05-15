package fr.maxlego08.zspawner.zcore.utils.gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import fr.maxlego08.zspawner.api.PlayerSpawner;
import fr.maxlego08.zspawner.api.Spawner;
import fr.maxlego08.zspawner.objects.PlayerObject;
import fr.maxlego08.zspawner.zcore.ZPlugin;

public class PlayerSpawnerAdapter extends TypeAdapter<PlayerSpawner> {

	private static Type seriType = new TypeToken<Map<String, Object>>() {
	}.getType();

	private final String UUIDNAME = "uuid";
	private final String SPAWNERS = "spawners";

	@Override
	public PlayerSpawner read(JsonReader jsonReader) throws IOException {
		if (jsonReader.peek() == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		}
		return fromRaw(jsonReader.nextString());
	}

	@Override
	public void write(JsonWriter jsonWriter, PlayerSpawner playerSpawner) throws IOException {
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
	private String getRaw(PlayerSpawner playerSpawner) {
		Map<String, Object> serial = new HashMap<String, Object>();
		serial.put(UUIDNAME, playerSpawner.getUser());
		serial.put(SPAWNERS, playerSpawner.getSpawners());
		return ZPlugin.z().getGson().toJson(serial);
	}

	/**
	 * 
	 * @param raw
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private PlayerSpawner fromRaw(String raw) {
		Map<String, Object> keys = ZPlugin.z().getGson().fromJson(raw, seriType);
		UUID uuid = UUID.fromString((String) keys.get(UUIDNAME));
		List<Spawner> spawners = new ArrayList<Spawner>((List<Spawner>) keys.get(SPAWNERS));
		return new PlayerObject(uuid, spawners);
	}

}
