package nl.visi.interaction_framework.editor.v16;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.visi.interaction_framework.editor.DateField;
import nl.visi.interaction_framework.editor.DocumentAdapter;
import nl.visi.interaction_framework.editor.InteractionFrameworkEditor;
import nl.visi.interaction_framework.editor.ui.RotatingButton;
import nl.visi.schemas._20160331.ElementConditionType;
import nl.visi.schemas._20160331.ElementConditionType.MessageInTransaction;
import nl.visi.schemas._20160331.GroupTypeType;
import nl.visi.schemas._20160331.GroupTypeTypeRef;
import nl.visi.schemas._20160331.MessageInTransactionTypeType;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Group;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Message;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Previous;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Transaction;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.TransactionPhase;
import nl.visi.schemas._20160331.MessageTypeType;
import nl.visi.schemas._20160331.MessageTypeTypeRef;
import nl.visi.schemas._20160331.ObjectFactory;
import nl.visi.schemas._20160331.RoleTypeType;
import nl.visi.schemas._20160331.RoleTypeTypeRef;
import nl.visi.schemas._20160331.TransactionPhaseTypeType;
import nl.visi.schemas._20160331.TransactionPhaseTypeTypeRef;
import nl.visi.schemas._20160331.TransactionTypeType;
import nl.visi.schemas._20160331.TransactionTypeType.Executor;
import nl.visi.schemas._20160331.TransactionTypeType.Initiator;
import nl.visi.schemas._20160331.TransactionTypeType.SubTransactions;
import nl.visi.schemas._20160331.TransactionTypeTypeRef;

public class TransactionsPanelControl16 extends PanelControl16<TransactionTypeType> {
	private static final String TRANSACTIONS_PANEL = "nl/visi/interaction_framework/editor/swixml/TransactionsPanel16.xml";

	private JPanel startDatePanel, endDatePanel, canvasPanel, canvas2Panel, sequencePanel, elementConditionPanel,
			elementsTreePanel;
	JTabbedPane transactionTabs;
	JTable tbl_Messages;

	private JTable tbl_Subtransactions;
	private JTextField tfd_Result;
	private JComboBox<String> cbx_Initiator, cbx_Executor;

	JComboBox<String> cbx_Messages;

	private JComboBox<String> cbx_TransactionPhases;

	private JComboBox<String> cbx_Groups;
	private MessagesTableModel messagesTableModel;
	private SequenceTable sequenceTable;
	ElementConditionTable elementConditionTable;
	private MessageInTransactionDialogControl16 messageInTransactionDialogControl;
	private SubtransactionsTableModel subtransactionsTableModel;
	private JButton btn_AddMessage, btn_EditMessage, btn_RemoveMessage, btn_Reverse, btn_NavigateInitiator,
			btn_NavigateExecutor;
	private JTextArea tar_Initiator, tar_Executor;
	private JScrollPane scrollPane, scrollPane2;
	private Canvas drawingPlane;
	Canvas16 canvas16Plane;
	private Canvas.MessageItem activeItem;

	private Map<MessageInTransactionTypeType, List<MessageInTransactionTypeType>> successorMap;

	@SuppressWarnings("serial")
	public class Canvas extends JPanel {
		private Dimension preferredSize;
		private Role init, exec;
		private final List<MessageItem> messages;
		private final Map<Integer, TransactionConnection> tcMap;

		private TransactionTypeType currentTransaction;
		private int leftMargin, rightMargin, middleMargin;

		private void showButton(final Canvas canvas, int x, int y, MessageInTransactionTypeType mitt, String label,
				String toolTipText) {
			if (!printMode) {
				int hash = (Integer.toString(x) + Integer.toString(y)).hashCode();
				TransactionConnection tc = tcMap.get(hash);
				if (tc == null) {
					tc = new TransactionConnection(canvas, mitt, label, toolTipText, x, y);
					tc.activeLabel.setText(tc.label);
					tc.activeLabel.setToolTipText(tc.toolTipText);
					tc.activeLabel.setContentAreaFilled(false);
					tc.activeLabel.setBackground(Color.white);
					tc.activeLabel.setBorderPainted(false);
					tc.activeLabel.setBorder(null);
					tc.activeLabel.setMargin(new Insets(0, 0, 0, 0));
					tc.activeLabel.setFont(tc.font);
					tc.activeLabel.setLocation(x, y - 10);
				}
				List<Component> components = Arrays.asList(canvas.getComponents());
				if (!components.contains(tc.activeLabel)) {
					canvas.add(tc.activeLabel);
					canvas.revalidate();
				}
			}
		}

		private class TransactionConnection {
			private final Font font = new Font("Dialog", Font.PLAIN, 10);
			private RotatingButton activeLabel;
			private String label, toolTipText;

			public TransactionConnection(final Canvas canvas, final MessageInTransactionTypeType mitt, String label,
					String toolTipText, int x, int y) {
				int hash = (Integer.toString(x) + Integer.toString(y)).hashCode();
				tcMap.put(hash, this);
				this.label = label;
				if (!printMode) {
					this.activeLabel = new RotatingButton(label);
					this.toolTipText = toolTipText;
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
							InteractionFrameworkEditor.navigate(getTransaction(mitt));
						}
					});
				}
			}
		}

		private class Role {
			private int x, y;
			private String label;
			private final Canvas canvas;
			private RotatingButton activeLabel;

			Role(final Canvas canvas, int x, int y, boolean isInit) {
				this.canvas = canvas;
				this.x = x;
				this.y = y;

				if (!printMode) {
					activeLabel = new RotatingButton();
					if (isInit) {
						RoleTypeType initiator = getInitiator(selectedElement);
						activeLabel.setText(initiator != null ? initiator.getDescription() : "?");
						activeLabel.setToolTipText(initiator != null ? initiator.getId() : "?");
					} else {
						RoleTypeType executor = getExecutor(selectedElement);
						activeLabel.setText(executor != null ? executor.getDescription() : "?");
						activeLabel.setToolTipText(executor != null ? executor.getId() : "?");
					}
					activeLabel.setContentAreaFilled(false);
					activeLabel.setBackground(Color.white);
					activeLabel.setBorderPainted(false);
					activeLabel.setFont(getFont().deriveFont(getFont().getSize() - 2.0f));
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
							InteractionFrameworkEditor.navigate(getRole());
						}
					});
				}
			}

			void paint(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				label = getLabel();
				if (label != null) {
					g2.drawRect(x, y, 100, 50);
					g2.setFont(getFont().deriveFont(getFont().getSize() - 2.0f));
					int stringWidth = g2.getFontMetrics().stringWidth(label);
					if (printMode) {
						g2.drawString(label, x + 50 - (stringWidth / 2), y + 25);
					} else {
						showButton(canvas, x + 50 - (stringWidth / 2), y + 25);
					}
					Stroke saveStroke = g2.getStroke();
					float dash[] = { 5.0f };
					g2.setStroke(
							new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
					g2.drawLine(x + 50, y + 50, x + 50, getHeight() - 10);
					g2.setStroke(saveStroke);
				}
			}

			public void showButton(final Canvas canvas, int x, int y) {
				activeLabel.setLocation(x - 10, y - 10);
				List<Component> components = Arrays.asList(canvas.getComponents());
				if (!components.contains(activeLabel)) {
					canvas.add(activeLabel);
					canvas.revalidate();
				}
			}

			private String getLabel() {
				if (this == init) {
					RoleTypeType initiator = getInitiator(selectedElement);
					return initiator != null ? initiator.getDescription() : "?";
				} else {
					RoleTypeType executor = getExecutor(selectedElement);
					return executor != null ? executor.getDescription() : "?";
				}
			}

			private RoleTypeType getRole() {
				if (this == init) {
					return getInitiator(selectedElement);
				} else {
					return getExecutor(selectedElement);
				}
			}
		};

		private class MessageItem {
			private List<String> incomingTransactions, outgoingTransactions;
			private List<MessageInTransactionTypeType> incomingMitts, outgoingMitts;
			private final MessageInTransactionTypeType mitt;
			private String name;
			private int x, y;
			private List<MessageItem> incomingConnections, outgoingConnections;
			private boolean initiatorToExecutor, loop, endMitt, startMitt, linked;
			private Font font;
			private RotatingButton activeLabel;

			public MessageItem(final Canvas canvas, MessageInTransactionTypeType mitt) {
				this.mitt = mitt;
				MessageTypeType messageType = getMessage(mitt);
				name = messageType != null ? messageType.getDescription() : null;
				this.font = new Font("Dialog", Font.PLAIN, 11);
				if (!printMode) {
					activeLabel = new RotatingButton(name);
					String toolTip = mitt.getId();
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

					activeLabel.setToolTipText(toolTip);
					activeLabel.setContentAreaFilled(false);
					activeLabel.setBackground(Color.white);
					activeLabel.setBorderPainted(false);
					activeLabel.setFont(font);
					activeLabel.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseEntered(MouseEvent e) {
							e.getComponent().setForeground(Color.red);
							canvas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
						}

						@Override
						public void mouseExited(MouseEvent e) {
							if (activeItem != null && e.getComponent() == activeItem.activeLabel) {
								e.getComponent().setForeground(Color.blue);
							} else {
								e.getComponent().setForeground(Color.black);
							}
							canvas.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}

						@Override
						public void mouseClicked(MouseEvent e) {
							if (SwingUtilities.isRightMouseButton(e)) {
								if (activeItem != null) {
									activeItem.activeLabel.setForeground(Color.black);
								}
								activeItem = MessageItem.this;
								// popupMenu.show(e.getComponent(), e.getX(), e.getY());
							} else {
								if (e.getClickCount() == 2) {
									InteractionFrameworkEditor.navigate(getMessage(MessageItem.this.mitt));
								} else {
									if (activeItem != null) {
										activeItem.activeLabel.setForeground(Color.black);
									}
									activeItem = MessageItem.this;
									editMitt();
								}
							}
						}
					});
				}
				this.loop = false;
				this.incomingConnections = new ArrayList<TransactionsPanelControl16.Canvas.MessageItem>();
				this.outgoingConnections = new ArrayList<TransactionsPanelControl16.Canvas.MessageItem>();
			}

			public void showButton(final Canvas canvas) {
				activeLabel.setLocation(getX() - 10, getY() - 10);
				List<Component> components = Arrays.asList(canvas.getComponents());
				if (!components.contains(activeLabel)) {
					canvas.add(activeLabel);
					canvas.revalidate();
				}
			}

			public MessageInTransactionTypeType getMitt() {
				return this.mitt;
			}

			public String getName() {
				return this.name;
			}

			public int getX() {
				return this.x;
			}

			public void setX(int x) {
				this.x = x;
			}

			public int getY() {
				return this.y;
			}

			public void setY(int y) {
				this.y = y;
			}

			public Iterator<MessageItem> getIncomingConnections() {
				return incomingConnections.iterator();
			}

			public void addIncomingConnection(MessageItem connection) {
				incomingConnections.add(connection);
			}

			public Iterator<MessageItem> getOutgoingConnections() {
				return outgoingConnections.iterator();
			}

			public void addOutgoingConnection(MessageItem connection) {
				outgoingConnections.add(connection);
			}

			public boolean isInitiatorToExecutor() {
				return initiatorToExecutor;
			}

			public void setInitiatorToExecutor(boolean initiatorToExecutor) {
				this.initiatorToExecutor = initiatorToExecutor;
			}

			public boolean isLoop() {
				return loop;
			}

			public void setLoop(boolean loop) {
				this.loop = loop;
			}

			public boolean isLinked() {
				return linked;
			}

			public void setLinked(boolean linked) {
				this.linked = linked;
			}

			public boolean isEndMitt() {
				return endMitt;
			}

			public void setEndMitt(boolean endMitt) {
				this.endMitt = endMitt;
			}

			public boolean isStartMitt() {
				return startMitt;
			}

			public void setStartMitt(boolean startMitt) {
				this.startMitt = startMitt;
			}

			public List<String> getIncomingTransactions() {
				return incomingTransactions;
			}

			public void setIncomingTransactions(List<MessageInTransactionTypeType> incomingTransactions) {
				this.incomingTransactions = new ArrayList<String>();
				this.incomingMitts = new ArrayList<>();
				for (int index = 0; index < incomingTransactions.size(); index++) {
					MessageInTransactionTypeType mitt = incomingTransactions.get(index);
					this.incomingMitts.add(mitt);
					String label = getLabel(mitt);
					this.incomingTransactions.add(label);
				}
			}

			public List<String> getOutgoingTransactions() {
				return outgoingTransactions;
			}

			public void setOutgoingTransactions(List<MessageInTransactionTypeType> outgoingTransactions) {
				this.outgoingTransactions = new ArrayList<String>();
				this.outgoingMitts = new ArrayList<>();
				for (int index = 0; index < outgoingTransactions.size(); index++) {
					MessageInTransactionTypeType mitt = outgoingTransactions.get(index);
					this.outgoingMitts.add(mitt);
					String label = getLabel(mitt);
					this.outgoingTransactions.add(label);
				}
			}

			private String getLabel(MessageInTransactionTypeType incomingMitt) {
				String incomingTransaction = getTransaction(incomingMitt).getDescription();
				String incomingMessage = getMessage(incomingMitt).getDescription();
				String label = incomingTransaction.substring(0, Math.min(incomingTransaction.length(), 12));
				label += incomingTransaction.length() > 12 ? ".../" : "/";
				label += incomingMessage.substring(0, Math.min(incomingMessage.length(), 18));
				label += incomingMessage.length() > 12 ? "..." : "";
				return label;
			}

		}

		public Canvas() {
			setLayout(null);
			preferredSize = new Dimension(getWidth(), getHeight());
			setSize(getPreferredSize());
			messages = new ArrayList<TransactionsPanelControl16.Canvas.MessageItem>();
			tcMap = new HashMap<>();
		}

		public Graphics2D g2d;
		int previousMiddleMargin;

		@Override
		public Dimension getPreferredSize() {
			return preferredSize;
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g2d = (Graphics2D) g;
			if (selectedElement == null) {
				reset(g2d);
				return;
			} else {
				int width = getWidth() / 3;
				middleMargin = width > 300 ? width : 300;
				leftMargin = (getWidth() - middleMargin) / 2;
				rightMargin = (getWidth() - middleMargin) / 2;
			}

			boolean newDrawing = selectedElement != currentTransaction;
			reset(g2d);

			boolean endMitt = false;
			boolean prevMitt = false;
			boolean lastDirection = true;
			if (newDrawing) {
				previousMiddleMargin = middleMargin;
				List<MessageInTransactionTypeType> initGroup = new ArrayList<MessageInTransactionTypeType>();
				List<MessageInTransactionTypeType> execGroup = new ArrayList<MessageInTransactionTypeType>();
				for (int index = 0; index < messagesTableModel.getRowCount(); index++) {
					MessageInTransactionTypeType mitt = messagesTableModel.get(index);
					MessageTypeType messageType = getMessage(mitt);
					if (messageType != null) {
						MessageItem item = new MessageItem(this, mitt);
						messages.add(item);
						item.setStartMitt(TransactionsPanelControl16.this.isStart(mitt, selectedElement));
						item.setEndMitt(isEndMitt(mitt));
						Boolean initiatorToExecutor = mitt.isInitiatorToExecutor();
						item.setInitiatorToExecutor(initiatorToExecutor != null ? initiatorToExecutor : true);
						item.setIncomingTransactions(incomingTransaction(mitt));
						item.setOutgoingTransactions(outgoingTransaction(mitt));
						if (item.isInitiatorToExecutor()) {
							item.setLoop(isLoop(mitt, execGroup));
							initGroup.add(mitt);
							if (!item.isLoop()) {
								execGroup.clear();
							}
							if (index > 0) {
								MessageInTransactionTypeType prevMsg = null;
								boolean init2Exec = true;
								for (int i = index - 1; init2Exec && i >= 0; i--) {
									prevMsg = messagesTableModel.get(i);
									init2Exec = prevMsg.isInitiatorToExecutor() != null
											? prevMsg.isInitiatorToExecutor()
											: true;
								}

								item.setLinked(!init2Exec && successorMap.get(prevMsg) != null
										&& successorMap.get(prevMsg).contains(mitt));
								workAround(mitt, item);
							}
						} else {
							item.setLoop(isLoop(mitt, initGroup));
							execGroup.add(mitt);
							if (!item.isLoop()) {
								initGroup.clear();
							}
							if (index > 0) {
								MessageInTransactionTypeType prevMsg = null;
								boolean init2Exec = false;
								for (int i = index - 1; !init2Exec && i >= 0; i--) {
									prevMsg = messagesTableModel.get(i);
									init2Exec = prevMsg.isInitiatorToExecutor() != null
											? prevMsg.isInitiatorToExecutor()
											: true;
								}
								item.setLinked(init2Exec && successorMap.get(prevMsg) != null
										&& successorMap.get(prevMsg).contains(mitt));

								workAround(mitt, item);
							}
						}
					}
				}
			}
			endMitt = false;
			prevMitt = false;

			String title = selectedElement.getDescription();
			if (title == null || title.length() == 0) {
				title = selectedElement.getId();
			}
			int titleWidth = g2d.getFontMetrics().stringWidth(title);
			g2d.drawString(title, (getWidth() - titleWidth) / 2, 18);

			if (init == null) {
				init = new Role(this, leftMargin - 50, 25, true);
			} else {
				init.x = leftMargin - 50;
			}
			if (exec == null) {
				exec = new Role(this, leftMargin + middleMargin - 50, 25, false);
			} else {
				exec.x = leftMargin + middleMargin - 50;
			}
			init.paint(g2d);
			exec.paint(g2d);

			int yInitStart = init.y + 65;
			int yInitHeight = -5;
			int yExecStart = exec.y + 65;
			int yExecHeight = -5;
			int y = init.y + 50;
			int init_dx = 35;
			int exec_dx = 10;

			for (int index = 0; index < messages.size(); index++) {
				MessageItem item = messages.get(index);
				prevMitt = endMitt;
				endMitt = item.isEndMitt();

				y += 20;
				int stringWidth = g.getFontMetrics().stringWidth(item.getName());
				if (stringWidth + 100 > middleMargin) {
					middleMargin = stringWidth + 100;
					leftMargin = (getWidth() - middleMargin) / 2;
					rightMargin = leftMargin;
				}
				item.setX((exec.x - init.x - stringWidth) / 2 + init.x + 50);
				item.setY(y);
				g2d.drawString(item.getName(), item.getX(), item.getY());
				item.showButton(this);
				g2d.drawLine(init.x + 55, y, exec.x + 45, y);
				if (item.isInitiatorToExecutor()) {

					/*
					 * if (!item.isLoop() && (lastDirection != item.isInitiatorToExecutor() ||
					 * prevMitt || endMitt)) { yExecStart += yExecHeight + 5; yExecHeight = 15; }
					 * else { yExecHeight += 20; }
					 */

					// --------------------------------------------------------------
					if (!item.isLoop() && ((lastDirection != item.isInitiatorToExecutor() || prevMitt || endMitt)
							|| (index > 0 && messages.get(index - 1).isLoop()
									&& lastDirection == item.isInitiatorToExecutor()))) {
						yExecStart += yExecHeight + 5;
						yExecHeight = 15;
					} else {
						yExecHeight += 20;
					}
					// --------------------------------------------------------------

					/*
					 * if ((lastDirection != item.isInitiatorToExecutor()) && index > 0 &&
					 * !item.isLinked()) { yInitStart += yInitHeight + 5; yInitHeight = ((index ==
					 * messages.size() - 1) !item // .isLinked()) ? -5 : 15; }
					 */

					// --------------------------------------------------------------
					if (index > 0 && !item.isLinked()) {
						yInitStart += yInitHeight + 5;
						yInitHeight = ((index == messages.size() - 1) || !item.isLinked()) ? -5 : 15;
					}
					// --------------------------------------------------------------

					if (index > 0) {
						Iterator<MessageItem> incomingConnections = item.getIncomingConnections();
						while (incomingConnections.hasNext()) {
							MessageItem connection = incomingConnections.next();
							int connectionY = connection.getY();
							if (yInitStart > connectionY) {
								int x0 = init.x + init_dx;
								int x1 = init.x + 45;
								g2d.drawLine(x0 + 5, connectionY, x1, connectionY);
								g2d.drawArc(x0, connectionY, 10, 10, 90, 90);
								g2d.drawLine(x0, connectionY + 5, x0, y - 5);
								g2d.drawArc(x0, y - 10, 10, 10, 180, 90);
								g2d.drawLine(x0 + 5, y, x1, y);
								g2d.drawLine(x1 - 5, y - 3, x1, y);
								g2d.drawLine(x1 - 5, y + 3, x1, y);
								init_dx -= 10;
							}
						}
						Iterator<MessageItem> outgoingConnections = item.getOutgoingConnections();
						while (outgoingConnections.hasNext()) {
							MessageItem connection = outgoingConnections.next();
							int connectionY = connection.getY();
							if (yExecStart > connectionY) {
								int x0 = exec.x + 55 + exec_dx;
								int x1 = exec.x + 55;
								g2d.drawLine(x0 + 5, connectionY, x1, connectionY);
								g2d.drawLine(x1 + 5, connectionY - 3, x1, connectionY);
								g2d.drawLine(x1 + 5, connectionY + 3, x1, connectionY);
								g2d.drawArc(x0, connectionY, 10, 10, 0, 90);
								g2d.drawLine(x0 + 10, connectionY + 5, x0 + 10, y - 5);
								g2d.drawArc(x0, y - 10, 10, 10, 270, 90);
								g2d.drawLine(x0 + 5, y, x1, y);
								exec_dx -= 10;
							}
						}
					}
					yInitHeight += 20;
					int xEnd = exec.x + 45;
					g2d.drawLine(xEnd, y, xEnd - 5, y - 3);
					g2d.drawLine(xEnd, y, xEnd - 5, y + 3);
					List<String> incomingTransactions = item.getIncomingTransactions();
					List<String> outgoingTransactions = item.getOutgoingTransactions();
					if (item.isStartMitt() && incomingTransactions.size() == 0) {
						xEnd = init.x + 45;
						g2d.drawLine(init.x + 5, y, xEnd, y);
						g2d.drawLine(xEnd, y, xEnd - 5, y - 3);
						g2d.drawLine(xEnd, y, xEnd - 5, y + 3);
						drawInitExitPoint(g2d, y, init.x - 3, false);
					}
					if (item.isEndMitt() && outgoingTransactions.size() == 0) {
						xEnd = exec.x + 95;
						g2d.drawLine(exec.x + 55, y, xEnd, y);
						g2d.drawLine(xEnd, y, xEnd - 5, y - 3);
						g2d.drawLine(xEnd, y, xEnd - 5, y + 3);
						drawInitExitPoint(g2d, y, xEnd, true);
					}
					int deltaY = 10;
					int max = Math.max(incomingTransactions.size(), outgoingTransactions.size());
					if (max > 0) {
						for (int i = 0; i < max; i++) {
							String incomingMitt = i < incomingTransactions.size() ? incomingTransactions.get(i) : null;
							String outgoingMitt = i < outgoingTransactions.size() ? outgoingTransactions.get(i) : null;
							if (incomingMitt != null) {
								String label = incomingMitt;
								xEnd = init.x + 45;
								g2d.drawLine(init.x + 5, y, xEnd, y);
								g2d.drawLine(xEnd, y, xEnd - 5, y - 3);
								g2d.drawLine(xEnd, y, xEnd - 5, y + 3);
								stringWidth = g2d.getFontMetrics().stringWidth(label);
								if (printMode) {
									g2d.drawString(label, init.x + 5 - stringWidth, y);
								} else {
									showButton(this, init.x + 5 - stringWidth, y, item.incomingMitts.get(i), label,
											item.incomingMitts.get(i).getId());
								}
								if (50 + stringWidth > leftMargin) {
									leftMargin = 50 + stringWidth;
								}
							}
							if (outgoingMitt != null) {
								String label = outgoingMitt;
								xEnd = exec.x + 95;
								g2d.drawLine(exec.x + 55, y, xEnd, y);
								g2d.drawLine(xEnd, y, xEnd - 5, y - 3);
								g2d.drawLine(xEnd, y, xEnd - 5, y + 3);
								stringWidth = g2d.getFontMetrics().stringWidth(label);
								if (printMode) {
									g2d.drawString(label, exec.x + 95, y);
								} else {
									showButton(this, exec.x + 95, y, item.outgoingMitts.get(i), label,
											item.outgoingMitts.get(i).getId());
								}
								if (50 + stringWidth > rightMargin) {
									rightMargin = 50 + stringWidth;
								}
							}
							y += deltaY;
							yInitHeight += deltaY;
							yExecHeight += deltaY;
						}
						y -= deltaY;
						yInitHeight -= deltaY;
						yExecHeight -= deltaY;
					}
				} else {
					/*
					 * if (!item.isLoop() && (lastDirection != item.isInitiatorToExecutor() ||
					 * prevMitt || endMitt)) { yInitStart += yInitHeight + 5; yInitHeight = 15; }
					 * else { yInitHeight += 20; }
					 */

					// --------------------------------------------------------------
					if (!item.isLoop() && ((lastDirection != item.isInitiatorToExecutor() || prevMitt || endMitt)
							|| (index > 0 && messages.get(index - 1).isLoop()
									&& lastDirection == item.isInitiatorToExecutor()))) {
						yInitStart += yInitHeight + 5;
						yInitHeight = 15;
					} else {
						yInitHeight += 20;
					}
					// --------------------------------------------------------------

					/*
					 * if ((lastDirection != item.isInitiatorToExecutor()) && index > 0 &&
					 * !item.isLinked()) { yExecStart += yExecHeight + 5; yExecHeight = (index ==
					 * messages.size() - 1 || !item .isLinked()) ? -5 : 15; }
					 */

					// --------------------------------------------------------------
					if (index > 0 && !item.isLinked()) {
						yExecStart += yExecHeight + 5;
						yExecHeight = (index == messages.size() - 1 || !item.isLinked()) ? -5 : 15;
					}
					// --------------------------------------------------------------

					if (index > 0) {
						Iterator<MessageItem> connections = item.getIncomingConnections();
						while (connections.hasNext()) {
							MessageItem connection = connections.next();
							int connectionY = connection.getY();
							if (yExecStart > connectionY) {
								int x0 = exec.x + 55 + exec_dx;
								int x1 = exec.x + 55;
								g2d.drawLine(x0 - 5, connectionY, x1, connectionY);
								g2d.drawArc(x0 - 10, connectionY, 10, 10, 0, 90);
								g2d.drawLine(x0, connectionY + 5, x0, y - 5);
								g2d.drawArc(x0 - 10, y - 10, 10, 10, 270, 90);
								g2d.drawLine(x0 - 5, y, x1, y);
								g2d.drawLine(x1, y, x1 + 5, y - 3);
								g2d.drawLine(x1, y, x1 + 5, y + 3);
								exec_dx += 15;
							}
						}
						Iterator<MessageItem> outgoingConnections = item.getOutgoingConnections();
						while (outgoingConnections.hasNext()) {
							MessageItem connection = outgoingConnections.next();
							int connectionY = connection.getY();
							if (yInitStart > connectionY) {
								int x0 = init.x + init_dx;
								int x1 = init.x + 45;
								g2d.drawLine(x0 + 5, connectionY, x1, connectionY);
								g2d.drawLine(x1 - 5, connectionY - 3, x1, connectionY);
								g2d.drawLine(x1 - 5, connectionY + 3, x1, connectionY);
								g2d.drawArc(x0, connectionY, 10, 10, 90, 90);
								g2d.drawLine(x0, connectionY + 5, x0, y - 5);
								g2d.drawArc(x0, y - 10, 10, 10, 180, 90);
								g2d.drawLine(x0 + 5, y, x1, y);
								init_dx -= 10;
							}
						}
					}
					yExecHeight += 20;
					int xEnd = init.x + 55;
					g2d.drawLine(xEnd, y, xEnd + 5, y - 3);
					g2d.drawLine(xEnd, y, xEnd + 5, y + 3);
					List<String> incomingTransactions = item.getIncomingTransactions();
					List<String> outgoingTransactions = item.getOutgoingTransactions();
					if (item.isStartMitt() && incomingTransactions.size() == 0) {
						xEnd = exec.x + 55;
						g2d.drawLine(exec.x + 95, y, xEnd, y);
						g2d.drawLine(xEnd, y, xEnd + 5, y - 3);
						g2d.drawLine(xEnd, y, xEnd + 5, y + 3);
						drawInitExitPoint(g2d, y, exec.x + 95, false);
					}
					if (endMitt && outgoingTransactions.size() == 0) {
						xEnd = init.x + 5;
						g2d.drawLine(init.x + 45, y, xEnd, y);
						g2d.drawLine(xEnd, y, xEnd + 5, y - 3);
						g2d.drawLine(xEnd, y, xEnd + 5, y + 3);
						drawInitExitPoint(g2d, y, xEnd - 8, true);
					}
					int deltaY = 10;
					int max = Math.max(incomingTransactions.size(), outgoingTransactions.size());
					if (max > 0) {
						for (int i = 0; i < max; i++) {
							String incomingMitt = i < incomingTransactions.size() ? incomingTransactions.get(i) : null;
							String outgoingMitt = i < outgoingTransactions.size() ? outgoingTransactions.get(i) : null;
							if (incomingMitt != null) {
								String label = incomingMitt;
								xEnd = exec.x + 55;
								g2d.drawLine(xEnd, y, exec.x + 95, y);
								g2d.drawLine(xEnd, y, xEnd + 5, y - 3);
								g2d.drawLine(xEnd, y, xEnd + 5, y + 3);
								stringWidth = g2d.getFontMetrics().stringWidth(label);
								if (printMode) {
									g2d.drawString(label, exec.x + 95, y);
								} else {
									showButton(this, exec.x + 95, y, item.incomingMitts.get(i), label,
											item.incomingMitts.get(i).getId());
								}
								if (50 + stringWidth > rightMargin) {
									rightMargin = 50 + stringWidth;
								}
							}
							if (outgoingMitt != null) {
								String label = outgoingMitt;
								xEnd = init.x + 5;
								g2d.drawLine(init.x + 45, y, xEnd, y);
								g2d.drawLine(xEnd, y, xEnd + 5, y - 3);
								g2d.drawLine(xEnd, y, xEnd + 5, y + 3);
								stringWidth = g2d.getFontMetrics().stringWidth(label);
								if (printMode) {
									g2d.drawString(label, xEnd - stringWidth, y);
								} else {
									showButton(this, xEnd - stringWidth, y, item.outgoingMitts.get(i), label,
											item.outgoingMitts.get(i).getId());
								}
								if (50 + stringWidth > leftMargin) {
									leftMargin = 50 + stringWidth;
								}
							}
							y += deltaY;
							yInitHeight += deltaY;
							yExecHeight += deltaY;
						}
						y -= deltaY;
						yInitHeight -= deltaY;
						yExecHeight -= deltaY;
					}
				}
				g2d.setColor(Color.WHITE);
				g2d.fillRect(init.x + 45, yInitStart, 10, yInitHeight);
				g2d.fillRect(exec.x + 45, yExecStart, 10, yExecHeight);
				g2d.setColor(Color.BLACK);
				g2d.drawRect(init.x + 45, yInitStart, 10, yInitHeight);
				g2d.drawRect(exec.x + 45, yExecStart, 10, yExecHeight);
				lastDirection = item.isInitiatorToExecutor();
			}

			int height = preferredSize.height;
			int width = preferredSize.width;
			preferredSize = new Dimension(scrollPane.getWidth(), yInitStart + yInitHeight + 20);

			if (height != preferredSize.height || width != preferredSize.width
					|| previousMiddleMargin != middleMargin) {
				previousMiddleMargin = middleMargin;
				// System.out.println("width=" + width + " preferredSize.width=" +
				// preferredSize.width);
				removeAll();
				tcMap.clear();
				setSize(getPreferredSize());
				canvasPanel.revalidate();
			}
		}

		private boolean printMode = false;

		public void print(Graphics graphics, int startLine, int linesPerPage) {
			printMode = true;
			paintComponent(graphics);
			printMode = false;
		}

		private void workAround(MessageInTransactionTypeType mitt, MessageItem item) {
			if ((item.getIncomingTransactions() == null || item.getIncomingTransactions().size() == 0)
					&& mitt.getPrevious() != null) {
				List<MessageInTransactionTypeType> previous = getPrevious(mitt);
				for (MessageInTransactionTypeType prevMess : previous) {
					for (MessageItem mi : messages) {
						if (mi.getMitt().equals(prevMess)) {
							item.addIncomingConnection(mi);
						}
					}
				}
			}
			if (item.getOutgoingTransactions() == null || item.getOutgoingTransactions().size() == 0) {
				for (MessageItem mi : messages) {
					MessageInTransactionTypeType miMitt = mi.getMitt();
					List<MessageInTransactionTypeType> previous = getPrevious(miMitt);
					if (previous != null) {
						for (MessageInTransactionTypeType prevMess : previous) {
							if (prevMess.equals(mitt)) {
								item.addOutgoingConnection(mi);
							}
						}
					}
				}
			}
		}

		private void drawInitExitPoint(Graphics2D g2d, int y, int xEnd, boolean exit) {
			g2d.setColor(exit ? Color.red : Color.green);
			g2d.fillOval(xEnd, y - 4, 8, 8);
			g2d.setColor(Color.black);
			g2d.drawOval(xEnd, y - 4, 8, 8);
		}

		private boolean isLoop(MessageInTransactionTypeType mitt, List<MessageInTransactionTypeType> group) {
			boolean loop = false;
			Iterator<MessageInTransactionTypeType> iterator = group.iterator();
			while (!loop && iterator.hasNext()) {
				MessageInTransactionTypeType groupMitt = iterator.next();
				List<MessageInTransactionTypeType> previous = getPrevious(groupMitt);
				if (previous != null) {
					for (MessageInTransactionTypeType prevMitt : previous) {
						if (mitt.equals(prevMitt)) {
							loop = true;
							break;
						}
					}
				}
			}
			return loop;
		}

		private void reset(Graphics g) {
			g.clearRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.BLACK);

			if (selectedElement != currentTransaction) {
				removeAll();
				init = null;
				exec = null;
				messages.clear();
				currentTransaction = selectedElement;
				successorMap = new HashMap<MessageInTransactionTypeType, List<MessageInTransactionTypeType>>();
				initPrevMap();
			}
		}

		public void setCurrentTransaction(Object object) {
			this.currentTransaction = null;
		}

	}

	List<MessageInTransactionTypeType> startMitt;
	private ListSelectionListener messageTableSelectionListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			int selectedRow = tbl_Messages.getSelectedRow();
			boolean isSelectedMessage = selectedRow >= 0;
			btn_EditMessage.setEnabled(isSelectedMessage);
			btn_RemoveMessage.setEnabled(isSelectedMessage);
			btn_Reverse.setEnabled(isSelectedMessage);
			sequenceTable.clear();
			elementConditionTable.clear();
			if (isSelectedMessage) {
				MessageInTransactionTypeType mitt = messagesTableModel.get(selectedRow);
				TransactionPhaseTypeType transactionPhase = getTransactionPhase(mitt);
				cbx_TransactionPhases.setSelectedItem(transactionPhase != null ? transactionPhase.getId() : null);

				sequenceTable.fillSequenceTable(null, "inOut", mitt);
				elementConditionTable.fillElementConditionsTable(mitt);

				elementConditionTable.setSelectedMitt(mitt);

				messageInTransactionDialogControl.fillTree(mitt);
			} else {
				elementConditionTable.setSelectedMitt(null);

				messageInTransactionDialogControl.clearTree();
			}
		}
	};

	@SuppressWarnings("serial")
	private class TransactionsTableRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (row >= 0) {
				row = tbl_Elements.getRowSorter().convertRowIndexToModel(row);
				if (column != TransactionsTableColumns.Main.ordinal()) {
					TransactionTypeType transactionTypeType = elementsTableModel.get(row);
					if (transactionTypeType != null) {
						if (isMainTransaction(transactionTypeType)) {
							setFont(getFont().deriveFont(Font.BOLD));
						}
					}
				}
			}
			return this;
		}

	}

	private enum TransactionsTableColumns {
		Id, Description, Main, Initiator, Executor;

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
			case Main:
				return isMainTransaction(transaction);
			case Initiator:
				RoleTypeType initiator = getInitiator(transaction);
				return initiator != null ? initiator.getDescription() : null;
			case Executor:
				RoleTypeType executor = getExecutor(transaction);
				return executor != null ? executor.getDescription() : null;
			default:
				return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (TransactionsTableColumns.values()[columnIndex] == TransactionsTableColumns.Main) {
				return Boolean.class;
			} else
				return String.class;
		}
	}

	private enum SubtransactionsTableColumns {
		Id, Description, Initiator, Executor;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}
	}

	@SuppressWarnings("serial")
	private class SubtransactionsTableModel extends ElementsTableModel<TransactionTypeType> {

		@Override
		public int getColumnCount() {
			return SubtransactionsTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return SubtransactionsTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			TransactionTypeType transaction = get(rowIndex);
			switch (SubtransactionsTableColumns.values()[columnIndex]) {
			case Id:
				return transaction.getId();
			case Description:
				return transaction.getDescription();
			case Initiator:
				RoleTypeType initiator = getInitiator(transaction);
				return initiator != null ? initiator.getId() : null;
			case Executor:
				RoleTypeType executor = getExecutor(transaction);
				return executor != null ? executor.getId() : null;
			default:
				return null;
			}
		}
	}

	private enum MessagesTableColumns {
		Id_Mitt, Id_Message, Message, TransactionPhase, Group, InitiatorToExecutor, OpenSecondaryTransactionsAllowed,
		Start, Navigate;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	public class MessagesTableModel extends ElementsTableModel<MessageInTransactionTypeType> {

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
			MessageTypeType messageType = getMessage(mitt);
			if (messageType == null)
				return null;

			switch (MessagesTableColumns.values()[columnIndex]) {
			case Id_Mitt:
				return mitt.getId();
			case Id_Message:
				return messageType.getId();
			case Message:
				return messageType.getDescription();
			case TransactionPhase:
				TransactionPhase transactionPhase = mitt.getTransactionPhase();
				if (transactionPhase != null) {
					TransactionPhaseTypeType transactionPhaseType = transactionPhase.getTransactionPhaseType();
					if (transactionPhaseType == null) {
						transactionPhaseType = (TransactionPhaseTypeType) transactionPhase.getTransactionPhaseTypeRef()
								.getIdref();
					}
					if (transactionPhaseType != null) {
						return transactionPhaseType.getDescription();
					}
				}
				return null;
			case Group:
				Group group = mitt.getGroup();
				if (group != null) {
					GroupTypeType groupType = group.getGroupType();
					if (groupType == null) {
						groupType = (GroupTypeType) group.getGroupTypeRef().getIdref();
					}
					if (groupType != null) {
						return groupType.getDescription();
					}
				}
				return null;
			case InitiatorToExecutor:
				return mitt.isInitiatorToExecutor() != null ? mitt.isInitiatorToExecutor() : true;
			case OpenSecondaryTransactionsAllowed:
				return mitt.isOpenSecondaryTransactionsAllowed() != null ? mitt.isOpenSecondaryTransactionsAllowed()
						: true;
			case Start:
				Boolean firstMessage = mitt.isFirstMessage();
				if (firstMessage != null) {
					return firstMessage;
				}
				return startMitt != null ? startMitt.contains(mitt) : false;
			default:
				return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (MessagesTableColumns.values()[columnIndex]) {
//			case TransactionPhase:
//				return String.class;
			case Id_Mitt:
				return String.class;
			case Id_Message:
				return String.class;
			case Message:
				return String.class;
			case InitiatorToExecutor:
				return Boolean.class;
			case OpenSecondaryTransactionsAllowed:
				return Boolean.class;
			case Start:
				return Boolean.class;
			default:
				break;
			}
			return Object.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			switch (MessagesTableColumns.values()[columnIndex]) {
			case Group:
				return true;
			case Id_Message:
				break;
			case Id_Mitt:
				break;
			case InitiatorToExecutor:
				return true;
			case Message:
				break;
			case Start:
				return true;
			case TransactionPhase:
				return true;
			case OpenSecondaryTransactionsAllowed:
				return true;
			case Navigate:
				return true;
			}
			return false;
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			if (inSelection)
				return;

			MessageInTransactionTypeType mitt = messagesTableModel.get(rowIndex);

			switch (MessagesTableColumns.values()[columnIndex]) {
			case Id_Message:
				break;
			case Id_Mitt:
				break;
			case TransactionPhase:
				setTransactionPhase(value, mitt);
				break;
			case Group:
				setGroup(value, mitt);
				break;
			case InitiatorToExecutor:
				reverse();
				break;
			case OpenSecondaryTransactionsAllowed:
				setOpenSecondaryTransactionsAllowed(rowIndex, mitt);
				break;
			case Start:
				setFirstMessage((Boolean) value, rowIndex, mitt);
				break;
			default:
				break;
			}
		}

		private void setFirstMessage(Boolean value, int rowIndex, MessageInTransactionTypeType mitt) {
			boolean firstMessage = mitt.isFirstMessage() != null ? mitt.isFirstMessage() : false;
			mitt.setFirstMessage(!firstMessage);
			messagesTableModel.fireTableRowsUpdated(rowIndex, rowIndex);
			messageTableSelectionListener.valueChanged(new ListSelectionEvent(tbl_Messages, rowIndex, rowIndex, false));
		}

		private void setOpenSecondaryTransactionsAllowed(int rowIndex, MessageInTransactionTypeType mitt) {
			boolean openSubtransactions = mitt.isOpenSecondaryTransactionsAllowed() != null
					? mitt.isOpenSecondaryTransactionsAllowed()
					: true;
			mitt.setOpenSecondaryTransactionsAllowed(!openSubtransactions);
			messagesTableModel.fireTableRowsUpdated(rowIndex, rowIndex);
			messageTableSelectionListener.valueChanged(new ListSelectionEvent(tbl_Messages, rowIndex, rowIndex, false));
		}

		private void setTransactionPhase(Object value, MessageInTransactionTypeType mitt) {
			TransactionPhaseTypeType transactionPhaseType = value != null
					? Editor16.getStore16().getElement(TransactionPhaseTypeType.class, (String) value)
					: null;
			MessageInTransactionTypeType.TransactionPhase transactionPhase = mitt.getTransactionPhase();
			if (transactionPhase == null && transactionPhaseType != null) {
				transactionPhase = objectFactory.createMessageInTransactionTypeTypeTransactionPhase();
				mitt.setTransactionPhase(transactionPhase);
			}
			if (transactionPhaseType != null) {
				TransactionPhaseTypeTypeRef transactionPhaseTypeRef = objectFactory.createTransactionPhaseTypeTypeRef();
				transactionPhaseTypeRef.setIdref(transactionPhaseType);
				transactionPhase.setTransactionPhaseTypeRef(transactionPhaseTypeRef);
			} else {
				mitt.setTransactionPhase(null);
			}
		}

		private void setGroup(Object value, MessageInTransactionTypeType mitt) {
			GroupTypeType groupType = value != null
					? Editor16.getStore16().getElement(GroupTypeType.class, (String) value)
					: null;
			MessageInTransactionTypeType.Group group = mitt.getGroup();
			if (group == null && groupType != null) {
				group = objectFactory.createMessageInTransactionTypeTypeGroup();
				mitt.setGroup(group);
			}
			if (groupType != null) {
				GroupTypeTypeRef groupTypeRef = objectFactory.createGroupTypeTypeRef();
				groupTypeRef.setIdref(groupType);
				group.setGroupTypeRef(groupTypeRef);
			} else {
				mitt.setGroup(null);
			}
		}

		/**
		 * Determine start MITT
		 */
		public List<MessageInTransactionTypeType> getStartMitt() {

			// Collect all mitt's with initiator / executor direction
			List<MessageInTransactionTypeType> startCandidates = new ArrayList<MessageInTransactionTypeType>();
			Iterator<MessageInTransactionTypeType> elemIter = elements.iterator();
			while (elemIter.hasNext()) {
				MessageInTransactionTypeType mitt = elemIter.next();
				/*
				 * if (mitt.isInitiatorToExecutor()) { startCandidates.add(mitt); }
				 */
				/* try first with both directions */
				startCandidates.add(mitt);
				/* ------------------------------ */
			}

			// Any candidates with zero previous mitt's ?
			Iterator<MessageInTransactionTypeType> iterator = startCandidates.iterator();
			List<MessageInTransactionTypeType> firstMessageMitts = new ArrayList<MessageInTransactionTypeType>();
			List<MessageInTransactionTypeType> zeroPreviousMitts = new ArrayList<MessageInTransactionTypeType>();
			List<MessageInTransactionTypeType> foreignPreviousMitts = new ArrayList<MessageInTransactionTypeType>();
			while (iterator.hasNext()) {
				MessageInTransactionTypeType mitt = iterator.next();
				if (mitt.isFirstMessage() != null && mitt.isFirstMessage()) {
					firstMessageMitts.add(mitt);
				} else if (mitt.getPrevious() == null) {
					zeroPreviousMitts.add(mitt);
				} else {
					List<Object> previousList = mitt.getPrevious()
							.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
					boolean allForeignPrevious = true;
					for (Object object : previousList) {
						MessageInTransactionTypeType previousElement = (MessageInTransactionTypeType) getElementType(
								object);
						Transaction transaction = previousElement.getTransaction();
						if (transaction != null) {
							TransactionTypeType transactionType = transaction.getTransactionType();
							if (transactionType == null) {
								transactionType = (TransactionTypeType) transaction.getTransactionTypeRef().getIdref();
								if (transactionType != null && transactionType.equals(selectedElement)) {
									allForeignPrevious = false;
									break;
								}
							}
						}
					}
					if (allForeignPrevious) {
						foreignPreviousMitts.add(mitt);
					}
				}
			}
			startCandidates.clear();
			startCandidates.addAll(firstMessageMitts);
			startCandidates.addAll(zeroPreviousMitts);
			startCandidates.addAll(foreignPreviousMitts);

			return startCandidates;
		}

		private class MittNode extends DefaultMutableTreeNode {

			public MittNode(Object userObject) {
				super(userObject);
			}

			@Override
			public String toString() {
				if (userObject instanceof MessageInTransactionTypeType) {
					MessageInTransactionTypeType mitt = (MessageInTransactionTypeType) userObject;
					MessageTypeType messageType = getMessage(mitt);
					if (messageType != null) {
						return messageType.getId();
					}
					return "";
				} else
					return super.toString();
			}

		}

		@SuppressWarnings("unused")
		private int level = 0;

		public void sort() {
			if (startMitt.size() > 0) {
				MittNode root = new MittNode("Root");
				DefaultTreeModel mittTreeModel = new DefaultTreeModel(root);
				Map<MessageInTransactionTypeType, Boolean> placeMap = new HashMap<MessageInTransactionTypeType, Boolean>();
				Iterator<MessageInTransactionTypeType> startIterator = startMitt.iterator();

				while (startIterator.hasNext()) {
					MessageInTransactionTypeType mitt = startIterator.next();
					MittNode parentNode = new MittNode(mitt);
					root.add(parentNode);
					placeMap.put(mitt, true);

					addChildren(placeMap, mittTreeModel, mitt, parentNode);
				}
				Iterator<MessageInTransactionTypeType> iterator = elements.iterator();
				while (iterator.hasNext()) {
					MessageInTransactionTypeType elemMitt = iterator.next();
					Boolean placed = placeMap.get(elemMitt);
					if (placed == null || placed == false) {
						List<MessageInTransactionTypeType> startSubMitts = getStartSubMitts(null, null, null, elemMitt);
						for (MessageInTransactionTypeType startSubMitt : startSubMitts) {
							List<MittNode> mittNodes = getMittNodes(null, root, startSubMitt);
							for (MittNode mittNode : mittNodes) {
								mittNode.add(new MittNode(elemMitt));
							}
						}
					}
				}

				elements.clear();
				MittNode currentNode = root;
				level = 0;
				traversMittTree(currentNode);
			}
		}

		private void traversMittTree(MittNode currentNode) {
			int beginIndex = elements.size();
			int middleIndex = elements.size();
			for (int index = 0; index < currentNode.getChildCount(); index++) {
				MittNode childNode = (MittNode) currentNode.getChildAt(index);
				MessageInTransactionTypeType mitt = (MessageInTransactionTypeType) childNode.getUserObject();
				if (currentNode.getUserObject() instanceof MessageInTransactionTypeType && startMitt.contains(mitt)) {
					continue;
				}
				if (!elements.contains(mitt)) {
					if (currentNode.getUserObject() instanceof String) {
						elements.add(mitt);
					} else if (isEndMitt(mitt)
							&& (mitt.isInitiatorToExecutor() != null && !mitt.isInitiatorToExecutor())) {
						elements.add(beginIndex, mitt);
						middleIndex++;
					} else if (isEndMitt(mitt)
							&& (mitt.isInitiatorToExecutor() == null || mitt.isInitiatorToExecutor())) {
						elements.add(mitt);
					} else {
						elements.add(middleIndex, mitt);
						middleIndex++;
					}
				}
				level++;
				traversMittTree(childNode);
				level--;
			}
			return;
		}

		private List<MittNode> getMittNodes(List<MittNode> mittNodes, MittNode parent,
				MessageInTransactionTypeType startSubMitt) {
			if (mittNodes == null) {
				mittNodes = new ArrayList<TransactionsPanelControl16.MessagesTableModel.MittNode>();
			}
			Object userObject = parent.getUserObject();
			if (userObject instanceof MessageInTransactionTypeType) {
				if (startSubMitt.equals(userObject)) {
					mittNodes.add(parent);
					return mittNodes;
				}
			}
			for (int index = 0; index < parent.getChildCount(); index++) {
				mittNodes = getMittNodes(mittNodes, (MittNode) parent.getChildAt(index), startSubMitt);
			}
			return mittNodes;
		}

		private void addChildren(Map<MessageInTransactionTypeType, Boolean> placeMap, DefaultTreeModel treeModel,
				MessageInTransactionTypeType mitt, MittNode parentNode) {
			Iterator<MessageInTransactionTypeType> elementsIterator = elements.iterator();
			while (elementsIterator.hasNext()) {
				MessageInTransactionTypeType elemMitt = elementsIterator.next();
				if (elemMitt.isInitiatorToExecutor() == mitt.isInitiatorToExecutor()) {
					continue;
				}
				if (elemMitt == mitt) {
					continue;
				}
				MittNode childNode = new MittNode(elemMitt);
				TreeNode[] pathToRoot = treeModel.getPathToRoot(parentNode);
				boolean alreadyInPath = false;
				for (TreeNode node : pathToRoot) {
					MittNode mNode = (MittNode) node;
					if (mNode.toString().equals(childNode.toString())) {
						alreadyInPath = true;
						break;
					}
				}
				if (alreadyInPath) {
					continue;
				}
				Previous previous = elemMitt.getPrevious();
				if (previous != null) {
					List<Object> previousList = previous.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
					for (Object object : previousList) {
						MessageInTransactionTypeType previousElement = (MessageInTransactionTypeType) getElementType(
								object);
						if (previousElement == mitt) {
							parentNode.add(childNode);
							placeMap.put(elemMitt, true);
							addChildren(placeMap, treeModel, elemMitt, childNode);
							break;
						}
					}
				}
			}
		}

		private List<MessageInTransactionTypeType> getStartSubMitts(List<MessageInTransactionTypeType> startSubMitts,
				List<MessageInTransactionTypeType> alreadyScanned, TransactionTypeType selectedTransaction,
				MessageInTransactionTypeType parentMitt) {
			if (startSubMitts == null) {
				startSubMitts = new ArrayList<MessageInTransactionTypeType>();
				alreadyScanned = new ArrayList<MessageInTransactionTypeType>();
			}
			Previous previous = parentMitt.getPrevious();
			if (previous != null) {
				List<Object> previousList = previous.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
				for (Object object : previousList) {
					MessageInTransactionTypeType previousElement = (MessageInTransactionTypeType) getElementType(
							object);
					if (alreadyScanned.contains(previousElement)) {
						continue;
					}
					alreadyScanned.add(previousElement);
					Transaction transaction = previousElement.getTransaction();
					if (transaction != null) {
						TransactionTypeType transactionType = transaction.getTransactionType();
						if (transactionType == null) {
							transactionType = (TransactionTypeType) transaction.getTransactionTypeRef().getIdref();
						}
						if (transactionType != null) {
							if (transactionType.equals(selectedElement)) {
								if (!startSubMitts.contains(previousElement)) {
									startSubMitts.add(previousElement);
									continue;
								}
							} else {
								if (selectedTransaction == null) {
									getStartSubMitts(startSubMitts, alreadyScanned, transactionType, previousElement);
								} else if (selectedTransaction.equals(transactionType)) {
									getStartSubMitts(startSubMitts, alreadyScanned, selectedTransaction,
											previousElement);
								}
							}
						}
					}
				}
			}
			return startSubMitts;
		}

	}

	public MessagesTableModel getMessagesTableModel() {
		return messagesTableModel;
	}

	enum TransactionTabs {
		Roles, Messages, Subtransactions, StaticSequenceDiagram, DynamicSequenceDiagram;

		JFrame tearOffFrame;

		JFrame getTearOffFrame() {
			if (tearOffFrame == null) {
				tearOffFrame = new JFrame(getBundle().getString("lbl_" + name()));
			}
			return tearOffFrame;
		}

	}

	public TransactionsPanelControl16() throws Exception {
		super(TRANSACTIONS_PANEL);

		transactionTabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				switch (TransactionTabs.values()[transactionTabs.getSelectedIndex()]) {
				case DynamicSequenceDiagram:
					break;
				case Messages:
					break;
				case Roles:
					break;
				case StaticSequenceDiagram:
					drawingPlane.setCurrentTransaction(null);
					break;
				case Subtransactions:
					break;
				default:
					break;
				}
//				if (transactionTabs.getSelectedComponent().equals(canvasPanel)) {
//					drawingPlane.setCurrentTransaction(null);
//				}
			}
		});
		transactionTabs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (transactionTabs.getSelectedIndex() == TransactionTabs.StaticSequenceDiagram.ordinal()
						|| transactionTabs.getSelectedIndex() == TransactionTabs.DynamicSequenceDiagram.ordinal()) {
					if (e.getClickCount() == 2) {
						// System.out.println(TransactionTabs.values()[transactionTabs.getSelectedIndex()].name());
						transactionTabs.setEnabledAt(transactionTabs.getSelectedIndex(), false);
						((JComponent) transactionTabs.getSelectedComponent()).removeAll();
						transactionTabs.getSelectedComponent().repaint();
						JFrame frame = TransactionTabs.values()[transactionTabs.getSelectedIndex()].getTearOffFrame();
						frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						tearOff(frame, TransactionTabs.values()[transactionTabs.getSelectedIndex()]);
						frame.pack();
						frame.setVisible(true);
					}
				}
			}

			private void tearOff(JFrame frame, final TransactionTabs tab) {
				frame.setTitle(getBundle().getString("lbl_" + tab.name()));
				switch (tab) {
				case DynamicSequenceDiagram:
					frame.add(scrollPane2, BorderLayout.CENTER);
					frame.getRootPane().setTransferHandler(Msg2MittTransferHandler.getInstance());
					break;
				case StaticSequenceDiagram:
					frame.add(scrollPane, BorderLayout.CENTER);
					break;
				default:
					break;
				}
//				frame.add(tab.getPanelControl().getPanel());
				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						super.windowClosing(e);
						JComponent tabComponent = (JComponent) transactionTabs.getComponentAt(tab.ordinal());
						switch (tab) {
						case DynamicSequenceDiagram:
							tabComponent.add(scrollPane2, BorderLayout.CENTER);
							break;
						case StaticSequenceDiagram:
							tabComponent.add(scrollPane, BorderLayout.CENTER);
							break;
						default:
							break;
						}
//						tabComponent.add(tab.getPanelControl().getPanel());
						transactionTabs.setEnabledAt(tab.ordinal(), true);
						tab.tearOffFrame = null;
					}
				});
			}
		});

		// Initialize tables and fields
		initTransactionsTable();
		initMessagesTable();
		initElementConditionTable();
		initSequenceTable();
		initElementsTree();
		initSubtransactionsTable();
		initStartDateField();
		initEndDateField();
		initResultField();

		drawingPlane = new Canvas();
		scrollPane = new JScrollPane(drawingPlane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		canvasPanel.add(scrollPane, BorderLayout.CENTER);
		canvas16Plane = new Canvas16(this);
		scrollPane2 = new JScrollPane(canvas16Plane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane2.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				Rectangle visibleRect = scrollPane2.getVisibleRect();
				canvas16Plane.setSize(new Dimension(visibleRect.width - 20, visibleRect.height - 20));
				canvas16Plane.setDimensions();
			}
		});
		canvas2Panel.add(scrollPane2, BorderLayout.CENTER);
		canvas2Panel.setTransferHandler(Msg2MittTransferHandler.getInstance());
	}

	private void initResultField() {
		tfd_Result.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected synchronized void update(DocumentEvent e) {
				if (inSelection)
					return;
				selectedElement.setResult(tfd_Result.getText());
			}
		});
	}

	private void initStartDateField() {
		startDateField = new DateField(startDatePanel);
		startDateField.addPropertyChangeListener("date", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				try {
					Date startDate = startDateField.getDate();
					if (startDate != null) {
						gcal.setTime(startDate);
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

	private void initSequenceTable() throws Exception {
		sequenceTable = new SequenceTable();
		sequencePanel.removeAll();
		sequencePanel.add(sequenceTable.getPanel());
		sequencePanel.revalidate();
	}

	private void initElementConditionTable() throws Exception {
		elementConditionTable = new ElementConditionTable(tbl_Messages);
		elementConditionPanel.removeAll();
		elementConditionPanel.add(elementConditionTable.getPanel());
		elementConditionPanel.revalidate();
	}

	private void initElementsTree() throws Exception {
		messageInTransactionDialogControl = new MessageInTransactionDialogControl16(this);
		elementsTreePanel.removeAll();
		elementsTreePanel.add(messageInTransactionDialogControl.getPanel());
		elementsTreePanel.revalidate();
		messageInTransactionDialogControl.clearTree();
	}

	private void initSubtransactionsTable() {
		subtransactionsTableModel = new SubtransactionsTableModel();
		tbl_Subtransactions.setModel(subtransactionsTableModel);
		tbl_Subtransactions.setAutoCreateRowSorter(true);
		tbl_Subtransactions.setFillsViewportHeight(true);
		tbl_Elements.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
			}
		});
	}

	private void initTransactionsTable() {
		elementsTableModel = new TransactionsTableModel();
		tbl_Elements.setModel(elementsTableModel);
		tbl_Elements.setAutoCreateRowSorter(true);
		tbl_Elements.setFillsViewportHeight(true);
		TableRowSorter<ElementsTableModel<TransactionTypeType>> tableRowSorter = new TableRowSorter<>(
				elementsTableModel);
		tbl_Elements.setRowSorter(tableRowSorter);
		TransactionsTableRenderer renderer = new TransactionsTableRenderer();
		for (int index = 0; index < tbl_Elements.getColumnCount(); index++) {
			if (index == TransactionsTableColumns.Main.ordinal())
				continue;
			tbl_Elements.getColumnModel().getColumn(index).setCellRenderer(renderer);
		}
		tbl_Elements.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				updateSelectionArea(e);
			}
		});

		tfd_Filter.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected void update(DocumentEvent e) {
				fillTable();
			}
		});
	}

	@SuppressWarnings("serial")
	private void initMessagesTable() {
		messagesTableModel = new MessagesTableModel();
		tbl_Messages.setModel(messagesTableModel);
		tbl_Messages.setFillsViewportHeight(true);
		tbl_Messages.setDropMode(DropMode.ON_OR_INSERT_ROWS);
		cbx_TransactionPhases = new JComboBox<>(new DefaultComboBoxModel<String>());
		TableColumn transactionPhaseColumn = tbl_Messages.getColumnModel()
				.getColumn(MessagesTableColumns.TransactionPhase.ordinal());
		transactionPhaseColumn.setCellEditor(new DefaultCellEditor(cbx_TransactionPhases));
		cbx_Groups = new JComboBox<>(new DefaultComboBoxModel<String>());
		TableColumn groupColumn = tbl_Messages.getColumnModel().getColumn(MessagesTableColumns.Group.ordinal());
		groupColumn.setCellEditor(new DefaultCellEditor(cbx_Groups));
		TableColumn init2ExecColumn = tbl_Messages.getColumnModel()
				.getColumn(MessagesTableColumns.InitiatorToExecutor.ordinal());
		init2ExecColumn.setMaxWidth(50);
		TableColumn openSubtransactionsColumn = tbl_Messages.getColumnModel()
				.getColumn(MessagesTableColumns.OpenSecondaryTransactionsAllowed.ordinal());
		openSubtransactionsColumn.setMaxWidth(60);
		TableColumn startColumn = tbl_Messages.getColumnModel().getColumn(MessagesTableColumns.Start.ordinal());
		startColumn.setMaxWidth(50);
		TableColumn navigateColumn = tbl_Messages.getColumnModel().getColumn(MessagesTableColumns.Navigate.ordinal());
		navigateColumn.setMaxWidth(50);
		navigateColumn.setCellRenderer(getButtonTableCellRenderer());
		navigateColumn.setCellEditor(new NavigatorEditor() {
			@Override
			protected void navigate() {
				int row = tbl_Messages.getSelectedRow();
				MessageInTransactionTypeType mitt = messagesTableModel.get(row);
				Message message = mitt.getMessage();
				if (message != null) {
					MessageTypeType messageType = message.getMessageType();
					if (messageType == null) {
						messageType = (MessageTypeType) message.getMessageTypeRef().getIdref();
					}
					if (messageType != null) {
						InteractionFrameworkEditor.navigate(messageType);
					}
				}
			}
		});
		cbx_Messages.setTransferHandler(Msg2MittTransferHandler.getInstance());
		tbl_Messages.setTransferHandler(Msg2MittTransferHandler.getInstance());

		tbl_Messages.getSelectionModel().addListSelectionListener(messageTableSelectionListener);
	}

	@Override
	public void fillTable() {
		String filterString = tfd_Filter.getText().toUpperCase();
		if (filterString.isEmpty()) {
			fillTable(TransactionTypeType.class);
		} else {
			List<TransactionTypeType> elements = Editor16.getStore16().getElements(TransactionTypeType.class);
			elementsTableModel.clear();
			for (TransactionTypeType element : elements) {
				if (element.getDescription().toUpperCase().contains(filterString)
						|| element.getId().toUpperCase().contains(filterString)) {
					elementsTableModel.add(element);
				}
			}
		}
		drawingPlane.currentTransaction = null;
	}

	protected void updateSelectionArea(ListSelectionEvent e) {
		inSelection = true;
		selectedRow = tbl_Elements.getSelectedRow();
		tbl_Elements.scrollRectToVisible(tbl_Elements.getCellRect(selectedRow, 0, true));
		if (selectedRow >= 0) {
			selectedRow = tbl_Elements.getRowSorter().convertRowIndexToModel(selectedRow);
		}
		boolean rowSelected = selectedRow >= 0;
		btn_CopyElement.setEnabled(rowSelected);
		btn_DeleteElement.setEnabled(rowSelected);
		tfd_Id.setEnabled(rowSelected);
		tfd_Description.setEnabled(rowSelected);
		startDateField.setEnabled(rowSelected);
		endDateField.setEnabled(rowSelected);
		tfd_State.setEnabled(rowSelected);
		tfd_DateLamu.setEnabled(rowSelected);
		tfd_UserLamu.setEnabled(rowSelected);
		tfd_Language.setEnabled(rowSelected);
		tfd_Category.setEnabled(rowSelected);
		tfd_HelpInfo.setEnabled(rowSelected);
		tfd_Code.setEnabled(rowSelected);
		tfd_Result.setEnabled(rowSelected);
		cbx_Initiator.setEnabled(rowSelected);
		tar_Initiator.setEnabled(rowSelected);
		cbx_Executor.setEnabled(rowSelected);
		tar_Executor.setEnabled(rowSelected);
		tbl_Messages.setEnabled(rowSelected);
		cbx_Messages.setEnabled(rowSelected);
		tbl_Subtransactions.setEnabled(rowSelected);

		successorMap = new HashMap<MessageInTransactionTypeType, List<MessageInTransactionTypeType>>();
		initPrevMap();

		if (rowSelected) {
			selectedElement = elementsTableModel.get(selectedRow);

			tfd_Id.setText(selectedElement.getId());
			tfd_Description.setText(selectedElement.getDescription());
			XMLGregorianCalendar startDate = selectedElement.getStartDate();
			if (startDate != null) {
				startDateField.setDate(startDate.toGregorianCalendar().getTime());
			}
			XMLGregorianCalendar endDate = selectedElement.getEndDate();
			if (endDate != null) {
				endDateField.setDate(endDate.toGregorianCalendar().getTime());
			}
			tfd_State.setText(selectedElement.getState());
			tfd_DateLamu.setText(selectedElement.getDateLaMu() != null
					? sdfDateTime.format(selectedElement.getDateLaMu().toGregorianCalendar().getTime())
					: "");
			tfd_UserLamu.setText(selectedElement.getUserLaMu());
			tfd_Language.setText(selectedElement.getLanguage());
			tfd_Category.setText(selectedElement.getCategory());
			tfd_HelpInfo.setText(selectedElement.getHelpInfo());
			tfd_Code.setText(selectedElement.getCode());
			tfd_Result.setText(selectedElement.getResult());

			cbx_Initiator.removeAllItems();
			tar_Initiator.setText("");
			cbx_Executor.removeAllItems();
			tar_Executor.setText("");
			cbx_Initiator.addItem(null);
			cbx_Executor.addItem(null);
			List<RoleTypeType> elements = Editor16.getStore16().getElements(RoleTypeType.class);
			for (RoleTypeType role : elements) {
				cbx_Initiator.addItem(role.getId());
				cbx_Executor.addItem(role.getId());
			}
			Initiator initiator = selectedElement.getInitiator();
			btn_NavigateInitiator.setEnabled(initiator != null);
			if (initiator != null) {
				RoleTypeType roleType = initiator.getRoleType();
				if (roleType == null) {
					roleType = (RoleTypeType) initiator.getRoleTypeRef().getIdref();
				}
				cbx_Initiator.setSelectedItem(roleType.getId());
				tar_Initiator.setText(roleType.getDescription());
			}
			Executor executor = selectedElement.getExecutor();
			btn_NavigateExecutor.setEnabled(executor != null);
			if (executor != null) {
				RoleTypeType roleType = executor.getRoleType();
				if (roleType == null) {
					roleType = (RoleTypeType) executor.getRoleTypeRef().getIdref();
				}
				cbx_Executor.setSelectedItem(roleType.getId());
				tar_Executor.setText(roleType.getDescription());
			}

			fillMessageTable();

			DefaultComboBoxModel<String> transactionPhasesModel = (DefaultComboBoxModel<String>) cbx_TransactionPhases
					.getModel();
			transactionPhasesModel.removeAllElements();
			transactionPhasesModel.addElement(null);
			List<TransactionPhaseTypeType> phasesList = Editor16.getStore16()
					.getElements(TransactionPhaseTypeType.class);
			for (TransactionPhaseTypeType phase : phasesList) {
				transactionPhasesModel.addElement(phase.getId());
			}

			DefaultComboBoxModel<String> groupsModel = (DefaultComboBoxModel<String>) cbx_Groups.getModel();
			groupsModel.removeAllElements();
			List<GroupTypeType> groupsList = Editor16.getStore16().getElements(GroupTypeType.class);
			for (GroupTypeType group : groupsList) {
				groupsModel.addElement(group.getId());
			}

			cbx_Messages.removeAllItems();
			cbx_Messages.addItem(null);
			List<MessageTypeType> messages = Editor16.getStore16().getElements(MessageTypeType.class);
			for (MessageTypeType message : messages) {
				cbx_Messages.addItem("[" + message.getId() + "] " + message.getDescription());
			}

			fillSubtransactionsTable();
		} else {
			selectedElement = null;
			tfd_Id.setText("");
			tfd_Description.setText("");
			startDateField.setDate(null);
			endDateField.setDate(null);
			tfd_State.setText("");
			tfd_DateLamu.setText("");
			tfd_UserLamu.setText("");
			tfd_Language.setText("");
			tfd_Category.setText("");
			tfd_HelpInfo.setText("");
			tfd_Code.setText("");
			tfd_Result.setText("");

			cbx_Initiator.removeAllItems();
			btn_NavigateInitiator.setEnabled(false);
			tar_Initiator.setText("");
			cbx_Executor.removeAllItems();
			btn_NavigateExecutor.setEnabled(false);
			messagesTableModel.clear();
			cbx_Messages.removeAllItems();
			elementConditionTable.clear();
			sequenceTable.clear();
			subtransactionsTableModel.clear();
		}

		canvasPanel.repaint();
		canvas2Panel.repaint();

		inSelection = false;
	}

	private void fillSubtransactionsTable() {
		subtransactionsTableModel.clear();
		List<Object> subTransactionsList = null;
		SubTransactions subTransactions = selectedElement.getSubTransactions();
		if (subTransactions != null) {
			subTransactionsList = subTransactions.getTransactionTypeOrTransactionTypeRef();
			for (Object object : subTransactionsList) {
				if (object instanceof TransactionTypeTypeRef) {
					object = ((TransactionTypeTypeRef) object).getIdref();
				}
				subtransactionsTableModel.add((TransactionTypeType) object);
			}
		}
		if (subTransactionsList == null) {
			subTransactionsList = new ArrayList<Object>();
		}
		List<Object> mittList = Editor16.getStore16().getElements(MessageInTransactionTypeType.class);
		for (Object object : mittList) {
			MessageInTransactionTypeType mitt = (MessageInTransactionTypeType) object;
			Transaction transaction = mitt.getTransaction();
			if (transaction != null) {
				TransactionTypeType transactionType = transaction.getTransactionType();
				if (transactionType == null) {
					transactionType = (TransactionTypeType) transaction.getTransactionTypeRef().getIdref();
				}
				if (transactionType != null && !transactionType.equals(selectedElement)
						&& isStart(mitt, transactionType)) {
					Previous previous = mitt.getPrevious();
					if (previous != null) {
						List<Object> previousList = previous.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
						for (Object previousObject : previousList) {
							MessageInTransactionTypeType previousMitt = (MessageInTransactionTypeType) getElementType(
									previousObject);
							Transaction previousTransaction = previousMitt.getTransaction();
							if (previousTransaction != null) {
								TransactionTypeType previousTransactionType = previousTransaction.getTransactionType();
								if (previousTransactionType == null) {
									previousTransactionType = (TransactionTypeType) previousTransaction
											.getTransactionTypeRef().getIdref();
								}
								if (previousTransactionType != null
										&& previousTransactionType.equals(selectedElement)) {
									if (!subTransactionsList.contains(transactionType)) {
										subTransactionsList.add(transactionType);
										subtransactionsTableModel.add(transactionType);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	void fillMessageTable() {
		messagesTableModel.clear();
		List<MessageInTransactionTypeType> mitts = Editor16.getStore16()
				.getElements(MessageInTransactionTypeType.class);
		if (mitts != null) {
			for (MessageInTransactionTypeType mitt : mitts) {
				Transaction transaction = mitt.getTransaction();
				if (transaction != null) {
					TransactionTypeType transactionType = transaction.getTransactionType();
					if (transactionType == null) {
						transactionType = (TransactionTypeType) transaction.getTransactionTypeRef().getIdref();
					}
					if (selectedElement.equals(transactionType)) {
						messagesTableModel.add(mitt);
					}
				}
			}
		}
		startMitt = messagesTableModel.getStartMitt();
		messagesTableModel.sort();
	}

	public void setInitiator() {
		String itemId = (String) cbx_Initiator.getSelectedItem();
		if (itemId != null) {
			RoleTypeType roleType = Editor16.getStore16().getElement(RoleTypeType.class, itemId);
			ObjectFactory objectFactory = new ObjectFactory();
			RoleTypeTypeRef typeTypeRef = objectFactory.createRoleTypeTypeRef();
			typeTypeRef.setIdref(roleType);
			Initiator initiator = new Initiator();
			initiator.setRoleTypeRef(typeTypeRef);
			selectedElement.setInitiator(initiator);
			elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			tar_Initiator.setText(roleType.getDescription());
		} else {
			tar_Initiator.setText("");
		}
	}

	public void setExecutor() {
		String itemId = (String) cbx_Executor.getSelectedItem();
		if (itemId != null) {
			RoleTypeType roleType = Editor16.getStore16().getElement(RoleTypeType.class, itemId);
			ObjectFactory objectFactory = new ObjectFactory();
			RoleTypeTypeRef typeTypeRef = objectFactory.createRoleTypeTypeRef();
			typeTypeRef.setIdref(roleType);
			Executor executor = new Executor();
			executor.setRoleTypeRef(typeTypeRef);
			selectedElement.setExecutor(executor);
			elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			tar_Executor.setText(roleType.getDescription());
		} else {
			tar_Executor.setText("");
		}
	}

	public void newElement() {
		try {
			TransactionTypeType newTransactionType = objectFactory.createTransactionTypeType();
			newElement(newTransactionType, "Transaction_");

			int row = elementsTableModel.add(newTransactionType);
			row = tbl_Elements.convertRowIndexToView(row);
			tbl_Elements.getSelectionModel().setSelectionInterval(row, row);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void copyElement() {
		Store16 store = Editor16.getStore16();
		int row = tbl_Elements.getSelectedRow();
		row = tbl_Elements.getRowSorter().convertRowIndexToModel(row);
		TransactionTypeType origTransactionType = elementsTableModel.get(row);

		try {
			TransactionTypeType copyTransactionType = objectFactory.createTransactionTypeType();
			newElement(copyTransactionType, "Transaction_");
			store.generateCopyId(copyTransactionType, origTransactionType);
			copyAppendices(origTransactionType, copyTransactionType);
			copyTransactionType.setCategory(origTransactionType.getCategory());
			copyTransactionType.setCode(origTransactionType.getCode());
			copyTransactionType.setDescription(origTransactionType.getDescription());
			copyTransactionType.setEndDate(origTransactionType.getEndDate());
			copyExecutor(origTransactionType, copyTransactionType);
			copyTransactionType.setHelpInfo(origTransactionType.getHelpInfo());
			copyInitiator(origTransactionType, copyTransactionType);
			copyTransactionType.setLanguage(origTransactionType.getLanguage());
			copyTransactionType.setResult(origTransactionType.getResult());
			copyTransactionType.setStartDate(origTransactionType.getStartDate());
			copyTransactionType.setState(origTransactionType.getState());
			copySubtransactions(origTransactionType, copyTransactionType);
			store.put(copyTransactionType.getId(), copyTransactionType);
			int copyrow = elementsTableModel.add(copyTransactionType);
			copyrow = tbl_Elements.convertRowIndexToView(copyrow);
			tbl_Elements.getSelectionModel().setSelectionInterval(copyrow, copyrow);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void copyAppendices(TransactionTypeType transactionType, TransactionTypeType copyTransactionType) {
		TransactionTypeType.AppendixTypes appendixTypes = transactionType.getAppendixTypes();
		if (appendixTypes != null) {
			List<Object> refs = appendixTypes.getAppendixTypeOrAppendixTypeRef();
			if (refs != null) {
				TransactionTypeType.AppendixTypes copyAppendixTypes = objectFactory
						.createTransactionTypeTypeAppendixTypes();
				List<Object> copyRefs = copyAppendixTypes.getAppendixTypeOrAppendixTypeRef();
				for (Object item : refs) {
					copyRefs.add(item);
				}
				copyTransactionType.setAppendixTypes(copyAppendixTypes);
			}
		}
	}

	private void copySubtransactions(TransactionTypeType transactionType, TransactionTypeType copyTransactionType) {
		TransactionTypeType.SubTransactions subtransactions = transactionType.getSubTransactions();
		if (subtransactions != null) {
			List<Object> refs = subtransactions.getTransactionTypeOrTransactionTypeRef();
			if (refs != null) {
				TransactionTypeType.SubTransactions copySubtransactions = objectFactory
						.createTransactionTypeTypeSubTransactions();
				List<Object> copyRefs = copySubtransactions.getTransactionTypeOrTransactionTypeRef();
				for (Object item : refs) {
					copyRefs.add(item);
				}
				copyTransactionType.setSubTransactions(copySubtransactions);
			}
		}
	}

	private void copyExecutor(TransactionTypeType transactionType, TransactionTypeType copyTransactionType) {
		TransactionTypeType.Executor executor = transactionType.getExecutor();
		if (executor != null) {
			RoleTypeType roleType = executor.getRoleType();
			if (roleType == null) {
				roleType = (RoleTypeType) executor.getRoleTypeRef().getIdref();
			}
			if (roleType != null) {
				TransactionTypeType.Executor copyExecutor = objectFactory.createTransactionTypeTypeExecutor();
				copyExecutor.setRoleType(roleType);
				copyTransactionType.setExecutor(copyExecutor);
			}
		}
	}

	private void copyInitiator(TransactionTypeType transactionType, TransactionTypeType copyTransactionType) {
		TransactionTypeType.Initiator initiator = transactionType.getInitiator();
		if (initiator != null) {
			RoleTypeType roleType = initiator.getRoleType();
			if (roleType == null) {
				roleType = (RoleTypeType) initiator.getRoleTypeRef().getIdref();
			}
			if (roleType != null) {
				TransactionTypeType.Initiator copyInitiator = objectFactory.createTransactionTypeTypeInitiator();
				copyInitiator.setRoleType(roleType);
				copyTransactionType.setInitiator(copyInitiator);
			}
		}
	}

	public void deleteElement() {
		Store16 store = Editor16.getStore16();
		int row = tbl_Elements.getSelectedRow();
		row = tbl_Elements.getRowSorter().convertRowIndexToModel(row);
		TransactionTypeType transactionType = elementsTableModel.get(row);

		int response = JOptionPane.showConfirmDialog(getPanel(),
				getBundle().getString("lbl_Remove") + ": " + transactionType.getId(),
				getBundle().getString("lbl_Remove"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (response == JOptionPane.CANCEL_OPTION)
			return;

		List<MessageInTransactionTypeType> toBeDeleted = new ArrayList<MessageInTransactionTypeType>();
		List<MessageInTransactionTypeType> mitts = store.getElements(MessageInTransactionTypeType.class);
		for (MessageInTransactionTypeType mitt : mitts) {
			Transaction transaction = mitt.getTransaction();
			if (transaction != null) {
				TransactionTypeType trnsType = transaction.getTransactionType();
				if (trnsType == null) {
					trnsType = (TransactionTypeType) transaction.getTransactionTypeRef().getIdref();
				}
				if (trnsType != null && trnsType.equals(transactionType)) {
					toBeDeleted.add(mitt);
					List<ElementConditionType> elementConditions = store.getElements(ElementConditionType.class);
					for (ElementConditionType ect : elementConditions) {
						MessageInTransaction messageInTransaction = ect.getMessageInTransaction();
						if (messageInTransaction != null) {
							MessageInTransactionTypeType mittRef = (MessageInTransactionTypeType) messageInTransaction
									.getMessageInTransactionTypeRef().getIdref();
							if (mitt.equals(mittRef)) {
								// store.remove(ect.getId());
								store.remove(ect);
							}
						}
					}
				} else {
					Previous previous = mitt.getPrevious();
					if (previous != null) {
						List<Object> previousList = previous.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
						for (Object object : previousList) {
							MessageInTransactionTypeType previousType = (MessageInTransactionTypeType) getElementType(
									object);
							Transaction trns = previousType.getTransaction();
							if (trns != null) {
								TransactionTypeType transType = trns.getTransactionType();
								if (transType == null) {
									transType = (TransactionTypeType) trns.getTransactionTypeRef().getIdref();
								}
								if (transactionType.equals(transType)) {
									previousList.remove(object);
									if (previousList.size() == 0) {
										mitt.setPrevious(null);
										updateLaMu(mitt, user);
									}
									break;
								}
							}
						}
					}
				}
			}
		}
		for (MessageInTransactionTypeType mitt : toBeDeleted) {
			store.remove(mitt);
		}

		List<TransactionTypeType> transactionElements = store.getElements(TransactionTypeType.class);
		for (TransactionTypeType transactionElement : transactionElements) {
			if (transactionElement.equals(transactionType))
				continue;
			SubTransactions subTransactions = transactionElement.getSubTransactions();
			if (subTransactions != null) {
				List<Object> list = subTransactions.getTransactionTypeOrTransactionTypeRef();
				for (Object object : list) {
					TransactionTypeType trnsType = (TransactionTypeType) getElementType(object);
					if (transactionType.equals(trnsType)) {
						list.remove(object);
						if (list.size() == 0) {
							transactionElement.setSubTransactions(null);
							updateLaMu(transactionElement, user);
						}
						break;
					}
				}
			}
		}

		store.remove(transactionType);
		elementsTableModel.remove(row);
	}

	public void selectMessage() {
		int selectedIndex = cbx_Messages.getSelectedIndex();
		btn_AddMessage.setEnabled(selectedIndex > 0);
	}

	public MessageInTransactionTypeType addMessage() {
		Store16 store = Editor16.getStore16();

		String content = (String) cbx_Messages.getSelectedItem();
		String msgId = content.substring(1, content.indexOf("]"));
		MessageTypeType messageType = store.getElement(MessageTypeType.class, msgId);
		MessageTypeTypeRef messageRef = objectFactory.createMessageTypeTypeRef();
		messageRef.setIdref(messageType);
		MessageInTransactionTypeType.Message message = objectFactory.createMessageInTransactionTypeTypeMessage();
		message.setMessageTypeRef(messageRef);
		MessageInTransactionTypeType mitt = objectFactory.createMessageInTransactionTypeType();
		mitt.setId(store.getNewId("Mitt_"));
		mitt.setRequiredNotify(new BigInteger("0"));
		mitt.setState("active");
		mitt.setInitiatorToExecutor(true);
		mitt.setMessage(message);
		GroupTypeType standardGroupType = store.getElement(GroupTypeType.class, "StandardGroupType");
		if (standardGroupType == null) {
			standardGroupType = objectFactory.createGroupTypeType();
			try {
				newElement(standardGroupType, "StandardGroupType");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (standardGroupType != null) {
			GroupTypeTypeRef groupRef = objectFactory.createGroupTypeTypeRef();
			groupRef.setIdref(standardGroupType);
			MessageInTransactionTypeType.Group group = objectFactory.createMessageInTransactionTypeTypeGroup();
			group.setGroupTypeRef(groupRef);
			mitt.setGroup(group);
		}
		updateLaMu(mitt, user);

		TransactionTypeTypeRef transactionRef = objectFactory.createTransactionTypeTypeRef();
		transactionRef.setIdref(selectedElement);
		MessageInTransactionTypeType.Transaction transaction = objectFactory
				.createMessageInTransactionTypeTypeTransaction();
		transaction.setTransactionTypeRef(transactionRef);
		mitt.setTransaction(transaction);
		store.put(mitt.getId(), mitt);

		int row = messagesTableModel.add(mitt);
		updateLaMu(selectedElement, user);
		elementsTableModel.update(selectedRow);
		tbl_Messages.getSelectionModel().setSelectionInterval(row, row);

		fillMessageTable();
		return mitt;
	}

	public void reset() {
		getDrawingPlane().setCurrentTransaction(null);
		getDrawingPlane().repaint();
	}

	public void editMessage() {
		int row = tbl_Messages.getSelectedRow();
		MessageInTransactionTypeType mitt = messagesTableModel.get(row);
		try {
			final MessageInTransactionDialogControl16 messageInTransactionDialogControl14 = new MessageInTransactionDialogControl16(
					this);
			messageInTransactionDialogControl14.fillTree(mitt);
			messageInTransactionDialogControl14.getDialog().setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private MessageInTransactionDialogControl16 messageInTransactionDialogControl16;

	public void editMitt() {
		MessageInTransactionTypeType mitt = activeItem.getMitt();
		try {
			Window window = SwingUtilities.windowForComponent(scrollPane);
			if (messageInTransactionDialogControl16 == null
					|| (TransactionTabs.StaticSequenceDiagram.tearOffFrame != null
							&& messageInTransactionDialogControl16.getDialog()
									.getOwner() != TransactionTabs.StaticSequenceDiagram.tearOffFrame)) {
				if (TransactionTabs.StaticSequenceDiagram.tearOffFrame != null) {
					window = TransactionTabs.StaticSequenceDiagram.tearOffFrame;
				}
				if (messageInTransactionDialogControl16 != null) {
					messageInTransactionDialogControl16.getDialog().dispose();
				}
				messageInTransactionDialogControl16 = new MessageInTransactionDialogControl16(this, window);
				messageInTransactionDialogControl16.getDialog().setModal(false);
				messageInTransactionDialogControl16.getDialog().addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosing(WindowEvent e) {
						super.windowClosed(e);
						activeItem.activeLabel.setForeground(Color.black);
						activeItem = null;
					}

				});
				messageInTransactionDialogControl16.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						switch (evt.getPropertyName()) {
						case "Previous removed":
							// MessageInTransactionTypeType removedPrev = (MessageInTransactionTypeType)
							// evt.getNewValue();
							// System.out.println("Previous removed: " + removedPrev.getId());
							drawingPlane.setCurrentTransaction(null);
							drawingPlane.repaint();
							break;
						case "Next removed":
							// MessageInTransactionTypeType removedNext = (MessageInTransactionTypeType)
							// evt.getOldValue();
							// System.out.println("Next removed: " + removedNext.getId());
							drawingPlane.setCurrentTransaction(null);
							drawingPlane.repaint();
							break;
						case "Previous added":
							// MessageInTransactionTypeType addedPrev = (MessageInTransactionTypeType)
							// evt.getNewValue();
							// System.out.println("Previous added: " + addedPrev.getId());
							drawingPlane.setCurrentTransaction(null);
							drawingPlane.repaint();
							break;
						case "Next added":
							// MessageInTransactionTypeType addedNext = (MessageInTransactionTypeType)
							// evt.getOldValue();
							// System.out.println("Next added: " + addedNext.getId());
							drawingPlane.setCurrentTransaction(null);
							drawingPlane.repaint();
							break;
						case "Direction changed":
							// MessageInTransactionTypeType currentMitt = (MessageInTransactionTypeType)
							// evt.getOldValue();
							// Boolean direction = (Boolean) evt.getNewValue();
							// System.out.println(
							// "Direction changed: " + currentMitt.getId() + "=" +
							// direction.booleanValue());
							drawingPlane.setCurrentTransaction(null);
							drawingPlane.repaint();
							break;
						}

					}
				});
			}
			messageInTransactionDialogControl16.fillTree(mitt);
			messageInTransactionDialogControl16.initSequenceElements();
			messageInTransactionDialogControl16.fillSequenceElements(mitt);
			MessageTypeType message = Control16.getMessage(mitt);
			messageInTransactionDialogControl16.getDialog().setTitle(
					selectedElement.getDescription() + " : " + message.getDescription() + " [" + mitt.getId() + "]");
			activeItem.activeLabel.setForeground(Color.blue);
			messageInTransactionDialogControl16.getDialog().setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reverse() {
		int row = tbl_Messages.getSelectedRow();
		MessageInTransactionTypeType mitt = messagesTableModel.get(row);
		boolean direction = mitt.isInitiatorToExecutor() != null ? mitt.isInitiatorToExecutor() : true;
		mitt.setInitiatorToExecutor(!direction);
		messagesTableModel.fireTableRowsUpdated(row, row);
		messageTableSelectionListener.valueChanged(new ListSelectionEvent(tbl_Messages, row, row, false));
		updateLaMu(selectedElement, user);
		elementsTableModel.update(selectedRow);
	}

	public boolean removeMessage() {
		Store16 store = Editor16.getStore16();

		int row = tbl_Messages.getSelectedRow();
		MessageInTransactionTypeType mitt = messagesTableModel.get(row);
		int response = JOptionPane.showConfirmDialog(getPanel(),
				getBundle().getString("lbl_Remove") + ": " + mitt.getId(), getBundle().getString("lbl_Remove"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (response == JOptionPane.OK_OPTION) {
			updateAllMittsWithThisMittAsPrevious(mitt);
			store.remove(mitt);
			fillMessageTable();
			updateLaMu(selectedElement, user);
			elementsTableModel.update(selectedRow);
			canvas16Plane.selectMessage(mitt);
			canvas16Plane.selectedMessage.removeFromDiagrams();
			return true;
		}
		return false;
	}

	private void updateAllMittsWithThisMittAsPrevious(MessageInTransactionTypeType selectedMitt) {
		List<MessageInTransactionTypeType> mitts = Editor16.getStore16()
				.getElements(MessageInTransactionTypeType.class);
		if (mitts != null) {
			for (MessageInTransactionTypeType mitt : mitts) {
				Previous previous = mitt.getPrevious();
				if (previous != null) {
					List<Object> list = previous.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
					for (Object object : list) {
						MessageInTransactionTypeType prevMitt = (MessageInTransactionTypeType) getElementType(object);
						if (prevMitt.equals(selectedMitt)) {
							list.remove(object);
							break;
						}
					}
					if (list.size() == 0) {
						mitt.setPrevious(null);
					}
				}
			}
		}
	}

	public boolean isMainTransaction(TransactionTypeType transactionType) {
		Store16 store = Editor16.getStore16();

		// get all MiTT's pointing to this transaction
		List<MessageInTransactionTypeType> mittList = store.getElements(MessageInTransactionTypeType.class);
		for (MessageInTransactionTypeType mitt : mittList) {
			Transaction transaction = mitt.getTransaction();
			if (transaction != null) {
				TransactionTypeType mittTransactionType = (TransactionTypeType) (transaction
						.getTransactionType() != null ? transaction.getTransactionType()
								: transaction.getTransactionTypeRef().getIdref());
				if (transactionType.equals(mittTransactionType)) {
					Previous previous = mitt.getPrevious();
					if (previous == null) {
						return true;
					} else {
						List<Object> list = previous.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
						if (list.size() == 0)
							return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isStart(MessageInTransactionTypeType mitt, TransactionTypeType trns) {
		Previous previous = mitt.getPrevious();
		if (previous != null) {
			List<Object> previousList = previous.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
			for (Object object : previousList) {
				MessageInTransactionTypeType previousType = (MessageInTransactionTypeType) getElementType(object);
				if (previousType != null) {
					MessageInTransactionTypeType.Transaction transaction = previousType.getTransaction();
					if (transaction != null) {
						TransactionTypeType transactionType = (TransactionTypeType) (transaction
								.getTransactionType() != null ? transaction.getTransactionType()
										: transaction.getTransactionTypeRef().getIdref());
						if (trns.equals(transactionType)) {
							return false;
						}
					}
				}
			}
			if (isMainTransaction(trns)) {
				return false;
			}
		}
		return true;
	}

	public void navigateInitiator() {
		String idref = (String) cbx_Initiator.getSelectedItem();
		RoleTypeType element = Editor16.getStore16().getElement(RoleTypeType.class, idref);
		InteractionFrameworkEditor.navigate(element);
	}

	public void navigateExecutor() {
		String idref = (String) cbx_Executor.getSelectedItem();
		RoleTypeType element = Editor16.getStore16().getElement(RoleTypeType.class, idref);
		InteractionFrameworkEditor.navigate(element);
	}

	private void initPrevMap() {
		List<Object> mittObjects = Editor16.getStore16().getElements(MessageInTransactionTypeType.class);
		for (Object mittObject : mittObjects) {
			MessageInTransactionTypeType mitt = (MessageInTransactionTypeType) getElementType(mittObject);
			Previous previous = mitt.getPrevious();
			if (previous != null) {
				List<Object> prevList = previous.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
				for (Object prevObject : prevList) {
					MessageInTransactionTypeType prevMitt = (MessageInTransactionTypeType) getElementType(prevObject);
					List<MessageInTransactionTypeType> list = successorMap.get(prevMitt);
					if (list == null) {
						list = new ArrayList<MessageInTransactionTypeType>();
						successorMap.put(prevMitt, list);
					}
					list.add(mitt);
				}
			}
		}
	}

	private boolean isEndMitt(MessageInTransactionTypeType mitt) {
		boolean endMitt = true;
		TransactionTypeType mittTransaction = getTransaction(mitt);

		List<MessageInTransactionTypeType> successorList = successorMap.get(mitt);
		if (successorList != null) {
			for (MessageInTransactionTypeType successor : successorList) {
				TransactionTypeType succTransaction = getTransaction(successor);
				if (succTransaction.equals(mittTransaction)) {
					endMitt = false;
					break;
				} else {
					boolean otherAndMainTransaction = isMainTransaction(succTransaction);
					if (otherAndMainTransaction) {
						continue;
					} else {
						endMitt = false;
						break;
					}
				}
			}
		}

		return endMitt;
	}

	private List<MessageInTransactionTypeType> outgoingTransaction(MessageInTransactionTypeType mitt) {
		List<MessageInTransactionTypeType> outgoingTransactions = new ArrayList<MessageInTransactionTypeType>();
		TransactionTypeType mittTransaction = getTransaction(mitt);

		List<MessageInTransactionTypeType> successorList = successorMap.get(mitt);
		if (successorList != null) {
			for (MessageInTransactionTypeType successor : successorList) {
				TransactionTypeType succTransaction = getTransaction(successor);
				if (!succTransaction.equals(mittTransaction) && !outgoingTransactions.contains(successor)) {
					outgoingTransactions.add(successor);
				}
			}
		}
		return outgoingTransactions;
	}

	private List<MessageInTransactionTypeType> incomingTransaction(MessageInTransactionTypeType mitt) {
		List<MessageInTransactionTypeType> incomingTransactions = new ArrayList<MessageInTransactionTypeType>();
		TransactionTypeType mittTransaction = getTransaction(mitt);

		Previous previous = mitt.getPrevious();
		if (previous != null) {
			List<Object> objects = previous.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
			for (Object object : objects) {
				MessageInTransactionTypeType prevMitt = (MessageInTransactionTypeType) getElementType(object);
				TransactionTypeType prevTransaction = getTransaction(prevMitt);
				if (!mittTransaction.equals(prevTransaction) && !incomingTransactions.contains(prevMitt)) {
					incomingTransactions.add(prevMitt);
				}
			}
		}

		return incomingTransactions;
	}

	public Canvas getDrawingPlane() {
		return drawingPlane;
	}
}
