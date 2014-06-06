package timeout.annotation.processor;

import java.lang.reflect.Proxy;

public class TimeoutProxy {

	public static Object getNewProxy(Object proxied, Class<?> interfaze) {
		Object proxy = Proxy.newProxyInstance(
				TimeoutInvocationHandler.class.getClassLoader(),
				new Class[] { interfaze },
				new TimeoutInvocationHandler(proxied));
		return proxy;
	}

}