<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.ConfigIO" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />
<h1>Save Configuration File</h1>
<%
try(final Handle handle = controller.createJspHandle(pageContext)) {
  ConfigIO.store(handle, request);  
} %>
<form class="invisible">
<p class="controllerActions">
<input type="button" value="Back to Editor" onclick="self.close()" />
</p>
</form>
<%@include file="/includes/defaultFooter.jsp" %>