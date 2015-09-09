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
</p><p>
Please notice that a benchmark instance must have at least one feature. This makes sense,
because in reality,
there cannot be a benchmark instance which has not at least one property, like
<code>scale</code>, <code>(a)symmetry</code>, the
<code>number of variables</code>, the <code>number of customers</code>,
the <code>number of clauses</code>, whatever.
Usually, you will have multiple benchmark instances with different values of
the same feature: Would, e.g., have three benchmark instances whose
<code>number of variables</code> could be
<code>10</code>, <code>100</code>, and <code>1000</code>, respectively.
Our system then can use these feature values to analyze what, well,
features a problem hard for a given algorithm.
</p><p>
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