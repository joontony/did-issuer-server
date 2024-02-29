-- tb_member
INSERT INTO `tb_member` (`member_id`, `created`, `updated`, `creator`, `updater`, `birth`, `device_id`, `email`, `member_name`, `mobile_auth_flag`, `mobile_auth_number`, `mobile_number`, `profile_file_path`, `register_flag`, `card_file_path`)
VALUES
    ('avchain', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '애브체인', 0, '', '01025847478', NULL, 1, 'card2'),
    ('ID202309150943253534', '2023-09-15 09:43:25.0', '2023-09-15 10:37:18.0', 'anonymousUser', 'ID202309150943253534', '1972-10-28 00:00:00.0', 'c4gYxg_xSN2gPOtPo1WJ5O:APA91bHK7dQDI01qpaN3Yc_TdbGePgg5CM5VucqouzjjSYK1b8u5Pt4CJWH5t3A7wdZFJnoJFMfB4W_VaFPfTm52-p5nRDlXAfmdH1rWvynpyvT8IdT19EO-29xrUObikn9Rz6q8at74', 'jangtaehoon2020@avchain.io', '장태훈', 1, '3534', '01000002020', '/home/healthavatar/springbootApp/did.server/interface/upload/profile/ID202309150943253534.png', 1, 'card1');

-- tb_member_did
INSERT INTO `tb_member_did` (`member_did_seq`, `created`, `updated`, `creator`, `updater`, `did`, `expired_date`, `member_public_key`, `valid`, `member_id`, `member_private_key`)
VALUES
    (1,NULL,NULL,NULL,NULL,'did:avchain:ebfeb1f712ebc6f1c276e12ec21',NULL,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq27JfFlVCxcTsBzd3uProJ5dunUuZ8acjPKn17b2yzm9CNaT4XyrTInW5xHYsyXdEj1SQkCKSD1FHgiTQMaDF5KU3rApc0QYRfYFEtoZT5Z+yx1NRNLbQsEmZqYsDsXEi7RwPsfIu4ogDcUwDzAE644FwrilasgZRChrLAbUS0KEkDX5mBA5FHV5PnapaAT',0,'avchain', 'privateKey1'),
    (2, NULL, NULL, NULL, NULL, 'did:avdid:92ffkjsnf9238r202', NULL, 'memberPublicKey001', 0, 'ID202309150943253534', 'privateKey2');

-- tb_club_role
INSERT INTO `tb_club_role` (`club_role_seq`, `created`, `updated`, `creator`, `updater`, `role_type`)
VALUES
    (1,NULL,NULL,NULL,NULL,'ISSUER'),
    (2,NULL,NULL,NULL,NULL,'HOLDER'),
    (3,NULL,NULL,NULL,NULL,'VERIFIER'),
    (4,NULL,NULL,NULL,NULL,'STAFF');

-- tb_club_category
INSERT INTO `tb_club_category` (`club_category_seq`, `created`, `updated`, `creator`, `updater`, `category_code`, `display`, `pod_yaml_path`, `svs_yaml_path`)
VALUES
    (1, NULL, NULL, NULL, NULL, '1', '의사회', '/home/healthavatar/springbootApp/did.server/interface/yaml/pod-v1.yaml', '/home/healthavatar/springbootApp/did.server/interface/yaml/svs-v1.yaml'),
    (2, NULL, NULL, NULL, NULL, '2', '의원', '/home/healthavatar/springbootApp/did.server/interface/yaml/pod-v1.yaml', '/home/healthavatar/springbootApp/did.server/interface/yaml/svs-v1.yaml'),
    (3, NULL, NULL, NULL, NULL, '2', '맴버쉽', '/home/healthavatar/springbootApp/did.server/interface/yaml/pod-v1.yaml', '/home/healthavatar/springbootApp/did.server/interface/yaml/svs-v1.yaml');

-- tb_club
INSERT INTO `tb_club` (`club_seq`, `created`, `updated`, `creator`, `updater`, `club_name`, `club_public_key`, `club_url`, `description`, `end_date`, `image_path1`, `image_path2`, `image_path3`, `image_path4`, `image_path5`, `location`, `operate_time`, `phone`, `pod_url`, `start_date`, `valid`, `club_category_seq`, `member_did_seq`)
VALUES
    (1, '2023-09-15 10:42:32', '2023-09-15 12:58:39', 'ID202309150943253534', 'ID202309150943253534', '테스트병원', 'clubPublicKey001', 'http', '테스트 병원입니다.', '2029-12-31 00:00:00', '/home/healthavatar/springbootApp/did.server/interface/upload/11_클럽이름/file1.png', '', '', '', '', '서울시 강남구 학동로 22-80 4층', '월~토', '02-3333-5656', 'http://localhost:30020', '2029-12-31 00:00:00', 0, 2, 2);


-- tb_agent
INSERT INTO `tb_agent` (`agent_seq`, `created`, `updated`, `creator`, `updater`, `agent_name`)
VALUES
    (1, NULL, NULL, NULL, NULL, '접수+증상입력,대기표'),
    (2, NULL, NULL, NULL, NULL, '송금'),
    (3, NULL, NULL, NULL, NULL, '접수+증상입력');

-- tb_agent_club
INSERT INTO `tb_agent_club` (`agent_club_seq`, `created`, `updated`, `creator`, `updater`, `flag`, `agent_setting`, `memo_setting`, `agent_seq`, `club__seq`)
VALUES
    (1, '2023-09-15 10:42:32', '2023-09-15 10:42:32', 'ID202309150943253534', 'ID202309150943253534', 0, '{"증상": [{"두통": true},{"심신미약": false}],"진료실": [{"임동권 원장님": true},{"최순호 원장님": true}]}', '{"회원태그": [{"수술예정": true},{"진료": true},{"코로나": false}]}', 1, 1);
