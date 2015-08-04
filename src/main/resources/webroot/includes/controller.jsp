<%@ page import="org.optimizationBenchmarking.gui.controller.FSElement" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.EFSElementType" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerState" %>
<%@ page import="org.optimizationBenchmarking.utils.text.TextUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Encoder" %>
<%@ page import="org.optimizationBenchmarking.utils.collections.lists.ArraySetView" %>
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
        case "remember": { // select
          controller.select(handle, request.getParameterValues("select"));
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
<h2>Path</h2>
<p class="breadcrumps">
  <%
  for(FSElement element : cstate.getPath()) { %>
    <a href="?cd=<%= Encoder.urlEncode(element.getRelativePath())%>&amp;submit=cda"><%= Encoder.htmlEncode(element.getName()) %></a> / <% } %>
<input type="text" name="cd" size="12" />&nbsp;<input type="submit" name="submit" value="cd" />
</p>
</form>

<form class="controller" method="get" action="#">
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
    <% switch(element.getType()) { 
          case NEXT_UP:
          case LIST_ROOT:
          case FOLDER: { %>
      <a href="?cd=<%= urlEncodedRelativePath%>&amp;submit=cda">
    <% } } %>
  <%= elementName %>
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
  <td class="folderViewSel"><input type="checkbox" name="select" value="<%= htmlEncodedRelativePath %>"/></td>
</tr>
<% } %>
</table>
<p class="controllerActions">
Selected elements:
<input type="submit" name="submit" value="remember" />
</p>
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
   String htmlEncodedRelativePath = Encoder.htmlEncode(element.getRelativePath()); %>
<tr class="folderViewRow">
  <td class="folderViewIcon">
    <% switch(element.getType()) { 
         case NEXT_UP:
         case LIST_ROOT: 
         case FOLDER: { %>
        <img src="/icons/folder.png" class="folderIcon" alt="Selected folder '<%= htmlEncodedRelativePath%>'." />
      <% break; } %>
      <% default: { %>
        <img src="/icons/file.png" class="folderIcon" alt="Selected file '<%= htmlEncodedRelativePath%>'." />
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
  <%= htmlEncodedRelativePath %>
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
  <td class="folderViewSel"><input type="checkbox" name="select" value="<%= htmlEncodedRelativePath %>"/></td>
</tr>
<% } %>
</table>
</form>

<% } %>
<% } %>