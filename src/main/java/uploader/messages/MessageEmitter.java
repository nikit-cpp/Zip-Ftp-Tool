package uploader.messages;

import java.util.Observable;

public abstract class MessageEmitter extends Observable {		
	public void emitMessage(MType t, Object o){
		setChanged();
		notifyObservers(new Message(t, o));
	}
}
