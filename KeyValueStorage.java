
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KeyValueStorage {
	private static String storagePath = null;
	public static KeyValueStorage obj = null;
	public String getStoragePath() {
		return storagePath;
	}
	private KeyValueStorage() {
		
	}
	public static synchronized KeyValueStorage getInstance(String path) {
		if(obj==null) {
			if(validatePath(path)) {  
				storagePath = path;
				System.out.println("The given Storage-Path recorded Successfully");
			}
			else {
				storagePath = setDefaultPath();
				System.out.println("The given path is invalid...Default Path is used");
			}
		}
		if(obj!=null) {
			System.out.println("A Stoarge path is already being used by this client! Failed to set the new path for this client");
			System.out.println("For reference : Current storage path for this client : "+storagePath);
		}
		File f = new File(storagePath+"/MyJsonStorage.txt");
		if(f.exists())
			deadKeyCleaner();
		if(obj == null)
			obj = new KeyValueStorage();
		return obj;
	}
	public static synchronized KeyValueStorage getInstance() {
		if(obj==null){
			storagePath = setDefaultPath();
			System.out.println("Storage-path is set as default as no path is given by you!");
		}
		if(obj!=null) {
			System.out.println("A Stoarge path is already being used by this client! Failed to set deafult path for this client");
			System.out.println("Current storage path for this client : "+storagePath);
		}
		File f = new File(storagePath+"/MyJsonStorage.txt");
		if(f.exists())
			deadKeyCleaner();
		if(obj == null)
			obj = new KeyValueStorage();
		return obj;
	}
	public synchronized void create(String key,JSONObject value) {
		try {
			if(key==null) {
				throw new NullKeyException();
			}
			if((key.length())>32) {                                             
				throw new OversizedKeyException();
			}
			if ((value.toString().length())>16384){
					throw new OversizedJSONObjectException();
			}
			File f  = new File(storagePath+"/MyJsonStorage.txt");
			if(f.exists()) {
				BufferedReader br = null;
				try {
					br= new BufferedReader(new FileReader(storagePath+"/MyJsonStorage.txt"));
					String lineA;
					while((lineA = br.readLine())!=null) {
						JSONObject joTemp = new JSONObject(lineA);
						if(joTemp.has(key)) {
							throw new DuplicateKeyException();
						}
					}
					if(f.length()>=1073741824) {
						throw new FileSizeOverException();
					}
				}
				finally {
					if(br!=null)
						br.close();
				}
			}
			JSONArray jaTemp = new JSONArray();
			JSONObject joInsert = new JSONObject();
			jaTemp.put(value);
			long timeToLive = Integer.MAX_VALUE*(long)1000;
			jaTemp.put(timeToLive);
			Date crDate = new Date();
			long creationTimeStamp = crDate.getTime();
			jaTemp.put(creationTimeStamp);
			joInsert.put(key,jaTemp);
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(storagePath+"/MyJsonStorage.txt",true));
				bw.write(joInsert.toString());
				bw.newLine();
				System.out.println("The given Key -> "+key+"   and corresponding value(JSONObject) stored successfully!");
			}
			finally {
				if(bw!=null)
					bw.close();
			}
		}
		catch (IOException e) {
			System.out.println("Sorry! Something Wrong happened while doing file operations ");
		} 
		catch(DuplicateKeyException e) {
			System.out.println("Operation failed...Key -> "+key+" already exists!");
		} 
	   	catch (OversizedJSONObjectException e) {
			System.out.println("Creation failed..given value(JSON Object) size crossed maximum allowed size 16 KB");
		}
		catch (OversizedKeyException e) {
			System.out.println("Creation failed...Maximun 32 characters are allowed for key");
		}
		catch(FileSizeOverException e) {
			System.out.println("The file reached maximum allowed size 1GB");
		}
		catch(NullKeyException e){
			System.out.println("Error : \"null\" is not a allowed Key");
		}
		catch(JSONException e) {
			System.out.println("Something Wrong happened while doing JSON Operations");
		}
	}
	public synchronized void create(String key,JSONObject value,int timeToLive) {
		try {
			if(key==null) {
				throw new NullKeyException();
			}
			if((key.length())>32) {
				throw new OversizedKeyException();
			}
			if ((value.toString().length())>16384){
				throw new OversizedJSONObjectException();
			}
			if(validateTimeToLive(timeToLive)==false) {
				throw new WrongTimeToLiveInputException();
			}
			File f2 = new File(storagePath+"/MyJsonStorage.txt");
			if(f2.exists()) {
				BufferedReader br2 = null;
				try {
					br2 = new BufferedReader(new FileReader(storagePath+"/MyJsonStorage.txt"));
					String lineB;
					while((lineB = br2.readLine())!=null) {
						JSONObject joTemp2 = new JSONObject(lineB);
						if(joTemp2.has(key)) {
							throw new DuplicateKeyException();
						}
					}
					if(f2.length()>=1073741824) {
						throw new FileSizeOverException();
					}
				}
				finally {
					if(br2!=null)
						br2.close();
				}
			}
			JSONArray jaTemp2 = new JSONArray();
			JSONObject joInsert2 = new JSONObject();
			jaTemp2.put(value);
			long tTL = timeToLive*(long)1000;
			jaTemp2.put(tTL);
			Date crDate = new Date();
			long creationTimeStamp = crDate.getTime();
			jaTemp2.put(creationTimeStamp);
			joInsert2.put(key,jaTemp2);
			BufferedWriter bw2 = null;
			try {
				bw2 = new BufferedWriter(new FileWriter(storagePath+"/MyJsonStorage.txt",true));
				bw2.write(joInsert2.toString());
				bw2.newLine();
				System.out.println("The given Key -> "+key+"   and corresponding value(JSONObject) stored successfully!");
			}
			finally {
				if(bw2!=null)
					bw2.close();
			}
		}
		catch (IOException e) {
			System.out.println("Sorry! Something Wrong happened while doing file operations ");
		} 
		catch(DuplicateKeyException e) {
			System.out.println("Operation failed...Key "+key+" already exists!");
		} 
	   	catch (OversizedJSONObjectException e) {
			System.out.println("Creation failed..given value(JSON Object) size crossed 16 KB");
		}
		catch (OversizedKeyException e) {
			System.out.println("Creation failed...Maximun 32 characters are allowed for key");
		}
		catch(FileSizeOverException e) {
			System.out.println("The File reached maximum allowed Size 1GB");
		}
		catch (NullKeyException e) {
			System.out.println("Error : \"null\" is not a allowed Key");
		}
		catch(JSONException e) {
			System.out.println("Something wrong happened while doing JSON Operations");
		}
		catch (WrongTimeToLiveInputException e) {
			System.out.println("Wrong input given for Time-To-Live(Value should be positive Integer)");
		}
	}
	public synchronized void read(String key) {
		BufferedReader br = null;
		try {
			boolean b = false;
			br = new BufferedReader(new FileReader(storagePath+"/MyJsonStorage.txt"));
			String lineC;
			while((lineC = br.readLine())!=null) {
				JSONObject joRead = new JSONObject(lineC);
				if(joRead.has(key)) {
					b = true;
					JSONArray jaTemp3 = new JSONArray();
					jaTemp3 = joRead.getJSONArray(key);
					long tTL = jaTemp3.getLong(1);
					long crTime = jaTemp3.getLong(2);
					Date cd = new Date();
					long currentMS = cd.getTime();
					if((tTL+crTime)<=currentMS)
						throw new LifeOverException();
					else
						System.out.println("Corresponding JSON value for key "+key+" "+joRead );
					break;
					}
			}
			if(b==false) {
				throw new KeyNotFoundException();
			}
		}
		catch(LifeOverException e) {
			delete(key);
			System.out.println("The key-> "+key+" is dead! Time to live for the key is over");
		}
		catch (JSONException e) {
			System.out.println("Something wrong happened while doing JSON operations");
		} 
		catch (IOException e) {
			System.out.println("Sorry! Something wrong happened while doing file operations ");
		} 
		catch (KeyNotFoundException e) {
			System.out.println("Sorry! The given key-> "+key+" doesn't exists");
		}
		finally {
			if(br!=null)
				try {
					br.close();
				}
				catch (IOException e) {
					System.out.println("Sorry! Something wrong happened while doing file operations");
				}
		}
	}
	public synchronized void delete(String key) {
		try {
			boolean b2 = false;
			BufferedReader br4 = null;
			JSONArray jaTemp4 = new JSONArray();
			try {
				br4 = new BufferedReader(new FileReader(storagePath+"/MyJsonStorage.txt"));
				String lineD;
				while((lineD = br4.readLine())!=null) {
					JSONObject joDelete = new JSONObject(lineD);
					if(joDelete.has(key))	
						b2 = true;
					if((joDelete.has(key)==false)) {
						jaTemp4.put(joDelete);
					}
				}
				if(b2 == false) {
					throw new KeyNotFoundException();
				}
			}
			finally {
				if(br4!=null)
					br4.close();
			}
			BufferedWriter bw3 =null;
			try {
				bw3 = new BufferedWriter(new FileWriter(storagePath+"/MyJsonStorage.txt"));
				for(int i = 0;i<jaTemp4.length();i++) {
					JSONObject temp = jaTemp4.getJSONObject(i);
					String str =  temp.toString();
					bw3.write(str);
					bw3.newLine();
				}
				System.out.println("The key -> "+key+" and corresponding value deleted successfully");
			}
			finally {
				if(bw3!=null)
					bw3.close();
			}
		}
		catch(IOException e) {
			System.out.println("Sorry! Something wrong happened while doing file opeartions ");
		}
		catch (JSONException e) {
			System.out.println("Sorry! Something wrong happened while doing JSON opeartions");
		}
		catch(KeyNotFoundException e) {
			System.out.println("The Given Key "+key+" doesn't exists");
		}
	}
	private synchronized static void deadKeyCleaner() {
		try {
			String lineE;
			List<String> ls = new LinkedList<String>();
			BufferedReader br5 = null;
			BufferedWriter bw4 = null;
			JSONArray temp = new JSONArray();
			try {
				br5  = new BufferedReader(new FileReader(storagePath+"/MyJsonStorage.txt"));
				while((lineE = br5.readLine())!=null) {
					String key = null;
					JSONObject jo = new JSONObject(lineE);
					Set<String> s = jo.keySet();
					for(String str : s) {
						key = str;
						break;
					}
					JSONArray jaTemp5 = new JSONArray();
					jaTemp5 = jo.getJSONArray(key);
					long tTL = jaTemp5.getLong(1);
					long crTime = jaTemp5.getLong(2);
					Date cd = new Date();
					long currentMS = cd.getTime();
					if((tTL+crTime)<=currentMS)
						ls.add(key);
					else
						temp.put(jo);
				}
			}
			finally {
				if(br5!=null)
					br5.close();
			}
			try {
				bw4 = new BufferedWriter(new FileWriter(storagePath+"/MyJsonStorage.txt"));
				for(int i = 0;i<temp.length();i++) {
					JSONObject joTemp = temp.getJSONObject(i);
					String str =  joTemp.toString();
					bw4.write(str);
					bw4.newLine();
				}
			}
			finally {
				if(bw4!=null)
					bw4.close();
			}
			if(ls.size()>0) {
				System.out.println("NOTE : Following keys have been cleaned as Time-To-Live is over for them");
				System.out.println("----->  "+ls);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
			System.out.println("Sorry! Something wrong happened while doing file operations");
		}
		catch (JSONException e) {
			System.out.println("Sorry! Something wrong happened while doing JSON operations ");
		}
	}
	private static String setDefaultPath() {
		File f = new File("freshWA01");
		String p = f.getAbsolutePath();
		String sp = "\\\\freshWA01";
		return p.split(sp)[0];
	}
	private static boolean validatePath(String path) {
		File f;
		f= new File(path);
		if(f.exists())
			return true;
		else 
			return false;
	}
	private static boolean validateTimeToLive(int timeToLive) {
		if(timeToLive>0)
			return true;
		else
			return false;
	}
}
@SuppressWarnings("serial")
class OversizedKeyException extends Exception {

}
@SuppressWarnings("serial")
class OversizedJSONObjectException extends Exception {

}
@SuppressWarnings("serial")
class DuplicateKeyException extends Exception {
	
}
@SuppressWarnings("serial")
class LifeOverException extends Exception{
	
}
@SuppressWarnings("serial")
class KeyNotFoundException extends Exception{
}
@SuppressWarnings("serial")
class FileSizeOverException extends Exception{
	
}
@SuppressWarnings("serial")
class NullKeyException extends Exception{
	
}
@SuppressWarnings("serial")
class WrongTimeToLiveInputException extends Exception{
	
}