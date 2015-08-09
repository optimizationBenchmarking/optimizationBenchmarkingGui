<%@ page import="org.optimizationBenchmarking.gui.controller.FSElement" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.EFSElementType" %>
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
  for(FSElement element : cstate.getPath()) { %>
    <a href="?<%= ControllerUtils.PARAMETER_CD_PATH%>=<%= Encoder.urlEncode(element.getRelativePath())%>&amp;submit=<%= ControllerUtils.COMMAND_CD_ABSOLUTE%>"><%= Encoder.htmlEncode(element.getName()) %></a> / <% } %>
<input type="text" name="<%= ControllerUtils.PARAMETER_CD_PATH%>" size="12" />&nbsp;<input type="submit" name="submit" value="<%= ControllerUtils.COMMAND_CD_RELATIVE%>" />
</p>
</form>

<form id="mainForm" class="controller" method="get" action="#">
<h2>Current Folder</h2>
<table class="folderView">
<tr class="folderViewHead">
<th class="folderViewHead" />
<th class="folderViewHead">name</th>
<th class="folderViewHead">size</th>
<th class="folderViewHead">changed</th>
<th class="folderViewHead"/>
</tr>

<% for(FSElement element : cstate.getCurrent()) { 
   String elementName             = Encoder.htmlEncode(element.getName());
   String urlEncodedRelativePath  = Encoder.urlEncode(element.getRelativePath()); 
   String htmlEncodedRelativePath = Encoder.htmlEncode(element.getRelativePath()); %>
<tr class="folderViewRow">
  <td class="folderViewIcon">
    <% switch(element.getType()) { %>
      <% case NEXT_UP: { %>
        <img src="/icons/folderUp.png" class="folderIcon" alt="Move up one folder (to '<%= elementName%>')." />
      <%  elementName = ".."; break; } %>
      <% case LIST_ROOT: {  %>
        <img src="/icons/folderCur.png" class="folderIcon" alt="The current folder ('<%= elementName%>')." />
      <% elementName = "."; break; } %>
      <% case FOLDER: { %>
        <img src="/icons/folder.png" class="folderIcon" alt="Enter folder '<%= elementName%>'." />
      <% break; } %>
      <% default: { %>
        <img src="/icons/file.png" class="folderIcon" alt="File '<%= elementName%>'." />
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
</table>
<p class="controllerActions">
Selected element(s):
<select id="mainSelection" name="<%= ControllerUtils.PARAMETER_WITH_SELECTED%>" onchange="onWithSelectionChange('main', this)">
<option><%= ControllerUtils.COMMAND_REMEMBER%></option>
<option><%= ControllerUtils.COMMAND_DOWNLOAD%></option>
<option><%= ControllerUtils.COMMAND_EDIT%></option>
<option><%= ControllerUtils.COMMAND_EXECUTE_EVALUATOR%></option>
</select>
<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%=ControllerUtils.BUTTON_OK%>" />
</p>
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
<th class="folderViewHead" />
</tr>

<% for(FSElement element : selected) { 
   String urlEncodedRelativePath  = Encoder.urlEncode(element.getRelativePath()); 
   String htmlEncodedRelativePath = Encoder.htmlEncode(element.getRelativePath()); %>
<tr class="folderViewRow">
  <td class="folderViewIcon">
    <% if(element.getType().isFile()) {  %>
        <img src="/icons/file.png" class="folderIcon" alt="Selected file '<%= htmlEncodedRelativePath%>'." />        
      <% } else { %>
        <img src="/icons/folder.png" class="folderIcon" alt="Selected folder '<%= htmlEncodedRelativePath%>'." />
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
</table>
<p class="controllerActions">
Selected remembered element(s):
<select id="remSelection" name="<%= ControllerUtils.PARAMETER_WITH_SELECTED%>" onchange="onWithSelectionChange('rem', this)">
<option><%= ControllerUtils.COMMAND_DOWNLOAD%></option>
<option><%= ControllerUtils.COMMAND_EDIT%></option>
<option><%= ControllerUtils.COMMAND_EXECUTE_EVALUATOR%></option>
</select>
<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%=ControllerUtils.BUTTON_OK%>" />
</p>
<p id="remDesc" class="actionDescription" />
</form>

<% } %>
<% } %>

<script type="text/javascript">
function onWithSelectionChange(prefix, selection) {
  var form = null;
  var desc = null;

  if(selection != null) {
    if(selection.id == (prefix+"Selection")) {
      form = document.getElementById(prefix + "Form");
      desc = document.getElementById(prefix + "Desc");
    }
    
    if(form != null) {
      switch(String(selection.value)) {
        case "<%= ControllerUtils.COMMAND_DOWNLOAD%>": {
          form.method = "get";
          form.action = "/download";
          form.target = "_blank";
          break;
        }
        case "<%= ControllerUtils.COMMAND_EXECUTE_EVALUATOR%>": {
          form.method = "get";
          form.action = "/evaluator.jsp";
          form.target = "_self";      
          break;
        }
        case "<%= ControllerUtils.COMMAND_EDIT%>": {
          form.method = "get";
          form.action = "/edit.jsp";
          form.target = "_self";      
          break;
        }
        default: {
          form.method = "get";
          form.action = "#";
          form.target = "_self";
        }
      }
    }
   
    if(desc != null) {  
      switch(String(selection.value)) {
        case "<%= ControllerUtils.COMMAND_REMEMBER%>": {
          desc.innerHTML = "Remember the selected files. The files will be listed at the bottom of the controller window. Remembering files allows you to pick files from different directories, e.g., for download, without having to choose the complete directories.";
          break;
        }
        case "<%= ControllerUtils.COMMAND_DOWNLOAD%>": {
          desc.innerHTML = "Download the selected file(s). If one file is selected, it is sent as-is. If multiple files or folders are selected, they will be put into a <code>zip</code> archive.";
          break;
        }
        case "<%= ControllerUtils.COMMAND_EDIT%>": {
          desc.innerHTML = "Edit the selected file(s) as text file (s). This assumes that you know what you are doing, as syntax and content of the file will not be verified but treated as plain text.";
          break;
        }
        case "<%= ControllerUtils.COMMAND_EXECUTE_EVALUATOR%>": {
          desc.innerHTML = "The selected file must be a configuration file for an evaluation process. Then evaluation process will be started. It may take some time to finish. During this time, depending on the <a href='/logLevel.jsp'>log level</a> you set, you will receive information about what's going on. While the process is running, do not close or refresh the page. If you selected multiple configuration files, they will be processed one after the other.";      
          break;
        }      
        default: {
          desc.innerHTML = "";
        }
      }
    }
  }
}

window.onload = function() {
  onWithSelectionChange("main", document.getElementById("mainSelection"));
  onWithSelectionChange("rem",  document.getElementById("remSelection"));
}
</script>
<%@include file="/includes/defaultFooter.jsp" %>