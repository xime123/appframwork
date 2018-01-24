package com.app.proxyservice;


import android.util.Log;

import com.app.util.JSONParser;
import com.app.util.MapUtils;
import com.app.util.NumberParser;

import java.util.HashMap;
import java.util.Map;

public abstract class MessageResp {
	private static final String TAG = "MessageResp";
	public String errMsg;
	public int errorCode;
	public int cacheTime;  		// 客户端需要缓存时间，单位秒。如果是0或者该字段不存在代表不缓存
	public Object errorInfo; 	// 支持错误处理，在UI页面会做相应处理
	public Object showViewInfo;

	public void parse(String content) throws Exception {
		
		Map<String, Object> r = JSONParser.parse(content);
		this.errorCode = (Integer) r.get("errorCode");
		if(r.containsKey("cacheTime")) {
			cacheTime = NumberParser.parseInt(MapUtils.getString(r, "cacheTime", "0"));
			Log.d(TAG, "cacheTime is " + cacheTime + "s.");
		}

		if (this.errorCode == ErrorCode.SUCC) {
			// 业务层面返回成功，进一步解析消息数据
			Object data = r.get("data");
			if(data != null && data instanceof Map) {
				Map<String, Object> dataMap = (Map<String, Object>) data;
				if(dataMap.containsKey("showView")) {
					showViewInfo = getMap(dataMap, "showView", null);
				}
			}
			this.parseData(data);
			this.parseOtherParam(r);
		}

	}

	public void parseOtherParam(Map<String, Object> rsp) {
	}

	public abstract void parseData(Object data);
	
	/**
	 * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值""<br>
	 * 2.若存在该key，则将该键值强转成String类型（整型，浮点型均可）<br>
	 * @param map
	 * @param key
	 * @return
	 */
	public String getString(Map<String, Object> map, String key) {
		return getString(map, key, "");
	}
	
	/**
	 * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值强转成String类型（整型，浮点型均可）<br>
	 * @param map
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getString(Map<String, Object> map, String key, String defaultValue) {
		return MapUtils.getString(map, key, defaultValue);
	}
	
	/**
	 * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值0<br>
	 * 2.若存在该key，则将该键值强转成int类型, 强转失败则返回默认值0<br>
	 * @param map
	 * @param key
	 * @return
	 */
	public int getInt(Map<String, Object> map, String key) {
		return getInt(map, key, 0);
	}
	
	/**
	 * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值强转成int类型，强转失败则返回默认值<br>
	 * @param map
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getInt(Map<String, Object> map, String key, int defaultValue) {
		return MapUtils.getInt(map, key, defaultValue);
	}
	
	/**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回0.0<br>
	 * 2.若存在该key，则将该键值强转成double类型，强转失败则返回0.0<br>
     *
     * @param key a String
     * @return a double value
     */
    public double getDouble(Map<String, Object> map, String key) {
        return getDouble(map, key, 0.0);
    }

    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值强转成double类型，强转失败则返回默认值<br>
     *
     * @param key a String
     * @return a double value
     */
    public double getDouble(Map<String, Object> map, String key, double defaultValue) {
    	return MapUtils.getDouble(map, key, defaultValue);
    }
    
    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回false<br>
	 * 2.若存在该key，则将该键值强转成double类型，强转失败则返回false<br>
     *
     * @param key a String
     * @return
     */
    public boolean getBoolean(Map<String, Object> map, String key) {
        return getBoolean(map, key, false);
    }

    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值强转成boolean类型，强转失败则返回默认值<br>
     *
     * @param key a String
     * @return 
     */
    public boolean getBoolean(Map<String, Object> map, String key, boolean defaultValue) {
    	return MapUtils.getBoolean(map, key, defaultValue);
    }
    
    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回false<br>
	 * 2.若存在该key，则将该键值强转成float类型，强转失败则返回0.0F<br>
     *
     * @param key a String
     * @return
     */
    public float getFloat(Map<String, Object> map, String key) {
        return getFloat(map, key, 0.0F);
    }

    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值强转成float类型，强转失败则返回默认值<br>
     *
     * @param key a String
     * @return 
     */
    public float getFloat(Map<String, Object> map, String key, float defaultValue) {
    	return MapUtils.getFloat(map, key, defaultValue);
    }
    
    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回false<br>
	 * 2.若存在该key，则将该键值强转成long类型，强转失败则返回0L<br>
     *
     * @param key a String
     * @return
     */
    public long getLong(Map<String, Object> map, String key) {
        return getLong(map, key, 0L);
    }

    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值强转成long类型，强转失败则返回默认值<br>
     *
     * @param key a String
     * @return 
     */
    public long getLong(Map<String, Object> map, String key, long defaultValue) {
    	return MapUtils.getLong(map, key, defaultValue);
    }
    
    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回false<br>
	 * 2.若存在该key，则将该键值强转成char类型，强转失败则返回(char) 0<br>
     *
     * @param key a String
     * @return
     */
    public char getChar(Map<String, Object> map, String key) {
        return getChar(map, key, (char) 0);
    }

    /**
     * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值强转成char类型，强转失败则返回默认值<br>
     *
     * @param key a String
     * @return 
     */
    public char getChar(Map<String, Object> map, String key, char defaultValue) {
    	return MapUtils.getChar(map, key, defaultValue);
    }
	
	/**
	 * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回空数组<br>
	 * 2.若存在该key，且对应键值是Object[]类型，则正常返回，否则返回空数组<br>
	 * @param map
	 * @param key
	 * @return
	 */
	public Object[] getObjectArray(Map<String, Object> map, String key){
		return getObjectArray(map, key, new Object[0]);
	}

	/**
	 * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值是Object[]类型，则正常返回，否则返回defaultValue<br>
	 * @param map
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Object[] getObjectArray(Map<String, Object> map, String key, Object[] defaultValue){
		return MapUtils.getObjectArray(map, key, defaultValue);
	}
	
	/**
	 * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回空Map<br>
	 * 2.若存在该key，则将该键值强转成Map<String, Object>类型，若强转失败则返回空Map<br>
	 * @param map
	 * @param key
	 * @return
	 */
	public Map<String, Object> getMap(Map<String, Object> map, String key){
		return getMap(map, key, new HashMap<String, Object>(0));
	}

	/**
	 * 读取map中key对应的键值，<br>
	 * 1.若无该键值则返回默认值defaultValue<br>
	 * 2.若存在该key，则将该键值强转成Map<String, Object>类型，若强转失败则返回defaultValue<br>
	 * @param map
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public Map<String, Object> getMap(Map<String, Object> map, String key, Map<String, Object> defaultValue){
		return MapUtils.getMap(map, key, defaultValue);
	}
}
