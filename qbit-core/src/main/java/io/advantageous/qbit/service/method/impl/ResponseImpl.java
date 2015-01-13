package io.advantageous.qbit.service.method.impl;

import io.advantageous.qbit.annotation.JsonIgnore;
import io.advantageous.qbit.message.MethodCall;
import io.advantageous.qbit.message.Request;
import io.advantageous.qbit.message.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Richard on 8/11/14.
 */
public class ResponseImpl<T> implements Response<T> {

    private final Request<Object> request;
    private String address;

    private String returnAddress;
    private Map<String, Object> params;
    private Object body;
    private long id;
    private long timestamp;

    @JsonIgnore
    private transient Object transformedBody;
    private boolean errors;


    public ResponseImpl(MethodCall<Object> methodCall, T returnValue) {
        this.address = methodCall.address();
        this.returnAddress = methodCall.returnAddress();
        this.timestamp = methodCall.timestamp();
        this.id = methodCall.id();
        this.params = null;
        this.body = returnValue;
        this.request = methodCall;

    }


    public static Response<Object> response(long id, long timestamp, String address, String returnAddress, Object body, Request<Object> requestForResponse) {

        return new ResponseImpl<>(id, timestamp, address, returnAddress, null, body, requestForResponse);

    }



    public ResponseImpl(MethodCall<Object> methodCall, Throwable ex) {

        this.returnAddress = methodCall.returnAddress();
        this.timestamp = methodCall.timestamp();
        this.id = methodCall.id();

        final Map<String, Object> body = new HashMap<>(8);
        this.body = body;
        this.transformedBody = ex;
        this.address = methodCall.address();
        body.put("Error", ex.getMessage());
        body.put("Cause", "" + ex.getCause());
        body.put("Message", "Problem while calling method " + methodCall.name());

        if (ex instanceof Exception) {
            body.put("Details", ex);
        }
        this.errors = true;
        this.params = null;
        this.request =  methodCall;

    }

    public ResponseImpl(long id, long timestamp, String address, String returnAddress, Map<String, Object> params,
                        Object body, Request<Object> request) {
        this.address = address;
        this.params = params;
        this.body = body;
        this.id = id;
        this.timestamp = timestamp;
        this.returnAddress = returnAddress;
        this.request = request;
    }


    @Override
    public long id() {
        return id;
    }


    @Override
    public T body() {
        if (transformedBody == null) {
            return (T) body;
        } else {
            return (T) transformedBody;
        }
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public boolean wasErrors() {
        return errors;
    }

    public void body(T newBody) {

        transformedBody = newBody;

    }

    @Override
    public String returnAddress() {
        return returnAddress;
    }

    @Override
    public String address() {
        return address;
    }

    @Override
    public long timestamp() {
        return timestamp;
    }

    @Override
    public Request<Object> request() {

        return (Request<Object>) this.request;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResponseImpl)) return false;

        ResponseImpl response = (ResponseImpl) o;

        if (address != null ? !address.equals(response.address) : response.address != null) return false;
        if (body != null ? !body.equals(response.body) : response.body != null) return false;
        if (params != null ? !params.equals(response.params) : response.params != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ResponseImpl{" +
                "address='" + address + '\'' +
                ", params=" + params +
                ", body=" + body +
                '}';
    }

    public static Response<Object> response(MethodCall<Object> methodCall, Object returnValue) {

        ResponseImpl<Object> response = new ResponseImpl<>(methodCall, returnValue);

        return response;
    }
}
