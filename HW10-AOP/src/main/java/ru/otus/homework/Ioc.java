package ru.otus.homework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Ioc {

    private Ioc() {
    }

    static TestLoggingInterface createMyClass() {
        InvocationHandler handler = new DemoInvocationHandler(new TestLogging());
        return (TestLoggingInterface) Proxy.newProxyInstance(Ioc.class.getClassLoader(),
                new Class<?>[]{TestLoggingInterface.class}, handler);
    }

    static class DemoInvocationHandler implements InvocationHandler {

        private final TestLoggingInterface myClass;
        private List<Method> methodsWithAnnotationLog = new ArrayList<>();
        private List<String> methodsWithAnnotationLogName = new ArrayList<>();
        private List<String> methodsWithAnnotationLogParameterTypes = new ArrayList<>();

        DemoInvocationHandler(TestLoggingInterface myClass) {
            this.myClass = myClass;
            getMethodsWithAnnotationLog();
        }

        private void getMethodsWithAnnotationLog() {
            methodsWithAnnotationLog = Arrays.stream(myClass.getClass().getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(Log.class)).toList();
            if (methodsWithAnnotationLog.size() > 0) {
                methodsWithAnnotationLogName = methodsWithAnnotationLog.stream().map(Method::getName).toList();
                methodsWithAnnotationLogParameterTypes = methodsWithAnnotationLog.stream().map(method -> Arrays.toString(method.getParameters())).toList();
            }

        }

        private boolean isMethodAnnotated(Method method) {
            return methodsWithAnnotationLogName.contains(method.getName())
                    && methodsWithAnnotationLogParameterTypes.contains(Arrays.toString(method.getParameters()));
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (isMethodAnnotated(method)) {
                System.out.println("\n=== Start AOP logging ===");
                System.out.println("method executed: " + method.getName());
                System.out.println("params: " + Arrays.toString(args));
                System.out.println("=== Finish AOP logging");
            }
            return method.invoke(myClass, args);
        }

    }
}
