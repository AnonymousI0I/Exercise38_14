<%@ page import="java.util.*" %>
<%
    int numQ = Integer.parseInt(request.getParameter("numQuestions"));
    int correctCount = 0;
%>
<!DOCTYPE html>
<html>
<head><title>Addition Quiz Answer</title></head>
<body>
<h2>Addition Quiz Answer</h2>
<pre>
<%
    for (int i = 0; i < numQ; i++) {
        int op1   = Integer.parseInt(request.getParameter("op1" + i));
        int op2   = Integer.parseInt(request.getParameter("op2" + i));
        int right = op1 + op2;

        String ansStr = request.getParameter("answer" + i);
        int userAns;
        try {
            userAns = Integer.parseInt(ansStr.trim());
        } catch (Exception e) {
            userAns = Integer.MIN_VALUE;   // forces “Wrong” on blank / bad input
        }

        boolean ok = (userAns == right);
        if (ok) correctCount++;
%>
<%= op1 %> + <%= op2 %> = <%= ansStr %>  <%= ok ? "Correct" : "Wrong" %>
<%
    }
%>
The total correct count is <%= correctCount %>
</pre>
</body>
</html>
