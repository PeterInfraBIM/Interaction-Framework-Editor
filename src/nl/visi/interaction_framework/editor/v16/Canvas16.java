package nl.visi.interaction_framework.editor.v16;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import nl.visi.interaction_framework.editor.Control;
import nl.visi.interaction_framework.editor.InteractionFrameworkEditor;
import nl.visi.interaction_framework.editor.SelectBox;
import nl.visi.interaction_framework.editor.ui.RotatingButton;
import nl.visi.schemas._20160331.MessageInTransactionTypeType;
import nl.visi.schemas._20160331.MessageTypeType;
import nl.visi.schemas._20160331.RoleTypeType;
import nl.visi.schemas._20160331.TransactionTypeType;

@SuppressWarnings("serial")
public class Canvas16 extends JPanel {
	private static final Color LIGHT_GREEN_1 = new Color(63, 175, 70);
	private static final Color LIGHT_GREEN_3 = new Color(175, 208, 149);
	private static final Color LIGHT_RED_1 = new Color(255, 56, 56);
	private static final Color LIGHT_RED_3 = new Color(255, 166, 166);
	private static final Color LIGHT_BLUE_3 = new Color(180, 199, 220);
	private static final Color LIGHT_YELLOW_4 = new Color(255, 255, 215);
	private static final Color LIGHT_GOLD_4 = new Color(255, 233, 148);
	private static final int MESSAGE_LINE_HEIGHT = 24;
	private final ImageIcon greenCircleIcon, redCircleIcon;

	final private ResourceBundle bundle = ResourceBundle.getBundle(Control.RESOURCE_BUNDLE);
	private final TransactionsPanelControl16 transactionPanel;
	TransactionTypeType selectedTransaction;
	TransactionTypeType currentTransaction;
	private Role executor, initiator;
	private List<Message> historyBefore, historyAfter, selectedNext, selectedPrev, selectedRequest, selectedResponse;
	Message selectedMessage;
	private Dimension preferredSize;
	private Graphics2D g2d;
	private int leftMargin, rightMargin, middleMargin, topMargin, initiatorFlow, executorFlow;
	private boolean printMode;
	private JPopupMenu popupMenu;
	private MessageInTransactionDialogControl16 messageInTransactionDialogControl16;

	class ResizeListener extends ComponentAdapter {

		public void componentResized(ComponentEvent e) {
//			setDimensions();
		}
	}

	private class Role {

		private RoleTypeType role;
		private int x;
		private int y;
		private Color color;
		private RotatingButton activeLabel;

		public Role(final RoleTypeType role, int x, int y, Color color) {
			this.role = role;
			this.x = x;
			this.y = y;
			this.color = color;
			activeLabel = new RotatingButton();
			activeLabel.setTransferHandler(Msg2MittTransferHandler.getInstance());
			activeLabel.setContentAreaFilled(false);
			activeLabel.setBackground(color);
			activeLabel.setBorderPainted(false);
			activeLabel.setFont(getFont().deriveFont(getFont().getSize() - 2.0f));
			activeLabel.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					InteractionFrameworkEditor.navigate(role);
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					e.getComponent().setForeground(Color.RED);
					Canvas16.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					e.getComponent().setForeground(Color.BLACK);
					Canvas16.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			});
			Canvas16.this.add(activeLabel);
		}

		void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			String label = role != null ? role.getDescription() : "?";
			activeLabel.setText(label);
			activeLabel.setToolTipText(role != null ? role.getId() : "?");
			g2.setFont(g2.getFont().deriveFont(Font.BOLD));
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
				activeLabel.setMinimumSize(new Dimension(stringWidth + 30, 50));
				activeLabel.setPreferredSize(new Dimension(stringWidth + 30, 50));
				activeLabel.setLocation(x - (stringWidth / 4) + 15, y + 25 - 10);
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

	class Message {
		private MessageState state;
		private final MessageInTransactionTypeType mitt;
		private RotatingButton activeLabel;
		private int x;
		@SuppressWarnings("unused")
		private int y;
		private final JPopupMenu popupMenu;
		private final JMenuItem mittEdit;

		public Message(MessageInTransactionTypeType mitt) {
			this(mitt, 0, 0);
		}

		public Message(MessageInTransactionTypeType mitt, int x, int y) {
			System.out.println(Control16.getMessage(mitt).getDescription());
			this.mitt = mitt;
			this.x = x;
			this.y = y;
			activeLabel = new RotatingButton();
			activeLabel.setTransferHandler(Msg2MittTransferHandler.getInstance());
			resetActiveLabel();

			setTitleAndToolTip(mitt);
			popupMenu = new JPopupMenu();
			mittEdit = new JMenuItem(new AbstractAction(bundle.getString("lbl_Edit")) {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						if (messageInTransactionDialogControl16 == null) {
							Window window = SwingUtilities.windowForComponent(Canvas16.this);
							messageInTransactionDialogControl16 = new MessageInTransactionDialogControl16(
									transactionPanel, window);
							messageInTransactionDialogControl16.getDialog().setModal(false);
							messageInTransactionDialogControl16.getDialog().addWindowListener(new WindowAdapter() {
								@Override
								public void windowClosing(WindowEvent e) {
									super.windowClosed(e);
								}
							});
							messageInTransactionDialogControl16.addPropertyChangeListener(new PropertyChangeListener() {
								@Override
								public void propertyChange(PropertyChangeEvent evt) {
									switch (evt.getPropertyName()) {
									case "Previous removed":
										MessageInTransactionTypeType removedPrev = (MessageInTransactionTypeType) evt
												.getNewValue();
										System.out.println("Previous removed: " + removedPrev.getId());
										TransactionTypeType prTransaction = Control16.getTransaction(removedPrev);
										List<Message> rpLstv = prTransaction.getId().equals(selectedTransaction.getId())
												? selectedPrev
												: selectedResponse;
										int index = -1;
										for (int i = 0; i < rpLstv.size(); i++) {
											if (rpLstv.get(i).mitt.getId().equals(removedPrev.getId())) {
												index = i;
												break;
											}
										}
										if (index != -1) {
											Canvas16.this.remove(rpLstv.get(index).activeLabel);
											rpLstv.remove(index);
										}
										break;
									case "Next removed":
										MessageInTransactionTypeType removedNext = (MessageInTransactionTypeType) evt
												.getOldValue();
										System.out.println("Next removed: " + removedNext.getId());
										TransactionTypeType nrTransaction = Control16.getTransaction(removedNext);
										List<Message> nrLstv = nrTransaction.getId().equals(selectedTransaction.getId())
												? selectedNext
												: selectedRequest;
										index = -1;
										for (int i = 0; i < nrLstv.size(); i++) {
											if (nrLstv.get(i).mitt.getId().equals(removedNext.getId())) {
												index = i;
												break;
											}
										}
										if (index != -1) {
											Canvas16.this.remove(nrLstv.get(index).activeLabel);
											nrLstv.remove(index);
										}
										break;
									case "Previous added":
										MessageInTransactionTypeType addedPrev = (MessageInTransactionTypeType) evt
												.getNewValue();
										System.out.println("Previous added: " + addedPrev.getId());
										TransactionTypeType paTransaction = Control16.getTransaction(addedPrev);
										Message newMessage = new Message(addedPrev);
										newMessage.state = MessageState.Previous;
										if (paTransaction.getId().equals(selectedTransaction.getId())) {
											selectedPrev.add(newMessage);
										} else {
											selectedRequest.add(newMessage);
										}
										break;
									case "Next added":
										MessageInTransactionTypeType addedNext = (MessageInTransactionTypeType) evt
												.getOldValue();
										System.out.println("Next added: " + addedNext.getId());
										TransactionTypeType naTransaction = Control16.getTransaction(addedNext);
										Message naMessage = new Message(addedNext);
										naMessage.state = MessageState.Next;
										if (naTransaction.getId().equals(selectedTransaction.getId())) {
											selectedNext.add(naMessage);
										} else {
											selectedRequest.add(naMessage);
										}
										break;
									}
									transactionPanel.getDrawingPlane().setCurrentTransaction(null);
									transactionPanel.getDrawingPlane().repaint();
								}
							});
						}
						TransactionTypeType transaction = Control16.getTransaction(Message.this.mitt);
						MessageTypeType message = Control16.getMessage(Message.this.mitt);
						messageInTransactionDialogControl16.getDialog().setTitle(transaction.getDescription() + " : "
								+ message.getDescription() + " [" + Message.this.mitt.getId() + "]");
						messageInTransactionDialogControl16.fillTree(Message.this.mitt);
						messageInTransactionDialogControl16.initSequenceElements();
						messageInTransactionDialogControl16.fillSequenceElements(Message.this.mitt);
						messageInTransactionDialogControl16.getDialog().setVisible(true);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
			mittEdit.setEnabled(false);
			popupMenu.add(mittEdit);
			popupMenu.add(new JSeparator());
			final JMenuItem addNewResponse = new JMenuItem(new AbstractAction(bundle.getString("lbl_AddNewResponse")) {
				@Override
				public void actionPerformed(ActionEvent e) {
					addNewResponse();
				}
			});
			popupMenu.add(addNewResponse);
			final JMenuItem addExistingResponse = new JMenuItem(
					new AbstractAction(bundle.getString("lbl_AddExistingResponse")) {
						@Override
						public void actionPerformed(ActionEvent e) {
							addExistingResponse();
						}
					});
			popupMenu.add(addExistingResponse);
			final JMenuItem addExternalRequest = new JMenuItem(
					new AbstractAction(bundle.getString("lbl_AddExternalRequest")) {
						@Override
						public void actionPerformed(ActionEvent e) {
							addExternalRequest();
						}
					});
			popupMenu.add(addExternalRequest);
			final JMenuItem addExternalResponse = new JMenuItem(
					new AbstractAction(bundle.getString("lbl_AddExternalResponse")) {
						@Override
						public void actionPerformed(ActionEvent e) {
							addExternalResponse();
						}
					});
			popupMenu.add(addExternalResponse);
			popupMenu.add(new JSeparator());
			final JMenuItem removeMenuItem = new JMenuItem(new AbstractAction(bundle.getString("lbl_Remove")) {
				@Override
				public void actionPerformed(ActionEvent e) {
					removeSelectedMessage();
				}
			});
			removeMenuItem.setEnabled(false);
			popupMenu.add(removeMenuItem);
			if (isEndMessage(mitt) || isStartMessage(mitt)) {
				if (isEndMessage(mitt)) {
					activeLabel.setBorder(
							BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LIGHT_RED_1, 2),
									BorderFactory.createEmptyBorder(2, 5, 2, 5)));
				}
				if (isStartMessage(mitt)) {
					activeLabel.setBorder(
							BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LIGHT_GREEN_1, 2),
									BorderFactory.createEmptyBorder(2, 5, 2, 5)));
				}
			} else {
				activeLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1),
						BorderFactory.createEmptyBorder(2, 5, 2, 5)));
			}
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
						mittEdit.setEnabled(state.equals(MessageState.Selected));
						removeMenuItem.setEnabled(state.equals(MessageState.Selected));
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
					} else {
						Message.this.setState(MessageState.Selected);
						if (messageInTransactionDialogControl16 != null) {
							TransactionTypeType transaction = Control16.getTransaction(Message.this.mitt);
							MessageTypeType message = Control16.getMessage(Message.this.mitt);
							messageInTransactionDialogControl16.getDialog().setTitle(transaction.getDescription()
									+ " : " + message.getDescription() + " [" + Message.this.mitt.getId() + "]");
							messageInTransactionDialogControl16.fillTree(Message.this.mitt);
							messageInTransactionDialogControl16.initSequenceElements();
							messageInTransactionDialogControl16.fillSequenceElements(Message.this.mitt);
						}
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					e.getComponent().setForeground(Color.RED);
					Canvas16.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					e.getComponent().setForeground(Color.BLACK);
					Canvas16.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			});

			Canvas16.this.add(activeLabel);
		}

		private void removeSelectedMessage() {
			List<MessageInTransactionTypeType> elements = transactionPanel.getMessagesTableModel().elements;
			int index = elements.indexOf(selectedMessage.mitt);
			transactionPanel.tbl_Messages.getSelectionModel().setSelectionInterval(index, index);
			transactionPanel.removeMessage();
		}

		void removeFromDiagrams() {
			MessageInTransactionTypeType saveFirstNext = selectedNext.size() > 0 ? selectedNext.get(0).mitt : null;
//			selectedTransaction = null;
			transactionPanel.getDrawingPlane().setCurrentTransaction(null);
			transactionPanel.getDrawingPlane().repaint();
			if (messageInTransactionDialogControl16 != null) {
				messageInTransactionDialogControl16.getDialog().dispose();
			}
			Canvas16.this.remove(selectedMessage.activeLabel);
			for (Message msg : selectedNext) {
				Canvas16.this.remove(msg.activeLabel);
			}
			selectedNext.clear();
			for (Message msg : selectedPrev) {
				Canvas16.this.remove(msg.activeLabel);
			}
			selectedPrev.clear();
			for (Message msg : selectedRequest) {
				Canvas16.this.remove(msg.activeLabel);
			}
			selectedRequest.clear();
			for (Message msg : selectedResponse) {
				Canvas16.this.remove(msg.activeLabel);
			}
			selectedResponse.clear();

			selectMessage(saveFirstNext);
		}

		private void setTitleAndToolTip(MessageInTransactionTypeType mitt) {
			String label = "?";
			String toolTip = "?";
			if (mitt != null) {
				toolTip = mitt.getId();
				TransactionTypeType transaction = Control16.getTransaction(mitt);
				if (transaction.getId().equals(selectedTransaction.getId())) {
					label = Control16.getMessage(mitt).getDescription();
				} else {
					label = transaction.getDescription() + ":" + Control16.getMessage(mitt).getDescription();
				}
				List<MessageInTransactionTypeType> befores = Control16.getSendBefores(mitt);
				if (befores != null) {
					toolTip += " send before: ";
					for (int i = 0; i < befores.size(); i++) {
						toolTip += befores.get(i).getId();
						if (i < befores.size() - 1) {
							toolTip += ", ";
						}
					}
				}
				List<MessageInTransactionTypeType> afters = Control16.getSendAfters(mitt);
				if (afters != null) {
					toolTip += " send after: ";
					for (int i = 0; i < afters.size(); i++) {
						toolTip += afters.get(i).getId() + " ";
						if (i < afters.size() - 1) {
							toolTip += ", ";
						}
					}
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
						TransactionTypeType transaction = Control16.getTransaction(mitt);
						if (transaction.getId().equals(selectedTransaction.getId())) {
							for (Message msg : historyAfter) {
								Canvas16.this.remove(msg.activeLabel);
							}
							historyAfter.clear();
						} else {
							int index = transactionPanel.elementsTableModel.elements.indexOf(transaction);
							index = transactionPanel.tbl_Elements.getRowSorter().convertRowIndexToView(index);
							transactionPanel.tbl_Elements.getSelectionModel().setSelectionInterval(index, index);
							selectedTransaction = transaction;
							initNewDiagram();
							transactionPanel.getDrawingPlane().setCurrentTransaction(null);
							transactionPanel.getDrawingPlane().repaint();
						}
						break;
					case Previous:
						transaction = Control16.getTransaction(mitt);
						if (transaction.getId().equals(selectedTransaction.getId())) {
							for (Message msg : historyBefore) {
								Canvas16.this.remove(msg.activeLabel);
							}
							historyBefore.clear();
						} else {
							int index = transactionPanel.elementsTableModel.elements.indexOf(transaction);
							index = transactionPanel.tbl_Elements.getRowSorter().convertRowIndexToView(index);
							transactionPanel.tbl_Elements.getSelectionModel().setSelectionInterval(index, index);
							selectedTransaction = transaction;
							initNewDiagram();
							transactionPanel.getDrawingPlane().setCurrentTransaction(null);
							transactionPanel.getDrawingPlane().repaint();
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
					if (historyBefore.contains(this)) {
						if (selectedMessage != null) {
							historyAfter.add(0, selectedMessage);
						}
						moveBeforeToAfter();
						historyBefore.remove(this);
					} else if (historyAfter.contains(this)) {
						if (selectedMessage != null) {
							historyBefore.add(selectedMessage);
						}
						moveAfterToBefore();
						historyAfter.remove(this);

					}
					this.state = newState;

					for (Message msg : selectedPrev) {
						Canvas16.this.remove(msg.activeLabel);
					}
					selectedPrev.clear();
					for (Message msg : selectedNext) {
						Canvas16.this.remove(msg.activeLabel);
					}
					selectedNext.clear();
					for (Message msg : selectedRequest) {
						Canvas16.this.remove(msg.activeLabel);
					}
					selectedRequest.clear();
					for (Message msg : selectedResponse) {
						Canvas16.this.remove(msg.activeLabel);
					}
					selectedResponse.clear();

					selectedMessage = this;
					if (previousState.equals(MessageState.Previous) || previousState.equals(MessageState.Next)) {
						Canvas16.this.add(selectedMessage.activeLabel);
					}
					List<MessageInTransactionTypeType> prevList = Control16.getPrevious(mitt);
					if (prevList != null) {
						for (MessageInTransactionTypeType prev : prevList) {
							Message prevMessage = new Message(prev);
							prevMessage.setState(MessageState.Previous);
							if (Control16.getTransaction(prev).getId().equals(selectedTransaction.getId())) {
								selectedPrev.add(prevMessage);
							} else {
								selectedResponse.add(prevMessage);
							}
						}
					}
					List<MessageInTransactionTypeType> nextList = Control16.getNext(mitt);
					if (nextList != null) {
						for (MessageInTransactionTypeType next : nextList) {
							Message nextMessage = new Message(next);
							nextMessage.setState(MessageState.Next);
							if (Control16.getTransaction(next).getId().equals(selectedTransaction.getId())) {
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

		private void moveBeforeToAfter() {
			int indexOfThis = -1;
			for (int i = 0; i < historyBefore.size(); i++) {
				if (historyBefore.get(i) == this) {
					indexOfThis = i;
					break;
				}
			}
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

		void paint(int y) {
			setTitleAndToolTip(mitt);

			TransactionTypeType transaction = Control16.getTransaction(mitt);
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
						if (selectedMessage.mitt.isInitiatorToExecutor()) {
							activeLabel.setLocation(executorFlow + 50, y);
						} else {
							activeLabel.setLocation(initiatorFlow - activeLabel.getWidth() - 50, y);
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
						if (selectedMessage.mitt.isInitiatorToExecutor()) {
							activeLabel.setLocation(initiatorFlow - activeLabel.getWidth() - 50, y);
						} else {
							activeLabel.setLocation(executorFlow + 50, y);
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
					if (selectedMessage.mitt.isInitiatorToExecutor()) {
						g2d.drawLine(executorFlow + 10, y + 8, executorFlow + 50, y + 8);
						drawArrowPoint(executorFlow + 50, y + 8, 10, 0);
					} else {
						g2d.drawLine(initiatorFlow - 10, y + 8, initiatorFlow - 50, y + 8);
						drawArrowPoint(initiatorFlow - 50, y + 8, 10, 180);
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
					if (selectedMessage.mitt.isInitiatorToExecutor()) {
						g2d.drawLine(initiatorFlow - 50, y + 8, initiatorFlow - 10, y + 8);
						drawArrowPoint(initiatorFlow - 10, y + 8, 10, 0);
					} else {
						g2d.drawLine(executorFlow + 50, y + 8, executorFlow + 10, y + 8);
						drawArrowPoint(executorFlow + 10, y + 8, 10, 180);
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

		private void addNewResponse() {
			List<String> items = new ArrayList<>();
			List<MessageTypeType> messages = Editor16.getStore16().getElements(MessageTypeType.class);
			for (MessageTypeType message : messages) {
				items.add(message.getDescription() + " [" + message.getId() + "]");
			}
			transactionPanel.getPanel().getParent();
			SelectBox selectBox = new SelectBox(((JFrame) SwingUtilities.getRoot(Canvas16.this)),
					bundle.getString("lbl_AddNewResponse"), items);
			selectBox.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					System.out.println(evt.getPropertyName() + " is " + evt.getNewValue());
					String result = (String) evt.getNewValue();
					int lastOpenBracket = result.lastIndexOf('[');
					int lastCloseBracket = result.lastIndexOf(']');
					String messageId = result.substring(lastOpenBracket, lastCloseBracket + 1) + " "
							+ result.substring(0, lastOpenBracket - 1);
					transactionPanel.cbx_Messages.setSelectedItem(messageId);
					MessageInTransactionTypeType newMitt = transactionPanel.addMessage();
					Control16.addPrevious(newMitt, Message.this.mitt);
					newMitt.setInitiatorToExecutor(!Message.this.mitt.isInitiatorToExecutor());
					Message message = new Message(newMitt);
					message.state = MessageState.Next;
					selectedNext.add(message);
					transactionPanel.getDrawingPlane().setCurrentTransaction(null);
					transactionPanel.getDrawingPlane().repaint();
					if (messageInTransactionDialogControl16 != null) {
						messageInTransactionDialogControl16.initSequenceElements();
						messageInTransactionDialogControl16.fillSequenceElements(Message.this.mitt);
					}
				}
			});
		}

		private void addExistingResponse() {
			List<String> items = new ArrayList<>();
			List<MessageInTransactionTypeType> nextMitts = Control16.getNext(this.mitt);
			List<MessageInTransactionTypeType> mitts = Editor16.getStore16()
					.getElements(MessageInTransactionTypeType.class);
			for (final MessageInTransactionTypeType existingMitt : mitts) {
				if (Control16.getTransaction(existingMitt).getId().equals(selectedTransaction.getId())) {
					if (existingMitt.isInitiatorToExecutor() != Message.this.mitt.isInitiatorToExecutor()) {
						if (nextMitts == null || !nextMitts.contains(existingMitt)) {
							MessageTypeType message = Control16.getMessage(existingMitt);
							items.add(message.getDescription() + " [" + existingMitt.getId() + "]");
						}
					}
				}
			}

			SelectBox selectBox = new SelectBox(((JFrame) SwingUtilities.getRoot(Canvas16.this)),
					bundle.getString("lbl_AddExistingResponse"), items);
			selectBox.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					System.out.println(evt.getPropertyName() + " is " + evt.getNewValue());
					String result = (String) evt.getNewValue();
					int lastOpenBracket = result.lastIndexOf('[');
					int lastCloseBracket = result.lastIndexOf(']');
					String mittId = result.substring(lastOpenBracket + 1, lastCloseBracket);
					MessageInTransactionTypeType existingMitt = Editor16.getStore16()
							.getElement(MessageInTransactionTypeType.class, mittId);
					Control16.addPrevious(existingMitt, Message.this.mitt);
					Message message = new Message(existingMitt);
					message.state = MessageState.Next;
					selectedNext.add(message);
					transactionPanel.getDrawingPlane().setCurrentTransaction(null);
					transactionPanel.getDrawingPlane().repaint();
					if (messageInTransactionDialogControl16 != null) {
						messageInTransactionDialogControl16.initSequenceElements();
						messageInTransactionDialogControl16.fillSequenceElements(Message.this.mitt);
					}
				}
			});
		}

		private void addExternalRequest() {
			List<String> items = new ArrayList<>();
			RoleTypeType role = mitt.isInitiatorToExecutor() ? Control16.getExecutor(mitt)
					: Control16.getInitiator(mitt);
			List<MessageInTransactionTypeType> messages = new ArrayList<>();
			List<TransactionTypeType> transactions = Editor16.getStore16().getElements(TransactionTypeType.class);
			List<MessageInTransactionTypeType> mitts = Editor16.getStore16()
					.getElements(MessageInTransactionTypeType.class);
			for (TransactionTypeType transaction : transactions) {
				if (!transaction.getId().equals(selectedTransaction.getId())) {
					for (MessageInTransactionTypeType externalMitt : mitts) {
						if (Control16.getTransaction(externalMitt).getId().equals(transaction.getId())) {
//							if (!role.getId().equals(Control16.getInitiator(externalMitt).getId())) {
							if (role.getId().equals(Control16.getInitiator(externalMitt).getId())) {
								List<MessageInTransactionTypeType> previousList = Control16.getPrevious(externalMitt);
								if (previousList == null) {
									messages.add(externalMitt);
								} else {
									boolean isCandidate = true;
									for (MessageInTransactionTypeType prev : previousList) {
										if (Control16.getTransaction(prev).getId().equals(transaction.getId())) {
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
			for (final MessageInTransactionTypeType externalMitt : messages) {
				TransactionTypeType transaction = Control16.getTransaction(externalMitt);
				MessageTypeType message = Control16.getMessage(externalMitt);
				items.add(transaction.getDescription() + ":" + message.getDescription() + " [" + externalMitt.getId()
						+ "]");
			}
			SelectBox selectBox = new SelectBox(((JFrame) SwingUtilities.getRoot(Canvas16.this)),
					bundle.getString("lbl_AddExternalRequest"), items);
			selectBox.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					System.out.println(evt.getPropertyName() + " is " + evt.getNewValue());
					String result = (String) evt.getNewValue();
					int lastOpenBracket = result.lastIndexOf('[');
					int lastCloseBracket = result.lastIndexOf(']');
					String mittId = result.substring(lastOpenBracket + 1, lastCloseBracket);
					MessageInTransactionTypeType existingMitt = Editor16.getStore16()
							.getElement(MessageInTransactionTypeType.class, mittId);
					Control16.addPrevious(existingMitt, Message.this.mitt);
					Message message = new Message(existingMitt);
					message.state = MessageState.Next;
					selectedRequest.add(message);
					transactionPanel.getDrawingPlane().setCurrentTransaction(null);
					transactionPanel.getDrawingPlane().repaint();
					if (messageInTransactionDialogControl16 != null) {
						messageInTransactionDialogControl16.initSequenceElements();
						messageInTransactionDialogControl16.fillSequenceElements(Message.this.mitt);
					}
				}
			});
		}

		private void addExternalResponse() {
			List<String> items = new ArrayList<>();
			List<MessageInTransactionTypeType> prevMitts = Control16.getPrevious(this.mitt);
			List<MessageInTransactionTypeType> messages = new ArrayList<>();
			List<MessageInTransactionTypeType> mitts = Editor16.getStore16()
					.getElements(MessageInTransactionTypeType.class);
			List<MessageInTransactionTypeType> previousList = Control16.getPrevious(Message.this.mitt);
			if (previousList != null) {
				for (MessageInTransactionTypeType prev : previousList) {
					List<MessageInTransactionTypeType> nextList = Control16.getNext(prev);
					if (nextList != null) {
						for (MessageInTransactionTypeType next : nextList) {
							TransactionTypeType transaction = Control16.getTransaction(next);
							if (!transaction.getId().equals(selectedTransaction.getId())) {
								for (MessageInTransactionTypeType externalMitt : mitts) {
									if (Control16.getTransaction(externalMitt).getId().equals(transaction.getId())) {
										List<MessageInTransactionTypeType> externalMittNextList = Control16
												.getNext(externalMitt);
										if (externalMittNextList == null) {
											if (!messages.contains(externalMitt)) {
												messages.add(externalMitt);
											}
										} else {
											boolean isCandidate = true;
											for (MessageInTransactionTypeType externalMittNext : externalMittNextList) {
												if (Control16.getTransaction(externalMittNext).getId()
														.equals(transaction.getId())) {
													isCandidate = false;
													break;
												}
											}
											if (isCandidate) {
												if (!messages.contains(externalMitt)) {
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
			}
			for (final MessageInTransactionTypeType externalMitt : messages) {
				TransactionTypeType transaction = Control16.getTransaction(externalMitt);
				MessageTypeType message = Control16.getMessage(externalMitt);
				items.add(transaction.getDescription() + ":" + message.getDescription() + " [" + externalMitt.getId()
						+ "]");
			}
			SelectBox selectBox = new SelectBox(((JFrame) SwingUtilities.getRoot(Canvas16.this)),
					bundle.getString("lbl_AddExternalResponse"), items);
			selectBox.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					System.out.println(evt.getPropertyName() + " is " + evt.getNewValue());
					String result = (String) evt.getNewValue();
					int lastOpenBracket = result.lastIndexOf('[');
					int lastCloseBracket = result.lastIndexOf(']');
					String mittId = result.substring(lastOpenBracket + 1, lastCloseBracket);
					MessageInTransactionTypeType externalMitt = Editor16.getStore16()
							.getElement(MessageInTransactionTypeType.class, mittId);
					Control16.addPrevious(Message.this.mitt, externalMitt);
					Message message = new Message(externalMitt);
					message.setState(MessageState.Previous);
					selectedResponse.add(message);
					transactionPanel.getDrawingPlane().setCurrentTransaction(null);
					transactionPanel.getDrawingPlane().repaint();
					if (messageInTransactionDialogControl16 != null) {
						messageInTransactionDialogControl16.initSequenceElements();
						messageInTransactionDialogControl16.fillSequenceElements(Message.this.mitt);
					}
				}
			});
		}
	}

	public Canvas16(final TransactionsPanelControl16 transactionPanel) {
		URL greenCircleURL = this.getClass()
				.getResource("/nl/visi/interaction_framework/editor/icons/circle-green-16.png");
		greenCircleIcon = new ImageIcon(greenCircleURL);
		URL redCircleURL = this.getClass().getResource("/nl/visi/interaction_framework/editor/icons/circle-red-16.png");
		redCircleIcon = new ImageIcon(redCircleURL);

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
		final JMenuItem addStartMessage = new JMenuItem(new AbstractAction(bundle.getString("lbl_AddStartMessage")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				addStartMessage();
			}
		});
		popupMenu.add(addStartMessage);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					fillSelectMenu(selectMenu);
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	protected void addStartMessage() {
		List<String> items = new ArrayList<>();
		List<MessageTypeType> messages = Editor16.getStore16().getElements(MessageTypeType.class);
		for (MessageTypeType message : messages) {
			items.add(message.getDescription() + " [" + message.getId() + "]");
		}
		transactionPanel.getPanel().getParent();
		SelectBox selectBox = new SelectBox(((JFrame) SwingUtilities.getRoot(Canvas16.this)),
				bundle.getString("lbl_AddStartMessage"), items);
		selectBox.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println(evt.getPropertyName() + " is " + evt.getNewValue());
				String result = (String) evt.getNewValue();
				int lastOpenBracket = result.lastIndexOf('[');
				int lastCloseBracket = result.lastIndexOf(']');
				// String messageId = result.substring(lastOpenBracket + 1, lastCloseBracket);
				String messageId = result.substring(lastOpenBracket, lastCloseBracket + 1) + " "
						+ result.substring(0, lastOpenBracket - 1);
				addMitt2Canvas(messageId, true);
			}
		});
	}

	private void fillSelectMenu(JMenu selectMenu) {
		selectMenu.removeAll();
		List<MessageInTransactionTypeType> messageList = transactionPanel.getMessagesTableModel().elements;
		if (messageList != null) {
			for (final MessageInTransactionTypeType mitt : messageList) {
				MessageTypeType message = Control16.getMessage(mitt);
				JMenuItem msgItem = new JMenuItem(
						new AbstractAction(message.getDescription() + " [" + mitt.getId() + "]") {
							@Override
							public void actionPerformed(ActionEvent e) {
								selectMessage(mitt);
							}
						});
				selectMenu.add(msgItem);

				if (isEndMessage(mitt))
					msgItem.setIcon(redCircleIcon);
				if (isStartMessage(mitt))
					msgItem.setIcon(greenCircleIcon);

			}
		}
	}

	void selectMessage(MessageInTransactionTypeType mitt) {
		initNewDiagram();
		if (mitt != null) {
			Message message = new Message(mitt);
			message.state = MessageState.Next;
			selectedNext.add(message);
			message.setState(MessageState.Selected);
		}
		System.out.println("exit selectMessage");
	}

	public Canvas16(TransactionsPanelControl16 transactionPanel, boolean printMode) {
		this(transactionPanel);
		this.selectedTransaction = transactionPanel.selectedElement;
		this.printMode = printMode;
	}

	@Override
	public Dimension getPreferredSize() {
		return preferredSize;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2d = (Graphics2D) g;

		if (transactionPanel.selectedElement == null) {
			selectedTransaction = null;
			Canvas16.this.removeAll();
			g2d.setColor(Color.WHITE);
			g2d.fillRect(0, 0, getWidth(), getHeight());
			return;
		}

		boolean newDiagram = !transactionPanel.selectedElement.equals(selectedTransaction)
				|| currentTransaction == null;
		currentTransaction = transactionPanel.selectedElement;
		if (newDiagram) {
			System.out.println("newDiagram");
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
			int internalPrevMaxY = lastY;
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
			int externalPrevMaxY = lastY;
			Message lastSelectedResponseMessage = !selectedResponse.isEmpty()
					? selectedResponse.get(selectedResponse.size() - 1)
					: null;
			lastMessage = lastSelectedResponseMessage != null ? lastSelectedResponseMessage : lastMessage;
			y = Math.max(internalPrevMaxY, externalPrevMaxY);

			//
			// Selected message
			//
			selectedMessage.paint(y);
			lastY = drawConnectorBoxes(null, lastMessage, y);
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
			lastY = y;
			for (Message message : selectedNext) {
				message.paint(y);
				y += MESSAGE_LINE_HEIGHT;
			}
			drawConnectorBox(initiatorFlow, lastY, selectedNext.size(), BoxType.CLOSED, Color.WHITE);
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
//			g2d.setColor(Color.GREEN);
			boolean startMessage = isStartMessage(selectedMessage.mitt);
			boolean endMessage = isEndMessage(selectedMessage.mitt);
			drawConnectorBox(getStartX(selectedMessage), lastY, 1, lastMsg == null ? BoxType.CLOSED : BoxType.OPEN_TOP,
					startMessage ? LIGHT_GREEN_1 : Color.WHITE);
			drawConnectorBox(getEndX(selectedMessage), lastY, 1,
					endMessage && selectedRequest.isEmpty() ? BoxType.CLOSED : BoxType.OPEN_BOTTOM,
					endMessage ? LIGHT_RED_1 : Color.WHITE);
//			g2d.setColor(Color.BLACK);
			lastY += MESSAGE_LINE_HEIGHT;
		} else if (!messageList.isEmpty()) {
			for (int index = 0; index < messageList.size(); index++) {
				Message prevMsg = index > 0 ? messageList.get(index - 1) : lastMsg;
				Message currMsg = messageList.get(index);
				Message nextMsg = index < messageList.size() - 1 ? messageList.get(index + 1) : null;
				boolean startMessage = isStartMessage(currMsg.mitt);
				boolean endMessage = isEndMessage(currMsg.mitt);

				if (prevMsg == null) {
					switch (currMsg.getState()) {
					case History:
						drawConnectorBox(getStartX(currMsg), lastY, 1, BoxType.CLOSED,
								startMessage ? LIGHT_GREEN_1 : Color.WHITE);
						drawConnectorBox(getEndX(currMsg), lastY, 1,
								nextMsg == null ? BoxType.CLOSED : BoxType.OPEN_BOTTOM,
								endMessage ? LIGHT_RED_1 : Color.WHITE);
						break;
					case Next:
						if (Control16.getTransaction(currMsg.mitt).getId().equals(selectedTransaction.getId())) {
							drawConnectorBox(getStartX(currMsg), lastY, 1,
									nextMsg == null ? BoxType.CLOSED : BoxType.OPEN_BOTTOM, Color.WHITE);
						} else {
							drawConnectorBox(getEndX(currMsg), lastY, 1, BoxType.OPEN_TOP_BOTTOM, Color.WHITE);
						}
						break;
					case Previous:
						if (Control16.getTransaction(currMsg.mitt).getId().equals(selectedTransaction.getId())) {
							drawConnectorBox(getEndX(currMsg), lastY, 1,
									nextMsg == null ? BoxType.CLOSED : BoxType.OPEN_BOTTOM, Color.WHITE);

						} else {
							drawConnectorBox(getEndX(currMsg), lastY, 1, BoxType.OPEN_BOTTOM, Color.WHITE);
						}
						break;
					default:
						break;
					}
				} else {
					switch (currMsg.getState()) {
					case History:
						drawConnectorBox(getStartX(currMsg), lastY, 1, BoxType.OPEN_TOP,
								startMessage ? LIGHT_GREEN_1 : Color.WHITE);
						drawConnectorBox(getEndX(currMsg), lastY, 1,
								nextMsg == null ? BoxType.CLOSED : BoxType.OPEN_BOTTOM,
								endMessage ? LIGHT_RED_1 : Color.WHITE);
						break;
					case Next:
						drawConnectorBox(getStartX(currMsg), lastY, 1,
								nextMsg == null ? BoxType.OPEN_TOP : BoxType.OPEN_TOP_BOTTOM, Color.WHITE);
						break;
					case Previous:
						drawConnectorBox(getEndX(currMsg), lastY, 1,
								nextMsg == null ? BoxType.OPEN_TOP : BoxType.OPEN_TOP_BOTTOM, Color.WHITE);
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
		TransactionTypeType transaction = Control16.getTransaction(currMsg.mitt);
		if (transaction.getId().equals(selectedTransaction.getId())) {
			return currMsg.mitt.isInitiatorToExecutor() ? executorFlow : initiatorFlow;
		} else {
			RoleTypeType selectedInitiator = Control16.getInitiator(selectedTransaction);
			RoleTypeType selectedExecutor = Control16.getExecutor(selectedTransaction);
			RoleTypeType initiator = Control16.getInitiator(currMsg.mitt);
			RoleTypeType executor = Control16.getExecutor(currMsg.mitt);
			if (initiator.getId().equals(selectedInitiator.getId())) {
				return initiatorFlow;
			} else if (initiator.getId().equals(selectedExecutor.getId())) {
				return executorFlow;
			} else if (executor.getId().equals(selectedInitiator.getId())) {
				return initiatorFlow;
			} else if (executor.getId().equals(selectedExecutor.getId())) {
				return executorFlow;
			} else if (selectedRequest.contains(currMsg)) {
				return selectedMessage.mitt.isInitiatorToExecutor() ? executorFlow : initiatorFlow;
			} else if (selectedResponse.contains(currMsg)) {
				return selectedMessage.mitt.isInitiatorToExecutor() ? initiatorFlow : executorFlow;
			}
			return 0;
		}
	}

	private int getStartX(Message currMsg) {
		TransactionTypeType transaction = Control16.getTransaction(currMsg.mitt);
		if (transaction.getId().equals(selectedTransaction.getId())) {
			return currMsg.mitt.isInitiatorToExecutor() ? initiatorFlow : executorFlow;
		} else {
			RoleTypeType selectedInitiator = Control16.getInitiator(selectedTransaction);
			RoleTypeType selectedExecutor = Control16.getExecutor(selectedTransaction);
			RoleTypeType initiator = Control16.getInitiator(currMsg.mitt);
			RoleTypeType executor = Control16.getExecutor(currMsg.mitt);
			if (initiator.getId().equals(selectedInitiator.getId())) {
				return initiatorFlow;
			} else if (initiator.getId().equals(selectedExecutor.getId())) {
				return executorFlow;
			} else if (executor.getId().equals(selectedInitiator.getId())) {
				return initiatorFlow;
			} else if (executor.getId().equals(selectedExecutor.getId())) {
				return executorFlow;
			} else if (selectedRequest.contains(currMsg)) {
				return selectedMessage.mitt.isInitiatorToExecutor() ? executorFlow : initiatorFlow;
			} else if (selectedResponse.contains(currMsg)) {
				return selectedMessage.mitt.isInitiatorToExecutor() ? initiatorFlow : executorFlow;
			}
			return 0;
		}
	}

	private enum BoxType {
		CLOSED, OPEN_TOP, OPEN_BOTTOM, OPEN_TOP_BOTTOM;
	};

	private void drawConnectorBox(int x, int y, int size, BoxType type, Color fill) {
		if (size > 0) {
			int x1 = x - 10;
			int x2 = x1 + 20;
			int y1 = y - 2;
			int y2 = y1 + size * 20;
			switch (type) {
			case CLOSED:
				g2d.clearRect(x1, y1, 20, size * 20);
				Color saveColor = g2d.getColor();
				g2d.setColor(fill);
				g2d.fillRect(x1, y1, 20, size * 20);
				g2d.setColor(saveColor);
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

	void initNewDiagram() {
		System.out.println("initNewDiagram");
		if (Canvas16.this.getComponentCount() > 0) {
			Canvas16.this.removeAll();
			Canvas16.this.repaint();
		}
		initiator = new Role(Control16.getInitiator(selectedTransaction), leftMargin - 50, 25, LIGHT_RED_3);
		executor = new Role(Control16.getExecutor(selectedTransaction), leftMargin + middleMargin, 25, LIGHT_GREEN_3);
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

	private boolean isStartMessage(MessageInTransactionTypeType mitt) {
		if (mitt.isFirstMessage() != null && mitt.isFirstMessage())
			return true;
		if (!Control16.getTransaction(mitt).getId().equals(selectedTransaction.getId())) {
			return false;
		}
		List<MessageInTransactionTypeType> prevMitts = Control16.getPrevious(mitt);
		if (prevMitts == null) {
			return true;
		}
		for (MessageInTransactionTypeType prev : prevMitts) {
			if (Control16.getTransaction(prev).getId().equals(selectedTransaction.getId())) {
				return false;
			}
		}
		return true;
	}

	private boolean isEndMessage(MessageInTransactionTypeType mitt) {
		if (!Control16.getTransaction(mitt).getId().equals(selectedTransaction.getId())) {
			return false;
		}
		List<MessageInTransactionTypeType> nextMitts = Control16.getNext(mitt);
		if (nextMitts == null) {
			return true;
		}
		for (MessageInTransactionTypeType next : nextMitts) {
			if (Control16.getTransaction(next).getId().equals(selectedTransaction.getId())) {
				return false;
			}
		}
		return true;
	}

	MessageInTransactionTypeType addMitt2Canvas(String messageId, boolean resetDiagrams) {
		TransactionsPanelControl16 transactionsPC = MainPanelControl16.getTransactionsPC();
		transactionsPC.canvas16Plane.selectedTransaction = transactionsPC.selectedElement;
		transactionPanel.cbx_Messages.setSelectedItem(messageId);
		MessageInTransactionTypeType mitt = transactionPanel.addMessage();
		initNewDiagram();
		Message message = new Message(mitt);
		message.state = MessageState.Next;
		selectedNext.add(message);
		if (resetDiagrams) {
			reset();
			transactionPanel.reset();
		}
		return mitt;
	}

	public void reset() {
		currentTransaction = null;
		repaint();
	}

}
