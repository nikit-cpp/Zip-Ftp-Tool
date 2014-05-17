import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.swing.SwingUtilities;

public class EDTInvocationHandler implements InvocationHandler {

    /**
     * Method invocation result
     */
    private Object invocationResult = null;

    /**
     * Target object to translate method's call
     */
    private UploadProgress ui;

    /**
     * Creates invocation handler
     *
     * @param ui target object
     */
    public EDTInvocationHandler(UploadProgress ui) {
        this.ui = ui;
    }

    /**
     * Invokes method on target object. If {@link RequireGUIThread} annotation present,
     * method is invoked in the EDT thread, otherwise - in current thread.
     *
     * @param proxy  proxy object
     * @param method method to invoke
     * @param args   method arguments
     * @return invocation result
     * @throws Throwable if error occures while calling method
     */
    
    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        RequireGUIThread mark = method.getAnnotation(RequireGUIThread.class);
        if (mark != null) {
            if (SwingUtilities.isEventDispatchThread()) {
                invocationResult = method.invoke(ui, args);
            } else {
                Runnable shell = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            invocationResult = method.invoke(ui, args);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                };
                SwingUtilities.invokeLater(shell);
            }
        } else {
            invocationResult = method.invoke(ui, args);
        }
        return invocationResult;
    }
}