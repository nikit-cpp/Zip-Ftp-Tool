package controller;

public class Event {
	
	public static enum Events {
		SERVER_CHANGED,
		FILE_CHANGED,
		PERSENT_CHANGED,
		EXIT,
		NEW_PROGRESS_WINDOW,
		UPLOAD_COMPLETED;
	}

	
	public Event(Events t, Object o) {
		this.type=t;
		this.thread_id=Thread.currentThread().getId();
		if(o!=null){
			if(o.getClass()==String.class)
				message_s=(String)o;
			if(o.getClass()==Double.class)
				message_d=(Double)o;
			if(o.getClass()==Integer.class)
				message_i=(Integer)o;
			if(o.getClass()==Long.class){
				thread_id=(Long)o;
			}
		}
			
	}
	
	
	public Events type;
	
	private String message_s;
	private double message_d;
	private int message_i;
	private long thread_id;

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
