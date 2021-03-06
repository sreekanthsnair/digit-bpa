/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2017>  eGovernments Foundation
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
 */
package org.egov.bpa.web.controller.ajax;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.bpa.master.entity.BpaScheme;
import org.egov.bpa.master.entity.BpaSchemeLandUsage;
import org.egov.bpa.master.entity.CheckListDetail;
import org.egov.bpa.master.entity.PostalAddress;
import org.egov.bpa.master.entity.RegistrarOfficeVillage;
import org.egov.bpa.master.entity.ServiceType;
import org.egov.bpa.master.entity.SlotMapping;
import org.egov.bpa.master.entity.StakeHolder;
import org.egov.bpa.master.entity.enums.SlotMappingApplType;
import org.egov.bpa.master.service.BpaSchemeService;
import org.egov.bpa.master.service.CheckListDetailService;
import org.egov.bpa.master.service.PostalAddressService;
import org.egov.bpa.master.service.RegistrarOfficeVillageService;
import org.egov.bpa.master.service.ServiceTypeService;
import org.egov.bpa.master.service.SlotMappingService;
import org.egov.bpa.master.service.StakeHolderService;
import org.egov.bpa.transaction.entity.BpaApplication;
import org.egov.bpa.transaction.entity.enums.StakeHolderType;
import org.egov.bpa.transaction.service.ApplicationBpaFeeCalculationService;
import org.egov.bpa.transaction.service.ApplicationBpaService;
import org.egov.bpa.utils.BpaConstants;
import org.egov.bpa.utils.OccupancyCertificateUtils;
import org.egov.common.entity.bpa.Occupancy;
import org.egov.common.entity.bpa.SubOccupancy;
import org.egov.common.entity.bpa.Usage;
import org.egov.commons.service.OccupancyService;
import org.egov.commons.service.SubOccupancyService;
import org.egov.commons.service.UsageService;
import org.egov.eis.entity.Assignment;
import org.egov.eis.entity.AssignmentAdaptor;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.DesignationService;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.BoundaryType;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.admin.master.service.BoundaryTypeService;
import org.egov.infra.admin.master.service.CrossHierarchyService;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.persistence.entity.enums.UserType;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.workflow.matrix.service.CustomizedWorkFlowService;
import org.egov.pims.commons.Designation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Controller
public class BpaAjaxController {

    private static final String BLOCK_NAME = "blockName";

    private static final String BLOCK_ID = "blockId";

    @Autowired
    private DesignationService designationService;
    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private CustomizedWorkFlowService customizedWorkFlowService;
    @Autowired
    private ApplicationBpaService applicationBpaService;
    @Autowired
    private StakeHolderService stakeHolderService;
    @Autowired
    private OccupancyService occupancyService;
    @Autowired
    private SubOccupancyService subOccupancyService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostalAddressService postalAddressService;
    @Autowired
    private CrossHierarchyService crossHierarchyService;
    @Autowired
    private BpaSchemeService bpaSchemeService;
    @Autowired
    private RegistrarOfficeVillageService registrarOfficeService;
    @Autowired
    private BoundaryService boundaryService;
    @Autowired
    private SlotMappingService slotMappingService;
    @Autowired
    private ApplicationBpaFeeCalculationService permitFeeCalculationService;
    @Autowired
    private CheckListDetailService checkListDetailService;
    @Autowired
    private OccupancyCertificateUtils occupancyCertificateUtils;
    @Autowired
    private AppConfigValueService appConfigValuesService;
    @Autowired
    private BoundaryTypeService boundaryTypeService;
    @Autowired
    private ServiceTypeService serviceTypeService;
    @Autowired
    private UsageService usageService;

    @GetMapping(value = "/ajax/getAdmissionFees", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BigDecimal isConnectionPresentForProperty(@RequestParam final Long[] serviceTypeIds) {
        if (serviceTypeIds.length > 0) {
            return applicationBpaService
                    .getTotalFeeAmountByPassingServiceTypeAndAmenities(Arrays.asList(serviceTypeIds));
        } else {
            return BigDecimal.ZERO;
        }
    }

    @GetMapping(value = "/bpaajaxWorkFlow-getDesignationsByObjectTypeAndDesignation", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Designation> getDesignationsByObjectTypeAndDesignation(
            @ModelAttribute("designations") @RequestParam final String departmentRule,
            @RequestParam final String currentState, @RequestParam final String type,
            @RequestParam final BigDecimal amountRule, @RequestParam final String additionalRule,
            @RequestParam final String pendingAction, @RequestParam final Long approvalDepartment) {
        List<Designation> designationList = designationService
                .getDesignationsByNames(customizedWorkFlowService.getNextDesignations(type, departmentRule, amountRule,
                        additionalRule, currentState, pendingAction, new Date()));
        if (designationList.isEmpty())
            designationList = designationService.getAllDesignationByDepartment(approvalDepartment, new Date());
        return designationList;
    }

    @GetMapping(value = "/ajax-designationsByDepartment", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Designation> getDesignationsByDepartmentId(
            @ModelAttribute("designations") @RequestParam final Long approvalDepartment) {
        List<Designation> designations = new ArrayList<>();
        if (approvalDepartment != null && approvalDepartment != 0 && approvalDepartment != -1)
            designations = designationService.getAllDesignationByDepartment(approvalDepartment, new Date());
        designations.forEach(designation -> designation.toString());
        return designations;
    }

    @GetMapping(value = "/bpaajaxWorkFlow-positionsByDepartmentAndDesignationAndBoundary", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getPositionByDepartmentAndDesignationAndBoundary(@RequestParam final Long approvalDepartment,
            @RequestParam final Long approvalDesignation, @RequestParam final Long boundaryId,
            final HttpServletResponse response) {
        if (approvalDepartment != null && approvalDepartment != 0 && approvalDepartment != -1
                && approvalDesignation != null && approvalDesignation != 0 && approvalDesignation != -1) {
            List<Assignment> assignmentList = assignmentService.findAssignmentByDepartmentDesignationAndBoundary(
                    approvalDepartment, approvalDesignation, boundaryId);
            final Gson jsonCreator = new GsonBuilder().registerTypeAdapter(Assignment.class, new AssignmentAdaptor())
                    .create();
            return jsonCreator.toJson(assignmentList, new TypeToken<Collection<Assignment>>() {
            }.getType());
        }
        return "[]";
    }

    @GetMapping(value = "/ajax/stakeholdersbytype", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<StakeHolder> getStakeHolderByType(@RequestParam final String name,
            @RequestParam final StakeHolderType stakeHolderType) {
        return stakeHolderService.getStakeHolderListByType(stakeHolderType, name);
    }

    @GetMapping(value = "/application/getoccupancydetails", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Occupancy> getOccupancyDetails() {
        return occupancyService.findAll();
    }

    @GetMapping(value = "/application/getsuboccupancydetails", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SubOccupancy> getSubOccupancyDetails() {
        return subOccupancyService.findAll();
    }

    @GetMapping(value = "/application/getOccupancyAndSuboccupancyMap", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<Long, List<SubOccupancy>> getOccupancyAndSuboccupancyMap() {
        List<SubOccupancy> subOccupancyList = subOccupancyService.findAll();
        Map<Long, List<SubOccupancy>> map = new HashMap<>();

        for (SubOccupancy subOccupancy : subOccupancyList) {
            if (map.containsKey(subOccupancy.getOccupancy().getId())) {
                map.get(subOccupancy.getOccupancy().getId()).add(subOccupancy);
            } else {
                List<SubOccupancy> sub = new ArrayList<>();
                sub.add(subOccupancy);
                map.put(subOccupancy.getOccupancy().getId(), sub);
            }
        }
        return map;
    }
    
    @GetMapping(value = "/application/group-usages/by-suboccupancy", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<Long, List<Usage>> getUsagesBySubOccupancy() {
        Map<Long, List<Usage>> usagesMap = new HashMap<>();
        for (Usage usage : usageService.findAll()) {
            if (usagesMap.containsKey(usage.getSubOccupancy().getId())) {
                usagesMap.get(usage.getSubOccupancy().getId()).add(usage);
            } else {
                List<Usage> usageSubMap = new ArrayList<>();
                usageSubMap.add(usage);
                usagesMap.put(usage.getSubOccupancy().getId(), usageSubMap);
            }
        }
        return usagesMap;
    }
    
    @GetMapping(value = "/getsuboccupancies/by-occupancy", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SubOccupancy> getSubOccupancyDetailsByOccupancy(@RequestParam String[] occupancies) {
        List<SubOccupancy> subOccupancies = new ArrayList<>();
        if(occupancies != null && occupancies.length > 0) {
            for(String occ : occupancies)
                subOccupancies.addAll(subOccupancyService.findSubOccupanciesByOccupancy(occ));
        }
        return subOccupancies;
    }

    @GetMapping(value = "/getApplicantDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> getApplicantDetailsForMobileNumber(@RequestParam final String mobileNumber) {
        Map<String, String> user = new HashMap<>();
        List<User> userList = userService.getUserByMobileNumberAndType(mobileNumber, UserType.CITIZEN);
        if (!userList.isEmpty()) {
            User dbUser = userList.get(0);
            user.put("name", dbUser.getName());
            user.put("address", dbUser.getAddress().get(0).getStreetRoadLine());
            user.put("emailId", dbUser.getEmailId());
            user.put("gender", dbUser.getGender().name());
            user.put("id", dbUser.getId().toString());

        }
        return user;
    }

    @GetMapping(value = "/getApplicantDetailsForEmailId", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public User getApplicantDetailsForEmailId(@RequestParam final String emailId) {
        return userService.getUserByUsername(emailId);
    }

    @GetMapping(value = "/ajax/postaladdress", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<PostalAddress> getPostalAddress(@RequestParam final String pincode) {
        return postalAddressService.getPostalAddressList(pincode);
    }

    @GetMapping(value = "/ajax/getpostaladdressbyid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PostalAddress getPostalAddressObj(@RequestParam final Long id) {
        return postalAddressService.findById(id);
    }

    @GetMapping(value = "/boundary/ajaxBoundary-localityByWard", produces = MediaType.APPLICATION_JSON_VALUE)
    public void localityByWard(@RequestParam Long wardId, HttpServletResponse response) throws IOException {

        final List<Boundary> blocks = crossHierarchyService
                .findChildBoundariesByParentBoundaryIdParentBoundaryTypeAndChildBoundaryType(BpaConstants.WARD,
                        BpaConstants.REVENUE_HIERARCHY_TYPE, BpaConstants.LOCALITY_BNDRY_TYPE, wardId);
        final List<JSONObject> jsonObjects = new ArrayList<>();
        for (final Boundary block : blocks) {
            final JSONObject jsonObj = new JSONObject();
            jsonObj.put("localityId", block.getId());
            jsonObj.put("localityName", block.getName());
            jsonObjects.add(jsonObj);
        }
        IOUtils.write(jsonObjects.toString(), response.getWriter());
    }

    @GetMapping(value = "/boundary/ajaxBoundary-electionwardbyrevenueward", produces = MediaType.APPLICATION_JSON_VALUE)
    public void electionWardByRevenueWard(@RequestParam Long wardId, HttpServletResponse response) throws IOException {

        final List<Boundary> blocks = crossHierarchyService
                .findChildBoundariesByParentBoundaryIdParentBoundaryTypeAndChildBoundaryType(BpaConstants.WARD,
                        BpaConstants.REVENUE_HIERARCHY_TYPE, BpaConstants.WARD, wardId);
        sortBoundaryByBndryNumberAsc(blocks);
        final List<JSONObject> jsonObjects = new ArrayList<>();
        for (final Boundary block : blocks) {
            final JSONObject jsonObj = new JSONObject();
            jsonObj.put("electionwardId", block.getId());
            jsonObj.put("electionwardName", block.getName());
            jsonObjects.add(jsonObj);
        }
        IOUtils.write(jsonObjects.toString(), response.getWriter());
    }

    @GetMapping(value = "/ajax/getlandusagebyscheme", produces = MediaType.APPLICATION_JSON_VALUE)
    public void landUsageByScheme(@RequestParam Long schemeId, HttpServletResponse response) throws IOException {

        if (schemeId != null) {
            final BpaScheme scheme = bpaSchemeService.findById(schemeId);

            final List<JSONObject> jsonObjects = new ArrayList<>();
            if (scheme != null) {
                for (final BpaSchemeLandUsage landUsage : scheme.getSchemeLandUsage()) {
                    final JSONObject jsonObj = new JSONObject();
                    jsonObj.put("usageId", landUsage.getId());
                    jsonObj.put("usageDesc", landUsage.getDescription());
                    jsonObjects.add(jsonObj);
                }
            }
            IOUtils.write(jsonObjects.toString(), response.getWriter());
        }
    }

    @GetMapping(value = "/ajax/registraroffice", produces = MediaType.APPLICATION_JSON_VALUE)
    public void registrarOfficeVillageMapping(@RequestParam Long villageId, HttpServletResponse response)
            throws IOException {

        if (villageId != null) {
            final List<RegistrarOfficeVillage> registrarOfficeList = registrarOfficeService
                    .getRegistrarOfficeByVillage(villageId);

            final List<JSONObject> jsonObjects = new ArrayList<>();
            if (!registrarOfficeList.isEmpty()) {
                for (final RegistrarOfficeVillage office : registrarOfficeList) {
                    final JSONObject jsonObj = new JSONObject();
                    jsonObj.put("registrarVillageId", office.getId());
                    jsonObj.put("registrarOfficeName", office.getRegistrarOffice().getName());
                    jsonObjects.add(jsonObj);
                }
            }
            IOUtils.write(jsonObjects.toString(), response.getWriter());
        }
    }

    @GetMapping("/boundary/ajaxBoundary-blockByWard")
    public void blockByWard(@RequestParam Long wardId, HttpServletResponse response) throws IOException {
        List<Boundary> revenueWards = boundaryService.getActiveChildBoundariesByBoundaryId(wardId);
        sortBoundaryByBndryNumberAsc(revenueWards);
        final List<JsonObject> jsonObjects = new ArrayList<>();
        revenueWards.stream().forEach(block -> {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty(BLOCK_ID, block.getId());
            jsonObj.addProperty(BLOCK_NAME, block.getName());
            jsonObjects.add(jsonObj);
        });
        IOUtils.write(jsonObjects.toString(), response.getWriter());
    }

    private void sortBoundaryByBndryNumberAsc(List<Boundary> boundaries) {
        boundaries.sort(Comparator.comparing(Boundary::getBoundaryNum));
    }

    @GetMapping(value = "/ajax/getOneDayPermitSlotByBoundary", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean getOneDayPermitSlotByBoundary(@RequestParam Long zoneId, @RequestParam Long electionWardId) {
        SlotMapping slotMapping = new SlotMapping();
        Boundary zone = boundaryService.getBoundaryById(zoneId);
        Boundary electionWard = boundaryService.getBoundaryById(electionWardId);
        slotMapping.setZone(zone);
        slotMapping.setElectionWard(electionWard);
        slotMapping.setApplType(SlotMappingApplType.ONE_DAY_PERMIT);
        List<SlotMapping> slotMappings = slotMappingService.searchSlotMapping(slotMapping);
        return !slotMappings.isEmpty();
    }

    @GetMapping(value = "/validate/emailandmobile", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean validateEmailAndMobileNumber(@RequestParam final String inputType,
            @RequestParam final String inputValue) {
        if ("email".equalsIgnoreCase(inputType)
                && stakeHolderService.validateStakeHolderIsRejected("", inputValue, "", "", "") == null) {
            return userService.getUserByEmailId(inputValue) != null;
        } else if ("mobile".equalsIgnoreCase(inputType)
                && stakeHolderService.validateStakeHolderIsRejected(inputValue, "", "", "", "") == null) {
            return !userService.getUserByMobileNumberAndType(inputValue, UserType.BUSINESS).isEmpty();
        } else if ("aadhaar".equalsIgnoreCase(inputType)
                && stakeHolderService.validateStakeHolderIsRejected("", "", inputValue, "", "") == null) {
            return userService.getUserByAadhaarNumber(inputValue) != null;
        } else if ("pan".equalsIgnoreCase(inputType)
                && stakeHolderService.validateStakeHolderIsRejected("", "", "", inputValue, "") == null) {
            return userService.getUserByPan(inputValue) != null;
        } else if ("licenseNo".equalsIgnoreCase(inputType)
                && stakeHolderService.validateStakeHolderIsRejected("", "", "", "", inputValue) == null) {
            return stakeHolderService.findByLicenseNumber(inputValue) != null;
        }
        return false;
    }

    @GetMapping(value = "/occupancy/sub-usages", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Usage> getSubUsagesByOccupancy(@RequestParam String occupancy) {
        return occupancyService.findSubUsagesByOccupancy(occupancy);
    }

    @GetMapping(value = "/application/findby-permit-number", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void getApplicationDetailsByPlanPermissionNumber(@RequestParam final String permitNumber,
            final HttpServletResponse response) throws IOException {
        BpaApplication application = StringUtils.isBlank(permitNumber) ? null
                : applicationBpaService.findByPermitNumber(permitNumber);
        final JsonObject jsonObj = new JsonObject();
        if (application != null) {
        	Map<String, String> ocApplicationDetails = occupancyCertificateUtils
                    .checkIsPermitNumberUsedWithAnyOCApplication(permitNumber);
            jsonObj.addProperty("isOcRequire", application.getServiceType().getIsOCRequired());
            jsonObj.addProperty("ocExists",ocApplicationDetails.get("isExists"));
            jsonObj.addProperty("ocExistsMessage", ocApplicationDetails.get(BpaConstants.MESSAGE));
            jsonObj.addProperty("id", application.getId());
            jsonObj.addProperty("stakeholderId", application.getStakeHolder().get(0).getId());
            jsonObj.addProperty("occupancy", application.getOccupanciesName());
            jsonObj.addProperty("zone", application.getSiteDetail().get(0).getAdminBoundary().getParent().getName());
            jsonObj.addProperty("revenueWard", application.getSiteDetail().get(0).getAdminBoundary().getName());
			jsonObj.addProperty("electionWard", application.getSiteDetail().get(0).getElectionBoundary() != null
					? application.getSiteDetail().get(0).getElectionBoundary().getName() : "");
            jsonObj.addProperty("reSurveyNumber", application.getSiteDetail().get(0).getReSurveyNumber());
            jsonObj.addProperty("village", application.getSiteDetail().get(0).getLocationBoundary() == null ? ""
                    : application.getSiteDetail().get(0).getLocationBoundary().getName());
            jsonObj.addProperty("plotArea", application.getSiteDetail().get(0).getExtentinsqmts());
            jsonObj.addProperty("serviceTypeId", application.getServiceType().getId());
            jsonObj.addProperty("serviceTypeDesc", application.getServiceType().getDescription());
            jsonObj.addProperty("serviceTypeCode", application.getServiceType().getCode());
            jsonObj.addProperty("applicationDate", DateUtils.toDefaultDateFormat(application.getApplicationDate()));
            jsonObj.addProperty("applicationType", application.getIsOneDayPermitApplication() ? "YES" : "NO");
            jsonObj.addProperty("dcrNumber", application.geteDcrNumber());
            jsonObj.addProperty("applicantName", application.getOwner().getName());
            jsonObj.addProperty("applicationNumber", application.getApplicationNumber());
            jsonObj.addProperty("planPermissionNumber", application.getPlanPermissionNumber());
            if (!application.getBuildingDetail().isEmpty()) {
                BigDecimal floorArea = permitFeeCalculationService.getTotalFloorArea(application);
                Optional<Occupancy> occ = application.getPermitOccupancies().stream().filter(o -> o.getCode().equalsIgnoreCase(BpaConstants.RESIDENTIAL)).findAny();
                jsonObj.addProperty("isSingleFamily",
                        occ.isPresent() && application.getBuildingDetail().get(0).getFloorCount().intValue() <= 2
                                && floorArea.doubleValue() <= 150);
            }
        }
        IOUtils.write(jsonObj.toString(), response.getWriter());
    }

    @GetMapping(value = "/application/getdocumentlistbyservicetype", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CheckListDetail> getDocumentsByServiceType(@RequestParam final Long serviceType,
            @RequestParam final String checklistType) {
        return checkListDetailService.findActiveCheckListByServiceType(serviceType, checklistType);
    }

	@GetMapping(value = "/boundary/ajax-boundary-configuration", produces = MediaType.APPLICATION_JSON_VALUE)
	public void getBoundaryConfiguration(HttpServletResponse response) throws IOException {
		JSONArray validBoundaryTypeJsonArray;
		JSONObject boundaryDataJson = new JSONObject();
		JSONObject boundaryOutputJson = new JSONObject();
		JSONObject boundaryInfoJson = null;
		JSONObject boundaryTypeInJson;
		JSONObject boundaryJson = null;
		JSONArray boundaryArray = null;
		BoundaryType boundaryType = null;

		final AppConfigValues boundaryConfiguration = appConfigValuesService
				.getConfigValuesByModuleAndKey(BpaConstants.BPA_MODULE_NAME, BpaConstants.GENERIC_BOUNDARY_CONFIGURATION_KEY)
				.get(0);
		JSONObject boundaryConfigJson = new JSONObject(boundaryConfiguration.getValue());
		JSONObject validBoundaryJson = (JSONObject) boundaryConfigJson.get("validBoundary");
		JSONObject crossBoundaryJson = (JSONObject) boundaryConfigJson.get("crossBoundary");
		if (crossBoundaryJson.length() != 0) {
			boundaryOutputJson.put("crossBoundary", crossBoundaryJson);
		}
		for (final String heirarchy : validBoundaryJson.keySet()) {
			validBoundaryTypeJsonArray = validBoundaryJson.getJSONArray(heirarchy);
			for (int i = 0; i < validBoundaryTypeJsonArray.length(); i++) {
				boundaryInfoJson = new JSONObject();
				boundaryTypeInJson = validBoundaryTypeJsonArray.getJSONObject(i);
				boundaryArray = new JSONArray();
				boundaryType = boundaryTypeService
						.getBoundaryTypeByNameAndHierarchyTypeName(boundaryTypeInJson.getString("boundary"), heirarchy);
				for (final Boundary boundary : boundaryService.getActiveBoundariesByBoundaryTypeId(boundaryType.getId())) {
					boundaryJson = new JSONObject();
					boundaryJson.put("id", boundary.getId());
					boundaryJson.put("name", boundary.getName());
					boundaryJson.put("parent", boundary.getParent() == null ? "" : boundary.getParent().getId());
					boundaryJson.put("materialpath", boundary.getMaterializedPath());
					boundaryArray.put(boundaryJson);
				}
				boundaryInfoJson.put("data", boundaryArray);
				boundaryDataJson.put(boundaryType.getHierarchyType().getId() + "-" + boundaryType.getHierarchy() + ":"
						+ heirarchy + ":" + boundaryTypeInJson.getString("displayName"), boundaryInfoJson);
			}
		}
		boundaryOutputJson.put("boundaryData", boundaryDataJson);
		System.out.println("getBoundaryConfiguration--->" + boundaryOutputJson.toString());
		IOUtils.write(boundaryOutputJson.toString(), response.getWriter());
	}
	
	@GetMapping(value = "/boundary/ajax-cross-boundary", produces = MediaType.APPLICATION_JSON_VALUE)
	public void getCrossBoundary(HttpServletResponse response, @RequestParam final String parent,
			@RequestParam final String child, @RequestParam final String selectedParent) throws IOException {
		JSONObject childBoundaryJson = new JSONObject();
		List<Boundary> childBoundaries;
		JSONObject boundaryJson = null;
		JSONArray boundaryArray = null;

		String parentHeirarchy = parent.split(":")[0];
		String parentBoundaryType = parent.split(":")[1];

		String[] childBoundary = child.split(",");
		if (selectedParent != null && !selectedParent.isEmpty()) {
			for (int i = 0; i < childBoundary.length; i++) {
				childBoundaries = crossHierarchyService
						.findChildBoundariesByParentBoundaryIdParentBoundaryTypeAndChildBoundaryType(parentBoundaryType,
								parentHeirarchy, childBoundary[i].split(":")[1], Long.valueOf(selectedParent));
				boundaryArray = new JSONArray();
				for (final Boundary boundary : childBoundaries) {
					boundaryJson = new JSONObject();
					boundaryJson.put("id", boundary.getId());
					boundaryJson.put("name", boundary.getName());
					boundaryJson.put("materialpath", boundary.getMaterializedPath());
					boundaryArray.put(boundaryJson);
				}
				childBoundaryJson.put(childBoundary[i].split(":")[1], boundaryArray);
			}
		}
		System.out.println("getCrossBoundary--->" + childBoundaryJson.toString());
		IOUtils.write(childBoundaryJson.toString(), response.getWriter());
	}
	
    @GetMapping(value = "/application/getServiceTypeDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ServiceType> getServiceTypeDetails() {
        return serviceTypeService.getAllActiveServiceTypes();
    }
}
