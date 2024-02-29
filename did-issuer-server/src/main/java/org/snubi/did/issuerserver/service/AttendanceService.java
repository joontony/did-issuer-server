package org.snubi.did.issuerserver.service;

import org.snubi.did.issuerserver.dto.ReceiveRequest;
import org.snubi.did.issuerserver.dto.SendRequest;

public interface AttendanceService {
    void attendanceCheck(ReceiveRequest.AttendanceCheckDto attendanceCheckDto);
}
