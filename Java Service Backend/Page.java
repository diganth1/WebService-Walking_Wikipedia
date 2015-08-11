package wikiwalking;

public class Page
{
	private String pageName;
	private int pageID;
	private int direction;
	private int depth;

	public Page(String pName, int pID, int nDepth, int direct)
	{
		this.pageName = pName;
		this.pageID = pID;
		this.depth = nDepth;
		this.direction = direct;
	}

	public Page(int pID, int nDepth, int direct)
	{
		this.pageID = pID;
		this.depth = nDepth;
		this.direction = direct;
	}

	public String getPageName()
	{
		return this.pageName;
	}

	public int getPageID()
	{
		return this.pageID;
	}
	
	public int getDirection()
	{
		return this.direction;
	}

	public void setDirection(int direct)
	{
		this.direction = direct;
	}

	public int getDepth()
	{
		return this.depth;
	}

	public void setDepth(int nDepth)
	{
		this.depth = nDepth;
	}
}
