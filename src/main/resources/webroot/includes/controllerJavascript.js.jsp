<%@ page import="org.optimizationBenchmarking.gui.controller.ControllerUtils" %>
function onWithSelectionChange(prefix, selection) {
  var form = null;
  var desc = null;

  if(selection != null) {
    if(selection.id == (prefix+"Selection")) {
      form = document.getElementById(prefix + "Form");
      desc = document.getElementById(prefix + "Desc");
    }
    
    if(form != null) {
      switch(String(selection.value)) {
        case "<%= ControllerUtils.COMMAND_REMEMBER%>": {
          form.method = "post";
          form.action = "/controller.jsp";
          form.target = "_self";
          break;
        }
        case "<%= ControllerUtils.COMMAND_DOWNLOAD%>": {
          form.method = "post";
          form.action = "/download";
          form.target = "_blank";
          break;
        }
        case "<%= ControllerUtils.COMMAND_EXECUTE_EVALUATOR%>": {
          form.method = "get";
          form.action = "/evaluator.jsp";
          form.target = "_self";      
          break;
        }
        case "<%= ControllerUtils.COMMAND_EDIT_AS_TEXT%>": {
          form.method = "get";
          form.action = "/textEdit.jsp";
          form.target = "_self";      
          break;
        }
        case "<%= ControllerUtils.COMMAND_EDIT_AS_DIMENSIONS%>": {
          form.method = "get";
          form.action = "/dimensionsEdit.jsp";
          form.target = "_self";      
          break;
        }
        case "<%= ControllerUtils.COMMAND_EDIT_AS_INSTANCES%>": {
          form.method = "get";
          form.action = "/instancesEdit.jsp";
          form.target = "_self";      
          break;
        }
        case "<%= ControllerUtils.COMMAND_EDIT_AS_EXPERIMENT%>": {
          form.method = "get";
          form.action = "/experimentEdit.jsp";
          form.target = "_self";      
          break;
        }
        case "<%= ControllerUtils.COMMAND_EDIT_AS_CONFIG%>": {
          form.method = "get";
          form.action = "/configEdit.jsp";
          form.target = "_self";      
          break;
        }
        case "<%= ControllerUtils.COMMAND_EDIT_AS_EVALUATION%>": {
          form.method = "get";
          form.action = "/evaluationEdit.jsp";
          form.target = "_self";      
          break;
        }
        default: {
          form.method = "get";
          form.action = "/controller.jsp";
          form.target = "_self";
        }
      }
    }
   
    if(desc != null) {
      var text = "";  
      switch(String(selection.value)) {
        case "<%= ControllerUtils.COMMAND_REMEMBER%>": {
          text = "Remember the selected files. The files will be listed at the bottom of the controller window. Remembering files allows you to pick files from different directories, e.g., for download, without having to choose the complete directories.";
          break;
        }
        case "<%= ControllerUtils.COMMAND_FORGET%>": {
          text = "Forget the remembered selected files. The files will be removed from the remembered selection.";
          break;
        }
        case "<%= ControllerUtils.COMMAND_DOWNLOAD%>": {
          text = "Download the selected file(s). If one file is selected, it is sent as-is. If multiple files or folders are selected, they will be put into a <code>zip</code> archive.";
          break;
        }
        case "<%= ControllerUtils.COMMAND_EDIT_AS_TEXT%>": {
          text = "Edit the selected file as text file. This assumes that you know what you are doing, as syntax and content of the file will not be verified but treated as plain text.";
          break;
        }
        case "<%= ControllerUtils.COMMAND_EDIT_AS_DIMENSIONS%>": {
          text = "Edit the selected file as dimensions file. A dimensions file specifies which measurements are taken during experiments. You could, for instance, count the number of objective function evaluations, measure the objective values, and/or measure the runtime. You define this in the dimensions file.";
          break;
        }
        case "<%= ControllerUtils.COMMAND_EDIT_AS_INSTANCES%>": {
          text = "Edit the selected file as instances file. An instances file specifies the names and features of benchmark problem instances.";
          break;
        }
        case "<%= ControllerUtils.COMMAND_EDIT_AS_EXPERIMENT%>": {
          text = "Edit the selected file as experiment file. An instances file specifies the parameter settings of one specific setup of an algorithm, which is applied to the benchmark instancs.";
          break;
        }
        case "<%= ControllerUtils.COMMAND_EDIT_AS_CONFIG%>": {
          text = "Edit the selected file as configuration file. A configuration file tells the evaluator where to find the input data, where to put the output documents, which format to use for the output documents, and where it can find the list of &quot;things to do&quot;. The files must be XML files following the configuration schema.";
          break;
        }
        case "<%= ControllerUtils.COMMAND_EDIT_AS_EVALUATION%>": {
          text = "Edit the selected file as configuration file. An evaluation file tells the evaluation what to do with the experiment data, i.e., what stuff you want in your output report.";
          break;
        }
        case "<%= ControllerUtils.COMMAND_EXECUTE_EVALUATOR%>": {
          text = "The selected file must be a configuration file for an evaluation process. Then evaluation process will be started. It may take some time to finish. During this time, depending on the <a href='/logLevel.jsp'>log level</a> you set, you will receive information about what's going on. While the process is running, do not close or refresh the page. If you selected multiple configuration files, they will be processed one after the other.";      
          break;
        }  
        case "<%= ControllerUtils.COMMAND_DELETE%>": {
          text = "Delete the selected items. If a folder is deleted, all files and folders therein are deleted recursively. Handle with care.";      
          break;
        }         
        default: {
          text = "";
        }
      }
      
      desc.innerHTML = '<em>Currently Chosen Action:</em>&nbsp;' + text;
    }
  }
}

function onSelButtonClick(formId, value) {
  var form = document.getElementById(formId);
  if(form != null) {
    var inputs  = form.getElementsByTagName("input");
    if(inputs != null) {
      for(i = inputs.length; (--i) >= 0; ) {
        var input = inputs[i];
        if(input.type == "checkbox") {
          if(input.name == "<%=ControllerUtils.PARAMETER_SELECTION%>") {
            input.checked = value;  
          }
        }
      }
    }
  }
}