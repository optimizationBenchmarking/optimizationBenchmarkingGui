<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.utils.text.TextUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.modules.Evaluation" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.files.FSElement" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.files.EFSElementType" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.utils.collections.lists.ArrayListView" %>
<%@ page import="javax.servlet.jsp.JspWriter" %>
<%@ page import="org.optimizationBenchmarking.utils.text.textOutput.ITextOutput" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Page" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.SelButtonFunctionRenderer" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerActionFunctionRenderer" %>
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
 final String                   submit    = TextUtils.prepare(request.getParameter(ControllerUtils.INPUT_SUBMIT));
       ArrayListView<FSElement> collected = null;
 try(final Handle handle = controller.createJspHandle(pageContext)) {
    if(ControllerUtils.BUTTON_OK.equalsIgnoreCase(submit)) {
      final String withSelected = request.getParameter(ControllerUtils.PARAMETER_WITH_SELECTED); 
      if(ControllerUtils.COMMAND_EXECUTE_EVALUATOR.equalsIgnoreCase(withSelected)) {
        collected = Evaluation.evaluate(select, handle);
      } else {
        handle.failure("Unknown selection command: " + withSelected);
      }      
    } else {
      if(ControllerUtils.COMMAND_EXECUTE_EVALUATOR.equalsIgnoreCase(submit)) {
        collected = Evaluation.evaluate(select, handle);
      } else {
        handle.unknownSubmit(submit);
      }
    }
  }
%>
<h2>Created Files</h2>
<% if( (collected != null) && (!(collected.isEmpty())) ) {
    try(final Page hpage = new Page(pageContext)) {
       final ControllerActionFunctionRenderer selFunc =
         new ControllerActionFunctionRenderer("<em>Currently Chosen Action:</em>&nbsp;"); %>
<form id="prodForm" class="controller" method="get" action="/controller.jsp">
<table class="folderView">
<tr class="folderViewHead">
<th class="folderViewHead" />
<th class="folderViewHead">name</th>
<th class="folderViewHead">size</th>
<th class="folderViewHead">changed</th>
<td class="folderViewSelect"><input type="button" class="selButton" onclick="<%=hpage.getFunction(SelButtonFunctionRenderer.INSTANCE)%>('prodForm',true)" value="&#x2611;"/></th>
</tr>
<% final ITextOutput encoded = Encoder.htmlEncode(out);
         int         row     = 0;   
   for(FSElement element : collected) { 
   String urlEncodedRelativePath  = Encoder.urlEncode(element.getRelativePath()); 
   String htmlEncodedRelativePath = Encoder.htmlEncode(element.getRelativePath());
   String shortPath               = htmlEncodedRelativePath;
   int    lastSlash               = shortPath.lastIndexOf('/');
   EFSElementType type            = element.getType();
   if((lastSlash > 0)&&(lastSlash<(shortPath.length()-1))) {
      shortPath = shortPath.substring(lastSlash+1);
   } %>
<tr class="folderViewRow<% if(((++row)&1)==0){%>Even<%}%>">
  <td class="folderViewIcon"><% (type.isFile()?type:EFSElementType.FOLDER).putIcon(out, encoded); %></td>
  <% final long size = element.getSize();
     final long time = element.getTime();     
     String tag;
     if (size < 0L) {
      if(time < 0L) {
        tag = " colspan=\"3\"";
      } else {
        tag = " colspan=\"2\"";
      }
    } else {
      tag = "";
    } %>
  <td class="folderViewName"<%= tag%>>
  <% if(element.getType().isFile()) { %><a target="_blank" href="/viewer?<%=ControllerUtils.PARAMETER_VIEW%>=<%= urlEncodedRelativePath%>"><% } %>
    <%= shortPath %>
  <% if(element.getType().isFile()) { %></a><% } %>
  </td>
  <% if (size >= 0L) {
       if(time < 0L) {
        tag = " colspan=\"2\"";
      } else {
        tag = "";
      } %>
    <td class="folderViewSize"<%= tag%>><%= element.getSizeString() %></td>
  <% } %>
  <% if (time >= 0L) { %>
    <td class="folderViewTime"><%= element.getTimeString() %></td>
  <% } %>
  <td class="folderViewSel"><input type="checkbox" name="<%=ControllerUtils.PARAMETER_SELECTION%>" value="<%= htmlEncodedRelativePath %>"/></td>
</tr>
<% } %>
<tr class="folderViewBottom"><td colspan="4" class="folderViewBottomInfo"/><td class="folderViewSelect"><input type="button" class="selButton" onclick="hpage.getFunction(SelButtonFunctionRenderer.INSTANCE)%>('prodForm',false)" value="&#x2610;"/></td></tr>
</table>
<div class="controllerActions">
Selected produced element(s):&nbsp;<% ControllerUtils.putFormSelection("prod", hpage, selFunc,
     ControllerUtils.REMEMBER,
     ControllerUtils.DOWNLOAD,
     ControllerUtils.EDIT_AS_TEXT,
     ControllerUtils.DELETE);
%>&nbsp;<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%=ControllerUtils.BUTTON_OK%>" />
</div>
<p id="prodDesc" class="actionDescription" />
</form>
<% } } else { %>
<p>No files were created. Probably something went wrong.</p>
<% } %>
<p>
You can (and probably should) now return to the <a href="/controller.jsp">control center</a>.
</p>
<%@include file="/includes/defaultFooter.jsp" %>