package com.liy.netty.rpc.utils;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RpcUtils {
    private static final AtomicInteger REQUEST_ID_GENERATOR = new AtomicInteger(0);

    public static int generateRequestId() {
        return REQUEST_ID_GENERATOR.incrementAndGet();
    }

    public static boolean isValidRequest(byte[] data) {
        if (data == null || data.length < 8) {
            return false;
        }
        int magic = ByteBuffer.wrap(data, 0, 4).getInt();
        int version = ByteBuffer.wrap(data, 4, 4).getInt();
        return magic == RpcConstants.MAGIC ;
    }

    // 其他工具方法
}