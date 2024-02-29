package org.snubi.did.issuerserver.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.snubi.did.issuerserver.entity.ClubInvitation;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BatchInsertRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clubInvitationSaveAll(List<ClubInvitation> clubInvitationList, int batchSize) {
        String sql = "INSERT INTO tb_club_invitation (club_seq, club_role_seq, member_name, member_grade, " +
                "mobile_number, data_from_issuer, extra_data, local_name, confirm_flag, valid, " +
                "expired_date, club_fee, send_flag) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int totalSize = clubInvitationList.size();
        int fromIdx = 0;
        Long clubSeq = clubInvitationList.get(0).getClub().getClubSeq();


        log.info("[ clubInvitationSaveAll ] - batchinsert 시작");
        while (fromIdx < totalSize) {
            int toIdx = Math.min(fromIdx + batchSize, totalSize); // batchSize 만큼 끊고 맨 마지막에 남은게 있으면 totalSize로
            List<ClubInvitation> batchList = clubInvitationList.subList(fromIdx, toIdx);

            BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
                    @Override
                public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                    ClubInvitation clubInvitation = batchList.get(i);
                    ps.setLong(1, clubSeq);
                    ps.setLong(2, 2);
                    ps.setString(3, clubInvitation.getMemberName());
                    ps.setString(4, clubInvitation.getMemberGrade());
                    ps.setString(5, clubInvitation.getMobileNumber());
                    ps.setString(6, clubInvitation.getDataFromIssuer());
                    ps.setString(7, clubInvitation.getExtraData());
                    ps.setString(8, clubInvitation.getLocalName());
                    ps.setBoolean(9, clubInvitation.isConfirmFlag());
                    ps.setBoolean(10, clubInvitation.isValid());
                    ps.setDate(11, new java.sql.Date(clubInvitation.getExpiredDate().getTime()));
                    ps.setBoolean(12, clubInvitation.isClubFee());
                    ps.setBoolean(13, clubInvitation.isSendFlag());
                }
                @Override
                public int getBatchSize() {
                    return batchList.size();
                }
            };

            jdbcTemplate.batchUpdate(sql, setter);

            log.info("[ clubInvitationSaveAll ] - batchSize : " + batchList.size());
            fromIdx = toIdx;
        }
    }
}