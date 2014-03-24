<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://stripes.sourceforge.net/stripes.tld" %>
<!DOCTYPE html>
<html>
<s:url beanclass="net.sourceforge.stripes.examples.bugzooky.BugListActionBean" var="listPage" />
<c:redirect url="${listPage}"/>
</html>
