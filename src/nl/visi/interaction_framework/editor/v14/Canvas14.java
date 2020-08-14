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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import nl.visi.interaction_framework.editor.Control;
import nl.visi.interaction_framework.editor.ui.RotatingButton;
import nl.visi.schemas._20140331.MessageInTransactionTypeType;
import nl.visi.schemas._20140331.MessageTypeType;
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
	private JPopupMenu popupMenu;

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
		private JPopupMenu popupMenu;

		public Message(MessageInTransactionTypeType mitt) {
			this(mitt, 0, 0);
		}

		public Message(MessageInTransactionTypeType mitt, int x, int y) {
			System.out.println(Control14.getMessage(mitt).getDescription());
			this.mitt = mitt;
			this.x = x;
			this.y = y;
			activeLabel = new RotatingButton();
			resetActiveLabel();

			setTitleAndToolTip(mitt);
			popupMenu = new JPopupMenu();
			ResourceBundle bundle = ResourceBundle.getBundle(Control.RESOURCE_BUNDLE);
			final JMenu addNewResponse = new JMenu(bundle.getString("lbl_AddNewResponse"));
			popupMenu.add(addNewResponse);
			final JMenu addExistingResponse = new JMenu(bundle.getString("lbl_AddExistingResponse"));
			popupMenu.add(addExistingResponse);
			final JMenu addExternalRequest = new JMenu(bundle.getString("lbl_AddExternalRequest"));
			popupMenu.add(addExternalRequest);
			final JMenu addExternalResponse = new JMenu(bundle.getString("lbl_AddExternalResponse"));
			popupMenu.add(addExternalResponse);
			activeLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1),
					BorderFactory.createEmptyBorder(2, 5, 2, 5)));
			activeLabel.setBackground(LIGHT_YELLOW_4);
			activeLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						boolean menuEnabled = state != null && state.equals(MessageState.Selected);
						addNewResponse.setEnabled(menuEnabled);
						addExistingResponse.setEnabled(menuEnabled);
						addExternalRequest.setEnabled(menuEnabled);
						addExternalResponse.setEnabled(menuEnabled);
						if (menuEnabled) {
							fillAddNewResponseMenu(addNewResponse);
							fillAddExistingResponseMenu(addExistingResponse);
							fillAddExternalRequestMenu(addExternalRequest);
							fillAddExternalResponseMenu(addExternalResponse);
						}
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
					} else {
						Message.this.setState(MessageState.Selected);
					}
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

		protected void fillAddNewResponseMenu(JMenu addNewResponse) {
			addNewResponse.removeAll();
			List<MessageTypeType> transactionMessages = new ArrayList<>();
			List<MessageInTransactionTypeType> mitts = Editor14.getStore14()
					.getElements(MessageInTransactionTypeType.class);
			for (MessageInTransactionTypeType mitt : mitts) {
				if (Control14.getTransaction(mitt).getId().equals(selectedTransaction.getId())) {
					transactionMessages.add(Control14.getMessage(mitt));
				}
			}
			List<MessageTypeType> messages = Editor14.getStore14().getElements(MessageTypeType.class);
			for (final MessageTypeType message : messages) {
				if (!transactionMessages.contains(message)) {
					addNewResponse.add(
							new JMenuItem(new AbstractAction(message.getDescription() + " [" + message.getId() + "]") {
								@Override
								public void actionPerformed(ActionEvent e) {
									transactionPanel.cbx_Messages.setSelectedItem(message.getId());
									MessageInTransactionTypeType newMitt = transactionPanel.addMessage();
									Control14.addPrevious(newMitt, Message.this.mitt);
									newMitt.setInitiatorToExecutor(!Message.this.mitt.isInitiatorToExecutor());
									Message message = new Message(newMitt);
									message.state = MessageState.Next;
									selectedNext.add(message);
								}
							}));
				}
			}
		}

		protected void fillAddExistingResponseMenu(JMenu addExistingResponse) {
			addExistingResponse.removeAll();
			List<MessageInTransactionTypeType> mitts = Editor14.getStore14()
					.getElements(MessageInTransactionTypeType.class);
			for (final MessageInTransactionTypeType existingMitt : mitts) {
				if (Control14.getTransaction(existingMitt).getId().equals(selectedTransaction.getId())) {
					if (existingMitt.isInitiatorToExecutor() != Message.this.mitt.isInitiatorToExecutor()) {
						MessageTypeType message = Control14.getMessage(existingMitt);
						addExistingResponse.add(new JMenuItem(
								new AbstractAction(message.getDescription() + " [" + existingMitt.getId() + "]") {
									@Override
									public void actionPerformed(ActionEvent e) {
										Control14.addPrevious(existingMitt, Message.this.mitt);
										Message message = new Message(existingMitt);
										message.state = MessageState.Next;
										selectedNext.add(message);
									}
								}));
					}
				}
			}
		}

		protected void fillAddExternalRequestMenu(JMenu addExternalRequest) {
			addExternalRequest.removeAll();
			List<MessageInTransactionTypeType> messages = new ArrayList<>();
			List<TransactionTypeType> transactions = Editor14.getStore14().getElements(TransactionTypeType.class);
			List<MessageInTransactionTypeType> mitts = Editor14.getStore14()
					.getElements(MessageInTransactionTypeType.class);
			for (TransactionTypeType transaction : transactions) {
				if (!transaction.getId().equals(selectedTransaction.getId())) {
					for (MessageInTransactionTypeType mitt : mitts) {
						if (Control14.getTransaction(mitt).getId().equals(transaction.getId())) {
							List<MessageInTransactionTypeType> previousList = Control14.getPrevious(mitt);
							if (previousList == null) {
								messages.add(mitt);
							} else {
								boolean isCandidate = true;
								for (MessageInTransactionTypeType prev : previousList) {
									if (Control14.getTransaction(prev).getId().equals(transaction.getId())) {
										isCandidate = false;
										break;
									}
								}
								if (isCandidate) {
									messages.add(mitt);
								}
							}
						}
					}
				}
			}
			for (final MessageInTransactionTypeType externalMitt : messages) {
				TransactionTypeType transaction = Control14.getTransaction(externalMitt);
				MessageTypeType message = Control14.getMessage(externalMitt);
				addExternalRequest.add(new JMenuItem(new AbstractAction(transaction.getDescription() + ":"
						+ message.getDescription() + " [" + externalMitt.getId() + "]") {
					@Override
					public void actionPerformed(ActionEvent e) {
						Control14.addPrevious(externalMitt, Message.this.mitt);
						Message message = new Message(externalMitt);
						message.state = MessageState.Next;
						selectedNext.add(message);
					}
				}));
			}
		}

		protected void fillAddExternalResponseMenu(JMenu addExternalResponse) {
			addExternalResponse.removeAll();
			List<MessageInTransactionTypeType> messages = new ArrayList<>();
			List<MessageInTransactionTypeType> mitts = Editor14.getStore14()
					.getElements(MessageInTransactionTypeType.class);
			List<MessageInTransactionTypeType> previousList = Control14.getPrevious(Message.this.mitt);
			if (previousList != null) {
				for (MessageInTransactionTypeType prev : previousList) {
					List<MessageInTransactionTypeType> nextList = Control14.getNext(prev);
					if (nextList != null) {
						for (MessageInTransactionTypeType next : nextList) {
							TransactionTypeType transaction = Control14.getTransaction(next);
							if (!transaction.getId().equals(selectedTransaction.getId())) {
								for (MessageInTransactionTypeType externalMitt : mitts) {
									if (Control14.getTransaction(externalMitt).getId().equals(transaction.getId())) {
										List<MessageInTransactionTypeType> externalMittNextList = Control14.getNext(externalMitt);
										if (externalMittNextList == null) {
											messages.add(externalMitt);
										} else {
											boolean isCandidate = true;
											for (MessageInTransactionTypeType externalMittNext : externalMittNextList) {
												if (Control14.getTransaction(externalMittNext).getId().equals(transaction.getId())) {
													isCandidate = false;
													break;
												}
											}
											if (isCandidate) {
												messages.add(externalMitt);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			for (final MessageInTransactionTypeType externalMitt : messages) {
				TransactionTypeType transaction = Control14.getTransaction(externalMitt);
				MessageTypeType message = Control14.getMessage(externalMitt);
				addExternalResponse.add(new JMenuItem(new AbstractAction(transaction.getDescription() + ":"
						+ message.getDescription() + " [" + externalMitt.getId() + "]") {
					@Override
					public void actionPerformed(ActionEvent e) {
						Control14.addPrevious(Message.this.mitt, externalMitt);
						Message message = new Message(externalMitt);
						message.setState(MessageState.Previous);
						selectedPrev.add(message);
					}
				}));
			}

		}

		private void setTitleAndToolTip(MessageInTransactionTypeType mitt) {
			String label = "?";
			String toolTip = "?";
			if (mitt != null) {
				toolTip = mitt.getId();
				TransactionTypeType transaction = Control14.getTransaction(mitt);
				if (transaction.getId().equals(selectedTransaction.getId())) {
					label = Control14.getMessage(mitt).getDescription();
				} else {
					label = transaction.getDescription() + ":" + Control14.getMessage(mitt).getDescription();
				}
			}
			activeLabel.setText(label);
			activeLabel.setToolTipText(toolTip);
		}

		private void resetActiveLabel() {
			activeLabel.setContentAreaFilled(false);
			activeLabel.setMargin(new Insets(0, 4, 0, 4));
			activeLabel.setBorderPainted(true);
			activeLabel.setFont(getFont().deriveFont(getFont().getSize() - 2.0f));
		}

		MessageState getState() {
			return state;
		}

		void setState(MessageState newState) {
			if (newState != this.state) {
				switch (newState) {
				case History:
					this.state = newState;
					activeLabel.setBackground(Color.WHITE);
					break;
				case Next:
					this.state = newState;
					activeLabel.setBackground(LIGHT_YELLOW_4);
					break;
				case Previous:
					this.state = newState;
					activeLabel.setBackground(LIGHT_BLUE_3);
					break;
				case Selected:
					MessageState previousState = this.state;
					switch (previousState) {
					case History:
						break;
					case Next:
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
						break;
					case Previous:
						transaction = Control14.getTransaction(mitt);
						if (transaction.getId().equals(selectedTransaction.getId())) {
							for (Message msg : historyBefore) {
								Canvas14.this.remove(msg.activeLabel);
							}
							historyBefore.clear();
						} else {
							int index = transactionPanel.elementsTableModel.elements.indexOf(transaction);
							transactionPanel.tbl_Elements.getSelectionModel().setSelectionInterval(index, index);
							selectedTransaction = transaction;
							initNewDiagram();
						}
						break;
					case Selected:
						break;
					default:
						break;
					}

					if (selectedMessage != null) {
						selectedMessage.setState(MessageState.History);
						selectedMessage.activeLabel.setBorder(
								BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1),
										BorderFactory.createEmptyBorder(2, 5, 2, 5)));
						if (this.state.equals(MessageState.Previous)) {
							historyAfter.add(0, selectedMessage);
						} else if (this.state.equals(MessageState.Next)) {
							historyBefore.add(selectedMessage);
						}
					}
//					if (isIn(historyBefore)) {
					if (historyBefore.contains(this)) {
						if (selectedMessage != null) {
							historyAfter.add(0, selectedMessage);
						}
						moveBeforeToAfter();
						historyBefore.remove(this);
//					} else if (isIn(historyAfter)) {
					} else if (historyAfter.contains(this)) {
						if (selectedMessage != null) {
							historyBefore.add(selectedMessage);
						}
						moveAfterToBefore();
						historyAfter.remove(this);

					}
					this.state = newState;

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
					activeLabel.setBorder(
							BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 2),
									BorderFactory.createEmptyBorder(2, 5, 2, 5)));
					activeLabel.setBackground(LIGHT_GOLD_4);
					break;
				default:
					break;
				}
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

		private void moveBeforeToAfter() {
			int indexOfThis = -1;
			for (int i = 0; i < historyBefore.size(); i++) {
				if (historyBefore.get(i) == this) {
					indexOfThis = i;
					break;
				}
			}
//			int indexOfThis = historyBefore.indexOf(this);
			if (indexOfThis < historyBefore.size() - 1) {
				List<Message> toBeMoved = new ArrayList<>();
				for (int i = historyBefore.size() - 1; i > indexOfThis; i--) {
					toBeMoved.add(historyBefore.get(i));
				}
				for (Message message : toBeMoved) {
					historyAfter.add(0, message);
					historyBefore.remove(message);
				}
			}
		}

		private void moveAfterToBefore() {
			int indexOfThis = -1;
			for (int i = 0; i < historyAfter.size(); i++) {
				if (historyAfter.get(i) == this) {
					indexOfThis = i;
					break;
				}
			}
//			int indexOfThis = historyAfter.indexOf(this);
			if (indexOfThis > -1) {
				List<Message> toBeMoved = new ArrayList<>();
				for (int i = 0; i < indexOfThis; i++) {
					toBeMoved.add(historyAfter.get(i));
				}
				for (Message message : toBeMoved) {
					historyBefore.add(message);
					historyAfter.remove(message);
				}
			}
		}

		private boolean isStartMessage() {
//			return transactionPanel.startMitt.contains(mitt);
			if (!Control14.getTransaction(mitt).getId().equals(selectedTransaction.getId())) {
				return false;
			}
			List<MessageInTransactionTypeType> prevMitts = Control14.getPrevious(mitt);
			if (prevMitts == null) {
				return true;
			}
			for (MessageInTransactionTypeType prev : prevMitts) {
				if (Control14.getTransaction(prev).getId().equals(selectedTransaction.getId())) {
					return false;
				}
			}
			return true;
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
			setTitleAndToolTip(mitt);

//			String label = mitt != null ? Control14.getMessage(mitt).getDescription() : "?";
//			activeLabel.setText(label);
//			activeLabel.setToolTipText(mitt != null ? Control14.getMessage(mitt).getId() : "?");

			if (isStartMessage()) {
				activeLabel.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(LIGHT_GREEN_1, 2), BorderFactory.createEmptyBorder(2, 5, 2, 5)));
			}
			if (isEndMessage()) {
				activeLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LIGHT_RED_1, 2),
						BorderFactory.createEmptyBorder(2, 5, 2, 5)));
			}
			TransactionTypeType transaction = Control14.getTransaction(mitt);
			int stringWidth = g2d.getFontMetrics().stringWidth(this.activeLabel.getText());
			if (printMode) {
				Paint paint = g2d.getPaint();
				g2d.setPaint(LIGHT_YELLOW_4);
				g2d.fillRect(x, y, 100, 25);
				g2d.setPaint(paint);
				g2d.drawRect(x, y, 100, 25);
				g2d.setFont(getFont().deriveFont(getFont().getSize() - 2.0f));
				g2d.drawString(this.activeLabel.getText(), x + 50 - (stringWidth / 2), y + 25);
			} else {
				String internalInitiator = Control14.getInitiator(selectedTransaction).getId();
				String internalExecutor = Control14.getExecutor(selectedTransaction).getId();
				String externalInitiator = Control14.getInitiator(mitt).getId();
				String externalExecutor = Control14.getExecutor(mitt).getId();
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
							if (externalInitiator.equals(internalInitiator)) { // EI = II
								activeLabel.setLocation(initiatorFlow - activeLabel.getWidth() - 50, y);
							} else if (externalInitiator.equals(internalExecutor)) { // EI = IE
								activeLabel.setLocation(executorFlow + 50, y);
							}
						} else {
							if (externalExecutor.equals(internalInitiator)) { // EI = II
								activeLabel.setLocation(initiatorFlow - activeLabel.getWidth() - 50, y);
							} else if (externalExecutor.equals(internalExecutor)) { // EE = IE
								activeLabel.setLocation(executorFlow + 50, y);
							}
						}
					}
//					else {
//						if (mitt.isInitiatorToExecutor()) {
//							RoleTypeType initiator = Control14.getInitiator(mitt);
//							if (initiator.getId().equals(Control14.getInitiator(selectedTransaction).getId())) {
//								activeLabel.setLocation(initiatorFlow - activeLabel.getWidth() - 50, y);
//							} else {
//								activeLabel.setLocation(executorFlow + 50, y);
//							}
//						} else {
//							RoleTypeType executor = Control14.getExecutor(mitt);
//							if (executor.getId().equals(Control14.getExecutor(selectedTransaction).getId())) {
//								activeLabel.setLocation(executorFlow + 50, y);
//							} else {
//								activeLabel.setLocation(initiatorFlow - activeLabel.getWidth() - 50, y);
//							}
//						}
//					}
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
							if (externalExecutor.equals(internalInitiator)) { // EE = II
								activeLabel.setLocation(initiatorFlow - activeLabel.getWidth() - 50, y);
							} else if (externalExecutor.equals(internalExecutor)) { // EE = IE
								activeLabel.setLocation(executorFlow + 50, y);
							}
						} else {
							if (externalInitiator.equals(internalInitiator)) { // EI = II
								activeLabel.setLocation(initiatorFlow - activeLabel.getWidth() - 50, y);
							} else if (externalInitiator.equals(internalExecutor)) { // EI = IE
								activeLabel.setLocation(executorFlow + 50, y);
							}
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
						RoleTypeType initiator = Control14.getInitiator(mitt);
						if (initiator.getId().equals(Control14.getInitiator(selectedTransaction).getId())) {
							g2d.drawLine(initiatorFlow - 10, y + 8, initiatorFlow - 50, y + 8);
							drawArrowPoint(initiatorFlow - 50, y + 8, 10, 180);
						} else {
							g2d.drawLine(executorFlow + 10, y + 8, executorFlow + 50, y + 8);
							drawArrowPoint(executorFlow + 50, y + 8, 10, 0);
						}
					} else {
						RoleTypeType executor = Control14.getExecutor(mitt);
						if (executor.getId().equals(Control14.getExecutor(selectedTransaction).getId())) {
							g2d.drawLine(executorFlow + 10, y + 8, initiatorFlow + 50, y + 8);
							drawArrowPoint(executorFlow + 50, y + 8, 10, 0);
						} else {
							g2d.drawLine(initiatorFlow - 10, y + 8, initiatorFlow - 50, y + 8);
							drawArrowPoint(initiatorFlow - 50, y + 8, 10, 180);
						}
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
						RoleTypeType initiator = Control14.getInitiator(mitt);
						if (initiator.getId().equals(Control14.getInitiator(selectedTransaction).getId())) {
							g2d.drawLine(executorFlow + 50, y + 8, executorFlow + 10, y + 8);
							drawArrowPoint(executorFlow + 10, y + 8, 10, 180);
						} else {
							g2d.drawLine(initiatorFlow - 50, y + 8, initiatorFlow - 10, y + 8);
							drawArrowPoint(initiatorFlow - 10, y + 8, 10, 0);
						}
					} else {
						RoleTypeType initiator = Control14.getInitiator(mitt);
						RoleTypeType executor = Control14.getExecutor(mitt);
						if (initiator.getId().equals(Control14.getInitiator(selectedTransaction).getId())) {
							g2d.drawLine(initiatorFlow - 50, y + 8, initiatorFlow - 10, y + 8);
							drawArrowPoint(initiatorFlow - 10, y + 8, 10, 0);
						} else if (initiator.getId().equals(Control14.getExecutor(selectedTransaction).getId())) {
							g2d.drawLine(executorFlow + 50, y + 8, executorFlow + 10, y + 8);
							drawArrowPoint(executorFlow + 10, y + 8, 10, 180);
						} else if (executor.getId().equals(Control14.getInitiator(selectedTransaction).getId())) {
							g2d.drawLine(executorFlow + 50, y + 8, executorFlow + 10, y + 8);
							drawArrowPoint(executorFlow + 10, y + 8, 10, 180);
						} else if (executor.getId().equals(Control14.getExecutor(selectedTransaction).getId())) {
							g2d.drawLine(executorFlow + 50, y + 8, executorFlow + 10, y + 8);
							drawArrowPoint(executorFlow + 10, y + 8, 10, 180);
						}
//						if (executor.getId().equals(Control14.getExecutor(selectedTransaction).getId())) {
//							g2d.drawLine(executorFlow + 50, y + 8, executorFlow + 10, y + 8);
//							drawArrowPoint(executorFlow + 10, y + 8, 10, 180);
//						} else {
//							g2d.drawLine(initiatorFlow - 50, y + 8, initiatorFlow - 10, y + 8);
//							drawArrowPoint(initiatorFlow - 10, y + 8, 10, 0);
//							g2d.drawLine(executorFlow + 50, y + 8, executorFlow + 10, y + 8);
//							drawArrowPoint(executorFlow + 10, y + 8, 10, 180);
//						}
					}
//					if (mitt.isInitiatorToExecutor()) {
//						g2d.drawLine(executorFlow + 10, y + 8, executorFlow + 50, y + 8);
//						drawArrowPoint(executorFlow + 10, y + 8, 10, 180);
//					} else {
//						g2d.drawLine(initiatorFlow - 50, y + 8, initiatorFlow - 10, y + 8);
//						drawArrowPoint(initiatorFlow - 10, y + 8, 10, 0);
//					}
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

	public Canvas14(final TransactionsPanelControl14 transactionPanel) {
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

		popupMenu = new JPopupMenu();
		ResourceBundle bundle = ResourceBundle.getBundle(Control.RESOURCE_BUNDLE);
		final JMenu selectMenu = new JMenu(bundle.getString("lbl_SelectMessage"));
		popupMenu.add(selectMenu);
		popupMenu.add(new JSeparator());
		final JMenu addStartMsg = new JMenu(bundle.getString("lbl_AddStartMessage"));
		popupMenu.add(addStartMsg);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					fillSelectMenu(selectMenu);
					fillAddStartMessageMenu(addStartMsg);
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	private void fillSelectMenu(JMenu selectMenu) {
		selectMenu.removeAll();
		List<MessageInTransactionTypeType> messageList = transactionPanel.getMessagesTableModel().elements;
		if (messageList != null) {
			for (final MessageInTransactionTypeType mitt : messageList) {
				MessageTypeType message = Control14.getMessage(mitt);
				JMenuItem msgItem = new JMenuItem(
						new AbstractAction(message.getDescription() + " [" + mitt.getId() + "]") {
							@Override
							public void actionPerformed(ActionEvent e) {
								selectMessage(mitt);
							}
						});
				selectMenu.add(msgItem);
			}
		}
	}

	private void fillAddStartMessageMenu(JMenu addStartMsg) {
		addStartMsg.removeAll();
		List<MessageTypeType> messages = Editor14.getStore14().getElements(MessageTypeType.class);
		for (final MessageTypeType message : messages) {
			addStartMsg.add(new JMenuItem(new AbstractAction(message.getDescription() + " [" + message.getId() + "]") {
				@Override
				public void actionPerformed(ActionEvent e) {
					transactionPanel.cbx_Messages.setSelectedItem(message.getId());
					MessageInTransactionTypeType mitt = transactionPanel.addMessage();
					initNewDiagram();
					Message message = new Message(mitt);
					message.state = MessageState.Next;
					selectedNext.add(message);
				}
			}));
		}
	}

	private void selectMessage(MessageInTransactionTypeType mitt) {
		initNewDiagram();
		Message message = new Message(mitt);
		message.state = MessageState.Next;
		selectedNext.add(message);
		message.setState(MessageState.Selected);
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
			//
			// History before messages
			//
			for (Message message : historyBefore) {
				message.paint(y);
				y += MESSAGE_LINE_HEIGHT;
			}
			lastY = drawConnectorBoxes(historyBefore, lastMessage, lastY);
			Message lastHistoryBeforeMessage = !historyBefore.isEmpty() ? historyBefore.get(historyBefore.size() - 1)
					: null;
			lastMessage = lastHistoryBeforeMessage;

			//
			// Internal previous messages
			//
			int saveY = y;
			List<Message> actualSelectedPrev = new ArrayList<>();
			for (Message message : selectedPrev) {
				if (!message.isIn(historyBefore)) {
					actualSelectedPrev.add(message);
					message.paint(y);
					y += MESSAGE_LINE_HEIGHT;
				} else {
					message.activeLabel.setVisible(false);
				}
			}
			lastY = drawConnectorBoxes(actualSelectedPrev, lastMessage, lastY);
			Message lastSelectedPrevMessage = !actualSelectedPrev.isEmpty()
					? actualSelectedPrev.get(actualSelectedPrev.size() - 1)
					: null;
			lastMessage = lastSelectedPrevMessage != null ? lastSelectedPrevMessage : lastHistoryBeforeMessage;

			//
			// External previous messages
			//
			if (!selectedResponse.isEmpty()) {
				y = saveY;
				for (Message message : selectedResponse) {
					message.paint(y);
					y += MESSAGE_LINE_HEIGHT;
				}
			}
			// lastY = drawConnectorBoxes(selectedResponse, lastMessage, lastY);
			lastY = drawConnectorBoxes(selectedResponse, lastHistoryBeforeMessage,
					y - (!selectedResponse.isEmpty() ? selectedResponse.size() : 0) * MESSAGE_LINE_HEIGHT);
			Message lastSelectedResponseMessage = !selectedResponse.isEmpty()
					? selectedResponse.get(selectedResponse.size() - 1)
					: null;
			lastMessage = lastSelectedResponseMessage != null ? lastSelectedResponseMessage : lastMessage;
			y = lastY;

			//
			// Selected message
			//
			selectedMessage.paint(y);
			lastY = drawConnectorBoxes(null, lastMessage, lastY);
			saveY = y;

			//
			// External next messages
			//
			y += MESSAGE_LINE_HEIGHT;
			for (Message message : selectedRequest) {
				message.paint(y);
				y += MESSAGE_LINE_HEIGHT;
			}
			lastMessage = selectedMessage != null ? selectedMessage : lastMessage;
			int saveLastY = lastY;
			lastY = drawConnectorBoxes(selectedRequest, lastMessage, lastY);

			//
			// Internal next messages
			//
			y = saveY;
			y += MESSAGE_LINE_HEIGHT;
			List<Message> actualSelectedNext = new ArrayList<>();
			for (Message message : selectedNext) {
				if (!message.isIn(historyAfter)) {
					actualSelectedNext.add(message);
					message.paint(y);
					y += MESSAGE_LINE_HEIGHT;
				} else {
					message.activeLabel.setVisible(false);
				}
			}
			lastMessage = selectedMessage != null ? selectedMessage : lastMessage;
			lastY = drawConnectorBoxes(actualSelectedNext, lastMessage, saveLastY);

			//
			// History after messages
			//
			for (Message message : historyAfter) {
				message.paint(y);
				y += MESSAGE_LINE_HEIGHT;
			}
			lastY = drawConnectorBoxes(historyAfter, selectedMessage, lastY);

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
			g2d.setColor(Color.GREEN);
			drawConnectorBox(getStartX(selectedMessage), lastY, 1, lastMsg == null ? BoxType.CLOSED : BoxType.OPEN_TOP);
			drawConnectorBox(getEndX(selectedMessage), lastY, 1,
					selectedMessage.isEndMessage() && selectedRequest.isEmpty() ? BoxType.CLOSED : BoxType.OPEN_BOTTOM);
			g2d.setColor(Color.BLACK);
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
							drawConnectorBox(getEndX(currMsg), lastY, 1, BoxType.OPEN_BOTTOM);
						}
						break;
					default:
						break;
					}
				} else {
					switch (currMsg.getState()) {
					case History:
						g2d.setColor(Color.RED);
						drawConnectorBox(getStartX(currMsg), lastY, 1, BoxType.OPEN_TOP);
						drawConnectorBox(getEndX(currMsg), lastY, 1,
								nextMsg == null ? BoxType.CLOSED : BoxType.OPEN_BOTTOM);
						g2d.setColor(Color.BLACK);
						break;
					case Next:
						g2d.setColor(Color.CYAN);
						drawConnectorBox(getStartX(currMsg), lastY, 1,
								nextMsg == null ? BoxType.OPEN_TOP : BoxType.OPEN_TOP_BOTTOM);
						g2d.setColor(Color.BLACK);
						break;
					case Previous:
						g2d.setColor(Color.PINK);
						drawConnectorBox(getEndX(currMsg), lastY, 1,
								nextMsg == null ? BoxType.OPEN_TOP : BoxType.OPEN_TOP_BOTTOM);
						g2d.setColor(Color.BLACK);
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
//		return currMsg.mitt.isInitiatorToExecutor() ? executorFlow : initiatorFlow;
		TransactionTypeType transaction = Control14.getTransaction(currMsg.mitt);
		if (transaction.getId().equals(selectedTransaction.getId())) {
			return currMsg.mitt.isInitiatorToExecutor() ? executorFlow : initiatorFlow;
		} else {
			RoleTypeType selectedInitiator = Control14.getInitiator(selectedTransaction);
			RoleTypeType selectedExecutor = Control14.getExecutor(selectedTransaction);
			RoleTypeType initiator = Control14.getInitiator(currMsg.mitt);
			RoleTypeType executor = Control14.getExecutor(currMsg.mitt);
			if (initiator.getId().equals(selectedInitiator.getId())) {
				return initiatorFlow;
			} else if (initiator.getId().equals(selectedExecutor.getId())) {
				return executorFlow;
			} else if (executor.getId().equals(selectedInitiator.getId())) {
				return initiatorFlow;
			} else if (executor.getId().equals(selectedExecutor.getId())) {
				return executorFlow;
			}
			return 0;
		}
	}

	private int getStartX(Message currMsg) {
		TransactionTypeType transaction = Control14.getTransaction(currMsg.mitt);
		if (transaction.getId().equals(selectedTransaction.getId())) {
			return currMsg.mitt.isInitiatorToExecutor() ? initiatorFlow : executorFlow;
		} else {
			RoleTypeType selectedInitiator = Control14.getInitiator(selectedTransaction);
			RoleTypeType selectedExecutor = Control14.getExecutor(selectedTransaction);
			RoleTypeType initiator = Control14.getInitiator(currMsg.mitt);
			RoleTypeType executor = Control14.getExecutor(currMsg.mitt);
			if (initiator.getId().equals(selectedInitiator.getId())) {
				return initiatorFlow;
			} else if (initiator.getId().equals(selectedExecutor.getId())) {
				return executorFlow;
			} else if (executor.getId().equals(selectedInitiator.getId())) {
				return initiatorFlow;
			} else if (executor.getId().equals(selectedExecutor.getId())) {
				return executorFlow;
			}
			return 0;
		}
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
