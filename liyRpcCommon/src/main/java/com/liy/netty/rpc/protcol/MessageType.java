package com.liy.netty.rpc.protcol;

import com.liy.netty.rpc.protcol.impl.HeartBeatMessage;
import com.liy.netty.rpc.protcol.impl.RpcRequest;
import com.liy.netty.rpc.protcol.impl.RpcResponse;

public enum MessageType {

    REQUEST(1, RpcRequest.class),RESPONSE(2, RpcResponse.class),HEARTBEAT(3, HeartBeatMessage.class);

    int id;
    Class<?>MessageInstanceType;
    MessageType(int id,Class<?>MessageInstanceType) {
        this.id = id;
        this.MessageInstanceType = MessageInstanceType;
    }


}
