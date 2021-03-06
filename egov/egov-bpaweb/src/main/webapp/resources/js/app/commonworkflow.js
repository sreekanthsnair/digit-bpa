/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
$(document).ready(function()
{	
	
	// On page load setting default department get selected
	$('[name=approvalDepartment] option').filter(function() { 
        return ($(this).text() == $('#defaultDepartment').val());
    }).prop('selected', true);
	
	$('#approvalDepartment').change(function(){
		$.ajax({
			url: "/bpa/bpaajaxWorkFlow-getDesignationsByObjectTypeAndDesignation",     
			type: "GET",
			data: {
				approvalDepartment : $('#approvalDepartment').val(),
				departmentRule : $('#approvalDepartment').find("option:selected").text(),
				type : $('#stateType').val(),
				currentState : $('#currentState').val(),
				amountRule : $('#amountRule').val(),
				additionalRule : $('#additionalRule').val(),
				pendingAction : $('#pendingActions').val()
			},
			dataType: "json",
			success: function (response) {
				console.log("success"+response);
				$('#approvalDesignation').empty();
				//$('#approvalDesignation').append($("<option value=''>Select from below</option>"));
				$.each(response, function(index, value) {
					$('#approvalDesignation').append($('<option>').text(value.name).attr('value', value.id));
				});
				$('#approvalDesignation').trigger('change');
			}, 
			error: function (response) {
				bootbox.alert('json fail');
				console.log("failed");
			}
		});
	});
	
	$('#approvalDesignation').change(function(){
		
		var url="";
		
		if($('#stateType').val()=='StakeHolderState')
			 url= "/eis/ajaxWorkFlow-positionsByDepartmentAndDesignation";
			else
				url= "/bpa/bpaajaxWorkFlow-positionsByDepartmentAndDesignationAndBoundary";	
		$.ajax({
			
			url: url,     
			type: "GET",
			data: {
				approvalDesignation : $('#approvalDesignation option:selected').val(),
				approvalDepartment : $('#approvalDepartment option:selected').val(),
				boundaryId :$('#workFlowBoundary').val()
			},
			dataType: "json",
			success: function (response) {
				console.log("success"+response);
				$('#approvalPosition').empty();
				//$('#approvalPosition').append($("<option value=''>Select from below</option>"));
				$.each(response, function(index, value) {
					$('#approvalPosition').append($('<option>').text(value.userName+'/'+value.positionName).attr('value', value.positionId));  
				});
				
				if($('#electionBoundaryName').val() == 'WARD 24-KUDILTHODU' && $('#revenueBoundaryName').val() == 'WARD 28-NELLIKKODE') {
					$('[name=approvalPosition] option').filter(function() { 
				        return ($(this).text() == 'AETP2/TP_Assistant Engineer_2');
				    }).prop('selected', true);
					
					$('[name=approvalPosition] option').filter(function() { 
				        return ($(this).text() == 'PWO5/TP_Town Planning Building Overseer_5');
				    }).prop('selected', true);
					
				} else if($('#electionBoundaryName').val() == 'WARD 24-KUDILTHODU' && $('#revenueBoundaryName').val() == 'WARD 33-CHEVAYUR') {
					$('[name=approvalPosition] option').filter(function() { 
						return ($(this).text() == 'AETP1/TP_Assistant Engineer_1');
				    }).prop('selected', true);
					
					$('[name=approvalPosition] option').filter(function() { 
				        return ($(this).text() == 'PWO3/TP_Town Planning Building Overseer_3');
				    }).prop('selected', true);
				}
				
			}, 
			error: function (response) {
				console.log("failed");
			}
		});
	});
	$('#approvalDepartment').trigger('change');
});

function callAlertForDepartment() {
    var value=$('#approvalDepartment').val();
	if(value=="" ||  value=="-1") {
		bootbox.alert($('#approveDepReq').val());
		return false;
	}
}

function callAlertForDesignation() {
	var value=$('#approvalDesignation').val();
	if(value=="" || value=="-1") {
		bootbox.alert($('#approveDesignReq').val());
		return false;
	}
}
	