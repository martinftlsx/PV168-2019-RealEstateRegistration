<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<h2>Fill form to edit owner</h2>
<c:if test="${not empty Error}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${Error}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/ownerEdit/update?id=${owner.getId()}" method="post">
    <table>
        <tr>
            <th>Name:</th>
            <td><input type="text" name="name" value="<c:out value='${owner.getName()}'/>"/></td>

        </tr>
        <tr>
            <th>Id card or corporation number:</th>
            <td><input type="text" name="idCardOrCorpNumber" value="<c:out value='${owner.getIdCardOrCorpNumber()}'/>"/></td>
        </tr>
    </table>
    <input type="Submit" value="Edit" />
</form>

</body>
</html>
