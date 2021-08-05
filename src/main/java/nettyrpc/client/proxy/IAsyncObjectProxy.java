package nettyrpc.client.proxy;

import nettyrpc.client.RPCFuture;


public interface IAsyncObjectProxy {
    public RPCFuture call(String funcName, Object... args);
}