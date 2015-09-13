<%@include file="/includes/defaultHeader.jsp" %>
<h1>Help</h1>  
<p>
I, your optimization benchmarking evaluator system, bid you a warm
<em>welcome</em>! I hope that I can be of assistance during your journey
in the field of optimization or machine learning, your hunt for the algorithms
that solve your problem best. Let us become friends and share the workload:
You do the algorithm development and run the experiments.
I help you to understand how your algorithms perform and compare to other
methods (and may be able to provide you with some data and figures for
your next paper).
</p>
<h2>What this system is</h2>
<p>I, basically, can read your experimental
results (in form of raw log data) and extract high-level information from it.
Often, when we design new algorithms in the fields of optimization or machine learning,
statistically soundly evaluating experimental results can take
much time, often as same as much as inventing the algorithms. Usually, we have
lots of data which we need to extract from text-based log files, copy into some
application MATLAB, draw diagrams, understand them, then export the diagrams in
a useful format so that we can import them in a paper draft. I want to help
you with all of that: I can load the data for you. I draw the diagrams for you.
In a format that you can import in LaTeX. Actually, I write whole reports for
you, including detailed analysis, descriptions, and figures, which you can
directly re-use in your publications.  
</p>
<h2>Getting Started</h2>
<p>
If you are new to this system, the I suggest to do the following:
</p>
<ol>
<li>Use <a href="/controller.jsp">control center</a> to create a new, empty folder
(by <code>cd</code>-ing into it). Let's call this folder <code>x</code>.</li>
<li>Then use the <a href="/demo.jsp">examples</a> page to download one of the
example data sets.</li>
<li>Use again the <a href="/controller.jsp">control center</a> to enter folder <code>x/evaluation</code>.
Select one of the files named <code>configFor...xml</code> (by checking the box to the right of it).
Then choose the <code>evaluate</code> action to run the evaluator for the example data set.</li>
<li>You will now find a report in the folder <code>x/reports</code>. This is what I can do for you.</li>
<li>Now you may want to better understand how I work. For this purpose, you can read
the <a href="/help/process.jsp">process help page</a>.</li>
<li>Now you should be able to work with me ^_^.</li>
<li>If you have any further question or request, you can <a href="/about.jsp#contact">contact</a>
Dr. <a href="mailto:tweise@ustc.edu.cn">Thomas Weise</a>, the lead developer of this software, at any time via email.</li>
</ol>
<%@include file="/includes/defaultFooter.jsp" %>