package com.app.proxyservice;


import com.app.http.cache.model.CacheMode;
import com.app.util.GsonUtil;
import com.app.util.JSONParser;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MessageReq {
	public int mWhat;

	// 请求的url
	public String url;
	//请求方式
	public RequestMethod requestMethod=RequestMethod.POST;
	// 请求的接口名
	public String methodName;
	// 请求超时时间
	public String timeout;
	// 响应类名
	public Class<? extends MessageResp> rspClass;

	public boolean isAsync=true;

	// 参数
	public Map<String, String> params = new HashMap<String, String>();
	// 是否需要缓存
	public CacheMode cacheMode= CacheMode.DEFAULT;//304缓存
	// 缓存时间
	public int cacheExpiretime;
	
	public EncryptionType encryptionType = null;
	
	public enum EncryptionType{
		/**
		 * 平台密钥
		 */
		TYPE_PLATFORM,
		/**
		 * 会话密钥（用户密钥）
		 */
		TYPE_BUSINESS
	}

	public void setParam(String name, String value) {
		this.params.put(name, value);
	}

	public void setParam(Map<String,String>params) {
		this.params.putAll(params);
	}
	public void setParam(String name, String value, boolean fitJSON) {
		if (fitJSON && value instanceof String) {
			// 需要进行转义符的处理
			value = JSONParser.fitJSON((String) value);
		}
		this.params.put(name, value);
	}

	public void removeParam(String name) {
		this.params.remove(name);
	}
	
	public Map<String, String> getParams() {
		return params;
	}

	public String getData() {
		HashMap<String, String> baseReqParams = BusinessProxy.shareInstance().getBaseReqParams();
		for(String key : baseReqParams.keySet()) {
			if(!params.containsKey(key)) {
				params.put(key, baseReqParams.get(key));
			}
		}
		return GsonUtil.objectToJson(params,new TypeToken<Map<String, Object>>() {}.getType());
	}
	
	public void setEncryptData(String encrypted, String userToken){
		params.clear();
		params.put("data", encrypted);
		if(userToken != null && userToken.length() != 0) {
			params.put("userToken", userToken);
		}
	}
	
	/**
	 * 将url切分成四段，重新组合,如http://113.108.40.12:10010/jsse/miUserAuthService/login，切分后为<br>	
	 * http<br>
	   ://113.108.40.12:<br>
	   10010<br>
	   /jsse/miUserAuthService/login<br>
	    再重组得到https://113.108.40.12:10011/jsse/miUserAuthService/login
	 */
	public void buildUrlToHttps(){
		if (url != null) {
			Pattern pattern = Pattern.compile("^(http[s]?)(.+?:)([0-9]+)(/.+)$");
			Matcher m = pattern.matcher(url);
			if(m.find()){
				int port = Integer.parseInt(m.group(3)) + 1;
				StringBuilder sb = new StringBuilder();
				sb.append("https").append(m.group(2)).append(port).append(m.group(4));
				url = sb.toString();
			}
		}
	}
	


	private String getBaseData(Object data) {
		StringBuffer sb = new StringBuffer();
		if (data instanceof String) {
			sb.append('\'');
			sb.append(data);
			sb.append('\'');
		} else if (data instanceof Integer) {
			sb.append(data);
		} else if (data instanceof Double) {
			sb.append(data);
		} else if (data instanceof Boolean) {
			sb.append(data);
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private String getArrayObjectData(Object[] datas) {
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		boolean hasContent = false;
		for (Object data : datas) {
			if (hasContent) {
				sb.append(',');
			}

			if (data instanceof Map<?, ?>) {
				sb.append(getObjectData((HashMap<String, Object>) data));
			} else if (data instanceof Object[]) {
				sb.append(getArrayObjectData((Object[]) data));
			} else {
				sb.append(getBaseData(data));
			}
			hasContent = true;
		}
		sb.append(']');
		return sb.toString();
	}

	// Map转Json格式
	private String getObjectData(Map<?, ?> data) {
		@SuppressWarnings("unchecked")
        HashMap<String, Object> dataMap = (HashMap<String, Object>) data;
		StringBuffer sb = new StringBuffer();
		sb.append('{');
		boolean hasContent = false;
		for (String key : dataMap.keySet()) {
			Object v = dataMap.get(key);
			if (hasContent) {
				sb.append(',');
			}
			sb.append(key);
			sb.append(':');
			if (v instanceof Map<?, ?>) {
				sb.append(getObjectData((Map<?, ?>) v));
			} else if (v instanceof Object[]) {
				sb.append(getArrayObjectData((Object[]) v));
			} else {
				sb.append(getBaseData(v));
			}
			hasContent = true;
		}
		sb.append('}');

		return sb.toString();
	}

}
