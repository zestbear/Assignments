package view;


public class AppConstant {

	public final static String ModeChanged = "Mode Changed";

	public final static String EndNodeChanged = "End Node Changed";
	public final static String NodeOpenStateChanged = "Node Open State Changed";
	public final static String NodeVisitedStateChange = "Node Visited State Changed";
	public final static String NodeSelectedStateChanged = "Node Selected State Changed";
	public final static String NodePredecessorChanged = "Node Predecessor Changed";
	public final static String NodeCostChanged = "Node Cost Changed";

	public final static String StartRequested = "Start Requested";
	public final static String StopRequested = "Stop Requested";
	
	public final static String SearchStarted = "Search Started";
	public final static String SearchCompleted = "Search Completed";

	public final static String ClearMapRequested = "Clear Map Requested";
	public final static String GenerateMapRequested = "Generate Map Requested";
	public final static String MapEdited = "Map Edited";
	public final static String CellSizeChanged = "Cell Size Changed";

	public static enum Painter {

		DESTINATION   ("도착점", 		"Select a cell as the destination for path search");
		

		String name;
		String description;

		Painter(String name, String description) {
			this.name = name;
			this.description = description;
		}

		public static Painter getValue(String name) {
			for (Painter p : values()) {
				if (p.name.equals(name)) {
					return p;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return this.name;
		}

	}

	public static enum Mode {

		MAP_EDITING_MODE("맵 수정 모드"),
		PATH_SEARCH_MODE("길 찾기 모드"),
		MAP_SETTING_MODE("맵 세팅 모드");

		String name;

		Mode(String name) {
			this.name = name;
		}

		public static Mode getValue(String name) {
			for (Mode m : values()) {
				if (m.name.equals(name)) {
					return m;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return this.name;
		}

	}

}
