import org.json.JSONObject;

public class TestCases {
	public static void main(String[] args) {
		KeyValueStorage kvs = KeyValueStorage.getInstance();
		System.out.println("=> Trying to delete a key when no entries is there in the file");
		System.out.println();
		kvs.delete("Key1");
		System.out.println("=> Trying to read a key when no entries is there in the file");
		kvs.read("Key2");
		System.out.println("=> Printing default file location without giving a path");
		System.out.println("The File Location is :"+kvs.getStoragePath());
		JSONObject jo1 = new JSONObject("{\"Device Type\":\"SmartPhone\",\"Brand\":\"Samsung\",\"Model\":\"Galaxy S10\",\"Price\":40000}");
		System.out.println("=> Adding a key value pair with unique key \"Electronic Device\" without giving Time-To-Live");
		kvs.create("Electronic Device",jo1);
		JSONObject jo2  = new JSONObject();
		jo2.put("Genre", "news");
		jo2.put("Channel no",100);
		jo2.put("isAvailable", true);
		System.out.println("=> Adding a key value pair with unique key \"Telivision Channel\" without giving Time-To-Live");
		kvs.create("Telivision Channel",jo2);
		JSONObject jo3 =new JSONObject("{\"employees\":[  {\"name\":\"Shyam\",\"email\":\"shyamjaiswal@gmail.com\"}, "
				+ "{\"name\":\"Bob\",\"email\":\"bob32@gmail.com\"},"
				+ "{\"name\":\"Jai\",\"email\":\"jai87@gmail.com\"}]}");
		System.out.println("=> Adding a key value pair with unique key \"ABC\" without giving Time-To-Live");
		kvs.create("ABC",jo3);
		System.out.println("=> Adding a key value pair with unique key \"null\" without giving Time-To-Live");
		kvs.create(null,jo1);
		System.out.println("=> Adding a key value pair with existing key \"Telivision Channel\" without giving Time-To-Live");
		kvs.create("Telivision Channel",jo1);
		System.out.println("=> Adding a key value pair with unique key \"MyKey\" with Time-To-Live = 10 seconds");
		kvs.create("MyKey",jo2,15);
		System.out.println("=> Adding a key value pair with unique key with more than 32 characters");
		kvs.create("=> Test key for finding if more than 32 characters are allowed as key",jo3);
		kvs.create("TimeToLiveCheck",jo2,-10);
		System.out.println("=> Adding a key value pair with unique key \"Lets test\" with Time-To-Live = 20 seconds");
		kvs.create("Lets test",jo2,20);
		System.out.println("=> Deleting a key value pair with existing key \"Telivision Channel\"");
		kvs.delete("Telivision Channel");
		System.out.println("=> Deleting a key value pair with a key \"NewKey\" which doest not exists");
		kvs.delete("NewKey");
		System.out.println("=> Reading a key value pair with existing key \"Electronic Device\"");
		kvs.read("Electronic Device");
		System.out.println("=> Reading a key value pair with a key \"NewKey2\" which doest not exists");
		kvs.read("NewKey2");
		System.out.println("=> Reading a key value pair with a key \"Telivision Channel\" which has been deleted already");
		kvs.read("Telivision Channel");
		}
}
