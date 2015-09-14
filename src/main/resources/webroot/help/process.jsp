<%@ page import="org.optimizationBenchmarking.gui.utils.files.EFSElementType" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.utils.text.textOutput.ITextOutput" %>
<%@ page import="org.optimizationBenchmarking.experimentation.evaluation.impl.EvaluationModuleDescriptions" %>
<%@ page import="org.optimizationBenchmarking.experimentation.evaluation.impl.evaluator.data.ModuleDescriptions" %>
<%@ page import="org.optimizationBenchmarking.utils.text.ESequenceMode" %>
<%@ page import="org.optimizationBenchmarking.utils.text.numbers.InTextNumberAppender" %>
<%@ page import="org.optimizationBenchmarking.utils.text.ETextCase" %>
<%@include file="/includes/defaultHeader.jsp" %>
<h1>The Experimentation and Evaluation Process</h1>

<p>The optimization benchmarking system is a versatile tool to analyze and
compare the performance of optimization or machine learning algorithms.
Matter of fact, you can use it for <em>arbitrary</em> algorithms and
<em>arbitrary</em> problems. The goal is to provide researchers
with strong support to analyze their data, while putting as little
restrictions as possible on what algorithms they investigate,
on which problems they test them, and how they run their experiments.</p>
<p>Below, we describe the benchmarking and evaluation procedure
we prescribe. It consists mainly of providing meta-information
about what you have <a href="#dimensions">measured</a>, what
<a href="#instances">features</a> the problems have to which you
applied your algorithm, what <a href="#experiment">parameter settings</a>
your algorithm has in the experiments, <a href="#evaluation">what</a>
information you want to get, and in which <a href="#config">format</a>
you want to obtain it.</p>
<p><em>There are no restrictions on how you implement your algorithm
or what programming language or system you use.</em></p>

<% int h2=0;
   final ITextOutput encoded = Encoder.htmlEncode(out); %>
<h2 id="dimensions"><%=(++h2)%>.&nbsp;Measurement Dimensions&nbsp;<% EFSElementType.EDI_DIMENSIONS.putIcon(out, encoded); %></h2>
<p>The first thing we need to think about when doing experiments is
what we can measure. In optimization, there are two basic things
of interest: The solution quality we can get and the runtime we
need to get it. These are so-called <em>dimensions</em>.</p>
<p>You can define arbitrarily many dimensions for your experiments.
You can have, for example, two time dimensions (maybe one for
counting the objective function evaluations [FEs] and one for
measuring the actual CPU time in ms) and one solution quality
dimension, if you want.</p>
<p>Of course, maybe you already have experimental data lying
around. Then you can just specify the dimensions later, so that
they fit to your data.</p>
<p id="logFormat">Either way, when you run your experiments,
your optimization processes should create log files which fit to the
dimensions you have specified. In the above example, each run of your
algorithm should produce a text file where each line holds three
numbers (separated by space or tab): the number of FEs, the elapsed
CPU time, and the best obtained solution quality.</p>
<p>The dimensions that you measure are specified in a &quot;dimensions&quot; file,
usually called <code>dimensions.xml</code> or <code>dimensions.edi</code>.
You can recognize such files by their icon <% EFSElementType.EDI_DIMENSIONS.putIcon(out, encoded); %>
and edit or create them via simple forms in the <a href="/controller.jsp">controller</a>.
</p><p>Some data formats, such as <code>tspSuite</code> and <code>bbob</code>
do not require you to specify the dimensions, as they are designed for one
specific, narrow problem domain, where the dimensions are known. In the
general case, however, you need to provide a dimensions file.</p> 

<h2 id="instances"><%=(++h2)%>.&nbsp;Benchmark/Problem Instances&nbsp;<% EFSElementType.EDI_INSTANCES.putIcon(out, encoded); %></h2>
<p>OK, so you have chosen what you want to measure.
Now you apply your algorithm to some problem instances for benchmarking.
Obviously, each such instance needs to have a name, and your algorithms
would put the log files obtained from run on an instance &quot;A&quot; into
a folder named &quot;A&quot; as well.</p>
<p>But you can tell our system more than just an instance name. Normally,
you will apply your algorithm to several problem instances to find out
whether it is good or not. Again, obviously, these instances must be
different in some aspect (otherwise using multiple would make no sense).
We call such aspects <em>features</em>. A feature of a benchmark instance
for the Traveling Salesman Problem (<a href="https://en.wikipedia.org/wiki/Travelling_salesman_problem">TSP</a>)
could be, for example, the number of cities the salesman has to visit and
whether distances are symmetric. Each benchmark instance may have a different
value for each feature.</p><p>
The reason why you would want to specify the features of your benchmark
problem instances is that we can use them during the evaluation process:
In the above example, we could group the instances according to the
numbers of cities they specify and get an impression how the benchmarked
algorithms behave when this number increases.</p>
<p>You can specify the names of the benchmark instances you use, as well as
arbitrarily many features in an &quot;instances&quot; file,
usually called <code>instances.xml</code> or <code>instances.edi</code>.
You can recognize such files by their icon <% EFSElementType.EDI_INSTANCES.putIcon(out, encoded); %>
and edit or create them via simple forms in the <a href="/controller.jsp">controller</a>.
</p>
<p>Of course, you can also do this step after you have run your
experiments, meaning existing result data can be used by our system.</p>
<p>Some data formats, such as <code>tspSuite</code> and <code>bbob</code>
do not require you to specify the instances, as they are designed for one
specific, narrow problem domain where the instances and their features are
known. In the general case, however, you need to
provide a dimensions file.</p>

<h2 id="experiment"><%=(++h2)%>.&nbsp;Experiments&nbsp;<% EFSElementType.EDI_EXPERIMENT.putIcon(out, encoded); %></h2>
<p>You now can run your experiments, where <em>experiment</em> means
&quot;conduct multiple independent runs of your algorithm for each benchmark instance&quot;.
For this you use your own software, your
algorithm implementation. This is not directly related to our software: You
can use whatever programming language you want and do this in whatever way
you like, locally, on a cluster, in the cloud, whatever. The only restriction
is that it must create log files in the format corresponding to your
<a href="#logFormat">dimension definition</a>. You should create
one separate folder for each experiment, which then holds the folders
for the instances, which then contain one log file for each independent
run.</p>
<p>Usually, you may want to run more than one experiment. The reason is
that you usually want to compare your algorithm to another one (so you
must also have data for the other one) or want to compare multiple
different setups of your algorithm to see which one works best.
Therefore, you need to tell our system how these experiments are different,
by providing their name, the name of used algorithms, as well as
the parameter settings of these algorithms.</p>
<p>Therefore, for each experiment, you put an &quot;experiment&quot; file into the
folder of the experiment. This file is usually called
<code>experiment.xml</code> or <code>experiment.edi</code>. These files
contain the above information: short experiment name and parameter settings
(the algorithm used is considered as parameter as well).
You can recognize such files by their icon <% EFSElementType.EDI_EXPERIMENT.putIcon(out, encoded); %>
and edit or create them via simple forms in the <a href="/controller.jsp">controller</a>.
</p>
<p>And again, of course, you can also do this step after you have run your
experiments, meaning existing result data can be used by our system.</p>
<p>Some data formats, such as <code>tspSuite</code> and <code>bbob</code>
do not require you to specify the experiment parameters, as they can
already be obtained from other sources (<code>tspSuite</code>) or are
not part of the file format and cannot be specified (<code>bbob</code>). In
the general case, however, you need to provide the experiment file.</p>

<h2 id="evaluation"><%=(++h2)%>.&nbsp;Evaluation Specification&nbsp;<% EFSElementType.EVALUATION.putIcon(out, encoded); %></h2>
<p>OK, now we got all the data from the experiments (the log files), as well
as the meta-data describing their meaning (<a href="#dimensions">what</a>
we measured, what the <a href="#instances">input</a> of our experiments was,
and <a href="#experiment">how</a> our algorithm was configured). We now
want to evaluate the results, i.e., extract high-level information and
conclusions from the raw data.</p>
<p>For this purpose, we apply our <em>evaluator</em>. The evaluator
can analyze several different aspects of your data and your algorithm's
performance. Each such aspect is encapsulated in a <em>module</em>. <%
final ModuleDescriptions descs;
final int modCount;
descs = EvaluationModuleDescriptions.getDescriptions(null);
if(descs != null) {
  modCount = descs.size();
%>Currently, there are <%
InTextNumberAppender.INSTANCE.appendTo(modCount, ETextCase.IN_SENTENCE, encoded);
%> different modules, namely <%
ESequenceMode.AND.appendSequence(ETextCase.IN_SENTENCE, descs, true, encoded); %>.<%
} else {
  modCount = 0;
}
%></p>
<p>Each module, in turn, may have configuration parameters. If a module
computes statistics over a certain <a href="#dimensions">dimension</a>, it may have a parameter allowing
you to choose whether you want the arithmetic mean, the median, or the standard
deviation. If a module plots a graphic,  may have a parameter for the designated size
of the graphic. If the graphic is actually a diagram, there may be additional
parameters allowing you to specify axis ranges. Or you may able to specify
that the diagram should be drawn several times, for problem <a href="#instances">instances</a> belonging
to different classes (based on their features, e.g., you may plot the
average solution quality for TSP instances with few cities, a medium amount of cities,
and many cities). And so on.</p>
<p>
In an <em>evaluation</em> file, you specify which modules to apply to the data and
how they should be configured. Of course, you can apply the same module multiple
times with different configuration to a setup.
Evaluation files are usually called <code>evaluation.xml</code> and
you can recognize such files by their icon <% EFSElementType.EVALUATION.putIcon(out, encoded); %>
and edit or create them via simple forms in the <a href="/controller.jsp">controller</a>.
</p>

<h2 id="config"><%=(++h2)%>.&nbsp;Evaluator Configuration&nbsp;<% EFSElementType.CONFIGURATION.putIcon(out, encoded); %></h2>
<p>The final piece of information you need to provide is the evaluator configuration.
You have already obtain the (log-)data from your experiments,
specified all the meta-information about what is what, as well as what you want to do
with the data, now we need to tell the system where it can find all of that and
what format the output should be.</p>
<p>In a <em>configuration</em> file, you do just that. You tell the system "OK,
here is the <a href="#evaluation">evaluation</a> file, in these folders you
can find the <a href="#experiment">experiment</a> files, log data,
<a href="#instances">instances</a> file, and the <a href="#dimensions">dimensions</a> file.
And you choose where the output should be stored and what output format you want: Do you want your output
to be an article written in <a href="https://en.wikipedia.org/wiki/LaTeX">LaTeX</a> with the
<a href="http://ctan.org/pkg/ieeetran"><code>IEEEtran</code></a> document class and
<a href="https://en.wikipedia.org/wiki/Portable_Document_Format"><code>pdf</code></a> figures? Or should the figures also be coded as LaTeX? Or as
<a href="https://en.wikipedia.org/wiki/Encapsulated_PostScript"><code>eps</code></a>? Or do you prefer Springer's
<a href="www.springer.com/computer/lncs/lncs+authors?SGWID=0-40209-0-0-0"><code>LLNCS</code></a> document class? Or maybe
want <a href="https://en.wikipedia.org/wiki/XHTML"><code>XHTML</code></a> output?
Or maybe just the raw evaluation result data for export to other applications
such as <a href="https://en.wikipedia.org/wiki/Gnuplot">Gnuplot</a> or
<a href="https://en.wikipedia.org/wiki/MATLAB">MATLAB</a>? This you can choose here.</p>
<p>
The reason why this information is not in the evaluation files is that, sometimes,
you may want to generate different outputs for the same evaluation. Or you may want
to use the same evaluation file for different experiments on the same set of problems.
A typical use case is that maybe you have 24 experiments. Plotting a diagram with 20 experiments could look
ugly, so maybe you divide them into several groups, say, each consisting of 8
experiments. Then you can use the same evaluation but different configuration files
for each of them.</p>
<p>Configuration files are usually called <code>configuration.xml</code> or
<code>configForXXX.xml</code> (where <code>XXX</code> could be a mnemonic for whatever
your output format is, such as <code>IEEEtran</code>).
You can recognize such files by their icon <% EFSElementType.CONFIGURATION.putIcon(out, encoded); %>
and edit or create them via simple forms in the <a href="/controller.jsp">controller</a>.
</p>

<h2 id="run"><%=(++h2)%>.&nbsp;Run the Evaluator</h2>
<p>Now you are basically done. You just need to select a configuration file in
the <a href="/controller.jsp">controller</a>, select &quot;evaluate&quot; as
action, and press <code>OK</code>. The evaluation process will start by loading
the data, evaluating it, and writing the output. This may take some time. If
you <a href="/logLevel.jsp">set</a> a low <a href="/logLevel.jsp">log level</a>, such as
<code>FINEST</code>, you will get lots of information about what the evaluator
is doing. If you <a href="/logLevel.jsp">set</a> a high log level, such as
<code>INFO</code>, you may only receive few status information updates about
the process. Either way, please just wait patiently and let the software
do its job. Once it is done, the generated output files will be shown to you
at the bottom of the web page.</p> 

<%@include file="/includes/defaultFooter.jsp" %>