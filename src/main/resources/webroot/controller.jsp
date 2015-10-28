<%@ page import="org.optimizationBenchmarking.gui.utils.files.FSElement" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.files.EFSElementType" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerState" %>
<%@ page import="org.optimizationBenchmarking.utils.text.TextUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.utils.collections.lists.ArraySetView" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<%@ page import="javax.servlet.jsp.JspWriter" %>
<%@ page import="org.optimizationBenchmarking.utils.text.textOutput.ITextOutput" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Page" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.SelButtonFunctionRenderer" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerActionFunctionRenderer" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />
<%@include file="/includes/defaultHeader.jsp" %>
<%
 final ControllerState cstate = ControllerUtils.performRequest(request, pageContext); 
 if(cstate != null) {
%>
<% try(final Page hpage = new Page(pageContext)) {
       final ControllerActionFunctionRenderer selFunc =
         new ControllerActionFunctionRenderer("<em>Currently Chosen Action:</em>&nbsp;"); %>
<form class="controller" id="pathForm" method="get" action="/controller.jsp">
<h2>Path</h2>
<p class="breadcrumps">
  <%
  String currentDir = null;
  for(FSElement element : cstate.getPath()) {
     currentDir = element.getRelativePath(); %>  
    <a href="?<%= ControllerUtils.PARAMETER_CD_PATH%>=<%= Encoder.urlEncode(currentDir)%>&amp;<%=ControllerUtils.INPUT_SUBMIT%>=<%= ControllerUtils.COMMAND_CD_ABSOLUTE%>"><%= Encoder.htmlEncode(element.getName()) %></a>/<%
   } 
%><input type="hidden" name="<%= ControllerUtils.PARAMETER_NEW%>" value="true" />
  <input type="text" name="<%= ControllerUtils.PARAMETER_SELECTION%>" size="12" />&nbsp;<%
    ControllerUtils.putFormSelection("path", hpage,
     new ControllerActionFunctionRenderer("<em>Currently Chosen Action:</em>&nbsp;"),
     ControllerUtils.CD,
     ControllerUtils.NEW_TEXT_FILE,
     ControllerUtils.NEW_CONFIGURATION_FILE,
     ControllerUtils.NEW_EVALUATION_FILE,
     ControllerUtils.NEW_DIMENSIONS_FILE,
     ControllerUtils.NEW_INSTANCES_FILE,
     ControllerUtils.NEW_EXPERIMENT_FILE);
%>&nbsp;<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%=ControllerUtils.BUTTON_OK%>" />
</p><p class="actionDescription" id="pathDesc"/><p>
<input type="file" name="<%= ControllerUtils.PARAMETER_FILES%>" multiple />&nbsp;<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%= ControllerUtils.COMMAND_UPLOAD%>" formmethod="post" formaction="/upload" formenctype="multipart/form-data" />
</p>
<p class="actionDescription">
<code><%= ControllerUtils.COMMAND_UPLOAD%></code> uploads a set of files. Uploaded <code>zip</code> archives are automatically extracted.
</p>
<input type="hidden" name="<%= ControllerUtils.INPUT_CURRENT_DIR%>" value="<%= Encoder.htmlEncode(currentDir)%>" />
</form>
<form id="mainForm" class="controller" method="get" action="/controller.jsp">
<h2>Current Folder</h2>
<table class="folderView">
<tr class="folderViewHead">
<th class="folderViewHead" />
<th class="folderViewHead">name</th>
<th class="folderViewHead">size</th>
<th class="folderViewHead">changed</th>
<td class="folderViewSelect"><input type="button" class="selButton" onclick="<%=hpage.getFunction(SelButtonFunctionRenderer.INSTANCE)%>('mainForm',true)" value="&#x2611;" /></th>
</tr>
<% final ITextOutput encoded = Encoder.htmlEncode(out);
         int         row     = 0;   
   for(FSElement element : cstate.getCurrent()) { 
   String elementName             = Encoder.htmlEncode(element.getName());
   String urlEncodedRelativePath  = Encoder.urlEncode(element.getRelativePath()); 
   String htmlEncodedRelativePath = Encoder.htmlEncode(element.getRelativePath()); 
   EFSElementType type            = element.getType(); %>   
<tr class="folderViewRow<% if(((++row)&1)==0){%>Even<%}%>">
  <td class="folderViewIcon"><% type.putIcon(out, encoded); %></td>
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
    <a <% if(element.getType().isFile()) { %>target="_blank" href="/viewer?<%=ControllerUtils.PARAMETER_VIEW%>=<%= urlEncodedRelativePath%><%
    } else {%>href="?<%= ControllerUtils.PARAMETER_CD_PATH%>=<%= urlEncodedRelativePath%>&amp;<%=ControllerUtils.INPUT_SUBMIT%>=<%= ControllerUtils.COMMAND_CD_ABSOLUTE%><% } %>"><%= elementName %></a>
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
  <td class="folderViewSel"><input type="checkbox" name="<%= ControllerUtils.PARAMETER_SELECTION%>" value="<%= htmlEncodedRelativePath %>"/></td>
</tr>
<% } %>
<tr class="folderViewBottom"><td colspan="4" class="folderViewBottomInfo"/><td class="folderViewSelect"><input type="button" class="selButton" onclick="<%=hpage.getFunction(SelButtonFunctionRenderer.INSTANCE)%>('mainForm',false)" value="&#x2610;"/></td></tr>
</table>
<div class="controllerActions">
Selected element(s):&nbsp;<% ControllerUtils.putFormSelection("main", hpage, selFunc,
     ControllerUtils.REMEMBER,
     ControllerUtils.DOWNLOAD,
     ControllerUtils.EDIT,
     ControllerUtils.EDIT_AS_TEXT,
     ControllerUtils.DELETE);
%>&nbsp;<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%=ControllerUtils.BUTTON_OK%>" />
</div>
<p id="mainDesc" class="actionDescription" />
</form>

<% ArraySetView<FSElement> selected = cstate.getSelected();
   if((selected != null) && (!(selected.isEmpty()))) {%>
<form id="remForm" class="controller" method="get" action="#">
<h2>Remembered Elements</h2>
<table class="folderView">
<tr class="folderViewHead">
<th class="folderViewHead" />
<th class="folderViewHead">name</th>
<th class="folderViewHead">size</th>
<th class="folderViewHead">changed</th>
<td class="folderViewSelect"><input type="button" class="selButton" onclick="<%=hpage.getFunction(SelButtonFunctionRenderer.INSTANCE)%>('remForm',true)" value="&#x2611;"/></th>
</tr>
<% row = 0;
   for(FSElement element : selected) { 
   String urlEncodedRelativePath  = Encoder.urlEncode(element.getRelativePath()); 
   String htmlEncodedRelativePath = Encoder.htmlEncode(element.getRelativePath());   
   EFSElementType type            = element.getType();  %>
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
    <%= htmlEncodedRelativePath %>
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
<tr class="folderViewBottom"><td colspan="4" class="folderViewBottomInfo"/><td class="folderViewSelect"><input type="button" class="selButton" onclick="<%=hpage.getFunction(SelButtonFunctionRenderer.INSTANCE)%>('remForm',false)" value="&#x2610;"/></td></tr>
</table>
<p class="controllerActions">
Selected remembered element(s):&nbsp;<% ControllerUtils.putFormSelection("rem", hpage, selFunc,
     ControllerUtils.FORGET,
     ControllerUtils.DOWNLOAD,
     ControllerUtils.EDIT,
     ControllerUtils.EDIT_AS_TEXT,
     ControllerUtils.DELETE);
%>&nbsp;<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%=ControllerUtils.BUTTON_OK%>" />
</p>
<p id="remDesc" class="actionDescription" />
</form>
<% }}} %>
<%@include file="/includes/defaultFooter.jsp" %>