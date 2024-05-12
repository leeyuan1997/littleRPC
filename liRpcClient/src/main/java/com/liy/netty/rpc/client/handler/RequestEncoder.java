package com.liy.netty.rpc.client.handler;

import com.liy.netty.rpc.protcol.impl.RpcRequest;
import com.liy.netty.rpc.serialize.Serializer;
import com.liy.netty.rpc.serialize.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RequestEncoder extends MessageToByteEncoder<RpcRequest> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest, ByteBuf byteBuf) throws Exception {
        int serializerTypeId = rpcRequest.getSerializerTypeId();
        Serializer serializer = SerializerFactory.getSerializer(serializerTypeId);
        byte[] encode = serializer.encode(rpcRequest);
        //整个请求序列化了

        byteBuf.writeInt(encode.length+8);
        byteBuf.writeInt(rpcRequest.getSerializerTypeId());
        byteBuf.writeInt(rpcRequest.getMessageTypeId());

        byteBuf.writeBytes(encode);
    }
}


