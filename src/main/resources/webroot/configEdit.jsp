<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.ConfigIO" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.FileIO" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Loaded" %>
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
<p>On this page, you can edit configuration files. A configuration file tells the
evaluator from where and how to load the data, where to write the output, which
output format to use, and where the list of things to do is. 
You can save your changes by pressing the &quot;Save&quot; button.
If you leave this page any un-saved changes will be discarded.</p>
<%
final String            submit   = request.getParameter(ControllerUtils.INPUT_SUBMIT);
final ArrayList<String> jsCollector;
      Loaded<Dump>      dumps      = null;      

try(final Handle handle = controller.createJspHandle(pageContext)) {
  if(ControllerUtils.BUTTON_OK.equalsIgnoreCase(submit)) {
    dumps = ConfigIO.load(null, 
                request.getParameterValues(ControllerUtils.PARAMETER_SELECTION),
                handle);
  } else {
    if(ControllerUtils.COMMAND_NEW_FILE.equalsIgnoreCase(submit)) {
      dumps = ConfigIO.load(request.getParameter(ControllerUtils.INPUT_CURRENT_DIR),
                            new String[] { request.getParameter(ControllerUtils.PARAMETER_CD_PATH) },
                            handle);
    } else {
      if(FileIO.PARAM_SAVE.equalsIgnoreCase(submit)) {
          ConfigIO.store(handle, request);
          dumps = ConfigIO.load(null, 
                new String[] { request.getParameter(ControllerUtils.PARAMETER_SELECTION) },
                handle);   
      } else {
        handle.unknownSubmit(submit);
      }
    }    
  }
}

if(dumps != null) {
  jsCollector = new ArrayList<>();
%>
<h2>File &quot;<%= Encoder.htmlEncode(dumps.getName()) %>&quot;</h2>
<form class="invisible" action="/configEdit.jsp" method="post">
<input type="hidden" name="<%= ControllerUtils.PARAMETER_SELECTION%>" value="<%= Encoder.htmlEncode(dumps.getRelativePath())%>" />
<% final String prefix = "c0"; %>
<input type="hidden" name="<%= ConfigIO.PARAMETER_PREFIX%>" value="<%= prefix%>" />
<% ConfigIO.putFormFields(prefix, dumps.getLoaded(), pageContext.getOut(), jsCollector); %>
<hr/>
<p class="controllerActions">
<input type="submit" name="<%= ControllerUtils.INPUT_SUBMIT%>" value="save">
<input type="submit" name="<%= ControllerUtils.INPUT_SUBMIT%>" value="download" formtarget="_blank" formmethod="post" formaction="/download">
<input type="submit" name="<%= ControllerUtils.INPUT_SUBMIT%>" value="<%= ControllerUtils.COMMAND_EXECUTE_EVALUATOR %>" formmethod="get" formaction="/evaluator.jsp">
</p>
</form>
<% ConfigIO.putJavaScript(pageContext.getOut(), jsCollector); } %>
<%@include file="/includes/defaultFooter.jsp" %>