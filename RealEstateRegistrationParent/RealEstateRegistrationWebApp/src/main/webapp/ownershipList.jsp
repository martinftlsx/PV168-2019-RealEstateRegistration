<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

    <table border="1">
        <thead>
        <tr>
            <th>Ownership created</th>
            <th>Ownership removed</th>
            <th>Owner name</th>
            <th>Owner ID card or Corporation number</th>
            <th>Real-estate cadastral area</th>
            <th>Real-estate parcel number</th>
            <th>Share</th>
        </tr>
        </thead>
        <c:forEach var="os" items='${ownershipModels}'>
            <tr>
                <td><c:out value="${os.getOwnershipCreated()}"/></td>
                <td><c:out value="${os.getOwnershipRemoved()}"/></td>
                <td><c:out value="${os.getOwnerName()}"/></td>
                <td><c:out value="${os.getOwnerIdCardOrCorpNumber()}"/></td>
                <td><c:out value="${os.getCadastralArea()}"/></td>
                <td><c:out value="${os.getPareclNumber()}"/></td>
                <td><c:out value="${os.getShare()}"/></td>
                <td><form method="post" action="${pageContext.request.contextPath}/ownerships/delete?id=${os.getId()}"
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
    <form action="${pageContext.request.contextPath}/ownerships/add" method="post">
        <table>
            <tr>
                <th>Owner:</th>
                <td>
                    <select name="owner" value="<c:out value='${param.owner}'/>">
                        <c:forEach var="owner" items="${owners}">
                            <option value="${owner.getId()}">${owner.getName()}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
            <th>Real-estate:</th>
                <td>
                    <select name="realEstate" value="<c:out value='${param.realEstate}'/>">
                        <c:forEach var="realEstate" items="${realEstates}">
                            <option value="${realEstate.getId()}">${realEstate.getCadastralArea()} - ${realEstate.getParcelNumber()}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <th>Share:</th>
                <td><input type="text" name="share" value="<c:out value='${param.share}'/>"/></td>
            </tr>
        </table>
        <input type="Submit" value="Submit" />
    </form>

</body>
</html>
