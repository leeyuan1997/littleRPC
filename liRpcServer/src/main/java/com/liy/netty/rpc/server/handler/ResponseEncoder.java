package com.liy.netty.rpc.server.handler;

import com.liy.netty.rpc.protcol.impl.RpcResponse;
import com.liy.netty.rpc.serialize.Serializer;
import com.liy.netty.rpc.serialize.SerializerFactory;
import com.liy.netty.rpc.serialize.SerializerType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;

public class ResponseEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object rpcResponse2, ByteBuf byteBuf) throws Exception {
        RpcResponse rpcResponse = (RpcResponse) rpcResponse2;
        int serializerTypeId = rpcResponse.getSerializerTypeId();
        Serializer serializer = SerializerFactory.getSerializer(serializerTypeId);
        byte[] encode = serializer.encode(rpcResponse);
        //整个请求序列化了
        System.out.println("有i结果了"+rpcResponse.getResult());
        byteBuf.writeInt(encode.length+8);
        byteBuf.writeInt(rpcResponse.getSerializerTypeId());
        byteBuf.writeInt(rpcResponse.getMessageTypeId());
        byteBuf.writeBytes(encode);
    }


}
