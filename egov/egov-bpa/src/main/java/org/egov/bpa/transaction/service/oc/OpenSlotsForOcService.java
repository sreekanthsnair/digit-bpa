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
package org.egov.bpa.transaction.service.oc;

import org.egov.bpa.master.entity.SlotMapping;
import org.egov.bpa.master.entity.enums.SlotMappingApplType;
import org.egov.bpa.master.repository.SlotMappingRepository;
import org.egov.bpa.master.service.HolidayListService;
import org.egov.bpa.transaction.entity.Slot;
import org.egov.bpa.transaction.entity.SlotDetail;
import org.egov.bpa.transaction.repository.SlotRepository;
import org.egov.bpa.transaction.service.SlotService;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OpenSlotsForOcService {
    private static final String MODULE_NAME = "BPA";
    private static final String SLOTS_DAYS_CONFIG = "NOOFDAYSFORASSIGNINGSLOTSFOROC";
    private static final String SLOT_TYPE_OC = "Occupancy Certificate";
    private static final String APP_TIME_MORNING = "Morning";
    private static final String APP_TIME_EVENING = "Evening";

    @Autowired
    private SlotMappingRepository zoneSlotRepository;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private AppConfigValueService appConfigValuesService;

    @Autowired
    private HolidayListService holidayListService;

    @Autowired
    private SlotService slotService;


    @Transactional
    public void openNewSlots() {
        List<SlotMapping> slotZoneList = zoneSlotRepository.findByApplType(SlotMappingApplType.OCCUPANCY_CERTIFICATE);
        List<AppConfigValues> appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(MODULE_NAME,
                SLOTS_DAYS_CONFIG);
        String noOfDays = appConfigValue.get(0).getValue();
        Integer scheduledSlotsAllowedForMorning = 0;
        Integer scheduledSlotsAllowedForEvening = 0;
        Integer rescheduledSlotsAllowedForMorning = 0;
        Integer rescheduledSlotsAllowedForEvening = 0;
        final User user = securityUtils.getCurrentUser();
        List<Slot> slots = new ArrayList<>();
        for (SlotMapping slotZone : slotZoneList) {
            Calendar calender = Calendar.getInstance();
            if (slotZone.getMaxSlotsAllowed() > 0) {
                scheduledSlotsAllowedForMorning = (int) Math.ceil(Double.valueOf(slotZone.getMaxSlotsAllowed()) / 2);
                scheduledSlotsAllowedForEvening = (int) Math.floor(Double.valueOf(slotZone.getMaxSlotsAllowed()) / 2);
            }
            if (slotZone.getMaxRescheduledSlotsAllowed() != null && slotZone.getMaxRescheduledSlotsAllowed() > 0) {
                rescheduledSlotsAllowedForMorning = (int) Math.ceil(Double.valueOf(slotZone.getMaxRescheduledSlotsAllowed()) / 2);
                rescheduledSlotsAllowedForEvening = (int) Math
                        .floor(Double.valueOf(slotZone.getMaxRescheduledSlotsAllowed()) / 2);
            }
            for (int i = 1; i <= Integer.valueOf(noOfDays); i++) {
                calender.add(Calendar.DAY_OF_YEAR, 1);
                calender = holidayListService.getNextWorkingDay(calender);
                if (slotService.isSlotOpen(slotZone.getZone(), calender.getTime(), SLOT_TYPE_OC))
                    continue;
                else {
                    Slot slot = new Slot(SLOT_TYPE_OC, slotZone.getZone(), calender.getTime(), null);
                    slot.setCreatedBy(user);
                    slot.setCreatedDate(new Date());
                    List<SlotDetail> slotDetailList = new ArrayList<>();
                    SlotDetail slotDetail = new SlotDetail(APP_TIME_MORNING, scheduledSlotsAllowedForMorning, rescheduledSlotsAllowedForMorning, 0, 0, slot);
                    slotDetail.setCreatedDate(new Date());
                    slotDetail.setCreatedBy(user);
                    slotDetailList.add(slotDetail);
                    slotDetail = new SlotDetail(APP_TIME_EVENING, scheduledSlotsAllowedForEvening, rescheduledSlotsAllowedForEvening, 0, 0, slot);
                    slotDetail.setCreatedDate(new Date());
                    slotDetail.setCreatedBy(user);
                    slotDetailList.add(slotDetail);
                    slot.setSlotDetail(slotDetailList);
                    slots.add(slot);
                }
            }
        }
        slotRepository.save(slots);
    }
}
