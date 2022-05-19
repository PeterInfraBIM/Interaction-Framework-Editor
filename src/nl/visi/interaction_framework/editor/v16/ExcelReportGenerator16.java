package nl.visi.interaction_framework.editor.v16;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import jxl.CellView;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import nl.visi.interaction_framework.editor.v16.Control16;
import nl.visi.interaction_framework.editor.v16.MainPanelControl16.Tabs;
import nl.visi.interaction_framework.editor.v16.ComplexElementsPanelControl16.SimpleElementsTableModel;
import nl.visi.interaction_framework.editor.v16.ComplexElementsPanelControl16.SubComplexElementsTableModel;
//import nl.visi.interaction_framework.editor.v16.MessagesPanelControl16.ComplexElementsTableModel;
import nl.visi.interaction_framework.editor.v16.MessagesPanelControl16.TransactionsTableModel;
import nl.visi.interaction_framework.editor.v16.TransactionsPanelControl16.MessagesTableModel;
import nl.visi.schemas._20160331.AppendixTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeType;
import nl.visi.schemas._20160331.ElementType;
import nl.visi.schemas._20160331.MessageInTransactionTypeType;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Message;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Transaction;
import nl.visi.schemas._20160331.MessageTypeType;
import nl.visi.schemas._20160331.RoleTypeType;
import nl.visi.schemas._20160331.SimpleElementTypeType;
import nl.visi.schemas._20160331.SimpleElementTypeType.UserDefinedType;
import nl.visi.schemas._20160331.TransactionTypeType;
import nl.visi.schemas._20160331.TransactionTypeType.Executor;
import nl.visi.schemas._20160331.TransactionTypeType.Initiator;
import nl.visi.schemas._20160331.UserDefinedTypeType;

class ExcelReportGenerator16 extends Control16 {

	private final MainPanelControl16 mainPanelControl;

	public ExcelReportGenerator16(MainPanelControl16 mainPanel) {
		this.mainPanelControl = mainPanel;
	}

	public void writeReport(File excelFile) throws IOException {
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(excelFile);
			writeRoleTypesSheet(workbook, 0);
			writeTransactionTypesSheet(workbook, 1);
			writeMessageTypesSheet(workbook, 2);
			writeComplexElementTypesSheet(workbook, 3);
			writeSimpleElementTypesSheet(workbook, 4);
			writeDataTypesSheet(workbook, 5);
			writeAppendixTypesSheet(workbook, 6);
			workbook.write();
			workbook.close();
		} catch (WriteException e) {
			throw new IOException(e);
		}
	}

	private DateFormat customDateFormat = new DateFormat("dd MMM yyyy");
	private WritableCellFormat headingFormat;
	private WritableCellFormat dataFormat;
	private WritableCellFormat dateFormat;

	private WritableCellFormat getHeadingFormat() throws WriteException {
		if (headingFormat == null) {
			headingFormat = new WritableCellFormat();
			headingFormat.setBackground(Colour.OCEAN_BLUE);
			headingFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			WritableFont headingFont = new WritableFont(WritableFont.ARIAL, 9, WritableFont.BOLD);
			headingFont.setColour(Colour.WHITE);
			headingFormat.setFont(headingFont);
		}
		return headingFormat;
	}

	private WritableCellFormat getDateFormat() throws WriteException {
		if (dateFormat == null) {
			dateFormat = new WritableCellFormat(customDateFormat);
			// dataFormat.setBackground(Colour.OCEAN_BLUE);
			dateFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			WritableFont headingFont = new WritableFont(WritableFont.ARIAL, 9, WritableFont.NO_BOLD);
			// headingFont.setColour(Colour.WHITE);
			dateFormat.setFont(headingFont);
			dateFormat.setWrap(true);
		}
		return dateFormat;
	}

	private WritableCellFormat getDataFormat() throws WriteException {
		if (dataFormat == null) {
			dataFormat = new WritableCellFormat();
			// dataFormat.setBackground(Colour.OCEAN_BLUE);
			dataFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			WritableFont headingFont = new WritableFont(WritableFont.ARIAL, 9, WritableFont.NO_BOLD);
			// headingFont.setColour(Colour.WHITE);
			dataFormat.setFont(headingFont);
			dataFormat.setWrap(true);
		}
		return dataFormat;
	}

	private void writeRoleTypesSheet(WritableWorkbook workbook, int tabIndex) throws WriteException {
		WritableSheet sheet = workbook.createSheet(getBundle().getString("lbl_RoleTypesSheet"), tabIndex);
		initColumnView(sheet, 0);
		int col = 0;
		int row = 0;
		sheet.addCell(new Label(col, row, getBundle().getString("lbl_RoleTypesSheet")));
		List<RoleTypeType> roleTypes = Editor16.getStore16().getElements(RoleTypeType.class);
		for (RoleTypeType roleType : roleTypes) {
			row++;
			sheet.addCell(new Label(col, row, "ID", getHeadingFormat()));
			sheet.addCell(new Label(col + 1, row, roleType.getId(), getHeadingFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Description"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, roleType.getDescription(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_StartDate"), getDataFormat()));
			XMLGregorianCalendar startDate = roleType.getStartDate();
			if (startDate != null) {
				sheet.addCell(new DateTime(col + 1, row, roleType.getStartDate().toGregorianCalendar().getTime(),
						getDateFormat()));
			}
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_EndDate"), getDataFormat()));
			XMLGregorianCalendar endDate = roleType.getEndDate();
			if (endDate != null) {
				sheet.addCell(new DateTime(col + 1, row, roleType.getEndDate().toGregorianCalendar().getTime(),
						getDateFormat()));
			}
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_State"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, roleType.getState(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Language"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, roleType.getLanguage(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Category"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, roleType.getCategory(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_HelpInfo"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, roleType.getHelpInfo(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Code"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, roleType.getCode(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row,
					getBundle().getString("lbl_Responsibility") + " " + getBundle().getString("lbl_Scope"),
					getDataFormat()));
			sheet.addCell(new Label(col + 1, row, roleType.getResponsibilityScope(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row,
					getBundle().getString("lbl_Responsibility") + " " + getBundle().getString("lbl_Task"),
					getDataFormat()));
			sheet.addCell(new Label(col + 1, row, roleType.getResponsibilityTask(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row,
					getBundle().getString("lbl_Responsibility") + " " + getBundle().getString("lbl_SupportTask"),
					getDataFormat()));
			sheet.addCell(new Label(col + 1, row, roleType.getResponsibilitySupportTask(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row,
					getBundle().getString("lbl_Responsibility") + " " + getBundle().getString("lbl_Feedback"),
					getDataFormat()));
			sheet.addCell(new Label(col + 1, row, roleType.getResponsibilityFeedback(), getDataFormat()));
			row++;

			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Transactions"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, "", getDataFormat()));
			row++;
			List<TransactionTypeType> elements = Editor16.getStore16().getElements(TransactionTypeType.class);
			for (TransactionTypeType transaction : elements) {
				Initiator initiator = transaction.getInitiator();
				if (initiator != null) {
					RoleTypeType rt = initiator.getRoleType();
					if (rt == null) {
						rt = (RoleTypeType) initiator.getRoleTypeRef().getIdref();
					}
					if (roleType.equals(rt)) {
						sheet.addCell(new Label(col, row, "", getDataFormat()));
						sheet.addCell(new Label(col + 1, row,
								transaction.getId() + " (" + getBundle().getString("lbl_Initiator") + ")",
								getDataFormat()));
						row++;
						continue;
					}
				}
				Executor executor = transaction.getExecutor();
				if (executor != null) {
					RoleTypeType rt = executor.getRoleType();
					if (rt == null) {
						rt = (RoleTypeType) executor.getRoleTypeRef().getIdref();
					}
					if (roleType.equals(rt)) {
						sheet.addCell(new Label(col, row, "", getDataFormat()));
						sheet.addCell(new Label(col + 1, row,
								transaction.getId() + " (" + getBundle().getString("lbl_Executor") + ")",
								getDataFormat()));
						row++;
						continue;
					}
				}
			}

		}
	}

	private void writeTransactionTypesSheet(WritableWorkbook workbook, int tabIndex) throws WriteException {
		WritableSheet sheet = workbook.createSheet(getBundle().getString("lbl_TransactionTypesSheet"), tabIndex);
		initColumnView(sheet, 1);
		int col = 0;
		int row = 0;
		sheet.addCell(new Label(col, row, getBundle().getString("lbl_TransactionTypesSheet")));

		int transactionIndex = -1;
		List<TransactionTypeType> transactionTypes = Editor16.getStore16().getElements(TransactionTypeType.class);
		for (TransactionTypeType transactionType : transactionTypes) {
			transactionIndex++;
			row++;
			sheet.addCell(new Label(col, row, "ID", getHeadingFormat()));
			sheet.addCell(new Label(col + 1, row, transactionType.getId(), getHeadingFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Description"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, transactionType.getDescription(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_StartDate"), getDataFormat()));
			XMLGregorianCalendar startDate = transactionType.getStartDate();
			if (startDate != null) {
				sheet.addCell(new DateTime(col + 1, row, transactionType.getStartDate().toGregorianCalendar().getTime(),
						getDateFormat()));
			}
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_EndDate"), getDataFormat()));
			XMLGregorianCalendar endDate = transactionType.getEndDate();
			if (endDate != null) {
				sheet.addCell(new DateTime(col + 1, row, transactionType.getEndDate().toGregorianCalendar().getTime(),
						getDateFormat()));
			}
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_State"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, transactionType.getState(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Language"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, transactionType.getLanguage(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Category"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, transactionType.getCategory(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_HelpInfo"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, transactionType.getHelpInfo(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Code"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, transactionType.getCode(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Result"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, transactionType.getResult(), getDataFormat()));
			row++;
//			sheet.addCell(new Label(col, row, getBundle().getString("lbl_BasePoint"), getDataFormat()));
//			sheet.addCell(new Label(col + 1, row, transactionType.getBasePoint(), getDataFormat()));

			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Initiator"), getDataFormat()));
			Initiator initiator = transactionType.getInitiator();
			RoleTypeType roleType = null;
			if (initiator != null) {
				roleType = initiator.getRoleType();
				if (roleType == null) {
					roleType = (RoleTypeType) initiator.getRoleTypeRef().getIdref();
				}
			}
			sheet.addCell(new Label(col + 1, row, roleType != null ? roleType.getId() : "", getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Executor"), getDataFormat()));
			Executor executor = transactionType.getExecutor();
			if (executor != null) {
				roleType = executor.getRoleType();
				if (roleType == null) {
					roleType = (RoleTypeType) executor.getRoleTypeRef().getIdref();
				}
			}
			sheet.addCell(new Label(col + 1, row, roleType != null ? roleType.getId() : "", getDataFormat()));

			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Messages"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, "", getDataFormat()));
			mainPanelControl.tabs.setSelectedIndex(Tabs.Transactions.ordinal());
			TransactionsPanelControl16 transactionsPC = MainPanelControl16.getTransactionsPC();
			transactionsPC.tbl_Elements.getSelectionModel().setSelectionInterval(transactionIndex, transactionIndex);
			MessagesTableModel messagesTableModel = transactionsPC.getMessagesTableModel();
			for (int msgIndex = 0; msgIndex < messagesTableModel.getRowCount(); msgIndex++) {
				MessageInTransactionTypeType mitt = messagesTableModel.get(msgIndex);
				Message message = mitt.getMessage();
				MessageTypeType messageType = message.getMessageType();
				if (messageType == null) {
					messageType = (MessageTypeType) message.getMessageTypeRef().getIdref();
				}
				row++;
				sheet.addCell(new Label(col, row, "", getDataFormat()));
				sheet.addCell(new Label(col + 1, row, messageType.getId(), getDataFormat()));
			}
			row++;

		}
	}

	private void writeMessageTypesSheet(WritableWorkbook workbook, int tabIndex) throws WriteException {
		WritableSheet sheet = workbook.createSheet(getBundle().getString("lbl_MessageTypesSheet"), tabIndex);
		initColumnView(sheet, 2);
		int col = 0;
		int row = 0;
		sheet.addCell(new Label(col, row, getBundle().getString("lbl_MessageTypesSheet")));

		int messageIndex = -1;
		List<MessageTypeType> messageTypes = Editor16.getStore16().getElements(MessageTypeType.class);
		for (MessageTypeType messageType : messageTypes) {
			messageIndex++;
			row++;
			sheet.addCell(new Label(col, row, "ID", getHeadingFormat()));
			sheet.addCell(new Label(col + 1, row, messageType.getId(), getHeadingFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Description"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, messageType.getDescription(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_StartDate"), getDataFormat()));
			XMLGregorianCalendar startDate = messageType.getStartDate();
			if (startDate != null) {
				sheet.addCell(new DateTime(col + 1, row, messageType.getStartDate().toGregorianCalendar().getTime(),
						getDateFormat()));
			}
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_EndDate"), getDataFormat()));
			XMLGregorianCalendar endDate = messageType.getEndDate();
			if (endDate != null) {
				sheet.addCell(new DateTime(col + 1, row, messageType.getEndDate().toGregorianCalendar().getTime(),
						getDateFormat()));
			}
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_State"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, messageType.getState(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Language"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, messageType.getLanguage(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Category"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, messageType.getCategory(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_HelpInfo"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, messageType.getHelpInfo(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Code"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, messageType.getCode(), getDataFormat()));

			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Transactions"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, "", getDataFormat()));
			mainPanelControl.tabs.setSelectedIndex(Tabs.Messages.ordinal());
			MessagesPanelControl16 messagesPC = MainPanelControl16.getMessagesPC();
			messagesPC.tbl_Elements.getSelectionModel().setSelectionInterval(messageIndex, messageIndex);
			TransactionsTableModel transactionsTableModel = messagesPC.getTransactionsTableModel();
			for (int trnsIndex = 0; trnsIndex < transactionsTableModel.getRowCount(); trnsIndex++) {
				MessageInTransactionTypeType mitt = transactionsTableModel.get(trnsIndex);
				Transaction transaction = mitt.getTransaction();
				TransactionTypeType transactionType = transaction.getTransactionType();
				if (transactionType == null) {
					transactionType = (TransactionTypeType) transaction.getTransactionTypeRef().getIdref();
				}
				row++;
				sheet.addCell(new Label(col, row, "", getDataFormat()));
				sheet.addCell(new Label(col + 1, row, transactionType.getId(), getDataFormat()));
			}
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_ComplexElements"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, "", getDataFormat()));
//			ComplexElementsTableModel complexElementsTableModel = messagesPC.getComplexElementsTableModel();
//			for (int cmpeIndex = 0; cmpeIndex < complexElementsTableModel.getRowCount(); cmpeIndex++) {
//				ComplexElementTypeType complexElementTypeType = complexElementsTableModel.get(cmpeIndex);
//				row++;
//				sheet.addCell(new Label(col, row, "", getDataFormat()));
//				sheet.addCell(new Label(col + 1, row, complexElementTypeType.getId(), getDataFormat()));
//			}
			row++;

		}
	}

	private void writeComplexElementTypesSheet(WritableWorkbook workbook, int tabIndex) throws WriteException {
		WritableSheet sheet = workbook.createSheet(getBundle().getString("lbl_ComplexElementTypesSheet"), tabIndex);
		initColumnView(sheet, 3);
		int col = 0;
		int row = 0;
		sheet.addCell(new Label(col, row, getBundle().getString("lbl_ComplexElementTypesSheet")));

		int complexElementIndex = -1;
		List<ComplexElementTypeType> complexElementTypes = Editor16.getStore16()
				.getElements(ComplexElementTypeType.class);
		for (ComplexElementTypeType complexElementType : complexElementTypes) {
			complexElementIndex++;
			row++;
			sheet.addCell(new Label(col, row, "ID", getHeadingFormat()));
			sheet.addCell(new Label(col + 1, row, complexElementType.getId(), getHeadingFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Description"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, complexElementType.getDescription(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_StartDate"), getDataFormat()));
			XMLGregorianCalendar startDate = complexElementType.getStartDate();
			if (startDate != null) {
				sheet.addCell(new DateTime(col + 1, row,
						complexElementType.getStartDate().toGregorianCalendar().getTime(), getDateFormat()));
			}
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_EndDate"), getDataFormat()));
			XMLGregorianCalendar endDate = complexElementType.getEndDate();
			if (endDate != null) {
				sheet.addCell(new DateTime(col + 1, row,
						complexElementType.getEndDate().toGregorianCalendar().getTime(), getDateFormat()));
			}
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_State"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, complexElementType.getState(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Language"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, complexElementType.getLanguage(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Category"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, complexElementType.getCategory(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_HelpInfo"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, complexElementType.getHelpInfo(), getDataFormat()));

			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_ComplexElements"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, "", getDataFormat()));
			mainPanelControl.tabs.setSelectedIndex(Tabs.ComplexElements.ordinal());
			ComplexElementsPanelControl16 complexElementsPC = MainPanelControl16.getComplexElementsPC();
			complexElementsPC.tbl_Elements.getSelectionModel().setSelectionInterval(complexElementIndex,
					complexElementIndex);
//			SubComplexElementsTableModel subComplexElementsTableModel = complexElementsPC
//					.getSubComplexElementsTableModel();
//			for (int cpleIndex = 0; cpleIndex < subComplexElementsTableModel.getRowCount(); cpleIndex++) {
//				ComplexElementTypeType subComplexElementType = subComplexElementsTableModel.get(cpleIndex);
//				row++;
//				sheet.addCell(new Label(col, row, "", getDataFormat()));
//				sheet.addCell(new Label(col + 1, row, subComplexElementType.getId(), getDataFormat()));
//			}
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_SimpleElements"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, "", getDataFormat()));
			SimpleElementsTableModel simpleElementsTableModel = complexElementsPC.getSimpleElementsTableModel();
			for (int smpeIndex = 0; smpeIndex < simpleElementsTableModel.getRowCount(); smpeIndex++) {
				SimpleElementTypeType simpleElementTypeType = simpleElementsTableModel.get(smpeIndex);
				row++;
				sheet.addCell(new Label(col, row, "", getDataFormat()));
				sheet.addCell(new Label(col + 1, row, simpleElementTypeType.getId(), getDataFormat()));
			}
			row++;
		}
	}

	private void writeSimpleElementTypesSheet(WritableWorkbook workbook, int tabIndex) throws WriteException {
		WritableSheet sheet = workbook.createSheet(getBundle().getString("lbl_SimpleElementTypesSheet"), tabIndex);
		initColumnView(sheet, 4);
		int col = 0;
		int row = 0;
		sheet.addCell(new Label(col, row, getBundle().getString("lbl_SimpleElementTypesSheet")));
		row++;
		sheet.addCell(new Label(col++, row, "ID", getHeadingFormat()));
		sheet.addCell(new Label(col++, row, getBundle().getString("lbl_Description"), getHeadingFormat()));
		sheet.addCell(new Label(col++, row, getBundle().getString("lbl_InterfaceType"), getHeadingFormat()));
		sheet.addCell(new Label(col++, row, getBundle().getString("lbl_State"), getHeadingFormat()));
		sheet.addCell(new Label(col++, row, getBundle().getString("lbl_Language"), getHeadingFormat()));
		sheet.addCell(new Label(col++, row, getBundle().getString("lbl_Category"), getHeadingFormat()));
		sheet.addCell(new Label(col++, row, getBundle().getString("lbl_HelpInfo"), getHeadingFormat()));
		sheet.addCell(new Label(col++, row, getBundle().getString("lbl_ValueList"), getHeadingFormat()));
		sheet.addCell(new Label(col++, row, getBundle().getString("lbl_UserDefinedType"), getHeadingFormat()));
		row++;

		List<SimpleElementTypeType> simpleElementTypes = Editor16.getStore16().getElements(SimpleElementTypeType.class);
		for (SimpleElementTypeType simpleElementType : simpleElementTypes) {
			col = 0;
			sheet.addCell(new Label(col++, row, simpleElementType.getId(), getDataFormat()));
			sheet.addCell(new Label(col++, row, simpleElementType.getDescription(), getDataFormat()));
			sheet.addCell(new Label(col++, row, simpleElementType.getInterfaceType(), getDataFormat()));
			sheet.addCell(new Label(col++, row, simpleElementType.getState(), getDataFormat()));
			sheet.addCell(new Label(col++, row, simpleElementType.getLanguage(), getDataFormat()));
			sheet.addCell(new Label(col++, row, simpleElementType.getCategory(), getDataFormat()));
			sheet.addCell(new Label(col++, row, simpleElementType.getHelpInfo(), getDataFormat()));
			sheet.addCell(new Label(col++, row, simpleElementType.getValueList(), getDataFormat()));

			UserDefinedTypeType userDefinedType = getUserDefinedType(simpleElementType.getUserDefinedType());
			sheet.addCell(
					new Label(col++, row, userDefinedType != null ? userDefinedType.getId() : "", getDataFormat()));

			row++;
		}
	}

	private UserDefinedTypeType getUserDefinedType(UserDefinedType udt) {
		UserDefinedTypeType userDefinedType = null;
		if (udt != null) {
			userDefinedType = udt.getUserDefinedType();
			if (userDefinedType == null) {
				userDefinedType = (UserDefinedTypeType) udt.getUserDefinedTypeRef().getIdref();
			}
		}
		return userDefinedType;
	}

	private void writeDataTypesSheet(WritableWorkbook workbook, int tabIndex) throws WriteException {
		WritableSheet sheet = workbook.createSheet(getBundle().getString("lbl_DataTypesSheet"), tabIndex);
		initColumnView(sheet, 5);
		int col = 0;
		int row = 0;
		sheet.addCell(new Label(col, row, getBundle().getString("lbl_DataTypesSheet")));
		row++;
		sheet.addCell(new Label(col++, row, "ID", getHeadingFormat()));
		sheet.addCell(new Label(col++, row, getBundle().getString("lbl_Description"), getHeadingFormat()));
		sheet.addCell(new Label(col++, row, getBundle().getString("lbl_State"), getHeadingFormat()));
		sheet.addCell(new Label(col++, row, getBundle().getString("lbl_BaseType"), getHeadingFormat()));
		sheet.addCell(new Label(col++, row, getBundle().getString("lbl_XsdRestriction"), getHeadingFormat()));
		sheet.addCell(new Label(col++, row, getBundle().getString("lbl_Language"), getHeadingFormat()));
		sheet.addCell(new Label(col++, row, getBundle().getString("lbl_HelpInfo"), getHeadingFormat()));
		row++;

		List<UserDefinedTypeType> userDefinedTypes = Editor16.getStore16().getElements(UserDefinedTypeType.class);
		for (UserDefinedTypeType userDefinedType : userDefinedTypes) {
			col = 0;
			sheet.addCell(new Label(col++, row, userDefinedType.getId(), getDataFormat()));
			sheet.addCell(new Label(col++, row, userDefinedType.getDescription(), getDataFormat()));
			sheet.addCell(new Label(col++, row, userDefinedType.getState(), getDataFormat()));
			sheet.addCell(new Label(col++, row, userDefinedType.getBaseType(), getDataFormat()));
			sheet.addCell(new Label(col++, row, userDefinedType.getXsdRestriction(), getDataFormat()));
			sheet.addCell(new Label(col++, row, userDefinedType.getLanguage(), getDataFormat()));
			sheet.addCell(new Label(col++, row, userDefinedType.getHelpInfo(), getDataFormat()));

			row++;
		}
	}

	private void writeAppendixTypesSheet(WritableWorkbook workbook, int tabIndex) throws WriteException {
		WritableSheet sheet = workbook.createSheet(getBundle().getString("lbl_AppendixTypesSheet"), tabIndex);
		initColumnView(sheet, 2);
		int col = 0;
		int row = 0;
		sheet.addCell(new Label(col, row, getBundle().getString("lbl_AppendixTypesSheet")));
		int miscellaneousIndex = 0;
		MiscellaneousPanelControl16 miscellaneousPC = MainPanelControl16.getMiscellaneousPC();
		ElementType elementType = null;
		nl.visi.interaction_framework.editor.v16.MiscellaneousPanelControl16.ComplexElementsTableModel complexElementsTableModel = null;
//		do {
//			miscellaneousPC.tbl_Elements.getSelectionModel().setSelectionInterval(miscellaneousIndex, miscellaneousIndex);
//			elementType = miscellaneousPC.elementsTableModel.get(miscellaneousIndex++);
//			complexElementsTableModel = miscellaneousPC.getComplexElementsTableModel();
//		} while (!(elementType instanceof AppendixTypeType) && miscellaneousIndex < miscellaneousPC.tbl_Elements.getRowCount());
		List<AppendixTypeType> appendixTypes = Editor16.getStore16().getElements(AppendixTypeType.class);
		for (AppendixTypeType appendixType : appendixTypes) {
			miscellaneousIndex++;
			row++;
			sheet.addCell(new Label(col, row, "ID", getHeadingFormat()));
			sheet.addCell(new Label(col + 1, row, appendixType.getId(), getHeadingFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Description"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, appendixType.getDescription(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_StartDate"), getDataFormat()));
			XMLGregorianCalendar startDate = appendixType.getStartDate();
			if (startDate != null) {
				sheet.addCell(new DateTime(col + 1, row, appendixType.getStartDate().toGregorianCalendar().getTime(),
						getDateFormat()));
			}
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_EndDate"), getDataFormat()));
			XMLGregorianCalendar endDate = appendixType.getEndDate();
			if (endDate != null) {
				sheet.addCell(new DateTime(col + 1, row, appendixType.getEndDate().toGregorianCalendar().getTime(),
						getDateFormat()));
			}
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_State"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, appendixType.getState(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Language"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, appendixType.getLanguage(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Category"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, appendixType.getCategory(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_HelpInfo"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, appendixType.getHelpInfo(), getDataFormat()));
			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_Code"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, appendixType.getCode(), getDataFormat()));

			row++;
			sheet.addCell(new Label(col, row, getBundle().getString("lbl_ComplexElements"), getDataFormat()));
			sheet.addCell(new Label(col + 1, row, "", getDataFormat()));
			mainPanelControl.tabs.setSelectedIndex(Tabs.Miscellaneous.ordinal());
			while (!(elementType instanceof AppendixTypeType)
					&& miscellaneousIndex < miscellaneousPC.tbl_Elements.getRowCount()) {
				miscellaneousPC.tbl_Elements.getSelectionModel().setSelectionInterval(miscellaneousIndex,
						miscellaneousIndex);
				elementType = miscellaneousPC.elementsTableModel.get(miscellaneousIndex++);
				complexElementsTableModel = miscellaneousPC.getComplexElementsTableModel();
			}
			for (int trnsIndex = 0; trnsIndex < complexElementsTableModel.getRowCount(); trnsIndex++) {
				ComplexElementTypeType complexElementTypeType = complexElementsTableModel.get(trnsIndex);
				row++;
				sheet.addCell(new Label(col, row, "", getDataFormat()));
				sheet.addCell(new Label(col + 1, row, complexElementTypeType.getId(), getDataFormat()));
			}
			row++;
		}
	}

	private void initColumnView(WritableSheet sheet, int sheetIndex) {
		CellView cv = new CellView();

		switch (sheetIndex) {
		case 0:
		case 1:
		case 2:
		case 3:
			cv.setSize(28 * 256);
			sheet.setColumnView(0, cv);
			cv.setSize(50 * 256);
			sheet.setColumnView(1, cv);
			break;
		case 4:
			cv.setSize(28 * 256);
			sheet.setColumnView(0, cv);
			cv.setSize(28 * 256);
			sheet.setColumnView(1, cv);
			cv.setSize(6 * 256);
			sheet.setColumnView(2, cv);
			cv.setSize(10 * 256);
			sheet.setColumnView(3, cv);
			cv.setSize(15 * 256);
			sheet.setColumnView(4, cv);
			cv.setSize(35 * 256);
			sheet.setColumnView(5, cv);
			cv.setSize(8 * 256);
			sheet.setColumnView(6, cv);
			cv.setSize(16 * 256);
			sheet.setColumnView(7, cv);
			break;
		case 5:
			cv.setSize(28 * 256);
			sheet.setColumnView(0, cv);
			cv.setSize(28 * 256);
			sheet.setColumnView(1, cv);
			cv.setSize(6 * 256);
			sheet.setColumnView(2, cv);
			cv.setSize(10 * 256);
			sheet.setColumnView(3, cv);
			cv.setSize(50 * 256);
			sheet.setColumnView(4, cv);
			cv.setSize(10 * 256);
			sheet.setColumnView(5, cv);
			cv.setSize(28 * 256);
			sheet.setColumnView(6, cv);
			break;
		}
	}
}