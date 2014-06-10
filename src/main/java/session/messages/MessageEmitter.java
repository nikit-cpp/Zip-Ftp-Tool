package session.messages;

import java.util.Observable;
import java.util.Observer;

public abstract class MessageEmitter extends Observable {
	protected boolean emitAvailable=false;
	
	synchronized public void addObserver(Observer o){
		emitAvailable=true;
		super.addObserver(o);
	}

	synchronized public void emitMessage(MType t, Object o){
		if(!emitAvailable)
			return;
		
		setChanged();
		notifyObservers(new Message(t, o));
	}
}
