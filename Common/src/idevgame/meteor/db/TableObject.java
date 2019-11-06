package idevgame.meteor.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import idevgame.meteor.data.BasePo;
import idevgame.meteor.data.PO;

import javax.sql.DataSource;

/**
 * 鏁版嵁搴撹〃瀵硅薄
 * @author moon
 */
public class TableObject {
	private static Logger logger = LoggerFactory.getLogger(TableObject.class);

	public final Class<?> cls;
	public final DataSource ds;
	public final String tbName;
	public final String selectAll;
	public final String select;
	public final String deleteAll;
	public final String delete;
	public final String update;
	public final String insert;
	public final String replace;
	/**
	 * 鏆傛椂鍙敤鍒版暟鎹簱琛ㄧ粨鏋勫姣旓紝妫�鏌ュ悗璁剧疆null
	 */
	public String[] props;

	public TableObject(Class<? extends BasePo> cls, DataSource ds) {
		this.cls = cls;
		this.ds = ds;
		PO po = cls.getAnnotation(PO.class);
		this.tbName = po.value();
		
		BasePo ins = null;
		try {
			ins = cls.newInstance();
		} catch (Exception e) {
			logger.error("!!!", e);
		}
		this.props = ins.props();
		this.selectAll = SqlHelper.selectAll(tbName);
		this.select = SqlHelper.select(tbName, ins.ids());
		this.deleteAll = SqlHelper.deleteAll(tbName);
		this.delete = SqlHelper.delete(tbName, ins.ids());

		this.update = SqlHelper.update(tbName, ins.props(), ins.ids());
		this.insert = SqlHelper.insert(tbName, ins.props());
		this.replace =SqlHelper.replace(tbName, ins.props());
	}

}
