<%@ page import="org.optimizationBenchmarking.gui.controller.FSElement" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.EFSElementType" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerState" %>
<%@ page import="org.optimizationBenchmarking.utils.text.TextUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.utils.collections.lists.ArraySetView" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />
<%
 final ControllerState cstate = ControllerUtils.performRequest(request, pageContext); 
 if(cstate != null) {
%>

<form class="controller" method="get" action="#">
<h2>Path</h2>
<p class="breadcrumps">
  <%
  for(FSElement element : cstate.getPath()) { %>
    <a href="?cd=<%= Encoder.urlEncode(element.getRelativePath())%>&amp;submit=cda"><%= Encoder.htmlEncode(element.getName()) %></a> / <% } %>
<input type="text" name="cd" size="12" />&nbsp;<input type="submit" name="submit" value="cd" />
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
    } else {%>href="?cd=<%= urlEncodedRelativePath%>&amp;<%=ControllerUtils.INPUT_SUBMIT%>=<%= ControllerUtils.COMMAND_CD_ABSOLUTE%><% } %>"><%= elementName %></a>
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
<select id="mainSelection" name="<%= ControllerUtils.PARAMETER_WITH_SELECTED%>" onchange="onWithSelectionChange(this)">
<option><%= ControllerUtils.COMMAND_REMEMBER%></option>
<option><%= ControllerUtils.COMMAND_DOWNLOAD%></option>
<option><%= ControllerUtils.COMMAND_EXECUTE_EVALUATOR%></option>
</select>
<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%=ControllerUtils.BUTTON_OK%>" />
</p>
<p id="mainDesc" class="actionDescription" />
</form>

<% ArraySetView<FSElement> selected = cstate.getSelected();
   if((selected != null) && (!(selected.isEmpty()))) {%>
<form class="controller" method="get" action="#">
<h2>Remembered Elements</h2>
<table class="folderView">
<tr class="folderViewHead">
<th class="folderViewHead" />
<th class="folderViewHead">name</th>
<th class="folderViewHead">size</th>
<th class="folderViewHead">changed</th>
<th class="folderViewHead"/>
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
Remembered elements:
<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%=ControllerUtils.COMMAND_DOWNLOAD%>" formmethod="get" formaction="/downloadRemembered" formatarget="_blank" />
</p>
</form>

<% } %>
<% } %>

<script type="text/javascript">
function onWithSelectionChange(selection) {
  var form = null;
  var desc = null;

  if(selection != null) {
    if(selection.id == "mainSelection") {
      form = document.getElementById("mainForm");
      desc = document.getElementById("mainDesc");
    }
  }

  if(form != null) {
    switch(String(selection.value)) {
      case "<%= ControllerUtils.COMMAND_DOWNLOAD%>": {
        form.method = "get";
        form.action = "/downloadSelected";
        form.target = "_blank";
        break;
      }
      case "<%= ControllerUtils.COMMAND_EXECUTE_EVALUATOR%>": {
        form.method = "get";
        form.action = "/evaluator.jsp";
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

window.onload = function() {
  onWithSelectionChange(document.getElementById("mainSelection"));
  }
</script>