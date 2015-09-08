<%@ page import="org.optimizationBenchmarking.utils.text.ESimpleDateFormat" %>
<%@ page import="org.optimizationBenchmarking.gui.utils.Menu" %>
<p class="footerInfo">
Server time: <%= ESimpleDateFormat.DATE_TIME.formatNow() %>
</p>
<p class="footerLinks"> 
[<a href="http://optimizationBenchmarking.github.io/optimizationBenchmarking">project page</a>] &bull;
[<a href="https://github.com/optimizationBenchmarking/optimizationBenchmarking">GitHub page</a>] &bull;
[<a href="http://optimizationbenchmarking.github.io/optimizationBenchmarking/repo/">downloads</a>] &bull;
[<a href="http://optimizationbenchmarking.github.io/optimizationBenchmarking/atom.xml">atom feed</a>] &bull;
[<a href="http://optimizationbenchmarking.github.io/optimizationBenchmarking/rss.xml">RSS feed</a>]
</p></section>
<aside id="sidebar">
<% Menu.renderMenu(request, out); %>
<div class="asideBottom">
<div class="ustcLogo"><img src="/images/ustcLogo.png" style="width:140px;height:140px;" /></div>
<div class="support">
Developed by Dr. <a href="http://www.it-weise.de/">Thomas Weise</a> at the University of Science and Technology of China (<a href="http://www.ustc.edu.cn/">USTC</a>).
Supported by the Fundamental Research Funds for the Central Universities.
</div></div>
</aside>
</div></div></body></html>