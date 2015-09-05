<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Page" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.modules.config.ConfigIO" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.files.Loaded" %>
<%@ page import="org.optimizationBenchmarking.utils.config.Dump" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />

<h1>Edit Configuration Files</h1>
<p>On this page, you can edit configuration files. A configuration file tells the
evaluator from where and how to load the data, where to write the output, which
output format to use, and where the list of things to do is. 
You can save your changes by pressing the &quot;Save&quot; button.
If you leave this page any un-saved changes will be discarded.</p>
<%
Loaded<Dump> dump = null;  
try(final Handle handle = controller.createJspHandle(pageContext)) {    
  dump = ConfigIO.INSTANCE.executeAndLoad(request, handle);
}
if(dump != null) {
%>
<h2>File &quot;<%= Encoder.htmlEncode(dump.getName()) %>&quot;</h2>
<form class="invisible" action="/configEdit.jsp" method="post">
<%
try(final Page hpage = new Page(pageContext)) {
  final String prefix = hpage.newPrefix();
  ConfigIO.INSTANCE.formPutEditorFields(prefix, dump.getLoaded(), hpage);
  ConfigIO.INSTANCE.formFinalize(prefix, dump, hpage);
}
%>
</form>
<% } %>
<%@include file="/includes/defaultFooter.jsp" %>