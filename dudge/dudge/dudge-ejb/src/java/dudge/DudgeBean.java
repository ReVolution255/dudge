/*
 * DudgeBean.java
 *
 * Created on April 14, 2007, 2:49 PM
 */

package dudge;

import dudge.db.ContestLanguage;
import dudge.db.ContestProblem;
import dudge.db.Param;
import dudge.db.Role;
import dudge.db.RoleType;
import dudge.db.SolutionStatus;
import dudge.monitor.AcmMonitorRecord;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import dudge.db.Contest;
import dudge.db.Language;
import dudge.db.Problem;
import dudge.db.Solution;
import dudge.db.User;
import dudge.db.Run;
import dudge.db.Test;
import dudge.monitor.GlobalMonitorRecord;
import dudge.monitor.SchoolMonitorRecord;
import java.net.URI;

/**
 * Класс основного Bean'а системы. Реализует
 * локальный и/или удаленный интерфейсы системы - содержит
 * методы для работы с объектами системы и выполнения
 * действий в ней.
 * @author Vladimir Shabanov
 */
@Stateless()
public class DudgeBean implements DudgeLocal, DudgeRemote {
	
	@Resource(mappedName="jms/solutionsQueueFactory")
	private ConnectionFactory solutionsQueueFactory;
	
	@Resource(mappedName = "jms/solutionsQueue")
	private Queue solutionsQueue;
	
	protected Logger logger = Logger.getLogger(this.getClass().toString());
	
	@PersistenceContext(unitName="dudge-ejbPU")
	private EntityManager em;
	
	private final int minimumPasswordLength = 3;
	private final int maximumPasswordLength = 128;
	
	/** Creates a new instance of DudgeBean */
	public DudgeBean() {
	}
	
	/*protected int getLastId(String seq) {
		return
				((List<Long>)
				em.createNativeQuery("SELECT last_value FROM " + seq)
				.getSingleResult())
				.get(0).intValue();
	}*/
	
	public String calcHash(String password) {
		/*return ( (List<String>)
		em.createNativeQuery("SELECT MD5( ?1 )").setParameter(1, password).getSingleResult()
		).get(0);*/
		
		try{
			byte[] bytes = password.getBytes("utf-8");
 
			java.security.MessageDigest algorithm =
					java.security.MessageDigest.getInstance("MD5");
 
			algorithm.reset();
			algorithm.update(bytes);
			byte messageDigest[] = algorithm.digest();
 
			StringBuffer hexString = new StringBuffer();
			for (int i=0;i<messageDigest.length;i++) {
				String hex = Integer.toHexString(0xFF & messageDigest[i]);
				if (hex.length() == 1) {
					hexString.append("0" + hex);
				} else {
					hexString.append(hex);
				}
			}
			return hexString.toString();
		} catch(java.security.NoSuchAlgorithmException nsae) {
			this.logger.severe("Unable to find hash algorithm.\n" + nsae.getMessage());
			return null;
		} catch(java.io.UnsupportedEncodingException uee) {
			this.logger.severe("Unsupported hash encoding.\n" + uee.getMessage());
			return null;
		}
	}
	
	public boolean authenticate(String login, String password) {
		
		if(login == null)
			throw new IllegalArgumentException("login is null.");
		
		if(password == null)
			throw new IllegalArgumentException("password is null.");
		
		User dbUser = em.find(User.class, login);
		
		//Если пользователь не авторизован
		if(dbUser == null) {
			this.logger.warning("Authentication for user '" + login + "' failed - user does not exist.");
			return false;
		}
		
		if( !dbUser.getPwdHash().equals(calcHash(password)) ) {
			this.logger.warning("Authentication for user '" + login + "' failed - incorrect password. ");// + calcHash(password) + " " + calcHash(password).length());
			//this.logger.severe(dbUser.getPwdHash() + " " + dbUser.getPwdHash().length());
			return false;
		}
		
		// Пользователь существует и пароль верен.
		return true;
	}
	
	public boolean isInRole(String login, int contestId, RoleType roleType) {
		long count = (Long) em.createQuery(
			"SELECT COUNT(r) FROM Role r WHERE"
			+ " r.contest.contestId = :contestId"
			+ " AND r.user.login = :username"
			+ " AND r.roleType = :roleType"
			)
		.setParameter("contestId", contestId)
		.setParameter("username", login)
		.setParameter("roleType", roleType.toString())
		.getResultList().get(0);
		
		return count != 0;
	}

	public boolean haveNoRoles(String login, int contestId) {
		long count = (Long) em.createQuery(
			"SELECT COUNT(r) FROM Role r WHERE"
			+ " r.contest.contestId = :contestId"
			+ " AND r.user.login = :username"
			)
		.setParameter("contestId", contestId)
		.setParameter("username", login)
		.getResultList().get(0);
		
		return count == 0;
	}
	
	public User getUser(String login) {
		
		User dbuser = (User) em.find(User.class, login);
		
		return dbuser;
	}
	
	public User registerUser(
			String login,
			String password,
			String email
			) {
		
		logger.finest("Attempting to register new user.");
		
/*		if(password.length() < minimumPasswordLength
				|| password.length() > maximumPasswordLength) {
 
			this.logger.finer("Password doesn't meet length "
					+ "requirements for account \""
					+ login + "\"");
			throw new ArrayIndexOutOfBoundsException("Password doesn't meet length "
					+ "requirements for account \""
					+ login + "\"");
 
		}*/
		
		User dbUser = new User(login.toLowerCase(), calcHash(password), email);
		dbUser.setRolesCollection(new ArrayList<Role>());
		
		em.persist(dbUser);
		em.flush();
		return dbUser;
	}

	public void joinAllOpenContests(String login) {
		User user = this.getUser(login);
		
		for(Contest contest : this.getContests()) {
			if(!contest.isOpen())
				continue;

			if(this.haveNoRoles(login, contest.getContestId())) {
				Role role = new Role(contest, user, RoleType.USER);
				em.merge(role);
				//contest.getRoles().add(role);
				em.flush();
			}
		}
	}
	
	public void modifyUser(User user) {
		em.merge(user);
	}
	
	public void deleteUser(String login) {
		em.remove( (User) em.find(User.class, login));
	}
	
	public Language getLanguage(String languageId) {
		return (Language) em.find(Language.class, languageId);
	}
	
	public List<Language> getLanguages() {	
		return (List<Language>) em.createNamedQuery("Language.getLanguages").getResultList();
	}
	
	public Language addLanguage(Language language) {
		em.persist(language);
		em.flush();
		return language;
	}
	
	public void modifyLanguage(Language language) {		
		em.merge(language);
	}

	public void deleteLanguage(String languageId){
		em.remove( (Language) em.find(Language.class, languageId));
	}
	
	public Contest getDefaultContest() {
		Param param = (Param) em.find(Param.class, "default_contest");
		if(param == null) {
			logger.severe("Database 'params' table doesn't contain 'default_contest'" +
					"parameter.");
			throw new NullPointerException("Database 'params' table doesn't contain 'default_contest'" +
					"parameter.");
		}
		
		return getContest( Integer.parseInt(param.getValue()) );
	}
	
	public Contest getContest(int contestId) {
		Contest contest = (Contest) em.find(Contest.class, contestId);

		return contest;
	}
	
	public List<Contest> getContests() {
		List<Contest> contests = (List<Contest>) em.createNamedQuery("Contest.getContests").getResultList();
		
		return contests;
	}

        public List<Contest> getPendingContests() {
		List<Contest> contests = (List<Contest>) em.createNamedQuery("Contest.getPendingContests").getResultList();

                List<Contest> pendingContests = new ArrayList<Contest>();

                for (Contest contest : contests)
                {
                    if(contest.isPending())
                    {
                        pendingContests.add(contest);
                    }
                }

		return pendingContests;
	}

        public List<Contest> getActiveContests() {
		List<Contest> contests = (List<Contest>) em.createNamedQuery("Contest.getActiveContests").getResultList();

                List<Contest> activeContests = new ArrayList<Contest>();

                for (Contest contest : contests)
                {
                    if(contest.isInProgress())
                    {
                        activeContests.add(contest);
                    }
                }

		return activeContests;
	}

        public List<Contest> getRecentlyFinishedContests() {
		List<Contest> contests = (List<Contest>) em.createNamedQuery("Contest.getRecentlyFinishedContests").getResultList();

                List<Contest> recentlyFinishedContests = new ArrayList<Contest>();

                for (Contest contest : contests)
                {
                    java.util.Calendar c = java.util.Calendar.getInstance();
                    c.setTime(contest.getEndTime());
                    c.add(java.util.Calendar.DAY_OF_MONTH, 7);

                    if(contest.isFinished() && c.after(java.util.Calendar.getInstance()))
                    {
                        recentlyFinishedContests.add(contest);
                    }
                }
                
		return recentlyFinishedContests;
	}

	public Contest addContest(Contest contest) {
		em.persist(contest);
		em.flush();
		return contest;
	}
	
	public void modifyContest(Contest contest) {
		
				
		// ВНИМАНИЕ! Данный код написан с учетом того, что в спецификации EJB 3.0 
		// не предусмотрено автоматическое каскадное удаление сущностей при @OneToMany-merge.
		
		// Получаем старые значения, сохраненные в коллекциях.
		
		List<Solution> oldSolutions = (List<Solution>) em.createNamedQuery("Solution.findByContestId").
				setParameter("contestId" ,contest.getContestId()).getResultList();
		List<Role> oldRoles = (List<Role>) em.createNamedQuery("Role.findByContestId").
				setParameter("contestId" ,contest.getContestId()).getResultList();
		List<ContestProblem> oldContestProblems = (List<ContestProblem>) em.createNamedQuery("ContestProblem.findByContestId").
				setParameter("contestId" ,contest.getContestId()).getResultList();
		List<ContestLanguage> oldContestLanguages = (List<ContestLanguage>) em.createNamedQuery("ContestLanguage.findByContestId").
				setParameter("contestId" ,contest.getContestId()).getResultList();
		
		// Если некоторая сущность более не присутствует в БД, она удаляется.
		List<Solution> newSolutions = (List<Solution>) contest.getSolutions();
		for (Iterator<Solution> iter = oldSolutions.iterator(); iter.hasNext();) {
			Solution s = iter.next();
			if (!newSolutions.contains(s)) {
				iter.remove();
				em.remove(s);
			}
		}
		
		// Если некоторая сущность более не присутствует в БД, она удаляется.
		List<Role> newRoles = (List<Role>) contest.getRoles();
		for (Iterator<Role> iter = oldRoles.iterator(); iter.hasNext();) {
			Role r = iter.next();
			if (!newRoles.contains(r)) {
				iter.remove();
				em.remove(r);
			}
		}
		
		// Если некоторая сущность более не присутствует в БД, она удаляется.
		List<ContestLanguage> newContestLanguages = (List<ContestLanguage>) contest.getContestLanguages();
		for (Iterator<ContestLanguage> iter = oldContestLanguages.iterator(); iter.hasNext();) {
			ContestLanguage cl = iter.next();
			if (!newContestLanguages.contains(cl)) {
				iter.remove();
				em.remove(cl);
			}
		}
		
		// Если некоторая сущность более не присутствует в БД, она удаляется.
		List<ContestProblem> newContestProblems = (List<ContestProblem>) contest.getContestProblems();
		for (Iterator<ContestProblem> iter = oldContestProblems.iterator(); iter.hasNext();) {
			ContestProblem cp = iter.next();
			if (!newContestProblems.contains(cp)) {
				iter.remove();
				em.remove(cp);
			}
		}
		
		em.merge(contest);
	}
	
	public void deleteContest(int contestId) {
		em.remove( (Contest) em.find(Contest.class, contestId) );
	}
	
	public Problem getProblem(int problemId) {
		return (Problem) em.find(Problem.class, problemId);
	}

	public long getProblemsCount() {
		return (Long) em.createQuery("SELECT COUNT(p) FROM Problem p").getSingleResult();
	}
	
	public List<Problem> getProblems() {
		return (List<Problem>) em.createNamedQuery("Problem.getProblems").getResultList();
	}

	public List<Problem> getProblems(int start, int limit) {
		return (List<Problem>) em.createQuery(
				"SELECT p FROM Problem p ORDER BY p.problemId"
				).setFirstResult(start).setMaxResults(limit).getResultList();
	}
		
	public List<User> getUsers(){
		return (List<User>) em.createNamedQuery("User.getUsers").getResultList();
	}
	
	public Problem addProblem(Problem problem) {
		
		em.persist(problem);
		em.flush();
		return problem;
	}
	
	public void modifyProblem(Problem problem) {	
		em.merge(problem);
	}
	
	public void deleteProblem(int problemId) {
		em.remove( (Problem) em.find(Problem.class, problemId) );
	}
	
	public Solution getSolution(int solutionId) {
		return (Solution) em.find(Solution.class, solutionId);
	}
	
	public Solution getSolutionEager(int solutionId) {
		//Logger.getLogger(this.getClass().getName()).info("Trying to get solution " + solutionId);
		Solution solution = getSolution(solutionId);
		if(solution == null)
			return null;
		
		// Инстанциируем отложенную загрузку для того, чтобы раб мог
		// получать коллекции.
		solution.getRuns().size();
		solution.getContest().getContestProblems().size();
		solution.getContest().getContestLanguages().size();
		solution.getProblem().getTests().size();
		
		return solution;
	}

	public List<Solution> getSolutions(String login, int contestId, int problemId) {
		List<Solution> lcpSolutions = (List<Solution>) em.createNamedQuery(
				"Solution.findByUserContestProblem"
				).setParameter("login", login)
				.setParameter("contestId", contestId)
				.setParameter("problemId", problemId)
				.getResultList();

		// Удаляем из полученного списка решений те, которые не попадают
		// в интервал проведения соревнования.
		List<Solution> solutions = new ArrayList<Solution>();
		for(Solution solution: lcpSolutions) {
			Date endTime = new Date(
					solution.getContest().getStartTime().getTime()
					+ solution.getContest().getDuration() * (long) 1000
					);
			if( ( solution.getSubmitTime().after(solution.getContest().getStartTime()) )
				&& ( solution.getSubmitTime().before(endTime)
					|| solution.getContest().getDuration() == 0 )
				) {
				solutions.add(solution);
			}
		}
		
		return solutions;
	}	
	
	public List<Solution> getPendingSolutions() {
		return (List<Solution>) em.createNamedQuery(
				"Solution.getPendingSolutions"
				).getResultList();
	}
	
	public List<Solution> getLastSolutions(int count) {
		return (List<Solution>) em.createNamedQuery(
				"Solution.getLastSolutions"
				).setMaxResults(count).getResultList();
	}
	
	public Solution submitSolution(Solution solution) {
		solution.setStatus(SolutionStatus.NEW);
		solution.setSubmitTime(new Date());
		
		User user = this.getUser(solution.getUser().getLogin());
		
		Contest contest = this.getContest(solution.getContest().getContestId());
		contest.getSolutions().add(solution);

		if(
			contest.isOpen() &&
			this.haveNoRoles(user.getLogin(), contest.getContestId())
			)
		{
			Role autoRegisteredUser = new Role(contest, user, RoleType.USER);
			contest.getRoles().add(em.merge(autoRegisteredUser));
		}
		
		em.flush();
		
		try {
			sendJMSMessageToSolutionsQueue( this.getSolutionEager(solution.getSolutionId()) );
			logger.info("Solution " + solution.getSolutionId() + " submitted to JMS queue.");
		} catch (JMSException ex) {
			logger.log(Level.SEVERE, "Exception thrown when sending solution message.", ex);
			throw new RuntimeException(ex);
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, "Exception thrown when sending solution message.", ex);
			throw new RuntimeException(ex);
		}
		
		return solution;
	}
	
	public void resubmitSolution(int solutionId) {
		Solution solution = this.getSolution(solutionId);
		solution.setStatus(SolutionStatus.NEW);
		
		// Удаляем все запуски на тестах для данного решения.
		for(Run run : solution.getRuns()){
			em.remove(run);
		}
		solution.getRuns().clear();
		
		try {
			sendJMSMessageToSolutionsQueue( this.getSolutionEager(solution.getSolutionId()) );
			logger.info("Solution " + solution.getSolutionId() + " submitted to JMS queue.");
		} catch (JMSException ex) {
			logger.log(Level.SEVERE, "Exception thrown when sending solution message.", ex);
			throw new RuntimeException(ex);
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, "Exception thrown when sending solution message.", ex);
			throw new RuntimeException(ex);
		}
	}

	public void resubmitSolutions(int contestId, int problemId) {
		List<Solution> sols = (List<Solution>) em.createQuery(
				"SELECT s FROM Solution s WHERE s.contestId = :contestId" +
				" AND s.problemId = :problemId" +
				" AND s.status = 'PROCESSED'"
				)
		.setParameter("contestId", contestId)
		.setParameter("problemId", problemId)
		.getResultList();
		
		for(Solution sol : sols) {
			this.resubmitSolution(sol.getSolutionId());
		}
	}
	
	public void modifySolution(Solution solution) {
		Solution merged = em.merge(solution);

		// Если решение прошло все тесты, то помечаем задачу
		// как здоровую.
		if(merged.isAllTestsPassed()) {
			merged.getProblem().setHealthy(true);
		}
		
		em.flush();
	}

	public void saveSolution(Solution solution) {

		Solution dbs = this.getSolution(solution.getSolutionId());

		dbs.setStatus(solution.getStatus());
		dbs.setStatusMessage(solution.getStatusMessage());
		dbs.setCompilationTime(solution.getCompilationTime());

		for(Run run : dbs.getRuns())
		{
			em.remove(run);
		}

		dbs.getRuns().clear();

		for(Run run : solution.getRuns())
		{
			dbs.getRuns().add(em.merge(run));
		}
	
		// Если решение прошло все тесты, то помечаем задачу
		// как здоровую.
		if(dbs.isAllTestsPassed()) {
			dbs.getProblem().setHealthy(true);
		}
		em.flush();
	}
	
	public Test getTest(int testId) {
		return (Test) em.createNamedQuery("Test.findByTestId").
				setParameter("testId", testId).getSingleResult();
	}
	
	public Test addTest(Test test) {
		Problem problem = em.find(Problem.class, test.getProblem().getProblemId());
		problem.getTests().add(test);
		
		// Помечаем задачу как нездоровую.
		problem.setHealthy(false);
		em.flush();

		return test;
	}
	
	public void modifyTest(Test test){
		Test merged = em.merge(test);
		
		// Помечаем задачу как нездоровую.
		merged.getProblem().setHealthy(false);
		
		em.flush();
		em.refresh(merged.getProblem());
	}

	public void deleteTest(int testId) {
		
		Problem problem = em.find(Test.class, testId).getProblem();
		int problemId = problem.getProblemId();
		int testNumber = em.find(Test.class, testId).getTestNumber();
		
		em.remove(em.find(Test.class, testId));
		
		problem.setTests( (List<Test>) em.
				createNamedQuery("Test.findByProblemId").
				setParameter("problemId" , problemId).getResultList() );
		
		Collections.sort( (List<Test>) problem.getTests());
		//problem.getTests().remove( em.find(Test.class , testId));
		//em.merge(problem);
		/*for (Test test : problem.getTests()) {
			if (test.getTestNumber() > testNumber)
				test.setTestNumber(test.getTestNumber() - 1);
		}*/
		int testCount = problem.getTests().size();
		for (int i = testNumber +1; i <= testCount+1; i++) {
			int index = ( (List<Test>) problem.getTests()).indexOf(
					(Test) em.createNamedQuery("Test.findByNumberAndProblemId").
					setParameter("testNumber" , i).setParameter("problemId" , problemId).
					getSingleResult());
			((List<Test>) problem.getTests()).get(index).setTestNumber(i - 1);
			
		}
		
		em.merge(problem);
		
		em.flush();		
	}
	
	public int getMinimumPasswordLength() {
		return minimumPasswordLength;
	}
	
	public int getMaximumPasswordLength() {
		return maximumPasswordLength;
	}
	
	public List<GlobalMonitorRecord> getGlobalMonitorRecords(Contest contest, Date when) {
		LinkedList<GlobalMonitorRecord> rows = new LinkedList<GlobalMonitorRecord>();
		
		for(Role role : contest.getRoles()) {
			// Показываем в мониторе только пользователей, которые являются
			// обычными участниками соревнования.
			if(role.getRoleType() != RoleType.USER)
				continue;
			
			rows.add( new GlobalMonitorRecord(this, contest, role.getUser(), when) );
		}
		
		Collections.sort(rows);
		Collections.reverse(rows);
		
		for(int i = 0; i < rows.size(); ++i) {
			rows.get(i).setPlace(i+1);
		}
		
		return rows;
	}
	
	public List<AcmMonitorRecord> getAcmMonitorRecords(Contest contest, Date when) {
		LinkedList<AcmMonitorRecord> rows = new LinkedList<AcmMonitorRecord>();
		
		for(Role role : contest.getRoles()) {
			// Показываем в мониторе только пользователей, которые являются
			// обычными участниками соревнования.
			if(role.getRoleType() != RoleType.USER)
				continue;
			
			rows.add( new AcmMonitorRecord(this, contest, role.getUser(), when) );
		}
		
		Collections.sort(rows);
		Collections.reverse(rows);
		
		for(int i = 0; i < rows.size(); ++i) {
			rows.get(i).setPlace(i+1);
		}
		
		return rows;
	}

        public List<SchoolMonitorRecord> getSchoolMonitorRecords(Contest contest, Date when) {
		LinkedList<SchoolMonitorRecord> rows = new LinkedList<SchoolMonitorRecord>();

		for(Role role : contest.getRoles()) {
			// Показываем в мониторе только пользователей, которые являются
			// обычными участниками соревнования.
			if(role.getRoleType() != RoleType.USER)
				continue;

			rows.add( new SchoolMonitorRecord(this, contest, role.getUser(), when) );
		}

		Collections.sort(rows);
		Collections.reverse(rows);

		for(int i = 0; i < rows.size(); ++i) {
			rows.get(i).setPlace(i+1);
		}

		return rows;
	}

	public URI getBugTrackingPath() {
		Param uriParam = em.find(Param.class, "bug_tracking_uri");
		URI bugURI = URI.create("https://www.glint.ru/dev/dudge/");
		
		try {
		if(uriParam != null) {
			bugURI = URI.create(uriParam.getValue());
		}
		} catch (IllegalArgumentException ex) {
			logger.log(Level.WARNING, "Wrong bugtracking URI parameter in database.", ex);
		}
		
		return bugURI;
	}
	
	private Message createJMSMessageForSolutionsQueue(Session session, Object messageData) throws JMSException {
		ObjectMessage message = session.createObjectMessage((Solution) messageData);
		return message;
	}
	
	private void sendJMSMessageToSolutionsQueue(Object messageData) throws NamingException, JMSException {
		Connection connection = null;
		Session session = null;
		try {
			connection = solutionsQueueFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer messageProducer = session.createProducer(solutionsQueue);
			messageProducer.send(createJMSMessageForSolutionsQueue(session, messageData));
		} finally {
			if (session != null) {
				session.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}	
}