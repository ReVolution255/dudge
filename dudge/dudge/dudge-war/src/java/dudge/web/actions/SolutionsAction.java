/*
 * SolutionAction.java
 *
 * Created on June 19, 2007, 12:31 AM
 */

package dudge.web.actions;

import dudge.DudgeLocal;
import dudge.db.Contest;
import dudge.db.Language;
import dudge.db.Problem;
import dudge.db.Solution;
import dudge.db.SolutionStatus;
import dudge.db.User;
import dudge.PermissionCheckerRemote;
import dudge.web.SessionObject;
import dudge.web.forms.SolutionsForm;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

/**
 * Струтсовый экшен для операций с решениями.
 * @author Vladimir Shabanov
 */
public class SolutionsAction extends DispatchAction {
	
	private Logger logger = Logger.getLogger(this.getClass().toString());
	
	/** Creates a new instance of SolutionsAction */
	public SolutionsAction() {
	}
	
	private DudgeLocal lookupDudgeBean() {
		try {
			Context c = new InitialContext();
			return (DudgeLocal) c.lookup("java:comp/env/ejb/DudgeBean");
		}
		catch(NamingException ne) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,"exception caught" ,ne);
			throw new RuntimeException(ne);
		}
	}
		
	public ActionForward view(
			ActionMapping mapping,
			ActionForm af,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		SolutionsForm sf = (SolutionsForm) af;
		
		DudgeLocal dudgeBean = lookupDudgeBean();
		
		SessionObject so = SessionObject.extract(request.getSession());
		if(so.getUsername() == null)
			return mapping.findForward("loginRequired");
		
		User user = dudgeBean.getUser(so.getUsername());
		int solutionId = Integer.parseInt(request.getParameter("solutionId"));
		// Проверяем право пользователя на просмотр решения.
		if(!so.getPermissionChecker().canViewSolution(user.getLogin(), solutionId)) {
			return mapping.findForward("accessDenied");
		}
		
		Solution solution = dudgeBean.getSolution(solutionId);
		
		sf.setSolutionId(Integer.toString(solution.getSolutionId()));
		
		if(solution.getStatus() != SolutionStatus.PROCESSED
				|| solution.getContest().getTraits().isRunAllTests()
				|| solution.getLastRunResult() == null) {
			sf.setStatus(solution.getStatus().toString());
		} else {
			sf.setStatus(solution.getLastRunResult().toString());
		}
		
		try {
			sf.setCurrentTestNumber(Collections.max(solution.getRuns()).getRunNumber());
		} catch (NoSuchElementException ex) {
			sf.setCurrentTestNumber(0);
		}
		
		sf.setStatusMessage(solution.getStatusMessage());
		
		sf.setSourceCode(solution.getSourceCode());
		
		return mapping.findForward("viewSolution");
	}
	
	public ActionForward submit(
			ActionMapping mapping,
			ActionForm af,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		SolutionsForm sf = (SolutionsForm) af;
		
		sf.reset(mapping, request);	
		
		SessionObject so = SessionObject.extract(request.getSession());
		DudgeLocal dudgeBean = lookupDudgeBean();
		int contestId;
		// Получаем идентификатор соревнования.
		String param = request.getParameter("contestId");
		if(param != null)
			contestId = Integer.parseInt(param);
		else
			// Если нам не послали идентификатор, то используем идентификатор
			// текущего соревнования.
			contestId = dudgeBean.getDefaultContest().getContestId();

		Contest contest = dudgeBean.getContest(contestId);

		// Проверяем право пользователя.
		PermissionCheckerRemote pcb = so.getPermissionChecker();
		if (!pcb.canSubmitSolution(
				so.getUsername(),
				contest.getContestId()
				)
				) {
			return mapping.findForward("accessDenied");
		}

		String prob = request.getParameter("problemId");
		int problemId = -1;
		if(prob != null) {
			problemId = Integer.parseInt(prob);
		} else if (contest.getContestProblems().size() != 0) {
			problemId = contest.getContestProblems().get(0).getProblem().getProblemId();
		}
		
		// Установление свойств, нужных для корректного отображение параметров языка соревнования.
		sf.getContestLanguages().addAll(contest.getContestLanguages());
		sf.getContestProblems().addAll(contest.getContestProblems());
		
		sf.setProblemId(Integer.toString(problemId));
		
		return mapping.findForward("submitSolution");
	}

	public ActionForward submitSubmit(
			ActionMapping mapping,
			ActionForm af,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		SolutionsForm sf = (SolutionsForm) af;
		DudgeLocal dudgeBean = lookupDudgeBean();
		SessionObject so = SessionObject.extract(request.getSession());
		
		if(so.getUsername() == null)
			return mapping.findForward("loginRequired");

		int contestId;
		// Получаем идентификатор соревнования.
		String param = request.getParameter("contestId");
		if(param != null)
			contestId = Integer.parseInt(param);
		else
			// Если нам не послали идентификатор, то используем идентификатор
			// текущего соревнования.
			contestId = dudgeBean.getDefaultContest().getContestId();
		
		User user = dudgeBean.getUser(so.getUsername());
		Contest contest = dudgeBean.getContest(contestId);
		Language language = dudgeBean.getLanguage(sf.getLanguageId());
		Problem problem = dudgeBean.getProblem( Integer.parseInt(sf.getProblemId()) );
			
		// Проверяем право пользователя.
		PermissionCheckerRemote pcb = so.getPermissionChecker();
		if (!pcb.canSubmitSolution(
				so.getUsername(),
				contest.getContestId(),
				problem.getProblemId())
				) {
			return mapping.findForward("accessDenied");
		}
	
		Solution solution = new Solution();
		solution.setUser(user);
		solution.setContest(contest);
		solution.setLanguage(language);
		solution.setProblem(problem);
		solution.setSourceCode(sf.getSourceCode());
		
		solution = dudgeBean.submitSolution(solution);
		sf.setSolutionId(Integer.toString(solution.getSolutionId()));
		
		return mapping.findForward("submitSuccess");
	}

	/**
	 * Метод для получения через AJAX списка из последних N отправленных
	 * в систему решений, где n задачется параметром limit.
	 */
	public void getLastSolutions(
			ActionMapping mapping,
			ActionForm af,
			HttpServletRequest request,
			HttpServletResponse response) {
		//  Получаем из запроса, какие данные требуются клиенту.
		int limit = Integer.parseInt( (String) request.getParameter("limit"));
		if(limit > 20) limit = 20;
		
		List<Solution> solutions = lookupDudgeBean().getLastSolutions(limit);
		
		JSONArray ja = new JSONArray();
		JSONObject jo = new JSONObject();
		
		try {
			jo.put("totalCount", solutions.size());
		} catch (JSONException ex) {
			ex.printStackTrace();
			return;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy.MM.dd");
		for (Solution sol: solutions) {
			JSONObject json = new JSONObject();

			// Заполняем данными пользователя созданный объект JSON.
			try {
				json.put("solutionId" , sol.getSolutionId());
				json.put("submitTime" , sdf.format(sol.getSubmitTime()));
				json.put("user" , sol.getUser().getLogin());
				json.put("contestId" , sol.getContest().getContestId());
				json.put("problemId" , sol.getProblem().getProblemId());
				json.put("languageId" , sol.getLanguage().getLanguageId());
				if(sol.getStatus() != SolutionStatus.PROCESSED
						|| sol.getLastRunResult() == null) {
					json.put("status" , sol.getStatus().toString());
				} else {
					json.put("status" , sol.getLastRunResult().toString());
				}
			}
			catch (JSONException je ) {
				logger.log(Level.SEVERE, "Creating JSON view of Solution object failed.", je);
				return;
			}
			
			ja.put(json);
		}
		try {
			jo.put("solutions", ja);
		} catch (JSONException ex) {
			logger.log(Level.SEVERE, "Exception occured.", ex);
			return;
		}
		
		// Устанавливаем тип контента
		response.setContentType("application/x-json");
		try {
			response.getWriter().print(jo);
		} catch (IOException ex) {
			ex.printStackTrace();
			logger.log(Level.SEVERE, "Exception occured.", ex);
			return;
		}		
	}				
}