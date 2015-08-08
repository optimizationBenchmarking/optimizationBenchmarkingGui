<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.utils.text.TextUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Evaluation" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<%@ page import="java.util.logging.Level" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />
<h1>Evaluation</h1>
<% final String[] select = request.getParameterValues(ControllerUtils.PARAMETER_SELECTION); %>
<p>
We now start <% if((select!=null)&&(select.length>1)) {%><%= select.length%><%} else {%>the<%}%> evaluation process<% if((select!=null)&&(select.length>1)) {%>es<%}%>.
If started successfully, an evaluation process may need quite some time to complete.
Please do not refresh or close this web page, otherwise the process may be terminated
prematurely or started again in parallel. In other words, leave this page open.
</p>
<p>Depending on the <a href="/logLevel.jsp" target="_blank">log level</a> you have set,
you may receive either much information or only notifications about success or failure.
If the <a href="/logLevel.jsp" target="_blank">log level</a> is higher than <code>INFO</code>,
you may not receive information for quite some time.
Take it easy, relax, and let the program do its job.</p>
<%
 final String  submit = TextUtils.prepare(request.getParameter(ControllerUtils.INPUT_SUBMIT));  
 try(final Handle handle = controller.createJspHandle(pageContext)) {
    if(ControllerUtils.BUTTON_OK.equalsIgnoreCase(submit)) {
      final String withSelected = request.getParameter(ControllerUtils.PARAMETER_WITH_SELECTED); 
      if(ControllerUtils.COMMAND_EXECUTE_EVALUATOR.equalsIgnoreCase(withSelected)) {
        Evaluation.evaluate(select, handle);
      } else {
        handle.failure("Unknown selection command: " + withSelected);
      }      
    } else {
      handle.unknownSubmit(submit);
    }
  }
%>

<p>
You can (and probably should) now return to the <a href="/controller.jsp">control center</a>.
</p>

<%@include file="/includes/defaultFooter.jsp" %>