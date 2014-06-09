package session.messages;

public class Message {
	public Message(MType t, Object o) {
		this.type=t;
		if(o!=null){
			if(o.getClass()==String.class)
				message_s=(String)o;
			if(o.getClass()==Double.class)
				message_d=(Double)o;
			if(o.getClass()==Integer.class)
				message_i=(Integer)o;
		}
	}
	public MType type;
	public String message_s;
	public double message_d;
	public Integer message_i;
}
