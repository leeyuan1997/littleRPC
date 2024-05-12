package com.liy.netty.rpc.client.handler;

import com.liy.netty.rpc.protcol.impl.HeartBeatMessage;
import com.liy.netty.rpc.protcol.impl.RpcRequest;
import com.liy.netty.rpc.protcol.impl.RpcResponse;
import com.liy.netty.rpc.serialize.Serializer;
import com.liy.netty.rpc.serialize.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ResponseDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int seralizedTypeId = byteBuf.readInt();
        int messageTypeId = byteBuf.readInt();
        byte[]instance = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(instance);
        Serializer serializer = SerializerFactory.getSerializer(seralizedTypeId);
        if(messageTypeId==2) {
            RpcResponse rpcResponse = serializer.parseObject(instance, RpcResponse.class);
            list.add(rpcResponse);
        }else{
            HeartBeatMessage heartBeatMessage = serializer.parseObject(instance, HeartBeatMessage.class);
            list.add(heartBeatMessage);
        }

    }
}
