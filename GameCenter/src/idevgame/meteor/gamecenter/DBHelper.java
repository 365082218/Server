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

//使用Tool/PoPrinter扫描数据库源，通过源生成对应的继承来自BasePo的Java类文件，这些文件就是把数据库上的表变为类，表的列变为类成员
//使用AutoDao.scan注册一个数据源和Java类目录，扫描数据源内和Java文件名对应的表内的所有成员，若缺失则报错.
//每个类都会通过PoRegister，同样扫描Java类目录，对每个由数据库转化来的类生成一个表对象TableObject，每个对象保存了源类和数据源，同时把部分sql语句缓存
//同时每个类都会生成一个Dao对象，Dao对象内部保存了同一个类的TableObject的引用
//Dao对象提供给外部进行查询，通过他的公共接口，实际内部调用QueryRunner的query，选择TableObject对应的sql语句查询，返回查询到的对象（一个或者列表）
//这个框架的数据库部分集成度比较高，基本实现了与数据库对应的Java类对象修改=>通过dao层直接更新到缓存或者数据库
public class DBHelper {
	Connection connection;
	PropertiesUtil db;
	DruidDataSource dataSource;
	public DBHelper(String properties)
	{
		db = new PropertiesUtil(properties);
		//取得数据源内所有表
		//ResultSet tbRs = getConnection.getMetaData().getTables(null, null, null, null);
		
		//取得所有表的所有数据
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
			dataSource.setValidationQuery("SELECT 1");//用来检测连接是否有效
	        dataSource.setTestOnBorrow(false);//申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
	        dataSource.setTestOnReturn(false);//归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
	        //申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
	        dataSource.setTestWhileIdle(true);//如果检测失败，则连接将被从池中去除
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
