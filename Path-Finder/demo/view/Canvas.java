package view;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;

import model.XMatrix;
import model.XNode;
import util.MathUtil;

@SuppressWarnings("serial")
public class Canvas extends JComponent {

	private final static Color LINE_COLOR = new Color(0, 0, 0);
	private final static Color START_NODE_COLOR = new Color(0, 0, 255);
	private final static Color END_NODE_COLOR = new Color(255, 0, 0);
	private final static Color DISABLED_NODE_COLOR = new Color(0, 0, 0);
	private final static Color VISITED_NODE_COLOR = new Color(100, 100, 100);
	private final static Color SELECTED_NODE_COLOR = new Color(0, 255, 120);

	private final static int MARGIN = 10;

	private Parameters parameters;
	private XMatrix matrix;

	private volatile boolean editable = true;

	private int left = 0;
	private int top = 0;
	private int right = 0;
	private int bottom = 0;

	private Point cursorPoint;

	public Canvas() {
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		addComponentListener(componentListener);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		try {
			paintMatrix(g);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		paintPainterMarkedArea(g);
	}

	protected void paintPainterMarkedArea(Graphics g) {
		switch (parameters.getMode()) {
		case MAP_EDITING_MODE:
			break;
		default:
			break;
		}
	}
	
	public Dimension calculateDimension(int width, int height, int cellSize) {
		int rows = (height - MARGIN*2)/cellSize;
		int cols = (width - MARGIN*2)/cellSize;
		return new Dimension(rows, cols);
	}
	
	public void updateMatrixIfDimensionChanged() {
		Dimension dimension = calculateDimension(getWidth(), getHeight(), parameters.getCellSize());
		if (dimension.getWidth() != matrix.getRows() || dimension.getHeight() != matrix.getColumns()) {
			updateMatrix();
		}
	}

	public void updateMatrix() {
		int panelWidth = getWidth();
		int panelHeight = getHeight();
		int cellsize = parameters.getCellSize();
		int rows = (panelHeight - MARGIN*2)/cellsize;
		int cols = (panelWidth - MARGIN*2)/cellsize;
		left = (panelWidth - cellsize*cols)/2;
		top = (panelHeight - cellsize*rows)/2;
		right = left + cellsize*cols;
		bottom = top + cellsize*rows;
		matrix.setDimension(rows, cols);
		matrix.setStart(null);
		matrix.setEnd(null);
		repaint();
	}

	protected void paintMatrix(Graphics g) throws IOException {
		int rows = matrix.getRows();
		int cols = matrix.getColumns();
		int cellsize = parameters.getCellSize();
		

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				XNode node = matrix.getValue(row, col);
				int x = left + cellsize*col;
				int y = top + cellsize*row;
				
				if (node == matrix.getStart()) {
					g.setColor(START_NODE_COLOR);
				}
				else if (node == matrix.getEnd()) {
					g.setColor(END_NODE_COLOR);
				}
				else if (!node.isEnabled()) {
					g.setColor(DISABLED_NODE_COLOR);
				}
				else if (node.isSelected()) {
					g.setColor(SELECTED_NODE_COLOR);
				}
				else if (node.isVisited()) {
					g.setColor(VISITED_NODE_COLOR);
				}
				else {
					continue;
				}
				g.fillRect(x, y, cellsize, cellsize);
			}
		}

		g.setColor(LINE_COLOR);
		for (int row = 0; row <= rows; row++) {
			int y = top + cellsize*row;
			g.drawLine(left, y, right, y);
		}
		for (int col = 0; col <= cols; col++) {
			int x = left + cellsize*col;
			g.drawLine(x, top, x, bottom);
		}
	}

	ComponentAdapter componentListener = new ComponentAdapter() {

		@Override
		public void componentResized(ComponentEvent aEvent) {
			updateMatrixIfDimensionChanged();
		}

	};

	XNode toNode(int x, int y) {
		int cellsize = parameters.getCellSize();
		if (x > left && x < right && y > top && y < bottom) {
			int row = (y - top)/cellsize;
			int col = (x - left)/cellsize;
			return matrix.getValue(row, col);
		}
		return null;
	}

	void setEnabled(List<Point> points, boolean enabled) {
		int cellsize = parameters.getCellSize();
		int rows = matrix.getRows();
		int cols = matrix.getColumns();
		double step = cellsize*0.3;

		double x1, y1, x2, y2;
		x1 = points.get(0).getX();
		y1 = points.get(0).getY();
		for (int i = 1; i < points.size(); i++) {
			x2 = points.get(i).getX();
			y2 = points.get(i).getY();

			double angle = MathUtil.direction(x1, y1, x2, y2);
			double dist  = MathUtil.distance(x1, y1, x2, y2);
			int steps = (int)(dist/step + 0.5);

			for (int j = 0; j < steps; j++) {
				double disp[];
				if (j < steps - 1) {
					disp = MathUtil.coordinate(x1, y1, angle, step*j);
				}
				else {
					disp = new double[2];
					disp[0] = x2;
					disp[1] = y2;
				}
				int row = (int)((disp[1]-top)/cellsize);
				int col = (int)((disp[0]-left)/cellsize);
				if (row >= 0 && row < rows && col >= 0	&& col < cols) {
					XNode node = matrix.getValue(row, col);
					node.setEnabled(enabled);
				}
			}

			x1 = x2;
			y1 = y2;
		}
	}

	void setEnabled(int x, int y, int width, int height, boolean enabled) {
		int cellsize = parameters.getCellSize();
		int rows = matrix.getRows();
		int cols = matrix.getColumns();

		int row1 = y/cellsize;
		int col1 = x/cellsize;
		int row2 = (y + height)/cellsize;
		int col2 = (x + width)/cellsize;

		if (row1 < 0) row1 = 0;
		if (col1 < 0) col1 = 0;
		if (row2 >= rows) row2 = rows - 1;
		if (col2 >= cols) col2 = cols - 1;

		for (int row = row1; row <= row2; row++) {
			for (int col = col1; col <= col2; col++) {
				XNode node = matrix.getValue(row, col);
				node.setEnabled(enabled);
			}
		}
	}

	MouseAdapter mouseListener = new MouseAdapter() {


		@Override
		public void mousePressed(MouseEvent aEvent) {
			if (!isEditable()) {
				return;
			}
			cursorPoint = aEvent.getPoint();
		}


		@Override
		public void mouseReleased(MouseEvent aEvent) {
			if (!isEditable()) {
				return;
			}
			cursorPoint = aEvent.getPoint();

			XNode node = toNode(cursorPoint.x, cursorPoint.y);

			switch (parameters.getMode()) {
			case PATH_SEARCH_MODE:
				if (node != null) {
					matrix.setStart(node);
					firePropertyChange(AppConstant.StartRequested, null, null);
				}
				break;

			case MAP_EDITING_MODE:
				switch (parameters.getPainter()) {
				case DESTINATION:
					if (node != null && node.isEnabled()) {
						matrix.setEnd(node);
					}
					break;
				default:
					break;
				}
				if (matrix.getStart() != null && !matrix.getStart().isEnabled()) {
					matrix.setStart(null);
				}
				if (matrix.getEnd() != null && !matrix.getEnd().isEnabled()) {
					matrix.setEnd(null);
				}
				matrix.buildGraph();
				firePropertyChange(AppConstant.MapEdited, null, null);
				break;
				
			default:
				break;
			}
			cursorPoint = null;
			repaint();
		}
	};

	public XMatrix getMatrix() {
		return matrix;
	}

	public void setMatrix(XMatrix matrix) {
		this.matrix = matrix;
	}

	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

}
