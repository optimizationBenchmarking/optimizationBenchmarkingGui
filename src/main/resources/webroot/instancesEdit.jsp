<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Page" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.modules.instances.InstancesIO" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.files.Loaded" %>
<%@ page import="org.optimizationBenchmarking.experimentation.data.spec.IInstanceSet" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />

<h1>Edit Instances Files</h1>
<p>On this page, you can edit instances files. A instances file specifies
the names and features of benchmark problem instances to which you apply your algorithm.
You can save your changes by pressing the &quot;Save&quot; button.
If you leave this page any un-saved changes will be discarded.</p>
<%
Loaded<IInstanceSet> instances = null;  
try(final Handle handle = controller.createJspHandle(pageContext)) {    
  instances = InstancesIO.INSTANCE.executeAndLoad(request, handle);
}
if(instances != null) { %>
<h2>File &quot;<%= Encoder.htmlEncode(instances.getName()) %>&quot;</h2>
<% 
try(final Page hpage = new Page(pageContext)) {
  InstancesIO.INSTANCE.formPutComponentButtonHelp(hpage);
%>
<form class="invisible" action="/instancesEdit.jsp" method="post">
<%
  final String prefix = hpage.newPrefix();
  InstancesIO.INSTANCE.formPutEditorFields(prefix, instances.getLoaded(), hpage);
  InstancesIO.INSTANCE.formFinalize(prefix, instances, hpage);
%>
</form>
<% } } %>
<%@include file="/includes/defaultFooter.jsp" %>