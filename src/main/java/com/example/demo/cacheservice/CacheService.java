package com.example.demo.cacheservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.demo.entity.Description;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

public class CacheService {

  public CacheService(String password) {
    shardInfo.setPassword(password);
  }

  private static String hostname = "instartgram.redis.cache.windows.net";

  private static int port = 6380;

  private static boolean ssl = true;

  private JedisShardInfo shardInfo = new JedisShardInfo(hostname, port, ssl);

  public boolean idExists(String id) {
    Jedis jedis = new Jedis(shardInfo);
    boolean exists = jedis.exists(id.getBytes());
    jedis.close();
    return exists;
  }

  public List<Object> getFromCache(String pattern) {
		List<Object> results = new ArrayList<Object>();
    Jedis jedis = new Jedis(shardInfo);
		Set<String> keys = jedis.keys(pattern);
		for (String key : keys){
			byte[] result = jedis.get(key.getBytes());
			results.add(unserialize(result));
		}
		jedis.close();
    return results;
  }

  public Object getFromCacheById(String id) {
		Jedis jedis = new Jedis(shardInfo);
		Object todo = unserialize(jedis.get(id.getBytes()));
		jedis.close();
		return todo;
	}

  public void insertCache(String id, Description description) {
		Jedis jedis = new Jedis(shardInfo);
		jedis.set(id.getBytes(), serialize(description));
		jedis.close();
	}

  public void deleteFromCacheById(String id) {
		Jedis jedis = new Jedis(shardInfo);
		jedis.del(id.getBytes());
		jedis.close();
	}

  private Object unserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {

		}
		return null;
	}

  private byte[] serialize(Description description) {
		try {
			ObjectOutputStream oos = null;
			ByteArrayOutputStream baos = null;
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(description);
			byte[] bytes = baos.toByteArray();
      return bytes;
		} catch (Exception e) {
      return null;
		}
	}
  
}
