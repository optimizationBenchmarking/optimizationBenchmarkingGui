<%@ page import="org.optimizationBenchmarking.gui.controller.FSElement" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.EFSElementType" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerState" %>
<%@ page import="org.optimizationBenchmarking.utils.text.TextUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Encoder " %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />
<%
 final String submit = TextUtils.prepare(request.getParameter("submit"));
 
 ControllerState cstate = null; 
 try(final Handle handle = controller.createHandle(pageContext)) {
    if(submit != null) {
      switch(TextUtils.toLowerCase(submit)) {
        case "cda": { // cd
          controller.cdAbsolute(handle, request.getParameter("cd"));
          break;
        }
        case "cd": { // relative cd
          controller.cdRelative(handle, request.getParameter("cd"));
          break;
        }
        
        default: {
          handle.unknownSubmit(submit);
        }
      }
    }
    cstate = controller.getState(handle);
  }
 if(cstate != null) {
%>

<form class="controller" method="get" action="#">
<p class="breadcrumps">
  <%
  for(FSElement element : cstate.getPath()) { %>
    <a href="?cd=<%= Encoder.urlEncode(element.getRelativePath())%>&amp;submit=cda"><%= Encoder.htmlEncode(element.getName()) %></a> / <% } %>
<input type="text" name="cd" size="12" />&nbsp;<input type="submit" name="submit" value="cd" />
</p>
</form>
<form class="controller" method="get" action="#">
<p>The contents of the current folder are:</p>
<table class="folderView">
<tr class="folderViewHead">
<th class="folderViewHead">what</th>
<th class="folderViewHead">name</th>
<th class="folderViewHead">size</th>
<th class="folderViewHead">changed</th>
</tr>

<% for(FSElement element : cstate.getCurrent()) { 
   String folderName = null; %>
<tr class="folderViewRow">
  <td class="folderViewIcon">
    <% switch(element.getType()) { %>
      <% case NEXT_UP: {
         folderName = ".."; %>
        <img src="/icons/folderUp.png" class="folderIcon" alt="Move up one folder (to '<%= element.getName()%>')." />
      <% break; } %>
      <% case LIST_ROOT: { 
         folderName = "."; %>
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
  <td class="folderViewName"<%= tag%>>
    <% switch(element.getType()) { 
          case NEXT_UP:
          case LIST_ROOT:
          case FOLDER: { %>
      <a href="?cd=<%= Encoder.urlEncode(element.getRelativePath())%>&amp;submit=cda">
    <% } } %>
  <%= ((folderName==null) ? Encoder.htmlEncode(element.getName()) : folderName) %>
    <% switch(element.getType()) { 
          case NEXT_UP:
          case LIST_ROOT:
          case FOLDER: { %></a><% } } %>
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
</tr>
<% } %>
</table>
</form>
<% } %>