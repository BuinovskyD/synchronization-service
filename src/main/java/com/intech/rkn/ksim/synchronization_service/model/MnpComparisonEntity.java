package com.intech.rkn.ksim.synchronization_service.model;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Builder
@Table(name = "mnp_full_comparison", schema = "sync")
public record MnpComparisonEntity(
        @Id
        Long msisdn,
        Integer mnpId,
        String mnpCode,
        Integer mnpMnc,
        Integer donorId,
        String donorCode,
        Integer donorMnc,
        Integer rangeHolderId,
        String rangeHolderCode,
        LocalDateTime mnpPortDate,
        String processType,
        Integer ksimId,
        String ksimCode,
        Integer ksimMnc,
        LocalDateTime ksimPortDate,
        boolean isSynchronized,
        LocalDateTime comparisonDate
) {
    public MnpComparisonEntity markAsProcessed(MnpMsisdnData mnpData) {
        return mnpData == null ? base().build() : base()
                .ksimId(mnpData.operatorId())
                .ksimCode(mnpData.operatorCode())
                .ksimMnc(mnpData.mnc())
                .ksimPortDate(mnpData.portedAt())
                .isSynchronized(isOperatorsEqual(this.mnpId, mnpData.operatorId()))
                .build();
    }

    private MnpComparisonEntityBuilder base() {
        return MnpComparisonEntity.builder()
                .msisdn(this.msisdn)
                .mnpId(this.mnpId)
                .mnpCode(this.mnpCode)
                .mnpMnc(this.mnpMnc)
                .donorId(this.donorId)
                .donorCode(this.donorCode)
                .donorMnc(this.donorMnc)
                .rangeHolderId(this.rangeHolderId)
                .rangeHolderCode(this.rangeHolderCode)
                .mnpPortDate(this.mnpPortDate)
                .processType(this.processType)
                .comparisonDate(LocalDateTime.now());
    }

    private boolean isOperatorsEqual(Integer first, Integer second) {
        return first != null && first.equals(second);
    }
}
