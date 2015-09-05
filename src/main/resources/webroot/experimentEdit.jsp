<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Page" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.modules.experiments.ExperimentIO" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.files.Loaded" %>
<%@ page import="org.optimizationBenchmarking.experimentation.data.spec.IExperiment" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />

<h1>Edit Experiment Files</h1>
<p>On this page, you can edit experiment files. An experiment file specifies
the parameters and description of an experiment. An experiment corresponds to
one specific algorithm setup applied to the benchmark experiment.</p>
<%
Loaded<IExperiment> experiment = null;  
try(final Handle handle = controller.createJspHandle(pageContext)) {    
  experiment = ExperimentIO.INSTANCE.executeAndLoad(request, handle);
}
if(experiment != null) { %>
<h2>File &quot;<%= Encoder.htmlEncode(experiment.getName()) %>&quot;</h2>
<% 
try(final Page hpage = new Page(pageContext)) {
  ExperimentIO.INSTANCE.formPutComponentButtonHelp(hpage);
%>
<form class="invisible" action="/experimentEdit.jsp" method="post">
<%
  final String prefix = hpage.newPrefix();
  ExperimentIO.INSTANCE.formPutEditorFields(prefix, experiment.getLoaded(), hpage);
  ExperimentIO.INSTANCE.formFinalize(prefix, experiment, hpage);
%>
</form>
<% } } %>
<%@include file="/includes/defaultFooter.jsp" %>