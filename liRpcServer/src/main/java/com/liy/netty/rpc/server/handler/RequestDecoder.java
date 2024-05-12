package com.liy.netty.rpc.server.handler;

import com.liy.netty.rpc.protcol.MessageType;
import com.liy.netty.rpc.protcol.impl.HeartBeatMessage;
import com.liy.netty.rpc.protcol.impl.RpcRequest;
import com.liy.netty.rpc.serialize.Serializer;
import com.liy.netty.rpc.serialize.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
//int RequestId;
//int serializerTypeId;
//int messageTypeId;
public class RequestDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int seralizedTypeId = byteBuf.readInt();
        int messageTypeId = byteBuf.readInt();
        byte[]instance = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(instance);
        Serializer serializer = SerializerFactory.getSerializer(seralizedTypeId);

        if(messageTypeId==1) {
            RpcRequest rpcRequest = serializer.parseObject(instance, RpcRequest.class);
            list.add(rpcRequest);
        }else{
            HeartBeatMessage heartBeatMessage = serializer.parseObject(instance, HeartBeatMessage.class);
            list.add(heartBeatMessage);
        }

    }
}
