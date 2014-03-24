<%@ include file="/WEB-INF/bugzooky/taglibs.jsp" %>
<%--@elvariable id="actionBean" type="net.sourceforge.stripes.examples.bugzooky.MultiBugActionBean"--%>

<stripes:layout-render name="/WEB-INF/bugzooky/layout/standard.jsp" title="Bulk Add/Edit Bugs">
    <stripes:layout-component name="contents">

        <stripes:form beanclass="net.sourceforge.stripes.examples.bugzooky.MultiBugActionBean" focus="">

            <stripes:errors/>

            <table class="display">
                <tr>
                    <th>ID</th>
                    <th><stripes:label name="bugs.component"/></th>
                    <th><stripes:label name="bugs.owner"/></th>
                    <th><stripes:label name="bugs.priority"/></th>
                    <th><stripes:label name="bugs.shortDescription"/></th>
                    <th><stripes:label name="bugs.longDescription"/></th>
                </tr>

                <c:forEach items="${actionBean.bugs}" var="bug" varStatus="loop">
                    <tr>
                        <td>
                            ${bug.id} <stripes:hidden name="bugs[${loop.index}]"/>
                        </td>
                        <td>
                            <stripes:select name="bugs[${loop.index}].component">
                                <stripes:option value="">Select One</stripes:option>
                                <stripes:options-collection collection="${actionBean.allComponents}" label="name" value="id"/>
                            </stripes:select>
                        </td>
                        <td>
                            <stripes:select name="bugs[${loop.index}].owner">
                                <stripes:option value="">Select One</stripes:option>
                                <stripes:options-collection collection="${actionBean.allPeople}" label="username" value="id"/>
                            </stripes:select>
                        </td>
                        <td>
                            <stripes:select name="bugs[${loop.index}].priority">
                                <stripes:option value="">Select One</stripes:option>
                                <stripes:options-enumeration enum="net.sourceforge.stripes.examples.bugzooky.biz.Priority"/>
                            </stripes:select>
                        </td>
                        <td>
                            <stripes:textarea name="bugs[${loop.index}].shortDescription"/>
                        </td>
                        <td>
                            <stripes:textarea name="bugs[${loop.index}].longDescription"/>
                        </td>
                    </tr>
                </c:forEach>
            </table>

            <div class="buttons"><stripes:submit name="save" value="Save"/></div>
        </stripes:form>
    </stripes:layout-component>
</stripes:layout-render>
