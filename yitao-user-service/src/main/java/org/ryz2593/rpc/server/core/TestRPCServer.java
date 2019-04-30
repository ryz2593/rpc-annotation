package org.ryz2593.rpc.server.core;

public class TestRPCServer {
    public static void main(String[] args) {
//        String path = TestRPCServer.class.getClassLoader().getResource("./").getPath();
//        System.out.println(path);
//        new RPCServer();

        RPCServer rpcServer = new RPCServer();
        rpcServer.start(12345);
    }
}
