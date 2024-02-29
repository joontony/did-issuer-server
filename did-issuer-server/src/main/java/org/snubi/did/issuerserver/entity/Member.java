package org.snubi.did.issuerserver.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Entity
@ToString
@Table(name = "tb_member")
@EntityListeners(AuditingEntityListener.class)
public class Member extends BaseEntity {

    @Id
    @NotNull
    @Column(name = "member_id", length = 20)
    private String memberId;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "member_name", length = 50)
    private String memberName;

    @NotBlank(message = "전화번호가 비어있습니다. 작성해주세요.")
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "10 ~ 11 자리의 숫자만 입력 가능합니다.")
    @Column(name = "mobile_number", length = 11, nullable = false)
    private String mobileNumber;

    @Column(name = "birth", columnDefinition="datetime")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDateTime birth;

    @Column(name = "profile_file_path", length = 255)
    private String profileFilePath;

    @NotNull
    @Column(name = "mobile_auth_number", length = 4, nullable = false)
    private String mobileAuthNumber;

    @Column(name = "mobile_auth_flag", columnDefinition = "boolean default false")
    private boolean mobileAuthFlag;

    @Column(name = "device_id" ,columnDefinition = "TEXT")
    private String deviceId;

    @Column(name = "register_flag")
    private boolean registerFlag;

    @Column(name = "card_file_path", length = 255)
    private String cardFilePath;

    public void updateRegisterFlag() {
        this.registerFlag = true;
    }
}
