package idevgame.meteor.data;

import java.io.Serializable;

/**
 * @author moon
 */
public abstract class BasePo implements Serializable {

	/**
	 * id鐨勫垪锛屽彲鐢ㄤ簬璋冩暣椤哄簭鍒嗚〃
	 * @return
	 */
	abstract public String[] ids();

	/**
	 * 鎵�鏈夊睘鎬у垪
	 * @return
	 */
	abstract public String[] props();

	/**
	 * 鎵�鏈夊睘鎬у垪鐨勫��
	 * @return
	 */
	abstract public Object[] propValues();

	/**
	 * 鎵�鏈塱d鍒楃殑鍊�
	 * @return
	 */
	abstract public Object[] idValues();

	private Object attachment;

	/**
	 * 鑾峰緱闄勪欢
	 * @return
	 */
	public Object getAttachment() {
		return attachment;
	}

	/**
	 * 璁惧畾闄勪欢
	 * @param attachment
	 */
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

}
