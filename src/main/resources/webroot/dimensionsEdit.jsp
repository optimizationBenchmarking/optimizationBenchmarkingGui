<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Page" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.modules.dimensions.DimensionsIO" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Loaded" %>
<%@ page import="org.optimizationBenchmarking.experimentation.data.spec.IDimensionSet" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />

<h1>Edit Dimensions  Files</h1>
<p>On this page, you can edit dimensions files. A dimensions file specifies
which measurements are stored in your log files. For example, you could
have log points containing a number of passed (objective) function
evaluations and a corresponding solution quality (objective value).
Then you would specify these two dimensions in the dimensions file,
along with some properties, such as that the time measured in 
function evaluations is increasing.
You can save your changes by pressing the &quot;Save&quot; button.
If you leave this page any un-saved changes will be discarded.</p>
<%
Loaded<IDimensionSet> dimensions = null;  
try(final Handle handle = controller.createJspHandle(pageContext)) {    
  dimensions = DimensionsIO.INSTANCE.executeAndLoad(request, handle);
}
if(dimensions != null) { %>
<h2>File &quot;<%= Encoder.htmlEncode(dimensions.getName()) %>&quot;</h2>
<% 
try(final Page hpage = new Page(pageContext)) {
  DimensionsIO.INSTANCE.formPutComponentButtonHelp(hpage); %>
%>
<form class="invisible" action="/dimensionsEdit.jsp" method="post">
<%
  final String prefix = hpage.newPrefix();
  DimensionsIO.INSTANCE.formPutEditorFields(prefix, dimensions.getLoaded(), hpage);
  DimensionsIO.INSTANCE.formFinalize(prefix, dimensions, hpage);
%>
</form>
<% } } %>
<%@include file="/includes/defaultFooter.jsp" %>