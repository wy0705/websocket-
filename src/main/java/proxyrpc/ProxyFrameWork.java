package proxyrpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyFrameWork {


    //暴露服务
    public static void export(final Object service, int port) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (service == null) {
            throw new IllegalArgumentException("argument");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port" + port);
        }
        System.out.println("Export service" + service.getClass().getName() + "port" + port);
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket accept = serverSocket.accept();
            ObjectInputStream objectInputStream = new ObjectInputStream(accept.getInputStream());
            String methodName = objectInputStream.readUTF();
            Class<?>[] paramTypes = (Class<?>[]) objectInputStream.readObject();
            Object[] args = (Object[]) objectInputStream.readObject();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(accept.getOutputStream());
            try {
                Method method = service.getClass().getMethod(methodName, paramTypes);
                Object invoke = method.invoke(service, args);
                objectOutputStream.writeObject(invoke);
                objectOutputStream.flush();
            } catch (Throwable throwable) {
                objectOutputStream.close();
            } finally {
                objectOutputStream.close();
            }

        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T refer(final Class<T> interfaceClass, final String host, final int port) {
        if (interfaceClass == null) {
            throw new IllegalArgumentException("interface class == null");
        }
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("The" + interfaceClass.getName() + "must be interface");
        }
        if (host == null || host.length() == 0) {
            throw new IllegalArgumentException("host==null!");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port" + port);
        }
        System.out.println("Get remote service" + interfaceClass.getName());

        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = new Socket(host, port);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeUTF(method.getName());
                objectOutputStream.writeObject(method.getParameterTypes());
                objectOutputStream.writeObject(args);
                objectOutputStream.flush();
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                return objectInputStream.readObject();
            }
        });
    }
}
