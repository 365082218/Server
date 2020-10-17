package idevgame.meteor.gameserver;

public enum RoomState
{
	Run,//比赛进行中
	Wait,//等待玩家进入场景
	Closing,//下一次更新，就要关闭房间了
}