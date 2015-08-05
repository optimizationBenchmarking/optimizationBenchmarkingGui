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
 try(final Handle handle = controller.createJspHandle(pageContext)) {
    if(submit != null) {
      sub: switch(TextUtils.toLowerCase(submit)) {
        case "cda": { // cd
          controller.cdAbsolute(handle, request.getParameter("cd"));
          break sub;
        }
        case "cd": { // relative cd
          controller.cdRelative(handle, request.getParameter("cd"));
          break sub;
        }
        case "ok": { // select          
          String selectionValue = request.getParameter("withSelected");
          if(selectionValue != null) {
            switch(TextUtils.toLowerCase(selectionValue)) {
              case  "remember": {
                controller.select(handle, request.getParameterValues("select"));
                break sub;
                }
              default: {
                handle.failure("Unknown selection command '" + selectionValue + '\'' + '.');
                break sub;
                }
              }
            }
            
          handle.warning("OK button pressed, but nothing to do.");
          break sub;
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
    <a <% if(element.getType().isFile()) { %>target="_blank" href="/viewer?view=<%= urlEncodedRelativePath%><%
    } else {%>href="?cd=<%= urlEncodedRelativePath%>&amp;submit=cda<% } %>"><%= elementName %></a>
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
<select name="withSelected">
<option>remember</option>
</select>
<input type="submit" name="submit" value="OK" />
or
<input type="submit" name="submit" value="download" formmethod="get" formaction="/downloadSelected" formatarget="_blank" />
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
  <td class="folderViewSel"><input type="checkbox" name="select" value="<%= htmlEncodedRelativePath %>"/></td>
</tr>
<% } %>
</table>
<p class="controllerActions">
Remembered elements:
<input type="submit" name="submit" value="download" formmethod="get" formaction="/downloadRemembered" formatarget="_blank" />
</p>
</form>

<% } %>
<% } %>