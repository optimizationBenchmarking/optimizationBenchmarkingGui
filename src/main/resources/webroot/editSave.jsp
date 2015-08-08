<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.TextFileIO" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />

<h1>Save Plain Text File</h1>
<%
try(final Handle handle = controller.createJspHandle(pageContext)) {
  final String submit = request.getParameter(ControllerUtils.INPUT_SUBMIT);
  if(submit.equalsIgnoreCase("Save")) {
    final String path = request.getParameter("path");
    if(path != null) {
      TextFileIO.store(path, handle, request.getParameter("contents"));
    } else {
      handle.failure("No path provided.");
    }    
  } else {
    handle.unknownSubmit(submit);
  }
} %>
<form class="invisible">
<p class="controllerActions">
<input type="button" value="Back to Editor" onclick="self.close()" />
</p>
</form>
<%@include file="/includes/defaultFooter.jsp" %>