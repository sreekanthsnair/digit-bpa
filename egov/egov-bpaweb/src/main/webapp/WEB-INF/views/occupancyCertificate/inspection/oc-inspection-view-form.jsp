<%--
  ~ eGov suite of products aim to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) <2017>  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  --%>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="row">
    <div class="col-md-12">
        <ul class="nav nav-tabs" id="settingstab">
            <li class="active"><a data-toggle="tab" href="#oc-inspection-details"
                                  data-tabidx=0><spring:message code='lbl.inspn.details'/></a></li>
            <li><a data-toggle="tab" href="#oc-plan-scrutiny" data-tabidx=1><spring:message
                    code='lbl.plan.scrutiny'/></a></li>                      
        </ul>

        <div class="tab-content">
            <div id="oc-inspection-details" class="tab-pane fade in active">
                <div class="panel panel-primary" data-collapsed="0">
                    <div class="panel-heading custom_form_panel_heading">
                        <div class="panel-title"><spring:message code="lbl.inspn.details"/></div>
                    </div>
                    <div class="panel-body">
                        <div class="row add-border">
                            <div class="col-sm-3 add-margin">
                                <spring:message code="lbl.inspn.no"/>
                            </div>
                            <div class="col-sm-2 add-margin view-content">
                                <c:out value="${ocInspection.inspection.inspectionNumber}"
                                       default="N/A"></c:out>
                            </div>
                            <div class="col-sm-2 add-margin">
                                <spring:message code="lbl.inspn.date"/>
                            </div>
                            <div class="col-sm-5 add-margin view-content">
                                <fmt:formatDate value="${ocInspection.inspection.inspectionDate}" pattern="dd/MM/yyyy"
                                                var="inspectionDate"/>
                                <c:out value="${inspectionDate}" default="N/A"/>
                            </div>
                        </div>
                        <div class="row add-border">
                            <div class="col-sm-3 add-margin">
                                <spring:message code="lbl.ins.remarks"/>
                            </div>
                            <div class="col-sm-2 add-margin view-content">
                                <c:out value="${ocInspection.inspection.inspectionRemarks}" default="N/A"></c:out>
                            </div>
                        </div>
                        <div class="row add-border">
                            <div class="col-sm-5 add-margin view-content">
                                <div class="panel-title"><spring:message code="lbl.inspec.checklist"/></div>
                            </div>
                            <div class="col-sm-3 add-margin view-content">
                                <div class="panel-title"><spring:message code="lbl.doc.provided"/></div>
                            </div>
                            <div class="col-sm-3 add-margin view-content">
                                <div class="panel-title"><spring:message code="lbl.remarks"/></div>
                            </div>
                        </div>
                    </div>
                    <c:choose>
                        <c:when test="${!docketDetailLocList.isEmpty()}">
                            <div class="panel-heading custom_form_panel_heading">
                                <div class="panel-title"><%-- <spring:message code="lbl.loc.of.plot"/> --%></div>
                            </div>
                            <div class="panel-body">
                                <c:forEach items="${docketDetailLocList}" var="doc"
                                           varStatus="counter">
                                    <div class="row add-border">
                                        <div class="col-sm-5 add-margin view-content">
                                            <c:out value="${doc.checkListDetail.description}" default="N/A"></c:out>
                                        </div>
                                        <div class="col-sm-3 add-margin view-content">
                                            <c:out value="${doc.value.checkListVal}"
                                                   default="N/A"></c:out>
                                        </div>
                                        <div class="col-sm-3 add-margin view-content">
                                            <c:out value="${doc.remarks}" default="N/A"></c:out>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                    </c:choose>
				</div>
                <div class="panel panel-primary" data-collapsed="0">
                    <div class="panel-body custom-form ">
                        <jsp:include page="view-oc-inspection-images.jsp"></jsp:include>
                    </div>
                </div>
            </div>
			<div id="oc-plan-scrutiny" class="tab-pane fade">
					<div class="panel panel-primary" data-collapsed="0">
						<jsp:include page="oc-view-plan-scrutiny-checklist-rule.jsp"></jsp:include>
					</div>
					<div class="panel panel-primary" data-collapsed="0">
						<jsp:include page="oc-view-plan-scrutiny-checklist-drawing.jsp"></jsp:include>
					</div>
			</div>
		</div>
    </div>
</div>
<div align="center">
    <input type="button" name="button2" id="button2" value="Close"
           class="btn btn-default" onclick="window.close();"/>
</div>
