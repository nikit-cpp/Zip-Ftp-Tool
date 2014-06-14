package controller;

public class Event {
	
	public static enum Events {
		SERVER_CHANGED,
		FILE_CHANGED,
		PERSENT_CHANGED,
		EXIT,
		NEW_PROGRESS_WINDOW;
	}

	
	public Event(Events t, Object o) {
		this.type=t;
		boolean thread_id_is_set=false;
		if(o!=null){
			if(o.getClass()==String.class)
				message_s=(String)o;
			if(o.getClass()==Double.class)
				message_d=(Double)o;
			if(o.getClass()==Integer.class)
				message_i=(Integer)o;
			if(o.getClass()==Long.class){
				thread_id=(Long)o;
				thread_id_is_set=true;
			}
		}
		
		if(!thread_id_is_set)
		this.thread_id=Thread.currentThread().getId();
	}
	
	private long thread_id;
	
	public Events type;
	
	public String message_s;
	public double message_d;
	public int message_i;
	
	public static String getServerChangedString(Object o){
		return ((Event)o).message_s;
	}
	
	public static String getFileChangedString(Object o){
		return ((Event)o).message_s;
	}
	
	public static double getPercentChangedDouble(Object o){
		return ((Event)o).message_d;
	}

	public static long getThreadId(Object o){
		return ((Event)o).thread_id;
	}
}
