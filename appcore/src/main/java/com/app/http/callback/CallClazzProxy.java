
package com.app.http.callback;

import com.app.http.model.ApiResult;
import com.app.http.utils.Utils;
import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;

/**
 * <p>描述：提供Clazz回调代理</p>
 * 主要用于可以自定义ApiResult<br>
 */
public abstract class CallClazzProxy<T extends ApiResult<R>, R> implements IType<T> {
    private Type type;


    public CallClazzProxy(Type type) {
        this.type = type;
    }

    public Type getCallType() {
        return type;
    }

    @Override
    public Type getType() {//CallClazz代理方式，获取需要解析的Type
        Type typeArguments = null;
        if (type != null) {
            typeArguments = type;
        }
        if (typeArguments == null) {
            typeArguments = ResponseBody.class;
        }
        Type rawType = Utils.findNeedType(getClass());
        if (rawType instanceof ParameterizedType) {
            rawType = ((ParameterizedType) rawType).getRawType();
        }
        return $Gson$Types.newParameterizedTypeWithOwner(null, rawType, typeArguments);
    }
}
