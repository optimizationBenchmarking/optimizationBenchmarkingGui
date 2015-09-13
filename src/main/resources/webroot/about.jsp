<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.gui.application.ApplicationTool" %>
<%@ page import="org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.Evaluator" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />
<h1>About</h1>  
<p>
This is the graphical user interface (<a href="https://github.com/optimizationBenchmarking/optimizationBenchmarkingGui">GUI</a>) of
the <a href="https://github.com/optimizationBenchmarking/optimizationBenchmarking">optimizationBenchmarking</a> core project.   
</p>
<h2>Version</h2>
<p>This software has the following components:</p>
<table class="invisibleL">
<tr class="invisibleL"><td colspan="2" class="invisibleL"><h3>GUI</h3></td></tr>
<% ApplicationTool app = ApplicationTool.getInstance(); %>
<tr class="invisibleL"><th class="invisibleL">name:&nbsp;</th><td class="invisibleL"><%= app.getProjectName()%></td></tr>
<tr class="invisibleL"><th class="invisibleL">version:&nbsp;</th><td class="invisibleL"><%= app.getProjectVersion()%></td></tr>
<tr class="invisibleL"><th class="invisibleL">required JDK:&nbsp;</th><td class="invisibleL"><%= app.getProjectJDK()%></td></tr>
<tr class="invisibleL"><th class="invisibleL">url:&nbsp;</th><td class="invisibleL"><a href="<%= app.getProjectURL()%>"><%= app.getProjectURL()%></a></td></tr>
<tr class="invisibleL"><th class="invisibleL">contact&nbsp;name:&nbsp;</th><td class="invisibleL"><%= app.getContactName()%></a></td></tr>
<tr class="invisibleL"><th class="invisibleL">contact&nbsp;url:&nbsp;</th><td class="invisibleL"><a href="<%= app.getContactURL()%>"><%= app.getContactURL()%></a></td></tr>
<tr class="invisibleL"><th class="invisibleL">contact&nbsp;email:&nbsp;</th><td class="invisibleL"><a href="mailto:<%= app.getContactEmail()%>"><%= app.getContactEmail()%></a></td></tr>
<tr class="invisibleL"><td colspan="2" class="invisibleL"><h3>Evaluator Core</h3></td></tr>
<% Evaluator ev = Evaluator.getInstance(); %>
<tr class="invisibleL"><th class="invisibleL">name:&nbsp;</th><td class="invisibleL"><%= ev.getProjectName()%></td></tr>
<tr class="invisibleL"><th class="invisibleL">version:&nbsp;</th><td class="invisibleL"><%= ev.getProjectVersion()%></td></tr>
<tr class="invisibleL"><th class="invisibleL">required JDK:&nbsp;</th><td class="invisibleL"><%= ev.getProjectJDK()%></td></tr>
<tr class="invisibleL"><th class="invisibleL">url:&nbsp;</th><td class="invisibleL"><a href="<%= ev.getProjectURL()%>"><%= ev.getProjectURL()%></a></td></tr>
<tr class="invisibleL"><th class="invisibleL">contact&nbsp;name:&nbsp;</th><td class="invisibleL"><%= ev.getContactName()%></a></td></tr>
<tr class="invisibleL"><th class="invisibleL">contact&nbsp;url:&nbsp;</th><td class="invisibleL"><a href="<%= ev.getContactURL()%>"><%= ev.getContactURL()%></a></td></tr>
<tr class="invisibleL"><th class="invisibleL">contact&nbsp;email:&nbsp;</th><td class="invisibleL"><a href="mailto:<%= ev.getContactEmail()%>"><%= ev.getContactEmail()%></a></td></tr>
</table>
<p><a href="/this">Here</a> you can download a copy of the <code>jar</code> of
this software you are using right now, but it is recommended to
download the most recent <a href="https://github.com/optimizationBenchmarking/optimizationBenchmarkingGui/releases">release</a>.</p> 
<h2>Web</h2>
<p>This project is developed by the <a href="http://www.optimizationbenchmarking.org">optimizationBenchmarking</a>
initiative. Our website can be found at <a href="http://www.optimizationbenchmarking.org">http://www.optimizationbenchmarking.org</a>
and <a href="http://www.github.com/optimizationBenchmarking">http://www.github.com/optimizationBenchmarking</a>.
</p><p>The specific website for this software project is <a href="http://optimizationbenchmarking.github.io/optimizationBenchmarking/">http://optimizationbenchmarking.github.io/optimizationBenchmarking/</a>.</p>  
<p>Our software is GPL-licensed open source, and can be found at <a href="http://www.github.com">GitHub</a> under the following projects:</p>
<ul>
<li><a href="https://github.com/optimizationBenchmarking/optimizationBenchmarking">core command-line argument</a></li>
<li><a href="https://github.com/optimizationBenchmarking/optimizationBenchmarkingGui">this GUI</a></li>
<li><a href="https://github.com/optimizationBenchmarking/optimizationBenchmarkingDocu">documentation</a></li>
<li>predecessor project: <a href="https://github.com/optimizationBenchmarking/tspSuite">tspSuite</a></li>
</ul>
<h2>Support and Funding</h2>
<p>The main work in this project is conducted by Dr. Thomas Weise at the
University of Science and Technology of China with support from the
Fundamental Research Funds for the Central Universities. Further
support is provided by the Faculty Strategic Initiatives Research Fund (SIRF) of the
University of Newcastle and Dr. Raymond Chiong.</p>
<h2 id="contact">Contact</h2>
<table class="invisibleL">
<tr class="invisibleL"><th class="invisibleL" colspan="2">Dr. Thomas Weise</th></tr>
<tr class="invisibleL"><td class="invisibleL" colspan="2">Nature Inspired Computation and Applications Laboratory (NICAL)</td></tr>
<tr class="invisibleL"><td class="invisibleL" colspan="2">USTC-Birmingham Joint Research Institute in Intelligent Computation and Its Applications (<a href="http://ubri.ustc.edu.cn">UBRI</a>)</td></tr>
<tr class="invisibleL"><td class="invisibleL" colspan="2">School of Computer Science and Technology (<a href="http://cs.ustc.edu.cn/">SCST</a>)</td></tr>
<tr class="invisibleL"><td class="invisibleL" colspan="2">University of Science and Technology of China (<a href="http://www.ustc.edu.cn">USTC</a>)</td></tr>
<tr class="invisibleL"><td class="invisibleL" colspan="2">West Campus, Science and Technology Building, West Wing, Room 601</td></tr>
<tr class="invisibleL"><td class="invisibleL" colspan="2">Huangshan Road/Feixi Road, Hefei 230027, Anhui, China</td></tr>
<tr class="invisibleL"><td class="invisibleL">Web:</td><td class="invisibleL">&nbsp;<a href="http://www.it-weise.de/">http://www.it-weise.de/</a></td></tr>
<tr class="invisibleL"><td class="invisibleL">Email:</td><td class="invisibleL">&nbsp;<a href="mailto:tweise@gmx.de">tweise@gmx.de</a>, <a href="mailto:tweise@ustc.edu.cn">tweise@ustc.edu.cn</a></td></tr>
</table>
<%@include file="/includes/defaultFooter.jsp" %>