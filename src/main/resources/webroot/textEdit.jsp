<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.FileIO" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Loaded" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />

<h1>Edit as Plain Text</h1>
<%
final String           submit   = request.getParameter(ControllerUtils.INPUT_SUBMIT);
      Loaded<String>[] texts    = null;
      String[]         relPaths = null;
      int              choice   = 2;
      
if(submit != null) {  
  if(submit.equalsIgnoreCase(ControllerUtils.BUTTON_OK)) {
    choice = 0;  
    relPaths = request.getParameterValues(ControllerUtils.PARAMETER_SELECTION);
  } else {
    if(submit.equalsIgnoreCase(ControllerUtils.COMMAND_NEW_FILE)) {
      choice = 1;  
      relPaths = new String[] { request.getParameter(ControllerUtils.PARAMETER_CD_PATH) };
    }
  }

  if((relPaths!=null) && (relPaths.length > 0)) { %>
  <p>On this page, you can edit files as plain text. No syntax validation or other verification
  will be applied to the files. You can save your changes by pressing the &quot;Save&quot; button.
  If you leave this page any un-saved changes will be discarded.</p>
  <p>For convenience and syntax highlighting, you may consider copying the contents to an
  editor of your choice, then copying the edited document back here and saving it.</p> 
<% }
  try(final Handle handle = controller.createJspHandle(pageContext)) {
    switch(choice) {
      case 0: {
        texts = FileIO.load(null, relPaths, handle);
        break; }
      case 1: {
        texts = FileIO.load(request.getParameter(ControllerUtils.INPUT_CURRENT_DIR),
                            relPaths, handle);
        break; }
      default: {
        handle.unknownSubmit(submit);
      }
    }
  }
    
  if(texts != null) {
    for(int i = 0; i < texts.length; i++) {
%>
<h2>File &quot;<%= Encoder.htmlEncode(texts[i].getName()) %>&quot;</h2>
<form class="invisible" action="/textEditSave.jsp" method="post" target="_blank">
<textarea class="editor" rows="25" cols="70" name="contents" wrap="off"<% if(i<=0) {%> autofocus<%}%>><%= texts[i].getLoaded() %></textarea>
<input type="hidden" name="<%= ControllerUtils.PARAMETER_SELECTION%>" value="<%= Encoder.htmlEncode(texts[i].getRelativePath()) %>" />
<p class="controllerActions">
<input type="submit" name="<%= ControllerUtils.INPUT_SUBMIT%>" value="<%= FileIO.PARAM_SAVE%>">
<input type="submit" name="<%= ControllerUtils.INPUT_SUBMIT%>" value="download" formtarget="_blank" formmethod="post" formaction="/download">
</p>
</form>
<% } } } %>
<%@include file="/includes/defaultFooter.jsp" %>