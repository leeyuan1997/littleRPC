package com.liy.netty.rpc.serialize;

public enum SerializerType {
    JSON(1),JDK(2);

    final int id;
    SerializerType(int id) {
        this.id = id;
    }


   public  static SerializerType fromId(int id) {
        for(SerializerType type: SerializerType.values()) {
            if(type.id == id) {
                return type;
            }
        }
        return null;
   }

    public static void main(String[] args) {
        System.out.println(SerializerType.JDK.toString());
    }
}

