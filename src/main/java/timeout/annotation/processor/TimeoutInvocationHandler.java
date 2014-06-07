package timeout.annotation.processor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import timeout.annotation.Timeout;

public class TimeoutInvocationHandler implements InvocationHandler {
	private Object proxied;

	public TimeoutInvocationHandler(Object proxied) {
		this.proxied = proxied;
	}

	@Override
	public Object invoke(Object proxy, final Method method, final Object[] args)
			throws Throwable {
		Method m = proxied.getClass().getMethod(method.getName(),
				method.getParameterTypes());
		Object ret = null;
		if (m.isAnnotationPresent(Timeout.class)) {
			System.out.println("\tIn the annotation processor");
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<Object> future = executor.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					return method.invoke(proxied, args);
				}
			});

			//try {
				System.out.println("Started..");
				long timeout2 = m.getAnnotation(Timeout.class).value();
				TimeUnit units2 = m.getAnnotation(Timeout.class).units();
				ret = future.get(timeout2, units2);
				System.out.println("Successfully finished!");
			/*} catch (TimeoutException e) {
				System.out.println("caused TimeoutException, terminated!");
			}*/

			executor.shutdownNow();
		} else {
			ret = method.invoke(proxied, args);
		}

		return ret;
	}
}
