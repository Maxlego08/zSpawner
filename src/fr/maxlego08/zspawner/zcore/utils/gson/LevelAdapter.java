package fr.maxlego08.zspawner.zcore.utils.gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import fr.maxlego08.zspawner.api.Level;
import fr.maxlego08.zspawner.objects.LevelObject;
import fr.maxlego08.zspawner.zcore.ZPlugin;
import fr.maxlego08.zspawner.zcore.utils.economy.Economy;

@Deprecated
public class LevelAdapter extends TypeAdapter<Level> {

	private static Type seriType = new TypeToken<Map<String, Object>>() {
	}.getType();

	private final String LEVEL = "level";
	private final String ECONOMY = "economy";
	private final String PRICE = "price";
	private final String MINDELAY = "minDelay";
	private final String MAXDELAY = "maxDelay";
	private final String SPAWNCOUNT = "spawnCount";
	private final String MAXENTITIES = "maxNearbyEntities";
	private final String SPAWNRANGE = "spawnRange";
	private final String REQUIREDPLAYER = "requiredPlayerRange";

	@Override
	public Level read(JsonReader jsonReader) throws IOException {
		if (jsonReader.peek() == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		}
		return fromRaw(jsonReader.nextString());
	}

	@Override
	public void write(JsonWriter jsonWriter, Level playerSpawner) throws IOException {
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
	private String getRaw(Level level) {
		Map<String, Object> serial = new HashMap<String, Object>();
		serial.put(LEVEL, level.getId());
		serial.put(ECONOMY, level.getEconomy().name());
		serial.put(PRICE, level.getPrice());
		serial.put(MINDELAY, level.getMinDelay());
		serial.put(MAXDELAY, level.getMaxDelay());
		serial.put(SPAWNCOUNT, level.getSpawnCount());
		serial.put(MAXENTITIES, level.getMaxNearbyEntities());
		serial.put(SPAWNRANGE, level.getSpawnRange());
		serial.put(REQUIREDPLAYER, level.getRequiredPlayerRange());
		return ZPlugin.z().getGson().toJson(serial);
	}

	/**
	 * 
	 * @param raw
	 * @return
	 */
	private Level fromRaw(String raw) {
		Map<String, Object> keys = ZPlugin.z().getGson().fromJson(raw, seriType);
		Economy economy = Economy.valueOf((String) keys.getOrDefault(ECONOMY, "VAULT"));
		Number levelNumber = (Number) keys.get(LEVEL);
		Number priceNumber = (Number) keys.get(PRICE);
		Number minNumber = (Number) keys.get(MINDELAY);
		Number maxNumber = (Number) keys.get(MAXDELAY);
		Number spawnNumber = (Number) keys.get(SPAWNCOUNT);
		Number entitiesNumber = (Number) keys.get(MAXENTITIES);
		Number rangeNumber = (Number) keys.get(SPAWNRANGE);
		Number requiredNumber = (Number) keys.get(REQUIREDPLAYER);

		return new LevelObject(levelNumber.intValue(), economy, priceNumber.doubleValue(), minNumber.intValue(),
				maxNumber.intValue(), spawnNumber.intValue(), entitiesNumber.intValue(), rangeNumber.intValue(),
				requiredNumber.intValue(), null);

	}

}
