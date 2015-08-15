<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.ConfigIO" %>
<%@ page import="org.optimizationBenchmarking.utils.config.ConfigurationBuilder" %>
<%@ page import="org.optimizationBenchmarking.utils.config.ConfigurationXMLInput" %>
<%@ page import="org.optimizationBenchmarking.utils.config.DefinitionXMLInput" %>
<%@ page import="org.optimizationBenchmarking.utils.config.Definition" %>
<%@ page import="org.optimizationBenchmarking.utils.config.Dump" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.optimizationBenchmarking.utils.config.Parameter" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />

<h1>Edit Configuration Files</h1>
<%
final String            submit   = request.getParameter(ControllerUtils.INPUT_SUBMIT);
final Definition        definition;
final ArrayList<String> jsCollector;
      Dump[]            dumps      = null;
      String[]          relPaths   = null;
      int               choice     = 2;
      String            prefix;
      
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
  <p>On this page, you can edit configuration files. A configuration file tells the
  evaluator from where and how to load the data, where to write the output, which
  output format to use, and where the list of things to do is. 
  You can save your changes by pressing the &quot;Save&quot; button.
  If you leave this page any un-saved changes will be discarded.</p> 
<% }
  try(final Handle handle = controller.createJspHandle(pageContext)) {
    definition = ConfigIO.getDefinition(handle);
  
    switch(choice) {
      case 0: {
        dumps = ConfigIO.load(null, relPaths, handle);
        break; }
      case 1: {
        dumps = ConfigIO.load(request.getParameter(ControllerUtils.INPUT_CURRENT_DIR),
                              relPaths, handle);
        break; }
      default: {
        handle.unknownSubmit(submit);
      }
    }
  }
    
  if((relPaths!=null) && (relPaths.length > 0) && (dumps != null)) {
    jsCollector = new ArrayList<>();
    for(int i = 0; i < relPaths.length; i++) {
      if( (relPaths[i] != null) && (dumps[i] != null) ) {
        final String relPath = Encoder.htmlEncode(relPaths[i]); 
%>
<h2>File &quot;<%= relPath %>&quot;</h2>
<form class="invisible" action="/configEditSave.jsp" method="post" target="_blank">
<input type="hidden" name="<%= ControllerUtils.PARAMETER_SELECTION%>" value="<%= relPath%>" />
<% prefix = String.valueOf(i); %>
<input type="hidden" name="<%= ConfigIO.PARAMETER_PREFIX%>" value="<%= prefix%>" />
<% ConfigIO.putFormFields(prefix, dumps[i], pageContext.getOut(), jsCollector); %>
<p class="controllerActions">
<input type="submit" name="<%= ControllerUtils.INPUT_SUBMIT%>" value="save">
<input type="submit" name="<%= ControllerUtils.INPUT_SUBMIT%>" value="download" formtarget="_blank" formmethod="get" formaction="/download">
<input type="submit" name="<%= ControllerUtils.INPUT_SUBMIT%>" value="<%= ControllerUtils.COMMAND_EXECUTE_EVALUATOR %>" formmethod="get" formaction="/evaluator.jsp">
</p>
</form>
<% } } ConfigIO.putJavaScript(pageContext.getOut(), jsCollector); } } %>
<%@include file="/includes/defaultFooter.jsp" %>