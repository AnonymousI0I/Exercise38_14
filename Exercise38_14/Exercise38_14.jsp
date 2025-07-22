<%@ page import="java.util.Random" %>
<%
    final int NUM_QUESTIONS = 10;
    Random rand = new Random();
%>
<!DOCTYPE html>
<html>
<head><title>Addition Quiz</title></head>
<body>
<h2>Addition Quiz</h2>

<form action="Exercise38_14DisplayResult.jsp" method="post">
<table>
<%
    for (int i = 0; i < NUM_QUESTIONS; i++) {
        int a = rand.nextInt(30) + 1;   // 1–30
        int b = rand.nextInt(30) + 1;   // 1–30
%>
  <tr>
    <td><%= a %> + <%= b %> = </td>
    <td><input type="text" name="answer<%= i %>" size="4" /></td>
  </tr>
  <!-- pass the operands to the next page -->
  <input type="hidden" name="op1<%= i %>" value="<%= a %>" />
  <input type="hidden" name="op2<%= i %>" value="<%= b %>" />
<%
    }
%>
</table>

<input type="hidden" name="numQuestions" value="<%= NUM_QUESTIONS %>" />
<br/>
<input type="submit" value="Submit" />
<p>Click the browser’s Refresh button for a new quiz.</p>
</form>
</body>
</html>
