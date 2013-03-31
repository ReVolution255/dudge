/*
 * SolutionsQueue.java
 *
 * Created on September 14, 2007, 1:13 AM
 */

package dudge.slave;

//import dudge.DudgeLocal;
import dudge.ifaces.DudgeRemote;
import dudge.db.Solution;
import dudge.db.SolutionStatus;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * Entity class SolutionsQueue
 * 
 * @author Vladimir Shabanov
 */
@MessageDriven(mappedName = "jms/solutionsQueue", activationConfig =  {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
	})
public class SolutionsQueue implements MessageListener {

	private Logger logger = Logger.getLogger(this.getClass().toString());

	@EJB
	private DudgeRemote dudgeBean;
	
	@EJB
	private SlaveLocal slaveBean;	
	
	/** Creates a new instance of SolutionsQueue */
	public SolutionsQueue() {
	}

	public void onMessage(Message message) {		
		Solution solution = null;
		
		try {
			ObjectMessage objectMessage = (ObjectMessage) message;
			solution = (Solution) objectMessage.getObject();

			if(solution == null) {
				logger.severe("No solution object in JMS message.");
				return;
			}

			logger.fine("Solution " + solution.getSolutionId() + " received.");
			
			if(solution.getStatus() != SolutionStatus.NEW)
				return;

			slaveBean.testSolution(solution);
			
		} catch (JMSException ex) {
			logger.log(Level.SEVERE, "JMS exception during message parsing.", ex);

			if(solution != null) {
				solution.setStatus(SolutionStatus.INTERNAL_ERROR);

				StringWriter sw = new StringWriter();
				ex.printStackTrace(new PrintWriter(sw));
				solution.setStatusMessage(sw.getBuffer().toString());
				dudgeBean.saveSolution(solution);
			}

			return;
		} catch (Throwable ex) {
			logger.log(Level.SEVERE, "Internal slave error occured on solution "
					+ (solution != null ? solution.getSolutionId() : "")
					+ "Exception " + ex.getClass().getName(), ex);
			return;
		}
	}
	
}
