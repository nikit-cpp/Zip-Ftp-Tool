package controller;

import java.util.ArrayList;

import main.UploadProgressWindow;

public class Controller implements Listener{
	private Controller(){
		listeners = new ArrayList<Listener>();
		
		// добавляем себя в слушатели
		listeners.add(this);
	}
	
    public static class SingletonHolder {
        public static final Controller HOLDER_INSTANCE = new Controller();
    }
    
    public static Controller getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }
    
    private ArrayList<Listener> listeners;
	private boolean allowGui=false;
	
    synchronized public void addListener(Listener listener){
    	listeners.add(listener);
    }

    synchronized public void fireEvent(Event event) {
        for (int i=0; i<listeners.size(); i++) {
        	Listener listener = listeners.get(i);
            listener.onEvent(event);
        }
    }
    
    
    
    
    
	public synchronized void onEvent(Event event) {
		if (event == null)
			return;

		switch (event.type) {
		case EXIT:
			System.exit(0);
			break;
		case NEW_PROGRESS_WINDOW:
			// создаём окно
			if(!allowGui)
				break;
			UploadProgressWindow progressWindow = new UploadProgressWindow(Event.getThreadId(event)); // добавление слушателя -- в конструкторе окна
			progressWindow.show();
			break;
		default:
			break;
		}
	}

	public void allowGui(boolean b) {
		this.allowGui=b;
	}

}
