package nl.visi.interaction_framework.editor.v14;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import nl.visi.interaction_framework.editor.ui.RotatingButton;
import nl.visi.schemas._20140331.MessageInTransactionTypeType;
import nl.visi.schemas._20140331.RoleTypeType;
import nl.visi.schemas._20140331.TransactionTypeType;

@SuppressWarnings("serial")
public class Canvas14 extends JPanel {
	private static final Color LIGHT_GREEN_1 = new Color(63, 175, 70);
	private static final Color LIGHT_RED_1 = new Color(255, 56, 56);
	private static final Color LIGHT_RED_3 = new Color(255, 166, 166);
	private static final Color LIGHT_BLUE_3 = new Color(180, 199, 220);
	private static final Color LIGHT_YELLOW_4 = new Color(255, 255, 215);
	private static final Color LIGHT_GOLD_4 = new Color(255, 233, 148);
	private static final int MESSAGE_LINE_HEIGHT = 24;
	private final TransactionsPanelControl14 transactionPanel;
	TransactionTypeType selectedTransaction;
	private Role executor, initiator;
	private List<Message> historyBefore, historyAfter, selectedNext, selectedPrev, selectedRequest, selectedResponse;
	private Message selectedMessage;
	private Dimension preferredSize;
	private Graphics2D g2d;
	private int leftMargin, rightMargin, middleMargin, topMargin, initiatorFlow, executorFlow;
	private boolean printMode;

	class ResizeListener extends ComponentAdapter {

		public void componentResized(ComponentEvent e) {
			setDimensions();
		}
	}

	private class Role {

		private RoleTypeType role;
		private int x;
		private int y;
		private Color color;
		private RotatingButton activeLabel;

		public Role(RoleTypeType role, int x, int y, Color color) {
			this.role = role;
			this.x = x;
			this.y = y;
			this.color = color;
			activeLabel = new RotatingButton();
			activeLabel.setContentAreaFilled(false);
			activeLabel.setBackground(color);
			activeLabel.setBorderPainted(false);
			activeLabel.setFont(getFont().deriveFont(getFont().getSize() - 2.0f));
			activeLabel.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					e.getComponent().setForeground(Color.RED);
					Canvas14.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					e.getComponent().setForeground(Color.BLACK);
					Canvas14.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			});
			Canvas14.this.add(activeLabel);
		}

		void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			String label = role != null ? role.getDescription() : "?";
			activeLabel.setText(label);
			activeLabel.setToolTipText(role != null ? role.getId() : "?");
			int stringWidth = g2.getFontMetrics().stringWidth(label);
			if (printMode) {
				Paint paint = g2.getPaint();
				g2.setPaint(color);
				g2.fillRect(x, y, 100, 50);
				g2.setPaint(paint);
				g2.drawRect(x, y, 100, 50);
				g2.setFont(getFont().deriveFont(getFont().getSize() - 2.0f));
				g2.drawString(label, x + 50 - (stringWidth / 2), y + 25);
			} else {
				activeLabel.setPreferredSize(new Dimension(150, 50));
				activeLabel.setLocation(x + 25 - (stringWidth / 2) - 10, y + 25 - 10);
			}
			Stroke saveStroke = g2.getStroke();
			float dash[] = { 5.0f };
			g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
			g2.drawLine(x + 50, y + 50, x + 50, getHeight() - 10);
			g2.setStroke(saveStroke);
		}
	}

	private enum MessageState {
		History, Previous, Next, Selected;
	}

	private class Message {
		private MessageState state;
		private final MessageInTransactionTypeType mitt;
		private RotatingButton activeLabel;
		private int x;
		private int y;

		public Message(MessageInTransactionTypeType mitt) {
			this(mitt, 0, 0);
		}

		public Message(MessageInTransactionTypeType mitt, int x, int y) {
			System.out.println(Control14.getMessage(mitt).getDescription());
			this.mitt = mitt;
			this.x = x;
			this.y = y;
			activeLabel = new RotatingButton();
			activeLabel.setContentAreaFilled(false);
			activeLabel.setBackground(LIGHT_YELLOW_4);
			activeLabel.setMargin(new Insets(0, 4, 0, 4));
			activeLabel.setBorderPainted(true);
			activeLabel.setFont(getFont().deriveFont(getFont().getSize() - 2.0f));
			activeLabel.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					Message.this.setState(MessageState.Selected);
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					e.getComponent().setForeground(Color.RED);
					Canvas14.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					e.getComponent().setForeground(Color.BLACK);
					Canvas14.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			});
			Canvas14.this.add(activeLabel);
		}

		MessageState getState() {
			return state;
		}

		void setState(MessageState state) {
			switch (state) {
			case History:
				this.state = state;
				activeLabel.setBackground(Color.WHITE);
				break;
			case Next:
				this.state = state;
				activeLabel.setBackground(LIGHT_YELLOW_4);
				break;
			case Previous:
				this.state = state;
				activeLabel.setBackground(LIGHT_BLUE_3);
				break;
			case Selected:
				MessageState previousState = this.state;
				if (previousState.equals(MessageState.Next)) {
					TransactionTypeType transaction = Control14.getTransaction(mitt);
					if (transaction.getId().equals(selectedTransaction.getId())) {
						for (Message msg : historyAfter) {
							Canvas14.this.remove(msg.activeLabel);
						}
						historyAfter.clear();
					} else {
						int index = transactionPanel.elementsTableModel.elements.indexOf(transaction);
						transactionPanel.tbl_Elements.getSelectionModel().setSelectionInterval(index, index);
						selectedTransaction = transaction;
						initNewDiagram();
					}
				} else if (previousState.equals(MessageState.Previous)) {
					for (Message msg : historyBefore) {
						Canvas14.this.remove(msg.activeLabel);
					}
					historyBefore.clear();
				}
				if (selectedMessage != null) {
					selectedMessage.setState(MessageState.History);
					if (selectedMessage.isIn(historyBefore)) {
						selectedMessage.move(historyBefore, historyAfter);
					} else if (selectedMessage.isIn(historyAfter)) {
						selectedMessage.move(historyAfter, historyBefore);
					}
					historyBefore.add(selectedMessage);
				}
				if (isIn(historyBefore)) {
					move(historyBefore, historyAfter);
					historyBefore.remove(this);
				} else if (isIn(historyAfter)) {
					move(historyAfter, historyBefore);
					historyAfter.remove(this);
				}
				this.state = state;

				for (Message msg : selectedPrev) {
					Canvas14.this.remove(msg.activeLabel);
				}
				selectedPrev.clear();
				for (Message msg : selectedNext) {
					Canvas14.this.remove(msg.activeLabel);
				}
				selectedNext.clear();
				for (Message msg : selectedRequest) {
					Canvas14.this.remove(msg.activeLabel);
				}
				selectedRequest.clear();
				for (Message msg : selectedResponse) {
					Canvas14.this.remove(msg.activeLabel);
				}
				selectedResponse.clear();

				selectedMessage = this;
				if (previousState.equals(MessageState.Previous) || previousState.equals(MessageState.Next)) {
					Canvas14.this.add(selectedMessage.activeLabel);
				}
				List<MessageInTransactionTypeType> prevList = Control14.getPrevious(mitt);
				if (prevList != null) {
					for (MessageInTransactionTypeType prev : prevList) {
						Message prevMessage = new Message(prev);
						prevMessage.setState(MessageState.Previous);
						if (Control14.getTransaction(prev).getId().equals(selectedTransaction.getId())) {
							selectedPrev.add(prevMessage);
						} else {
							selectedResponse.add(prevMessage);
						}
					}
				}
				List<MessageInTransactionTypeType> nextList = Control14.getNext(mitt);
				if (nextList != null) {
					for (MessageInTransactionTypeType next : nextList) {
						Message nextMessage = new Message(next);
						nextMessage.setState(MessageState.Next);
						if (Control14.getTransaction(next).getId().equals(selectedTransaction.getId())) {
							selectedNext.add(nextMessage);
						} else {
							selectedRequest.add(nextMessage);
						}
					}
				}
				activeLabel.setBackground(LIGHT_GOLD_4);
				break;
			default:
				break;
			}
		}

		private void move(List<Message> from, List<Message> to) {
			int indexOfThis = from.indexOf(this);
			if (indexOfThis < from.size() - 1) {
				List<Message> toBeMoved = new ArrayList<>();
				for (int i = from.size() - 1; i > indexOfThis; i--) {
					toBeMoved.add(from.get(i));
				}
				for (Message message : toBeMoved) {
					to.add(0, message);
					from.remove(message);
				}
			}
		}

		private boolean isStartMessage() {
			return transactionPanel.startMitt.contains(mitt);
		}

		private boolean isEndMessage() {
			if (!Control14.getTransaction(mitt).getId().equals(selectedTransaction.getId())) {
				return false;
			}
			List<MessageInTransactionTypeType> nextMitts = Control14.getNext(mitt);
			if (nextMitts == null) {
				return true;
			}
			for (MessageInTransactionTypeType next : nextMitts) {
				if (Control14.getTransaction(next).getId().equals(selectedTransaction.getId())) {
					return false;
				}
			}
			return true;
		}

		void paint(int y) {
			String label = mitt != null ? Control14.getMessage(mitt).getDescription() : "?";
			activeLabel.setText(label);
			activeLabel.setToolTipText(mitt != null ? Control14.getMessage(mitt).getId() : "?");
			if (isStartMessage()) {
				activeLabel.setBorder(BorderFactory.createLineBorder(LIGHT_GREEN_1, 2));
			}
			if (isEndMessage()) {
				activeLabel.setBorder(BorderFactory.createLineBorder(LIGHT_RED_1, 2));
			}
			TransactionTypeType transaction = Control14.getTransaction(mitt);
			int stringWidth = g2d.getFontMetrics().stringWidth(label);
			if (printMode) {
				Paint paint = g2d.getPaint();
				g2d.setPaint(LIGHT_YELLOW_4);
				g2d.fillRect(x, y, 100, 25);
				g2d.setPaint(paint);
				g2d.drawRect(x, y, 100, 25);
				g2d.setFont(getFont().deriveFont(getFont().getSize() - 2.0f));
				g2d.drawString(label, x + 50 - (stringWidth / 2), y + 25);
			} else {
				switch (state) {
				case History:
					if (mitt.isInitiatorToExecutor()) {
						activeLabel.setLocation(initiatorFlow + 50, y);
					} else {
						activeLabel.setLocation(executorFlow - activeLabel.getWidth() - 50, y);
					}
					break;
				case Next:
					if (transaction.getId().equals(selectedTransaction.getId())) {
						if (mitt.isInitiatorToExecutor()) {
							activeLabel.setLocation(initiatorFlow + 50, y);
						} else {
							activeLabel.setLocation(executorFlow - activeLabel.getWidth() - 50, y);
						}
					} else {
						if (mitt.isInitiatorToExecutor()) {
							activeLabel.setLocation(initiatorFlow - activeLabel.getWidth() - 50, y);
						} else {
							activeLabel.setLocation(executorFlow + 50, y);
						}
					}
					break;
				case Previous:
					if (transaction.getId().equals(selectedTransaction.getId())) {
						if (mitt.isInitiatorToExecutor()) {
							activeLabel.setLocation(executorFlow - activeLabel.getWidth() - 50, y);
						} else {
							activeLabel.setLocation(initiatorFlow + 50, y);
						}
					} else {
						if (mitt.isInitiatorToExecutor()) {
							activeLabel.setLocation(executorFlow + 50, y);
						} else {
							activeLabel.setLocation(initiatorFlow - activeLabel.getWidth() - 50, y);
						}
					}
					break;
				case Selected:
					if (mitt.isInitiatorToExecutor()) {
						activeLabel.setLocation(initiatorFlow + 50, y);
					} else {
						activeLabel.setLocation(executorFlow - activeLabel.getWidth() - 50, y);
					}
					break;
				default:
					break;
				}
			}
			if (state.equals(MessageState.Next)) {
				if (transaction.getId().equals(selectedTransaction.getId())) {
					if (mitt.isInitiatorToExecutor()) {
						g2d.drawLine(initiatorFlow + 10, y + 8, initiatorFlow + 50, y + 8);
						drawArrowPoint(initiatorFlow + 50, y + 8, 10, 0);
					} else {
						g2d.drawLine(executorFlow - 10, y + 8, executorFlow - 50, y + 8);
						drawArrowPoint(executorFlow - 50, y + 8, 10, 180);
					}
				} else {
					if (mitt.isInitiatorToExecutor()) {
						g2d.drawLine(initiatorFlow - 10, y + 8, initiatorFlow - 50, y + 8);
						drawArrowPoint(initiatorFlow - 50, y + 8, 10, 180);
					} else {
						g2d.drawLine(executorFlow + 10, y + 8, executorFlow + 50, y + 8);
						drawArrowPoint(executorFlow + 50, y + 8, 10, 0);
					}
				}
			} else if (state.equals(MessageState.Previous)) {
				if (transaction.getId().equals(selectedTransaction.getId())) {
					if (mitt.isInitiatorToExecutor()) {
						g2d.drawLine(executorFlow - 50, y + 8, executorFlow - 10, y + 8);
						drawArrowPoint(executorFlow - 10, y + 8, 10, 0);
					} else {
						g2d.drawLine(initiatorFlow + 50, y + 8, initiatorFlow + 10, y + 8);
						drawArrowPoint(initiatorFlow + 10, y + 8, 10, 180);
					}
				} else {
					if (mitt.isInitiatorToExecutor()) {
						g2d.drawLine(executorFlow + 10, y + 8, executorFlow + 50, y + 8);
						drawArrowPoint(executorFlow + 10, y + 8, 10, 180);
					} else {
						g2d.drawLine(initiatorFlow - 50, y + 8, initiatorFlow - 10, y + 8);
						drawArrowPoint(initiatorFlow - 10, y + 8, 10, 0);
					}
				}
			} else {
				if (mitt.isInitiatorToExecutor()) {
					g2d.drawLine(initiatorFlow + 10, y + 8, executorFlow - 10, y + 8);
					drawArrowPoint(executorFlow - 10, y + 8, 10, 0);
				} else {
					g2d.drawLine(executorFlow - 10, y + 8, initiatorFlow + 10, y + 8);
					drawArrowPoint(initiatorFlow + 10, y + 8, 10, 180);
				}
			}
		}

		public boolean isIn(List<Message> searchList) {
			for (Message historyMessage : searchList) {
				if (historyMessage.mitt.getId().equals(mitt.getId())) {
					return true;
				}
			}
			return false;
		}
	}

	public Canvas14(TransactionsPanelControl14 transactionPanel) {
		addComponentListener(new ResizeListener());
		this.transactionPanel = transactionPanel;
		this.printMode = false;
		setLayout(null);
		setBackground(Color.WHITE);
		setDimensions();

		historyAfter = new ArrayList<>();
		historyBefore = new ArrayList<>();
		selectedNext = new ArrayList<>();
		selectedPrev = new ArrayList<>();
		selectedRequest = new ArrayList<>();
		selectedResponse = new ArrayList<>();

	}

	public Canvas14(TransactionsPanelControl14 transactionPanel, boolean printMode) {
		this(transactionPanel);
		this.selectedTransaction = transactionPanel.selectedElement;
		this.printMode = printMode;
	}

	@Override
	public Dimension getPreferredSize() {
		return preferredSize;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2d = (Graphics2D) g;

		boolean newDiagram = transactionPanel.selectedElement != null
				&& !transactionPanel.selectedElement.equals(selectedTransaction);
		if (newDiagram) {
			selectedTransaction = transactionPanel.selectedElement;
			initNewDiagram();
		}

		drawTitle();
		if (initiator != null) {
			initiator.paint(g2d);
		}

		if (executor != null) {
			executor.paint(g2d);
		}

		if (newDiagram) {
			if (selectedMessage == null) {
				List<MessageInTransactionTypeType> startMitt = transactionPanel.startMitt;
				if (startMitt != null) {
					for (MessageInTransactionTypeType start : startMitt) {
						Message message = new Message(start);
						message.setState(MessageState.Next);
						selectedNext.add(message);
					}
				}
			}
		}

		int y = topMargin;
		int lastY = topMargin;
		Message lastMessage = null;
		if (selectedMessage != null) {
			for (Message message : historyBefore) {
				message.paint(y);
				y += MESSAGE_LINE_HEIGHT;
			}
			lastY = drawConnectorBoxes(historyBefore, lastMessage, lastY);
			lastMessage = !historyBefore.isEmpty() ? historyBefore.get(historyBefore.size() - 1) : lastMessage;

			int saveY = y;
			List<Message> actualSelectedPrev = new ArrayList<>();
			for (Message message : selectedPrev) {
				if (!message.isIn(historyBefore)) {
					actualSelectedPrev.add(message);
					message.paint(y);
					y += MESSAGE_LINE_HEIGHT;
				}
			}
			lastMessage = !actualSelectedPrev.isEmpty() ? actualSelectedPrev.get(actualSelectedPrev.size() - 1)
					: lastMessage;
			lastY = drawConnectorBoxes(actualSelectedPrev, lastMessage, lastY);

			if (!selectedResponse.isEmpty()) {
				y = saveY;
//				y += MESSAGE_LINE_HEIGHT;
				for (Message message : selectedResponse) {
					message.paint(y);
					y += MESSAGE_LINE_HEIGHT;
				}
			}
			// lastY = drawConnectorBoxes(selectedResponse, lastMessage, lastY);
			lastY = drawConnectorBoxes(selectedResponse, lastMessage,
					y - (!selectedResponse.isEmpty() ? selectedResponse.size() + 1 : 0) * MESSAGE_LINE_HEIGHT);
			y = lastY;
			selectedMessage.paint(y);
			lastY = drawConnectorBoxes(null, lastMessage, lastY);
			saveY = y;
			for (Message message : selectedRequest) {
				message.paint(y);
				y += MESSAGE_LINE_HEIGHT;
			}
			y = saveY;
			y += MESSAGE_LINE_HEIGHT;
			List<Message> actualSelectedNext = new ArrayList<>();
			for (Message message : selectedNext) {
				if (!message.isIn(historyAfter)) {
					actualSelectedNext.add(message);
					message.paint(y);
					y += MESSAGE_LINE_HEIGHT;
				}
			}
			lastMessage = selectedMessage != null ? selectedMessage : lastMessage;
			lastY = drawConnectorBoxes(actualSelectedNext, lastMessage, lastY);
			for (Message message : historyAfter) {
				message.paint(y);
				y += MESSAGE_LINE_HEIGHT;
			}
		} else {
			for (Message message : selectedNext) {
				message.paint(y);
			}
			drawConnectorBox(initiatorFlow, y, selectedNext.size(), BoxType.CLOSED);
		}

		if (y > getHeight()) {
			preferredSize = new Dimension(getWidth(), y);
		} else {
			preferredSize = new Dimension(getWidth(), getHeight());

		}
		setSize(getPreferredSize());
	}

	private int drawConnectorBoxes(List<Message> messageList, Message lastMsg, int lastY) {
		if (messageList == null) {
			drawConnectorBox(getStartX(selectedMessage), lastY, 1, lastMsg == null ? BoxType.CLOSED : BoxType.OPEN_TOP);
			drawConnectorBox(getEndX(selectedMessage), lastY, 1, BoxType.OPEN_BOTTOM);
			lastY += MESSAGE_LINE_HEIGHT;
		} else if (!messageList.isEmpty()) {
			for (int index = 0; index < messageList.size(); index++) {
				Message prevMsg = index > 0 ? messageList.get(index - 1) : lastMsg;
				Message currMsg = messageList.get(index);
				Message nextMsg = index < messageList.size() - 1 ? messageList.get(index + 1) : null;

				if (prevMsg == null) {
					switch (currMsg.getState()) {
					case History:
						drawConnectorBox(getStartX(currMsg), lastY, 1, BoxType.CLOSED);
						drawConnectorBox(getEndX(currMsg), lastY, 1,
								nextMsg == null ? BoxType.CLOSED : BoxType.OPEN_BOTTOM);
						break;
					case Next:
						if (Control14.getTransaction(currMsg.mitt).getId().equals(selectedTransaction.getId())) {
							drawConnectorBox(getStartX(currMsg), lastY, 1,
									nextMsg == null ? BoxType.CLOSED : BoxType.OPEN_BOTTOM);
						} else {
							drawConnectorBox(getEndX(currMsg), lastY, 1, BoxType.OPEN_TOP_BOTTOM);
						}
						break;
					case Previous:
						if (Control14.getTransaction(currMsg.mitt).getId().equals(selectedTransaction.getId())) {
							drawConnectorBox(getEndX(currMsg), lastY, 1,
									nextMsg == null ? BoxType.CLOSED : BoxType.OPEN_BOTTOM);

						} else {
							drawConnectorBox(getEndX(currMsg), lastY, 1, BoxType.OPEN_TOP_BOTTOM);
						}
						break;
					default:
						break;
					}
				} else {
					switch (currMsg.getState()) {
					case History:
						drawConnectorBox(getStartX(currMsg), lastY, 1, BoxType.OPEN_TOP);
						drawConnectorBox(getEndX(currMsg), lastY, 1,
								nextMsg == null ? BoxType.CLOSED : BoxType.OPEN_BOTTOM);
						break;
					case Next:
						drawConnectorBox(getStartX(currMsg), lastY, 1,
								nextMsg == null ? BoxType.OPEN_TOP : BoxType.OPEN_TOP_BOTTOM);
						break;
					case Previous:
						drawConnectorBox(getEndX(currMsg), lastY, 1,
								nextMsg == null ? BoxType.OPEN_TOP : BoxType.OPEN_TOP_BOTTOM);
						break;
					default:
						break;
					}
				}
				lastY += MESSAGE_LINE_HEIGHT;
			}
		}
		return lastY;
	}

	private int getEndX(Message currMsg) {
		return currMsg.mitt.isInitiatorToExecutor() ? executorFlow : initiatorFlow;
	}

	private int getStartX(Message currMsg) {
		return currMsg.mitt.isInitiatorToExecutor() ? initiatorFlow : executorFlow;
	}

	private enum BoxType {
		CLOSED, OPEN_TOP, OPEN_BOTTOM, OPEN_TOP_BOTTOM;
	};

	private void drawConnectorBox(int x, int y, int size, BoxType type) {
		if (size > 0) {
			int x1 = x - 10;
			int x2 = x1 + 20;
			int y1 = y - 2;
			int y2 = y1 + size * 20;
			switch (type) {
			case CLOSED:
				g2d.clearRect(x1, y1, 20, size * 20);
				g2d.drawRect(x1, y1, 20, size * 20);
				break;
			case OPEN_BOTTOM:
				g2d.clearRect(x1, y1, 20, size * 20);
				g2d.drawLine(x1, y1, x2, y1); // top
				g2d.drawLine(x1, y1, x1, y2); // left
				g2d.drawLine(x2, y1, x2, y2); // right
				break;
			case OPEN_TOP:
				g2d.clearRect(x1, y1 - 4, 20, size * 20 + 4);
				g2d.drawLine(x1, y1 - 4, x1, y2); // left
				g2d.drawLine(x2, y1 - 4, x2, y2); // right
				g2d.drawLine(x1, y2, x2, y2); // bottom
				break;
			case OPEN_TOP_BOTTOM:
				g2d.clearRect(x1, y1 - 4, 20, size * 20 + 4);
				g2d.drawLine(x1, y1 - 4, x1, y2); // left
				g2d.drawLine(x2, y1 - 4, x2, y2); // right
				break;
			default:
				break;
			}
		}
	}

	private void initNewDiagram() {
		Canvas14.this.removeAll();
		initiator = new Role(Control14.getInitiator(selectedTransaction), leftMargin - 50, 25, LIGHT_RED_3);
		executor = new Role(Control14.getExecutor(selectedTransaction), leftMargin + middleMargin, 25, LIGHT_BLUE_3);
		historyAfter.clear();
		historyBefore.clear();
		selectedNext.clear();
		selectedPrev.clear();
		selectedRequest.clear();
		selectedResponse.clear();
		selectedMessage = null;
		setDimensions();
	}

	private void drawTitle() {
		TransactionTypeType selectedElement = transactionPanel.selectedElement;
		String title = selectedElement != null ? selectedElement.getDescription() : "?";
		if (title == null || title.length() == 0) {
			title = selectedElement != null ? selectedElement.getId() : "?";
		}
		int titleWidth = g2d.getFontMetrics().stringWidth(title);
		g2d.drawString(title, (getWidth() - titleWidth) / 2, 18);
	}

	private void drawArrowPoint(int x, int y, int size, int rot) {
		int[] xPoints = new int[3];
		int[] yPoints = new int[3];
		if (rot == 0) {
			xPoints[0] = x - size;
			yPoints[0] = y - size / 2;
			xPoints[1] = x;
			yPoints[1] = y;
			xPoints[2] = x - size;
			yPoints[2] = y + size / 2;
		} else if (rot == 180) {
			xPoints[0] = x + size;
			yPoints[0] = y - size / 2;
			xPoints[1] = x;
			yPoints[1] = y;
			xPoints[2] = x + size;
			yPoints[2] = y + size / 2;
		}
		g2d.fillPolygon(xPoints, yPoints, 3);
	}

	void setDimensions() {
		preferredSize = new Dimension(getWidth(), getHeight());
		setSize(getPreferredSize());
		int width = getWidth() / 3;
		middleMargin = width > 300 ? width : 300;
		leftMargin = (getWidth() - middleMargin) / 2;
		rightMargin = (getWidth() - middleMargin) / 2;
		topMargin = 100;
		initiatorFlow = leftMargin;
		executorFlow = leftMargin + middleMargin;

		if (initiator != null) {
			initiator.x = initiatorFlow - 50;
		}
		if (executor != null) {
			executor.x = executorFlow - 50;
		}
	}

}
