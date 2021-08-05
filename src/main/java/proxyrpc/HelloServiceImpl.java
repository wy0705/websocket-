package proxyrpc;

public class HelloServiceImpl implements HelloService {
    @Override
    public void hello(String msg) {
        System.out.println(msg);

//        Class<String> aClass = String.class;
    }

}
