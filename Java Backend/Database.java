package wikiwalking;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class Database {
	private final Connection conn;
	
	public Database() throws SQLException {
		conn = DriverManager.getConnection("DATABASE URL HERE");
	}
	
	//close the connection when this is GC'd
	protected void finalize() throws Throwable {conn.close();};
	
	public boolean setPathCache(int from, int to, String path, boolean isShortest, int numPages, int namespace){
		try{
			PreparedStatement ps = conn.prepareStatement("INSERT INTO path_cache (from, to, path, isShortest, numPages, namespace) VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE path=?, isShortest=?");
			ps.setInt(1,from);
			ps.setInt(2,to);
			ps.setString(3,path);
			ps.setBoolean(4,isShortest);
			ps.setInt(5,numPages);
			ps.setInt(6,namespace);
			ps.setString(7,path);
			ps.setBoolean(8,isShortest);
			ps.execute();
			ps.close();
			return true;
		}catch(Exception e){
			return false;
		}
	}

	public String getPathCache(int from, int to){
		//returns "path;isShortest;numPages", where isShortest is 1 for true and 0 for false
		//to get the raw path string, remove the last 2 characters of the string
		try{
			String retval = null;
			PreparedStatement ps = conn.prepareStatement("SELECT path, isShortest, numPages FROM path_cache WHERE from=? AND to=?");
			ps.setInt(1,from);
			ps.setInt(2,to);
			ps.execute();
			ResultSet rs = ps.getResultSet();
			if(rs.next()) retval = rs.getString(1)+";"+(rs.getBoolean(2)?"1":"0")+";"+rs.getInt(3);
			rs.close();
			ps.close();
			return retval;
		}catch(Exception e){
			return null;
		}
	}
	
	public int getPageCountInCategory(String catName){
		int retval = -1;
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT cat_pages FROM category WHERE cat_title=?;");
			ps.setString(1, catName);
			ps.execute();
			ResultSet rs = ps.getResultSet();
			if(rs.next()) retval=rs.getInt(1);
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retval;
	}
	
	public ArrayList<Integer> getLinkIDsFromPage(int pageid){
		try {
			ArrayList<Integer> links = new ArrayList<Integer>();
			PreparedStatement ps = conn.prepareStatement("SELECT page_id FROM pagelinks LEFT JOIN page ON pl_title=page_title WHERE pl_from=?;");
			ps.setInt(1, pageid);
			ps.execute();
			ResultSet rs = ps.getResultSet();
			while(rs.next()){
				links.add(rs.getInt(1));
			}
			rs.close();
			ps.close();
			return links;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<Integer> getLinkIDsToPage(int pageid){
		try {
			ArrayList<Integer> links = new ArrayList<Integer>();
			PreparedStatement ps = conn.prepareStatement("SELECT pl_from FROM pagelinks WHERE pl_title=(SELECT page_title FROM page WHERE page_id=? AND page_title IS NOT NULL);");
			ps.setInt(1, pageid);
			ps.execute();
			ResultSet rs = ps.getResultSet();
			while(rs.next()){
				links.add(rs.getInt(1));
			}
			rs.close();
			ps.close();
			return links;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<String> getLinkTitlesFromPage(int pageid){
		try {
			ArrayList<String> links = new ArrayList<String>();
			PreparedStatement ps = conn.prepareStatement("SELECT pl_title FROM pagelinks WHERE pl_from=?;");
			ps.setInt(1, pageid);
			ps.execute();
			ResultSet rs = ps.getResultSet();
			while(rs.next()){
				links.add(rs.getString(1));
			}
			rs.close();
			ps.close();
			return links;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<String> getLinkTitlesToPage(int pageid){
		try {
			ArrayList<String> links = new ArrayList<String>();
			PreparedStatement ps = conn.prepareStatement("SELECT page_title FROM pagelinks LEFT JOIN page ON pl_from=page_id WHERE pl_title=(SELECT page_title FROM page WHERE page_id=?) AND page_title IS NOT NULL;");
			ps.setInt(1, pageid);
			ps.execute();
			ResultSet rs = ps.getResultSet();
			while(rs.next()){
				links.add(rs.getString(1));
			}
			rs.close();
			ps.close();
			return links;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getTitleForID(int pageid){
		String retval = null;
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT page_title FROM page WHERE page_id=?;");
			ps.setInt(1, pageid);
			ps.execute();
			ResultSet rs = ps.getResultSet();
			if(rs.next()) retval = rs.getString(1);
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retval;
	}
	
	public int getIDForTitle(String title){
		int retval = -1;
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT page_id FROM page WHERE page_title=?;");
			ps.setString(1, title);
			ps.execute();
			ResultSet rs = ps.getResultSet();
			if(rs.next()) retval=rs.getInt(1);
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retval;
	}
	
	public ArrayList<String> getCategoriesInPage(int id){
		try {
			ArrayList<String> links = new ArrayList<String>();
			PreparedStatement ps = conn.prepareStatement("SELECT cl_to FROM categorylinks WHERE cl_from=?;");
			ps.setInt(1, id);
			ps.execute();
			ResultSet rs = ps.getResultSet();
			while(rs.next()){
				links.add(rs.getString(1));
			}
			rs.close();
			ps.close();
			return links;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<Integer> getPagesInCategory(String categoryName){
		try {
			ArrayList<Integer> links = new ArrayList<Integer>();
			PreparedStatement ps = conn.prepareStatement("SELECT cl_from FROM categorylinks WHERE cl_to=?;");
			ps.setString(1, categoryName);
			ps.execute();
			ResultSet rs = ps.getResultSet();
			while(rs.next()){
				links.add(rs.getInt(1));
			}
			rs.close();
			ps.close();
			return links;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		try {
			new Database();
		} catch (SQLException e) {e.printStackTrace();
		}
	}
}