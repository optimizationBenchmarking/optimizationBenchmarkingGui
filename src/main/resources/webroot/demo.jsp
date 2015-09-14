<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
<%@ page import="org.optimizationBenchmarking.utils.io.EOSFamily" %>
<%@ page import="org.optimizationBenchmarking.utils.tools.impl.shell.ShellTool" %>
<%@ page import="org.optimizationBenchmarking.utils.tools.impl.shell.EShellType" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />
<%@include file="/includes/defaultHeader.jsp" %>
<h1>Example Data</h1>
<p>In order to get started with out tool suite, you may consider downloading
some example data and running some example evaluation processes. Currently,
we have three data sets available, which you can download it into your
workspace right here.</p>
<% boolean canDo = true;
  switch(EOSFamily.DETECTED) {
  case Windows: { %>
<p>The computer running this GUI uses the <a href="https://en.wikipedia.org/wiki/Microsoft_Windows">Windows</a> operating system.
In order to use the automated installation of example data sets, to have <a href="https://en.wikipedia.org/wiki/Windows_PowerShell"><code>PowerShell</code></a> installed,
which should be installed by default starting at <a href="https://en.wikipedia.org/wiki/Windows_7">Windows 7</a> an higher.
If you have an older Windows (say, <a href="https://en.wikipedia.org/wiki/Windows_XP">XP</a>),
the automated download of the examples may not work.</p><%
 break; }
 case Linux: {%>
<p>The computer running this GUI uses the <a href="https://en.wikipedia.org/wiki/Linux">Linux</a>
operating. In order to use the automated installation of example data sets, it needs
to have <a href="https://en.wikipedia.org/wiki/Apache_Subversion"><code>svn</code></a> installed.</p>
 <% break; }
 case MacOS: {%>
<p>The computer running this GUI uses <a href="https://en.wikipedia.org/wiki/Mac_OS">MacOS</a>.
To be honest, I haven't tested the automated installation of examples under that OS.
We can assume that MacOS is something like 
<a href="https://en.wikipedia.org/wiki/Linux">Linux</a> and hope for the best,
but all sorts of odd things may happen. If that idea would work, you additionally need
to have <a href="https://en.wikipedia.org/wiki/Apache_Subversion"><code>svn</code></a> installed.
<em>!! Continue at your own risk !!</em></p>
 <% break; }
 default: {%>
<p>I don't know what to do with the operating system running on this computer. It seems to
not be something reasonable like <a href="https://en.wikipedia.org/wiki/Linux">Linux</a>,
<a href="https://en.wikipedia.org/wiki/Microsoft_Windows">Windows</a>, or
<a href="https://en.wikipedia.org/wiki/Mac_OS">MacOS</a>. Thus, the automated download
and installation of examples will not work.</p>
<% canDo = false; break; } }
if(canDo) {
  final EShellType shellType = ShellTool.getInstance().getType();
  if((shellType==null)||(!(ShellTool.getInstance().canUse()))) {
    canDo=false; %>
<p>Sorry, I could not detect any shell. We need a shell for running the download
script of the example data. Maybe you can get the data manually.</p><%
   } else {
   if((shellType==EShellType.DOS) && (EOSFamily.DETECTED!=EOSFamily.Windows)) {
   canDo = false; %>
<p>I detected a <code>dos</code>-type shell (as used under Windows). However, your
operating system seems to not be <a href="https://en.wikipedia.org/wiki/Microsoft_Windows">Windows</a>, but <%= EOSFamily.DETECTED%>. For this
strange situation, now suitable automated download script exists.</p><% 
    } else { 
    if((shellType==EShellType.BOURNE) && ((EOSFamily.DETECTED != EOSFamily.Linux)
                                      &&  (EOSFamily.DETECTED != EOSFamily.MacOS))) {
     canDo = false; %>
<p>I detected a <a href="https://en.wikipedia.org/wiki/Bourne_shell">Bourne</a>-type shell,
such as <a href="https://en.wikipedia.org/wiki/Bash_%28Unix_shell%29">Bash</a>. We can
support this shell under <a href="https://en.wikipedia.org/wiki/Linux">Linux</a>
and under <a href="https://en.wikipedia.org/wiki/Mac_OS">MacOS</a>,
but your operating system seems to be <%= EOSFamily.DETECTED%>. For this
strange situation, now suitable automated download script exists.</p> 
<% } } }
 if(canDo) { 
 final String currentDir = Encoder.htmlEncode(controller.getRootDir().relativize(controller.getCurrentDir()).toString());
 final String printDir   = ('/' + currentDir);
 int example = 0;
%>
<p>You can now pick an example data set to download into the current folder (<code><%=printDir%></code>).
After the download has completed, you will find two new folders: <code>results</code>
contains log files from experiments with the measured data, and <code>evaluation</code>,
which contains the configuration files for running the evaluation process. You may
choose one of the configuration files and run it (via the <code>evaluate</code> selection).
You can, of course, also edit the configuration file as well as the specification of
what evaluator modules should be run and what settings they use (in <code>evaluation.xml</code>
in the same folder).</p>
<p>Downloading the example data may take a while. During this time, your browser
will be loading the page without much feedback if your <a href="/logLevel.jsp">log level</a>
is too high. If you want to receive progress information, you may <a href="/logLevel.jsp">set it</a>
to a more sensitive level, such as <code>FINER</code>. Otherwise, you may
just be notified about success (or (hopefully not) failure).</p>
<h2>Example&nbsp;<%=(++example)%>:&nbsp;MAX-3SAT</h2> 
<p>The <a href="https://github.com/optimizationBenchmarking/optimizationBenchmarkingDocu/blob/master/examples/maxSat/README.md">MAX-3SAT</a>
example data set contains results from a few simple experiments on the, well,
<a href="http://en.wikipedia.org/wiki/MAX-3SAT">MAX-3SAT</a> problem.</p>
<p>The MAX-3SAT problem is a well-known combinatorial optimization problem, where the
goal is to find a bit string which makes a certain Boolean expression become true
(here: by minimizing the number of false clauses in said expression).</p>  
<form method="get" action="/controller.jsp">
<input type="hidden" name="<%=ControllerUtils.PARAMETER_DEMO%>" value="maxSat" />
<input type="hidden" name="<%=ControllerUtils.INPUT_CURRENT_DIR%>" value="<%=currentDir%>" />
<input type="hidden" name="<%=ControllerUtils.PARAMETER_WITH_SELECTED%>" value="<%=ControllerUtils.COMMAND_INSTALL_DEMO%>" />
Download the MAX-3SAT data into folder <code><%=printDir%></code>:&nbsp;<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%=ControllerUtils.BUTTON_OK%>" />
</form>

<h2>Example&nbsp;<%=(++example)%>:&nbsp;BBOB</h2>
<p>The <a href="https://github.com/optimizationBenchmarking/optimizationBenchmarkingDocu/blob/master/examples/bbob/README.md">BBOB</a>
example applies our software to some data gathered by other researchers: We download (some of) the
results of the <a href="http://coco.gforge.inria.fr/doku.php?id=bbob-2013">Black-Box Optimization Benchmarking (BBOB) 2013</a>,
which was collected with the
COmparing Continuous Optimisers (<a href="http://coco.gforge.inria.fr/doku.php?id=start">COCO</a>) system.</p>
<p>BBOB is an (approximately) yearly held workshop on black-box numerical optimization.
The COCO system is a great software developed by the <a href="https://www.lri.fr/projet.associe_en.php?prj=14">TAO</a>
team of the Laboratoire de Recherche en Informatique (<a href="https://www.lri.fr/">LRI</a>).
Since COCO is one of the strongest inspirations of our system, we chose to also support
their data format.</p>  
<form method="get" action="/controller.jsp">
<input type="hidden" name="<%=ControllerUtils.PARAMETER_DEMO%>" value="bbob" />
<input type="hidden" name="<%=ControllerUtils.INPUT_CURRENT_DIR%>" value="<%=currentDir%>" />
<input type="hidden" name="<%=ControllerUtils.PARAMETER_WITH_SELECTED%>" value="<%=ControllerUtils.COMMAND_INSTALL_DEMO%>" />
Download the BBOB data into folder <code><%=printDir%></code>:&nbsp;<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%=ControllerUtils.BUTTON_OK%>" />
</form>

<h2>Example&nbsp;<%=(++example)%>:&nbsp;TSP&nbsp;Suite</h2>
<p>The <a href="https://github.com/optimizationBenchmarking/tspSuite">TSP Suite</a> is a system
to benchmark and evaluate algorithms for the Traveling Salesman Problem (<a href="https://en.wikipedia.org/wiki/Travelling_salesman_problem">TSP</a>).
It is the direct predecessor of this software. We run several small experiments on
the TSP and gathered the result data in this example.</p>
<form method="get" action="/controller.jsp">
<input type="hidden" name="<%=ControllerUtils.PARAMETER_DEMO%>" value="tspSuite" />
<input type="hidden" name="<%=ControllerUtils.INPUT_CURRENT_DIR%>" value="<%=currentDir%>" />
<input type="hidden" name="<%=ControllerUtils.PARAMETER_WITH_SELECTED%>" value="<%=ControllerUtils.COMMAND_INSTALL_DEMO%>" />
Download the TSP Suite data into folder <code><%=printDir%></code>:&nbsp;<input type="submit" name="<%=ControllerUtils.INPUT_SUBMIT%>" value="<%=ControllerUtils.BUTTON_OK%>" />
</form>
<% } } %>
<%@include file="/includes/defaultFooter.jsp" %>