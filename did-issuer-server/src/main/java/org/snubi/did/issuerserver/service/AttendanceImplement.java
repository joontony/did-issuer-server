package org.snubi.did.issuerserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snubi.did.issuerserver.dto.ReceiveRequest;
import org.snubi.did.issuerserver.dto.SendRequest;
import org.snubi.did.issuerserver.entity.ClubAttendanceCheck;
import org.snubi.did.issuerserver.repository.ClubAttendanceCheckRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceImplement implements AttendanceService {
    private final ClubAttendanceCheckRepository clubAttendanceCheckRepository;
    @Override
    public void attendanceCheck(ReceiveRequest.AttendanceCheckDto attendanceCheckDto) {

        ClubAttendanceCheck attendanceCheck = ClubAttendanceCheck.createClubAttendanceCheckOf(attendanceCheckDto.getClubSeq(), attendanceCheckDto.getScannerDid(), attendanceCheckDto.getQrCode());

        clubAttendanceCheckRepository.save(attendanceCheck);
    }
}
