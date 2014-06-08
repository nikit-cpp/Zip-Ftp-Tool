package uploader.messages;

public enum MType {
	SERVER_CHANGED,
	FILE_CHANGED,
	PERSENT_CHANGED;
	
	public static String getServerChangedString(Object o){
		return ((Message)o).message_s;
	}
	
	public static String getFileChangedString(Object o){
		return ((Message)o).message_s;
	}
	
	public static double getPercentChangedDouble(Object o){
		return ((Message)o).message_d;
	}
}
