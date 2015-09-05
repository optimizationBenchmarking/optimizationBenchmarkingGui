<%@ page import="org.optimizationBenchmarking.gui.utils.files.FSElement" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.files.EFSElementType" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerState" %>
<%@ page import="org.optimizationBenchmarking.utils.text.TextUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.utils.collections.lists.ArraySetView" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />
<%@include file="/includes/defaultHeader.jsp" %>
<%
 final ControllerState cstate = ControllerUtils.performRequest(request, pageContext); 
 if(cstate != null) {
%>

<form class="controller" method="get" action="#">
<h2>Path</h2>
<p class="breadcrumps">
  <%
  String currentDir = null;
  for(FSElement element : cstate.getPath()) {
     currentDir = element.getRelativePath(); %>  
    <a href="?<%= ControllerUtils.PARAMETER_CD_PATH%>=<%= Encoder.urlEncode(currentDir)%>&amp;<%=ControllerUtils.INPUT_SUBMIT%>=<%= ControllerUtils.COMMAND_CD_ABSOLUTE%>"><%= Encoder.htmlEncode(element.getName()) %></a>/<% } %>
<input type="text" name="<%= ControllerUtils.PARAMETER_CD_PATH%>" size="12" />&nbsp;<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%= ControllerUtils.COMMAND_CD_RELATIVE%>" />&nbsp;<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%= ControllerUtils.COMMAND_NEW_FILE%>" formaction="/textEdit.jsp" />
</p>
<p>
<input type="file" name="<%= ControllerUtils.PARAMETER_FILES%>" multiple />&nbsp;<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%= ControllerUtils.COMMAND_UPLOAD%>" formmethod="post" formaction="/upload" formenctype="multipart/form-data" />
</p>
<p class="actionDescription">
<code><%= ControllerUtils.COMMAND_CD_RELATIVE%></code> will create a new directory if necessary.
<code><%= ControllerUtils.COMMAND_NEW_FILE%></code> creates a new file (if it does not exist yet) and opens it as text file in the editor.
<code><%= ControllerUtils.COMMAND_UPLOAD%></code> uploads a set of files. Uploaded <code>zip</code> archives are automatically extracted.
</p>
<input type="hidden" name="<%= ControllerUtils.INPUT_CURRENT_DIR%>" value="<%= Encoder.htmlEncode(currentDir)%>" />
</form>

<form id="mainForm" class="controller" method="get" action="#">
<h2>Current Folder</h2>
<table class="folderView">
<tr class="folderViewHead">
<th class="folderViewHead" />
<th class="folderViewHead">name</th>
<th class="folderViewHead">size</th>
<th class="folderViewHead">changed</th>
<td class="folderViewSelect"><input type="button" class="selButton" onclick="onSelButtonClick('mainForm', true)" value="&#x2611;" /></th>
</tr>
<% int row = 0;
   for(FSElement element : cstate.getCurrent()) { 
   String elementName             = Encoder.htmlEncode(element.getName());
   String urlEncodedRelativePath  = Encoder.urlEncode(element.getRelativePath()); 
   String htmlEncodedRelativePath = Encoder.htmlEncode(element.getRelativePath()); 
   EFSElementType type            = element.getType(); %>   
<tr class="folderViewRow<% if(((++row)&1)==0){%>Even<%}%>">
  <td class="folderViewIcon">
    <% switch(type) { %>
      <% case NEXT_UP: { %>
        <img src="/icons/<%=type.getIcon()%>.png" class="folderIcon" alt="Move up one folder (to '<%= elementName%>')." />
      <%  elementName = ".."; break; } %>
      <% case LIST_ROOT: {  %>
        <img src="/icons/<%=type.getIcon()%>.png" class="folderIcon" alt="The current folder ('<%= elementName%>')." />
      <% elementName = "."; break; } %>
      <% case FOLDER: { %>
        <img src="/icons/<%=type.getIcon()%>.png" class="folderIcon" alt="Enter folder '<%= elementName%>'." />
      <% break; } %>
      <% default: { %>
        <img src="/icons/<%=type.getIcon()%>.png" class="folderIcon" alt="File '<%= elementName%>'." />
      <% break; } 
     } %>
  </td>
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
    <a <% if(element.getType().isFile()) { %>target="_blank" href="/viewer?view=<%= urlEncodedRelativePath%><%
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
<tr class="folderViewBottom"><td colspan="4" class="folderViewBottomInfo"/><td class="folderViewSelect"><input type="button" class="selButton" onclick="onSelButtonClick('mainForm', false)" value="&#x2610;"/></td></tr>
</table>
<div class="controllerActions">
Selected element(s):
<select id="mainSelection" name="<%= ControllerUtils.PARAMETER_WITH_SELECTED%>" onchange="onWithSelectionChange('main', this)">
<option><%= ControllerUtils.COMMAND_REMEMBER%></option>
<option><%= ControllerUtils.COMMAND_DOWNLOAD%></option>
<option><%= ControllerUtils.COMMAND_EDIT_AS_TEXT%></option>
<option><%= ControllerUtils.COMMAND_EDIT_AS_DIMENSIONS%></option>
<option><%= ControllerUtils.COMMAND_EDIT_AS_INSTANCES%></option>
<option><%= ControllerUtils.COMMAND_EDIT_AS_EXPERIMENT%></option>
<option><%= ControllerUtils.COMMAND_EDIT_AS_CONFIG%></option>
<option><%= ControllerUtils.COMMAND_EDIT_AS_EVALUATION%></option>
<option><%= ControllerUtils.COMMAND_EXECUTE_EVALUATOR%></option>
<option><%= ControllerUtils.COMMAND_DELETE%></option>
</select>
<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%=ControllerUtils.BUTTON_OK%>" />
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
<td class="folderViewSelect"><input type="button" class="selButton" onclick="onSelButtonClick('remForm', true)" value="&#x2611;"/></th>
</tr>
<% row = 0;
   for(FSElement element : selected) { 
   String urlEncodedRelativePath  = Encoder.urlEncode(element.getRelativePath()); 
   String htmlEncodedRelativePath = Encoder.htmlEncode(element.getRelativePath());   
   EFSElementType type            = element.getType();  %>
<tr class="folderViewRow<% if(((++row)&1)==0){%>Even<%}%>">
  <td class="folderViewIcon">
    <% if(element.getType().isFile()) {  %>
        <img src="/icons/<%=type.getIcon()%>.png" class="folderIcon" alt="Selected file '<%= htmlEncodedRelativePath%>'." />        
      <% } else { %>
        <img src="/icons/<%=EFSElementType.FOLDER.getIcon()%>.png" class="folderIcon" alt="Selected folder '<%= htmlEncodedRelativePath%>'." />
      <% } %>
  </td>
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
  <% if(element.getType().isFile()) { %><a target="_blank" href="/viewer?view=<%= urlEncodedRelativePath%>"><% } %>
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
<tr class="folderViewBottom"><td colspan="4" class="folderViewBottomInfo"/><td class="folderViewSelect"><input type="button" class="selButton" onclick="onSelButtonClick('remForm', false)" value="&#x2610;"/></td></tr>
</table>
<p class="controllerActions">
Selected remembered element(s):
<select id="remSelection" name="<%= ControllerUtils.PARAMETER_WITH_SELECTED%>" onchange="onWithSelectionChange('rem', this)">
<option><%= ControllerUtils.COMMAND_FORGET%></option>
<option><%= ControllerUtils.COMMAND_DOWNLOAD%></option>
<option><%= ControllerUtils.COMMAND_EDIT_AS_TEXT%></option>
<option><%= ControllerUtils.COMMAND_EDIT_AS_DIMENSIONS%></option>
<option><%= ControllerUtils.COMMAND_EDIT_AS_INSTANCES%></option>
<option><%= ControllerUtils.COMMAND_EDIT_AS_EXPERIMENT%></option>
<option><%= ControllerUtils.COMMAND_EDIT_AS_CONFIG%></option>
<option><%= ControllerUtils.COMMAND_EDIT_AS_EVALUATION%></option>
<option><%= ControllerUtils.COMMAND_EXECUTE_EVALUATOR%></option>
<option><%= ControllerUtils.COMMAND_DELETE%></option>
</select>
<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%=ControllerUtils.BUTTON_OK%>" />
</p>
<p id="remDesc" class="actionDescription" />
</form>
<% }} %>
<script type="text/javascript">
<%@include file="/includes/controllerJavascript.js.jsp" %>
window.onload = function() {
  onWithSelectionChange("main", document.getElementById("mainSelection"));
  onWithSelectionChange("rem",  document.getElementById("remSelection"));
}
</script>
<%@include file="/includes/defaultFooter.jsp" %>