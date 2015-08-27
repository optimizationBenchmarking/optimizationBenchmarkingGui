<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Page" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.modules.evaluation.EvaluationIO" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Loaded" %>
<%@ page import="org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.EvaluationModules" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />

<h1>Edit Evaluation Files</h1>
<p>On this page, you can edit evaluation files. An evaluation file tells the
evaluator what to do with the data it has loaded. 
You can save your changes by pressing the &quot;Save&quot; button.
If you leave this page any un-saved changes will be discarded.</p>
<%
Loaded<EvaluationModules> modules = null;  
try(final Handle handle = controller.createJspHandle(pageContext)) {    
  modules = EvaluationIO.INSTANCE.executeAndLoad(request, handle);
}
if(modules != null) {%>
<h2>File &quot;<%= Encoder.htmlEncode(modules.getName()) %>&quot;</h2>
<form class="invisible" action="/evaluationEdit.jsp" method="post">
<%
try(final Page hpage = new Page(pageContext)) {
  final String prefix = hpage.newPrefix();
  EvaluationIO.INSTANCE.formPutEditorFields(prefix, modules.getLoaded(), hpage);
  EvaluationIO.INSTANCE.formFinalize(prefix, modules, hpage);
}
%>
</form>
<% } %>
<%@include file="/includes/defaultFooter.jsp" %>