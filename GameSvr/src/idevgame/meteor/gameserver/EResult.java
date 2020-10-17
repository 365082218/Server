package idevgame.meteor.gameserver;

public class EResult {
	public static final int PlayerInRoom = 30;//玩家正在房间中
	public static final int RoomMaxed = 29;//服务器无法创建更多的房间
	public static final int Succeed = 1;//成功
	public static final int RoomInvalid = 28;//房间不存在
	public static final int PlayerMax = 27;//房间人数已满
	public static final int PasswordError = 26;//密码不匹配
	public static final int ModelMiss = 25;//缺少模型
	public static final int Skicked = 24;//被限制
	public static final int VersionInvalid = 23;//流星版本不一致
	public static final int CampMax = 22;//阵营人数已满
	public static final int Timeup = 21;//当前轮次即将结束
}
