package proxyrpc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Server {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        HelloService service=new HelloServiceImpl();
        ProxyFrameWork.export(service,6666);
    }
}
