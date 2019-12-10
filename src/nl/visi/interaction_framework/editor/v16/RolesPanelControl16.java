package nl.visi.interaction_framework.editor.v16;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.visi.schemas._20160331.ElementType;
import nl.visi.schemas._20160331.MessageInTransactionTypeType;
import nl.visi.schemas._20160331.MessageTypeType;
import nl.visi.schemas._20160331.RoleTypeType;
import nl.visi.schemas._20160331.TransactionTypeType;

class RolesPanelControl16 extends PanelControl16<RoleTypeType> {
	private static final String ROLES_PANEL = "nl/visi/interaction_framework/editor/swixml/RolesPanel16.xml";

	private JTabbedPane relationsTabs;
	private JPanel startDatePanel, endDatePanel, canvas;
	private JTable tbl_Transactions, tbl_Messages, tbl_Conditions;
	private JTextField tfd_ResponsibilityScope, tfd_ResponsibilityTask, tfd_ResponsibilitySupportTask,
			tfd_ResponsibilityFeedback;
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
		private int offsetLeft = 200;
		private int yInitStart = 200;
		private int yHeight = 0;

		private class Transaction {
			private int x, y;
			private TransactionTypeType transactionType;
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

			Transaction(TransactionTypeType transactionType, int x, int y) {
				this.transactionType = transactionType;
				this.x = x;
				this.y = y;
				transactionMap.put(transactionType.getId(), this);
			}

			void paint(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				String label = transactionType.getId();
				if (label != null) {
					g2.setFont(getFont().deriveFont(getFont().getSize() - 2.0f));
					int stringWidth = g2.getFontMetrics().stringWidth(label);
					g2.translate((float) x, (float) y);
					g2.rotate(Math.toRadians(-90));
					g2.drawString(label, -stringWidth, -5);
					g2.rotate(-Math.toRadians(-90));
					g2.translate(-(float) x, -(float) y);

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
			private Condition condition;
			private int index = 0;

			public MessageItem(MessageInTransactionTypeType mitt) {
				this.mitt = mitt;
				initiator = Control16.getInitiator(mitt);
				executor = Control16.getExecutor(mitt);
				this.initiatorToExecutor = mitt.isInitiatorToExecutor();
				this.messageType = Control16.getMessage(mitt);
				this.transactionType = Control16.getTransaction(mitt);
				label = this.messageType.getId();
				condition = new Condition(mitt);
				this.index = messageItemMap.size();
				messageItemMap.put(mitt.getId(), this);
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
			preferredSize = new Dimension(getWidth(), getHeight());
			setSize(getPreferredSize());
			transactions = new ArrayList<RolesPanelControl16.Canvas.Transaction>();
			messages = new ArrayList<RolesPanelControl16.Canvas.MessageItem>();
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
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
					transactions.add(new Transaction(transactionTypeType,
							offsetLeft + transactionWidth * (index + 1) - transactionWidth / 2, 25));
				}
				for (int index = 0; index < messagesTableModel.getRowCount(); index++) {
					MessageInTransactionTypeType mitt = messagesTableModel.get(index);
					MessageTypeType messageType = getMessage(mitt);
					if (messageType != null) {
						messages.add(new MessageItem(mitt));
					}
				}
			}

			String title = selectedElement.getDescription();
			if (title == null || title.length() == 0) {
				title = selectedElement.getId();
			}
			int titleWidth = g2d.getFontMetrics().stringWidth(title);
			g2d.drawString(title, (getWidth() - titleWidth) / 2, 18);

			for (Transaction transaction : transactions) {
				transaction.paint(g2d);
			}

			int lineNumber = 0;
			for (MessageItem messageItem : messages) {
				Transaction transaction = messageItem.getTransaction();
				titleWidth = g2d.getFontMetrics().stringWidth(messageItem.label);
				int lineX = 20;
				int lineY = lineNumber * 12 + yInitStart;
				g2d.drawString(messageItem.label, lineX, lineY);
				g2d.drawLine(lineX, lineY, transaction.x, lineY);
				if (messageItem.isIn()) {
					int[] xPoints = { transaction.x - 5, transaction.x - 12, transaction.x - 12 };
					int[] yPoints = { lineY, lineY - 4, lineY + 4 };
					g2d.fillPolygon(xPoints, yPoints, 3);
				} else {
					int[] xPoints = { transaction.x - 12, transaction.x - 5, transaction.x - 5 };
					int[] yPoints = { lineY, lineY - 4, lineY + 4 };
					g2d.fillPolygon(xPoints, yPoints, 3);
				}
				if (messageItem.isIn()) {
					List<MessageInTransactionTypeType> actions = messageItem.condition.getActions();
					if (actions != null) {
						for (MessageInTransactionTypeType action : actions) {
							Transaction actionTransaction = transactionMap
									.get(RolesPanelControl16.getTransaction(action).getId());
							MessageItem actionMessage = messageItemMap.get(action.getId());
							int actionY = actionMessage.index * 12 + yInitStart;
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
									g2d.drawLine(transaction.x + 4 + disp, lineY - 4, actionTransaction.x + 4 + disp,
											actionY + 4);
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
									} else {
										disp = 4 * actionTransaction.addInterval(lineY + 4, actionY - 4);
										// disp = 3 * actionTransaction.routeCount++;
										g2d.drawLine(transaction.x + 4, lineY, actionTransaction.x + disp, lineY);
										g2d.drawArc(actionTransaction.x - 4 + disp, lineY, 8, 8, 90, -90);
										g2d.drawLine(actionTransaction.x + 4 + disp, lineY + 4,
												actionTransaction.x + 4 + disp, actionY - 4);
										g2d.drawArc(actionTransaction.x - 4 + disp, actionY - 8, 8, 8, 0, -90);
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
									} else {
										disp = 4 * actionTransaction.addInterval(lineY - 4, actionY + 4);
										// disp = 3 * actionTransaction.routeCount++;
										g2d.drawLine(transaction.x, lineY, actionTransaction.x + disp, lineY);
										g2d.drawArc(actionTransaction.x - 4 + disp, lineY - 8, 8, 8, -90, 90);
										g2d.drawLine(actionTransaction.x + 4 + disp, lineY - 4,
												actionTransaction.x + 4 + disp, actionY + 4);
										g2d.drawArc(actionTransaction.x - 4 + disp, actionY, 8, 8, 0, 90);
										g2d.drawLine(actionTransaction.x + disp, actionY, actionTransaction.x, actionY);
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

			int height = preferredSize.height;
			int width = preferredSize.width;
			preferredSize = new Dimension(width, yInitStart + yHeight + 20);

			if (height != preferredSize.height || width != preferredSize.width) {
				setSize(getPreferredSize());
				canvas.invalidate();
				canvas.repaint();
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
			return Control16.getSendAfters(mitt);
		}

		public List<MessageInTransactionTypeType> getSendBefores() {
			return Control16.getSendBefores(mitt);
		}

		public List<MessageInTransactionTypeType> getActions() {
			if (mitt != null) {
				List<MessageInTransactionTypeType> actions = null;

				List<MessageInTransactionTypeType> allMitts = Editor16.getStore16()
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

	public RolesPanelControl16() throws Exception {
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
		tfd_ResponsibilityFeedback.getDocument().addDocumentListener(new DocumentAdapter16() {
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
		tfd_ResponsibilitySupportTask.getDocument().addDocumentListener(new DocumentAdapter16() {
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
		tfd_ResponsibilityTask.getDocument().addDocumentListener(new DocumentAdapter16() {
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
		tfd_ResponsibilityScope.getDocument().addDocumentListener(new DocumentAdapter16() {
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
		endDateField = new DateField16(endDatePanel);
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
		startDateField = new DateField16(startDatePanel);
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
				Editor16.getMainFrameControl().navigate(transactionsTableModel.get(row));
			}
		});
	}

	@SuppressWarnings("serial")
	private void initMessagesTable() {
		messagesTableModel = new MessagesTableModel();
		tbl_Messages.setModel(messagesTableModel);
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
				Editor16.getMainFrameControl().navigate(message);
			}
		});

		tbl_Messages.getSelectionModel().addListSelectionListener(messageTableSelectionListener);
	}

	private ListSelectionListener messageTableSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			int selectedRow = tbl_Messages.getSelectedRow();
			boolean selectedMessage = selectedRow >= 0;
			conditionsTableModel.clear();
			tbl_Conditions.setEnabled(selectedMessage);
			if (selectedMessage) {
				String inOut = (String) messagesTableModel.getValueAt(selectedRow, MessagesTableColumns.Type.ordinal());
				MessageInTransactionTypeType mitt = messagesTableModel.get(selectedRow);
				Condition condition = new Condition(mitt);
				if (inOut.contentEquals("out")) {
					List<MessageInTransactionTypeType> triggers = condition.getTriggers();
					if (triggers != null) {
						for (MessageInTransactionTypeType trigger : triggers) {
							conditionsTableModel.add(new ConditionRule(ConditionRuleType.Trigger, trigger));
						}
					} else {
						conditionsTableModel.add(new ConditionRule(ConditionRuleType.Start, null));
					}
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
		tbl_Conditions.setFillsViewportHeight(true);
	}

	private void initRolesTable() {
		elementsTableModel = new RolesTableModel();
		tbl_Elements.setModel(elementsTableModel);
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
		canvas.repaint();

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
			List<TransactionTypeType> transactions = Editor16.getStore16().getElements(TransactionTypeType.class);
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
		inSelection = false;
	}

	private void fillMessagesTable() {
		messagesTableModel.clear();
		List<MessageInTransactionTypeType> mitts = Editor16.getStore16()
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
		Store16 store = Editor16.getStore16();
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

		Editor16.getStore16().remove(roleType.getId());
		elementsTableModel.remove(row);
	}
}
