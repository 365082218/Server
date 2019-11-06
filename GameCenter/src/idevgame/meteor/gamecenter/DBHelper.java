package idevgame.meteor.gamecenter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.dbutils.QueryRunner;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import idevgame.meteor.data.BaseResultSetHandler;
import idevgame.meteor.utils.PropertiesUtil;

//ʹ��Tool/PoPrinterɨ�����ݿ�Դ��ͨ��Դ���ɶ�Ӧ�ļ̳�����BasePo��Java���ļ�����Щ�ļ����ǰ����ݿ��ϵı��Ϊ�࣬����б�Ϊ���Ա
//ʹ��AutoDao.scanע��һ������Դ��Java��Ŀ¼��ɨ������Դ�ں�Java�ļ�����Ӧ�ı��ڵ����г�Ա����ȱʧ�򱨴�.
//ÿ���඼��ͨ��PoRegister��ͬ��ɨ��Java��Ŀ¼����ÿ�������ݿ�ת������������һ�������TableObject��ÿ�����󱣴���Դ�������Դ��ͬʱ�Ѳ���sql��仺��
//ͬʱÿ���඼������һ��Dao����Dao�����ڲ�������ͬһ�����TableObject������
//Dao�����ṩ���ⲿ���в�ѯ��ͨ�����Ĺ����ӿڣ�ʵ���ڲ�����QueryRunner��query��ѡ��TableObject��Ӧ��sql����ѯ�����ز�ѯ���Ķ���һ�������б�
//�����ܵ����ݿⲿ�ּ��ɶȱȽϸߣ�����ʵ���������ݿ��Ӧ��Java������޸�=>ͨ��dao��ֱ�Ӹ��µ�����������ݿ�
public class DBHelper {
	Connection connection;
	PropertiesUtil db;
	DruidDataSource dataSource;
	public DBHelper(String properties)
	{
		db = new PropertiesUtil(properties);
		//ȡ������Դ�����б�
		//ResultSet tbRs = getConnection.getMetaData().getTables(null, null, null, null);
		
		//ȡ�����б����������
		//List<String> tbNames = new LinkedList<>();

//		while(tbRs.next()) {
//			String tName = tbRs.getString("TABLE_NAME");
//			tbNames.add(tName);
//		}
//
//		for (String tbName : tbNames) {
//
//			ResultSet rs = conn.getMetaData().getColumns(null, null, tbName, null);
//	}
//		QueryRunner qr = new QueryRunner(tableObject.ds);
		//QueryRunner qr = new QueryRunner(dataSource);
		//qr.query("", rsh);
		Init();
		try
		{
			List<String> tbNames = new LinkedList<>();
			ResultSet tbRs = getConnection().getMetaData().getTables(getConnection().getCatalog(), null, null, null);
			while(tbRs.next()) {
				String tName = tbRs.getString("TABLE_NAME");
				tbNames.add(tName);
			}
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Object query(String sql)
	{
		QueryRunner qr = new QueryRunner(dataSource);
		try {
			return qr.query(sql, new BaseResultSetHandler());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public void Init()
	{
		if (dataSource == null)
		{
			dataSource = new DruidDataSource();
			dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
			dataSource.setUrl(db.getProperty("url"));
			dataSource.setUsername(db.getProperty("user"));
			dataSource.setPassword(db.getProperty("password"));
			dataSource.setValidationQuery("SELECT 1");//������������Ƿ���Ч
	        dataSource.setTestOnBorrow(false);//��������ʱִ��validationQuery��������Ƿ���Ч������������ûή������
	        dataSource.setTestOnReturn(false);//�黹����ʱִ��validationQuery��������Ƿ���Ч������������ûή������
	        //�������ӵ�ʱ���⣬�������ʱ�����timeBetweenEvictionRunsMillis��ִ��validationQuery��������Ƿ���Ч��
	        dataSource.setTestWhileIdle(true);//������ʧ�ܣ������ӽ����ӳ���ȥ��
			dataSource.setInitialSize(10);
			dataSource.setMinIdle(1);
			dataSource.setMaxActive(20);
			dataSource.setMinEvictableIdleTimeMillis(300000);
			dataSource.setTimeBetweenEvictionRunsMillis(60000);
		}
	}
	public DruidPooledConnection getConnection() {
		DruidPooledConnection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
	}
}
