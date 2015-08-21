<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.FileIO" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Loaded" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />

<h1>Edit as Plain Text</h1>

<p>On this page, you can edit files as plain text. No syntax validation or other verification
will be applied to the files. You can save your changes by pressing the &quot;Save&quot; button.
If you leave this page any un-saved changes will be discarded.</p>
<p>For convenience and syntax highlighting, you may consider copying the contents to an
editor of your choice, then copying the edited document back here and saving it.</p>
<%
final String         submit   = request.getParameter(ControllerUtils.INPUT_SUBMIT);
      Loaded<String> texts    = null;
  
try(final Handle handle = controller.createJspHandle(pageContext)) {    
  if(ControllerUtils.BUTTON_OK.equalsIgnoreCase(submit)) { 
    texts = FileIO.load(null, request.getParameterValues(ControllerUtils.PARAMETER_SELECTION), handle);
  } else {
    if(ControllerUtils.COMMAND_NEW_FILE.equalsIgnoreCase(submit)) {
      texts = FileIO.load(request.getParameter(ControllerUtils.INPUT_CURRENT_DIR),
                          new String[]{ request.getParameter(ControllerUtils.PARAMETER_CD_PATH) }, handle);
    } else {
      if(FileIO.PARAM_SAVE.equalsIgnoreCase(submit)) {
        String path = request.getParameter(ControllerUtils.PARAMETER_SELECTION);
        FileIO.store(path, handle, request.getParameter("contents"));
        texts = FileIO.load(null, new String[] { path }, handle);
      } else {
        handle.unknownSubmit(submit);
      }
    }    
  }
}
if(texts != null) {
%>
<h2>File &quot;<%= Encoder.htmlEncode(texts.getName()) %>&quot;</h2>
<form class="invisible" action="/textEdit.jsp" method="post">
<textarea class="editor" rows="25" cols="70" name="contents" wrap="off" autofocus><%= texts.getLoaded() %></textarea>
<input type="hidden" name="<%= ControllerUtils.PARAMETER_SELECTION%>" value="<%= Encoder.htmlEncode(texts.getRelativePath()) %>" />
<p class="controllerActions">
<input type="submit" name="<%= ControllerUtils.INPUT_SUBMIT%>" value="<%= FileIO.PARAM_SAVE%>">
<input type="submit" name="<%= ControllerUtils.INPUT_SUBMIT%>" value="download" formtarget="_blank" formmethod="post" formaction="/download">
</p>
</form>
<% } %>
<%@include file="/includes/defaultFooter.jsp" %>