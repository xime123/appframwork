
package com.app.http.func;


import com.app.http.exception.ApiException;
import com.app.http.exception.ServerException;
import com.app.http.model.ApiResult;

import rx.functions.Func1;

/**
 * <p>描述：ApiResult<T>转换T</p>

 */
public class HandleFuc<T> implements Func1<ApiResult<T>, T> {
    @Override
    public T call(ApiResult<T> response) {
        if (ApiException.isOk(response)) {
            return response.getData();
        } else {
            throw new ServerException(response.getCode(), response.getMsg());
        }
    }
}
