package nl.visi.interaction_framework.editor.v14;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.visi.interaction_framework.editor.DateField;
import nl.visi.interaction_framework.editor.DocumentAdapter;
import nl.visi.interaction_framework.editor.InteractionFrameworkEditor;
import nl.visi.interaction_framework.editor.ui.RotatingButton;
import nl.visi.schemas._20140331.ElementType;
import nl.visi.schemas._20140331.MessageInTransactionTypeType;
import nl.visi.schemas._20140331.MessageTypeType;
import nl.visi.schemas._20140331.RoleTypeType;
import nl.visi.schemas._20140331.TransactionTypeType;

public class RolesPanelControl14 extends PanelControl14<RoleTypeType> {
	private static final String ROLES_PANEL = "nl/visi/interaction_framework/editor/swixml/RolesPanel16.xml";

	private JTabbedPane relationsTabs;
	private JPanel startDatePanel, endDatePanel, canvas;
	private JTable tbl_Transactions;

	JTable tbl_Messages;

	private JTable tbl_Conditions;
	private JTextField tfd_ResponsibilityScope, tfd_ResponsibilityTask, tfd_ResponsibilitySupportTask,
			tfd_ResponsibilityFeedback;
	private JButton btn_RemoveCondition, btn_AddCondition;
	private TransactionsTableModel transactionsTableModel;
	private MessagesTableModel messagesTableModel;
	private ConditionsTableModel conditionsTableModel;
	private JScrollPane scrollPane;
	private Canvas drawingPlane;

	@SuppressWarnings("serial")
	public class Canvas extends JPanel {
		private Dimension preferredSize;
		private final List<Transaction> transactions;
		private Map<String, Transaction> transactionMap = new HashMap<>();
		private Map<String, MessageItem> messageItemMap = new HashMap<>();
		private final List<MessageItem> messages;
		private RoleTypeType currentRole;
		private int offsetLeft = 220;
		private int yInitStart = 200;
		private int yHeight = 0;
		private int lineHeight = 15;
		private int startLine = -1;
		private int linesPerPage = -1;

		private class Transaction {
			private int x, y;
			private TransactionTypeType transactionType;
			RotatingButton activeLabel;
			// private int routeCount = 0;

			class Interval {
				public int lowerBound;
				public int upperBound;

				public Interval(int lowerBound, int upperBound) {
					this.lowerBound = lowerBound;
					this.upperBound = upperBound;
				}
			}

			private List<List<Interval>> routes = new ArrayList<>();

			public int addInterval(int lowerBound, int upperBound) {
				int index = 0;
				boolean found = true;
				for (List<Interval> level : routes) {
					for (Interval interval : level) {
						if (lowerBound >= interval.lowerBound && lowerBound <= interval.upperBound) {
							found = false;
							break;
						}
						if (upperBound >= interval.lowerBound && upperBound <= interval.upperBound) {
							found = false;
							break;
						}
						if (lowerBound <= interval.lowerBound && upperBound >= interval.upperBound) {
							found = false;
							break;
						}
						if (lowerBound >= interval.lowerBound && upperBound <= interval.upperBound) {
							found = false;
							break;
						}
					}
					if (found) {
						level.add(new Interval(lowerBound, upperBound));
						return index;
					}
					found = true;
					index++;
				}
				ArrayList<Interval> intervalList = new ArrayList<Interval>();
				intervalList.add(new Interval(lowerBound, upperBound));
				routes.add(intervalList);
				return routes.size() - 1;
			}

			Transaction(final Canvas canvas, Graphics g, final TransactionTypeType transactionType, int x, int y) {
				this.transactionType = transactionType;
				this.x = x;
				this.y = y;
				String label = transactionType.getDescription();
				Font font = new Font("Dialog", Font.PLAIN, 11);
				int stringWidth = g.getFontMetrics(font).stringWidth(label);
				if (stringWidth > yInitStart - 50) {
					while (stringWidth > yInitStart - 50) {
						label = label.substring(0, label.length() - 2);
						stringWidth = g.getFontMetrics(font).stringWidth(label);
					}
					label += "...";
				}

				activeLabel = new RotatingButton(label);
				activeLabel.setToolTipText(transactionType.getDescription());
				activeLabel.setRotation(Math.PI / 2);
				activeLabel.setContentAreaFilled(false);
				activeLabel.setBackground(Color.white);
				activeLabel.setBorderPainted(false);
				activeLabel.setFont(font);
				activeLabel.setLocation(x, y);
				activeLabel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						e.getComponent().setForeground(Color.red);
						canvas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					}

					@Override
					public void mouseExited(MouseEvent e) {
						e.getComponent().setForeground(Color.black);
						canvas.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}

					@Override
					public void mouseClicked(MouseEvent e) {
						InteractionFrameworkEditor.navigate(transactionType);
					}
				});

				Graphics2D g2d = (Graphics2D) g;
				AffineTransform old = g2d.getTransform();
				g2d.rotate(Math.toRadians(90), x, y);
				g.drawString(label, x, y);
				g2d.setTransform(old);

				canvas.add(activeLabel);
				transactionMap.put(transactionType.getId(), this);
			}

			void paint(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				String label = transactionType.getId();
				if (label != null) {
//					g2.setFont(getFont().deriveFont(getFont().getSize() - 2.0f));
//					int stringWidth = g2.getFontMetrics().stringWidth(label);
//					g2.translate((float) x, (float) y);
//					g2.rotate(Math.toRadians(-90));
//					g2.drawString(label, -stringWidth, -5);
//					g2.rotate(-Math.toRadians(-90));
//					g2.translate(-(float) x, -(float) y);

					Stroke saveStroke = g2.getStroke();
					float dash[] = { 5.0f };
					g2.setStroke(
							new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
					g2.drawLine(x, y + 25, x, getHeight() - 10);
					g2.setStroke(saveStroke);
				}
			}
		}

		private class MessageItem {
			private MessageInTransactionTypeType mitt;
			private MessageTypeType messageType;
			private TransactionTypeType transactionType;
			private RoleTypeType initiator;
			private RoleTypeType executor;
			private boolean initiatorToExecutor;
			private String label;
			private Font font;
			private Condition condition;
			private int index = 0;

			public MessageItem(final Canvas canvas, Graphics g, final MessageInTransactionTypeType mitt, int x, int y) {
				this.mitt = mitt;
				initiator = Control14.getInitiator(mitt);
				executor = Control14.getExecutor(mitt);
				this.initiatorToExecutor = mitt.isInitiatorToExecutor();
				this.messageType = Control14.getMessage(mitt);
				this.transactionType = Control14.getTransaction(mitt);
				label = this.messageType.getId();
				condition = new Condition(mitt);
				this.index = messageItemMap.size();

				// ---------------------------------------------------------------------
				String other = null;
				if (initiator.getId().equals(selectedElement.getId())) {
					other = executor.getDescription();
				} else {
					other = initiator.getDescription();
				}
				font = new Font("Dialog", Font.PLAIN, 11);
				String rolOther = shorten(g, other, font, offsetLeft - 150);
				String msglabel = shorten(g, getMessage(mitt).getDescription(), font, offsetLeft - 120);
				label = "[" + rolOther + "] " + msglabel;

				RotatingButton activeLabel = new RotatingButton(label);
				activeLabel.setToolTipText("[" + other + "] " + getMessage(mitt).getDescription());
				// activeLabel.setRotation(Math.PI / 2);
				activeLabel.setContentAreaFilled(false);
				activeLabel.setBackground(Color.white);
				activeLabel.setBorderPainted(false);
				activeLabel.setFont(font);
				activeLabel.setLocation(x, y);
				activeLabel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						e.getComponent().setForeground(Color.red);
						canvas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					}

					@Override
					public void mouseExited(MouseEvent e) {
						e.getComponent().setForeground(Color.black);
						canvas.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}

					@Override
					public void mouseClicked(MouseEvent e) {
						InteractionFrameworkEditor.navigate(getMessage(mitt));
					}
				});
//				Color saveColor = g.getColor();
//				g.setColor(Color.WHITE);
//				g.fillRect(x, y + 3, g.getFontMetrics(font).stringWidth(label), lineHeight);
//				g.setColor(saveColor);
				g.drawString(label, x, y + lineHeight);
				canvas.add(activeLabel);
				canvas.validate();
				// -------------------------------------------------

				messageItemMap.put(mitt.getId(), this);
			}

			private String shorten(Graphics g, String label, Font font, int maxLength) {
				int stringWidth = g.getFontMetrics(font).stringWidth(label);
				if (stringWidth > maxLength) {
					int leftMarker = label.length() / 2;
					int rightMarker = label.length() / 2;
					while (stringWidth > maxLength) {
						leftMarker--;
						rightMarker++;
						String text = label.substring(0, leftMarker) + label.substring(rightMarker);
						stringWidth = g.getFontMetrics(font).stringWidth(text);
					}
					label = label.substring(0, leftMarker) + "..." + label.substring(rightMarker);
				}
				return label;
			}

			public Transaction getTransaction() {
				return transactionMap.get(transactionType.getId());
			}

			public boolean isIn() {
				if (initiator != null) {
					if (selectedElement.equals(initiator)) {
						return !initiatorToExecutor;
					}
					if (executor != null) {
						if (selectedElement.equals(executor)) {
							return initiatorToExecutor;
						}
					}
				}
				return false;
			}

			@SuppressWarnings("unused")
			public boolean isOut() {
				return !isIn();
			}
		}

		public Canvas() {
			setLayout(null);
			preferredSize = new Dimension(getWidth(), getHeight());
			setSize(getPreferredSize());
			transactions = new ArrayList<RolesPanelControl14.Canvas.Transaction>();
			messages = new ArrayList<RolesPanelControl14.Canvas.MessageItem>();
		}

		public Graphics2D g2d;

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g2d = (Graphics2D) g;
			if (selectedElement == null) {
				reset(g2d);
				return;
			}

			boolean newDrawing = selectedElement != currentRole;
			reset(g2d);

			if (newDrawing) {
				int transactionsRowCount = transactionsTableModel.getRowCount();
				int transactionWidth = (getWidth() - offsetLeft) / transactionsRowCount;
				for (int index = 0; index < transactionsRowCount; index++) {
					TransactionTypeType transactionTypeType = transactionsTableModel.get(index);
					transactions.add(new Transaction(this, g2d, transactionTypeType,
							offsetLeft + transactionWidth * (index + 1) - transactionWidth / 2, 25));
				}

				for (int index = 0; index < messagesTableModel.getRowCount(); index++) {
					if (startLine == -1) {
						MessageInTransactionTypeType mitt = messagesTableModel.get(index);
						MessageTypeType messageType = getMessage(mitt);
						if (messageType != null) {
							messages.add(new MessageItem(this, g2d, mitt, 20, index * 15 + yInitStart - 12));
						}
					} else {
						if (index >= startLine && index < startLine + linesPerPage) {
							MessageInTransactionTypeType mitt = messagesTableModel.get(index);
							MessageTypeType messageType = getMessage(mitt);
							if (messageType != null) {
								messages.add(new MessageItem(this, g2d, mitt, 20,
										(index - startLine) * 15 + yInitStart - 12));
							}
						}
					}
				}
			} else {
				int transactionsRowCount = transactionsTableModel.getRowCount();
				int transactionWidth = (getWidth() - offsetLeft) / transactionsRowCount;
				for (int index = 0; index < transactionsRowCount; index++) {
					TransactionTypeType transactionTypeType = transactionsTableModel.get(index);
					transactions.get(index).x = offsetLeft + transactionWidth * (index + 1) - transactionWidth / 2;
					transactions.get(index).activeLabel.setLocation(transactions.get(index).x,
							transactions.get(index).y);
				}
			}

			String title = selectedElement.getDescription();
			if (title == null || title.length() == 0) {
				title = selectedElement.getId();
			}
			int titleWidth = g2d.getFontMetrics().stringWidth(title);
			System.out.println("width=" + getWidth());
			g2d.drawString(title, (getWidth() - titleWidth) / 2, 18);

			for (Transaction transaction : transactions) {
				transaction.paint(g2d);
			}

			int lineNumber = 0;
			for (final MessageItem messageItem : messages) {
				Transaction transaction = messageItem.getTransaction();
				titleWidth = g2d.getFontMetrics().stringWidth(messageItem.label);
				int lineX = 20;
				int lineY = lineNumber * lineHeight + yInitStart;
//				if (startLine != -1) {
//					if (lineNumber < startLine || lineNumber > startLine + linesPerPage) {
//						continue;
//					}
//					lineY -= startLine * lineHeight;
//				}
				// g2d.drawString(messageItem.label, lineX, lineY);

				g2d.drawLine(titleWidth + lineX + 2, lineY, transaction.x, lineY);
				if (messageItem.isIn()) {
					int[] xsPoints = { lineX - 5, lineX - 12, lineX - 12 };
					int[] ysPoints = { lineY, lineY - 4, lineY + 4 };
					g2d.fillPolygon(xsPoints, ysPoints, 3);
					int[] xPoints = { transaction.x - 5, transaction.x - 12, transaction.x - 12 };
					int[] yPoints = { lineY, lineY - 4, lineY + 4 };
					g2d.fillPolygon(xPoints, yPoints, 3);
				} else {
					int[] xsPoints = { lineX - 13, lineX - 6, lineX - 6 };
					int[] ysPoints = { lineY, lineY - 4, lineY + 4 };
					g2d.fillPolygon(xsPoints, ysPoints, 3);
					int[] xPoints = { transaction.x - 12, transaction.x - 5, transaction.x - 5 };
					int[] yPoints = { lineY, lineY - 4, lineY + 4 };
					g2d.fillPolygon(xPoints, yPoints, 3);
				}
				if (messageItem.isIn()) {
					List<MessageInTransactionTypeType> actions = messageItem.condition.getActions();
					if (actions != null) {
						for (MessageInTransactionTypeType action : actions) {
							Transaction actionTransaction = transactionMap
									.get(RolesPanelControl14.getTransaction(action).getId());
							MessageItem actionMessage = messageItemMap.get(action.getId());
							if (actionMessage != null) {
								int actionY = actionMessage.index * 15 + yInitStart;
								boolean sameTransaction = transaction == actionTransaction;
								int disp = 0;
								if (sameTransaction) {
									// disp = 3 * transaction.routeCount++;
									if (lineY < actionY) {
										disp = 4 * transaction.addInterval(lineY + 4, actionY - 4);
										g2d.drawLine(transaction.x, lineY, transaction.x + disp, lineY);
										g2d.drawArc(transaction.x - 4 + disp, lineY, 8, 8, 90, -90);
										g2d.drawLine(actionTransaction.x + 4 + disp, lineY + 4,
												actionTransaction.x + 4 + disp, actionY - 4);
										g2d.drawArc(actionTransaction.x - 4 + disp, actionY - 8, 8, 8, 0, -90);
										g2d.drawLine(actionTransaction.x + disp, actionY, actionTransaction.x, actionY);
									} else {
										disp = 4 * transaction.addInterval(lineY - 4, actionY + 4);
										g2d.drawLine(transaction.x, lineY, transaction.x + disp, lineY);
										g2d.drawArc(transaction.x - 4 + disp, lineY - 8, 8, 8, -90, 90);
										g2d.drawLine(transaction.x + 4 + disp, lineY - 4,
												actionTransaction.x + 4 + disp, actionY + 4);
										g2d.drawArc(actionTransaction.x - 4 + disp, actionY, 8, 8, 0, 90);
										g2d.drawLine(actionTransaction.x + disp, actionY, actionTransaction.x, actionY);
									}
								} else {
									if (lineY < actionY) {
										if (transaction.x > actionTransaction.x) {
											disp = 4 * transaction.addInterval(lineY + 4, actionY - 4);
											// disp = 3 * transaction.routeCount++;
											g2d.drawLine(transaction.x, lineY, transaction.x + disp, lineY);
											g2d.drawArc(transaction.x - 4 + disp, lineY, 8, 8, 90, -90);
											g2d.drawLine(transaction.x + 4 + disp, lineY + 4, transaction.x + 4 + disp,
													actionY - 4);
											g2d.drawArc(transaction.x - 4 + disp, actionY - 8, 8, 8, 0, -90);
											g2d.drawLine(transaction.x + disp, actionY, actionTransaction.x, actionY);
											int[] xPoints = { transaction.x - 12, transaction.x - 5,
													transaction.x - 5 };
											int[] yPoints = { actionY, actionY - 4, actionY + 4 };
											g2d.fillPolygon(xPoints, yPoints, 3);
										} else {
											disp = 4 * actionTransaction.addInterval(lineY + 4, actionY - 4);
											// disp = 3 * actionTransaction.routeCount++;
											g2d.drawLine(transaction.x + 4, lineY, actionTransaction.x + disp, lineY);
											g2d.drawArc(actionTransaction.x - 4 + disp, lineY, 8, 8, 90, -90);
											g2d.drawLine(actionTransaction.x + 4 + disp, lineY + 4,
													actionTransaction.x + 4 + disp, actionY - 4);
											g2d.drawArc(actionTransaction.x - 4 + disp, actionY - 8, 8, 8, 0, -90);
											int[] xsPoints = { actionTransaction.x - 5, actionTransaction.x - 12,
													actionTransaction.x - 12 };
											int[] ysPoints = { lineY, lineY - 4, lineY + 4 };
											g2d.fillPolygon(xsPoints, ysPoints, 3);
										}
									} else if (lineY > actionY) {
										if (transaction.x > actionTransaction.x) {
											disp = 4 * transaction.addInterval(lineY - 4, actionY + 4);
											// disp = 3 * transaction.routeCount++;
											g2d.drawLine(transaction.x, lineY, transaction.x + disp, lineY);
											g2d.drawArc(transaction.x - 4 + disp, lineY - 8, 8, 8, -90, 90);
											g2d.drawLine(transaction.x + 4 + disp, lineY - 4, transaction.x + 4 + disp,
													actionY + 4);
											g2d.drawArc(transaction.x - 4 + disp, actionY, 8, 8, 0, 90);
											g2d.drawLine(transaction.x + disp, actionY, actionTransaction.x, actionY);
											int[] xPoints = { transaction.x - 12, transaction.x - 5,
													transaction.x - 5 };
											int[] yPoints = { actionY, actionY - 4, actionY + 4 };
											g2d.fillPolygon(xPoints, yPoints, 3);
										} else {
											disp = 4 * actionTransaction.addInterval(lineY - 4, actionY + 4);
											// disp = 3 * actionTransaction.routeCount++;
											g2d.drawLine(transaction.x, lineY, actionTransaction.x + disp, lineY);
											g2d.drawArc(actionTransaction.x - 4 + disp, lineY - 8, 8, 8, -90, 90);
											g2d.drawLine(actionTransaction.x + 4 + disp, lineY - 4,
													actionTransaction.x + 4 + disp, actionY + 4);
											g2d.drawArc(actionTransaction.x - 4 + disp, actionY, 8, 8, 0, 90);
											g2d.drawLine(actionTransaction.x + disp, actionY, actionTransaction.x,
													actionY);
											int[] xPoints = { actionTransaction.x - 5, actionTransaction.x - 12,
													actionTransaction.x - 12 };
											int[] yPoints = { lineY, lineY - 4, lineY + 4 };
											g2d.fillPolygon(xPoints, yPoints, 3);
										}
									}
								}
							}
						}
					} else {
						g2d.setColor(Color.red);
						g2d.fillOval(transaction.x - 4, lineY - 4, 8, 8);
						g2d.setColor(Color.black);
						g2d.drawOval(transaction.x - 4, lineY - 4, 8, 8);
					}
				} else {
					if (messageItem.mitt.getPrevious() == null) {
						g2d.setColor(Color.green);
						g2d.fillOval(transaction.x - 4, lineY - 4, 8, 8);
						g2d.setColor(Color.black);
						g2d.drawOval(transaction.x - 4, lineY - 4, 8, 8);
					}
				}
				lineNumber++;
				yHeight = lineY;
			}

			if (startLine == -1) {
				int height = preferredSize.height;
				int width = preferredSize.width;
				preferredSize = new Dimension(width, yInitStart + yHeight + 20);

				if (height != preferredSize.height || width != preferredSize.width) {
					setSize(getPreferredSize());
					canvas.invalidate();
					canvas.repaint();
				}
			}

		}

		@Override
		public Dimension getPreferredSize() {
			return preferredSize;
		}

		private void reset(Graphics g) {
			g.clearRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.BLACK);

			for (Transaction transaction : transactions) {
				transaction.routes.clear();
			}

			if (selectedElement != currentRole) {
				removeAll();
				transactions.clear();
				messages.clear();
				transactionMap.clear();
				messageItemMap.clear();
				currentRole = selectedElement;
			}
		}

		public void setCurrentRole(Object object) {
			this.currentRole = null;
		}

		public int getMessageCount() {
			return messages.size();
		}

		public void print(Graphics graphics, int startLine, int linesPerPage) {
			this.startLine = startLine;
			this.linesPerPage = linesPerPage;
			paintComponent(graphics);
			this.startLine = -1;
			this.linesPerPage = -1;
		}
	}

	private enum RoleTableColumns {
		Id, Description, StartDate, EndDate, State, DateLamu, UserLamu;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	private class RolesTableModel extends ElementsTableModel<RoleTypeType> {

		@Override
		public int getColumnCount() {
			return RoleTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return RoleTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			RoleTypeType role = get(rowIndex);
			switch (RoleTableColumns.values()[columnIndex]) {
			case Id:
				return role.getId();
			case Description:
				return role.getDescription();
			case StartDate:
				return getDate(role.getStartDate());
			case EndDate:
				return getDate(role.getEndDate());
			case State:
				return role.getState();
			case DateLamu:
				return getDateTime(role.getDateLaMu());
			case UserLamu:
				return role.getUserLaMu();
			default:
				return null;
			}
		}
	}

	private enum TransactionsTableColumns {
		Id, Description, Initiator, Navigate;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	private class TransactionsTableModel extends ElementsTableModel<TransactionTypeType> {

		@Override
		public int getColumnCount() {
			return TransactionsTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return TransactionsTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			TransactionTypeType transaction = get(rowIndex);
			switch (TransactionsTableColumns.values()[columnIndex]) {
			case Id:
				return transaction.getId();
			case Description:
				return transaction.getDescription();
			case Initiator:
				RoleTypeType roleType = getInitiator(transaction);
				if (roleType != null) {
					if (selectedElement.equals(roleType)) {
						return true;
					} else {
						return false;
					}
				}
				return null;
			default:
				break;
			}
			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == TransactionsTableColumns.Initiator.ordinal())
				return Boolean.class;
			else
				return Object.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return (columnIndex == TransactionsTableColumns.Navigate.ordinal()) ? true : false;
		}

	}

	private enum ConditionRuleType {
		Action, Trigger, SendAfter, SendBefore, Start, Stop;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}
	}

	private class ConditionRule extends ElementType {
		private ConditionRuleType type;
		private MessageInTransactionTypeType mitt;

		public ConditionRule() {
			super();
		}

		public ConditionRule(ConditionRuleType type, MessageInTransactionTypeType mitt) {
			this();
			this.type = type;
			this.mitt = mitt;
		}

		public ConditionRuleType getType() {
			return type;
		}

		public MessageInTransactionTypeType getMitt() {
			return mitt;
		}

		@Override
		public String getId() {
			return mitt != null ? mitt.getId() : null;
		}

		@Override
		public String toString() {
			return mitt != null ? mitt.getId() : null;
		}

	}

	private class Condition {
		private MessageInTransactionTypeType mitt;

		@SuppressWarnings("unused")
		public Condition() {
		}

		public Condition(MessageInTransactionTypeType mitt) {
			this.mitt = mitt;
		}

		public List<MessageInTransactionTypeType> getTriggers() {
			return getPrevious(mitt);
		}

		public List<MessageInTransactionTypeType> getSendAfters() {
			return Control14.getSendAfters(mitt);
		}

		public List<MessageInTransactionTypeType> getSendBefores() {
			return Control14.getSendBefores(mitt);
		}

		public List<MessageInTransactionTypeType> getActions() {
			if (mitt != null) {
				List<MessageInTransactionTypeType> actions = null;

				List<MessageInTransactionTypeType> allMitts = Editor14.getStore14()
						.getElements(MessageInTransactionTypeType.class);
				for (MessageInTransactionTypeType mittElement : allMitts) {
					List<MessageInTransactionTypeType> previous = getPrevious(mittElement);
					if (previous != null) {
						for (MessageInTransactionTypeType prev : previous) {
							if (prev.getId().equals(mitt.getId())) {
								if (actions == null) {
									actions = new ArrayList<>();
								}
								actions.add(mittElement);
							}
						}
					}
				}
				return actions;
			}
			return null;
		}
	}

	private enum MessagesTableColumns {
		Type, Id, Role, Transaction, Message, Navigate;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	private class MessagesTableModel extends ElementsTableModel<MessageInTransactionTypeType> {

		@Override
		public int getColumnCount() {
			return MessagesTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return MessagesTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			MessageInTransactionTypeType mitt = get(rowIndex);
			Boolean initiatorToExecutor = mitt.isInitiatorToExecutor();
			if (initiatorToExecutor == null) {
				initiatorToExecutor = false;
			}
			RoleTypeType initiator = getInitiator(mitt);
			RoleTypeType executor = getExecutor(mitt);
			switch (MessagesTableColumns.values()[columnIndex]) {
			case Type:
				if (initiator != null) {
					if (selectedElement.equals(initiator)) {
						return initiatorToExecutor ? "out" : "in";
					} else {
						if (executor != null) {
							if (selectedElement.equals(executor)) {
								return initiatorToExecutor ? "in" : "out";
							}
						}
					}
				}
				return null;
			case Id:
				return mitt.getId();
			case Role:
				if (getValueAt(rowIndex, MessagesTableColumns.Type.ordinal()).equals("in")) {
					if (initiatorToExecutor) {
						return initiator != null ? initiator.getId() : null;
					} else {
						return executor != null ? executor.getId() : null;
					}
				} else {
					if (!initiatorToExecutor) {
						return initiator != null ? initiator.getId() : null;
					} else {
						return executor != null ? executor.getId() : null;
					}
				}
			case Transaction:
				TransactionTypeType transactionType = getTransaction(mitt);
				return transactionType != null ? transactionType.getId() : null;
			case Message:
				MessageTypeType messageType = getMessage(mitt);
				return messageType != null ? messageType.getId() : null;
			default:
				break;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return (columnIndex == MessagesTableColumns.Navigate.ordinal()) ? true : false;
		}

	}

	private enum ConditionsTableColumns {
		Type, Id, Role, Transaction, Message;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	private class ConditionsTableModel extends ElementsTableModel<ConditionRule> {

		@Override
		public int getColumnCount() {
			return ConditionsTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return ConditionsTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ConditionRule conditionRule = get(rowIndex);
			MessageInTransactionTypeType mitt = conditionRule.getMitt();
			switch (ConditionsTableColumns.values()[columnIndex]) {
			case Type:
				return conditionRule.getType().name();
			case Id:
				return conditionRule.getId();
			case Role:
				if (mitt != null) {
					RoleTypeType initiator = getInitiator(mitt);
					RoleTypeType executor = getExecutor(mitt);
					Boolean initiatorToExecutor = mitt.isInitiatorToExecutor();
					if (initiatorToExecutor == null) {
						initiatorToExecutor = false;
					}
					if (initiatorToExecutor) {
						if (selectedElement.equals(initiator)) {
							return executor != null ? executor.getId() : null;
						} else {
							return initiator != null ? initiator.getId() : null;
						}
					} else {
						if (selectedElement.equals(executor)) {
							return initiator != null ? initiator.getId() : null;
						} else {
							return executor != null ? executor.getId() : null;
						}
					}
				}
				break;
			case Transaction:
				TransactionTypeType transactionType = getTransaction(mitt);
				return transactionType != null ? transactionType.getId() : null;
			case Message:
				MessageTypeType messageType = getMessage(mitt);
				return messageType != null ? messageType.getId() : null;
			}

			return null;
		}

	}

	public RolesPanelControl14() throws Exception {
		super(ROLES_PANEL);

		relationsTabs.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (relationsTabs.getSelectedComponent().equals(canvas)) {
					drawingPlane.setCurrentRole(null);
				}
			}
		});

		initRolesTable();
		initTransactionsTable();
		initMessagesTable();
		initConditionsTable();
		initStartDateField();
		initEndDateField();
		initResponsibilityScope();
		initResponsibilityTask();
		initResponsibilitySupportTask();
		initResponsibilityFeedback();

		drawingPlane = new Canvas();
		scrollPane = new JScrollPane(drawingPlane);
		canvas.add(scrollPane, BorderLayout.CENTER);
	}

	private void initResponsibilityFeedback() {
		tfd_ResponsibilityFeedback.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected synchronized void update(DocumentEvent e) {
				if (inSelection)
					return;

				selectedElement.setResponsibilityFeedback(tfd_ResponsibilityFeedback.getText());
				updateLaMu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});
	}

	private void initResponsibilitySupportTask() {
		tfd_ResponsibilitySupportTask.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected synchronized void update(DocumentEvent e) {
				if (inSelection)
					return;

				selectedElement.setResponsibilitySupportTask(tfd_ResponsibilitySupportTask.getText());
				updateLaMu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});
	}

	private void initResponsibilityTask() {
		tfd_ResponsibilityTask.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected synchronized void update(DocumentEvent e) {
				if (inSelection)
					return;

				selectedElement.setResponsibilityTask(tfd_ResponsibilityTask.getText());
				updateLaMu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});
	}

	private void initResponsibilityScope() {
		tfd_ResponsibilityScope.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected synchronized void update(DocumentEvent e) {
				if (inSelection)
					return;

				selectedElement.setResponsibilityScope(tfd_ResponsibilityScope.getText());
				updateLaMu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});
	}

	private void initEndDateField() {
		endDateField = new DateField(endDatePanel);
		endDateField.addPropertyChangeListener("date", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				try {
					Date endDate = endDateField.getDate();
					if (endDate != null) {
						gcal.setTime(endDate);
						XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
						selectedElement.setEndDate(xgcal);
						elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
						if (!inSelection) {
							updateLaMu(selectedElement, user);
							elementsTableModel.update(selectedRow);
						}
					}
				} catch (DatatypeConfigurationException e1) {
					e1.printStackTrace();
				}
			}
		});
		endDateField.setEnabled(false);
	}

	private void initStartDateField() {
		startDateField = new DateField(startDatePanel);
		startDateField.addPropertyChangeListener("date", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				try {
					Date startDate = startDateField.getDate();
					if (startDate != null) {
						gcal.setTime(startDateField.getDate());
						XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
						selectedElement.setStartDate(xgcal);
						elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
						if (!inSelection) {
							updateLaMu(selectedElement, user);
							elementsTableModel.update(selectedRow);
						}
					}
				} catch (DatatypeConfigurationException e1) {
					e1.printStackTrace();
				}
			}
		});
		startDateField.setEnabled(false);
	}

	@SuppressWarnings("serial")
	private void initTransactionsTable() {
		transactionsTableModel = new TransactionsTableModel();
		tbl_Transactions.setModel(transactionsTableModel);
		tbl_Transactions.setAutoCreateRowSorter(true);
		tbl_Transactions.setFillsViewportHeight(true);
		tbl_Transactions.getColumnModel().getColumn(TransactionsTableColumns.Initiator.ordinal()).setMaxWidth(50);
		TableColumn navigateColumn = tbl_Transactions.getColumnModel()
				.getColumn(TransactionsTableColumns.Navigate.ordinal());
		navigateColumn.setMaxWidth(50);
		navigateColumn.setCellRenderer(getButtonTableCellRenderer());
		navigateColumn.setCellEditor(new NavigatorEditor() {
			@Override
			protected void navigate() {
				int row = tbl_Transactions.getSelectedRow();
				InteractionFrameworkEditor.navigate(transactionsTableModel.get(row));
			}
		});
	}

	@SuppressWarnings("serial")
	private void initMessagesTable() {
		messagesTableModel = new MessagesTableModel();
		tbl_Messages.setModel(messagesTableModel);
		tbl_Messages.setAutoCreateRowSorter(true);
		tbl_Messages.setFillsViewportHeight(true);
		tbl_Messages.getColumnModel().getColumn(MessagesTableColumns.Type.ordinal()).setMaxWidth(50);
		TableColumn navigateColumn = tbl_Messages.getColumnModel().getColumn(MessagesTableColumns.Navigate.ordinal());
		navigateColumn.setMaxWidth(50);
		navigateColumn.setCellRenderer(getButtonTableCellRenderer());
		navigateColumn.setCellEditor(new NavigatorEditor() {
			@Override
			protected void navigate() {
				int row = tbl_Messages.getSelectedRow();
				MessageTypeType message = getMessage(messagesTableModel.get(row));
				InteractionFrameworkEditor.navigate(message);
			}
		});

		tbl_Messages.getSelectionModel().addListSelectionListener(messageTableSelectionListener);
	}

	private ListSelectionListener messageTableSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			int selectedRow = tbl_Messages.getSelectedRow() > -1
					? tbl_Messages.getRowSorter().convertRowIndexToModel(tbl_Messages.getSelectedRow())
					: -1;
			// int selectedRow = tbl_Messages.getSelectedRow();
			boolean selectedMessage = selectedRow >= 0;
			conditionsTableModel.clear();
			tbl_Conditions.setEnabled(selectedMessage);
			btn_AddCondition.setEnabled(selectedMessage);
			if (selectedMessage) {
				String inOut = (String) messagesTableModel.getValueAt(selectedRow, MessagesTableColumns.Type.ordinal());
				MessageInTransactionTypeType mitt = messagesTableModel.get(selectedRow);
				Condition condition = new Condition(mitt);
				if (inOut.contentEquals("out")) {
					List<MessageInTransactionTypeType> sendAfters = condition.getSendAfters();
					if (sendAfters != null) {
						for (MessageInTransactionTypeType sendAfter : sendAfters) {
							conditionsTableModel.add(new ConditionRule(ConditionRuleType.SendAfter, sendAfter));
						}
					}
					List<MessageInTransactionTypeType> sendBefores = condition.getSendBefores();
					if (sendBefores != null) {
						for (MessageInTransactionTypeType sendBefore : sendBefores) {
							conditionsTableModel.add(new ConditionRule(ConditionRuleType.SendBefore, sendBefore));
						}
					}
					List<MessageInTransactionTypeType> triggers = condition.getTriggers();
					if (triggers != null) {
						for (MessageInTransactionTypeType trigger : triggers) {
							conditionsTableModel.add(new ConditionRule(ConditionRuleType.Trigger, trigger));
						}
					} else {
						if (sendAfters == null && sendBefores == null) {
							conditionsTableModel.add(new ConditionRule(ConditionRuleType.Start, null));
						}
					}
				} else {
					List<MessageInTransactionTypeType> actions = condition.getActions();
					if (actions != null) {
						for (MessageInTransactionTypeType action : actions) {
							conditionsTableModel.add(new ConditionRule(ConditionRuleType.Action, action));
						}
					} else {
						conditionsTableModel.add(new ConditionRule(ConditionRuleType.Stop, null));
					}
				}
			}
		}
	};

	private void initConditionsTable() {
		conditionsTableModel = new ConditionsTableModel();
		tbl_Conditions.setModel(conditionsTableModel);
		tbl_Conditions.setAutoCreateRowSorter(true);
		tbl_Conditions.setFillsViewportHeight(true);
		tbl_Conditions.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				int selectedRow = tbl_Conditions.getSelectedRow();
				if (selectedRow < 0) {
					btn_RemoveCondition.setEnabled(false);
				} else {
					ConditionRule conditionRule = conditionsTableModel
							.get(tbl_Conditions.getRowSorter().convertRowIndexToModel(selectedRow));
					boolean isStopCondition = conditionRule.getType().equals(ConditionRuleType.Stop);
					boolean isStartCondition = conditionRule.getType().equals(ConditionRuleType.Start);
					btn_RemoveCondition.setEnabled(!isStartCondition && !isStopCondition);
				}
			}
		});
	}

	public void removeCondition() {
		int selectedRow = tbl_Conditions.getSelectedRow() > -1
				? tbl_Conditions.getRowSorter().convertRowIndexToModel(tbl_Conditions.getSelectedRow())
				: -1;
		if (selectedRow > -1) {
			int selectedMessageTableRow = tbl_Messages.getRowSorter()
					.convertRowIndexToModel(tbl_Messages.getSelectedRow());
			MessageInTransactionTypeType parent = messagesTableModel.get(selectedMessageTableRow);
			ConditionRule conditionRule = conditionsTableModel.get(selectedRow);
			switch (conditionRule.getType()) {
			case Action:
				removePrevious(conditionRule.getMitt(), parent);
				break;
			case SendAfter:
				removeSendAfter(parent, conditionRule.getMitt());
				break;
			case SendBefore:
				removeSendBefore(parent, conditionRule.getMitt());
				break;
			case Start:
				// Should not occur
				return;
			case Stop:
				// Should not occur
				return;
			case Trigger:
				removePrevious(parent, conditionRule.getMitt());
				break;
			default:
				break;
			}
			conditionsTableModel.elements.remove(selectedRow);
			conditionsTableModel.fireTableRowsDeleted(selectedRow, selectedRow);
			messageTableSelectionListener.valueChanged(null);
		}
	}

	public void addCondition() {
		btn_AddCondition.setEnabled(false);

		try {
			int selectedRow = tbl_Messages.getSelectedRow();
			final int selectedRowIndex = tbl_Messages.getRowSorter().convertRowIndexToModel(selectedRow);
			String inOut = (String) messagesTableModel.getValueAt(selectedRowIndex,
					MessagesTableColumns.Type.ordinal());
			final NewConditionDialogControl newConditionDialogControl = new NewConditionDialogControl(inOut);
			newConditionDialogControl.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					System.out.println(evt.getPropertyName() + ": " + evt.getNewValue());
					if (evt.getPropertyName().equals("btn_Create")) {
						String conditionType = newConditionDialogControl.getConditionType();
						MessageInTransactionTypeType value = newConditionDialogControl.getMitt();
						String mittId = (String) messagesTableModel.getValueAt(selectedRowIndex,
								MessagesTableColumns.Id.ordinal());
						MessageInTransactionTypeType mitt = Editor14.getStore14()
								.getElement(MessageInTransactionTypeType.class, mittId);
						addCondition(conditionType, mitt, value);
						conditionsTableModel.add(new ConditionRule(ConditionRuleType.valueOf(conditionType), value));
					}
				}

			});
			newConditionDialogControl.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		btn_AddCondition.setEnabled(true);
	}

	private void addCondition(String conditionType, MessageInTransactionTypeType mitt,
			MessageInTransactionTypeType value) {
		switch (conditionType) {
		case "Action":
			addPrevious(value, mitt);
			break;
		case "SendAfter":
			addSendAfter(mitt, value);
			break;
		case "SendBefore":
			addSendBefore(mitt, value);
			break;
		case "Trigger":
			addPrevious(mitt, value);
			break;
		default:
			break;
		}
	}

	private void initRolesTable() {
		elementsTableModel = new RolesTableModel();
		tbl_Elements.setModel(elementsTableModel);
		tbl_Elements.setAutoCreateRowSorter(true);
		TableRowSorter<ElementsTableModel<RoleTypeType>> tableRowSorter = new TableRowSorter<>(elementsTableModel);
		tableRowSorter.setComparator(RoleTableColumns.StartDate.ordinal(), dateComparator);
		tableRowSorter.setComparator(RoleTableColumns.EndDate.ordinal(), dateComparator);
		tableRowSorter.setComparator(RoleTableColumns.DateLamu.ordinal(), dateTimeComparator);
		tbl_Elements.setRowSorter(tableRowSorter);
		tbl_Elements.setFillsViewportHeight(true);
		tbl_Elements.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				updateSelectionArea(e);
			}
		});
	}

	public void fillTable() {
		fillTable(RoleTypeType.class);
	}

	protected void updateSelectionArea(ListSelectionEvent e) {
		inSelection = true;

		selectedRow = tbl_Elements.getSelectedRow();
		if (selectedRow >= 0) {
			selectedRow = tbl_Elements.getRowSorter().convertRowIndexToModel(selectedRow);
		}
		tbl_Elements.scrollRectToVisible(tbl_Elements.getCellRect(selectedRow, 0, true));
		boolean rowSelected = selectedRow >= 0;
		btn_DeleteElement.setEnabled(rowSelected);
		tfd_Id.setEnabled(rowSelected);
		tfd_Description.setEnabled(rowSelected);
		startDateField.setEnabled(rowSelected);
		endDateField.setEnabled(rowSelected);
		tfd_State.setEnabled(rowSelected);
		tfd_Language.setEnabled(rowSelected);
		tfd_Category.setEnabled(rowSelected);
		tfd_HelpInfo.setEnabled(rowSelected);
		tfd_Code.setEnabled(rowSelected);
		tfd_ResponsibilityScope.setEnabled(rowSelected);
		tfd_ResponsibilityTask.setEnabled(rowSelected);
		tfd_ResponsibilitySupportTask.setEnabled(rowSelected);
		tfd_ResponsibilityFeedback.setEnabled(rowSelected);

		tbl_Transactions.setEnabled(rowSelected);
		tbl_Messages.setEnabled(rowSelected);

		if (rowSelected) {
			selectedElement = elementsTableModel.get(selectedRow);
			tfd_Id.setText(selectedElement.getId());
			tfd_Description.setText(selectedElement.getDescription());
			XMLGregorianCalendar startDate = selectedElement.getStartDate();
			if (startDate != null) {
				startDateField.setDate(selectedElement.getStartDate().toGregorianCalendar().getTime());
			}
			XMLGregorianCalendar endDate = selectedElement.getEndDate();
			if (endDate != null) {
				endDateField.setDate(selectedElement.getEndDate().toGregorianCalendar().getTime());
			}
			tfd_State.setText(selectedElement.getState());
			tfd_Language.setText(selectedElement.getLanguage());
			tfd_Category.setText(selectedElement.getCategory());
			tfd_HelpInfo.setText(selectedElement.getHelpInfo());
			tfd_Code.setText(selectedElement.getCode());
			tfd_ResponsibilityScope.setText(selectedElement.getResponsibilityScope());
			tfd_ResponsibilityTask.setText(selectedElement.getResponsibilityTask());
			tfd_ResponsibilitySupportTask.setText(selectedElement.getResponsibilitySupportTask());
			tfd_ResponsibilityFeedback.setText(selectedElement.getResponsibilityFeedback());

			transactionsTableModel.clear();
			List<TransactionTypeType> transactions = Editor14.getStore14().getElements(TransactionTypeType.class);
			for (TransactionTypeType transaction : transactions) {
				RoleTypeType initiator = getInitiator(transaction);
				if (selectedElement.equals(initiator)) {
					transactionsTableModel.add(transaction);
					continue;
				}
				RoleTypeType executor = getExecutor(transaction);
				if (selectedElement.equals(executor)) {
					transactionsTableModel.add(transaction);
					continue;
				}
			}
			fillMessagesTable();
		} else {
			selectedElement = null;
			tfd_Id.setText("");
			tfd_Description.setText("");
			startDateField.setDate(null);
			endDateField.setDate(null);
			tfd_State.setText("");
			tfd_Language.setText("");
			tfd_Category.setText("");
			tfd_HelpInfo.setText("");
			tfd_Code.setText("");
			tfd_ResponsibilityScope.setText("");
			tfd_ResponsibilityTask.setText("");
			tfd_ResponsibilitySupportTask.setText("");
			tfd_ResponsibilityFeedback.setText("");
			transactionsTableModel.clear();
			messagesTableModel.clear();
			conditionsTableModel.clear();
		}

		canvas.repaint();

		inSelection = false;
	}

	void fillMessagesTable() {
		messagesTableModel.clear();
		tbl_Messages.getSelectionModel().clearSelection();
		List<MessageInTransactionTypeType> mitts = Editor14.getStore14()
				.getElements(MessageInTransactionTypeType.class);
		if (mitts != null) {
			for (MessageInTransactionTypeType mitt : mitts) {
				TransactionTypeType transactionType = getTransaction(mitt);
				RoleTypeType initiator = getInitiator(transactionType);
				if (selectedElement.equals(initiator)) {
					messagesTableModel.add(mitt);
				}
				RoleTypeType executor = getExecutor(transactionType);
				if (selectedElement.equals(executor)) {
					messagesTableModel.add(mitt);
				}
			}
		}

		messagesTableModel.elements.sort(new Comparator<MessageInTransactionTypeType>() {

			@Override
			public int compare(MessageInTransactionTypeType o1, MessageInTransactionTypeType o2) {
				RoleTypeType initiator1 = getInitiator(o1);
				RoleTypeType executor1 = getExecutor(o1);
				RoleTypeType initiator2 = getInitiator(o2);
				RoleTypeType executor2 = getExecutor(o2);
				RoleTypeType role1 = initiator1.equals(selectedElement) ? executor1 : initiator1;
				RoleTypeType role2 = initiator2.equals(selectedElement) ? executor2 : initiator2;
				return role1.getId().compareTo(role2.getId());
			}
		});
	}

	public void newElement() {
		try {
			RoleTypeType newRoleType = objectFactory.createRoleTypeType();
			newElement(newRoleType, "Role_");
			int row = elementsTableModel.add(newRoleType);
			tbl_Elements.getSelectionModel().setSelectionInterval(row, row);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteElement() {
		Store14 store = Editor14.getStore14();
		int row = tbl_Elements.getSelectedRow();
		RoleTypeType roleType = elementsTableModel.get(row);

		List<TransactionTypeType> elements = store.getElements(TransactionTypeType.class);
		for (TransactionTypeType element : elements) {
			RoleTypeType initiator = getInitiator(element);
			if (initiator != null && initiator.equals(roleType)) {
				element.setInitiator(null);
			}

			RoleTypeType executor = getExecutor(element);
			if (executor != null && executor.equals(roleType)) {
				element.setExecutor(null);
			}
		}

		Editor14.getStore14().remove(roleType.getId());
		elementsTableModel.remove(row);
	}

	public Canvas getDrawingPlane() {
		return drawingPlane;
	}
}