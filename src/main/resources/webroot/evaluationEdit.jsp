<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.ConfigIO" %>
<%@ page import="org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleEntry" %>
<%@ page import="org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.EvaluationModules" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.EvaluationIO" %>
<%@ page import="org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleDescriptions" %>
<%@ page import="java.util.ArrayList" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />

<h1>Edit Evaluation Files</h1>
<%
final String                submit    = request.getParameter(ControllerUtils.INPUT_SUBMIT);
final ArrayList<String>     jsCollector;
      EvaluationModules[]   modules   = null;
      ModuleDescriptions    descs     = null;
      String[]              relPaths  = null;
      int                   choice    = 2;
      String                prefix;
      
if(submit != null) {  
  if(submit.equalsIgnoreCase(ControllerUtils.BUTTON_OK)) {
    choice = 0;  
    relPaths = request.getParameterValues(ControllerUtils.PARAMETER_SELECTION);
  } else {
    if(submit.equalsIgnoreCase(ControllerUtils.COMMAND_NEW_FILE)) {
      choice = 1;  
      relPaths = new String[] { request.getParameter(ControllerUtils.PARAMETER_CD_PATH) };
    }
  }

  if((relPaths!=null) && (relPaths.length > 0)) { %>
  <p>On this page, you can edit evaluation files. An evaluation file tells the
  evaluator what to do with the data it has loaded. 
  You can save your changes by pressing the &quot;Save&quot; button.
  If you leave this page any un-saved changes will be discarded.</p> 
<% }
  try(final Handle handle = controller.createJspHandle(pageContext)) {  
    switch(choice) {
      case 0: {
        modules = EvaluationIO.load(null, relPaths, handle);
        break; }
      case 1: {
        modules = EvaluationIO.load(request.getParameter(ControllerUtils.INPUT_CURRENT_DIR),
                              relPaths, handle);
        break; }
      default: {
        handle.unknownSubmit(submit);
      }
    }    
    descs = EvaluationIO.getDescriptions(handle);
  }
    
  if((relPaths!=null) && (relPaths.length > 0) && (modules != null)) {
    jsCollector = new ArrayList<>();
    for(int i = 0; i < relPaths.length; i++) {
      if( (relPaths[i] != null) && (modules[i] != null) ) {
        final String relPath = Encoder.htmlEncode(relPaths[i]); 
%>
<h2>File &quot;<%= relPath %>&quot;</h2>
<form class="invisible" action="/evaluationEditSave.jsp" method="post" target="_blank">
<input type="hidden" name="<%= ControllerUtils.PARAMETER_SELECTION%>" value="<%= relPath%>" />
<% prefix = String.valueOf(i); %>
<input type="hidden" name="<%= EvaluationIO.PARAMETER_EVALUATION_PREFIX%>" value="<%= prefix%>" />
<% EvaluationIO.putFormFields(prefix, descs, modules[i], pageContext.getOut(), jsCollector); %>
<p class="controllerActions">
<input type="submit" name="<%= ControllerUtils.INPUT_SUBMIT%>" value="save">
<input type="submit" name="<%= ControllerUtils.INPUT_SUBMIT%>" value="download" formtarget="_blank" formmethod="post" formaction="/download">
</p>
</form>
<% } } ConfigIO.putJavaScript(pageContext.getOut(), jsCollector); } } %>
<%@include file="/includes/defaultFooter.jsp" %>