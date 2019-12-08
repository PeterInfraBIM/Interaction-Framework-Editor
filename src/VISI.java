import java.math.BigInteger;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.visi.interaction_schema.GroupTypeType;
import nl.visi.interaction_schema.GroupTypeTypeRef;
import nl.visi.interaction_schema.MessageInTransactionTypeType;
import nl.visi.interaction_schema.MessageTypeType;
import nl.visi.interaction_schema.MessageTypeTypeRef;
import nl.visi.interaction_schema.ObjectFactory;
import nl.visi.interaction_schema.ProjectTypeType;
import nl.visi.interaction_schema.RoleTypeType;
import nl.visi.interaction_schema.RoleTypeTypeRef;
import nl.visi.interaction_schema.TransactionTypeType;
import nl.visi.interaction_schema.TransactionTypeTypeRef;
import nl.visi.interaction_schema.VisiXMLVISISystematics;
import nl.visi.interaction_schema.MessageInTransactionTypeType.Group;
import nl.visi.interaction_schema.MessageInTransactionTypeType.Message;
import nl.visi.interaction_schema.MessageInTransactionTypeType.Transaction;
import nl.visi.interaction_schema.TransactionTypeType.Executor;
import nl.visi.interaction_schema.TransactionTypeType.Initiator;


public class VISI {
	private ObjectFactory of;

	public VISI() throws DatatypeConfigurationException, JAXBException {
		of = new ObjectFactory();
		VisiXMLVISISystematics visiXMLVISISystematics = of.createVisiXMLVISISystematics();

		ProjectTypeType projectTypeType = of.createProjectTypeType();
		DatatypeFactory newInstance = DatatypeFactory.newInstance();
		XMLGregorianCalendar gregorianCalendar = newInstance.newXMLGregorianCalendar(new GregorianCalendar());
		projectTypeType.setDateLamu(gregorianCalendar);
		projectTypeType.setDescription("");
		projectTypeType.setId("StandardProject");
		projectTypeType.setEndDate(newInstance.newXMLGregorianCalendar(2010, 12, 31, 23, 59, 59, 999, 0));
		projectTypeType.setStartDate(newInstance.newXMLGregorianCalendar(2010, 1, 1, 0, 0, 0, 0, 0));
		projectTypeType.setState("active");
		projectTypeType.setUserLamu("PWI");

		GroupTypeType groupTypeType = of.createGroupTypeType();
		groupTypeType.setId("StandaardGroep");
		groupTypeType.setDescription("Standaard Groep");
		groupTypeType.setEndDate(newInstance.newXMLGregorianCalendar(2010, 12, 31, 23, 59, 59, 999, 0));
		groupTypeType.setStartDate(newInstance.newXMLGregorianCalendar(2010, 1, 1, 0, 0, 0, 0, 0));
		groupTypeType.setState("active");
		groupTypeType.setDateLamu(gregorianCalendar);
		groupTypeType.setUserLamu("PWI");

		RoleTypeType oberRoleTypeType = of.createRoleTypeType();
		oberRoleTypeType.setId("Ober");
		oberRoleTypeType.setDescription("Ober");
		oberRoleTypeType.setEndDate(newInstance.newXMLGregorianCalendar(2010, 12, 31, 23, 59, 59, 999, 0));
		oberRoleTypeType.setStartDate(newInstance.newXMLGregorianCalendar(2010, 1, 1, 0, 0, 0, 0, 0));
		oberRoleTypeType.setState("active");
		oberRoleTypeType.setDateLamu(gregorianCalendar);
		oberRoleTypeType.setUserLamu("PWI");
		RoleTypeType klantRoleTypeType = of.createRoleTypeType();
		klantRoleTypeType.setId("Klant");
		klantRoleTypeType.setDescription("Klant");
		klantRoleTypeType.setEndDate(newInstance.newXMLGregorianCalendar(2010, 12, 31, 23, 59, 59, 999, 0));
		klantRoleTypeType.setStartDate(newInstance.newXMLGregorianCalendar(2010, 1, 1, 0, 0, 0, 0, 0));
		klantRoleTypeType.setState("active");
		klantRoleTypeType.setDateLamu(gregorianCalendar);
		klantRoleTypeType.setUserLamu("PWI");

		TransactionTypeType transactionTypeType = of.createTransactionTypeType();
		transactionTypeType.setId("T1");
		transactionTypeType.setDescription("T1 transactie");
		transactionTypeType.setStartDate(newInstance.newXMLGregorianCalendar(2010, 1, 1, 0, 0, 0, 0, 0));
		transactionTypeType.setEndDate(newInstance.newXMLGregorianCalendar(2010, 12, 31, 23, 59, 59, 999, 0));
		transactionTypeType.setState("active");
		transactionTypeType.setDateLamu(gregorianCalendar);
		transactionTypeType.setUserLamu("PWI");

		Initiator initiator = of.createTransactionTypeTypeInitiator();
		RoleTypeTypeRef oberRoleTypeTypeRef = of.createRoleTypeTypeRef();
		oberRoleTypeTypeRef.setIdref(oberRoleTypeType);
		initiator.setRoleTypeRef(oberRoleTypeTypeRef);
		transactionTypeType.setInitiator(initiator);

		Executor executor = of.createTransactionTypeTypeExecutor();
		RoleTypeTypeRef klantRoleTypeTypeRef = of.createRoleTypeTypeRef();
		klantRoleTypeTypeRef.setIdref(klantRoleTypeType);
		executor.setRoleTypeRef(klantRoleTypeTypeRef);
		transactionTypeType.setExecutor(executor);

		MessageTypeType messageTypeType = of.createMessageTypeType();
		messageTypeType.setId("WiltUDeKaartZien");
		messageTypeType.setDescription("Wilt U de kaart zien?");
		messageTypeType.setStartDate(newInstance.newXMLGregorianCalendar(2010, 1, 1, 0, 0, 0, 0, 0));
		messageTypeType.setEndDate(newInstance.newXMLGregorianCalendar(2010, 12, 31, 23, 59, 59, 999, 0));
		messageTypeType.setState("active");
		messageTypeType.setDateLamu(gregorianCalendar);
		messageTypeType.setUserLamu("PWI");

		MessageInTransactionTypeType messageInTransactionTypeType = of.createMessageInTransactionTypeType();
		messageInTransactionTypeType.setId("Bericht1");
		messageInTransactionTypeType.setRequiredNotify(new BigInteger("0"));
		messageInTransactionTypeType.setDateLamu(gregorianCalendar);
		messageInTransactionTypeType.setUserLamu("PWI");
		messageInTransactionTypeType.setState("active");

		Message message = of.createMessageInTransactionTypeTypeMessage();
		MessageTypeTypeRef messageTypeTypeRef = of.createMessageTypeTypeRef();
		messageTypeTypeRef.setIdref(messageTypeType);
		message.setMessageTypeRef(messageTypeTypeRef);
		messageInTransactionTypeType.setMessage(message);

		Transaction transaction = of.createMessageInTransactionTypeTypeTransaction();
		TransactionTypeTypeRef transactionTypeTypeRef = of.createTransactionTypeTypeRef();
		transactionTypeTypeRef.setIdref(transactionTypeType);
		transaction.setTransactionTypeRef(transactionTypeTypeRef);
		messageInTransactionTypeType.setTransaction(transaction);

		Group group = of.createMessageInTransactionTypeTypeGroup();
		GroupTypeTypeRef groupTypeTypeRef = of.createGroupTypeTypeRef();
		groupTypeTypeRef.setIdref(groupTypeType);
		group.setGroupTypeRef(groupTypeTypeRef);
		messageInTransactionTypeType.setGroup(group);

		visiXMLVISISystematics.getAppendixTypeOrComplexElementTypeOrElementCondition().add(projectTypeType);
		visiXMLVISISystematics.getAppendixTypeOrComplexElementTypeOrElementCondition().add(groupTypeType);
		visiXMLVISISystematics.getAppendixTypeOrComplexElementTypeOrElementCondition().add(transactionTypeType);
		visiXMLVISISystematics.getAppendixTypeOrComplexElementTypeOrElementCondition().add(messageTypeType);
		visiXMLVISISystematics.getAppendixTypeOrComplexElementTypeOrElementCondition().add(oberRoleTypeType);
		visiXMLVISISystematics.getAppendixTypeOrComplexElementTypeOrElementCondition().add(klantRoleTypeType);
		visiXMLVISISystematics.getAppendixTypeOrComplexElementTypeOrElementCondition()
				.add(messageInTransactionTypeType);

		JAXBContext jc = JAXBContext.newInstance("visi");
		Marshaller m = jc.createMarshaller();
		m.marshal(visiXMLVISISystematics, System.out);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new VISI();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
