<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />
<%@include file="/includes/defaultHeader.jsp" %>
<h1>Welcome</h1>  
<p>
This is the graphical user interface of the <em>optimizationBenchmarking.org</em>
evaluator. This software can support you in analyzing experimental results in
the fields of optimization and machine learning. 
</p>
<p>
On the right-hand side, you can find the menu. If you are not sure yet
what to do, you may go to the <a href="/help.jsp">help page</a>
and read about how to get started.</p>
<p>
Otherwise, you should now go to the <a href="./controller.jsp">control center</a>.
</p>
<p>The system has been started with <code><%= Encoder.htmlEncode(controller.getRootDir().toString())%></code> as root folder &ndash; no
files outside this folder can be accessed in the <a href="./controller.jsp">control center</a>. See the <a href="/help/system.jsp#arguments">help</a> page
for information about the command line arguments of this system.</p>
<p><em>Warning:</em> This system does not provide any user management or security features.
<em><span style="color:red">You must never make it available in the internet, i.e., do not
run it on a computer which is accessible from the &quot;outside&quot;.</span></em></p> 
<%@include file="/includes/defaultFooter.jsp" %>