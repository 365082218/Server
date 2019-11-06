package idevgame.meteor.utils;

import java.util.*;

/**
 * SortedSet - 鐠哄疇銆冩禒绺璭dis閻ㄥ嫬鐤勯悳锟�
 * <p/>
 * 閸欘亝鏁幐涔籧ore:Long key:Long
 * <p/>
 * 鏉╂瑩鍣烽惃锟� skiplist 鐎圭偟骞囬崪锟� William Pugh 閸︼拷 "Skip Lists: A Probabilistic
 * Alternative to Balanced Trees" 闁插本寮挎潻鎵畱瀹割喕绗夋径姘剧礉閸欘亝婀佹稉澶夐嚋閸︾増鏌熸潻娑滎攽娴滃棔鎱ㄩ弨鐧哥窗
 * <p/>
 * <ul>
 * <li>鏉╂瑤閲滅�圭偟骞囬崗浣筋啅闁插秴顦查崐锟�</li>
 * <li>娑撳秳绮庣�碉拷 score 鏉╂稖顢戝В鏂款嚠閿涘矁绻曢棁锟界憰浣割嚠 key 鏉╂稖顢戝В鏂款嚠</li>
 * <li>濮ｅ繋閲滈懞鍌滃仯闁棄鐢張澶夌娑擃亜澧犳す杈ㄥ瘹闁藉牞绱濋悽銊ょ艾娴犲氦銆冪亸鎯ф倻鐞涖劌銇旀潻顓濆敩</li>
 * </ul>
 *
 * @author moon
 * @version 2.0 - 2014-04-06
 */
public final class SortedSet {

	public static final class RecordObject {
		private long score;
		private long key;
		private int rank;

		public RecordObject(long key, long score) {
			this.key = key;
			this.score = score;
		}

		public RecordObject(long score, long key, int rank) {
			this.score = score;
			this.key = key;
			this.rank = rank;
		}

		public long getScore() {
			return score;
		}

		public long getKey() {
			return key;
		}

		public int getRank() {
			return rank;
		}

		public void setRank(int rank) {
			this.rank = rank;
		}

		@Override
		public String toString() {
			return "RecordObject{" +
					"score=" + score +
					", key=" + key +
					", rank=" + rank +
					'}';
		}
	}

	public static final class RangeSpec {
		boolean minex, maxex;
		long min, max;

		public RangeSpec(boolean minex, boolean maxex, long min, long max) {
			this.minex = minex;
			this.maxex = maxex;
			this.min = min;
			this.max = max;
		}

		/**
		 * 姒涙顓绘稉鍝勫蓟闂傤厼灏梻锟�
		 * @param min
		 * @param max
		 */
		public RangeSpec(long min, long max) {
			this.min = min;
			this.max = max;
			this.minex = false;
			this.maxex = false;
		}
	}

	private static final class SkipListLevel {
		/**
		 * 閸撳秷绻橀幐鍥嫛
		 */
		private SkipListNode forward = null;
		/**
		 * 鏉╂瑤閲滅仦鍌濇硶鐡掑﹦娈戦懞鍌滃仯閺佷即鍣�
		 */
		private int span = 0;
	}

	private static final class SkipListNode {
		/**
		 * 閸掑棗锟斤拷
		 */
		private Long score;
		/**
		 * 鐎电钖�
		 */
		private Long obj;
		/**
		 * 閸氬酣锟斤拷閹稿洭鎷�
		 */
		private SkipListNode backward = null;
		/**
		 * 鐏烇拷
		 */
		private SkipListLevel[] level;

		public SkipListNode(int level, Long score, Long obj) {
			this.level = new SkipListLevel[level];
			for (int i = 0; i < level; i++) {
				this.level[i] = new SkipListLevel();
			}
			this.score = score;
			this.obj = obj;
		}

		public RecordObject toRecordObject() {
			return new RecordObject(obj, score);
		}
	}

	private static final class SkipList {
		private SkipListNode header = null;
		private SkipListNode tail = null;
		private int length = 0;
		private int level = 1;
		private Random random = new Random();

		private SkipList() {
			this.header = new SkipListNode(ZSKIPLIST_MAXLEVEL, 0L, null);
			for (int i = 0; i < this.header.level.length; i++) {
				this.header.level[i] = new SkipListLevel();
			}
		}

		/**
		 * 闁俺绻冩径姘偧闂呭繑婧�鏉╁洨鈻�
		 *
		 * @return 娑擄拷娑擃亙绮欐禍锟� 1 閸滐拷 ZSKIPLIST_MAXLEVEL 娑斿妫块惃鍕閺堝搫锟界》绱濇担婊�璐熼懞鍌滃仯閻ㄥ嫬鐪伴弫鑸拷锟�
		 */
		private int randomLevel() {
			int level = 1;
			while ((random.nextInt() & 0xFFFF) < (ZSKIPLIST_P * 0xFFFF))
				level += 1;
			return (level < ZSKIPLIST_MAXLEVEL) ? level : ZSKIPLIST_MAXLEVEL;
		}

		/**
		 * 鐏忓棗瀵橀崥顐ょ舶鐎癸拷 score 閻ㄥ嫬顕挒锟� obj 濞ｈ濮為崚锟� skiplist 闁诧拷
		 * <p/>
		 * T_worst = O(N), T_average = O(log N)
		 */
		private SkipListNode insert(long score, long obj) {

			/**鐠佹澘缍嶇�电粯澹橀崗鍐鏉╁洨鈻兼稉顓ㄧ礉濮ｅ繐鐪伴懗钘夊煂鏉堝墽娈戦張锟介崣瀹犲Ν閻愶拷*/
			SkipListNode[] update = new SkipListNode[ZSKIPLIST_MAXLEVEL];

			/**鐠佹澘缍嶇�电粯澹橀崗鍐鏉╁洨鈻兼稉顓ㄧ礉濮ｅ繐鐪伴幍锟界捄銊ㄧШ閻ㄥ嫯濡悙瑙勬殶*/
			int[] rank = new int[ZSKIPLIST_MAXLEVEL];

			SkipListNode x = this.header;

			int i;

			// 鐠佹澘缍嶅▽鍧楋拷鏃囶問闂傤喚娈戦懞鍌滃仯閿涘苯鑻熺拋鈩冩殶 span 缁涘鐫橀幀锟�
			for (i = this.level - 1; i >= 0; i--) {
				rank[i] = i == (this.level - 1) ? 0 : rank[i + 1];

				// 閸欏疇濡悙閫涚瑝娑撹櫣鈹�
				while (x.level[i].forward != null &&
						// 閸欏疇濡悙鍦畱 score 濮ｆ梻绮扮�癸拷 score 鐏忥拷
						(x.level[i].forward.score < score ||
								// 閸欏疇濡悙鍦畱 score 閻╃鎮撻敍灞肩稻閼哄倻鍋ｉ惃锟� member 濮ｆ棁绶崗锟� member 鐟曚礁鐨�
								(x.level[i].forward.score == score && x.level[i].forward.obj < obj))) {

					// 鐠佹澘缍嶇捄銊ㄧШ娴滃棗顦跨亸鎴滈嚋閸忓啰绀�
					rank[i] += x.level[i].span;
					// 缂佈呯敾閸氭垵褰搁崜宥堢箻
					x = x.level[i].forward;
				}
				// 娣囨繂鐡ㄧ拋鍧楁６閼哄倻鍋�
				update[i] = x;
			}

			// 閸ョ姳璐熸潻娆庨嚋閸戣姤鏆熸稉宥呭讲閼宠棄顦╅悶鍡曡⒈娑擃亜鍘撶槐鐘垫畱 member 閸滐拷 score 闁晫娴夐崥宀�娈戦幆鍛枌閿涳拷
			// 閹碉拷娴犮儳娲块幒銉ュ灡瀵ょ儤鏌婇懞鍌滃仯閿涘奔绗夐悽銊︻梾閺屻儱鐡ㄩ崷銊︼拷锟�

			// 鐠侊紕鐣婚弬鎵畱闂呭繑婧�鐏炲倹鏆�
			int level = randomLevel();

			// 婵″倹鐏� level 濮ｆ柨缍嬮崜锟� skiplist 閻ㄥ嫭娓舵径褍鐪伴弫鎷岀箷鐟曚礁銇�
			// 闁絼绠為弴瀛樻煀 this.level 閸欏倹鏆�
			// 楠炴湹绗栭崚婵嗩潗閸栵拷 update 閸滐拷 rank 閸欏倹鏆熼崷銊ф祲鎼存梻娈戠仦鍌滄畱閺佺増宓�
			if (level > this.level) {
				for (i = this.level; i < level; i++) {
					rank[i] = 0;
					update[i] = this.header;
					update[i].level[i].span = this.length;
				}
				this.level = level;
			}

			// 閸掓稑缂撻弬鎷屽Ν閻愶拷
			x = new SkipListNode(level, score, obj);

			// 閺嶈宓� update 閸滐拷 rank 娑撱倓閲滈弫鎵矋閻ㄥ嫯绁弬娆欑礉閸掓繂顫愰崠鏍ㄦ煀閼哄倻鍋�
			// 楠炴儼顔曠純顔炬祲鎼存梻娈戦幐鍥嫛
			// O(N)
			for (i = 0; i < level; i++) {
				// 鐠佸墽鐤嗛幐鍥嫛
				x.level[i].forward = update[i].level[i].forward;
				update[i].level[i].forward = x;

				// 鐠佸墽鐤� span
				x.level[i].span = update[i].level[i].span - (rank[0] - rank[i]);
				update[i].level[i].span = (rank[0] - rank[i]) + 1;
			}

			// 閺囧瓨鏌婂▽鍧楋拷鏃囶問闂傤喛濡悙鍦畱 span 閸婏拷
			for (i = level; i < this.level; i++) {
				update[i].level[i].span++;
			}

			// 鐠佸墽鐤嗛崥搴拷锟介幐鍥嫛
			x.backward = (update[0] == this.header) ? null : update[0];
			// 鐠佸墽鐤� x 閻ㄥ嫬澧犳潻娑欏瘹闁斤拷
			if (x.level[0].forward != null)
				x.level[0].forward.backward = x;
			else
				// 鏉╂瑤閲滈弰顖涙煀閻ㄥ嫯銆冪亸鎹愬Ν閻愶拷
				this.tail = x;

			// 閺囧瓨鏌婄捄瀹犵┈鐞涖劏濡悙瑙勬殶闁诧拷
			this.length++;

			return x;
		}

		/**
		 * 閼哄倻鍋ｉ崚鐘绘珟閸戣姤鏆�
		 * <p/>
		 * T = O(N)
		 */
		private void deleteNode(SkipListNode x, SkipListNode[] update) {
			int i;

			// 娣囶喗鏁奸惄绋跨安閻ㄥ嫭瀵氶柦鍫濇嫲 span , O(N)
			for (i = 0; i < this.level; i++) {
				if (update[i].level[i].forward == x) {
					update[i].level[i].span += x.level[i].span - 1;
					update[i].level[i].forward = x.level[i].forward;
				} else {
					update[i].level[i].span -= 1;
				}
			}

			// 婢跺嫮鎮婄悰銊ャ仈閸滃矁銆冪亸鎹愬Ν閻愶拷
			if (x.level[0].forward != null) {
				x.level[0].forward.backward = x.backward;
			} else {
				this.tail = x.backward;
			}

			// 閺�鍓佺級 level 閻ㄥ嫬锟斤拷, O(N)
			while (this.level > 1 && this.header.level[this.level - 1].forward == null)
				this.level--;

			this.length--;
		}

		/**
		 * 娴狅拷 skiplist 娑擃厼鍨归梽銈呮嫲缂佹瑥鐣� obj 娴犮儱寮风紒娆忕暰 score 閸栧綊鍘ら惃鍕帗缁憋拷
		 * <p/>
		 * T_worst = O(N), T_average = O(log N)
		 *
		 * @return 1=success 0=not found
		 */
		private int delete(long score, long obj) {
			int i;

			SkipListNode[] update = new SkipListNode[ZSKIPLIST_MAXLEVEL];

			SkipListNode x = this.header;
			// 闁秴宸婚幍锟介張澶婄湴閿涘矁顔囪ぐ鏇炲灩闂勩倛濡悙鐟版倵闂囷拷鐟曚浇顫︽穱顔芥暭閻ㄥ嫯濡悙鐟板煂 update 閺佹壆绮�
			for (i = this.level - 1; i >= 0; i--) {
				while (x.level[i].forward != null &&
						(x.level[i].forward.score < score ||
								(x.level[i].forward.score == score &&
										x.level[i].forward.obj < obj)))
					x = x.level[i].forward;
				update[i] = x;
			}
			// 閸ョ姳璐熸径姘嚋娑撳秴鎮撻惃锟� member 閸欘垵鍏橀張澶屾祲閸氬瞼娈� score 
			// 閹碉拷娴犮儴顩︾涵顔荤箽 x 閻拷 member 閸滐拷 score 闁棄灏柊宥嗘閿涘本澧犳潻娑滎攽閸掔娀娅�
			x = x.level[0].forward;
			if (x != null && score == x.score && x.obj == obj) {
				this.deleteNode(x, update);
				return 1;
			} else {
				return 0;
			}
		}

		private static boolean keyGteMin(long key, RangeSpec spec) {
			return spec.minex ? (key > spec.min) : (key >= spec.min);
		}

		private static boolean keyLteMax(long key, RangeSpec spec) {
			return spec.maxex ? (key < spec.max) : (key <= spec.max);
		}

		/**
		 * 濡拷閺岋拷 skiplist 娑擃厾娈戦崗鍐閺勵垰鎯侀崷銊х舶鐎规俺瀵栭崶缈犵閸愶拷
		 * <p/>
		 * T = O(1)
		 */
		private boolean isInRange(RangeSpec range) {
			SkipListNode x;

			// range 娑撹櫣鈹�
			if (range.min > range.max ||
					(range.min == range.max && (range.minex || range.maxex)))
				return false;

			// 婵″倹鐏� skiplist 閻ㄥ嫭娓舵径褑濡悙鍦畱 score 濮ｆ棁瀵栭崶瀵告畱閺堬拷鐏忓繐锟借壈顩︾亸锟�
			// 闁絼绠� skiplist 娑撳秴婀懠鍐ㄦ纯娑斿鍞�
			x = this.tail;
			if (x == null || !keyGteMin(x.score, range))
				return false;

			// 婵″倹鐏� skiplist 閻ㄥ嫭娓剁亸蹇氬Ν閻愬湱娈� score 濮ｆ棁瀵栭崶瀵告畱閺堬拷婢堆冿拷鑹邦洣婢讹拷
			// 闁絼绠� skiplist 娑撳秴婀懠鍐ㄦ纯娑斿鍞�
			x = this.header.level[0].forward;
			if (x == null || !keyLteMax(x.score, range))
				return false;

			// 閸︺劏瀵栭崶鏉戝敶
			return true;
		}

		/**
		 * 閹垫儳鍩岀捄瀹犵┈鐞涖劋鑵戠粭顑跨娑擃亞顑侀崥鍫㈢舶鐎规俺瀵栭崶瀵告畱閸忓啰绀�
		 * <p/>
		 * T_worst = O(N) , T_average = O(log N)
		 */
		private SkipListNode firstInRange(RangeSpec range) {
			SkipListNode x;
			int i;

			if (!isInRange(range)) return null;

			// 閹垫儳鍩岀粭顑跨娑擄拷 score 閸婄厧銇囨禍搴ｇ舶鐎规俺瀵栭崶瀛樻付鐏忓繐锟借偐娈戦懞鍌滃仯
			// O(N)
			x = this.header;
			for (i = this.level - 1; i >= 0; i--) {
				while (x.level[i].forward != null &&
						!keyGteMin(x.level[i].forward.score, range))
					x = x.level[i].forward;
			}

			x = x.level[0].forward;

			// O(1)
			if (!keyLteMax(x.score, range)) return null;
			return x;
		}

		/**
		 * 閹垫儳鍩岀捄瀹犵┈鐞涖劋鑵戦張锟介崥搴濈娑擃亞顑侀崥鍫㈢舶鐎规俺瀵栭崶瀵告畱閸忓啰绀�
		 * <p/>
		 * T_worst = O(N) , T_average = O(log N)
		 */
		private SkipListNode lastInRange(RangeSpec range) {
			SkipListNode x;
			int i;

			if (!isInRange(range)) return null;

			// O(N)
			x = this.header;
			for (i = this.level - 1; i >= 0; i--) {
				while (x.level[i].forward != null &&
						keyLteMax(x.level[i].forward.score, range))
					x = x.level[i].forward;
			}

			if (!keyGteMin(x.score, range)) return null;
			return x;
		}


		/**
		 * 鏉╂柨娲栭惄顔界垼閸忓啰绀岄崷銊︽箒鎼村繘娉︽稉顓犳畱 rank
		 * <p/>
		 * 婵″倹鐏夐崗鍐娑撳秴鐡ㄩ崷銊ょ艾閺堝绨梿鍡礉闁絼绠炴潻鏂挎礀 0 閵嗭拷
		 * <p/>
		 * T_worst = O(N) , T_average = O(log N)
		 */
		private int getRank(long score, long obj) {
			SkipListNode x;
			int rank = 0;
			int i;

			x = this.header;
			// 闁秴宸� skiplist 閿涘苯鑻熺槐顖溞濆▽鍧楋拷鏃傛畱 span 閸掞拷 rank 閿涘本澹橀崚鎵窗閺嶅洤鍘撶槐鐘虫鏉╂柨娲� rank
			// O(N)
			for (i = this.level - 1; i >= 0; i--) {
				while (x.level[i].forward != null &&
						(x.level[i].forward.score < score ||
								(x.level[i].forward.score == score &&
										x.level[i].forward.obj <= obj))) {
					// 缁鳖垳袧
					rank += x.level[i].span;
					// 閸撳秷绻�
					x = x.level[i].forward;
				}

				// 閹垫儳鍩岄惄顔界垼閸忓啰绀�
				if (x.obj != null && x.obj == obj) {
					return rank;
				}
			}
			return 0;
		}

		/**
		 * 閺嶈宓佺紒娆忕暰閻拷 rank 閺屻儲澹橀崗鍐
		 * <p/>
		 * T = O(N)
		 */
		private SkipListNode getElementByRank(int rank) {
			SkipListNode x;
			int traversed = 0;
			int i;

			// 濞岃法娼冮幐鍥嫛閸撳秷绻橀敍宀�娲块崚鎵柈缁夘垳娈戝銉︽殶 traversed 缁涘绨� rank 娑撶儤顒�
			// O(N)
			x = this.header;
			for (i = this.level - 1; i >= 0; i--) {
				while (x.level[i].forward != null && (traversed + x.level[i].span) <= rank) {
					traversed += x.level[i].span;
					x = x.level[i].forward;
				}
				if (traversed == rank) {
					return x;
				}
			}

			// 濞屸剝澹橀崚锟�
			return null;
		}

	}


	private static final int ZSKIPLIST_MAXLEVEL = 32; /* Should be enough for 2^32 elements */
	private static final float ZSKIPLIST_P = 0.25f;

	private SkipList list = new SkipList();
	private Map<Long, Long> dict = new HashMap<>();

	/**
	 * 濞撳懐鎮婃潻娆庨嚋 SortedSet
	 */
	public void clear() {
		synchronized (this) {
			this.list = new SkipList();
			this.dict.clear();
		}
	}

	/**
	 * 閸掔娀娅庣紒娆忕暰閼煎啫娲块崘鍛畱 score 閻ㄥ嫬鍘撶槐鐘拷锟�
	 * <p/>
	 * T = O(N^2)
	 */
	private int deleteRangeByScore(RangeSpec range) {
		SkipListNode[] update = new SkipListNode[ZSKIPLIST_MAXLEVEL];
		SkipListNode x;
		int removed = 0;
		int i;

		// 鐠佹澘缍嶅▽鍧楋拷鏃傛畱閼哄倻鍋�
		// O(N)
		x = this.list.header;
		for (i = this.list.level - 1; i >= 0; i--) {
			while (x.level[i].forward != null && (range.minex ?
					x.level[i].forward.score <= range.min :
					x.level[i].forward.score < range.min))
				x = x.level[i].forward;
			update[i] = x;
		}

		x = x.level[0].forward;

		// 娑擄拷閻╂潙鎮滈崣鍐插灩闂勩倧绱濋惄鏉戝煂閸掓媽鎻� range 閻ㄥ嫬绨虫稉鐑橆剾
		// O(N^2)
		while (x != null && (range.maxex ? x.score < range.max : x.score <= range.max)) {
			// 娣囨繂鐡ㄩ崥搴ｆ埛閹稿洭鎷�
			SkipListNode next = x.level[0].forward;
			// 閸︺劏鐑︾捄鍐�冩稉顓炲灩闂勶拷, O(N)
			this.list.deleteNode(x, update);
			// 閸︺劌鐡ч崗闀愯厬閸掔娀娅庨敍瀛�(1)
			this.dict.remove(x.obj);

			removed++;

			x = next;
		}

		return removed;
	}

	/**
	 * 閸掔娀娅庣紒娆忕暰閹烘帒绨懠鍐ㄦ纯閸愬懐娈戦幍锟介張澶庡Ν閻愶拷
	 * <p/>
	 * T = O(N^2)
	 */
	private int deleteRangeByRank(int start, int end) {
		SkipListNode[] update = new SkipListNode[ZSKIPLIST_MAXLEVEL];
		SkipListNode x;
		int traversed = 0, removed = 0;
		int i;

		// 闁俺绻冪拋锛勭暬 rank 閿涘瞼些閸斻劌鍩岄崚鐘绘珟瀵拷婵娈戦崷鐗堟煙
		// O(N)
		x = this.list.header;
		for (i = this.list.level - 1; i >= 0; i--) {
			while (x.level[i].forward != null && (traversed + x.level[i].span) < start) {
				traversed += x.level[i].span;
				x = x.level[i].forward;
			}
			update[i] = x;
		}

		// 缁犳ぞ绗� start 閼哄倻鍋�
		traversed++;
		// 娴狅拷 start 瀵拷婵绱濋崚鐘绘珟閻╂潙鍩岄崚鎷屾彧缁便垹绱� end 閿涘本鍨ㄩ懓鍛汞鐏忥拷
		// O(N^2)
		x = x.level[0].forward;
		while (x != null && traversed <= end) {
			// 娣囨繂鐡ㄩ崥搴濈閼哄倻鍋ｉ惃鍕瘹闁斤拷
			SkipListNode next = x.level[0].forward;
			// 閸掔娀娅� skiplist 閼哄倻鍋�, O(N)
			this.list.deleteNode(x, update);
			// 閸掔娀娅� dict 閼哄倻鍋�, O(1)
			this.dict.remove(x.obj);

			removed++;
			traversed++;
			x = next;
		}
		return removed;
	}

	private String debugString() {
		StringBuilder sb = new StringBuilder();
		for (int i = this.list.level - 1; i >= 0; i--) {
			SkipListNode node = this.list.header;
			sb.append("level ").append(i).append(":");
			while (node.level[i].forward != null) {
				node = node.level[i].forward;
				sb.append("[k=").append(node.obj).append(":v=").append(node.score).append("]");
			}
			sb.append("\n");
		}

		return sb.toString();
	}



	/*-----------------------------------------------------------------------------
	 * sorted set API
	 *----------------------------------------------------------------------------*/

	public int size() {
		return this.dict.size();
	}

	/**
	 *
	 * @param key
	 * @return null if not found
	 */
	public Long getScore(long key){
		return this.dict.get(key);
	}

	/**
	 * 濞ｈ濮為敍灞肩窗閼奉亜濮╅崥鍫濊嫙闁插秴顦查惃鍒眅y
	 *
	 * @param score
	 * @param key
	 */
	public void add(long score, long key) {
		synchronized (this) {
			if (this.dict.containsKey(key)) {
				this.list.delete(this.dict.get(key), key);
			}
			this.dict.put(key, score);
			this.list.insert(score, key);
		}
	}

	/**
	 * 閹靛綊鍣哄ǎ璇插閿涘奔绱伴懛顏勫З閸氬牆鑻熼柌宥咁槻閻ㄥ埍ey
	 *
	 * @param recordObjects RecordObject::rank娑撳秳绱伴懛顏勫З閺囧瓨鏌�
	 */
	public void addAdll(RecordObject[] recordObjects) {
		synchronized (this) {
			for (RecordObject recordObject : recordObjects) {
				long score = recordObject.getScore();
				long key = recordObject.getKey();
				if (this.dict.containsKey(key)) {
					this.list.delete(this.dict.get(key), key);
				}
				this.dict.put(key, score);
				this.list.insert(score, key);
			}
		}
	}

	/**
	 * 閹靛綊鍣哄ǎ璇插閿涘奔绱伴懛顏勫З閸氬牆鑻熼柌宥咁槻閻ㄥ埍ey
	 *
	 * @param recordObjects RecordObject::rank娑撳秳绱伴懛顏勫З閺囧瓨鏌�
	 */
	public void addAdll(Collection<RecordObject> recordObjects) {
		synchronized (this) {
			for (RecordObject recordObject : recordObjects) {
				long score = recordObject.getScore();
				long key = recordObject.getKey();
				if (this.dict.containsKey(key)) {
					this.list.delete(score, key);
				}
				this.dict.put(key, score);
				this.list.insert(score, key);
			}
		}
	}

	/**
	 * 閸掔娀娅庢稉锟芥稉顏勶拷锟�
	 *
	 * @param key
	 */
	public void remove(long key) {
		synchronized (this) {
			if (this.dict.containsKey(key)) {
				Long score = this.dict.remove(key);
				this.list.delete(score, key);
			}
		}
	}

	/**
	 * 闁俺绻冮崚鍡樻殶閼煎啫娲块崚鐘绘珟
	 */
	public void removeByScore(RangeSpec range) {
		synchronized (this) {
			this.deleteRangeByScore(range);
		}
	}

	/**
	 * 闁俺绻冮崚鍡樻殶閼煎啫娲块崚鐘绘珟
	 */
	public void removeByRank(int start, int end, boolean reverse) {

		synchronized (this) {
			if(reverse) {
				int size = this.size();
				this.deleteRangeByRank(size + 1 - end, size + 1 - start);
			} else {
				this.deleteRangeByRank(start, end);
			}
		}
	}

	/**
	 * 閼惧嘲褰囬幒鎺戞倳
	 *
	 * @param key
	 * @param reverse true=娴犲骸銇囬崚鏉跨毈 false=娴犲骸鐨崚鏉裤亣
	 * @return
	 */
	public int rank(long key, boolean reverse) {
		synchronized (this) {
			if (this.dict.containsKey(key)) {
				Long score = this.dict.get(key);
				return reverse
						? this.dict.size() + 1 - this.list.getRank(score, key)
						: this.list.getRank(score, key);
			}
			return -1;
		}
	}

	/**
	 * 閼惧嘲绶遍幒鎺戞倳缁楃憞閻ㄥ嫬顕挒锟�
	 *
	 * @param rank
	 * @param reverse true=娴犲骸銇囬崚鏉跨毈 false=娴犲骸鐨崚鏉裤亣
	 * @return
	 */
	public RecordObject getByRank(int rank, boolean reverse) {
		int realRank = reverse ? this.dict.size() + 1 - rank : rank;

		synchronized (this) {

			SkipListNode res = this.list.getElementByRank(realRank);
			if (res == null || res.obj == null) {
				return null;
			}
			RecordObject recordObject = res.toRecordObject();
			recordObject.setRank(rank);

			return recordObject;
		}
	}

	/**
	 * 闁俺绻冮幒鎺戞倳閼惧嘲褰囨稉锟藉▓锟�
	 *
	 * @param rankBegin 娴ｅ孩甯撻崥锟�
	 * @param rankEnd   妤傛ɑ甯撻崥锟�
	 * @param reverse   true=娴犲骸銇囬崚鏉跨毈 false=娴犲骸鐨崚鏉裤亣
	 * @return
	 */
	public List<RecordObject> getRangeByRank(int rankBegin, int rankEnd, boolean reverse) {
		if (rankBegin > rankEnd) {
			return null;
		}
		int size = this.dict.size();

		rankBegin = rankBegin < 1 ? 1 : rankBegin;
		rankEnd = rankEnd > size ? size : rankEnd;

		int realRankBegin = reverse ? size + 1 - rankEnd : rankBegin;

		int i = 0;
		int r = rankEnd - rankBegin;

		int rank = realRankBegin;

		List<RecordObject> ls = new LinkedList<>();

		synchronized (this) {
			SkipListNode node = this.list.getElementByRank(realRankBegin);
			if (node == null || node.obj == null) {
				return ls;
			}
			RecordObject ro = node.toRecordObject();
			ro.setRank(reverse ? size + 1 - rank : rank);
			ls.add(ro);

			while (i++ < r && node.level[0].forward != null) {
				node = node.level[0].forward;
				ro = node.toRecordObject();
				rank++;
				ro.setRank(reverse ? size + 1 - rank : rank);
				ls.add(ro);
			}

		}

		if(reverse) {
			Collections.reverse(ls);
		}

		return ls;
	}

	/**
	 * 闁俺绻冮崚鍡樻殶閼惧嘲褰囨稉锟藉▓锟� 閸欏矂妫撮崠娲？
	 *
	 * @param scoreBegin 娴ｅ骸鍨�
	 * @param scoreEnd   妤傛ê鍨�
	 * @param reverse   true=娴犲骸銇囬崚鏉跨毈 false=娴犲骸鐨崚鏉裤亣
	 * @return
	 */
	public List<RecordObject> getRangeByScore(int scoreBegin, int scoreEnd, boolean reverse) {
		if (scoreBegin > scoreEnd) {
			return null;
		}

		int size = this.dict.size();

		List<RecordObject> ls = new LinkedList<>();

		synchronized (this) {
			SkipListNode node = this.list.firstInRange(new RangeSpec(scoreBegin, scoreEnd));
			if (node == null || node.obj == null) {
				return ls;
			}

			int r = this.list.getRank(node.score, node.obj);

			RecordObject ro = node.toRecordObject();
			ro.setRank(reverse ? size + 1 - r : r);
			ls.add(ro);

			while (node.level[0].forward != null && node.level[0].forward.score <= scoreEnd) {
				node = node.level[0].forward;
				ro = node.toRecordObject();
				r++;
				ro.setRank(reverse ? size + 1 - r : r);
				ls.add(ro);
			}

		}

		if(reverse) {
			Collections.reverse(ls);
		}

		return ls;
	}

}
