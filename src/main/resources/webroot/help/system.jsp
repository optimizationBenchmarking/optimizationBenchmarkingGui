<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.gui.application.ApplicationTool" %>
<%@ page import="org.optimizationBenchmarking.gui.servlets.DownloadJar" %>
<%@ page import="org.optimizationBenchmarking.utils.io.paths.PathUtils" %>
<%@ page import="java.nio.file.Path" %>
<%@ page import="org.optimizationBenchmarking.utils.config.DefinitionXMLInput" %>
<%@ page import="org.optimizationBenchmarking.utils.config.Definition" %>
<%@ page import="org.optimizationBenchmarking.utils.config.Parameter" %>
<%@ page import="org.optimizationBenchmarking.utils.text.textOutput.ITextOutput" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />
<%@include file="/includes/defaultHeader.jsp" %>
<h1>The System</h1>
<%
final String      pathName;
final Definition  definition;
final ITextOutput encoded;
encoded = Encoder.htmlEncode(out);
try(final Handle handle = controller.createJspHandle(pageContext)) {    
  final Path path = DownloadJar.getAppPath(handle);
  if(path == null) {
    pathName = ("optimizationBenchmarkingGui-" +
      ApplicationTool.getInstance().getProjectVersion() + "-full.jar");
  } else {
    pathName = PathUtils.getName(path);
  }
  definition = DefinitionXMLInput.getInstance().forClass(ApplicationTool.class, handle);
}
%>
<h2>Two Use Cases</h2>
<p>
You are using the graphical user interface (GUI) to the
optimization benchmarking tool suite. This user interface is
designed as a stand-alone web application, which can be used
in two ways:</p>
<ol>
<li>As local, stand-alone application: You start the program locally, a browser opens,
and you do your stuff.</li>
<li>As client-server application: If you have much data, the process of evaluating
it may be resource-hungry and take quite some time. Thus, it make sense to
run the system on a strong server. You can access the user interface from
your PC or laptop and start evaluation process there (as well as upload or download data).
This also makes sense if you work in a research group and want to gather all your
experimental data in one central location (which could also have a suitable backup
policy defined).</li>
</ol>
<p>In both cases, the GUI will behave exactly the same.</p>

<h2>Command Line Arguments</h2>
<p>You can start the system by typing <code>java -jar <%encoded.append(pathName);%></code> on
the command line. To this command, you can add the following parameters
(in the form of <code>parameter=value</code>).</p>
<% if(definition!=null) { boolean first = true;%>
<table class="invisibleL" style="vertical-align:baseline;margin-top:1em;margin-bottom:1em"><% for(Parameter<?> param: definition) {
 if(first) { first = false; } else { %><tr class="configSpacer"><td class="configSpacer" colspan="2"></td></tr><%}%>
<tr class="invisibleL"><th class="invisibleL" style="vertical-align:baseline"><code><%encoded.append(param.getName());%></code>&nbsp;</th>
<td class="invisibleL" style="vertical-align:baseline"><%encoded.append(param.getDescription());%>
<br/>Type:&nbsp;<code><%encoded.append(param.getParser().getOutputClass().getSimpleName());%></code>
<br/>Default value:&nbsp;<code><%encoded.append(param.getDefault());%></code></td>
<%}%></table>
<%}%>
<p><a href="/this">Here</a> you can download a copy of the <code>jar</code> of
this software you are using right now, but it is recommended to
download the most recent <a href="https://github.com/optimizationBenchmarking/optimizationBenchmarkingGui/releases">release</a>.</p>

<%@include file="/includes/defaultFooter.jsp" %>