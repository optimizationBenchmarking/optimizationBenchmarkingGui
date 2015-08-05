<%@include file="/includes/defaultHeader.jsp" %>
<%@ page import="org.optimizationBenchmarking.utils.text.TextUtils" %>
<%@ page import="org.optimizationBenchmarking.gui.controller.Handle" %>
<jsp:useBean id="controller" scope="session" class="org.optimizationBenchmarking.gui.controller.Controller" />
<h1>Log Level</h1>  
<p>
Whenever you perform an operation, the system will tell you whether it was successful or not.
However, some of the operations you may perform can provide (much) more fine-grained details than
just that. If you run the evaluator, for instance, it may tell you which files it loads and when,
what evaluation module is executed, when which graphic is drawn, and so on. This can provide
you with a better understanding of what is going on, but it may also simply be &quot;too much&quot;.
Thus, here, you can set the level of detail of the information you want to receive about ongoing
operations.
</p>

<%
final String submit   = TextUtils.prepare(request.getParameter("submit"));
if(submit != null) {
  try(final Handle handle = controller.createJspHandle(pageContext)) {
    if(submit.equalsIgnoreCase("OK")) {
      controller.setLogLevel(handle, request.getParameter("level"));
    } else {
      handle.unknownSubmit(submit);
    }
  }
} 
final String currentLevel = controller.getLogLevel();
%>
<h2>Current Log Level</h2>
<p>
The current log level is <code><%= currentLevel %></code>.<br/>
The most verbose level, <code>ALL</code>, means that you will receive all available information &ndash; which could be a lot.
The least verbose level, <code>SUCCESS</code>, means that you will only be informed about success or failure of your
operations, as well as warnings and critical errors.
</p>
<h2>Set Log Level</h2>
Here you can set the log level.
<form method="get" action="#">
Choose log levels:
<select name="level">
<option<% if("ALL".equals(currentLevel))     { %> selected="selected"<% } %>>ALL</option>
<option<% if("FINEST".equals(currentLevel))  { %> selected="selected"<% } %>>FINEST</option>
<option<% if("FINER".equals(currentLevel))   { %> selected="selected"<% } %>>FINER</option>
<option<% if("FINE".equals(currentLevel))    { %> selected="selected"<% } %>>FINE</option>
<option<% if("CONFIG".equals(currentLevel))  { %> selected="selected"<% } %>>CONFIG</option>
<option<% if("INFO".equals(currentLevel))    { %> selected="selected"<% } %>>INFO</option>
<option<% if("SUCCESS".equals(currentLevel)) { %> selected="selected"<% } %>>SUCCESS</option><br/>
</select><br/>
<input type=submit name="submit" value="OK">
</form>

</p>
<%@include file="/includes/defaultFooter.jsp" %>