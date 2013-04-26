/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dudge.opaque;

import dudge.DudgeLocal;
import dudge.db.Contest;
import dudge.db.Language;
import dudge.db.Problem;
import dudge.db.Solution;
import dudge.db.SolutionStatus;
import dudge.db.User;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author duke
 * TODO: всь логику приложения унести в opaqueBean
 */
@WebService(serviceName = "OpaqueQuestionEngine", targetNamespace = "http://opaque.dudge/")
@SOAPBinding(style = SOAPBinding.Style.RPC, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)

public class OpaqueQuestionEngine {
    
    private Logger logger = Logger.getLogger(this.getClass().toString());
    
    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }
    
    /**
     *.
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "getEngineInfo")
    @WebResult(name = "EngineInfoReturn", targetNamespace = "http://opaque.dudge/", partName = "getEngineInfoReturn")
    public String getEngineInfo() {
        
        OpaqueBeanLocal opaqueBean=lookupOpaqueBean();

        return "<engineinfo>"+
                     "<name>DudgeQuestionEngine</name>"+
                     "<usedmemory>0 bytes</usedmemory>"+
                     "<activesessions>"+opaqueBean.getSessionsCount()+"</activesessions>"+
               "</engineinfo>";
    }

    /**
     *.
     * @param questionVersion
     * @param questionBaseURL
     * @param questionID
     * @return
     *     returns java.lang.String
     * @throws OmException
     */
    @WebMethod(operationName = "getQuestionMetadata")
    @WebResult(name = "QuestionMetadataReturn", targetNamespace = "http://opaque.dudge/", partName = "getQuestionMetadata")
    public String getQuestionMetadata(
        @WebParam(name = "questionID", targetNamespace = "http://opaque.dudge/", partName = "questionID")
        String questionID,
        @WebParam(name = "questionVersion", targetNamespace = "http://opaque.dudge/", partName = "questionVersion")
        String questionVersion,
        @WebParam(name = "questionBaseURL", targetNamespace = "http://opaque.dudge/", partName = "questionBaseURL")
        String questionBaseURL)
        throws OmException
    {
        // FIXME: число баллов marks должно совпадать с баллами положительной оценки в process()
        return "<questionmetadata>"+
                  "<scoring><marks>1</marks></scoring>"+
                  "<plainmode>yes</plainmode>"+
               "</questionmetadata>";
    }

     /**
     * 
     * @param questionSession
     * @throws OmException
     */
    @WebMethod(operationName = "stop")
    public void stop(
        @WebParam(name = "questionSession", targetNamespace = "http://opaque.dudge/", partName = "questionSession")
        String questionSession)
        throws OmException
    {
        logger.info("Enter stop() questionSession="+questionSession);
        
        OpaqueBeanLocal opaqueBean=lookupOpaqueBean();
        opaqueBean.dropSession(questionSession);
    }

    /**
     * 
     * @param initialParamNames
     * @param questionVersion
     * @param questionBaseURL
     * @param questionID
     * @param initialParamValues
     * @param cachedResources
     * @return
     *     returns dudge.opaque.StartReturn
     * @throws OmException
     */
    @WebMethod(operationName = "start")
    @WebResult(name = "StartReturn", targetNamespace = "http://opaque.dudge/", partName = "startReturn")
    public StartReturn start(
        @WebParam(name = "questionID", targetNamespace = "http://opaque.dudge/", partName = "questionID")
        String questionID,
        @WebParam(name = "questionVersion", targetNamespace = "http://opaque.dudge/", partName = "questionVersion")
        String questionVersion,
        @WebParam(name = "questionBaseURL", targetNamespace = "http://opaque.dudge/", partName = "questionBaseURL")
        String questionBaseURL,
        @WebParam(name = "initialParamNames", targetNamespace = "http://opaque.dudge/", partName = "initialParamNames")
        ArrayOfStrings initialParamNames,
        @WebParam(name = "initialParamValues", targetNamespace = "http://opaque.dudge/", partName = "initialParamValues")
        ArrayOfStrings initialParamValues,
        @WebParam(name = "cachedResources", targetNamespace = "http://opaque.dudge/", partName = "cachedResources")
        ArrayOfStrings cachedResources)
        throws OmException
    {
        StartReturn val = new StartReturn();   

        logger.info("Enter start()");
        Map<String,String> param=params2map(initialParamNames,initialParamValues);
        
        int problemid=question2ProblemId(questionID,questionVersion); 
        
        DudgeLocal dudgeBean=lookupDudgeBean();
        Problem problem = dudgeBean.getProblem(problemid);
        
        OpaqueBeanLocal opaqueBean=lookupOpaqueBean();
        
        String seed=param.get("randomseed");
        String sessionid=opaqueBean.makeQuestionSession(seed);
        logger.info("sessionid="+sessionid+" problemid="+problemid);
        OpaqueSession S=new OpaqueSession(problemid);
        
        String pval;
        pval=param.get("preferredbehaviour");
        if(pval!=null) S.setBehaviour(pval);

        pval=param.get("language"); // язык надписей
        if(pval!=null) S.setLocale(pval);

        pval=param.get("display_readonly");
        if(pval!=null) S.setDisplayReadOnly("1".equals(pval));
        
        pval=param.get("display_correctness");
        if(pval!=null) S.setDisplayCorrectness("1".equals(pval));

        pval=param.get("display_feedback");
        if(pval!=null) S.setDisplayFeedback("1".equals(pval));
        
        pval=param.get("display_generalfeedback");
        if(pval!=null) S.setDisplayRemark("1".equals(pval));

        opaqueBean.updateSession(sessionid,S);
        
        String langid=param.get("prglang");
        String src=param.get("result");
        if(src==null) src="";

        val.setProgressInfo("Question loaded"); // FIXME: возможно здесь должно быть более внятное сообщение
        val.setQuestionSession(sessionid);        

        String resultHtml=makeXHTML(dudgeBean,problem,S,src,langid,S.isDisplayReadOnly(),sessionid);
        val.setXHTML(resultHtml);
      
        logger.info("progressInfo: "+val.getProgressInfo());
        return val;
    }

    /**
     *
     * @param values
     * @param names
     * @param questionSession
     * @return
     *     returns dudge.opaque.ProcessReturn
     * @throws OmException
     */
    @WebMethod(operationName = "process")
    @WebResult(name = "ProcessReturnElement", targetNamespace = "http://opaque.dudge/", partName = "processReturn")
    public ProcessReturn process(
        @WebParam(name = "questionSession", targetNamespace = "http://opaque.dudge/", partName = "questionSession")
        String questionSession,
        @WebParam(name = "names", targetNamespace = "http://opaque.dudge/", partName = "names")
        ArrayOfStrings names,
        @WebParam(name = "values", targetNamespace = "http://opaque.dudge/", partName = "values")
        ArrayOfStrings values)
        throws OmException
    {
        logger.info("Enter process() questionSession="+questionSession);
        Map<String,String> param=params2map(names,values);
        
        ProcessReturn val = new ProcessReturn();
        
        int contestId=2; // Global contest TODO: брать из настроек
        String username="admin"; // TODO: брать из настроек
        
        String langid=param.get("prglang");
        String originalsessionid=param.get("originalsession");
        
        String src=param.get("result");
        if(src==null) src="";

        Boolean isFinish="1".equals(param.get("-finish")); // 1 - goto finish state
        logger.info("isFinish="+isFinish+" -finish='"+param.get("-finish")+"'");
        Boolean isIntermediaStep=false;


        OpaqueBeanLocal opaqueBean=lookupOpaqueBean();
        OpaqueSession session=opaqueBean.getSession(questionSession);
        if(session==null) {
            logger.warning("Unknown questionSession detected, aborted");
            // FIXME: нужно заполнить ответ подобающим образом
            return val;
        }
        
        session.nextStep();
        
        Boolean isReadOnly=(isFinish || session.isDisplayReadOnly());
        
        int problemid=session.getProblemId();
        logger.info("problemid="+problemid);
        
        DudgeLocal dudgeBean = lookupDudgeBean();              

        // FIXME: финишный запрос не содержит никаких пользовательских параметров 
        // и по нему нельзя определить - slave mode у нас или нет
        // но на этот момент нужная информация уже точно есть в сессии
        if(isFinish)
            originalsessionid=session.getOriginalSession();
        
        // TODO: если originalsessionid не совпадает с sessionid то это означает,
        // что идет повтор запросов (slave mode) и нужно возвращать ровно те ответы,
        // которые были возвращены при первичных запросах
        // и следовательно не требуется запускать проверку решения т.к. она будет повторной
        if(originalsessionid==null) 
            originalsessionid=questionSession;
        
        boolean isSlaveMode=!questionSession.equals(originalsessionid);
        if(isSlaveMode)
            logger.info("Slave mode detected: cur="+questionSession+" orig="+originalsessionid);

        if(isSlaveMode && !isFinish) {
            int originalsolutionid;
            OpaqueSession originalsession2=opaqueBean.getSession(originalsessionid);
            
            if(originalsession2==null) {
                OpaqueOriginalSession originalsession=opaqueBean.getOriginalSession(originalsessionid);
                if(originalsession==null) {
                    logger.warning("Undefined original session in slave mode, aborted");
                    // FIXME: нужно заполнить ответ подобающим образом
                    return val;                
                }
                originalsolutionid=originalsession.getSolutionId();
                isIntermediaStep=session.getSteps()<originalsession.getSteps();
            }
            else {
                originalsolutionid=originalsession2.getSolutionId();
                isIntermediaStep=session.getSteps()<originalsession2.getSteps();                
            }

            if(originalsolutionid==-1) {
                    logger.warning("Undefined original solutionid in slave mode, aborted");
                    // FIXME: нужно заполнить ответ подобающим образом
                    return val;
            }            
            session.setSolutionId(originalsolutionid);
            if(isIntermediaStep) {
                logger.info("Intermedia step in slave mode");
                val.setProgressInfo("Answer saved"); // специальное значение
            }
        }

        logger.info("isSolution="+session.isSolution());
        Boolean needNewSolution;
        if(session.isSolution()) {

            Solution solution=dudgeBean.getSolution(session.getSolutionId());
            if(!isIntermediaStep)
                 needNewSolution=checkSolutionStatus(dudgeBean,solution,src,val);
            else needNewSolution=false;

            if(!needNewSolution && src.isEmpty()) { // restore source code of solution
                src=solution.getSourceCode();
                langid=solution.getLanguage().getLanguageId();
                } 
        }
        else {
            if(isSlaveMode) {
                // в этом режиме уже не нужно запускать решение на проверку, 
                // просто имитируем аналогичный ответ
                logger.info("New solution emulation (slave mode)");
                val.setProgressInfo("Answer saved"); // специальное значение
                needNewSolution=false;
            }
            else needNewSolution=true;
        }
        
        Problem problem = dudgeBean.getProblem(problemid);
        
        if (needNewSolution) {
            if (!src.isEmpty()) {
                Contest contest = dudgeBean.getContest(contestId);
                User user = dudgeBean.getUser(username);
                Language language = dudgeBean.getLanguage(langid);

                Solution solution = new Solution();
                solution.setUser(user);
                solution.setContest(contest);
                solution.setLanguage(language);
                solution.setProblem(problem);
                solution.setSourceCode(src);

                solution = dudgeBean.submitSolution(solution); // FIXME:
                session.setSolutionId(solution.getSolutionId());
                opaqueBean.updateSession(questionSession, session);
                val.setProgressInfo("Answer saved"); // специальное значение
                logger.info("New solution");
            } else {
                val.setProgressInfo("Empty answer, none todo.");
                logger.info("Skip solution creation - empty answer received");
            }
        }

        if(!isSlaveMode && isFinish) 
            opaqueBean.saveAsOriginalSession(questionSession);

        String resultHtml=makeXHTML(dudgeBean,problem,session,src,langid,isReadOnly,originalsessionid);
        val.setXHTML(resultHtml);
        
        logger.info("progressInfo: "+val.getProgressInfo());
        return val;
    }

    
    private String makeXHTML(DudgeLocal dudgeBean,
                Problem problem,
                OpaqueSession session,
                String src,
                String langid,
                boolean isReadOnly,
                String originalsessionid) {
        
        List<Language> llist=dudgeBean.getLanguages();
        String langHtml="";
        String def;
        for(Language l : llist) {
            def = (l.getLanguageId().equals(langid)) ? "selected":"";
            //logger.info("default="+langid+" curr="+l.getLanguageId()+" res="+def);
            langHtml+="<option "+def+" value='"+l.getLanguageId()+"'>"+
                    l.getName()+"</option>";
        }

        String title=problem.getTitle();
        String desc=problem.getDescription();
       	//problem.getCpuTimeLimit();
	//problem.getRealTimeLimit();
	//problem.getMemoryLimit();
	//problem.getOutputLimit();
		
	//problem.getOwner().getLogin();
        //problem.getAuthor();
	//problem.getCreateTime();

        String roHtml;
        
        if(isReadOnly) roHtml="readonly='readonly'";
        else roHtml="";
        
        src=stringToHTMLString(src);
        String resultHtml;
        resultHtml="<div class='qtext'>"+
                "<h1>"+title+"</h1>"+desc+"</div>"+
                "<div class='ablock'><div class='answer'>"+
                "<br/><select "+roHtml+" name='%%IDPREFIX%%prglang'>"+langHtml+"</select>"+
                "<br/><textarea class='qtype_opaque_monospaced qtype_opaque_response' "+roHtml+
                        " rows='30' cols='60' name='%%IDPREFIX%%result'>"+
                        src+"</textarea>";
        if(session.getSolutionId()!=-1)
                resultHtml+="<input type='hidden' name='%%IDPREFIX%%solutionid' value='"+session.getSolutionId()+"' />";
        if(originalsessionid!=null)
                resultHtml+="<input type='hidden' name='%%IDPREFIX%%originalsession' value='"+originalsessionid+"' />";
        if(!isReadOnly && session.isInteractiveBehaviour()) {
            // FIXME: в зависимости от ожидаемого поведения можно показывать разный набор кнопок
            // например:
            // enteranswer - узнать результаты проверки ранее отправленного решения
            // tryagain - отправить новое решение на проверку, если старое уже оценено (как неверное)
            // clear - очистить поле для вставки нового решения методом copy-paste
            resultHtml+="\n<br/>"+
                //"<input type='submit' name='%%IDPREFIX%%omact_tryagain' value='%%lTRYAGAIN%%'/>"+
                "<input type='submit' name='%%IDPREFIX%%omact_enteranswer' value='%%lENTERANSWER%%'/>"+
                //"<input type='button' onClick='' name='%%IDPREFIX%%omact_clear' value='%%lCLEAR%%'/>"+
                "";            
        }
        resultHtml+="</div></div>"; // end of answer&ablock
                
        return resultHtml;
    }
    
    private boolean checkSolutionStatus(DudgeLocal dudgeBean,
                                Solution solution,
                                String src,
                                ProcessReturn val) {
            String oldSrc=solution.getSourceCode();
            //String oldLangid=solution.getLanguage().getLanguageId();
            boolean needNewSolution;

            logger.info("Old source code: "+oldSrc);
            logger.info("New source code: "+src);
            
            needNewSolution = !src.equals(oldSrc) && !src.isEmpty();
            logger.info("Source code is changed ? "+needNewSolution);
            
            if(!needNewSolution) {
                String status;
                                
                if(solution.getStatus() != SolutionStatus.PROCESSED
				|| solution.getContest().getTraits().isRunAllTests()
				|| solution.getLastRunResult() == null) {
                    status=solution.getStatus().toString();
		} else { 
                    status = solution.getLastRunResult().toString();
                }

                // итоговое состояние
                if(solution.getStatus() == SolutionStatus.PROCESSED ||
                   solution.getStatus() == SolutionStatus.DISQUALIFIED ||
                   solution.getStatus() == SolutionStatus.COMPILATION_ERROR) {
                    
                    Results res= new Results();
                    Score score=new Score();
                    val.setQuestionEnd(true);
                    if(status.equals("SUCCESS"))
                              score.setMarks(1); // FIXME: максимальный балл должен совпадать с getQuestionMetadata
                    else  score.setMarks(0); 
                    res.getScores().add(score);
                    val.setProgressInfo("Answer graded"); // must be
                    res.setActionSummary(status+" "+solution.getStatusMessage());

                    val.setResults(res); // результаты должны быть в ответе только в итоговом ответе
                    logger.info("Solution "+solution.getSolutionId()+" finished, status= "+status);
                } else { // Solution в процессе обработки
                    // пока нет оценки, отображается только progressInfo
                    //res.setActionSummary(solution.getStatusMessage());
                    //status+"<br/>"+solution.getStatusMessage()
                    val.setProgressInfo("Answer saved"); // специальное значение
                    logger.info("Solution "+solution.getSolutionId()+" in progress, status="+status);
                }
            }
            
        return needNewSolution;
    }

    
    private int question2ProblemId(String questionId, String questionVersion) {
        int pid;
        // questionId должно быть в формате idNNN
        Pattern p = Pattern.compile("id(\\d+)");
        Matcher m = p.matcher(questionId);
        if(m.matches()) {
            pid=Integer.parseInt(m.group(1));
        }
        else {
            NamingException ne= new NamingException("Invalid question ID");
            throw new RuntimeException(ne);
        }
        logger.info("question2ProblemId() "+questionId+"/"+questionVersion+" --> "+pid);
        
        return pid; // TODO: брать из questionID+questionVersion
    }
    
    private Map<String,String> params2map(ArrayOfStrings names, ArrayOfStrings values) {
        Map<String,String> ret=new HashMap<String,String>();
        
        List<String> n=names.getItem();
        List<String> v=values.getItem();
        int k;
        
        for(k=0;k<n.size();k++) {
            ret.put(n.get(k),v.get(k));
            logger.info("param["+n.get(k)+"]='"+v.get(k)+"'");
        }
        
        return ret;
    }
    
    private static String stringToHTMLString(String string) {
    StringBuffer sb = new StringBuffer(string.length());
    // true if last char was blank
    boolean lastWasBlankChar = false;
    int len = string.length();
    char c;

    for (int i = 0; i < len; i++)
        {
        c = string.charAt(i);
        if (c == ' ') {
            // blank gets extra work,
            // this solves the problem you get if you replace all
            // blanks with &nbsp;, if you do that you loss 
            // word breaking
            if (lastWasBlankChar) {
                lastWasBlankChar = false;
                sb.append("&nbsp;");
                }
            else {
                lastWasBlankChar = true;
                sb.append(' ');
                }
            }
        else {
            lastWasBlankChar = false;
            //
            // HTML Special Chars
            if (c == '"')      sb.append("&quot;");
            else if (c == '&') sb.append("&amp;");
            else if (c == '<') sb.append("&lt;");
            else if (c == '>') sb.append("&gt;");
            //else if (c == '\n')sb.append("&lt;br/&gt;");
            else {
                int ci = 0xffff & c;
                if (ci < 160 )
                    // nothing special only 7 Bit
                    sb.append(c);
                else {
                    // Not 7 Bit use the unicode system
                    sb.append("&#");
                    sb.append(new Integer(ci).toString());
                    sb.append(';');
                    }
                }
            }
        }
    return sb.toString();
}

    private OpaqueBeanLocal lookupOpaqueBean() {
		try {
			Context c = new InitialContext();
			return (OpaqueBeanLocal) c.lookup("java:global/dudge/dudge-ejb/OpaqueBean!dudge.opaque.OpaqueBeanLocal");
		}
		catch(NamingException ne) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,"exception caught" ,ne);
			throw new RuntimeException(ne);
		}
	}

    
    private DudgeLocal lookupDudgeBean() {
		try {
			Context c = new InitialContext();
			return (DudgeLocal) c.lookup("java:global/dudge/dudge-ejb/DudgeBean!dudge.DudgeLocal");
			//return (DudgeLocal) c.lookup("java:comp/env/ejb/DudgeBean");
		}
		catch(NamingException ne) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,"exception caught" ,ne);
			throw new RuntimeException(ne);
		}
	}
    
}