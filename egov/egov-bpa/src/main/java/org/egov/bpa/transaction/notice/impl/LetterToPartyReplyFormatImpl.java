/*
 * eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) <2019>  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *      Further, all user interfaces, including but not limited to citizen facing interfaces,
 *         Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *         derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *      For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *      For any further queries on attribution, including queries on brand guidelines,
 *         please contact contact@egovernments.org
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.bpa.transaction.notice.impl;

import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.bpa.master.entity.CheckListDetail;
import org.egov.bpa.master.entity.LpReason;
import org.egov.bpa.transaction.entity.LettertoParty;
import org.egov.bpa.transaction.entity.LettertoPartyDocument;
import org.egov.bpa.transaction.notice.LetterToPartyFormat;
import org.egov.bpa.utils.BpaConstants;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.reporting.engine.ReportRequest;
import org.egov.infra.reporting.engine.ReportService;
import org.egov.infra.reporting.util.ReportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * This is default implementation of all. This can be overridden by state, district, ulb and grade level.
 * When override following name convention to be follow,
 * i) If using same code base for multiple state's then for state level LetterToPartyReplyFormatImpl_<State Name>
 * ii)For state, district level can be override by LetterToPartyReplyFormatImpl_<District Name>
 * similarly for others can override.
 * If follow above standards, automatically will be pick concern implementation.
 */
@Service
public class LetterToPartyReplyFormatImpl implements LetterToPartyFormat {

    static final String LPREPLYCHK = "lpreply";

    @Autowired
    private ReportService reportService;
    @Autowired
    private CityService cityService;

    @Override
    public ReportOutput generateNotice(LettertoParty letterToParty) {

        ReportRequest reportInput = null;
        ReportOutput reportOutput;
        if (letterToParty != null) {
            reportInput = new ReportRequest("lettertopartyreply", letterToParty,
                    buildReportParameters(letterToParty));
            reportInput.setPrintDialogOnOpenReport(true);
        }
        reportOutput = reportService.createReport(reportInput);
        return reportOutput;
    }

    public Map<String, Object> buildReportParameters(final LettertoParty lettertoParty) {
        final Map<String, Object> reportParams = new HashMap<>();
        Boolean checkListPresent = Boolean.FALSE;
        List<CheckListDetail> chkList = new ArrayList<>();
        for (LettertoPartyDocument document : lettertoParty.getLettertoPartyDocument()) {
            if (!isEmpty(document.getChecklistDetail()) && document.getIsrequested() == Boolean.TRUE
                    && document.getChecklistDetail().getDescription() != null) {
                chkList.add(document.getChecklistDetail());
            }
            checkListPresent = chkList.isEmpty() ? Boolean.FALSE : Boolean.TRUE;
        }
        reportParams.put("stateLogo", ReportUtil.getImageURL(BpaConstants.STATE_LOGO_PATH));
        reportParams.put("checkListPresent", checkListPresent);
        reportParams.put("logoPath", cityService.getCityLogoAsStream());
        reportParams.put("cityName", ApplicationThreadLocals.getCityName());
        reportParams.put("ulbName", ApplicationThreadLocals.getMunicipalityName());
        reportParams.put("lpReason",
                lettertoParty.getLpReason().stream().map(LpReason::getDescription).collect(Collectors.joining(",")));
        return reportParams;
    }
}
