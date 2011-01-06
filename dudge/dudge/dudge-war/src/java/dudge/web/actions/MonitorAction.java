/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dudge.web.actions;

import dudge.DudgeLocal;
import dudge.db.Contest;
import dudge.db.ContestProblem;
import dudge.db.RoleType;
import dudge.logic.AcmTraits;
import dudge.logic.ContestTraits;
import dudge.logic.GlobalTraits;
import dudge.monitor.AcmMonitorRecord;
import dudge.monitor.GlobalMonitorRecord;
import dudge.PermissionCheckerRemote;
import dudge.monitor.SchoolMonitorRecord;
import dudge.web.SessionObject;
import java.io.IOException;
import java.util.Date;
import java.util.List;
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
 *
 * @author virl
 */
public class MonitorAction extends DispatchAction {

	protected static Logger logger = Logger.getLogger(ContestsAction.class.toString());

	public MonitorAction() {
	}

	private DudgeLocal lookupDudgeBean() {
		try {
			Context c = new InitialContext();
			return (DudgeLocal) c.lookup("java:comp/env/ejb/DudgeBean");
		} catch(NamingException ne) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,"exception caught" ,ne);
			throw new RuntimeException(ne);
		}
	}

	public ActionForward showStatus(
			ActionMapping mapping,
			ActionForm af,
			HttpServletRequest request,
			HttpServletResponse response) {
			
			return mapping.findForward("status");
	}
	
	public ActionForward view(
			ActionMapping mapping,
			ActionForm af,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		DudgeLocal dudgeBean = lookupDudgeBean();
		SessionObject so = SessionObject.extract(request.getSession());
		
		int contestId;
		// Получаем идентификатор соревнования.
		String param = request.getParameter("contestId");
		if(param != null)
			contestId = Integer.parseInt(param);
		else
			// Если нам не послали идентификатор, то используем идентификатор
			// текущего соревнования.
			contestId = dudgeBean.getDefaultContest().getContestId();
		
		// Проверяем право пользователя.
		PermissionCheckerRemote pcb = so.getPermissionChecker();
		if (!pcb.canViewMonitor(
				so.getUsername(),
				contestId)
				) {
			return mapping.findForward("accessDenied");
		}
		
		// Получаем свойства соревнования.
		ContestTraits traits = dudgeBean.getContest(contestId).getTraits();
		
		//Редирект на страницу монитора.
		return mapping.findForward("monitor" + traits.getMonitorSuffix());
	}
	
	/**
	 * Метод для получения через AJAX данных монитора глобального соревнования.
	 * Возвращает в response нужные данные.
	 * Само соревнование задается через параметр contestId.
	 */
	public void getGlobalMonitorData(
			ActionMapping mapping,
			ActionForm af,
			HttpServletRequest request,
			HttpServletResponse response) {
		//  Получаем идентификатор соревнования.
		int contestId = Integer.parseInt( request.getParameter("contestId"));
		
		SessionObject so = SessionObject.extract(request.getSession());
		
		// Проверяем право пользователя.
		PermissionCheckerRemote pcb = so.getPermissionChecker();
		if (!pcb.canViewMonitor(
				so.getUsername(),
				contestId)
				) {
			return;
		}
		
		DudgeLocal dudgeBean = lookupDudgeBean();
		Contest contest = dudgeBean.getContest(contestId);
		GlobalTraits traits = (GlobalTraits) contest.getTraits();
		
		JSONArray jaRows = new JSONArray();
		
		// Администраторы соревнования видят монитор размороженным.
		boolean userIsContestAdmin = dudgeBean.isInRole(
				so.getUsername(),
				contest.getContestId(),
				RoleType.ADMINISTRATOR
				);
		
		List<GlobalMonitorRecord> monitorRows = dudgeBean.getGlobalMonitorRecords(
				contest,
				(userIsContestAdmin || contest.isInfinite()) ? 
					null :
					new Date(contest.getEndTime().getTime() - contest.getFreezeTime() * 1000)
				);
		
		for(GlobalMonitorRecord row : monitorRows) {
			JSONObject joRow = new JSONObject();
			
			try {
				joRow.put("user", row.getUser());
				joRow.put("place", row.getPlace());
				
				joRow.put("solvedProblems", row.getSolvedProblemsCount());
				joRow.put("rating", row.getRating());
			} catch (JSONException je ) {
				logger.log(Level.SEVERE, "Creating JSON view of Solution object failed.", je);
				return;
			}
			
			jaRows.put(joRow);
		} // for row
		
		JSONObject joRoot = new JSONObject();
		try {
			joRoot.put("totalCount", monitorRows.size());
		} catch (JSONException ex) {
			ex.printStackTrace();
			return;
		}
		
		try {
			joRoot.put("rows", jaRows);
		} catch (JSONException ex) {
			logger.log(Level.SEVERE, "Exception occured.", ex);
			return;
		}
		
		// Устанавливаем тип контента
		response.setContentType("application/x-json");
		try {
			response.getWriter().print(joRoot);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Exception occured.", ex);
			return;
		}
	}
	
	/**
	 * Метод для получения через AJAX данных монитора соревнования ACM.
	 * Возвращает в response нужные данные.
	 * Само соревнование задается через параметр contestId.
	 */
	public void getAcmMonitorData(
			ActionMapping mapping,
			ActionForm af,
			HttpServletRequest request,
			HttpServletResponse response) {
		//  Получаем идентификатор соревнования.
		int contestId = Integer.parseInt( request.getParameter("contestId"));
		
		SessionObject so = SessionObject.extract(request.getSession());
		
		// Проверяем право пользователя.
		PermissionCheckerRemote pcb = so.getPermissionChecker();
		if (!pcb.canViewMonitor(
				so.getUsername(),
				contestId)
				) {
			return;
		}
		
		DudgeLocal dudgeBean = lookupDudgeBean();
		Contest contest = dudgeBean.getContest(contestId);
		AcmTraits traits = (AcmTraits) contest.getTraits();
		
		JSONArray jaRows = new JSONArray();
		
		// Администраторы соревнования видят монитор размороженным.
		boolean userIsContestAdmin = dudgeBean.isInRole(
				so.getUsername(),
				contest.getContestId(),
				RoleType.ADMINISTRATOR
				);
		
		List<AcmMonitorRecord> monitorRows = dudgeBean.getAcmMonitorRecords(
				contest,
				(userIsContestAdmin || contest.isInfinite()) ? 
					null :
					new Date(contest.getEndTime().getTime() - contest.getFreezeTime() * 1000)
				);
		
		for(AcmMonitorRecord row : monitorRows) {
			JSONObject joRow = new JSONObject();
			
			try {
				joRow.put("user", row.getUser());
				joRow.put("place", row.getPlace());
				
				for(ContestProblem contestProblem : contest.getContestProblems()) {
					// Данные ячейки в мониторе.
					String cellData = "";
					
					if(row.isSolved(contestProblem.getProblemMark())) {
						// Выводим в ячейку "плюс";
						cellData += "+";
					} else {
						cellData += "-";
					}
					
					// Выводим количество неуспешных попыток,
					// если таковые были.
					int unsucAttempts = row.getProblemTriesCount(contestProblem.getProblemMark());
					if(unsucAttempts != 0) {
						cellData += Integer.toString(unsucAttempts);
					}
					joRow.put("problem" + contestProblem.getProblemMark(), cellData);
				} // for contestProblem
				
				joRow.put("solvedProblems", row.getSolvedProblemsCount());
				// Выводим время в минутах.
				joRow.put("time", row.getTime() / (60*1000) );
			} catch (JSONException je ) {
				logger.log(Level.SEVERE, "Creating JSON view of Solution object failed.", je);
				return;
			}
			
			jaRows.put(joRow);
		} // for row
		
		JSONObject joRoot = new JSONObject();
		try {
			joRoot.put("totalCount", monitorRows.size());
		} catch (JSONException ex) {
			ex.printStackTrace();
			return;
		}
		
		try {
			joRoot.put("rows", jaRows);
		} catch (JSONException ex) {
			logger.log(Level.SEVERE, "Exception occured.", ex);
			return;
		}
		
		// Устанавливаем тип контента
		response.setContentType("application/x-json");
		try {
			response.getWriter().print(joRoot);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Exception occured.", ex);
			return;
		}
	}

        /**
	 * Метод для получения через AJAX данных монитора школьного
         * соревнования. Возвращает в response нужные данные.
	 * Само соревнование задается через параметр contestId.
	 */
	public void getSchoolMonitorData(
			ActionMapping mapping,
			ActionForm af,
			HttpServletRequest request,
			HttpServletResponse response) {
		//  Получаем идентификатор соревнования.
		int contestId = Integer.parseInt( request.getParameter("contestId"));

		SessionObject so = SessionObject.extract(request.getSession());

		// Проверяем право пользователя.
		PermissionCheckerRemote pcb = so.getPermissionChecker();
		if (!pcb.canViewMonitor(
				so.getUsername(),
				contestId)
				) {
			return;
		}
                
		DudgeLocal dudgeBean = lookupDudgeBean();
		Contest contest = dudgeBean.getContest(contestId);

		JSONArray jaRows = new JSONArray();

		// Администраторы соревнования видят монитор размороженным.
		boolean userIsContestAdmin = dudgeBean.isInRole(
				so.getUsername(),
				contest.getContestId(),
				RoleType.ADMINISTRATOR
				);

		List<SchoolMonitorRecord> monitorRows = dudgeBean.getSchoolMonitorRecords(
				contest,
				(userIsContestAdmin || contest.isInfinite()) ?
					null :
					new Date(contest.getEndTime().getTime() - contest.getFreezeTime() * 1000)
				);

		for(SchoolMonitorRecord row : monitorRows) {
			JSONObject joRow = new JSONObject();

			try {
				joRow.put("user", row.getUser());
				joRow.put("place", row.getPlace());

				for(ContestProblem contestProblem : contest.getContestProblems()) {
					// Данные ячейки в мониторе.
					String cellData = "";

					if(row.isSolved(contestProblem.getProblemMark())) {
						// Выводим в ячейку "плюс";
						cellData += "+";
					} else {
						cellData += "-";
					}

					// Выводим количество неуспешных попыток,
					// если таковые были.
					int unsucAttempts = row.getProblemTriesCount(contestProblem.getProblemMark());
					if(unsucAttempts != 0) {
						cellData += Integer.toString(unsucAttempts);
					}
					joRow.put("problem" + contestProblem.getProblemMark(), cellData);
				} // for contestProblem

				joRow.put("solvedProblems", row.getSolvedProblemsCount());
				// Выводим время в минутах.
				joRow.put("time", row.getTime() / (60*1000) );
			} catch (JSONException je ) {
				logger.log(Level.SEVERE, "Creating JSON view of Solution object failed.", je);
				return;
			}

			jaRows.put(joRow);
		} // for row

		JSONObject joRoot = new JSONObject();
		try {
			joRoot.put("totalCount", monitorRows.size());
		} catch (JSONException ex) {
			ex.printStackTrace();
			return;
		}

		try {
			joRoot.put("rows", jaRows);
		} catch (JSONException ex) {
			logger.log(Level.SEVERE, "Exception occured.", ex);
			return;
		}

		// Устанавливаем тип контента
		response.setContentType("application/x-json");
		try {
			response.getWriter().print(joRoot);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Exception occured.", ex);
			return;
		}
	}
}