<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

    <table border="1">
        <thead>
        <tr>
            <th>Cadastral area</th>
            <th>Parcel number</th>
            <th>Area in meters squared</th>
        </tr>
        </thead>
        <c:forEach items="${realEstates}" var="realEstate">
            <tr>
                <td><c:out value="${realEstate.getCadastralArea()}"/></td>
                <td><c:out value="${realEstate.getParcelNumber()}"/></td>
                <td><c:out value="${realEstate.getAreaInMetersSquared()}"/></td>
                <td><form method="post" action="${pageContext.request.contextPath}/realEstates/delete?id=${realEstate.getId()}"
                          style="margin-bottom: 0;"><input type="submit" value="Delete"></form></td>
            </tr>
        </c:forEach>
    </table>

    <h2>Fill form to add new real-estate</h2>
    <c:if test="${not empty Error}">
        <div style="border: solid 1px red; background-color: yellow; padding: 10px">
            <c:out value="${Error}"/>
        </div>
    </c:if>
    <form action="${pageContext.request.contextPath}/realEstates/add" method="post">
        <table>
            <tr>
                <th>Cadastral area:</th>
                <td><input type="text" name="cadastralArea" value="<c:out value='${param.cadastralArea}'/>"/></td>
            </tr>
            <tr>
                <th>Parcel number:</th>
                <td><input type="text" name="parcelNumber" value="<c:out value='${param.parcelNumber}'/>"/></td>
            </tr>
            <tr>
                <th>Area in meters squared:</th>
                <td><input type="text" name="areaInMetersSquared" value="<c:out value='${param.areaInMetersSquared}'/>"/></td>
            </tr>
        </table>
        <input type="Submit" value="Submit" />
    </form>


</body>
</html>
