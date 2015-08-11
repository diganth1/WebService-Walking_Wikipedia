package wikiwalking;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.sql.*;
import java.lang.Math;
import java.util.*;

public class WebService_WithClasses
{		
	public String getSimilarPages(String pageName)
	{
		Database db = null;
		try
		{
			db = new Database();
		}catch (SQLException e){e.printStackTrace();}
		char quotes = '"';
		int id = db.getIDForTitle(pageName);
		int numCats, perCat;
		ArrayList<Integer> similarPages = new ArrayList<Integer>();
		StringBuilder returnString = new StringBuilder("");
		long start = System.currentTimeMillis();
		int k=1;

		if (id == -1)
		{
			return "The page name you entered is invalid. Please try again.\n";
		}
		else
		{
			ArrayList<String> pageCats = db.getCategoriesInPage(id);
			numCats = pageCats.size();
			if (numCats <= 10)
			{
				perCat = 4;
			}
			else if (numCats <= 20)
			{
				perCat = 3;
			}
			else
			{
				perCat = 2;
			}

			returnString.append("Related pages to " + quotes + pageName + quotes + ":\n");
			for(int i=0; i<numCats; ++i)
			{				
				String curCat = pageCats.get(i);
				int numPages = db.getPageCountInCategory(curCat);
				System.out.print("Num pages is " + numPages);
				if (numPages > 165){continue;}
				
				ArrayList<Integer> pages = db.getPagesInCategory(curCat);
				
				returnString.append(" From category " + quotes + curCat + quotes + ":\n");

				Collections.shuffle(pages);
				for(int j=0; j<perCat; ++j)
				{
					similarPages.add(pages.get(j));
					returnString.append("    " + k + ". " + db.getTitleForID(pages.get(j)) + "\n");
					k++;
				}
			}
			
			String timeString = "Time elapsed: " + (System.currentTimeMillis() - start) + " ms\n";
			return returnString.toString() + timeString + "\n";
			
		}
	}

	public String getPageTitle(int pageID)
	{
		Database db = null;
		try
		{
			db = new Database();
		}catch (SQLException e){e.printStackTrace();}
		char quotes = '"';
		long start = System.currentTimeMillis();
		String title = db.getTitleForID(pageID);

		if (title != null)
		{			
			String timeString = "Time elapsed: " + (System.currentTimeMillis() - start) + " ms\n";
			return "Page ID " + pageID + " corresponds to the page " + quotes + title + quotes + "\n" + timeString;
		}
		else
		{
			return "The page ID you entered is invalid. Please try again.\n";
		}
	}

	public String getPageID(String pageName)
	{
		Database db = null;
		try
		{
			db = new Database();
		}catch (SQLException e){e.printStackTrace();}
		char quotes = '"';
		long start = System.currentTimeMillis();
		
		int id = db.getIDForTitle(pageName);
		if (id != -1)
		{
			String timeString = "Time elapsed: " + (System.currentTimeMillis() - start) + " ms\n";
			return "The page " + quotes + pageName + quotes + " has Page ID " + id + "\n" + timeString;
		}

		else
		{
			return "The page name you entered is invalid. Please try again.\n";
		}
	}

	public String getPageCategories(String pageName)
	{
		Database db = null;
		try
		{
			db = new Database();
		}catch (SQLException e){e.printStackTrace();}
		ArrayList<String> links = null;
		int pageid = db.getIDForTitle(pageName);
		String returnString = "";
		char quotes = '"';
		long start = System.currentTimeMillis();

		if (pageid == -1)
		{
			return "We're sorry, but the page name you provided is invalid\n";
		}
		else
		{
			links = db.getCategoriesInPage(pageid);
			for(int i=0; i<links.size(); ++i)
			{
				returnString += "" + (i+1) + ". " + links.get(i) + "\n";
			}
			String timeString = "Time elapsed: " + (System.currentTimeMillis() - start) + " ms\n";
			return "The page " + quotes + pageName + quotes + " is in the following " + links.size() + " categories:\n" + returnString + timeString + "\n";
		}
	}

	public String getCategoryPages(String catName)
	{
		Database db = null;
		try
		{
			db = new Database();
		}catch (SQLException e){e.printStackTrace();}
		ArrayList<Integer> pages = null;
		String returnString = "";
		char quotes = '"';
		long start = System.currentTimeMillis();

		pages = db.getPagesInCategory(catName);
		if (pages == null)
		{
			return "We're sorry, but the category name you provided is invalid\n";
		}
		else
		{
			for(int i=0; i<pages.size(); ++i)
			{
				returnString += "" + (i+1) + ". " + db.getTitleForID(pages.get(i)) + "\n";
			}
			String timeString = "Time elapsed: " + (System.currentTimeMillis() - start) + " ms\n";
			return "There are " + pages.size() + " pages in the category " + quotes + catName + quotes + ":\n" + returnString + timeString + "\n";
		}
	}

		
	public String getLinksToPage(String pageName)
	{
		Database db = null;
		try
		{
			db = new Database();
		}catch (SQLException e){e.printStackTrace();}
		ArrayList<String> links = null;
		int pageid = db.getIDForTitle(pageName);
		String returnString = "";
		char quotes = '"';
		long start = System.currentTimeMillis();
		if (pageid == -1)
		{
			return "We're sorry, but the page name you provided is invalid\n";
		}
		else
		{
			links = db.getLinkTitlesToPage(pageid);
			for(int i=0; i<links.size(); ++i)
			{
				returnString += "" + (i+1) + ". " + links.get(i) + "\n";
			}
			String timeString = "Time elapsed: " + (System.currentTimeMillis() - start) + " ms\n";
			return "There are " + links.size() + " links to " + quotes + pageName + quotes + ":\n" + returnString + timeString + "\n";
		}
	}

	public String getLinksFromPage(String pageName)
	{
		Database db = null;
		try
		{
			db = new Database();
		}catch (SQLException e){e.printStackTrace();}
		ArrayList<String> links = null;
		int pageid = db.getIDForTitle(pageName);
		String returnString = "";
		char quotes = '"';
		long start = System.currentTimeMillis();
		if (pageid == -1)
		{
			return "We're sorry, but the page name you provided is invalid\n";
		}
		else
		{
			links = db.getLinkTitlesFromPage(pageid);
			for(int i=0; i<links.size(); ++i)
			{
				returnString += "" + (i+1) + ". " + links.get(i) + "\n";
			}
			String timeString = "Time elapsed: " + (System.currentTimeMillis() - start) + " ms\n";
			return "There are " + links.size() + " links from " + quotes + pageName + quotes + ":\n" + returnString + timeString + "\n";
		}
	}

	public String computeRelatedness(String srcPageName, String dstPageName)
	{
		Database db = null;
		try
		{
			db = new Database();
		}catch (SQLException e){e.printStackTrace();}
		int srcPageID = db.getIDForTitle(srcPageName);
		int dstPageID = db.getIDForTitle(dstPageName);
		int max, min, inter;
		char quotes = '"';
		StringBuilder returnString = new StringBuilder("");
		long start = System.currentTimeMillis();

		if (srcPageID == -1 || dstPageID == -1)
		{
			return "We're sorry, but one or more of the page names you provided are invalid\n";
		}
		else
		{			
			ArrayList<String> srcLinks = db.getLinkTitlesToPage(srcPageID);
			ArrayList<String> dstLinks = db.getLinkTitlesToPage(dstPageID);
            
            int srcNum = srcLinks.size();
			int dstNum = dstLinks.size();
			if (srcNum >= dstNum)
			{
				max = srcNum;
				min = dstNum;
			}
			else
			{
				max = dstNum;
				min = srcNum;
			}
			srcLinks.retainAll(dstLinks);
			inter = srcLinks.size();
			returnString.append(quotes + srcPageName + quotes + " is linked to by " + srcNum + " pages.\n");
			returnString.append(quotes + dstPageName + quotes + " is linked to by " + dstNum + " pages.\n");
			returnString.append("\nThese pages share " + inter + " of their links in common.\n");
			for(int i=0;i<inter;++i)
			{
				returnString.append("" + (i+1) + ". " + srcLinks.get(i)+"\n");
			}
			double numerator = Math.log(max) - Math.log(inter);
			double denominator = Math.log(700000.0) - Math.log(min);
			returnString.append(String.format("\nThe relatedness coefficient is %.3f.\n",(numerator/denominator)));
			returnString.append("\nTime elapsed: " + (System.currentTimeMillis() - start) + " ms\n");
			return returnString.toString() + "\n";
		}
	}

	public String getPath_serial(String srcPageName, String dstPageName)
	{
		Database db = null;
		try
		{
			db = new Database();
		}catch (SQLException e){e.printStackTrace();}
		int srcPageID = db.getIDForTitle(srcPageName);
		int dstPageID = db.getIDForTitle(dstPageName);
		boolean found = false;
		StringBuilder route = new StringBuilder("");
		long start = System.currentTimeMillis();
		int pagesTouched = 0;
		ArrayList<Integer> finalPath = new ArrayList<Integer>();

		if (srcPageID == -1 || dstPageID == -1)
		{
			return "We're sorry, but one or more of the page names you provided are invalid\n";
		}
		else
		{            
			//Sanity check
			ArrayList<Integer> firstJump = db.getLinkIDsFromPage(srcPageID);
			if(firstJump.contains(dstPageID))
			{
				finalPath.add(dstPageID);
				finalPath.add(srcPageID);
				pagesTouched = firstJump.size();
				found = true;
				//return "The two pages are right next to one another\n";
			}
			else
			{
				Hashtable<Integer,Integer[]> linkMap = new Hashtable<Integer,Integer[]>();
				ArrayList<Integer[]> pageQueue = new ArrayList<Integer[]>();
				Integer[] startPage = {srcPageID,0,0};
				Integer[] finalPage = {dstPageID,0,1};
				linkMap.put(srcPageID, new Integer[]{srcPageID,-1,0});
				linkMap.put(dstPageID, new Integer[]{dstPageID,-1,1});
				pageQueue.add(startPage);
				pageQueue.add(finalPage);

				while((! pageQueue.isEmpty()) && (! found))
				{
					Integer[] curPage = pageQueue.remove(0);
					int curID = curPage[0];
					System.out.print("\nExpanding page: " + curID);
					int curDepth = curPage[1];
					int curDirection = curPage[2];
					ArrayList<Integer> curLinks;

					if(curDirection == 0)
					{
						curLinks = db.getLinkIDsFromPage(curID);
					}
					else
					{
						curLinks = db.getLinkIDsToPage(curID);
					}
					for(int i=0; i < curLinks.size(); ++i)
					{
						pagesTouched++;
						int nextLink = curLinks.get(i);
						if (! linkMap.containsKey(nextLink))
						{
							linkMap.put(nextLink, new Integer[]{nextLink,curID,curDirection});
							pageQueue.add(new Integer[]{nextLink,curDepth+1,curDirection});
						}
						else if ((linkMap.get(nextLink))[2] == curDirection)
						{
						}
						else
						{
							found = true;
							Integer[] endpoint = linkMap.get(nextLink);
							int direction = endpoint[2];
							int curParent = endpoint[1];
							finalPath.add(nextLink);
							if (direction == 0)
							{
								while (curParent != srcPageID)
								{
									finalPath.add(0,curParent);
									Integer[] next = linkMap.get(curParent);
									curParent = next[1];
								}
								finalPath.add(0,srcPageID);

								if(curPage[0] != dstPageID)
								{
									int otherParent = linkMap.get(curPage[0])[1];
									finalPath.add(curPage[0]);
									while(otherParent != dstPageID)
									{
										finalPath.add(otherParent);
										Integer[] next = linkMap.get(otherParent);
										otherParent = next[1];
									}
								}
								finalPath.add(dstPageID);
							}
							else
							{
								while(curParent != dstPageID)
								{
									finalPath.add(curParent);
									Integer[] next = linkMap.get(curParent);
									curParent = next[1];
								}
								finalPath.add(dstPageID);
								if(curPage[0] != srcPageID)
								{
									int otherParent = linkMap.get(curPage[0])[1];
									finalPath.add(0,curPage[0]);
									while(otherParent != srcPageID)
									{
										finalPath.add(0,otherParent);
										Integer[] next = linkMap.get(otherParent);
										otherParent = next[1];
									}
								}
								finalPath.add(0,srcPageID);
							}
							break;
						}
					}
				}				
			}
		}
		if (found == true)
		{
			route.append("\n\nSuccess! A path of length " + (finalPath.size()-1) + " was found:\n\n");
			route.append(db.getTitleForID(finalPath.get(0)));
			for(int i=1;i<finalPath.size();++i)
			{
				route.append(" --> " + db.getTitleForID(finalPath.get(i)));
			}
			route.append("\n\nTime elapsed: " + (System.currentTimeMillis() - start) + " ms\n");
			route.append("Pages touched: " + pagesTouched + "\n");
			return route.toString()+"\n";
		}
		else
		{
			return "\nFailure: No path was found\n";
		}
	}
	
	public static String getPath_parallel(String srcPageName, String dstPageName)
	{
		ParallelPathFinding ppf = new ParallelPathFinding();
		return ppf.getPath_parallel(srcPageName,dstPageName);
	}
}