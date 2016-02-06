package utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class MappingUtility {
	private final static String TYPE = "type";
	private final static String SAMEAS = "owl:sameAs";
	private HashMap<String, String> organizationMapping;
	
	public MappingUtility(InputStream inputStream) {

		try {
			readMapping(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Iterates recursively over the whole JSON structure and inserts the mapping
	 */
	public void insertMapping(JSONObject json) {
		if (json.has(TYPE)) {
			String type = json.getString(TYPE);
			String sameAs = "";
			if (!json.isNull("permalink")) {
				String permalink = json.getString("permalink")+ "#id";
				if (type.equals("Organization")) {
					sameAs = organizationMapping.get(permalink);
				} 
			}
			if ((sameAs != null) && (!sameAs.isEmpty())) {
				json.put(SAMEAS, sameAs);
			}
		}
		
		Iterator<String> keyIterator = json.keys();
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			try {
				Object object = json.get(key);
				if (object instanceof JSONObject) {
					this.insertMapping((JSONObject)object);
				} else if (object instanceof JSONArray) {
					JSONArray array = (JSONArray) object;
					Iterator<Object> arrayIterator = array.iterator();
					while (arrayIterator.hasNext()) {
						Object jso = arrayIterator.next();
						if (jso instanceof JSONObject) {
							this.insertMapping((JSONObject)jso);
						}
					}
				}
			} catch (JSONException e) {
			}
		}
	}
	
	private void readMapping(InputStream inputStream) throws IOException {
		organizationMapping = new HashMap<>();
		InputStream fis;
		BufferedReader br;
		String line;
		int startIndex = 61; // length of "http://km.aifb.kit.edu/services/crunchbase/api/organizations/"
		br = new BufferedReader(new InputStreamReader(inputStream));
		String tmp;
		while ((line = br.readLine()) != null) {
			tmp = line.trim();
			if (!tmp.isEmpty()) {
				String[] splittedStrings = tmp.split("\\s+");
				String cbEntity = splittedStrings[0];
				String dbpEntity = splittedStrings[2];
				dbpEntity = dbpEntity.substring(1, dbpEntity.length()-1); // remove braces
				cbEntity = cbEntity.substring(1, cbEntity.length()-1);
				cbEntity = cbEntity.substring(startIndex); // remove the url
				organizationMapping.put(cbEntity, dbpEntity);
			}

		}
		br.close();
		br = null;
		fis = null;
	}
	
}
