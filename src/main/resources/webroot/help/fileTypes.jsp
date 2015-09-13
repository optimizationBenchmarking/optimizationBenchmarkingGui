<%@ page import="org.optimizationBenchmarking.gui.utils.files.EFSElementType" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Encoder" %>
<%@ page import="org.optimizationBenchmarking.utils.text.textOutput.ITextOutput" %>
<%@include file="/includes/defaultHeader.jsp" %>
<h1>File Types</h1>
<p>Here we list and describe the icons of the file (and folder) types that you may encounter when working with this system.</p>
<% int state = 0;
   final ITextOutput encoded = Encoder.htmlEncode(out);
   for(EFSElementType type : EFSElementType.values()) {
   if(type.isFile()) {
     if(state != 1) {
       state = 1; %><h2>Files</h2><%
     }
   } else {
    if(state != -1) {
       state = -1; %><h2>Folders</h2><%
     }
   } 
%>
<p><% type.putIcon(out, encoded); %>&nbsp;&nbsp;&mdash;&nbsp;&nbsp;<% encoded.append(type.getDescription()); %></p>
<% } %>
<%@include file="/includes/defaultFooter.jsp" %>