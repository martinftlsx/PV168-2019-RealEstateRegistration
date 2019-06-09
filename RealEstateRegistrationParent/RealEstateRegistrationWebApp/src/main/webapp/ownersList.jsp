<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

    <table border="1">
        <thead>
        <tr>
            <th>Name</th>
            <th>Id card or corporation number</th>
            <th>Is it corporation</th>
        </tr>
        </thead>
        <c:forEach items="${owners}" var="owner">
            <tr>
                <td><c:out value="${owner.getName()}"/></td>
                <td><c:out value="${owner.getIdCardOrCorpNumber()}"/></td>
                <td><c:out value="${owner.getCorp()}"/></td>
                <td><form method="post" action="${pageContext.request.contextPath}/owners/delete?id=${owner.getId()}"
                          style="margin-bottom: 0;"><input type="submit" value="Delete"></form></td>
                <td><form method="post" action="${pageContext.request.contextPath}/owners/update?id=${owner.getId()}"
                          style="margin-bottom: 0;"><input type="submit" value="Edit"></form></td>
            </tr>
        </c:forEach>
    </table>

    <h2>Fill form to add new owner</h2>
    <c:if test="${not empty Error}">
        <div style="border: solid 1px red; background-color: yellow; padding: 10px">
            <c:out value="${Error}"/>
        </div>
    </c:if>
    <form action="${pageContext.request.contextPath}/owners/add" method="post">
        <table>
            <tr>
                <th>Name:</th>
                <td><input type="text" name="name" value="<c:out value='${param.name}'/>"/></td>
            </tr>
            <tr>
                <th>Id card or corporation number:</th>
                <td><input type="text" name="idCardOrCorpNumber" value="<c:out value='${param.idCardOrCorpNumber}'/>"/></td>
            </tr>
            <tr>
                <th>Corporation:</th>
                <td>
                    <select name="isCorp" value="<c:out value='${param.isCorp}'/>">
                        <option value="Yes">Yes</option>
                        <option value="No">No</option>
                    </select>
                </td>
            </tr>
        </table>
        <input type="Submit" value="Submit" />
    </form>

</body>
</html>
