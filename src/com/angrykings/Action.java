package com.angrykings;

public class Action {

	public class Server {
		public static final int LOBBY_UPDATE = 1001;
		public static final int NEW_GAME = 1002;
		public static final int EXISTING_GAME = 1003;
		public static final int TURN = 1004;
		public static final int CONFIRM = 1005;
		public static final int UNKNOWN_USER = 1006;
		public static final int YOU_WIN = 1007;
		public static final int KNOWN_USER = 1008;
		public static final int SEND_NAME = 1009;
		public static final int DRAW = 1010;
        public static final int GAMES = 1011;
	}

	public class Client {
		public static final int SET_NAME = 2001;
		public static final int GO_TO_LOBBY = 2002;
		public static final int ENTER_GAME = 2003;
		public static final int SET_ID = 2004;
		public static final int LOSE = 2005;
		public static final int GET_NAME = 2006;
		public static final int END_TURN = 2007;
		public static final int LEAVE_LOBBY = 2008;
        public static final int LEAVE_GAME = 2009;
        public static final int GET_RUNNING_GAMES = 2010;
        public static final int WIN = 2011;
	}
}
