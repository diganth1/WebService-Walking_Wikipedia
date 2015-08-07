package wikiwalking;
public class HashEntry
{
	private int pageID;
	private int parentID;
	private int direction;

	public HashEntry(int id, int pid, int direct)
	{
		this.pageID = id;
		this.parentID = pid;
		this.direction = direct;
	}

	public int getID()
	{
		return this.pageID;
	}

	public int getParentID()
	{
		return this.parentID;
	}
	
	public int getDirection()
	{
		return this.direction;
	}
}