<%@ page import="org.optimizationBenchmarking.gui.controller.FSElement" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.EFSElementType" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerState" %>
<%@ page import="org.optimizationBenchmarking.utils.text.TextUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />
<%
 ControllerState cstate = null;
 try(final Handle handle = controller.createHandle(pageContext)) {
    cstate = controller.getState(handle);
  }
 if(cstate != null) {
%>
<div class="controller">
<p>The contents of the current folder are:</p>
<table class="folderView">
<tr class="folderViewHead">
<th class="folderViewHead">what</th>
<th class="folderViewHead">name</th>
<th class="folderViewHead">size</th>
<th class="folderViewHead">changed</th>
</tr>

<% for(FSElement element : cstate.getCurrent()) { 
   String name = null; %>
<tr class="folderViewRow">
  <td class="folderViewIcon">
    <% switch(element.getType()) { %>
      <% case NEXT_UP: {
         name = ".."; %>
        <img src="/icons/folderUp.png" class="folderIcon" alt="Move up one folder (to '<%= element.getName()%>')." />
      <% break; } %>
      <% case LIST_ROOT: { 
         name = "."; %>
        <img src="/icons/folderCur.png" class="folderIcon" alt="The current folder ('<%= element.getName()%>')." />
      <% break; } %>
      <% case FOLDER: { %>
        <img src="/icons/folder.png" class="folderIcon" alt="Enter folder '<%= element.getName()%>'." />
      <% break; } %>
      <% default: { %>
        <img src="/icons/file.png" class="folderIcon" alt="File '<%= element.getName()%>'." />
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
  <td class="folderViewName"<%= tag%>><%= ((name==null) ? element.getName() : name) %></td>
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
</tr>
<% } %>
</table>
</div>
<% } %>