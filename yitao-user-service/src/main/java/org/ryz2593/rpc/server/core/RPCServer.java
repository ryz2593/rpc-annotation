package org.ryz2593.rpc.server.core;

import com.ryz2593.rpc.entity.RequestData;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ryz2593
 */
public class RPCServer {
    /**
     * 存储暴露服务的容器
     */
    private static Map<String, Object> serverMap = new ConcurrentHashMap<>(32);

    /**
     * 创建一个线程池
     */
    ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(8,
            20,
            200,
            TimeUnit.MICROSECONDS,
            new ArrayBlockingQueue<Runnable>(5));

    public RPCServer() {
        scanService("./");
    }

    /**
     * 暴露服务的核心方法
     */
    private void scanService(String path) {
        URL resource = this.getClass().getClassLoader().getResource(path.trim());
        File file = new File(resource.getFile());
        File[] files = file.listFiles();
        for (File childFile : files) {
            //判断子文件是否为文件夹
            if (childFile.isDirectory()) {
                scanService(path + childFile.getName() + "/");
            } else {
                String fileName = childFile.getName();
                if (fileName.endsWith(".class")) {
                    //获取类路径
                    String classPath = getClassPath(childFile.getPath());
//                    System.out.println(classPath);

                    try {
                        //把类路径转换为一个类对象
                        Class<?> cls = ClassLoader.getSystemClassLoader().loadClass(classPath);
                        //判断是否为要发布的服务
                        if (cls.isAnnotationPresent(GerryService.class)) {
                            serverMap.put(cls.getInterfaces()[0].getName(), cls.newInstance());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String getClassPath(String filePath) {
        String key = "classes";
        String packageFilePath = filePath
                .substring(filePath.indexOf(key) + key.length() + 1);
        return packageFilePath
                .replaceAll("\\\\", ".")
                .replaceAll("\\.class", "");
    }

    public void start(int port) {
        try {
            //服务端socket对象
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(port));
            System.out.println("服务启动中。。。");
            while (true) {
                poolExecutor.execute(new TaskThread(serverSocket.accept()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class TaskThread implements Runnable {
        private final Socket client;

        public TaskThread(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try (ObjectInputStream deSerializer = new ObjectInputStream(client.getInputStream());
                 ObjectOutputStream serializer = new ObjectOutputStream(client.getOutputStream())
            ) {
                //可以从客户端中获取到包装的RequestData 对象
                RequestData requestData = (RequestData) deSerializer.readObject();
                //获取这个请求接口对应service实例
                String interfaceName = requestData.getInterfaceName();

                Object serviceInstance = serverMap.get(interfaceName);
                //创建客户调用的方法对象
                Method method = serviceInstance.getClass()
                        .getDeclaredMethod(requestData.getMethodName(), requestData.getParameterTypes());

                //反射调用对应的方法
                Object result = method.invoke(serviceInstance, requestData.getParameters());
                //把结果序列化到网络
                serializer.writeObject(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
