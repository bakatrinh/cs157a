<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>

    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <meta name="description" content="#"/>
    <meta name="author" content="#"/>

    <title>${page_name}</title>

    <jsp:include page="common/top.jsp"/>
</head>
<body>

<jsp:include page="common/navigation.jsp"/>

<a href="/file/view/1">Browse Files</a>
<br>
<% if (session.getAttribute("username") == null) { %>
<p>no username session</p>
<% } else {%>
<p>${sessionScope.username}</p>
<% } %>

<jsp:include page="common/bottom.jsp" />
</body>
</html>