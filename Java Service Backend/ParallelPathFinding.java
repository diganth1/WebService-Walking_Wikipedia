package wikiwalking;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.concurrent.PriorityBlockingQueue;

public class ParallelPathFinding
{

	public static Database db;

	public static void main(String[] args)
	{
		try
		{
			db = new Database();
		}catch (SQLException e){e.printStackTrace();}
	}

	public static String getPath_parallel(String srcPageName, String dstPageName)
	{
		int srcPageID = db.getIDForTitle(srcPageName);
		int dstPageID = db.getIDForTitle(dstPageName);
		boolean found = false;
		StringBuilder route = new StringBuilder("");
		long start = System.currentTimeMillis();
		int pagesTouched = 0;
		ArrayList<Integer> finalPath;

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
				finalPath = new ArrayList<Integer>();
				finalPath.add(dstPageID);
				finalPath.add(srcPageID);
				pagesTouched = firstJump.size();
				found = true;
			}
			else
			{
				Page startPage = new Page(srcPageName,srcPageID,0,0);
				Page finalPage = new Page(dstPageName,dstPageID,0,1);
				
				PageSearch ps = new PageSearch(startPage, finalPage);
				ps.search(4); //tune this
				
				found = ps.getFound();
				finalPath = ps.getFinalPath();
				pagesTouched = ps.getPagesTouched();
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
	
	private static class PageSearch{
		private PriorityBlockingQueue<Page> pageQueue = new PriorityBlockingQueue<Page>(10, new PageComparator());
		private int pagesTouched = 0;
		private Hashtable<Integer,HashEntry> linkMap = new Hashtable<Integer, HashEntry>();
		private boolean found = false;
		private ArrayList<Integer> finalPath = new ArrayList<Integer>();
		private int srcPageID;
		private int dstPageID;
		
		//getters
		public int getPagesTouched(){ return pagesTouched; }
		public boolean getFound(){ return found; }
		public ArrayList<Integer> getFinalPath(){ return finalPath; }
		
		public PageSearch(Page startPage, Page finalPage) {
			srcPageID = startPage.getPageID();
			dstPageID = finalPage.getPageID();
			
			pageQueue.add(startPage);
			pageQueue.add(finalPage);
			
			linkMap.put(srcPageID, new HashEntry(srcPageID,-1,0));
			linkMap.put(dstPageID, new HashEntry(dstPageID,-1,0));
		}
		
		public void search(int threadCount) {
			ArrayList<SearchThread> threads = new ArrayList<SearchThread>();
			//spawn threadCount threads
			for(int i=0; i<threadCount; i++){
				SearchThread cur = new SearchThread();
				cur.start();
				threads.add(cur);
			}
			//wait for each thread to finish
			for(SearchThread cur : threads) synchronized (cur){
				try {
					cur.join();
				} catch (InterruptedException e) {e.printStackTrace();
				}
			}
		}//end search
		
		private class SearchThread extends Thread{
			@Override
			public void run() {
				while(!pageQueue.isEmpty()){ //removed !found because it's checked inside the loop, stupid parallelization
					Page curPage = pageQueue.poll();
					int curID = curPage.getPageID();
					System.out.print("\nExpanding page: " + curID);
					int curDepth = curPage.getDepth();
					int curDirection = curPage.getDirection();
					ArrayList<Integer> curLinks;
					
					if(found && (finalPath.size())/2 <= curDepth+1){ //if we've found a path and we can't find a shorter one
						return;
					}
		
					if(curPage.getDirection() == 0){
						curLinks = db.getLinkIDsFromPage(curID);
					}else{
						curLinks = db.getLinkIDsToPage(curID);
					}
					
					for(int i=0; i < curLinks.size(); ++i)
					{
						pagesTouched++;
						int nextLink = curLinks.get(i);
						boolean inMap;
						synchronized (linkMap) {
							inMap = !linkMap.containsKey(nextLink);
							if(inMap){
								 //if this link isn't in the map of all the links we've visited,
								if(!(found && (finalPath.size())/2 <= curDepth+2)){ //if we've found a path and we can't find a shorter one
									linkMap.put(nextLink, new HashEntry(nextLink,curID,curDirection)); //add it to the hash
									pageQueue.add(new Page(nextLink,curDepth+1,curDirection)); //and add this page to the page queue
								}
							}
						}
						if(!inMap) //simply skip this if you did the above block
						if (linkMap.get(nextLink).getDirection() != curDirection){ //if it is in the map, and it's going in the opposite direction
							HashEntry endpoint = linkMap.get(nextLink);
							int direction = endpoint.getDirection();
							int curParent = endpoint.getParentID();
							ArrayList<Integer> foundPath = new ArrayList<Integer>();
							foundPath.add(nextLink);
							if (direction == 0)
							{
								while (curParent != srcPageID)
								{
									foundPath.add(curParent);
									HashEntry next = linkMap.get(curParent);
									curParent = next.getParentID();
								}
								foundPath.add(0,srcPageID);
		
								if(curPage.getPageID() != dstPageID)
								{
									int otherParent = linkMap.get(curPage.getPageID()).getParentID();
									foundPath.add(curPage.getPageID());
									while(otherParent != dstPageID)
									{
										foundPath.add(otherParent);
										HashEntry next = linkMap.get(otherParent);
										otherParent = next.getParentID();
									}
								}
								foundPath.add(dstPageID);
							}
							else
							{
								while(curParent != dstPageID)
								{
									foundPath.add(curParent);
									HashEntry next = linkMap.get(curParent);
									curParent = next.getParentID();
								}
								foundPath.add(dstPageID);
								if(curPage.getPageID() != srcPageID)
								{
									int otherParent = linkMap.get(curPage.getPageID()).getParentID();
									foundPath.add(0,curPage.getPageID());
									while(otherParent != srcPageID)
									{
										foundPath.add(0,curPage.getPageID());
										HashEntry next = linkMap.get(otherParent);
										otherParent = next.getParentID();
									}
								}
								foundPath.add(0,srcPageID);
							}
							
							//if we haven't found a path before, or this path is shorter, save it
							synchronized (finalPath) {
								if(!found || finalPath.size() > foundPath.size()){
									finalPath = foundPath;
								}
								found = true;
							}
							continue; //NOT BREAK! You might find a shorter one still!
						}//end if we found the end; note that if we found the same page twice going in the same direction, ignore that link, because we've been there before
					}//end for loop over curLinks
				}//end while
				
				/* NOTE: The below is written from the perspective that the
				 * start pages have depth 1, not depth 0. The code compensates
				 * for this by add 1 to page depth when checking for doneness.
				 * 
				 * OK, so at this point, one of two things happened: either
				 * someone found the path, or the queue's empty and we can't
				 * find a path. If the queue's empty, we can just quit here,
				 * since there's definitely no path between the two pages. But,
				 * if someone did find a path, it's theoretically possible that
				 * there's a shorter path than the one found. So we have to
				 * search the queue until the lowest depth is (pathLength+1)/2.
				 * Proof by example, because I'm lazy, using even and odd
				 * lengths:
				 * - If the found path length is 6, then a shorter path would
				 *   be length 5 or less. (6+1)/2 = 7/2 = 3 (int rounding). If
				 *   the queue has nothing but depth 3 and higher pages, then
				 *   it's impossible to form a path of length 5 or less, thus
				 *   proving the shortest path is what was found.
				 * - If the found path is length 5, then a shorter path would
				 *   be length 4 or less. (5+1)/2 = 3. If the queue has nothing
				 *   but depth 3 and higher pages, then it's impossible to form
				 *   a path of less than length 6, thus proving that the path
				 *   of length 5 we found is the shortest.
				 */
				
			}//end run
		}//end SearchThread
	}//end class

	private static class PageComparator implements Comparator<Page>
	{
		public int compare(Page page1, Page page2)
		{
			return page1.getDepth() - page2.getDepth();
		}
	}
}