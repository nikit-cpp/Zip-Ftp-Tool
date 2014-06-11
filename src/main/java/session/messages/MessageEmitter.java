package session.messages;

import java.util.Observable;
import java.util.Observer;

public abstract class MessageEmitter extends Observable {
	protected boolean emitAvailable=false;
	
	public void addObserver(Observer o){
		if(o==null) return;
		emitAvailable=true;
		super.addObserver(o);
	}

	public void emitMessage(MType t, Object o){
		if(!emitAvailable)
			return;
		
		setChanged();
		notifyObservers(new Message(t, o));
	}
}
