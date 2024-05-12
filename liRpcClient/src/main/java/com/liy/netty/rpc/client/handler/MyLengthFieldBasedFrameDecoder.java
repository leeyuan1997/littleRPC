package com.liy.netty.rpc.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class MyLengthFieldBasedFrameDecoder extends LengthFieldBasedFrameDecoder {
    public MyLengthFieldBasedFrameDecoder() {
        super(Integer.MAX_VALUE,0,4,0,4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return super.decode(ctx, in);
    }
}
