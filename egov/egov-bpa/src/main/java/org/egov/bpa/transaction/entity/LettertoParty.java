/**
 * eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.
    Copyright (C) <2015>  eGovernments Foundation
    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .
    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:
        1) All versions of this program, verbatim or modified must carry this
           Legal Notice.
        2) Any misrepresentation of the origin of the material is prohibited. It
           is required that all modified versions of this material be marked in
           reasonable ways as different from the original version.
        3) This license does not grant any rights to any user of the program
           with regards to rights under trademark law for use of the trade names
           or trademarks of eGovernments Foundation.
  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.bpa.transaction.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.egov.bpa.master.entity.LpReason;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "EGBPA_LETTERTOPARTY")
@SequenceGenerator(name = LettertoParty.SEQ_LETTERTOPARTY, sequenceName = LettertoParty.SEQ_LETTERTOPARTY, allocationSize = 1)
public class LettertoParty extends AbstractAuditable {

    private static final long serialVersionUID = 3078684328383202788L;
    public static final String SEQ_LETTERTOPARTY = "SEQ_EGBPA_LETTERTOPARTY";

    @Id
    @GeneratedValue(generator = SEQ_LETTERTOPARTY, strategy = GenerationType.SEQUENCE)
    private Long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "application")
    private BpaApplication application;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection")
    private Inspection inspection;
    @Length(min = 1, max = 32)
    private String acknowledgementNumber;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "egbpa_lp_and_reason", joinColumns = @JoinColumn(name = "lettertoparty"), inverseJoinColumns = @JoinColumn(name = "lpreason"))
    private List<LpReason> lpReason;
    @Length(min = 1, max = 128)
    private String lpNumber;

    @Temporal(TemporalType.DATE)
    private Date letterDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduledby")
    private User scheduledby;
    @Length(min = 1, max = 128)
    private String scheduledPlace;
    @Temporal(TemporalType.DATE)
    private Date scheduledtime;
    @Temporal(TemporalType.DATE)
    private Date sentDate;
    @Temporal(TemporalType.DATE)
    private Date replyDate;
    @Length(min = 1, max = 1024)
    private String lpRemarks;
    @Length(min = 1, max = 1024)
    private String lpReplyRemarks;
    @Length(min = 1, max = 1024)
    private String lpDesc;
    @Length(min = 1, max = 1024)
    private String lpReplyDesc;
    private Boolean isHistory;
    @Length(min = 1, max = 512)
    private String documentid;
    @Temporal(TemporalType.DATE)
    private Date lastReplyDate;

    @OneToMany(mappedBy = "letterToParty", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AutoDcrMap> autoDcrMap = new ArrayList<AutoDcrMap>(0);
    @OrderBy("id ASC")
    @OneToMany(mappedBy = "lettertoParty", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<LettertoPartyDocument> lettertoPartyDocument = new ArrayList<>(0);
    private String currentStateValueOfLP;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "currentapplnstatus")
    private BpaStatus currentApplnStatus;
    private String stateForOwnerPosition;
    private String pendingAction;
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public Inspection getInspection() {
        return inspection;
    }

    public void setInspection(final Inspection inspection) {
        this.inspection = inspection;
    }

    public String getAcknowledgementNumber() {
        return acknowledgementNumber;
    }

    public void setAcknowledgementNumber(final String acknowledgementNumber) {
        this.acknowledgementNumber = acknowledgementNumber;
    }

    public List<LpReason> getLpReason() {
        return lpReason;
    }

    public void setLpReason(List<LpReason> lpReason) {
        this.lpReason = lpReason;
    }

    public String getLpNumber() {
        return lpNumber;
    }

    public void setLpNumber(final String lpNumber) {
        this.lpNumber = lpNumber;
    }

    public Date getLetterDate() {
        return letterDate;
    }

    public void setLetterDate(final Date letterDate) {
        this.letterDate = letterDate;
    }

    public User getScheduledby() {
        return scheduledby;
    }

    public void setScheduledby(final User scheduledby) {
        this.scheduledby = scheduledby;
    }

    public String getScheduledPlace() {
        return scheduledPlace;
    }

    public void setScheduledPlace(final String scheduledPlace) {
        this.scheduledPlace = scheduledPlace;
    }

    public Date getScheduledtime() {
        return scheduledtime;
    }

    public void setScheduledtime(final Date scheduledtime) {
        this.scheduledtime = scheduledtime;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(final Date sentDate) {
        this.sentDate = sentDate;
    }

    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(final Date replyDate) {
        this.replyDate = replyDate;
    }

    public String getLpRemarks() {
        return lpRemarks;
    }

    public void setLpRemarks(final String lpRemarks) {
        this.lpRemarks = lpRemarks;
    }

    public String getLpReplyRemarks() {
        return lpReplyRemarks;
    }

    public void setLpReplyRemarks(final String lpReplyRemarks) {
        this.lpReplyRemarks = lpReplyRemarks;
    }

    public String getLpDesc() {
        return lpDesc;
    }

    public void setLpDesc(final String lpDesc) {
        this.lpDesc = lpDesc;
    }

    public String getLpReplyDesc() {
        return lpReplyDesc;
    }

    public void setLpReplyDesc(final String lpReplyDesc) {
        this.lpReplyDesc = lpReplyDesc;
    }

    public Boolean getIsHistory() {
        return isHistory;
    }

    public void setIsHistory(final Boolean isHistory) {
        this.isHistory = isHistory;
    }

    public String getDocumentid() {
        return documentid;
    }

    public void setDocumentid(final String documentid) {
        this.documentid = documentid;
    }

    public BpaApplication getApplication() {
        return application;
    }

    public void setApplication(final BpaApplication application) {
        this.application = application;
    }

    public List<AutoDcrMap> getAutoDcrMap() {
        return autoDcrMap;
    }

    public List<LettertoPartyDocument> getLettertoPartyDocument() {
        return lettertoPartyDocument;
    }

    public void setLettertoPartyDocument(List<LettertoPartyDocument> lettertoPartyDocument) {
        this.lettertoPartyDocument = lettertoPartyDocument;
    }

    public Date getLastReplyDate() {
        return lastReplyDate;
    }

    public void setLastReplyDate(Date lastReplyDate) {
        this.lastReplyDate = lastReplyDate;
    }

    public String getCurrentStateValueOfLP() {
        return currentStateValueOfLP;
    }

    public void setCurrentStateValueOfLP(String currentStateValueOfLP) {
        this.currentStateValueOfLP = currentStateValueOfLP;
    }

    public BpaStatus getCurrentApplnStatus() {
        return currentApplnStatus;
    }

    public void setCurrentApplnStatus(BpaStatus currentApplnStatus) {
        this.currentApplnStatus = currentApplnStatus;
    }

    public String getStateForOwnerPosition() {
        return stateForOwnerPosition;
    }

    public void setStateForOwnerPosition(String stateForOwnerPosition) {
        this.stateForOwnerPosition = stateForOwnerPosition;
    }

    public String getPendingAction() {
        return pendingAction;
    }

    public void setPendingAction(String pendingAction) {
        this.pendingAction = pendingAction;
    }
    
}