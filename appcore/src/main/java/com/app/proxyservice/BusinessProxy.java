package com.app.proxyservice;

import com.app.http.EasyHttp;
import com.app.http.callback.CallBack;

import java.util.HashMap;

import rx.Subscription;


public class BusinessProxy {

	private static final String TAG_NET = "BusinessProxy/NET";
	private final static BusinessProxy BUSINESSPROXY = new BusinessProxy();
	private HashMap<String, String> mBaseReqParams = new HashMap<>();
    private HashMap<String, String> mReqHeaders = new HashMap<String, String>();

	private BusinessProxy() {

	}

	public static BusinessProxy shareInstance() {
		return BUSINESSPROXY;
	}


	public HashMap<String, String> getBaseReqParams(){
		return mBaseReqParams;
	}

	/**
	 * 异步请求Get
	 * @param req
	 * @param respCallback
	 * @return
	 */
	public <T> Subscription asyncAccessBusinessProxyGet(MessageReq req,  CallBack<T> respCallback) {

		return EasyHttp.get(req.url)
			.params(req.params)
			.execute(respCallback);
	}

	/**
	 * 异步请求Post
	 * @param req
	 * @param respCallback
	 * @return
	 */
	public <T> Subscription asyncAccessBusinessProxyPost(MessageReq req,  CallBack<T> respCallback) {

		return EasyHttp.post(req.url)
			.upJson(req.getData())//上传Json
			.params(req.params)
			.execute(respCallback);
	}

	/**
	 * 异步请求Put
	 * @param req
	 * @param respCallback
	 * @return
	 */
	public <T> Subscription asyncAccessBusinessProxyPut(MessageReq req, CallBack<T> respCallback) {

		return EasyHttp.put(req.url)
			.params(req.params)
			.execute(respCallback);
	}

	/**
	 * 请求Delete
	 * @param req
	 * @param respCallback
	 * @return
	 */
	public <T> Subscription asyncAccessBusinessProxyDelete(MessageReq req, CallBack<T> respCallback) {

		return EasyHttp.delete(req.url)
			.params(req.params)
			.execute(respCallback);
	}


}
