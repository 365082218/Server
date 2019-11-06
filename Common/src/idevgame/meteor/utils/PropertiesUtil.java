package idevgame.meteor.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtil
{
	public PropertiesUtil(String file)
	{
		readProperties(file);
	}
	private Properties prop = new Properties();
	public void readProperties(String fileName)
	{
		try
		{
			InputStream in = PropertiesUtil.class.getResourceAsStream("/" + fileName);
			if (in == null)
				return;
			BufferedReader bf = new BufferedReader(new InputStreamReader(in));
			prop.load(bf);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public String getProperty(String key)
	{
		return prop.getProperty(key);
	}
}