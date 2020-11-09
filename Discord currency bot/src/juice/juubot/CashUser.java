package juice.juubot;
public  class CashUser
{
	private int _cash;
	private long _lastTimely;
	private long _ID;
	private String _tag;
	private String _mention;
	//private String _lastSomething;

	
	public static void compareByCash(CashUser lcu, CashUser rcu) {// find the fastest fighter
	//	return lcu.stats.get("Initiative").compareTo(rcu.stats.get("Initiative"));
	//	return lcu.get_cash().compareTo(rcu.get_cash());
	//	return lcu.get_cash() >  rcu.get_cash();
	}
	
	public CashUser() {
		
	}
public CashUser(int cash ,long lastTimely, long _ID, String _tag, String _mention )
{
	this._cash = cash;
	this._lastTimely = lastTimely;
	this._ID = _ID;
	this._tag = _tag;
	this._mention =_mention;
	}
public int get_cash() {
	return _cash;
}
public void set_cash(int _cash) {
	this._cash = _cash;
}
public long get_lastTimely() {
	return _lastTimely;
}
public void set_lastTimely(long _lastTimely) {
	this._lastTimely = _lastTimely;
}
public long get_ID() {
	return _ID;
}
public void set_ID(long _ID) {
	this._ID = _ID;
}
public String get_tag() {
	return _tag;
}
public void set_tag(String _tag) {
	this._tag = _tag;
}
public String get_mention() {
	return _mention;
}
public void set_mention(String _mention) {
	this._mention = _mention;
}
public void set(int counter, String subString) {
	switch(counter) {
	case 0: 
 this._cash = Integer.parseInt(subString);
		break;
	case 1: this._lastTimely = Long.parseLong(subString);
		break;
	case 2:  set_ID(Long.parseLong(subString));//this._ID = //Integer.parseInt(subString);
		break;
	case 3: this._mention = subString;
		break;
	case 4: this._tag = subString; 
		break;
	}
	
}


	
}
