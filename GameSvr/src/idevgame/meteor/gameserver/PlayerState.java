package idevgame.meteor.gameserver;

public enum PlayerState {
	InLobby,//大厅拉列表
	InRoom,//进入房间还未进入场景
	InLevel,//进入场景-还未开始战斗，在选择界面
	InFight,//关卡内战斗
}
