package com.liy.netty.rpc.protcol.impl;

import com.liy.netty.rpc.protcol.Message;
import com.liy.netty.rpc.protcol.MessageType;
import com.liy.netty.rpc.serialize.SerializerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeartBeatMessage extends Message {
    int RequestId;
    int serializerTypeId;
    int messageTypeId;





}
