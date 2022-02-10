package nl.visi.interaction_framework.editor.v16;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.prefs.Preferences;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.visi.interaction_framework.editor.Control;
import nl.visi.schemas._20160331.ComplexElementTypeType;
import nl.visi.schemas._20160331.ElementConditionType;
import nl.visi.schemas._20160331.ElementConditionType.MessageInTransaction;
import nl.visi.schemas._20160331.MessageInTransactionTypeType;
import nl.visi.schemas._20160331.MessageTypeTypeRef;
import nl.visi.schemas._20160331.ObjectFactory;
import nl.visi.schemas._20160331.RoleTypeType;
import nl.visi.schemas._20160331.TransactionTypeType;
import nl.visi.schemas._20160331.TransactionTypeTypeRef;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Message;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Previous;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Transaction;
import nl.visi.schemas._20160331.MessageInTransactionTypeTypeRef;

public class CopyTransaction {
	private ObjectFactory objectFactory;
	private TransactionsPanelControl16 transactionsPC;
	private Store16 store;
	private GregorianCalendar gcal;
	private Preferences userPrefs;

	public CopyTransaction() {
		objectFactory = new ObjectFactory();
		transactionsPC = MainPanelControl16.getTransactionsPC();
		store = Editor16.getStore16();
		gcal = new GregorianCalendar();
		userPrefs = Preferences.userNodeForPackage(Control.class);
	}

	public TransactionTypeType copyAttributes(TransactionTypeType origTransactionType) {
		try {
			TransactionTypeType copyTransactionType = objectFactory.createTransactionTypeType();
			transactionsPC.newElement(copyTransactionType, "Transaction_");
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
			return copyTransactionType;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void copyInternalRelations(TransactionTypeType origTransactionType,
			TransactionTypeType copyTransactionType) {

		List<MessageInTransactionTypeType> copyMitts = new ArrayList<MessageInTransactionTypeType>();
		try {
			// First pass
			for (MessageInTransactionTypeType origMitt : transactionsPC.messagesTableModel.elements) {
				MessageInTransactionTypeType copyMitt = objectFactory.createMessageInTransactionTypeType();
				copyMitts.add(copyMitt);
				copyMitt.setId(store.getNewId("Mitt_"));
				copyMitt.setAppendixTypes(origMitt.getAppendixTypes());
//				copyMitt.setConditions(origMitt.getConditions());
				gcal.setTime(new Date());
				copyMitt.setDateLaMu(DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal));
				copyMitt.setFirstMessage(origMitt.isFirstMessage());
				copyMitt.setGroup(origMitt.getGroup());
				copyMitt.setInitiatorToExecutor(origMitt.isInitiatorToExecutor());
				Message message = objectFactory.createMessageInTransactionTypeTypeMessage();
				MessageTypeTypeRef messageRef = objectFactory.createMessageTypeTypeRef();
				messageRef.setIdref(origMitt.getMessage().getMessageTypeRef().getIdref());
				message.setMessageTypeRef(messageRef);
				copyMitt.setMessage(message);
				copyMitt.setOpenSecondaryTransactionsAllowed(origMitt.isOpenSecondaryTransactionsAllowed());
				copyMitt.setReceived(origMitt.isReceived());
				copyMitt.setRequiredNotify(origMitt.getRequiredNotify());
				Transaction transaction = objectFactory.createMessageInTransactionTypeTypeTransaction();
				TransactionTypeTypeRef transactionRef = objectFactory.createTransactionTypeTypeRef();
				transactionRef.setIdref(copyTransactionType);
				transaction.setTransactionTypeRef(transactionRef);
				copyMitt.setTransaction(transaction);
				copyMitt.setTransactionPhase(origMitt.getTransactionPhase());
				copyMitt.setUserLaMu(userPrefs.get("user", "???"));
				store.put(copyMitt.getId(), copyMitt);
				// Element conditions specific for this mitt
				List<ElementConditionType> ecList = store.getElements(ElementConditionType.class);
				for (ElementConditionType origEC : ecList) {
					MessageInTransaction messageInTransaction = origEC.getMessageInTransaction();
					if (messageInTransaction != null) {
						MessageInTransactionTypeType ecMitt = messageInTransaction.getMessageInTransactionType();
						if (ecMitt == null) {
							ecMitt = (MessageInTransactionTypeType) messageInTransaction
									.getMessageInTransactionTypeRef().getIdref();
						}
						if (ecMitt.getId().equals(origMitt.getId())) {
							ElementConditionType copyEC = objectFactory.createElementConditionType();
							copyEC.setComplexElements(origEC.getComplexElements());
							copyEC.setCondition(origEC.getCondition());
							copyEC.setDescription(origEC.getDescription());
							copyEC.setHelpInfo(origEC.getHelpInfo());
							copyEC.setId(store.getNewId("EC_"));
							MessageInTransaction copyMessageInTransaction = objectFactory.createElementConditionTypeMessageInTransaction();
							MessageInTransactionTypeTypeRef copyMessageInTransactionRef = objectFactory.createMessageInTransactionTypeTypeRef();
							copyMessageInTransactionRef.setIdref(copyMitt);
							copyMessageInTransaction.setMessageInTransactionTypeRef(copyMessageInTransactionRef);
							copyEC.setMessageInTransaction(copyMessageInTransaction);
							copyEC.setSimpleElement(origEC.getSimpleElement());
							store.put(copyEC.getId(), copyEC);
						}
					}
				}
			}
			// Second pass
			int index = 0;
			for (MessageInTransactionTypeType origMitt : transactionsPC.messagesTableModel.elements) {
				List<MessageInTransactionTypeType> previousList = Control16.getPrevious(origMitt);
				if (previousList != null) {
					for (MessageInTransactionTypeType prevMitt : previousList) {
						TransactionTypeType transaction = Control16.getTransaction(prevMitt);
						if (transaction.getId().equals(origTransactionType.getId())) {
							int prefIndex = transactionsPC.messagesTableModel.elements.indexOf(prevMitt);
							Control16.addPrevious(copyMitts.get(index), copyMitts.get(prefIndex));
						}
					}
				}
				index++;
			}
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
}
