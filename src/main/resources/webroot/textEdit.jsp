<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Page" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.modules.TextIO" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Loaded" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />

<h1>Edit as Plain Text</h1>

<p>On this page, you can edit files as plain text. No syntax validation or other verification
will be applied to the files. You can save your changes by pressing the &quot;Save&quot; button.
If you leave this page any un-saved changes will be discarded.</p>
<p>For convenience and syntax highlighting, you may consider copying the contents to an
editor of your choice, then copying the edited document back here and saving it.</p>
<%
Loaded<String> text = null;  
try(final Handle handle = controller.createJspHandle(pageContext)) {    
  text = TextIO.INSTANCE.executeAndLoad(request, handle);
}
if(text != null) {
%>
<h2>File &quot;<%= Encoder.htmlEncode(text.getName()) %>&quot;</h2>
<form class="invisible" action="/textEdit.jsp" method="post">
<%
try(final Page hpage = new Page(pageContext)) {
  final String prefix = hpage.newPrefix();
  TextIO.INSTANCE.formPutEditorFields(prefix, text.getLoaded(), hpage);
  TextIO.INSTANCE.formFinalize(prefix, text, hpage);
}
%>
</form>
<% } %>
<%@include file="/includes/defaultFooter.jsp" %>