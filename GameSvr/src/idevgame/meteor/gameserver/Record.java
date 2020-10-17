package idevgame.meteor.gameserver;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import idevgame.meteor.proto.MeteorMsgs.FrameCommand;

public class Record {
	ConcurrentMap<Integer, FrameCommand> playerCommand;
	public Record()
	{
		
	}
}
