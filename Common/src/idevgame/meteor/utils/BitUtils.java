package idevgame.meteor.utils;

/**
 * 娴ｅ秷绻嶇粻锟�
 * @author chen 2014楠烇拷9閺堬拷9閺冿拷
 *
 */
public class BitUtils {
	
	/**
	 * 閺佹壆绮嶆稉顓熺槨娑擄拷娑撶寵it閸楃姳绔存担锟�,妤犲矁鐦夌粭鐞緊s娑擃亙缍呯純顔炬畱閸婏拷,閸欘亝婀侀崣顖濆厴閺勶拷0,閹达拷1
	 * 
	 * @param data
	 * @param pos
	 * @return
	 */
	public static byte getPosState(byte[] data, int pos) {
		if (data == null) {
			return 0;
		}
		if ((data.length << 3) <= pos) {
			return 0;
		}

		byte _data = data[pos >> 3];
		int _shift = pos & 7;
		return (byte) ((_data >>> (_shift)) & 0x01);
	}

	/**
	 * data閺佹壆绮嶆稉顓熺槨娑擄拷娑撶寵it閸楃姳绔存担锟�,娴犲酣鐝担宥呭煂娴ｅ簼缍�,鐠佸墽鐤嗙粭鐞緊s娴ｅ秶鐤嗛惃鍕拷锟�.
	 * 
	 * @param data
	 * @param pos
	 * @param state
	 * @return
	 */
	public static byte setPosState(byte[] data, int pos, byte state) {
		if (data == null) {
			return -1;
		}
		if ((data.length << 3) <= pos) {
			return -1;
		}
		int _len = pos >> 3; // pos / 8 ,閸︹暊ata娑擃厾娈戞担宥囩枂
		int _bit = pos & 7; // pos % 8 閸︹暊ata[_len]娑擃厾娈戞担宥囩枂

		int _mask = (~(1 << _bit));
		data[_len] = (byte) ((data[_len] & _mask) | ((state & 1) << (_bit)));
		return 0;
	}
}
